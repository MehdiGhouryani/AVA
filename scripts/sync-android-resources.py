#!/usr/bin/env python3
"""Deterministic, offline resource generation. --check never writes."""
from pathlib import Path
import json,re,sys,hashlib,html
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/'android/app/src/main/res'
KOT=ROOT/'android/app/src/main/java/com/ava/reading'
check='--check' in sys.argv
outputs={}
def out(path,text):outputs[path]=(text+'\n').encode()
def kt(value):return json.dumps(value,ensure_ascii=False).replace('$','\\$')
def snake(s):return re.sub(r'(?<!^)(?=[A-Z])','_',s).lower().replace('.','_')
strings={}
def walk(d,p=''):
 if isinstance(d,dict) and 'fa' in d:strings[snake(p)]=d['fa'];return
 if isinstance(d,dict):
  for k,v in d.items():
   if not k.startswith('_'):walk(v,f'{p}_{k}' if p else k)
walk(json.loads((ROOT/'design/strings.fa.json').read_text()))
def xml(s):return html.escape(s,quote=False).replace("'","\\'").replace('"','\\"')
out(RES/'values/strings.xml','<resources>\n'+'\n'.join(f'    <string name="{k}" formatted="false">{xml(v)}</string>' for k,v in strings.items())+'\n</resources>')
out(KOT/'content/TextIds.kt','package com.ava.reading.content\nimport com.ava.reading.R\nobject TextIds { val ids = mapOf(\n'+',\n'.join(f'    {kt(k)} to R.string.{k}' for k in strings)+'\n) }')
tokens=json.loads((ROOT/'design/design_tokens.json').read_text())
out(KOT/'ui/theme/DesignTokens.kt','package com.ava.reading.ui.theme\nobject DesignTokens {\n'+ '\n'.join(f' const val {snake(k)} = 0xFF{v[1:]}L' for k,v in tokens['color'].items())+'\n}')
out(RES/'values/styles.xml','<resources><style name="Theme.Ava" parent="android:style/Theme.Material.Light.NoActionBar"><item name="android:fontFamily">sans</item><item name="android:windowLightStatusBar">true</item><item name="android:statusBarColor">'+tokens['color']['paper']+'</item><item name="android:navigationBarColor">'+tokens['color']['cream']+'</item><item name="android:windowBackground">'+tokens['color']['paper']+'</item><item name="android:windowActionModeOverlay">true</item></style></resources>')
assets=json.loads((ROOT/'design/assets.manifest.json').read_text())['assets']
art=[]
for a in assets:
 source=ROOT/'public'/a['src'].lstrip('/')
 dest=RES/'drawable-nodpi'/(snake(a['id'])+source.suffix)
 outputs[dest]=source.read_bytes();art.append(f'    {kt(a["id"])} to R.drawable.{snake(a["id"])}')
