# P0 — Prep, pedagogy foundation, Style Gate, spec consolidation

Goal: remove ambiguity before writing app code. (1) merge the v1.8 product docs into one clean spec, (2) build the pedagogy foundation required by `docs/pedagogy/01_master_educational_spec_v2.md` §39 (skill graph, orthographic policy, content bank, station map, stage schema, learner model, adaptive engine spec, assessment, AI stage prompt, QA, pilot plan), (3) decide the character style with a real test, (4) create the asset prompt system, (5) pin the stack.
Read first: `AI_START_HERE.md`, `docs/PROJECT_STRUCTURE_FA.md`, and `docs/pedagogy/README.md`, then the specific source files named by each task. In this snapshot, `ai/DECISIONS.md` and `ai/FILE_MAP.md` are absent. Do not treat U-/C-/OQ- labels in this backlog as verified locked decisions unless you can trace them to an available source or owner instruction. For unresolved conflicts, record the evidence and mark the decision `UNVERIFIED`/`OPEN` rather than guessing.
Output language of pedagogy docs: Persian prose with English ids/keys; machine-readable files (JSON/YAML) next to each.

### T-P0-01 — Verify the repo layout
Type: TASK
Do: Compare the actual folders with `docs/PROJECT_STRUCTURE_FA.md` and update that current map if something moved. Do not create a competing `ai/FILE_MAP.md` unless a later task explicitly establishes it as canonical. Inspect `data/baseline/b/` and record verified counts (stations, steps, symbols, words, poems) in `docs/spec/00_FACTS.md` (create it).
Files: `docs/PROJECT_STRUCTURE_FA.md`, `docs/spec/00_FACTS.md`.
Done when: the current structure map matches reality; FACTS lists counts computed from the actual JSON, not copied from historical docs.

### T-P0-02a — Merge doc 01 (product & flows) into `docs/spec/01_PRODUCT_AND_FLOWS_FA.md`
Type: TASK
Needs: T-P0-09
Do: Merge `docs/spec_sources_v1_8/01_PRODUCT_AND_FLOWS_FA.md` into one de-duplicated Persian spec file. Keep traceable requirements and IDs (CR-, D-, screen ids, state ids); remove repetition and historical narration from the new derived spec. The phone-number + OTP target and exclusion of Google/email login are corroborated by this phase plan and P2; do not implement email/Google authentication from legacy records. For any other claimed lock (for example rewards or asset delivery) that cannot be verified in an available current source, mark it `UNVERIFIED` and list the conflict rather than presenting it as an owner decision. Where source learning-design statements conflict with `docs/pedagogy/`, use the pedagogy documents and record the change in `docs/spec/SUPERSEDED_MAP.md`. Never delete or rewrite the v1.8 source. Create/update the map with old file → new file, removed items, and changed items.
Done when: merged file exists and SUPERSEDED_MAP is updated; it offers no Google/email sign-in. State Ava's brand/lesson role only as an owner-confirmed rule if the source can be verified; otherwise label it `OPEN` rather than treating U-037 as proof.

### T-P0-02b — Merge doc 02 (design system) into `docs/spec/02_DESIGN_SYSTEM_FA.md`
Type: TASK
Needs: T-P0-09
Do: Merge `docs/spec_sources_v1_8/02_DESIGN_SYSTEM_FA.md` into `docs/spec/02_DESIGN_SYSTEM_FA.md`. If `design/design_tokens.json` exists, cross-check values against it. If it is absent (as in this snapshot), create an initial `design/design_tokens.json` from the available design source and `data/ui_layout.json`; mark any unsupported style/color/font choices `PENDING` until the Style Gate and owner review. Do not claim a missing token file was consulted.
Done when: as T-P0-02a.

### T-P0-02c — Merge doc 03 (brand, characters, avatar) into `docs/spec/03_BRAND_CHARACTERS_AVATAR_FA.md`
Type: TASK
Needs: T-P0-09
Do: Merge `docs/spec_sources_v1_8/03_BRAND_CHARACTERS_AND_AVATAR_FA.md`. The referenced files under `design/` are absent from this snapshot; use available character metadata and current source documents instead, and create new design files only when specified as task outputs. Do not state the vector-vs-3D conflict is resolved until a real Style Gate decision exists. Ava's brand-only/never-a-lesson-host role must be supported by an available current source or explicitly marked as a proposed rule awaiting owner confirmation; do not rely on an unverified C-/U- id alone.
Done when: as T-P0-02a.

