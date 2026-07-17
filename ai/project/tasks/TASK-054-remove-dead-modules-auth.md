---
id: TASK-054

title: Remove Dead modules/auth/ Package

type: Refactor

scope: backend

status: TESTED

priority: High

owner: developer

assigned_to:

assigned_branch: feature/TASK-054

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

change_summary: ai/project/changes/CHANGE-TASK-054.md

test_report: ai/project/tests/TEST-TASK-054.md

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: merged to PRD branch, READY_FOR_TEST (SE)

---

# Goal

Remove the stale `modules/auth/` package that has been superseded by `platform/identity/`.

---

# Description

The `modules/auth/` package contains 5 files (`AuthController.java`, `AuthService.java`, `AuthRepository.java`, `AuthEntity.java`, `AuthDto.java`). Zero external references exist — nothing in the codebase imports from `com.erp.modules.auth`. The real authentication system lives in `platform/identity/` which has the full JWT auth, login, password management, and session handling.

This is safe to delete entirely. Run `mvn clean compile` after deletion to confirm no broken imports.

---

# Acceptance Criteria

- [ ] `backend/src/main/java/com/erp/modules/auth/` directory deleted
- [ ] `mvn clean compile` succeeds with no errors
- [ ] All 36 existing tests still pass
- [ ] No references to `modules.auth` remain in the codebase

---

# Technical Notes

- No code changes needed — just delete the directory
- Verify with: `grep -rn "modules\.auth" backend/src/main/java/` before and after

---

# Files Expected

- DELETE `backend/src/main/java/com/erp/modules/auth/controller/AuthController.java`
- DELETE `backend/src/main/java/com/erp/modules/auth/service/AuthService.java`
- DELETE `backend/src/main/java/com/erp/modules/auth/repository/AuthRepository.java`
- DELETE `backend/src/main/java/com/erp/modules/auth/entity/AuthEntity.java`
- DELETE `backend/src/main/java/com/erp/modules/auth/dto/AuthDto.java`
