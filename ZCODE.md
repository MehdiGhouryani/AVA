# نقطهٔ ورود Zcode و سایر coding agentها

این repository با هارنس مستقل از ابزار هدایت می‌شود. قبل از هر تغییر:

```bash
python3 scripts/harness.py validate
python3 scripts/harness.py status
python3 scripts/harness.py next
```

سپس متن کامل [harness/ZCODE_SYSTEM_PROMPT_FA.md](harness/ZCODE_SYSTEM_PROMPT_FA.md) را به‌عنوان دستور ثابت عامل بخوانید و فقط task برگشتی را اجرا کنید. `AGENTS.md`، `harness/roadmap.json` و playbook همان فاز قرارداد الزام‌آورند.

عامل اجازه ندارد task مسدود یا human gate را دور بزند، خارج از `allowedPaths` بنویسد، تست اجرا‌نشده را موفق اعلام کند یا APK/AAB بسازد.
