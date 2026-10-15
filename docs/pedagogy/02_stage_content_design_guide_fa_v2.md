# راهنمای اجرایی طراحی Stage — ایستگاه، مرحله، تمرین، مرور، ارزیابی و مسیر تطبیقی

**نسخه:** 2.0
**مرجع بالادستی:** `01_master_educational_spec_v2.md`
**کاربرد:** مستقیم برای مدل‌های هوش مصنوعی طراح Stage، طراح Curriculum، Content Designer و تیم QA
**دامنه:** تولید دقیق Stage برای کودکان ۴ تا ۷ سال

---

# 0. مأموریت این سند

این سند باید به مدل AI بگوید **یک مرحله دقیقاً چگونه ساخته شود**.

این فایل نباید دوباره اصول کلان را به‌صورت طولانی تکرار کند؛ اصول بالادستی در فایل 01 هستند. این سند فقط آن اصول را به قرارداد اجرایی تبدیل می‌کند:

```text
Input
→ Select target
→ Select content
→ Design child action
→ Design feedback/scaffold
→ Define evidence
→ Define next decision
→ Schedule retrieval/transfer
→ QA
→ Output Stage
```

---

# 1. تعاریف غیرقابل‌مخلوط

```text
STATION = جهان، فضا، روایت یا زمینهٔ معنادار
STAGE = واحد اجرایی یادگیری
ACTIVITY = الگوی تعامل
SKILL = توانایی هدف
CONTENT ITEM = واژه/صدا/نشانه/تصویر/جمله/متن/داستان...
SESSION = یک نوبت استفاده که می‌تواند چند Stage داشته باشد
REVIEW EVENT = بازگشت برنامه‌ریزی‌شده به یک skill/item
TRANSFER EVENT = استفاده در context یا item تازه
```

---

# 2. قانون مرجع‌خوانی قبل از تولید

مدل پیش از تولید Stage باید این منابع را بررسی کند:

```text
1. Master Educational Spec
2. Skill Graph
3. Prerequisite Graph
4. Persian Content Bank
5. Station Map
6. Learner State
7. Due Review Pool
8. Teacher Guide reference (در صورت نیاز)
```

اگر یکی از این ورودی‌ها وجود ندارد:

```text
status = DRAFT / BLOCKED_BY_MISSING_DATA
```

مدل حق ندارد خلأ را با حدس پنهان کند.

---

# 3. Authority برای مدل تولیدکننده

وقتی تعارض وجود دارد:

```text
approved project constraints
>
approved schema
>
Persian orthographic policy
>
skill/prerequisite graph
>
content bank
>
Teacher Guide reference
>
research evidence
>
design hypothesis
>
model guess
```

---

# 4. Stage Contract — قرارداد اجباری

هر Stage باید این schema را داشته باشد:

```yaml
stage_id:
station_id:
stage_index:
version:
status: draft | reviewed | pilot | approved | retired

learner:
  age_band:
  learner_state_ref:
  prerequisite_state:

learning:
  primary_skill:
  secondary_skills:
  objective:
  why_now:
  prerequisites:

content:
  new_items: []
  support_items: []
  review_items: []
  transfer_items: []
  prohibited_items: []
  source_refs: []

experience:
  station_context:
  narrative_role:
  activity_family:
  response_mode:
  duration_target:
  interaction_difficulty:

sequence:
  warmup:
  teaching:
  guided_practice:
  independent_practice:
  retrieval:
  transfer:
  exit_check:

correctness:
  expected_response:
  acceptable_variants:
  correctness_rule:

adaptation:
  error_taxonomy:
  scaffold_ladder:
  retry_variation:
  difficulty_axes:
  advance_rule:
  consolidate_rule:
  remediate_rule:
  diagnose_rule:

mastery:
  evidence:
  independence_requirement:
  transfer_requirement:
  delayed_retrieval_requirement:

review:
  hooks: []
  priority:

analytics:
  events: []
  metrics: []

assets:
  image_refs: []
  audio_refs: []
  animation_refs: []

accessibility:
  rtl_safe:
  audio_repeatable:
  color_independent:
  text_minimized:

traceability:
  content_source:
  pedagogy_source:
  design_rationale:
  hypotheses_to_validate: []

qa:
  status:
  blockers: []
```

---

# 5. مرحله باید یک «واحد تصمیم» باشد

هر Stage در پایان باید یک تصمیم قابل‌اجرا بدهد:

```text
ADVANCE
CONSOLIDATE
REMEDIATE
DIAGNOSE
REVIEW_LATER
INSUFFICIENT_EVIDENCE
```

`INSUFFICIENT_EVIDENCE` برای مواردی مثل رها کردن stage، نویز ورودی یا پاسخ مبهم ضروری است.

---

# 6. قبل از طراحی Stage: چرا الان؟

مدل باید بتواند جملهٔ زیر را پر کند:

> «این Stage الان ارائه می‌شود، زیرا کودک prerequisiteهای X را دارد و دادهٔ Y نشان می‌دهد که skill Z بیشترین ارزش یادگیری بعدی را دارد.»

اگر `why_now` خالی باشد، Stage به‌صورت خودکار `DRAFT` محسوب شود.

---

# 7. انتخاب Primary Skill

فقط یک `primary_skill` انتخاب کن.

نمونه‌های مجاز:

```text
PA.initial_sound_identification
SYMBOL.visual_identity
MAP.sound_to_symbol
READ.controlled_word_decoding
WRITE.symbol_formation
SPELL.word_encoding
COMP.narrative_recall
VOC.receptive_word_meaning
```

اهداف ثانویه فقط باید پشتیبانی‌کننده باشند.

---

# 8. چرا هدف مرحله نباید مبهم باشد؟

