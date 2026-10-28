#!/usr/bin/env python3
"""Static, deterministic gates for the runnable Payload prototype."""
from pathlib import Path
import json
import re

root = Path(__file__).resolve().parents[1]
cms = root / "cms/payload"

required = [
    "package.json",
    "pnpm-lock.yaml",
    "compose.yaml",
    ".env.example",
    "src/payload.config.ts",
    "src/payload-types.ts",
    "src/collections/Users.ts",
    "src/collections/AudioAssets.ts",
    "src/collections/CurriculumItems.ts",
    "src/collections/Releases.ts",
    "src/collections/AuditEvents.ts",
    "src/domain/audioInspection.ts",
    "src/domain/audioWorkflow.ts",
    "src/domain/deterministicExport.ts",
]
for relative in required:
    assert (cms / relative).is_file(), f"Missing CMS prototype file: {relative}"

package = json.loads((cms / "package.json").read_text())
expected_exact = {
    "@payloadcms/db-postgres": "3.92.1",
    "@payloadcms/next": "3.92.1",
    "@payloadcms/richtext-lexical": "3.92.1",
    "@payloadcms/storage-s3": "3.92.1",
    "@payloadcms/ui": "3.92.1",
    "next": "16.3.3",
    "payload": "3.92.1",
    "react": "19.2.6",
    "react-dom": "19.2.6",
}
for name, version in expected_exact.items():
    assert package["dependencies"].get(name) == version, (name, package["dependencies"].get(name))
for section in ("dependencies", "devDependencies"):
    for name, version in package[section].items():
        assert not version.startswith(("^", "~", ">", "<", "*")), f"Unpinned dependency: {name}={version}"

roles_source = (cms / "src/domain/roles.ts").read_text()
expected_roles = {
    "content_author",
    "educator_reviewer",
    "audio_reviewer",
    "publisher",
    "auditor",
    "system_admin",
}
assert all(f"'{role}'" in roles_source for role in expected_roles)

config = (cms / "src/payload.config.ts").read_text()
for marker in ("postgresAdapter", "s3Storage", "'audio-assets'", "signedDownloads: true"):
    assert marker in config, f"Missing config gate: {marker}"

audio = (cms / "src/collections/AudioAssets.ts").read_text()
for marker in (
    "beforeValidate: [initializeAudioUpload]",
    "quarantined",
    "technical_review",
    "educational_review",
    "deliveryObjectKey",
    "deliverySha256",
):
    assert marker in audio, f"Missing audio workflow gate: {marker}"

release_policy = (cms / "src/domain/releasePolicy.ts").read_text()
for marker in ("required_audio_delivery_not_ready", "deliveryStatus !== 'ready'", "deliverySha256"):
    assert marker in release_policy, f"Missing fail-closed delivery gate: {marker}"

env = (cms / ".env.example").read_text()
assert "PAYLOAD_SECRET=CHANGE_ME" in env
assert not re.search(r"PAYLOAD_SECRET=(?!CHANGE_ME|$).+", env)

compose = (cms / "compose.yaml").read_text()
assert "postgres:" in compose and "minio:" in compose and "127.0.0.1:" in compose

android_sources = "\n".join(
    path.read_text(errors="ignore")
    for path in (root / "android/app/src/main").rglob("*")
    if path.is_file() and path.suffix in {".kt", ".xml"}
).lower()
for forbidden in ("payloadcms", "/api/graphql", "s3_endpoint", "ava_cms"):
    assert forbidden not in android_sources, f"Android directly references CMS marker: {forbidden}"

print(
    "Payload prototype audit passed: exact stable dependencies, 5 collections, 6 roles, "
    "PostgreSQL/S3 config, quarantine/review gates, generated types and no Android CMS coupling."
)
