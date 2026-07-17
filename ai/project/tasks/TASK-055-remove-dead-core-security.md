---
id: TASK-055

title: Remove Dead core/security/ Package

type: Refactor

scope: backend

status: TESTED

priority: High

owner: developer

assigned_to:

assigned_branch: feature/TASK-055

locked: false

created: 2026-07-16

updated: 2026-07-17

started: 2026-07-17

completed: 2026-07-17

estimated_hours: 1

actual_hours:

parent_prd: PRD-005

prd_version: 1.3.0

prd_branch: prd/PRD-005

base_branch:

merge_target:

merge_strategy:

parent_task:

related_tasks: []

depends_on: []

blocks: []

labels:
  - backend
  - cleanup
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary: ai/project/changes/CHANGE-TASK-055.md

test_report: ai/project/tests/TEST-TASK-055.md

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: merged to PRD branch, READY_FOR_TEST (SE)

---

# Goal

Remove the stale `core/security/` package that has been superseded by `platform/identity/authorization/`.

---

# Description

The `core/security/` package contains 12 files: `PermissionController`, `PermissionService`, `PermissionServiceImpl`, `PermissionRegistry`, `PermissionValidator`, `PermissionMapper`, `PermissionLevel` enum, `PermissionEvaluator`, `PermissionCheckRequestDto`, `PermissionCheckResponseDto`, `PermissionMetadataDto`, and `PermissionDeniedException`.

Zero external references exist — nothing imports from `com.erp.core.security`. The frontend calls `/identity/permissions` and `/auth/permissions` (both handled by `platform/identity/`), not `/security/check`. The real permission system lives in `platform/identity/authorization/`.

This is safe to delete entirely. Run `mvn clean compile` after deletion to confirm no broken imports.

---

# Acceptance Criteria

- [ ] `backend/src/main/java/com/erp/core/security/` directory deleted
- [ ] `mvn clean compile` succeeds with no errors
- [ ] All 36 existing tests still pass
- [ ] No references to `core.security` remain in the codebase

---

# Technical Notes

- No code changes needed — just delete the directory
- The `PermissionEvaluator` in `core/security/evaluator/` is NOT the same as `platform/identity/authorization/PermissionEvaluator.java` — they're different classes. The `platform/identity/` one is the active one.

---

# Files Expected

- DELETE `backend/src/main/java/com/erp/core/security/` (entire directory, 12 files)
