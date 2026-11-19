#!/usr/bin/env python3
"""Unit tests for the repository documentation consistency checker."""
import importlib.util
from pathlib import Path
import tempfile
import unittest

SCRIPT = Path(__file__).with_name("check-docs.py")
SPEC = importlib.util.spec_from_file_location("check_docs", SCRIPT)
check_docs = importlib.util.module_from_spec(SPEC)
assert SPEC and SPEC.loader
SPEC.loader.exec_module(check_docs)


class DocsCheckTests(unittest.TestCase):
    def test_snapshot_count_accepts_persian_digits(self):
        state = "- تعداد تست‌های Native در سورس: **۱۵۴**.\n"
        self.assertEqual(
            check_docs.extract_snapshot_count(state, "تعداد تست‌های Native در سورس"),
            154,
        )

    def test_snapshot_count_accepts_ascii_digits(self):
        state = "- رشته‌های Android تولیدی: **295**.\n"
        self.assertEqual(
            check_docs.extract_snapshot_count(state, "رشته‌های Android تولیدی"),
            295,
        )

    def test_snapshot_count_rejects_missing_or_duplicate_values(self):
        with self.assertRaises(ValueError):
            check_docs.extract_snapshot_count("", "تعداد تست‌های Native در سورس")
        state = (
            "- تعداد تست‌های Native در سورس: **۱۵۴**.\n"
            "- تعداد تست‌های Native در سورس: **۱۵۵**.\n"
        )
        with self.assertRaises(ValueError):
            check_docs.extract_snapshot_count(state, "تعداد تست‌های Native در سورس")

    def test_count_test_annotations(self):
        self.assertEqual(
            check_docs.count_test_annotations(["@Test fun first() {}", "fun helper() {}\n@Test"]),
            2,
        )


    def test_file_discovery_in_source_archive(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            (root / "docs").mkdir()
            (root / "docs/README.md").write_text("# Docs", encoding="utf-8")
            (root / "node_modules").mkdir()
            (root / "node_modules/ignored.md").write_text("ignore", encoding="utf-8")
            self.assertEqual(
                check_docs.tracked_files(root),
                ["docs/README.md"],
            )

    def test_android_string_count_and_duplicate_detection(self):
        xml = (
            '<resources><string name="hello">سلام</string>'
            '<string name="goodbye">خداحافظ</string></resources>'
        )
        self.assertEqual(check_docs.count_android_strings(xml), 2)
        duplicate = (
            '<resources><string name="same">الف</string>'
            '<string name="same">ب</string></resources>'
        )
        with self.assertRaises(ValueError):
            check_docs.count_android_strings(duplicate)


if __name__ == "__main__":
    unittest.main()