### T-P0-02d — Merge doc 04 (state, behavior, content) into `docs/spec/04_STATE_BEHAVIOR_CONTENT_FA.md`
Type: TASK
Needs: T-P0-09
Do: Merge `docs/spec_sources_v1_8/04_STATE_BEHAVIOR_AND_CONTENT_FA.md`. Cross-check against `data/state_contracts_v18.json` and `data/baseline/b/`. For reward economy, derive only what current sources support; if C-105 cannot be verified, mark the choice `UNVERIFIED` and do not invent a spendable currency/locked reward rule.
Done when: as T-P0-02a.

### T-P0-02e — Merge docs 05, 06, 07 (handoff, QA gates, screen-by-screen) into `docs/spec/05_*`, `06_*`, `07_*`
Type: TASK
Needs: T-P0-09, T-P0-03
Do: Merge sources 05, 06 and 07 into new current files. In 05, distinguish the React UX prototype from the future Flutter implementation; the architecture document is an output of T-P0-08, so create it before relying on it. For 07, reconcile with the derived `data/screens_v2.json` from T-P0-03, not the historical registry. Before appending `docs/qa/QA_DISPOSITION_FA.md` into 06, correct its legacy references to email/alternate login screens so removed flows are not described as current acceptance criteria.
Done when: three merged files exist; every screen id of the registry appears in 07.

### T-P0-02f — Merge doc 08 (decisions/changelog) into `docs/spec/08_HISTORY_FA.md`
Type: TASK
Needs: T-P0-09
Do: Condense the changelog to one table (id, decision, evidence/source, status, still valid?). Because `ai/DECISIONS.md` is absent in this snapshot, do not claim a row is superseded by an unverifiable U-/C- id; mark the status `UNVERIFIED` until supported by a current source or owner instruction.
Done when: table complete; SUPERSEDED_MAP finished.

### T-P0-03 — Conflict sweep and registry update
Type: TASK
Needs: T-P0-09
Do: (1) Derive `data/screens_v2.json` from `data/screens_v18.json` and keep the v1.8 source untouched. Make sign-in phone-number + OTP only: mark S03c and S03e (Google/email variants) removed and make S03a phone-only, consistent with P2 and the current owner instruction. For S22/S24, G2/G6 and S31/S32/S33, apply changes only when corroborated by available current documentation; otherwise preserve the source record in the derived registry and clearly mark the item `UNVERIFIED`/`OPEN` rather than treating the cited U-/C- number as proof.
(2) Add to every screen record that shows learning content the fields `stage_context` (which stage roles it appears in) and `evidence_emitted` (what it records) — fill only for activity screens S08–S14, S31–S33, G1/G3/G4/G5; leave `TODO(T-P0-14)` otherwise.
(3) Reconcile state count (v1.8 docs say 358 in some places, 359 in others): count from `data/state_contracts_v18.json` and write the true number in `docs/spec/00_FACTS.md`.
(4) Audit companion lines such as `آوام` and prepare a proposed copy in `data/baseline_v2/` plus `data/copy_changes.md`; do not mechanically rewrite every match. Change text only when the current product/character rules clearly require it; if Ava's exact onboarding role is unverified, record alternatives and leave the original unchanged. Never edit `data/baseline/`.
(5) Map the friend roles in S07 (fox/owl/bird) to existing characters (Roka/Fandogh/Pofi) or mark TODO(OQ-13).
(6) Compare the meanings of S06-B, S07, S17, S19 and S26 between the historical sources and current pedagogy. Record verified changes in `docs/spec/SUPERSEDED_MAP.md`; cite U-033 only if an available source confirms it, otherwise label the proposed interpretation `OPEN`.
Done when: `data/screens_v2.json` valid JSON, 58 records, statuses updated; changes listed; FACTS has the true state count.

