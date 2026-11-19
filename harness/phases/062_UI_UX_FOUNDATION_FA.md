# فاز ۰۶۲ — Foundation UI و اجزای مشترک AVA

## هدف
اتصال دیزاین‌سیستم R01 به مسیر واقعی Compose، بدون تغییر رفتار محصول یا جریان‌های یادگیری.

## مرز فاز
- داخل: توکن‌ها، کامپوننت‌های مشترک، wrapperهای صفحه، artwork و manifest، نگاشت منابع Android، گالری و تست‌های UI، مستندات.
- خارج: تغییر route/state، احراز هویت واقعی، مدل حساب، CMS/network، curriculum، LessonDetails، Lesson، StoryQuestion و Practice rendererها.
- صفحه‌های پیرامونی در برش‌های بعدی، جداگانه و از روی inventory با شناسه‌های U01–U100 مهاجرت می‌کنند.

## روش اجرا
1. پیش از جایگزینی یک wrapper، قرارداد API و رفتار فعلی آن را حفظ کن؛ screen APIهای موجود نباید بی‌دلیل تغییر کنند.
2. کامپوننت جدید باید stateهای enabled/disabled/selected/loading مرتبط، هدف لمسی، semantics و حالت RTL را تعریف کند.
3. artwork باید در design/assets.manifest.json ثبت شود؛ فایل public منبع حقیقت است و drawable/Art mapping فقط از generator رسمی یا همان اجرای تأییدشدهٔ آن تولید شود.
4. VectorDrawableها باید ابعاد intrinsic و viewport برابر با manifest داشته باشند. audit باید VectorDrawable را parse کند و rasterها را همچنان با Pillow بررسی کند.
5. گالری باید حالت‌های مهم هر component را با متن فارسی و RTL نمایش دهد؛ برای wrapperهای legacy تست رگرسیون بگذار.
6. هیچ image reference، content ID، route، reducer، permission یا flow احراز هویت در این فاز تغییر نکند.

## خروجی قابل قبول
- shared wrappers به componentهای R01 متصل شده باشند.
- artwork و generated drawable از نظر byte و ID مطابق manifest باشند.
- تست regression در سورس وجود داشته باشد.
- inventory، Design System، PROGRESS و WORKLOG با وضعیت دقیق به‌روز شوند.
- اجرای همهٔ گیت‌ها در task ثبت و تا قبل از پاس شدن، task in_progress بماند.

## ملاحظات
- imagegen یا asset جدید نباید جایگزین چهار شخصیت مصوب شود؛ تصویر تزئینی جدید mascot تازه محسوب نشود.
- وزن فونت یا dependency جدید بدون فایل رسمی، مجوز و approval وارد نشود.
- screenshot محلی یا Robolectric برابر QA دستگاه واقعی نیست.