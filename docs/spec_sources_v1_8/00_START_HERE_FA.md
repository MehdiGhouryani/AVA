> **هشدار نسخهٔ تاریخی:** این سند بازتاب‌دهندهٔ بستهٔ v1.8 در زمان خودش است. عبارت‌هایی مانند «فعلی/منبع حقیقت»، تصمیم‌های محصول و مسیرهای فایل در بدنه فقط نسبت به همان snapshot معتبرند؛ آن‌ها را به ساختار/تصمیم جاری تعمیم ندهید. مسیرها و وضعیت جاری در `docs/PROJECT_STRUCTURE_FA.md` و `AI_START_HERE.md` است؛ قبل از اجرا، تعارض‌ها را با منابع موجود و دستور مالک بررسی کنید. جزئیات: `00_HISTORICAL_PATHS_NOTICE_FA.md`.

# ۰: از اینجا شروع کنید

این revision یک baseline تمیز برای ادامهٔ طراحی آواست. artworkهای قبلی حذف شده‌اند، پنج character sheet جدید به‌عنوان مرجع اضافه شده و محصول برای **۴ تا ۷ سال** ثابت شده است.

## ترتیب مطالعه
1. `01_PRODUCT_AND_FLOWS_FA.md` برای فلو
2. `02_DESIGN_SYSTEM_FA.md` برای UI و layout
3. `03_BRAND_CHARACTERS_AND_AVATAR_FA.md` برای قرارداد شخصیت‌ها؛ فقط character sheet مرجع وجود دارد (`design_handoff/characters/`)، asset تولیدی نه
4. `04_STATE_BEHAVIOR_AND_CONTENT_FA.md` برای stateها
5. `05_IMPLEMENTATION_HANDOFF_FA.md` برای انتقال به اجرا
6. `06_QA_AND_OPEN_GATES_FA.md` برای گیت‌های باز
7. `07_SCREEN_BY_SCREEN_FA.md` برای جزئیات screenها
8. `08_DECISIONS_AND_CHANGELOG_FA.md` برای تصمیم‌های revision

## source of truth
`canonical/screens_v18.json` تنها منبع رجیستر است. `prototype_source/src/generated/screens.json` خروجی مشتق‌شده و قابل تولید مجدد است. `canonical/state_contracts_v18.json` منبع state و `design_handoff/state_contracts_v18.csv` خروجی handoff است.

## دامنه فعلی
کل محصول برای ۴ تا ۷ سال است. هیچ selector یا route برای تغییر گروه سنی در current baseline وجود ندارد.
