# Phase 062 — Entry/Auth UI (R02)

**Task ID:** `062-entry-auth-ui`  
**Branch:** `phase-062-r02-entry-auth-ui`  
**Baseline:** `phase-062-r01-ui-foundation` @ `01e0576ff572b77abc4c1a2b38e2edefc9afbd01`  
**Status:** `in_progress`

## هدف

بهبود چیدمان و واکنش‌گرایی مسیر ورود به آوا؛ از Onboarding تا Phone و OTP. این برش فقط Presentation را تغییر می‌دهد. منطق احراز هویت آزمایشی و تمام قراردادهای ViewModel ثابت می‌مانند.

## منابع لازم

- `AGENTS.md`
- `docs/design/AVA_DESIGN_SYSTEM.md`
- `docs/design/UI_UX_BACKLOG_NON_LESSON_FA.md`
- `android/app/src/main/java/com/ava/reading/ui/screens/AuthScreens.kt`
- `android/app/src/test/java/com/ava/reading/JourneyUiTest.kt`

## محدودهٔ مجاز

- `AuthScreens.kt`: layout، spacing، max width، scroll، responsive proportions و semantics ظاهری
- `JourneyUiTest.kt`: regression test برای viewport کوتاه و fontScale بزرگ
- اسناد و Harness مربوط به task

## ممنوع

- تغییر Route، ViewModel، reducer، action contract یا navigation
- تغییر MockAuthRepository، کد آزمایشی ۱۲۳۴۵۶، validation شماره، OTP timer/lock/resend یا رفتار focus
- حذف/مخفی‌کردن mock hint یا ادعای ارسال پیامک واقعی
- تغییر permission، storage، شبکه، backend یا dependency
- تغییر درس، تمرین، StoryQuestion، content schema، curriculum یا داده‌های آموزشی

## معیار پذیرش

1. Onboarding در viewport کوتاه و fontScale بالا scroll شود؛ CTA و skip قابل دسترسی بمانند.
2. Phone در عرض عریض حداکثر ۵۲۰dp و در مرکز باشد؛ فرم در SoftCard گروه‌بندی شود.
3. ورودی شماره، PersianDigits، focus/blur، consent، legal، feedback، busy و enabled state بدون تغییر رفتار باقی بمانند.
4. OTP در compact/large-text فضای برند محدود و پنل محتوای scrollable داشته باشد.
5. کد آزمایشی و پیام «پیامک واقعی ارسال نمی‌شود» همچنان آشکار باشند.
6. Journey test مسیر Onboarding → Phone → OTP را در ۳۹۰×۶۴۰dp و fontScale=1.3 پوشش دهد.
7. diff تأیید کند هیچ فایل مرتبط با auth logic، route/state، permission یا learning content تغییر نکرده است.

## گیت‌های نهایی

از ریشهٔ مخزن:

```bash
python3 scripts/sync-android-resources.py --check
python3 scripts/audit-project.py
```

از پوشهٔ `android`:

```bash
./gradlew testDebugUnitTest lintDebug --no-daemon
```

Task فقط پس از موفقیت تمام گیت‌ها و مرور diff به `completed` تغییر کند. screenshot/device QA و TalkBack همچنان human gate جداگانه‌اند.

## وضعیت فعلی

پیاده‌سازی سورس و تست regression افزوده شده؛ هیچ گیت اجرایی در این نشست اجرا نشده است. تا آن زمان task عمداً `in_progress` می‌ماند.
