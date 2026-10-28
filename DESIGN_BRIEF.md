> Historical initial web specification. Current native Android work starts at [docs/README.md](docs/README.md). Flutter/Home-placeholder instructions below are superseded.

# AVA — Design Brief (v1, first 4 screens)

Persian (RTL) early-reading app for children aged 4–7, mobile-first. This file is the source of truth for look, layout and behavior of the **first flow**:

`Splash → Onboarding 1 → Onboarding 2 → Phone → OTP → Home (placeholder)`

Visual style: **dreamy storybook, painterly, soft-3D characters**, mint/cream/teal palette, big rounded shapes. Reference screenshots are in `/reference` (visual comparison only — they contain baked-in text/UI and must never be rendered in the app).

Everything the code needs lives in `/design`:
- `design_tokens.json` — colors, type, spacing, sizes, motion, mock-auth constants
- `strings.fa.json` — all Persian copy (never hard-code text in components)
- `assets.manifest.json` — all images with real sizes and known issues (never hard-code image paths)

---

## 0. Hard rules

1. **No parent/guardian content anywhere.** No parent section, no "for parents" text, no parent-gate. The only adult-facing thing is the phone login, and it is written in neutral language.
2. **Phone number + OTP only.** No Google, no email, no social, no guest button.
3. **RTL.** `<html lang="fa" dir="rtl">`. Use CSS logical properties (`margin-inline-start`, `padding-inline-end`, `inset-inline-start`…). "Forward" = **left**. Arrow icons for next/continue point **left**.
   **Exception — artwork is never mirrored.** Illustrations, characters and decorations are placed with **physical** `left`/`right` (never `inline-start/end`), exactly as in the reference screenshots. Wherever this brief says "physical left/right" it means screen sides. Text, controls and icons use logical properties.
4. **No external network dependencies**: no CDN fonts/icons/images/scripts at runtime. Font is local (`/fonts/Vazirmatn.ttf`, variable wght 100–900). Icons = small inline SVG components.
5. **No red** for errors (child-safe tone). Errors use the amber family (see tokens). Never shame; never block permanently.
6. **Only values from `design_tokens.json`.** No magic colors/sizes in components. Expose tokens as CSS variables at `:root`.
7. **Only images from `assets.manifest.json`**, referenced by id through one `assets.ts` helper.
8. **Everything is a mock.** No real SMS/API. Auth sits behind an `AuthService` interface (see §7).
9. Text sizes follow tokens; **do not shrink them to match screenshot pixel proportions** (the screenshots are AI-generated art, not pixel-accurate mockups).
10. Respect `prefers-reduced-motion`: disable transforms/animations, keep the final visual state.

## 1. Architecture (must be portable to Flutter later)

```
src/
  design/tokens.ts          // imports design_tokens.json, sets CSS variables, exports typed tokens
  i18n/fa.ts                // imports strings.fa.json, exports t('path.to.key', {vars})
  assets/assets.ts          // imports assets.manifest.json, exports asset('bg.splash') -> {src,width,height}
  services/auth.ts          // interface AuthService + MockAuthService
  app/flow.ts               // typed flow state machine (reducer): route + shared form state
  app/App.tsx               // AppFrame + router by flow.route
  components/               // PrimaryButton, SkipButton, PageDots, PhoneField, ConsentRow,
                            // OtpInput, OfflineBanner, BottomSheet, Icon, AppFrame, LogoImage
  screens/                  // Splash, Onboarding(step), Phone, Otp, Home
  debug/DebugPanel.tsx      // only when URL has ?debug=1
```

- Each screen is a **pure function of props** (`state`, `actions`); all logic in `flow.ts` / `auth.ts`. This maps 1:1 to Flutter widgets + a Riverpod notifier.
- Plain CSS (CSS variables) preferred. No animation libraries (CSS keyframes only). No router library needed: the flow is a reducer with a typed `route`.
- Stack: React + TypeScript (Vite if the project template uses it). Static files MUST be served at the URL paths `/ava-assets/...` and `/fonts/...`. In a Vite template that is the `/public` folder; if the template differs, use its equivalent and keep these URLs unchanged (the manifest depends on them).

