---
task_id: BUG-011
type: Bug
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: bugfix/BUG-011
base_branch: prd/PRD-004-v2
status: READY_FOR_TEST
created: 2026-07-15
author: Software Engineer
---

# Change Report — BUG-011

## Summary

Fixed child tab record data not loading in record detail view. The backend's `getRecordWithChildren()` and `getTabRecordWithChildren()` computed `buildTabConditions()` but never passed the resulting `where_clause` conditions to the child record query. This caused child tab `where_clause` filters (e.g., `shipment_type = 'outbound'` on Sales Orders → Shipments) to be silently ignored.

## Root Cause

`WindowDataService.getRecordWithChildren()` called `buildTabConditions(childTab, recordId)` to build conditions including `where_clause` filters, but then only used the `parentColumn` FK relation for the actual query. The remaining conditions (like `shipment_type = 'outbound'`, `order_type = 'purchase'`) were never applied to the `getChildRecords()` call.

The `getChildRecords()` method in `DynamicCrudService` also did not accept additional conditions — it only supported the FK-based relation filter + tenant isolation.

## Files Modified

| File | Description |
|------|-------------|
| `backend/src/main/java/com/erp/core/runtime/service/DynamicCrudService.java` | Added `additionalConditions` parameter to `getChildRecords()` |
| `backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java` | Pass `conditions` from `buildTabConditions()` to both `getRecordWithChildren()` and `getTabRecordWithChildren()` |
| `backend/src/main/java/com/erp/core/runtime/service/RecordCrudService.java` | Updated `getChildRecords()` call to pass `null` for new parameter |

## Changes

### DynamicCrudService.getChildRecords()
- Added `Map<String, String> additionalConditions` parameter
- Applies each key-value pair as `"column" = :param` in the WHERE clause
- Validates column names to prevent SQL injection
- Combined with the existing FK relation + tenant_id conditions via AND

### WindowDataService.getRecordWithChildren()
- Removes the `parentColumn` key from conditions (it's passed separately as `relationColumn`)
- Passes remaining conditions (where_clause filters) to `getChildRecords()`

### WindowDataService.getTabRecordWithChildren()
- Same fix: passes `buildTabConditions` result to `getChildRecords()`
- Also removes `parentColumn` from conditions map before passing

## Behavior Changes

| Scenario | Before | After |
|----------|--------|-------|
| Sales Orders → Shipments (`shipment_type = 'outbound'`) | All shipments returned (incorrect) | Only outbound shipments returned |
| Sales Orders → Lines (no where_clause) | All lines returned (correct) | All lines returned (unchanged) |
| Purchase Orders → Lines (no where_clause) | All lines returned (correct) | All lines returned (unchanged) |
| Sales Invoices → Lines (no where_clause) | All invoice lines returned (correct) | All invoice lines returned (unchanged) |
| Shipments → Lines (no where_clause) | All shipment lines returned (correct) | All shipment lines returned (unchanged) |

## Validation Results

| Check | Result |
|-------|--------|
| `mvn clean compile` | PASS |
| `mvn test` (36 tests) | ALL PASS |
| `pnpm typecheck` | PASS |

## Known Limitations

- The `where_clause` parsing in `buildTabConditions()` is basic — only `field = value` and `field = @id@` patterns are supported. Complex expressions (OR, IN, etc.) will not be applied.
- If a child tab has a `where_clause` that references a field not present in the child table, `validateColumnName()` will throw an `IllegalArgumentException`.

## Follow-up Recommendations

- Consider adding seed data for transaction line tables (`tx_order_line`, `tx_invoice_line`, `tx_shipment_line`) so child tabs have sample data to display
- Consider enhancing `where_clause` parsing for more complex expressions
