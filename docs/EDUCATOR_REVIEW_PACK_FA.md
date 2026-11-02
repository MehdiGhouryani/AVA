# بستهٔ بازبینی برای متخصص آموزش خواندن فارسی

> **این فرم است، نه محتوای تأییدشده.** همهٔ موارد پایین همین حالا در اپ هستند و `approvedForRelease=false` است. تا تأیید ثبت‌شدهٔ شما هیچ‌چیز تأییدشده شمرده نمی‌شود. این فایل را اسکریپت `scripts/export-educator-pack.py` از `design/content.fa.json` و `design/audio.inventory.json` می‌سازد؛ دستی ویرایشش نکنید.

نسخهٔ محتوا: `0.6.1-dev.1` · زبان: `fa-IR` · ۳ واحد، ۶ درس، ۱۷ فعالیت، ۲ قصه، ۲۲ نیاز صوتی.

## چه چیزی از شما خواسته می‌شود

برای هر ردیف یکی از سه علامت را در ستون «تصمیم» بنویسید: **✔** تأیید، **✎** اصلاح (در «توضیح» بنویسید چه)، **✖** رد. اگر دربارهٔ مورد نظری ندارید خالی بگذارید؛ خالی یعنی «بررسی نشده» و تأیید نیست. هر ردیف شناسهٔ پایدار دارد (ID)؛ در پاسخ از همان استفاده کنید.

پرسش‌های کلی برای هر درس: آیا هدف مناسب سن ۴ تا ۷ سال است؟ ترتیب فعالیت‌ها منطقی است؟ گزینه‌های نادرست (distractor) گمراه‌کننده یا ناعادلانه نیستند؟ متن مکتوب جایگزین (fallback) درست است؟ تلفظ مورد نیاز برای ضبط روشن است؟

## واحد ۱ — نشانه‌ها و صداها (`unit_signs`)

### درس ۱ — دوستی با نشانه‌ها (`visual_shapes_01`)

هدف: شکل و صدای چند نشانه را ببین و پیدا کن.

| ID | نوع | هدف | گزینه‌ها / توکن‌ها | متن مکتوب | مهارت | صوت | تصمیم | توضیح |
|---|---|---|---|---|---|---|---|---|
| `shape_aa` | تشخیص شکل | آ | آ، ا، د | آ | شناخت نشانه | `task.shape_aa.prompt`: «آ» (needs_educator_script) |  |  |
| `sound_be` | شنیدن صدا | ب | پ، ب، ت | ب | آگاهی واجی | `task.sound_be.prompt`: «ب» (needs_educator_script) |  |  |
| `shape_mim` | تشخیص شکل | م | ن، م، و | م | شناخت نشانه | `task.shape_mim.prompt`: «م» (needs_educator_script) |  |  |

### درس ۲ — راز نقطه‌ها (`dots_02`)

هدف: به نقطه‌ها دقت کن و صدای نشانه را هم بشنو.

| ID | نوع | هدف | گزینه‌ها / توکن‌ها | متن مکتوب | مهارت | صوت | تصمیم | توضیح |
|---|---|---|---|---|---|---|---|---|
| `dots_pe` | هم‌خانواده‌ها | پ | ب، پ، ت | پ | شناخت نشانه | `task.dots_pe.prompt`: «پ» (needs_educator_script) |  |  |
| `sound_te` | شنیدن صدا | ت | ن، ب، ت | ت | آگاهی واجی | `task.sound_te.prompt`: «ت» (needs_educator_script) |  |  |
| `dots_nun` | هم‌خانواده‌ها | ن | ن، ی، ب | ن | شناخت نشانه | `task.dots_nun.prompt`: «ن» (needs_educator_script) |  |  |

## واحد ۲ — صداهای واژه (`unit_sounds`)

### درس ۱ — اول و آخر واژه (`edge_sounds_03`)

هدف: صدای آغاز و پایان چند واژهٔ آشنا را پیدا کن.

| ID | نوع | هدف | گزینه‌ها / توکن‌ها | متن مکتوب | مهارت | صوت | تصمیم | توضیح |
|---|---|---|---|---|---|---|---|---|
| `initial_baba` | صدای آغاز | ب | ب، م، ن | بابا | آگاهی واجی | `task.initial_baba.prompt`: «بابا» (needs_educator_script) |  |  |
| `initial_madar` | صدای آغاز | م | ن، م، ب | مادر | آگاهی واجی | `task.initial_madar.prompt`: «مادر» (needs_educator_script) |  |  |
| `final_ab` | صدای پایان | ب | ب، د، م | آب | آگاهی واجی | `task.final_ab.prompt`: «آب» (needs_educator_script) |  |  |

### درس ۲ — صداها کنار هم (`blend_words_04`)

هدف: نشانه‌ها را به ترتیب بچین و واژهٔ آشنا بساز.

| ID | نوع | هدف | گزینه‌ها / توکن‌ها | متن مکتوب | مهارت | صوت | تصمیم | توضیح |
|---|---|---|---|---|---|---|---|---|
| `blend_ab` | کنار هم | آب | ب، آ | — | ساخت واژه | بدون صوت |  |  |
| `blend_bad` | کنار هم | باد | د، ب، ا | — | ساخت واژه | بدون صوت |  |  |

