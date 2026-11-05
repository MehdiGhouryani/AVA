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

## ۲۰۲۶-۱۱-۰۳ — صفحه‌های A تا D، گام ۲: منطق خالص پیشرفت

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۳
- Task ID: `phase-2b-progression-logic` (خارج از roadmap اجرایی؛ `docs/COMPLETION_PLAN_FA.md` گام ۲)
- Agent/tool: Claude، sandbox بدون Android SDK؛ `kotlinc` 2.1.20
- Baseline source hash/version: شاخهٔ `phase-4i-activity-kinds` `59bab90`
- Files changed: `content/Progression.kt` (تازه)، `ProgressionTest.kt` (تازه)، `scripts/jvm-core-check.py` (کلاس تازه)، `docs/COMPLETION_PLAN_FA.md`، `PROJECT_STATE.md`، این فایل
- Commands actually executed: `jvm-core-check` (سه بار: عادی و دو آزمون جهش)، `harness.py validate`، `audit-project.py`
- Results: ۳۸ تست پاس، ۰ شکست (۶ تازه). آزمون جهش ۱ (شروع هفته از دوشنبه) و ۲ (حذف سقف ۶۰ روز) هر دو تست را شکست دادند و با بازگرداندن سبز شدند. کارت کشف از `completed` مشتق می‌شود؛ پس «فقط یک‌بار» ذاتی است و ذخیرهٔ تازه نمی‌خواهد.
- **اجرا نشد:** ذخیرهٔ `activeDays` در DataStore و هر UI (Compose، ViewModel).
- Blocked by: —
- Evidence paths: `android/app/src/test/java/com/ava/reading/ProgressionTest.kt`
- Next action: ذخیره و ViewModel برای گل‌ها، سپس UI صفحه‌های A تا D.

## ۲۰۲۶-۱۱-۰۳ — صفحه‌های A تا D، گام ۲ب: ذخیرهٔ روزهای فعال برای گل‌ها

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۳
- Task ID: `phase-2b-flowers-storage` (خارج از roadmap اجرایی؛ `docs/COMPLETION_PLAN_FA.md` گام ۲ب)
- Agent/tool: Claude، فقط ویرایش و خواندن کد؛ بدون Gradle
- Baseline source hash/version: شاخهٔ `phase-2b-progression-logic` `02ed739`
- Files changed: `storage/LearnerRepository.kt` (`Learner.activeDays`، کلید `active_days`)، `ui/AvaViewModel.kt` (ثبت روز هنگام تکمیل درس؛ پاک‌شدن در بازنشانی)، `LearnerPersistenceTest.kt` (یک تست رفت‌وبرگشت)، `docs/COMPLETION_PLAN_FA.md`، `PROJECT_STATE.md`، این فایل
- Commands actually executed: `harness.py validate`، `audit-project.py`، `jvm-core-check`، `grep` برای مقایسهٔ کل `Learner` در تست‌ها
- Results: فیلد در انتهای constructor با مقدار پیش‌فرض است، پس سازنده‌های موضعی موجود (از جمله تست رفت‌وبرگشت قبلی) همچنان معتبرند. هیچ تستی کل `Learner` را پس از تکمیل درس مقایسه نمی‌کند. ثبت روز فقط هنگام تکمیل درس است، نه مرور.
- **اجرا نشد:** DataStore، ViewModel و تست جدید `activeDaysSurviveStoreCloseAndReopen` (نیاز به Gradle). همه `read in code`.
- Blocked by: toolchain Native
- Evidence paths: `android/app/src/test/java/com/ava/reading/LearnerPersistenceTest.kt`
- Next action: UI صفحه‌های A تا D.

