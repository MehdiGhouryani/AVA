#!/usr/bin/env python3
"""Deterministic AVA content v2 -> v3 migration and compatibility check."""
from __future__ import annotations
from pathlib import Path
import argparse
import json
from strict_json import load_json

ROOT = Path(__file__).resolve().parents[1]
CONTENT_VERSION = "0.6.1-dev.1"
SKILLS = [
    {"id": "grapheme_recognition", "title": "شناخت نشانه", "description": "تشخیص دیداری نشانه‌های فارسی"},
    {"id": "phoneme_awareness", "title": "آگاهی واجی", "description": "توجه به صدای هدف با راهنمای نوشتاری"},
    {"id": "word_recognition", "title": "شناخت واژه", "description": "پیداکردن واژه آشنا در میان گزینه‌ها"},
    {"id": "word_building", "title": "ساخت واژه", "description": "چیدن نشانه‌ها برای ساخت واژه"},
    {"id": "guided_review", "title": "مرور هدایت‌شده", "description": "بازتمرین مهارت معرفی‌شده بدون ادعای تسلط"},
]
TASK_SKILLS = {
    "shape_aa": ["grapheme_recognition"], "sound_be": ["phoneme_awareness"],
    "shape_mim": ["grapheme_recognition"], "dots_pe": ["grapheme_recognition"],
    "sound_te": ["phoneme_awareness"], "dots_nun": ["grapheme_recognition"],
    "build_ab": ["word_building"], "word_baba": ["word_recognition"],
    "build_bad": ["word_building"],
    "sound_mim": ["phoneme_awareness", "guided_review"],
    "sound_pe": ["phoneme_awareness", "guided_review"],
    "build_ab_review": ["word_building", "guided_review"],
}
UNIT_LAYOUT = [
    ("unit_signs", "نشانه‌ها و صداها", ["visual_shapes_01", "dots_02"]),
    ("unit_words", "واژه‌های نخست", ["words_03", "garden_04"]),
]

def task_v3(task: dict) -> dict:
    kind = "dot_family" if task["id"] in {"dots_pe", "dots_nun"} else task["kind"]
    has_prompt_audio = kind in {"recognize", "listen", "dot_family", "positional_form", "initial_sound", "final_sound"}
    result = {
        "id": task["id"], "kind": kind, "target": task["target"],
        "skillIds": TASK_SKILLS[task["id"]], "assetRefs": [],
        "audioRef": f"task.{task['id']}.prompt" if has_prompt_audio else None,
        "audioRequiredForPublication": kind in {"listen", "initial_sound", "final_sound"},
        "writtenFallback": task["target"] if has_prompt_audio else None,
    }
    result["tokens" if kind == "build_word" else "choices"] = task["tokens" if kind == "build_word" else "choices"]
    return result

def sound_unit() -> dict:
    def choice(task_id: str, kind: str, target: str, fallback: str, choices: list[str]) -> dict:
        return {
            "id": task_id, "kind": kind, "target": target,
            "skillIds": ["phoneme_awareness"], "assetRefs": [],
            "audioRef": f"task.{task_id}.prompt", "audioRequiredForPublication": True,
            "writtenFallback": fallback, "choices": choices,
        }
    def blend(task_id: str, target: str, tokens: list[str]) -> dict:
        return {
            "id": task_id, "kind": "blend", "target": target,
            "skillIds": ["word_building"], "assetRefs": [], "audioRef": None,
            "audioRequiredForPublication": False, "writtenFallback": None, "tokens": tokens,
        }
    return {
        "id": "unit_sounds", "title": "صداهای واژه", "order": 2,
        "lessons": [
            {
                "id": "edge_sounds_03", "title": "اول و آخر واژه",
                "goal": "صدای آغاز و پایان چند واژهٔ آشنا را پیدا کن.", "order": 1,
                "tasks": [
                    choice("initial_baba", "initial_sound", "ب", "بابا", ["ب", "م", "ن"]),
                    choice("initial_madar", "initial_sound", "م", "مادر", ["ن", "م", "ب"]),
                    choice("final_ab", "final_sound", "ب", "آب", ["ب", "د", "م"]),
                ],
            },
            {
                "id": "blend_words_04", "title": "صداها کنار هم",
                "goal": "نشانه‌ها را به ترتیب بچین و واژهٔ آشنا بساز.", "order": 2,
                "tasks": [blend("blend_ab", "آب", ["ب", "آ"]), blend("blend_bad", "باد", ["د", "ب", "ا"])],
            },
        ],
    }

