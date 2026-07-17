---
id: TASK-046

title: Add childTabIds to TabDefinitionResponse

type: Feature

scope: backend

status: READY_FOR_TEST

priority: High

owner: developer

assigned_to:

assigned_branch: feature/TASK-046

locked: false

created: 2026-07-16

updated: 2026-07-17

started: 2026-07-17

completed: 2026-07-17

estimated_hours: 2

actual_hours:

parent_prd: PRD-005

prd_version: 1.3.0

prd_branch: prd/PRD-005

base_branch:

merge_target:

merge_strategy:

parent_task:

related_tasks:
  - TASK-047

depends_on: []

blocks: []

labels:
  - backend
  - dto
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary: ai/project/changes/CHANGE-TASK-046.md

test_report:

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: merged to PRD branch, READY_FOR_TEST (SE)

---

# Goal

Add `childTabIds` to the tab definition response so the frontend doesn't need to derive parent-child tab relationships from naming conventions.

---

# Description

The `TabDefinitionResponse` DTO currently lacks `childTabIds`. The backend `WindowDefinitionAssemblyService` should compute which tabs are children of each tab (by matching `parentColumn` to table names) and include `childTabIds: UUID[]` in each tab response.

Currently the frontend's `WindowPage.tsx:230-242` has a `findChildTabs()` function that parses `parentColumn` naming conventions (e.g., `window_id` → strip `_id` → match table name). This is schema logic that should be in the backend.

---

# Acceptance Criteria

- [ ] `TabDefinitionResponse` has new `childTabIds: UUID[]` field (empty list if no children)
- [ ] `WindowDefinitionAssemblyService` populates it by scanning all tabs of the window and matching each tab's `parentColumn` against the other tabs' `table.name`
- [ ] Frontend `WindowPage.tsx` removes `findChildTabs()` function
- [ ] Frontend reads `tab.childTabIds` directly from the API response
- [ ] Backend compiles and existing tests pass

---

# Technical Notes

- In `WindowDefinitionAssemblyService.assembleDefinition()`, after loading all tabs, iterate and for each tab find others whose `parentColumn` references this tab's table
- The current logic in frontend `WindowPage.tsx:234-241` is:
  ```
  parentTable.endsWith('_' + colStub)  // colStub = parentColumn.strip('_id')
  ```
- The backend already has access to the full tab list and table metadata — this is trivial to compute server-side
- Keep the `parentColumn` field on the response for backward compatibility, add `childTabIds` as new field

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/dto/window/TabDefinitionResponse.java` — add `childTabIds` field
- `backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java` — populate childTabIds during assembly
- `frontend/src/routes/window/WindowPage.tsx` — remove `findChildTabs()`, use `tab.childTabIds`
- `frontend/src/core/runtime/api/runtimeApi.ts` — add `childTabIds` to `WindowTabDefinition` interface
