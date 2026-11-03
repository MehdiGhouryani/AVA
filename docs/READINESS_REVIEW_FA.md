# بازبینی تا این مرحله و آمادگی برای ادامهٔ پیاده‌سازی (۲۰۲۶-۱۱-۰۳)

وضعیت: گزارش بازبینی. همه‌چیز زیر `ran` (اجرا شد) یا `read in code` (فقط خوانده شد) برچسب دارد. هیچ ادعایی دربارهٔ «کامپایل‌شدن» یا اجرای Gradle نیست، چون هنوز انجام نشده.

## ۱. دامنهٔ بازبینی

- ۴۳ سند Markdown مخزن (به‌جز مهارت‌های ثالث و `docs/preview|history`) با کد و داده تطبیق داده شد: مسیرهای نوشته‌شده، شمارش‌ها، فهرست `docs/README.md`، قواعد لمس، ادعاهای نسخه.
- تمام تغییرهای Kotlin این شاخه‌ها نسبت به `main` (`cd9c581`) دوباره بازبینی شد، و یک «بررسی نحو» با کامپایلر Kotlin 2.1.20 روی کل سورس `main/` انجام شد.

## ۲. یافته‌ها و اصلاح‌ها

| # | یافته | اصلاح |
|---|---|---|
| ۱ | مهارت قفل‌شدهٔ `ava-duolingo-learning` هنوز «۴۸dp» می‌گفت و با تصمیم DG-3 (۶۴/۷۲dp) تناقض داشت؛ `SKILLS.md`، `DEVICE_QA_FA.md`، `CLAUDE.md` و مهارت `ava-app-engineering` هم همین‌طور یا «در انتظار تصمیم» بودند | هر پنج متن هم‌تراز شد؛ hash مهارت در `.agents/skills.lock.json` تازه شد (`audit` سه hash را می‌سنجد و پاس شد) |
| ۲ | پیام موفقیت `audit-project.py` عدد ثابت «۱۳ مرجع» می‌گفت در حالی که ۱۰ مرجع می‌ماند | عدد از داده خوانده می‌شود |
| ۳ | `PROJECT_STATE.md` شمارش کهنهٔ تست (۱۰۴ ← ۱۱۶) و رشته (۲۶۳ ← ۲۶۴) داشت | اصلاح شد و `scripts/check-docs.py` این رانش را اکنون خودکار می‌گیرد |
| ۴ | عنوان task هارنس `061-native-toolchain` «۱۰۴ تست» را ثابت کرده بود | بدون عدد شد |
| ۵ | `AVA_MASTER_ROADMAP_FA.md` هنوز ترتیب قدیمی را می‌گفت و از ADR-0004 و برنامهٔ تکمیل خبر نداشت | یادداشت به‌روزرسانی افزوده شد |
| ۶ | `PROMPT_01.md` (پرامپت قدیمی وب) برخلاف `DESIGN_BRIEF.md` برچسب «تاریخی» نداشت | برچسب افزوده شد |
| ۷ | `PROGRESS_FA.md`، `PROJECT_MAP_FA.md` و `TEST_STRATEGY_FA.md` از فایل‌ها و لایهٔ JVM-only بی‌خبر بودند | بخش تازه و ردیف لایه افزوده شد؛ شواهد قدیمی دست نخورد |
| ۸ | تست پروب انواع فعالیت در audit نبود؛ بدون آن قید رشد محتوا بی‌نگهبان می‌ماند | `audit-project.py` اکنون `test-activity-kinds.py` را اجرا می‌کند |
| ۹ | مسیرهای مرده در سندها: فقط ارجاع‌های تاریخی و مسیرهای برنامه‌ریزی‌شده؛ مسیر مردهٔ واقعی نبود | `check-docs.py` اجازهٔ مسیرهای برنامه‌ریزی‌شده را صریح نگه می‌دارد |

## ۳. ماتریس شواهد

