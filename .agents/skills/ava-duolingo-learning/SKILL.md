---
name: ava-duolingo-learning
description: Use for AVA Persian alphabet/literacy curriculum, lesson sequencing, review sessions, story flows and recoverable learning activities.
---

# AVA learning and curriculum design

Use this skill only for educational flow and content decisions. Repository-wide product rules and task execution are defined by `AGENTS.md` and the selected harness task.

## Research and product boundaries

- Read `docs/AVA_MASTER_ROADMAP_FA.md`, the relevant screen mapping and `docs/RESEARCH_FA.md`. For claims about learning, inspect and cite the primary source; otherwise label the choice as a design judgement.
- Use learning patterns as inspiration, not as permission to copy another product's branding, mascot, proprietary sounds, fonts or copy.
- Distinguish language-learning patterns from Persian alphabet literacy. Visual matching or a Duolingo-like flow is not a validated curriculum; keep `approvedForRelease=false` until educational review.
- Do not add navigation depth just to create activity variety. Keep the guided path simple and vary activity inside each lesson where meaningful.

## Lesson and review design

- Give Home one obvious next action. Show an ordered learning path with completed/current/locked states and clear reasons for locked steps.
- Keep lessons bite-sized. Prefer visual recognition, optional listen-and-choose and tap-to-build words where appropriate; use tap tokens with undo instead of mandatory drag.
- Every listening task needs a written hint and must remain completable when narration is disabled or a Persian voice is unavailable.
- Keep every lesson/task/audio reference on a stable content ID. When correcting review references, read legacy `lesson:index` keys, write the stable key, and remove both aliases only after the correction is verified.
- Keep review sessions short. Prioritize valid mistakes, add activity-kind variety only when available, cap the session and rotate learned items deterministically.
- Do not describe the review as spaced repetition unless the data model records timing and mastery evidence.
- Give mistakes a recoverable path: retry, useful hint and clear completion. Never punish a wrong answer with permanent blocking or loss of progress.
- Preserve predictable navigation and back behavior. Confirm lesson exit/reset and preserve completed steps and story position.

## Evidence

- Separate code/test evidence from educational validation. Unit tests, static review and screenshots do not replace review by a qualified Persian literacy educator or supervised child testing.
- Keep specialist approval as a human gate; never mark educational approval complete on behalf of a reviewer.