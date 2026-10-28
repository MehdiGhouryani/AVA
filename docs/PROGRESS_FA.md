# وضعیت واقعی AVA — سورس ۰٫۶٫۱

فاز ۰٫۶٫۱ اکنون علاوه بر برش واقعی محتوای سطح ۱، پروژهٔ مستقل Payload و هارنس ادامهٔ کار دارد. مسیر توسعه‌ای ۳ واحد، ۶ درس و ۱۷ فعالیت است. CMS شامل PostgreSQL، adapter خصوصی S3، پنج collection، نقش‌های شش‌گانه، قرنطینه و دو بازبینی صوت، audit و export قطعی است. مرز master/delivery fail-closed است و ۱۸ تست دامنه CMS موفق‌اند. با درخواست صریح مالک، مسیر ساخت APK فعال و اسکریپت یک‌مرحله‌ای build/verify افزوده شد؛ اما APK در محیط حاضر تولید نشد، زیرا Android SDK/Build Tools وجود ندارد و Gradle 8.11.1 نیز به علت `Network is unreachable` قابل دریافت نیست. امتیاز مهندسی عمداً افزایش نیافته است.

## خلاصهٔ قابل سنجش

- پیشرفت کل نقشهٔ ۱۰۰ امتیازی: **۴۵ از ۱۰۰ (۴۵٪)**.
- پیشرفت تا محدودهٔ پایان فاز ۰٫۶٫۱: **۴۵ از ۵۵ امتیاز (۸۲٪)**.
- خود فاز ۰٫۶٫۱: **۸ از ۱۰ امتیاز**.
- دو امتیاز باز این فاز میان سه گیت تقسیم می‌شوند: integration واقعی DB/object storage، تأیید متخصص آموزش/صوت و compile/QA Native.
- درصدها شواهد مهندسی‌اند؛ آمادگی انتشار یا اثربخشی آموزشی را نشان نمی‌دهند.

## چک‌لیست فازبندی

| فاز | وزن | بسته‌شده | وضعیت | کار باز اصلی |
|---|---:|---:|---|---|
| مبنا و معماری محتوا — ۰٫۴٫۲ | ۱۰ | ۶ | 🟡 | ابزار/دستگاه/تأیید آموزشی |
| قرارداد content v3 — ۰٫۵٫۰ | ۱۰ | ۹ | 🟡 | اجرای Native |
| runtime محتوای محلی — ۰٫۵٫۱ | ۱۰ | ۹ | 🟡 | persistence دیسک و اجرای Native |
| موتور فعالیت v3 — ۰٫۶٫۰ | ۱۵ | ۱۳ | 🟡 | compile و QA دستگاه |
| محتوای سطح ۱ + CMS spike — ۰٫۶٫۱ | ۱۰ | ۸ | 🟡 | integration DB/storage، review آموزشی/صوت و QA |
| CMS MVP و workflow صوت — ۰٫۷٫۰ | ۱۵ | ۰ | ⬜ | پنل، upload، quarantine، QC، audit و release آزمایشی |
| همگام‌سازی offline-first — ۰٫۷٫۱ | ۱۰ | ۰ | ⬜ | persistence، دانلود، verify، activation و rollback |
| غنی‌سازی، QA و انتشار — ۰٫۸٫۰+ | ۲۰ | ۰ | ⬜ | محتوای کامل، دستگاه، حقوقی، حساب و release |
| **جمع** | **۱۰۰** | **۴۵** | **۴۵٪** | APK/AAB عمداً ساخته نشده است |

## چک‌لیست انجام‌شده در ۰٫۶٫۱

