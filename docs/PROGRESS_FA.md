# وضعیت واقعی AVA — سورس ۰٫۶٫۱

فاز ۰٫۶٫۱ اکنون علاوه بر برش واقعی محتوای سطح ۱، پروژهٔ مستقل Payload و هارنس ادامهٔ کار دارد. مسیر توسعه‌ای ۳ واحد، ۶ درس و ۱۷ فعالیت است. CMS شامل PostgreSQL، adapter خصوصی S3، پنج collection، نقش‌های شش‌گانه، قرنطینه و دو بازبینی صوت، audit و export قطعی است. مرز master/delivery fail-closed است و ۱۸ تست دامنه CMS موفق‌اند. با درخواست صریح مالک، مسیر ساخت APK فعال و اسکریپت یک‌مرحله‌ای build/verify افزوده شد؛ اما APK در محیط حاضر تولید نشد، زیرا Android SDK/Build Tools وجود ندارد و Gradle 8.11.1 نیز به علت `Network is unreachable` قابل دریافت نیست. امتیاز مهندسی عمداً افزایش نیافته است.

## خلاصهٔ قابل سنجش

- پیشرفت کل نقشهٔ ۱۰۰ امتیازی: **۴۵ از ۱۰۰ (۴۵٪)**.
- پیشرفت تا محدودهٔ پایان فاز ۰٫۶٫۱: **۴۵ از ۵۵ امتیاز (۸۲٪)**.
- خود فاز ۰٫۶٫۱: **۸ از ۱۰ امتیاز**.
- دو امتیاز باز این فاز میان سه گیت تقسیم می‌شوند: integration واقعی DB/object storage، تأیید متخصص آموزش/صوت و compile/QA Native.
- درصدها شواهد مهندسی‌اند؛ آمادگی انتشار یا اثربخشی آموزشی را نشان نمی‌دهند.

## چک‌لیست فازبندی

| فاز | وزن | بسته‌شده | وضعیت | کار باز اصلی |
|---|---:|---:|---|---|
| مبنا و معماری محتوا — ۰٫۴٫۲ | ۱۰ | ۶ | 🟡 | ابزار/دستگاه/تأیید آموزشی |
| قرارداد content v3 — ۰٫۵٫۰ | ۱۰ | ۹ | 🟡 | اجرای Native |
| runtime محتوای محلی — ۰٫۵٫۱ | ۱۰ | ۹ | 🟡 | persistence دیسک و اجرای Native |
| موتور فعالیت v3 — ۰٫۶٫۰ | ۱۵ | ۱۳ | 🟡 | compile و QA دستگاه |
| محتوای سطح ۱ + CMS spike — ۰٫۶٫۱ | ۱۰ | ۸ | 🟡 | integration DB/storage، review آموزشی/صوت و QA |
| CMS MVP و workflow صوت — ۰٫۷٫۰ | ۱۵ | ۰ | ⬜ | پنل، upload، quarantine، QC، audit و release آزمایشی |
| همگام‌سازی offline-first — ۰٫۷٫۱ | ۱۰ | ۰ | ⬜ | persistence، دانلود، verify، activation و rollback |
| غنی‌سازی، QA و انتشار — ۰٫۸٫۰+ | ۲۰ | ۰ | ⬜ | محتوای کامل، دستگاه، حقوقی، حساب و release |
| **جمع** | **۱۰۰** | **۴۵** | **۴۵٪** | APK/AAB عمداً ساخته نشده است |

## چک‌لیست انجام‌شده در ۰٫۶٫۱

