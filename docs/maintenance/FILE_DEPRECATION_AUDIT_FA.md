# ممیزی منسوخ‌سازی فایل‌ها و دستورالعمل‌های AVA

**دامنه:** فایل‌های متنی، اسناد محصول و مخصوصاً دستورالعمل‌های هوش مصنوعی در snapshot `AVA_organized_v3`؛ بررسی مسیرها، ارجاع‌های متنی، تناقض‌های عملیاتی، فایل‌های همسان و ارتباط source/generated.

## نتیجهٔ اجرایی

- یک فایل واقعاً زائد از ساختار فعال حذف شد: `prototypes/legacy/OPEN_FIRST_legacy.html`. فایل فقط یک صفحهٔ معرفیِ صریحاً legacy بود، هیچ ارجاع فعالی به آن پیدا نشد و محتوای راهنمای آن در READMEهای فعلی پوشش داده شده بود.
- چند دستورالعمل خطرناک یا متناقض اصلاح شدند تا مدل فایل/ابزار غایب را موجود فرض نکند، فازها را با وابستگی مفقود شروع نکند و روش احراز هویت قدیمی را پیاده‌سازی نکند.
- اسناد تاریخی، baselineهای منبع و فایل‌های generated عمداً حذف نشدند؛ حذفشان می‌توانست traceability یا prototype موجود را بشکند. دلایل پایین آمده است.

## فایل‌های اصلاح‌شده و دلیل

