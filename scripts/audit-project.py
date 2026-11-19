#!/usr/bin/env python3
from pathlib import Path
import hashlib,json,subprocess,sys
import xml.etree.ElementTree as ET
from PIL import Image
root=Path(__file__).resolve().parents[1]
assets=json.loads((root/'design/assets.manifest.json').read_text())['assets']
schema=json.loads((root/'design/content.schema.json').read_text())
assert schema['$id'].endswith('content-v3.json') and schema['properties']['schemaVersion']['const']==3
ANDROID_NS='{http://schemas.android.com/apk/res/android}'
for a in assets:
 p=root/'public'/a['src'].lstrip('/')
 if p.suffix.lower()=='.xml':
  vector=ET.parse(p).getroot()
  assert vector.tag=='vector',(a['id'],'expected Android VectorDrawable')
  assert vector.attrib.get(ANDROID_NS+'width')==f"{a['width']}dp",(a['id'],'width')
  assert vector.attrib.get(ANDROID_NS+'height')==f"{a['height']}dp",(a['id'],'height')
  size=(int(float(vector.attrib[ANDROID_NS+'viewportWidth'])),int(float(vector.attrib[ANDROID_NS+'viewportHeight'])))
 else:
  with Image.open(p) as im:
   size=im.size
   im.verify()
 assert size==(a['width'],a['height']),(a['id'],size)
for s in json.loads((root/'.agents/skills.lock.json').read_text())['skills']:
 p=root/f'.agents/skills/{s["name"]}/SKILL.md';b=p.read_bytes()
 assert b.startswith(b'---\nname:')
 assert hashlib.sha256(b).hexdigest()==s['skillSha256'],s['name']
inventory=json.loads((root/'design/screen-references.audit.json').read_text())['files']
reviewed={item['path'] for item in inventory}
actual=set()
for folder in [root/'reference',root/'public/assets/aistudio',root/'.aistudio',root/'docs/history/web-prototype/references']:
 for p in folder.rglob('*'):
  if p.is_file() and p.suffix.lower() in {'.png','.jpg','.jpeg','.webp'}:actual.add(p.relative_to(root).as_posix())
assert actual==reviewed,('Unreviewed or removed design references',actual^reviewed)
for item in inventory:
 p=root/item['path'];assert hashlib.sha256(p.read_bytes()).hexdigest()==item['sha256'],item['path']
assert (root/'public/fonts/Vazirmatn.ttf').stat().st_size>10000
subprocess.run([sys.executable,str(root/'scripts/sync-android-resources.py'),'--check'],check=True)
subprocess.run([sys.executable,str(root/'scripts/validate-content-contract.py')],check=True)
subprocess.run([sys.executable,str(root/'scripts/validate-audio-inventory.py')],check=True)
subprocess.run([sys.executable,str(root/'scripts/validate-cms-contract.py')],check=True)
subprocess.run([sys.executable,str(root/'scripts/validate-payload-prototype.py')],check=True)
subprocess.run([sys.executable,str(root/'scripts/harness.py'),'validate'],check=True)
subprocess.run([sys.executable,str(root/'scripts/test-content-bundle.py')],check=True)
subprocess.run([sys.executable,str(root/'scripts/test-activity-kinds.py')],check=True)
subprocess.run([sys.executable,str(root/'scripts/test-docs-check.py')],check=True)
subprocess.run([sys.executable,str(root/'scripts/check-docs.py')],check=True)
print('Project audit passed: content v3/release/audio/CMS contracts, Payload prototype, coding-agent harness, deterministic bundle/tamper checks, migration, docs consistency, asset dimensions, 3 skill hashes, '+str(len(reviewed))+' design references, font and generated native resources.')