def migrate(data: dict) -> dict:
    if data.get("schemaVersion") == 3:
        return data
    if data.get("version") != 2 or data.get("approvedForRelease") is not False:
        raise ValueError("Only unapproved AVA content v2 can be migrated by this script")
    lessons_by_id = {lesson["id"]: lesson for lesson in data["lessons"]}
    units = []
    for unit_order, (unit_id, title, lesson_ids) in enumerate(UNIT_LAYOUT, start=1):
        lessons = []
        for lesson_order, lesson_id in enumerate(lesson_ids, start=1):
            lesson = lessons_by_id.pop(lesson_id)
            lessons.append({"id": lesson["id"], "title": lesson["title"], "goal": lesson["goal"], "order": lesson_order, "tasks": [task_v3(task) for task in lesson["tasks"]]})
        units.append({"id": unit_id, "title": title, "order": unit_order, "lessons": lessons})
    if lessons_by_id:
        raise ValueError(f"Unmapped lessons: {sorted(lessons_by_id)}")
    units[1]["order"] = 3
    units.insert(1, sound_unit())
    stories = []
    for order, story in enumerate(data["stories"], start=1):
        stories.append({
            "id": story["id"], "unitId": "unit_words", "title": story["title"],
            "subtitle": story["subtitle"], "order": order, "artRef": story["art"],
            "pages": [{"id": f"{story['id']}_page_{index + 1:02d}", "text": text, "audioRef": f"story.{story['id']}.page_{index + 1:02d}"} for index, text in enumerate(story["pages"])],
            "question": story["question"], "choices": story["choices"], "answer": story["answer"],
        })
    return {
        "schemaVersion": 3, "contentVersion": CONTENT_VERSION, "locale": "fa-IR",
        "approvedForRelease": False, "scope": data["scope"], "skills": SKILLS,
        "levels": [{"id": "level_1", "title": "شروع خواندن", "order": 1, "units": units}],
        "stories": stories,
    }

def _same(old: dict, new: dict, ignore: tuple[str, ...], where: str) -> None:
    left = {k: v for k, v in old.items() if k not in ignore}
    right = {k: v for k, v in new.items() if k not in ignore}
    assert left == right, f"Frozen v2 item changed in canonical v3: {where}"


def _by_id(items: list[dict], where: str) -> dict[str, dict]:
    ids = [item["id"] for item in items]
    assert len(ids) == len(set(ids)), f"Duplicate ids in {where}"
    return dict(zip(ids, items))


def _keep_order(old_ids: list[str], new_ids: list[str], where: str) -> None:
    assert [i for i in new_ids if i in old_ids] == old_ids, f"Frozen v2 order changed in {where}"


def assert_preserved(migrated: dict, canonical: dict) -> None:
    """Everything from the frozen v2 migration must still exist unchanged in canonical v3, matched by id.

    New skills, units, lessons, tasks and stories may be added; existing ids, targets, choices, tokens,
    audio refs and titles may not change (IDs stay stable; changes need a migration and validator).
    """
    assert canonical["schemaVersion"] == migrated["schemaVersion"] and canonical["locale"] == migrated["locale"]
    skills = _by_id(canonical["skills"], "skills")
    for skill in migrated["skills"]:
        assert skill["id"] in skills, f"Frozen v2 skill missing: {skill['id']}"
        _same(skill, skills[skill["id"]], (), f"skill {skill['id']}")
    levels = _by_id(canonical["levels"], "levels")
    for level in migrated["levels"]:
        assert level["id"] in levels, f"Frozen v2 level missing: {level['id']}"
        new_level = levels[level["id"]]
        _same(level, new_level, ("order", "units"), f"level {level['id']}")
        units = _by_id(new_level["units"], f"units of {level['id']}")
        _keep_order([u["id"] for u in level["units"]], list(units), f"units of {level['id']}")
        for unit in level["units"]:
            assert unit["id"] in units, f"Frozen v2 unit missing: {unit['id']}"
            new_unit = units[unit["id"]]
            _same(unit, new_unit, ("order", "lessons"), f"unit {unit['id']}")
            lessons = _by_id(new_unit["lessons"], f"lessons of {unit['id']}")
            _keep_order([x["id"] for x in unit["lessons"]], list(lessons), f"lessons of {unit['id']}")
            for lesson in unit["lessons"]:
                assert lesson["id"] in lessons, f"Frozen v2 lesson missing: {lesson['id']}"
                new_lesson = lessons[lesson["id"]]
                _same(lesson, new_lesson, ("order", "tasks"), f"lesson {lesson['id']}")
                tasks = _by_id(new_lesson["tasks"], f"tasks of {lesson['id']}")
                _keep_order([t["id"] for t in lesson["tasks"]], list(tasks), f"tasks of {lesson['id']}")
                for task in lesson["tasks"]:
                    assert task["id"] in tasks, f"Frozen v2 task missing: {task['id']}"
                    _same(task, tasks[task["id"]], (), f"task {task['id']}")
    stories = _by_id(canonical["stories"], "stories")
    for story in migrated["stories"]:
        assert story["id"] in stories, f"Frozen v2 story missing: {story['id']}"
        _same(story, stories[story["id"]], ("order",), f"story {story['id']}")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", type=Path, default=ROOT / "design/fixtures/content-v2.json")
    parser.add_argument("--output", type=Path)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    migrated = migrate(load_json(args.input))
    canonical = ROOT / "design/content.fa.json"
    if args.check:
        expected = load_json(canonical)
        assert_preserved(migrated, expected)
        assert migrate(expected) == expected, "v3 migration must be idempotent"
        print("Content migration passed: frozen v2 items are preserved in canonical v3 (new content may be added); v3 is idempotent.")
        return
    text = json.dumps(migrated, ensure_ascii=False, indent=2) + "\n"
    if args.output:
        args.output.write_text(text, encoding="utf-8")
    else:
        print(text, end="")

if __name__ == "__main__":
    main()
