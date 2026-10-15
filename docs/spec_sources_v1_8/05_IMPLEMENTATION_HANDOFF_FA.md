> **هشدار نسخهٔ تاریخی:** این سند بازتاب‌دهندهٔ بستهٔ v1.8 در زمان خودش است. عبارت‌هایی مانند «فعلی/منبع حقیقت»، تصمیم‌های محصول و مسیرهای فایل در بدنه فقط نسبت به همان snapshot معتبرند؛ آن‌ها را به ساختار/تصمیم جاری تعمیم ندهید. مسیرها و وضعیت جاری در `docs/PROJECT_STRUCTURE_FA.md` و `AI_START_HERE.md` است؛ قبل از اجرا، تعارض‌ها را با منابع موجود و دستور مالک بررسی کنید. جزئیات: `00_HISTORICAL_PATHS_NOTICE_FA.md`.

# ۰۵: تحویل به تیم پیاده‌سازی

## معماری پیشنهادی
App shell و router تولیدی از دفتر طراحی جدا باشند. route guard پیش از کودک باید auth/session، consent version، profile B و package readiness را بررسی کند. ابتدا S01/S02 در first run؛ ورود مستقیم به خانه برای استفادهٔ واقعی ممنوع است. انتخاب صفحه در نمونه و `window.avaSelect` فقط ابزار review/test است و وارد build تولیدی نمی‌شود.

لایه‌ها:
۱. دامنه: profile، node_run، learned/mastered، reward ledger، package manifest، entitlement.
۲. repository: local DB، sync queue، verified backend/store adapters.
۳. controller: lesson state machine، audio marks، trace engine، companion adapter.
۴. UI: tokenها، کامپوننت‌های child/parent و routes.
۵. CMS جدا: author/reviewer/publisher، assets، approval، build و audit.

React این بسته نمونهٔ تعامل است؛ Flutter implementation ایجاد نشده است. tokenها و SVG قابل انتقال‌اند، نه API یا SDK تولیدی.

## ساختار فایل پیشنهادی Flutter
`features/onboarding/` برای معرفی و handoff بزرگ‌تر؛ `features/auth/` برای سرویس رسمی؛ `features/profiles/` برای نام نمایشی و avatar schema؛ `features/path/` برای خانه B و review؛ `features/lesson/` برای engine و activity variants؛ `features/story/` برای audio marks؛ `features/parent/` برای گزارش و lifecycle حساب؛ `core/packages/` برای دریافت امن؛ `core/ledger/` برای reward exactly-once؛ `design/` برای tokens/components/character states.

در source نمونه، `Art.jsx` فقط art، `contracts.js` copy/pose labels، `CMSWorkspace.jsx` ابزارهای مدیریتی و `App.jsx` orchestration صفحات است. منطق تکراری CMS حذف شد. توکن‌ها در CSS یکجا و JSON handoff ثبت شده‌اند. جداسازی feature moduleهای production وظیفهٔ migration است، نه استفادهٔ مستقیم از monolithic review app.

## قراردادهای داده
شناسهٔ screen از registry پایدار است. برای تغییر copy/asset، نسخهٔ بسته تعیین شود. source refs و تفاوت baseline/proposal حفظ شوند. registryهای prototype و canonical با hash/ID reconciliation بررسی می‌شوند.

profile: ID داخلی، track=B، نام نمایشی طبق سیاست، avatar versioned، تنظیمات صوت/حرکت و انتخاب فعال. avatar هر profile مستقل است؛ schema در سند برند آمده است.

node_run: run_id، node_id، content_version، step_index، began_at/resumed_at، completion_event_id، learned/mastered signals. snapshot زیر ۲۴ ساعت restore می‌شود. با تغییر content_version، migration یا شروع امن لازم است.

reward: کلید یکتا از profile_id + completion_event_id، transaction commit پیش از reveal. fixture نمونه از `profile_id:demo_node_b` استفاده می‌کند و با ورود از دفتر طراحی یک completion **شبیه‌سازی‌شده** را نمایش می‌دهد. **این رفتار خودکار نمونه نباید به router تولیدی منتقل شود.** در تولید، بازکردن S13 بدون completion معتبر فقط read-only/recovery است.

package: version، required_bytes، hash/signature، audio/media/glyph manifests، approved status، atomic install. نصب فقط بعد verify، فضای کافی و commit manifest.

entitlement: server-verified transaction/receipt، expiration، source store، offline cached validity، restore و revoke. UI قادر به grant حق دسترسی نیست.

## CMS
CMS-00: SSO/MFA/session timeout/recovery و secure auth، بدون دریافت credential در prototype.
CMS-01: گیت سلامت با تفکیک ۱۲۰ baseline و ۱۲۳ proposal.
CMS-02: hierarchy ایستگاه/گره/step/asset، version و curriculum validation.
CMS-03: stable asset ID، hash، orphan/missing، source/license و review.
CMS-04: audio slot، متن، واج، فایل، silence/clipping/duplicate.
CMS-05: waveform واقعی، replay، متن/timing، reason-required approve/reject مستقل.
CMS-06: plan catalog، free boundary، store product، entitlement lifecycle و مجوز.
CMS-07: validate → approval → build/sign → publish → rollback.
CMS-08: filter/export/retention و audit با privacy minimization.
CMS-09: author/reviewer/publisher roles، segregation، conflict و denied.

## برنامهٔ اجرای بدون حذف دامنه
مرحلهٔ اول: tokenها، art، پوسته‌ها، onboarding و profile با mock قراردادمحور. خروجی: golden screens، RTL، keyboard/safe area و route guard.

مرحلهٔ دوم: local-first engine، voice/copy parity، S08/S09/S10/S10b، G1..G6، S31/S32/S33 و stories. خروجی: vector/audio approval و session/reward replay tests.

مرحلهٔ سوم: backend/sync/download، consent/account deletion، store entitlement و CMS. خروجی: امنیت، rollback، failure recovery و ownership tests.

مرحلهٔ چهارم: pilot فارسی با کودک/سرپرست، accessibility native، فونت بزرگ، دستگاه کم‌حافظه، بسته ناقص و release gate. این فازبندی CMS/payment/backend را از V1 حذف نمی‌کند.

## کیفیت لازم پیش از قفل
کنتراست، هدف لمس، بدون overflow، pointer/tap alternatives، screen reader labels و focus order، کاهش حرکت، audio transcript parity، retry و مقصد امن، duplicate reward صفر، auth bypass صفر، child paywall صفر، PII در diagnostics صفر، approved content/audio/vector، و privacy/legal review.

محتوای missing یا approval=false باید release را مسدود کند، حتی اگر UI زیبا و validator baseline سبز باشد.
