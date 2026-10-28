# قرارداد محتوا و پیشرفت AVA — به‌روزرسانی ۰٫۶٫۱

## محتوای v3

`design/content.fa.json` منبع canonical و آفلاین است. سلسله‌مراتب آن `Level → Unit → Lesson → Task` است و Skillها جدا تعریف می‌شوند. UI هنوز برای سازگاری از `Catalog.lessons` تخت استفاده می‌کند، اما Catalog همان فهرست را از Level/Unit تولید می‌کند؛ بنابراین در مرحله بعد مسیر باغ بدون تغییر دوبارهٔ داده ساخته می‌شود.

هر Task شناسه پایدار، `skillIds`، `assetRefs`، `audioRef`، سیاست الزام صوت و fallback نوشتاری دارد. صفحه قصه نیز ID و `audioRef` مستقل دارد. URL یا مسیر storage در محتوای آموزشی نگهداری نمی‌شود.

از ۰٫۶٫۱ در فعالیت‌های InitialSound/FinalSound، `target` پاسخ صحیح و `writtenFallback` واژهٔ محرک است؛ صوت و hint باید واژه را ارائه کنند، نه پاسخ را. نوع‌های دیداری همچنان fallback برابر target دارند. validator این تفاوت را بر اساس خانواده فعالیت کنترل می‌کند.

## مهاجرت و سازگاری

نسخه v2 در `design/fixtures/content-v2.json` حفظ شده است. `migrate-content-v2-to-v3.py` آن را به خروجی canonical v3 تبدیل می‌کند و در حالت `--check` هم برابری خروجی و هم idempotent بودن v3 را می‌سنجد. شناسه درس، task و story تغییر نکرده تا completed، mistake و resume قدیمی معتبر بمانند.

## ContentRelease

`design/content.release.json` manifest نسخه seed است و schema، contentVersion، locale، حداقل نسخه اپ، hash محتوا و hash inventory صوت را ثبت می‌کند. این manifest در وضعیت `development` است، امضا ندارد و publishable نیست. release تولیدی فقط با `approvedForRelease=true`، زمان انتشار و signature معتبر پذیرفته خواهد شد.

مدل‌های Kotlin قراردادهای `Bundled`، `PublishedLocal` و `Remote` را تعریف می‌کنند، اما ۰٫۵٫۰ هیچ implementation شبکه ندارد. `ContentRepository` فقط مرز `active/activate/rollback` را تثبیت می‌کند.

## پیشرفت task

`TaskProgress` برای هر کلید پایدار task، تعداد تلاش، پاسخ درست، hint و آخرین زمان مشاهده را ذخیره می‌کند. زمان از clock قابل تزریق دریافت می‌شود. schema ذخیره فعلاً ۱ است؛ داده قدیمی بدون این key به map خالی مهاجرت می‌کند و schema ناشناخته به‌جای حذف خاموش داده، خطای قابل بازیابی storage ایجاد می‌کند.

این داده هنوز برای ادعای mastery یا spaced repetition استفاده نمی‌شود؛ planner زمان‌محور در فاز مربوط به مرور و پس از تعریف سیاست قابل توضیح اضافه خواهد شد.

## کنترل‌های آفلاین

```sh
python3 scripts/migrate-content-v2-to-v3.py --check
python3 scripts/validate-content-contract.py
python3 scripts/validate-audio-inventory.py
python3 scripts/sync-android-resources.py --check
python3 scripts/validate-cms-contract.py
```