بد:

> «کودک با نشانهٔ ب آشنا شود.»

خوب:

> «کودک بتواند نشانهٔ هدف را در سه واژهٔ آموزشی تشخیص دهد.»

بهتر برای سیستم داده:

```yaml
objective:
  behavior: identify
  target: SYMBOL_B
  condition: mixed_words
  independence: low_scaffold
```

آستانهٔ عددی دقیق باید توسط Assessment Spec تعیین شود، نه توسط مدل به‌صورت خودسرانه.

---

# 9. prerequisite check

پیش از تولید هر Stage این موارد بررسی شود:

```text
Is meaning known enough?
Is instruction understood?
Is required sound skill present?
Is required symbol skill present?
Is response mode feasible?
```

اگر prerequisite مهارتی وجود ندارد، مسیر:

```text
Remediation / Diagnostic
```

است، نه «همان Stage با سختی کمتر» به‌صورت کور.

---

# 10. Content Budget

هر Stage باید با بودجهٔ محتوایی ساخته شود.

## 10.1 سن ۴–۵

پیشنهاد اولیه `DESIGN`:

```text
new target concepts: 1
new target vocabulary: معمولاً 1–3
review: چند مورد کوتاه اما مستمر
transfer: 0–1 مورد ساده
```

## 10.2 سن ۵–۶

```text
new target concept: 1
new target vocabulary: معمولاً 2–4
review: چند مورد mixed
transfer: 1 مورد
```

## 10.3 سن ۶–۷

```text
new target concept: 1
new target vocabulary: معمولاً 2–5
review: mixed/interleaved
transfer: 1 مورد یا بیشتر بر اساس هدف
```

این اعداد **سقف تولید نیستند**؛ بودجهٔ ابتدایی برای جلوگیری از overload هستند و باید با داده اصلاح شوند.

---

# 11. New / Support / Review / Transfer

هر Stage باید این چهار bucket را جدا کند:

```text
NEW       = چیزی که الان آموزش داده می‌شود
SUPPORT   = چیزی که فهم NEW را آسان می‌کند
REVIEW    = چیزی که قبلاً آموزش داده شده و بازیابی می‌شود
TRANSFER  = همان skill در item/context جدید
```

اشتباه رایج: همهٔ واژه‌های داستان را `NEW` حساب کردن.

فقط contentی که قرار است کودک واقعاً در مرحله یاد بگیرد یا ارزیابی کند target محسوب می‌شود.

---

# 12. نسبت محتوایی پیشنهادی

این نسبت‌ها `DESIGN/HYPOTHESIS` هستند:

| Stage role | NEW | REVIEW | TRANSFER |
|---|---:|---:|---:|
| Teach | 40–60% | 30–50% | 10–20% |
| Practice | 20–30% | 50–60% | 20–30% |
| Review | 0–10% | 60–80% | 20–40% |
| Transfer | 0–20% | 40–60% | 30–50% |

اصل ثابت: **تمرین جدید نباید آن‌قدر پر از novelty باشد که retrieval و interpretation را خراب کند.**

---

# 13. Stage Archetypes

مدل از این خانواده‌ها انتخاب می‌کند:

```text
A ACTIVATE / RECALL
B MEANING / CONTEXT
C LISTEN / SOUND
D SYMBOL / PRINT
E SOUND–SYMBOL MAPPING
F WORD BUILDING / CONTROLLED READING
G WRITING / ENCODING
H GUIDED PLAY
I RETRIEVAL / REVIEW
J TRANSFER / EXIT CHECK
K STORY / INTERACTIVE READING
L DIAGNOSTIC MINI-STAGE
```

یک Stage می‌تواند ترکیب محدودی از دو archetype باشد، ولی primary target باید واحد بماند.

---

# 14. Stage Pattern — Activate

```text
Recall prompt
→ child response
→ brief feedback
→ decision
```

هدف: فعال‌کردن دانش قبلی، نه تدریس مفصل.

بازه زمانی باید کوتاه باشد و از پاسخ‌های واقعی کودک evidence بسازد.

---

# 15. Stage Pattern — Meaning / Context

```text
scene
→ observe
→ talk / point
→ connect meaning
→ target attention
```

برای نگاره‌ها و واژگان جدید مناسب است.

---

# 16. Stage Pattern — Listen / Sound

الگوی رشد:

```text
hear
→ notice
→ compare
→ identify
→ locate
→ blend/segment
→ manipulate
```

همهٔ سطوح لازم نیست در هر Stage باشند.

---

# 17. Stage Pattern — Symbol / Print

```text
look
→ find
→ discriminate
→ locate
→ recall
```

در فارسی باید position/form مناسب content رعایت شود.

---

# 18. Stage Pattern — Mapping

mapping را با recognition یکی نکن.

چهار جهت مفید:

```text
sound → symbol
symbol → sound
symbol → word
word → target symbol
```

این‌ها می‌توانند skillهای جدا در گراف باشند.

---

# 19. Stage Pattern — Word Building / Reading

```text
known units
→ combine
→ read
→ verify meaning
→ new word/context
```

برای متن و واژه‌های کنترل‌شده، اول دانش آموخته را به کار بگیر و novelty را کنترل کن.

---

# 20. Stage Pattern — Writing

## Motor

```text
movement
→ trace
→ complete
→ copy
→ recall
```

## Encoding

```text
hear
→ segment/notice
→ select symbol
→ order
→ write
→ check
```

## Composition

```text
idea
→ say
→ write
→ reread
→ improve
```

Trace نباید endpoint باشد.

---

# 21. Stage Pattern — Story / Interactive Reading

```text
predict
→ read/listen
→ talk
→ notice language
→ retell
→ optional role-play
```

