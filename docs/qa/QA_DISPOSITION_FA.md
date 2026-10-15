# پاسخ به بازبینی مستقل

این سند یافته‌ها را پنهان نمی‌کند؛ مشخص می‌کند کدام ایراد نمونه اصلاح شد و کدام gate تولید است.

| یافته | پاسخ نهایی |
|---|---|
| شروع نمونه در S06-B و دسترسی مستقیم catalog | طراحی عمدی review studio؛ در ۰۱ و ۰۵ guard تولیدی mandatory auth/consent/profile/package مشخص شده. ابزار studio نباید وارد app شود. |
| profile/download/placement نمایشی | نمونه دریافت credential/دانلود واقعی نمی‌کند؛ flow تولیدی conditional S22 و starter placement تصریح شده. |
| lessonSequence کوتاه و hardcode | فقط slice نمایشی؛ curriculum/allowed_stages baseline محفوظ. انتقال به تولید ممنوع و acceptance هر activity جداست. |
| رضایت/احراز هویت در مسیرهای قدیمی | این یادداشت تاریخیِ prototype دربارهٔ S03b/c/e است، نه پذیرش فعلی. مسیر هدف فقط شماره تلفن + OTP است؛ S03c (Google) و S03e (ایمیل) باید در رجیستری مشتق‌شدهٔ P0 حذف/غیرفعال شوند و نباید برای آن‌ها مسیر ورود ساخته شود. هر الزام رضایتِ مسیر OTP باید در spec جاری تأیید شود. |
| ادغام نمایشی | S03d layout دو profile و no silent delete دارد؛ engine merge باید owner/conflict/replay را پیاده کند. |
| completed global | مقدار وابسته به profile از ledger خوانده می‌شود و هنگام switch تغییر می‌کند. |
| commit با ورود مستقیم جشن | دفتر طراحی completion fixture را شبیه‌سازی می‌کند؛ document production invariant: completion معتبر قبل ledger و reveal. رفتار fixture به router تولیدی منتقل نمی‌شود. |
| mastery/progress hardcode | data fixture صریح در گزارش؛ schema، learned/mastered separation و acceptance production ثبت شده. |
| arbitrary stroke موفق | دیگر trace stroke آزاد به success خودکار نمی‌رود؛ فقط gesture نمایش داده می‌شود. vector و engine تأیید نشده‌اند. |
| G4 و G3 engine کامل ندارند | تعامل نمونه بازکردن/چیدن؛ قرارداد تولید matching، order و distractor جدا و الزامی است. |
| A/C و parent puzzle | مسیر تغییر رده‌بندی سنی از current حذف شده؛ S06-A/S06-C/S23 صرفاً معماری future باقی مانده‌اند. |
| CMS duplicate | renderCMS مرده حذف شد؛ CMSWorkspace تنها renderer مدیریتی است. |
| timer replay بدون cleanup | ref، clear پیش از replay/navigation و cleanup روی unmount اضافه شد. |
| local storage fail به‌عنوان success | saveError، عدم نمایش موفقیت و retry copy اضافه شد. |
| نبود backend/audio/store/native/pilot | ادعا نشده؛ گیت‌ها باز و قابل‌پیگیری در ۰۶ هستند. |

این پاسخ معادل بازآزمون مستقل بصری توسط پژوهشگر نیست؛ آزمون‌های مرورگر بعد از اصلاح دوباره اجرا می‌شوند.


**وضعیت شمارش baseline prototype:** ۵۸ screen / ۳۵۹ state در رجیستری v1.8؛ این شمارش به معنی تأیید همهٔ صفحه‌ها برای محصول نهایی نیست.
