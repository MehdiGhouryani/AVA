# AVA Design System (R01) — توکن‌ها و کامپوننت‌های پایه

**نسخه:** ۱٫۰ · **تاریخ:** ۲۰۲۶-۱۱-۰۷ · **شاخه:** `062-r01-design-system` (از `b70b7f9` روی `062-ux-discovery`)
**مبنای طراحی:** `docs/design/AVA_UX_DISCOVERY.md` (فاز ۰۶۲) · **مبنای کد:** `2f67ba8` با گیت ۱۴۳/۰
**قاعدهٔ برش:** R01 فقط توکن‌ها و کامپوننت‌های پایه را اضافه می‌کند؛ هیچ صفحه‌ای تغییر ظاهر نداده است و مهاجرت صفحه‌به‌صفحه در R02..R13 انجام می‌شود.

## 1. Design Principles

1. **هویت ثابت:** پالت سبز/کرم/کاغذی، Vazirmatn و artwork محلی تنها زبان بصری AVA است؛ هیچ رنگ/فونت تازه‌ای وارد نشده است.
2. **توکن = حقیقت واحد:** مقادیر فقط در `ui/theme/AvaTokens.kt` (غیررنگی) و `ui/theme/DesignTokens.kt` (رنگ خام، تولیدی از `design/` توسط `scripts/sync-android-resources.py`) تعریف می‌شوند. صفحه‌ها و کامپوننت‌ها عدد جادویی نمی‌نویسند.
3. **بدون خطای قرمز:** خطا/هشدار فقط کهربایی (`amber`) است؛ موفقیت سبز عمیق.
4. **لمس کودک:** هدف ۶۴dp، CTA اصلی ۷۲dp (DG-3)، حداقل Android ۴۸dp، فاصلهٔ اهداف ≥۸dp.
5. **دسترس‌پذیری پیش‌فرض:** semantics/liveRegion/Role در کامپوننت‌ها، نه در صفحه‌ها؛ وضعیت هرگز فقط با رنگ.
6. **حرکت هدفمند:** کوتاه، یک‌بار، قابل‌حذف با `motionMs()` (کاهش حرکت درون‌اپ یا سیستم).
7. **سازگاری تکاملی:** کامپوننت‌های موجود `ui/screens/Components.kt` دست‌نخورده می‌مانند تا هر صفحه مهاجرت کند.

## 2. Color Tokens

رنگ‌های خام در `DesignTokens.kt` تولیدی‌اند (ویرایش دستی ممنوع). معناها در `AvaSemanticColors` مشتق شده‌اند — بدون رنگ خام تازه.

| نقش معنایی | توکن | مقدار | مصرف |
|---|---|---|---|
| Primary | `AvaColors.green` (`evergreen`) | `0xFF14766A` | CTA، انتخاب، حلقهٔ فوکوس/انتخاب |
| Primary (تأکید/موفقیت) | `AvaSemanticColors.success` | `0xFF0A5C54` | تیک موفقیت، متن تأکید |
| Primary Container | `AvaSemanticColors.successContainer` (`line`) | `0xFFC7DDD2` | زمینهٔ موفقیت، گزینهٔ انتخاب‌شده |
| Secondary | `AvaColors.deep` (`evergreen_deep`) | `0xFF0A5C54` | متن/آیکون تأکیدی |
| Background | `AvaColors.paper` (`paper`) | `0xFFEDF6F2` | پس‌زمینهٔ صفحه (باغ) |
| Surface | `AvaColors.surface` (`surface`) | `0xFFFBFDF8` | کارت پیش‌فرض |
| Surface Variant | `AvaColors.cream` (`cream`) | `0xFFFEF7E1` | کارت اصلی/انتخاب‌شده، نوار پایین |
| Text Primary | `AvaColors.ink` (`ink`) | `0xFF233D47` | متن اصلی |
| Text Secondary | `AvaColors.soft` (`text_soft`) | `0xFF395965` | متن ثانویه |
| Text Disabled | `AvaColors.disabledText` | `0xFF625F50` | فقط روی `disabled_bg` |
| Success | `AvaSemanticColors.success` | بالا | بازخورد موفق |
| Warning/Error | `AvaColors.amber` + `amberTint` | `0xFFE3900F`/`0xFFFDEEC9` | بازخورد خطا/هشدار — بدون قرمز |
| Progress | `AvaSemanticColors.progress`/`progressTrack` | `14766A`/`C7DDD2` | نوار پیشرفت |
| Border | `AvaColors.line` (`line`) | `0xFFC7DDD2` | حاشیهٔ کارت/گزینه |
| Focus / Selected Ring | `AvaSemanticColors.focus`/`selectedRing` | `14766A` | حلقهٔ ۳dp انتخاب/فوکوس |
| Scrim | `AvaSemanticColors.scrim` | ink@45% | پس‌زمینهٔ دیالوگ (آینده) |
| Interactive States | pressed scale .98؛ selected=`line` bg + ring ۳dp؛ disabled=`disabled_bg/text` | — | در همهٔ کامپوننت‌های R01 |