### T-P0-04 — Teacher-guide and book analysis → REFERENCE_SPINE
Type: TASK (optional input: owner files in `inputs/books/`, OQ-01 — not blocking, U-035)
Needs: T-P0-09
Do: If owner-provided books exist under `inputs/books/`, read the teacher guide, student book, workbook and relevant references. Write `docs/pedagogy/04a_reference_spine_fa.md` and `data/content_bank/reference_spine.json`, recording the 10 worlds, source page/lesson, letter/sign order, vocabulary and activities, plus an honest comparison with `data/baseline/b/`. Label every sourced statement `SOURCE` with page/lesson. Propose any baseline changes separately; do not overwrite baseline data.
Done when: a transparent spine exists for all 10 worlds. If books are absent, derive only what is explicitly supported by `docs/pedagogy/01_master_educational_spec_v2.md` and current baseline data; label unsourced content `DESIGN` with `source_ref: none` and list open questions. Do not invent textbook page references or pretend a missing input was read.

### T-P0-05 — Style Gate: neutral comparison pack
Type: TASK
Do: First inspect the actual image files and current `packages/ava_ui_assets/manifest.json`. Do not assume Pofi, Ava, Roka, or Fandogh is the final brand/host character: historical v1.8 text, archived character metadata, and present image folders do not express one verified cast decision. The current page-asset manifest records `Dreamy storybook painterly style` and says its image references were user-approved on 2026-10-10; verify the actual files and retain this as the current recorded reference unless stronger owner guidance supersedes it. This conflicts with the historical v1.8 vector direction and with the older Pofi/Ava character assumptions, so separate visual style from character-role approval. Create `design/style_gate/STYLE_GATE_FA.md` by evaluating the currently recorded painterly reference first, then compare soft 3D, 2D-soft/vector, and hybrid only as alternatives—not as a preselected winner—using the same subject and composition wherever possible. Include a prompt pack for 6 safe poses (idle, happy, thinking, cheering, waving, pointing); do not add sad/angry/scared expressions unless the owner approves the emotional range. Include one scene background and one UI button/card sample; document palette/reference consistency, transparent cut-out quality, target size (≤150 KB WebP as a proposal), possible Rive layering, low-end device load, cultural fit, scorecard (0–2 per criterion), thresholds, and fallback. If the claimed approval cannot be traced beyond the manifest text, label it `SOURCE-CLAIMED / VERIFY`, not a newly confirmed owner decision.
Files: `design/style_gate/`.
Done when: the owner can compare options without a preselected winner; each assumption is labelled and the scorecard is repeatable.

### T-P0-06 — Style Gate: evaluate and decide
Type: GATE (needs images in `inputs/style_test/`)
Do: Score the owner's supplied images with the checklist and propose a decision (3D / 2D-soft / hybrid). Record it in `ai/DECISIONS.md` only after the owner confirms the choice; if no decision ledger exists, create it as a deliverable of this gate and clearly identify the owner-confirmed decision and evidence. Do not mark the gate complete without actual images and owner approval. Update `design/design_tokens.json` notes and spec 02b only after the decision is confirmed.
Then resolve every placeholder: run `grep -rn "\[\[STYLE\]\]" design/ ai/` and replace each with the decided style wording (prompt system, style-gate fallback text, screen briefs under `design/screens/`).
Done when: decision recorded with scores and reasons AND no `[[STYLE]]` placeholder remains. If images are missing: BLOCKED + OQ-02; P1 can proceed style-agnostic. When the images arrive later, resume using `AI_START_HERE.md` and the phase order in `ai/phases/README.md`; also re-open T-P1-08 notes if briefs were written with placeholders.

### T-P0-07 — Asset prompt system v1
Type: TASK
Do: Create `design/prompt_system/` with: `STYLE_BIBLE_FA.md` (look, palette, line/light rules, forbidden elements), `CHARACTER_LOCK.md` (fixed descriptor block per character), `IMAGE_TEMPLATES.md` (templates for: word picture, scene, letter card, sticker; each with variables), `NEGATIVE_LIST.md`, `NAMING_AND_IDS.md` (how an image id maps to `data/baseline/b/words.json` ids and file names), `REVIEW_CHECKLIST.md`, and `batch_manifest_example.csv` (id, prompt, status, reviewer, file). Needs the Style Gate decision for final wording; write now with `[[STYLE]]` placeholders and finish after T-P0-06.
Done when: a batch of 10 sample word prompts can be generated from templates without manual rewriting.