همهٔ این‌ها را در یک Stage جا نده؛ بسته به primary target جدا کن.

شواهد متاآنالیزی جدید اثر مثبت خواندن تعاملی بر توانایی روایی کودکان خردسال را گزارش می‌کند؛ با این حال، ناهمگونی مطالعات بالاست و باید آن را به‌عنوان پشتیبان طراحی، نه تضمین نتیجه، در نظر گرفت.

---

# 22. Stage Pattern — Guided Play

قبل از بازی باید این چهار مورد مشخص باشد:

```text
mechanic
primary_skill
expected_response
evidence
```

اگر یکی مبهم است، بازی هنوز آماده نیست.

راهنمای طراحی اپ ۲۰۲۶ نیز بر interaction معنادار، تناسب رشدی، وضوح task، استفاده از دانش قبلی و feedback/scaffolding تأکید دارد و در عین حال هشدار می‌دهد ادبیات موجود عمدتاً انگلیسی/اروپایی است؛ بنابراین هر توصیه برای فارسی باید با pilot بررسی شود.

---

# 23. Stage Pattern — Retrieval

Retrieval باید از حافظه پاسخ بخواهد.

ترتیب شدت cue:

```text
R1 cue-heavy
R2 reduced-cue
R3 open / production
```

در سنین پایین، R3 فقط وقتی مناسب است که response مورد انتظار با رشد کودک متناسب باشد.

Retrieval با feedback اصلاحی طراحی شود؛ مرور ۲۰۲۵ نشان می‌دهد مزیت retrieval در حضور feedback پررنگ‌تر است.

---

# 24. Stage Pattern — Transfer

ladder:

```text
same item / same context
→ new item / same context
→ same skill / new context
→ mixed context
→ real-world
```

در هر قدم فقط بخشی از novelty تغییر کند.

---

# 25. Sequencing داخل Stage

الگوی پیش‌فرض:

```text
1. Orient / Recall
2. Model / Focus
3. Guided Practice
4. Independent Attempt
5. Variation
6. Retrieval
7. Transfer / Exit
```

برای Stageهای کوتاه ممکن است فقط ۳–۵ بخش داشته باشیم.

اصل: **Stage را با مراحل خالی پر نکن؛ فقط اجزای لازم برای هدف را نگه دار.**

---

# 26. Sequencing بین Stageها

در یک sequence پایه:

```text
Activate
→ Teach
→ Guided Practice
→ Independent Practice
→ Mix
→ Retrieve
→ Transfer
```

ولی adaptive branch می‌تواند این را تغییر دهد:

```text
error
→ diagnose
→ remediate
→ retry
→ retrieve later
```

---

# 27. ایستگاه چگونه Stageها را در کنار هم بچیند؟

یک Station نباید فقط یک skill را تکرار کند.

نمونهٔ architecture:

```text
S1 Meaning / Activate
S2 Vocabulary / Oral
S3 Sound Attention
S4 New Skill Teaching
S5 Guided Practice
S6 Independent Practice
S7 Game/Application
S8 Retrieval
S9 Transfer/Exit
```

اما این یک skeleton است، نه تعداد ثابت.

یک ایستگاه داستانی ممکن است فقط S1–S6 داشته باشد و retrieval در Stationهای بعدی انجام شود.

---

# 28. Stage Placement Rule

وقتی Stage را در Station قرار می‌دهی، این امتیازها بررسی شوند:

```text
context_fit
skill_dependency_fit
review_due_fit
novelty_budget_fit
narrative_fit
age_fit
asset_reuse_fit
```

اگر Stage از نظر skill درست ولی از نظر context بی‌ربط است، interaction را بهانهٔ تغییر context نکن؛ Station یا Stage دیگری انتخاب کن.

---

# 29. تعداد Stage در Station

عدد ثابت وجود ندارد.

پیشنهاد `DESIGN`:

| Station role | معمولاً |
|---|---:|
| story/language | 4–7 |
| foundation literacy | 5–8 |
| new sound/symbol | 5–10 |
| reading | 5–9 |
| writing | 5–9 |
| consolidation/review | 3–6 |

تصمیم واقعی باید بر اساس مقدار learning work لازم باشد.

**۱۲ مرحلهٔ مصنوعی بهتر از ۶ مرحلهٔ مؤثر نیست.**

---

# 30. Session Composition

Session می‌تواند چند Stage را شامل شود:

```text
retrieval warm-up
→ main learning stage
→ guided practice
→ short game/application
→ retrieval/exit
```

مدت stage نباید با «دقیقهٔ اجباری» سنجیده شود. اگر target به‌خوبی محقق شد، activity ادامه پیدا نمی‌کند فقط برای پر کردن زمان.

---

# 31. مدت Stage

پیشنهاد اولیه `DESIGN`:

```text
4–5: معمولاً 2–5 دقیقه
5–6: معمولاً 3–6 دقیقه
6–7: معمولاً 3–8 دقیقه
```

این مدت‌ها baseline هستند، نه target رفتاری کودک.

---

# 32. Blocked → Mixed

برای معرفی اولیه:

```text
more blocked
```

برای تثبیت:

```text
more mixed
```

برای transfer:

```text
novel mixed
```

مثلاً پس از تمرین متمرکز /ب/، کودک باید در ادامه آن را میان نشانه‌ها/واژه‌های قبلی نیز تشخیص دهد.

---

# 33. Difficulty Tuning

هر تغییر سختی باید axis داشته باشد:

```yaml
difficulty_change:
  axis: visual_similarity
  from: low
  to: medium
  reason: sufficient_accuracy
```

هیچ چیزی به نام فقط `level = hard` کافی نیست.

---