- [x] نسخه اپ ۰٫۶٫۱ و versionCode 16.
- [x] ترتیب سه‌مرحله‌ای سطح ۱: نشانه‌ها → صداهای واژه → واژه‌های نخست.
- [x] دو درس تازه: «اول و آخر واژه» و «صداها کنار هم».
- [x] پنج فعالیت تازه: دو InitialSound، یک FinalSound و دو Blend.
- [x] تغییر معنایی دو فعالیت نقطه از Recognize به DotFamily بدون تغییر ID.
- [x] افزایش دامنه از ۴ درس/۱۲ فعالیت به ۶ درس/۱۷ فعالیت؛ ۲ قصه/۹ صفحه ثابت.
- [x] اصلاح صوت فعالیت واجی: `writtenFallback` واژهٔ محرک است و target پاسخ.
- [x] TTS/hint برای «بابا»، «مادر» و «آب» به‌جای لو‌دادن پاسخ حرف.
- [x] سه نیاز صوت تازه؛ inventory اکنون ۲۲ asset و ۱۲ prompt فعالیت دارد.
- [x] migration قطعی v2→canonical ۰٫۶٫۱ و حفظ IDهای قدیمی.
- [x] strict JSON loader و رد کلید تکراری در قراردادهای release.
- [x] قرارداد `cms-export-v1` مستقل از Strapi/Payload.
- [x] شش نقش و پنج separation rule برای جلوگیری از self-approval.
- [x] fixture خروجی Payload و validator تطبیق محتوا/صوت.
- [x] چهار blocker واقعی publication: محتوای تأییدنشده، صوت تأییدنشده، export جزئی و candidate ناآماده.
- [x] مقایسه منابع رسمی و انتخاب مشروط Payload برای prototype؛ انتخاب production هنوز مشروط است.
- [x] پروژهٔ مستقل Payload 3.92.1 با نسخه‌های دقیق، lockfile، PostgreSQL و adapter رسمی S3.
- [x] پنج collection واقعی: Users، CurriculumItems، AudioAssets، Releases و AuditEvents.
- [x] bootstrap امن مدیر نخست، کنترل دسترسی شش‌نقشی و فیلدهای server-managed غیرقابل‌دستکاری از API.
- [x] upload صوت با allowlist، سقف ۲۵ MiB، تشخیص magic WAV/MP3/M4A و SHA-256 در `beforeValidate`.
- [x] workflow قرنطینه → review فنی → review آموزشی → تأیید، با ممنوعیت self-review و reviewer تکراری.
- [x] release gate، audit append-only در سطح API و export قطعی بر پایه stable ID.
- [x] موفقیت ۱۸ تست CMS، type-check، تولید payload-types و production build با routeهای Admin/REST/GraphQL.
- [x] بستن شکاف master/delivery: inspection پیش از review و delivery آماده/hash معتبر پیش از release.
- [x] export صوت فقط از delivery metadata؛ filename/hash فایل master قرنطینه وارد خروجی اپ نمی‌شود.
- [x] افزایش تست‌های دامنه CMS از ۱۶ به ۱۸ برای inspection و delivery gate.
- [x] هارنس tool-neutral برای Zcode/Codex/Claude: ۱۴ task، ۴ فاز، dependency بدون چرخه و commandهای allowlisted.
- [x] چهار playbook تفصیلی برای integration، CMS/audio، offline sync و quality/release.
- [x] human gate غیرقابل‌خودکارسازی برای review آموزشی، حقوق صوت و QA دستگاه.
- [x] مرتب‌سازی تحویل نهایی: مسیرهای canonical، ignore cache/build/keystore و فیلتر ZIP سورس.
- [x] اسکریپت رسمی `build-android-apk.py` برای sync → audit → test → lint → assembleDebug → integrity/hash.
- [x] تلاش واقعی build در محیط فعلی: JDK 17 موجود است.
- [ ] تولید APK: Android SDK/Build Tools نصب نیست و Gradle 8.11.1 از شبکه دریافت نمی‌شود.
- [x] اضافه‌شدن تست UI برای fallback/hint واژهٔ محرک؛ مجموع تست‌سورس ۱۰۴.
- [x] audit محتوا، migration، release hash، صوت، CMS، bundle deterministic و tamper rejection موفق.
- [ ] اجرای integration واقعی server با PostgreSQL/MinIO؛ Docker در محیط حاضر نصب نیست.
- [ ] اسکن malware، decode واقعی، transcode و delivery job صوت (فاز ۰٫۷٫۰).
- [ ] تأیید ترتیب، distractorها، متن و تلفظ توسط متخصص آموزش فارسی.
- [ ] compile/اجرای ۱۰۴ تست و QA emulator/گوشی.

## دلیل ترتیب آموزشی

