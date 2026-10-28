from __future__ import annotations

from collections import defaultdict
from pathlib import Path, PurePosixPath
from typing import Any
import json

ROOT = Path(__file__).resolve().parents[1]
ROADMAP = ROOT / "harness/roadmap.json"

ALLOWED_STATUSES = {"ready", "blocked", "human_gate", "in_progress", "completed"}
ALLOWED_PRIORITIES = {"P0", "P1", "P2"}
ALLOWED_EXECUTABLES = {"python3", "pnpm", "./gradlew"}
FORBIDDEN_GRADLE_TASKS = {
    "assemble",
    "assembleDebug",
    "assembleRelease",
    "bundle",
    "bundleDebug",
    "bundleRelease",
}


def _strict_object(pairs: list[tuple[str, Any]]) -> dict[str, Any]:
    result: dict[str, Any] = {}
    for key, value in pairs:
        if key in result:
            raise ValueError(f"duplicate JSON key: {key}")
        result[key] = value
    return result


def load_roadmap(path: Path = ROADMAP) -> dict[str, Any]:
    return json.loads(path.read_text(), object_pairs_hook=_strict_object)


def task_index(roadmap: dict[str, Any]) -> dict[str, dict[str, Any]]:
    return {task["id"]: task for phase in roadmap["phases"] for task in phase["tasks"]}


def validate_roadmap(roadmap: dict[str, Any]) -> list[str]:
    errors: list[str] = []
    if roadmap.get("schemaVersion") != 1:
        errors.append("schemaVersion must be 1")
    if roadmap.get("project") != "AVA":
        errors.append("project must be AVA")
    if not roadmap.get("globalInvariants") or not roadmap.get("forbiddenActions"):
        errors.append("global invariants and forbidden actions must not be empty")

    phase_ids: set[str] = set()
    tasks: dict[str, dict[str, Any]] = {}
    phase_for_task: dict[str, str] = {}
    for phase in roadmap.get("phases", []):
        phase_id = phase.get("id")
        if phase_id in phase_ids:
            errors.append(f"duplicate phase id: {phase_id}")
        phase_ids.add(phase_id)
        playbook = phase.get("playbook", "")
        if not _safe_relative(playbook) or not (ROOT / playbook).is_file():
            errors.append(f"missing or unsafe playbook: {playbook}")
        if not phase.get("exitGate"):
            errors.append(f"phase {phase_id} has no exit gate")

        for task in phase.get("tasks", []):
            task_id = task.get("id", "")
            if task_id in tasks:
                errors.append(f"duplicate task id: {task_id}")
                continue
            tasks[task_id] = task
            phase_for_task[task_id] = phase_id
            if not task_id.startswith(f"{phase_id}-"):
                errors.append(f"task {task_id} does not belong to phase {phase_id}")
            if task.get("status") not in ALLOWED_STATUSES:
                errors.append(f"invalid status for {task_id}")
            if task.get("priority") not in ALLOWED_PRIORITIES:
                errors.append(f"invalid priority for {task_id}")
            if task.get("status") == "human_gate" and task.get("automationAllowed") is not False:
                errors.append(f"human gate {task_id} must disable automation")
            if task.get("automationAllowed") is False and task.get("status") != "human_gate":
                errors.append(f"non-human task {task_id} unexpectedly disables automation")
            for field in ("readFirst", "allowedPaths", "acceptance", "evidenceFiles", "gates"):
                if not task.get(field):
                    errors.append(f"task {task_id} has no {field}")
            for relative in task.get("readFirst", []):
                if not _safe_relative(relative) or not (ROOT / relative).is_file():
                    errors.append(f"task {task_id} references missing file: {relative}")
            for relative in task.get("evidenceFiles", []):
                if not _safe_relative(relative):
                    errors.append(f"task {task_id} has unsafe evidence path: {relative}")
            for pattern in task.get("allowedPaths", []):
                clean = pattern[:-3] if pattern.endswith("/**") else pattern
                if not _safe_relative(clean):
                    errors.append(f"task {task_id} has unsafe allowed path: {pattern}")
                if clean.startswith("src/") or clean == "src":
                    errors.append(f"task {task_id} may not expand historical React source")
            for gate in task.get("gates", []):
                errors.extend(_validate_gate(task_id, gate))

    for task_id, task in tasks.items():
        for dependency in task.get("dependsOn", []):
            if dependency not in tasks:
                errors.append(f"task {task_id} has unknown dependency: {dependency}")
            elif int(phase_for_task[dependency]) > int(phase_for_task[task_id]):
                errors.append(f"task {task_id} depends on later phase task: {dependency}")
        if task.get("status") == "ready":
            incomplete = [
                dependency
                for dependency in task.get("dependsOn", [])
                if tasks.get(dependency, {}).get("status") != "completed"
            ]
            if incomplete:
                errors.append(f"ready task {task_id} has incomplete dependencies: {incomplete}")

    errors.extend(_cycle_errors(tasks))
    return errors


def _safe_relative(value: str) -> bool:
    if not value or "\\" in value:
        return False
    path = PurePosixPath(value)
    return not path.is_absolute() and ".." not in path.parts


def _validate_gate(task_id: str, gate: dict[str, Any]) -> list[str]:
    errors: list[str] = []
    cwd = gate.get("cwd", "")
    command = gate.get("command", [])
    if not _safe_relative(cwd) or not (ROOT / cwd).is_dir():
        errors.append(f"task {task_id} has invalid gate cwd: {cwd}")
    if not command or command[0] not in ALLOWED_EXECUTABLES:
        errors.append(f"task {task_id} uses non-allowlisted command: {command}")
        return errors
    if command[0] == "./gradlew" and any(token in FORBIDDEN_GRADLE_TASKS for token in command[1:]):
        errors.append(f"task {task_id} attempts forbidden package build: {command}")
    if any("--stacktrace" == token for token in command) and task_id.startswith("080-"):
        errors.append(f"task {task_id} should not force verbose secret-bearing logs")
    return errors


def _cycle_errors(tasks: dict[str, dict[str, Any]]) -> list[str]:
    graph = defaultdict(list)
    for task_id, task in tasks.items():
        graph[task_id].extend(task.get("dependsOn", []))
    visiting: set[str] = set()
    visited: set[str] = set()
    errors: list[str] = []

    def visit(node: str) -> None:
        if node in visiting:
            errors.append(f"dependency cycle includes {node}")
            return
        if node in visited:
            return
        visiting.add(node)
        for dependency in graph[node]:
            if dependency in tasks:
                visit(dependency)
        visiting.remove(node)
        visited.add(node)

    for task_id in tasks:
        visit(task_id)
    return errors


def assert_valid(path: Path = ROADMAP) -> dict[str, Any]:
    roadmap = load_roadmap(path)
    errors = validate_roadmap(roadmap)
    if errors:
        raise ValueError("\n".join(f"- {error}" for error in errors))
    return roadmap
