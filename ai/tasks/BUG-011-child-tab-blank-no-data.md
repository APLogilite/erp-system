---
id: BUG-011

title: Child tab form renders but record data does not load — empty grid/accordion panels

status: READY_FOR_TEST

priority: Critical

severity: Critical

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: bugfix/BUG-011

locked: false

assigned_to:

assigned_branch:

locked: false

created: 2026-07-15

updated: 2026-07-15

started:

completed:

parent_prd: PRD-004

parent_task: TASK-041

reported_by: User

detected_in: Runtime — Record detail view, child tab panels

related_test:

fix_summary: Fixed backend to apply where_clause conditions to child tab record queries. getChildRecords() now accepts additional conditions. Both getRecordWithChildren() and getTabRecordWithChildren() now pass buildTabConditions() results.

verification_report:

history:
  - 2026-07-15 — Product Manager — Created. Child tab form renders (fields visible) but record data does not load in the grid/accordion.
  - 2026-07-15 — Software Engineer — Locked, started development.
  - 2026-07-15 — Software Engineer — Fixed. Added additionalConditions parameter to getChildRecords(), applied buildTabConditions() in both child record fetching paths. Merged to prd/PRD-004-v2.

---

# Summary

When opening a record detail view (e.g., Sales Order, Purchase Order, Invoice), the main tab form renders with data correctly. Child tabs (e.g., "Lines", "Shipments", "Receipts") are visible and their **form structure/layout renders** (fields are displayed), but the **actual record data within the child tab does not load** — the grid/accordion panel shows no rows/records.

---

# Problem

**Steps to reproduce:**
1. Navigate to any transaction window with child tabs (e.g., Sales Orders, Purchase Orders, Invoices)
2. Click on any record in the list view to open the detail dialog
3. The main tab ("Header") form renders with field data ✅
4. Click on a child tab (e.g., "Lines", "Shipments", "Payments") or look at the accordion child panel
5. **Observe:** The child tab form/layout is rendered (fields visible), but the grid/table within it is empty — no record data is loaded

**Windows affected** (all windows with child tabs):
- Sales Orders → child tabs: Lines, Shipments
- Purchase Orders → child tabs: Lines, Receipts
- Invoices → child tabs: Lines, Payments
- Payments → child tabs: Allocations
- Shipments / Material Receipts → child tabs: Lines

**Admin windows affected** (hierarchical tabs):
- Table Definitions → child tab: Columns
- Window Definitions → child tabs: Tabs → Fields

---

# Expected Behaviour

1. Opening a record detail view shows the main tab header form with data
2. Child tabs/accordion panels show a loading spinner while fetching data
3. Child tab panels display records from the linked child table, filtered by `parent_column` FK to the current parent record
4. The child tab uses `sys_tab.parent_column` + `sys_tab.where_clause` (e.g., `order_id = @id@`) to fetch linked records
5. If no child records exist, the grid shows "No records" (not a blank/empty panel)

---

# Actual Behaviour

The child tab **form** (fields, labels, layout) is visible and renders correctly, but the **record data/grid within the child tab is empty** — no rows of data are loaded. This means the child tab's visual structure works but the actual data fetching/display is broken. This could be caused by:

1. **Child tab API endpoint not called** — Frontend doesn't fire the request to fetch child records when tab is selected
2. **API returns 500/error silently** — `GET /{windowName}/records/{id}` endpoint fails for child tab data but the error is swallowed
3. **Child tab data not in response** — The main `fetchWindowRecord` response should include child tab records but doesn't
4. **URL-decoding issue** — Same space-in-window-name problem from BUG-010 might affect child tab data fetching
5. **Frontend rendering glitch** — Data is fetched but the accordion/grid component doesn't render it

**Cross-location check required:** Same as BUG-010 — if the root cause is space-related (`parent_column`, `where_clause` containing spaces, or window names with spaces), check ALL windows with child tabs, not just one.

---

# Root Cause

(To be determined by Software Engineer)

Likely candidates:
- The child tab data fetching logic in `WindowPage.tsx` (added in BUG-009) is not triggering — the `getTabRecordWithChildren()` API endpoint may not be called
- The parent record's ID is not being passed to the child tab fetch function
- The API endpoint `GET /{windowName}/records/{id}` returns data but the frontend doesn't parse child tab records from the response
- The `where_clause` containing `@id@` is not resolved correctly, causing the child query to return zero results
- The child tab accordion/grid component fails to render due to missing data structure

---

# Fix

(To be determined by Software Engineer)

---

# Validation

(To be filled by QA Engineer)

After fix:
- [ ] Opening a Sales Order record: child tab "Lines" form renders **with line item rows** (not empty)
- [ ] Opening a Purchase Order record: child tab "Lines" form renders with line item rows
- [ ] Opening an Invoice record: child tab "Lines" form renders with invoice line rows
- [ ] Opening a Payment record: child tab "Allocations" renders with allocation rows
- [ ] Opening a Shipment record: child tab "Lines" renders with shipment line rows
- [ ] Child tab fields/layout render correctly (visual structure works)
- [ ] Child tab shows "No records" when no child data exists (not just empty)
- [ ] Opening a Business Partner record (no child tabs) shows only main tab (no regression)
- [ ] Admin windows with hierarchical tabs work (Table → Columns, Window → Tabs → Fields)
- [ ] All 36 backend tests pass
- [ ] Frontend typecheck passes

---

# Related Documents

- PRD-004: Window Hierarchy & Menu System v1.0.0
- TASK-041: Frontend — Update Routing to /window/{name} + Fix RuntimePage (WindowPage with tabbed layout)
- TASK-039: Backend — Runtime Window Data API (CRUD Records — GET /{id} returns record + child tab data)
- CHANGE-TASK-041: Known limitation listed: "Child tab records (inline grids) not yet rendered — only main tab shown"
- BUG-009: WindowPage redesign with tabbed dialog and accordion child panels (intended to fix this)
- BUG-010: POST Sales Orders record returns 500 (possible shared root cause with space handling)
