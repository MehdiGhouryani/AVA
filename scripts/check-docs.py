#!/usr/bin/env python3
"""Offline consistency check for the project documents.

Checks: (1) every docs/*.md is listed in docs/README.md, (2) every full repo path written in backticks
(docs/, design/, scripts/, android/, harness/, cms/, .agents/) exists unless it is a declared planned path,
(3) the test and string counts in PROJECT_STATE.md match the source. Not part of audit-project.py.
"""
import re, subprocess, sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PREFIXES = ("docs/", "design/", "scripts/", "android/", "harness/", "cms/", ".agents/")
PLANNED = {"design/curriculum.blueprint.json", "design/drafts/", "docs/CONTENT_AUTHORING_FA.md"}
SKIP_DIRS = ("docs/preview", "docs/history", ".agents/skills", "cms/payload/node_modules")


def tracked() -> list[str]:
    return [f for f in subprocess.run(["git", "ls-files"], cwd=ROOT, capture_output=True, text=True).stdout.split("\n") if f]


def main() -> int:
    files = tracked()
    fileset = set(files)
    problems: list[str] = []
    index = (ROOT / "docs/README.md").read_text(encoding="utf-8")
    for f in files:
        if f.startswith("docs/") and f.endswith(".md") and f != "docs/README.md" and not f.startswith(SKIP_DIRS[:2]):
            if f[len("docs/"):] not in index:
                problems.append(f"not indexed in docs/README.md: {f}")
    for f in files:
        if not f.endswith(".md") or f.startswith(SKIP_DIRS) or f in ("harness/WORKLOG.md",):
            continue
        text = (ROOT / f).read_text(encoding="utf-8")
        for m in re.finditer(r"`([A-Za-z0-9_./-]+)`", text):
            p = m.group(1)
            if not p.startswith(PREFIXES) or "..." in p or p in PLANNED:
                continue
            if p not in fileset and not any(x.startswith(p.rstrip("/") + "/") for x in files):
                problems.append(f"{f}: missing path `{p}`")
    tests = sum(Path(p).read_text(encoding="utf-8").count("@Test") for p in (ROOT / "android/app/src/test").rglob("*.kt"))
    state = (ROOT / "PROJECT_STATE.md").read_text(encoding="utf-8")
    m = re.search(r"(\d+) tests exist in source", state)
    if not m or int(m.group(1)) != tests:
        problems.append(f"PROJECT_STATE.md says {m.group(1) if m else 'no count'} tests exist in source, source has {tests}")
    strings = len(re.findall(r'<string name=', (ROOT / "android/app/src/main/res/values/strings.xml").read_text(encoding="utf-8")))
    m = re.search(r"(\d+) strings in generated resources", state)
    if not m or int(m.group(1)) != strings:
        problems.append(f"PROJECT_STATE.md says {m.group(1) if m else 'no count'} strings, resources have {strings}")
    for p in problems:
        print("PROBLEM", p)
    print(f"Docs check: {len(problems)} problem(s); {tests} tests in source, {strings} strings.")
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
