#!/usr/bin/env python3
"""Sync the generated UX prototype registry from the canonical project registry."""
from pathlib import Path
import json

HERE = Path(__file__).resolve()
PROTOTYPE_ROOT = HERE.parents[1]
PROJECT_ROOT = HERE.parents[3]
SOURCE = PROJECT_ROOT / "data" / "screens_v18.json"
DESTINATION = PROTOTYPE_ROOT / "src" / "generated" / "screens.json"

if not SOURCE.is_file():
    raise SystemExit(f"Source registry not found: {SOURCE}")

screens = json.loads(SOURCE.read_text(encoding="utf-8-sig"))
DESTINATION.parent.mkdir(parents=True, exist_ok=True)
DESTINATION.write_text(json.dumps(screens, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
print(f"synced {len(screens)} screens -> {DESTINATION.relative_to(PROJECT_ROOT)}")
