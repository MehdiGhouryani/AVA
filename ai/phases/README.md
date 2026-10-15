# AVA phase prompts — index and safety rules

These files are an implementation backlog. They do **not** prove that the code, output documents, tools, or decision records mentioned in them already exist. Read `AI_START_HERE.md` and `docs/PROJECT_STRUCTURE_FA.md` first. Inspect the repository before acting.

## Snapshot checks before any phase

- The current snapshot does not contain `ai/DECISIONS.md`, `ai/FILE_MAP.md`, `ai/STATE.md`, `ai/PROTOCOL.md`, `ai/SESSION_LOG.md`, `ai/OPEN_QUESTIONS.md`, `ai/specs/`, `ai/tools/`, `inputs/`, or `design/`. A task may create a missing item only when its deliverables explicitly call for it.
- References such as `U-xxx`, `C-xxx`, and `OQ-xx` in these prompts are not independently verified because the decision ledger is absent. Do not call them locked decisions unless supported by an available document or owner instruction.
- Existing pedagogy source files are `docs/pedagogy/01_master_educational_spec_v2.md` and `docs/pedagogy/02_stage_content_design_guide_fa_v2.md`. In task text, shorthand `01` and `02` refers to these full filenames. Planned `03…14_*` files are outputs to be created during P0, not existing dependencies.
- In `docs/spec_sources_v1_8/`, paths such as `canonical/`, `prototype_source/`, `baseline_data/`, and `design_handoff/` belong to the historical input package. Current paths are documented in `docs/PROJECT_STRUCTURE_FA.md`. Never follow historical paths as active filesystem instructions. Preserve the v1.8 source documents.
- Authentication target is phone number + OTP. Do not implement email/Google login from legacy copy or v1.8 screen records. An optional email field, if ever approved, belongs only in profile settings/completion, not authentication.
- Before starting a phase, compare each prerequisite with the actual files. If a prerequisite is absent and is neither created earlier nor included in the current task, stop that dependent task and document the exact blocker instead of silently substituting a historical file.

## Phase order

Run in this logical order. File-system alphabetical sorting places `P10` between `P1` and `P2`, so P10 is after P9.

1. `P0_prep_style_gate.md`
2. `P1_design_system_foundation.md`
3. `P2_app_shell_auth_local.md`
4. `P3_backend_admin.md`
5. `P4_learning_engine_path.md`
6. `P5_activities_games.md`
7. `P6_characters_avatar.md`
8. `P7_parent_billing_legal.md`
9. `P8_content_production.md`
10. `P9_hardening_pilot_release.md`
11. `P10_post_v1_backlog.md` — post-V1, after P9.

These files are plans, not evidence that a feature is built. Verify implementation and tests before reporting completion.
