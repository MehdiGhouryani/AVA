#!/usr/bin/env python3
"""Generate docs/EDUCATOR_REVIEW_PACK_FA.md from design/content.fa.json and design/audio.inventory.json.

Deterministic and offline. `--check` fails if the committed pack is stale. The pack is a review form,
not approved content: it never changes approvedForRelease.
"""
import json, sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs/EDUCATOR_REVIEW_PACK_FA.md"
FA = str.maketrans("0123456789", "۰۱۲۳۴۵۶۷۸۹")
KINDS = {"recognize": "تشخیص شکل", "listen": "شنیدن صدا", "dot_family": "هم‌خانواده‌ها", "initial_sound": "صدای آغاز",
         "final_sound": "صدای پایان", "blend": "کنار هم", "build_word": "ساخت واژه",
         "find": "یافتن", "bubble": "حباب صدا", "match": "جفت‌یابی"}
DECISIONS = [
    ("مصوت‌های کوتاه", "در نوشتار معمولی نانوشته‌اند. واژه‌های اولیه با اعراب نمایش داده شوند، یا فقط واژه‌های دارای حرف کشیده و واژه‌های آشنا با بافت؟"),
    ("شکل‌های وابسته به جایگاه", "حرف‌ها در آغاز، میان و پایان شکل عوض می‌کنند و فعالیت‌ها فعلاً حرف جدا نشان می‌دهند. کی و چگونه شکل‌های پیوسته آموزش داده شوند؟"),
    ("و و ی", "هم صامت‌اند هم مصوت. هر دو نقش در یک واحد معرفی شوند یا فقط نقش کشیده؟"),
    ("ترتیب نشانه‌ها", "محتوای فعلی پ ب ت را زود کنار هم می‌آورد و در گزینه‌ها حرف‌های هنوز آموخته‌نشده (ا د و ی) می‌گذارد. ترتیب مناسب چیست؟"),
    ("واژه‌های خانوادگی", "«بابا» و «مادر» در محتوا هست. برای کودک ۴ تا ۷ ساله مناسب و کافی است؟ واژه‌های دیگری پیشنهاد می‌کنید؟"),
    ("تصویر واژه‌ها", "نسخهٔ ۱ بدون تصویر واژه است. این برای کودکی که هنوز نمی‌خواند کافی است؟"),
    ("طول و تعداد", "۳ تا ۶ فعالیت در هر درس و حداکثر ۳ نشانهٔ تازه در هر واحد مناسب است؟ `blend_words_04` فقط ۲ فعالیت دارد."),
    ("نام حرف یا واج", "prompt «ب» یعنی نام نشانه یا صدای آن؟ متن ضبط هر مورد را تعیین کنید.")]


def fa(n):
    return str(n).translate(FA)


def cell(x):
    if x is None or x == "":
        return "—"
    if isinstance(x, list):
        x = "، ".join(str(i) for i in x)
    return str(x).replace("|", "\\|")