**کنتراست جفت‌های واقعی (WCAG 2.2، محاسبه از کد):** همه ≥۴٫۵ برای متن عادی — white/evergreen ۵٫۴۸ · ink/paper ۱۰٫۴۲ · ink/surface ۱۱٫۲۱ · ink/cream ۱۰٫۷۱ · deep/paper ۷٫۱۴ · muted/paper ۵٫۰۹ · soft/surface ۷٫۳۵ · soft/cream ۷٫۰۳ · amberText/amberTint ۵٫۷۰ · deep/line ۸٫۰۳. غیرمتنی: focus ring روی paper ۴٫۹۸ (هدف ≥۳ ✓). جفت disabled ‏۴٫۷۷ (معاف از AA). حالت‌ها با آیکون/شکل/`stateDescription` هم‌زمان با رنگ بیان می‌شوند.

## 3. Typography

فونت: Vazirmatn (فایل فعلی `android/app/src/main/res/font/vazirmatn.ttf` — تک‌وزن). **در R01 وزن جدید اضافه نشد:** طبق مأموریت، افزودن وزن منوط به تأیید منبع/مجوز است (تصمیم باز F5 سند Discovery؛ فایل رسمی Vazirmatn ‏OFL تنها مسیر مجاز است). تا آن زمان `FontWeight.Bold/SemiBold` مصنوعی رندر می‌شود — ثبت‌شده به‌عنوان محدودیت شناخته‌شده، نه الگوی تازه.

| نقش | اندازه/وزن | line-height | مصرف | محل تعریف فعلی |
|---|---|---|---|---|
| Display | 40 / Bold | 1.5 | لحظهٔ جشن/نتیجه (آینده) | `AvaTokens` doc؛ هنوز در MaterialTheme نیست |
| Headline L/M/S | 32/28/24 Bold | 1.7 | عنوان‌های بزرگ | `AvaTheme.kt` (بدون تغییر) |
| Title L/M/S | 22 SemiBold / 18 Medium / 16 Medium | 1.7 | عنوان کارت/هدر | همان |
| Body L/M/S | 17/16/14 | 1.7 | متن فارسی | همان |
| Label/Button | 18 SemiBold (دکمه)، 14، 12 | 1.7 | دکمه/برچسب/شمارنده | همان |
| Caption | bodySmall در رنگ `soft`/`muted` | — | یادداشت کم‌اهمیت | الگو |

قواعد: اعداد فارسی با `fa()` (`content/Models.kt:85`)؛ ارتفاع خط ۱٫۷ برای فارسی ثابت می‌ماند؛ ترکیب فارسی/انگلیسی با `TextDirection` واقعی (الگوی موجود Phone: ‏`textDirection=Ltr`)؛ در fontScale بزرگ فقط چیدمان تطبیق می‌کند (compact >1.15)، نه اندازهٔ فونت.

