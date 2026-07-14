---
id: BUG-008

title: Ctrl+K search still uses old PRD-001 form schema instead of new Window names

status: COMPLETED

priority: High

severity: Medium

owner: QA Engineer

assigned_to: Software Engineer

assigned_branch: bugfix/BUG-008

locked: false

created: 2026-07-14

updated: 2026-07-14

started: 2026-07-14

completed: 2026-07-14

parent_prd: PRD-004

parent_task: TASK-041

reported_by: User

detected_in: Frontend FormSearchBar

related_test: TEST-TASK-041

fix_summary:

verification_report: TEST-BUG-008

history:
  - 2026-07-14 — Product Manager — Created. Search still references old schema.

---

# Summary

PRD-004 FR-011 requires Ctrl+K search to show window names (not raw table codes) and FR-012 requires routes to use `/window/{windowName}`. The `FormSearchBar` component was never updated to use the new Window schema. It still searches the old metadata tables and navigates to old `/runtime/` routes.

---

# Problem

The `FormSearchBar` (built in PRD-001 TASK-025) searches forms using the old metadata schema. It was explicitly listed as a **known limitation** in CHANGE-TASK-041:
> "Ctrl+K search still uses old /runtime paths (needs separate update)"

This was never resolved — the search functionality was not part of TASK-041's scope, so it was left as-is. This violates:
- **FR-011**: "Ctrl+K search shows window names, not table codes"
- **FR-012**: "Route pattern changes from `/runtime/sales_order` to `/window/sales_order`"

---

# Expected Behaviour

- Opening Ctrl+K shows a searchable list of window names (e.g. "Sales Orders", "Business Partners", "Products")
- Selecting a result navigates to `/window/{windowName}`
- No raw table codes or old form names are displayed
- Old `/runtime/*` paths are not suggested

---

# Actual Behaviour

- Ctrl+K search still shows old form names from the PRD-001 metadata schema
- Selecting a result navigates to the old `/runtime/{formCode}` path (which now redirects, but it's still wrong)
- Raw table codes like `md_business_partner` may be visible instead of friendly window names

---

# Steps To Reproduce

1. Start the application
2. Log in as sys_admin
3. Press Ctrl+K
4. Observe search results — shows old form names, not window names
5. Select a result — navigates to old `/runtime/` path

---

# Root Cause

The `FormSearchBar` component and its backing hook `useAccessibleForms` still query the old PRD-001 metadata schema via `GET /runtime/forms` (which queries `sys_metadata_views`). The returned `AccessibleForm` interface used `formCode`, `formLabel`, `modelName` fields from the old schema. Navigation targeted `/app/runtime?form={formCode}` (the old PRD-001 route).

Additionally, the dead-code component `FormNavigationMenu.tsx` still referenced the old `AccessibleForm` interface fields and would fail to compile with the updated interface.

---

# Fix

## Backend
- Added `findByRoleIdIn(List<UUID>)` method to `SysWindowAccessRepository` for batch lookup of window access by role IDs.
- Added `GET /runtime/windows/accessible` endpoint to `WindowDefinitionController` that:
  - Queries `sys_window` for all active windows
  - Filters by role-based access via `sys_window_access`
  - Resolves table labels from `sys_table` via the window's `table_id` FK
  - Returns lightweight entries: `windowId`, `windowName`, `windowLabel`, `tableName`, `tableLabel`

## Frontend
- Updated `useAccessibleForms` hook to call `GET /runtime/windows/accessible` instead of `GET /runtime/forms`
- Updated `AccessibleForm` interface to use new field names: `windowId`, `windowName`, `windowLabel`, `tableName`, `tableLabel`
- Updated `FormSearchBar` component to:
  - Display window labels/names instead of form labels/codes
  - Navigate to `/window/{windowName}` (PRD-004 FR-012 route)
  - Updated search text and tooltip to reference "windows" instead of "forms"
- Updated `FormNavigationMenu` (dead code, kept for compatibility) to use the new interface and navigate to `/window/{windowName}`

---

# Validation

(To be filled by QA Engineer)

After fix:
- [ ] Ctrl+K shows window names only (no raw codes)
- [ ] Selecting a search result navigates to `/window/{windowName}`
- [ ] No old `/runtime/` routes appear in search results
- [ ] Frontend typecheck passes

---

# Files Changed

## Backend
- `backend/src/main/java/com/erp/modules/metadata/repository/SysWindowAccessRepository.java` — Added `findByRoleIdIn` method
- `backend/src/main/java/com/erp/core/runtime/controller/WindowDefinitionController.java` — Added `GET /runtime/windows/accessible` endpoint

## Frontend
- `frontend/src/core/runtime/hooks/useAccessibleForms.ts` — Updated to call new window endpoint with new interface
- `frontend/src/core/runtime/components/FormSearchBar.tsx` — Updated to display windows and navigate to `/window/{windowName}`
- `frontend/src/core/runtime/components/FormNavigationMenu.tsx` — Updated dead code to use new interface

---

# Related Documents

- PRD-004: FR-011 (Show user-friendly labels), FR-012 (Route change to /window/)
- TASK-041: Frontend Routing + WindowPage
- CHANGE-TASK-041: Known Limitation — "Ctrl+K search still uses old /runtime paths"
- BUG-007: PRD-004 Flyway not enabled (prerequisite — data must exist in sys_window/sys_menu before search can work)
