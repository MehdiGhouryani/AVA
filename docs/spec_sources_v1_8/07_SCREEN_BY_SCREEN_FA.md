> **هشدار نسخهٔ تاریخی:** این سند بازتاب‌دهندهٔ بستهٔ v1.8 در زمان خودش است. عبارت‌هایی مانند «فعلی/منبع حقیقت»، تصمیم‌های محصول و مسیرهای فایل در بدنه فقط نسبت به همان snapshot معتبرند؛ آن‌ها را به ساختار/تصمیم جاری تعمیم ندهید. مسیرها و وضعیت جاری در `docs/PROJECT_STRUCTURE_FA.md` و `AI_START_HERE.md` است؛ قبل از اجرا، تعارض‌ها را با منابع موجود و دستور مالک بررسی کنید. جزئیات: `00_HISTORICAL_PATHS_NOTICE_FA.md`.

# ۰۷: بازطراحی صفحه‌به‌صفحه

۵۸ رکورد، ۳۵۸ state اصلی؛ صفحات آینده و پنجره‌ها جدا از route فعال V1 هستند. طرح دقیق بصری در PNG و گالری همراه است. نمونهٔ review جای engine تولیدی نیست.


## S01: Splash

**مخاطب:** child؛ **V1:** فعال/در دامنه

**هدف:** اجرای اول/بازگشت و bootstrap محلی بدون انتظار شبکه

**ساختار تازه:** لوگو، کتاب باز همراه، نشان آماده‌سازی آرام؛ CTA فقط در دفتر طراحی برای عبور دستی.

**ورود:** اجرای اپ

**خروج:** S02 در first-run؛ S06-B/ S22 در بازگشت

**تعامل:** لوگوی proposed/non-final فقط هویت؛ timeout یا درصد جعلی ممنوع

**همراه:** read؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** first_load, returning, offline_load

**پذیرش تولیدی:** بدون شبکه مقصد محلی ≤۱٫۵ث و white flash صفر.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ auth/session و local DB؛ تأیید حقوقی copy

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S02: Onboarding سه‌صحنه‌ای

**مخاطب:** child؛ **V1:** فعال/در دامنه

**هدف:** معرفی سه‌صحنه‌ای و آماده‌سازی ورود والد

**ساختار تازه:** سه صحنهٔ متفاوت: سلام آوا، باغ کتابی، دوستان؛ روایت/بازپخش، next و skip؛ کودک محور.

**ورود:** S01

**خروج:** S03a

**تعامل:** next/skip/back؛ روایت auto-advance ندارد؛ swipe و دکمه هر دو

**همراه:** welcome؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** scene1, scene2, scene3, narrating, idle, skipped

**پذیرش تولیدی:** هر سه صحنه با TalkBack و reduce-motion قابل عبور؛ skip در هر صحنه.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ auth/session و local DB؛ تأیید حقوقی copy

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** artwork قدیمی پاک شده؛ فقط character sheet مرجع در `design_handoff/characters/` هست و asset تولیدی هنوز تعریف نشده است. قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S04: پروفایل کودک

**مخاطب:** child؛ **V1:** فعال/در دامنه

**هدف:** ساخت پروفایل کودک برای بازهٔ ثابت ۴ تا ۷ سال؛ هیچ انتخاب رده یا گروه سنی در رابط وجود ندارد

**ساختار تازه:** پیش‌نمایش همراه و سه انتخاب رنگ؛ بازهٔ سنی فقط به‌صورت متن ثابت «مناسب ۴ تا ۷ سال» نمایش داده می‌شود.

**ورود:** auth/افزودن profile

**خروج:** شروع دانلود پس‌زمینه و S05؛ سپس S22 فقط اگر بستهٔ لازم آماده نیست

**تعامل:** نام ۱–۲۰ نویسه فارسی/لاتین/فاصله، بدون emoji؛ سقف ۳ profile

**همراه:** welcome؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** empty, typing, invalid_name, complete, saving, storage_low

**پذیرش تولیدی:** ادامه فقط نام معتبر؛ برگشت داده را حفظ کند؛ profile چهارم رد شود.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ auth/session و local DB؛ تأیید حقوقی copy

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S22: آماده‌سازی/دانلود بسته

**مخاطب:** child؛ **V1:** فعال/در دامنه

**هدف:** پیش‌نیاز، دریافت، verify و نصب اتمیک بسته/ادامه آفلاین

**ساختار تازه:** همراه منتظر، کارت بسته و progress fixture؛ بازی آماده و کمک بزرگ‌تر؛ دانلود واقعی نیست.

**ورود:** S04/S05/parent action

**خروج:** S05 یا S06-B یا مقصد درخواست

