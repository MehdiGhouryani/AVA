# P6 — Characters, companion, avatar

Goal: integrate the owner-approved character system and avatar personalization after the Style Gate. The prompt body refers to Pofi, Fandogh and Roka as lesson/role characters and Ava as a brand character, but the absent decision ledger means these U-/C- labels are not independent proof; verify each role against current source files or owner instruction before locking it. Keep personalization limited and age-appropriate.
Needs from owner: approved character art and any current character specification, if available. `design/CHARACTER_ASSET_PRODUCTION_SPECIFICATION_FA.md` is absent in this snapshot; do not assume it was supplied. If no approved spec exists, record the gap and draft a replacement only when this phase explicitly reaches that deliverable.
More characters belong to P10 unless the owner delivers them early.

Phase needs: T-P1-06

### T-P6-01 — Avatar research note
Type: TASK
Do: Write `docs/avatar_research_fa.md`: what personalization suits ages 4–7 (color of companion, hat/scarf, a few accessories, no body/face editing, no gendered pressure, no spending), pitfalls (choice overload, anything that needs reading), and a proposed item list tied to milestones. If you have web access, cite sources; otherwise mark as reasoning, not evidence.
Done when: note lists max 12 items for V1 and unlock rules.

### T-P6-02 — Character asset intake and Rive pipeline
Type: GATE
Do: Check whether `inputs/` and `design/characters/` actually exist and whether final art was delivered; document per character what exists and what is missing. Do not create empty folders or claim any art was supplied. Build the Rive state-machine contract per `docs/character_asset_contract.md`; wire `CharacterView` to real files when present, else keep static fallback.
Done when: contract status table in `ai/STATE.md` if it exists, otherwise the hand-off; missing assets marked `OPEN` unless a real OQ ledger exists.

### T-P6-03 — State mapping for all screens
Type: TASK
Do: Map every screen state that shows a character to a character state id (table in `docs/character_state_map.md`); implement in screens; Roka only in challenges.
Done when: no screen references an undefined character state.

### T-P6-04 — S15 Companion home and avatar customization
Type: TASK
Do: Companion color/hat/book/role with preview and independent save (per S15 purpose); items unlock by milestones (C-105); no purchase.
Done when: all 5 states of S15; items list from T-P6-01.

### T-P6-05 — Character voice cues and SFX
Type: TASK
Do: Define short cue sounds per character (not full voices), generated/recorded via the admin audio pipeline; hook into `AudioService`.
Done when: cue list in `docs/character_audio_cues.md` and wired.

### T-P6-06 — S07 friends roles
Type: TASK
Do: Implement the friend roles decision from T-P0-03 in world intros.
Done when: no TODO(OQ-13) left in S07.

### T-P6-07 — Brand deployment with Ava (icon, splash, loading, store, share)
Type: TASK
Needs: T-P6-02, T-P1-06
Do: (1) Inspect `packages/ava_ui_assets/manifest.json` and the actual images under `packages/ava_ui_assets/assets/characters/`; do not assume role or approval from filenames. The review `ai/reviews/REVIEW_characters_package_v1_2.md` is absent. (2) The old `metadata/characters.json`, Dart resolver/view widgets, and scripts were moved to `packages/ava_ui_assets/legacy/character_pipeline/` because they expect a conflicting folder/manifest schema. Do not run, import, or extend that pipeline as though it were current. If the approved product spec establishes the cast and the owner requests migration, design a new schema from actual files and migrate only the useful pieces with tests. If role/OQ-31/U-036 remains unsupported by current evidence, mark it for owner confirmation instead of presenting those IDs as verified. (3) Verify the actual source dimensions of final art; do not assume a particular file is 1024 px without checking. (4) Create the icon set under `design/brand/icon/` only after the required source art and style decision are present; do not generate missing masters or invent approved assets. (5) Use Ava for splash/loading only if the current owner-approved character decision confirms it; do not import an unapproved brand asset into lesson screens.
Done when: the owner-approved brand uses and character roles are documented against the current manifest/schema; any migrated helper has passing tests; the icon set exists only if its required source art and style decisions are approved; no lesson screen imports a brand asset (grep/test); unresolved OQ items remain explicitly open. Do not count archived legacy tools as validation evidence.

### T-P6-08 — SMOKE P6
Type: SMOKE
Do: Checklist: companion pick, change hat, characters react on correct/incorrect/celebration, no jank on a low-end phone. Fix reported errors first.
Done when: checklist in `ai/STATE.md` if it exists; otherwise in the hand-off.
