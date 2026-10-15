> **هشدار نسخهٔ تاریخی:** این سند بازتاب‌دهندهٔ بستهٔ v1.8 در زمان خودش است. عبارت‌هایی مانند «فعلی/منبع حقیقت»، تصمیم‌های محصول و مسیرهای فایل در بدنه فقط نسبت به همان snapshot معتبرند؛ آن‌ها را به ساختار/تصمیم جاری تعمیم ندهید. مسیرها و وضعیت جاری در `docs/PROJECT_STRUCTURE_FA.md` و `AI_START_HERE.md` است؛ قبل از اجرا، تعارض‌ها را با منابع موجود و دستور مالک بررسی کنید. جزئیات: `00_HISTORICAL_PATHS_NOTICE_FA.md`.

# آوا ۱.۸ — baseline تمیز ۴ تا ۷ سال

این بسته مرجع فعلی طراحی و prototype آواست؛ artworkهای قبلی عمداً حذف شده‌اند و طراحی بصری جدید هنوز ساخته نشده است.

## منبع حقیقت
- `canonical/` — تنها مرجع فعال برای screen registry، state contract، تصمیم‌ها، flow و handoff.
- `baseline_data/b/` — داده و copy آموزشی فعال.
- `design_handoff/` — خروجی‌های تحویل؛ CSV حالت‌ها مشتق‌شده از JSON canonical است.
- `design_handoff/characters/` و `design_handoff/character_assets/` — character sheetهای مرجع، spec تولید و manifestهای هر شخصیت (نگاشت: `characters/CHARACTERS_INDEX.json`).
- `prototype_source/` — prototype بررسی؛ `src/generated/screens.json` خروجی تولیدشده از `canonical/screens_v18.json` است.
- `qa/` — گیت‌های کیفیت و مرز ادعا.
- `archive/` — نسخه‌های قدیمی v16/v17 و ممیزی‌های منسوخ؛ منبع current نیستند.

## دامنه محصول
کل محصول در این مرحله برای کودکان **۴ تا ۷ سال** است. انتخاب رده‌بندی سنی در UI و route تغییر گروه سنی حذف شده است.

## وضعیت محتوا و تصویر
سه PNG به‌عنوان character sheet مرجع (پوفی، فندق، روکا) در `design_handoff/characters/` است و برای هرکدام master لایه‌بندی‌شدهٔ SVG و stateها ساخته شده (۴۹ فایل SVG + PNG ۱۰۲۴ و `states_index.json`)؛ ابزارها در `character_assets/_tools/`. آوا و دانا فعلاً حذف شده‌اند (جزئیات در `canonical/03`)؛ شخصیت اصلی برند هنوز تعیین نشده. این assetها پیش‌نویس‌اند و `Art.jsx` هنوز به آن‌ها وصل نیست.

## شمارش فعلی
`58` screen record و `359` state contract فعال در این baseline وجود دارد. مسیر تغییر رده‌بندی سنی و stateهای آن از current حذف شده‌اند؛ تصمیم این حذف در changelog ثبت شده است.

این بسته proof تولیدی برای backend، audio، native، payment، accessibility روی دستگاه یا pilot کودک نیست؛ فقط قرارداد طراحی، prototype و دادهٔ baseline را نگه می‌دارد.
