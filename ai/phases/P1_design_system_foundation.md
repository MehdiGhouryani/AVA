# P1 — Design system & Flutter foundation

**Gate note:** this prompt labels P1 as `HOLD (U-038)`, but the decision ledger is absent from this snapshot. Verify the hold against an available owner instruction before starting implementation; do not treat U-038 as independently confirmed. Run T-P1-08 only when the owner requests visual briefs, as described below.

Goal: a polished, child-safe UI kit that every screen reuses. Beauty and correct layout are the top priority (U-028).
Read: `docs/pedagogy/01_master_educational_spec_v2.md`, `docs/pedagogy/02_stage_content_design_guide_fa_v2.md`, `ai/specs/TECH_STACK.md`, `docs/spec/02_DESIGN_SYSTEM_FA.md`, `design/design_tokens.json`, and `data/ui_layout.json`. The AI spec files and current design spec/token files are created by P0; if P0 has not produced them yet, do not pretend they exist or substitute a historical path.
Style: if T-P0-06 is not done yet, build style-agnostic (all visuals come from tokens + asset slots).

### T-P1-01 — Create the Flutter project in `apps/mobile/`
Type: TASK
Do: Create the project structure from `docs/ARCHITECTURE.md` (feature-first), `pubspec.yaml` with pinned packages, `analysis_options.yaml` (strict lints), three flavors (`bazaar`, `myket`, `web`) as Android product flavors, `l10n`-style string files (Persian only), RTL enforced, Android minSdk per TECH_STACK. Include `apps/mobile/README.md` with the exact commands to run on a phone, and write `docs/dev_setup_fa.md` (Persian, step by step for a beginner): install Flutter SDK + Android Studio + JDK, enable USB debugging, `flutter doctor`, run each flavor, build an APK; Node LTS and Docker for the server; how to copy errors back to the AI (full terminal output of `flutter analyze` / `flutter run`, plus a screenshot). Include a network note for Iran: pub.dev/Flutter downloads may need a mirror or VPN (`PUB_HOSTED_URL`, `FLUTTER_STORAGE_BASE_URL` environment variables); mark any mirror address `TO_VERIFY`.
Files: `apps/mobile/**`.
Done when: structure follows `ai/specs/ARCHITECTURE_SKELETON.md`; pubspec + flavors + README + `docs/dev_setup_fa.md` exist; `main.dart` shows a placeholder screen.

### T-P1-02 — Tokens → Dart theme
Type: TASK
Do: Generate `apps/mobile/lib/core/design/tokens.dart` and `theme.dart` from `design/design_tokens.json` (colors, spacing scale, radii, elevations, typography, motion durations/curves, touch-target sizes). Keep a single place to change values. Pick the Persian font from the design spec; if licensing is unclear log OQ and use a free font (check license; record in `docs/fonts.md`).
Done when: no hard-coded colors/sizes outside tokens (grep clean); theme applies to a sample screen.

### T-P1-03 — Core components (child-safe)
Type: TASK (split per component group if large: 03a buttons/cards, 03b feedback/dialogs, 03c progress/rewards)
Do: Build and document: BigButton (primary/secondary/icon), Card, LessonNode (path node: locked/open/done/current), ProgressDots, StarsRow, LeafBadge, SoftDialog/BottomSheet, SpeakerButton (replay sound), BackButton (safe), LoadingBlob, EmptyState. Every component: states default/pressed/disabled/loading, min touch size from tokens, press animation, soft sound hook, semantic labels.
Done when: each component is in `apps/mobile/lib/core/widgets/` with a doc comment of states and a usage example in the gallery (T-P1-06).

### T-P1-04 — Responsive scaffold for phone and tablet
Type: TASK
Do: Implement `AdaptiveScaffold` and layout helpers: phone portrait lock, tablet portrait + landscape (C-108), centered content column (max width from tokens), safe areas, large-text safety, keyboard-safe forms. Provide breakpoints in one file. Write `docs/layout_rules.md` (how a screen must behave at 360×640, 411×891, 600×960, 1280×800).
Done when: scaffold used by the gallery; layout rules documented with the four sizes.

### T-P1-05 — Audio and haptics service
Type: TASK
Do: `AudioService` (interface + just_audio implementation + fake): play voice line, play sfx, stop on navigation, duck sfx under voice, global mute, volume policy (soft by default, U-016), preloading, error → silent fallback (never crash). `HapticsService` (very light). Placeholder sfx list in `apps/mobile/assets/audio/sfx/README.md` with names and moods (soft pop, gentle chime, quiet whoosh, no harsh sounds).
Done when: services exist with unit-test stubs; sfx names are referenced by components via an enum.

### T-P1-06 — Character widget (Rive-ready)
Type: TASK
Do: `CharacterView(characterId, state)` that plays a Rive state machine when the `.riv` exists and falls back to a static image otherwise. Define the asset contract in `docs/character_asset_contract.md`: file names, state-machine name, input names, the state list per character taken from `design/characters/*` and the spec, size budget.
Done when: widget compiles against a placeholder asset; contract document lists every state id used by screens.

### T-P1-07 — Component gallery and golden test setup
Type: TASK
Do: A hidden dev screen listing all components and states, light/dark not needed, tablet and phone variants; golden-test scaffolding under `apps/mobile/test/golden/` (owner runs later).
Done when: gallery route `/dev/gallery` (debug only) exists.

### T-P1-08 — Screen visual briefs
Type: TASK
Needs: T-P0-05
Do: For every child-facing screen and the parent screens built in P2, P4–P7, write `design/screens/<ID>/BRIEF_FA.md`: purpose, layout zones and visual hierarchy, touch targets, character presence and expression, motion, sound, the states to depict, tablet variation, copy keys (never invent copy), and two ready-to-paste image prompts (phone mockup, tablet mockup) built from `design/style_gate/` and `design/prompt_system/` (use `[[STYLE]]` until T-P0-06 is decided). Write `design/screens/INDEX.md` with the priority order: S01, S02, S03a, S03b, S04, S06-B, S07, S08, S09, S10, S13, S30 first.
Done when: briefs exist for the 12 priority screens; the rest are listed in INDEX as TODO.

### T-P1-09 — Mockup intake
Type: GATE (owner action OQ-21, never blocking)
Do: The owner generates mockups from the briefs in ChatGPT (OQ-21, U-038) and drops them into `design/screens/<ID>/mockup_*.png`. For each screen: compare the mockup with the tokens (tokens win for values, mockup wins for layout and mood) and record deviations in `design/screens/<ID>/NOTES.md`. No mockup → implement from brief + tokens and write `mockup: none` in NOTES.
Done when: every screen implemented in P2 and later has a NOTES.md stating its mockup status.

### T-P1-10 — SMOKE P1
Type: SMOKE
Do: Write the Smoke checklist in `ai/STATE.md` if that file and its schema exist; otherwise include it in the hand-off. Checklist: `flutter pub get`, `flutter analyze`, `flutter run --flavor bazaar`, open gallery on a phone and a tablet/emulator. Fix anything the owner reports before moving on.
Done when: checklist written to `ai/STATE.md` if it exists, otherwise included in the hand-off; if the owner already supplied errors, they are fixed.
