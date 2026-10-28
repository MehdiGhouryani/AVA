#!/usr/bin/env python3
"""Compile and verify deterministic, offline AVA content bundles."""
from pathlib import Path, PurePosixPath
import argparse
import hashlib
import json
import subprocess
import sys
import zipfile

ROOT=Path(__file__).resolve().parents[1]
FIXED_TIME=(2026,1,1,0,0,0)
PAYLOADS={"manifest.json":ROOT/"design/content.release.json","content.json":ROOT/"design/content.fa.json","audio-inventory.json":ROOT/"design/audio.inventory.json"}

def digest(data: bytes)->str:return hashlib.sha256(data).hexdigest()
def zip_info(name: str)->zipfile.ZipInfo:
    info=zipfile.ZipInfo(name,FIXED_TIME);info.compress_type=zipfile.ZIP_STORED;info.external_attr=0o100644<<16
    return info
def write_bundle(path: Path)->None:
    subprocess.run([sys.executable,str(ROOT/"scripts/validate-content-contract.py")],check=True)
    subprocess.run([sys.executable,str(ROOT/"scripts/validate-audio-inventory.py")],check=True)
    payloads={name:source.read_bytes() for name,source in PAYLOADS.items()}
    release=json.loads(payloads["manifest.json"])
    index={"bundleFormatVersion":1,"releaseId":release["releaseId"],"entries":[{"path":name,"bytes":len(payloads[name]),"sha256":digest(payloads[name])} for name in sorted(payloads)]}
    payloads["bundle-index.json"]=(json.dumps(index,ensure_ascii=False,sort_keys=True,separators=(",",":"))+"\n").encode()
    path.parent.mkdir(parents=True,exist_ok=True)
    with zipfile.ZipFile(path,"w") as archive:
        for name in sorted(payloads):archive.writestr(zip_info(name),payloads[name])
    verify_bundle(path)

def verify_bundle(path: Path)->None:
    with zipfile.ZipFile(path) as archive:
        names=archive.namelist();expected={"bundle-index.json",*PAYLOADS}
        if len(names)!=len(set(names)) or set(names)!=expected:raise ValueError("bundle_entries_invalid")
        for name in names:
            pure=PurePosixPath(name)
            if pure.is_absolute() or ".." in pure.parts or archive.getinfo(name).is_dir():raise ValueError("bundle_path_unsafe")
        payloads={name:archive.read(name) for name in names}
    index=json.loads(payloads["bundle-index.json"])
    if index.get("bundleFormatVersion")!=1:raise ValueError("bundle_format_incompatible")
    indexed={item["path"]:item for item in index.get("entries",[])}
    for name in PAYLOADS:
        item=indexed.get(name)
        if not item or item["bytes"]!=len(payloads[name]) or item["sha256"]!=digest(payloads[name]):raise ValueError(f"bundle_hash_invalid:{name}")
    release=json.loads(payloads["manifest.json"]);content=json.loads(payloads["content.json"]);audio=json.loads(payloads["audio-inventory.json"])
    if index["releaseId"]!=release["releaseId"]:raise ValueError("release_id_mismatch")
    if release["schemaVersion"]!=content["schemaVersion"] or release["contentVersion"]!=content["contentVersion"]:raise ValueError("content_version_mismatch")
    if release["bundleSha256"]!=digest(payloads["content.json"]):raise ValueError("content_payload_hash_invalid")
    if release["audioInventorySha256"]!=digest(payloads["audio-inventory.json"]):raise ValueError("audio_payload_hash_invalid")
    if audio["contentSchemaVersion"]!=content["schemaVersion"] or audio["contentVersion"]!=content["contentVersion"]:raise ValueError("audio_version_mismatch")

def main()->None:
    parser=argparse.ArgumentParser();sub=parser.add_subparsers(dest="command",required=True)
    compile_cmd=sub.add_parser("compile");compile_cmd.add_argument("--output",type=Path,required=True)
    verify_cmd=sub.add_parser("verify");verify_cmd.add_argument("--input",type=Path,required=True)
    args=parser.parse_args()
    if args.command=="compile":
        write_bundle(args.output);path=args.output
    else:
        verify_bundle(args.input);path=args.input
    print(f"Content bundle {args.command} passed: {path.name}, bytes={path.stat().st_size}, sha256={digest(path.read_bytes())}")

if __name__=="__main__":main()
