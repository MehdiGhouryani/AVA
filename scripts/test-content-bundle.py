#!/usr/bin/env python3
"""Exercise deterministic compile, verify and tamper rejection in a temp directory."""
from pathlib import Path
import json
import subprocess
import sys
import tempfile
import zipfile

ROOT=Path(__file__).resolve().parents[1]
COMPILER=ROOT/"scripts/compile-content-bundle.py"
with tempfile.TemporaryDirectory(prefix="ava-content-") as raw:
    folder=Path(raw);first=folder/"first.avacontent";second=folder/"second.avacontent";tampered=folder/"tampered.avacontent"
    subprocess.run([sys.executable,str(COMPILER),"compile","--output",str(first)],check=True,stdout=subprocess.DEVNULL)
    subprocess.run([sys.executable,str(COMPILER),"compile","--output",str(second)],check=True,stdout=subprocess.DEVNULL)
    assert first.read_bytes()==second.read_bytes(),"Bundle compiler is not deterministic"
    with zipfile.ZipFile(first) as source:
        items={name:source.read(name) for name in source.namelist()}
    content=json.loads(items["content.json"]);content["scope"]+=" tampered"
    items["content.json"]=(json.dumps(content,ensure_ascii=False,indent=2)+"\n").encode()
    with zipfile.ZipFile(tampered,"w") as target:
        for name,data in sorted(items.items()):target.writestr(name,data)
    result=subprocess.run([sys.executable,str(COMPILER),"verify","--input",str(tampered)],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
    assert result.returncode!=0,"Tampered bundle must be rejected"
print("Content bundle tests passed: deterministic compile, valid verify, tamper rejected.")
