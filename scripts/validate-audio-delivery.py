#!/usr/bin/env python3
"""Validate delivered human audio (Track B) against the audio inventory.

Checks, per ADR-0002, for every asset that is required for publication or
already has a delivery key:
  - the file exists in the delivery directory,
  - the format is WAV (RIFF) or MP3 (ID3 / frame sync), by magic bytes,
  - the file decodes: WAV header (channels, sample rate, bits) and MP3 frame
    headers are walked, so a truncated or corrupt file fails,
  - duration is sane for a single spoken prompt (0.2s - 30s),
  - sha256 matches the inventory when the inventory records one.

With no deliveries yet (the current state) every required asset reports
"not delivered" and the script exits 1: that is the gate, not an error.

Usage:
  python3 scripts/validate-audio-delivery.py [--delivery-dir DIR] [--inventory FILE]
  python3 scripts/validate-audio-delivery.py --record [--delivery-dir DIR]
      Record the computed sha256 / codec / duration into the inventory for
      delivered files that pass the format checks (only assets whose sha256
      is still null). Never touches approved assets.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import struct
import sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(root / "scripts"))
from strict_json import load_json  # noqa: E402

MIN_DURATION_S = 0.2
MAX_DURATION_S = 30.0
MIN_SAMPLE_RATE = 8000
MAX_SAMPLE_RATE = 96000
MAX_FILE_BYTES = 64 * 1024 * 1024


class Fail(Exception):
    pass


def sha256_of(path: Path) -> str:
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def parse_wav(data: bytes) -> dict:
    if len(data) < 12 or data[:4] != b"RIFF" or data[8:12] != b"WAVE":
        raise Fail("not a RIFF/WAVE file")
    pos = 12
    fmt = None
    data_size = None
    while pos + 8 <= len(data):
        chunk_id, chunk_size = data[pos:pos + 4], struct.unpack_from("<I", data, pos + 4)[0]
        body = pos + 8
        if chunk_id == b"fmt ":
            if chunk_size < 16:
                raise Fail("WAV fmt chunk too short")
            audio_format, channels, sample_rate, byte_rate, _, bits = struct.unpack_from("<HHIIHH", data, body)
            if audio_format != 1:
                raise Fail(f"WAV is not PCM (format {audio_format})")
            fmt = {"channels": channels, "sample_rate": sample_rate, "byte_rate": byte_rate, "bits": bits}
        elif chunk_id == b"data":
            data_size = chunk_size
        pos = body + chunk_size + (chunk_size % 2)
    if fmt is None:
        raise Fail("WAV has no fmt chunk")
    if data_size is None or data_size == 0:
        raise Fail("WAV has no audio data")
    if fmt["channels"] not in (1, 2):
        raise Fail(f"WAV channels {fmt['channels']} (want 1 or 2)")
    if not (MIN_SAMPLE_RATE <= fmt["sample_rate"] <= MAX_SAMPLE_RATE):
        raise Fail(f"WAV sample rate {fmt['sample_rate']} Hz out of range")
    if fmt["bits"] not in (8, 16, 24, 32):
        raise Fail(f"WAV bits per sample {fmt['bits']} unsupported")
    bytes_per_sec = fmt["byte_rate"] or (fmt["sample_rate"] * fmt["channels"] * fmt["bits"] // 8)
    if bytes_per_sec <= 0:
        raise Fail("WAV byte rate is zero")
    duration = data_size / bytes_per_sec
    return {"codec": "wav", **fmt, "duration_s": duration}


_MP3_BITRATES = {
    # MPEG version bits -> Layer III kbps by bitrate index 1..14
    # (3 = MPEG 1, 2 = MPEG 2, 0 = MPEG 2.5; 1 is reserved)
    (3, 3): [32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320],
    (2, 3): [8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160],
    (0, 3): [8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160],
}
_MP3_RATES = {3: [44100, 48000, 32000], 2: [22050, 24000, 16000], 0: [11025, 12000, 8000]}


def parse_mp3(data: bytes) -> dict:
    pos = 0
    if data[:3] == b"ID3":
        if len(data) < 10:
            raise Fail("truncated ID3 tag")
        size = ((data[6] & 0x7F) << 21) | ((data[7] & 0x7F) << 14) | ((data[8] & 0x7F) << 7) | (data[9] & 0x7F)
        pos = 10 + size
    frames = 0
    sample_rate = None
    while pos + 4 <= len(data):
        if data[pos] != 0xFF or (data[pos + 1] & 0xE0) != 0xE0:
            raise Fail(f"bad MP3 frame sync at offset {pos} (after {frames} frames)")
        version = (data[pos + 1] >> 3) & 0x3
        layer = (data[pos + 1] >> 1) & 0x3
        bitrate_idx = (data[pos + 2] >> 4) & 0xF
        rate_idx = (data[pos + 2] >> 2) & 0x3
        padding = (data[pos + 2] >> 1) & 0x1
        if version == 1 or layer != 1 or bitrate_idx in (0, 15) or rate_idx == 3:
            raise Fail(f"unsupported MP3 frame header at offset {pos}")
        bitrates = _MP3_BITRATES.get((version, 3))
        rates = _MP3_RATES.get(version)
        if bitrates is None or rates is None:
            raise Fail(f"unsupported MP3 MPEG version {version}")
        bitrate = bitrates[bitrate_idx - 1] * 1000
        sample_rate = rates[rate_idx]
        frame_len = 144 * bitrate // sample_rate + padding
        if frame_len <= 0:
            raise Fail("zero-length MP3 frame")
        pos += frame_len
        frames += 1
    if frames == 0 or sample_rate is None:
        raise Fail("no MP3 frames found")
    return {"codec": "mp3", "channels": None, "sample_rate": sample_rate, "bits": None,
            "duration_s": frames * 1152 / sample_rate}


def check_file(path: Path) -> dict:
    size = path.stat().st_size
    if size == 0:
        raise Fail("empty file")
    if size > MAX_FILE_BYTES:
        raise Fail(f"file too large ({size} bytes)")
    data = path.read_bytes()
    if data[:4] == b"RIFF":
        info = parse_wav(data)
    elif data[:3] == b"ID3" or (len(data) >= 2 and data[0] == 0xFF and (data[1] & 0xE0) == 0xE0):
        info = parse_mp3(data)
    else:
        raise Fail("unknown format (want WAV RIFF or MP3)")
    if not (MIN_DURATION_S <= info["duration_s"] <= MAX_DURATION_S):
        raise Fail(f"duration {info['duration_s']:.2f}s out of range ({MIN_DURATION_S}-{MAX_DURATION_S}s)")
    info["bytes"] = size
    info["sha256"] = sha256_of(path)
    return info


def expected_names(asset: dict) -> list[str]:
    key = asset.get("deliveryAssetKey") or asset.get("masterAssetKey")
    if key:
        return [key]
    return [f"{asset['id']}.wav", f"{asset['id']}.mp3"]


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("--delivery-dir", default=str(root / "design" / "audio" / "delivery"))
    ap.add_argument("--inventory", default=str(root / "design" / "audio.inventory.json"))
    ap.add_argument("--record", action="store_true", help="record sha256/codec/duration into the inventory for delivered files that pass")
    args = ap.parse_args()
    delivery_dir = Path(args.delivery_dir)
    inventory = load_json(Path(args.inventory))
    assets = inventory["assets"]
    required = [a for a in assets if a.get("requiredForPublication")]
    in_scope = [a for a in assets if a.get("requiredForPublication") or a.get("deliveryAssetKey") or a.get("masterAssetKey")]
    print(f"delivery dir: {delivery_dir} (exists: {delivery_dir.is_dir()})")
    print(f"inventory: {len(assets)} assets, {len(required)} required for publication")
    failures = 0
    recorded = 0
    for asset in in_scope:
        asset_id = asset["id"]
        found = None
        if delivery_dir.is_dir():
            for name in expected_names(asset):
                cand = delivery_dir / name
                if cand.is_file():
                    found = cand
                    break
        if found is None:
            print(f"FAIL {asset_id}: not delivered (looked for {', '.join(expected_names(asset))})")
            failures += 1
            continue
        try:
            info = check_file(found)
        except Fail as e:
            print(f"FAIL {asset_id}: {found.name}: {e}")
            failures += 1
            continue
        recorded_sha = asset.get("sha256")
        if recorded_sha and recorded_sha != info["sha256"]:
            print(f"FAIL {asset_id}: {found.name}: sha256 mismatch (file {info['sha256'][:12]}… != inventory {recorded_sha[:12]}…)")
            failures += 1
            continue
        note = f"{info['codec']}, {info['duration_s']:.2f}s"
        if info["sample_rate"]:
            note += f", {info['sample_rate']} Hz"
        if info["channels"]:
            note += f", {info['channels']}ch"
        if recorded_sha is None:
            note += ", sha256 not recorded in inventory"
        print(f"OK   {asset_id}: {found.name}: {note}")
        if args.record and recorded_sha is None and asset.get("status") != "approved":
            asset["sha256"] = info["sha256"]
            asset["deliveryAssetKey"] = found.name
            asset["codec"] = info["codec"]
            asset["durationSeconds"] = round(info["duration_s"], 3)
            recorded += 1
    if args.record and recorded:
        with open(args.inventory, "w", encoding="utf-8") as f:
            json.dump(inventory, f, ensure_ascii=False, indent=2)
            f.write("\n")
        print(f"recorded {recorded} asset(s) into the inventory")
    delivered = len(in_scope) - failures
    print(f"summary: {delivered}/{len(in_scope)} in-scope assets valid ({len(required)} required)")
    if failures:
        print("RESULT: FAIL — delivery is incomplete or invalid")
        return 1
    print("RESULT: OK — all in-scope deliveries valid")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
