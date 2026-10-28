# تحویل نهایی سورس و APK آوا ۰٫۶٫۱

## وضعیت سورس

سورس canonical در این مسیرهاست:

- `android/`: اپ Native Kotlin/Compose؛
- `cms/payload/`: prototype خصوصی CMS؛
- `design/`: قرارداد محتوا، صوت، منابع و fixtureها؛
- `scripts/`: generator، validator، audit، بسته‌بندی و build؛
- `harness/`: مسیر ادامه فازهای بعدی برای coding agent؛
- `docs/`: معماری، شواهد و گیت‌ها.

`src/` و فایل‌های Vite فقط سابقهٔ اولیه‌اند و محصول production محسوب نمی‌شوند. برای حفظ provenance حذف نشده‌اند. cache، dependency، secret، local SDK path، keystore و خروجی build داخل ZIP سورس قرار نمی‌گیرند.

## فرمان رسمی build APK این برش

پیش‌نیاز: JDK 17، Gradle wrapper 8.11.1 قابل دریافت/کش‌شده، Android SDK 35 و Build Tools سازگار.

```bash
python3 scripts/build-android-apk.py --output-dir /absolute/output/path
```

این فرمان به‌ترتیب resource sync، project audit، `testDebugUnitTest`، `lintDebug` و `assembleDebug` را اجرا می‌کند؛ سپس ZIP integrity، وجود manifest/classes.dex، اندازه و SHA-256 را ثبت می‌کند. APK این مرحله debug است و برای تست داخلی است، نه نسخه امضاشده فروشگاه.

## مرز ادعا

موفقیت build و تست محلی معادل QA گوشی، تأیید آموزشی، تأیید تلفظ یا آمادگی انتشار عمومی نیست. release APK امضاشده فقط پس از signing خارج repository، review حقوقی/آموزشی و QA دستگاه مجاز است.
