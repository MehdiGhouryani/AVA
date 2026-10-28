# نقشه پروژه

| مسیر | مسئولیت |
|---|---|
| android/app/src/main/java/com/ava/reading/MainActivity.kt | اتصال مخزن و ViewModel، lifecycle و صدا |
| content/ | مدل، reducer خالص تمرین، catalog و نگاشت منابع تولیدشده |
| data/AuthRepository.kt | قرارداد و mock قابل تزریق، انقضا/قفل |
| storage/LearnerRepository.kt | مدل داده، DataStore و مخزن حافظه |
| platform/AndroidNarrator.kt | voice فارسی آفلاین و صدای تشویق کوتاه |
| ui/AvaViewModel.kt | state و رفتار ۲۲ مقصد، صف ذخیره، auth و یادگیری |
| ui/AvaActions.kt | قرارداد رویداد مستقل از ViewModel برای نمایش |
| ui/screens/ | صفحات ورود، یادگیری، قصه، پروفایل و اجزای مشترک |
| ui/theme/ | typography و semantic palette |
| android/app/src/test/ | آزمون منطق/auth/state، دیسک و کنترل‌های Compose |
| design/ | متن، محتوای v3، release/audio schema، fixture مهاجرت، توکن و manifest اصلی art |
| public/ | ۱۶ art اصلی و فونت محلی |
| reference/ | عکس‌های مقایسه و خانه ارسالی؛ نمایش در اپ ممنوع |
| scripts/ | sync و audit و کنترل خروجی |
| cms/payload/ | پنل خصوصی Payload، workflow محتوا/صوت، release و audit؛ بدون اتصال مستقیم Android |
| harness/ | نقشه ماشین‌خوان فازهای باقی‌مانده، playbook، CLI امن و worklog عامل‌های کدنویسی |
| .agents/ | سه مهارت و lock منابع |
| src/ | مرجع تاریخی React، محصول نهایی نیست |
| docs/ | قرارداد، پژوهش، ماتریس، فازبندی و شواهد |

سورس پکیج شامل SDK، cache، build، مسیر محلی، node_modules یا کلید امضا نیست. خروجی‌های تولیدشده art/font/strings/catalog داخل منابع Android برای بازشدن فوری در IDE حفظ شده‌اند و با اسکریپت کنترل می‌شوند. فایل‌های تاریخچه AI Studio بدون حذف حفظ شده‌اند.

## تفکیک نمایش یادگیری در ۰٫۳٫۳
`HomeScreen.kt` فقط خانه و مسیر است. `LessonScreens.kt` معرفی، سؤال، گروه انتخاب، بازخورد و نتیجه را در توابع جدا نگه می‌دارد. `PracticeScreen.kt` حالت خالی و صف‌های مرور را نمایش می‌دهد. فایل یکپارچه قبلی `LearningScreens.kt` جایگزین این سه فایل شد؛ package و قرارداد actions ثابت مانده‌اند.

## جریان قصه در ۰٫۳٫۴
`StoryScreens.kt` پنج مقصد کتابخانه، معرفی، خواندن، پرسش و پایان را نگه می‌دارد و فقط state/actions دریافت می‌کند. وضعیت شروع/موقعیت/تکمیل در `AvaViewModel` و `LearnerRepository` ذخیره می‌شود. `AndroidNarrator` فقط voice فارسی آفلاین نصب‌شده را می‌پذیرد و نبود آن از dialog مشترک گزارش می‌شود؛ UI دسترسی به شبکه یا میکروفون اضافه نمی‌کند.

## صفحات شخصی و اطلاعات در ۰٫۳٫۵
`ProfileScreens.kt` فقط پروفایل، ویرایش دوست و پیشرفت را نگه می‌دارد. `SettingsScreens.kt` تنظیمات، FAQ، درباره و متن‌های حقوقی را نمایش می‌دهد. هر دو فقط `AvaState/AvaActions` می‌گیرند؛ ذخیرهٔ نام/ترجیحات و reset/logout همچنان در ViewModel/repository است. شمار صفحه‌ها با catalog معتبر محاسبه می‌شود تا دادهٔ قدیمی ناشناخته وارد UI نشود.

## مرزهای مسیر و داده در ۰٫۳٫۶
توابع مشتق‌شدهٔ معتبر (`knownLessonCount`، `knownStoryCount`، `resumableLesson` و `orderedTaskKeys`) در `content/Models.kt` قرار دارند تا Home، Profile و ViewModel یک تعریف مشترک از catalog داشته باشند. ViewModel مسیر ثانویه را با back صریح می‌بندد و پایان کامل مسیر را به Library هدایت می‌کند. دادهٔ ناشناخته از فایل به اجبار حذف نمی‌شود، اما در شمار، resume، unlock و صف مرور اثر نمی‌گذارد؛ این انتخاب امکان مهاجرت محتوای آینده را حفظ می‌کند.

## موتور فعالیت در ۰٫۴٫۰
`design/content.fa.json` از schema نسخهٔ ۲ پیروی می‌کند. generator آن را به `TaskKind` و catalog بومی تبدیل می‌کند و قواعد choices/tokens را پیش از تولید می‌سنجد. `ExerciseReducer` تنها مرجع انتخاب گزینه، چیدن/برداشتن token، نمایش hint، امکان بررسی و گذار بعدی است. `LessonScreens.kt` نمایش سه فعالیت را جدا می‌کند، اما progress، feedback و footer مشترک می‌مانند. کلید مرور جدید `lesson:taskId` است و resolver کلید عددی نسخه‌های قبل را نیز می‌خواند.

