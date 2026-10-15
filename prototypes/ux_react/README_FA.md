# منبع prototype آوا

این کد prototype برای design review است، نه اپ تولیدی. artwork واقعی عمداً وجود ندارد و `Art.jsx` از asset-slotهای خنثی استفاده می‌کند.

`src/generated/screens.json` خروجی تولیدشده از `data/screens_v18.json` است و نباید دستی ویرایش شود. برای همگام‌سازی، از `tools/sync_registry.py` استفاده کنید. این registry نسخهٔ baseline prototype است، نه رجیستری نهایی محصول؛ رکوردهای تاریخی ورود ایمیل/Google از آن نباید وارد اپ شوند. رجیستری هدف از این منبع در فاز P0 به `data/screens_v2.json` مشتق می‌شود و احراز هویت محصول فقط با شماره تلفن + OTP است.

مسیرهای کودک و والد، قراردادهای state و CMS فقط برای بررسی تجربه هستند. هیچ backend، credential، دانلود واقعی، پرداخت یا سرویس زنده در prototype ادعا نمی‌شود.

شمارش baseline prototype: 58 screen؛ 359 state. این اعداد شمارش فایل‌های prototype هستند، نه تأیید نهایی همهٔ screenها برای release.
