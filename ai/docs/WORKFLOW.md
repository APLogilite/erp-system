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
PLANNED
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

## Status Meaning

| Status | Description | Owner |
|----------|-------------|-------|
| PLANNING | Task is still being defined. | Planner |
| PLANNED | Task is complete but waiting for activation. | Planner |
| READY_FOR_DEV | All dependencies are satisfied and the task is ready for implementation. | Developer |
| IN_DEVELOPMENT | Developer is actively implementing the task. | Developer |
| READY_FOR_TEST | Implementation completed and waiting for QA. | Developer |
| TESTING | QA is executing tests. | Tester |
| TESTED | QA completed successfully. | Tester |
| COMPLETED | Task is closed and included in a completed release. | System |
| BLOCKED | Work cannot continue. | Current Owner |
| ON_HOLD | Temporarily paused. | Current Owner |
| CANCELLED | Task will never be implemented. | Planner |

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

Only one agent owns a task at any given time.

Ownership determines which agent is allowed to modify a task's implementation state.

| Task Status | Owner | Responsibilities |
|--------------|--------|------------------|
| PLANNING | Planner | Create and refine the task definition. |
| PLANNED | Planner | Finalize planning and maintain task details until it becomes eligible for development. |
| READY_FOR_DEV | Developer | Select the task for implementation and begin work. |
| IN_DEVELOPMENT | Developer | Implement the task and maintain implementation progress. |
| READY_FOR_TEST | Tester | Accept the completed implementation for testing. |
| TESTING | Tester | Execute manual and automated testing. |
| TESTED | Tester | Approve the completed implementation. |
| COMPLETED | System | Closed task. No further modification unless a new Enhancement or Bug task is created. |
| BLOCKED | Current Owner | Document the blocker and required action. |
| ON_HOLD | Current Owner | Pause work until resumed. |
| CANCELLED | Planner | Task permanently removed from execution. |

## Ownership Rules

- Only one agent may own a task at any time.
- Agents may modify only tasks they currently own.
- Completed tasks must never be modified directly.
- Requirement changes after implementation begins must create an Enhancement Task.
- Testing failures must create a Bug Task.
- Ownership transfers only through valid workflow status transitions.

---

# Task Selection Rules

PROJECT_BOARD.md is the single source of truth for execution.

Agents must always select work from PROJECT_BOARD.md.

---

## Developer Task Selection

Developer may only select tasks that satisfy ALL of the following:

- Status = READY_FOR_DEV
- Locked = false
- Parent PRD = APPROVED
- Assigned to Developer or Unassigned
- All dependencies are READY_FOR_TEST/COMPLETED

Priority order:

1. Highest Priority
2. Oldest Creation Date
3. Lowest Estimated Effort (if priorities are equal)

Before implementation Developer MUST:

- Lock the task.
- Assign ownership.
- Record start time.
- Create the Git branch.
- Update PROJECT_BOARD.md.

---

## Automatic Task Activation

Developer is responsible for activating newly available work.

Whenever a task reaches READY_FOR_TEST/COMPLETED:

Review every task that depends on it.

If ALL dependency tasks are READY_FOR_TEST/COMPLETED:

AND

- Parent PRD is APPROVED
- Task Status = PLANNED
- Task is not BLOCKED
- Task is not CANCELLED

Then automatically update:

PLANNED

↓

READY_FOR_DEV

Update PROJECT_BOARD.md.

Planner involvement is NOT required.

---

## Tester Task Selection

Tester may only select tasks that satisfy:

- Status = READY_FOR_TEST
- Locked = false

Priority:

1. Highest Priority
2. Oldest Completion Date

Before testing Tester MUST:

- Lock the task.
- Record start time.
- Update PROJECT_BOARD.md.

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

Every approved PRD owns its own integration branch.

The PRD branch is the working integration branch for all implementation tasks.

```
main
    │
    ▼
prd/PRD-001-<short-name>
    │
    ├── feature/TASK-001
    │       │
    │       └── Merge → prd/PRD-001-<short-name>
    │
    ├── feature/TASK-002
    │       │
    │       └── Merge → prd/PRD-001-<short-name>
    │
    ├── bugfix/BUG-001
    │       │
    │       └── Merge → prd/PRD-001-<short-name>
    │
    ├── enhancement/TASK-015
    │       │
    │       └── Merge → prd/PRD-001-<short-name>
    │
    ▼
QA validates the PRD branch
    │
    ▼
Merge PRD branch → main
```

## Branch Types

### Main Branch

```
main
```

Contains only production-ready code.

Direct development on `main` is never allowed.

---

### PRD Branch