**تعامل:** Range/resume؛ checksum؛ required bytes؛ cancel فقط والد؛ صفحه در صورت آماده‌بودن flash نشود

**همراه:** wait؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** preflight, manifest, downloading, paused, resume, verify, installed, low_storage, offline, error

**پذیرش تولیدی:** قطع در ۵۰٪ و بازگشایی همان byte؛ فضا=Σbytes×۲+۵۰MB؛ خطا مسیر retry دارد.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ manifest/hash/Range/atomic install؛ فضای موردنیاز Σbytes×۲+۵۰MB و سیاست شبکه

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S03a: ورود والد: موبایل/گوگل/ایمیل

**مخاطب:** parent؛ **V1:** فعال/در دامنه

**هدف:** ورود والد با موبایل/Google/ایمیل و ثبت پذیرش نسخه قوانین

**ساختار تازه:** لوگو و همراه کوچک، handoff بزرگ‌تر، رضایت، سه روش auth بدون credential در نمونه.

**ورود:** S02/S23-off

**خروج:** S03b یا S03c یا S03e

**تعامل:** شماره +۹۸ و normalize؛ terms اجباری؛ لینک UX-01

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** empty, phone, terms_unchecked, invalid, loading, provider_error

**پذیرش تولیدی:** دکمه تا پذیرش disabled؛ terms_version ذخیره؛ هیچ credential capture در demo.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ auth/session و local DB؛ تأیید حقوقی copy

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S03b: کد تأیید

**مخاطب:** parent؛ **V1:** فعال/در دامنه

**هدف:** تأیید OTP برای SMS/email

**ساختار تازه:** شش خانهٔ کد غیرقابل‌ورود؛ موفقیت/ارسال دوبارهٔ نمایشی؛ وضعیت wrong/expired/locked.

**ورود:** S03a

**خروج:** S03d یا مقصد حساب

**تعامل:** ۶ خانه از راست؛ resend ۹۰ث، expiry ۵د، ۵ تلاش؛ manual همیشه

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** empty, typing, verifying, wrong, expired, resend, locked

**پذیرش تولیدی:** rotation/background مقدار را حفظ کند؛ رقم ششم auto verify؛ خطا متن+لرزش نه رنگ تنها.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ auth/session و local DB؛ تأیید حقوقی copy

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S03c: ورود با گوگل

**مخاطب:** parent؛ **V1:** فعال/در دامنه

**هدف:** احراز Google از طریق provider رسمی

**ساختار تازه:** نمای provider، ادامهٔ نمایشی و برگشت؛ auth در سرویس امن تولیدی.

**ورود:** S03a

**خروج:** S03d/مقصد یا fallback

**تعامل:** بدون Google مسیر موبایل/ایمیل کامل؛ token فقط secure storage

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** loading, cancelled, unavailable, success, error

**پذیرش تولیدی:** cancel بی‌خطا به S03a؛ provider error با retry؛ credential هرگز log نشود.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ auth/session و local DB؛ تأیید حقوقی copy

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S03e: ورود با ایمیل

**مخاطب:** parent؛ **V1:** فعال/در دامنه

**هدف:** ورود/تأیید از کانال ایمیل

**ساختار تازه:** نمای ایمیل بزرگ‌تر، پیوند و مسیر not_received/error؛ بدون فیلد دریافت ایمیل در نمونه.

**ورود:** S03a

**خروج:** S03b با channel=email

**تعامل:** email داخل RTL به‌صورت LTR؛ trim/lowercase؛ حداکثر ۱۰۰

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** empty, typing, invalid, sent, not_received, error

**پذیرش تولیدی:** validation و hint بعد ۶۰ث؛ delivery تست و منبع رسمی لازم.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ auth/session و local DB؛ تأیید حقوقی copy

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S03d: ادغام حساب

**مخاطب:** parent؛ **V1:** فعال/در دامنه

**هدف:** ادغام حساب و پروفایل محلی بدون حذف ناخواسته

**ساختار تازه:** دو پروفایل محلی/حساب، انتخاب حفظ هر دو و انصراف؛ merge بدون حذف پنهان.

**ورود:** auth موفق با local+server

**خروج:** profile/house

**تعامل:** نام/مسیر/تعداد completion؛ add پیش‌فرض؛ حذف فقط تأیید

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** review, choose, merge, conflict, done, cancel

**پذیرش تولیدی:** مجموع profile>۳ اجازه add ندهد؛ local تا تأیید محفوظ.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ auth/session و local DB؛ تأیید حقوقی copy

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S23: دروازه والد

**مخاطب:** child؛ **V1:** پنهان

**هدف:** دروازه والد برای اقدام حساس؛ در V1 gate_mode=off و route hidden

