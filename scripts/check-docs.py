#!/usr/bin/env python3
"""Offline repository documentation and snapshot consistency checks."""
from pathlib import Path
import re
import subprocess
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
PREFIXES = ("docs/", "design/", "scripts/", "android/", "harness/", "cms/", ".agents/")
PLANNED = {
    "design/curriculum.blueprint.json",
    "design/drafts/",
    "docs/CONTENT_AUTHORING_FA.md",
}
SKIP_PREFIXES = (
    "docs/preview",
    "docs/history",
    ".agents/skills",
    "cms/payload/node_modules",
)
TEST_COUNT_LABEL = "تعداد تست‌های Native در سورس"
STRING_COUNT_LABEL = "رشته‌های Android تولیدی"
SNAPSHOT_DIGITS = re.compile(r"^[0-9۰-۹٠-٩,٬]+$")
DIGIT_MAP = str.maketrans(
    "۰۱۲۳۴۵۶۷۸۹٠١٢٣٤٥٦٧٨٩",
    "01234567890123456789",
)


def extract_snapshot_count(state: str, label: str) -> int:
    """Read exactly one bold integer from a labeled PROJECT_STATE bullet."""
    pattern = re.compile(
        rf"(?m)^\s*-\s*{re.escape(label)}:\s*\*\*([0-9۰-۹٠-٩,٬]+)\*\*\s*\.?\s*$"
    )
    matches = pattern.findall(state)
    if len(matches) != 1:
        raise ValueError(f"expected one '{label}' snapshot line, found {len(matches)}")
    raw = matches[0]
    if not SNAPSHOT_DIGITS.fullmatch(raw):
        raise ValueError(f"invalid numeric value for '{label}': {raw}")
    normalized = raw.translate(DIGIT_MAP).replace(",", "").replace("٬", "")
    return int(normalized)


def count_test_annotations(contents) -> int:
    """Count Kotlin @Test annotations in the supplied source contents."""
    return sum(content.count("@Test") for content in contents)


def count_android_strings(xml_content: str) -> int:
    """Count generated Android string resources and reject duplicate names."""
    root = ET.fromstring(xml_content)
    if root.tag != "resources":
        raise ValueError("Android strings root element must be <resources>")
    elements = root.findall("string")
    names = [element.get("name") for element in elements]
    if any(not name for name in names):
        raise ValueError("Android string resource is missing its name")
    if len(names) != len(set(names)):
        raise ValueError("duplicate Android string resource name")
    return len(elements)


def is_skipped(path: str) -> bool:
    return any(path.startswith(prefix) for prefix in SKIP_PREFIXES)


def tracked_files(root: Path = ROOT) -> list[str]:
    """Include tracked and non-ignored new files; support source ZIPs without Git."""
    try:
        result = subprocess.run(
            ["git", "ls-files", "--cached", "--others", "--exclude-standard"],
            cwd=root,
            check=False,
            capture_output=True,
            text=True,
        )
    except OSError:
        result = None
    if result is not None and result.returncode == 0:
        return [path for path in result.stdout.splitlines() if path]
    if (root / ".git").exists():
        raise subprocess.CalledProcessError(
            result.returncode if result is not None else 1,
            ["git", "ls-files"],
            output=result.stdout if result is not None else "",
            stderr=result.stderr if result is not None else "git executable unavailable",
        )

    ignored_dirs = {
        ".git", ".gradle", ".kotlin", ".next", "__pycache__",
        "build", "coverage", "dist", "node_modules", "venv", ".venv",
    }
    return sorted(
        path.relative_to(root).as_posix()
        for path in root.rglob("*")
        if path.is_file() and not any(part in ignored_dirs for part in path.relative_to(root).parts)
    )


def main() -> int:
    try:
        files = tracked_files()
    except (OSError, subprocess.CalledProcessError) as error:
        print(f"PROBLEM cannot list tracked files: {error}")
        return 1

    fileset = set(files)
    problems: list[str] = []
    index_path = ROOT / "docs/README.md"
    try:
        index = index_path.read_text(encoding="utf-8")
    except OSError as error:
        print(f"PROBLEM cannot read docs/README.md: {error}")
        return 1

    for path in files:
        if (
            path.startswith("docs/")
            and path.endswith(".md")
            and path != "docs/README.md"
            and not is_skipped(path)
            and path[len("docs/"):] not in index
        ):
            problems.append(f"not indexed in docs/README.md: {path}")

    path_pattern = re.compile(r"`([A-Za-z0-9_./-]+)`")
    for path in files:
        if not path.endswith(".md") or is_skipped(path) or path == "harness/WORKLOG.md":
            continue
        try:
            document = (ROOT / path).read_text(encoding="utf-8")
        except (OSError, UnicodeError) as error:
            problems.append(f"cannot read Markdown file {path}: {error}")
            continue
        for match in path_pattern.finditer(document):
            reference = match.group(1)
            if (
                not reference.startswith(PREFIXES)
                or "..." in reference
                or reference in PLANNED
            ):
                continue
            if reference not in fileset and not any(
                item.startswith(reference.rstrip("/") + "/") for item in files
            ):
                problems.append(f"{path}: missing path `{reference}`")

    state_path = ROOT / "PROJECT_STATE.md"
    test_root = ROOT / "android/app/src/test"
    strings_path = ROOT / "android/app/src/main/res/values/strings.xml"
    tests = 0
    strings = 0
    try:
        state = state_path.read_text(encoding="utf-8")
        test_sources = [
            path.read_text(encoding="utf-8")
            for path in sorted(test_root.rglob("*.kt"))
        ]
        tests = count_test_annotations(test_sources)
        snapshot_tests = extract_snapshot_count(state, TEST_COUNT_LABEL)
        if snapshot_tests != tests:
            problems.append(
                f"PROJECT_STATE.md says {snapshot_tests} Native tests; source has {tests}"
            )
        strings = count_android_strings(strings_path.read_text(encoding="utf-8"))
        snapshot_strings = extract_snapshot_count(state, STRING_COUNT_LABEL)
        if snapshot_strings != strings:
            problems.append(
                f"PROJECT_STATE.md says {snapshot_strings} generated Android strings; "
                f"resources have {strings}"
            )
    except (OSError, UnicodeError, ET.ParseError, ValueError) as error:
        problems.append(f"snapshot/resource consistency check failed: {error}")

    for problem in problems:
        print("PROBLEM", problem)
    print(
        f"Docs check: {len(problems)} problem(s); "
        f"{tests} Native tests in source, {strings} generated Android strings."
    )
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
