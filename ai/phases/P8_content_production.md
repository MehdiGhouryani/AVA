# P8 — Content production at scale

Goal: all 10 worlds × 3 tracks with real audio, images, texts, stories and approvals (U-022, U-033, C-130). Rich V1 content, produced by a governed pipeline (never ad-hoc generation).
Prerequisites: T-P0-10…T-P0-18 done (skill graph, orthographic policy, content bank, station map, stage schema, learner model, engine spec, assessment, generator prompt + `stage_lint`), Style Gate decided (T-P0-06), prompt system done (T-P0-07), TTS test results (T-P3-07), admin panel working (P3).
Rule: `docs/pedagogy/01_master_educational_spec_v2.md` §39 — no mass stage production before those documents exist. AI creates stage *specifications* and *candidates* only; `approved` is set by a human reviewer (C-127).

Phase needs: T-P0-12, T-P0-13, T-P0-18, T-P0-22, T-P3-06

### T-P8-01 — Finalize the reference spine and content bank
Type: GATE
Do: From `04a_reference_spine_fa.md` and the pedagogy docs, prepare the final content bank (symbols, words, texts) with orthography and provenance; one-page Persian summary for the owner and the teacher; approvals in `inputs/approvals/`.
Done when: approval file present; items imported through CMS-02 as `reviewed`/`approved` as the approver decides.

### T-P8-02 — Stage catalog generation (per world and track)
Type: TASK (repeat per world W01…W10; per track A, B, C)
Do: With `10_ai_stage_generator_prompt.md`, generate stage specifications for the world from the station map; each stage: one primary target, new/support/review/transfer separated, correctness rule, error routes, scaffold + fade, retry variation, evidence, review hooks, adult extension where meaningful, provenance. Run `stage_lint.py` and the 20-question check; failing stages stay `draft`. Return `MISSING_CONTENT` instead of inventing items.
Done when: zero lint blockers for the world/track; catalog file `docs/pedagogy/06_stage_catalog/<world>_<track>.json`; station-gate (`02` §89) report written.

### T-P8-03 — Curriculum integrity check per segment
Type: TASK
Do: Run the integrity check (`02` §99) over each finished segment: every important skill has introduction, review, transfer, assessment and a remediation path; backlog gaps to `docs/pedagogy/06_stage_catalog/BACKLOG.md`.
Done when: gaps are either fixed or listed with an owner decision.

### T-P8-04 — Audio generation batches
Type: TASK (per world)
Needs: T-P3-07
Do: Create slots in CMS-04. The owner produces the audio in batches outside the system and uploads it per slot (U-041). Reviewers listen in CMS-05, re-upload failures, publish. Keep rejected phrases and fixes in `docs/tts_lessons_fa.md`. Feedback lines follow [what to notice] + [next action].
Done when: every stage item has an approved audio file; letter sounds meet the quality bar or fall back to human recording.

### T-P8-05 — Image generation batches
Type: TASK (per world)
Needs: T-P0-06, T-P0-07
Do: Using `design/prompt_system/`, produce batch manifests (CSV), the owner generates images, files are named by id and imported through CMS-03; reviewers check consistency. Reuse scenes across stages with different interactions (asset reuse, `02` §60).
Done when: every item/scene has an approved image; file-size budget respected.

### T-P8-06 — Texts, stories and poems (two lanes, C-126)
Type: TASK
Do: Controlled instructional texts made only of taught units (instructional lane) and stories/poems for pleasure (story lane, interactive reading prompts). Verify rights for every poem/song/story (public domain or owner-created) in `docs/content_rights.md`.
Done when: rights table complete; no unclear-rights item shipped; every controlled text passes the "linguistically natural + meaning available" check (`02` §53).

### T-P8-07 — Content QA scripts
Type: TASK
Do: `ai/tools/content_qa.py`: fails the build when an item or stage is not approved, audio/image missing, a controlled text uses an untaught unit, a stage fails `stage_lint`, duplicate ids, or size budget exceeded.
Done when: passes on the final content.

### T-P8-08 — Pack builder and size budget
Type: TASK
Do: Build content into the APK assets with hashes, per-flavor size report, WebP/OGG settings; record the budget and what moves to downloadable packs post-V1 if exceeded.
Done when: size report in `docs/build_size.md`.

### T-P8-09 — SMOKE P8
Type: SMOKE
Do: Checklist: play three random worlds per track end to end; verify audio sync, no missing images, review and remediation appear when you make mistakes on purpose. Fix reported errors first.
Done when: checklist in `ai/STATE.md` if it exists; otherwise in the hand-off.
