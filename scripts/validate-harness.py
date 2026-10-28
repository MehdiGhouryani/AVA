#!/usr/bin/env python3
from pathlib import Path
import sys

root = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(root))

from harness.validate import assert_valid, task_index

roadmap = assert_valid()
print(f"Harness audit passed: {len(task_index(roadmap))} ordered tasks, safe commands and acyclic dependencies.")
