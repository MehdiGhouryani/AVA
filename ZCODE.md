# نقطهٔ ورود Zcode و coding agentها

قرارداد سراسری پروژه در `AGENTS.md` است و پروتکل اجرایی کامل در [harness/ZCODE_SYSTEM_PROMPT_FA.md](harness/ZCODE_SYSTEM_PROMPT_FA.md) قرار دارد. این فایل فقط ترتیب شروع را مشخص می‌کند.

ابتدا `AGENTS.md` و سپس پرامپت ثابت هارنس را بخوانید. بعد:

```bash
python3 scripts/harness.py validate
python3 scripts/harness.py status
python3 scripts/harness.py next
python3 scripts/harness.py task <TASK_ID>
```

اگر مالک task ID مشخصی داده است، دستور `next` را اجرا نکنید و مستقیماً همان task را با `task <TASK_ID>` بخوانید. وضعیت، مجازبودن اتوماسیون و کامل‌بودن dependencyها را بررسی کنید؛ task مسدود یا `human_gate` قابل اجرا نیست.

فقط یک task را مطابق `readFirst`، playbook، `allowedPaths`، acceptance و gateهای خودش اجرا کنید. قواعد عمومی، شاخه، شواهد و تحویل را از `AGENTS.md` بگیرید؛ این فایل سیاست جداگانه‌ای ایجاد نمی‌کند.