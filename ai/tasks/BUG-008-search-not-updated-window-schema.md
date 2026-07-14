---
id: BUG-008

title: Ctrl+K search still uses old PRD-001 form schema instead of new Window names

status: READY_FOR_DEV

priority: High

severity: Medium

owner: Software Engineer

assigned_to:

assigned_branch:

locked: false

created: 2026-07-14

updated: 2026-07-14

started:

completed:

parent_prd: PRD-004

parent_task: TASK-041

reported_by: User

detected_in: Frontend FormSearchBar

related_test: TEST-TASK-041

fix_summary:

verification_report:

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

(To be filled by Software Engineer)

The `FormSearchBar` component queries the old metadata schema for form definitions. It was never updated to:
- Query `sys_window` / `sys_menu` instead of old `sys_metadata_views`
- Navigate to `/window/{windowName}` instead of `/runtime/{formCode}`
- Display window names from the new schema

---

# Fix

(To be filled by Software Engineer)

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

(To be filled by Software Engineer)

Likely files:
- `frontend/src/core/runtime/components/FormSearchBar.tsx` (or similar)
- `frontend/src/core/runtime/api/runtimeApi.ts` (new search API function)

---

# Related Documents

- PRD-004: FR-011 (Show user-friendly labels), FR-012 (Route change to /window/)
- TASK-041: Frontend Routing + WindowPage
- CHANGE-TASK-041: Known Limitation — "Ctrl+K search still uses old /runtime paths"
- BUG-007: PRD-004 Flyway not enabled (prerequisite — data must exist in sys_window/sys_menu before search can work)
