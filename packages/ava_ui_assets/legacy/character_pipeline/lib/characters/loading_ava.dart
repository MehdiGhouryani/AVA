// MINIMAL Ava for the loading screen (S22): her head rides the end of the progress bar.
// No body, no text, one sprite swap at the end. Respects RTL (bar fills from the right in Persian).
import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'character_models.dart';

class LoadingAvaBar extends StatefulWidget {
  const LoadingAvaBar({super.key, required this.progress, this.ready = false, this.reducedMotion = false});
  final double progress; // 0..1, monotonic (never indeterminate, file 02 section 3.5)
  final bool ready; // true -> excited head + one hop
  final bool reducedMotion;
  @override
  State<LoadingAvaBar> createState() => _LoadingAvaBarState();
}

class _LoadingAvaBarState extends State<LoadingAvaBar> with SingleTickerProviderStateMixin {
  late final AnimationController _c =
      AnimationController(vsync: this, duration: const Duration(milliseconds: 1400))..repeat(reverse: true);

  @override
  void dispose() {
    _c.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final m = CharacterScope.of(context);
    final path = m.avaHead(widget.ready ? AvaHead.excited : AvaHead.happy);
    final rtl = Directionality.of(context) == TextDirection.rtl;
    const head = 64.0, track = 12.0;
    return SizedBox(
      height: head + 16,
      child: LayoutBuilder(builder: (context, c) {
        final w = c.maxWidth, p = widget.progress.clamp(0.0, 1.0);
        final x = (rtl ? (1 - p) : p) * (w - head);
        return AnimatedBuilder(
          animation: _c,
          builder: (context, _) {
            final bob = widget.reducedMotion ? 0.0 : 4 * math.sin(math.pi * _c.value);
            return Stack(clipBehavior: Clip.none, children: [
              Positioned(
                left: head / 2, right: head / 2, bottom: 6, height: track,
                child: DecoratedBox(
                  decoration: BoxDecoration(color: const Color(0xFFEFE7D5), borderRadius: BorderRadius.circular(track)),
                  child: Align(
                    alignment: rtl ? Alignment.centerRight : Alignment.centerLeft,
                    child: FractionallySizedBox(
                      widthFactor: p,
                      child: DecoratedBox(
                          decoration: BoxDecoration(color: const Color(0xFF0FA596), borderRadius: BorderRadius.circular(track))),
                    ),
                  ),
                ),
              ),
              if (path != null)
                Positioned(
                  left: x, bottom: 6 + bob, width: head, height: head,
                  child: ExcludeSemantics(child: Image.asset(path, fit: BoxFit.contain, gaplessPlayback: true)),
                ),
            ]);
          },
        );
      }),
    );
  }
}
