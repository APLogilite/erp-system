---
id: TASK-057

title: Audit and Remove Stale Frontend API Endpoints

type: Refactor

scope: frontend

status: TESTED

priority: Low

owner: developer

assigned_to:

assigned_branch: feature/TASK-057

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

related_tasks:
  - TASK-056

depends_on: []

blocks: []

labels:
  - frontend
  - cleanup
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary: ai/project/changes/CHANGE-TASK-057.md

test_report: ai/project/tests/TEST-TASK-057.md

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: merged to PRD branch, READY_FOR_TEST (SE)

---

# Goal

Clean up stale endpoint configurations in `endpoints.ts` to remove unused entries and prevent confusion.

---

# Description

`frontend/src/core/api/endpoints.ts` contains endpoint definitions for various backend services. Some entries may no longer be actively called by any page component. Audit each section:

- `auth` — verify login/logout/refresh/me/change-password are used
- `identity` — verify all CRUD endpoints are called by admin pages
- `authz` — verify permissions endpoints are used
- `metadata` — verify old PRD-001 metadata endpoints are used by admin form/table designer
- `customers` — check if any page imports this (see TASK-056)
- `users` — check if any page imports this

Remove any section where no page component references it.

---

# Acceptance Criteria

- [ ] Each section in `endpoints.ts` is verified against actual usage in page components
- [ ] Unused sections are removed
- [ ] Frontend compiles with no errors
- [ ] No broken imports from removing endpoint configs

---

# Technical Notes

- Search pattern: `grep -rn "ENDPOINTS\.<section>" frontend/src/ --include="*.ts" --include="*.tsx"`
- Example: `grep -rn "ENDPOINTS\.users" frontend/src/`
- Remove unused sections entirely, not just comment them out

---

# Files Expected

- `frontend/src/core/api/endpoints.ts` — remove stale sections
