# وضعیت جاری پروژه AVA

**آخرین بازبینی:** ۲۰۲۶-۱۱-۱۹  
**مخزن:** `MehdiGhouryani/AVA` · شاخهٔ اصلی: `main`

این فایل فقط snapshot عملیاتی کوتاه است. برای جزئیات و شواهد، اسناد canonical زیر را بخوانید؛ تاریخچهٔ آزمون‌ها در این فایل تکثیر نمی‌شود.

## هویت و منبع حقیقت

- محصول اصلی: اپ Native Android با Kotlin و Jetpack Compose در `android/`.
- `src/` و پیکربندی Vite در ریشه، پروتوتایپ تاریخی React هستند؛ آن‌ها را توسعه ندهید و صرفاً به دلیل تاریخی بودن حذف نکنید.
- محتوای canonical: `design/content.fa.json`؛ متن: `design/strings.fa.json`؛ رنگ‌ها: `design/design_tokens.json`؛ فهرست artwork: `design/assets.manifest.json`.
- منابع Android تولیدی‌اند؛ آن‌ها را دستی ویرایش نکنید. مرجع Sync: `scripts/sync-android-resources.py`.
- CMS در `cms/payload/` یک پروژهٔ مستقل است؛ اپ Android مستقیماً به CMS، PostgreSQL یا S3 وصل نمی‌شود.

## وضعیت قابل اتکا

- نسخهٔ Native: **۰٫۶٫۱** · versionCode **۱۶** · minSdk **۲۶** · targetSdk **۳۵**.
- دامنهٔ محتوای توسعه: ۱ سطح، ۳ واحد، ۶ درس، ۱۷ فعالیت، ۲ قصه و ۹ صفحه؛ `approvedForRelease=false`.
- تعداد تست‌های Native در سورس: **۱۵۴**.
- رشته‌های Android تولیدی: **۲۹۵**.
- آخرین اجرای ثبت‌شدهٔ کامل Native در **۲۰۲۶-۱۱-۱۱**: **۱۵۴ تست، ۰ شکست، ۰ خطا** و `lintDebug` موفق.
- پاک‌سازی‌های مخزن پس از آن، از جمله انتقال گالری به source set تست، هنوز با Gradle دوباره اجرا نشده‌اند؛ نتیجهٔ قبلی، تأیید این تغییرات جدید نیست.
- CMS prototype: Payload، پنج collection و ۱۸ تست دامنه؛ اتصال واقعی DB/object storage هنوز اثبات نشده است.
- صوت انسانی تحویل‌شده: **صفر**؛ inventory شامل ۲۲ نیاز صوتی است.

## گیت‌ها و کارهای باز

- `061-db-s3-integration`: آماده در هارنس؛ نیازمند محیط واقعی Docker/PostgreSQL/object storage.
- `080-account-privacy-operations`: آماده در هارنس.
- بازبین آموزشی، حقوق صوت و QA دسترس‌پذیری/دستگاه، گیت انسانی‌اند و عامل خودکار حق بستن آن‌ها را ندارد.
- فازهای CMS و offline sync تا رفع dependencyهایشان مسدود می‌مانند.
- حساب واقعی/SMS، متن حقوقی تأییدشده، تأیید آموزشی و QA دستگاه از آمادگی انتشار مستقل‌اند.

## قواعد ثابت توسعه

1. فارسی، RTL، Vazirmatn و artwork محلی هویت محصول‌اند؛ چهار شخصیت مجاز فقط آوا، خرگوش، سنجاب و روباه‌اند.
2. پنل والد، تبلیغ، پرداخت، خرید سکه، lives، streak تنبیهی و خطای قرمز اضافه نشود.
3. ورود فعلی mock تلفن/OTP است؛ سرویس پیامک واقعی یا دسترسی شبکه بدون تصمیم و مجوز صریح اضافه نشود.
4. شناسه‌های lesson/task/audio پایدارند؛ تغییر یا حذف آن‌ها migration و validator می‌خواهد.
5. UI فقط state/actions دریافت می‌کند؛ I/O و repository داخل Composable ممنوع است.
6. خروجی پیش‌فرض source-only است. APK/AAB فقط با درخواست صریح مالک ساخته می‌شود.
7. taskهای `blocked` یا `human_gate` را شروع یا دور نزنید؛ یک task آماده در هر چرخه، دقیقاً در `allowedPaths` خودش.
8. شواهد را فقط با نتیجهٔ واقعاً اجراشده به‌روزرسانی کنید؛ Robolectric جای QA دستگاه واقعی نیست.

## مسیر کار عامل‌ها

ابتدا `AGENTS.md`، `ZCODE.md`، وضعیت در `docs/PROGRESS_FA.md` و `harness/roadmap.json` را بخوانید. سپس:

```bash
python3 scripts/harness.py validate
python3 scripts/harness.py status
python3 scripts/harness.py next
```

فقط task آماده را مطابق playbook، `readFirst`، `allowedPaths`، acceptance و gateهای خودش اجرا کنید. نتیجهٔ واقعی در `harness/WORKLOG.md` ثبت شود.

## اسناد canonical

- وضعیت و پیشرفت/شواهد: `docs/PROGRESS_FA.md` و `docs/NATIVE_VERIFICATION.json`
- نقشهٔ اسناد و معماری: `docs/README.md` و `docs/PROJECT_MAP_FA.md`
- UX و طراحی: `docs/design/AVA_UX_DISCOVERY.md` و `docs/design/AVA_DESIGN_SYSTEM.md`
- قواعد عامل: `AGENTS.md`، `CLAUDE.md`، `ZCODE.md`
- فازها و قرارداد اجرا: `harness/roadmap.json` و `harness/phases/`
- سوابق اجرا: `harness/WORKLOG.md`

**مجوز شاخه:** مالک در این گفتگو Push مستقیم به `main` را برای پاک‌سازی و نگهداری مخزن مجاز کرده است. این مجوز به‌تنهایی مجوز تغییرات محصول، طراحی یا انتشار نیست؛ دامنهٔ هر کار جدید باید مطابق درخواست مالک باشد.