## برنامه‌ریز مرور در ۰٫۴٫۱
`plannedReviewKeys` در `content/Models.kt` بدون وابستگی Android کار می‌کند و سه حالت Mix/Mistakes/Learned دارد. ورودی آن completed، mistakes، شمار مرور و سقف جلسه است؛ خروجی فقط کلیدهای معتبر catalog است. `PracticeScreen.kt` ترکیب همان خروجی را نشان می‌دهد و `AvaViewModel.review` فقط جلسه را آغاز می‌کند. شمار `reviews` پس از پایان کامل جلسه، seed چرخش بعدی است و معیار تسلط محسوب نمی‌شود.

## قرارداد محتوا و پیشرفت در ۰٫۵٫۰

`content.fa.json` از schema v3 و ساختار Level/Unit/Lesson/Task استفاده می‌کند. `Catalog.kt` همچنان خروجی تولیدشده است و برای سازگاری `lessons` تخت را از hierarchy مشتق می‌کند. `ContentContract.kt` مدل release، snapshot، source و repository را مستقل از Android/CMS نگه می‌دارد. `content.release.json` hash محتوای seed و inventory صوت را قفل می‌کند.

`LearnerRepository` نقشه نسخه‌دار `TaskProgress` را ذخیره می‌کند؛ هر check واقعی attempts/correct/hints/lastSeen را با clock تزریقی ثبت می‌کند. planner فعلی هنوز از این زمان استفاده نمی‌کند و spaced repetition ادعا نمی‌شود. migration محتوای v2 و schema ذخیره قدیمی با تست/validator جدا کنترل می‌شوند.

## runtime محلی محتوا در ۰٫۵٫۱

`LocalContentRepository.kt` منبع Bundled و PublishedLocal، store آزمایشی، validation، staging، commit و rollback را مستقل از UI پیاده می‌کند. `AvaViewModel` snapshot فعال را پیش از خروج از Splash بارگذاری می‌کند و `AvaState` آن را به تمام صفحات می‌رساند. Home، Lesson، Story، Practice، Profile و About فقط از snapshot state می‌خوانند.

`compile-content-bundle.py` محتوای JSON، inventory صوت و manifest را در bundle deterministic با index/hash بسته‌بندی می‌کند. `test-content-bundle.py` تکرارپذیری و رد دست‌کاری را می‌سنجد. store فعلی fixture حافظه‌ای است؛ persistence فایل و process-death recovery هنوز گیت مرحله remote/local-disk است.

## موتور فعالیت v3 در ۰٫۶٫۰

`ActivityRegistry.kt` همه ۹ `TaskKind` را به یکی از سه خانوادهٔ دیداری، شنیداری یا ساخت واژه و یکی از دو interaction انتخاب/token نگاشت می‌کند. `ExerciseReducer` فقط registry را می‌خواند؛ بنابراین DotFamily/PositionalForm/InitialSound/FinalSound/Segment/Blend از reducer و renderer مشترک استفاده می‌کنند. `LessonScreens.kt` نیز بر اساس قابلیت‌های registry renderer را انتخاب می‌کند، نه enumهای hard-coded یا منبع محتوا.

`SessionSnapshot` پاسخ نیمه‌تمام را نسخه‌دار و قابل بازیابی می‌کند و ViewModel آن را با owner درس/مرور در `SavedStateHandle` نگه می‌دارد. `AudioPlayback.kt` درخواست صوت را با ref پایدار، fallback و purpose از UI جدا می‌کند؛ `AndroidNarrator` فعلاً TTS آفلاین fallback است و implementation فایل انسانی بعداً پشت همین interface قرار می‌گیرد. جزئیات در `ACTIVITY_ENGINE_FA.md` است.

## محتوای سطح ۱ و مرز CMS در ۰٫۶٫۱

`content.fa.json` اکنون سه واحد و شش درس دارد. `writtenFallback` در فعالیت آگاهی واجی می‌تواند با target متفاوت باشد و `LessonScreens` همان واژهٔ محرک را برای TTS/hint استفاده می‌کند. migration v2→v3 همچنان خروجی canonical تازه را قطعی تولید می‌کند و strict JSON loader کلید تکراری را رد می‌کند.

`cms.roles.json`، `cms.export.schema.json` و fixture Payload مرز تحریریه را بدون واردکردن SDK به اپ تعریف می‌کنند. `validate-cms-contract.py` نقش‌ها، جداسازی approval، تطبیق متن/شناسه صوت و blockerهای publication را کنترل می‌کند. تصمیم و محدودیت prototype در ADR-0003 ثبت شده است.

## هارنس ادامه پروژه در ۰٫۶٫۱

`harness/roadmap.json` فازهای ۰٫۶٫۱، ۰٫۷٫۰، ۰٫۷٫۱ و ۰٫۸٫۰+ را به taskهای دارای dependency، scope فایل، acceptance و commandهای allowlisted تقسیم می‌کند. `scripts/harness.py` فقط task آماده را نمایش می‌دهد و gateها را بدون shell اجرا می‌کند؛ assemble/bundle در validator ممنوع است. approval آموزشی، حقوق صوت و QA دستگاه `human_gate` هستند و عامل کدنویسی نمی‌تواند آن‌ها را completed کند.