out(KOT/'content/Art.kt','package com.ava.reading.content\nimport com.ava.reading.R\nobject Art { val ids = mapOf(\n'+',\n'.join(art)+'\n) }')
outputs[RES/'font/vazirmatn.ttf']=(ROOT/'public/fonts/Vazirmatn.ttf').read_bytes()
data=json.loads((ROOT/'design/content.fa.json').read_text())
release=json.loads((ROOT/'design/content.release.json').read_text())
audio_ids={item['id'] for item in json.loads((ROOT/'design/audio.inventory.json').read_text())['assets']}
asset_ids={a['id'] for a in assets}
assert data['schemaVersion']==3 and data['approvedForRelease'] is False and data['locale']=='fa-IR'
assert release['schemaVersion']==3 and release['contentVersion']==data['contentVersion'] and release['locale']==data['locale']
skills=data['skills'];skill_ids={s['id'] for s in skills}
assert len(skill_ids)==len(skills) and all(re.fullmatch(r'[a-z0-9_]+',x) for x in skill_ids)
skills_code=[f'Skill({kt(s["id"])}, {kt(s["title"])}, {kt(s["description"])})' for s in skills]
task_ids=set();lesson_ids=set();unit_ids=set();level_ids=set();kinds={'recognize':'Recognize','listen':'Listen','build_word':'BuildWord','dot_family':'DotFamily','positional_form':'PositionalForm','initial_sound':'InitialSound','final_sound':'FinalSound','segment':'Segment','blend':'Blend'}
levels=[];lesson_count=0;task_count=0
for level in data['levels']:
 assert level['id'] not in level_ids;level_ids.add(level['id'])
 assert [u['order'] for u in level['units']]==list(range(1,len(level['units'])+1))
 units=[]
 for unit in level['units']:
  assert unit['id'] not in unit_ids;unit_ids.add(unit['id'])
  assert [l['order'] for l in unit['lessons']]==list(range(1,len(unit['lessons'])+1))
  lessons=[]
  for l in unit['lessons']:
   lesson_count+=1;assert l['id'] not in lesson_ids;lesson_ids.add(l['id']);tasks=[]
   for t in l['tasks']:
    task_count+=1;assert t['id'] not in task_ids and re.fullmatch(r'[a-z0-9_]+',t['id']);task_ids.add(t['id'])
    assert t['kind'] in kinds and t['target'].strip() and set(t['skillIds'])<=skill_ids and set(t['assetRefs'])<=asset_ids
    choices=t.get('choices',[]);tokens=t.get('tokens',[])
    assert all(isinstance(v,str) and v.strip() for v in choices+tokens)
    if t['kind'] in {'build_word','segment','blend'}:
     assert not choices and 2<=len(tokens)<=8 and sorted(''.join(tokens))==sorted(t['target'])
     assert t['audioRef'] is None and t['writtenFallback'] is None and not t['audioRequiredForPublication']
    else:
     assert not tokens and len(choices)==len(set(choices))==3 and t['target'] in choices
     assert t['audioRef'] in audio_ids and isinstance(t['writtenFallback'],str) and t['writtenFallback'].strip()
     assert t['audioRequiredForPublication'] is (t['kind'] in {'listen','initial_sound','final_sound'})
     if t['kind'] in {'recognize','dot_family','positional_form'}:assert t['writtenFallback']==t['target']
    choice_code='listOf('+', '.join(kt(v) for v in choices)+')' if choices else 'emptyList()'
    token_code='listOf('+', '.join(kt(v) for v in tokens)+')' if tokens else 'emptyList()'
    skill_code='listOf('+', '.join(kt(v) for v in t['skillIds'])+')'
    asset_code='listOf('+', '.join(f'AssetRef({kt(v)})' for v in t['assetRefs'])+')' if t['assetRefs'] else 'emptyList()'
    audio_code=f'AudioRef({kt(t["audioRef"])})' if t['audioRef'] else 'null'
    fallback_code=kt(t['writtenFallback']) if t['writtenFallback'] else 'null'
    required_code=str(t['audioRequiredForPublication']).lower()
    tasks.append(f'Task({kt(t["id"])}, TaskKind.{kinds[t["kind"]]}, {kt(t["target"])}, {choice_code}, {token_code}, {skill_code}, {asset_code}, {audio_code}, {required_code}, {fallback_code})')
   lessons.append(f'Lesson({kt(l["id"])}, {kt(l["title"])}, {kt(l["goal"])}, {l["order"]}, {kt(unit["id"])}, listOf('+', '.join(tasks)+'))')
  units.append(f'UnitNode({kt(unit["id"])}, {kt(unit["title"])}, {unit["order"]}, listOf('+', '.join(lessons)+'))')
 levels.append(f'Level({kt(level["id"])}, {kt(level["title"])}, {level["order"]}, listOf('+', '.join(units)+'))')
stories=[];page_ids=set()
for s in data['stories']:
 assert s['unitId'] in unit_ids and s['artRef'] in asset_ids and s['pages']
 assert len(s['choices'])==len(set(s['choices'])) and s['answer'] in s['choices']
 pages=[]
 for page in s['pages']:
  assert page['id'] not in page_ids and page['audioRef'] in audio_ids;page_ids.add(page['id'])
  pages.append(f'StoryPage({kt(page["id"])}, {kt(page["text"])}, AudioRef({kt(page["audioRef"])}))')
 stories.append(f'Story({kt(s["id"])}, {kt(s["unitId"])}, {kt(s["title"])}, {kt(s["subtitle"])}, {s["order"]}, AssetRef({kt(s["artRef"])}), listOf('+', '.join(pages)+f'), {kt(s["question"])}, listOf('+', '.join(kt(v) for v in s['choices'])+f'), {kt(s["answer"])})')
status={'development':'Development','staged':'Staged','published':'Published','archived':'Archived','rolled_back':'RolledBack'}[release['status']]
max_version=kt(release['maxAppVersion']) if release['maxAppVersion'] else 'null'
signature=kt(release['signature']) if release['signature'] else 'null'
release_code=f'ContentRelease({kt(release["releaseId"])}, ContentReleaseStatus.{status}, {release["schemaVersion"]}, {kt(release["contentVersion"])}, {kt(release["locale"])}, {kt(release["minAppVersion"])}, {max_version}, {kt(release["bundleSha256"])}, {kt(release["audioInventorySha256"])}, {str(release["approvedForRelease"]).lower()}, {signature})'
out(KOT/'content/Catalog.kt','package com.ava.reading.content\n// Generated from design/content.fa.json and content.release.json. Do not edit.\nobject Catalog {\n val release = '+release_code+'\n val skills = listOf(\n'+',\n'.join(skills_code)+'\n)\n val levels = listOf(\n'+',\n'.join(levels)+'\n)\n val lessons = levels.flatMap {level->level.units.flatMap {it.lessons}}\n val stories = listOf(\n'+',\n'.join(stories)+'\n)\n val snapshot = ContentSnapshot(release,skills,levels,stories)\n}')
fail=[]
for p,b in outputs.items():
 if check:
  if not p.exists() or p.read_bytes()!=b:fail.append(str(p.relative_to(ROOT)))
 else:p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(b)
if fail:print('Generated resources differ:',*fail,sep='\n');sys.exit(1)
print(f'{"Verified" if check else "Generated"}: {len(assets)} images, {len(strings)} strings, font, palette, {len(data["levels"])} level, {len(unit_ids)} units, {lesson_count} lessons, {task_count} activities and {len(data["stories"])} stories.')