## 4. Spacing — `AvaSpacing`

| توکن | مقدار | مصرف |
|---|---|---|
| `xxs` | 4 | جزئیات درونی |
| `xs` | 8 | فاصلهٔ آیکون-متن، **gap اهداف لمسی نزدیک** |
| `sm` | 12 | فاصلهٔ کارت-کارتهای درون‌گروهی |
| `md` | 16 | padding استاندارد بلوک |
| `lg` | 24 | padding بخش/دکمه افقی |
| `xl`/`xxl`/`xxxl` | 32/40/48 | جداکنندهٔ بخش‌های بزرگ |
| `page` | 20 | **استثنای مستند:** حاشیهٔ صفحهٔ فعلی (تا مهاجرت R02+) |
| `touchGap` | 8 | حداقل فاصلهٔ دو هدف لمسی |

## 5. Shape — `AvaShapes` (۵ سطح، جایگزین ۱۰ مقدار پراکنده)

| سطح | Radius | کاربرد | نگاشت مقادیر فعلی |
|---|---|---|---|
| small | 12 | چیپ آیکون، فکت‌ها | 8 (فقط برای چیپ‌ها) |
| medium | 18 | توکن واژه، کاشی درون‌خطی | 18 |
| large | 24 | گزینه‌ها، کارت ثانویه، دیالوگ | 20/22/24 |
| xlarge | 32 | SoftCard، گرهٔ مسیر، کارت اصلی | 26/28/32 |
| pill | 100 | دکمه، حباب، نقطه، **نوار پیشرفت** | 100/50% و 8 (نوارها) |

Border استاندارد: ۱dp `line` (حالت عادی) ← ۲dp/۳dp `selectedRing` (انتخاب؛ گزینه‌های درون‌صفحه ۲dp، کارت/کاشی بزرگ ۳dp — مطابق الگوی موجود).

## 6. Elevation — `AvaElevation`

`flat=0` (نوار پایین، گزینه‌ها) · `card=1` (کارت‌ها) · `raised=3` (کارت قصه) · `sheet=6` (شیت OTP). سایهٔ سنگین ممنوع؛ عمق از رنگ/حاشیه می‌آید (clay نرم).

## 7. Motion — `AvaMotion` + `motionMs()`

| الگو | توکن | reduced motion |
|---|---|---|
| فشار دکمه (scale .98) | `press=150` | 0 (`motionMs`) — مطابق `Components.kt:50` |
| ورود/خروج بازخورد | `feedback=200` | 0 |
| پر شدن پیشرفت | `progress=300` | سوییچ آنی |
| پاپ کشف | `celebrate=600` | 0 — مطابق `AlbumScreen.kt:34` |
| سقف جشن تکمیل | `celebrationMax=3000` | حذف کامل |
| شناوری حباب | 2600+500n (موجود) | ثابت — استثنای موجود، افکت بی‌پایان تنها |

قواعد: هیچ انیمیشن بی‌پایان تازه؛ هیچ انیمیشنی جلوی اقدام بعدی را نمی‌گیرد؛ مرجع کاهش حرکت فقط `LocalReducedMotion` (`AvaApp.kt:17-28`).

## 8. Component Catalog — `ui/components/AvaUI.kt`

۹ کامپوننت پایه (همه RTL-safe، با semantics؛ متن از پارامتر صفحه می‌آید — بدون رشتهٔ هاردکد):

