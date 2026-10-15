# پژوهش عمومی Duolingo و Duolingo ABC برای آوا

**هدف:** استخراج الگوهای قابل‌انتقال برای یک اپ خواندن فارسیِ ۴–۷ ساله؛ الهام‌گیری، نه کپی UI/برند.

**وضعیت شواهد:** این گزارش فقط بر صفحات عمومی رسمی Duolingo/ABC (وبلاگ، brand guidelines، privacy/help و listing رسمی App Store) و اسناد canonical آوا تکیه دارد. من به نسخهٔ لاگین‌شدهٔ اپ Duolingo/ABC دسترسی نداشتم و ادعای مشاهدهٔ جریان واقعیِ داخل اپ، paywall یا همهٔ stateهای تولیدی را نمی‌کنم. صفحات رسمی گاهی اسکرین‌شات/نمودار embed دارند؛ چون استخراج متنیِ وب تصاویر را به‌صورت پیکسل‌به‌پیکسل در اختیار نمی‌گذارد، جزئیات دقیق screenshot را مبنای تصمیم رنگ/فاصله قرار نداده‌ام.

## ۱) مرجع محصول آوا و معیار قضاوت

مطابق `01_PRODUCT_AND_FLOWS.md` و `03_DESIGN_SYSTEM.md`:

- مخاطب V1: کودک ۴–۷ سال، مسیر B و u00…u15؛ صدای فارسی → نشانه → ردگیری → ترکیب → داستان خواندنی.
- فلو باید آرام، آفلاین‌محور و بدون شرم باشد؛ کودک نباید paywall، گزارش والد، credential یا خطای فنی ببیند.
- child و parent فضا و زبان جدا دارند؛ parent مسیر گزارش/تنظیمات/پرداخت است.
- progress و mastery جدا، ستاره فقط completion جلسه‌ای تا ۳، و reward exactly-once با ledger قبل از نمایش جشن است.
- توکن‌های پیشنهادی آوا (نه brand رسمی): زمینه `#FFF9ED`، ink `#2B2A4C`، action `#087D72`، gold `#E4AC32`، کتاب `#8060AE`؛ Vazirmatn/Estedad؛ target کودک ۶۴dp و CTA کودک ۷۲dp؛ حرکت micro ۱۵۰ms، standard ۳۰۰ms، جشن ≤۴ ثانیه؛ reduced-motion و audio parity.

**قرارداد placement و parent در V1:** `S05/S05r` station-based placement حفظ می‌شود؛ فقط مسیر «از اول/شروع از صفر» به `u00` می‌رود. `gate_mode=off` است: parent consent/login و ownership در setup اولیه الزامی‌اند، اما child-side parent puzzle/passcode/challenge وجود ندارد؛ child فقط handoff صریح به parent area دارد.

**قاعدهٔ تفسیر:** هر ردیف زیر سه لایه دارد: «الگوی مستند رسمی»، «ترجمهٔ پیشنهادی برای آوا»، «چه چیزی رد می‌شود و چرا».

---

## ۲) جمع‌بندی مقایسه‌ای: Duolingo اصلی در برابر ABC

| محور | Duolingo اصلی (زبان‌آموز عمومی) | Duolingo ABC (کودک/سواد) | نتیجه برای آوا |
|---|---|---|---|
| مخاطب/هدف | زبان‌آموز عمومی؛ مسیر، gamification، رقابت، اشتراک | صفحهٔ رسمی ABC خود را برای کودکان ۳–۸ سال، سواد و خواندن معرفی می‌کند | از ABC برای child UX و از main برای اصول سیستم/مسیر الهام بگیر؛ هدف سواد فارسی را مستقل نگه دار |
| واحد یادگیری | درس‌های چنددقیقه‌ای، واحد/مسیر، reading/listening/speaking/writing | bite-sized chunks؛ صفحهٔ «How we teach» می‌گوید درس‌ها ۵ دقیقه یا کمتر، ۹ سطح شروع، مسیر cumulative | جلسهٔ ۳–۵ دقیقه، یک هدف قابل‌فهم، بدون آشفتگی تب‌ها |
| محتوای یادگیری | مسیر spaced practice، تمرین شخصی‌سازی‌شده، Stories در دوره‌های خاص | phonics صریح، phonemic awareness، comprehension، fluency، vocabulary؛ story تعاملی | ترتیب صدای فارسی/نشانه/ترکیب/داستان، نه صرفاً «امتیاز گرفتن» |
| انیمیشن | شخصیت‌ها در تمرین‌ها و mid-lesson interstitial؛ lip-sync با state machine | صدا/انیمیشن برای نمایش decoding و encoding؛ «snap» حروف، Duo، sparkle پاسخ درست | حرکت باید مفهوم را نشان دهد یا آرام جشن بگیرد؛ decoration دائم رد می‌شود |
| رقابت/فشار | XP، streak، leaderboard، heart/energy و فروش subscription در تجربهٔ عمومی | listing رسمی ABC: ad-free، بدون in-app purchase و offline؛ وضعیت فعلی store ممکن است stale باشد | leaderboard، streak loss، hearts/energy loss، ad، paywall کودک رد می‌شود |
| والد/حریم خصوصی | Privacy برای child محدودیت‌های اجتماعی می‌گذارد؛ leaderboards برای زیر ۱۳ غیرفعال/محدود در Help | Privacy می‌گوید ABC با parent setup و age کار می‌کند و personal information از کودک جمع نمی‌کند | parent-area handoff و data minimization از ABC و privacy رسمی؛ در V1، `gate_mode=off` یعنی هیچ puzzle/passcode کودک‌محور نداریم؛ parent dashboard هرگز در child home |
| آفلاین | وبلاگ app-size از bundle کردن asset برای اتصال ضعیف/offline و trade-off می‌گوید؛ offline در main تاریخی با اشتراک گره خورده | ABC در سایت و App Store به no Wi‑Fi/offline و no ads/IAP تصریح می‌کند | مدل ABC برای V1 آوا: بستهٔ محتوای امضاشده، resume، verify/atomic install، مسیر کمبود فضا در parent |

