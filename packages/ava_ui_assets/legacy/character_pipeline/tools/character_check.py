#!/usr/bin/env python3
"""Validates characters.json (schema 2). Exit 1 on any FAIL.
Checks: ids/names/silhouettes/accessories unique; banned emotions; colour distinctness (normal + 3 colour-vision types);
contrast vs cream; silhouette IoU; ZONE RULES (Ava never in lesson zone; exactly one host per lesson screen = its owner);
activity-owner coverage; Ava has a single colour."""
import json, os, sys, itertools
import numpy as np
here=os.path.dirname(os.path.abspath(__file__))
_p=os.path.join(here,"..","metadata","characters.json"); _p=_p if os.path.exists(_p) else os.path.join(here,"..","characters.json")
C=json.load(open(_p,encoding="utf-8"))
fails=[]; notes=[]
F=lambda m: fails.append(m)
hexrgb=lambda h: tuple(int(h[i:i+2],16) for i in (1,3,5))
def lin(c): c=np.array(c)/255.0; return np.where(c<=0.04045,c/12.92,((c+0.055)/1.055)**2.4)
def delin(c): c=np.clip(c,0,1); return np.where(c<=0.0031308,c*12.92,1.055*c**(1/2.4)-0.055)*255
def lab(rgb):
    l=lin(rgb); M=np.array([[.4124,.3576,.1805],[.2126,.7152,.0722],[.0193,.1192,.9505]]); x,y,z=M@l
    f=lambda t: np.where(t>0.008856,np.cbrt(t),7.787*t+16/116); fx,fy,fz=f(x/.95047),f(y),f(z/1.08883)
    return np.array([116*fy-16,500*(fx-fy),200*(fy-fz)])
dE=lambda a,b: float(np.linalg.norm(lab(a)-lab(b)))
CVD={"normal":np.eye(3),
 "protan":np.array([[0.152286,1.052583,-0.204868],[0.114503,0.786281,0.099216],[-0.003882,-0.048116,1.051998]]),
 "deutan":np.array([[0.367322,0.860646,-0.227968],[0.280085,0.672501,0.047413],[-0.011820,0.042940,0.968881]]),
 "tritan":np.array([[1.255528,-0.076749,-0.178779],[-0.078411,0.930809,0.147602],[0.004733,0.691367,0.303900]])}
sim=lambda rgb,k: delin(CVD[k]@lin(rgb))
# ---- cast table (Ava + 6 friends + finale mascot)
cast=[{"id":"ava","name":C["ava"]["name_fa"],"base":C["ava"]["palette"]["base"],"sil":C["ava"]["silhouette_class"],
       "acc":"backpack+3-leaf crest","size":C["ava"]["size_ratio"],"contour":None}]
for f in C["friends"]+([C["finale"]] if "finale" in C else []):
    cast.append({"id":f["id"],"name":f["name_fa"],"base":f["palette"]["base"],"sil":f["silhouette_class"],
                 "acc":f["signature"]["accessory"],"size":f["size_ratio"],"contour":f.get("contour")})
ids=[c["id"] for c in cast]
if len(set(ids))!=len(ids): F("duplicate ids")
if len({c["name"][0] for c in cast})!=len(cast): F("two cast names start with the same letter")
if len({c["sil"] for c in cast})!=len(cast): F("two characters share a silhouette_class")
if len({c["acc"] for c in cast})!=len(cast): F("two characters share a signature accessory")
for f in C["friends"]+([C["finale"]] if "finale" in C else []):
    if len(f["construction"])>3: F(f"{f['id']}: >3 construction primitives")
# ---- Ava: single colour, emotions
if "variants" in C["ava"] or not C["ava"].get("single_colour"): F("Ava must have ONE colour (no variants)")
em=set(C["ava"]["emotion_map"])
if not em<=set(C["allowed_emotions"]): F("Ava emotion outside allowed list")
if em&set(C["banned_emotions"]): F("banned emotion mapped")
if "Sad" not in C["ava"]["sheet_expressions_unused"]: F("sheet 'Sad' must stay unused")
# ---- colours
print("COLOUR DISTINCTNESS (ΔE76; >=30 normal, >=20 colour-blind unless second identifier)")
worst=[]
for a,b in itertools.combinations(cast,2):
    ra,rb=hexrgb(a["base"]),hexrgb(b["base"]); row={k:dE(sim(ra,k),sim(rb,k)) for k in CVD}; low=min(row.values()); worst.append((low,a["id"],b["id"],row))
    if row["normal"]<30: F(f"{a['id']}~{b['id']} too close in normal vision ΔE={row['normal']:.0f}")
    if low<20:
        second=a["sil"]!=b["sil"] and a["acc"]!=b["acc"] and abs(a["size"]-b["size"])>=0.04
        (notes if second else fails).append(f"{a['id']}~{b['id']} ΔE={low:.0f} colour-blind → "+("second identifiers OK" if second else "NO second identifier"))
for low,a,b,row in sorted(worst)[:4]: print(f"  {a:8}~{b:8} min ΔE {low:5.1f}")
bg=hexrgb(C["bg"])
for c in cast:
    d=dE(hexrgb(c["base"]),bg)
    if d<25 and not c["contour"]: F(f"{c['id']} nearly vanishes on cream (ΔE={d:.0f}) and has no contour")
# ---- silhouettes
thr=C["measured"]["iou_confusable_threshold"]
for pair,v in C["measured"]["silhouette_iou"].items():
    if v>=thr and pair not in C["measured"]["mitigation"]: F(f"silhouette {pair} IoU {v}")
    elif v>=thr: notes.append(f"silhouette {pair} IoU {v}: mitigated")
# ---- ownership + ZONE RULES
owners={}
for f in C["friends"]:
    for a in f["owns"]: owners.setdefault(a,[]).append(f["id"])
for a in C["required_owners"]:
    if len(owners.get(a,[]))!=1: F(f"activity {a} owners={owners.get(a)} (need exactly one)")
fid={f["id"] for f in C["friends"]}
lesson=set(C["zones"]["lesson_zone"]); avaz=set(C["zones"]["ava_zone"]); nochar=set(C["zones"]["no_character"])
if lesson&avaz or lesson&nochar or avaz&nochar: F("a screen is in two zones")
for s,v in C["screen_cast"].items():
    if s in lesson:
        if v["ava"]: F(f"{s}: Ava appears in a LESSON screen (forbidden, D-77)")
        h=v["host"]
        if h!="owner_of_finished_node":
            if h not in fid: F(f"{s}: host {h} is not a lesson friend")
            elif owners.get(s)!=[h]: F(f"{s}: host {h} is not the owner {owners.get(s)}")
        if v.get("extras"): F(f"{s}: lesson screens have exactly one character")
    elif s in avaz:
        if not v["ava"]: F(f"{s}: Ava zone screen without Ava")
    else: F(f"{s}: not in any zone")
for s in lesson|avaz:
    if s not in C["screen_cast"]: F(f"{s}: missing in screen_cast")
for f in C["friends"]:
    lo,hi=f["host_dp"]
    if not (100<=lo<=hi<=240): F(f"{f['id']}: host_dp {lo}-{hi} outside 100..240")
print("\nZONES: lesson screens =",len(lesson),"| Ava-zone screens =",len(avaz),"| no-character =",len(nochar))
print("NOTES:"); [print("  -",n) for n in notes]
print("FAILS:",len(fails)); [print("  ✗",f) for f in fails]
sys.exit(1 if fails else 0)
