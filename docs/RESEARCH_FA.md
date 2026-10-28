# پژوهش و تطبیق با AVA
بازبینی منابع اصلی در ۶ آبان ۱۴۰۵؛ هدف اقتباس از روش تعامل است، نه کپی هویت دولینگو. منابع تازه دوباره خوانده شدند؛ نتایج آزمون قبلی جای شاهد بازسازی نیستند.

| الگو | منبع اولیه | تصمیم AVA |
|---|---|---|
| مسیر با قدم بعد روشن و امکان مرور قبلی | https://blog.duolingo.com/new-duolingo-home-screen-design/ | اقدام واحد بالای خانه، چهار مرحله با وضعیت متنی و replay |
| مرور موضوع قبلی با اختیار کاربر | https://blog.duolingo.com/how-to-review-lessons-on-duolingo/ | مرور اشتباه و آموخته‌ها؛ بدون جان محدود یا فشار streak |
| هماهنگی تب‌ها، هدر، فاصله و تایپوگرافی | https://blog.duolingo.com/core-tabs-redesign/ | چهار مقصد ریشه، اجزای مشترک و حفظ فضای محتوا بالای نوار |
| قصه و صوت در سوادآموزی و پیچیدگی هماهنگی صدا/حرکت | https://blog.duolingo.com/a-good-read-building-duolingo-abc-for-android/ | قصه کوتاه صفحه‌ای، شنیدن اختیاری و توقف صدا در lifecycle؛ حرکت کم |
| ذخیره کوچک و تراکنش با Flow | https://developer.android.com/topic/libraries/architecture/datastore | repository و ViewModel؛ DataStore بیرون Composable و تست دیسک |
| محتوای معنایی ورودی Autofill | https://developer.android.com/develop/ui/compose/text/autofill و https://developer.android.com/reference/kotlin/androidx/compose/ui/autofill/ContentType | PhoneNumberNational و SmsOtpCode؛ یک ورودی واقعی، بدون ادعای SMS واقعی |

این نگاشت تصمیم مهندسی پروژه است؛ اثبات اثربخشی آموزشی AVA یا استفاده از معماری داخلی Duolingo نیست. راهنمای زبان‌آموزی عمومی و ABC یک برنامه درسی یکسان نیستند. محتوای v2 سه تعامل توسعه‌ای دارد و هنوز برنامهٔ درسی تأییدشده نیست. TTS دستگاه نیز جای فایل صوتی آموزشی بررسی‌شده را نمی‌گیرد.

Kotlin/Compose برای هدف انحصاری Android و کنترل بومی ورودی/دسترسی/چرخه عمر انتخاب شده است. DataStore برای این حجم کوچک مناسب است؛ داده رابطه‌ای یا محتوای عظیم آینده می‌تواند Room بخواهد. نسخه‌های build بر مبنای قبلی سازگار pin شدند؛ ادعای آخرین نسخه یا تأیید سیاست فروشگاه نمی‌شود.

## بازبینی ۰٫۳٫۲
منابع مسیر خانه و هماهنگی تب‌ها دوباره باز شدند. نگاشت این مرحله: حفظ جای مسیر پس از عوض‌کردن تب، هدر و خوانایی مشترک، و کنترل خواندن قابل پیش‌بینی. حفظ state کوچک نمایشی از DataStore پیشرفت جداست؛ `rememberSaveableStateHolder` فقط چهار ریشه را نگه می‌دارد تا state گذرای OTP/ویرایش وارد آن نشود. مرجع API: https://developer.android.com/reference/kotlin/androidx/compose/runtime/saveable/SaveableStateHolder . برای محتوای بزرگ‌شده، بازچینی و اسکرول عمودی بر کوچک‌کردن فونت اولویت دارد؛ سند https://developer.android.com/develop/ui/compose/accessibility/scalable-content این اصل را توضیح می‌دهد. در AVA gesture جدید pinch-to-zoom یا Navigation3 اضافه نشده است.

