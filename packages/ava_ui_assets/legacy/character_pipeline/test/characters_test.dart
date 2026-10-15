// Pure-Dart unit tests for the resolver (no Flutter engine needed). UNRUN: Flutter SDK was unavailable.
import 'package:flutter_test/flutter_test.dart';
import 'package:your_app/characters/characters.dart'; // adjust package name

CharacterManifest _m() => CharacterManifest.fromJson({
      'assets': [
        {'id': 'ava.standing', 'path': 'a/standing.webp', 'status': 'final'},
        {'id': 'ava.sleeping', 'path': 'a/sleeping.webp', 'status': 'missing'},
        {'id': 'ava.head_happy', 'path': 'a/h_happy.webp', 'status': 'placeholder'},
        {'id': 'bunny.idle', 'path': 'b/idle.webp', 'status': 'placeholder'},
        {'id': 'bunny.cheer', 'path': 'b/cheer.webp', 'status': 'missing'},
      ]
    });

void main() {
  test('missing Ava pose falls back to standing', () => expect(_m().avaBody(AvaPose.sleeping), 'a/standing.webp'));
  test('missing head falls back to happy', () => expect(_m().avaHead(AvaHead.excited), 'a/h_happy.webp'));
  test('missing friend emotion falls back to idle', () => expect(_m().friend(FriendId.bunny, FriendEmotion.cheer), 'b/idle.webp'));
  test('friend with no art returns null (UI shows dev box)', () => expect(_m().friend(FriendId.owl, FriendEmotion.idle), isNull));
  test('Ava is not a lesson host type', () => expect(FriendId.values.map((e) => e.name), isNot(contains('ava'))));
}
