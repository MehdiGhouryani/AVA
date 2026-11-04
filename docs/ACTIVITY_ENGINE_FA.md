# موتور فعالیت v3 — قرارداد ۰٫۶٫۰

## هدف

یک Lesson باید بتواند نوع‌های تازهٔ تمرین را بدون افزودن route یا وابستگی UI به CMS/URL اجرا کند. `TaskKind` محتوایی است، `ActivityRegistry` رفتار را تعیین می‌کند، `ExerciseReducer` گذار state را مالک است و renderer فقط از تعریف registry استفاده می‌کند.

## registry

| خانواده | نوع‌ها | تعامل | hint | صوت |
|---|---|---|---|---|
| دیداری | Recognize، DotFamily، PositionalForm، **Find** | انتخاب واحد | خیر | اختیاری |
| دیداری | **Match** | جفت‌یابی (`PairMatch`) | خیر | خیر |
| شنیداری | Listen، InitialSound، FinalSound، **Bubble** | انتخاب واحد | بله | اختیاری در اجرا؛ ممکن است برای publication الزامی باشد |
| ساخت واژه | BuildWord، Segment، Blend | token + undo | خیر | در این برش خیر |

افزودن `TaskKind` بدون تعریف registry خطای compile ایجاد می‌کند، چون `when` exhaustive است. generator و schema نیز shape داده را کنترل می‌کنند: فعالیت انتخابی choices می‌خواهد و فعالیت tokenی tokens.

## نوع‌های Find، Bubble و Match (۰٫۶٫۲ پیش‌نویس)

- **Find** و **Bubble** همان قرارداد انتخاب سه‌گزینه‌ای را دارند؛ فقط renderer متفاوت می‌شود (صحنه با هدف‌های بزرگ؛ حباب‌های شناور). Find مثل Recognize `writtenFallback == target` دارد؛ Bubble مثل Listen برای انتشار صوت لازم و hint نوشتاری دارد.
- **Match**: `tokens` فهرست زوج‌هاست و `tokens[2i]` با `tokens[2i+1]` جفت است (۴ تا ۸ کارت، همه یکتا)؛ `target` باید دقیقاً `الف=ب|پ=ت|…` باشد. `ExerciseReducer.flip` کارت را برمی‌گرداند، جفت‌شدن را در `matched` نگه می‌دارد، و ناجور بودن را در `mismatch` (UI آن را پس از ۹۰۰ms با `clearMismatch` پاک می‌کند). اشتباه شمرده نمی‌شود (`wrongCount` دست‌نخورده) و با جفت‌شدن همهٔ کارت‌ها `checked=correct=true` می‌شود. `matched/pending/mismatch` در snapshot ذخیره نمی‌شوند؛ پس از بازگشت بازی از اول شروع می‌شود.
- **Match در محتوای منتشرشده ممنوع است تا renderer روی دستگاه تأیید شود**: renderer نوشته شده (`MatchTask` در `LessonScreens.kt`، `flip`/`clearMismatch` در ViewModel) ولی کامپایل و اجرا نشده؛ `validate-content-contract.py` تا آن زمان آن را رد می‌کند (`unrendered_kinds`). پس از فاز ۱ و QA دستگاه آن قید را بردار.
- **Renderer‌ها:** Find با سه کاشی صحنه‌ای ≥۱۱۲dp؛ Bubble با حباب‌های ۹۶dp که فقط حدود ۵dp بر ثانیه بالا و پایین می‌روند و در کاهش حرکت ثابت‌اند؛ Match با کارت‌های رو (نه حافظه‌ای): دو کارت جفت را بزن، جفت می‌ماند و ناجور پس از ۹۰۰ms با حلقهٔ کهربایی و آیکون برگشت پاک می‌شود. شمارش اشتباه و زمان‌سنج ندارد.
- آزمون: `python3 scripts/test-activity-kinds.py` (schema و validatorها)، `scripts/jvm-core-check.py` (reducer).

## افزودن نوع فعالیت تازه — چک‌لیست

