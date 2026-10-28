#!/usr/bin/env python3
"""Validate the vendor-neutral AVA CMS export seam without running a CMS."""
from pathlib import Path
from strict_json import load_json

ROOT=Path(__file__).resolve().parents[1]
schema=load_json(ROOT/"design/cms.export.schema.json")
roles=load_json(ROOT/"design/cms.roles.json")
candidate=load_json(ROOT/"design/fixtures/cms-payload-export.review.json")
content=load_json(ROOT/"design/content.fa.json")
inventory=load_json(ROOT/"design/audio.inventory.json")

assert schema["$id"].endswith("cms-export-v1.json") and candidate["exportVersion"]==1
assert candidate["provider"] in {"payload","strapi"}
assert candidate["contentVersion"]==content["contentVersion"]==inventory["contentVersion"]
assert candidate["scope"]=="partial_spike" and candidate["status"]=="educational_review"
required_roles={"author","audio_producer","education_reviewer","technical_reviewer","publisher","qa"}
assert {role["id"] for role in roles["roles"]}==required_roles
assert set(roles["separationRules"])=={
    "uploader_cannot_technical_review_same_asset","uploader_cannot_educational_review_same_asset",
    "author_cannot_publish_same_release","publisher_requires_both_audio_reviews","qa_cannot_mutate_editorial_content",
}

inventory_by_id={asset["id"]:asset for asset in inventory["assets"]}
ids=[]
for asset in candidate["audioAssets"]:
    ids.append(asset["id"]);canonical=inventory_by_id[asset["id"]]
    assert asset["recordingText"]==canonical["recordingText"]
    if asset["status"]=="approved":
        assert all(asset[field] for field in ("uploadedBy","educationalReviewer","technicalReviewer","masterAssetKey","deliveryAssetKey","sha256","licenseConsentRef"))
        assert asset["uploadedBy"] not in {asset["educationalReviewer"],asset["technicalReviewer"]}
    else:
        assert not any(asset[field] for field in ("masterAssetKey","deliveryAssetKey","sha256"))
assert len(ids)==len(set(ids))

required_ids={task["audioRef"] for level in content["levels"] for unit in level["units"] for lesson in unit["lessons"] for task in lesson["tasks"] if task["audioRequiredForPublication"]}
approved_ids={asset["id"] for asset in inventory["assets"] if asset["status"]=="approved" and asset["sha256"] and asset["masterAssetKey"] and asset["deliveryAssetKey"]}
publication_blockers=[]
if not content["approvedForRelease"]:publication_blockers.append("content_not_educator_approved")
if required_ids-approved_ids:publication_blockers.append("required_audio_not_approved")
if candidate["scope"]!="complete_release":publication_blockers.append("cms_export_scope_partial")
if candidate["status"]!="ready_for_compiler":publication_blockers.append("cms_candidate_not_ready")
assert publication_blockers==["content_not_educator_approved","required_audio_not_approved","cms_export_scope_partial","cms_candidate_not_ready"]
print(f"CMS contract passed: {len(required_roles)} roles, {len(ids)} spike audio records; publication safely blocked by {len(publication_blockers)} gates.")
