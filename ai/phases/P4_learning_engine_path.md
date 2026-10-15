# P4 — Adaptive learning engine, path and rewards

Goal: the heart of the app — the stage runner, the on-device adaptive engine (U-032, C-121, C-122), the zigzag path of worlds (U-015, U-033), station/world intro, celebration and rewards (C-105, C-129).
Read the P0 outputs `docs/pedagogy/08_review_engine_and_adaptation_fa.md`, `docs/pedagogy/09_assessment_spec_fa.md`, `docs/pedagogy/14_learner_model_and_evidence_fa.md`, and `data/content_bank/adaptation_config.json`; if any is absent, report the missing P0 prerequisite. Review screen records S06-B, S07, S30, S13, S17, S16, S26, S05/S05r. `design/characters/roka_fox/ROKA_CHALLENGE_SYSTEM_FA.md` is not present in this snapshot or assigned as an earlier output; do not cite it as existing. T-P4-09 must create an explicitly labelled draft challenge rule from available sources or remain blocked on the owner if a product decision is needed.
The child never sees failure, locks or adaptation: everything is invisible and gentle (UX principles).

Phase needs: T-P0-14, T-P0-15, T-P0-16, T-P0-21, T-P2-02

### T-P4-01 — Stage runner (S30)
Type: TASK
Do: A stage state machine independent of UI: loads a stage (schema from P0), renders by activity family via a registry, applies the scaffold ladder SC0–SC7 with fade, retry **with variation** (new item, fewer distractors or changed response mode — never the same question), classifies provisional errors (raw vs interpretation), records evidence, computes the stage end state and exit decision (ADVANCE/CONSOLIDATE/REMEDIATE/DIAGNOSE/REVIEW_LATER/INSUFFICIENT_EVIDENCE). Pause/resume at any point; abandoned ≠ weak.
Done when: unit-test list derived from the P0 worked examples; unknown activity families render a safe placeholder in debug and are skipped with a log in release.

### T-P4-02 — Learner-state engine
Type: TASK
Do: Implement skill states (UNSEEN…AT_RISK) and mastery rules EV1/EV2/EV3 from `adaptation_config.json`; update states from evidence; never infer mastery from completion, speed, one attempt or full-scaffold success.
Done when: the 10 worked examples of the P0 engine spec pass as unit tests (write them even if the owner runs them later).

### T-P4-03 — Review engine
Type: TASK
Do: Item/skill-level review scheduling R0–R5, priority formula from config, cue fading (cue-heavy → reduced → open), retrieval with corrective feedback (not restudy), review events stored; "Daily practice" session = retrieval warm-up + main stage + short exit retrieval.
Done when: scheduler is deterministic for a fixed clock; config changes take effect without app update.

### T-P4-04 — Stage selection and remediation routing
Type: TASK
Do: Next-best-stage selection over the spine (C-121) using prerequisites, due reviews, error relevance, novelty fit, fatigue (MVP + adaptive scoring), remediation decision tree and diagnostic mini-stages, cold-start from age/track with embedded diagnostics (no exam). Session fatigue policy: lower interaction load, keep the learning target.
Done when: selection explains itself in a debug log (`why_now` per chosen stage); a deterministic simulation script plays 3 synthetic children through W01 without dead ends.

### T-P4-05 — Config loader (bundled defaults + server override)
Type: TASK
Do: Load `adaptation_config.json` from assets; override from the server when available (CMS-07); validate and fall back safely; version recorded in every evidence record.
Done when: invalid remote config can never crash or change child-visible behaviour unexpectedly.

### T-P4-06 — Zigzag path of worlds (S06-B)
Type: TASK
Do: Per track, a scrollable zigzag path through the worlds: world scenery, spine stage nodes with large icon-only targets, a gentle "review" node type fed by the adaptive layer, current-node highlight, locked/free/paid states from entitlement, companion peek, auto-scroll to current, tablet layout. Follow the 7 states of S06-B.
Done when: all states reachable with mock data; offscreen nodes are lazy.

### T-P4-07 — World intro (S07) and celebration (S13)
Type: TASK
Do: World intro (scene, voice intro, start), end-of-stage/world celebration (stars 1–3, leaf, character cheer, confetti within motion budget). Feedback copy follows the structure [what to notice] + [next action] and is never "wrong".
Done when: states of S07 and S13 implemented.

### T-P4-08 — Reward engine, S17 weekly flowers and daily goal, S16 album
Type: TASK
Do: Stars and leaves are engagement-only (C-129); weekly flowers/daily goal without streak-loss or shame; album; avatar-item unlock hook by milestones.
Done when: `docs/reward_rules.md` written; S17 and S16 states implemented; nothing shows rewards as mastery.

### T-P4-09 — Roka challenge per world (U-017)
Type: TASK
Do: If an owner-approved Roka challenge system document exists by this phase, implement from it. Otherwise create `design/characters/roka_fox/ROKA_CHALLENGE_SYSTEM_FA.md` as a draft based on available pedagogy sources, label every unsourced rule `DESIGN`/`OPEN`, and do not present it as approved. The challenge should be a **transfer stage** (same skill, new item/context) so it can yield transfer evidence; it must never block progress. Owner approval is required before treating character-specific rules as locked.
Done when: challenge flow works with stub activities.

### T-P4-10 — Placement and track suggestion (S05/S05r)
Type: TASK
Do: Embedded diagnostics replace an exam: the first sessions sample the skill graph; S05/S05r become a friendly "let's play and see" screen. Track-change suggestion (C-120) is shown to the parent only.
Done when: a child who is ahead or behind in one lane gets different next stages without any visible test.

### T-P4-11 — S26 end of path
Type: TASK
Do: End-of-path screen per track, replay options, calm "more coming soon".
Done when: all 4 states done.

### T-P4-12 — SMOKE P4
Type: SMOKE
Do: Smoke checklist: scroll the path, play a stage, make deliberate mistakes (retry varies, hints fade), kill app mid-stage → resume, run the simulation script, change config and see the behaviour change. Fix reported errors first.
Done when: checklist in `ai/STATE.md` if it exists; otherwise in the hand-off.
