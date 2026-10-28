# AVA Native Android — 0.6.1 source increment
Kotlin + Jetpack Compose. Open android/ in Android Studio with JDK17 and Android SDK35. Root React/Vite remains the original reference.

```sh
python3 ../scripts/sync-android-resources.py --check
./gradlew testDebugUnitTest
# Run lintDebug when relevant. Packaging is on explicit request only.
```
Windows: gradlew.bat. Set sdk.dir in an untracked local.properties or use Android Studio. Gradle wrapper and its distribution checksum are pinned. Build versions are a compatible development baseline, not a claim of newest/store-ready versions.

Mock login: valid Iranian mobile number, consent, code 123456. No real SMS/backend account and no INTERNET/MICROPHONE permission. Phone/consent use transient SavedStateHandle; phone and OTP are not stored in DataStore. A mock signed-in flag persists locally, not a credential. Returning users open Home.

MVP: 22 destinations, 6 development lessons/17 matching activities, 2 original stories/9 pages, mistakes/learned review, profile/preferences/progress. DataStore persists progress and resume positions. Failed storage has visible retry; resets and exits are confirmed. TTS uses an installed offline Persian voice or reports its absence; pronunciation/device playback needs device QA.

Run tests on the actual rebuilt source. Compose UI tests use Robolectric4.15.1 API35 native graphics; local PNGs under app/build/reports/native-ui are not device screenshots or golden comparisons. Some compact/large text cases intentionally scroll. Real IME/TalkBack/SMS autofill, lifecycle/performance and educator review remain release gates in ../docs/DEVICE_QA_FA.md.

Content and text sources: ../design/content.fa.json and strings.fa.json. Do not edit generated catalog/resource copies manually. No cloud sync or production authentication is implied. Read ../docs/REBUILD_CONTEXT_FA.md for reconstruction provenance and ../docs/NATIVE_VERIFICATION.json for actual results.

Debug-only QA intents (ignored by release builds):

```sh
adb shell am start -n com.ava.reading/.MainActivity --es qa_route Home
adb shell am start -n com.ava.reading/.MainActivity --es qa_route Phone --ez qa_offline true
```

Project audit needs Pillow: `python3 -m pip install -r ../scripts/requirements.txt`. The resource sync command itself uses only the Python standard library. Source can be repackaged with `python3 ../scripts/package-source.py /absolute/path/AVA-source.zip` after saving changes (tracked files in a checkout; filtered tree when extracted).

Current workflow: source-only increments. Do not build an APK/AAB at every step. Run focused tests for affected behavior and resource checks; keep the previous full-suite evidence separate when running only a subset. Package source and update progress/next actions. Device QA/release checks remain open until a device/package pass is requested.

0.3.2 preserves root-tab scroll state using a SaveableStateHolder limited to the four roots. Story text scrolls independently of its page controls; Back from the question returns to the last read page. Compact/large text tests cover Home, reader, profile/edit and library. The design inventory and remaining page specifications are in ../docs/PAGE_DESIGN_PLAN_FA.md.

0.3.3 separates HomeScreen, LessonScreens and PracticeScreen. Lesson actions and feedback stay outside the scrolling question; each new task resets question scroll. Character choices use a row on roomy layouts and words/large text use a vertical group of native selectable items. Last-correct-answer still requires Finish; results use actual activity counts and one primary next action. Review cards exclude unknown catalog IDs. Native TTS is stopped on task/result changes; hardware playback remains unverified.

0.3.4 refines the complete story flow. Library cards show fresh/reading/completed states and real resume progress, reflow for compact or large-text layouts, and use restrained soft depth. Story introduction has one state-aware primary action. Reader and question actions stay outside scrolling content; the last page names the question step, narration-off is explained, and choices use native RadioButton semantics. Completion is stored only after the explicit Finish action; rereading starts from page one. Offline Persian TTS availability still requires device QA.

0.3.5 completes Profile/EditProfile, Progress, Settings and information pages. Profile and progress counts validate catalog IDs, avatars use a labeled 2x2 RadioButton group, and progress exposes actual lesson/story/review state without XP or streaks. Settings use one full-row Switch target with Persian state descriptions and separate data/account operations. FAQ has expanded/collapsed semantics; privacy and terms expose development and review limits. `SettingsScreens.kt` separates settings/information UI from profile/progress UI.


0.4.1 introduces a deterministic five-item review planner. Valid mistakes are prioritized, available activity kinds are represented, and learned tasks rotate by the completed review count. Dedicated mistake and learned sessions remain available and are capped at five items. The product does not label this spaced repetition because no timing or mastery model is stored. This source increment does not add Internet or microphone permissions and does not build an APK/AAB.

0.4.2 establishes the content-platform boundary without adding a backend or network permission. The app remains seeded and offline. Two ADRs define the future private CMS → compiler/validator → immutable release → local activation flow and the human-reviewed audio workflow. `design/audio.inventory.json` inventories every current task/story audio need, and project audit rejects missing or stale content references. No APK/AAB is produced by this increment.

0.5.0 migrates the canonical seed from content v2 to v3 with Level/Unit/Skill hierarchy, stable `AssetRef`/`AudioRef`, story-page IDs and a hash-verified development `ContentRelease`. The v2 fixture and deterministic migration remain in source. Kotlin contracts define Bundled/PublishedLocal/Remote sources without a network implementation. Versioned per-task progress records attempts, correct answers, hints and last-seen time using an injectable clock; it is not yet presented as mastery or spaced repetition. The app remains offline and no APK/AAB is produced.

0.5.1 implements the local content runtime. ViewModel loads an active snapshot through `LocalContentRepository` before leaving Splash; production screens no longer read generated Catalog directly. Bundled content is the guaranteed fallback, a validated PublishedLocal candidate can stage and activate atomically, and rollback restores the previous snapshot. The store is an in-memory fixture in this increment, not a claim of process-persistent downloaded content. The offline compiler produces deterministic hash-indexed bundles and rejects tampering. No Internet permission, remote sync or APK/AAB is added.

0.6.0 introduces an activity registry for nine task kinds across visual, listening and word-construction families. A shared reducer and capability-driven renderer handle choice/token interactions, correction, hints and completion without CMS or URL dependencies. Versioned session snapshots preserve an in-progress answer through ViewModel recreation and reject stale selections. Structured audio requests carry stable `AudioRef`, fallback text and purpose through an injectable playback interface; Android TTS remains the current offline fallback, not approved human instruction. Canonical content still uses the original three kinds pending educator review. No Internet permission, CMS implementation or APK/AAB is added.

0.6.1 starts the educator-reviewable level-1 slice. A sound-awareness unit now sits between sign recognition and first words, adding initial/final-sound tasks with word-level audio fallback and two short blend tasks; dot activities use their specific kind. The canonical development scope is 3 units, 6 lessons and 17 tasks, still unapproved for production. A vendor-neutral CMS export contract, six-role separation policy, Payload-shaped fixture and publication blocker were added after an official Strapi/Payload comparison. Payload is selected only for the runnable prototype; no CMS SDK, network permission, human audio file or APK/AAB is added.
