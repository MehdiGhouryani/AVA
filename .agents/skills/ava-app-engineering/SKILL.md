---
name: ava-app-engineering
description: Use when writing, reviewing or changing code, content or docs in the AVA (آوا) children's reading app repo (native Android, Kotlin, Jetpack Compose, Persian RTL). Enforces the repo contract, disciplined changes, child-friendly UX and verified delivery.
---

# AVA app engineering (Android line, version 2)

Replaces the React-era version. Where `AGENTS.md`, `harness/roadmap.json` or a phase playbook is more specific, it wins.

## 1. Start every task this way
1. Read `PROJECT_STATE.md`, `AGENTS.md`, `ZCODE.md`.
2. Run `git status` and `git branch --show-current`. Never work on `main`.
3. Run `python3 scripts/harness.py validate`; if it fails, change nothing. Then `next` (or the task the owner names) and `task <ID>`.
4. Never start a `blocked` or `human_gate` task.
5. Write a short plan. Each step has its check: `step -> verify: <script | test | lint | screenshot>`.
6. State assumptions. Ask the owner only when the answer changes what a child sees or does.

## 2. Coding discipline
- Think before coding. Name assumptions and two readings when both exist.
- Simplicity first. The smallest change that meets the acceptance points. No flags, abstractions or dependencies nobody asked for.
- Surgical changes. Only the task's `allowedPaths`. No reformatting or renaming nearby code. Mention unrelated dead code; do not delete it.
- Goal-driven. Define the check first. "Make it work" is not a check.

## 3. Hard rules (a violation fails the task)
1. Child audience aged 4 to 7. No parent panel, gate or report. No Google or email sign-in; sign-in is the mock phone + OTP flow.
2. No payment, subscription, ads, coin shop, lives, lost streaks or punishing timers. Errors are amber, not red.
3. Four characters only: Ava, bunny, squirrel, fox.
4. Persian RTL, Vazirmatn, Persian numerals, local art. No runtime assets from the network.
5. Touch targets: child screens use 64 dp targets and a 72 dp main CTA (decision DG-3, 2026-11-01); the `minTouch` token (48) stays only for adult-facing form controls.
6. Reduced motion honoured; large fonts reflow, not shrink.
7. Existing IDs never change without migration and validator. Content stays `approvedForRelease=false` until a human approves it.
8. No APK/AAB unless the owner asks in that request. Native unit tests and lint are allowed.
9. Never commit to, push to or merge into `main` without the owner's written approval.
10. Do not copy another product's visuals, mascot, sounds, copy or brand.

## 4. Code conventions
- Separate content, state/actions, repositories and UI. A Composable takes state and actions only.
- Generated files (`Catalog.kt`, resources, payload types) come only from their generator. Run `python3 scripts/sync-android-resources.py --check` after content or resource changes.
- Copy comes from `design/strings.fa.json` (with `source`); art only from `design/assets.manifest.json`; sizes and colours from `design/design_tokens.json` and `DesignTokens.kt`. Do not hardcode Persian text or colours in Composables.
- Audio only through the injectable audio interface with a stable ref and a visible text fallback. No activity is completable by audio alone.
- Every screen has a visible way back. Every state (loading, empty, locked, error, done) is a real screen.
- Every control has a semantic label or visible Persian text. Feedback never relies on colour alone.

## 5. UX standard (child 4 to 7)
Tags: [evidence], [analysis], [judgement]. Most items here are [judgement] until tested with children aged 4 to 7.
1. One main action per screen, large, in the thumb zone.
2. A path of nodes; the next unlocks when the previous completes. The current node is largest. Locked nodes say why in text.
3. Lessons have 3 to 6 steps; each step has one goal.
4. Immediate feedback in several channels: shape or icon, short Persian line, optional sound. Wrong: amber outline, gentle hint, retry.
5. Recognition before recall. Building words before writing.
6. Praise effort, not ability.
7. Every spoken prompt has a replay control.
8. A wrong answer never ends a lesson, removes anything, starts a timer or says a streak was lost. After two wrong answers offer a clear skip.
9. Celebrations are short (at most 4 s), skippable, removed under reduced motion, granted once.
10. Progress is always visible with Persian numerals.
11. The companion guides, never judges.
12. Touch comfort: generous spacing, controls away from screen edges and the system bars.
13. Short friendly sentences. Nothing addressed to an adult.
14. The same button in the same place with the same feedback on every step type.

UX check before reporting: Can a four-year-old finish the screen with one tap per decision? Is the primary action the largest element and within thumb reach? Does every state exist as a real screen? Would a child know what to do without reading?

For any claim about how children learn, cite a source you have read, or label it as a design judgement. Open question for every child-facing choice: a test with real children.

## 6. Verification (before any "done")
1. Run the checks the task names: `python3 scripts/harness.py run-gates <ID>`, plus the relevant validators (`validate-content-contract.py`, `validate-audio-inventory.py`, `sync-android-resources.py --check`).
2. Native tests and lint (`testDebugUnitTest`, `lintDebug`) on a machine with JDK 17, Android SDK 35 and Gradle 8.11.1. If the toolchain is missing, say so; do not claim success.
3. Robolectric is not a device. TalkBack, IME, TTS, startup and jank need a real device.
4. Label every claim `ran` or `read in code`. List what you did not run.
5. Record real results in `harness/WORKLOG.md` and `docs/PROGRESS_FA.md`.

## 7. Git
- One branch per phase from the base the owner names: `phase-<n>-<topic>`.
- Commit `feat(<id>): <title>` with a body of what changed and what ran. Stage only the task's files.
- Push the branch. If refused, write `git format-patch` and `git bundle` files and report their paths. Never force-push. Never delete what the task does not name.

## 8. Report (in Persian, in this order)
1. Branch, commit hash, push status.
2. Files changed, one line each.
3. Checks, each `ran` or `read in code`.
4. UX check answers.
5. Deviations and reasons.
6. Open decisions for the owner.
7. Stop and wait.

## 9. Stop and ask when
- A requirement conflicts with a hard rule or the harness.
- A change would add a parent, account, payment, ad or email element.
- The task is `blocked` or a `human_gate`.
- A product choice changes what a child sees and no document settles it.
- The repo state does not match what the brief expects.