def render() -> str:
    c = json.loads((ROOT / "design/content.fa.json").read_text(encoding="utf-8"))
    a = json.loads((ROOT / "design/audio.inventory.json").read_text(encoding="utf-8"))
    audio = {x["id"]: x for x in a["assets"]}
    skills = {s["id"]: s.get("title", s["id"]) for s in c["skills"]}
    tasks = [t for lv in c["levels"] for u in lv["units"] for l in u["lessons"] for t in l["tasks"]]
    for t in tasks:
        assert t["audioRef"] is None or t["audioRef"] in audio, f"missing audio asset for {t['id']}"
    L = ["# بستهٔ بازبینی برای متخصص آموزش خواندن فارسی",
         "",
         "> **این فرم است، نه محتوای تأییدشده.** همهٔ موارد پایین همین حالا در اپ هستند و `approvedForRelease=false` است. "
         "تا تأیید ثبت‌شدهٔ شما هیچ‌چیز تأییدشده شمرده نمی‌شود. این فایل را اسکریپت `scripts/export-educator-pack.py` از "
         "`design/content.fa.json` و `design/audio.inventory.json` می‌سازد؛ دستی ویرایشش نکنید.",
         "",
         f"نسخهٔ محتوا: `{c['contentVersion']}` · زبان: `{c['locale']}` · {fa(len(c['levels'][0]['units']))} واحد، "
         f"{fa(sum(len(u['lessons']) for lv in c['levels'] for u in lv['units']))} درس، {fa(len(tasks))} فعالیت، "
         f"{fa(len(c['stories']))} قصه، {fa(len(audio))} نیاز صوتی.",
         "",
         "## چه چیزی از شما خواسته می‌شود",
         "",
         "برای هر ردیف یکی از سه علامت را در ستون «تصمیم» بنویسید: **✔** تأیید، **✎** اصلاح (در «توضیح» بنویسید چه)، **✖** رد. "
         "اگر دربارهٔ مورد نظری ندارید خالی بگذارید؛ خالی یعنی «بررسی نشده» و تأیید نیست. "
         "هر ردیف شناسهٔ پایدار دارد (ID)؛ در پاسخ از همان استفاده کنید.",
         "",
         "پرسش‌های کلی برای هر درس: آیا هدف مناسب سن ۴ تا ۷ سال است؟ ترتیب فعالیت‌ها منطقی است؟ گزینه‌های نادرست (distractor) گمراه‌کننده یا ناعادلانه نیستند؟ "
         "متن مکتوب جایگزین (fallback) درست است؟ تلفظ مورد نیاز برای ضبط روشن است؟",
         ""]
    for lv in c["levels"]:
        for u in lv["units"]:
            L += [f"## واحد {fa(u['order'])} — {u['title']} (`{u['id']}`)", ""]
            for l in u["lessons"]:
                L += [f"### درس {fa(l['order'])} — {l['title']} (`{l['id']}`)", "", f"هدف: {l['goal']}", "",
                      "| ID | نوع | هدف | گزینه‌ها / توکن‌ها | متن مکتوب | مهارت | صوت | تصمیم | توضیح |", "|---|---|---|---|---|---|---|---|---|"]
                for t in l["tasks"]:
                    opts = t.get("choices") or t.get("tokens")
                    au = audio.get(t["audioRef"]) if t["audioRef"] else None
                    sound = f"`{t['audioRef']}`: «{au['recordingText']}» ({au['status']})" if au else "بدون صوت"
                    L.append("| " + " | ".join([f"`{t['id']}`", KINDS.get(t['kind'], t['kind']), cell(t["target"]), cell(opts),
                                               cell(t["writtenFallback"]), cell([skills.get(s, s) for s in t["skillIds"]]), sound, "", ""]) + " |")
                L.append("")
    L += ["## قصه‌ها", ""]
    for s in c["stories"]:
        L += [f"### {s['title']} (`{s['id']}`) — {s['subtitle']}", "", "| ID | متن صفحه | صوت | تصمیم | توضیح |", "|---|---|---|---|---|"]
        for p in s["pages"]:
            L.append(f"| `{p['id']}` | {cell(p['text'])} | `{p['audioRef']}` | | |")
        L += ["", f"پرسش: {s['question']} · گزینه‌ها: {cell(s['choices'])} · پاسخ: {s['answer']}", "", "| تصمیم دربارهٔ پرسش | توضیح |", "|---|---|", "| | |", ""]
    L += ["## فهرست کامل صوت‌ها", "", "| ID | دسته | متن ضبط | تلفظ | وضعیت | الزام انتشار | تصمیم | توضیح |", "|---|---|---|---|---|---|---|---|"]
    for x in a["assets"]:
        L.append(f"| `{x['id']}` | {x['category']} | {cell(x['recordingText'])} | {x['pronunciationIntent']} | {x['status']} | "
                 f"{'بله' if x['requiredForPublication'] else 'خیر'} | | |")
    L += ["", "## تصمیم‌های آموزشی که به شما نیاز دارد", "",
          "این‌ها از طرح پیشنهادی `docs/CURRICULUM_BLUEPRINT_FA.md` آمده‌اند (واحدهای ۴ تا ۱۰ پیشنهادی، تأییدنشده).", "",
          "| # | موضوع | پرسش | پاسخ شما |", "|---|---|---|---|"]
    for i, (a_, b_) in enumerate(DECISIONS, 1):
        L.append(f"| {fa(i)} | {a_} | {b_} | |")
    L += ["", "## ثبت بازبینی", "", "| مورد | مقدار |", "|---|---|", "| نام بازبین | |", "| صلاحیت و تجربه | |", "| تاریخ | |",
          "| دامنهٔ بازبینی (کدام واحدها) | |", "| محدودیت‌ها | |", ""]
    return "\n".join(L)


if __name__ == "__main__":
    text = render()
    if "--check" in sys.argv:
        ok = OUT.exists() and OUT.read_text(encoding="utf-8") == text
        print("Educator pack up to date." if ok else "Educator pack is stale or missing.")
        sys.exit(0 if ok else 1)
    OUT.write_text(text, encoding="utf-8")
    print(f"Wrote {OUT.relative_to(ROOT)}: {len(text.splitlines())} lines")
