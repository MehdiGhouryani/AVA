---
name: ava-duolingo-learning
description: Build and review AVA native Android Persian reading screens, guided learning paths, story flows and recoverable feedback using primary Duolingo and ABC research, AVA artwork and RTL rules.
---

Read docs/AVA_MASTER_ROADMAP_FA.md and the relevant screen mapping. Use docs/RESEARCH_FA.md for primary sources.
- Preserve AVA mint/cream/teal, local Vazirmatn and art. Never copy Duolingo branding, mascot, proprietary sounds or fonts.
- Give Home one obvious next action, an ordered path and completed/current/locked labels. Permit replay. Derive achievements and progress from real local actions.
- Distinguish Duolingo language-learning patterns from ABC literacy. Development visual matching is not a validated curriculum; retain approvedForRelease=false pending educational review.
- Keep lessons bite-sized while varying activity inside the guided path: visual recognition, optional listen-and-choose, and tap-to-build words. Do not add navigation depth merely to create variety.
- Treat device TTS as optional support. Every listening activity must expose a written hint and remain completable when narration is off or no Persian voice exists.
- Prefer tap tokens with undo over mandatory drag. Keep each target at least 48dp, expose selected/used state to accessibility services, and support large-text reflow.
- Give every task a stable content ID. Read legacy lesson:index review keys, write stable keys, and remove both aliases after a corrected review.
- Keep default review sessions short. Prioritize valid mistakes, add available activity-kind variety, cap the session, and rotate learned items deterministically. Do not call this spaced repetition unless the data model records timing and mastery evidence.
- Use large targets and choices, gentle amber feedback, retry and clear completion. Do not punish mistakes with payment, permanent blocking or lost lives.
- Keep four root destinations and predictable back. Confirm lesson exit/reset. Preserve completed steps and story position.
- Use Kotlin/Compose state/actions, repositories and lifecycle-aware observation. Keep storage outside Composables. Verify generic skill/version advice with official Android documentation and actual builds.
- Preserve 48dp touch targets, RTL, unmirrored artwork, large text, semantic controls, a single OTP input, reduced motion and lifecycle audio stop.
- Use installed offline Persian TTS, report absent voices, request no microphone or Internet permission. Keep authorized mock phone/OTP during development.
- Run logic, disk and UI tests; inspect renderings. Separate local evidence from device testing. Never reuse lost results as new proof.
- Provide recoverable deliverables with persistent links. These are project-bundled skills, not personal installations.