# 34. Novelty Budget

هر Stage حداقل این noveltyها را پایش کند:

```text
skill_novelty
content_novelty
visual_novelty
interaction_novelty
language_novelty
memory_load
```

قانون:

```text
new skill → familiar interaction
new interaction → familiar content
new context → one or two variables changed
```

---

# 35. Feedback

Feedback باید:

```text
immediate
specific
brief
actionable
non-shaming
```

ساختار پیشنهادی:

```text
[what to notice]
+
[next action]
```

مثال:

> «به صدای اول کلمه گوش کن؛ دوباره یکی را انتخاب کن.»

نه فقط:

> «اشتباه!»

---

# 36. Scaffold Ladder

نردبان پایه:

```text
S0 no hint
S1 repeat instruction
S2 visual cue
S3 highlight feature
S4 fewer choices
S5 partial model
S6 full model
S7 guided completion
```

### Fade rule

پس از موفقیت با scaffold:

```text
same skill + reduced support
```

را اجرا کن، نه بلافاصله mastery.

---

# 37. Retry

Retry باید variation داشته باشد:

```text
same target
+ new item OR reduced distractor OR changed response mode
```

نباید همان سؤال، همان گزینه‌ها و همان layout را بی‌نهایت تکرار کند.

---

# 38. Error Taxonomy

```text
E0 no attempt / disengagement
E1 instruction
E2 attention/selection
E3 vocabulary/meaning
E4 phonological
E5 visual discrimination
E6 symbol identity
E7 mapping
E8 blending/segmentation
E9 decoding
E10 spelling/orthography
E11 motor/handwriting
E12 fluency
E13 comprehension
E14 transfer
```

AI باید error را از event خام به interpretation جدا کند:

```text
observed_event
→ provisional_error
→ intervention
```

این مانع overclaim می‌شود.

---

# 39. Remediation Decision Tree

```text
Error
 ↓
Instruction understood?
 ├─ no → simplify/replay
 └─ yes
    ↓
Meaning adequate?
 ├─ no → vocabulary/meaning support
 └─ yes
    ↓
Sound skill adequate?
 ├─ no → phonological support
 └─ yes
    ↓
Symbol adequate?
 ├─ no → symbol discrimination
 └─ yes
    ↓
Mapping adequate?
 ├─ no → mapping practice
 └─ yes
    ↓
Transfer adequate?
 ├─ no → transfer bridge
 └─ yes → advance / increase difficulty
```

---

# 40. Diagnostic Mini-Stage

وقتی چند علت ممکن وجود دارد، قبل از remediation یک mini diagnostic طراحی کن.

مثال:

```text
Child misses symbol-selection task.

Hypotheses:
A symbol identity weak
B visual attention weak
C sound mapping weak
```

Mini-stage فقط یک distinction را آزمایش کند.

---

# 41. Mastery Evidence

برای skillهای مهم، حداقل سه نوع evidence ترجیح دارد:

```text
accuracy
+ independence
+ delayed retrieval OR transfer
```

برای reading/writing production:

```text
+ production evidence
```

برای oral language:

```text
+ quality/adequacy of response
```

آستانهٔ عددی توسط assessment config تعیین می‌شود.

---

# 42. Stage End States

```text
MASTERED
PRACTICED
NEEDS_REVIEW
NEEDS_REMEDIATION
INSUFFICIENT_EVIDENCE
ABANDONED
```

`ABANDONED` ≠ `NEEDS_REMEDIATION`.

---

# 43. Advance / Consolidate / Remediate / Diagnose

## Advance

وقتی evidence کافی است و scaffold dependency پایین آمده.

## Consolidate

وقتی skill emerging/practicing است و prerequisite سالم است.

## Remediate

وقتی error علت مشخص دارد.

## Diagnose

وقتی علت هنوز روشن نیست.

---

# 44. Review Engine

Review باید بر اساس item/skill باشد، نه lesson.

هر item دارای:

```yaml
last_seen:
last_retrieved:
retrieval_success:
delayed_success:
transfer_success:
error_frequency:
scaffold_dependency:
decay_risk:
review_due:
```

---

# 45. Review Priority

یک فرمول شروع می‌تواند چنین باشد:

```text
priority =
0.30 decay_risk
+ 0.25 error_frequency
+ 0.20 skill_importance
+ 0.15 transfer_weakness
+ 0.10 scaffold_dependency
```

این **HYPOTHESIS** است.

موتور باید بعدها آن را با داده و outcome آموزشی بهینه کند.

---

# 46. Review Schedule

از schedule اسمی استفاده نکن؛ از schedule قابل‌تنظیم استفاده کن.

baseline پیشنهادی:

```text
R0 = همان Stage، پس از چند item دیگر
R1 = پایان همان session
R2 = session بعدی
R3 = فاصلهٔ کوتاه چندروزه
R4 = حدود یک تا دو هفته
R5 = maintenance چند هفته‌ای / transfer
```

این مقادیر **starting policy** هستند و نباید fixed scientific interval تلقی شوند.

شواهد کلی از spacing و retrieval حمایت می‌کنند، اما بهترین فاصله به material، learner و شرایط بستگی دارد.

---

# 47. Review Difficulty

Review باید تدریجی از cue کم شود:

```text
cue-heavy
→ reduced-cue
→ open recall / production
```

ولی کودک ۴ ساله را مجبور به open production پیچیده نکن؛ response format نیز باید developmental باشد.

---

# 48. Retrieval ≠ Restudy

Review می‌تواند exposure مجدد داشته باشد، ولی وقتی هدف تثبیت حافظه است، بخش مهمی از review باید retrieval واقعی باشد.

بد:

