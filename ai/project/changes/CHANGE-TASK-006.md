---
id: CHANGE-TASK-006

task_id: TASK-006

parent_prd: PRD-001

branch: feature/TASK-006

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 4h

related_commits: []

related_files:
  - frontend/src/modules/admin/tables/TableListPage.tsx
  - frontend/src/modules/admin/tables/CreateTablePage.tsx
  - frontend/src/modules/admin/tables/TableDetailPage.tsx
  - frontend/src/modules/admin/tables/components/ColumnList.tsx
  - frontend/src/modules/admin/tables/components/ColumnFormDialog.tsx
  - frontend/src/modules/admin/tables/components/SchemaHistoryTimeline.tsx
  - frontend/src/modules/admin/tables/hooks/useTables.ts
  - frontend/src/modules/admin/tables/hooks/useColumns.ts
  - frontend/src/modules/admin/tables/types.ts
  - frontend/src/core/api/endpoints.ts
  - frontend/src/routes/AppRoutes.tsx
  - frontend/src/components/layouts/Sidebar/Sidebar.tsx

review_required: true

test_required: true

---

# Summary

Built the complete Table Designer Admin UI frontend with 3 pages (TableListPage, CreateTablePage, TableDetailPage), 3 reusable components (ColumnList, ColumnFormDialog, SchemaHistoryTimeline), and 2 React Query hooks (useTables, useColumns). Integrated routes under `/app/admin/tables` and added sidebar navigation. The UI allows System Admins to create, view, edit, and deactivate table definitions, manage columns (add, edit, delete, reorder), and view schema change history.

---

# Business Requirements Implemented

- FR-001: Create Table Definition — CreateTablePage with snake_case validation, inline column addition, redirect to detail page
- FR-002: Manage Table Columns — ColumnList with add/edit/delete/reorder, ColumnFormDialog with dynamic type-specific fields
- FR-003: View Available Tables — TableListPage with search, deactivate, navigate to detail
- FR-004: Deactivate Table — Delete button on list with confirmation dialog
- FR-005: View Table Schema History — SchemaHistoryTimeline component on TableDetailPage

---

# Files Added

| File | Purpose |
|------|---------|
| `frontend/src/modules/admin/tables/TableListPage.tsx` | List all table definitions with search, create, deactivate actions |
| `frontend/src/modules/admin/tables/CreateTablePage.tsx` | Create a new table definition with snake_case validation and inline column addition |
| `frontend/src/modules/admin/tables/TableDetailPage.tsx` | View table details with tabs: Columns, Schema History, Forms |
| `frontend/src/modules/admin/tables/components/ColumnList.tsx` | Inline table of columns with add/edit/delete/reorder controls |
| `frontend/src/modules/admin/tables/components/ColumnFormDialog.tsx` | Dialog for add/edit column with dynamic type-specific fields |
| `frontend/src/modules/admin/tables/components/SchemaHistoryTimeline.tsx` | Timeline view of schema change history |
| `frontend/src/modules/admin/tables/hooks/useTables.ts` | React Query hooks for table CRUD API calls |
| `frontend/src/modules/admin/tables/hooks/useColumns.ts` | React Query hooks for column CRUD API calls |
| `frontend/src/modules/admin/tables/types.ts` | TypeScript type definitions for tables, columns, payloads |

---

# Files Modified

| File | Summary |
|------|---------|
| `frontend/src/core/api/endpoints.ts` | Added `metadata.tables` endpoints (base, detail, columns, column, reorder, history) |
| `frontend/src/routes/AppRoutes.tsx` | Added 3 routes under `/app/admin`: `tables`, `tables/create`, `tables/:tableId` |
| `frontend/src/components/layouts/Sidebar/Sidebar.tsx` | Added "Table Designer" navigation item with TableChart icon under admin section |

---

# Files Removed

None

---

# Database Changes

None (frontend only)

---

# API Changes

None (consumes existing TASK-004 backend APIs)

---

# Routes

## Added

- `/app/admin/tables` — TableListPage (list all tables)
- `/app/admin/tables/create` — CreateTablePage (create new table)
- `/app/admin/tables/:tableId` — TableDetailPage (table detail with tabs)

## Updated

None

## Removed

None

---

# Classes Added

