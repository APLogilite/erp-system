---
id: TASK-048

title: Backend Pre-Filters and Pre-Sorts Fields

type: Feature

scope: backend

status: READY_FOR_DEV

priority: Medium

owner: developer

assigned_to:

assigned_branch:

locked: false

created: 2026-07-16

updated: 2026-07-17

started:

completed:

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
  - TASK-047

depends_on: []

blocks: []

labels:
  - backend
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary:

test_report:

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)

---

# Goal

Backend returns fields pre-filtered (no hidden fields) and pre-sorted, so the frontend renders what it receives without additional processing.

---

# Description

Currently the frontend filters out `isDisplayed=false` fields and sorts by `seqNo` in multiple places:
- `WindowPage.tsx:52-54` — `getDisplayedFields()` filters by `isDisplayed !== false` and sorts by `seqNo`
- `DynamicListView.tsx:71-74` — filters by `visible`, sorts by `position`, slices to 8

The backend already sorts fields by `seq_no` in `WindowDefinitionAssemblyService.java:109`. It should also exclude non-displayed fields. The frontend should render whatever fields it receives without additional filtering or sorting.

---

# Acceptance Criteria

- [ ] `WindowDefinitionAssemblyService` excludes fields where `isDisplayed=false` from the response
- [ ] Frontend `WindowPage.tsx` removes `getDisplayedFields()` function, uses fields directly from API
- [ ] Frontend `DynamicListView.tsx` removes field filtering (`filter((f) => f.visible)`), sorting (`.sort((a, b) => a.position - b.position)`), and slicing (`.slice(0, 8)`)
- [ ] Backend compiles and existing tests pass

---

# Technical Notes

- `WindowDefinitionAssemblyService.assembleTab()` already loads fields via `fieldService.findByTabIdOrderBySeqNoAsc()` — the order is correct
- Add a `.filter(f -> f.getIsDisplayed() != Boolean.FALSE)` before or during field assembly
- For the list view, the backend already sends fields — the frontend doesn't need to limit to 8

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java` — filter out non-displayed fields
- `frontend/src/routes/window/WindowPage.tsx` — remove `getDisplayedFields()`
- `frontend/src/core/runtime/components/DynamicListView.tsx` — remove field filter/sort/slice
