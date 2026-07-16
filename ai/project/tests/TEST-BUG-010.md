---
id: TEST-BUG-010
task_id: BUG-010
parent_prd: PRD-004
test_date: 2026-07-16
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL)
build_commit_tested: prd/PRD-004-v2 (latest)
test_scope: Verification of POST create record fix — required field validation, type conversion, error handling
status: PASSED
---

# Test Report — BUG-010: Saving Sales Order data returns HTTP 500

## Summary

BUG-010 fixed the HTTP 500 error when creating window records via `POST /api/v1/runtime/windows/{windowName}/records`. Root cause was missing required field validation before SQL INSERT causing unhandled `DataIntegrityViolationException`, and missing UUID/date type conversion from JSON strings to Java objects.

## Fixes Verified

| Fix | Description | Status |
|-----|-------------|--------|
| Required field validation | `WindowDataService.createRecord()` checks `isMandatory` + `column.required` before INSERT | ✅ |
| DataAccessException handler | `GlobalApiExceptionHandler` catches DB errors → HTTP 400 with specific message | ✅ |
| UUID type conversion | `convertTypedStrings()` converts `_id` column strings → UUID objects | ✅ |
| Date type conversion | `convertTypedStrings()` converts `_date` column strings → LocalDate objects | ✅ |
| `_display` fields skipped on save | `createRecord`/`updateRecord` skip keys ending with `_display` | ✅ |

## Structural Verification

| Check | Result |
|-------|--------|
| `mvn test` (36 backend tests) | ALL PASS |
| `pnpm build` (frontend) | BUILD SUCCESS |
| `WindowDataService.createRecord()` calls `convertTypedStrings()` before SQL | ✅ |
| `DynamicCrudService.createRecord()` calls `convertTypedStrings()` | ✅ |
| `DynamicCrudService.updateRecord()` calls `convertTypedStrings()` | ✅ |
| `_display` keys filtered in `createRecord` column loop | ✅ |
| `_display` keys filtered in `updateRecord` column loop | ✅ |

## Acceptance Criteria

| Criteria | Status |
|----------|--------|
| POST to `/{windowName}/records` with all required fields returns HTTP 200 | ✅ |
| POST with missing required field returns HTTP 400 with field name | ✅ |
| UUID FK fields (`partner_id`, `warehouse_id`) survive save without type error | ✅ |
| Date fields (`order_date`, `invoice_date`) survive save without type error | ✅ |
| `_display` fields from `resolveDisplayNames` don't cause SQL error on save | ✅ |
| Database constraint violation returns HTTP 400 (not 500) | ✅ |
| All 3 admin windows (Table/Win/Menu Definitions) create records successfully | ✅ |

## Test Summary

| Metric | Value |
|--------|-------|
| Total Tests | 10 |
| Passed | 10 |
| Failed | 0 |
| Bugs Created | 0 |
