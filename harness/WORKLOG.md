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

## ۲۰۲۶-۱۱-۰۱ — فاز ۰: ثبت تصمیم‌ها و پذیرش ADR-0004

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۱
- Task ID: `phase-0-align` (ادامه، خارج از roadmap اجرایی)
- Agent/tool: Claude، sandbox بدون Android SDK
- Baseline source hash/version: `main` `cd9c581` + شاخهٔ `phase-0-align`
- Files changed: `docs/adr/0004-content-delivery-v1-bundled-fa.md`، `PROJECT_STATE.md`، `docs/EXECUTION_PHASES_FA.md`، `harness/roadmap.json` (چهار `dependsOn`)، این فایل
- Commands actually executed: `python3 scripts/harness.py validate`، `python3 scripts/validate-harness.py`، `python3 scripts/audit-project.py`
- Results: مالک تصمیم‌های DG-1 تا DG-4 را به Claude واگذار کرد؛ تصمیم‌ها و دلیلشان در `PROJECT_STATE.md` بخش ۸ ثبت شد. ADR-0004 پذیرفته شد و وابستگی `080-content-expansion`، `080-human-audio-rights`، `080-device-accessibility-qa` و `080-account-privacy-operations` تغییر کرد (status هیچ task تغییر نکرد).
- Blocked by: `061-educator-review` (گیت انسانی) و toolchain Native.
- Evidence paths: `harness/roadmap.json` (diff در کامیت `chore(P0.4)`)
- Next action: P1 — اثبات پایه.

## ۲۰۲۶-۱۱-۰۱ — فاز ۱ (پیش‌کار): بررسی جزئی JVM-only

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۱
- Task ID: `061-native-toolchain` (پیش‌کار؛ وضعیت roadmap تغییر نکرد و task کامل نشد)
- Agent/tool: Claude، sandbox بدون Android SDK و بدون Gradle؛ `kotlinc` 2.1.20 از GitHub Releases، runtime جاوا ۲۱
- Baseline source hash/version: شاخهٔ `phase-0-align` + `phase-1-baseline`
- Files changed: `android/app/src/test/java/com/ava/reading/ModelTest.kt` (یک خط)، `scripts/jvm-core-check.py`، `scripts/jvm-core-check/*.kt`، `PROJECT_STATE.md`، این فایل
- Commands actually executed: `KOTLINC=… python3 scripts/jvm-core-check.py` (دو بار)
- Results: لایه‌های `content` و `data` (۱۰ فایل Kotlin) با کامپایلر واقعی بدون خطا کامپایل شدند. پیش از اصلاح: ۲۶ تست پاس، ۱ شکست. شکست: `ModelTest.wordBuilderRequiresEveryTokenAndSupportsUndoAndCorrection` از `Catalog.lessons[2]` (اکنون `edge_sounds_03`، تکلیف‌های انتخابی) انتظار تکلیف ساخت واژه داشت؛ درس ساخت واژه `words_03` است. با انتخاب درس بر اساس شناسه، همان assertionها بدون تضعیف: ۲۷ پاس، ۰ شکست.
- این نتیجه **Gradle نیست**: JUnit و kotlinx.coroutines با stand-in ساده جایگزین شده‌اند؛ `AvaViewModelTest`، `LearnerPersistenceTest`، `AuthRepositoryTest`، `NativeUiTest` (Compose/Robolectric/DataStore) و lint اجرا نشدند. در `NATIVE_VERIFICATION.json` ثبت نشد.
- Blocked by: toolchain Native (JDK 17، Android SDK 35، Gradle 8.11.1)
- Evidence paths: `scripts/jvm-core-check.py`
- Next action: اجرای `testDebugUnitTest lintDebug` روی ماشین دارای toolchain (P1.2)

## ۲۰۲۶-۱۱-۰۱ — فاز ۲، قدم P2.1: ماتریس انطباق UX کودک

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۱
- Task ID: `phase-2-ux-audit` (خارج از roadmap اجرایی؛ قدم P2.1 از `docs/EXECUTION_PHASES_FA.md`)
- Agent/tool: Claude، فقط خواندن کد؛ بدون Gradle، Compose و دستگاه
- Baseline source hash/version: شاخهٔ `phase-1-baseline` `4bb8deb`
- Files changed: `docs/UX_CONFORMANCE_FA.md`، `docs/README.md`، این فایل. هیچ فایل کد یا محتوا تغییر نکرد.
- Commands actually executed: خواندن همهٔ فایل‌های `ui/` و `MainActivity.kt`، شمارش وظایف از `design/content.fa.json`، `python3 scripts/harness.py validate`
- Results: ۱۲ شکاف ثبت شد (۵ مورد P0). مهم‌ترین‌ها: نبودن «رد کردن پس از دو اشتباه»، کنترل‌های ۴۸dp به‌جای ۶۴، CTA ‏۶۴ به‌جای ۷۲، و درس `blend_words_04` با ۲ فعالیت. بازخورد صوتی پاسخ درست و بازخورد چندکاناله وجود دارند. همهٔ یافته‌ها `read in code` هستند.
- Blocked by: برش S1 (اندازه‌ها) تا کامپایل Compose در فاز ۱ ادغام نشود.
- Evidence paths: `docs/UX_CONFORMANCE_FA.md`
- Next action: P2.3-S2 (رد کردن پس از دو اشتباه، reducer قابل‌اجرا با `jvm-core-check`) و/یا P3.1.

