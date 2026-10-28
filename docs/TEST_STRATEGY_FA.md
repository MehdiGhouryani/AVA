# راهبرد آزمون AVA — مبنای ۰٫۴٫۲

هدف این سند جداکردن «سلامت سورس»، «رفتار Native»، «رندر محلی» و «شواهد دستگاه» است. موفقیت یک لایه به‌جای لایه دیگر گزارش نمی‌شود.

| لایه | پوشش | فرمان/شاهد | گیت |
|---|---|---|---|
| قرارداد منبع | JSON، content، audio inventory، asset و hash | `sync-android-resources.py --check` و `audit-project.py` | هر تغییر محتوا/منبع |
| منطق خالص | reducer، planner، migration و validator | تست‌های JUnit/Python | هر تغییر مدل |
| repository | DataStore، صف write، reopen و corruption | Robolectric/unit | هر تغییر داده |
| component | semantics، reflow، انتخاب و feedback | Compose/Robolectric | هر تغییر UI |
| journey | ورود، درس، قصه، مرور، reset و process recreation | androidTest/device | پایان feature |
| انتشار | minified release، startup/jank، TalkBack، TTS و IME | دستگاه و staging | پیش از ۱٫۰ |

## ماتریس ثابت UI

عرض ۳۲۰، ۳۶۰، ۳۹۰ و ۴۱۲؛ ارتفاع حداقل ۵۶۰؛ fontScale برابر ۱٫۰، ۱٫۳ و ۱٫۵؛ RTL؛ narration روشن/خاموش؛ voice موجود/ناموجود؛ reduced motion؛ storage تازه/قدیمی/خراب؛ catalog فعلی/قدیمی/دارای ID ناشناخته.

## قواعد شواهد

- تعداد test source با تعداد تست اجراشده یکی نیست.
- رندر Robolectric اسکرین‌شات دستگاه محسوب نمی‌شود.
- نتیجه قدیمی به نسخه تازه نسبت داده نمی‌شود.
- شکست محیط، همراه command و علت ثبت می‌شود و «passed» نیست.
- APK/AAB فقط با درخواست صریح مالک تولید می‌شود؛ unit/UI test مجاز است.
- برای هر برش، `PROGRESS_FA.md` و `NATIVE_VERIFICATION.json` باید شواهد، گیت باز و قدم بعد را نشان دهند.
