# راهنمای توسعه AVA
از [وضعیت واقعی](PROGRESS_FA.md)، سپس [نقشه راه](AVA_MASTER_ROADMAP_FA.md) و [راهنمای ساخت](../android/README.md) شروع کنید.

| سند | کاربرد |
|---|---|
| AVA_MASTER_ROADMAP_FA.md | دامنه، فازها و دروازه‌های تکمیل |
| PAGE_DESIGN_PLAN_FA.md | ممیزی تصاویر، مشخصات صفحه‌به‌صفحه و ترتیب تکمیل |
| SCREEN_MATRIX_FA.md | تمام مقصدها، رفتار و مرجع ظاهر |
| RESEARCH_FA.md | پژوهش اولیه و تصمیم‌های اجرایی |
| SKILLS.md | منابع pinned، روش استفاده و تقدم قواعد AVA |
| PROJECT_MAP_FA.md | ساختار منابع، داده، ذخیره، state و UI |
| NATIVE_VERIFICATION.json | نتایج واقعی همین بازسازی، هش و وضعیت آزمون دستگاه |
| DEVICE_QA_FA.md | چک‌لیست باز دستگاه و انتشار |
| TEST_STRATEGY_FA.md | لایه‌های آزمون، ماتریس UI و قواعد شواهد |
| REBUILD_CONTEXT_FA.md | تصمیم‌های بازیابی‌شده از گفت‌وگو و مرز بازسازی |
| AUDIO_INVENTORY_FA.md | پوشش صوت فعلی، سیاست fallback و گیت ضبط/تأیید |
| CONTENT_CONTRACT_FA.md | content v3، release manifest، migration و TaskProgress |
| LOCAL_CONTENT_RUNTIME_FA.md | bundle محلی، staging، activation، rollback و منبع UI |
| ACTIVITY_ENGINE_FA.md | registry نه‌نوعی، reducer/session snapshot و قرارداد صوت تزریق‌پذیر |
| LEVEL1_SEQUENCE_FA.md | ترتیب آموزشی سطح ۱، برش تازه و گیت بازبینی متخصص |
| CMS_SPIKE_FA.md | مقایسه CMS، قرارداد export و وضعیت prototype |
| adr/0001-content-platform-boundary-fa.md | مرز CMS خصوصی، release و منبع محلی اپ |
| adr/0002-audio-policy-and-workflow-fa.md | سیاست صوت انسانی و workflow آینده ادمین |
| adr/0003-cms-technology-spike-fa.md | انتخاب مشروط Payload برای prototype و معیار production |
| ../ZCODE.md | نقطه ورود coding agentها و هارنس ادامه پروژه |
| ../harness/roadmap.json | ترتیب ماشین‌خوان taskها، dependency، scope و gate |
| ../harness/ZCODE_SYSTEM_PROMPT_FA.md | دستور ثابت و محدودیت‌های عامل خودکار |
| FINAL_DELIVERY_FA.md | مسیر canonical سورس و فرمان رسمی build/verify APK |

`DESIGN_BRIEF.md` و `README_FA.md` تاریخچه جریان اولیه وب هستند؛ دستور Flutter و Home موقت در آن‌ها مبنای توسعه جاری نیست. `reference/` فقط برای مقایسه است و داخل UI رندر نمی‌شود. محصول Native در `android/` است.
