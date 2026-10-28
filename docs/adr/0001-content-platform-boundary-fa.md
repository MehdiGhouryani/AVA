# ADR-0001 — مرز اپ، CMS و محتوای منتشرشده

وضعیت: پذیرفته‌شده برای معماری؛ اجرای شبکه در فاز ۰٫۷٫۱
تاریخ: ۲۰۲۶-۱۰-۲۸

## زمینه

ادمین آینده باید بتواند درس، قصه، صوت و تنظیمات محتوایی را در وب مدیریت کند. اتصال مستقیم UI اندروید به رکوردهای CMS، اپ را به vendor و وضعیت شبکه وابسته می‌کند و انتشار ناقص می‌تواند تجربه کودک را خراب کند.

## تصمیم

CMS یک ابزار خصوصی تحریریه است. اپ هیچ query مستقیم به CMS یا جدول editorial نمی‌زند. جریان رسمی چنین است:

`CMS خصوصی → بررسی/تأیید → compiler و validator → ContentRelease تغییرناپذیر → storage/CDN → staging محلی → verify → activation اتمیک`

UI فقط از `PublishedLocalSource` می‌خواند. نسخه seed همراه اپ همیشه قابل اجرا می‌ماند. دریافت راه‌دور باید hash/signature، schemaVersion، contentVersion و minAppVersion را بررسی کند و release قبلی را برای rollback نگه دارد.

## مرز داده

- CMS: محتوای آموزشی، metadata asset، workflow و audit log.
- اپ کودک: پیشرفت محلی، تنظیمات و وضعیت جلسه.
- CMS داده پیشرفت کودک، شماره تلفن، OTP یا secret اپ را نگه نمی‌دارد.
- Remote config فقط فیلدهای تایپ‌شده و allowlistشده دارد؛ code، HTML اجرایی، endpoint امنیتی و منطق auth از راه دور مجاز نیست.

## پیامد

در ۰٫۴٫۲ هیچ SDK، URL، permission اینترنت یا backend وارد اپ نمی‌شود. در ۰٫۵٫۰ interface و مدل مستقل از vendor طراحی می‌شود. انتخاب Strapi/Payload پس از spike با dataset واقعی در ۰٫۶٫۱ انجام خواهد شد.
