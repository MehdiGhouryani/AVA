# وضعیت واقعی AVA — سورس ۰٫۵٫۰

این برش قراردادهای محتوای قابل‌انتشار و پیشرفت task را پیاده می‌کند. UI فعلی، دامنه ۴ درس/۱۲ فعالیت و ۲ قصه/۹ صفحه حفظ شده‌اند؛ محتوا همچنان `approvedForRelease=false` است. هیچ CMS اجرایی، شبکه، backend، APK یا AAB اضافه نشده است.

## وضعیت فاز

| بخش قرارداد ۰٫۵٫۰ | وضعیت | شاهد |
|---|---|---|
| content schema v3 | انجام‌شده | Level/Unit/Lesson/Task، Skill و story page پایدار |
| مهاجرت v2 → v3 | انجام‌شده | fixture تاریخی، خروجی deterministic و idempotent |
| `AssetRef` و `AudioRef` | انجام‌شده | مدل Kotlin، JSON schema و تطبیق با inventory |
| `ContentRelease` | انجام‌شده | manifest توسعه با hash محتوا/صوت و minAppVersion |
| قرارداد source/repository | انجام‌شده | Bundled/PublishedLocal/Remote و active/activate/rollback بدون implementation شبکه |
| TaskProgress نسخه‌دار | انجام‌شده | attempts/correct/hints/lastSeen با clock تزریقی |
| سازگاری داده قبلی | انجام‌شده | IDهای lesson/task/story ثابت؛ schema قدیمی به map خالی مهاجرت می‌کند |
| validator و generator | انجام‌شده | migration، content contract، audio inventory و Catalog تولیدشده |
| اجرای ۸۸ تست Native | مسدود | Gradle 8.11.1 قابل دانلود نیست و Android SDK در محیط موجود نیست |
| رندر/QA دستگاه | باز | تغییری در UI این برش ایجاد نشده؛ گیت قبلی همچنان باز است |

پیشرفت قراردادهای فاز ۰٫۵٫۰ بر اساس خروجی‌های قابل اثبات **۹ از ۱۰ واحد** است. واحد باقی‌مانده اجرای واقعی تست/کامپایل Native است؛ شمار فایل‌های تست به‌تنهایی نتیجه passed محسوب نمی‌شود.

## تغییرهای این مرحله

- نسخه اپ به `0.5.0` و versionCode به ۱۳ رسید.
- `content.fa.json` از v2 تخت به v3 با ۱ Level، ۲ Unit، ۵ Skill، ۴ Lesson و ۱۲ Task مهاجرت کرد.
- `Catalog.lessons` از hierarchy جدید مشتق می‌شود تا Home، Lesson و Review فعلی بدون تغییر رفتار کار کنند.
- برای هر prompt و صفحه قصه، `AudioRef` پایدار ثبت شد؛ URL یا storage path وارد مدل آموزشی نشد.
- `AssetRef` برای art و assetهای آینده اضافه شد.
- fixture و schema نسخه v2 در `design/fixtures` حفظ شدند.
- `content.release.json` hash محتوای canonical و inventory صوت را قفل می‌کند؛ release توسعه بدون signature، publishable نیست.
- قراردادهای Kotlin برای `ContentSource` و `ContentRepository` اضافه شدند، اما implementation شبکه عمداً وجود ندارد.
- `TaskProgress` با schema ذخیره ۱، تلاش، پاسخ درست، hint و آخرین زمان مشاهده را ثبت می‌کند.
- هر check واقعی یک attempt ثبت می‌کند؛ اصلاح پاسخ attempt دوم است. reset پیشرفت، TaskProgress را نیز پاک می‌کند.
- schema ناشناخته آینده به‌جای حذف خاموش داده، خطای storage قابل مشاهده ایجاد می‌کند.
- پشتیبانی token تکراری در قرارداد و reducer با تست منبع پوشش داده شد.
- نسخه‌های Progress و Verification قبلی در تاریخچه حفظ شدند.

## شواهد همین سورس

- migration v2→v3 و idempotency: موفق.
- content contract: موفق؛ schema v3، ۱ level، ۲ unit، ۱۲ task، ۹ story page و hashهای release.
- audio inventory: موفق؛ ۱۹ asset، ۹ task prompt و ۹ story page.
- resource sync و project audit: موفق.
- ۶۶ فایل JSON و اسکریپت‌های Python: معتبر.
- whitespace فایل‌های تغییرکرده: بدون خطا.
- ۸۸ `@Test` در سورس موجود است؛ ۵ test source تازه برای content v3، release compatibility، token تکراری، TaskProgress و schema ناشناخته افزوده شد.
- اجرای `./gradlew testDebugUnitTest --no-daemon --stacktrace` دوباره پیش از شروع تست با `Network is unreachable` هنگام دریافت Gradle 8.11.1 متوقف شد؛ هیچ تست Native اجراشده گزارش نمی‌شود.
- manifest همچنان فاقد `INTERNET` و `RECORD_AUDIO` است.
- APK/AAB ساخته نشد.

## گیت‌های باز

1. اجرای ۸۸ تست، compile و lint در محیط دارای Gradle 8.11.1، Android SDK 35 و dependency cache.
2. رندر Practice در ابعاد/فونت هدف و QA دستگاه که از فاز مبنا باز مانده است.
3. بازبینی آموزشی Skillها، ترتیب Unitها و متن ضبط؛ `approvedForRelease=false` تا آن زمان حفظ می‌شود.
4. manifest production فقط پس از compiler، امضا و rollback واقعی می‌تواند published شود.

## قدم بعد ثبت‌شده

برش بعدی `0.5.1` است: پیاده‌سازی `BundledContentSource` و `PublishedLocalSource`، compiler محلی schema→bundle، staging/verify/activation اتمیک و rollback روی fixture؛ UI فقط از snapshot فعال محلی خواهد خواند. شبکه و CMS همچنان وارد runtime نمی‌شوند.