## واحد ۳ — واژه‌های نخست (`unit_words`)

### درس ۱ — کلمه‌های کوچک (`words_03`)

هدف: با نشانه‌ها کلمه بساز و کلمهٔ آشنا را پیدا کن.

| ID | نوع | هدف | گزینه‌ها / توکن‌ها | متن مکتوب | مهارت | صوت | تصمیم | توضیح |
|---|---|---|---|---|---|---|---|---|
| `build_ab` | ساخت واژه | آب | ب، آ | — | ساخت واژه | بدون صوت |  |  |
| `word_baba` | تشخیص شکل | بابا | باد، آب، بابا | بابا | شناخت واژه | `task.word_baba.prompt`: «بابا» (needs_recording) |  |  |
| `build_bad` | ساخت واژه | باد | د، ب، ا | — | ساخت واژه | بدون صوت |  |  |

### درس ۲ — جشن باغ نشانه‌ها (`garden_04`)

هدف: با دیدن، شنیدن و ساختن، آموخته‌ها را مرور کن.

| ID | نوع | هدف | گزینه‌ها / توکن‌ها | متن مکتوب | مهارت | صوت | تصمیم | توضیح |
|---|---|---|---|---|---|---|---|---|
| `sound_mim` | شنیدن صدا | م | م، ن، و | م | آگاهی واجی، مرور هدایت‌شده | `task.sound_mim.prompt`: «م» (needs_educator_script) |  |  |
| `sound_pe` | شنیدن صدا | پ | ب، ت، پ | پ | آگاهی واجی، مرور هدایت‌شده | `task.sound_pe.prompt`: «پ» (needs_educator_script) |  |  |
| `build_ab_review` | ساخت واژه | آب | آ، ب | — | ساخت واژه، مرور هدایت‌شده | بدون صوت |  |  |

## قصه‌ها

### دانهٔ کوچک (`little_seed`) — یک قصه دربارهٔ صبر و مراقبت

| ID | متن صفحه | صوت | تصمیم | توضیح |
|---|---|---|---|---|
| `little_seed_page_01` | آوا گفت: «بیا برای دانه یک خانه بسازیم!» او دانه را آرام در خاک گذاشت. | `story.little_seed.page_01` | | |
| `little_seed_page_02` | دانه زیر خاک خوابید. آوا برایش کمی آب آورد. | `story.little_seed.page_02` | | |
| `little_seed_page_03` | هر روز آوا به باغ سر زد. خورشید گرم می‌تابید و دانه آرام رشد می‌کرد. | `story.little_seed.page_03` | | |
| `little_seed_page_04` | یک صبح، جوانه‌ای کوچک از خاک بیرون آمد. آوا لبخند زد. | `story.little_seed.page_04` | | |
| `little_seed_page_05` | آوا گفت: «با کمی صبر و مراقبت، چیزهای کوچک هم بزرگ می‌شوند.» | `story.little_seed.page_05` | | |

پرسش: آوا برای دانه چه آورد؟ · گزینه‌ها: آب، توپ، کفش · پاسخ: آب

| تصمیم دربارهٔ پرسش | توضیح |
|---|---|
| | |

### کتابِ دوستان (`friends_book`) — یک کتاب و چند دوست مهربان

| ID | متن صفحه | صوت | تصمیم | توضیح |
|---|---|---|---|---|
| `friends_book_page_01` | آوا یک کتاب تازه پیدا کرد. روی جلدش یک ستاره بود. | `story.friends_book.page_01` | | |
| `friends_book_page_02` | خرگوش و روباه آمدند. سنجاب هم کنارشان نشست. | `story.friends_book.page_02` | | |
| `friends_book_page_03` | آوا کتاب را باز کرد. هر دوست یک تصویر تازه در آن پیدا کرد. | `story.friends_book.page_03` | | |
| `friends_book_page_04` | دوستان گفتند: «خواندن کنار هم چه لذتی دارد!» کتاب دوباره در باغ باز شد. | `story.friends_book.page_04` | | |

پرسش: دوستان با هم چه خواندند؟ · گزینه‌ها: کفش، کتاب، توپ · پاسخ: کتاب

| تصمیم دربارهٔ پرسش | توضیح |
|---|---|
| | |

## فهرست کامل صوت‌ها