### T-P0-08 — Pin the stack and write the architecture
Type: TASK
Do: The referenced `ai/specs/TECH_STACK.md` and `ai/specs/ARCHITECTURE_SKELETON.md` do not exist in this snapshot. Create `ai/specs/` and both files as deliverables of this task, using available source docs and current owner instructions; treat architecture choices not grounded in sources as proposals, not locked decisions. If web access is available, verify current stable versions of Flutter/Dart, NestJS, Prisma, PostgreSQL, Next.js, Rive, Drift, Riverpod, go_router, just_audio, dio and flutter_poolakey. Otherwise mark versions `TO_VERIFY` and provide commands for the owner. Then write `docs/ARCHITECTURE.md` with layers, offline-first sync, entitlement flow, flavors, error handling and folder trees for mobile, API and admin.
Done when: the two stack/architecture inputs and `docs/ARCHITECTURE.md` exist; each unverified version has a command or explicit `TO_VERIFY` status; no missing file is described as previously consulted.

### T-P0-09 — Pedagogy intake and glossary
Type: TASK
Needs: T-P0-01
Do: Read `docs/pedagogy/01_master_educational_spec_v2.md` and `docs/pedagogy/02_stage_content_design_guide_fa_v2.md` fully. Write `docs/pedagogy/03a_intake_and_glossary_fa.md`: (a) one-page summary of both documents, (b) glossary mapping v1.8 terms to current terms (step→stage, screen activity→activity family, station→world, learned/mastered claims→skill states) and mark proposed terminology changes as proposals if no owner decision is available, (c) create `ai/specs/ID_CONVENTIONS.md` as a deliverable of this task and define the mapping between screen/state/evidence ids, (d) compare the v1.8 statements with the two existing pedagogy sources and cite each file/section. `ai/SESSION_LOG.md` and its purported “11 conflicts” section are absent; do not assume or invent that list, (e) list the proposed three tracks and ten worlds only where supported by current sources, and label unverified details `OPEN`.
Done when: glossary and sourced supersession list exist; ID conventions are defined; missing evidence or owner decisions are clearly marked instead of fabricated.

### T-P0-10 — Skill graph and prerequisite graph
Type: TASK
Needs: T-P0-09
Do: Create `docs/pedagogy/03_skill_graph.md` and `data/content_bank/skill_graph.json`: nodes by lane (ORAL, VOC, PRINT, PA, SYMBOL, MAP[4 directions], READ, COMP, WRITE.motor, WRITE.encoding, WRITE.composition) with ids like `PA.initial_sound_identification`, prerequisites, applicable tracks (A/B/C), evidence type needed (accuracy/independence/delayed or transfer/production), and the error types (ERR0–ERR14; spec labels E0–E14) most relevant. Follow `01` §6, §15, §21–22; keep reading and spelling separate (§7.1).
Done when: every node has id, lane, prerequisites, tracks, evidence type; graph is acyclic (write a check in `ai/tools/skill_graph_check.py`).

### T-P0-11 — Persian orthographic policy
Type: TASK
Needs: T-P0-09
Do: Write `docs/pedagogy/13_persian_orthographic_policy_fa.md` + `data/content_bank/orthography.json`: treatment of short vowels (not written in standard text), homophone sets (e.g. ت/ط, س/ص/ث, ز/ذ/ض/ظ, ق/غ, ه/ح — verify the set from `data/baseline/b/symbols.json` and the book), which mapping directions are allowed per symbol (sound→symbol is NOT unique for homophones: it belongs to spelling, not mapping), forms/positions and joining, dots/diacritics, spelling-variant policy, controlled-text rules (what a "decodable" word/text is), what is taught when. Label each rule SOURCE / PERSIAN-SPECIFIC / DESIGN / OPEN. Unknown → `OPEN`, never guessed.
Done when: every baseline symbol has an orthography record; each `OPEN` is collected in a persistent open-question file if one is created by this task, otherwise included explicitly in the hand-off. Do not claim `ai/OPEN_QUESTIONS.md` exists before creating it.

### T-P0-12 — Content bank schema and baseline migration
Type: TASK
Needs: T-P0-10, T-P0-11
Do: Implement the Content Object Model of `01` §13 as JSON Schema `data/content_bank/schema/content_item.schema.json` (fields: kind, form, spoken_form, meaning, image_ref, audio_ref, source_ref, age_bands/tracks, allowed_skill_nodes, prerequisites, phonological_properties, orthographic_properties, familiarity, confusion_sets, allowed_contexts, review_priority, tier CORE/LEARNING/LITERARY, status draft/reviewed/approved/retired, provenance triple). Migrate `data/baseline/b/` symbols (41), words (210), poems (6) into `data/content_bank/*.json` (status `draft`, unknown fields = `UNKNOWN`, `approved:false` → `draft`; `teacher_approved:false` → `draft`). Write `ai/tools/content_bank_check.py`.
Done when: all baseline items migrated; checker passes; no item invented.

