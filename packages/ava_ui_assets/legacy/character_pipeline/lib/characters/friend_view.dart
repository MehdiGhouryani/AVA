// Lesson-zone host (one friend per educational screen). Friends never speak text and never judge answers:
// they react with gestures/sounds; the unseen narrator voice gives instructions (V1).
import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'character_models.dart';

class FriendView extends StatefulWidget {
  const FriendView({
    super.key,
    required this.friend,
    required this.label, // localized name for TalkBack, comes from data (names are still pending approval)
    this.emotion = FriendEmotion.idle,
    this.height = 160, // host_dp range per friend: characters.json
    this.reducedMotion = false,
    this.onTap,
  });
  final FriendId friend;
  final String label;
  final FriendEmotion emotion;
  final double height;
  final bool reducedMotion;
  final VoidCallback? onTap;
  @override
  State<FriendView> createState() => _FriendViewState();
}

class _FriendViewState extends State<FriendView> with TickerProviderStateMixin {
  late final AnimationController _idle =
      AnimationController(vsync: this, duration: const Duration(milliseconds: 2600))..repeat(reverse: true);
  late final AnimationController _hop = AnimationController(vsync: this, duration: const Duration(milliseconds: 420));
  late final AnimationController _tilt = AnimationController(vsync: this, duration: const Duration(milliseconds: 600));

  @override
  void didUpdateWidget(FriendView old) {
    super.didUpdateWidget(old);
    if (widget.emotion != old.emotion && !widget.reducedMotion) {
      if (widget.emotion == FriendEmotion.cheer || widget.emotion == FriendEmotion.happy) _hop.forward(from: 0);
      if (widget.emotion == FriendEmotion.curious) _tilt.forward(from: 0);
    }
  }

  @override
  void dispose() {
    _idle.dispose();
    _hop.dispose();
    _tilt.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final path = CharacterScope.of(context).friend(widget.friend, widget.emotion);
    return Semantics(
      label: widget.label,
      button: widget.onTap != null,
      child: GestureDetector(
        onTap: widget.onTap,
        child: SizedBox(
          height: widget.height + 20,
          child: AnimatedBuilder(
            animation: Listenable.merge([_idle, _hop, _tilt]),
            builder: (context, _) {
              final air = math.sin(math.pi * _hop.value);
              final bob = widget.reducedMotion ? 0.0 : 3 * math.sin(math.pi * _idle.value);
              final rot = 6 * math.pi / 180 * math.sin(math.pi * 1.2 * _tilt.value);
              return Stack(alignment: Alignment.bottomCenter, children: [
                Container(
                  width: widget.height * 0.55 * (1 - 0.3 * air),
                  height: 12,
                  decoration: BoxDecoration(color: const Color(0x282B2A4C), borderRadius: BorderRadius.circular(8)),
                ),
                Transform(
                  alignment: Alignment.bottomCenter,
                  transform: Matrix4.identity()..translate(0.0, -55.0 * air - bob)..rotateZ(rot),
                  child: path == null
                      ? _MissingBox(widget.friend.name, widget.height) // dev only; CI release gate blocks missing art
                      : AnimatedSwitcher(
                          duration: const Duration(milliseconds: 200),
                          child: Image.asset(path, key: ValueKey(path), height: widget.height, gaplessPlayback: true),
                        ),
                ),
              ]);
            },
          ),
        ),
      ),
    );
  }
}

class _MissingBox extends StatelessWidget {
  const _MissingBox(this.name, this.h);
  final String name;
  final double h;
  @override
  Widget build(BuildContext context) => Container(
        width: h * 0.8,
        height: h,
        alignment: Alignment.center,
        decoration: BoxDecoration(color: const Color(0xFFEDE7D8), borderRadius: BorderRadius.circular(24)),
        child: Text(name, style: const TextStyle(color: Color(0xFF5C5B7E))),
      );
}