## ۲۰۲۶-۱۱-۰۳ — بازبینی تا این مرحله و آمادگی برای ادامه

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۳
- Task ID: `phase-2b-review` (خارج از roadmap اجرایی)
- Agent/tool: Claude، sandbox بدون Android SDK؛ `kotlinc` 2.1.20
- Baseline source hash/version: شاخهٔ `phase-2b-flowers-storage` `27c0095`
- Files changed: `CLAUDE.md`، `PROJECT_STATE.md`، `PROMPT_01.md`، `.agents/skills/ava-app-engineering/SKILL.md`، `.agents/skills/ava-duolingo-learning/SKILL.md` و `.agents/skills.lock.json` (hash تازه)، `scripts/audit-project.py`، `scripts/check-docs.py` (تازه)، `harness/roadmap.json` (فقط یک عنوان)، `docs/` (DEVICE_QA، SKILLS، TEST_STRATEGY، PROJECT_MAP، PROGRESS، MASTER_ROADMAP، README، READINESS_REVIEW)، این فایل
- Commands actually executed: `check-docs.py`، `audit-project.py`، `harness.py validate`، `validate-harness.py`، بررسی نحو با `kotlinc` روی کل سورس `main/` برای شاخهٔ فعلی و `cd9c581`
- Results: ۴۳ سند با کد تطبیق داده شد؛ ۹ یافته (تناقض ۴۸ در برابر ۶۴ در پنج متن، پیام audit، شمارش‌های کهنه، یک عنوان هارنس، قدیمی‌بودن نقشهٔ راه، یک سند بدون برچسب تاریخی، سه سند بی‌خبر از افزوده‌ها، تست پروب بیرون از audit) اصلاح شد. بررسی نحو: صفر خطای نحوی؛ ۱۹۹۴ خطا در برابر ۱۹۸۱ روی `main` که همه از نبودن کتابخانه‌اند.
- **اجرا نشد:** Gradle و Compose. «بررسی نحو» تضمین کامپایل نیست.
- Blocked by: toolchain Native
- Evidence paths: `docs/READINESS_REVIEW_FA.md`
- Next action: UI صفحه‌های A تا D در برش‌های کوچک.

## ۲۰۲۶-۱۱-۰۳ — صفحه‌های A تا D: UI

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۳
- Task ID: `phase-2b-ui-a` (خارج از roadmap اجرایی؛ `docs/COMPLETION_PLAN_FA.md` گام ۳)
- Agent/tool: Claude، فقط ویرایش و خواندن کد؛ `kotlinc` فقط برای بررسی نحو و JVM-only
- Baseline source hash/version: شاخهٔ `phase-2b-review` `0e58d9f`
- Files changed: `AlbumScreen.kt` (تازه)، `LessonScreens.kt`، `ProfileScreens.kt`، `HomeScreen.kt`، `AvaApp.kt`، `AvaViewModel.kt` (`Route.Album` و مسیر بازگشت)، `design/strings.fa.json` و منابع تولیدی (۲۶ رشتهٔ پیشنهادی)، `NativeUiTest.kt` (۲ تست)، `docs/` (COMPLETION_PLAN، SCREEN_MATRIX، PROJECT_MAP)، `PROJECT_STATE.md`
- Commands actually executed: `jvm-core-check` (۳۸ پاس)، `sync-android-resources --check`، `check-docs.py`، بررسی نحو با `kotlinc` روی کل سورس، grep واژه‌های ممنوع روی رشته‌های تازه
- Results: چهار برش جدا کامیت شد. بررسی نحو: صفر خطای نحوی؛ خطاهای خطوط تازه همه از نوع «پیدا نشد» به‌جز یک «نوع پارامتر قابل استنباط نیست» روی `?.let {card->` در `LessonResultScreen` که همان خانوادهٔ ۳۷ خطای cascade در `main` است (بدنهٔ lambda به نمادهای Compose پیدا‌نشده ارجاع می‌دهد). رشتهٔ «روزهای خالی جریمه ندارند» به «هر روز که تمرین کنی، یک گل می‌شکفد.» تغییر کرد تا مفهوم جریمه را به کودک نیاورد.
- **اجرا نشد:** Gradle، Compose، Robolectric و دو تست UI تازه. انیمیشن جشن عمداً نیست (قاعده ۷)، پس کاهش حرکت اینجا موضوعیت ندارد.
- Blocked by: toolchain Native
- Evidence paths: `android/app/src/main/java/com/ava/reading/ui/screens/AlbumScreen.kt`
- Next action: F1 تا F3 (renderer یافتن، حباب، جفت‌یابی) و سیم‌کشی ViewModel.

