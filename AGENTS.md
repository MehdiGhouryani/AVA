# AVA development contract

## Product and architecture invariants
- Product target: Native Android, Kotlin + Jetpack Compose in `android/`. Root React `src/` is a historical reference; never expand it as the production app.
- Audience is children only. No parent area, panel, gate, report, ads, payment, subscription, coin shop, lives, lost streaks or punishing timers.
- The only characters are Ava, bunny, squirrel and fox. Preserve existing Persian copy, RTL, Vazirmatn, local tokens and artwork. Never render reference screenshots as UI.
- Sign-in remains mock phone + OTP. No Google/email login, real SMS, network permission or external runtime assets without an explicit product decision and required approval.
- Child screens use 64 dp touch targets and a 72 dp primary CTA; adult-facing form controls keep the 48 dp minimum. Check widths 320/360/390/412 dp and font scales 1/1.3/1.5.
- Respect reduced motion and large fonts. Errors use the approved amber treatment, never red.
- UI receives state and actions only. No I/O, repository, database or network inside a Composable.
- Content IDs for lessons, tasks and audio refs are stable. Changing an existing ID requires migration and validation. Keep `approvedForRelease=false` until human approval.
- CMS is a private authoring/review/release tool. Android never connects directly to Payload, PostgreSQL, GraphQL or S3.

## Development and verification
- Read `docs/README.md` and the roadmap before changes. Use `.agents/skills/ava-app-engineering/SKILL.md` for engineering and `.agents/skills/ava-duolingo-learning/SKILL.md` for learning-flow/content work. For visual work, perform focused UI UX Pro Max searches and consult Claymorphism guidance. Existing AVA tokens, typography and local artwork override generic skill palettes/fonts.
- Keep pinned third-party skills and licenses intact. Project skills are bundled guidance, not personal installations.
- New original code must separate content, state/actions, repositories and UI. Generated files must be changed only through their canonical generator.
- Use a `phase-<n>-<topic>` branch by default. Commit/push/merge directly to `main` only with the owner's explicit written approval for the exact scope; keep any exception within that scope.
- Run `scripts/sync-android-resources.py --check` and tests appropriate to the actual change. Never equate local Robolectric rendering with device testing.
- Do not claim earlier build evidence for a new change. Update `docs/PROGRESS_FA.md`, `docs/NATIVE_VERIFICATION.json` and `harness/WORKLOG.md` only with actual new results. Do not rerun unrelated checks for documentation-only edits.
- Do not invoke `assembleDebug`, `assembleRelease`, bundle tasks, or produce/deliver APK/AAB unless the owner explicitly requests a package in that request. Native unit/UI tests are allowed.

## Automated continuation contract
Coding agents such as Zcode/Codex/Claude must start from `ZCODE.md`, validate `harness/roadmap.json`, select exactly one ready task, obey its `readFirst`/`allowedPaths`/acceptance/gates and record actual results in `harness/WORKLOG.md`. Never start blocked or human_gate work, forge approval, auto-mark unexecuted evidence passed, or modify task dependencies to bypass a gate.

## Delivery
Prefer source-only increments. Deliver a versioned source ZIP with actual results, open gates, a clean change record and a persistent download. Record the next action before ending each source increment.