| کامپوننت | حالت‌ها | ابعاد | Semantics | نکته |
|---|---|---|---|---|
| `AvaButton` | Primary/Secondary/Tertiary × enabled/disabled/loading | 72 (Primary) / 64 dp | دکمهٔ متن، `labelLarge` | loading اسپینر (بدون reduced motion) + غیرفعال |
| `AvaIconButton` | enabled/disabled | 64dp (حداقل 48 Android) | `contentDescription` **الزامی** (`require`) | آیکون بدون برچسب ممنوع |
| `AvaCard` | Default/Interactive/Selected/Disabled | min 64dp در Interactive | `Role.Button` + `stateDescription` اختیاری | حلقهٔ ۳dp انتخاب |
| `AvaHeader` | با/بدون بازگشت | 64dp | `heading()` + برچسب الزامی بازگشت | `AutoMirrored` ← RTL خودکار |
| `AvaProgress` | 0..1 + برچسب | 8dp pill | `ProgressBarRangeInfo` + `contentDescription` برچسب | تنها نوار پیشرفت از این پس |
| `AvaFeedback` | Success/Info/Attention | — | `liveRegion=Polite` (merge) | موفق=سبز/خطا=کهربایی؛ آیکون + رنگ + متن |
| `AvaAnswerOption` | selected/unselected/disabled | min 64dp | `Role.RadioButton` + Selected | تجمیع ۴ پیاده‌سازی گزینه در مهاجرت |
| `AvaCharacterMessage` | — | آواتار 44dp | متن پیام (تصویر تزئینی) | آواتار+حباب کرم |
| `AvaStateMessage` | خالی/خطا + اقدام واحد | — | متن عنوان/بدنه | الگوی `empty_practice` تعمیم‌یافته |

## 9. Accessibility

- لمس: 48dp حداقل Android، 64dp کودک، 72dp CTA — در کامپوننت‌ها اعمال (`heightIn`/`size`).
- فاصلهٔ اهداف: `touchGap=8`؛ گروه گزینه‌ها با `selectableGroup`.
- Semantics: `Role` (Button/RadioButton)، `stateDescription` فارسی، `liveRegion=Polite` برای بازخورد/پیشرفت، `heading()` برای عنوان‌ها، برچسب الزامی آیکون.
- فوکوس: ترتیب DOM = ترتیب خواندن RTL (کامپوننت‌ها از Row/Column استاندارد؛ هیچ ترتیب دستی).
- فونت بزرگ: `fontScale` 1/1.3/1.5/2 — تست `componentsSurviveLargeFontInRtl` (1.5) و Preview `fontScale=1.5`؛ 2.0 روی دستگاه (QA ۰۸۰).
- کنتراست: جدول بخش ۲؛ وضعیت‌ها رنگ+آیکون+`stateDescription`.
- TalkBack روی دستگاه: به human gate `080-device-accessibility-qa` واگذار (NOT VERIFIED در R01).

## 10. RTL Rules

1. فقط APIهای جهت-آگاه: `Row/Column`، `start/end`، `TopEnd/BottomEnd`؛ هیچ `Left/Right` دستی.
2. آیکون‌های جهت‌دار فقط `AutoMirrored` (`ArrowBack`).
3. استثناهای LTR مستندشده فقط برای ورودی عدد/شماره (الگوی `PhoneScreen`/`OtpField`) — R01 چیزی اضافه نکرد.
4. نوار پیشرفت و حلقه‌ها M3 خودشان RTL می‌شوند؛ تست RTL در `AvaComponentsTest` همهٔ کامپوننت‌ها را در `LayoutDirection.Rtl` رندر می‌کند.

## 11. Preview Gallery

انتهای `AvaUI.kt`: تابع `AvaComponentGallery(fontScale, width)` — هر ۹ کامپوننت در همهٔ حالت‌ها، RTL و فارسی، با چهار بخش (دکمه‌ها، کارت‌ها و گزینه‌ها، بازخورد و وضعیت‌ها، هدر و پیشرفت). **شواهد رندر واقعی:** تست `componentGallerySnapshotRendersEveryState` گالری را روی Robolectric رندر و در مسیر build/reports/native-ui (خروجی build، بدون track در مخزن) ثبت می‌کند (همان سازوکار snapshot سند `android/README.md`)؛ یک تست جدا در عرض/فونت ۳۲۰dp/۱٫۵ هم رندر سالم را می‌سنجد. **`@Preview` عمداً حذف شد:** وابستگی `androidx.compose.ui:ui-tooling-preview:1.8.0` روی این ماشین قابل دریافت نیست (خطای TLS handshake آینهٔ maven — همان شبکهٔ بلاک‌شدهٔ ثبت‌شده در WORKLOG) و نسخهٔ 1.12.0 موجود در کش با BOM فعلی ناسازگار است؛ افزودن annotation بعداً فقط یک import تغییر است. پیش‌فرض‌های گالری: RTL، متن فارسی، متن چندخطی، حالت Disabled، فونت ۱٫۵، عرض ۳۲۰.