شناخت دیداری و تفاوت نقطه قبل از آگاهی صدای آغاز/پایان می‌آید؛ سپس دو فعالیت کوتاه کنارهم‌گذاری نشانه، کودک را برای ساخت واژه آماده می‌کند. `Segment` و `PositionalForm` عمداً فعال نشده‌اند: interaction فعلی برای segmentation و محرک جایگاه حرف هنوز از نظر آموزشی کافی نیست. جزئیات در `LEVEL1_SEQUENCE_FA.md` ثبت است.

## وضعیت CMS و صوت

Payload برای prototype از حالت ADR خارج و به سورس اجرایی تبدیل شده است: نسخهٔ پایدار 3.92.1، PostgreSQL، S3-compatible خصوصی، access/hook/version، پنج collection و ۱۸ تست دامنه. production build بدون DB موفق است، اما خود server و مسیر upload در برابر PostgreSQL/MinIO اجرا نشده‌اند؛ بنابراین تصمیم production هنوز مشروط است. تشخیص magic/hash پیاده شده ولی antivirus، decode و transcode عمداً به‌عنوان گیت واقعی باقی مانده‌اند.

اپ Android هیچ وابستگی، URL یا query مستقیم به CMS ندارد. ادمین آینده asset را مدیریت می‌کند؛ compiler فقط export کامل و تأییدشده را به ContentRelease تبدیل می‌کند. TTS دستگاه همچنان fallback توسعه است.

## شواهد همین سورس

| بررسی | نتیجه |
|---|---|
| resource sync | ۱۶ تصویر، ۲۶۳ متن، ۳ واحد، ۶ درس، ۱۷ فعالیت، ۲ قصه |
| content migration/contract | موفق؛ v2→v3 قطعی و hash release معتبر |
| audio inventory | موفق؛ ۲۲ asset، ۱۲ prompt و ۹ صفحه قصه |
| CMS contract | موفق؛ ۶ نقش، ۳ رکورد spike و ۴ blocker publication |
| Payload prototype | type-check و ۱۸/۱۸ تست موفق؛ type generation/build قبلی موفق؛ ۵ collection و ۶ نقش |
| coding-agent harness | معتبر؛ ۱۴ task در ۴ فاز، dependency بدون چرخه، scope و gate امن |
| DB/object storage | compose آماده است؛ اجرا نشد چون Docker در محیط نصب نیست |
| bundle/tamper | موفق و deterministic |
| JSON/Python | ۷۶ JSON strict و Python compile موفق |
| تست‌های Native/Compose در سورس | ۱۴۰ |
| اجرای Gradle/APK | JDK 17 و Android SDK 35 و Gradle 8.11.1 در سندباکس نصب‌اند؛ اجرای Gradle در سندباکس ممکن نیست — پس از Ask شدن `other_tcp` پیام مسدودیت رفت ولی daemon جاوا همان اول می‌میرد (مسیر دادهٔ loopback سیاه‌چاله است)؛ مالک گیت‌ها را روی Android Studio ماشین خودش اجرا می‌کند؛ هیچ تستی هنوز اجرا نشد |
| permission اینترنت/میکروفن | وجود ندارد |
| APK/AAB | ساخته نشد |

## گیت‌های باز و قدم بعد

1. اجرای Compose روی محیط دارای Docker و اثبات migration، bootstrap مدیر، upload خصوصی و signed download.
2. افزودن malware scan، decode با ابزار واقعی، transcode و job ساخت delivery.
3. اثبات access control، دو review، audit و export deterministic در تست integration متصل به DB/S3.
4. طراحی store دیسکی content bundle و process-death recovery پیش از remote sync.
5. اجرای Native tests/lint و رندر typeهای تازه در toolchain کامل.

قدم بعدی ثبت‌شده: اجرای `python3 scripts/build-android-apk.py --output-dir <PATH>` در محیط دارای JDK 17، Android SDK 35/Build Tools و Gradle 8.11.1؛ سپس ثبت hash APK و QA نصب. در هارنس، `061-db-s3-integration` نیز در محیط دارای Docker باز است.

## بروزرسانی ۲۰۲۶-۱۱-۰۳ — برش‌های شاخه‌های Claude (بدون اثبات Native)

