---
id: TASK-050

title: Guarantee _display on Every Record

type: Feature

scope: both

status: TESTED

priority: High

owner: developer

assigned_to:

assigned_branch: feature/TASK-050

locked: false

created: 2026-07-16

updated: 2026-07-17

started: 2026-07-17

completed: 2026-07-17

estimated_hours: 3

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
  - both
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary: ai/project/changes/CHANGE-TASK-050.md

test_report:

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: merged to PRD branch, READY_FOR_TEST (SE)

---

# Goal

Backend guarantees `_display` is present on every returned record, so the frontend can use it without fallback scanning logic.

---

# Description

The backend `WindowDataService` already resolves `_display` via the `is_display_column` flag (lines 118-132). However, the resolution is not guaranteed for all records. The frontend has fallback logic:
- `useForm.ts:220-238` — `getRecordLabel()` scans fields for the first non-null non-id value
- `WindowPage.tsx:298-299` — checks `_display`, `name`, `code` as fallbacks

The backend should guarantee that every returned record has a `_display` key. If no display column is configured, fall back to the first non-id column value, or the record's UUID as last resort.

---

# Acceptance Criteria

- [ ] Every record from `fetchWindowRecords()`, `fetchWindowRecord()`, and `fetchTabRecord()` has `_display` key
- [ ] Display column resolution logic: `is_display_column` → first non-id column → `id` UUID as last resort
- [ ] Frontend `useForm.ts` removes `getRecordLabel()` — use `record._display` instead
- [ ] Frontend `WindowPage.tsx:298-299` simplifies to `rec._display`
- [ ] Frontend `DynamicListView.tsx` uses `_display` for FK fields, removes `formatCellValue()`
- [ ] Backend compiles and existing tests pass

---

# Technical Notes

- `WindowDataService` already has `populateDisplayName()` method (line 125) — strengthen it to guarantee a value
- Current logic sets `_display` from `is_display_column` and resolves FK `_display` suffixes — this is correct but needs a guaranteed fallback
- For FK display values, the existing `resolveFkDisplayNames()` already handles this — just ensure it always runs

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java` — strengthen _display guarantee
- `backend/src/main/java/com/erp/core/runtime/service/DynamicCrudService.java` — add _display to list/single responses
- `frontend/src/core/runtime/hooks/useForm.ts` — remove `getRecordLabel()`
- `frontend/src/routes/window/WindowPage.tsx` — simplify display value extraction
- `frontend/src/core/runtime/components/DynamicListView.tsx` — use _display, remove `formatCellValue()`
