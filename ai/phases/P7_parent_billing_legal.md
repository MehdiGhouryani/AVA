# P7 — Parent area, billing, legal

Goal: everything a parent touches: settings, progress report, subscription via three channels (U-011), legal pages, data deletion, support.
Screens: S18, S19, S20, S23, S24, UX-01, UX-02, UX-03, UX-05. No push notifications, no email (U-018). Gate is not strict (U-019) except C-106.

Phase needs: T-P2-02, T-P3-04

### T-P7-01 — Billing abstraction and flavors
Type: TASK
Do: `BillingProvider` interface (products, purchase, restore, status) with implementations per flavor: `bazaar` (flutter_poolakey), `myket` (official Myket billing SDK — verify package/API first, OQ-04), `web` (external payment page, see T-P7-04). Product ids and plans come from server config (U-027). The app never trusts a local purchase: it sends the receipt/token to the server (T-P7-05) and caches the signed entitlement.
Done when: flavor wiring documented in `docs/billing.md`; fake provider for debug.

### T-P7-02 — Bazaar adapter
Type: TASK
Do: Subscription purchase/query/restore using the Bazaar SDK for Flutter; note Bazaar's client app must be installed and signed in on the device for testing; add the permission/queries entries required by the SDK; error states per UX-05.
Done when: adapter + test checklist for a real device in `docs/billing.md`.

### T-P7-03 — Myket adapter
Type: TASK
Do: Same for Myket after verifying the current SDK/docs. If no Flutter wrapper exists, write a thin platform-channel wrapper around the official Android SDK.
Done when: adapter compiles logically; real-device checklist written.

### T-P7-04 — Web (site APK) payment flow
Type: GATE
Do: The site APK cannot use store billing. Design: parent taps subscribe → short hold-to-confirm (C-106) → opens the payment gateway page in the browser → gateway callback hits the server → server grants entitlement → app refreshes. Choose the gateway with the owner (OQ-03); implement the server side behind a `PaymentGateway` interface.
Done when: flow diagram in `docs/billing.md`; interface + fake gateway implemented. BLOCKED on OQ-03 for the real adapter.

### T-P7-05 — Server-side verification and entitlement
Type: TASK
Needs: T-P7-01, T-P3-04
Do: Verify Bazaar/Myket purchases server-side, store purchase records, expiry handling, grace period, restore, refunds/cancellations, idempotency; entitlement token signed by the server; admin visibility in CMS-06.
Done when: state machine documented and implemented; unit-test list written.

### T-P7-06 — S18 Subscription and UX-05 purchase lifecycle
Type: TASK
Do: Plans screen (prices from server), purchase flow, pending/failed/restored states, calm copy, no dark patterns, no countdowns, no pressure on the child.
Done when: all states of S18 (8) and UX-05 (10).

### T-P7-07 — S19 Parent progress report
Type: TASK
Do: Accurate, readable in-app report built from **skill states** (not stars): per lane (oral, sounds, symbols, reading, writing, spelling) introduced vs practicing vs mastered vs at risk, what is due for review, the next recommended step, and optional adult-bridge prompts (1–3 min). No fake charts when there is no data (`no_data`). Per child profile; computed from synced skill states; no export of identifiers.
Done when: report formulas in `docs/report_rules.md` trace to `09_assessment_spec_fa.md`; all 6 states.

### T-P7-08 — S20 Settings, S23 Parent gate (off), S24 Storage
Type: TASK
Do: Settings (sound, language fixed, profiles, subscription, support, legal, delete data); gate implemented but disabled by flag (U-019); S24 reduced to storage info (U-009).
Done when: states of S20, S23, S24 implemented.

### T-P7-09 — Legal pages, account deletion, support
Type: TASK
Do: UX-01 (terms/privacy rendered from CMS text; includes the pilot raw-evidence consent switch, OQ-19), UX-02 (logout, delete account and all data, confirmation, server deletion job), UX-03 (support/report a problem with optional log attachment, no personal data by default). Texts come from the lawyer-reviewed version of `docs/legal/` (placeholders until then, OQ-05).
Done when: all states of UX-01/02/03; deletion verified in server tests list.

### T-P7-10 — SMOKE P7 (M2 check)
Type: SMOKE
Do: Checklist: sandbox/real purchase on Bazaar and Myket test accounts, restore on a second device, deletion end to end, report matches known activity. Fix reported errors first.
Done when: checklist in `ai/STATE.md` if it exists; otherwise in the hand-off.