## اجرای ۰٫۳٫۳
برای انتخاب تک‌گزینه‌ای، به‌جای چند کنترل هم‌پوشان از `selectableGroup` و یک `selectable` با نقش RadioButton روی هر کارت استفاده شد؛ مرجع رسمی https://developer.android.com/develop/ui/compose/components/radio-button . خطای تمرین باید راه اصلاح روشن و نزدیک اقدام داشته باشد؛ توصیه UX Pro Max درباره Error Recovery با متن و آیکن کهربایی AVA پیاده شد و توصیه عمومی رنگ قرمز کنار گذاشته شد. نشان کنار گزینه فقط انتخاب‌شدن را نشان می‌دهد؛ تیک درست‌بودن فقط در بازخورد پاسخ درست ظاهر می‌شود.

## اجرای ۰٫۳٫۴ — قصه و درک مطلب
منابع رسمی Duolingo، Stories را قصه‌های کوتاه قابل خواندن و شنیدن با پرسش‌های درک مطلب توصیف می‌کنند و بر روبه‌روشدن با واژه در بافت تأکید دارند: https://blog.duolingo.com/duolingo-stories-the-journey-to-android/ و https://blog.duolingo.com/covering-all-the-bases-duolingos-approach-to-reading-skills/ . مقالهٔ مسیر خانه نیز می‌گوید Stories به مسیر هدایت‌شده افزوده شدند تا قدم بعد روشن باشد: https://blog.duolingo.com/new-duolingo-home-screen-design/ . AVA متن یا ساختار محتوای آن‌ها را کپی نمی‌کند؛ نتیجهٔ اجرایی، قصهٔ کوتاه صفحه‌ای، صدا به‌عنوان کمک اختیاری، یک پرسش آرام و اقدام بعدی روشن است.

راهنمای رسمی Compose برای controls تعاملی حداقل ۴۸dp و برای عناصر سفارشی semantics معنادار را توصیه می‌کند: https://developer.android.com/codelabs/jetpack-compose-accessibility و https://developer.android.com/develop/ui/compose/accessibility/semantics . بنابراین کارت‌های گزینه یک `selectable` واحد با نقش RadioButton هستند، progress فقط بصری نیست و متن وضعیت کتابخانه کنار رنگ نمایش داده می‌شود. راهنمای UI UX Pro Max نیز reflow با ارتفاع محتوایی و حفظ برچسب در عرض کم را پیشنهاد کرد. claymorphism به سطح‌های گرد با shadow محدود شد؛ خوانایی و وضعیت صریح بر جلوهٔ سه‌بعدی تقدم دارد.

این پژوهش اثربخشی آموزشی دو قصهٔ فعلی را تأیید نمی‌کند. محتوا توسعه‌ای است و بررسی مربی، تلفظ TTS فارسی و آزمون کودک/دستگاه پیش از انتشار لازم است.

## اجرای ۰٫۳٫۵ — پروفایل، پیشرفت و تنظیمات
منابع رسمی دولینگو نشان می‌دهند شخصیت‌ها برای زنده‌کردن تجربه و جشن پیشرفت در درس‌ها استفاده می‌شوند و featureهای پروفایل/achievement بخشی از سازوکار engagement آن محصول‌اند: https://blog.duolingo.com/why-i-chose-duolingo-liz-nagler/ . AVA فقط نقش همراه و بازخورد پیشرفت را با art موجود می‌گیرد؛ leaderboard، streak، XP، دوست‌یابی و quest وارد نسخهٔ کودک نشده‌اند. پیشرفت AVA از مرحله/قصه/مرور واقعی محلی مشتق می‌شود و ادعای سطح علمی ندارد.

