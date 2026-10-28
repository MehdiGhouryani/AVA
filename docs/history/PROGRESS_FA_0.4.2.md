# وضعیت واقعی AVA — سورس ۰٫۴٫۲

این برش آغاز فاز «مبنای قابل اعتماد و معماری محتوا» است. رفتار و دامنه UI عوض نشده‌اند: ۴ درس، ۱۲ فعالیت، ۲ قصه/۹ صفحه و `approvedForRelease=false`. هیچ CMS، شبکه، backend، APK یا AAB اضافه نشده است.

## وضعیت فاز

| بخش فاز ۰ | وضعیت | شاهد |
|---|---|---|
| همگامی منابع و audit | انجام‌شده | ۱۶ تصویر، ۲۶۳ متن، ۴ درس/۱۲ فعالیت، ۲ قصه و ۱۳ مرجع |
| ADR مرز CMS و اپ | انجام‌شده | ADR-0001؛ UI فقط از release محلی آینده می‌خواند |
| سیاست و workflow صوت | انجام‌شده | ADR-0002؛ صوت انسانی نهایی و TTS fallback |
| inventory صوت | انجام‌شده | ۱۹ asset: ۹ prompt، ۹ صفحه قصه و ۱ effect |
| validator inventory | انجام‌شده | تطبیق refها با content v2 داخل project audit |
| test strategy | انجام‌شده | لایه‌ها، ماتریس UI و قواعد شواهد ثبت شد |
| اجرای ۸۳ تست Native | مسدود | wrapper برای Gradle 8.11.1 به دانلود نیاز دارد و شبکه در دسترس نیست؛ Android SDK نیز موجود نیست |
| رندر Practice و QA دستگاه | باز | نیازمند محیط Gradle/SDK و سپس دستگاه واقعی |

پیشرفت اجرایی فاز ۰ بر اساس خروجی‌های قابل اثبات **۶ از ۱۰ واحد** است؛ چهار واحد مربوط به اجرای تست، lint/compile و رندر/QA هنوز بسته نشده‌اند. این درصد ادعای تکمیل کل محصول نیست.

## تغییرهای این مرحله

- نسخه سورس و اپ به ۰٫۴٫۲/کد ۱۲ رسید.
- `design/audio.inventory.json` و schema v1 افزوده شدند؛ شناسه‌های صوت از نام فایل و URL مستقل‌اند.
- نیازهای حرف/واج عمداً `needs_educator_script` هستند تا نام نشانه و صدای آن اشتباه یکی نشوند.
- فعالیت‌های شنیداری `requiredForPublication=true` و fallback نوشتاری دارند؛ صدای تشخیص و قصه اختیاری و قابل تکمیل با متن است.
- `scripts/validate-audio-inventory.py` ref گم‌شده، تکراری، متن ناسازگار و سیاست غلط required/fallback را رد می‌کند.
- project audit اکنون کنترل صوت را هم اجرا می‌کند.
- مرز آینده `CMS خصوصی → compiler/validator → ContentRelease → PublishedLocalSource` ثبت شد؛ هنوز چیزی از این مسیر وارد runtime اپ نشده است.
- نسخه‌های قبلی Progress و Verification در `docs/history` حفظ شدند.

## شواهد همین سورس

- `sync-android-resources.py --check`: موفق.
- `validate-audio-inventory.py`: موفق؛ ۱۹ asset، ۹ task prompt و ۹ story page.
- `audit-project.py`: موفق؛ content/audio schema، ابعاد asset، skill hash، reference hash، font و منابع Native.
- اعتبارسنجی JSON و compile اسکریپت‌های Python: موفق.
- ۸۳ `@Test` همچنان در سورس موجود است.
- `./gradlew testDebugUnitTest --no-daemon --stacktrace`: اجرا شد اما پیش از Gradle به‌علت `Network is unreachable` هنگام دریافت Gradle 8.11.1 متوقف شد؛ بنابراین هیچ تست Native اجراشده یا passed گزارش نمی‌شود.
- manifest فاقد `INTERNET` و `RECORD_AUDIO` است.
- APK/AAB ساخته نشد.

## گیت‌های باز

1. فراهم‌شدن Gradle 8.11.1، Android SDK 35 و dependency cache؛ سپس اجرای ۸۳ تست و lint مرتبط.
2. رندر Practice در Mix/Mistakes/Learned روی ۳۲۰×۵۶۰ و fontScale 1.3/1.5.
3. TalkBack، TTS، lifecycle و IME روی دستگاه واقعی.
4. بازبینی متخصص آموزش فارسی برای تفکیک نام نشانه/واج و متن ضبط.

## قدم بعد ثبت‌شده

پس از رفع گیت ابزار، تست و رندر ۰٫۴٫۲ بسته می‌شود. سپس برش ۰٫۵٫۰ با `content schema v3`، `AssetRef`/`AudioRef`، مدل مستقل `ContentRelease`، migration داده و contract test آغاز می‌شود. در ۰٫۵٫۰ هنوز شبکه و CMS اجرایی اضافه نخواهد شد.
