# CMS spike — وضعیت ۰٫۶٫۱

## خروجی این برش

`cms-export-v1` مرز vendor-neutral میان پنل ادمین و compiler است. در کنار fixture قرارداد، پروژهٔ واقعی Payload در `cms/payload` ساخته شده و با production build، type generation و ۱۶ تست دامنه اعتبارسنجی شده است. validator شناسه و متن را با inventory اصلی تطبیق می‌دهد، جداسازی نقش‌ها را کنترل می‌کند و چهار دلیل واقعی مسدودبودن انتشار را گزارش می‌دهد.

| قابلیت | Strapi 5 | Payload | نتیجه برای AVA |
|---|---|---|---|
| Media/upload | Media Library آماده | Upload Collection قابل تنظیم | هر دو مناسب |
| Draft | Draft & Publish | Versions + Drafts | هر دو مناسب |
| نقش و دسترسی | RBAC | access function در collection/field | Payload برای قواعد دقیق code-first مناسب‌تر |
| review چندمرحله‌ای | قابلیت رسمی Enterprise | مدل status + access/hook سفارشی | Payload ارزان‌تر اما مسئولیت کد/تست بیشتر |
| audit/history | Audit Logs در Enterprise | Versions تاریخچه سند؛ audit workflow باید تکمیل شود | prototype باید audit AVA را اثبات کند |
| job پردازش صوت | extension/queue نیازمند طراحی | Jobs/Tasks/Workflows داخلی | Payload مسیر مستقیم‌تری برای transcode/compiler دارد |
| مرز اپ | API موجود | REST/GraphQL/Local API | هیچ‌کدام مستقیم وارد Android نمی‌شوند |

## وضعیت اجرایی

- قرارداد نقش‌های شش‌گانه و separation rules ماشین‌خوان است.
- Payload 3.92.1، PostgreSQL adapter، S3 adapter و lockfile با نسخه‌های دقیق وجود دارند.
- پنج collection برای کاربر، محتوا، صدا، release و audit پیاده شده‌اند.
- حساب نخست به‌صورت server-side مدیر می‌شود؛ فیلدهای creator/reviewer/hash/export از API قابل جعل نیستند.
- upload در `beforeValidate` از allowlist، magic WAV/MP3/M4A، سقف ۲۵ MiB و SHA-256 عبور می‌کند و در قرنطینه می‌ماند.
- دو review مستقل، ممنوعیت self-approval، release gate و export قطعی پیاده و با تست دامنه پوشش داده شده‌اند.
- schema خروجی، fixture Payload و validator آفلاین همچنان مرز مستقل از vendor را حفظ می‌کنند.
- release فعلی به‌درستی publishable نیست: محتوا تأیید آموزشی ندارد، صوت لازم approved نیست، export جزئی است و candidate آماده compiler نیست.
- production build مسیرهای Admin/REST/GraphQL موفق است؛ server متصل به PostgreSQL/MinIO اجرا نشده چون Docker در محیط حاضر وجود ندارد.
- malware scan، decode واقعی، transcode و delivery job هنوز پیاده نشده‌اند؛ بنابراین این مرحله prototype اجرایی است، نه CMS MVP.
