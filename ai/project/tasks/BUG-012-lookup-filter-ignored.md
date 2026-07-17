---
id: BUG-012

title: Lookup options in reference dropdowns ignore field-level filter_where_clause

type: Bug

scope: backend

status: READY_FOR_TEST

priority: Critical

owner: Software Engineer

assigned_to: Software Engineer

locked: false

created: 2026-07-17

updated: 2026-07-17

parent_prd: PRD-005

parent_task: TASK-047

severity: Critical

related_tasks: []

labels:
  - regression
  - lookup
  - filter

history:
  - 2026-07-17: Bug created (QA Engineer)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: fix merged to PRD branch, READY_FOR_TEST (SE)

---

# Bug Report

## Description

After PRD-005 TASK-047 (Add htmlType and lookupOptions to FieldDefinitionResponse), the lookup options in reference/many2one dropdowns no longer respect the field-level or column-level `filter_where_clause`. Previously, `fetchLookupRecords()` (still available at `GET /api/v1/runtime/windows/lookup/{tableName}`) applied the filter using `getFieldFilterClause()` with `@tab.field@` placeholder resolution. The new `fetchLookupOptions()` in `WindowDefinitionAssemblyService` uses a simple query:

```sql
SELECT id, <display_col> AS label FROM <relation_table> WHERE is_active = true LIMIT 100
```

This ignores:
1. Field-level `filter_where_clause` from `sys_window_field`
2. Column-level `filter_where_clause` from `sys_column`
3. Dynamic `@tab.field@` placeholder resolution using drill context
4. Some tables may not have an `is_active` column (returns 0 results)

## Steps to Reproduce

1. Open any window with a many2one/reference field that has a `filter_where_clause` configured
2. Open the record dialog and click the reference dropdown
3. Observe that ALL records from the relation table are listed, not filtered by the configured where clause

## Expected Behavior

The lookup options in the dropdown should respect the field-level `filter_where_clause` (from `sys_window_field`) and fall back to the column-level `filter_where_clause` (from `sys_column`), including resolving dynamic `@tab.field@` placeholders using the current drill context.

## Actual Behavior

The lookup options are eagerly loaded during window definition assembly with a simple `SELECT ... WHERE is_active = true` query, ignoring any configured filters.

## Root Cause

`WindowDefinitionAssemblyService.fetchLookupOptions()` (line ~210) builds a static query without consulting `getFieldFilterClause()` logic. The field-level filter resolution (which exists in `WindowDataService.getFieldFilterClause()`) is not called during assembly — only the old `lookupRecords` endpoint uses it.

## Suggested Fix

Option A (Recommended): Revert eager loading of lookup options and have the frontend fall back to the old `fetchLookupRecords()` API for filtered lookups. Only eagerly load lookup options for fields that have NO `filter_where_clause` configured.

Option B: Add `filter_where_clause` resolution to `fetchLookupOptions()` by passing the field context (tabId, fieldCode, windowId, drillContext) and resolving placeholders. This would require significant changes to the assembly service since drill context is not available at assembly time.

Option C: Remove eager loading of `lookupOptions` from `WindowDefinitionAssemblyService` entirely, and keep the frontend using `fetchLookupRecords()` for dynamic lookups. The `lookupOptions` field on `ColumnInfo` should only be populated when no filter clause is present.

## Additional Notes

- The old `lookupRecords` endpoint still works (`GET /api/v1/runtime/windows/lookup/{tableName}`) — it correctly applies filters
- The `lookupOptions` field was meant to eliminate N+1 queries, but eagerly loading filtered lookups without drill context is incorrect
- Tables without `is_active` column return 0 results due to the hardcoded WHERE clause
- This affects ALL dropdowns where relationTable + filter_where_clause are configured
