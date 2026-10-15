// Ava (ONE colour). Allowed ONLY outside lessons (map, loading, rewards, shop, album, onboarding, graduation).
import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'character_models.dart';

enum AvaEmotion { idle, curious, explain, encourage, retry, celebrate, listen, wink, sleep }

class CompanionView extends StatefulWidget {
  const CompanionView({
    super.key,
    this.emotion = AvaEmotion.idle,
    this.pose = AvaPose.standing,
    this.height = 240,
    this.reducedMotion = false,
    this.onTap,
  });
  final AvaEmotion emotion;
  final AvaPose pose;
  final double height;
  final bool reducedMotion; // calm mode / weak device: breathing only
  final VoidCallback? onTap;
  @override
  State<CompanionView> createState() => _CompanionViewState();
}

class _CompanionViewState extends State<CompanionView> with TickerProviderStateMixin {
  late final AnimationController _breath =
      AnimationController(vsync: this, duration: const Duration(milliseconds: 3200))..repeat(reverse: true);
  late final AnimationController _hop = AnimationController(vsync: this, duration: const Duration(milliseconds: 420));
  late final AnimationController _tilt = AnimationController(vsync: this, duration: const Duration(milliseconds: 600));

  @override
  void didUpdateWidget(CompanionView old) {
    super.didUpdateWidget(old);
    if (widget.emotion != old.emotion && !widget.reducedMotion) {
      switch (widget.emotion) {
        case AvaEmotion.celebrate:
        case AvaEmotion.encourage:
          _hop.forward(from: 0);
        case AvaEmotion.curious:
        case AvaEmotion.retry: // small tilt, never a head-shake "no"
          _tilt.forward(from: 0);
        default:
      }
    }
  }

  @override
  void dispose() {
    _breath.dispose();
    _hop.dispose();
    _tilt.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final path = CharacterScope.of(context).avaBody(widget.pose);
    return Semantics(
      label: 'آوا',
      button: widget.onTap != null,
      child: GestureDetector(
        onTap: widget.onTap,
        child: SizedBox(
          height: widget.height + 24,
          child: AnimatedBuilder(
            animation: Listenable.merge([_breath, _hop, _tilt]),
            builder: (context, _) {
              final breath = widget.reducedMotion ? 1.0 : 1 + 0.02 * Curves.easeInOut.transform(_breath.value);
              final p = _hop.value, air = math.sin(math.pi * p);
              final sq = p < 0.15 ? p / 0.15 : (p > 0.85 ? (1 - p) / 0.15 : 0.0);
              final sx = 1 + 0.08 * sq - 0.03 * air, sy = breath - 0.10 * sq + 0.06 * air;
              final rot = 5 * math.pi / 180 * math.sin(math.pi * 1.2 * _tilt.value);
              return Stack(alignment: Alignment.bottomCenter, children: [
                Container(
                  width: widget.height * 0.5 * (1 - 0.35 * air),
                  height: 14,
                  decoration: BoxDecoration(color: const Color(0x282B2A4C), borderRadius: BorderRadius.circular(8)),
                ),
                if (path != null)
                  Transform(
                    alignment: Alignment.bottomCenter,
                    transform: Matrix4.identity()..translate(0.0, -70.0 * air)..rotateZ(rot)..scale(sx, sy),
                    child: AnimatedSwitcher(
                      duration: const Duration(milliseconds: 220),
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