این بخش تغییرهای پس از ۰٫۶٫۱ را جدا از شواهد بالا نشان می‌دهد. هیچ گیت باز بسته نشد و درصد‌ها تغییر نکردند.

- تست‌های سورس اکنون ۱۱۶ است (۱۰۴ + ۱۲ تازه)؛ رشته‌های تولیدی ۲۶۴ (+ `app.skip_step`).
- اجرا‌شده (`ran`): `scripts/jvm-core-check.py` روی `content` و `data`: ۳۸ تست پاس؛ همهٔ validatorها؛ `audit-project.py` پاس (پس از حذف سه مرجع کهنهٔ `.aistudio` از ممیزی).
- تغییرهای UI/ViewModel/DataStore (اندازهٔ لمس ۶۴/۷۲dp، رد کردن پس از دو اشتباه، `activeDays`) فقط `read in code` هستند و کامپایل نشده‌اند.
- گیت‌های باز بدون تغییر: Gradle/lint، تأیید متخصص آموزش، صوت انسانی، DB/S3، QA دستگاه.

## بروزرسانی ۲۰۲۶-۱۱-۰۵ — toolchain و رفع P0 در شاخهٔ `phase-1-native-toolchain` (بدون اجرای Gradle)

این بخش کار تسک `061-native-toolchain` را نشان می‌دهد. هیچ گیتی بسته نشد و امتیاز ۴۵ عمداً تغییر نکرد.

- محیط toolchain در سندباکس آماده شد: OpenJDK 17.0.20.1 (apt)، Android SDK 35 (پلتفرم ext15، build-tools 35.0.0، platform-tools — با curl گرفته شد چون `sdkmanager` با جاوا به شبکه نمی‌رسد)، Gradle 8.11.1 (توزیع با SHA-256 تاییدشده).
- اجرای Gradle در این سندباکس ممکن نیست: سوکت‌های JVM بسته‌اند (`other_tcp` روی Deny) و با جاوای ساده بازتولید شد — دانلود wrapper، IPC حلقه‌به‌حلقهٔ daemon و دانلود وابستگی‌ها هر سه می‌میرند. رفع آن دست کاربر است: Muse settings ← Permissions ← Direct network protocols ← `other_tcp` روی Ask. تا آنجا `testDebugUnitTest` و `lintDebug` اجرا نشده‌اند.
- رفع P0 (فقط `read in code`، کامپایل نشده): در `AvaViewModel` بارگذاری محتوا به `loadContent()` جدا شد و شکست آن بنر تلاش دوباره (`storageError`، همان بنری که در Splash هم دیده می‌شود) را نشان می‌دهد؛ `retryStorage()` حالا محتوای ناموفق را هم دوباره بار می‌کند. دو تست رگرسیون تازه در `AvaViewModelTest` (همیشه‌خراب و یک‌بار‌خراب) این رفتار را می‌سنجند؛ مجموع تست‌های سورس اکنون ۱۲۲ است.
- رشتهٔ نسخه درست شد: `design/strings.fa.json` (`version`) از «۰٫۴٫۱» به «۰٫۶٫۱» و `strings.xml` با sync بازتولید شد؛ `sync --check` پاس شد.
- `android/gradlew` در ریپو مجوز اجرا ندارد (mode 644)؛ تغییر mode با API گیت‌هاب قابل push نیست و موقع مرج با یک دستور `chmod +x android/gradlew` انجام می‌شود.
- اجرا‌شده (`ran`): `audit-project.py`، `validate-harness.py`، `check-docs.py` (پس از اصلاح شمار تست در `PROJECT_STATE.md` به ۱۲۲)، `validate-content-contract.py`، `sync --check` — همه پاس.
- گیت‌های باز بدون تغییر: Gradle/lint (مسدود به تنظیم کاربر)، تأیید متخصص آموزش، صوت انسانی، DB/S3، QA دستگاه.

## بروزرسانی ۲۰۲۶-۱۱-۰۵ — تکمیل v1-3 و اسکریپت I7 در شاخهٔ `phase-1-native-toolchain` (بدون اجرای Gradle)

این بخش بقیهٔ موارد گام v1-3 و اسکریپت اعتبارسنجی تحویل صوت (I7) را نشان می‌دهد. هیچ گیتی بسته نشد و امتیاز ۴۵ عمداً تغییر نکرد.

