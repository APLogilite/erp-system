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
