#!/usr/bin/env python3
"""Package source from either a Git checkout or an extracted source ZIP."""
from pathlib import Path
import subprocess,sys,zipfile
root=Path(__file__).resolve().parents[1]
target=Path(sys.argv[1]).resolve() if len(sys.argv)>1 else root.parent/'AVA-Native-Source-0.6.1.zip'
assert not target.is_relative_to(root),'Write output outside the source tree'
if (root/'.git').exists():
 files=[root/p for p in subprocess.check_output(['git','ls-files','-z'],cwd=root).decode().split('\0') if p]
else:
 excluded={'.git','.gradle','.kotlin','.next','build','node_modules','dist','coverage','__pycache__','audio-assets'}
 files=[p for p in root.rglob('*') if p.is_file() and not set(p.relative_to(root).parts)&excluded and p.name!='local.properties' and not (p.name.startswith('.env') and p.name!='.env.example') and p.suffix not in {'.jks','.keystore','.log','.tsbuildinfo'}]
with zipfile.ZipFile(target,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
 for p in sorted(files):z.write(p,p.relative_to(root).as_posix())
with zipfile.ZipFile(target) as z:assert z.testzip() is None
print(target)
