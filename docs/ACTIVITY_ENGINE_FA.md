# موتور فعالیت v3 — قرارداد ۰٫۶٫۰

## هدف

یک Lesson باید بتواند نوع‌های تازهٔ تمرین را بدون افزودن route یا وابستگی UI به CMS/URL اجرا کند. `TaskKind` محتوایی است، `ActivityRegistry` رفتار را تعیین می‌کند، `ExerciseReducer` گذار state را مالک است و renderer فقط از تعریف registry استفاده می‌کند.

## registry

| خانواده | نوع‌ها | تعامل | hint | صوت |
|---|---|---|---|---|
| دیداری | Recognize، DotFamily، PositionalForm | انتخاب واحد | خیر | اختیاری |
| شنیداری | Listen، InitialSound، FinalSound | انتخاب واحد | بله | اختیاری در اجرا؛ ممکن است برای publication الزامی باشد |
| ساخت واژه | BuildWord، Segment، Blend | token + undo | خیر | در این برش خیر |

افزودن `TaskKind` بدون تعریف registry خطای compile ایجاد می‌کند، چون `when` exhaustive است. generator و schema نیز shape داده را کنترل می‌کنند: فعالیت انتخابی choices می‌خواهد و فعالیت tokenی tokens.

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