۱. `TaskKind` در `Models.kt`؛ ۲. `ActivityRegistry.kt` (interaction، family، hint، audio)؛ ۳. در صورت لزوم `ExerciseReducer`؛ ۴. `design/content.schema.json` (enum یا تعریف تازه)؛ ۵. مجموعه‌های kind در `validate-content-contract.py` و `validate-audio-inventory.py`؛ ۶. نگاشت و اعتبارسنجی در `sync-android-resources.py`؛ ۷. برچسب در `export-educator-pack.py`؛ ۸. تست registry/reducer و یک مورد در `test-activity-kinds.py`؛ ۹. renderer؛ ۱۰. این سند.

## قواعد رشد محتوا

- `migrate-content-v2-to-v3.py --check` دیگر برابری کامل نمی‌خواهد: هرچه از v2 فریز‌شده بوده باید با همان شناسه و مقدار در `content.fa.json` بماند، ولی واحد، درس، فعالیت، مهارت و قصهٔ تازه مجاز است.
- تست‌های Kotlin شمارش ثابت ندارند؛ شناسه‌های فریزشده و ترتیبشان، یکتایی و ناوردایی‌ها را می‌سنجند.

## state و نشست

`Exercise` تنها state جاری renderer است. `SessionSnapshot` نسخه ۱، index، انتخاب، شناسهٔ index tokenها، hint، checked و complete را encode می‌کند. restore ورودی خراب را رد می‌کند، اندیس را clamp می‌کند، انتخاب‌های حذف‌شده را کنار می‌گذارد و `correct` را از محتوای فعال دوباره محاسبه می‌کند؛ در نتیجه correctness از فایل ذخیره‌شده اعتماد نمی‌شود.

ViewModel برای هر درس یا صف مرور یک owner پایدار می‌سازد. snapshot بعد از هر انتخاب/hint/check/advance در `SavedStateHandle` نوشته و بعد از پایان یا reset پاک می‌شود. Learner همچنان active lesson/index را در DataStore نگه می‌دارد؛ snapshot پاسخ نیمه‌تمام را برای recreation نگه می‌دارد.

## صوت قابل تزریق

`AudioRequest` شامل `AudioRef?`، متن fallback و purpose است. UI نام فایل، URL، storage key یا SDK CMS را نمی‌شناسد. `AudioPlayback` دو عمل `play/stop` دارد و نتیجه PlayedLocal، PlayedFallback یا Missing است. implementation فعلی Android فقط voice فارسی نصب‌شده و آفلاین را به‌عنوان fallback اجرا می‌کند؛ `FakeAudioPlayback` تست را بدون Android/TTS ممکن می‌کند.

مسیر آینده بدون تغییر قرارداد UI:

`CMS خصوصی → فایل و metadata/QC → compiler → ContentRelease امضاشده → asset resolver محلی → AudioPlayback`

پخش فایل انسانی و resolver bundle هنوز پیاده نشده‌اند. صوت لازم publication باید در inventory وضعیت approved و hash معتبر داشته باشد؛ TTS جای تأیید آموزش و تلفظ را نمی‌گیرد.

## قواعد ایمنی آموزشی

- نبود صوت، درس را مسدود نمی‌کند و فعالیت شنیداری hint نوشتاری دارد.
- پاسخ غلط قابل اصلاح است و جهش بدون پاسخ کامل ممکن نیست.
- token تکراری با index هویت مستقل دارد و undo اجباری نیست.
- type تازه به معنی محتوای تأییدشده نیست؛ متن/ترتیب/تلفظ جداگانه بازبینی می‌شوند.
- URL/CMS record داخل Task ذخیره نمی‌شود؛ فقط ref پایدار وارد release می‌شود.

## شواهد و محدودیت

تست‌سورس ۰٫۶٫۰ پوشش registry همه ۹ type، reducer دو interaction، hint، snapshot خراب/stale، متن فارسی، token تکراری، audio fake، recreation ViewModel و renderer نماینده را اضافه می‌کند. در محیط فعلی auditهای deterministic موفق‌اند، ولی compile/اجرای ۱۰۳ تست و QA دستگاه هنوز انجام نشده است.