```text
همان تصویر دوباره نمایش داده شود و پاسخ هم همراه آن باشد.
```

بهتر:

```text
cue removed
→ child recalls/selects
→ corrective feedback
```

---

# 49. Context Variation

برای هر content item چند context ثبت کن:

```text
C1 same scene
C2 same item / different scene
C3 new item / same skill
C4 new story
C5 real-world
```

هدف این است که کودک فقط layout را یاد نگیرد.

---

# 50. Content Variation Rules

برای تکرار آموزشی، حداقل یکی از این موارد تغییر کند:

```text
spacing
item
context
cue level
response mode
contrast set
application
```

تکرار identical exercise را به‌عنوان «۵ تمرین» حساب نکن.

---

# 51. Persian Sound–Symbol Rule

در آموزش فارسی همیشه مشخص کن:

```text
phonological target
symbol target
position/form
reading target
writing/spelling target
```

مثلاً ممکن است Stage فقط:

```yaml
phonological_target: SOUND_X
symbol_target: SYMBOL_Y
reading_target: false
writing_target: false
```

باشد.

---

# 52. Confusion Sets

برای نشانه‌ها و الگوهای مشابه، confusion set نگه دارید:

```text
visual_confusion
point_confusion
position_confusion
phonological_confusion
connection_confusion
orthographic_confusion
```

remediation باید contrast-specific باشد.

---

# 53. Controlled Reading

قبل از استفاده از هر واژه/متن به‌عنوان decoding task بررسی کن:

```text
Are required units learned?
Is the word linguistically natural?
Is meaning available?
Is novelty within budget?
```

متن مصنوعی برای جا دادن یک نشانه reject شود.

---

# 54. Sentence Policy

جملهٔ آموزشی باید:

- طبیعی؛
- معنی‌دار؛
- مناسب سن؛
- از نظر واژگان کنترل‌شده؛
- از نظر الگوی نوشتاری مجاز

باشد.

پیچیدگی فقط با «تعداد کلمات» سنجیده نشود؛ syntax و language load نیز مهم‌اند.

---

# 55. Vocabulary Policy

هر واژه را فقط در صورت نیاز target کن.

واژه باید تا حد امکان:

```text
meaningful
understandable
visualizable
usable
reusable
```

باشد.

سه سطح کاربرد:

```text
CORE = everyday
LEARNING = instructional/school vocabulary
LITERARY = story/cultural
```

فقط `CORE` و `LEARNING` لزوماً candidate mastery هستند؛ `LITERARY` می‌تواند exposure باشد مگر اینکه هدف دیگری تعیین شده باشد.

---

# 56. Activity Selection Matrix

| هدف | خانواده‌های مناسب |
|---|---|
| oral language | ببین و بگو، داستان، نقش‌آفرینی، تعریف کن |
| vocabulary | گوش کن/انتخاب کن، تصویرخوانی، retrieval |
| phonological awareness | listen/compare, sound hunt, blend/segment |
| symbol recognition | search, match, discriminate |
| mapping | sound↔symbol match, select, build |
| reading | build, read, mixed read, connected text |
| motor writing | trace, complete, copy, recall |
| spelling | hear→segment→select→write |
| comprehension | retell, sequence, infer, explain |
| transfer | mission, new scene, real-world task |

اسم activity هیچ‌گاه بدون primary target ذخیره نشود.

---

# 57. چهار حالت پاسخ

پاسخ کودک می‌تواند:

```text
select / point
match / tap
arrange / drag
speak / repeat
read aloud
trace
write
construct
explain / retell
```

برای skillهای چندبعدی، در صورت امکان یک evidence ثانویه با modality متفاوت جمع شود؛ اما primary target نباید تغییر کند.

---

# 58. Voice Input

Voice فقط یکی از evidenceهاست.

خطای voice می‌تواند ناشی از:

- نویز؛
- میکروفون؛
- لهجه؛
- سرعت؛
- بی‌میلی کودک؛
- محدودیت مدل

باشد.

پس:

```text
voice result ≠ final mastery decision
```

مگر اینکه محصول پس از validation علمی خلاف آن را نشان دهد.

---

# 59. Accessibility

هر Stage باید تا حد امکان:

```text
RTL-safe
audio-repeatable
large touch target
color-independent
pause/resume
low text dependency
non-shaming feedback
```

باشد.

---

# 60. Asset Reuse

هر asset ترجیحاً چند کاربرد آموزشی داشته باشد.

مثلاً یک scene بازار:

```text
vocabulary
+ oral language
+ visual search
+ sound hunt
+ sentence
+ story
+ transfer
```

اما همان asset نباید در همه‌جا با همان interaction تکرار شود.

---

# 61. Animation

Animation فقط در نقش‌های زیر مجاز است:

```text
explain
focus
signal
motivate
confirm
```

هر animation بدون نقش مشخص، candidate حذف است.

---

# 62. Audio

Audio می‌تواند:

- دستور بدهد؛
- model بدهد؛
- target sound را برجسته کند؛
- story را روایت کند؛
- feedback بدهد.

ولی نباید همیشه پاسخ را به‌جای کودک تولید کند.

---

# 63. Child Action Test

قبل از approval از خود بپرس:

> «کودک دقیقاً چه کار فکری انجام می‌دهد؟»

اگر پاسخ فقط:

```text
tap the moving object
```

باشد ولی target skill مثلاً phonological awareness است، Stage احتمالاً interaction مناسبی ندارد.

---

# 64. Pattern Memorization Test

هر Stage باید این تست را پاس کند:

```text
Can child solve after item order changes?
Can child solve after image changes?
Can child solve with one cue removed?
Can child solve in another context?
```

اگر نه، mastery را به تعویق بینداز.

---

