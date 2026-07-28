---
id: CHANGE-BUG-013

task_id: BUG-013

parent_prd: PRD-005

branch: bugfix/BUG-013

type: Bug

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-21

completed: 2026-07-21

duration: 3 hours

related_commits:
  - fix(BUG-013): rename parentColumn to parentLinkColumn_ID with UUID FK reference to sys_column

related_files:
  - backend/src/main/java/com/erp/core/layout/entity/SysTab.java
  - backend/src/main/java/com/erp/core/runtime/dto/window/TabDefinitionResponse.java
  - backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java
  - backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java
  - backend/src/main/resources/db/migration/V3__init_metadata_schema.sql
  - backend/src/main/resources/db/migration/V4__seed_admin_windows.sql
  - backend/src/main/resources/db/migration/V5__seed_erp_windows.sql
  - backend/src/main/resources/db/migration/V6__rename_parent_column.sql
  - frontend/src/core/runtime/api/runtimeApi.ts
  - frontend/src/routes/window/WindowPage.tsx
  - ai/project/schema/metadata/sys_tab.sql

review_required: true

test_required: true

---

# Summary

Replaced the fragile naming-convention-based `childTabIds` computation with a proper UUID FK reference. The `sys_tab.parentColumn` (VARCHAR, column name string) was renamed to `sys_tab.parent_link_column_id` (UUID FK → sys_column.id). The `childTabIds` computation now loads `sys_column` directly by UUID, reads its `relation_table`, and matches against parent tab table names — no naming convention guessing. All child record WHERE clause queries were updated to resolve the column name from the UUID reference.

---

# Scope Verification

- [x] Frontend
- [x] Backend
- [x] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-001: Rename `sys_tab.parentColumn` → `parent_link_column_id`, type VARCHAR → UUID FK to sys_column.id
- FR-002: `TabDefinitionResponse.parentColumn` → `parentLinkColumn_ID` (UUID)
- FR-003: `childTabIds` computed via UUID lookup: load sys_column by UUID → get `relation_table` → find parent tab
- FR-004: Child record WHERE clause queries resolve column name from the UUID reference
- FR-005: Frontend `WindowTabDefinition` interface updated, both `!parentColumn` checks → `!parentLinkColumn_ID`
- FR-006: Flyway V6 migration migrates existing data from old VARCHAR to new UUID values
- FR-007: Schema DDL updated in V3, V4, V5 seed data updated for fresh deployments

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/.../entity/SysTab.java` | Renamed `parentColumn` (String) → `parentLinkColumn_ID` (UUID). Added `@ManyToOne` relationship to `SysColumn` |
| `backend/.../dto/window/TabDefinitionResponse.java` | Renamed `parentColumn` → `parentLinkColumn_ID`, type `String` → `UUID` |
| `backend/.../WindowDefinitionAssemblyService.java` | Replaced naming convention matching with UUID-based `sysColumnRepository.findById()` + `relation_table` resolution for `childTabIds` |
| `backend/.../WindowDataService.java` | Added `sysColumnRepository` dependency + `resolveParentColumnName()` helper. Updated `buildTabConditions()`, `getRecordWithChildren()`, `createRecord()`, `getRecordAndChildRecords()`, `findMainTab()`, `findChildTabs()` to use new UUID field |
| `backend/.../V3__init_metadata_schema.sql` | Updated `parent_column VARCHAR(100)` → `parent_link_column_id UUID REFERENCES sys_column(id)` |
| `backend/.../V4__seed_admin_windows.sql` | Updated `add_child_tab` function and all call sites to use UUID params. Updated sys_tab column registration |
| `backend/.../V5__seed_erp_windows.sql` | Updated all 5 `add_child_tab` calls to use UUID subqueries |
| `backend/.../V6__rename_parent_column.sql` | **New migration**: renames column, migrates data, updates function, drops old column |
| `frontend/.../runtimeApi.ts` | `WindowTabDefinition.parentColumn` → `parentLinkColumn_ID` |
| `frontend/.../WindowPage.tsx` | `!t.parentColumn` → `!t.parentLinkColumn_ID` (two occurrences) |
| `ai/project/schema/metadata/sys_tab.sql` | Updated schema reference DDL |

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/.../V6__rename_parent_column.sql` | Flyway migration for column rename, data migration, and seed function update |

---

# Script Updates

- [x] `ai/project/schema/metadata/sys_tab.sql` updated
- [ ] Verification scripts updated (if applicable)

---

# Database Changes

## Tables Modified

- `sys_tab` — Column `parent_column` dropped, column `parent_link_column_id UUID REFERENCES sys_column(id)` added

## Columns Added

- `sys_tab.parent_link_column_id` UUID FK → `sys_column(id)`

## Columns Removed

- `sys_tab.parent_column` VARCHAR(100)

## Indexes

- `idx_sys_tab_parent_link` on `sys_tab(parent_link_column_id)`

## Constraints

- `fk_sys_tab_parent_link_column` — FOREIGN KEY (parent_link_column_id) REFERENCES sys_column(id)

## Migrations

- `V6__rename_parent_column.sql` — New migration

---

# API Changes

## Response Changes

`GET /api/v1/runtime/windows/{windowName}/definition`

Tab definition response: `parentColumn` (string) → `parentLinkColumn_ID` (UUID string or null)

---

# Classes Updated

