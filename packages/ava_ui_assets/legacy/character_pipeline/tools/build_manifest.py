#!/usr/bin/env python3
"""Builds assets/characters/manifest.json from the files on disk + characters.json.
status is DERIVED (never typed by hand): missing | placeholder (too small) | final.
Re-run after dropping real art into assets/characters/...  Usage: python3 tools/build_manifest.py"""
import json, os
from PIL import Image
root=os.path.abspath(os.path.join(os.path.dirname(__file__),".."))
A=os.path.join(root,"assets","characters"); C=json.load(open(os.path.join(root,"metadata","characters.json"),encoding="utf-8"))
MIN_FINAL={"ava_body":800,"ava_head":256,"ava_face":128,"friend":400,"simo":400}   # min pixel HEIGHT to count as final
entries=[]
def add(group,cid,key,rel,required,zone):
    path=os.path.join(A,rel); e={"id":f"{cid}.{key}","character":cid,"key":key,"group":group,"path":f"assets/characters/{rel}","zone":zone,"required_v1":required}
    if os.path.exists(path):
        im=Image.open(path); e["px"]=[im.width,im.height]; e["alpha"]=("A" in im.getbands())
        mf=MIN_FINAL[group]; e["max_dp"]=int(im.height/2.75)     # largest safe display height at 2.75x density
        e["status"]="final" if im.height>=mf else ("lowres" if im.height>=0.6*mf else "placeholder")
    else: e["status"]="missing"
    entries.append(e)
for p in C["ava"]["poses_v1"]+["peek","waving","jumping","reading","running"]:
    add("ava_body","ava",p,f"ava/body/{p}.webp",p in C["ava"]["poses_v1"],"ava_zone")
for h in ["happy","excited","thinking","surprised","wink","laughing","sleepy"]:
    add("ava_head","ava",f"head_{h}",f"ava/head/{h}.webp",h in ("happy","excited"),"ava_zone")
for k in ["eye_open","eye_half","eye_happy","eye_wide","eye_wink","cheek"]+[f"mouth_m{i}" for i in range(10)]:
    add("ava_face","ava",k,f"ava/face/{k}.webp",False,"ava_zone")                 # tier-2 layered face (optional, spike S-09)
for f in C["friends"]:
    for em in ("idle","happy","cheer","curious"):
        add("friend",f["id"],em,f"friends/{f['id']}/{em}.webp",True,"lesson_zone")
for em in ("idle","happy","cheer","curious"):
    add("simo","simo",em,f"friends/simo/{em}.webp",False,"ava_zone")             # P1 finale mascot
notes=json.load(open(os.path.join(root,"metadata","asset_notes.json"),encoding="utf-8")) if os.path.exists(os.path.join(root,"metadata","asset_notes.json")) else {}
for e in entries:
    if e["id"] in notes: e["note"]=notes[e["id"]]
man={"schema":1,"banned_names":["sad","angry","scared","crying"],"min_final_px_height":MIN_FINAL,"assets":entries}
json.dump(man,open(os.path.join(A,"manifest.json"),"w",encoding="utf-8"),ensure_ascii=False,indent=1)
from collections import Counter
print(dict(Counter(e["status"] for e in entries)),"| required_v1 not final:",sum(1 for e in entries if e["required_v1"] and e["status"]!="final"))
