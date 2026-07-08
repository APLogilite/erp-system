---
document: CHANGE_REPORT
task: TASK-019
status: COMPLETE
created: 2026-07-08
---

# Change Report — TASK-019

## Summary

Built the `useForm()` React hook implementing the two-request pattern (definition cached + data fresh) with React Query. Created companion API client functions and TypeScript type definitions. This hook is the central data-fetching abstraction for all runtime form components.

## Files Added

| File | Description |
|------|-------------|
| `frontend/src/core/runtime/hooks/useForm.types.ts` | TypeScript interfaces: `FormDefinition`, `FieldDefinition`, `FieldRule`, `FieldValidation`, `LayoutSection`, `SubFormDefinition`, `RecordEntry`, `RecordData`, `BreadcrumbEntry`, `ParentContext`, `ListRecordsResponse`, `SingleRecordResponse`, `UseFormOptions`, `UseFormResult`, `ApiResponse` |
| `frontend/src/core/runtime/api/runtimeApi.ts` | API client functions: `fetchFormDefinition`, `fetchRecords`, `fetchRecord`, `fetchAccessibleForms`, `createRecord`, `updateRecord`, `deleteRecord`. All unwrap the `ApiResponse<T>` envelope. |
| `frontend/src/core/runtime/hooks/useForm.ts` | Main `useForm()` hook with two-request pattern, mutations, `getRecordLabel()` helper |
| `frontend/src/core/runtime/hooks/index.ts` | Barrel export |

## Files Modified

None.

## Validation Results

| Check | Result |
|-------|--------|
| `tsc --noEmit` (frontend) | PASS |
| `eslint --max-warnings=0` (TASK-019 files) | PASS (pre-existing errors in unrelated files unchanged) |

## Acceptance Criteria

- [x] `useForm()` returns form definition (cached) and data (fresh) in a single unified result
- [x] Definition is not re-fetched when navigating between records in the same form (staleTime: 5 min, gcTime: 30 min)
- [x] Data is always re-fetched on navigation (staleTime: 0 for data queries)
- [x] Mutations (create/update/delete) invalidate the data query
- [x] Loading state shows when either request is in flight
- [x] Error state captures both definition and data errors
- [x] `refreshData()` refetches data only (invalidates form-data query keys)
- [x] `invalidateDefinition()` refetches form definition (invalidates form-definition query keys)

## Key Implementation Decisions

1. **Separate list/single queries**: Instead of conditionally switching between list and single record query functions, the hook uses two separate `useQuery` calls — one enabled only when `recordId` is not set, one enabled only when `recordId` is set. This avoids TypeScript union type issues with React Query's typed `useQuery`.

2. **Axios-based API client**: Uses the existing `apiClient` (axios instance with JWT interceptor) rather than plain `fetch`. This ensures authentication headers and error handling work consistently.

3. **React Query caching strategy**:
   - Definition: `staleTime: 5 min, gcTime: 30 min` — matches the backend `Cache-Control: max-age=300`
   - Data: `staleTime: 0` — always re-fetched, matching the "always fresh" requirement
   - Mutations invalidate only the data query (`['form-data', formCode]`), not the definition

4. **`getRecordLabel()` helper**: Exported separately so components can display record labels in breadcrumbs and navigation without coupling to the hook.

## Known Limitations

1. **Pagination**: The `setPage()` callback invalidates the query but does not directly set page state. Components should manage page state externally and pass it to `useForm()` via options.

2. **Sort direction type**: Backend accepts `asc`/`desc` as string; typed as string union in `UseFormOptions`.

3. **Pre-existing lint errors**: 13 pre-existing ESLint errors in unrelated files (ContextSelectPage.tsx, etc.) are unchanged by this task.

## Breaking Changes

None. This is a new module with no existing consumers.

## Follow-up Recommendations

- **TASK-020**: Build `DynamicFormRenderer` component consuming `useForm()`
- **TASK-021**: Build client-side rules engine consuming form definition + field rules
- **TASK-024**: Build breadcrumb navigation using `getRecordLabel()` and breadcrumb data