| Class | Summary |
|--------|---------|
| `SysTab` | Field `parentColumn` → `parentLinkColumn_ID`, type String → UUID. Added `@ManyToOne` to SysColumn |
| `TabDefinitionResponse` | Field `parentColumn` → `parentLinkColumn_ID`, type String → UUID |
| `WindowDefinitionAssemblyService` | Injection of `SysColumnRepository`. `childTabIds` computation now uses UUID lookup |
| `WindowDataService` | Injection of `SysColumnRepository`. Added `resolveParentColumnName()`. Updated all parent-column references |

---

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| `WindowDataService` | `resolveParentColumnName(TabDefinitionResponse)` | Resolves column name from `parentLinkColumn_ID` UUID via `sysColumnRepository.findById()` |

---

# Validation

## Build

PASS — Backend `mvn clean compile` succeeds. Frontend `tsc --noEmit` succeeds.

---

## Existing Automated Tests

PASS — 33/33 functional tests pass (3 pre-existing DatabaseConnectionTest errors unchanged — BUG-001).

---

# Breaking Changes

**Yes** — the `TabDefinitionResponse.parentColumn` field is renamed to `parentLinkColumn_ID` and its type changed from `String` (null or column name) to `UUID` (null or UUID string). Any consumer of this API field must update.

---

# Known Issues

None.

---

# Future Improvements

Consider whether a migration helper function should be created for converting between column-name-based and UUID-based references to simplify future seed data scripts.

---

# QA Handoff

**Suggested test focus:**
- Open every window with child tabs (Sales Orders, Purchase Orders, Sales Invoices, Purchase Invoices, Shipments, Table Definitions, Window Definitions)
- Verify all child tab buttons appear correctly
- Verify child record data loads correctly in each child tab
- Verify creating new records auto-sets parent FK fields
- Verify backward API compat — old clients using `parentColumn` will break (expected)

**Potential risk areas:**
- The UUID subqueries in V4/V5 seed data rely on the column names being correct and existing in sys_column before `add_child_tab` is called
- The Flyway V6 data migration assumes specific window/tab names — verify all environments have the expected seed data

**Edge cases:**
- Windows with no child tabs (Business Partners, Products, Payments) — should work unchanged
- Grandchild tabs (Window Definitions → Tabs → Fields) — verify cascade works

---

# Addendum — 2026-07-28 Rework (SE, found during final server verification)

## What was still broken

On a **fresh database** (`start-all.sh --setup`), every child tab except Table Definitions → Columns still had `parent_link_column_id = NULL`, so `childTabIds` was empty at runtime and child tabs did not render.

**Root cause:** the V4/V5 seeds never registered the parent-FK columns in `sys_column` metadata (they are not form fields, so they were omitted). V5's `add_child_tab()` UUID subqueries and V7's backfill UPDATEs both join `sys_column` on those codes, so they matched **zero rows** and silently left NULLs. The missing metadata rows were:

| Table | Column | relation_table |
|-------|--------|----------------|
| `tx_order_line` | `order_id` | `tx_order` |
| `tx_invoice_line` | `invoice_id` | `tx_invoice` |
| `tx_shipment_line` | `shipment_id` | `tx_shipment` |
| `sys_tab` | `window_id` | `sys_window` |
| `sys_window_access` | `window_id` | `sys_window` |
| `sys_window_field` | `tab_id` | `sys_tab` |

## Fix

- **New migration `V8__seed_fk_columns_backfill_parent_link.sql`** (idempotent):
  - Part 1 inserts the 6 missing FK columns into `sys_column` (metadata only — form fields come from `sys_window_field`, so nothing new appears on forms).
  - Part 2 re-runs the `sys_tab.parent_link_column_id` backfill UPDATEs (same mappings as V7) — now they match.
  - V5 was not edited (already released on main); V8 corrects the data after V5–V7 run.
- **`backend/db-reset.sh` infra fix**: `pkill -f "erp-system"` matched the script's own absolute path (`/mnt/EXT_LL1/erp-system/backend/db-reset.sh`) and killed the script itself mid-run. Pattern narrowed to `pkill -f "java.*erp-system"`.

## Files added/modified (this pass)

| File | Summary |
|------|---------|
| `backend/.../db/migration/V8__seed_fk_columns_backfill_parent_link.sql` | **New** — seed FK sys_column rows + backfill parent links |
| `backend/db-reset.sh` | Fixed self-killing pkill pattern |

## Verification evidence (fresh DB, 2026-07-28)

- Flyway: all 8 migrations applied (`Successfully applied 8 migrations ... now at version v8`)
- Backend: `Started ErpApplication in 9.437 seconds`, zero `ERROR`/exception lines, `mvn test` → **36/36 pass, BUILD SUCCESS**
- Frontend: `VITE ready`, zero log errors, `tsc --noEmit` clean
- DB: all 9 child tabs across 7 windows have `parent_link_column_id` set with correct `relation_table`
- API `GET /runtime/windows/{name}/definition` — `childTabIds` populated for: Sales Orders, Purchase Orders, Sales Invoices, Purchase Invoices, Shipments (→ Lines); Table Definitions (→ Columns); Window Definitions (Windows → Tabs+Access, Tabs → Fields)
- API `GET /runtime/windows/Sales Orders/records/{id}` — `childRecords.Lines` returned 2 rows correctly filtered by `order_id = <parent id>`

## Follow-ups for QA

- `ai/project/scripts/verify-prd-002-data.sql` (line 26), `verify-prd-003-data.sql` (lines 67, 100) still SELECT the dropped `sys_tab.parent_column` and will error on the current schema; `verify-prd-004-schema.sql` line 57 mentions it in an echo comment. These are QA-owned scripts — needs QA update (`parent_column` → `parent_link_column_id`).