| فایل | ایراد کشف‌شده | اصلاح انجام‌شده |
|---|---|---|
| `AGENTS.md`, `CLAUDE.md`, `GEMINI.md`, `.cursorrules` | می‌گفتند وضعیت جاری در `ai/STATE.md` ثبت است، درحالی‌که آن فایل وجود ندارد. | هر چهار فایل شفاف می‌گویند State/decision ledger در این snapshot موجود نیست و فازها فقط برنامه‌اند. تکرار عمداً حفظ شده چون هر ابزار ممکن است فقط فایل خودش را بخواند. |
| `AI_START_HERE.md` | به workflow، state و logهایی اشاره داشت که موجود نیستند و «تصمیم قفل‌شده» را ممکن بود از متن prompt برداشت کند. | مرز فایل‌های موجود/غایب، مسیرهای تاریخی، رجیستری legacy، نبود decision ledger و روش hand-off بدون جعل اجرای ابزارها صریح شد. |
| `ai/phases/README.md` | فهرست فازها هشدار نمی‌داد که بعضی dependencyها خروجی آینده‌اند یا شناسه‌های U/C تأیید نشده‌اند. | قوانین ایمنی snapshot، مسیرهای تاریخی v1.8، auth هدف و نحوهٔ برخورد با پیش‌نیاز مفقود اضافه شد. |
| `ai/phases/P0_prep_style_gate.md` | وابستگی به `ai/FILE_MAP.md`, `ai/DECISIONS.md`, `ai/SESSION_LOG.md`, `ai/tools/check_state.py` و `ai/specs/*` غایب؛ فرض وجود «۱۱ تعارض»؛ ارجاع مبهم به `docs/pedagogy/01`; اتکای بیش از حد به IDهای تصمیم بدون منبع؛ prompt سبک بصری نیز به‌اشتباه Pofi/3D و یک حالت احساسی ممنوع را پیش‌فرض گرفته بود. | مسیر ساختار جاری تصحیح شد؛ taskها ایجاد فایل‌های واقعاً لازم را مشخص می‌کنند؛ دادهٔ گمشده فرض نمی‌شود؛ IDهای بدون منبع `UNVERIFIED/OPEN` هستند؛ خروجی P0 بدون State tool نیز قابل hand-off است؛ Style Gate از پیش‌فرض Pofi/3D به مقایسهٔ بی‌طرف بر اساس manifest و فایل‌های تأییدشده تغییر کرد. |
| `ai/phases/P1_design_system_foundation.md` | به `ai/specs/UX_PRINCIPLES_4_7.md` اشاره می‌کرد که نه موجود است و نه در task قبلی ساخته می‌شد. | از دو سند آموزشی واقعاً موجود استفاده می‌کند و می‌گوید خروجی‌های P0 باید پیش از استفاده وجود داشته باشند. gate با U-ID تأییدنشده هم صریح شد. |
| `ai/phases/P2_app_shell_auth_local.md` | اگر `data/screens_v2.json` ساخته نشده بود، خطر استفاده از رجیستری v1.8 و روش‌های ورود قدیمی وجود داشت. | اجرای P2 مشروط به خروجی P0/P1 شده؛ استفاده از v1.8 به‌جای رجیستری مشتق‌شده ممنوع است؛ phone + OTP تصریح شده است. |
| `ai/phases/P4_learning_engine_path.md` | سند challenge روکا را در `design/characters/roka_fox/` می‌خواست، اما آن فایل نه موجود بود نه خروجی فاز قبلی. | فایل به‌عنوان draft خروجی خود task تعریف شده و باید موارد بدون منبع `DESIGN/OPEN` بمانند تا تأیید شود. |
| `ai/phases/P6_characters_avatar.md` | به review غایب و metadata/کد Dart ناسازگار اشاره می‌کرد و ابزارهای قدیمی را بخشی از مسیر فعال جلوه می‌داد. | پیش‌نیاز فعلی به manifest صفحه‌محور و تصاویر واقعی محدود شد؛ pipeline قدیمی به legacy منتقل شد؛ نقش شخصیت‌ها نیازمند سند واقعی یا تأیید مالک است. |
| `ai/phases/P10_post_v1_backlog.md` | عبارت «optional email login» با احراز هویت phone + OTP تناقض داشت. | به «optional profile email» تبدیل شد؛ ورود با ایمیل صریحاً ممنوع است و ایمیل فقط در تنظیمات/تکمیل پروفایل و با درخواست مالک مطرح می‌شود. |
| `docs/qa/QA_DISPOSITION_FA.md` | یک ردیف QA دربارهٔ رضایت S03b/c/e می‌توانست ورود ایمیل/Google را همچنان معیار current نشان دهد. | یادداشت تاریخی شد؛ S03c/S03e برای محصول هدف حذف/غیرفعال‌اند و رضایت مسیر OTP باید در spec جاری تعیین شود. شمارش 58/359 نیز baseline prototype نامیده شد. |
| `docs/pedagogy/README.md` | `ai/DECISIONS.md` را مرجع بالادستی معرفی می‌کرد، در حالی که وجود نداشت. | مرجع تصمیم را مشروط به ledger واقعی کرد و U/C/OQ را به‌تنهایی مدرک تأیید ندانست. |
| `docs/research/START_CURRENT_FA.md` | می‌گفت ممیزی‌های v17 در `archive/research_v17/` هستند، اما پوشه وجود ندارد. | ارجاع شکسته اصلاح شد و پژوهش موجود به‌صراحت معرفی شد. |
| `prototypes/ux_react/README_FA.md` | آمار registry را «فعلی» می‌نامید و دربارهٔ S03c/e هشدار نداشت. | شمارش به baseline prototype نسبت داده شد و روش ورود قدیمی از هدف محصول جدا شد. |
| `docs/spec_sources_v1_8/00_HISTORICAL_PATHS_NOTICE_FA.md` و تمام فایل‌های منبع v1.8 | برخی source docs مسیرهای قدیمی (`canonical/`, `prototype_source/`, `design_handoff/`) و عباراتی مانند «منبع حقیقت/فعلی» دارند. | به هر سند تاریخی banner افزوده شد؛ متن تاریخی حفظ شد و یک notice مسیرها/ادعاهای قدیمی را توضیح می‌دهد. |
| `docs/pedagogy/01_master_educational_spec_v2.md` و `02_stage_content_design_guide_fa_v2.md` | به یکدیگر با نام‌های متفاوت و غیرموجود لینک می‌دادند. | ارجاع‌های داخلی به نام دقیق فایل موجود اصلاح شد. |
| `packages/ava_ui_assets/legacy/character_pipeline/` | چند فایل Dart و ابزار اجرایی بر `assets/characters/manifest.json` و مسیرهای `ava/body/` / `friends/<id>/` تکیه داشتند؛ تصاویر فعلی و manifest ریشه قرارداد دیگری دارند و برنامه‌ای آن‌ها را import نمی‌کند. | کل زنجیره به پوشهٔ `legacy/` منتقل و با README صریحاً غیرقابل‌اجرا علامت‌گذاری شد؛ فاز P6 از گسترش مستقیم آن منع شد. حذف فیزیکی نهایی منوط به تصمیم مهاجرت شخصیت‌هاست. |

