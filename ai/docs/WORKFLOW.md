---
document: WORKFLOW
version: 1.0.0
status: ACTIVE
owner: AI Framework
last_updated: 2026-07-07
---

# AI Engineering Workflow

## Purpose

This document defines the standard workflow followed by every AI agent working on this project.

It is the authoritative source for:

- Project lifecycle
- PRD lifecycle
- Task lifecycle
- Agent responsibilities
- Status transitions
- Ownership rules
- Bug handling
- Enhancement handling
- Deployment readiness

Every AI agent MUST read this document before starting any work.

If an agent prompt conflicts with this document, this document takes precedence.

---

# Core Principles

1. The PRD is the single source of truth.
2. Tasks are derived from the PRD.
3. Every task belongs to exactly one PRD.
4. Every task has exactly one owner at any time.
5. Completed work is never modified directly.
6. Every significant change must be traceable.
7. Preserve project history.
8. Never skip workflow stages.

---

# Project Lifecycle

Each Product Requirement Document (PRD) follows this lifecycle.

```
DRAFT
    ↓
REVIEW
    ↓
APPROVED
    ↓
IN_DEVELOPMENT
    ↓
TESTING
    ↓
READY_FOR_DEPLOYMENT
    ↓
COMPLETED
```

| Status | Description |
|----------|-------------|
| DRAFT | Initial discussion with user |
| REVIEW | Waiting for approval |
| APPROVED | Requirements finalized |
| IN_DEVELOPMENT | Tasks are being implemented |
| TESTING | QA validation in progress |
| READY_FOR_DEPLOYMENT | All tasks successfully tested |
| COMPLETED | Released and closed |

---

# Task Lifecycle

Implementation tasks follow this lifecycle.

```
PLANNING
      ↓
READY_FOR_DEV
      ↓
IN_DEVELOPMENT
      ↓
READY_FOR_TEST
      ↓
TESTING
      ↓
TESTED
      ↓
COMPLETED
```

Additional states

```
BLOCKED
ON_HOLD
CANCELLED
```

---

# Bug Lifecycle

Bug tasks follow this lifecycle.

```
READY_FOR_DEV
      ↓
IN_DEVELOPMENT
      ↓
READY_FOR_TEST
      ↓
TESTING
      ↓
RESOLVED
```

If testing fails again, the cycle repeats.

---

# Agent Responsibilities

## Planner

Responsibilities

- Communicate with the user
- Gather requirements
- Create and maintain PRDs
- Version PRDs
- Generate implementation tasks
- Update tasks that have NOT started
- Create enhancement tasks
- Maintain project changelog

Planner MUST NOT

- Write production code
- Modify application source code
- Perform implementation
- Approve testing

---

## Developer

Responsibilities

- Select development tasks
- Lock assigned task
- Create feature branch
- Implement requested functionality
- Run build
- Run lint
- Run available automated tests
- Update task metadata
- Generate change summary

Developer MUST NOT

- Modify PRD
- Change requirements
- Approve testing

---

## Tester

Responsibilities

- Generate test cases
- Generate automation scripts
- Execute tests
- Produce test reports
- Create bug tasks
- Verify bug fixes
- Approve completed tasks

Tester MUST NOT

- Change requirements
- Implement new features
- Modify unrelated source code

---

# Ownership Rules

Only one agent owns a task at any moment.

| Task Status | Owner |
|--------------|--------|
| PLANNING | Planner |
| READY_FOR_DEV | Developer |
| IN_DEVELOPMENT | Developer |
| READY_FOR_TEST | Tester |
| TESTING | Tester |
| TESTED | Tester |
| COMPLETED | System |

Agents may only modify tasks they currently own.

---

# Task Selection Rules

Developer selects the next task using the following priority:

1. Highest Priority
2. No unresolved dependencies
3. Oldest creation date

Developer MUST NOT start a task if:

- Dependencies are incomplete
- Status is not READY_FOR_DEV
- Task is already assigned

Tester selects the next task using:

1. Highest Priority
2. READY_FOR_TEST
3. Oldest creation date

---

# PRD Rules

The PRD is the source of truth.

Every task references:

- Parent PRD
- PRD Version

When the PRD changes:

## If task status is

- PLANNING
- READY_FOR_DEV

Planner updates the existing task.

## If task status is

- IN_DEVELOPMENT
- READY_FOR_TEST
- TESTING
- TESTED
- COMPLETED

Planner MUST create a new Enhancement Task.

Completed work must never be silently modified.

---

# Enhancement Rules

Enhancement tasks are created when approved requirements change after implementation has started.

Enhancement task must contain:

- Parent PRD
- Parent Task
- Previous PRD Version
- Current PRD Version
- Reason for enhancement

Enhancements follow the normal task lifecycle.

---

# Bug Rules

If testing fails:

1. Never edit the original task.
2. Create a Bug Task.
3. Link the Bug Task to:
   - Parent PRD
   - Parent Task
4. Set Bug Task status to READY_FOR_DEV.
5. Developer fixes Bug Task.
6. Tester reruns the original task.
7. Original task is only marked TESTED when all related bugs are resolved.

---

# Task Locking

Before starting work:

Developer must:

- Assign itself as owner
- Update task status
- Record start time

Tester must:

- Assign itself as owner
- Update task status
- Record start time

This prevents multiple agents from working on the same task.

---

# Git Workflow

Every implementation task uses its own branch.

Feature

```
feature/TASK-001
```

Bug

```
bugfix/BUG-003
```

Enhancement

```
enhancement/TASK-014
```

Developer never merges branches.

Merge happens after successful testing.

---

# Required Documents

Every PRD must have:

- PRD Document
- Related Tasks
- Version History

Every Task must have:

- Task Document
- Change Summary
- Test Report (after testing)

Every Bug must have:

- Bug Document
- Fix Summary
- Verification Report

---

# File Ownership

Planner manages

```
ai/prd/
ai/docs/
ai/tasks/
```

Developer manages

```
Application Source Code
ai/changes/
```

Tester manages

```
ai/tests/
ai/scripts/
```

Agents should not modify files outside their responsibility unless explicitly instructed.

---

# General Rules

Every AI agent MUST:

1. Read WORKFLOW.md before starting.
2. Read PROJECT_MEMORY.md.
3. Read assigned task completely.
4. Update task metadata immediately after changing status.
5. Record important decisions in task history.
6. Never overwrite another agent's work.
7. Preserve project history.
8. Never skip workflow stages.
9. Never change project requirements without Planner approval.
10. Stop and report when blocked instead of guessing.

---

# Future Expansion

This workflow is designed to support additional agents.

Possible future agents:

- Supervisor
- Documentation Writer
- Security Reviewer
- Performance Reviewer
- Deployment Agent
- Release Manager

All future agents must follow this workflow.

---

# Workflow Summary

```
User
 │
 ▼
Planner
 │
 ▼
PRD
 │
 ▼
Task Generation
 │
 ▼
Developer
 │
 ▼
Change Summary
 │
 ▼
Tester
 │
 ├───────────────┐
 │               │
 │ PASS          │ FAIL
 ▼               │
TESTED           │
 │               │
 ▼               │
READY_FOR_DEPLOYMENT
                 │
                 ▼
             Create Bug
                 │
                 ▼
          READY_FOR_DEV
                 │
                 └──────────────► Developer
```
