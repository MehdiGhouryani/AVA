#!/usr/bin/env python3
"""Validate AVA's phase-zero audio inventory against current content IDs."""
from pathlib import Path
import json
from strict_json import load_json

root = Path(__file__).resolve().parents[1]
content = load_json(root / "design/content.fa.json")
inventory = load_json(root / "design/audio.inventory.json")
schema = load_json(root / "design/audio.inventory.schema.json")

assert schema["$id"].endswith("audio-inventory-v1.json")
assert inventory["schemaVersion"] == 1
assert inventory["contentSchemaVersion"] == content["schemaVersion"] == 3
assert inventory["contentVersion"] == content["contentVersion"]
assert inventory["locale"] == "fa-IR"
assert inventory["policy"] == {
    "finalVoice": "human-reviewed",
    "ttsFallback": "offline-device-only",
    "requiredStatusForPublish": "approved",
}

allowed_status = {"needs_educator_script", "needs_recording", "draft", "processing", "review", "approved", "rejected", "archived"}
assets = inventory["assets"]
ids = [item["id"] for item in assets]
assert len(ids) == len(set(ids)), "Duplicate audio asset id"

task_refs = {}
story_refs = {}
for item in assets:
    assert item["status"] in allowed_status, item["id"]
    assert item["recordingText"].strip(), item["id"]
    assert item["contentRefs"], item["id"]
    if item["status"] != "approved":
        assert item["masterAssetKey"] is None and item["deliveryAssetKey"] is None and item["sha256"] is None, item["id"]
    for ref in item["contentRefs"]:
        if ref["type"] == "task":
            key = (ref["lessonId"], ref["taskId"])
            assert key not in task_refs, ("Duplicate task audio ref", key)
            task_refs[key] = item
        elif ref["type"] == "story_page":
            key = (ref["storyId"], ref["pageIndex"])
            assert key not in story_refs, ("Duplicate story audio ref", key)
            story_refs[key] = item
        else:
            assert ref["type"] == "system" and ref.get("key"), item["id"]

expected_tasks = {}
choice_kinds = {"recognize", "listen", "dot_family", "positional_form", "initial_sound", "final_sound"}
listening_kinds = {"listen", "initial_sound", "final_sound"}
lessons = [lesson for level in content["levels"] for unit in level["units"] for lesson in unit["lessons"]]
for lesson in lessons:
    for task in lesson["tasks"]:
        if task["kind"] not in choice_kinds:
            continue
        key = (lesson["id"], task["id"])
        expected_tasks[key] = task
        item = task_refs.get(key)
        assert item is not None, ("Missing task audio inventory", key)
        assert task["audioRef"] == item["id"], key
        assert item["recordingText"] == task["writtenFallback"], key
        assert task["audioRequiredForPublication"] is (task["kind"] in listening_kinds), key
        assert item["requiredForPublication"] is task["audioRequiredForPublication"], key
        assert item["fallback"] == ("written_hint" if task["kind"] in listening_kinds else "visible_text"), key
assert set(task_refs) == set(expected_tasks), ("Unknown task audio refs", set(task_refs) - set(expected_tasks))

expected_story_pages = {}
for story in content["stories"]:
    for index, text in enumerate(story["pages"]):
        key = (story["id"], index)
        expected_story_pages[key] = text
        item = story_refs.get(key)
        assert item is not None, ("Missing story audio inventory", key)
        assert item["id"] == text["audioRef"], key
        assert item["recordingText"] == text["text"], key
        assert item["requiredForPublication"] is False, key
        assert item["fallback"] == "visible_text", key
assert set(story_refs) == set(expected_story_pages), ("Unknown story audio refs", set(story_refs) - set(expected_story_pages))

print(f"Audio inventory passed: {len(assets)} assets, {len(task_refs)} task prompts, {len(story_refs)} story pages.")