## فایل‌های خط لولهٔ قدیمی که از مسیر فعال خارج شدند

این ۱۳ فایل در ساختار فعال ناسازگار بودند و به‌جای حذف بی‌بازگشت، زیر `packages/ava_ui_assets/legacy/character_pipeline/` بایگانی شدند. برای دیدن دلیل ناسازگاری و شرط بازیابی، README همان پوشه را بخوانید.

| مسیر قبلی | مسیر بایگانی |
|---|---|
| `packages/ava_ui_assets/metadata/characters.json` | `legacy/character_pipeline/metadata/characters.json` |
| `packages/ava_ui_assets/metadata/asset_notes.json` | `legacy/character_pipeline/metadata/asset_notes.json` |
| `packages/ava_ui_assets/tools/build_manifest.py` | `legacy/character_pipeline/tools/build_manifest.py` |
| `packages/ava_ui_assets/tools/character_check.py` | `legacy/character_pipeline/tools/character_check.py` |
| `packages/ava_ui_assets/tools/import_art.py` | `legacy/character_pipeline/tools/import_art.py` |
| `packages/ava_ui_assets/tools/validate_package.py` | `legacy/character_pipeline/tools/validate_package.py` |
| `packages/ava_ui_assets/lib/characters/character_models.dart` | `legacy/character_pipeline/lib/characters/character_models.dart` |
| `packages/ava_ui_assets/lib/characters/characters.dart` | `legacy/character_pipeline/lib/characters/characters.dart` |
| `packages/ava_ui_assets/lib/characters/companion_view.dart` | `legacy/character_pipeline/lib/characters/companion_view.dart` |
| `packages/ava_ui_assets/lib/characters/friend_view.dart` | `legacy/character_pipeline/lib/characters/friend_view.dart` |
| `packages/ava_ui_assets/lib/characters/loading_ava.dart` | `legacy/character_pipeline/lib/characters/loading_ava.dart` |
| `packages/ava_ui_assets/test/characters_test.dart` | `legacy/character_pipeline/test/characters_test.dart` |
| `packages/ava_ui_assets/integration/flutter/pubspec_assets_snippet.yaml` | `legacy/character_pipeline/integration/flutter/pubspec_assets_snippet.yaml` |

## حذف انجام‌شده

| مسیر حذف‌شده | دلیل | بررسی وابستگی |
|---|---|---|
| `prototypes/legacy/OPEN_FIRST_legacy.html` | صفحهٔ راه‌انداز قدیمی، صریحاً برچسب Legacy؛ شامل خلاصه‌هایی است که در READMEهای جاری وجود دارند و به اسناد تاریخی لینک می‌دهد. | جست‌وجوی ارجاع فعال انجام شد؛ به‌عنوان ورودی، import یا لینک جاری استفاده نمی‌شد. حذف شد. |

## فایل‌هایی که عمداً حذف نشدند

