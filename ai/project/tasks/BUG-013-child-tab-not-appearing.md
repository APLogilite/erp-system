---
id: BUG-013

title: Child tab (Lines) does not appear — rename parentColumn to parentLinkColumn_ID with UUID FK reference to sys_column

status: READY_FOR_TEST

priority: Critical

severity: Critical

owner: QA Engineer

assigned_to:

assigned_branch: prd/PRD-005-v2

locked: false

created: 2026-07-21

updated: 2026-07-28

started: 2026-07-21

completed:

parent_prd: PRD-005

parent_task: TASK-046

reported_by: User

detected_in: Runtime — Window record detail view, child tabs not rendered

related_test:

fix_summary: Rename sys_tab.parentColumn → parentLinkColumn_ID, change type to UUID FK to sys_column.id. Use sys_column.relation_table to resolve parent-child tab relationships. Update seed data, entity, DTO, and services.

verification_report:

history:
  - 2026-07-21 — Product Manager — Created. Post-release regression from PRD-005 TASK-046. User reports child tab (Lines) does not appear at all when opening a Sales Order record.
  - 2026-07-21 — Software Engineer — Locked, started implementation.
  - 2026-07-21 — Software Engineer — Implemented: renamed parentColumn to parentLinkColumn_ID, UUID FK reference, updated all services, seed data, and frontend. Merged to prd/PRD-005-v2.
  - 2026-07-21 — QA Engineer — Locked, started testing.
  - 2026-07-21 — QA Engineer — Code review + automated tests pass (36/36), server starts cleanly. Manual UI verification pending user confirmation.
  - 2026-07-28 — Software Engineer — Final server verification (fresh DB via `start-all.sh --setup`) found the fix incomplete: V4/V5 seeds never registered the parent-FK columns (`tx_order_line.order_id`, `tx_invoice_line.invoice_id`, `tx_shipment_line.shipment_id`, `sys_tab.window_id`, `sys_window_access.window_id`, `sys_window_field.tab_id`) in `sys_column`, so V7 backfill matched zero rows and `parent_link_column_id` stayed NULL for all child tabs except Table Definitions → Columns. `childTabIds` was empty at runtime. Rework started (READY_FOR_TEST → IN_DEVELOPMENT); also fixes stale lock from previous transition.
  - 2026-07-28 — Software Engineer — Rework complete: new migration V8 seeds the 6 missing FK columns in `sys_column` and backfills `parent_link_column_id`; also fixed `db-reset.sh` self-killing pkill pattern. Verified on fresh DB: 8 migrations applied, all child tabs linked, `childTabIds` populated for all 7 windows, child records fetch returns correctly filtered rows, 36/36 tests pass, `tsc --noEmit` clean, both servers start error-free. All [SE] acceptance criteria checked. → READY_FOR_TEST (unlocked, QA for manual UI verification). See CHANGE-BUG-013 addendum.

---

# Summary

When opening a Sales Order record (or any transaction record with child tabs), the child tab (e.g., "Lines", "Shipments") does not render at all — not even the tab header/button. The main Header tab renders correctly.

This is a **regression from PRD-005 TASK-046** which moved the `findChildTabs()` logic from the frontend to the backend. The backend's naming convention matching (`table.endsWith('_' + strippedParentColumn)`) fails for **plural table names** like `tx_orders`, `tx_order_lines`, etc.

**Fix approach:** Replace the loose string `parentColumn` with a proper UUID FK reference `parentLinkColumn_ID` pointing to `sys_column.id`. Use `sys_column.relation_table` to resolve exact parent-child tab relationships — no naming convention guessing.

---

# Problem

**Steps to reproduce:**
1. Navigate to any transaction window with child tabs (e.g., Sales Orders, Purchase Orders, Sales Invoices, Shipments)
2. Click on any record in the list view to open the detail view
3. The main tab ("Header") form renders with field data ✅
4. **Observe:** The child tab buttons/headers (e.g., "Lines", "Shipments", "Payments") are **missing** — only the Header tab is visible

**This is different from BUG-011.** BUG-011 was: child tab header/footer renders but data grid is empty. This bug is: **child tab header itself is absent**.

**Windows affected** (all windows with child tabs and plural table names):
- Sales Orders → child tabs: Lines, Shipments
- Purchase Orders → child tabs: Lines, Receipts
- Sales Invoices → child tabs: Lines, Payments
- Purchase Invoices → child tabs: Lines
- Shipments → child tabs: Lines

**Master data windows** (single tab, no children) — unaffected.

---

# Expected Behaviour

1. Opening a Sales Order record detail view shows the **Header tab** and **Lines tab** (and Shipments tab)
2. Child tab buttons/headers are visible so the user can switch between tabs
3. Child tab naming convention matching works correctly for plural table names like `tx_orders`, `tx_order_lines`, etc.

---

# Actual Behaviour

Only the Header tab is visible. Child tab headers are absent. The user cannot navigate to Lines, Shipments, or any other child tab.

---

# Root Cause

