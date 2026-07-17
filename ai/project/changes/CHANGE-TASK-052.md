---
id: CHANGE-TASK-052

task_id: TASK-052

parent_prd: PRD-005

branch: feature/TASK-052

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 2 hours (estimated)

related_commits:
  - feat(TASK-052): add backend search endpoint for Ctrl+K, update FormSearchBar

related_files:
  - backend/src/main/java/com/erp/core/runtime/controller/WindowDataController.java
  - backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java
  - frontend/src/core/runtime/api/runtimeApi.ts
  - frontend/src/core/runtime/components/FormSearchBar.tsx

review_required: true

test_required: true

---

# Summary

Added a backend search endpoint `GET /api/v1/runtime/windows/search?q={query}` that searches windows by name, description, and table name/label, returning up to 20 results with menu path context. The frontend `FormSearchBar.tsx` was updated to use the new endpoint via React Query instead of client-side filtering. Results now include `menuPath` for better context.

---

# Scope Verification

- [x] Frontend
- [x] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-007: Backend Search Endpoint for Ctrl+K
  - New endpoint: `GET /api/v1/runtime/windows/search?q={query}`
  - Returns: `[{ windowId, windowName, windowLabel, tableName, tableLabel, menuPath }]`
  - Searches across `windowLabel`, `windowName`, `tableName`, `tableLabel`
  - Results ordered by name, limited to 20
  - Frontend `FormSearchBar.tsx` uses API search instead of client-side filtering
  - Minimum 2 characters to trigger search

---

# Files Added

None.

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/.../WindowDataController.java` | Added `GET /search` endpoint with @RequestParam q |
| `backend/.../WindowDataService.java` | Added `searchWindows()` method querying sys_window + sys_table + sys_menu |
| `frontend/.../runtimeApi.ts` | Added `searchWindows()` function and `WindowSearchResult` interface |
| `frontend/.../FormSearchBar.tsx` | Replaced client-side filter with React Query backend search |

---

# Validation

## Build

PASS — Backend `mvn clean compile` succeeds; Frontend `tsc --noEmit` succeeds

---

## Existing Automated Tests

PASS — All 36 backend tests pass
