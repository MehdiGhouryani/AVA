# runtime محتوای محلی AVA — نسخه ۰٫۵٫۱

## مسیر خواندن UI

`Catalog.snapshot` فقط seed کامپایل‌شدهٔ داخل اپ است. `BundledContentSource` آن را به `LocalContentRepository` می‌دهد. اگر `PublishedLocalSource` یک snapshot معتبر داشته باشد، همان فعال می‌شود؛ در غیر این صورت repository بدون وابستگی شبکه به seed برمی‌گردد. ViewModel تا دریافت snapshot فعال از Splash عبور نمی‌کند و همه صفحات از `AvaState.content` می‌خوانند.

در سورس production صفحه‌ها هیچ دسترسی مستقیم به `Catalog` ندارند. helperهای مسیر، شمارش، مرور و کلید task فهرست lesson همان snapshot را ورودی می‌گیرند؛ بنابراین release محلی تازه در تمام مقصدها یکدست دیده می‌شود.

## staging و activation

جریان ۰٫۵٫۱ چنین است:

1. candidate وارد staging می‌شود.
2. schema، locale، app compatibility، hash format، وضعیت انتشار، IDها و referenceها بررسی می‌شوند.
3. staged snapshot دوباره بررسی می‌شود.
4. commit زیر mutex انجام می‌شود و previous snapshot حفظ می‌گردد.
5. rollback فقط به previous معتبر برمی‌گردد.
6. candidate ناسازگار، خالی یا published بدون approval/signature، active را تغییر نمی‌دهد.

`MemoryPublishedContentStore` در این نسخه fixture اجرای atomic activation و rollback است و persistence دیسک/بازیابی process هنوز ادعا نمی‌شود. storage فایل و decoder bundle باید پیش از remote sync اضافه و روی process death آزموده شوند.

## قالب bundle

`compile-content-bundle.py` یک فایل deterministic با چهار عضو تولید می‌کند:

- `manifest.json`
- `content.json`
- `audio-inventory.json`
- `bundle-index.json`

نام‌ها allowlist هستند، path traversal و عضو تکراری رد می‌شوند و index، اندازه و SHA-256 هر payload را ثبت می‌کند. compiler با timestamp و ترتیب ثابت خروجی تکرارپذیر می‌سازد. آزمون خودکار دو build یکسان را مقایسه و bundle دست‌کاری‌شده را رد می‌کند.

```sh
python3 scripts/compile-content-bundle.py compile --output /tmp/seed.avacontent
python3 scripts/compile-content-bundle.py verify --input /tmp/seed.avacontent
python3 scripts/test-content-bundle.py
```

## مرز این نسخه

هیچ permission اینترنت، دانلود، CMS SDK یا parser محتوای remote وارد اپ نشده است. Remote source همچنان فقط interface است. UI تازه و تغییر برنامه آموزشی نیز در این برش وجود ندارد.
