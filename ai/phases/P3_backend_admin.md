# P3 — Backend and admin panel

Goal: a domestic-hosted API (login, sync, content, entitlement) and an admin web panel (CMS + audio pipeline) that can run on one server today and scale later (U-002, U-007).
Stack: NestJS + Prisma + PostgreSQL (`services/api/`), Next.js (`apps/admin/`), see `ai/specs/TECH_STACK.md`. Screens: CMS-00 … CMS-09 in `data/screens_v2.json`.
Rules: stateless API, every list endpoint paginated, idempotent writes (client sends operation ids), rate limits on OTP, structured logs, no foreign SaaS in core paths (U-007).

### T-P3-01 — Server skeleton
Type: TASK
Do: `services/api/` NestJS project, config via env (validated), Prisma + PostgreSQL, `docker-compose.yml` (api + postgres), health endpoint, request-id logging, global error format, `.env.example`, OpenAPI spec generated to `services/api/openapi.json`.
Done when: structure compiles logically; README lists run commands.

### T-P3-02 — OTP authentication
Type: TASK
Do: `SmsProvider` interface with `KavenegarProvider` (verify-lookup template API; read the provider's current docs, OQ-08) and `FakeSmsProvider`; endpoints: request-code, verify-code, refresh, logout, delete-account. Rules: 6-digit code, 2-minute validity, resend cooldown, max attempts per number/device/IP, constant-time compare, hashed codes, JWT access + rotating refresh, device record. Phone normalization (Persian/Arabic digits, +98/0098/09).
Done when: provider swap via env; abuse limits documented in `services/api/docs/auth.md`. OQ-08 (SMS account) logged.

### T-P3-03 — Profiles and progress sync
Type: TASK
Needs: T-P2-02, T-P0-15
Do: Models and endpoints for parent, child profiles (max 3, with track), **skill states and stage-attempt summaries** (not raw evidence, C-123) with batch upload (`/sync/push`, `/sync/pull`), idempotency keys, conflict policy (per skill: merge by evidence counters and latest timestamp; server time authoritative), tombstones for deletions. Optional pilot endpoint `/evidence/raw` that is off unless the parent consented (OQ-19). Document in `services/api/docs/sync.md`.
Done when: contract matches the Drift schema from T-P2-02 (write the mapping table).

### T-P3-04 — Content and entitlement APIs
Type: TASK
Needs: T-P0-12
Do: Versioned content pack endpoint (start by serving the JSON from the CMS tables), entitlement endpoint (`free boundary` + plans from config, C-109), purchase-verification endpoint stubs per store (filled in P7). Content items carry `approved` state; clients in release mode receive only approved items.
Done when: OpenAPI updated; entitlement logic unit-test list written.

### T-P3-05 — Admin skeleton: CMS-00, CMS-09, CMS-01
Type: TASK
Do: Next.js app, secure admin login (email+password+TOTP or long random passphrase — choose and record), roles (owner, editor, reviewer, teacher-approver), CMS-01 dashboard. Persian RTL UI, simple and clean.
Done when: screens implement all states of CMS-00, CMS-09, CMS-01.

### T-P3-06 — CMS-02 content/curriculum and CMS-03 media library
Type: TASK
Needs: T-P0-12, T-P0-14
Do: CRUD for worlds (W01…W10), stages (stage schema), content-bank items (symbols, words, texts, stories, poems) with the P0 schemas; 4-state status (draft/reviewed/approved/retired) and provenance triple (C-127); skill-graph and station-map viewers; import/export JSON; media upload (images/audio) with checksum and size limits; `stage_lint` results shown per stage.
Done when: the P0 content bank and station map import and re-export without loss.

### T-P3-07 — CMS-04 audio slot queue, CMS-05 audio review, audio provider (ManualUploadProvider in V1; GATE-lite)
Type: TASK
Do: `TtsProvider` interface with `ManualUploadProvider` as the V1 path (required): the owner produces the audio outside the system and uploads one file per slot in the admin panel (U-041). Optional later: `GeminiTtsProvider` (model `gemini-3.8-flash-tts`, Iranian Persian; input is a verbatim transcript; directions go in metadata), only if an allowed route exists. Flow: slot (text + voice profile) → upload (or generate, if the optional provider exists) → listen → approve, reject or replace → publish with version. Store raw text, voice profile, model or `manual`, and date per file. Voice design: one warm friendly voice for instructions; character voices later.
Test plan (write `docs/tts_test_plan_fa.md`): 41 letter sounds, vowels and diacritics, 20 words, 3 sentences; owner and teacher rate each. Human recording is the fallback for letter sounds if quality fails.
Done when: queue → upload → review → publish works against a fake storage; test plan written; no TTS API key is needed for V1 (if the optional provider is built, its key lives only on the server).

### T-P3-08 — CMS-06 access/revenue, CMS-07 config/pack/publish, CMS-08 reports/audit
Type: TASK
Needs: T-P0-15
Do: Free-boundary setting (which worlds/stages are free), **adaptation config editor** (mastery thresholds, review intervals, priority weights — C-124, versioned, with rollback and a diff view), plans and prices as config, publish flow (draft → review → published, version number, rollback), audit log of every admin action, basic usage reports (counts only, no child identifiers).
Done when: states of CMS-06/07/08 implemented.

### T-P3-09 — Deployment guide for a domestic server
Type: TASK
Do: `services/api/docs/deploy_fa.md` (Persian): server sizing for 200 families now and 100k users later, Docker/compose, reverse proxy + TLS, firewall, backups (daily DB + media), monitoring/alerts, log rotation, update procedure, rollback, secrets handling, domain and SMS sender checklist. No vendor lock-in; list what the owner must buy.
Done when: a non-expert can follow it; OQ-07 logged.

### T-P3-10 — Connect the app to the server
Type: TASK
Needs: T-P3-02, T-P3-03, T-P3-04, T-P2-05
Do: Implement real `AuthRepository`, sync client (queue → push/pull with retry/backoff), entitlement cache in the app; keep fakes for tests. Offline behavior per U-008.
Done when: app flavors read `API_BASE_URL` from build config; sync documented.

### T-P3-11 — SMOKE P3
Type: SMOKE
Do: Smoke checklist: `docker compose up`, request/verify OTP with the fake provider, create profile in the app, see it in admin, import baseline content, generate fake audio. Fix reported errors first.
Done when: checklist in `ai/STATE.md` if it exists; otherwise in the hand-off.