- مورد ۳ (برگردانده شد — تصمیم مالک ۲۰۲۶-۱۱-۰۵): کد آزمایشی در حالت دولوپ می‌ماند — راهنمای `mock_hint` در صفحه‌های Phone و OTP و رشته‌اش برگردانده شد؛ `strings.xml` و `TextIds.kt` با sync بازتولید شدند (رشته‌ها دوباره ۲۹۵). حذف `mock_hint` و کد `123456` از مسیر انتشار به گام v1-7 موکول شد، وقتی سرویس واقعی می‌آید.
- مورد ۴: `SessionSnapshot` نسخه ۲ شد — `wrongCount` و وضعیت Match (`matched` و `mismatch`) هم در snapshot ذخیره می‌شوند؛ decode نسخهٔ ۱ را هم هنوز می‌خواند. `restore()` وضعیت Match را اعتبارسنجی می‌کند و یک Match کامل را checked/correct برمی‌گرداند.
- مورد ۵: guard برای Match غیرقابل‌بازی — وقتی توکن‌های PairMatch کمتر از دو تا باشند، `canSkip` همیشه true است تا کاربر روی صفحهٔ خالی گیر نکند؛ دکمهٔ رد کردن (که از قبل هست) راه خروج است.
- مورد ۶: کاهش حرکت زنده خوانده می‌شود — `AvaApp` تغییر `ANIMATOR_DURATION_SCALE` را با ContentObserver دنبال می‌کند (نه یک‌بار با remember) و مقیاس ۰٫۵× هم حرکتِ کاهش‌یافته حساب می‌شود.
- مورد ۷: آستانه‌های compact یکدست شدند — `LayoutThresholds` در `AvaTheme` با `compactWidth=360.dp` و `compactHeight=640.dp`؛ دو استثنا (۶۵۰ در Auth و ۳۴۰ در Story) اصلاح شدند. assertionهای ۴۸dp در `NativeUiTest` به ۶۴dp رسیدند (هدف لمسی ۶۴dp که در خود کامپوننت‌ها بود).
- مورد ۸: `LearnerRepository.decode` حالا با named arguments ساخته می‌شود و اندازهٔ حباب‌ها در `BubbleTask` واکنشی است — در عرض ۳۲۰ سه حباب ۹۶dp دیگر تمام عرض را پر نمی‌کنند و حداقل ۱۶dp فاصله می‌ماند (کف ۶۴dp، سقف ۹۶dp).
- تست‌های خروجی: دو تست تازه در `ActivityEngineTest` (round-trip وضعیت Match و wrongCount + سازگاری v1، و رد کردن Match خالی + کامل‌شدن توکن فرد)، `MainActivityTest` (smoke test سیم‌کشی onCreate) و `AndroidNarratorTest` (play بدون صدای فارسی، Missing می‌دهد؛ همهٔ فراخوانی‌ها پس از close امن‌اند). مجموع تست‌های سورس اکنون ۱۲۷ است.
- I7: `scripts/validate-audio-delivery.py` نوشته شد — تحویل صوت مسیر B را با inventory می‌سنجد: وجود فایل، فرمت (WAV با magic bytes و هدر، MP3 با پیمایش فریم)، مدت (۰٫۲ تا ۳۰ ثانیه) و تطابق sha256؛ حالت `--record` هش و codec و مدت را در inventory ثبت می‌کند. با فیکسچرهای WAV/MP3 ساخته‌شده تست شد (OK/FAIL درست). هنوز تحویلی نداریم: ۷ مورد لازم، ۰ تحویل‌شده. اسکریپت به‌عنوان گیت دوم تسک `080-human-audio-rights` در `harness/roadmap.json` اضافه شد و I7 در `docs/COMPLETION_PLAN_FA.md` تیک خورد.
- اجرا‌شده (`ran`): `audit-project.py`، `validate-harness.py`، `check-docs.py` (پس از اصلاح شمار تست به ۱۲۷ و رشته‌ها به ۲۹۴ در `PROJECT_STATE.md` و stage کردن فایل‌های تازه)، `validate-content-contract.py`، `sync --check`، `validate-audio-delivery.py` (بدون تحویل: exit 1 درست؛ با فیکسچر: رفتار OK/FAIL درست) — همه پاس، جز `validate-audio-delivery.py` بدون تحویل که طبق طراحی fail می‌کند.
- گیت‌های باز بدون تغییر: Gradle/lint (مسدود به تنظیم کاربر — تست جاوا ۱۸:۲۷ دوباره مسدود بودن را نشان داد)، تأیید متخصص آموزش، صوت انسانی (تحویل ۷ prompt)، DB/S3، QA دستگاه.

