#!/usr/bin/env python3
"""Build and verify the current AVA debug APK in a complete Android toolchain."""
from __future__ import annotations

from pathlib import Path
import argparse
import hashlib
import json
import os
import shutil
import subprocess
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "android"
GRADLEW = ANDROID / ("gradlew.bat" if os.name == "nt" else "gradlew")


def run(command: list[str], cwd: Path = ROOT) -> None:
    print(f"RUN {cwd.relative_to(ROOT) if cwd != ROOT else '.'}: {' '.join(command)}", flush=True)
    subprocess.run(command, cwd=cwd, check=True)


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def sdk_available() -> bool:
    candidates = [os.environ.get("ANDROID_HOME"), os.environ.get("ANDROID_SDK_ROOT")]
    local_properties = ANDROID / "local.properties"
    if local_properties.is_file():
        for line in local_properties.read_text().splitlines():
            if line.startswith("sdk.dir="):
                candidates.append(line.split("=", 1)[1].replace("\\:", ":").replace("\\\\", "\\"))
    return any(candidate and Path(candidate).is_dir() for candidate in candidates)


def main() -> None:
    parser = argparse.ArgumentParser(description="Build the authorized AVA debug APK")
    parser.add_argument("--output-dir", type=Path, default=ROOT.parent / "ava-final-artifacts")
    parser.add_argument("--check-only", action="store_true")
    args = parser.parse_args()

    if not GRADLEW.is_file():
        raise SystemExit("Gradle wrapper is missing")
    java = shutil.which("java")
    if not java:
        raise SystemExit("JDK 17 is required but java was not found")
    if not sdk_available():
        raise SystemExit("Android SDK is required: set ANDROID_HOME/ANDROID_SDK_ROOT or android/local.properties")

    run([str(GRADLEW), "--version"], ANDROID)
    if args.check_only:
        print("Android build prerequisites are available.")
        return

    run([sys.executable, "scripts/sync-android-resources.py", "--check"])
    run([sys.executable, "scripts/audit-project.py"])
    run([str(GRADLEW), "testDebugUnitTest", "lintDebug", "assembleDebug", "--no-daemon"], ANDROID)

    source_apk = ANDROID / "app/build/outputs/apk/debug/app-debug.apk"
    if not source_apk.is_file() or source_apk.stat().st_size == 0:
        raise SystemExit(f"Expected APK was not produced: {source_apk}")
    with zipfile.ZipFile(source_apk) as archive:
        bad = archive.testzip()
        if bad:
            raise SystemExit(f"APK ZIP integrity failed at {bad}")
        names = set(archive.namelist())
        if "AndroidManifest.xml" not in names or "classes.dex" not in names:
            raise SystemExit("APK is missing AndroidManifest.xml or classes.dex")

    args.output_dir.mkdir(parents=True, exist_ok=True)
    target = args.output_dir.resolve() / "AVA-0.6.1-debug.apk"
    shutil.copy2(source_apk, target)
    report = {
        "artifact": target.name,
        "buildType": "debug",
        "sha256": sha256(target),
        "sizeBytes": target.stat().st_size,
        "sourceVersion": "0.6.1",
        "tests": "testDebugUnitTest",
        "lint": "lintDebug",
        "deviceQa": "not performed by this script"
    }
    report_path = args.output_dir.resolve() / "AVA-0.6.1-debug.build.json"
    report_path.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n")
    print(target)
    print(report_path)


if __name__ == "__main__":
    main()
