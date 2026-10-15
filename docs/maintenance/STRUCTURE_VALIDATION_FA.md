# گزارش اعتبارسنجی ساختار AVA

این گزارش صحت ساختار و ارجاع‌های محلی را می‌سنجد؛ اجرای اپ Flutter، پنل، backend یا prototype را تأیید نمی‌کند. پس از ممیزی منسوخ‌سازی، مسیرهای آموزشی و زنجیرهٔ قدیمی شخصیت‌ها اصلاح/قرنطینه شده‌اند.

- فایل‌های پروژه در snapshot: 308، شامل 176 فایل متنی/ساخت‌یافته؛ فایل‌های JSON: 46 و خطای parse: 0.
- رجیستری prototype از `data/screens_v18.json` تولید شد؛ اجرای واقعی `python prototypes/ux_react/tools/sync_registry.py` موفق بود و دادهٔ source/generated برابر است (58 screen).
- syntax پنج فایل Python با AST بررسی شد؛ خطا: 0. لینک‌های نسبی Markdown/HTML بررسی شدند؛ لینک شکسته: 0. هیچ ارجاع فعالی از متن پروژه به مسیرهای قبلی ابزارهای شخصیت یافت نشد. این بررسی وجود/صحت برنامهٔ اجرایی را اثبات نمی‌کند.
- `packages/ava_ui_assets/manifest.json` شامل 18 ورودی صفحه‌محور است؛ هفت مسیر تصویر در بسته غایب‌اند. جزئیات در پایین آمده است.
- `prototypes/legacy/OPEN_FIRST_legacy.html`، صفحهٔ راه‌انداز legacy بدون ارجاع، حذف شد.
- زنجیرهٔ قدیمی manifest شخصیت‌ها به `packages/ava_ui_assets/legacy/character_pipeline/` منتقل شد؛ آن پوشه بایگانی غیرقابل اجراست.
- bootstrapهای AI مشابه عمداً حفظ شده‌اند؛ گروه‌های فایل دقیقاً یکسان فقط همین bootstrapها و زوج source/generated رجیستری‌اند. درخت ساختار با 308 فایل و 63 پوشه با فایل‌سیستم تطبیق دارد؛ ZIP آزمون فشرده‌سازی جداگانه می‌گیرد.

## دارایی‌هایی که manifest درخواست می‌کند اما در آرشیو نیستند

- `bg_s02p1` → `assets/backgrounds/onboarding/bg_s02p1.webp`
- `background_storybook_garden` → `assets/review/candidates/page4/background_storybook_garden.webp`
- `character_ava_walking_with_book` → `assets/review/candidates/page4/character_ava_walking_with_book.webp`
- `character_rabbit_star_wand` → `assets/review/candidates/page4/character_rabbit_star_wand.webp`
- `prop_magic_star_wand` → `assets/review/candidates/page4/prop_magic_star_wand.webp`
- `character_squirrel_waving` → `assets/review/candidates/page4/character_squirrel_waving.webp`
- `character_fox_with_map` → `assets/review/candidates/page4/character_fox_with_map.webp`

## تفسیر و محدودیت‌ها

برای مسیرهای غایب یا فایل خالی/تصویر ساختگی ساخته نشده است. باید فایل منبع واقعی ارائه شود یا درباره حذف/بازتعریف آن تصمیم گرفته شود. اعتبارسنجی ساختار، درستی محتوای آموزشی، تأیید شخصیت‌ها یا موفقیت build اجرایی را اثبات نمی‌کند.