| ID | دسته | متن ضبط | تلفظ | وضعیت | الزام انتشار | تصمیم | توضیح |
|---|---|---|---|---|---|---|---|
| `task.shape_aa.prompt` | letter_or_sound | آ | pending_educator_review | needs_educator_script | خیر | | |
| `task.sound_be.prompt` | letter_or_sound | ب | pending_educator_review | needs_educator_script | بله | | |
| `task.shape_mim.prompt` | letter_or_sound | م | pending_educator_review | needs_educator_script | خیر | | |
| `task.dots_pe.prompt` | letter_or_sound | پ | pending_educator_review | needs_educator_script | خیر | | |
| `task.sound_te.prompt` | letter_or_sound | ت | pending_educator_review | needs_educator_script | بله | | |
| `task.dots_nun.prompt` | letter_or_sound | ن | pending_educator_review | needs_educator_script | خیر | | |
| `task.word_baba.prompt` | word | بابا | word | needs_recording | خیر | | |
| `task.sound_mim.prompt` | letter_or_sound | م | pending_educator_review | needs_educator_script | بله | | |
| `task.sound_pe.prompt` | letter_or_sound | پ | pending_educator_review | needs_educator_script | بله | | |
| `task.initial_baba.prompt` | word | بابا | pending_educator_review | needs_educator_script | بله | | |
| `task.initial_madar.prompt` | word | مادر | pending_educator_review | needs_educator_script | بله | | |
| `task.final_ab.prompt` | word | آب | pending_educator_review | needs_educator_script | بله | | |
| `story.little_seed.page_01` | story_narration | آوا گفت: «بیا برای دانه یک خانه بسازیم!» او دانه را آرام در خاک گذاشت. | natural_story | needs_recording | خیر | | |
| `story.little_seed.page_02` | story_narration | دانه زیر خاک خوابید. آوا برایش کمی آب آورد. | natural_story | needs_recording | خیر | | |
| `story.little_seed.page_03` | story_narration | هر روز آوا به باغ سر زد. خورشید گرم می‌تابید و دانه آرام رشد می‌کرد. | natural_story | needs_recording | خیر | | |
| `story.little_seed.page_04` | story_narration | یک صبح، جوانه‌ای کوچک از خاک بیرون آمد. آوا لبخند زد. | natural_story | needs_recording | خیر | | |
| `story.little_seed.page_05` | story_narration | آوا گفت: «با کمی صبر و مراقبت، چیزهای کوچک هم بزرگ می‌شوند.» | natural_story | needs_recording | خیر | | |
| `story.friends_book.page_01` | story_narration | آوا یک کتاب تازه پیدا کرد. روی جلدش یک ستاره بود. | natural_story | needs_recording | خیر | | |
| `story.friends_book.page_02` | story_narration | خرگوش و روباه آمدند. سنجاب هم کنارشان نشست. | natural_story | needs_recording | خیر | | |
| `story.friends_book.page_03` | story_narration | آوا کتاب را باز کرد. هر دوست یک تصویر تازه در آن پیدا کرد. | natural_story | needs_recording | خیر | | |
| `story.friends_book.page_04` | story_narration | دوستان گفتند: «خواندن کنار هم چه لذتی دارد!» کتاب دوباره در باغ باز شد. | natural_story | needs_recording | خیر | | |
| `feedback.correct` | feedback_effect | تأیید پاسخ درست | non_speech_effect | needs_recording | خیر | | |

## تصمیم‌های آموزشی که به شما نیاز دارد

این‌ها از طرح پیشنهادی `docs/CURRICULUM_BLUEPRINT_FA.md` آمده‌اند (واحدهای ۴ تا ۱۰ پیشنهادی، تأییدنشده).

| # | موضوع | پرسش | پاسخ شما |
|---|---|---|---|
| ۱ | مصوت‌های کوتاه | در نوشتار معمولی نانوشته‌اند. واژه‌های اولیه با اعراب نمایش داده شوند، یا فقط واژه‌های دارای حرف کشیده و واژه‌های آشنا با بافت؟ | |
| ۲ | شکل‌های وابسته به جایگاه | حرف‌ها در آغاز، میان و پایان شکل عوض می‌کنند و فعالیت‌ها فعلاً حرف جدا نشان می‌دهند. کی و چگونه شکل‌های پیوسته آموزش داده شوند؟ | |
| ۳ | و و ی | هم صامت‌اند هم مصوت. هر دو نقش در یک واحد معرفی شوند یا فقط نقش کشیده؟ | |
| ۴ | ترتیب نشانه‌ها | محتوای فعلی پ ب ت را زود کنار هم می‌آورد و در گزینه‌ها حرف‌های هنوز آموخته‌نشده (ا د و ی) می‌گذارد. ترتیب مناسب چیست؟ | |
| ۵ | واژه‌های خانوادگی | «بابا» و «مادر» در محتوا هست. برای کودک ۴ تا ۷ ساله مناسب و کافی است؟ واژه‌های دیگری پیشنهاد می‌کنید؟ | |
| ۶ | تصویر واژه‌ها | نسخهٔ ۱ بدون تصویر واژه است. این برای کودکی که هنوز نمی‌خواند کافی است؟ | |
| ۷ | طول و تعداد | ۳ تا ۶ فعالیت در هر درس و حداکثر ۳ نشانهٔ تازه در هر واحد مناسب است؟ `blend_words_04` فقط ۲ فعالیت دارد. | |
| ۸ | نام حرف یا واج | prompt «ب» یعنی نام نشانه یا صدای آن؟ متن ضبط هر مورد را تعیین کنید. | |

## ثبت بازبینی

| مورد | مقدار |
|---|---|
| نام بازبین | |
| صلاحیت و تجربه | |
| تاریخ | |
| دامنهٔ بازبینی (کدام واحدها) | |
| محدودیت‌ها | |