راهنمای رسمی Compose پیشنهاد می‌کند رفتار Switch/Checkbox به ردیف والد منتقل شود، کنترل داخلی callback نداشته باشد و stateDescription متناسب با زمینه تعریف شود: https://developer.android.com/codelabs/jetpack-compose-accessibility و https://developer.android.com/develop/ui/compose/accessibility/api-defaults . حداقل هدف تعاملی ۴۸dp، برچسب متنی کنار رنگ و بازچینی در فونت بزرگ نیز معیار پذیرش‌اند. جست‌وجوی هدفمند UI UX Pro Max دربارهٔ progress، text reflow، full-row switch و destructive confirmation این اولویت‌ها را تأیید کرد؛ رنگ/فونت پیشنهادی عمومی آن با توکن و Vazirmatn آوا جایگزین شد.

متن حریم خصوصی و قوانین بر اساس رفتار فعلی سورس نوشته شده است: ذخیرهٔ محلی، بدون INTERNET/MIC permission و ورود mock. این متن هنوز مشاورهٔ حقوقی یا تأیید انتشار نیست و در خود UI نیز همین محدودیت اعلام می‌شود.

## بازبینی جامع ۰٫۳٫۶ — مسیر، state و دسترس‌پذیری
مقالهٔ رسمی مسیر دولینگو بر قدم بعد روشن، حفظ پیشرفت هنگام تغییر ساختار و امکان مرور سطح تکمیل‌شده تأکید می‌کند: https://blog.duolingo.com/new-duolingo-home-screen-design/ . نگاشت AVA این است که شناسهٔ قدیمی نباید پیشرفت را بیشتر یا قدم تکمیل‌شده را به‌صورت resume نشان دهد، replay مجاز است و قفل مرحله با تمام پیش‌نیازهای قبلی کنترل می‌شود. این رفتار اقتباس محصولی است و اثربخشی آموزشی محتوای فعلی را ثابت نمی‌کند.

جست‌وجوی هدفمند UI UX Pro Max برای `predictable back navigation state restoration` و `large text compact mobile reflow`، حفظ تاریخچه، ارتفاع محتوایی و جلوگیری از clipping را اولویت داد. در Compose، `SaveableStateHolder` جای اسکرول چهار ریشه را نگه می‌دارد؛ پیشرفت لازم برای منطق کسب‌وکار در DataStore است. راهنمای رسمی Android توصیه می‌کند فقط حداقل state گذرا در Bundle/SavedStateHandle قرار گیرد و دادهٔ پیچیده از لایهٔ داده بازسازی شود: https://developer.android.com/develop/ui/compose/state-saving . بنابراین AVA پس از مرگ فرایند از checkpoint امن Home/Phone برمی‌گردد و active lesson/story position را از مخزن محلی بازیابی می‌کند.

برای ردیف رضایت، راهنمای رسمی `toggleable` والد و پاک‌کردن semantics تزئینی فرزند مبنا بود: https://developer.android.com/develop/ui/compose/accessibility/merging-clearing . راهنمای scalable content نیز reflow را برای حذف pan افقی توصیه می‌کند: https://developer.android.com/develop/ui/compose/accessibility/scalable-content . آزمون محلی فونت ۱٫۵ جای TalkBack و اندازه‌گیری دستگاه را نمی‌گیرد.

Lint نسخهٔ Lifecycle 2.11 را پیشنهاد می‌کند، اما release note رسمی می‌گوید artifactهای Compose این نسخه به AGP 9.2 و compileSdk جدیدتر نیاز دارند: https://developer.android.com/jetpack/androidx/releases/lifecycle . پروژه در این برش AGP 8.9.2/SDK 35 را حفظ کرد؛ ارتقای toolchain باید جدا، با SDK و تست دستگاه انجام شود و هشدارها در verification ثبت شده‌اند.