### جمع‌بندی ضدکپی
- **قابل‌انتقال:** کوتاهی درس، مسیر روشن، تنوع کنترل‌شده، تمرین فاصله‌دار، شخصیتِ هدفمند، بازخورد اصلاحی، story برای context، child-safe parent separation، offline واقعی.
- **غیرقابل‌انتقال:** سبز Duolingo، Duo/شخصیت‌ها، Feather Bold، XP/league/streak، قلب/energy، gem shop، ساختار تب‌ها و نگاشت پیکسلی صفحه‌ها. آوا باید زبان فارسی، RTL، نقطهٔ شروع/ترتیب stroke و جهان باغ/کتاب خود را بسازد.

---

## ۳) فهرست منابع خوانده‌شده و نوع شواهد

### منابع رسمی UX/visual
1. **[Duolingo Brand: Color](https://design.duolingo.com/identity/color#core-brand-colors)** — صفحهٔ رسمی، بدون تاریخ نمایان در متن بازیابی‌شده. [توصیف رسمی] Feather Green `#58CC02`، Mask Green `#89E219`، Eel `#4B4B4B`، Snow سفید و secondaryهای Macaw/Cardinal/Bee/Fox/Beetle/Humpback را نام می‌برد و hierarchy می‌دهد.
2. **[Typography](https://design.duolingo.com/identity/typography#feather-bold)** — بدون تاریخ نمایان. [توصیف رسمی] Feather Bold برای headline کوتاه (حداقل ۳۰px روی صفحه، lowercase، line leading 100–110%)، DIN Next Rounded برای متن/heading طولانی (حداقل ۱۴px، leading 140%) و Nunito به‌عنوان fallback.
3. **[Writing style](https://design.duolingo.com/writing/style)** — بدون تاریخ نمایان. [توصیف رسمی] متن کوتاه، sentence case در header، دکمهٔ ALL CAPS و بدون punctuation، عددها به‌صورت numeral؛ این قواعد انگلیسی‌اند و نباید عیناً بر فارسی تحمیل شوند.
4. **[Imagery](https://design.duolingo.com/identity/imagery)** — بدون تاریخ نمایان. [توصیف رسمی] illustration سبک پیش‌فرض است؛ character pose باید mood/activity را روشن کند، crop برای تمرکز بر expression مجاز است؛ شخصیت‌ها expressive و گاه awkward هستند. متن صفحه حتی از guilt/shame به‌عنوان بخشی از شخصیت Duo می‌گوید؛ این بخش برای آوا رد می‌شود.
5. **[Character guidelines](https://design.duolingo.com/illustration/characters#body-types)** — بدون تاریخ نمایان. [توصیف رسمی] قطعات ساده و قابل‌تولید، سر/بدن معمولاً ۱–۲ shape، چشم هندسی، pupil/پلک/دهان برای emotion، pose غیرایستا. [ترجمهٔ محصول] برای mascot مستقل آوا مفید است، نه برای کپی فرم Duo.
6. **[Shape language: Duolingo’s art style](https://blog.duolingo.com/shape-language-duolingos-art-style/)** — ۲ ژوئیهٔ ۲۰۲۰. [توصیف رسمی] گذار به illustrationهای bright/round/friendly از پروژهٔ KIDS؛ vector برای scale؛ کمترین جزئیات لازم، silhouette و white negative space برای خوانایی؛ humor/story و animation برای جلب توجه. [تصویر رسمی] صفحهٔ مقاله artwork دارد، اما جزئیات پیکسلی از آن استخراج نشده است.
7. **[Building character](https://blog.duolingo.com/building-character/)** — ۱۰ نوامبر ۲۰۲۰. [توصیف رسمی] characterها پس از ۱۸ ماه طراحی برای diverse/fun بودن ساخته شدند؛ در بیشتر exerciseها «سخن می‌گویند»، برای پاسخ درست animation و پس از چند پاسخ درست mid-lesson animation دارند.
8. **[World-character visemes](https://blog.duolingo.com/world-character-visemes/)** — ۱۰ نوامبر ۲۰۲۲. [توصیف رسمی/فنی] Rive state machine، stateهای pose و mouth، timing بر اساس word/phoneme، idle (blink/head nod/eyebrow) و final reaction. [تصویر رسمی] chart و diagram داخل صفحه هست؛ از آن برای ادعای ظاهر دقیق استفاده نشده است.

### منابع مسیر، onboarding، درس و انگیزش
9. **[Introducing the new learning path](https://blog.duolingo.com/new-duolingo-home-screen-design/)** — ۶ مهٔ ۲۰۲۲؛ launch عمومی طبق متن ۱ نوامبر ۲۰۲۲. [توصیف رسمی] مسیر step-by-step، mixed concepts، practice درون مسیر، Stories/guidebook در مسیر، spaced repetition؛ levelهای قبلی interleave می‌شوند؛ floating arrow برای بازگشت به نقطهٔ فعلی؛ عنوان unitها descriptiveتر شده‌اند. [تصویر رسمی] صفحهٔ مقاله old/new path و generic illustration embed دارد؛ pixel audit انجام نشده.
10. **[Duolingo 101](https://blog.duolingo.com/duolingo-101-how-to-learn-a-language-on-duolingo/)** — ۲ دسامبر ۲۰۲۴. [توصیف رسمی] onboarding هدف/انگیزه و notification؛ انتخاب شروع از صفر یا placement؛ ساخت profile برای save/sync؛ درس‌ها از recognition به typing دشوارتر می‌شوند؛ practice بر اساس weak content/mistakes؛ gamification شامل XP, leaderboards, streak, gems, friends، hearts؛ Super/Max اشتراک‌ها را اضافه می‌کنند.
11. **[Lessons from Asia](https://blog.duolingo.com/lessons-from-asia-turning-local-research-into-global-experiments/)** — ۲ فوریهٔ ۲۰۲۱؛ به‌روزرسانی ۱۶ فوریهٔ ۲۰۲۶ در صفحه. [توصیف رسمی + نتیجهٔ آزمایش] در ژاپن جایگزینی “placement test” با “check your English level” placement را دوبرابر و retention را بیشتر کرد؛ تغییر واژگان onboarding در هند completion را ۱٪ بالا برد. [استنباط آوا] این فقط دربارهٔ لحن/ترس از واژهٔ آزمون است؛ منطق station-based placement آوا باید حفظ شود.
12. **[Duolingo teaching method](https://blog.duolingo.com/duolingo-teaching-method/)** — ۲ فوریهٔ ۲۰۲۳. [توصیف رسمی] پنج اصل: interactive، personalized، important skills، motivation، delight؛ exercise از ساده به دشوار، optional hint و bite-sized explanation؛ AI تعادل familiar/challenge و practice شخصی ایجاد می‌کند؛ points/streak/friends ابزار انگیزش main هستند.
13. **[Spaced repetition](https://blog.duolingo.com/spaced-repetition-for-learning/)** — ۲۶ دسامبر ۲۰۲۳. [توصیف رسمی] مرور فاصله‌دار و active recall؛ پایان درس مرور mistakeها؛ practice بر accuracy و زمان انتخاب می‌شود. [ترجمهٔ آوا] «مرور» را فرصت رشد نشان بده، نه بازگشت/شکست.
14. **[Adaptive lessons](https://blog.duolingo.com/keeping-you-at-the-frontier-of-learning-with-adaptive-lessons/)** — ۲۶ مهٔ ۲۰۲۰. [توصیف رسمی] lesson در لحظه دشواری را تغییر می‌دهد؛ mistakes practice بر خطاهای واقعی بنا می‌شود. [محدودیت] زبان‌آموز بزرگسال/زبان‌محور است؛ انتقال الگوریتم به سواد فارسی نیازمند اعتبارسنجی آموزشی است.
15. **[Partial credit placement](https://blog.duolingo.com/partial-credit-improvements-to-duolingos-placement-test/)** — ۱۴ سپتامبر ۲۰۱۸. [توصیف رسمی] شدت خطا و نوع challenge در grading placement لحاظ می‌شود. [استنباط] برای ردگیری نزدیک‌به‌درست در trace/صدای کودک مفید است، ولی grading آوا نباید کودک را با عدد یا درصد قضاوت کند.
16. **[Adventures](https://blog.duolingo.com/adventures/)** — ۲۴ سپتامبر ۲۰۲۴. [توصیف رسمی] در سناریوی تعاملی، خطا با redirect طبیعی «Oh, did you mean…?» پاسخ می‌گیرد نه نمره‌دادن؛ اشیاء محیطی trigger یادگیری‌اند. [استنباط] الگوی redirect را به «دوباره با هم گوش بدهیم» ترجمه کن؛ داستان و محیط فارسی مستقل باشد.
17. **[Achievements](https://blog.duolingo.com/achievement-badges/)** — ۱۱ دسامبر ۲۰۲۳. [توصیف رسمی] badges بازطراحی و در profile به‌صورت trophy shelf جمع می‌شوند؛ milestone و personal record celebration. [رد آوا] achievement بر streak/year/XP برای کودک مناسب نیست.
18. **[Streak animation](https://blog.duolingo.com/streak-milestone-design-animation/)** — ۲۱ ژانویهٔ ۲۰۲۲. [توصیف رسمی] research نشان داد بعضی تازه‌واردها metaphor شعله را نمی‌فهمند؛ تیم به Phoenix و animation milestone رسید؛ share card نیز دارد. [رد] streak loss/pressure/social share برای AVA کودک.

### منابع ABC، حریم خصوصی، آفلاین و درآمد
19. **[Duolingo ABC home](https://abc.duolingo.com/)** — بدون تاریخ نمایان. [توصیف رسمی] 100% free؛ phonics/handwriting/sight words/vocabulary؛ National Reading Panel؛ EDC claim دربارهٔ ۲۸٪ پس از ۹ هفته در صفحه آمده است اما این گزارش آن را به‌عنوان اثربخشی مستقل AVA تأیید نمی‌کند.
20. **[How we teach](https://abc.duolingo.com/how-we-teach)** — بدون تاریخ نمایان. [توصیف رسمی] سن ۳–۸، lesson ≤۵ دقیقه، ۹ level، differentiated/cumulative، independent/no Wi‑Fi؛ phonics explicit، phonemic awareness، comprehension در interactive story، fluency و vocabulary؛ stories humor/SEL و comprehension check دارند.
21. **[A good read: building ABC for Android](https://blog.duolingo.com/a-good-read-building-duolingo-abc-for-android/)** — ۶ اکتبر ۲۰۲۲. [توصیف رسمی] ABC برای ۳–۸ به audio narration/animation متکی است؛ animation decoding/encoding را step-by-step نشان می‌دهد؛ حروف در نام snap می‌شوند و پاسخ درست sparkle دارد؛ onboarding، home، lesson به‌صورت moduleهای جدا معماری شده‌اند.
22. **[App Store ABC listing](https://apps.apple.com/us/apps/mobile/learn-to-read-duolingo-abc/id1440502568)** — listing عمومی؛ metadata قابل‌مشاهده version 1.96.2 در ۲ اوت ۲۰۲۳ را نشان می‌دهد، بنابراین برای current UI مرجع تازه‌ای نیست. [توصیف رسمی listing] بیش از ۷۰۰ درس، tracing، drag-and-drop، story با narration و word highlighting، mini-games/rewards، ad-free/no IAP، offline، 4+؛ accessibility features در listing «not yet indicated» است. [اسکرین‌شات رسمی] listing screenshot دارد اما در fetch متنی قابل مشاهده/ممیزی پیکسلی نبود.
23. **[Privacy Policy](https://www.duolingo.com/privacy)** — صفحه می‌گوید آخرین بازبینی ۲۶ مهٔ ۲۰۲۶ (تاریخ صفحه ممکن است پس از snapshot تغییر کند). [توصیف رسمی] ABC توسط parent setup می‌شود، age کودک را می‌گیرد و anonymously برای research نگه می‌دارد؛ personal information از کودک جمع نمی‌کند؛ speech data روی phone و Apple speech recognition، بدون اشتراک با Duolingo؛ email parent اختیاری. برای Child Users main نیز username غیرواقعی، حذف نام/email/phone، social محدود و ads family-safe/non-personalized توضیح داده شده است.
24. **[Leaderboards help](https://www.duolingo.com/help/leaderboards-and-league)** — بدون تاریخ نمایان. [توصیف رسمی] leaderboard رقابت هفتگی XP است و promotion/demotion دارد؛ زیر ۱۳ سال social features از جمله leaderboards را ندارد؛ profile private آن را خاموش می‌کند.
25. **[App size/offline trade-offs](https://blog.duolingo.com/emerge-tool-app-size/)** — ۱۴ ژوئیهٔ ۲۰۲۳. [توصیف رسمی] bundle کردن بیشتر imageها complexity را بالا می‌برد اما تجربهٔ poor connection/offline را consistent می‌کند؛ vector/SVG و حذف assetهای unused حجم را کم کرده‌اند.
26. **[Language learning in India](https://blog.duolingo.com/language-learning-for-the-next-billion-duolingo-in-india/)** — ۲۴ سپتامبر ۲۰۲۰. [توصیف رسمی + آزمایش] promotion اولیهٔ Plus حذف شد و DAU یک درصد بالا رفت؛ حذف کامل Plus در چند بازار ۶٪ DAU را بالا برد؛ کاربران promotion را با paywall اشتباه می‌گرفتند. این عددها مختص بازار/زمان آن مقاله‌اند و نباید برای آوا ادعای causal شوند.
27. **[Energy](https://blog.duolingo.com/duolingo-energy/)** — ۳ ژوئیهٔ ۲۰۲۵. [توصیف رسمی] آزمایش محدود: Hearts هر mistake را کم می‌کرد و beginnerها ۲ برابر بیشتر به اتمام heart می‌رسیدند؛ Energy به‌جای هر خطا با lesson کم می‌شود، با پاسخ‌های درست برمی‌گردد؛ rewarded ad/gem refill دارد. [تصمیم] همین شواهد برای رد هر resource-loss در کودک کافی است.

---

**منبع تکمیلی avatar:** **[How to Change Your Profile Picture to an Avatar on Duolingo](https://blog.duolingo.com/avatar-creator/)** — ۱۹ ژوئیهٔ ۲۰۲۳. [توصیف رسمی] ابزار mix-and-match را برای hairstyle، body shape، piercings، skin tones، glasses/accessories و expression معرفی می‌کند و «billions of possible combinations» می‌گوید. این شاهد دربارهٔ avatar main Duolingo است؛ برای آوا فقط اصل preset personalization قابل‌الهام است، با privacy/parent قرارداد آوا.

---

## ۴) تحلیل الگو → سازگارسازی آوا → رد

### ۴.۱ زبان بصری، typography و components

**الگوی مستند:** Duolingo برند خود را با green/white/gray، secondaryهای پررنگ، illustration ساده و vector، silhouette/negative space و فرم‌های گرد توضیح می‌دهد. Typography رسمی انگلیسی Feather Bold/DIN Rounded/Nunito و style copy کوتاه است. Shape language می‌گوید جزئیات کمتر، خوانایی بیشتر؛ animation برای attention است.

**سازگار‌سازی آوا:**
- `AVA.Surface` کرم و `AVA.Ink` نیلی فعلی canonical را حفظ کند؛ action teal تیره فقط در اندازه/کنتراست آزموده شود. gold فقط state جشن/پیشرفت، نه رنگ همهٔ CTAها.
- یک سطح خلوت در exercise: یک prompt، یک هدف، یک primary action؛ illustration فقط اگر به صدای/نشانهٔ همان step کمک می‌کند.
- `ChildTopBar`: خروج بالا-راست، progress از راست، replay همیشه در دسترس و label فارسی؛ `ChoiceCard`: selected را با border/shape/icon و رنگ هم‌زمان نشان دهد؛ رنگ به‌تنهایی state نباشد.
- فونت فارسی واقعی Vazirmatn برای UI و Estedad فقط اگر glyph، اعراب، وزن و reflow در کودک تست شود. اعداد فارسی/لاتین، punctuation و sentence case باید با خوانایی کودک تعیین شوند، نه کپی قواعد انگلیسی Duolingo.
- component contract شامل `idle / pressed / selected / correct / near_miss / incorrect / disabled / loading / offline / reduced_motion` باشد.

**رد:** Feather Bold، green Duolingo، rounded button، سفید خالی، uppercase CTA یا «کارت/مسیر را مثل تصویر» کپی نکن. در RTL، کپی‌کردن alignment چپ و arrowهای Duolingo غلط است؛ stroke direction از RTL نتیجه نمی‌شود.

### ۴.۲ شخصیت، emotion و animation

**الگوی مستند:** characterهای main با pose و expression واضح به lesson identity می‌دهند؛ پاسخ درست animation و mid-lesson interstitial دارد. ABC انیمیشن را در decoding/encoding آموزشی، snap حروف و sparkle پاسخ درست به‌کار می‌گیرد. Viseme post توضیح می‌دهد idle/response stateها باید قابل‌کنترل و صوت‌همگام باشند.

**سازگار‌سازی آوا:**
- mascot آوا «معلم/همراه» باشد، نه فروشندهٔ reward یا داور. ۴ حالت پایه: آرامِ منتظر، دعوت به replay، جشنِ پاسخ درست، کمکِ بدون شرم برای near-miss.
- animation آموزشی: هر صدای فارسی هنگام پخش روی glyph/قطعه pulse کند؛ در blending، قطعه‌ها با timing صدا کنار هم snap شوند؛ در trace، demo کوتاه پس از دو near-miss.
- جشن exactly-once پس از ثبت ledger: ۰٫۸–۲٫۵ ثانیه، بدون loop، قابل skip، reduced motion = fade/scale کم؛ audio cue هم transcript/visual cue داشته باشد.
- state machine داده‌محور با قطع‌پذیری: stop/replay، خروج و resume، audio latency fallback، offline assets؛ نه ویدئوی سنگین برای هر state.
- expression خطا: مکث/فکرکردن/اشارهٔ آرام به نقطهٔ شروع؛ چشم گریان، شرمندگی، خشم، countdown و «از دست دادی» ممنوع.

**رد:** guilt trip صریح Duo در brand guideline و Phoenix/Fire streak از main برای کودک مناسب نیست؛ sparkle مداوم attention را از literacy task می‌گیرد؛ lip-sync کامل برای هر متن فارسی فقط پس از ارزش آموزشی و pipeline صوتی معتبر.

### ۴.۳ onboarding و parent separation

**الگوی مستند:** main در Duolingo 101 انگیزه/هدف، notification، صفر یا placement، profile/save را معرفی می‌کند؛ مطالعهٔ Japan نشان می‌دهد واژهٔ «test» ترس‌آور بود و بازنویسی به «check your level» نتیجه را بهتر کرد. ABC صراحتاً child را parent setup می‌داند و age را anonymous می‌گیرد.

**سازگار‌سازی آوا:**
- S01–S04 مطابق canonical: consent و login/ownership والد در فضای parent؛ سپس انتخاب نام نمایشی کودک محلی و بستهٔ starter، بدون credential والد در child. این مسئولیت والد در setup حفظ می‌شود، اما V1 child-side `gate_mode=off` است.
- برای کودک «از کجا شروع کنیم؟» را به یک بازی صوتی ۳۰–۶۰ ثانیه‌ای تبدیل کن: شنیدن صدای آشنا، انتخاب/کشیدن glyph و بدون نمایش آزمون/fail/placement score. **S05/S05r ایستگاه‌محور باقی می‌ماند:** placement می‌تواند کودک را به station مناسب route کند؛ فقط گزینهٔ «از اول/شروع از صفر» به u00 می‌رود و placement را به u00 ختم نمی‌کند.
- برای parent توضیح کوتاه: هدف آموزشی، زمان جلسه ۵ دقیقه، offline، نوع telemetry حداقلی و مالکیت/رضایت والد. **در V1 هیچ puzzle، passcode یا challenge برای عبور کودک وجود ندارد (`gate_mode=off`)**؛ handoff به parent area صریح و جداست و login/consent والد در setup انجام می‌شود. زبان parent رسمی-مهربان؛ کودک ساده و صوتی.
- entryهای child/parent جدا و قابل‌تشخیص: یک handoff صریح به parent area (مثلاً کنترل/لینک والد طبق navigation contract)؛ **نه gesture/long-press اجباری، نه gate عددی، نه puzzle/passcode در V1**. parent report از خانهٔ کودک قابل مشاهده نیست؛ parent ownership از S01–S04 می‌آید.

**رد:** اعلان و daily goal/main streak را در onboarding کودک نگذار؛ notification فقط opt-in والد و schedule آرام؛ credential/ایمیل و نام قانونی کودک را نخواه. display name محلیِ محصول طبق canonical مجاز است، اما public/social/telemetry نباید شامل آن یا نام قانونی باشد. placement رسمی main را بدون بازطراحی محتوایی وارد نکن؛ S05/S05r station placement آوا را حفظ کن.

### ۴.۴ مسیر یادگیری و resume

**الگوی مستند:** main learning path مسیر step-by-step دارد؛ mixed concepts و practice در مسیر؛ spaced repetition؛ jump-back floating arrow؛ topic label توصیفی. ABC ۹ سطح شروع و cumulative path دارد.

**سازگار‌سازی آوا:**
- خانهٔ B یک مسیر باغ/کتاب با ایستگاه‌های u00…u15؛ فقط node بعدی + دو node context، نه endless scroll و نه چند tab.
- هر node: icon glyph/صدای هدف، label کوتاه فارسی، `locked / ready / in_progress / complete / review / mastery`; progress حلقه‌ای segment‌شده برای activityهای همان node، نه XP.
- review در مسیر با label «دوباره می‌شنویم»/«یادآوری» و بدون رنگ shattered/خراب‌شدن؛ mastery از progress جدا بماند.
- resume: کمتر از ۲۴ ساعت همان step؛ بعد از آن node از ابتدا، طبق canonical. offline interruption در همان state محلی ثبت و sync بعدی idempotent.
- «مسیر به‌عنوان contract»: ترتیب آموزشی CMS/manifest، UI فقط نمایش‌دهنده؛ node content readiness قبل از ورود.

**رد:** مسیر main را با XP، crown، gold/Legendary، قفل subscription، ranking یا endless scroll کپی نکن. بازبودن همهٔ nodeها بدون scaffold هم رد است؛ آزادی باید از داخل parent/decision release بیاید.

### ۴.۵ lesson، پاسخ درست/نادرست و feedback

**الگوی مستند:** main از آسان به دشوار می‌رود، hint و explanation دارد؛ adaptive lesson/partial credit شدت خطا را می‌سنجد؛ Adventures خطا را redirect می‌کند؛ ABC story narration کلمه را highlight می‌کند و decoding/encoding را animate می‌کند.

**سازگار‌سازی آوا:**
- `TraceCanvas`: vector/data-driven، first finger، start marker، tolerance/near-miss؛ score نشان نده. 
- بعد از پاسخ درست: glyph/صدا را یک بار replay، یک جملهٔ کوتاه «درست شنیدی» یا «صدای …» و CTA «ادامه»؛ رنگ + shape + icon.
- after near-miss: خطا را به یک cue محدود کن: «از اینجا شروع کن» یا «با هم آرام گوش بدهیم»؛ سپس یک retry با همان target. پس از دو بار، demo و allow skip/continue؛ کودک هرگز در بن‌بست نماند.
- after incorrect choice: گزینهٔ اشتباه را قرمز/ضربدر نکن؛ گزینه‌ها reset نرم، distractor را دوباره narrator کن، correct answer را با motion آموزشی reveal کن.
- پایان session: ۳ سطح completion/ستاره طبق canonical، مستقل از accuracy؛ summary کودک فقط «امروز ۲ صدای تازه شنیدی»، summary تشخیصی فقط parent.
- reading story: highlight هم‌زمان صدا و کلمه، tap-to-replay، سؤال comprehension با پاسخ‌های تصویری/شنیداری؛ progress داستان به‌عنوان mastery ثبت شود.

**رد:** هر پاسخ اشتباه = heart loss، screen shake، «غلط»، retry نامحدود، نمرهٔ accuracy، explain طولانی AI یا feedback متنی بزرگسالانه. partial credit داخلی می‌تواند برای `near_miss` استفاده شود ولی نباید به label کودک تبدیل شود.

### ۴.۶ reward، جشن و avatar personalization

**الگوی مستند:** main achievementها را به trophy shelf و personal record وصل می‌کند؛ [منبع رسمی Avatar Creator](https://blog.duolingo.com/avatar-creator/)، ۱۹ ژوئیهٔ ۲۰۲۳، avatar mix-and-match (مو، body shape، skin tone، accessory، expression) و میلیاردها ترکیب را توضیح می‌دهد؛ ABC mini-game/reward و sparkle را در listing/blog توصیف می‌کند. این منبع مربوط به main Duolingo است، نه مجوز import همهٔ گزینه‌ها به کودک.

**سازگار‌سازی آوا:**
- reward محتوایی/هویتی: برگ/صفحهٔ کتاب/قطعهٔ باغ پس از node؛ هر node فقط یک reward ledger event؛ reveal بعد از commit.
- reward choice کم‌دامنه و non-monetary: ۲–۳ رنگ/الگوی کتاب، sticker قصه، accessory mascot؛ همه از ابتدا accessible یا با progress بازشونده، نه خرید.
- avatar کودک در پروفایل child فقط presetهای امن: شکل/رنگ/کلاه/کتاب/حالت خنثی-شاد؛ no photo و no free-text public/social profile؛ parent می‌تواند reset/delete کند. **نام نمایشی کودکِ محلی** که در canonical برای child profile، child home و parent report مجاز است، حفظ می‌شود؛ این نام نباید نام قانونی/اطلاعات هویتی تلقی شود و نباید به telemetry، گزارش تشخیصی/تحلیلی یا public/social surface ارسال شود.
- جشن قابل skip و بدون share بیرونی؛ parent می‌تواند intensity/صدا/animation را کم کند.

**رد:** trophy shelf بر اساس XP، longest streak، league یا «کمیاب‌ترین badge» فشار اجتماعی ایجاد می‌کند؛ gem currency، shop، loot/random reward و avatar cosmetic پولی مناسب ۴–۷ سال نیست؛ happy/sad face که ارزش کودک را به accuracy وصل کند ممنوع.

### ۴.۷ parent، privacy، accessibility و RTL

**الگوی مستند:** Privacy رسمی ABC را parent-setup، age anonymous، بدون personal info کودک و speech data روی phone توصیف می‌کند؛ main child نیز username غیرواقعی/محدودیت social/ads safe دارد. App Store ABC صریحاً می‌گوید accessibility features را اعلام نکرده است؛ این یک خلأ عمومی است، نه اثبات inaccessible بودن.

**سازگار‌سازی آوا:**
- parent boundary: گزارش، حذف/reset، download/space، consent، settings و entitlement فقط در parent area؛ child فقط feedback یادگیری. V1 `gate_mode=off`: handoff به parent area صریح است، اما هیچ puzzle/passcode/challenge کودک‌محور ندارد؛ parent consent/login و ownership در setup اولیه انجام می‌شود.
- حداقل داده: **display name کودک در حد local/product UI مجاز است** و در child profile/home و parent report می‌تواند دیده شود؛ اما نام قانونی، display name و شناسهٔ قابل‌ربط نباید در telemetry، diagnostic/analytics یا public/social surface ارسال شوند؛ eventها anonymous/idempotent؛ audio کودک share نشود؛ transcript replay local؛ delete واقعی و قابل‌ردیابی.
- accessibility: همهٔ instructionها audio + transcript؛ replay labelled؛ caption برای narration؛ target کودک ۶۴/۷۲dp؛ contrast با Persian glyph واقعی ≥۴٫۵:۱ برای متن معمولی؛ state با icon/shape/text نه رنگ؛ reduced motion و mute مستقل.
- RTL: progress، tabs، text و navigation راست‌به‌چپ؛ canvas stroke از glyph contract می‌آید، نه mirror. ا/آ/د/ر و اتصال‌ها direction note و validator داشته باشند.
- sensory: no flashing، no autoplay loop، volume مستقل SFX/voice؛ screen-reader semantics در parent؛ کودک کم‌سواد با narration وابسته به متن نشود.

**رد:** social graph، leaderboard، comments/free text، contact sync، profile photo، child-facing report، speech upload، dark pattern parent gate. «زیر ۱۳ در main محدود است» مجوزی برای import social نیست؛ آوا هدف ۵–۶ است و baseline باید stricter باشد.

### ۴.۸ offline، download و monetization

**الگوی مستند:** ABC صفحهٔ رسمی و App Store را no Wi‑Fi/offline، ad-free/no IAP معرفی می‌کند؛ main app-size مقاله bundle asset را راهی برای consistency در poor connection/offline می‌داند، اما D101 و Energy اشتراک، hearts/energy، rewarded ad و gem refill را توضیح می‌دهند. آزمایش هند نشان داد promotion اولیهٔ اشتراک می‌تواند با paywall اشتباه شود و retention/DAU را پایین آورد.

**سازگار‌سازی آوا:**
- قبل از محتوای جدید `DownloadState`: preflight → manifest → download → Range/resume → verify hash/signature → atomic install → ready؛ percent بر اساس required bytes؛ قطع/پس‌زمینه resume.
- APK دارایی‌های u00 و S05/S05r را داشته باشد؛ دانلود شروع شود اما تعیین سطح پیش‌نیاز آن نباشد؛ نبود بسته فقط S22/parent path است.
- child همیشه offline وارد node آماده شود؛ در network unavailable پیام فنی نده: «این بخش آماده نیست؛ یک داستان آماده را انتخاب کنیم»؛ parent می‌تواند فضا/Retry را ببیند.
- V1 رایگان u00–u03 طبق canonical؛ اگر entitlement لازم شد، فقط بعد node و در parent route، هیچ paywall وسط lesson یا جشن. parent route با handoff صریح و `gate_mode=off` در child همراه است، نه با puzzle/passcode.
- asset pipeline: vector/atlas/compression و حذف unused؛ اما audio/narration داخل package باید atomic و قابل‌اعتماد باشد.

**رد:** heart/energy loss، ad برای ادامه، gem refill، Super/Max offer در child، paywall قبل از جشن، «اینترنت وصل نیست» به‌عنوان خطای کودک. از عددهای DAU هند برای آوا نتیجهٔ قطعی monetization نساز؛ فقط warning دربارهٔ comprehension/retention است.

---

## ۵) قرارداد پیشنهادی component/state برای handoff

| component | stateهای لازم | رفتار آوا | anti-pattern |
|---|---|---|---|
| `ChildTopBar` | ready, in_progress, offline, paused | خروج، segment progress، replay راوی؛ label صوتی | streak/heart/XP/coin |
| `StationNode` | locked, ready, active, complete, review, mastery | label هدف، icon صدای target، review بدون shame | shattered، ranking، paywall lock |
| `ChoiceCard` | idle, pressed, selected, correct, near_miss, incorrect, disabled | border/shape/icon + narration؛ error نرم | رنگ تنها، red X بزرگ، shake |
| `TraceCanvas` | demo, awaiting_first_touch, tracing, near_miss, correct, retry, skipped | marker شروع، tolerance، cue یکتا، demo بعد ۲ خطا | accuracy number، mirror glyph، timeout |
| `AudioCue` | idle, loading, playing, paused, replay, unavailable | voice همیشه available در package؛ transcript؛ fallback visual | autoplay loop، فقط sound بدون parity |
| `FeedbackSheet` | correct, redirect, retry, demo, continue | پاسخ درست را مدل کن؛ near-miss را قابل‌بازگشت کن | «غلط»، heart loss، blame mascot |
| `RewardSheet` | pending, committed, revealed, skipped | ledger قبل reveal؛ یک جشن کوتاه؛ skip/reduced motion | duplicate reward، lootbox، خرید |
| `AvatarPicker` | preset grid, selected, saved, reset | preset امن، no photo/free text، parent reset | cosmetic purchase، social comparison |
| `ParentAreaHandoff` | child_surface, handoff, parent_area, return | handoff صریح به parent area؛ parent-only برای تنظیم/گزارش/حذف/دانلود؛ V1 `gate_mode=off`، بدون puzzle/passcode/challenge | credential والد در child، gate پنهان یا dark pattern |
| `DownloadState` | preflight, manifest, downloading, paused, verify, install, ready, error_parent | کودک فقط انتخاب محتوای ready؛ جزئیات در parent | technical error child، partial install |
| `StoryReader` | narrator, reading, tap_replay, question, complete | word highlight sync، comprehension تصویری، audio transcript | متن طولانی، سؤال نمره‌محور |

### state invariantهای حیاتی
1. `reward.reveal` فقط پس از `ledger.commit` و exactly-once.
2. `incorrect` نباید `progress -=` یا `mastery -=` را در child ایجاد کند؛ review event جداست.
3. `offline` نباید lesson آماده را قفل کند؛ هر node readiness باید در manifest باشد.
4. هر audio instruction یک `replay` و transcript/cue دارد؛ reduced-motion و mute مسیر جایگزین‌اند.
5. `parent` هرگز child result را در همان صفحهٔ جشن نمایش ندهد؛ report در parent route و privacy-safe.

---

## ۶) اولویت‌های actionable قبل از prototype

### Gate A — محتوایی/ایمنی
- برای هر u00…u15، mapping رسمی glyph، phoneme، مثال فارسی، stroke start/order/tolerance، replay audio و near-miss cue؛ هرگز glyph را با RTL mirror نکن.
- تعریف دقیق «mastered» و «learned» همان canonical؛ accuracy را از celebration کودک جدا کن.
- فهرست بسته‌های offline، حجم، hash/signature، حداقل APK و S22 parent-only.
- privacy contract: دادهٔ local، event anonymization، speech never upload، delete/reset، parent consent.

### Gate B — interaction/state prototype
- چهار slice: onboarding parent/child، station ready→lesson، trace near-miss→demo، node complete→reward ledger.
- golden viewportهای ۳۲۰×۵۶۰، ۳۶۰×۸۰۰، tablet max ۶۰۰؛ Persian font scale کودک ×۱٫۲ و parent ×۱٫۶؛ reflow/ellipsis ممنوع برای prompt.
- تست کودک/والد با prototype بدون login واقعی، بدون paywall و بدون network؛ measure comprehension/comfort، نه فقط completion.

### Gate C — pilot و visual lock
- گروه ۵–۶ با guardian؛ معیارها: completion بدون شرم، فهم instruction با/بدون صدا، replay discovery، near-miss recovery، stroke validity، memory در review، خطای offline، parent clarity.
- A/B داخلی فقط برای layout/cue در صورت approval، نه آزمایش streak/monetization روی کودک.
- visual lock بعد از محتوای Gate A، pilot و audit contrast/RTL/audio؛ نمونه‌های static فعلی reference-only هستند.

---

## ۷) چیزهایی که عمداً import نمی‌شوند

1. `Hearts/Energy`: خود Duolingo در ژوئیهٔ ۲۰۲۵ نوشت heart برای beginnerها discouraging بوده؛ آوا حتی نسخهٔ battery را هم وارد نمی‌کند.
2. `Streak loss / Freeze`: مسیر آوا باید بازگشت بدون شرم داشته باشد؛ استمرار را parent می‌بیند، کودک badge روزانه نمی‌بازد.
3. `Leaderboards/Leagues/Friends Quests`: Help رسمی main رقابت و promotion/demotion را توصیف می‌کند و زیر ۱۳ را محدود می‌سازد؛ برای ۵–۶ نیاز نیست.
4. `Gems/shop/loot/random cosmetics`: خرید و scarcity کودک را از هدف خواندن منحرف می‌کند.
5. `Duo guilt/shame`: در brand guideline main صریحاً آمده، اما با قرارداد «بدون شرم» آوا ناسازگار است.
6. `All-caps English CTA، Feather Bold، green palette، exact layout`: قواعد brand main، نه استاندارد universal کودک فارسی.
7. `Adult placement test / AI explain`: می‌توان backend signal و cue را استفاده کرد، ولی کودک باید بازی/راهنمای کوتاه و deterministic ببیند.
8. `Social profile/photo/free text`: با parent/child privacy و سن مخاطب ناسازگار است؛ این رد به public/social surface مربوط است و display name محلیِ مجاز در child profile/home و parent report را حذف نمی‌کند.

---

## ۸) محدودیت‌ها و موارد نیازمند راستی‌آزمایی مستقیم

- نسخهٔ لاگین‌شدهٔ Duolingo اصلی و ABC در این پژوهش باز نشد؛ بنابراین current rollout هر screen، ترتیب دقیق onboarding، stateهای paywall و اختلاف iOS/Android تأیید نشده است.
- App Store listing رسمی ABC اطلاعات version 1.96.2 (۲ اوت ۲۰۲۳) را نشان می‌دهد؛ برای وضعیت فعلی، listing را توصیفی و نه UI current تلقی کن.
- صفحات عمومی official ABC دربارهٔ parent dashboard تفصیلی مثل canonical آوا ارائه نمی‌دهند؛ privacy page فقط parent setup، age anonymous و data boundary را مستند می‌کند. parent flow آوا باید از نیاز محصول/قانون محلی و تست guardian بیاید، نه ادعای parity با ABC.
- App Store می‌گوید accessibility featureهای ABC هنوز توسط developer اعلام نشده؛ این «شکاف مستندسازی» است، نه قضاوت قطعی دربارهٔ implementation.
- ادعای EDC/۲۸٪ در صفحهٔ ABC و هر metric retention/DAU متعلق به محصولات/آزمایش‌های Duolingo است؛ برای اثربخشی فارسی یا AVA قابل‌تعمیم نیست.
- Privacy page هنگام خواندن تاریخ بازبینی ۲۶ مهٔ ۲۰۲۶ نشان داد؛ چون سیاست ممکن است تغییر کند، قبل از shipping باید snapshot/legal review دوباره انجام شود.
- رنگ‌های Duolingo و guidance typography از brand guideline گرفته شده‌اند؛ هیچ رنگ/فونت Duolingo نباید به‌عنوان approval برند آوا تلقی شود.

## نتیجهٔ اجرایی

بهترین «الهام» برای آوا، **ABC در مرزبندی کودک/والد، درس کوتاه، داستان/صوت، offline و animation آموزشی** به‌همراه **main Duolingo در مسیر روشن، تمرین فاصله‌دار، feedback قابل‌فهم، character state machine و discipline طراحی vector** است. اما reward باید از accuracy/streak جدا، feedback باید redirect و recovery محور، parent باید تنها محل داده/پرداخت، و Persian RTL/glyph/stroke باید منبع حقیقت باشد. هر تصمیم تصویری بعد از Gate A و pilot معتبر است، نه صرفاً چون در screenshot یا blog Duolingo جذاب دیده می‌شود.
