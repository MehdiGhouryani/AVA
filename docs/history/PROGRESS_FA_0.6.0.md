# وضعیت واقعی AVA — سورس ۰٫۶٫۰

این برش «موتور فعالیت v3» را در سطح سورس پیاده می‌کند. مدل از ۳ نوع به ۹ نوع فعالیت قابل توسعه رسیده، renderer و reducer از منبع CMS/URL مستقل‌اند، نشست نیمه‌تمام snapshot می‌شود و مسیر پخش صوت قرارداد تزریق‌پذیر دارد. محتوای فعلی همچنان همان ۴ درس توسعه‌ای/۱۲ فعالیت و ۲ قصه/۹ صفحه است؛ نوع‌های تازه هنوز برنامهٔ درسی تأییدشده نیستند. هیچ CMS اجرایی، شبکه، APK یا AAB اضافه نشده است.

## خلاصهٔ قابل سنجش

- پیشرفت کل نقشهٔ اجرایی ۱۰۰ امتیازی: **۳۷ از ۱۰۰ (حدود ۳۷٪)**.
- پیشرفت در محدودهٔ فازهایی که تا ۰٫۶٫۰ باید طی می‌شد: **۳۷ از ۴۵ امتیاز (حدود ۸۲٪)**.
- امتیاز خود برش ۰٫۶٫۰: **۱۳ از ۱۵**؛ دو امتیاز باز، compile/اجرای واقعی ۱۰۳ تست و رندر/QA دستگاه است.
- این درصد «شواهد مهندسی بسته‌شده» است، نه درصد آمادگی انتشار یا اثربخشی آموزشی.

## چک‌لیست فازبندی

| فاز | وزن | بسته‌شده | وضعیت | شاهد/کار باز |
|---|---:|---:|---|---|
| مبنا و معماری محتوا — ۰٫۴٫۲ | ۱۰ | ۶ | 🟡 | ADR، inventory و audit بسته؛ compile/device/تأیید آموزشی باز |
| قرارداد content v3 — ۰٫۵٫۰ | ۱۰ | ۹ | 🟡 | schema/migration/release/TaskProgress بسته؛ اجرای Native باز |
| runtime محتوای محلی — ۰٫۵٫۱ | ۱۰ | ۹ | 🟡 | activation/rollback/bundle بسته؛ persistence دیسک و اجرای Native باز |
| موتور فعالیت v3 — ۰٫۶٫۰ | ۱۵ | ۱۳ | 🟡 | registry/reducer/session/audio seam/renderer و تست‌سورس بسته؛ اجرای واقعی و QA دستگاه باز |
| محتوای سطح ۱ + prototype CMS — ۰٫۶٫۱ | ۱۰ | ۰ | ⬜ | شروع نشده؛ انتخاب فناوری فقط بعد از prototype و ADR |
| CMS MVP و workflow صوت — ۰٫۷٫۰ | ۱۵ | ۰ | ⬜ | نقش‌ها، Media Library، quarantine، QC، audit، preview و release آزمایشی |
| همگام‌سازی offline-first — ۰٫۷٫۱ | ۱۰ | ۰ | ⬜ | دانلود، verify، نوشتن اتمیک دیسک، recovery و rollback واقعی |
| غنی‌سازی، QA و آمادگی انتشار — ۰٫۸٫۰+ | ۲۰ | ۰ | ⬜ | محتوای تأییدشده، دستگاه، دسترس‌پذیری، عملکرد، حقوقی، حساب و release |
| **جمع** | **۱۰۰** | **۳۷** | **۳۷٪** | APK/AAB عمداً ساخته نشده است |

## چک‌لیست برش ۰٫۶٫۰

