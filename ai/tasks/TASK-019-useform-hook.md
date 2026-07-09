---
id: TASK-019

title: Build useForm() Hook with Two-Request Pattern & Caching (Frontend)

type: Feature

status: TESTED

priority: Critical

owner: developer

assigned_to: QA Engineer

assigned_branch: feature/TASK-019

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 6

actual_hours: 2

completed:

estimated_hours: 6

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-016
  - TASK-017

depends_on:
  - TASK-016
  - TASK-017

blocks:
  - TASK-020
  - TASK-021
  - TASK-022
  - TASK-023
  - TASK-024
  - TASK-025
  - TASK-026
  - TASK-027

labels: [frontend, hook, caching, runtime]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-019.md

test_report: ai/tests/TEST-TASK-019.md

history:
  - created
  - 2026-07-08 — Developer: Cascade-activated from PLANNED to READY_FOR_DEV (dependencies TASK-016, TASK-017 both READY_FOR_TEST). Locked task, created feature/TASK-019 branch.
  - 2026-07-08 — Developer: Created useForm.types.ts (FormDefinition, RecordEntry, ListRecordsResponse, SingleRecordResponse, UseFormOptions, UseFormResult). Created runtimeApi.ts (API client with fetchFormDefinition, fetchRecords, fetchRecord, createRecord, updateRecord, deleteRecord). Created useForm.ts (two-request hook with React Query, separate list/single record queries, mutations with auto-invalidation, getRecordLabel helper). Created barrel export index.ts. TypeScript typecheck passes. ESLint passes. Task marked READY_FOR_TEST.

---

# Goal

Build the central `useForm()` React hook that abstracts the two-request pattern (definition cached + data fresh) and provides a unified interface for all runtime form components.

---

# Description

Create the `useForm()` hook in `frontend/src/core/runtime/hooks/`.

## Hook API

```typescript
function useForm(formCode: string, options?: UseFormOptions): UseFormResult

interface UseFormOptions {
  recordId?: string;       // If set, fetches single record instead of list
  page?: number;           // For list view pagination
  pageSize?: number;       // Default 20
  sortField?: string;
  sortDir?: 'asc' | 'desc';
}

interface UseFormResult {
  // Form definition (cached)
  formDefinition: FormDefinition | undefined;
  isLoadingDefinition: boolean;
  definitionError: Error | null;

  // Record data (fresh)
  records: Record[] | undefined;          // List view
  record: Record | undefined;             // Single record view
  subFormRecords: Record<string, Record[]> | undefined; // Keyed by sub-form code
  breadcrumb: BreadcrumbEntry[] | undefined;
  parent: ParentContext | undefined;
  isLoadingData: boolean;
  dataError: Error | null;

  // Combined state
  isLoading: boolean;
  error: Error | null;

  // Actions
  createRecord: (data: RecordData) => Promise<Record>;
  updateRecord: (id: string, data: RecordData) => Promise<Record>;
  deleteRecord: (id: string) => Promise<void>;
  refreshData: () => void;
  invalidateDefinition: () => void;
}
```

## Implementation

### Request 1: Form Definition (cached)
- React Query `useQuery` with `queryKey: ['form-definition', formCode]`
- `staleTime: 5 * 60 * 1000` (5 minutes)
- `cacheTime: 30 * 60 * 1000` (30 minutes in cache)
- Called from `GET /api/runtime/forms/{formCode}/definition`

### Request 2: Record Data (fresh)
- React Query `useQuery` with `queryKey: ['form-data', formCode, recordId, page]`
- `staleTime: 0` (always fresh)
- Called from `GET /api/runtime/forms/{formCode}/records` or `.../records/{id}`

### Mutations
- `createRecord`: `POST /api/runtime/forms/{formCode}/records`
- `updateRecord`: `PUT /api/runtime/forms/{formCode}/records/{id}`
- `deleteRecord`: `DELETE /api/runtime/forms/{formCode}/records/{id}`
- All mutations invalidate the data query on success

### Formatted Label
- Provide a helper `getRecordLabel(record, formDefinition)` that returns a display label

---

# Acceptance Criteria

- [x] `useForm()` returns form definition (cached) and data (fresh) in a single unified result
- [x] Definition is not re-fetched when navigating between records in the same form
- [x] Data is always re-fetched on navigation
- [x] Mutations (create/update/delete) invalidate the data query
- [x] Loading state shows when either request is in flight
- [x] Error state captures both definition and data errors
- [x] `refreshData()` refetches data only
- [x] `invalidateDefinition()` refetches form definition (call after admin saves form config)

---

# Technical Notes

- Use existing axios instance with JWT interceptor
- Use existing React Query configuration
- The hook lives in `frontend/src/core/runtime/hooks/useForm.ts`
- TypeScript interfaces for FormDefinition, Record, BreadcrumbEntry should be defined in a types file

---

# Files Expected

- `frontend/src/core/runtime/hooks/useForm.ts`
- `frontend/src/core/runtime/hooks/useForm.types.ts`
- `frontend/src/core/runtime/api/runtimeApi.ts` — API client functions