- [x] نسخه اپ ۰٫۶٫۱ و versionCode 16.
- [x] ترتیب سه‌مرحله‌ای سطح ۱: نشانه‌ها → صداهای واژه → واژه‌های نخست.
- [x] دو درس تازه: «اول و آخر واژه» و «صداها کنار هم».
- [x] پنج فعالیت تازه: دو InitialSound، یک FinalSound و دو Blend.
- [x] تغییر معنایی دو فعالیت نقطه از Recognize به DotFamily بدون تغییر ID.
- [x] افزایش دامنه از ۴ درس/۱۲ فعالیت به ۶ درس/۱۷ فعالیت؛ ۲ قصه/۹ صفحه ثابت.
- [x] اصلاح صوت فعالیت واجی: `writtenFallback` واژهٔ محرک است و target پاسخ.
- [x] TTS/hint برای «بابا»، «مادر» و «آب» به‌جای لو‌دادن پاسخ حرف.
- [x] سه نیاز صوت تازه؛ inventory اکنون ۲۲ asset و ۱۲ prompt فعالیت دارد.
- [x] migration قطعی v2→canonical ۰٫۶٫۱ و حفظ IDهای قدیمی.
- [x] strict JSON loader و رد کلید تکراری در قراردادهای release.
- [x] قرارداد `cms-export-v1` مستقل از Strapi/Payload.
- [x] شش نقش و پنج separation rule برای جلوگیری از self-approval.
- [x] fixture خروجی Payload و validator تطبیق محتوا/صوت.
- [x] چهار blocker واقعی publication: محتوای تأییدنشده، صوت تأییدنشده، export جزئی و candidate ناآماده.
- [x] مقایسه منابع رسمی و انتخاب مشروط Payload برای prototype؛ انتخاب production هنوز مشروط است.
- [x] پروژهٔ مستقل Payload 3.92.1 با نسخه‌های دقیق، lockfile، PostgreSQL و adapter رسمی S3.
- [x] پنج collection واقعی: Users، CurriculumItems، AudioAssets، Releases و AuditEvents.
- [x] bootstrap امن مدیر نخست، کنترل دسترسی شش‌نقشی و فیلدهای server-managed غیرقابل‌دستکاری از API.
- [x] upload صوت با allowlist، سقف ۲۵ MiB، تشخیص magic WAV/MP3/M4A و SHA-256 در `beforeValidate`.
- [x] workflow قرنطینه → review فنی → review آموزشی → تأیید، با ممنوعیت self-review و reviewer تکراری.
- [x] release gate، audit append-only در سطح API و export قطعی بر پایه stable ID.
- [x] موفقیت ۱۸ تست CMS، type-check، تولید payload-types و production build با routeهای Admin/REST/GraphQL.
- [x] بستن شکاف master/delivery: inspection پیش از review و delivery آماده/hash معتبر پیش از release.
- [x] export صوت فقط از delivery metadata؛ filename/hash فایل master قرنطینه وارد خروجی اپ نمی‌شود.
- [x] افزایش تست‌های دامنه CMS از ۱۶ به ۱۸ برای inspection و delivery gate.
- [x] هارنس tool-neutral برای Zcode/Codex/Claude: ۱۴ task، ۴ فاز، dependency بدون چرخه و commandهای allowlisted.
- [x] چهار playbook تفصیلی برای integration، CMS/audio، offline sync و quality/release.
- [x] human gate غیرقابل‌خودکارسازی برای review آموزشی، حقوق صوت و QA دستگاه.
- [x] مرتب‌سازی تحویل نهایی: مسیرهای canonical، ignore cache/build/keystore و فیلتر ZIP سورس.
- [x] اسکریپت رسمی `build-android-apk.py` برای sync → audit → test → lint → assembleDebug → integrity/hash.
- [x] تلاش واقعی build در محیط فعلی: JDK 17 موجود است.
- [ ] تولید APK: Android SDK/Build Tools نصب نیست و Gradle 8.11.1 از شبکه دریافت نمی‌شود.
- [x] اضافه‌شدن تست UI برای fallback/hint واژهٔ محرک؛ مجموع تست‌سورس ۱۰۴.
- [x] audit محتوا، migration، release hash، صوت، CMS، bundle deterministic و tamper rejection موفق.
- [ ] اجرای integration واقعی server با PostgreSQL/MinIO؛ Docker در محیط حاضر نصب نیست.
- [ ] اسکن malware، decode واقعی، transcode و delivery job صوت (فاز ۰٫۷٫۰).
- [ ] تأیید ترتیب، distractorها، متن و تلفظ توسط متخصص آموزش فارسی.
- [ ] compile/اجرای ۱۰۴ تست و QA emulator/گوشی.

