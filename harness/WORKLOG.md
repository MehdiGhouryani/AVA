# دفتر اجرای هارنس AVA

این فایل فقط شاهد اجرای واقعی است. عامل کدنویسی حق ندارد گیت انسانی یا تست اجرا‌نشده را `passed` ثبت کند.

## قالب هر ورودی

```text
Date (Asia/Tehran):
Task ID:
Agent/tool:
Baseline source hash/version:
Files changed:
Commands actually executed:
Results:
Blocked by:
Evidence paths:
Next action:
```

## ۲۰۲۶-۱۰-۲۸ — آماده‌سازی هارنس

- Task ID: `harness-bootstrap` (خارج از roadmap اجرایی)
- نتیجه: roadmap ماشین‌خوان، validator، CLI، playbookها و سیاست fail-closed برای delivery صوت اضافه شد.
- تست واقعی: CMS type-check و ۱۸/۱۸ تست دامنه موفق.
- blocker: Docker/PostgreSQL/MinIO و toolchain Native در این محیط اجرا نشده‌اند.
- قدم بعد: `061-db-s3-integration` یا `061-native-toolchain` مطابق امکانات محیط بعدی.

## ۲۰۲۶-۱۰-۲۸ — تلاش مجاز برای ساخت APK

- Task ID: `061-native-toolchain` (تلاش محیطی؛ وضعیت roadmap برای runner بعدی ready باقی می‌ماند)
- درخواست مالک: ساخت APK و تحویل سورس نهایی.
- JDK: OpenJDK 17.0.20 موجود.
- Gradle: wrapper 8.11.1 موجود، distribution موجود نیست؛ دانلود با `Network is unreachable` متوقف شد.
- Android toolchain: `ANDROID_HOME/ANDROID_SDK_ROOT`، `sdkmanager`، platform، build-tools، `aapt2` و `apksigner` موجود نیستند.
- نتیجه: configuration/test/lint/assemble شروع نشد و APK/AAB تولید نشد.
- کار انجام‌شده: اسکریپت build/verify یک‌مرحله‌ای و سند تحویل نهایی افزوده شد؛ project audit، harness و ۱۸ تست CMS موفق‌اند.
- قدم بعد: اجرای همان اسکریپت در runner دارای Android SDK 35 و Gradle 8.11.1.

## ۲۰۲۶-۱۰-۳۱ — فاز ۰: هم‌ترازی و پاک‌سازی پایه

- Date (Asia/Tehran): ۲۰۲۶-۱۰-۳۱
- Task ID: `phase-0-align` (خارج از roadmap اجرایی، مانند `harness-bootstrap`؛ مرحلهٔ P0 از `docs/EXECUTION_PHASES_FA.md`)
- Agent/tool: Claude، sandbox بدون Android SDK و بدون دسترسی به Gradle
- Baseline source hash/version: `main` `cd9c581`، نسخهٔ Native ۰٫۶٫۱
- Files changed: `CLAUDE.md`، `PROJECT_STATE.md`، `.agents/skills/ava-app-engineering/SKILL.md`، `docs/EXECUTION_PHASES_FA.md`، `docs/CURRICULUM_BLUEPRINT_FA.md`، `docs/adr/0004-content-delivery-v1-bundled-fa.md`، `docs/README.md`، `design/screen-references.audit.json`، `harness/WORKLOG.md`. هیچ فایلی زیر `android/`، `design/content*`، `cms/` یا `src/` تغییر نکرد.
- Commands actually executed: `python3 scripts/harness.py validate`، `python3 scripts/validate-harness.py`، `python3 scripts/audit-project.py`
- Results: روی `main` فرمان `audit-project.py` به‌خاطر سه مرجع `.aistudio/...` که در clean push حذف شده‌اند شکست می‌خورد. پس از انتقال این سه ورودی به `removedEntries`، audit کامل پاس شد. validate و validate-harness پاس شدند. پیام موفقیت اسکریپت هنوز «۱۳ مرجع» می‌گوید (متن ثابت، اکنون ۱۰)؛ دست نخورد.
- Blocked by: ADR-0004 پیشنهادی است و تا تأیید مالک روی roadmap اعمال نمی‌شود. تصمیم‌های DG-1 تا DG-4 صریح نگرفته شده‌اند؛ پیش‌فرض‌ها در `PROJECT_STATE.md` بخش ۸ ثبت شده‌اند.
- Evidence paths: این ورودی، `PROJECT_STATE.md` بخش ۸. `docs/PROGRESS_FA.md` و `docs/NATIVE_VERIFICATION.json` دست نخوردند چون سورس Native تغییر نکرده.
- Next action: P1 (`061-native-toolchain`) روی ماشین دارای JDK 17، Android SDK 35 و Gradle 8.11.1.