## 12. Migration Guide (Existing → Proposed)

| الگوی فعلی (`ui/screens/Components.kt`/صفحه‌ها) | جایگزین R01 | اقدام مهاجرت |
|---|---|---|
| `Primary` (72dp pill) | `AvaButton(Primary)` | R02+ صفحه‌به‌صفحه |
| `Secondary` (64dp outline) | `AvaButton(Secondary)` | همان |
| `TextButton` ۶۴dp کودک | `AvaButton(Tertiary)` | همان |
| `Header` | `AvaHeader` (برچسب بازگشت صریح) | همان |
| `SoftCard` | `AvaCard` (حالت‌ها) | همان |
| `Feedback` | `AvaFeedback` (tone) | همان |
| `LessonChoice`/`SceneTile`/`BubbleChoice`/`StoryChoice` | `AvaAnswerOption` | R04/R07 (با تست رفتار) |
| نوار پیشرفت 4/6/7/8/9dp | `AvaProgress` | در هر صفحه هنگام مهاجرت |
| الگوی `empty_practice` | `AvaStateMessage` | R09 |
| حباب سلام آوا در Home | `AvaCharacterMessage` | R02 |
| `IconButton` 64dp (بازگشت/آواتار) | `AvaIconButton` | R02+ |
| شعاع‌های 8/18/20/22/24/26/28/32/100/50% | ۵ سطح `AvaShapes` (جدول بخش ۵) | هنگام مهاجرت هر صفحه |

قواعد مهاجرت: هر برش حداکثر یک صفحه/خانواده؛ کامپوننت قدیمی حذف نمی‌شود تا آخرین مصرف‌نده منتقل شود؛ تغییر رشته‌ها = به‌روزرسانی هم‌زمان تست‌ها؛ هر برش گیت کامل (testDebugUnitTest+lintDebug+audit) و ثبت WORKLOG.

## 13. شواهد و محدودیت‌های R01

- `ran`: گیت `gradlew.bat testDebugUnitTest lintDebug -Pkotlin.compiler.execution.strategy=in-process --no-daemon` با فلگ‌های ماشین (`_JAVA_OPTIONS=-Djava.net.preferIPv4Stack=true`، `JAVA_TOOL_OPTIONS=-Drobolectric.dependency.dir=D:/robolectric-deps`) — نتیجهٔ واقعی (شمار تست‌ها + لینت) در `docs/PROGRESS_FA.md` و `docs/NATIVE_VERIFICATION.json` ثبت شده است.
- `ran`: رندر اسنپ‌شات گالری در خروجی build زیر reports/native-ui (خروجی build است و در مخزن track نمی‌شود).
- **بدون تغییر وابستگی Gradle:** تلاش افزودن `ui-tooling-preview` به‌دلیل بلاک بودن دانلود حذف شد (خطای دقیق: `Could not resolve androidx.compose.ui:ui-tooling-preview:1.8.0 … Remote host terminated the handshake`).
- `NOT VERIFIED`: پنل Preview اندروید استودیو (نیازمند ui-tooling-preview)، TalkBack دستگاه، fontScale ‏2.0 رندر واقعی، وزن‌های واقعی فونت (تصمیم باز مالک)، مقایسهٔ پیکسلی اسنپ‌شات (گیت «gold» وجود ندارد و عمداً ساخته نشده است).
