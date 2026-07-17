---
id: TASK-056

title: Move customerService.ts Out of core/api/services/

type: Refactor

scope: frontend

status: READY_FOR_TEST

priority: Low

owner: developer

assigned_to:

assigned_branch: feature/TASK-056

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
  - TASK-057

depends_on: []

blocks: []

labels:
  - frontend
  - cleanup
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary: ai/project/changes/CHANGE-TASK-056.md

test_report:

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: merged to PRD branch, READY_FOR_TEST (SE)

---

# Goal

Move or delete `customerService.ts` from `core/api/services/` — it's CRM-specific and doesn't belong in the core layer.

---

# Description

`frontend/src/core/api/services/customerService.ts` is a service for customer-related API calls. "Customers" are a CRM/business partner concept, not a core concern. This file should be moved to a CRM module if actively used, or deleted if it's dead code.

First check if any page component imports `customerService.ts`. If yes, move it to `modules/crm/services/` and update imports. If no imports exist, delete the file and remove the `ENDPOINTS.customers` config from `endpoints.ts`.

---

# Acceptance Criteria

- [ ] Verify if `customerService.ts` is imported anywhere in the frontend
- [ ] If used: move to `modules/crm/services/customerService.ts` and update imports
- [ ] If unused: delete `customerService.ts`
- [ ] Clean up `ENDPOINTS.customers` from `core/api/endpoints.ts` if no longer needed
- [ ] Frontend compiles with no errors

---

# Technical Notes

- Search: `grep -rn "customerService\|ENDPOINTS\.customers" frontend/src/`
- If the service calls endpoints that exist but no page uses them, it's dead code

---

# Files Expected

- `frontend/src/core/api/services/customerService.ts` — move or delete
- `frontend/src/core/api/endpoints.ts` — remove `customers` section if unused
