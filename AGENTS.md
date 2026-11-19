# AVA development contract

## Rule ownership and precedence
- This file is the canonical contract for product, architecture, security and repository-wide engineering rules.
- `harness/roadmap.json` owns task status, dependencies, `allowedPaths`, acceptance criteria and executable gates. It may narrow this contract, never weaken it.
- The selected phase playbook and task `readFirst` files add task-specific requirements. Apply the relevant project skill only for its specialist domain.
- `CLAUDE.md` and `ZCODE.md` are entrypoints, not independent policy sources. In a conflict, follow this file and the stricter valid task constraint; stop and ask the owner if the conflict cannot be resolved.

## Product and architecture invariants
- The product is the Native Android app in `android/`, using Kotlin and Jetpack Compose. Root React `src/` and Vite are historical prototype code: do not expand them or delete them merely because they are historical.
- Audience: children aged 4–7. No parent area/panel/gate/report, ads, payment, subscription, coin shop, lives, lost streaks or punishing timers.
- Only four characters are allowed: Ava, bunny, squirrel and fox. Preserve Persian copy, RTL, local Vazirmatn, design tokens and artwork. Never render reference screenshots as product UI or copy another product's branding, mascots, sounds or fonts.
- Sign-in remains mock phone + OTP. Do not add Google/email login, real SMS, network permissions or external runtime assets without an explicit product decision and required approval.
- Child-facing touch targets are at least 64 dp and the primary CTA is 72 dp; adult-facing form controls retain the 48 dp minimum. Check widths 320/360/390/412 dp and font scales 1/1.3/1.5.
- Respect reduced motion and large-text reflow. Errors use the approved amber treatment, never red.
- A Composable receives state and actions only. Keep I/O, repositories, databases, network and file access outside Composables.
- Lesson, task and audio-reference IDs are stable. Changing or removing an existing ID requires migration and validation. Keep `approvedForRelease=false` until human approval.
- CMS is a private authoring/review/release tool. Android consumes validated, approved local content; it must never connect directly to Payload, PostgreSQL, GraphQL or S3.
- Human audio needs technical and educational review plus a valid delivery artifact and independent publishing approval. Device TTS is optional fallback; every listening activity needs a written hint and must remain completable without narration.
- Never place secrets, credentials, signed URLs, raw child audio or personal data in source control, logs or evidence.

## Execution protocol
- Read `docs/README.md` for the document map. Let the harness validate the roadmap; read only the selected task's `readFirst` files, its phase playbook and the applicable skill instead of loading every project document.
- Run `python3 scripts/harness.py validate`, then `status`, then `next` unless the owner supplied a task ID. Inspect the selected task with `task <TASK_ID>`.
- Execute exactly one task per cycle. It must be automation-allowed, in `ready` or `in_progress`, and have all dependencies completed. Never start `blocked` or `human_gate` work, forge approval or bypass a dependency.
- Record the baseline with `git status` and the current branch. Preserve pre-existing owner changes; never overwrite, reset or discard them.
- Edit only the task's `allowedPaths`. Do not broaden scope, change unrelated task state/dependencies or weaken acceptance criteria to make work pass.
- Generated files are changed only through their canonical generator. Before adding a dependency, permission, service or schema, document the need and impact in the relevant ADR and obtain required approval.

## Verification and evidence
- Run the smallest relevant check first, then every task gate with `python3 scripts/harness.py run-gates <TASK_ID>`. Run resource sync check when content/resources are affected.
- Update `harness/WORKLOG.md` and project evidence only with commands actually run and results actually observed. Label code inspection as `read in code`; never describe it as an executed test.
- Do not reuse old test/build results as proof for a new change. Robolectric and local screenshots are not real-device QA.
- Do not invoke `assemble*`, `bundle*`, or create/deliver APK/AAB unless the owner explicitly requests a package in that same request. Unit/UI tests and lint remain allowed.

## Git and delivery
- Default to a `phase-<n>-<topic>` branch. Commit focused, reversible changes and push the working branch when authorized and available; never force-push.
- Direct work or push to `main` requires the owner's explicit written approval for that exact scope. Keep the exception inside that scope; it does not authorize product or UX changes.
- Source-only is the default. Create a versioned source ZIP only when the owner requests one or the task's acceptance/delivery explicitly requires it—not for every small cycle. Use `scripts/package-source.py`; inspect the archive and exclude secrets, caches, build outputs and APK/AAB.
- Report in Persian: task and branch/commit, changed files, checks actually run, checks not run, blockers and the next action. Stop at the task boundary.