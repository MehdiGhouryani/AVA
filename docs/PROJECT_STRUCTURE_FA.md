# ساختار استاندارد فایل‌های AVA

این نقشه ساختار **همین snapshot** را توصیف می‌کند. مسیرهایی که در اسناد `v1.8` یا کد بایگانی آمده‌اند الزاماً مسیرهای فعال نیستند.

## درخت سطح بالا

```text
AVA/
├── AI_START_HERE.md, README_FA.md       راهنمای عامل و صاحب پروژه
├── AGENTS.md, CLAUDE.md, GEMINI.md, .cursorrules  bootstrap ابزارهای AI؛ تکرار عمدی
├── ai/phases/                           backlog مرحله‌ای P0 تا P10؛ همهٔ فازها وضعیت اجراشده نیستند
├── apps/mobile/                         placeholder اپ Flutter؛ کد اجرایی فعلاً نیست
├── apps/admin/                          placeholder پنل وب؛ کد اجرایی فعلاً نیست
├── services/api/                        placeholder بک‌اند؛ کد اجرایی فعلاً نیست
├── data/
│   ├── screens_v18.json                 source رجیستری baseline برای prototype
│   ├── state_contracts_v18.json         قراردادهای baseline state
│   ├── ui_layout.json                   دادهٔ چیدمان
│   └── baseline/b/                      داده‌های آموزشی موجود
├── docs/
│   ├── pedagogy/                        دو مرجع آموزشی موجود؛ فایل‌های 03…14 خروجی آینده‌اند
│   ├── spec_sources_v1_8/                منبع تاریخی؛ مسیرها و ادعاهای وضعیت داخل آن تاریخی‌اند
│   ├── spec/                            محل خروجی مشخصات جاری؛ فعلاً README دارد
│   ├── maintenance/                     گزارش ممیزی، اعتبارسنجی، تغییرات و درخت فایل
│   ├── research/, qa/, legal/            اسناد پژوهش، QA و یادداشت حقوقی
│   └── PROJECT_STRUCTURE_FA.md           نقشهٔ جاری ساختار
├── packages/ava_ui_assets/
│   ├── assets/                           تصاویر/آیکون‌ها/illustrations/دارایی‌های بازبینی
│   ├── manifest.json                     manifest فعال و صفحه‌محور
│   ├── preview/                          پیش‌نمایش‌ها
│   └── legacy/character_pipeline/         کد و metadata قدیمی؛ فقط بایگانی، اجرا نشود
└── prototypes/ux_react/                  prototype برای بررسی UX، نه اپ تولیدی
    ├── src/generated/screens.json        خروجی از data/screens_v18.json؛ دستی ویرایش نشود
    ├── tests/                            آزمون‌های prototype
    ├── tools/sync_registry.py            ابزار همگام‌سازی منبع و خروجی
    └── vendor/shadcn/                    کد UI که prototype واقعاً import می‌کند
```

## قواعد نگهداری

- **Source و generated:** `data/screens_v18.json` منبع baseline است؛ `prototypes/ux_react/src/generated/screens.json` خروجی آن است. اجرا از ریشهٔ پروژه: `python prototypes/ux_react/tools/sync_registry.py`. رجیستری هنوز baseline prototype است، نه مجوز پیاده‌سازی همهٔ صفحه‌ها.
- **وضعیت auth:** ورودی‌های email/Google در baseline تاریخی‌اند. هدف محصول phone number + OTP است؛ ایمیل فقط در تکمیل/تنظیمات پروفایل، آن هم با نیاز صریح مالک، قابل بررسی است.
- **اسناد v1.8:** `docs/spec_sources_v1_8/` را برای traceability نگه دار؛ banner ابتدای هر سند را بخوان. مسیرها/تصمیم‌های قدیمی را به‌عنوان وضعیت جاری اجرا نکن.
- **کد شخصیت قدیمی:** `packages/ava_ui_assets/legacy/character_pipeline/` با مسیر و schema فعلی سازگار نیست و از برنامه‌ای استفاده نمی‌شود. آن را اجرا یا مستقیم import نکن؛ در صورت نیاز باید مهاجرت طراحی شود.
- **Manifest دارایی‌ها:** `packages/ava_ui_assets/manifest.json` manifest صفحه‌محور فعلی است. هفت مسیر فایل در آن هنوز غایب‌اند؛ آن‌ها را با فایل جعلی پر نکن.
- **Bootstrapهای AI:** `AGENTS.md`, `CLAUDE.md`, `GEMINI.md` و `.cursorrules` تکرار عمدی دارند تا ابزارهای مختلف دستور شروع درست را بخوانند.
- **فازها و فایل‌های آینده:** وجود یک prompt به معنی وجود ورودی‌ها، تأیید تصمیم‌ها یا اجرای task نیست. فایل‌های غایب فقط در task مربوط ایجاد شوند؛ شناسه‌های U/C/OQ بدون منبع، تأیید قطعی نیستند.
- **خروجی ZIP:** فایل‌های موقت (`__pycache__`, `*.pyc`, `.venv`, `build`, `dist`, `node_modules`, گزارش تست) نباید وارد بسته شوند.

## محدودیت این snapshot

این بسته اپ Flutter، پنل یا بک‌اند قابل اجرا ندارد. نمونهٔ React هم package manifest ریشه ندارد؛ از این ساختار نباید ادعای build یا انتشار نتیجه گرفت.
