#!/usr/bin/env python3
"""Validate AVA v3 content, migration compatibility and release hashes offline."""
from pathlib import Path
import hashlib
import json
import re
import subprocess
import sys
from strict_json import load_json

ROOT = Path(__file__).resolve().parents[1]
content_path = ROOT / "design/content.fa.json"
audio_path = ROOT / "design/audio.inventory.json"
content = load_json(content_path)
schema = load_json(ROOT / "design/content.schema.json")
release = load_json(ROOT / "design/content.release.json")
release_schema = load_json(ROOT / "design/content.release.schema.json")
assets = {item["id"] for item in load_json(ROOT / "design/assets.manifest.json")["assets"]}
audio = {item["id"]: item for item in load_json(audio_path)["assets"]}

assert schema["$id"].endswith("content-v3.json") and content["schemaVersion"] == 3
assert release_schema["$id"].endswith("content-release-v1.json") and release["manifestVersion"] == 1
assert content["locale"] == "fa-IR" and content["approvedForRelease"] is False
assert re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+(?:-[a-z0-9.]+)?", content["contentVersion"])

def ordered(values: list[dict]) -> None:
    orders = [item["order"] for item in values]
    assert orders == list(range(1, len(values) + 1)), orders

ids: set[str] = set()
skill_ids = [skill["id"] for skill in content["skills"]]
assert len(skill_ids) == len(set(skill_ids))
units: set[str] = set()
task_count = 0
token_kinds = {"build_word", "segment", "blend"}
listening_kinds = {"listen", "initial_sound", "final_sound"}
visual_kinds = {"recognize", "dot_family", "positional_form"}
for level in content["levels"]:
    assert level["id"] not in ids; ids.add(level["id"])
    ordered(level["units"])
    for unit in level["units"]:
        assert unit["id"] not in units; units.add(unit["id"])
        ordered(unit["lessons"])
        for lesson in unit["lessons"]:
            assert lesson["id"] not in ids; ids.add(lesson["id"])
            task_ids = set()
            for task in lesson["tasks"]:
                task_count += 1
                assert task["id"] not in task_ids; task_ids.add(task["id"])
                assert task["skillIds"] and set(task["skillIds"]) <= set(skill_ids)
                assert set(task["assetRefs"]) <= assets
                assert task["kind"] in token_kinds | listening_kinds | visual_kinds
                if task["kind"] in token_kinds:
                    assert task["audioRef"] is None and task["writtenFallback"] is None
                    assert not task["audioRequiredForPublication"] and not task.get("choices")
                    assert sorted("".join(task["tokens"])) == sorted(task["target"])
                else:
                    assert task["audioRef"] in audio and task["writtenFallback"].strip()
                    assert len(task["choices"]) == len(set(task["choices"])) == 3 and task["target"] in task["choices"]
                    assert task["audioRequiredForPublication"] is (task["kind"] in listening_kinds)
                    if task["kind"] in visual_kinds:
                        assert task["writtenFallback"] == task["target"]

page_ids = set()
for story in content["stories"]:
    assert story["unitId"] in units and story["artRef"] in assets
    assert story["answer"] in story["choices"] and len(story["choices"]) == len(set(story["choices"]))
    for page in story["pages"]:
        assert page["id"] not in page_ids; page_ids.add(page["id"])
        assert page["audioRef"] in audio and page["text"].strip()

digest = lambda path: hashlib.sha256(path.read_bytes()).hexdigest()
assert release["schemaVersion"] == content["schemaVersion"]
assert release["contentVersion"] == content["contentVersion"]
assert release["locale"] == content["locale"]
assert release["approvedForRelease"] == content["approvedForRelease"]
assert release["bundlePath"] == "design/content.fa.json" and release["bundleSha256"] == digest(content_path)
assert release["audioInventoryPath"] == "design/audio.inventory.json" and release["audioInventorySha256"] == digest(audio_path)
if release["status"] == "published":
    assert release["approvedForRelease"] and release["publishedAt"] and release["signature"]
else:
    assert release["status"] == "development" and release["signature"] is None

subprocess.run([sys.executable, str(ROOT / "scripts/migrate-content-v2-to-v3.py"), "--check"], check=True)
print(f"Content contract passed: schema v3, {len(content['levels'])} level, {len(units)} units, {task_count} tasks, {len(page_ids)} story pages, release hashes verified.")