# 65. Session Fatigue Policy

اگر نشانه‌های خستگی/کاهش توجه افزایش یافت:

```text
interaction load ↓
novelty ↓
response complexity ↓

while

learning target stays constant
```

این به معنی آسان‌سازی دائمی skill نیست.

---

# 66. Stage Selection Algorithm — MVP

```text
INPUT:
learner_state
station
primary_skill_candidate
prerequisites
due_reviews
content_bank

1. Filter invalid skills
2. Check prerequisites
3. Rank due reviews
4. Select target skill
5. Select new/support/review/transfer items
6. Choose familiar context
7. Choose simplest valid interaction
8. Define correctness
9. Define error routes
10. Define scaffold
11. Define retry variation
12. Set difficulty axes
13. Define evidence
14. Define exit decision
15. Create review hooks
16. Create transfer condition
17. Create analytics
18. Run QA
19. Output stage
```

---

# 67. Stage Selection Algorithm — Adaptive

برای هر candidate stage امتیاز داخلی بساز:

```text
value =
skill_relevance
+ prerequisite_fit
+ review_urgency
+ transfer_weakness
+ error_relevance
+ age_fit
+ novelty_fit
+ context_fit
+ fatigue_fit
```

وزن‌ها `HYPOTHESIS` هستند.

candidate انتخاب‌شده باید علاوه بر value از QA نیز عبور کند.

---

# 68. Review Engine — adaptive

اگر skill قوی است:

```text
review_frequency ↓
context_novelty ↑
cue ↓
```

اگر skill در حال افت است:

```text
review_frequency ↑
cue briefly ↑
context difficulty ↓
then fade support
```

---

# 69. Real-World Transfer

برای بعضی مهارت‌ها extension بیرون از صفحه طراحی کن:

```text
find an object
say a sentence
find target sound in environment
read a simple label
show a word to parent
retell a story
```

در همهٔ skillها لازم نیست؛ فقط وقتی هدف transfer واقعاً meaningful است.

---

# 70. Parent Extension

```yaml
adult_extension:
  required: false
  duration: 1-3m
  goal:
  prompt:
  child_output:
```

والد باید بداند «چه چیزی را تمرین کند»، نه اینکه صرفاً یک بازی دیگر اجرا کند.

---

# 71. QA — آموزش

- [ ] primary target واحد است.
- [ ] prerequisite مشخص است.
- [ ] چرا الان مشخص است.
- [ ] new/support/review/transfer جدا هستند.
- [ ] expected response روشن است.
- [ ] evidence قابل‌تفسیر است.

---

# 72. QA — فارسی

- [ ] RTL سالم است.
- [ ] form/position نشانه درست است.
- [ ] reading و writing جدا شده‌اند.
- [ ] واکه‌های کوتاه به‌طور آموزشی کنترل شده‌اند.
- [ ] spelling variant policy رعایت شده.
- [ ] واژه و جمله طبیعی‌اند.

---

# 73. QA — تطبیق

- [ ] error type قابل ثبت است.
- [ ] scaffold level ثبت می‌شود.
- [ ] retry variation دارد.
- [ ] difficulty axis روشن است.
- [ ] exit decision روشن است.
- [ ] insufficient evidence امکان‌پذیر است.

---

# 74. QA — UX

- [ ] task بدون متن طولانی قابل کشف است.
- [ ] interaction در خدمت target است.
- [ ] animation نقش مشخص دارد.
- [ ] response mode مناسب سن است.
- [ ] کودک با pattern layout موفق نمی‌شود.
- [ ] failure loop شرم‌آور نیست.

---

# 75. QA — Traceability

هر Stage باید بگوید:

```text
content came from where?
pedagogy came from where?
which decisions are DESIGN?
which hypotheses need validation?
```

Stage بدون provenance = `DRAFT`.

---

# 76. Analytics Events

حداقل:

```text
stage_started
instruction_replayed
item_presented
attempt_submitted
attempt_correct
attempt_incorrect
error_classified
scaffold_shown
retry_started
retry_success
transfer_passed
transfer_failed
stage_completed
stage_abandoned
review_due
review_attempt
```

متریک‌های مفید:

```text
accuracy
scaffold_level
error_type
context_success
novel_context_success
delayed_retrieval
transfer_success
abandonment
```

---

# 77. Stage Output — قالب Markdown برای انسان

```markdown
# Stage: [عنوان]

## 1. هدف اصلی

## 2. چرا اکنون؟

## 3. پیش‌نیازها

## 4. محتوای جدید

## 5. محتوای حمایتی

## 6. مرور

## 7. انتقال

## 8. روایت / زمینه

## 9. جریان دقیق اجرا
1. ...
2. ...
3. ...

## 10. پاسخ مورد انتظار

## 11. معیار درست/غلط

## 12. خطاهای محتمل

## 13. Feedback

## 14. Scaffold و Fade

## 15. Retry

## 16. تنظیم سختی

## 17. Evidence / Mastery

## 18. Exit Decision

## 19. Review Hooks

## 20. Transfer Condition

## 21. Analytics

## 22. Assets

## 23. Accessibility

## 24. Provenance
```

---

# 78. Stage Output — JSON/YAML قراردادی برای ماشین

```yaml
stage:
  id:
  version:
  status:
  learning:
    primary_skill:
    objective:
    why_now:
    prerequisites: []

  content:
    new: []
    support: []
    review: []
    transfer: []

  experience:
    context:
    activity_family:
    response_mode:
    duration_target:

  flow:
    - step_id:
      role: orient | model | guided | independent | retrieve | transfer | exit
      child_action:
      feedback:
      scaffold:

  correctness:
    expected_response:
    rule:
    acceptable_variants: []

  adaptation:
    errors: []
    retry:
    difficulty_axes: []
    exit_routes:
      advance:
      consolidate:
      remediate:
      diagnose:

  mastery:
    evidence: []
    delayed_required:
    transfer_required:

  review:
    hooks: []

  analytics:
    events: []
    metrics: []

  assets:
    images: []
    audio: []
    animation: []

  traceability:
    content_source:
    pedagogy_source:
    design_rationale:
    hypotheses: []

  qa:
    blockers: []
```