| لایه | وضعیت | شاهد |
|---|---|---|
| قراردادهای محتوا، صوت، منابع، هارنس، audit | ✅ `ran` | همهٔ اسکریپت‌ها پاس؛ `audit-project.py` کامل پاس |
| منطق خالص (`content`، `data`) | ✅ `ran`، **جایگزین Gradle نیست** | `jvm-core-check`: ۳۸ تست پاس؛ آزمون جهش برای رد‌کردن، جفت‌یابی، هفته و سقف روزها |
| نحو کل کد Kotlin | ✅ `ran` | کامپایلر Kotlin 2.1.20: صفر خطای نحوی؛ ۱۹۹۴ خطا که همه از نبودن Android/Compose/DataStore در این محیط می‌آیند |
| تفاوت خطا با `main` | ✅ `ran` | ۱۹۸۱ ← ۱۹۹۴ (۱۳ خطای تازه)؛ همه از همان نوع‌های «پیدا نشد» و «ناسازگاری نوع» ناشی از نبودن کتابخانه‌اند؛ هیچ خطای نحوی تازه نیست. این سیگنال ضعیف است: مطمئن نمی‌کند که Compose کامپایل می‌شود |
| Compose، ViewModel، DataStore | ❌ اجرا نشد | `read in code` |
| تست‌های ViewModel/UI/Persistence/Auth | ❌ اجرا نشد | ۷۸ تست در سورس اجرا نشده‌اند (ViewModel ۲۷، UI ۴۱، Auth ۵، Persistence ۵)؛ ۳۸ تست دیگر در JVM-only اجرا شده‌اند |
| lint، دستگاه، TalkBack | ❌ | — |

## ۴. ریسک‌ها به ترتیب اثر

۱. **کامپایل Compose.** تغییرهای کامپایل‌نشدهٔ من: `Components.kt`، `HomeScreen.kt`، `LessonScreens.kt`، `StoryScreens.kt`، `AuthScreens.kt`، `AvaApp.kt`، `AvaViewModel.kt`، `AvaActions.kt`، `LearnerRepository.kt` و سه تست. هر صفحهٔ تازه این سطح را بزرگ‌تر می‌کند. کاهش: هر برش یک کامیت کوچک تا در فاز ۱ بتوان با `git bisect` خطا را پیدا کرد.
۲. **تست‌های کهنه در سوئیت‌های اجرا‌نشده.** در تست JVM یک تست به‌خاطر اندیس کهنه شکست خورد؛ احتمالاً مواردی مشابه در `AvaViewModelTest` و `NativeUiTest` هست. همچنین `NativeUiTest` اندازه‌ها را با Robolectric می‌سنجد و برخی ادعاهای چیدمان ممکن است پس از CTA ‏۷۲dp تغییر کنند.
۳. **پیش‌فرض‌های محتوایی بدون متخصص.** طرح سطح ۱ و فهرست واژه‌ها داوری من است؛ تا بازبینی انسانی نباید وارد اپ شود.
۴. **تعارض دو خط مستند.** `main` و مستندات قدیمی هنوز به وب React اشاره دارند؛ `PROJECT_STATE.md` و برچسب‌های تاریخی این را کم‌خطر کرده‌اند.
۵. **رانش سند.** با `scripts/check-docs.py` خودکار گرفته می‌شود؛ باید در پایان هر برش اجرا شود.

## ۵. آمادگی برای ادامه

| پیش‌شرط | وضعیت |
|---|---|
| تصمیم‌های DG-1 تا DG-6 ثبت‌شده | ✅ |
| audit سبز و گیت‌های هارنس معتبر | ✅ |
| فهرست صفحه‌ها و زیرساخت محتوا مشخص | ✅ `COMPLETION_PLAN_FA.md` |
| منطق خالص صفحه‌های A تا D و ذخیره‌شان | ✅ (ذخیره `read in code`) |
| نوع‌های Find، Bubble، Match در موتور | ✅ منطق؛ renderer مانده |
| راهنمای افزودن مقصد | ✅ پایین |
| کامپایل Compose | ❌ تا فاز ۱ |
| متخصص آموزش | ❌ |

