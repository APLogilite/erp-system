---
id: TASK-027

title: Build Dynamic List View with DataGrid (Frontend)

type: UI

status: IN_DEVELOPMENT

priority: High

owner: developer

assigned_to: developer

assigned_branch: feature/TASK-027

locked: true

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 8

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-019
  - TASK-022

depends_on:
  - TASK-019
  - TASK-022

blocks: []

labels: [frontend, component, list, datagrid]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Build the dynamic list view component that displays records for a form with sortable columns, pagination, search, and create/edit/delete actions.

---

# Description

Create `DynamicListView` component in `frontend/src/core/runtime/components/`.

## Component API

```typescript
interface DynamicListViewProps {
  formCode: string;
}
```

## Behavior
- Uses `useForm(formCode)` to get form definition and records
- Renders a DataGrid with:
  - Columns: the visible fields from the form definition (first N fields)
  - Column headers use field labels
  - Rows: records from the data response
  - Sortable columns (click header to sort)
  - Pagination controls (page size, page number, total count)
  - Search/filter bar above the grid
- "Create New" button in the toolbar area
- Clicking a row navigates to the record edit view
- Loading skeleton while data loads
- Empty state when no records exist
- Error state with retry button

## Search/Filter
- Global search input that filters records by any visible column
- Debounced (300ms) to avoid excessive API calls
- Sends filter parameter to backend

## Integration
- Works with `FormToolbar` (TASK-022) for actions
- Integrates with `useForm()` for data fetching and mutations

---

# Acceptance Criteria

- [ ] List view shows records in a DataGrid with correct columns from form definition
- [ ] Columns are sortable
- [ ] Pagination works (page size, page navigation)
- [ ] Search/filter works with debounce
- [ ] "Create New" button opens create form
- [ ] Clicking a row navigates to record edit view
- [ ] Loading skeleton is shown while loading
- [ ] Empty state is shown when no records
- [ ] Error state with retry button is shown on failure
- [ ] Grid refreshes after create/update/delete

---

# Technical Notes

- Use AG Grid Enterprise (preferred per architecture) or MUI DataGrid
- Column definitions are derived from the form definition's visible fields
- Row click navigates via React Router `useNavigate()`
- Pagination uses the backend's page/size/total response

---

# Files Expected

- `frontend/src/core/runtime/components/DynamicListView.tsx`
- `frontend/src/core/runtime/components/DynamicListSkeleton.tsx`
- `frontend/src/core/runtime/components/DynamicListEmptyState.tsx`
- `frontend/src/core/runtime/components/DynamicListErrorState.tsx`
- `frontend/src/core/runtime/hooks/useRecordList.ts`