---

# 79. Stage Generator Prompt — هستهٔ System/Developer Prompt

```text
You are generating one educational stage for a Persian early-literacy system for children ages 4–7.

You MUST:
1. Use the supplied learner state, skill graph, prerequisite graph, content bank, station context and due-review pool.
2. Treat the Teacher Guide as a reference spine, not an immutable child schedule.
3. Select exactly one primary learning target.
4. Separate oral language, vocabulary, phonological awareness, print awareness, symbol recognition, sound–symbol mapping, reading, spelling, writing and comprehension.
5. Separate NEW, SUPPORT, REVIEW and TRANSFER content.
6. Do not invent unsupported Persian orthographic rules or arbitrary sign sequences.
7. Preserve Persian RTL, position/form rules and the distinction between reading and spelling.
8. Define the child action and the evidence captured.
9. Define correctness, error routes, feedback, scaffold, fade and retry.
10. Change only 1–2 main difficulty axes at a time.
11. Design retrieval with corrective feedback and do not confuse restudy with retrieval.
12. Include at least one context/item variation before claiming mastery.
13. Never claim mastery from completion, speed, one attempt or full-scaffold success alone.
14. Use INSUFFICIENT_EVIDENCE when the data cannot support a learning interpretation.
15. Mark product choices as DESIGN and unvalidated assumptions as HYPOTHESIS.
16. Return a traceable, testable Stage specification.
```

---

# 80. Prompt برای جلوگیری از hallucination محتوایی

```text
Do not invent new content items when an approved content bank is provided.
If a required word, symbol form, spelling variant, image, or text is missing from the bank, return MISSING_CONTENT rather than silently creating an approved item.
If the Teacher Guide and current project policy differ, follow the current approved project policy and record the difference in provenance.
Never infer mastery from missing data.
Never interpret disengagement as a skill deficit without additional evidence.
```

---

# 81. دستور «review item باید skill-level باشد»

بد:

```text
Review Lesson 4
```

خوب:

```text
Review:
PA.initial_sound /ب/
Items:
WORD_01, WORD_07, WORD_12
```

Review engine باید بتواند یک skill را در چند Station و چند زمان بازیابی کند.

---

# 82. دستور «station مالک skill نیست»

مثلاً `/ب/` ممکن است در:

```text
Station 2 = introduction
Station 5 = mixed practice
Station 8 = story reuse
Station 11 = delayed retrieval
Station 15 = transfer
```

ظاهر شدن skill باید تابع learner state باشد، نه فقط ترتیب داستان.

---

# 83. دستور «یکسانی interaction را مساوی تکرار آموزشی ندان»

اگر فقط:

```text
same picture
same layout
same answer order
```

تکرار شده است، این سه event می‌تواند یک تمرین محسوب شود، نه سه Stage مستقل.

---

# 84. دستور «تغییر context را کوچک نگه دار»

در transfer:

```text
same skill
+ new item
+ familiar mechanic
```

اول.

سپس:

```text
same skill
+ new context
+ familiar mechanic
```

و بعد:

```text
same skill
+ mixed content
+ new context
```

---

# 85. دستور «معنا و decoding»

وقتی Stage خواندن است، دو سؤال جدا نگه دار:

```text
Can the child decode?
Can the child understand?
```

اگر هدف primary فقط decoding است، comprehension می‌تواند secondary evidence باشد؛ اما نباید حذف شود در جایی که متن معنی‌دار و مناسب سن است.

---

# 86. دستور «voice evidence»

```text
voice_attempt
→ acoustic/system result
→ confidence
→ cross-check with other evidence
```

نتیجهٔ voice بدون cross-check، mastery قطعی نیست.

---

# 87. دستور «real-world extension»

اگر transfer meaningful است، مدل می‌تواند task بیرون صفحه پیشنهاد کند؛ اما آن را شرط completion قرار ندهد مگر curriculum policy صریحاً چنین گفته باشد.

---

# 88. Stage Review Gate — Blockers

هر یک از موارد زیر blocker است:

```text
missing primary target
missing prerequisite
unsupported orthography
unknown correctness rule
no error route for an assessed skill
no provenance
false mastery claim
pattern-memorization risk
```

موارد non-blocking باید به `warnings` بروند.

---

# 89. Station Review Gate

یک Station زمانی approved است که:

```text
context coherent
+ stage dependencies coherent
+ new/review/transfer visible
+ skill reuse visible
+ retrieval path exists
+ transfer exists where meaningful
+ branches exist
+ content is traceable
```

---

# 90. Curriculum Segment Gate

هر segment باید این حلقه را داشته باشد:

```text
Teach
→ Practice
→ Retrieve
→ Transfer
→ Assess
→ Remediate / Advance
```

---

# 91. اصل «مرحلهٔ بیشتر = یادگیری بیشتر» غلط است

Stage count یک output design است، نه learning outcome.

اگر یک skill با Stageهای کمتر، evidence بهتر و transfer قوی‌تر ایجاد شود، segment بهتر است.

---

# 92. اصل «بازی بیشتر = یادگیری بیشتر» غلط است

interaction فقط وقتی ارزشمند است که learning target را بهتر کند.

بازی ممکن است target را پشتیبانی کند؛ ممکن است نکند.

