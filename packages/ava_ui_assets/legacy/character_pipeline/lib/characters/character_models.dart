// Character package: models + manifest + asset resolver with safe fallbacks.
// UNTESTED SKETCH: Flutter SDK was not available where this was written. M1 must compile + run it.
import 'dart:convert';
import 'package:flutter/widgets.dart';
import 'package:flutter/services.dart' show rootBundle;

/// Lesson-zone hosts. Ava is intentionally NOT in this enum: she never appears in educational screens (D-77).
enum FriendId { bunny, duck, bird, squirrel, snail, owl }
enum FriendEmotion { idle, happy, cheer, curious }
enum AvaPose { standing, cheering, thinking, holdingStar, sleeping, peek, waving, jumping, reading, running }
enum AvaHead { happy, excited, thinking, surprised, wink, laughing, sleepy } // no "sad" (banned)

String _snake(String s) => s.replaceAllMapped(RegExp('[A-Z]'), (m) => '_${m[0]!.toLowerCase()}');

class CharacterAsset {
  const CharacterAsset(this.id, this.path, this.status);
  final String id, path, status; // status: final | placeholder | missing
  bool get usable => status != 'missing';
}

class CharacterManifest {
  CharacterManifest(this._byId);
  final Map<String, CharacterAsset> _byId;

  factory CharacterManifest.fromJson(Map<String, dynamic> j) => CharacterManifest({
        for (final a in (j['assets'] as List).cast<Map<String, dynamic>>())
          a['id'] as String: CharacterAsset(a['id'] as String, a['path'] as String, a['status'] as String),
      });

  static Future<CharacterManifest> load() async => CharacterManifest.fromJson(
      jsonDecode(await rootBundle.loadString('assets/characters/manifest.json')) as Map<String, dynamic>);

  String? _path(String id) => (_byId[id]?.usable ?? false) ? _byId[id]!.path : null;

  /// Ava body sprite; any missing pose falls back to `standing` (never a broken image).
  String? avaBody(AvaPose p) => _path('ava.${_snake(p.name)}') ?? _path('ava.standing');
  String? avaHead(AvaHead h) => _path('ava.head_${h.name}') ?? _path('ava.head_happy');

  /// Friend sprite; missing emotion falls back to `idle`; null if even idle is missing (UI shows a dev box).
  String? friend(FriendId f, FriendEmotion e) => _path('${f.name}.${e.name}') ?? _path('${f.name}.idle');
}

/// Put once above the app (after `await CharacterManifest.load()`).
class CharacterScope extends InheritedWidget {
  const CharacterScope({super.key, required this.manifest, required super.child});
  final CharacterManifest manifest;
  static CharacterManifest of(BuildContext c) =>
      c.dependOnInheritedWidgetOfExactType<CharacterScope>()!.manifest;
  @override
  bool updateShouldNotify(CharacterScope old) => old.manifest != manifest;
}