## ۲۰۲۶-۱۱-۰۳ — F1 تا F3: renderer یافتن، حباب، جفت‌یابی

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۳
- Task ID: `phase-2b-games` (خارج از roadmap اجرایی؛ `docs/COMPLETION_PLAN_FA.md` گام ۴)
- Agent/tool: Claude، فقط ویرایش و خواندن کد؛ `kotlinc` برای بررسی نحو
- Baseline source hash/version: شاخهٔ `phase-2b-ui-a` `6c73490`
- Files changed: `LessonScreens.kt` (`FindTask`، `BubbleTask`، `MatchTask`، استخراج `ListenCard`)، `AvaViewModel.kt` (`flip`، `clearMismatch`، استخراج `recordChecked`)، `AvaActions.kt`، `design/strings.fa.json` و منابع تولیدی (۶ رشتهٔ پیشنهادی)، `validate-content-contract.py` و `test-activity-kinds.py` (پیام قید)، `docs/` و `PROJECT_STATE.md`
- Commands actually executed: بررسی نحو با `kotlinc` روی کل سورس، `jvm-core-check`، `sync --check`، `check-docs.py`، `test-activity-kinds.py`، `audit-project.py`
- Results: صفر خطای نحوی؛ همهٔ خطاهای خطوط تازه از نوع «پیدا نشد». یک import لازم (`animateFloat`) را با بازبینی دستی پیدا و اضافه کردم (فایل importهای صریح دارد و بررسی نحو چنین خطایی را نمی‌گیرد). Match طراحی «کارت‌های رو» است نه حافظه‌ای؛ حباب‌ها حدود ۵dp بر ثانیه حرکت می‌کنند و در کاهش حرکت ثابت‌اند؛ اشتباه شمرده نمی‌شود.
- **اجرا نشد:** Compose، ViewModel، Robolectric. تست UI برای renderer‌ها نوشته نشد، چون ساخت `AvaState` با محتوای سفارشی بدون Gradle قابل راستی‌آزمایی نبود؛ fixture آن در I8 می‌ماند.
- Blocked by: toolchain Native؛ برداشتن قید `unrendered_kinds` پس از QA دستگاه
- Evidence paths: `android/app/src/main/java/com/ava/reading/ui/screens/LessonScreens.kt`
- Next action: G1 تا G3 (مسیر، دیالوگ‌ها، کاهش حرکت پیش‌فرض)، سپس I3 تا I9.

## ۲۰۲۶-۱۱-۰۳ — بهبود UI/UX اصولی: G1 تا G3 و دو بهبود همراه

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۳
- Task ID: `phase-2c-ux` (خارج از roadmap اجرایی؛ `docs/UX_CONFORMANCE_FA.md` نسخهٔ ۰٫۲)
- Agent/tool: Claude، فقط ویرایش و خواندن کد؛ `kotlinc` برای بررسی نحو و JVM-only
- Baseline source hash/version: شاخهٔ `phase-2b-games` `d102951`
- Files changed: `content/Progression.kt` (`PathNode`، `pathNodes`)، `ProgressionTest.kt` (۲ تست)، `HomeScreen.kt` (گرهٔ فعلی)، `AvaApp.kt` (دیالوگ‌ها، حرکت سیستم)، `LessonScreens.kt` (آوا در بازخورد، پاپ)، `AlbumScreen.kt` (پاپ)، `docs/UX_CONFORMANCE_FA.md` (نسخهٔ ۰٫۲ و جدول اصل هر بهبود)، `docs/COMPLETION_PLAN_FA.md`، `docs/PROJECT_MAP_FA.md`، `PROJECT_STATE.md`
- Commands actually executed: `jvm-core-check` (۴۰ پاس + آزمون جهش)، بررسی نحو با `kotlinc`، `check-docs.py`، `sync --check`، `audit-project.py`
- Results: منطق مسیر با دو تست و آزمون جهش (۳۸ پاس و ۲ شکست) ثابت شد. بررسی نحو: صفر خطای نحوی؛ خطاهای خطوط تازه «پیدا نشد» و دو «نوع پارامتر قابل استنباط نیست» روی lambdaهای `let` دیالوگ‌ها که از قبل در `main` هم هستند. هیچ رشتهٔ تازه‌ای افزوده نشد.
- **اجرا نشد:** Compose، Robolectric؛ همهٔ تغییرهای ظاهری `read in code`. اثر واقعی روی کودک آزموده نشده.
- Blocked by: toolchain Native؛ آزمون با کودک (فاز ۷)
- Evidence paths: `docs/UX_CONFORMANCE_FA.md` بخش ۷
- Next action: زیرساخت محتوا I3 تا I9.