## بروزرسانی ۲۰۲۶-۱۱-۰۵ — I8: fixture همهٔ نوع‌ها برای تست UI (بدون اجرای Gradle)

- I8 (`AllKindsFixture` و `AllKindsUiTest` در `android/app/src/test`) ساخته شد: یک درس مصنوعی با یک task از هر ۱۲ نوع فعالیت، فقط برای تست — هیچ ورودی به محتوای منتشرشده ندارد. تست هر نوع را از مسیر واقعی صفحهٔ درس رندر می‌کند و متن هدف (یا اولین توکن در Match) و بودن/نبودن دکمهٔ «بررسی کن» را می‌سنجد. این تنها جایی است که rendererهای Find و Bubble و Match تمرین می‌شوند، چون کاتالوگ منتشرشده هنوز taskای از این نوع‌ها ندارد. PositionalForm و Segment هم در fixture هستند، هرچند در محتوا عمداً غیرفعال‌اند.
- I6 انجام نشد — اصولی: ابزار hash نسخه زیر گام v1-8 و تسک `080-release-readiness` است که هنوز blocked است (پیش‌نیاز: گام‌های v1-2 تا v1-7)؛ قرارداد AGENTS.md شروع کار blocked را ممنوع می‌کند. پس از بسته‌شدن گام‌های میانی انجام می‌شود.
- مجموع تست‌های سورس اکنون ۱۲۹ است. تغییرها فقط `read in code` هستند و کامپایل نشده‌اند (Gradle همچنان مسدود به `other_tcp`).
- اجرا‌شده (`ran`): `audit-project.py`، `validate-harness.py`، `check-docs.py` (۰ مشکل پس از اصلاح شمار تست به ۱۲۹)، `validate-content-contract.py`، `sync --check` — همه پاس.

## بروزرسانی ۲۰۲۶-۱۱-۰۵ — بازبینی کد و بهبودها پیش از اجرای مالک روی Android Studio (بدون اجرای Gradle)

- بازبینی کامل تغییرهای PR شماره ۲ (خواندن diff و ردیابی منطق) چند مورد واقعی پیدا کرد و همه اصلاح شدند:
  - **خطای کامپایل P0:** در `LessonScreens.kt` پارامتر `size: Dp` بدون import کلاس `Dp` بود — `import androidx.compose.ui.unit.Dp` اضافه شد.
  - **تست غلط AllKinds:** `onNodeWithText(target)` برای ۶ نوع از ۱۲ نوع درست نبود — برای Recognize/DotFamily/PositionalForm/Find هدف دو بار رندر می‌شود (TargetCard + کاشی انتخاب) و برای InitialSound/FinalSound هدف پشت hint پنهان است؛ حالا هر نوع با node درست خودش سنجیده می‌شود.
  - **Fixture غیرقابل‌برد:** هدف InitialSound/FinalSound واژه («بابا»/«آب») بود در حالی که انتخاب‌ها حرف‌اند و `check` هیچ‌وقت درست نمی‌شد؛ هدف‌ها «ب» شدند و واژه‌ها در `writtenFallback` ماندند (همان قرارداد محتوای واقعی).
  - **سوراخ validation:** `validationError` درس با صفر فعالیت را قبول می‌کرد و `startLesson` بعد با `coerceIn(0, -1)` کرش می‌کرد؛ حالا `lesson_tasks_empty` رد می‌شود و یک تست در `ContentRepositoryTest` دارد.
  - **کامنت حباب:** عدد «۱۶dp فاصله» با ریاضی فرمول نمی‌خواند (در ۳۲۰dp فاصله‌ها حدود ۸dp‌ا‌ند)؛ کامنت با محاسبهٔ درست بازنویسی شد.
