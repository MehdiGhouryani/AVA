> **هشدار نسخهٔ تاریخی:** این سند بازتاب‌دهندهٔ بستهٔ v1.8 در زمان خودش است. عبارت‌هایی مانند «فعلی/منبع حقیقت»، تصمیم‌های محصول و مسیرهای فایل در بدنه فقط نسبت به همان snapshot معتبرند؛ آن‌ها را به ساختار/تصمیم جاری تعمیم ندهید. مسیرها و وضعیت جاری در `docs/PROJECT_STRUCTURE_FA.md` و `AI_START_HERE.md` است؛ قبل از اجرا، تعارض‌ها را با منابع موجود و دستور مالک بررسی کنید. جزئیات: `00_HISTORICAL_PATHS_NOTICE_FA.md`.

# ۰۸: تغییرات و تصمیم‌های طراحی

بازطراحی کامل UI در همین گفتگو تأیید شد؛ این تأیید، مجوز انتشار یا تصویب محتوای فارسی نیست.

CR-UI-01: شروع کودک‌محور و حذف «والد عزیز»، با حفظ auth/consent بزرگ‌تر.
CR-UI-02: static PNG به Companion stateful SVG؛ assetهای جدید مستقل از دولینگو.
CR-UI-03: کتاب‌باغ مینیمال و مسیر روشن، سه تب و پنل والد جدا؛ ترتیب آموزشی تغییر نکرد.
CR-UI-04: S15 به سازندهٔ آواتار آزاد تبدیل شد. surface خرید با سکهٔ قبلی در prototype عرضه نمی‌شود؛ coin ledger baseline دست‌نخورده است. پیش از migration تولیدی، این پیشنهاد UI باید با اقتصاد baseline تطبیق/قفل شود؛ حذف داده یا تغییر reward amounts انجام نشده است.
CR-UI-05: برگ کتاب نمایش reward است، نه ارز جدید؛ جشن کوتاه/قابل عبور، بدون auto-home ناخواسته.
CR-UI-06: CMS ده‌ماژولی به desktop workspace تبدیل شد؛ داده/عملیات واقعی ساخته نشده است.
CR-UI-07: متن‌های فنی ممیزی بیرون از تمرین کودک و در review studio قرار گرفتند.

A/C و gate puzzle پنهان ماندند. u00 v2/S31/S33 محتوا همچنان pending هستند. هیچ فایل baseline، قیمت، entitlement یا دادهٔ آموزشی تغییر نکرد.


CR-CLEANUP-01: تمام artworkهای قبلی UI/character و خروجی‌های تصویری از package حذف شدند؛ prototype به asset-slot خنثی برگشت تا طراحی بصری از صفر ساخته شود.
CR-CLEANUP-02: انتخاب رده‌بندی سنی از onboarding/profile حذف شد؛ مخاطب کل محصول در این مرحله ۴ تا ۷ سال ثابت است.

CR-CLEANUP-03: baseline current به single-source نزدیک شد: `canonical/screens_v18.json` منبع رجیستر و `prototype_source/src/generated/screens.json` خروجی مشتق‌شده است؛ state CSV نیز خروجی handoff محسوب می‌شود. بلوک‌های تکراری `baseline_contract` از screen registry حذف شدند.
CR-CLEANUP-04: S25 «تغییر گروه سنی» و ۶ state آن از current خارج شد؛ محصول دیگر route یا UI تغییر رده‌بندی سنی ندارد.
CR-CLEANUP-05: ممیزی‌ها و ابزارهای v17/legacy به `archive/` منتقل شدند تا با current source قاطی نشوند.
CR-CLEANUP-06: copyهای group A/B/C و change-group از baseline active حذف و copy canonical «مناسب ۴ تا ۷ سال» اضافه شد.

