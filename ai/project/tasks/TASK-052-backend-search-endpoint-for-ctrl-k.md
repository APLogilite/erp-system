---
id: TASK-052

title: Backend Search Endpoint for Ctrl+K

type: Feature

scope: both

status: IN_DEVELOPMENT

priority: Medium

owner: developer

assigned_to:

assigned_branch:

locked: true

created: 2026-07-16

updated: 2026-07-17

started: 2026-07-17

completed:

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

change_summary:

test_report:

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)

---

# Goal

Add a backend search endpoint for Ctrl+K window search, removing the client-side filtering logic from `FormSearchBar.tsx`.

---

# Description

`FormSearchBar.tsx` currently fetches ALL accessible windows (`fetchAccessibleForms()`) and filters them client-side by `windowLabel`, `windowName`, and `tableLabel`. This is inefficient — the backend should accept a search query and return matching results, including parent menu path for context.

---

# Acceptance Criteria

- [ ] New endpoint: `GET /api/v1/runtime/windows/search?q={query}`
- [ ] Returns: `[{ windowId, windowName, windowLabel, tableName, tableLabel, menuPath }]`
- [ ] Searches across `windowLabel`, `windowName`, `tableLabel`
- [ ] Results are ordered by relevance and limited to 20
- [ ] Frontend `FormSearchBar.tsx` sends query to backend instead of client-side filtering
- [ ] Frontend removes manual `filter()` logic
- [ ] Backend compiles and existing tests pass

---

# Technical Notes

- Search across `sys_window.name`, `sys_window.description`, `sys_table.name`, `sys_table.label`
- Use `WHERE` with `ILIKE '%query%'` for PostgreSQL
- `menuPath` should be assembled by walking the `sys_menu` tree up to root (e.g., "Master Data > Product")
- The existing `fetchAccessibleForms()` can remain for the initial list, but the search functionality should use the new endpoint

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/controller/WindowDataController.java` — add search endpoint
- `backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java` — add search query logic
- `frontend/src/core/runtime/components/FormSearchBar.tsx` — replace client filter with API search
- `frontend/src/core/runtime/api/runtimeApi.ts` — add `searchWindows()` function
