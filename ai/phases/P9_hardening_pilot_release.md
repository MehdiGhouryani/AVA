# P9 — Hardening, pilot, release

Goal: a stable app on cheap Android phones and tablets, a 200-family pilot, and store-ready builds.

### T-P9-01 — Performance and low-end device pass
Type: TASK
Do: Profile list: cold start, path scroll, animations, memory with large images; reduce overdraw; target: 60 fps on the path, cold start under a number you record in `docs/perf.md` (measure on the owner's phone). Provide a profiling checklist.
Done when: budgets recorded and code changes applied.

### T-P9-02 — Tablet and accessibility QA
Type: TASK
Do: Review all screens against `docs/layout_rules.md` at four sizes; text scaling; contrast; touch targets; voice replay everywhere; fix defects.
Done when: QA table in `docs/qa_tablet_a11y.md` with zero open blockers.

### T-P9-03 — Error logging to our own server
Type: TASK
Do: Minimal crash/error reporting endpoint, no child identifiers, opt-out for parents, rate-limited; admin view.
Done when: end-to-end test list written (C-110).

### T-P9-04 — Release engineering
Type: TASK
Do: Signing config (owner holds keys; document how), versioning, flavors for Bazaar/Myket/site, obfuscation/shrinking, `docs/release_checklist_fa.md` for each store (assets, screenshots list, description draft, age rating, privacy URL).
Done when: checklist complete.

### T-P9-05 — Store assets and name check
Type: TASK
Do: Icon set and store assets come from the brand character (T-P6-07, U-037); feature graphic, screenshots plan using real screens; verify the name AVA/آوا is available on the stores; propose alternatives if not (U-001).
Done when: assets list + name check result in `docs/store_listing_fa.md`.

### T-P9-06 — Pilot plan (≈200 families)
Type: TASK
Do: Based on `docs/pedagogy/12_pilot_validation_plan_fa.md`: recruit message, consent text (incl. raw-evidence consent, OQ-19), test protocol (what to observe, 4-session script), feedback form, metrics list that respects privacy, bug triage flow, how thresholds and review intervals are tuned from the admin panel during the pilot, success criteria (OQ-16).
Done when: `docs/pilot_plan_fa.md` ready.

### T-P9-07 — Pilot feedback loop
Type: GATE
Do: After the owner pastes feedback/metrics into `inputs/pilot/`, summarize themes, prioritize fixes, update backlog.
Done when: prioritized fix list in `ai/STATE.md` if it exists; otherwise in the hand-off.

### T-P9-08 — Final SMOKE and release candidate
Type: SMOKE
Do: Final checklist across flavors; produce the release candidate zip.
Done when: checklist passed by the owner.