## ۲۰۲۶-۱۱-۰۵ — تسک 061-native-toolchain و رفع P0 (بدون اجرای Gradle)

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۵
- Task ID: `061-native-toolchain` (تسک آمادهٔ فاز ۰۶۱؛ v1-2 و v1-3 از طرح v1)
- Agent/tool: Muse، شاخهٔ `phase-1-native-toolchain` از `main` (`e38d940`)؛ شاخه‌های پیاده‌سازی جدا از `main` منشعب می‌شوند، نه روی `phase-0-v1-phasing`
- Baseline source hash/version: `main` `e38d940`
- Files changed: `AvaViewModel.kt` (بارگذاری محتوا به `loadContent()` جدا شد؛ شکست، بنر تلاش دوباره را نشان می‌دهد و `retryStorage()` محتوا را هم دوباره بار می‌کند)، `AvaViewModelTest.kt` (دو تست رگرسیون: محتوای همیشه‌خراب و یک‌بار‌خراب)، `design/strings.fa.json` + `android/app/src/main/res/values/strings.xml` (رشتهٔ نسخه «۰٫۴٫۱» به «۰٫۶٫۱»)، `docs/PROGRESS_FA.md`، `docs/NATIVE_VERIFICATION.json`، `PROJECT_STATE.md` (شمار تست ۱۲۰ به ۱۲۲)
- توجه: `android/gradlew` در ریپو mode 644 دارد و `./gradlew` روی کلون تازه کار نمی‌کند؛ تغییر mode با API گیت‌هاب قابل push نیست و موقع مرج با `chmod +x android/gradlew` انجام می‌شود.
- Commands actually executed: `apt-get install openjdk-17-jdk` (JDK 17.0.20.1)؛ دانلود با curl: cmdline-tools اندروید، `platforms;android-35`، `build-tools;35.0.0`، `platform-tools`، توزیع Gradle 8.11.1 (SHA-256 تایید شد)؛ `gradle testDebugUnitTest lintDebug --no-daemon`؛ `python3 scripts/sync-android-resources.py` و `--check`؛ `audit-project.py`، `validate-harness.py`، `check-docs.py`، `validate-content-contract.py`
- Results: همهٔ validatorهای پایتون پاس شدند؛ `sync --check` تایید کرد رشتهٔ نسخه «نسخهٔ توسعه ۰٫۶٫۱» است؛ شمار تست سورس ۱۲۲ شد. تغییرهای Kotlin فقط `read in code` هستند و کامپایل نشده‌اند.
- **اجرا نشد:** `testDebugUnitTest` و `lintDebug` (گیت‌های تسک). دلیل دقیق: سندباکس سوکت‌های JVM را می‌بندد (`other_tcp` روی Deny)؛ با جاوای ساده بازتولید شد — هر سوکت جاوا (حتی localhost) به پیام «muse: Other TCP connections is turned off… switch other_tcp from Deny to Ask» می‌رسد. دانلود wrapper، IPC حلقه‌به‌حلقهٔ daemon و دانلود وابستگی‌ها هر سه از همین علت می‌میرند. curl سالم است. رفع آن: کاربر در Muse settings ← Permissions ← Direct network protocols گزینهٔ `other_tcp` را روی Ask بگذارد؛ پس از آن گیت‌ها دوباره اجرا می‌شوند.
- Blocked by: تنظیم `other_tcp` در سندباکس (تصمیم کاربر)؛ سپس QA دستگاه و بازبینی متخصص
- Evidence paths: `docs/NATIVE_VERIFICATION.json` (بخش `nativeUnitAndCompose` و شمار تست)، `docs/PROGRESS_FA.md` (بروز‌رسانی ۲۰۲۶-۱۱-۰۵)
- Next action: پس از Ask شدن `other_tcp`، اجرای گیت‌های تسک و ثبت نتیجهٔ واقعی؛ سپس مسیر موازی بازبینی متخصص/صوت و حساب/حقوقی از روز اول.

## ۲۰۲۶-۱۱-۰۵ — تکمیل v1-3 (موارد ۳ تا ۸) و I7 در شاخهٔ `phase-1-native-toolchain` (بدون اجرای Gradle)

