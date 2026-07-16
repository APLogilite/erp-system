---
id: CHANGE-BUG-008

task_id: BUG-008

parent_prd: PRD-004

branch: bugfix/BUG-008

type: Bug

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-14

completed: 2026-07-14

duration: 1 session

related_commits:
  - c4eee13 (lock BUG-008)
  - (implementation commit to follow)

related_files:
  - backend/src/main/java/com/erp/modules/metadata/repository/SysWindowAccessRepository.java
  - backend/src/main/java/com/erp/core/runtime/controller/WindowDefinitionController.java
  - frontend/src/core/runtime/hooks/useAccessibleForms.ts
  - frontend/src/core/runtime/components/FormSearchBar.tsx
  - frontend/src/core/runtime/components/FormNavigationMenu.tsx
  - ai/project/changes/CHANGE-BUG-008.md

review_required: false

test_required: true

---

# Summary

BUG-008: Ctrl+K search (FormSearchBar) still used the old PRD-001 form metadata schema instead of the new PRD-004 Window schema. Fixed by adding a new backend endpoint (`GET /runtime/windows/accessible`) that queries `sys_window`/`sys_window_access`/`sys_table` tables, and updating the frontend `useAccessibleForms` hook and `FormSearchBar` component to use window names/labels and navigate to `/window/{windowName}` (PRD-004 FR-012 route).

---

# Business Requirements Implemented

- FR-011 (PRD-004): Ctrl+K search now shows window names (e.g. "Sales Orders", "Business Partners") instead of raw form codes or table codes
- FR-012 (PRD-004): Selecting a search result navigates to `/window/{windowName}` instead of old `/runtime/{formCode}`
- Role-based access filtering via `sys_window_access` table (same pattern as existing menu navigation)

---

# Files Added

| File | Purpose |
|------|---------|
| ai/project/changes/CHANGE-BUG-008.md | Change report for BUG-008 |

---

# Files Modified

| File | Summary |
|------|---------|
| backend/.../SysWindowAccessRepository.java | Added `findByRoleIdIn` for batch window-access lookup |
| backend/.../WindowDefinitionController.java | Added `GET /runtime/windows/accessible` endpoint |
| frontend/.../useAccessibleForms.ts | Updated to call new window endpoint with new interface |
| frontend/.../FormSearchBar.tsx | Updated to display windows, navigate to `/window/` routes |
| frontend/.../FormNavigationMenu.tsx | Updated dead code to use new interface (kept for compatibility) |

---

# Files Removed

None

---

# Database Changes

None

## Migrations

None (uses existing PRD-004 tables: sys_window, sys_window_access, sys_table)

---

# API Changes

## New Endpoints

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/v1/runtime/windows/accessible` | Returns accessible windows for the current user |

### Response format
```json
{
  "success": true,
  "data": [
    {
      "windowId": "uuid",
      "windowName": "sales_order",
      "windowLabel": "Sales Orders",
      "tableName": "md_sales_order",
      "tableLabel": "Sales Order"
    }
  ],
  "message": "Accessible windows retrieved. Total: X"
}
```

---

# Routes

## Updated

- Ctrl+K search now navigates to `/window/{windowName}` (relative) → resolves to `/app/window/{windowName}`

---

# Configuration

None

---

# Validation

## Build

PASS — `mvn clean compile` succeeds (backend)

## Lint

PASS (pre-existing warnings only, 0 new issues)

## Typecheck

PASS — `tsc --noEmit` succeeds (frontend)

## Existing Automated Tests

PASS — All 36 backend tests pass

---

# Manual Verification

- Backend compiles and all 36 tests pass
- Frontend typecheck passes
- New endpoint implementation follows same RBAC pattern as existing code

---

# Breaking Changes

- `useAccessibleForms` hook now returns windows with different field names (`windowName`/`windowLabel`/`tableName`/`tableLabel` instead of `formCode`/`formLabel`/`modelName`/`modelLabel`). Any code still using the old field names would break (only `FormNavigationMenu.tsx` was affected — now fixed).
- The old `GET /runtime/forms` endpoint still exists for backward compatibility but is no longer called by the search bar.

---

# Known Issues

None

---

# Future Improvements

Consider adding menu hierarchy context (e.g., which menu tree the window belongs to) to search results so users can see the navigation path.

---

# Developer Notes

- The `FormNavigationMenu.tsx` component was dead code (not imported anywhere, replaced by `MenuNavigation.tsx` in the sidebar), but was updated to prevent TypeScript compilation errors and kept for potential future use.
- The endpoint implements the same access control pattern as the menu system: sys_admin bypasses all checks, regular users must have matching `sys_window_access` entries.

---

# QA Handoff

- Verify Ctrl+K shows window names instead of old form names
- Verify selecting a result navigates to `/window/{windowName}`
- Verify old `/runtime/` routes are not suggested
- Verify sys_admin sees all windows in search
- Verify restricted users only see windows they have access to
- Verify all 36 backend tests pass
- Verify frontend typecheck passes

---

# Related Documents

- BUG-008: Ctrl+K search not updated to Window schema
- PRD-004: FR-011 (Show user-friendly labels), FR-012 (Route change to /window/)
- TASK-041: Frontend Routing + WindowPage
- CHANGE-TASK-041: Known Limitation — "Ctrl+K search still uses old /runtime paths"
