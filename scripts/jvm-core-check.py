#!/usr/bin/env python3
"""Partial JVM-only check of the pure Kotlin layers (content, data).

This is NOT Gradle, Android or Robolectric. It compiles content/ and data/ with kotlinc and
runs ModelTest, ActivityEngineTest, ContentRepositoryTest and ProgressionTest against small stand-ins for
JUnit and kotlinx.coroutines (scripts/jvm-core-check/*.kt). It does not replace
testDebugUnitTest or lintDebug and must never be recorded as native verification.

Usage: KOTLINC=/path/to/kotlinc python3 scripts/jvm-core-check.py   (kotlinc 2.1.20, java on PATH)
"""
import os, re, shutil, subprocess, sys, tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "android/app/src"
SHIM = Path(__file__).with_name("jvm-core-check")
TESTS = ["ModelTest", "ActivityEngineTest", "ContentRepositoryTest", "ProgressionTest"]


def r_stub() -> str:
    ids = {"string": set(), "drawable": set()}
    for f in (SRC / "main/java/com/ava/reading").rglob("*.kt"):
        for m in re.finditer(r"\bR\.(string|drawable)\.([A-Za-z0-9_]+)", f.read_text(encoding="utf-8")):
            ids[m.group(1)].add(m.group(2))
    out, n = "package com.ava.reading\nobject R {\n", 1
    for kind in ("string", "drawable"):
        out += f"  object {kind} {{\n"
        for name in sorted(ids[kind]):
            out += f"    const val {name} = {n}\n"
            n += 1
        out += "  }\n"
    return out + "}\n"


def main() -> int:
    kotlinc = os.environ.get("KOTLINC") or shutil.which("kotlinc")
    if not kotlinc:
        print("Set KOTLINC to a kotlinc 2.1.20 binary.", file=sys.stderr)
        return 2
    stdlib = Path(kotlinc).resolve().parents[1] / "lib" / "kotlin-stdlib.jar"
    main_dir = SRC / "main/java/com/ava/reading"
    test_dir = SRC / "test/java/com/ava/reading"
    with tempfile.TemporaryDirectory() as tmp:
        out = Path(tmp) / "cls"
        out.mkdir()
        (Path(tmp) / "R.kt").write_text(r_stub(), encoding="utf-8")
        files = [str(Path(tmp) / "R.kt")]
        files += [str(p) for p in sorted((main_dir / "content").glob("*.kt"))]
        files += [str(p) for p in sorted((main_dir / "data").glob("*.kt"))]
        files += [str(p) for p in sorted(SHIM.glob("*.kt"))]
        files += [str(test_dir / f"{t}.kt") for t in TESTS]
        compile_run = subprocess.run([kotlinc, "-nowarn", "-jvm-target", "17", "-d", str(out), *files],
                                     capture_output=True, text=True)
        print(f"compile exit={compile_run.returncode}")
        if compile_run.returncode != 0:
            print((compile_run.stdout + compile_run.stderr)[-3000:])
            return 1
        classes = [f"com.ava.reading.{t}" for t in TESTS]
        run = subprocess.run(["java", "-cp", f"{out}{os.pathsep}{stdlib}", "RunnerKt", *classes],
                             capture_output=True, text=True)
        print((run.stdout + run.stderr).strip())
        return run.returncode


if __name__ == "__main__":
    sys.exit(main())