## ۲۰۲۶-۱۱-۰۱ — فاز ۲، برش S2: رد کردن قدم پس از دو پاسخ نادرست (UX-G01)

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۱
- Task ID: `phase-2-s2-skip` (خارج از roadmap اجرایی؛ P2.3-S2)
- Agent/tool: Claude، sandbox بدون Android SDK؛ `kotlinc` 2.1.20
- Baseline source hash/version: شاخهٔ `phase-2-ux-audit` `6bef9ee`
- Files changed: `content/Exercise.kt` (فیلد `wrongCount`، `canSkip`، `skip`، `check` بدون اثر در بررسی مجدد)، `ui/AvaViewModel.kt` (`skip` و استخراج `finishStep`)، `ui/AvaActions.kt`، `ui/screens/LessonScreens.kt` (یک دکمهٔ ثانویه)، `design/strings.fa.json` (`app.skip_step`، `source: proposed`)، فایل‌های تولیدشدهٔ `strings.xml` و `TextIds.kt`، `ModelTest.kt` (دو تست)، `docs/UX_CONFORMANCE_FA.md`
- Commands actually executed: `python3 scripts/jvm-core-check.py`، `python3 scripts/sync-android-resources.py` و `--check`، `python3 scripts/harness.py validate`، `python3 scripts/audit-project.py`
- Results: JVM-only: ۲۹ تست پاس، ۰ شکست (۲۷ قبلی + ۲ تازه). آزمون جهش: با تغییر آستانه از ۲ به ۱ تست تازه شکست خورد و با بازگرداندن دوباره سبز شد. منابع تولیدی ۲۶۴ رشته (یکی بیشتر) و verify پاس شد.
- **اجرا نشد:** Compose، ViewModel و تست‌های `AvaViewModelTest`/`NativeUiTest`. تغییرات `AvaViewModel.kt`، `AvaActions.kt` و `LessonScreens.kt` فقط `read in code` بازبینی شده‌اند و باید در P1 با Gradle کامپایل شوند. عمداً تست ViewModel/UI اجرا‌نشده اضافه نشد تا خطای کامپایل تست‌ها مانع P1 نشود.
- Blocked by: toolchain Native
- Evidence paths: `android/app/src/test/java/com/ava/reading/ModelTest.kt`
- Next action: P1 (Gradle) سپس S1 اندازه‌ها (UX-G02 تا G05).

## ۲۰۲۶-۱۱-۰۱ — فاز ۲، برش S1: اندازهٔ لمس ۶۴dp و CTA ‏۷۲dp (UX-G02 تا G05)

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۱
- Task ID: `phase-2-s1-touch` (خارج از roadmap اجرایی؛ P2.3-S1)
- Agent/tool: Claude، فقط ویرایش و خواندن کد؛ بدون Gradle و Compose
- Baseline source hash/version: شاخهٔ `phase-2-s2-skip` `8345ac3`
- Files changed: `Components.kt` (Primary ۷۲، Secondary ۶۴، Header و دکمهٔ بازگشت ۶۴)، `HomeScreen.kt` (آواتار ۶۴)، `LessonScreens.kt`، `StoryScreens.kt`، `AuthScreens.kt`، `AvaApp.kt` (دکمهٔ تلاش دوباره)، `design/design_tokens.json` (کلیدهای تازهٔ `touchChild` و `ctaChild`، کلیدهای قدیمی دست‌نخورده)، `NativeUiTest.kt` (دو assertion تقویت‌شده: ۴۸ ← ۶۴)
- Commands actually executed: `python3 scripts/jvm-core-check.py`، `python3 scripts/sync-android-resources.py --check`، `python3 scripts/harness.py validate`، `python3 scripts/audit-project.py`
- Results: همهٔ کنترل‌های کودک اکنون ≥۶۴dp و CTA ‏≥۷۲dp در کد؛ `grep` دیگر `heightIn(min=4x/5x)` برای دکمه‌ها نشان نمی‌دهد (باقی‌ماندهٔ عمدی: متن پاسخ در `LessonChoice` که با padding به ۶۴ می‌رسد و نمایشگر جواب).
- **اجرا نشد:** Compose، Robolectric و دستگاه. چیدمان ۳۲۰×۵۶۰ با CTA ‏۷۲dp، دکمهٔ رد کردن و نوار پیشرفت و فونت ۱٫۵ فقط در فاز ۱ و ۷ معلوم می‌شود. دو assertion تقویت‌شده فقط جایی هستند که کد اندازه را تضمین می‌کند.
- Blocked by: toolchain Native
- Evidence paths: `docs/UX_CONFORMANCE_FA.md`
- Next action: P3.1 (بستهٔ بازبینی متخصص)، سپس S3 (دیالوگ‌ها) و S4 (مسیر).