- [x] `TaskKind` برای ۹ نوع: Recognize، Listen، BuildWord، DotFamily، PositionalForm، InitialSound، FinalSound، Segment و Blend.
- [x] `ActivityRegistry` به‌عنوان مرجع واحد خانواده، interaction، hint و قابلیت صوت.
- [x] reducer مشترک برای انتخاب، tokenهای تکراری، undo، hint، check، correction، advance و complete.
- [x] `SessionSnapshot` نسخه‌دار با encode/decode امن، clamp اندیس و حذف انتخاب stale/نامعتبر.
- [x] اتصال snapshot به `SavedStateHandle` برای درس و مرور و پاک‌سازی پس از تکمیل/reset.
- [x] اصلاح resume: `activeIndex` از snapshot بازیابی‌شده ذخیره می‌شود.
- [x] renderer خانواده‌محور؛ UI به URL، CMS یا نام فایل صوت وابسته نیست.
- [x] planner مرور بر اساس `ActivityFamily` تنوع می‌سازد تا نوع‌های تازه بدون شکنندگی وارد شوند.
- [x] `AudioRequest` با `AudioRef` پایدار، fallback متنی و purpose؛ `AudioPlayback` تزریق‌پذیر و Fake قابل آزمون.
- [x] مسیر prompt و صفحه قصه به درخواست صوت ساختاریافته متصل است؛ TTS آفلاین فقط fallback توسعه باقی مانده است.
- [x] schema و validator/generator برای ۹ نوع همگام شدند.
- [x] تست‌سورس registry، reducer، snapshot، صوت، recreation ViewModel و renderer نوع تازه افزوده شد.
- [x] audit محتوا، migration، release hash، inventory صوت، bundle deterministic، tamper rejection، Python و ۶۸ JSON موفق.
- [ ] compile و اجرای ۱۰۳ تست Native/Compose در toolchain کامل.
- [ ] رندر و QA گوشی برای typeهای تازه، process recreation، TalkBack، متن بزرگ و TTS.

## نتیجهٔ بررسی همین سورس

| بررسی | نتیجه |
|---|---|
| resource/content/audio/project audit | موفق |
| migration v2→v3 و release hashes | موفق |
| bundle deterministic و tamper rejection | موفق |
| JSON/Python | ۶۸ JSON معتبر؛ Python compile موفق |
| تعداد تست‌های Native/Compose در سورس | ۱۰۳ |
| دسترسی مستقیم screenهای production به `Catalog` | صفر |
| permission اینترنت/میکروفن | وجود ندارد |
| اجرای Gradle/Native | مسدود؛ Gradle 8.11.1/Android SDK در محیط حاضر آماده نیست |
| APK/AAB | ساخته نشد |

## زیرساخت صوت و CMS؛ وضعیت دقیق

اکنون UI فقط `AudioRef` پایدار و متن fallback را می‌شناسد. این همان seam لازم برای آینده است: ادمین در CMS فایل و metadata/QC را به همان ref متصل می‌کند؛ compiler فقط asset تأییدشده را داخل `ContentRelease` تغییرناپذیر می‌گذارد؛ اپ release را verify و محلی فعال می‌کند. اپ هیچ query مستقیم به CMS نمی‌زند و نبود فایل صوتی، فعالیت را قفل نمی‌کند.

هنوز decoder/پخش فایل انسانیِ داخل bundle، storage دیسکی process-persistent، upload وب، نقش‌ها و workflow approval پیاده نشده‌اند. بنابراین «آمادهٔ اتصال اصولی» هستیم، نه «CMS یا صدای نهایی آماده است».

## گیت‌های باز قبل از فاز ۰٫۶٫۱

1. toolchain کامل: Gradle 8.11.1، JDK 17، Android SDK 35 و dependency cache؛ اجرای ۱۰۳ تست و lint.
2. اجرای renderer typeهای تازه روی emulator/گوشی و ثبت شواهد اندازه، متن بزرگ، RTL، TalkBack و lifecycle صوت.
3. طراحی persistence دیسکی bundle با atomic write و process-death recovery؛ پیش‌نیاز remote sync است.
4. تعیین scope محتوای سطح ۱ با متخصص آموزش فارسی؛ نوع‌های تازه صرفاً ظرفیت فنی‌اند.

## قدم بعد ثبت‌شده

پس از بستن گیت toolchain، برش `0.6.1` با دو track محدود آغاز می‌شود: (الف) نمونهٔ کوچک و بازبینی‌پذیر محتوای سطح ۱ روی همین ۹ نوع، (ب) prototype مقایسه‌ای CMS برای Strapi/Payload و ثبت ADR انتخاب. هیچ اتصال مستقیم UI به CMS و هیچ افزایش انبوه درس پیش از تأیید آموزشی انجام نمی‌شود.