- Date (Asia/Tehran): ۲۰۲۶-۱۱-۰۵
- Task ID: `061-native-toolchain` (تسک آمادهٔ فاز ۰۶۱؛ v1-3 و I7 از طرح v1)
- Agent/tool: Muse، شاخهٔ `phase-1-native-toolchain` از `main` (`e38d940`)؛ شاخه‌های پیاده‌سازی جدا از `main` منشعب می‌شوند، نه روی `phase-0-v1-phasing`
- Baseline source hash/version: `main` `e38d940`؛ ادامهٔ کامیت `cd50833` روی همین شاخه (PR #2)
- Files changed: `AuthScreens.kt` + `design/strings.fa.json` + `strings.xml` + `TextIds.kt` (حذف `mock_hint` از UI انتشار؛ رشته‌ها ۲۹۵ به ۲۹۴)، `Exercise.kt` (`SessionSnapshot` نسخهٔ ۲ با `wrongCount` و وضعیت Match، `restore()` با اعتبارسنجی Match، guard رد کردن Match غیرقابل‌بازی)، `AvaApp.kt` (خواندن زندهٔ `ANIMATOR_DURATION_SCALE` با ContentObserver و آستانهٔ ۰٫۵×)، `AvaTheme.kt` (`LayoutThresholds`: عرض ۳۶۰ و ارتفاع ۶۴۰) + ۷ فایل صفحه (یکدست‌سازی آستانه‌ها؛ ۶۵۰ در Auth و ۳۴۰ در Story اصلاح شد)، `LessonScreens.kt` (حباب‌های واکنشی)، `LearnerRepository.kt` (named arguments در `decode`)، `ActivityEngineTest.kt` (۲ تست تازه)، `MainActivityTest.kt` و `AndroidNarratorTest.kt` (تازه)، `NativeUiTest.kt` (assertionهای ۴۸dp به ۶۴dp)، `scripts/validate-audio-delivery.py` (تازه؛ I7)، `harness/roadmap.json` (گیت دوم `080-human-audio-rights`)، `docs/COMPLETION_PLAN_FA.md` (I7 تیک خورد)، `docs/PROGRESS_FA.md`، `docs/NATIVE_VERIFICATION.json`، `PROJECT_STATE.md` (تست ۱۲۲ به ۱۲۷، رشته ۲۹۵ به ۲۹۴)
- توجه: `MockAuthRepository` و کد ثابت `123456` به‌عنوان double تست/توسعه می‌مانند؛ سرویس واقعی در گام v1-7 می‌آید. `android/gradlew` همچنان mode 644 دارد؛ تغییر mode با API گیت‌هاب قابل push نیست و موقع مرج با `chmod +x android/gradlew` انجام می‌شود.
- Commands actually executed: `python3 scripts/sync-android-resources.py` و `--check`؛ `python3 scripts/validate-audio-delivery.py` (بدون تحویل: exit 1 درست؛ با فیکسچرهای WAV/MP3 ساخته‌شده در `/tmp/audiotest`: رفتار OK/FAIL درست؛ `--record` روی کپی inventory: هش و codec و مدت ثبت شد)؛ `audit-project.py`، `validate-harness.py`، `check-docs.py` (۰ مشکل)، `validate-content-contract.py`
- Results: همهٔ validatorهای پایتون پاس شدند؛ `check-docs.py` پس از اصلاح شمار تست و رشته‌ها و stage کردن فایل‌های تازه ۰ مشکل گزارش کرد. شمار تست سورس ۱۲۷ شد (ActivityEngine ۱۲، MainActivity ۱، AndroidNarrator ۲). تغییرهای Kotlin فقط `read in code` هستند و کامپایل نشده‌اند. I7 روی فیکسچرها تمرین شد: WAV سالم OK، MP3 سالم OK، فایل خراب FAIL، فایل غایب FAIL.
- **اجرا نشد:** `testDebugUnitTest` و `lintDebug` (گیت‌های تسک). دلیل دقیق: سندباکس سوکت‌های JVM را می‌بندد (`other_tcp` روی Deny)؛ ۱۸:۲۷ با جاوای ساده دوباره بازتولید شد و هنوز مسدود است. رفع آن: کاربر در Muse settings ← Permissions ← Direct network protocols گزینهٔ `other_tcp` را روی Ask بگذارد؛ پس از آن گیت‌ها دوباره اجرا می‌شوند.
- Blocked by: تنظیم `other_tcp` در سندباکس (تصمیم کاربر)؛ سپس QA دستگاه و بازبینی متخصص
- Evidence paths: `docs/NATIVE_VERIFICATION.json` (شمار تست ۱۲۷)، `docs/PROGRESS_FA.md` (بروز‌رسانی دوم ۲۰۲۶-۱۱-۰۵)، `scripts/validate-audio-delivery.py`
- Next action: پس از Ask شدن `other_tcp`، اجرای گیت‌های تسک و ثبت نتیجهٔ واقعی؛ سپس مسیر موازی بازبینی متخصص/صوت و حساب/حقوقی از روز اول.