---

# 93. اصل «سرعت = mastery» غلط است

Speed فقط زمانی metric اصلی است که target واقعاً fluency/automaticity باشد.

در سایر skillها، accuracy + independence + retention + transfer مهم‌ترند.

---

# 94. اصل «ترک مرحله = ضعف» غلط است

رها کردن Stage می‌تواند ناشی از:

```text
fatigue
technical issue
instruction confusion
context mismatch
disengagement
```

باشد.

---

# 95. Data Model برای Stage Evidence

```yaml
stage_evidence:
  stage_id:
  child_action:
  response:
  correctness:
  latency_optional:
  error_candidate:
  scaffold_level:
  retry:
  context_id:
  item_id:
  transfer:
  delayed_retrieval:
  confidence:
```

این schema باید اجازه دهد later models از raw evidence دوباره interpretation کنند.

---

# 96. Rule برای نگهداری raw data

همیشه این دو را جدا نگه دارید:

```text
RAW OBSERVATION
MODEL INTERPRETATION
```

مثلاً:

```yaml
raw:
  child_selected: SYMBOL_T

interpretation:
  candidate_error: VISUAL_CONFUSION
  confidence: 0.62
```

این کار از overfitting مدل adaptive جلوگیری می‌کند.

---

# 97. مدل دادهٔ Review Event

```yaml
review_event:
  item_id:
  skill_id:
  scheduled_at:
  actual_at:
  interval:
  cue_level:
  response:
  correctness:
  error_type:
  transfer_context:
  next_interval:
```

`next_interval` باید قابل محاسبه توسط Review Engine باشد.

---

# 98. Review Policy پیشنهادی

در MVP می‌توان از intervalهای محدود و قابل‌تنظیم استفاده کرد؛ در نسخهٔ بعد، interval بر اساس:

```text
retrieval success
error recurrence
cue dependence
delayed retention
transfer
```

تطبیق یابد.

این رویکرد با شواهد spacing/retrieval سازگار است، اما فرمول دقیق باید با pilot تعیین شود.

---

# 99. Curriculum Integrity Check

قبل از ساخت هر batch بزرگ از Stageها:

```text
Find every skill with:
- no introduction
- no review
- no transfer
- no assessment
- no remediation path
```

هر skill مهم با یکی از این نواقص باید به backlog آموزشی برگردد.

---

# 100. Final Machine Checklist

مدل قبل از خروجی نهایی این ۲۰ سؤال را پاسخ دهد:

```text
1. Which skill?
2. Why now?
3. Which prerequisites?
4. What is new?
5. What is support?
6. What is review?
7. What is transfer?
8. What does the child do?
9. What counts as correct?
10. What can fail?
11. What is the first remediation?
12. How does scaffold fade?
13. What changes on retry?
14. Which difficulty axis changes?
15. What is evidence of learning?
16. What prevents pattern memorization?
17. When does skill return?
18. What data is logged?
19. What is the source/provenance?
20. Which assumptions still need validation?
```

اگر هر سؤال بی‌پاسخ است، Stage فقط `DRAFT` است.

---

# منابع منتخب پژوهشی

1. **راهنمای معلم فارسی اول دبستان**، سازمان پژوهش و برنامه‌ریزی آموزشی، ۱۳۹۱. 
2. **Institute of Education Sciences / WWC.** *Foundational Skills to Support Reading for Understanding in Kindergarten Through 3rd Grade.* https://ies.ed.gov/ncee/wwc/PracticeGuide/21
3. **Rahbari, N., & Sénéchal, M. (2010).** Learning to read and spell in Persian: a cross-sectional study from Grades 1 to 4. https://pubmed.ncbi.nlm.nih.gov/20677856/
4. **Rahbari, N., Sénéchal, M., & Arab-Moghaddam, N. (2007).** The role of orthographic and phonological processing skills in the reading and spelling of monolingual Persian children. https://doi.org/10.1007/s11145-006-9042-x
5. **Akbari, E., et al. (2019).** Persian prereading skills battery. https://pmc.ncbi.nlm.nih.gov/articles/PMC6825371/
6. **Myat Kyaw, H. M. M., & Li, Y. (2026).** Design Principles and Pedagogical Practices in Literacy Apps for Young Children. https://link.springer.com/article/10.1007/s10643-026-02326-1
7. **Gonçalves, A. O., Muniz, B. F. B., & Jaeger, A. (2025).** Retrieval Practice Versus Elaborative Encoding. https://doi.org/10.1007/s10648-025-10076-6
8. **Carpenter, S. K., Pan, S. C., & Butler, A. C. (2022).** The science of effective learning with spacing and retrieval practice. https://doi.org/10.1038/s44159-022-00089-1
9. **Xing, L., et al. (2025).** Interactive reading and narrative ability meta-analysis. https://pmc.ncbi.nlm.nih.gov/articles/PMC12645831/
10. **López-Escribano, C., et al. (2022).** Handwriting fluency meta-analysis. https://pmc.ncbi.nlm.nih.gov/articles/PMC9204270/

---

# خلاصهٔ اجرایی

```text
MASTER SPEC
→ defines the learning system

STAGE GUIDE
→ defines how one executable stage is generated

SKILL GRAPH
→ defines dependencies

CONTENT BANK
→ defines approved content

REVIEW ENGINE
→ decides when skills return

ASSESSMENT
→ decides what evidence means

AI STAGE GENERATOR
→ assembles the next best stage without inventing curriculum logic
```

> **مدل AI نباید «مرحله بسازد»؛ باید از روی یک سیستم آموزشی موجود، «مرحلهٔ درست بعدی را استنتاج و مشخصات آن را تولید کند».**
