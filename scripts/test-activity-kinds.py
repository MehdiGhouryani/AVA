#!/usr/bin/env python3
"""Regression test for the Find, Bubble and Match activity kinds.

Copies scripts/ and design/*.json into a temp root, injects one probe lesson that uses the three kinds,
and checks that the real validators accept it, reject broken variants, and keep Match blocked
in shipped content until its renderer exists. Nothing in the repo is modified.
"""
import copy, hashlib, json, os, shutil, subprocess, sys, tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def run(root: Path, script: str, allow_unrendered: bool) -> subprocess.CompletedProcess:
    env = {**os.environ, "AVA_ALLOW_UNRENDERED_KINDS": "1" if allow_unrendered else "0"}
    return subprocess.run([sys.executable, str(root / "scripts" / script)], cwd=root, env=env, capture_output=True, text=True)


def write_set(root: Path, content: dict, audio: dict) -> None:
    for name, data in (("content.fa.json", content), ("audio.inventory.json", audio)):
        (root / "design" / name).write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    rel = json.loads((root / "design/content.release.json").read_text(encoding="utf-8"))
    sha = lambda n: hashlib.sha256((root / "design" / n).read_bytes()).hexdigest()
    rel["bundleSha256"], rel["audioInventorySha256"] = sha("content.fa.json"), sha("audio.inventory.json")
    (root / "design/content.release.json").write_text(json.dumps(rel, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def probe(content: dict, audio: dict) -> tuple[dict, dict]:
    content, audio = copy.deepcopy(content), copy.deepcopy(audio)
    unit = content["levels"][0]["units"][-1]
    lesson = {"id": "kinds_probe", "title": "آزمون نوع‌ها", "goal": "آزمون فنی", "order": len(unit["lessons"]) + 1, "tasks": [
        {"id": "find_probe", "kind": "find", "target": "ب", "skillIds": ["grapheme_recognition"], "assetRefs": [], "audioRef": "task.find_probe.prompt",
         "audioRequiredForPublication": False, "writtenFallback": "ب", "choices": ["ب", "پ", "ت"]},
        {"id": "bubble_probe", "kind": "bubble", "target": "م", "skillIds": ["phoneme_awareness"], "assetRefs": [], "audioRef": "task.bubble_probe.prompt",
         "audioRequiredForPublication": True, "writtenFallback": "م", "choices": ["م", "ن", "ب"]},
        {"id": "match_probe", "kind": "match", "target": "آب=آ|باد=ب|گل=گ", "skillIds": ["word_building"], "assetRefs": [], "audioRef": None,
         "audioRequiredForPublication": False, "writtenFallback": None, "tokens": ["آب", "آ", "باد", "ب", "گل", "گ"]}]}
    unit["lessons"].append(lesson)
    template = next(a for a in audio["assets"] if a["category"] == "letter_or_sound" and a["contentRefs"][0]["type"] == "task")
    for tid, text, required, fallback in (("find_probe", "ب", False, "visible_text"), ("bubble_probe", "م", True, "written_hint")):
        item = copy.deepcopy(template)
        item.update(id=f"task.{tid}.prompt", recordingText=text, requiredForPublication=required, fallback=fallback,
                    contentRefs=[{"type": "task", "lessonId": "kinds_probe", "taskId": tid}])
        audio["assets"].append(item)
    return content, audio


def main() -> int:
    content = json.loads((ROOT / "design/content.fa.json").read_text(encoding="utf-8"))
    audio = json.loads((ROOT / "design/audio.inventory.json").read_text(encoding="utf-8"))
    failures = []
    with tempfile.TemporaryDirectory() as tmp:
        root = Path(tmp)
        shutil.copytree(ROOT / "scripts", root / "scripts", ignore=shutil.ignore_patterns("__pycache__", "jvm-core-check*"))
        (root / "design").mkdir()
        for f in (ROOT / "design").glob("*.json"):
            shutil.copy(f, root / "design" / f.name)
        shutil.copytree(ROOT / "design/fixtures", root / "design/fixtures")
        good_c, good_a = probe(content, audio)
        write_set(root, good_c, good_a)
        checks = [("contract accepts find, bubble and match", run(root, "validate-content-contract.py", True).returncode == 0),
                  ("audio inventory accepts find and bubble", run(root, "validate-audio-inventory.py", True).returncode == 0)]
        blocked = run(root, "validate-content-contract.py", False)
        checks.append(("match stays blocked in shipped content", blocked.returncode != 0 and "No renderer yet" in blocked.stderr))
        for name, mutate in (
            ("odd number of match tokens", lambda t: t["tokens"].pop()),
            ("match target out of sync with tokens", lambda t: t.update(target="آب=آ|گل=گ|باد=ب")),
            ("bubble without publication audio flag", lambda t: t.update(audioRequiredForPublication=False)),
        ):
            bad_c, bad_a = probe(content, audio)
            lesson = bad_c["levels"][0]["units"][-1]["lessons"][-1]
            mutate(next(t for t in lesson["tasks"] if t["kind"] == ("bubble" if "bubble" in name else "match")))
            write_set(root, bad_c, bad_a)
            checks.append((f"rejects: {name}", run(root, "validate-content-contract.py", True).returncode != 0))
        changed_c, changed_a = probe(content, audio)
        changed_c["levels"][0]["units"][0]["lessons"][0]["title"] = "عنوان تغییر کرده"
        write_set(root, changed_c, changed_a)
        checks.append(("rejects: a frozen v2 lesson was renamed", run(root, "validate-content-contract.py", True).returncode != 0))
    for label, ok in checks:
        print(("PASS " if ok else "FAIL ") + label)
        if not ok:
            failures.append(label)
    print(f"Activity kinds test: {len(checks) - len(failures)} of {len(checks)} passed.")
    return 1 if failures else 0


if __name__ == "__main__":
    sys.exit(main())
