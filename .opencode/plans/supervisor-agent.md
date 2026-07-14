# Supervisor Agent — Specification

## Problem

When a PRD branch merges to main, the "cascade" (PRD→COMPLETED, all tasks/bugs→COMPLETED, PROJECT_BOARD.md update) does not happen automatically. No dedicated "Release" agent exists, and SE/QA agents are forbidden from merging per their agent definitions.

## Solution

A **Supervisor** agent — a Python process in `ai/monitor/` — that acts as a central orchestrator:

- User clicks "Start" on a PRD in the dashboard
- Supervisor clones the repo into isolated workspaces per PRD
- Assigns tasks to SE/QA agents one by one (file-based communication)
- Controls parallelism (default 1 SE at a time, configurable at start)
- Detects task completion via file watcher → updates statuses → assigns next task
- Handles merge + cascade when PRD is complete

## Architecture

```
Dashboard (dashboard.html)
    │ [Start] button per PRD + parallel count selector
    ▼
Server (server.py) — extended with Supervisor API
    │ POST /api/supervisor/start-prd
    │ GET  /api/supervisor/status
    │ POST /api/supervisor/confirm-merge
    ▼
supervisor/
├── orchestrator.py   Core loop: watches file changes, updates state, assigns tasks
├── repo_manager.py   Clone/cleanup workspaces per PRD
└── cascade.py        Merge + cascade completion
```

## Flow

1. PM creates PRD + tasks (unchanged workflow)
2. User opens dashboard, sees new PRD in APPROVED status
3. User clicks "Start", configures parallelism (default 1)
4. Supervisor clones repo → `workspaces/PRD-XXX/`
5. Supervisor activates dependency-free tasks → READY_FOR_DEV
6. Supervisor assigns first task to SE-1 (writes `assigned_to: SE-1`)
7. SE-1 agent picks up assigned task, implements, updates status → READY_FOR_TEST
8. File watcher detects change → Supervisor updates PROJECT_BOARD.md, activates next eligible tasks, assigns to available SE
9. All SE tasks done → Supervisor advances PRD → TESTING, assigns tested tasks to QA agents
10. QA verifies → updates task → TESTED
11. All TESTED → PRD → READY_FOR_DEPLOYMENT → user confirms
12. Supervisor merges PRD branch → main, cascades all to COMPLETED, cleans up workspace

## Communication Model

File-based — no changes to existing SE/QA agent workflow:

| Who | Action |
|-----|--------|
| Supervisor | Writes `assigned_to` field + updates task status in frontmatter |
| SE/QA agent | Reads task doc, sees assignment, works, updates status when done |
| Supervisor | File watcher detects `.md` change via `os.path.getmtime` → triggers next action |
| Dashboard | SSE pushes reload event to browser → real-time UI updates |

## Resource Control

- Default parallelism: 1 SE at a time per PRD
- User configures at PRD start time via dashboard selector
- Supervisor enforces: only assigns N tasks, waits for completion before assigning next
- Same pattern for QA agents

## Files to Create

| File | Est. Lines | Purpose |
|------|-----------|---------|
| `ai/monitor/supervisor/__init__.py` | — | Package init |
| `ai/monitor/supervisor/orchestrator.py` | ~250 | Core orchestration: file watcher callbacks, state machine, task dispatch |
| `ai/monitor/supervisor/repo_manager.py` | ~100 | Git clone per PRD, branch creation, cleanup |
| `ai/monitor/supervisor/cascade.py` | ~80 | Merge PRD branch → main, update all statuses to COMPLETED, push |

## Files to Modify

| File | Change |
|------|--------|
| `ai/monitor/server.py` | Add Supervisor API endpoints and background orchestrator thread |
| `ai/monitor/dashboard.html` | Add PRD "Start" button, parallelism slider, execution log panel, agent queue view |
| `ai/monitor/README.md` | Document Supervisor section |
| `ai/docs/WORKFLOW.md` | Add Supervisor row to agent role table |
| `ai/docs/STATUS_TRANSITIONS.md` | Change cascade owner from "Release" to "Supervisor" |
| `ai/docs/GIT_WORKFLOW.md` | Add Supervisor merge rules section |
| `.opencode/agents/supervisor.md` | New agent definition file |

## Dependencies

- Python 3.8+ (already used by `server.py`)
- `pyyaml`, `watchdog` (already optional dependencies)
- `git` CLI available on system PATH
- No new Python packages required

## Status

**Planned** — designed 2026-07-14. Not yet implemented.
