# ADR-0003 — انتخاب فناوری برای prototype CMS

وضعیت: Payload برای prototype انتخاب شد؛ تصمیم production مشروط است

تاریخ: ۲۰۲۶-۱۰-۲۸

## مسئله

CMS خصوصی AVA باید درس، ترتیب، صوت، metadata، review، release و rollback را مدیریت کند. اپ کودک نباید مستقیماً به CMS وصل شود. هزینه نگهداری و امکان self-host برای یک تیم کوچک مهم است و جداسازی Author/Audio Producer/Reviewer/Publisher باید قابل اعمال باشد.

## شواهد رسمی

- Strapi 5 دارای Media Library و Draft & Publish است؛ Review Workflows و Audit Logs در مستندات فعلی Enterprise معرفی شده‌اند.
- Payload مجموعه‌های code-first، upload collection، access control، hooks، versions/drafts و jobs/workflows اجرایی دارد و self-host متن‌باز ارائه می‌کند.
- Payload workflow تحریریهٔ چندمرحله‌ای آمادهٔ AVA را به‌صورت no-code تحویل نمی‌دهد؛ state، access و audit موردنیاز باید در مدل/Hook خود پروژه نوشته و آزموده شوند.

منابع رسمی:

- https://docs.strapi.io/cms/features/media-library
- https://docs.strapi.io/cms/features/draft-and-publish
- https://docs.strapi.io/cms/features/review-workflows
- https://docs.strapi.io/cms/features/audit-logs
- https://payloadcms.com/docs/configuration/collections
- https://payloadcms.com/docs/upload/overview
- https://payloadcms.com/docs/access-control/overview
- https://payloadcms.com/docs/versions/overview
- https://payloadcms.com/docs/jobs-queue/overview

## تصمیم prototype

Payload برای prototype انتخاب می‌شود، چون schema تایپ‌شده و code-first با قرارداد JSON/compiler پروژه هم‌راستا است و upload/access/version/job را بدون وابستگی به قابلیت Enterprise در اختیار می‌گذارد. این انتخاب به معنی اتصال اپ Android به Payload نیست؛ فقط adapter خروجی CMS به قرارداد `cms-export-v1` ساخته خواهد شد.

## شرط تبدیل به تصمیم production

prototype باید این موارد را واقعاً اثبات کند:

1. ورود و نمایش درست فارسی/RTL در فرم‌های درس و صوت.
2. upload master به quarantine، کنترل MIME/magic/decode/size/hash و تولید delivery توسط job جدا.
3. جلوگیری عملی از self-approval و publish پیش از دو review.
4. version history و audit قابل استخراج برای تغییر متن/صوت/status.
5. export deterministic کامل و عبور از compiler مستقل AVA.
6. backup/restore پایگاه‌داده و object storage در یک محیط آزمایشی.

اگر پیاده‌سازی workflow/audit سفارشی Payload هزینهٔ نگهداری نامتناسب داشت و بودجه Enterprise پذیرفته شد، Strapi دوباره ارزیابی می‌شود.

## شواهد prototype در ۰٫۶٫۱

- سورس مستقل Payload 3.92.1 با PostgreSQL و storage رسمی S3 تولید type و production build می‌شود.
- پنج collection، شش نقش، فیلدهای server-managed، audit hook و gate انتشار وجود دارند.
- ۱۶ تست دامنه، تشخیص magic/hash، جداسازی دو reviewer و export deterministic را پوشش می‌دهند.
- شرط‌های ۲ تا ۵ در سطح کد/دامنه پیش رفته‌اند، اما اثبات integration با DB/S3 و decode/transcode هنوز باز است.
- شرط ۱ نیازمند QA مرورگر واقعی و شرط ۶ نیازمند محیط deployment آزمایشی است؛ بنابراین وضعیت production همچنان مشروط می‌ماند.