### افزودن مقصد (Route) تازه — چک‌لیست (`read in code`)

۱. `enum class Route` در `AvaViewModel.kt` (همان خط ۱۳). ۲. شاخهٔ `when(s.route)` در `AvaApp.kt` (بدون آن کامپایل نمی‌شود، پس خودکار یادآوری می‌شود). ۳. نگاشت بازگشت در `AvaViewModel.back()` (والد صفحه). ۴. رشته‌ها در `design/strings.fa.json` با `source: proposed`، سپس `python3 scripts/sync-android-resources.py`. ۵. `NativeUiTest.pageGalleryRendersEveryDestination` همهٔ مقصدها را با یک `AvaState` پیش‌فرض رندر می‌کند، پس صفحهٔ تازه باید با همان state بدون خطا رندر شود. ۶. `MainActivity.kt` دکمهٔ بازگشت سیستم را برای مقصد‌های غیرریشه خودکار می‌گیرد. ۷. اندازه‌ها ۶۴/۷۲dp و `Secondary` پیش‌فرض ۶۴.

### قالب دستور کار برای برش UI

نام شاخه `phase-2b-<صفحه>`؛ فایل‌های مجاز: صفحهٔ تازه، یک ردیف در `AvaApp.kt`، `AvaViewModel.kt`/`AvaActions.kt` برای اقدام‌ها، رشته‌ها، یک تست؛ الگو: نزدیک‌ترین صفحهٔ موجود؛ منطق در لایهٔ خالص با تست JVM؛ بررسی: `jvm-core-check`، `check-docs.py`، `audit-project.py`، `sync --check`، و «بررسی نحو» همین سند؛ کامیت `feat(<صفحه>): <عنوان>`؛ ثبت در `WORKLOG` با برچسب `read in code` برای Compose.

## ۶. تاریخچهٔ کامیت‌ها نسبت به `main`

| کامیت | پیام |
|---|---|
| `bcc99f6` | feat(P0.2): add Claude entry, project state, skill, phasing and curriculum draft |
| `ccda15c` | fix(P0.3): drop three stale AI Studio entries from the design-reference audit |
| `8703dc0` | docs(P0.4): propose ADR-0004 bundled content and audio for version 1 |
| `a29931d` | docs(P0.5): log phase 0 in the harness worklog |
| `01c1359` | docs(P0.1): record delegated decisions DG-1..DG-6 and accept ADR-0004 |
| `c4ce79b` | chore(P0.4): bundle content and audio in version 1 (ADR-0004) |
| `c2ea01d` | fix(P1.2a): pick the word-builder lesson by id in ModelTest |
| `4bb8deb` | chore(P1.2a): add a reproducible JVM-only check of the pure Kotlin layers |
| `6bef9ee` | docs(P2.1): add child-UX conformance matrix (12 gaps, read in code) |
| `8345ac3` | feat(P2.3-S2): offer 'skip this step' after two wrong answers (UX-G01) |
| `82d047d` | feat(P2.3-S1): 64 dp child touch targets and 72 dp main CTA (UX-G02..G05) |
| `94ca981` | feat(P3.1): generate the educator review pack from content and audio inventory |
| `946d139` | feat(I1,I2): Find, Bubble and Match activity kinds; content can now grow |
| `59bab90` | docs: completion plan (screens first, then content) and activity-engine update |
| `02ed739` | feat(A-D): pure progression logic for discoveries, path end and weekly flowers |
| `27c0095` | feat(C): persist active days for the weekly flowers |

## ۷. قدم بعدی

UI صفحه‌های A تا D (پاداش و کارت کشف، آلبوم، گل‌های هفته، پایان مسیر) در برش‌های کوچک، سپس F1 تا F3، سپس G1 تا G3 و در پایان زیرساخت I3 تا I9. نقطهٔ کامپایل (فاز ۱) با تصمیم مالک.
