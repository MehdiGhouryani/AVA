# Playbook فاز ۰٫۸٫۰+ — محتوا، کیفیت و انتشار

## ترتیب

1. review آموزشی برش موجود؛
2. گسترش کوچک محتوا با ID و migration؛
3. ضبط/حقوق/تأیید صوت انسانی؛
4. QA ماتریس دستگاه و accessibility؛
5. account/privacy/operations؛
6. release-readiness audit؛
7. فقط با درخواست صریح مالک: ساخت و امضای بسته انتشار.

## کیفیت آموزشی

- هر فعالیت هدف واحد و قابل توضیح دارد.
- distractor از اشتباه رایج و مناسب سن می‌آید، نه تصادفی.
- ترتیب سختی، طول جلسه و تنوع activity با بازبین ثبت می‌شود.
- داده توسعه‌ای با نتیجه یادگیری واقعی اشتباه گرفته نمی‌شود.
- lives، پرداخت تنبیهی یا قفل دائمی اضافه نمی‌شود.

## QA دستگاه

ماتریس حداقل: عرض ۳۲۰/۳۶۰/۳۹۰/۴۱۲، fontScale ۱/۱٫۳/۱٫۵، RTL، TalkBack، reduced motion، narration روشن/خاموش، voice موجود/ناموجود، storage تازه/قدیمی/خراب، process recreation و مسیر offline.

## release-readiness

- content/audio approval و حقوق استفاده؛
- permission/dependency/license/secret audit؛
- backup/restore و rollback rehearsal؛
- privacy retention/delete-account؛
- startup/jank/memory روی دستگاه؛
- signing خارج repo؛
- changelog و incident owner.

عامل کدنویسی حق ندارد QA انسانی، approval آموزشی/حقوقی یا آمادگی فروش را از روی موفقیت تست واحد نتیجه بگیرد.