TASK-046 (PRD-005) modified `WindowDefinitionAssemblyService.assembleDefinition()` to compute `childTabIds` server-side, removing the frontend's `findChildTabs()` function. The algorithm used **naming convention matching** — guessing parent-child relationships by stripping `_id` from `parentColumn` and trying to match it against table name suffixes (e.g., `order_id` → `order` → check if a table ends with `_order`).

This is fundamentally fragile. It fails for plural table names (`tx_orders` ends with `_orders`, not `_order`), inconsistent naming, or any case where the FK column name doesn't mirror the table name.

The **correct mechanism already exists** in the system — the bug is that it wasn't used.

---

# Fix — Schema Change + Reference-Based Resolution

Replace the loose string `parentColumn` with a **direct FK reference** to `sys_column.id`, and use that column's `relation_table` to determine parent-child tab relationships. No naming convention guessing at all.

---

## Part 1 — Database Schema Change

### Rename + Retype `sys_tab.parentColumn`

| Change | Old | New |
|--------|-----|-----|
| Column name | `parentColumn` | `parentLinkColumn_ID` |
| Type | `VARCHAR` (column name string) | `UUID` (FK reference to `sys_column.id`) |
| Purpose | Stored column name like `'order_id'` | Direct pointer to the `sys_column` record |

Add a FK constraint: `FOREIGN KEY (parentLinkColumn_ID) REFERENCES sys_column(id)`

This affects:
- `sys_tab` DDL definition (in the migration that created it)
- The JPA entity `Tab`/`SysTab`
- The DTO `TabDefinitionResponse`
- All seed data that sets `parentColumn`

### Update Seed Data (V27 — ERP Windows)

For each child tab that currently sets `parentColumn = 'some_name'`, replace it with the actual UUID of the corresponding `sys_column` record:

| Window | Child Tab | parentColumn (old) | parentLinkColumn_ID (new) |
|--------|-----------|-------------------|--------------------------|
| Sales Orders | Lines | `order_id` | UUID of `tx_order_lines.order_id` in `sys_column` |
| Sales Orders | Shipments | `order_id` or similar | UUID of `tx_shipment.order_id` in `sys_column` |
| Purchase Orders | Lines | `order_id` | UUID of `tx_order_lines.order_id` in `sys_column` |
| Sales Invoices | Lines | `invoice_id` | UUID of `tx_invoice_lines.invoice_id` in `sys_column` |
| Sales Invoices | Payments | `invoice_id` | UUID of `tx_payment.invoice_id` in `sys_column` |
| Purchase Invoices | Lines | `invoice_id` | UUID of `tx_invoice_lines.invoice_id` in `sys_column` |
| Shipments | Lines | `shipment_id` | UUID of `tx_shipment_line.shipment_id` in `sys_column` |

*(Also check V26 — Admin Windows — for any hierarchical tabs using parentColumn)*

---

## Part 2 — JPA Entity + DTO Updates

### `Tab` entity (`SysTab`)
- Rename field `parentColumn` → `parentLinkColumn_ID`
- Change type: `String` → `UUID` (or `SysColumn` with `@ManyToOne` / `@OneToOne`)
- Add JPA relationship: `@ManyToOne @JoinColumn(name = "parentLinkColumn_ID")` pointing to `SysColumn`

### `TabDefinitionResponse` DTO
- Rename field `parentColumn` → `parentLinkColumn_ID` (keep old name as alias if backward compat needed)
- Type: `UUID`

### `runtimeApi.ts` (frontend `WindowTabDefinition` interface)
- Rename `parentColumn` → `parentLinkColumn_ID`
- Type: `string` (UUID string)

---

## Part 3 — `childTabIds` Computation (Core Fix)

In `WindowDefinitionAssemblyService.assembleDefinition()`:

```java
// OLD: naming convention guessing (BROKEN)
String stub = tab.parentColumn.replace("_id", "");
parentTable.endsWith("_" + stub)  // FAILS for plural tables

// NEW: reference-based resolution
for (TabDefinitionResponse tab : allTabs) {
    if (tab.parentLinkColumn_ID == null) continue;
    
    // 1. Load the sys_column by UUID directly
    SysColumn col = sysColumnRepo.findById(tab.parentLinkColumn_ID).orElse(null);
    if (col == null || col.relationTable == null) continue;
    
    // 2. relation_table tells us the parent tab's table
    String parentTableName = col.relationTable;
    
    // 3. Find the parent tab whose table matches
    TabDefinitionResponse parentTab = allTabs.stream()
        .filter(t -> parentTableName.equals(t.table.name))
        .findFirst().orElse(null);
    if (parentTab == null) continue;
    
    // 4. Link them
    parentTab.childTabIds.add(tab.id);
}
```

This is **guaranteed correct** — `relation_table` in `sys_column` stores the actual target table name of the FK, so it always matches.

---

## Part 4 — Child Record WHERE Clause Queries

Wherever the system builds WHERE clauses for child record fetching (e.g., `WindowDataService.getChildRecords()`), it currently uses `parentColumn` as the column name:

```sql
WHERE order_id = '<parent_id>'
```

With the UUID reference, the column name must be resolved:

