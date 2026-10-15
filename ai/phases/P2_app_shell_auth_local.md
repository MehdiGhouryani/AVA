# P2 — App shell, auth UI, profiles, local data

Goal (updated for U-031…U-033): the first minutes of the app, final-quality, with a data layer that already carries the adaptive learner model: splash → onboarding → phone login → child profile → home skeleton.
Auth is **mobile number + OTP only, registration from the start** (U-010). The server comes in P3; here use a repository interface + fake implementation.
Prerequisites: P0 must have produced `data/screens_v2.json` and current docs under `docs/spec/`; P1 must have supplied the foundation needed for implementation. Before building a screen, read its existing `design/screens/<ID>/` brief/mockup/NOTES if present; those folders are not in this snapshot yet. If `data/screens_v2.json` or required current specs are missing, stop and report the prerequisite instead of using `data/screens_v18.json` as if it were final.

Phase needs: T-P0-03, T-P0-15, T-P0-22, T-P1-03

### T-P2-01 — Routing, app state, session guard
Type: TASK
Do: go_router with typed routes for every screen id in this phase, Riverpod providers, `SessionState` (loggedOut / loggedIn(parent) / profileSelected). Redirect rules: no content before login (no guest). Deep-link-free.
Done when: route table documented in `docs/routes.md` with screen ids.

### T-P2-02 — Local database schema v1
Type: TASK
Do: Drift schema + migrations derived from `docs/pedagogy/14_learner_model_and_evidence_fa.md` and `data/content_bank/learner_model.schema.json` (P0): `parent`, `child_profile` (max 3, includes `track` TRK-A/B/C), `skill_state` (per skill: state, counters, last_seen, confidence), `evidence_raw` (attempt-level, rolling window, separate from `evidence_interpretation`), `stage_attempt` (exit decision), `review_item` (due dates, R-level, cue level), `reward` (engagement only, C-129), `setting`, `sync_queue` (idempotent operations), `content_version`, `config_cache` (adaptation config). Data-minimization (C-123): raw evidence stays on device. Add `docs/data_model.md`.
Done when: every table/enum traces to the P0 learner-model doc; migration v1 defined; model doc written.

### T-P2-03 — Content repository from bundled JSON
Type: TASK
Do: Write `ai/tools/sync_assets.py`, a script that copies `data/content_bank/*.json`, `docs/pedagogy/06_stage_catalog/*.json` and `adaptation_config.json` into `apps/mobile/assets/content/` and `apps/mobile/assets/config/`. Write loaders with JSON-schema validation and a `ContentRepository` interface (bundled now, server pack later). Enforce the build mode (C-131): `debug` all statuses, `pilot` reviewed + approved, `release` approved only; stages that reference a non-allowed item are skipped with a log.
Done when: loader returns worlds, stages, symbols, words, texts typed (using the W01 seed catalog from T-P0-22); validation errors are readable; unit tests cover the three build modes.

### T-P2-04 — S01 Splash and S02 Onboarding (3 scenes)
Type: TASK
Do: Implement exactly the states/interactions/acceptance of S01 and S02 from `data/screens_v2.json`. Voice-first (no reading needed), skip-safe, tablet layouts, companion placeholder = Pofi (C-111). All strings from `apps/mobile/assets/l10n/fa.json` (draft copy allowed, C-133).
Done when: both screens implement every state listed in the registry; acceptance bullets checked one by one in `ai/SESSION_LOG.md` if it exists; otherwise include an explicit checklist in the hand-off (do not claim a log file was updated).

### T-P2-05 — Phone login: S03a, S03b (and S03d merge case)
Type: TASK
Do: Mobile-number screen (Persian digits accepted, validation, clear errors), OTP screen (6 digits, resend timer, wrong-code states, no lock-out shaming), `AuthRepository` interface + `FakeAuthRepository` (code 123456 in debug only). S03d: when the device already has local progress and the account differs/same, implement the merge decision per spec. S03c/S03e are REMOVED — do not build.
Done when: all states of S03a, S03b, S03d covered; fake repo swappable for the real one in P3.

### T-P2-06 — S04 Child profile and S27 Profiles
Type: TASK
Do: Create/edit child profile (name or nickname, age → default track TRK-A 4–5 / TRK-B 5–6 / TRK-C 6–7 per C-120, companion pick placeholder), max 3 profiles, switch profile, change track (parent confirmation, OQ-18), delete profile with parent confirmation. Collect no photo, no surname, no location.
Done when: all states of S04 and S27 implemented against Drift.

### T-P2-07 — S21 Special states and UX-04 offline/connection lifecycle
Type: TASK
Do: Global handling of: offline banner (calm), sync pending, maintenance, update required, low storage, expired entitlement placeholder. One `AppStatusBanner` + full-screen variants per spec.
Done when: states reachable from a debug menu for review.

### T-P2-08 — S22 First-run preparation (reduced)
Type: TASK
Do: With bundled assets (U-009) S22 only seeds the DB, validates content, and shows a short friendly progress screen. Keep the full download states in code behind a feature flag for later.
Done when: first run seeds DB idempotently; second run skips.

### T-P2-09 — Home skeleton (S06-B shell only)
Type: TASK
Do: Placeholder path home showing world/stage nodes from the bundled stage catalog (static positions), profile chip, settings entry. Real path in P4.
Done when: logged-in user with a profile lands here and can open settings and switch profile.

### T-P2-10 — SMOKE P2
Type: SMOKE
Do: Smoke checklist: login with fake OTP, create profile, kill app, reopen → still logged in and on home; airplane mode behaves calmly. Fix reported errors first.
Done when: checklist in `ai/STATE.md` if it exists; otherwise in the hand-off.