## 2. App frame

- Mobile design frame **390×844**. On viewports ≤ 430px wide the app is full-bleed (`100dvh`, respect `env(safe-area-inset-*)`).
- On wider viewports: center the frame (390×844, radius 32, `shadow.frame`) on a neutral `paper` backdrop.
- Must work at 320×560, 360×800, 390×844, 412×915 without clipping; page may scroll vertically only when content truly cannot fit (small heights), never horizontally.
- **Height-adaptive art (critical on 320×560):** size logo, character group and illustrations with `dvh`-based clamps (e.g. `min(<token px>, <n>dvh)`), never with fixed pixels alone. The primary CTA and the dots must always be fully visible without scrolling on Onboarding 1/2 at 320×560; shrink the art (character group ≤ 34dvh, logo ≤ 12dvh) before shrinking text or controls.
- **The logo is TALL** (aspect 1 : 1.48). Always size it by HEIGHT (`logoHeight*` tokens) and let width follow.
- Browser font-size scaling up to 1.3× must not break layout (use `rem`/`clamp`).

## 3. Screens

Coordinates are fractions of frame height (`H`) unless stated. Screenshot names refer to `/reference`.

### 3.1 Splash — ref `ref_1_splash.jpg`
- Background `bg.splash` (cover, anchored bottom `50% 100%`). Ava peeking over the hill is **baked into the image** — do not draw her again.
- Logo `brand.logo`, centered, top ≈ 0.12H, height `logoHeightSplash`. Fade in + rise 12px over `enterMs`.
- Loader: three dots (evergreen, 10px) pulsing in sequence, centered at ≈ 0.84H, plus caption `splash.loading` (font `label`, `muted`). **No percentage.** `role="status"`, `aria-label` = `splash.a11yLoading`.
- Minimum display `splashMinMs`, then auto-navigate to Onboarding 1. Tapping anywhere skips the wait (debug convenience, harmless).
- No white flash: the page background color must equal the scene's dominant mint before the image loads (`paper`).