## دلیل ترتیب آموزشی

شناخت دیداری و تفاوت نقطه قبل از آگاهی صدای آغاز/پایان می‌آید؛ سپس دو فعالیت کوتاه کنارهم‌گذاری نشانه، کودک را برای ساخت واژه آماده می‌کند. `Segment` و `PositionalForm` عمداً فعال نشده‌اند: interaction فعلی برای segmentation و محرک جایگاه حرف هنوز از نظر آموزشی کافی نیست. جزئیات در `LEVEL1_SEQUENCE_FA.md` ثبت است.

## وضعیت CMS و صوت

Payload برای prototype از حالت ADR خارج و به سورس اجرایی تبدیل شده است: نسخهٔ پایدار 3.92.1، PostgreSQL، S3-compatible خصوصی، access/hook/version، پنج collection و ۱۶ تست دامنه. production build بدون DB موفق است، اما خود server و مسیر upload در برابر PostgreSQL/MinIO اجرا نشده‌اند؛ بنابراین تصمیم production هنوز مشروط است. تشخیص magic/hash پیاده شده ولی antivirus، decode و transcode عمداً به‌عنوان گیت واقعی باقی مانده‌اند.

اپ Android هیچ وابستگی، URL یا query مستقیم به CMS ندارد. ادمین آینده asset را مدیریت می‌کند؛ compiler فقط export کامل و تأییدشده را به ContentRelease تبدیل می‌کند. TTS دستگاه همچنان fallback توسعه است.

## شواهد همین سورس

| بررسی | نتیجه |
|---|---|
| resource sync | ۱۶ تصویر، ۲۶۳ متن، ۳ واحد، ۶ درس، ۱۷ فعالیت، ۲ قصه |
| content migration/contract | موفق؛ v2→v3 قطعی و hash release معتبر |
| audio inventory | موفق؛ ۲۲ asset، ۱۲ prompt و ۹ صفحه قصه |
| CMS contract | موفق؛ ۶ نقش، ۳ رکورد spike و ۴ blocker publication |
| Payload prototype | type-check و ۱۸/۱۸ تست موفق؛ type generation/build قبلی موفق؛ ۵ collection و ۶ نقش |
| coding-agent harness | معتبر؛ ۱۴ task در ۴ فاز، dependency بدون چرخه، scope و gate امن |
| DB/object storage | compose آماده است؛ اجرا نشد چون Docker در محیط نصب نیست |
| bundle/tamper | موفق و deterministic |
| JSON/Python | ۷۶ JSON strict و Python compile موفق |
| تست‌های Native/Compose در سورس | ۱۰۴ |
| اجرای Gradle/APK | متوقف پیش از configuration؛ Android SDK موجود نیست و دانلود Gradle 8.11.1 با `Network is unreachable` شکست خورد |
| permission اینترنت/میکروفن | وجود ندارد |
| APK/AAB | ساخته نشد |

## گیت‌های باز و قدم بعد

1. اجرای Compose روی محیط دارای Docker و اثبات migration، bootstrap مدیر، upload خصوصی و signed download.
2. افزودن malware scan، decode با ابزار واقعی، transcode و job ساخت delivery.
3. اثبات access control، دو review، audit و export deterministic در تست integration متصل به DB/S3.
4. طراحی store دیسکی content bundle و process-death recovery پیش از remote sync.
5. اجرای Native tests/lint و رندر typeهای تازه در toolchain کامل.

قدم بعدی ثبت‌شده: اجرای `python3 scripts/build-android-apk.py --output-dir <PATH>` در محیط دارای JDK 17، Android SDK 35/Build Tools و Gradle 8.11.1؛ سپس ثبت hash APK و QA نصب. در هارنس، `061-db-s3-integration` نیز در محیط دارای Docker باز است.
