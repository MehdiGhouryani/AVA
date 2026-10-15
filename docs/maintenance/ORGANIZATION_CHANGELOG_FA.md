# گزارش تاریخی جابه‌جایی فایل‌ها

> این فایل فقط تاریخچهٔ بازچینی‌هاست؛ برای مسیرهای امروز از `docs/PROJECT_STRUCTURE_FA.md` و `docs/maintenance/PROJECT_FILE_TREE_FA.txt` استفاده کن. مسیر مبدأ قدیمی در این changelog الزاماً دیگر وجود ندارد.

- `AVA_UI_ASSETS/` → `packages/ava_ui_assets/`
- `assets/review/page1-splash/page1.png` → `assets/review/screenshots/page1-splash/page1.png`
- `assets/review/Page2-1/PAGE2-1.png` → `assets/review/screenshots/page2-1/PAGE2-1.png`
- `assets/review/Page2-1/*` (اجزای تصویری) → `assets/review/candidates/page2-1/`
- `assets/review/Pagr2-2/PAGE2-2.png` → `assets/review/screenshots/page2-2/PAGE2-2.png`
- `assets/review/Pagr2-2/*` (جز یادداشت/اسکرین‌شات تفکیک‌شده) → `assets/review/candidates/page2-2/`
- `assets/review/Page3/S03a-V1.png`, `S03b-P.png`, `S03b-V1.png` → `assets/review/screenshots/page3/`
- `assets/review/Page3/*` (اجزای تصویری و README) → `assets/review/candidates/s03a-login/`
- فایل‌های `__pycache__`, `*.pyc` و `*.pyo` از بستهٔ تمیز حذف شدند.

مسیرها در این گزارش نسبت به ریشهٔ پروژه یا بستهٔ `packages/ava_ui_assets/` هستند، بسته به اینکه مسیر با `assets/` آغاز شود.


## مرحلهٔ دوم — بازچینی معماری پوشه‌ها

- `app/` → `apps/mobile/`، `admin/` → `apps/admin/` و `server/` → `services/api/` برای ساختار monorepo روشن‌تر؛ فقط READMEهای placeholder جابه‌جا شدند، نه کد اجرایی.
- `reference/prototype_react/` → `prototypes/ux_react/` و `reference/OPEN_FIRST.html` → `prototypes/legacy/OPEN_FIRST_legacy.html`؛ prototype از مسیرهای تولیدی جدا شد.
- `kits/shadcn/` → `vendor/shadcn/` چون یک کیت همراه/ثالث است، نه کد اصلی محصول.
- `assets/icons/icon/` → `assets/icons/ui/` و `assets/icons/illustrations/` → `assets/illustrations/`؛ تصویرسازی دیگر زیر آیکون‌ها نیست.
- `assets/review/page3_upload/` → `assets/review/candidates/page2-1/`، `s03a_upload/` → `candidates/page2-2/` و `login_s03a/` → `candidates/s03a-login/`؛ screenshotها جدا باقی ماندند.
- `source_alternates/` → `assets/review/alternates/`؛ جایگزین‌ها زیر بخش بازبینی قرار گرفتند.
- `tools/characters.json` و `tools/asset_notes.json` → `metadata/`؛ `tools/` اکنون فقط اسکریپت‌های نگهداری دارد.
- `pubspec_snippet.yaml` → `integration/flutter/pubspec_assets_snippet.yaml`.
- `docs/spec/README.md` در `docs/spec/` باقی ماند؛ این پوشه مقصد مشخصات جاری در فاز P0 است و منابع نسخهٔ 1.8 جداگانه در `docs/spec_sources_v1_8/` نگهداری می‌شوند.
- اسکریپت sync نمونه به `data/screens_v18.json` و `src/generated/screens.json` متصل شد؛ مسیر مطلق گزارش‌های Playwright به `.artifacts/prototype_qa/` پروژه‌محور شد.
- `ai/phases/README.md` به عنوان ترتیب رسمی فازها اضافه شد تا P10 با P1 اشتباه نشود.

- ممیزی منسوخ‌سازی v3: حذف صفحهٔ معرفی legacy بدون ارجاع؛ قرنطینهٔ خط لولهٔ شخصیت ناسازگار؛ اصلاح نام ارجاع‌های دو سند آموزشی؛ افزودن هشدار نسخهٔ تاریخی به منابع v1.8 و اصلاح دستورالعمل‌های P0/P6.