```java
// Load the sys_column to get the actual column name for the WHERE clause
SysColumn col = sysColumnRepo.findById(tab.parentLinkColumn_ID).orElse(null);
String columnName = col.columnName;  // e.g., "order_id"
// Use it in the WHERE clause
```

---

## Summary of All Changes

| Layer | Change |
|-------|--------|
| **DB schema** | Rename `sys_tab.parentColumn` → `sys_tab.parentLinkColumn_ID`, change type `VARCHAR` → `UUID` |
| **Seed data** | V27 (and V26 if needed): replace column name strings with actual `sys_column` UUIDs |
| **JPA entity** | Rename field, add `@ManyToOne` to `SysColumn` |
| **DTO** | `TabDefinitionResponse.parentColumn` → `TabDefinitionResponse.parentLinkColumn_ID` |
| **Assembly service** | Replace naming convention matching with UUID-based `sysColumnRepo.findById()` resolution |
| **Data service** | Resolve column name from UUID for WHERE clause queries |
| **Frontend interface** | Update `WindowTabDefinition` in `runtimeApi.ts` |

---

# Acceptance Criteria

## Schema & Seed (`[SE]`)
- [x] `sys_tab.parentColumn` renamed to `sys_tab.parentLinkColumn_ID`, type changed to UUID with FK to `sys_column.id`
- [x] New Flyway migration created for the schema change (V7 rename + V8 seed/backfill)
- [x] All seed data updated: child tabs set `parentLinkColumn_ID` to the correct `sys_column` UUID instead of column name strings — completed via V8 on 2026-07-28 (V5/V7 seed gap: FK columns were missing from `sys_column`; verified all 9 child tabs linked)
- [x] Admin windows (V26) checked for any `parentColumn` usage and updated — Window Definitions → Tabs/Access/Fields linked via V8

## JPA & DTO (`[SE]`)
- [x] `Tab` entity: field renamed, type changed to `UUID` (note: final design uses a plain UUID column `parentLinkColumnId` with explicit `SysColumnRepository.findById()` resolution — the `@ManyToOne` mapping from the original plan was tried and removed as conflicting, commit 273594b)
- [x] `TabDefinitionResponse`: field renamed to `parentLinkColumn_ID` (UUID)
- [x] Frontend `WindowTabDefinition`: field renamed in `runtimeApi.ts`

## childTabIds Computation (`[SE]`)
- [x] Naming convention matching (`strip '_id'` + `endsWith`) completely removed from `WindowDefinitionAssemblyService.assembleDefinition()`
- [x] New logic: for each tab `T`, load `sys_column` by `T.parentLinkColumn_ID`, get `relation_table`, find parent tab by `table.name`, add `T.id` to `parentTab.childTabIds`
- [x] `childTabIds` is correctly populated for ALL windows — verified via API on fresh DB 2026-07-28: Sales/Purchase Orders, Sales/Purchase Invoices, Shipments → Lines; Table Definitions → Columns; Window Definitions → Tabs/Access/Fields
- [x] Works regardless of any table naming convention — verified: `tx_order_line` resolves to parent `tx_order` purely via `relation_table` reference

## Child Record Queries (`[SE]`)
- [x] Wherever `parentColumn` was used to build WHERE clauses (e.g., `WindowDataService`), update to resolve column name from `sys_column` via UUID
- [x] Child record filtering still works correctly: `WHERE {columnName} = '<parent_id>'` — verified: `GET /runtime/windows/Sales Orders/records/{id}` returns `childRecords.Lines` (2 rows) filtered by `order_id`

## Verification — All Windows with Child Tabs (`[QA]`)
- [ ] Opening **Sales Orders** record: child tabs "Lines" and "Shipments" are visible and show data
- [ ] Opening **Purchase Orders** record: child tab "Lines" is visible and shows data
- [ ] Opening **Sales Invoices** record: child tabs "Lines" and "Payments" are visible and show data
- [ ] Opening **Purchase Invoices** record: child tab "Lines" is visible and shows data
- [ ] Opening **Shipments** record: child tab "Lines" is visible and shows data
- [ ] Opening **Payments** record: single tab only (no regression)

## Regression — Windows Without Child Tabs (`[QA]`)
- [ ] Master data windows (Business Partners, Products, UOM, Warehouses) — single tab, no regressions
- [ ] Admin windows (Table Definitions → Columns, Window Definitions → Tabs → Fields) — hierarchical child tabs still work

## Build & Tests (`[SE]`)
- [x] Backend `mvn clean compile` succeeds
- [x] Frontend `tsc --noEmit` succeeds
- [x] All 36 backend tests pass — re-verified 2026-07-28, BUILD SUCCESS

---

# Related Documents

- PRD-005: Backend-Frontend Separation & Code Standardization v1.3.0
- TASK-046: Add childTabIds to TabDefinitionResponse
- CHANGE-TASK-046: Implementation change report
- BUG-011: Previous issue with child tab data being empty (DIFFERENT from this issue)
- PRD-004: Window Hierarchy & Menu System (original window schema)