1. **`AGENTS.md`, `CLAUDE.md`, `GEMINI.md`, `.cursorrules`:** مشابه بودنشان تکرار غیرضروری معمول نیست؛ فایل ورودی ابزارهای مختلف‌اند. هرکدام باید مستقل باقی بماند و متن مشترکشان حالا صحیح شده است.
2. **`data/screens_v18.json` و `prototypes/ux_react/src/generated/screens.json`:** محتوا یکسان است، اما دومی خروجی generated مورد استفادهٔ prototype است. حذف دومی prototype را ناقص می‌کند؛ فقط از طریق `prototypes/ux_react/tools/sync_registry.py` تولید شود.
3. **`docs/spec_sources_v1_8/`:** محتوایش قدیمی است و درونش path و auth legacy وجود دارد، اما P0 و راهنمای ساختار دستور می‌دهند منابع نسخه‌دار حفظ شوند. به‌جای حذف/بازنویسی، هشدار تاریخی اضافه شد و spec جاری باید در `docs/spec/` ساخته شود.
4. **`data/baseline/b/` و copy/meta قدیمی:** این‌ها ورودی‌های baseline و منبع مهاجرت‌اند. رشته‌های «ورود با ایمیل» و «سلام من آوام» در منبع به‌تنهایی مجوز پیاده‌سازی نیستند؛ P0 باید نسخهٔ مشتق‌شده بسازد و منبع را دست‌نخورده نگه دارد.
5. **READMEهای `apps/mobile/`, `apps/admin/`, `services/api/`, `data/curriculum/` و `docs/spec/`:** placeholder هستند، ولی وجودشان هدف پوشه را روشن می‌کند و به‌خودی‌خود منسوخ نیستند.
6. **`prototypes/ux_react/vendor/shadcn/`:** کد همراه prototype است؛ نبودن `package.json` در ریشهٔ React به معنی زائد بودن اجزای vendor نیست.

## وضعیت باقیمانده؛ عمداً حل‌نشده

- هفت مسیر تصویری در `packages/ava_ui_assets/manifest.json` هنوز به فایل‌های غایب اشاره دارند. فایل جعلی ساخته نشده و آن مسیرها حذف نشده‌اند، چون به تأیید/تصمیم منبع نیاز دارند. جزئیات در `docs/maintenance/STRUCTURE_VALIDATION_FA.md`.
- `data/screens_v18.json` و کد React هنوز baseline/نمایش قدیمی را نگه می‌دارند؛ آن‌ها عمداً پاک‌سازی نشده‌اند تا منبع prototype از بین نرود. target رجیستری باید در P0 مشتق شود.
- هیچ‌یک از این تغییرات اپ Flutter، backend، پنل یا prototype را به پروژهٔ اجرایی تبدیل نمی‌کند. این ممیزی مسیرهای متنی و حذف‌های امن را هدف می‌گیرد.


## مواردی که عمداً حذف نشدند

- چهار فایل bootstrap ابزارهای AI شبیه‌اند، اما برای ابزارهای مختلف لازم‌اند؛ یکی‌کردن آن‌ها خطر از دست رفتن دستور شروع در یک ابزار را دارد.
- `data/screens_v18.json` و نسخهٔ generated آن به لحاظ محتوا تکرارند، اما رابطهٔ source/generated لازم است؛ فقط source دستی ویرایش و خروجی با اسکریپت تولید شود.
- اسناد v1.8، baseline آموزشی، QA، changelog و فایل‌های review نگه داشته شدند چون تاریخچه یا ردیابی دارایی/تصمیم را حفظ می‌کنند. هشدارهای تاریخی افزوده شدند، نه اینکه سابقه حذف شود.
- فایل‌های manifest که به assets گمشده اشاره دارند حفظ شدند تا نیاز طراحی/تأیید از دید پنهان نشود؛ فایل جعلی ساخته نشده است.

## مورد نیازمند تصمیم مالک

زنجیرهٔ `legacy/character_pipeline/` اکنون از مسیر فعال جدا شده، اما هنوز تصمیم نهایی لازم است: آیا مدل قبلی شخصیت‌ها باید کامل حذف شود، یا از آن بخش‌هایی پس از طراحی schema جدید مهاجرت داده شوند؟ تا آن تصمیم، آن را اجرا نکنید.
