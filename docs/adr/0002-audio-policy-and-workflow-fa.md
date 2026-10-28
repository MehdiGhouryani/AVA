# ADR-0002 — سیاست صوت انسانی و workflow ادمین

وضعیت: پذیرفته‌شده برای معماری؛ ضبط و CMS هنوز اجرا نشده
تاریخ: ۲۰۲۶-۱۰-۲۸

## تصمیم آموزشی

صوت نهایی آموزش خواندن باید توسط انسان ضبط و از نظر تلفظ/هدف آموزشی بازبینی شود. TTS نصب‌شده و آفلاین فقط fallback توسعه و دسترس‌پذیری است. فعالیت شنیداری همیشه hint نوشتاری دارد و نبود صدا مانع تکمیل نمی‌شود.

## workflow آینده

`Draft → Needs Audio → Educational Review → Technical Validation → Ready to Publish → Published → Archived/Rolled Back`

نقش‌های حداقلی: Author، Audio Producer، Education Reviewer، Technical Reviewer، Publisher و QA. نویسنده یا تولیدکننده صوت نمی‌تواند همان مورد را به‌تنهایی publish کند.

## پردازش فایل

1. master بدون افت در storage خصوصی بارگذاری می‌شود.
2. پسوند، MIME، magic bytes، decode، مدت، کانال، حجم و checksum کنترل می‌شوند.
3. فایل ابتدا در quarantine است و نام تصادفی storage می‌گیرد.
4. job غیرهمزمان نسخه delivery را می‌سازد؛ پروفایل اولیه برای آزمون AAC-LC/M4A mono است، نه تصمیم کیفیت نهایی.
5. بازبین متن، نیت تلفظ، سکوت، clipping و تطابق گوینده را بررسی می‌کند.
6. فقط asset دارای وضعیت `approved` می‌تواند نیاز صوتی اجباری را در release تولیدی برآورده کند.

## مدل آینده AudioAsset

شناسه پایدار، kind، locale، recordingText/markedText، speaker/take، master key، delivery key، codec، duration، sample rate، channels، bytes، sha256، license/consent، status، reviewer و contentVersion ثبت می‌شوند. task فقط `AudioRef` نگه می‌دارد و URL در مدل آموزشی ذخیره نمی‌شود.