**ساختار تازه:** صفحهٔ مرجع غیرفعال: راه بزرگ‌ترها باز است؛ بدون معما/رمز در V1.

**ورود:** surface parent action

**خروج:** مقصد والد

**تعامل:** V1 بدون puzzle و بدون parent puzzle؛ فقط تأیید ساده destructive در parent

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** off, puzzle_proposed, failed, locked, success

**پذیرش تولیدی:** در V1 هیچ gate_token؛ flavor آینده پس از ۳ خطا قفل و token پس از ۳۰ث باطل.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S18: اشتراک

**مخاطب:** parent؛ **V1:** فعال/در دامنه

**هدف:** نمایش plan و خرید/restore اشتراک از CMS/store

**ساختار تازه:** انتخاب ماهانه/سالانه، شرایط و قیمت از فروشگاه، pending، restore، بعداً؛ parent-only.

**ورود:** S20/S21

**خروج:** success→مقصد؛ failure→retry

**تعامل:** قیمت/SKU/trial/promo hardcode نیست؛ حداکثر ۳ plan؛ fake gateway فقط تست

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** loading, plans, pending, success, failure, cancelled, restore, offline

**پذیرش تولیدی:** purchase idempotent؛ cancel/restore قابل retry؛ paywall فقط پس از مسیر والد.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ BillingGateway/receipt verification/entitlement idempotency؛ CMS SKU/قانون بازار

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S19: گزارش پیشرفت

**مخاطب:** parent؛ **V1:** فعال/در دامنه

**هدف:** گزارش والد، نیاز به مرور و پیشنهاد تمرین/انتقال

**ساختار تازه:** معرفی‌شدن و تسلط جدا، سه ردیف نشانه، پیشنهاد تمرین بیرون صفحه؛ no_data بدون نمودار جعلی.

**ورود:** S15 → S20 → S19

**خروج:** S20/UX-03

**تعامل:** انتخاب child؛ اعداد فارسی؛ نمودار پیچیده نیست؛ child PII در analytics نیست

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** loading, no_data, report, needs_review, suggest_group, offline

**پذیرش تولیدی:** no_data و offline هرکدام action دارند؛ پیشنهاد خودکار نیست و کودک نمی‌بیند.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S20: تنظیمات والد

**مخاطب:** parent؛ **V1:** فعال/در دامنه

**هدف:** مرکز والد: گزارش، دانلود، پروفایل، صدا، حساب

**ساختار تازه:** فهرست والد با گزارش، صدا/حرکت، بسته‌ها، پروفایل، اشتراک، حساب، پشتیبانی و قوانین.

**ورود:** parent

**خروج:** S19/S24/S27/S18/UX-01/UX-02/UX-03

**تعامل:** session والد پس از ۵د بی‌تعامل بسته؛ گروه‌بندی روشن؛ راوی همیشه روشن

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** loaded, session_expired, offline, confirm_destructive

**پذیرش تولیدی:** هر switch label/state؛ اقدام مخرب دو تأیید؛ خروج امن.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.


## S24: دانلودها و فضا

**مخاطب:** parent؛ **V1:** فعال/در دامنه

**هدف:** مدیریت chunk و فضای ذخیره

**ساختار تازه:** بستهٔ آماده، بستهٔ درحال‌دریافت، pause/resume، گام‌های verify/install و مسیر کمبود فضا.

**ورود:** S20

**خروج:** S20

**تعامل:** pause/resume/delete old؛ هشدار chunk فعال؛ Wi-Fi preference

**همراه:** none؛ retry و پخش/جشن بر pose پایه تقدم دارند.

**حالت‌ها:** empty, active, paused, low_space, delete_confirm, offline, error

**پذیرش تولیدی:** اندازه با installer یکی؛ حذف فعال توضیح re-download؛ عملیات safe.

**وابستگی‌ها:** JSON route/state contract؛ RTL و accessibility (TalkBack، reduced-motion)؛ copy/audio parity و asset manifest؛ manifest/hash/Range/atomic install؛ فضای موردنیاز Σbytes×۲+۵۰MB و سیاست شبکه

**معیارهای عمومی:** ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، ۴۱۲×۹۱۵ و tablet با max-width ۶۰۰dp بدون بریدگی؛ هدف لمس کودک ≥۶۴dp و CTA اصلی ≥۷۲dp؛ متن child×۱٫۲ و parent×۱٫۶؛ RTL واقعی، ارقام فارسی در نمایش، TalkBack، reduced-motion و مسیر بدون dead-end

**محدودیت نمونه:** screenshot/selector rendering؛ صوت، auth، download، payment، engine آموزشی و backend واقعی متصل نیستند.

**تصویر:** `حذف شده`؛ قرارداد تمام stateها: `state_contracts_v18.json` با کلید screen_id.
