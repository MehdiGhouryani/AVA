from __future__ import annotations

from pathlib import Path
from typing import Any
import argparse
import json
import subprocess

from .validate import ROOT, assert_valid, task_index

PRIORITY = {"P0": 0, "P1": 1, "P2": 2}


def _ready_tasks(roadmap: dict[str, Any]) -> list[dict[str, Any]]:
    tasks = task_index(roadmap)
    result = []
    for task in tasks.values():
        if task["status"] not in {"ready", "in_progress"} or not task["automationAllowed"]:
            continue
        if all(tasks[dependency]["status"] == "completed" for dependency in task["dependsOn"]):
            result.append(task)
    return sorted(result, key=lambda task: (PRIORITY[task["priority"]], task["id"]))


def _print_task(task: dict[str, Any]) -> None:
    print(f"{task['id']} [{task['priority']}/{task['status']}] {task['titleFa']}")
    print("Read first:")
    for path in task["readFirst"]:
        print(f"  - {path}")
    print("Allowed paths:")
    for path in task["allowedPaths"]:
        print(f"  - {path}")
    print("Acceptance:")
    for item in task["acceptance"]:
        print(f"  - {item}")


def command_status(roadmap: dict[str, Any]) -> None:
    for phase in roadmap["phases"]:
        counts: dict[str, int] = {}
        for task in phase["tasks"]:
            counts[task["status"]] = counts.get(task["status"], 0) + 1
        summary = ", ".join(f"{key}={counts[key]}" for key in sorted(counts))
        print(f"{phase['id']} {phase['titleFa']}: {summary}")


def command_next(roadmap: dict[str, Any], as_json: bool) -> None:
    tasks = _ready_tasks(roadmap)
    if as_json:
        print(json.dumps(tasks, ensure_ascii=False, indent=2))
        return
    if not tasks:
        print("No automation task is ready. Resolve blockers or human gates first.")
        return
    _print_task(tasks[0])


def command_task(roadmap: dict[str, Any], task_id: str, as_json: bool) -> None:
    task = task_index(roadmap).get(task_id)
    if task is None:
        raise SystemExit(f"Unknown task: {task_id}")
    if as_json:
        print(json.dumps(task, ensure_ascii=False, indent=2))
    else:
        _print_task(task)


def command_run_gates(roadmap: dict[str, Any], task_id: str) -> None:
    tasks = task_index(roadmap)
    task = tasks.get(task_id)
    if task is None:
        raise SystemExit(f"Unknown task: {task_id}")
    if not task["automationAllowed"]:
        raise SystemExit(f"Task {task_id} is a human gate and cannot be automated")
    incomplete = [dependency for dependency in task["dependsOn"] if tasks[dependency]["status"] != "completed"]
    if incomplete:
        raise SystemExit(f"Task {task_id} is blocked by: {', '.join(incomplete)}")
    if task["status"] not in {"ready", "in_progress"}:
        raise SystemExit(f"Task {task_id} is not runnable in status {task['status']}")
    for gate in task["gates"]:
        cwd = ROOT / gate["cwd"]
        command = gate["command"]
        print(f"RUN {gate['cwd']}: {' '.join(command)}", flush=True)
        subprocess.run(command, cwd=cwd, check=True)


def main() -> None:
    parser = argparse.ArgumentParser(description="AVA coding-agent execution harness")
    subparsers = parser.add_subparsers(dest="command", required=True)
    subparsers.add_parser("validate")
    subparsers.add_parser("status")
    next_parser = subparsers.add_parser("next")
    next_parser.add_argument("--json", action="store_true")
    task_parser = subparsers.add_parser("task")
    task_parser.add_argument("task_id")
    task_parser.add_argument("--json", action="store_true")
    gates_parser = subparsers.add_parser("run-gates")
    gates_parser.add_argument("task_id")
    args = parser.parse_args()

    roadmap = assert_valid()
    if args.command == "validate":
        print(f"Harness valid: {len(task_index(roadmap))} tasks across {len(roadmap['phases'])} phases")
    elif args.command == "status":
        command_status(roadmap)
    elif args.command == "next":
        command_next(roadmap, args.json)
    elif args.command == "task":
        command_task(roadmap, args.task_id, args.json)
    elif args.command == "run-gates":
        command_run_gates(roadmap, args.task_id)


if __name__ == "__main__":
    main()
