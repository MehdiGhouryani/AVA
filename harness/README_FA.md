# هارنس اجرای مرحله‌ای AVA

این پوشه مسیر ادامه پروژه را برای Zcode، Claude Code، Codex و سایر coding agentها یکسان می‌کند. هارنس جایگزین تصمیم محصول یا بازبین انسانی نیست؛ فقط ترتیب، محدوده فایل، گیت‌های فنی و شواهد هر task را enforce می‌کند.

## اجزا

| فایل | مسئولیت |
|---|---|
| `roadmap.json` | منبع ماشین‌خوان فازها، dependency، allowed paths و acceptance |
| `roadmap.schema.json` | شکل داده و فیلدهای اجباری |
| `ZCODE_SYSTEM_PROMPT_FA.md` | دستور ثابت قابل کپی برای coding agent |
| `phases/*.md` | playbook معماری و ترتیب هر فاز |
| `WORKLOG.md` | فقط نتایج واقعی و blockerها |
| `scripts/harness.py` | validate، status، next، task و اجرای gateهای allowlisted |

## استفاده

```bash
python3 scripts/harness.py validate
python3 scripts/harness.py status
python3 scripts/harness.py next
python3 scripts/harness.py task 061-db-s3-integration
```

پس از انجام task و قبل از ادعای تکمیل:

```bash
python3 scripts/harness.py run-gates 061-db-s3-integration
```

`run-gates` فقط فرمان‌های آرایه‌ای allowlisted را بدون shell اجرا می‌کند و assemble/bundle را نمی‌پذیرد. اجرای gate به‌تنهایی task را completed نمی‌کند؛ acceptanceها و evidence files نیز باید کامل شوند.

## تغییر وضعیت task

1. فقط task دارای dependencyهای `completed` می‌تواند `ready` شود.
2. عامل هنگام شروع می‌تواند همان task را `in_progress` کند.
3. `completed` فقط پس از عبور همه acceptanceها، gateها و ثبت evidence مجاز است.
4. `human_gate` هرگز توسط عامل کدنویسی تکمیل نمی‌شود.
5. اگر محیط کافی نیست، وضعیت را سبز نکنید؛ blocker، command و نتیجه را در `WORKLOG.md` و اسناد شواهد ثبت کنید.

## اولویت جاری

هارنس دو کار مستقل P0 را آماده می‌داند: integration واقعی Payload/DB/S3 و اجرای Native toolchain. فاز ۰٫۷٫۰ تا بسته‌شدن dependencyهای مربوط خودکار باز نمی‌شود.
