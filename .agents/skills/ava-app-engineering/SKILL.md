---
name: ava-app-engineering
description: Use for implementation and review in AVA's native Android app, Kotlin, Jetpack Compose, Persian RTL, local content and child-facing UI.
---

# AVA Native engineering

The repository-wide contract and execution workflow live in `AGENTS.md` and the task harness. This skill adds implementation conventions and child UX checks; it does not override either source.

## Implementation conventions

- Keep content, state/actions, repositories and UI separate. A Composable receives state and actions only.
- Source copy from `design/strings.fa.json` (including `source` metadata); artwork from `design/assets.manifest.json`; sizes and colors from `design/design_tokens.json` and `DesignTokens.kt`. Do not hardcode product copy or colors in Composables.
- Generated files (`Catalog.kt`, Android resources and payload types) are changed only through their canonical generator. After content/resource changes run `python3 scripts/sync-android-resources.py --check`.
- Route audio through the injectable audio interface with a stable reference and visible text fallback. Never make an activity completable by audio alone.
- Every screen has a visible way back. Loading, empty, locked, error and completed states must be represented explicitly.
- Every control needs a semantic label or visible Persian text. Feedback must not rely on color alone.

## Child UX review (ages 4–7)

Treat these as design criteria, not proven learning outcomes. Label findings as evidence, analysis or judgement; test with children before claiming usability or educational effectiveness.

1. One obvious main action per screen, large and within the thumb zone.
2. Use a clear ordered path with completed/current/locked states. Explain a lock in text.
3. Keep lessons to 3–6 short steps, each with one goal.
4. Give immediate multi-channel feedback: shape/icon, a short Persian line and optional sound. For wrong answers use gentle amber feedback, a hint and retry.
5. Prefer recognition before recall, and word building before writing.
6. Praise effort, not ability. The companion guides; it never judges.
7. Provide replay for every spoken prompt.
8. A wrong answer never ends a lesson, removes progress or starts a timer. After two wrong attempts, offer a clear skip.
9. Keep celebrations short (at most 4 seconds), skippable, absent under reduced motion and granted once.
10. Keep progress visible with Persian numerals.
11. Use generous spacing; keep controls away from screen edges and system bars.
12. Use short, friendly sentences addressed to the child.
13. Keep the same control in the same place with consistent feedback across activity steps.

Before reporting a child-facing screen, check: Can a four-year-old complete each decision clearly? Is the primary action the largest and reachable? Are all states represented? Can the child understand the next action without adult explanation?

For claims about child development or learning, cite research actually read or label the statement as a design judgement. An observed design pattern is not a validated curriculum.