### T-P0-13 — Station map: 10 worlds × 3 tracks
Type: TASK
Needs: T-P0-04, T-P0-10, T-P0-12 (works with or without books; unsourced parts stay `DESIGN`)
Do: Create `docs/pedagogy/05_station_map_fa.md` + `data/content_bank/station_map.json`: for each world W01…W10 (from the reference spine) and each track: world context/scene, which skills are introduced, practiced, reviewed and transferred there (skills recur across worlds — spiral, `01` §41), target stage count by `02` §29 roles, vocabulary of the world, cultural-layer notes (OQ-17), asset reuse plan (`02` §60), content budget per stage by track (`02` §10, labelled DESIGN).
Done when: every skill node of the graph appears in ≥1 introduction, ≥1 review and (where meaningful) ≥1 transfer slot — run the Curriculum Integrity Check of `02` §99 and report gaps.

### T-P0-14 — Stage schema and activity templates
Type: TASK
Needs: T-P0-10
Do: (1) `data/content_bank/schema/stage.schema.json` from `02` §4 and §78. (2) `docs/pedagogy/07_activity_templates_fa.md` + `activity_templates.json`: map every v1.8 activity (S08, S09, S10, S10b, S11, S12-G1/G3/G4/G5, S14, S31, S32, S33) and the new ones (print awareness, see-and-say, picture-sequence retell, copy/recall writing, encoding/word-building, interactive story reading) to: activity_family, possible primary targets, response modes, evidence captured, allowed scaffold levels, retry variations, difficulty axes. Games must state mechanic + primary skill + expected response + evidence (`01` §19) or be marked not ready.
Done when: each template validates against the schema and every V1 activity has an evidence definition.

### T-P0-15 — Learner model, evidence and error/scaffold config
Type: TASK
Needs: T-P0-10
Do: `docs/pedagogy/14_learner_model_and_evidence_fa.md` + `data/content_bank/learner_model.schema.json` and `adaptation_config.json`: skill states (UNSEEN…AT_RISK), evidence record (`02` §95) with strict separation of RAW observation and INTERPRETATION (§96), error taxonomy ERR0–ERR14 (spec E0–E14) with a first intervention for each, scaffold ladder SC0–SC7 (spec S0–S7) with fade rule; use the ids of `ai/specs/ID_CONVENTIONS.md` everywhere, difficulty axes (`01` §26), novelty budget rules (`01` §27). Default numeric values are `HYPOTHESIS` and live only in the config file (C-124).
Done when: all enums and defaults exist in the config; the Drift schema task (T-P2-02) can be derived from it without guessing.

### T-P0-16 — Adaptive engine specification (on-device)
Type: TASK
Needs: T-P0-14, T-P0-15
Do: `docs/pedagogy/08_review_engine_and_adaptation_fa.md`: stage selection algorithm (MVP steps of `02` §66 and adaptive scoring of §67), spine + adaptive layer behaviour (C-121), review engine with R0–R5, priority formula as HYPOTHESIS (§45), decay/at-risk, remediation decision tree (§39), diagnostic mini-stage (§40), exit decisions (ADVANCE/CONSOLIDATE/REMEDIATE/DIAGNOSE/REVIEW_LATER/INSUFFICIENT_EVIDENCE), session composition and fatigue policy (§30, §65), cold-start rules for a new child (age band → initial skill states, embedded diagnostics instead of an exam), track-change suggestion rule (C-120). Give pseudo-code and 10 worked examples with expected decisions (these become unit tests).
Done when: pseudo-code is complete and every example resolves deterministically; all weights/intervals are in `adaptation_config.json`.

### T-P0-17 — Assessment and mastery policy
Type: TASK
Needs: T-P0-15
Do: `docs/pedagogy/09_assessment_spec_fa.md`: three assessment layers (in-task, periodic check, delayed/transfer), mastery = EV1 accuracy + EV2 independence + EV3 delayed or novel-context evidence (spec E1–E3) (+production evidence for reading/writing), initial thresholds as HYPOTHESIS in config, how oral and voice-free evidence is treated (C-125), how stars/leaves stay separate (C-129), the mapping from skill states to the parent report (S19) and to adult-bridge prompts (`02` §70).
Done when: every skill node has a mastery rule referencing config keys; parent-report fields listed.

