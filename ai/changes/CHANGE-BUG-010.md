---
task_id: BUG-010
type: Bug
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: bugfix/BUG-010
base_branch: prd/PRD-004-v2
status: READY_FOR_TEST
created: 2026-07-15
author: Software Engineer
---

# Change Report — BUG-010

## Summary

Fixed HTTP 500 error when creating records via `POST /api/v1/runtime/windows/{windowName}/records`. The error occurred because required fields (NOT NULL columns) were not validated before the SQL INSERT, causing an unhandled `DataAccessException` from PostgreSQL.

## Root Cause

`WindowDataService.createRecord()` did not validate required fields before delegating to `DynamicCrudService.createRecord()`. When the request body was missing a required column (e.g., `order_number`, `order_date`, `partner_id`), PostgreSQL threw a NOT NULL constraint violation. This `DataIntegrityViolationException` was not caught by the controller's `IllegalArgumentException` handler, and the global `Exception` handler returned a generic HTTP 500.

## Files Modified

| File | Description |
|------|-------------|
| `backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java` | Added required field validation in `createRecord()` before INSERT |
| `backend/src/main/java/com/erp/core/runtime/service/DynamicCrudService.java` | Changed `SYSTEM_COLUMNS` from `private` to `public` for reuse |
| `backend/src/main/java/com/erp/config/GlobalApiExceptionHandler.java` | Added `DataAccessException` handler for database-level errors |

## Changes

### WindowDataService.createRecord()

- Added required field validation after where_clause auto-set and before INSERT
- Checks both `field.isMandatory` (from `sys_window_field.is_mandatory`) and `column.required` (from `sys_column.required`)
- Skips system columns (auto-set by `DynamicCrudService`) and where_clause fields (auto-set above)
- Throws `IllegalArgumentException` with a list of missing field labels → returned as HTTP 400

### GlobalApiExceptionHandler

- Added `@ExceptionHandler(DataAccessException.class)` → returns HTTP 400 with the most specific cause message
- Catches constraint violations, SQL grammar errors, and other database exceptions gracefully

## Behavior Changes

| Scenario | Before | After |
|----------|--------|-------|
| Missing `order_number` (required) | HTTP 500 (generic error) | HTTP 400 with message: "Required fields are missing: Order Number" |
| Missing `partner_id` (required) | HTTP 500 (generic error) | HTTP 400 with message: "Required fields are missing: Partner" |
| All required fields present | HTTP 200 | HTTP 200 (unchanged) |
| Invalid column name | HTTP 400 | HTTP 400 (unchanged) |
| Database constraint violation | HTTP 500 (generic error) | HTTP 400 with specific database error message |

## Validation Results

| Check | Result |
|-------|--------|
| `mvn clean compile` | PASS |
| `mvn test` (36 tests) | ALL PASS |
| `pnpm typecheck` | PASS |

## Cross-location Check

The fix is applied at the `WindowDataService.createRecord()` level, which is the single entry point for ALL window record creation. This means:

- ✅ Sales Orders (where_clause: `order_type = 'sales'`)
- ✅ Purchase Orders (where_clause: `order_type = 'purchase'`)
- ✅ All master data windows (Business Partners, Products, UOM, Warehouses)
- ✅ All admin windows (Table Definitions, Window Definitions, Menu Configuration)
- ✅ All other windows created in the future

The required field validation uses the metadata from each window's tab fields, so it automatically adapts to each window's specific schema.

## Known Limitations

- Validation does not check field-level `display_logic` — hidden fields with `is_displayed=false` are still checked if marked mandatory
- Auto-generated IDs for FK lookups are not resolved before validation (user must send raw UUID values)

## Follow-up Recommendations

- Consider adding FK existence validation (e.g., check that `partner_id` references a real partner)
- Consider adding field default value injection (from `defaultValue` in field definition)