| Class | Purpose |
|--------|---------|
| TableListPage | React functional component for table listing |
| CreateTablePage | React functional component for table creation |
| TableDetailPage | React functional component for table detail view |
| ColumnList | React functional component for column list management |
| ColumnFormDialog | React functional component for column add/edit form |
| SchemaHistoryTimeline | React functional component for history timeline |

---

# Classes Updated

| Class | Summary |
|--------|---------|
| (none) | |

---

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| useTables | useTableList | Fetch paginated table list with search |
| useTables | useTable | Fetch single table by ID |
| useTables | useTableHistory | Fetch schema change history |
| useTables | useCreateTable | Create new table definition |
| useTables | useUpdateTable | Update table metadata |
| useTables | useDeleteTable | Soft-delete table definition |
| useColumns | useAddColumn | Add column to table |
| useColumns | useUpdateColumn | Update column metadata |
| useColumns | useDeleteColumn | Delete column |
| useColumns | useReorderColumns | Reorder columns by position |

---

# Methods Updated

None

---

# Models

None (frontend only)

---

# Services

None (frontend only)

---

# Repositories

None (frontend only)

---

# DTOs

None (frontend only - uses TypeScript types in types.ts)

---

# Requests

See API Changes above

---

# Policies

None

---

# Events

None

---

# Jobs

None

---

# Configuration

None

---

# Dependencies

None new. Uses existing: React 18, React Router v6, MUI 5, React Query, Zustand.

---

# Validation

## Build

PASS — `pnpm build` (typecheck + vite build) completed successfully

## Lint

PASS — 0 errors from new files. 3 pre-existing errors in TASK-020 files (DynamicFormRenderer, FormFieldRenderer, FormSection).

## Static Analysis

N/A

## Existing Automated Tests

N/A (frontend has no test framework per AGENTS.md)

---

# Manual Verification

- [x] TypeScript typecheck passes with zero errors
- [x] Vite production build completes successfully
- [x] All pages, components, and hooks compile without TypeScript errors
- [x] Prettier formatting applied
- [x] ESLint shows only pre-existing errors (not from this task)
- [x] Snake_case validation works client-side via SNAKE_CASE_REGEX
- [x] Column type dropdown shows dynamic type-specific fields (maxLength, precision/scale, relationTable, enumOptions)
- [x] Route navigation structure correct (list → create → detail)

---

# Breaking Changes

None. All changes are additive — new pages, new routes, new sidebar item.

---

# Known Issues

- Column reordering uses up/down arrows instead of drag-and-drop (drag-and-drop deferred to future iteration)
- Forms tab on TableDetailPage shows placeholder message (requires FormFieldRepository integration)
- Delete column warning is static confirmation dialog; doesn't dynamically check actual form usage
- No server-side pagination in table list (loads up to 50 records)

---

# Future Improvements

- Drag-and-drop column reordering using @dnd-kit
- Dynamic form-usage check on column delete
- EditTablePage for editing table metadata after creation
- Server-side pagination with page controls
- Column reorder via drag-and-drop
- Forms tab showing actual form references

---

# Developer Notes

- Uses existing `notifyActions` for toast notifications (success/error)
- Uses existing `useQuery`/`useMutation` from React Query for API calls
- Cache invalidation on mutations invalidates `['admin', 'tables']` query key
- ColumnFormDialog shows type-specific fields dynamically based on selected column type
- Physical table name auto-generated as `t_{code}` from user-entered code
- Column code is immutable after creation (disabled in edit mode)
- Schema history uses a simplified custom timeline (no @mui/lab dependency needed)

---

# QA Handoff

Suggested test focus:
1. Navigate to Table Designer from sidebar (admin user)
2. Create a table with snake_case code, labels, description, and multiple columns
3. Verify table appears in list with correct info
4. Navigate to table detail, add/edit/delete columns
5. Verify column reorder works (up/down arrows)
6. Check schema history tab shows changes
7. Deactivate a table from the list
8. Verify search filters tables by code/label
9. Test snake_case validation rejects invalid codes
10. Test column type-specific fields appear correctly (decimal shows precision/scale, many2one shows relation field, enum shows JSON editor)

Potential risk areas:
- API endpoints need TASK-004 backend running with SYSTEM_ADMIN role
- TableDetailPage depends on tableId URL param - verify 404 handling
- Reorder uses mutate without optimistic update (may feel laggy on slow connections)

---

# Related Documents

Task: ai/project/tasks/TASK-006-table-designer-admin-ui.md

PRD: ai/project/prd/PRD-001-dynamic-form-configuration-system.md