A branch is created for every approved PRD.

Example:

```
prd/PRD-001-dynamic-form
```

The PRD branch is the integration branch for that feature.

If the original PRD branch has been merged into `main`, subsequent work uses a versioned PRD branch:

```
prd/PRD-001-v2
```

Versioned PRD branches follow the exact same lifecycle as the original.

All implementation, bug fixes, and enhancements for the PRD are merged into this branch.

---

### Task Branch

Every implementation task creates its own branch from the PRD branch.

Example:

```
feature/TASK-001
```

Base Branch:

```
prd/PRD-001-dynamic-form
```

After implementation and validation:

```
feature/TASK-001
        │
        ▼
Merge
        │
        ▼
prd/PRD-001-dynamic-form
```

Task branches should not be merged directly into `main`.

---

### Bug Branch

Bug fixes also start from the PRD branch.

Example:

```
bugfix/BUG-004
```

Base Branch:

```
prd/PRD-001-dynamic-form
```

After validation they are merged back into the PRD branch.

---

### Enhancement Branch

Enhancement tasks also start from the PRD branch.

Example:

```
enhancement/TASK-015
```

Base Branch:

```
prd/PRD-001-dynamic-form
```

After validation they are merged back into the PRD branch.

---

### Post-Merge Bug Fix Branch

When a PRD branch has been merged into `main`, subsequent bug fixes or enhancements for that PRD must not reuse the original PRD branch. Instead, a new versioned PRD branch is created from the latest `main`.

Determine the base branch:

1. Fetch the latest remote state: `git fetch --all`
2. Check if the original PRD branch exists locally or on remote.
3. If the branch does not exist, or if its tip is an ancestor of `main` (checked via `git merge-base --is-ancestor`):
   - The PRD branch has been merged.
   - Checkout `main` and pull the latest.
   - Determine the next versioned PRD branch name:
     - Check for existing branches matching `prd/PRD-XXX-v*` locally and on remote.
     - Use the next available version number: `prd/PRD-XXX-v2`, `-v3`, etc.
   - Create the versioned PRD branch from `main` and push it.
   - This versioned branch becomes the Parent PRD branch for this work.
4. If the branch exists and has NOT been merged into `main`:
   - Checkout the original PRD branch and pull the latest.
   - It remains the Parent PRD branch.

Once the base is determined, create the task/bug branch from it normally.

After testing, the versioned PRD branch is merged into `main`:

```
main
    │
    ▼
prd/PRD-001-v2
    │
    ├── bugfix/BUG-002
    │       │
    │       └── Merge → prd/PRD-001-v2
    │
    ▼
QA validates prd/PRD-001-v2
    │
    ▼
Merge prd/PRD-001-v2 → main
```

---

## Merge Rules

Software Engineer

- Creates the task branch from the PRD branch.
- Implements the assigned task.
- Runs all required validation.
- Merges the completed task branch back into the PRD branch.
- Updates PROJECT_BOARD.md.
- Updates the Task document.
- Generates the Change Report.

Software Engineer never merges directly into `main`.

---

QA Engineer

QA always validates the determined test branch (original or versioned PRD branch).

QA never validates individual task branches unless specifically requested.

---

Release

When every task under the PRD has passed testing:

```
prd/PRD-001-dynamic-form       or       prd/PRD-001-v2
        │                                        │
        ▼                                        ▼
Merge                                   Merge
        │                                        │
        ▼                                        ▼
main                                            main
```

Only the completed PRD branch (original or versioned) may be merged into `main`.

---

# Branch Ownership

| Branch | Owner |
|---------|-------|
| main | Release / Supervisor |
| prd/* (original and versioned) | Software Engineer (during development), QA (during testing) |
| feature/* | Software Engineer |
| bugfix/* | Software Engineer |
| enhancement/* | Software Engineer |

Rules:

- Only one active task branch may exist for a task.
- Every task branch must originate from its base PRD branch (original or versioned).
- Every completed task branch must be merged into its base PRD branch before the next task begins.
- QA always tests the latest determined test branch.
- Only a fully tested PRD branch (original or versioned) may be merged into `main`.

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

# Failure Reports

Validation failures must never be silently ignored.

When validation fails:

1. Do not merge the task.

2. Create

ai/failures/FAIL-{TASK_ID}.md

using

ai/docs/FAILURE_TEMPLATE.md

3. Update the Task.

4. Set status to BLOCKED.

5. Link the Failure Report.

6. Determine whether other READY_FOR_DEV tasks may continue.

If another task has no dependency on the failed task,

development continues.

Failure Reports become part of the permanent project history.

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
