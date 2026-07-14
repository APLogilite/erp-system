---
document: WORKFLOW
version: 2.0.0
status: ACTIVE
owner: AI Framework
last_updated: 2026-07-14
---

# AI Engineering Workflow

This is the master workflow document. Every AI agent MUST read this before starting any work.

Detailed rules for each topic are in the referenced files below.

---

## Core Principles

1. The PRD is the single source of truth.
2. Every task belongs to exactly one PRD.
3. Every task has exactly one owner at any time.
4. Completed work is never modified directly.
5. Never skip workflow stages.
6. Never guess — ask.

---

## Required Reading Order

Every agent reads these on startup:

| Order | File | What it covers |
|-------|------|----------------|
| 1 | `WORKFLOW.md` | This file — master overview |
| 2 | `PROJECT_MEMORY.md` | Project context and conventions |
| 3 | `STATUS_TRANSITIONS.md` | All statuses, transitions, lock rules, cascade |
| 4 | `COMMUNICATION_GUIDE.md` | Principles, escalation, blocker reporting |
| 5 | `EXECUTION_RULES.md` | Startup, continuous execution, stopping, reporting |
| 6 | `DOCUMENTATION_RULES.md` | File ownership, sync rules, templates |
| 7 | `GIT_WORKFLOW.md` (SE + QA only) | Branches, lock-before-branch, merge rules |

---

## Lifecycle Overview

### PRD Lifecycle

```
DRAFT → REVIEW → APPROVED → IN_DEVELOPMENT → TESTING → READY_FOR_DEPLOYMENT → COMPLETED
```

### Task Lifecycle

```
PLANNING → PLANNED → READY_FOR_DEV → IN_DEVELOPMENT → READY_FOR_TEST → TESTING → TESTED → COMPLETED
```

Plus: `BLOCKED | ON_HOLD | CANCELLED`

### Bug Lifecycle

```
READY_FOR_DEV → IN_DEVELOPMENT → READY_FOR_TEST → TESTING → RESOLVED
```

→ See `ai/docs/STATUS_TRANSITIONS.md` for full transition rules per status.

---

## Agent Role Summary

| Agent | Owns | Key Transitions |
|-------|------|-----------------|
| Product Manager | Planning docs, PRDs, tasks (PLANNING→PLANNED), CHANGELOG | Creates PRD (DRAFT→APPROVED), creates tasks, manages deps, confirms merge |
| Software Engineer | Source code, change reports | Creates PRD branch (APPROVED→IN_DEVELOPMENT), implements tasks, activates deps, advances PRD→TESTING |
| QA Engineer | Test reports, bug tasks, test scripts | Tests tasks on PRD branch, creates bugs, advances PRD→READY_FOR_DEPLOYMENT |
| Release | Merge to main, cascade completion | Merges PRD→main, cascades COMPLETED |

---

## Git Branch Overview

```
main
  └── prd/PRD-XXX          (visible to all agents)
        └── feature/TASK-XXX  (visible only to working SE)
        └── bugfix/BUG-XXX
```

→ See `ai/docs/GIT_WORKFLOW.md` for lock-before-branch rules, merge rules, and commit conventions.

---

## File Ownership Overview

| Directory | Owner |
|-----------|-------|
| `ai/prd/`, `ai/tasks/` | Product Manager |
| `ai/docs/`, `ai/PROJECT_BOARD.md`, `ai/docs/CHANGELOG.md` | Product Manager |
| `backend/`, `frontend/` | Software Engineer |
| `ai/changes/` | Software Engineer |
| `ai/tests/`, `ai/scripts/` | QA Engineer |
| `ai/modules/`, `ai/flows/` | Technical Writer |

→ See `ai/docs/DOCUMENTATION_RULES.md` for full ownership and sync rules.

---

## Full Workflow Diagram

```
User
  │
  ▼
Product Manager
  │  Creates PRD (DRAFT→APPROVED)
  │  Generates tasks (PLANNING→PLANNED)
  ▼
Software Engineer
  │  1. Creates PRD branch (APPROVED→IN_DEVELOPMENT)
  │  2. Locks task → feature branch → implements
  │  3. Merges to PRD branch → READY_FOR_TEST
  │  4. Activates next PLANNED tasks
  │  5. When all done: PRD→TESTING
  ▼
QA Engineer
  │  1. Tests each task on PRD branch
  │  2. READY_FOR_TEST → TESTING → TESTED
  │  3. Creates bugs on failure
  │  4. When all TESTED: PRD→READY_FOR_DEPLOYMENT
  ▼
Product Manager
  │  Gets user confirmation
  ▼
Release
  │  1. Merges PRD branch → main
  │  2. Cascades: PRD + all tasks + bugs → COMPLETED
  ▼
Done
```
