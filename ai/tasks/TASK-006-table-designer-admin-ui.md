---
id: TASK-006

title: Build Table Designer Admin UI (Frontend)

type: UI

status: PLANNED

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 12

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0

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

change_summary:

test_report:

history:
  - created

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

- [ ] System Admin can create a table with columns through the UI
- [ ] Table creation shows loading state, success/error feedback
- [ ] Columns can be added, edited, deleted, and reordered
- [ ] Deleting a column warns about forms using it
- [ ] Schema history is viewable
- [ ] UI is responsive (works on tablet+)
- [ ] Navigation: left sidebar has "Table Designer" link
- [ ] All form validations work client-side before API call

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