### 3.2 Onboarding step 1 — ref `ref_2_onboarding1.jpg`
- Background `bg.onboarding1` (cover, `50% 30%`).
- Logo top ≈ 0.05H, height `logoHeightOnboarding`.
- Text block (centered, max-width 300px) top ≈ 0.17H: title `onboarding.step1.title` (800, `title`, `evergreenDeep`, 2 lines), body `onboarding.step1.body` (500, `body`, `textSoft`, ≤ 3 lines). Text must stay readable over the busy scene (shelves, window, foliage): put a soft **cream scrim** behind the block (a blurred rounded rectangle extending ≈ 18px beyond the text horizontally and 10px vertically, `radial-gradient(ellipse, rgba(254,247,225,.80), rgba(254,247,225,.55) 65%, transparent)`, `filter: blur(8px)`, behind the text, `pointer-events:none`) **plus** a faint cream text-shadow. Use `text-wrap: balance` on all centered titles/body so no single word is orphaned on its own line.
- Illustration (lower half): `char.ava.reading` anchored **physical bottom-left** (left edge ≈ 2% of width), width ≈ 76% of frame (and height ≤ 34dvh), bottom ≈ 0.25H (so its base stays clear of the CTA, which starts at ≈ 0.81H).
  - Glow bubble to the **physical right** of Ava and slightly above her head-line (**must never cover Ava's face**: circle of `min(150px, 19dvh)`, right edge ≈ 3% from the physical right frame edge, vertical center ≈ 0.43H, so it sits in the free space right of her head and above the book; verified at 320×560, 390×844 and 412×915; translucent warm radial gradient + 1px white-ish ring, blurred glow) containing `decor.tree` (~70px) and a few CSS/SVG **gold stars** (twinkle) and two **music-note SVGs** (plum/pink) floating. Whole bubble floats ±6px over `floatS`. This is the "simple version" — castle/bird art does not exist yet.
  - Ava breathes: scale 1 → 1.015 over 3.5s.
- Controls: see §4 (shared).

### 3.3 Onboarding step 2 — ref `ref_3_onboarding2.jpg`
- Background `bg.garden` (cover, `50% 100%`).
- Logo top ≈ 0.05H (the balloon is at the physical top-right — keep clear).
- Text block like step 1 (`onboarding.step2.*`), top ≈ 0.17H (below the logo), max-width 280px (the castle sits at the physical right edge, y 24–38%).
- Character group, **feet planted on the path at ≈ 0.77H** (CTA top is at ≈ 0.81H), group height ≤ 34dvh, must NOT overlap the controls:
  - `char.friend.squirrel` behind-center (top of the group), width ≈ 150px
  - `char.ava.wave_book` center-front, largest, width ≈ 175px
  - `char.friend.bunny` **physical left**, width ≈ 120px
  - `char.friend.fox` **physical right**, width ≈ 135px
  - Entrance: pop-in with 80ms stagger (translateY 16→0, opacity). Ava waves: gentle rotate ±3° around bottom center, 2.4s loop.
- Controls: see §4.

### 3.4 Phone — refs `ref_4a_phone.jpg`
- Background `bg.login` (cover, anchored bottom). Decorations (`pointer-events:none`, never under inputs): `decor.login.top_left` at the **physical top-left** (≈ 220px wide), `decor.login.bottom_left` at the **physical bottom-left** (≈ 200px), `decor.login.bottom_right` at the **physical bottom-right** (≈ 180px). Reserve the bottom ≈ 0.14H for decoration.
- Order top→bottom:
  1. `OfflineBanner` (only when offline): full-width, `bannerBg`, scalloped bottom edge, info icon (amber) + `phone.offline` in `amberText`, `role="status"`.
  2. Logo (height `logoHeightForm`, centered) + tagline `brand.tagline` flanked by two tiny leaf glyphs.
  3. Title `phone.title`.
  4. Label `phone.label`, then `PhoneField`.
  5. Helper/error line (reserved height so layout doesn't jump).
  6. `ConsentRow`: checkbox + `phone.consent`; row hit-area ≥ 48px.
  7. Hairline divider, then links row: `phone.linkPrivacy` • `phone.linkTerms` (underlined, `evergreen`). Each opens a `BottomSheet` with a placeholder text (`phone.legalPlaceholder*`) and a close button.
  8. CTA `phone.cta` (arrow-left icon at inline-end).
- `PhoneField`: height `fieldHeight`, radius `field`, bg `fieldBg`, 2px border `line`; focus = 2px `apricot` border + soft glow; error = 2px `amber` border. `inputMode="numeric"`, `autocomplete="tel"`, `dir="ltr"` with `text-align: right`. Accepts Persian/Arabic-Indic/Latin digits, strips spaces/dashes, **displays Persian digits**, max 11 digits after normalization. Normalization: accept `09xxxxxxxxx`, `9xxxxxxxxx`, `+989xxxxxxxxx`, `00989xxxxxxxxx` → canonical `09xxxxxxxxx`; valid regex `^09\d{9}$`.
- Validation timing: do **not** scold while typing. Show `phone.errorInvalid` (amber, info icon, `aria-live="polite"`) after blur, or after the user presses the CTA, or when ≥ 11 digits are typed and invalid.
- CTA enabled only when phone valid **and** consent checked. Disabled style: `disabledBg`/`disabledText`, `aria-disabled="true"` (still focusable). Pressing it while disabled gives gentle guidance: invalid phone → show error + focus field; consent missing → pulse the consent row once + show `phone.consentHint` in `amberText`.
- Loading: CTA shows a small spinner + `phone.sending`, inputs disabled, ~900ms (mock), then go to OTP.
- Provider-error state (debug-triggerable): amber message `phone.providerError` above the CTA, CTA re-enabled.

### 3.5 OTP — refs `ref_4b_otp_CHOSEN.jpg` (layout) and `ref_4b_otp_alt_not_chosen.jpg` (only for its error look)
- Top ≈ 36% = scene: `bg.garden` (cover, anchored **top**) + logo (height `logoHeightOtp`) + tagline.
- Bottom sheet: height follows its content, clamped between 0.62H and 0.80H (top edge ≈ 0.34H at 390×844); the scene fills the rest. At heights < 640px hide the tagline; if the content still does not fit, the sheet scrolls internally (never the whole page). Style: bg `cream`, top radius `sheetTop`, `shadow.sheet`, small leaf-sprig SVG ornaments in both top corners (evergreen, 45% opacity). The castle may peek over the sheet's physical top-right corner.
- Sheet content, top→bottom:
  1. Title `otp.title`.
  2. Subtitle `otp.subtitle` with the phone number (Persian digits, `dir="ltr"`) in `berry`, weight 600.
  3. Outline pill button `otp.editPhone` + pencil icon (height 48, border 2px `evergreen`, text `evergreen`, 600). Returns to Phone with number and consent preserved.
  4. `OtpInput`: 6 boxes **filled from the right** (first digit in the right-most box — natural in RTL flex). Box height `otpBoxH`, **width = clamp(40px, (100vw − 40px − 5×gap) / 6, `otpBoxW`)** with gap 10px (6px when the viewport is < 360px wide) so all 6 boxes always fit inside the sheet padding (20px) — at 320px wide they are ≈ 42px wide; the whole row (not each box) is the ≥ 48px touch target. Radius `otpBox`, bg `creamBox`, 1px `line` border (so empty boxes stay visible on the cream sheet), soft `shadow.box`; digit `otpDigit` 800 `evergreenDeep`; active box: 2.5px `evergreen` border + blinking caret. Implementation: ONE visually-hidden `<input inputMode="numeric" autocomplete="one-time-code" maxLength=6>` drives the 6 visual boxes (supports SMS autofill and paste; accepts Persian digits). Tapping the boxes focuses it.
  5. Message line (reserved height): states below.
  6. Timer/resend row.
  7. CTA `otp.cta` (arrow-left), disabled until 6 digits.
- OTP states:
  - **typing**: normal.
  - **auto-verify**: when the 6th digit is entered, verify after 250ms (button also works).
  - **verifying**: boxes dimmed, CTA spinner, `otp.verifying`.
  - **wrong** (`otp.wrong`): boxes get `amberTint` bg + `amber` border and shake gently (`shakeMs`, ±6px ×3), leaf icon + message in `amberText`; digits clear on next keystroke; attempt counter ++.
  - **expired** (after `expirySeconds`): `otp.expired`, resend enabled immediately.
  - **locked** (after `maxWrongAttempts` wrong): `otp.locked`, boxes disabled, mock cooldown `lockSeconds` with a visible countdown, then reset.
  - **resend countdown**: `otp.resendIn` with `mm:ss` in `berry`, Persian digits, from `resendSeconds`; at 0 it becomes a button `otp.resend` (refresh icon, ≥ 48px) → resets timer, clears boxes, shows toast `otp.resent`.
  - **success**: boxes tint evergreen + check mark, `otp.success`, navigate to Home after 600ms.
  - Value must survive rotation / app-background / re-render (keep in flow state).
- Screen-reader: each box has `aria-label` from `otp.a11yBox`; message line `aria-live="polite"`; errors are communicated by **text + shake + icon**, never color alone.

### 3.6 Home (placeholder)
- `bg.garden`, logo, `char.ava.wave_book` centered, `home.title`, small note `home.placeholderNote`, and a small dev button `home.restart` (resets flow to Splash). This screen exists only to close the flow.

## 4. Shared onboarding controls (both steps use the SAME layout)

Decision: unify on the centered-wide-CTA layout (from `ref_3_onboarding2.jpg`).
- Primary CTA `onboarding.next`: centered, width `min(320px, 100% − 64px)`, height `ctaHeightOnboarding`, pill, bg `evergreen`, label white 800 `cta`, arrow-left icon at inline-end, `shadow.cta`. Bottom ≈ 0.115H.
- `PageDots`: **2 dots** centered under the CTA (10px; active = evergreen pill 24px wide, idle `dotIdle`). Dots follow the document direction (RTL): step 1 = the **right-most** dot, step 2 = the left-most; progress grows toward the left, like the arrow. Do not force `dir="ltr"` on them. `role="img"` + `aria-label` from `onboarding.a11yDots`.
- `SkipButton` `onboarding.skip`: text button at bottom **inline-end** (left in RTL), `muted`, min hit area 48×48, drawn on a **soft cream pill** (`rgba(254,247,225,.84)`, radius 999, 16px horizontal padding) because it sits over flowers/foliage and would otherwise be illegible. Skips straight to Phone from either step.
- Gestures: horizontal swipe in **RTL logic** — forward = finger moves **left→right**, back = right→left (threshold 60px). Buttons always work. No auto-advance.
- Press feedback: scale .97 over `pressMs`. Focus-visible ring: 3px `plum`, offset 3px.

## 5. Accessibility minimums
- Touch targets ≥ `minTouch` (48); onboarding CTA ≥ 64.
- Contrast: all text pairs in tokens `_meta.contrast_computed_WCAG_2_1` are ≥ 4.5 (computed). Keep it that way; if you add a color, compute its ratio.
- `lang="fa"`, labelled controls, logical focus order (RTL), decorative images `alt=""`, logo `alt="آوا"`.
- Visible focus; no hover-only affordances.

## 6. Debug panel (only with `?debug=1`)
Floating small panel (physical bottom-right, collapsed by default, outside the app frame on wide screens) to: jump to any screen; toggle `offline`; force `phone invalid`, `provider error`, `loading`; force OTP `wrong`, `expired`, `locked`, `verifying`, `success`; toggle reduced motion. It must not be reachable without the flag and must not change production visuals.

## 7. Auth mock contract (mirror later in Dart)
```ts
interface AuthService {
  requestOtp(phone: string): Promise<{ ok: true; resendAfterSec: number } | { ok: false; reason: 'network' | 'provider' }>;
  verifyOtp(phone: string, code: string): Promise<{ ok: true } | { ok: false; reason: 'wrong' | 'expired' | 'locked' | 'network' }>;
}
```
`MockAuthService`: ~700ms latency; accepts `123456`; 5 wrong attempts → `locked`; code expires after 300s; request errors injectable from the debug panel. Never log or store the phone number anywhere except in-memory flow state.

## 8. Known asset gaps (use simple stand-ins; do NOT generate or fetch new art)
- Glow-bubble castle/bird art for Onboarding 1 → CSS/SVG stars + notes + `decor.tree`.
- Logo is low-res (~165×243 px true size): keep CSS height ≤ 120px (that is what the `logoHeight*` tokens do).
- Friend character outfits differ slightly from the screenshot — accepted for now.
- No icon set: draw inline SVG (arrow-left, pencil, refresh, check, info, leaf, close).

## 9. Open decisions — do NOT resolve on your own, just keep the code easy to change
1. Wordmark in the logo reads «اوا» while the product name is written «آوا». Use «آوا» in text; flag only.
2. Legal text for privacy/terms is pending (placeholders only).
3. Whether Onboarding 2's CTA should read «ادامه» or «شروع» (screenshot says «ادامه»).
4. Final art for the splash (Ava is baked into the scene).
