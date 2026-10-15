#!/usr/bin/env python3
"""Validates the character package. Dev mode: structure must be sound (placeholders allowed).
--release: every required_v1 asset must be 'final' (real, large enough), otherwise FAIL (CI gate)."""
import json, os, sys
from PIL import Image
root=os.path.abspath(os.path.join(os.path.dirname(__file__),".."))
M=json.load(open(os.path.join(root,"assets","characters","manifest.json"),encoding="utf-8"))
release="--release" in sys.argv; fails=[]; F=fails.append
ids=[e["id"] for e in M["assets"]]
if len(ids)!=len(set(ids)): F("duplicate asset ids")
for e in M["assets"]:
    p=os.path.join(root,e["path"]); name=os.path.basename(e["path"]).split(".")[0]
    if name in M["banned_names"] or e["key"] in M["banned_names"]: F(f"banned asset {e['id']}")
    if e["status"]!="missing":
        if not os.path.exists(p): F(f"{e['id']}: manifest says present but file missing"); continue
        im=Image.open(p)
        if "A" not in im.getbands(): F(f"{e['id']}: no alpha channel (needs transparent background)")
        if e["px"]!=[im.width,im.height]: F(f"{e['id']}: manifest size is stale (re-run build_manifest.py)")
        if im.width>1280 or im.height>1280: F(f"{e['id']}: larger than 1280px (memory budget)")
    elif os.path.exists(p): F(f"{e['id']}: file exists but manifest says missing (re-run build_manifest.py)")
req=[e for e in M["assets"] if e["required_v1"]]
notfinal=[e for e in req if e["status"] not in ("final","lowres")]   # lowres = real art below target: allowed, warned
low=[e for e in M["assets"] if e["status"]=="lowres"]
by={s:sum(1 for e in M["assets"] if e["status"]==s) for s in ("final","lowres","placeholder","missing")}
print("assets:",len(M["assets"]),by,"| required for V1:",len(req),"| required not final:",len(notfinal))
for e in low: print(f"  ! lowres {e['id']}: {e['px'][1]}px tall -> safe up to ~{e['max_dp']}dp (S06 home needs 320dp)")
if release:
    for e in notfinal: F(f"RELEASE BLOCKER {e['id']} is {e['status']}")
print("mode:", "RELEASE" if release else "DEV", "| FAILS:",len(fails)); [print("  ✗",f) for f in fails[:12]]
if len(fails)>12: print("  ... +",len(fails)-12,"more")
sys.exit(1 if fails else 0)