- تست‌های تازه (همه فقط `read in code`، کامپایل و اجرا با مالک روی Android Studio):
  - `everyKindAnswersCorrectly` در `AllKindsUiTest`: هر ۱۲ نوع با actionهای واقعی پاسخ داده می‌شوند (انتخاب، توکن‌ها، جفت‌های Match) تا «پیدایش کردی!».
  - `JourneyUiTest` (۴ تست): ورود کاربر تازه با کد آزمایشی، تکمیل درس اول، خواندن قصهٔ اول، و درس → مرور → شروع دوباره (reset) — با `AvaViewModel` واقعی و `AvaApp` واقعی.
  - `WidthsUiTest` (۲ تست): رندر Home/درس/حباب در عرض‌های ۳۶۰dp (مرز compact) و ۴۱۲dp.
- مجموع تست‌های سورس اکنون ۱۳۷ است. I6 همچنان انجام نشد (همان دلیل بالا).
- اجرا‌شده (`ran`): `audit-project.py`، `validate-harness.py`، `check-docs.py` (۰ مشکل پس از اصلاح شمار تست به ۱۳۷)، `validate-content-contract.py`، `sync --check` — همه پاس.

## بروزرسانی ۲۰۲۶-۱۱-۰۵ — دور دوم بازبینی عمیق و اصلاح‌ها پیش از اجرای مالک روی Android Studio (بدون اجرای Gradle)

- دور دوم بازبینی عمیق (خواندن کد واقعی، نه خلاصه‌ها) روی شش حوزه — منطق session/ذخیره‌سازی در `AvaViewModel`، تست‌های تازه، مسیرهای صوت، قرارداد محتوا، دسترس‌پذیری/RTL، و تطابق مستندات با کد — یک مورد P0 و سه مورد P1 و ده مورد P2 پیدا کرد. موارد زیر اصلاح شدند:
  - **مسابقهٔ ورود P1-1:** در `verify()` ذخیرهٔ `signedIn=true` بعد از مکث ۶۰۰ میلی‌ثانیه‌ای پیام موفقیت بود؛ برگشت در آن پنجره job را کنسل می‌کرد و ورود بی‌صدا از دست می‌رفت. حالا ذخیره قبل از مکث انجام می‌شود و یک تست (`backDuringSuccessDelayDoesNotLoseLogin`) می‌سنجد که برگشت در آن پنجره ورود را از بین نمی‌برد. رفتار کنسل‌شدن درخواست هنگام برگشت (تست موجود) دست نخورده ماند.
  - **جعبه‌های OTP برعکس P1-2:** جعبه‌های ۶ رقم در چیدمان RTL اجباری اپ برعکس رندر می‌شدند (رقم اول سمت راست). حالا Row جعبه‌ها با `LocalLayoutDirection provides LayoutDirection.Ltr` پوشیده شده — همان قراردادی که فیلد شماره با `textDirection=Ltr` دارد. تست خودکار ندارد (ارقام از semantics پاک می‌شوند)؛ بازبینی بصری روی دستگاه.
  - **جای دکمه‌های دیالوگ P1-3:** در `AvaApp` دکمهٔ «فعلاً نه» در اسلات confirm (کنش اصلی) بود و «بله، ادامه بده» در اسلات dismiss؛ در RTL دکمهٔ اصلی سمت چپ «فعلاً نه» بود و کلیک اشتباه می‌توانست پیشرفت کودک را پاک کند. اسلات‌ها جابه‌جا شدند (confirm = «بله» پررنگ ۷۲dp، dismiss = «فعلاً نه» خط‌دار ۶۴dp) و یک تست موقعیت (`resetDialogPutsConfirmActionInPrimarySlot`) می‌سنجد که «بله» سمت چپ «فعلاً نه» است.
  - **قصهٔ بدون صفحه P2-6:** `startStory` با صفر صفحه کرش می‌کرد (`coerceIn(0, -1)`)، هم‌خانوادهٔ `lesson_tasks_empty`؛ نه `validationError` و نه اسکریپت قرارداد آن را نمی‌سنجیدند. حالا `story_pages_empty` رد می‌شود، اسکریپت `validate-content-contract.py` صفحه‌ها را non-empty می‌خواهد، و یک تست در `ContentRepositoryTest` دارد.
  - **Drift مستندات P2-9:** در `NATIVE_VERIFICATION.json` جدول `sourceSha256` بازسازی شد (۷ ورودی stale به‌روز و ۲۶ فایل تغییرکردهٔ PR شماره ۲ اضافه — اکنون ۷۸ ورودی و پوشش کامل ۳۴ فایل تغییرکردهٔ شاخه)، گیت «execute 104 tests» به ۱۴۰ اصلاح شد، `nextIncrement` از `build-android-apk.py` به اجرای گیت‌ها روی Android Studio مالک تغییر کرد (بدون APK)، و جملهٔ گمراه‌کنندهٔ «mock_hint removed» در `latestResult` با ارجاع به اصلاحیهٔ همان روز بازنویسی شد.
  - **WidthsUiTest ‏P2-10:** کوالیفایر مردهٔ سطح کلاس (`w390dp`) حذف شد؛ فقط `sdk=[35]` در سطح کلاس ماند و عرض‌ها روی متدها هستند.
