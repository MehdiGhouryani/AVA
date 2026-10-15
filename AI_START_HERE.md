# AVA — AI START HERE

You are helping continue **AVA**, a Persian early-reading product for children aged 4–7 in Iran. The owner communicates in Persian. This file is the main agent entry point.

## 0. Snapshot boundaries — read before following any prompt

This archive is a **planning/content snapshot**, not a complete runnable product. Verify the filesystem instead of assuming a path exists. The mobile app, admin app, API, and standalone React setup are not currently runnable projects in this snapshot.

- Present: `data/screens_v18.json`, `data/state_contracts_v18.json`, `data/ui_layout.json`, learning baseline under `data/baseline/b/`, the two core pedagogy documents, the v1.8 source specifications, phase prompts, UI asset bundle, and a React UX prototype.
- `prototypes/ux_react/src/generated/screens.json` is a generated copy of `data/screens_v18.json`; do not edit it by hand. The synchronization script is `prototypes/ux_react/tools/sync_registry.py`.
- The v1.8 materials under `docs/spec_sources_v1_8/` are preserved historical sources. References inside them to `canonical/`, `prototype_source/`, `baseline_data/`, `design_handoff/`, or their old archive directories describe the **older source package**, not this repository's current paths. Do not follow those paths as though they exist here. Current paths are listed in `docs/PROJECT_STRUCTURE_FA.md`.
- The old character Dart/tools pipeline is quarantined under `packages/ava_ui_assets/legacy/character_pipeline/` because its schema and paths do not match current art. Do not run or import it as current code; design a migration only after owner-approved character decisions.
- The v1.8 registry and current prototype still contain legacy email-auth text/screens. They are not approval to build those flows. The target sign-in flow is **phone number + OTP**. Email is not a sign-in method; a future optional email field may only live in profile settings/profile completion if explicitly required. Do not implement Google/email login.
- `ai/DECISIONS.md`, `ai/FILE_MAP.md`, `ai/STATE.md`, `ai/PROTOCOL.md`, `ai/OPEN_QUESTIONS.md`, `ai/SESSION_LOG.md`, `ai/specs/`, `ai/tools/`, `inputs/`, and `design/` are absent from this snapshot unless a later task creates them. Some phase prompts name these as future inputs/outputs. A reference to a missing path is not evidence that the file exists.
- `U-`/`C-`/`OQ-` identifiers cited inside phase prompts are not independently verified by a decision ledger in this snapshot. Treat them as historical labels or proposed requirements unless supported by an available authoritative document or a clear owner instruction. Never invent their contents or status.
- Phase prompts are a **backlog**, not an audit of completed work. Read `ai/phases/README.md` before starting one; create missing files only when the selected task explicitly requires them.

## 1. Language rules

- Talk to the owner in **Persian**, short and clear.
- Write code, comments, and files under `ai/` in **English**.
- Product/pedagogy documents under `docs/` remain Persian unless a task says otherwise.

## 2. Resume procedure

1. If the project is a ZIP, extract it. The project root is the folder containing this file.
2. Read `README_FA.md`, `docs/PROJECT_STRUCTURE_FA.md`, and `ai/phases/README.md`.
3. Check whether `ai/tools/state.py` exists. Run `python ai/tools/state.py new-session --model "<your model name>"` only if the file exists; otherwise do not invent the command or claim it ran.
4. Read `ai/PROTOCOL.md`, `ai/DECISIONS.md`, `ai/OPEN_QUESTIONS.md`, `ai/STATE.md`, `ai/FILE_MAP.md`, `ai/SESSION_LOG.md`, and `inputs/INDEX.md` only when they actually exist. A missing file is a missing input, not an empty task list or an approved decision.
5. If `ai/tools/state.py` exists, run its `next` command and follow its output. Otherwise follow the explicit order in `ai/phases/README.md`, but first verify the selected phase's prerequisites against the actual repository. Do not jump to a later phase merely because an earlier phase prompt exists.
6. For learning-design work, read `docs/pedagogy/README.md`, then the relevant sections of `docs/pedagogy/01_master_educational_spec_v2.md` and `docs/pedagogy/02_stage_content_design_guide_fa_v2.md`. The planned `03…14` files are not present until P0 creates them.

## 3. Work loop

1. Update task state only if a real state tool and its documented schema exist. Otherwise track exact changed paths in the hand-off.
2. Before editing, inspect the actual current source, dependencies, generated files, and local references. Do not make changes based only on a stale path mentioned in a prompt.
3. Keep source data separate from derived outputs. In particular, do not edit `data/screens_v18.json` or `data/baseline/` when a task says to create a new version/copy. Never hand-edit `prototypes/ux_react/src/generated/screens.json`; regenerate it from its source.
4. For missing content or unresolved decisions, mark `OPEN`/`MISSING_CONTENT` and report the exact question. Do not guess facts, approved copy, curriculum, legal text, or owner decisions.
5. Do not wait for approval between ordinary tasks. Stop at a genuine owner gate or when prerequisites are unavailable; explain the blocker instead of silently bypassing it.

## 4. Hand-off and validation

1. Run only validation tools that actually exist. `ai/tools/verify_all.py`, `ai/tools/make_zip.py`, `ai/tools/state.py`, and `ai/tools/check_state.py` are not included in this snapshot.
2. If an output archive is requested, create it using available tools and include a concise Persian hand-off with changed paths, checks actually run, known blockers, owner inputs needed, and the archive link.
3. Never claim a command, test, build, state update, or decision was completed unless it was actually completed and evidenced.

## 5. Never

- Never implement a missing path based solely on its mention in a historical source.
- Never treat `docs/spec_sources_v1_8/` as current-path authority; preserve these source files and record supersession in new current docs instead.
- Never invent product facts, approved copy, pedagogy, curriculum, or content. If `ai/OPEN_QUESTIONS.md` is absent, do not claim it was updated; report unresolved items in the hand-off or create that file only as an explicit task output.
- Never silently change a verified owner decision. If the decision ledger is absent or the record cannot be verified, mark the decision **unverified**, show the evidence/conflict, and request a decision only when necessary.
- If `ai/STATE.md` is eventually created, keep it current; until then, do not claim it exists or is up to date.
