# وضعیت واقعی AVA — سورس ۰٫۵٫۱

این برش runtime محتوای محلی را پیاده می‌کند. UI و دامنه آموزشی تغییر نکرده‌اند؛ محتوا همچنان توسعه‌ای و `approvedForRelease=false` است. هیچ شبکه، CMS اجرایی، APK یا AAB اضافه نشده است.

## وضعیت فاز

| بخش runtime محلی | وضعیت | شاهد |
|---|---|---|
| `BundledContentSource` | انجام‌شده | seed تولیدشده همیشه fallback است |
| `PublishedLocalSource` | انجام‌شده | snapshot فعال store را می‌خواند |
| validation candidate | انجام‌شده | schema/locale/app/hash/status/ID/ref |
| staging و commit | انجام‌شده | بررسی دوباره و commit زیر mutex |
| rollback | انجام‌شده | previous snapshot معتبر حفظ و بازیابی می‌شود |
| compiler bundle | انجام‌شده | manifest/content/audio/index با ترتیب و timestamp ثابت |
| tamper rejection | انجام‌شده | تغییر content بدون index/hash تازه رد می‌شود |
| اتصال UI به snapshot | انجام‌شده | ViewModel قبل از خروج Splash بارگذاری می‌کند؛ Catalog read صفحه‌ها صفر |
| fixture activation tests | انجام‌شده | fallback، activate، reject، rollback و published guard |
| اجرای ۹۴ تست Native | مسدود | Gradle 8.11.1 قابل دانلود نیست و Android SDK وجود ندارد |

پیشرفت برش ۰٫۵٫۱ بر اساس شواهد قابل اجرا **۹ از ۱۰ واحد** است. واحد باز اجرای واقعی compile/test Native است. persistence دیسک برای PublishedLocal هنوز خارج از ادعای این نسخه است و store فعلی fixture حافظه‌ای محسوب می‌شود.

## تغییرهای این مرحله

- نسخه اپ به `0.5.1` و versionCode به ۱۴ رسید.
- `BundledContentSource`، `PublishedLocalSource`، `MemoryPublishedContentStore` و `LocalContentRepository` پیاده شدند.
- candidate پیش از stage و پس از stage اعتبارسنجی می‌شود.
- commit و rollback زیر mutex انجام می‌شوند و candidate ردشده active را تغییر نمی‌دهد.
- release ناسازگار با نسخه اپ، محتوای خالی، ID تکراری، ref نامعتبر و published بدون approval/signature رد می‌شوند.
- اولین activation نیز seed Bundled را به‌عنوان previous نگه می‌دارد تا rollback واقعی باشد.
- `compile-content-bundle.py` bundle آفلاین deterministic تولید و verify می‌کند.
- bundle فقط چهار عضو allowlistشده دارد و path traversal، عضو تکراری، اندازه/hash اشتباه و version mismatch را رد می‌کند.
- آزمون compiler دو خروجی byte-identical می‌سازد و tamper را عمداً امتحان می‌کند.
- `AvaState` snapshot فعال را نگه می‌دارد و ViewModel تا آماده‌شدن آن از Splash عبور نمی‌کند.
- Home، Lesson، Story، Practice، Profile و About از snapshot state می‌خوانند.
- دسترسی مستقیم screenهای production به `Catalog` به صفر رسید؛ Catalog فقط seed داخلی و default سازگاری تست است.
- ۶ test source تازه برای repository و اتصال ViewModel اضافه شد.
- نسخه‌های Progress و Verification ۰٫۵٫۰ در تاریخچه حفظ شدند.

## شواهد همین سورس

- migration v2→v3 و content/audio contract: موفق.
- compiler deterministic و verify bundle: موفق.
- tamper rejection: موفق.
- resource sync و project audit: موفق.
- ۶۷ فایل JSON و اسکریپت‌های Python: معتبر.
- ۹۴ `@Test` در سورس موجود است.
- دسترسی مستقیم production screenها به Catalog: صفر.
- manifest همچنان فاقد `INTERNET` و `RECORD_AUDIO` است.
- اجرای `./gradlew testDebugUnitTest --no-daemon --stacktrace` پیش از شروع تست با `Network is unreachable` هنگام دریافت Gradle متوقف شد؛ هیچ تست Native اجراشده یا passed گزارش نمی‌شود.
- APK/AAB ساخته نشد.

## گیت‌های باز

1. اجرای ۹۴ تست، compile و lint در محیط دارای Gradle 8.11.1، Android SDK 35 و dependency cache.
2. store فعلی process-persistent نیست؛ decoder فایل، نوشتن اتمیک روی دیسک و recovery پس از process death باید پیش از remote sync پیاده شود.
3. رندر و QA دستگاه مراحل قبلی همچنان باز است.
4. release تولیدی همچنان نیازمند تأیید آموزشی، signature و صوت معتبر است.

## قدم بعد ثبت‌شده

برش بعدی `0.6.0` است: activity engine v3، registry نوع فعالیت، session reducer، renderer مستقل از URL/CMS و audio playback قابل تزریق. persistence دیسک bundle پیش از فعال‌شدن remote sync به‌عنوان گیت معماری حفظ می‌شود.
