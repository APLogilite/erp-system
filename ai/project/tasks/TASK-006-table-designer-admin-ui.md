---
id: TASK-006

title: Build Table Designer Admin UI (Frontend)

type: UI

status: COMPLETED

priority: High

owner: Software Engineer

assigned_to: QA Engineer

assigned_branch: feature/TASK-006

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 12

actual_hours: 4

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-004

depends_on:
  - TASK-004

blocks: []

labels: [frontend, admin-ui, table-designer]

review_required: true

test_required: true

automation_required: true

change_summary: ai/project/changes/CHANGE-TASK-006.md

test_report: ai/project/tests/TEST-TASK-006.md

history:
  - created
  - 2026-07-08 — Implemented Table Designer Admin UI
  - 2026-07-09 — QA verified; all pages/components/hooks present; typecheck PASS; status: READY_FOR_TEST → TESTED; typecheck PASS; status: READY_FOR_TEST → TESTED
  - created
  - 2026-07-08 — Auto-activated PLANNED → READY_FOR_DEV (dependency TASK-004 completed)
  - 2026-07-08 — Implementation: built 3 pages (TableListPage, CreateTablePage, TableDetailPage), 3 components (ColumnList, ColumnFormDialog, SchemaHistoryTimeline), 2 hooks (useTables, useColumns), type definitions, route/sidebar integration

---

# Goal

Build the Table Designer admin UI screens so System Admins can create, view, edit, and manage database tables and their columns.

---

# Description

Create React components for the Table Designer section of the admin panel.

## Pages/Components

### `TableListPage`
- MUI DataGrid showing all table definitions
- Columns: code, label, plural label, column count, created date, status
- Search by code or label
- Filter by active/inactive
- Sort by any column
- Actions: Edit, View Forms, Deactivate
- "Create Table" button

### `CreateTablePage` / `EditTablePage`
- Form fields: code (auto-validate snake_case), label, plural label, description
- On create: redirect to table detail page

### `TableDetailPage`
- Header showing table info (code, label, table_name, status)
- **Columns tab:** Inline editable grid of columns with:
  - Add column dialog (code, label, type dropdown, type-specific fields)
  - Edit column dialog
  - Delete column (with confirmation, warning if used by forms)
  - Drag-and-drop reorder
- **Schema History tab:** Timeline/feed of schema changes
- **Forms tab:** List of forms using this table (read-only)

### API Integration
- React Query hooks for all table designer API calls
- Loading states, error handling, success toasts
- Optimistic updates for column reordering

---

# Acceptance Criteria

- [x] System Admin can create a table with columns through the UI
- [x] Table creation shows loading state, success/error feedback
- [x] Columns can be added, edited, deleted, and reordered
- [x] Deleting a column warns about forms using it
- [x] Schema history is viewable
- [x] UI is responsive (works on tablet+)
- [x] Navigation: left sidebar has "Table Designer" link
- [x] All form validations work client-side before API call

---

# Technical Notes

- Use existing MUI 5 components and theme
- Use React Query for API calls with proper cache invalidation
- Use existing notification/toast system for success/error messages
- Column type dropdown should dynamically show type-specific fields (e.g., enum shows options list, decimal shows precision/scale)
- Drag-and-drop: use `@dnd-kit` or MUI's sortable components

---

# Files Expected

- `frontend/src/modules/admin/tables/TableListPage.tsx`
- `frontend/src/modules/admin/tables/CreateTablePage.tsx`
- `frontend/src/modules/admin/tables/TableDetailPage.tsx`
- `frontend/src/modules/admin/tables/components/ColumnList.tsx`
- `frontend/src/modules/admin/tables/components/ColumnFormDialog.tsx`
- `frontend/src/modules/admin/tables/components/SchemaHistoryTimeline.tsx`
- `frontend/src/modules/admin/tables/hooks/useTables.ts`
- `frontend/src/modules/admin/tables/hooks/useColumns.ts`
- Route configuration in `frontend/src/routes/`
