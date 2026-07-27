---
id: BUG-013

title: Child tab (Lines) does not appear — rename parentColumn to parentLinkColumn_ID with UUID FK reference to sys_column

status: READY_FOR_TEST

priority: Critical

severity: Critical

owner: QA Engineer

assigned_to:

assigned_branch:

locked: false

created: 2026-07-21

updated: 2026-07-21

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
- [ ] `sys_tab.parentColumn` renamed to `sys_tab.parentLinkColumn_ID`, type changed to UUID with FK to `sys_column.id`
- [ ] New Flyway migration created for the schema change (or existing V27 updated if not yet deployed on this env)
- [ ] All seed data updated: child tabs set `parentLinkColumn_ID` to the correct `sys_column` UUID instead of column name strings
- [ ] Admin windows (V26) checked for any `parentColumn` usage and updated

## JPA & DTO (`[SE]`)
- [ ] `Tab` entity: field renamed, type changed, `@ManyToOne` to `SysColumn` added
- [ ] `TabDefinitionResponse`: field renamed to `parentLinkColumn_ID` (UUID)
- [ ] Frontend `WindowTabDefinition`: field renamed in `runtimeApi.ts`

## childTabIds Computation (`[SE]`)
- [ ] Naming convention matching (`strip '_id'` + `endsWith`) completely removed from `WindowDefinitionAssemblyService.assembleDefinition()`
- [ ] New logic: for each tab `T`, load `sys_column` by `T.parentLinkColumn_ID`, get `relation_table`, find parent tab by `table.name`, add `T.id` to `parentTab.childTabIds`
- [ ] `childTabIds` is correctly populated for ALL windows — no empty lists where child tabs exist
- [ ] Works regardless of any table naming convention

## Child Record Queries (`[SE]`)
- [ ] Wherever `parentColumn` was used to build WHERE clauses (e.g., `WindowDataService`), update to resolve column name from `sys_column` via UUID
- [ ] Child record filtering still works correctly: `WHERE {columnName} = '<parent_id>'`

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
- [ ] Backend `mvn clean compile` succeeds
- [ ] Frontend `tsc --noEmit` succeeds
- [ ] All 36 backend tests pass

---

# Related Documents

- PRD-005: Backend-Frontend Separation & Code Standardization v1.3.0
- TASK-046: Add childTabIds to TabDefinitionResponse
- CHANGE-TASK-046: Implementation change report
- BUG-011: Previous issue with child tab data being empty (DIFFERENT from this issue)
- PRD-004: Window Hierarchy & Menu System (original window schema)
