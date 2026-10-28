# نمونهٔ اجرایی CMS آوا

این پوشه یک پروژهٔ مستقل Payload است؛ اپ Android هیچ import، URL یا query مستقیمی به آن ندارد. خروجی نهایی CMS باید از قرارداد `cms-export-v1` عبور کند و پس از verify به بستهٔ offline-first اپ تبدیل شود.

## آنچه اکنون اجرا شده است

- Payload `3.92.1` با نسخه‌های دقیق و بدون caret برای همهٔ بسته‌های Payload/Next/React؛
- PostgreSQL و فایل Compose محلی برای PostgreSQL + MinIO خصوصی؛
- پنج collection: کاربر، محتوای آموزشی، صوت، release و audit append-only؛
- شش نقش و جداسازی نویسنده، بازبین فنی، بازبین آموزشی و ناشر؛
- upload صوت با سقف ۲۵ MiB، allowlist MIME، تشخیص magic برای WAV/MP3/M4A و SHA-256؛
- قرنطینهٔ پیش‌فرض، دو مرحلهٔ review و ممنوعیت self-approval؛
- release gate کامل و export قطعی با مرتب‌سازی stable ID؛
- جداسازی master قرنطینه از delivery: release فقط وقتی مجاز است که delivery آماده، object key و SHA-256 معتبر داشته باشد؛
- ذخیره‌سازی S3-compatible اختیاری؛ بدون تنظیم S3، توسعه از storage محلی Payload استفاده می‌کند.

## اجرای محلی

پیش‌نیاز رسمی این نسخه Node `^22.23.4` یا `>=24.22.1` است. سپس:

```bash
cd cms/payload
docker compose up -d
cp .env.example .env
```

مقادیر `CHANGE_ME` را عوض کنید. برای Compose حاضر، `DATABASE_URL` باید با کاربر/رمز محلی Compose هماهنگ شود. برای MinIO نیز این مقادیر را در `.env` قرار دهید:

```dotenv
S3_BUCKET=ava-audio-private
S3_ENDPOINT=http://127.0.0.1:9000
S3_ACCESS_KEY_ID=ava_local
S3_SECRET_ACCESS_KEY=ava_local_only_change_me
S3_FORCE_PATH_STYLE=true
```

در پایان:

```bash
pnpm install --frozen-lockfile
pnpm generate:types
pnpm dev
```

پنل روی `/admin` است. حساب نخست فقط وقتی collection کاربران خالی است ساخته می‌شود و hook سرور نقش آن را به `system_admin` ارتقا می‌دهد. حساب‌های بعدی فقط توسط مدیر سامانه ساخته می‌شوند و نقش پیش‌فرضشان `content_author` است.

## گیت‌های امنیتی و محدودیت آگاهانه

- bucket باید private بماند؛ download از access control و URL امضاشده عبور می‌کند.
- تشخیص magic و hash جای antivirus و decode واقعی را نمی‌گیرد. اسکن malware، decode با ffprobe و transcode delivery در فاز ۰٫۷٫۰ گیت باز هستند.
- `deliveryStatus` و metadata آن فقط server-managed هستند؛ تا job واقعی آن‌ها را پس از read-back verification تکمیل نکند، release fail-closed می‌ماند.
- audit از UI قابل ایجاد/ویرایش/حذف نیست و hookهای server-side آن را می‌نویسند؛ immutability سطح DB هنوز گیت deployment است.
- compose برای توسعه است و credentialهای نمونه هرگز برای staging/production مناسب نیستند.
- تغییر مستقیم وضعیت یا فایل در DB خارج از مدل تهدید این prototype است؛ production به least privilege، backup، secret manager و log خارجی نیاز دارد.

## بررسی سریع

```bash
pnpm typecheck
pnpm test
python3 ../../scripts/validate-payload-prototype.py
```
