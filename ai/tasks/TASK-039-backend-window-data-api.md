---
id: TASK-039

title: Backend — Runtime Window Data API (CRUD Records)

type: API

status: TESTED

priority: Critical

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: feature/TASK-039

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 6

actual_hours: 2

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-037, TASK-038]

blocks: [TASK-041]

labels: [backend, api, runtime, crud]

history:
  - 2026-07-13: Status READY_FOR_DEV → IN_DEVELOPMENT. Assigned to Software Engineer. Started implementation.
  - 2026-07-13: CRUD data API endpoint created. Validation passed. Status → READY_FOR_TEST.
  - 2026-07-14: QA verification completed. 9/9 tests passed, 0 bugs. Status → TESTED.

review_required: true

test_required: true

test_report: ai/tests/TEST-TASK-039.md

change_report: ai/changes/CHANGE-TASK-039.md

---

# Goal

Create the runtime data API endpoints for listing, creating, updating, and deleting records through a window.

---

# Description

Replace the old PRD-001 runtime data endpoints with window-based paths:

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/runtime/windows/{windowName}/records` | Paginated record list |
| GET | `/api/runtime/windows/{windowName}/records/{id}` | Single record with sub-tab data |
| POST | `/api/runtime/windows/{windowName}/records` | Create record |
| PUT | `/api/runtime/windows/{windowName}/records/{id}` | Update record |
| DELETE | `/api/runtime/windows/{windowName}/records/{id}` | Soft-delete record |

## Key behaviors

- **Record list** uses the window's main tab (first tab where `parent_column IS NULL`)
- **Single record** includes: record data + sub-tab child records for each child tab
- **Tab where_clause** is applied automatically (e.g. `order_type = 'sales'` for Sales Orders)
- **Child tabs** use `parent_column = @parentRecordId` + `where_clause` to filter
- **Tenant isolation** is enforced automatically (existing Hibernate @Filter)
- **Field display/readonly logic** is evaluated frontend-side (this endpoint returns raw data)

## Authentication

- Requires authenticated user
- User's role must have access via `sys_window_access`
- 403 if user has no access

---

# Acceptance Criteria

- [ ] List records returns paginated data from the window's main tab table
- [ ] Single record returns record + child records for each sub-tab
- [ ] Create record inserts into the main tab table
- [ ] Update record modifies existing record
- [ ] Delete record soft-deletes
- [ ] Tab where_clause is applied to queries
- [ ] Child tab data uses parent_column FK filter
- [ ] Tenant isolation preserved
- [ ] All endpoints return `ApiResponse<T>` envelope

---

# Technical Notes

- Replace old PRD-001 endpoints: `GET /api/runtime/forms/{formCode}/records`, etc.
- Use the existing `DynamicCRUDService` or create a new `WindowDataService`
- Query the physical table names via `SysTab → SysTable → table_name`
- For child tab data, resolve the FK column from `parent_column` and filter by parent record ID
