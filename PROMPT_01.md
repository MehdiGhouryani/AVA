# PROMPT 01 — paste this into Google AI Studio (Build mode)

(Attach the 6 images from `/reference` to this message if the UI allows. They are in the project too.)

---

You are building the first flow of **AVA**, a Persian (RTL) early-reading mobile app for children aged 4–7. This project already contains everything you need. Do **not** invent art, copy, colors or flows.

**Step 0 — check the project:** list the files you can see at the project root. If `/DESIGN_BRIEF.md`, `/design/` or `/public/ava-assets/` are missing, STOP and say exactly which are missing. Do not continue with invented art or copy.

**Read first, in this order:**
1. `/DESIGN_BRIEF.md` — the specification (layout, states, behavior, architecture). Follow it exactly.
2. `/design/design_tokens.json` — every color/size/motion value.
3. `/design/strings.fa.json` — every Persian string.
4. `/design/assets.manifest.json` — every image, with real sizes and known issues.
5. `/reference/*.jpg` — target look. Visual comparison only; never render these images in the app.

**Build** a React + TypeScript + Vite app with this flow:
`Splash → Onboarding 1 → Onboarding 2 → Phone → OTP → Home (placeholder)`

**Non-negotiable rules** (full list in the brief §0):
- RTL (`<html lang="fa" dir="rtl">`), CSS logical properties, "forward" = left, arrows point left.
- Phone + OTP only. No Google/email/guest. **No mention of parents/guardians anywhere.**
- No network dependencies at runtime (local font `/fonts/Vazirmatn.ttf`, local images, inline SVG icons).
- All visual values from tokens (expose as CSS variables), all text from `strings.fa.json` via `t()`, all images via `asset('id')`. No hard-coded colors, sizes, strings or image paths in components.
- Errors use the amber family, never red. Never shame, never block permanently.
- Everything is a mock behind an `AuthService` interface (brief §7). OTP `123456` succeeds.
- Two onboarding pages → **2** page dots. Both onboarding pages share the same control layout (brief §4).
- Respect `prefers-reduced-motion`.
- Keep the structure from brief §1 (screens are pure functions of props; logic lives in `app/flow.ts` and `services/auth.ts`). This code will be ported to Flutter later, so avoid clever CSS-only tricks, animation libraries and anything that cannot be expressed as widgets + state.
- Add the `?debug=1` panel from brief §6 so every state can be reviewed.

**Build order** (do all of it, but in this order so nothing is skipped): tokens + i18n + assets helpers → AppFrame + shared components → Splash → Onboarding (2 steps) → Phone → OTP → Home → Debug panel → polish.

**If something is unclear:** do not ask questions. Pick the simplest option consistent with the brief and list it under "Assumptions" in your final message. If an asset has a known issue in the manifest, use it as is — do not regenerate or replace it.

**Acceptance checklist** (verify each at 320×560, 360×800, 390×844 and 412×915 before finishing):
1. Splash: logo + dots + caption over the mint scene, no percentage, auto-advances after ~1.8s, no white flash.
2. Onboarding 1 and 2: text readable over the scene; Ava + glow bubble (step 1) / 4 characters standing on the path (step 2); nothing overlaps the CTA/dots; skip works from both; swipe works in RTL direction; dots show 1/2 then 2/2 (step 1 = right-most dot, RTL); skip button sits on a cream pill; at 320×560 the CTA, dots and skip are fully visible without scrolling.
3. Phone: field accepts Persian/Arabic/Latin digits and displays Persian digits; error appears only after blur/CTA press; CTA disabled until valid phone AND consent; disabled-press gives guidance; offline banner appears when `navigator.onLine` is false (and via debug); legal links open a placeholder bottom sheet.
4. OTP: top scene + cream bottom sheet; 6 boxes fill from the **right** and all 6 fit inside the sheet at 320px wide; caret on active box; auto-verify on the 6th digit; states wrong / expired / locked / resend countdown (90s, Persian `mm:ss`) / success all reachable via debug panel; edit-phone returns with number and consent preserved.
5. No horizontal scroll at any tested size; every touch target ≥ 48px (onboarding CTA ≥ 64px).
6. No text anywhere mentions parents, Google, email.
7. `?debug=1` panel works; without the flag it is absent.

**Final message format** (keep it short):
1. List of files created/changed, one line each.
2. "Assumptions" (anything you decided without a source).
3. "Deviations" (anything in the checklist you could not satisfy and why).
4. Nothing else — no marketing text, no re-explaining the brief.
