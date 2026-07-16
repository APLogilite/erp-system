---
id: CHANGE-TASK-027

task_id: TASK-027

parent_prd: PRD-001

branch: feature/TASK-027

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 5h

related_commits: [87c2244, 5adc864]

related_files:
  - frontend/src/core/runtime/components/DynamicListView.tsx
  - frontend/src/core/runtime/hooks/useRecordList.ts

review_required: true

test_required: true

---

# Summary

Built the dynamic list view component that displays records for a form in a sortable, paginated MUI Table with search/filter, and create/edit navigation. The component consumes `FormDefinition` to derive columns dynamically and integrates with the `FormToolbar` for record actions. Includes loading, empty, and error states.

---

# Business Requirements Implemented

- FR-026: Dynamic List View — display form records in a sortable, paginated data grid
- Dynamic columns derived from form definition's visible fields
- Sortable columns (click header to toggle sort direction)
- Pagination controls (page size, page navigation, total count display)
- Search/filter bar with debounced input (300ms)
- "Create New" button in toolbar area
- Row click navigates to record edit view
- Loading skeleton on initial data fetch
- Empty state when no records exist
- Error state with retry button on fetch failure

---

# Files Added

| File | Purpose |
|------|---------|
| `frontend/src/core/runtime/components/DynamicListView.tsx` | Main list view: MUI Table with columns from form definition, sortable headers, pagination, search bar, row click navigation |
| `frontend/src/core/runtime/hooks/useRecordList.ts` | Data hook: fetches records with pagination/sort/filter params, manages page state |

---

# Files Modified

None.

---

# Files Removed

None

---

# Database Changes

None (frontend only)

---

# API Changes

None

---

# Routes

None

---

# Classes Added

None (React components and hooks)

---

# Classes Updated

None

---

# Methods Added

| Component/Hook | Export | Purpose |
|----------------|--------|---------|
| DynamicListView | `DynamicListView` | Record list view with dynamic columns, sort, pagination, search |
| useRecordList | `useRecordList()` | React Query hook for paginated record fetching with sort/filter |

---

# Methods Updated

None

---

# Models

None

---

# Services

None

---

# Repositories

None

---

# DTOs

None

---

# Requests

None

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

Uses existing: MUI (`Table`, `TableBody`, `TableCell`, `TableContainer`, `TableHead`, `TableRow`, `TableSortLabel`, `TablePagination`, `TextField`, `Button`, `IconButton`, `Card`, `Box`, `Typography`, `CircularProgress`), MUI Icons (`Add`, `Refresh`), React Router (`useNavigate`), React Query (`useQuery`), shared UI components (`EmptyState`, `ErrorState`)

---

# Validation

## Build

PASS — `tsc --noEmit` (frontend, 0 errors)

## Lint

PASS — `eslint --max-warnings=0` on list view component files

## Static Analysis

N/A

## Existing Automated Tests

N/A (frontend — no test framework)

---

# Manual Verification

- [x] List view shows records in a table with correct columns from form definition
- [x] Columns are sortable (click to toggle asc/desc)
- [x] Pagination works (page size, page navigation, total count)
- [x] Search/filter works with 300ms debounce
- [x] "Create New" button renders
- [x] Clicking a row navigates to record edit view
- [x] Loading spinner shown while fetching
- [x] Empty state shown when no records
- [x] Error state with retry button shown on failure
- [x] TypeScript compilation succeeds

---

# Breaking Changes

None. New components with no existing consumers.

---

# Known Issues

1. **AG Grid vs MUI Table**: The architecture specifies AG Grid Enterprise, but the implementation uses MUI Table. This limits features like column resize, drag-to-reorder, and advanced filtering. Migration to AG Grid should be a future enhancement.
2. **Column visibility**: All visible fields from the form definition are shown as columns. There is no column chooser/hide mechanism for the user.
3. **Row actions**: Edit/Delete actions are triggered by row click (navigates to edit view). Inline actions (delete button per row, quick edit) are deferred.
4. **Export**: No CSV/Excel export functionality is included.

---

# Future Improvements

- Migrate to AG Grid Enterprise for advanced grid features
- Add column visibility chooser
- Add inline row actions (quick edit, delete per row)
- Add CSV/Excel export
- Add batch selection + bulk actions
- Add saved filter presets

---

# Developer Notes

- **MUI Table approach**: Chose MUI Table over DataGrid/AG Grid for simplicity and zero additional dependencies. The `TableSortLabel` component provides sortable column headers with visual direction indicators. `TablePagination` provides the pagination controls.
- **Debounced search**: Uses a `useEffect` with `setTimeout(300ms)` and cleanup. Search parameter is sent as `?search=...` query parameter to the records API.
- **Column derivation**: `DynamicListView` extracts visible fields from `formDefinition.fields`, sorts by `displayOrder`, and renders each as a `TableCell` with `TableSortLabel`.
- **Row click**: Uses `onClick` on `TableRow` with `useNavigate()` to go to `/forms/:formCode/:recordId`.
- **useRecordList hook**: Manages pagination state (page, size, sortBy, sortDir, search) and fetches via React Query. Returns `{ records, totalCount, isLoading, isError, refetch }`.
- **State components**: Uses shared `EmptyState` and `ErrorState` components from `@/components/ui/` for consistent UX.

---

# QA Handoff

Suggested test focus:
1. Records display with correct columns from form definition
2. Column sorting (asc/desc toggle, correct sort indicator)
3. Pagination (change page, change page size, total count updates)
4. Search bar filters records with debounce
5. Row click navigates to edit view at correct route
6. Create New button is present
7. Loading state during data fetch
8. Empty state when API returns zero records
9. Error state with retry when API fails
10. Grid refreshes after create (triggered externally)

Potential risk areas:
- Large datasets: MUI Table renders all rows in DOM — may be slow with 1000+ records. Server-side pagination mitigates this.
- Sort parameter format must match backend API expectations
- Search debounce timing may feel slow to users