## اجرای ۰٫۴٫۰ — تنوع درون درس کوتاه
منابع رسمی دولینگو مسیر هدایت‌شده را ترکیبی از محتوای تازه و مرور معرفی می‌کنند و فعالیت را در قدم‌های کوتاه نگه می‌دارند: https://blog.duolingo.com/new-duolingo-home-screen-design/ . مقالهٔ مهارت‌های خواندن نمونه‌هایی مانند انتخاب واژهٔ جاافتاده، جفت‌کردن و پاسخ به متن را برای ایجاد تنوع در خواندن شرح می‌دهد: https://blog.duolingo.com/covering-all-the-bases-duolingos-approach-to-reading-skills/ . گزارش ساخت Duolingo ABC نیز بر تعادل تصویر شاد با آموزش صریح، narration/animation و اجزای ماژولار درس تأکید دارد: https://blog.duolingo.com/a-good-read-building-duolingo-abc-for-android/ . نتیجه برای AVA سه صفحهٔ تازه نیست؛ سه فعالیت کوتاه در همان Lesson است تا مسیر و مدل ذهنی کودک ثابت بماند.

تمرین شنیداری AVA پاسخ را پشت دسترسی به voice قفل نمی‌کند. دکمهٔ شنیدن از TTS فارسی آفلاین موجود استفاده می‌کند، اما راهنمای نوشتاری با یک لمس باز می‌شود و انتخاب‌ها همیشه قابل استفاده‌اند. این تصمیم محدودیت محتوای صوتی توسعه را صادقانه نگه می‌دارد و باید پس از تهیهٔ صدای معتبر با متخصص آموزش بازبینی شود.

برای ساخت کلمه، `FlowRow` نشانه‌ها را در عرض کم بازمی‌چیند و هر دکمه حداقل ۴۸dp است؛ راهنماهای رسمی Compose بر semantics کنترل، هدف لمس و reflow تأکید دارند: https://developer.android.com/develop/ui/compose/accessibility/semantics ، https://developer.android.com/develop/ui/compose/accessibility/api-defaults و https://developer.android.com/develop/ui/compose/layouts/flow . drag اجباری استفاده نشد؛ لمس و undo با TalkBack و کنترل حرکتی سازگارتر و آزمون‌پذیرتر است. جست‌وجوی UI UX Pro Max نیز feedback روشن، target بزرگ و layout قابل wrap را تأیید کرد. claymorphism در کارت پاسخ، گوش‌دادن و نمونه به سطح روشن گرد با خط و سایهٔ کم محدود ماند.

## اجرای ۰٫۴٫۱ — مرور کوتاه و شفاف
منبع رسمی Practice Hub مرور اشتباه‌ها و تمرین مهارت‌های مشخص را به‌صورت انتخاب‌های مستقل ارائه می‌کند: https://blog.duolingo.com/guide-to-duolingo-practice-hub/ . توضیح رسمی spaced repetition می‌گوید زمان مرور و دقت در انتخاب محتوای شخصی‌سازی‌شده نقش دارند: https://blog.duolingo.com/spaced-repetition-for-learning/ . راهنمای کلی محصول نیز تمرین شخصی را بر محتوای زمان مرور و اشتباه‌های مشخص بنا می‌کند: https://blog.duolingo.com/duolingo-101-how-to-learn-a-language-on-duolingo/ .

AVA فعلاً timestamp، شمار پاسخ هر فعالیت یا تخمین تسلط ذخیره نمی‌کند؛ بنابراین الگوریتم جدید spaced repetition نامیده نمی‌شود. جلسهٔ Mix حداکثر پنج فعالیت است، اشتباه‌های معتبر را مقدم می‌کند، تنوع `TaskKind` را در صورت وجود نگه می‌دارد و برای تغییر نقطهٔ شروع آموخته‌ها از شمار مرور کامل استفاده می‌کند. نتیجه قطعی است تا state قابل بازسازی و آزمون بماند. راهنمای UI UX Pro Max برای semantics و ساختار کم‌عمق Compose اعمال شد؛ کارت اصلی تنها CTA پررنگ صفحه است و دو مرور هدفمند با دکمهٔ ثانویه باقی می‌مانند.