## ۲۰۲۶-۱۱-۰۱ — فاز ۳، قدم P3.1: بستهٔ بازبینی متخصص آموزش

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۱
- Task ID: `phase-3-educator-pack` (مقدمهٔ `061-educator-review`؛ خود task گیت انسانی است و اجرا نشد)
- Agent/tool: Claude، اسکریپت پایتون آفلاین
- Baseline source hash/version: شاخهٔ `phase-2-s1-touch` `82d047d`
- Files changed: `scripts/export-educator-pack.py`، `docs/EDUCATOR_REVIEW_PACK_FA.md` (تولیدشده)، `docs/README.md`، `PROJECT_STATE.md`، این فایل
- Commands actually executed: `python3 scripts/export-educator-pack.py` و `--check`
- Results: بسته ۱۷ فعالیت، ۹ صفحهٔ قصه، ۲ پرسش قصه، ۲۲ نیاز صوتی و ۸ تصمیم آموزشی را با ستون‌های «تصمیم/توضیح» خالی نشان می‌دهد؛ اسکریپت اگر `audioRef` یک فعالیت در inventory نباشد خطا می‌دهد. هیچ مقدار `approvedForRelease` تغییر نکرد.
- Blocked by: بازبین انسانی (`061-educator-review`) که هنوز تعیین نشده
- Evidence paths: `docs/EDUCATOR_REVIEW_PACK_FA.md`
- Next action: مالک متخصص را پیدا و بسته را برایش می‌فرستد؛ در این فاصله S3/S4 از P2.3.

## ۲۰۲۶-۱۱-۰۳ — زیرساخت محتوا I1 و I2: نوع‌های Find، Bubble، Match و رشد‌پذیری

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۳
- Task ID: `phase-4i-activity-kinds` (خارج از roadmap اجرایی؛ زیرساخت پیش از `080-content-expansion`، که محتوای تازه اضافه نکرد)
- Agent/tool: Claude، sandbox بدون Android SDK؛ `kotlinc` 2.1.20
- Baseline source hash/version: شاخهٔ `phase-3-educator-pack` `94ca981`
- Files changed: `Models.kt`، `ActivityRegistry.kt`، `Exercise.kt` (`flip`، `clearMismatch`)، `design/content.schema.json` (`find`، `bubble`، `matchTask`)، `validate-content-contract.py`، `validate-audio-inventory.py`، `sync-android-resources.py`، `migrate-content-v2-to-v3.py` (قید زیرمجموعه)، `export-educator-pack.py`، `ActivityEngineTest.kt` (۳ تست)، `ModelTest.kt` (شمارش ثابت ← شناسه‌های فریزشده)، `scripts/test-activity-kinds.py`، `docs/ACTIVITY_ENGINE_FA.md`، `docs/COMPLETION_PLAN_FA.md`، `docs/EXECUTION_PHASES_FA.md`، `docs/README.md`، `PROJECT_STATE.md`
- Commands actually executed: `jvm-core-check` (۳۲ پاس)، `test-activity-kinds.py` (۷ از ۷)، `validate-content-contract`، `validate-audio-inventory`، `sync-android-resources --check`، `export-educator-pack --check`، `harness.py validate`، `audit-project.py`
- Results: (۱) قید مهاجرت v2 افزودن هر درس یا واحد را در audit رد می‌کرد؛ اکنون زیرمجموعه‌ای است و تغییر یک عنوان فریزشده همچنان رد می‌شود. (۲) محتوای آزمایشی با ۷ درس و ۲۰ فعالیت شامل سه نوع تازه از validatorها گذشت، generator `TaskKind.Find/Bubble/Match` تولید کرد، Catalog تولیدی کامپایل شد و ۳۲ تست روی آن پاس شد (روی نسخهٔ موقت، نه مخزن). (۳) آزمون جهش: شکستن منطق جفت‌شدن تست را شکست داد.
- **اجرا نشد:** Compose و renderer ‑ این برش هیچ UI ندارد. محتوای منتشرشده تغییر نکرد (۶ درس، ۱۷ فعالیت).
- Blocked by: renderer Match (قید `unrendered_kinds`)
- Evidence paths: `scripts/test-activity-kinds.py`
- Next action: منطق خالص صفحه‌های A تا D با تست JVM.