### T-P0-18 — AI stage generator prompt and QA gates
Type: TASK
Needs: T-P0-14
Do: `docs/pedagogy/10_ai_stage_generator_prompt.md` (system prompt from `02` §79–87 + hallucination guard §80 + output as the stage schema), `docs/pedagogy/11_content_qa_fa.md` (QA lists §71–75, blockers §88, station gate §89, segment gate §90, integrity check §99, final 20 questions §100), and a spec for `ai/tools/stage_lint.py` (what it checks automatically: single primary target, content ids exist in the bank and are not retired, new/support/review/transfer separated, prohibited items absent, provenance present, error route for assessed skills, retry variation, no mastery claim from completion). Implement `stage_lint.py`.
Done when: the linter runs on 3 hand-written example stages (1 pass, 2 deliberate fails).

### T-P0-19 — Pilot validation plan
Type: TASK
Needs: T-P0-16, T-P0-17
Do: `docs/pedagogy/12_pilot_validation_plan_fa.md`: take every `HYPOTHESIS`/`OPEN` of `01` §40 and define what the 200-family pilot measures, which raw evidence is needed (C-123, consent OQ-19), how thresholds/intervals are tuned from the admin panel, sample sizes that are honest about the limits, success criteria (feeds OQ-16), and a privacy-safe data export format.
Done when: each hypothesis has a metric, a data source and a decision rule.

### T-P0-20 — Pedagogy approval
Type: GATE
Needs: T-P0-10, T-P0-11, T-P0-13, T-P0-17
Do: Prepare a short Persian review pack for the owner and the teacher (OQ-06): skill graph, orthographic policy, station map, mastery policy, parent-report fields. Record approvals/changes in `inputs/approvals/`. Until approved, all pedagogy docs stay `status: draft`; development continues on drafts.
Done when: approval note exists, or BLOCKED with OQ-06 open.

### T-P0-21 — Reference engine and test vectors
Type: TASK
Needs: T-P0-16, T-P0-17
Do: Implement the engine of `08_review_engine_and_adaptation_fa.md` in Python: `ai/tools/ref_engine.py` (skill-state update from evidence, review scheduling, stage selection, scaffold decision, exit decision) reading `data/content_bank/adaptation_config.json`. Write `data/content_bank/engine_test_vectors.json` (the 10 worked examples + at least 20 edge cases: input state, evidence list, config, clock → expected states, due reviews, next stage, exit decision) and `ai/tools/run_vectors.py` that runs them. Run it in the sandbox (Python works).
Done when: all vectors pass; every rule of the engine spec is covered by ≥1 vector; the Dart engine in P4 must pass the same vectors (C-134).

### T-P0-22 — W01 seed stage catalog (all three tracks)
Type: TASK
Needs: T-P0-12, T-P0-13, T-P0-14, T-P0-18
Do: Using `docs/pedagogy/10_ai_stage_generator_prompt.md`, the content bank, the station map (draft is fine) and the stage schema, author the stage specifications of World W01 for TRK-A, TRK-B, TRK-C (count per track from the station map budget) into `docs/pedagogy/06_stage_catalog/W01_A.json`, `W01_B.json`, `W01_C.json`. Run `stage_lint.py` and the 20-question check of `02` §100; write failures and fixes in `docs/pedagogy/06_stage_catalog/W01_REPORT.md`. Stages that cannot be completed without new content return `MISSING_CONTENT` and stay `draft`.
Done when: zero lint blockers; each file validates against the stage schema; every stage references only items that exist in the content bank; these files are what the app loads in P2–P5 (C-136).

### T-P0-23 — SMOKE/handoff for P0
Type: SMOKE
Do: Run only P0 validation scripts that were actually created and exist. This snapshot includes neither `ai/tools/check_state.py` nor `ai/tools/state.py`; do not claim either ran. List what the owner must provide (books/teacher guide if available, style images, teacher review) and the exact first unblocked task in the hand-off.
Done when: P0 artifacts and checks are itemized with truthful pass/fail/blocker status; the owner can identify the next unblocked task without relying on a nonexistent STATE file.
