---
id: TASK-019

title: Build useForm() Hook with Two-Request Pattern & Caching (Frontend)

type: Feature

status: PLANNING

priority: Critical

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 6

actual_hours:

parent_prd: PRD-001

prd_version: 1.5.0

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

change_summary:

test_report:

history:
  - created

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

- [ ] `useForm()` returns form definition (cached) and data (fresh) in a single unified result
- [ ] Definition is not re-fetched when navigating between records in the same form
- [ ] Data is always re-fetched on navigation
- [ ] Mutations (create/update/delete) invalidate the data query
- [ ] Loading state shows when either request is in flight
- [ ] Error state captures both definition and data errors
- [ ] `refreshData()` refetches data only
- [ ] `invalidateDefinition()` refetches form definition (call after admin saves form config)

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
