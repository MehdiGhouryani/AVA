# CLAUDE.md — AVA

AVA (آوا) is a Persian early-reading app for children aged 4 to 7. The product is **native Android** (Kotlin, Jetpack Compose) in `android/`. `src/` (React) is historical: do not extend it.

## Read first, in this order
1. `AGENTS.md` (the development contract) and `ZCODE.md`.
2. `docs/PROGRESS_FA.md` for the real state, then `docs/README.md`.
3. `harness/roadmap.json` and the playbook of the current phase. Run `python3 scripts/harness.py validate`, then `next`.
4. The skill `ava-app-engineering`, if available.

## Hard rules (never break)
1. Child audience only. No parent panel, parent gate or parent report. No Google or email sign-in. Sign-in is the mock phone + OTP flow.
2. No payment, subscription, ads, coin shop, lives, lost streaks or punishing timers. Errors are amber, never red.
3. Four characters only: Ava, bunny, squirrel, fox.
4. Persian RTL, Vazirmatn, Persian numerals, local artwork only.
5. Touch targets: child screens use 64 dp targets and a 72 dp main CTA (decision DG-3, 2026-11-01; the `minTouch` token stays 48 for adult-facing form controls).
6. Honour reduced motion and large fonts. Check widths 320, 360, 390, 412 and font scale 1, 1.3, 1.5.
7. UI receives state and actions only. No I/O, repository or network inside a Composable.
8. IDs of existing lessons, tasks and audio refs never change without migration and validator.
9. Content stays `approvedForRelease=false` until a human approves it. Never write "approved" in the UI.
10. Do not build APK/AAB unless the owner asks in that request. `testDebugUnitTest` and `lintDebug` are allowed.
11. Never run a `blocked` or `human_gate` harness task. Never fake approval or mark unexecuted evidence as passed.
12. Never commit to, push to or merge into `main` without the owner's written approval. Work on a branch `phase-<n>-<topic>`.

## Working method
- Pick exactly one ready task. Touch only its `allowedPaths`.
- Run the relevant checks and log them in `harness/WORKLOG.md`. Label each claim `ran` or `read in code`.
- If a push is refused, write `git format-patch` and report the path. Do not retry in a loop.
- Report in Persian, then stop and wait for the owner.