CR-CHARACTERS-01: بستهٔ شخصیت‌ها (آوا، پوفی، دانا، فندق، روکا) با baseline تمیز ادغام شد. character sheetها در `design_handoff/characters/` و ساختار/manifestهای تولید در `design_handoff/character_assets/` قرار گرفتند و دو ریشهٔ جداگانهٔ بستهٔ شخصیت‌ها یکی شد. این sheetها مرجع طراحی‌اند، نه asset تأییدشده؛ هیچ فایل baseline آموزشی، screen registry یا state contract تغییر نکرد.
CR-CHARACTERS-02: ساختار SVG نمونهٔ v2 (فقط اسکلت خالی) با master و stateهای کامل جایگزین شد. master v3 و ۵ state آوا (idle, happy, welcome, explaining, celebration) از روی character sheet ساخته شد و `character_assets/_tools/` شامل generator و validator است. v2 در `ava/source/archive/` نگه‌داری شد. این assetها پیش‌نویس‌اند و تأییدنشده؛ به prototype وصل نشده‌اند.
DECISION-ROLES-01: آوا شخصیت اصلی برند است و داخل مراحل درس ظاهر نمی‌شود؛ پوفی، دانا، فندق و روکا داخل مراحل می‌آیند و نقش درون‌مرحله‌ای دارند.
CR-CHARACTERS-03: master و stateهای SVG برای پوفی (idle, happy, retry, cheering)، دانا (idle, happy, showing_path, storytelling)، فندق (idle, happy, star, reward) و روکا (idle, challenge, question, success) از روی character sheetها ساخته شد. توابع مشترک در `_tools/svg_lib.py`؛ validator برای همهٔ شخصیت‌ها عمومی شد و اشکال‌های پیدا‌شده (idهای تکراری در variantها) برطرف شد. پیش‌نویس‌اند و تأییدنشده.
CR-CHARACTERS-05: فندق (دم بلند پشمی با مارپیچ، عنبیهٔ درشت‌تر) و روکا (کاکل شعله‌ای، دم زیگزاگ‌سفید، سردست پشمی، اصلاح پلک) به‌روزرسانی شدند. نام فایل‌ها و لایه‌ها تغییر نکرد.
CR-CHARACTERS-06: دم فندق از نو ساخته شد (شکل S با نوک مارپیچی، سایهٔ تدریجی لایه‌ای، خطوط پشم). نام فایل‌ها و لایه‌های اصلی تغییر نکرد؛ لایه‌های دم به tail.base/shade/inner/fur/swirl تغییر کرد.
CR-CHARACTERS-07: تکمیل حالت‌ها: ۶۷ حالت باقی‌مانده ساخته شد (آوا ۱۶، پوفی ۱۴، دانا ۱۳، فندق ۱۱، روکا ۱۳) و اکنون همهٔ حالت‌های فهرست‌شده در spec برای هر پنج شخصیت وجود دارد (جمعاً ۸۸ فایل SVG). طراحی شخصیت‌ها و ۲۱ فایل stateٔ قبلی بدون هیچ تغییری (بایت‌به‌بایت) ماندند؛ masterها فقط لایه‌های مخفی اشیا را گرفتند و رندرشان پیکسل‌به‌پیکسل مثل قبل است. اشیا در `_tools/svg_kit.py` و پس‌پردازش در `_tools/svg_post.py`؛ نگاشت حالت‌ها در `_tools/new_states.py`. حالت‌های with_ava و with_team با شخصیت‌های همراه (با idهای namespace‌شده) ساخته شدند. دو فایل spec برای فندق و روکا یک حالت کمتر از هدف فهرست کرده‌اند و تصمیم دربارهٔ حالت گم‌شده با صاحب محصول است.
CR-CHARACTERS-08: آوا (SVG v3، ۲۱ state و شش کاندیدای تخت) و دانا (هدهد، ۱۷ state) به درخواست صاحب محصول فعلاً از package حذف شدند: پوشه‌های character_assets/ava و dana، sheetهای مرجع companion_ava و dana_hoopoe، سازنده‌ها و حالت‌های آن‌ها در new_states، و pofi_with_ava (چون آوا را درون فایل می‌گذاشت). پوفی، فندق و روکا بدون هیچ تغییر دیگر ماندند. شخصیت اصلی برند و آیکون اپ تعیین‌نشده است. متن‌های prototype، screens و copy که نام آوا را دارند (آوا نام محصول هم هست) دست‌نخورده ماندند.
DECISION-FORMAT-01: شخصیت اصلی برند SVG نمی‌شود؛ برای زیبایی بیشتر به‌صورت رندر راستری با خروجی WebP (شفاف) ساخته می‌شود و آیکون اپ جدا و به‌صورت PNG ساخته می‌شود. شخصیت‌های داخل مراحل (پوفی، فندق، روکا) همچنان SVG لایه‌بندی‌شده‌اند. شخصیت اصلی برند هنوز انتخاب نشده است؛ همهٔ نمونه‌های پیشنهادی ردشد و بیرون از package ماندند.
CR-PACKAGE-FINAL: بستهٔ تحویلی فقط شامل سه شخصیت پوفی (خرگوش)، فندق (سنجاب) و روکا (روباه) است؛ هیچ نمونهٔ پیشنهادی دیگر (تخت یا WebP) در آن نیست.
