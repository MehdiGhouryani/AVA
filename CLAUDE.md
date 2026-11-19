# CLAUDE.md — AVA

This is Claude Code's entry point, not a second project contract. **`AGENTS.md` is the canonical source** for product invariants, architecture, branch policy, verification and delivery rules.

## Start here
1. Read `AGENTS.md` and `ZCODE.md` completely.
2. Read `docs/PROGRESS_FA.md`, then `docs/README.md` and the relevant roadmap/playbook.
3. Follow the harness start procedure in `ZCODE.md`: validate, inspect status, select exactly one ready task, then read its `readFirst` and acceptance gates.
4. Change only that task's `allowedPaths`. Never start a `blocked` or `human_gate` task or bypass a dependency.
5. Run relevant checks, label each claim `ran` or `read in code`, and record actual results in `harness/WORKLOG.md`.
6. Report in Persian with completed work, checks actually run, open gates and the next action. Then stop for the owner.
