# Playbook فاز ۰٫۶٫۱ — بستن integration

## هدف

اثبات اینکه prototype موجود فقط compile نمی‌شود، بلکه با PostgreSQL و object storage خصوصی درست کار می‌کند؛ هم‌زمان وضعیت واقعی تست Native روشن شود. این فاز قابلیت محصول جدید اضافه نمی‌کند.

## ترتیب اجرای فنی

1. محیط و نسخه‌های Node/pnpm/JDK/SDK/Docker را ثبت کن.
2. `.env` محلی با secret تصادفی بساز؛ هرگز commit نکن.
3. PostgreSQL و MinIO را با healthcheck بالا بیاور و bucket خصوصی را ایجاد کن.
4. migration و bootstrap حساب نخست را اجرا و access حساب دوم را تست کن.
5. یک WAV/MP3 fixture بی‌خطر کوچک upload کن؛ object key، ACL خصوصی، magic و hash را بررسی کن.
6. دسترسی ناشناس را رد و دسترسی احرازشده/signed را اثبات کن.
7. restart DB/CMS/storage و تکرار خواندن metadata را تست کن.
8. در مسیر مستقل، unit/lint Native را بدون assemble/bundle اجرا کن.

## تست‌های حداقلی CMS

- first-user bootstrap race؛
- unauthenticated user creation denial؛
- MIME/magic mismatch rejection؛
- private object denial؛
- signed download authorization؛
- persistence پس از restart؛
- audit event پس از تغییر status.

## شرط توقف

اگر Docker/DB/S3 یا toolchain Native موجود نیست، implementation حدسی برای سبزکردن task نساز. command و blocker را ثبت کن و task را باز نگه دار. approval آموزشی صرفاً human gate است.
