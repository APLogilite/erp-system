---
id: CHANGE-TASK-019

task_id: TASK-019

parent_prd: PRD-001

branch: feature/TASK-019

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 3h

related_commits: []

related_files:
  - frontend/src/core/runtime/hooks/useForm.ts
  - frontend/src/core/runtime/hooks/useForm.types.ts
  - frontend/src/core/runtime/api/runtimeApi.ts
  - frontend/src/core/runtime/hooks/index.ts

review_required: true

test_required: true

---

# Summary

Built the `useForm()` React hook implementing the two-request pattern (definition cached + data fresh) with React Query. Created companion API client functions and TypeScript type definitions. This hook is the central data-fetching abstraction for all runtime form components.

---

# Business Requirements Implemented

- FR-015: useForm() Hook — two-request loading pattern (definition cached, data fresh)
- FR-016: Form Data Management — mutations (create/update/delete) with query invalidation
- Two-request pattern: `fetchFormDefinition` (cached, staleTime: 5 min) + `fetchRecords`/`fetchRecord` (always fresh, staleTime: 0)
- Mutation-driven invalidation: create/update/delete invalidate data query keys
- Loading/error states for both definition and data

---

# Files Added

| File | Purpose |
|------|---------|
| `frontend/src/core/runtime/hooks/useForm.types.ts` | TypeScript interfaces: `FormDefinition`, `FieldDefinition`, `FieldRule`, `FieldValidation`, `LayoutSection`, `SubFormDefinition`, `RecordEntry`, `RecordData`, `BreadcrumbEntry`, `ParentContext`, `ListRecordsResponse`, `SingleRecordResponse`, `UseFormOptions`, `UseFormResult`, `ApiResponse` |
| `frontend/src/core/runtime/api/runtimeApi.ts` | API client functions: `fetchFormDefinition`, `fetchRecords`, `fetchRecord`, `fetchAccessibleForms`, `createRecord`, `updateRecord`, `deleteRecord`. All unwrap the `ApiResponse<T>` envelope. |
| `frontend/src/core/runtime/hooks/useForm.ts` | Main `useForm()` hook with two-request pattern, mutations, `getRecordLabel()` helper |
| `frontend/src/core/runtime/hooks/index.ts` | Barrel export |

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

None (consumes existing backend APIs)

---

# Routes

None

---

# Classes Added

None (React hooks and API functions)

---

# Classes Updated

None

---

# Methods Added

| Module | Function | Purpose |
|--------|----------|---------|
| runtimeApi.ts | fetchFormDefinition | GET /api/runtime/forms/{formCode}/definition |
| runtimeApi.ts | fetchRecords | GET /api/runtime/forms/{formCode}/records |
| runtimeApi.ts | fetchRecord | GET /api/runtime/forms/{formCode}/records/{id} |
| runtimeApi.ts | fetchAccessibleForms | GET /api/runtime/forms |
| runtimeApi.ts | createRecord | POST /api/runtime/forms/{formCode}/records |
| runtimeApi.ts | updateRecord | PUT /api/runtime/forms/{formCode}/records/{id} |
| runtimeApi.ts | deleteRecord | DELETE /api/runtime/forms/{formCode}/records/{id} |
| useForm.ts | useForm | Central hook with two-request pattern and mutations |
| useForm.ts | getRecordLabel | Helper to derive human-readable record label |

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

Added (TypeScript interfaces): FormDefinition, FieldDefinition, FieldRule, FieldValidation, LayoutSection, SubFormDefinition, RecordEntry, RecordData, BreadcrumbEntry, ParentContext, ListRecordsResponse, SingleRecordResponse, UseFormOptions, UseFormResult

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

Uses existing: `@tanstack/react-query`, `@/core/api/client` (axios instance with JWT interceptor)

---

# Validation

## Build

PASS — `tsc --noEmit` (frontend, 0 errors)

## Lint

PASS — `eslint --max-warnings=0` on TASK-019 files (pre-existing errors in unrelated files unchanged)

## Static Analysis

N/A

## Existing Automated Tests

N/A (frontend — no test framework)

---

# Manual Verification

- [x] TypeScript compilation succeeds
- [x] useForm() exports correctly for consumer components
- [x] API client unwraps ApiResponse<T> envelope correctly
- [x] Query key conventions are consistent (form-definition, form-data)
- [x] Mutation invalidation targets correct query keys

---

# Breaking Changes

None. New module with no existing consumers.

---

# Known Issues

1. **Pagination**: `setPage()` callback invalidates the query but does not directly set page state. Components should manage page state externally.
2. **Sort direction type**: Backend accepts `asc`/`desc` as string; typed as string union in `UseFormOptions`.
3. **Pre-existing lint errors**: 13 pre-existing ESLint errors in unrelated files (ContextSelectPage.tsx, etc.) are unchanged by this task.

---

# Future Improvements

- Add optimistic updates for mutations
- Add offline support via React Query persistence

---

# Developer Notes

- **Separate list/single queries**: Uses two separate `useQuery` calls — one enabled only when `recordId` is not set, one enabled only when `recordId` is set. Avoids TypeScript union type issues with React Query's typed `useQuery`.
- **Axios-based API client**: Uses the existing `apiClient` (axios instance with JWT interceptor) rather than plain `fetch`. Ensures authentication headers and error handling work consistently.
- **React Query caching strategy**:
  - Definition: `staleTime: 5 min, gcTime: 30 min` — matches backend `Cache-Control: max-age=300`
  - Data: `staleTime: 0` — always re-fetched, matching "always fresh" requirement
  - Mutations invalidate only the data query (`['form-data', formCode]`), not the definition
- **`getRecordLabel()` helper**: Exported separately so components can display record labels without coupling to the hook.

---

# QA Handoff

Suggested test focus:
1. `useForm()` returns form definition (cached) and data (fresh) in a single unified result
2. Definition is not re-fetched when navigating between records in the same form
3. Data is always re-fetched on navigation
4. Mutations (create/update/delete) invalidate the data query
5. Loading state shows when either request is in flight
6. Error state captures both definition and data errors
7. `refreshData()` refetches data only
8. `invalidateDefinition()` refetches form definition

Potential risk areas:
- Race conditions between definition and data queries
- Cache staleness if backend cache-control headers change

---

# Related Documents

Task: ai/tasks/TASK-019-useform-hook.md

PRD: ai/prd/PRD-001-dynamic-form-configuration-system.md