- بررسی شد و نادرست/غیرقابل‌دسترس تشخیص داده شد (بدون تغییر): ناهم‌ترازی `reviewKeys` با `tasks` (P2-2) — `plannedReviewKeys` فقط کلید taskهای موجود را تولید می‌کند، پس `tasks` کوتاه‌تر از `reviewKeys` نمی‌شود.
- **P0 باز، نیازمند تصمیم مالک — مسیر پخش صوت انسانی وجود ندارد:** `AndroidNarrator.play()` فقط متن fallback را با TTS می‌خواند و فیلد `ref` را هیچ‌وقت استفاده نمی‌کند؛ در کل `android/app/src/main` صفر مورد `MediaPlayer`/`SoundPool`/`ExoPlayer`/`AudioTrack` هست و `PlayedLocal` هیچ تولیدکننده‌ای ندارد. یعنی وقتی ۷ پرامپت انسانی v1-5 تحویل داده شوند، هیچ کدی برای پخش آن‌ها نیست؛ ولیدیتور I7 فایل‌ها را روی دیسک اعتبارسنجی می‌کند ولی اپ نمی‌تواند آن‌ها را بخواند. ساخت پخش‌کننده کار ماشینی در `android/**` است ولی طراحی می‌خواهد (کجا فایل‌ها در اپ بسته‌بندی شوند، کدام API پخش، و چگونه تحویل به بسته‌بندی برسد) و با ۰ فایل صوتی فعلی قابل اثبات با صوت واقعی نیست — پیش از پیاده‌سازی، تصمیم مالک لازم است.
- موارد P2 دیگر که اصلاح نشدند و در گزارش بازبینی دوم ثبت‌اند: قفل/تایمر OTP فقط در state است و با مرگ پروسه پاک می‌شود (با mock بی‌ضرر؛ با SMS واقعی در v1-7)؛ مسیر staging/فعال‌سازی محتوا در production صدا زده نمی‌شود (مطابق Track B برای v1)؛ `offline` در production کد مرده است (فقط intent آزمایشی QA؛ تشخیص آفلاین واقعی با v1-7)؛ اعتبارسنجی زمان اجرا ضعیف‌تر از قرارداد نویسندگی است و اسکیمای JSON هیچ‌وقت اجرا نمی‌شود؛ قاعدهٔ «۳ تا ۶ فعالیت در هر درس» هیچ‌جا اجرا نمی‌شود (`blend_words_04` فقط ۲ فعالیت دارد — افزودن فعالیت سوم فقط پس از تأیید متخصص)؛ `feedback.correct` در inventory یتیم است (هیچ مصرف‌کننده‌ای ندارد؛ `celebrate()` با ToneGenerator کار می‌کند).
- مجموع تست‌های سورس اکنون ۱۴۰ است. I6 همچنان انجام نشد (همان دلیل بالا).
- اجرا‌شده (`ran`): `audit-project.py`، `validate-harness.py`، `check-docs.py` (۰ مشکل پس از اصلاح شمار تست به ۱۴۰)، `validate-content-contract.py`، `sync --check` — همه پاس.
