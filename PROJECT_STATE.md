# AVA — project state (knowledge file). Updated 2026-10-31.

Read this first. If the owner's latest message disagrees with it, trust the owner and update the plan.
This file replaces the earlier version that described the React web line (`AVA-Ui`, phase 2/3).

## 1. Source of truth
- Repo: `https://github.com/MehdiGhouryani/AVA.git`, branch `main`, HEAD `cd9c581` ("Initial clean push", 2026-10-28). One commit, one branch.
- Product: **native Android**, Kotlin + Jetpack Compose, `android/`. Version 0.6.1, versionCode 16, minSdk 26, targetSdk 35.
- `src/` (React) is a historical reference. `AGENTS.md` and the harness forbid expanding it.
- `PAGES_PLAYBOOK.md` (React phase 3) is **superseded**. Do not run it. Its product ideas are kept as candidates (section 5).
- `AVA-Ui` and its branch `phase-2-s04` were not found in `AVA.git`. Treat them as unrelated history unless the owner says otherwise.

## 2. Working contract (in the repo)
Start from `ZCODE.md`, then `AGENTS.md`, `harness/roadmap.json`, the phase playbook in `harness/phases/`.
```
python3 scripts/harness.py validate
python3 scripts/harness.py status
python3 scripts/harness.py next
```
One ready task per cycle. Obey its `readFirst`, `allowedPaths`, acceptance and gates. Log real results in `harness/WORKLOG.md`.
Never start `blocked` or `human_gate` tasks. Never build APK/AAB unless the owner asks in that request. `testDebugUnitTest` and `lintDebug` are allowed.

## 3. What exists (read in code unless marked ran)
- 22 native destinations: Splash, Onboarding 1/2, Phone, OTP, Home, LessonDetails, Lesson, Library, StoryDetails/Reader/Question/Complete, Practice, Progress, Profile, EditProfile, Settings, Help, About, Privacy, Terms.
- Content (`design/content.fa.json`, schema v3): 1 level, 3 units, 6 lessons, 17 tasks (7 kinds), 2 stories (9 pages). `approvedForRelease=false`.
- 104 tests exist in source. Not compiled, not run in any session so far.
- CMS prototype (`cms/payload`): Payload 3.92.1, PostgreSQL, S3-compatible storage, 5 collections, 6 roles, 18 domain tests. Not run against a database.
- Audio: 22 needs in `design/audio.inventory.json`; no audio files; device TTS is the fallback.
- Art: 16 images in `design/assets.manifest.json`. No word pictures.

## 4. Verified on 2026-10-31 (ran, in a sandbox with no Android SDK; table shows `main` as cloned)
| Check | Result |
|---|---|
| `scripts/harness.py validate` | passed, 14 tasks, 4 phases |
| `scripts/validate-content-contract.py` | passed (3 units, 17 tasks) |
| `scripts/validate-audio-inventory.py` | passed (22 assets, 12 prompts) |
| `scripts/validate-harness.py` | passed |
| `scripts/sync-android-resources.py --check` | passed (16 images, 263 strings) |
| `scripts/audit-project.py` (and so `npm run check`) | **FAILS** on `main`: it expects 3 files under `.aistudio/artifacts/...` that the clean push removed. Fixed on branch `phase-0-align` (section 8) |
Not run: Gradle, unit tests, lint, any device, the CMS server.

## 5. Plan (phases)
The precise plan, with steps, gates and risks, is `docs/EXECUTION_PHASES_FA.md`. This list is only a summary.
0. Align: this file, `CLAUDE.md`, the skill, owner decisions in section 6.
1. Prove the baseline: harness task `061-native-toolchain` on a machine with JDK 17, Android SDK 35, Gradle 8.11.1. Fix the `audit-project.py` failure first.
2. Content truth (parallel): `061-educator-review` (human gate) using `docs/CURRICULUM_BLUEPRINT_FA.md` as the proposal.
3. Child UX on a device: owner explicitly asks for a debug APK; run `docs/DEVICE_QA_FA.md`.
4. Audio: `080-human-audio-rights`; then the CMS pipeline (`070-*`) or a simpler bundled-manifest path (owner decision).
5. Offline sync (`071-*`): only if content must change after release.
6. Release: `080-content-expansion`, `080-account-privacy-operations`, `080-device-accessibility-qa`, `080-release-readiness`.
Candidates from the old playbook (need educator review before they are activities): bubble, match, find, album of discoveries, weekly flowers, companion colours.

## 6. Open decisions for the owner
- Is `AVA.git` now the only repo? Is `AVA-Ui` retired?
- Touch size: tokens say `minTouch` 48 and form CTA 60; the old rule says 64 and 72. Pick one for child screens.
- Phone consent checkbox: confirm the child sees no wording addressed to an adult (SCREEN_MATRIX 0.3.6 mentions "والد").
- Words بابا/مادر versus the strings rule "no mention of parents": does the rule cover vocabulary?
- CMS now or later: Payload pipeline versus bundled audio manifest for version 1.
- Fix for `audit-project.py`: restore the reference images, or update `design/screen-references.audit.json` and the script.
- Educator decisions listed in section 8 of `docs/CURRICULUM_BLUEPRINT_FA.md`.
- Legal text, real SMS/account service, logo wordmark: unchanged and still open.

## 7. Decisions already made (do not reopen without the owner)
- Native Android is the product. Persian RTL, Vazirmatn, local art.
- No parent panel, payment, ads, social features, red errors, punishing timers, lives or lost streaks.
- Four characters only: Ava, bunny, squirrel, fox.
- Source-only increments; APK/AAB only on explicit request.
- Existing lesson and task IDs never change without migration and validator.

## 8. Phase 0 status (branch `phase-0-align`, from `main` `cd9c581`)
Decisions DG-1 to DG-4 were not answered one by one. The owner said "we can start", so the recommended defaults are **assumed** and stay revisable:
- DG-1: `AVA` is the only repository; `AVA-Ui` is archived.
- DG-2: Track B (bundled content and audio in v1; CMS and sync after v1). **Not applied.** `docs/adr/0004-content-delivery-v1-bundled-fa.md` is *proposed*; the four dependency changes are a separate patch the owner applies only if the ADR is accepted.
- DG-3: child screens use 64 dp targets and a 72 dp main CTA (used in phase 2; no code changed in phase 0).
- DG-4: option B. The three stale `.aistudio` entries were moved from `files` to `removedEntries` in `design/screen-references.audit.json`; the audit trail is kept.
Added: `CLAUDE.md`, this file, the skill `.agents/skills/ava-app-engineering/`, `docs/CURRICULUM_BLUEPRINT_FA.md`, `docs/EXECUTION_PHASES_FA.md`.
No file under `android/`, `design/content*`, `cms/` or `src/` changed.
