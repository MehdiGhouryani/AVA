# P5 — Activities, tracing, games, stories

Goal: every V1 activity family is playable for **World W01 on all three tracks**, with final UI and real evidence output. **M1 Playable slice** at the end of this phase.
Games in V1 (C-102): G1, G3, G4, G5 — each must declare mechanic, primary skill, expected response and evidence (`docs/pedagogy/07_activity_templates_fa.md`). Sound/phonics activities S31, S32, S33 are activity templates. Read each screen record AND its activity template before building. Every activity emits evidence per the template (scaffold level, retry variation, error candidate).

Phase needs: T-P0-14, T-P0-11, T-P4-01

### T-P5-01 — S08 Letter introduction
Type: TASK
Do: Introduce a symbol: shape, sound, example word with picture, replay sound, next. Voice-first. All 6 states.
Done when: works for all 41 symbols from content data (forms handled per spec).

### T-P5-02 — S09 Tracing engine
Type: TASK
Do: Finger tracing on official vector paths with stroke order and direction, tolerance bands, non-shaming feedback (guide dot, gentle snap, never "wrong"), reset, tablet-sized canvas, multi-touch safe. Define `data/symbols/vector_paths_spec.md` (path format: strokes, direction, start point, forms isolated/initial/medial/final). The owner's teacher approves shapes in P8; use generated paths now (OQ-14).
Done when: engine + 3 sample symbols working logically; all 7 states of S09.

### T-P5-03 — S10 Listen-and-choose and S10b First-sound / word-to-letter
Type: TASK
Do: Choice activities with big targets, replay button, distractor rules from content data, gentle retry rule from P4.
Done when: both screens' states implemented.

### T-P5-04 — S11 Search-and-find
Type: TASK
Do: Find the target in a scene/word cards; hit areas generous; hints after N seconds without penalty.
Done when: all 6 states.

### T-P5-05 — S32 Sound games and S31 Sound walk (phonological ladder)
Type: TASK
Do: Phonological-awareness ladder and environmental sounds per the registry; assets from placeholders until P8.
Done when: all states of S32 and S31 implemented.

### T-P5-06 — S33 Segment-and-blend
Type: TASK
Do: Continuous sound stretching and blending with visual support; the app never records the child's voice (no speech recognition in V1).
Done when: all 7 states.

### T-P5-07 — Game G1 "Catch the sound" (S12-G1)
Type: TASK
Do: Bubbles rise slowly, hear the target, tap; no timer pressure, no game over; difficulty from the stage definition.
Done when: all 6 states.

### T-P5-08 — Game G4 "Matching" (S12-G4)
Type: TASK
Do: Pair shape/sound/picture; card flip animation; tablet grid.
Done when: all 6 states.

### T-P5-09 — Game G3 "Word maker" (S12-G3)
Type: TASK
Do: Build a word from allowed letters (only letters already taught); drag with snap; sound feedback per letter.
Done when: all 6 states.

### T-P5-10 — Game G5 "Syllable bridge" (S12-G5)
Type: TASK
Do: Combine syllables to cross a bridge to a word; gentle animations.
Done when: all 6 states.

### T-P5-11 — S14 Story book
Type: TASK
Do: Two modes (C-126): (a) **interactive story reading** — listen, predict, talk, notice language, retell (picture sequencing), no quiz-only ending; (b) **controlled reading** — instructional texts made only of taught units, tap-to-hear, comprehension check separate from decoding. Authoring of stories is P8.
Done when: all 7 states; both modes selectable by stage definition.

### T-P5-12 — Print and book awareness activities
Type: TASK
Do: Activities for book/print awareness (what is text vs picture, direction right-to-left, following text with a finger, word as a unit), reusing the story reader; evidence per template.
Done when: skill nodes `PRINT.*` have at least one playable stage in W01 on TRK-A.

### T-P5-13 — Oral-language activities (no speech recognition)
Type: TASK
Do: See-and-say (unscored "say it" prompts), picture-sequence retelling by arranging, describe/compare by choosing, adult-bridge prompts (1–3 min, optional, never required, C-125). No microphone permission in V1.
Done when: `ORAL.*` skill nodes have playable stages; no screen claims mastery from unscored speech.

### T-P5-14 — Motor-writing ladder beyond tracing
Type: TASK
Do: Extend the tracing engine (S09): guided trace → complete the missing part → copy beside a model → recall from memory; each with its own evidence and tolerance; trace is never the endpoint (`02` §20).
Done when: `WRITE.motor.*` nodes have all four modes.

### T-P5-15 — Encoding (spelling) activity
Type: TASK
Do: Hear a word → notice units → select symbols → order → check, built on the G3 mechanic but scored as spelling (not mapping). Apply the homophone policy from `13_persian_orthographic_policy_fa.md` (no unique answer → accept the allowed set or avoid the item). Reading and spelling are separate skills in the learner state.
Done when: spelling evidence never changes a reading skill state, and vice versa.

### T-P5-16 — Evidence emission audit
Type: TASK
Do: Contract test list: every activity emits all required evidence fields, raw and interpretation stay separate, retries vary, no completion-only success path exists. Write `docs/evidence_audit.md`.
Done when: audit table has a row per activity with a pass/fail note.

### T-P5-17 — Wire the slice end to end (M1)
Type: TASK
Do: Map the stages of W01 (tracks A, B, C) to renderers, run `stage_lint` and content-bank checks, fix gaps; list in `ai/STATE.md` if it exists, otherwise the hand-off, which stages still use placeholder audio/art.
Done when: `ai/tools/slice_report.py` (write it) prints per-stage coverage for W01 on all tracks with no unknown activity family and no stage failing `stage_lint`.

### T-P5-18 — SMOKE P5 (M1)
Type: SMOKE
Do: Smoke checklist: play W01 on each track on a phone; test each game once; test on a tablet or emulator. Owner pastes errors/screenshots; fix first next session.
Done when: checklist in `ai/STATE.md` if it exists; otherwise in the hand-off.
