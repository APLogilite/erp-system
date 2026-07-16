---
id: TEST-TASK-039
task_id: TASK-039
parent_prd: PRD-004
test_date: 2026-07-14
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, H2 test DB)
build_commit_tested: 2448c33 (prd/PRD-004-window-hierarchy-menu)
test_scope: Verification of Window Data CRUD API — controller endpoints, service methods
status: PASSED
---

# Test Report — TASK-039: Backend — Runtime Window Data API (CRUD)

## Test Cases
- TC-001: WindowDataService exists with 5 CRUD methods ✅
- TC-002: WindowDataController has 5 CRUD endpoints ✅
- TC-003: GET /{windowName}/records (paginated list, main tab) ✅
- TC-004: GET /{windowName}/records/{id} (record + child tabs) ✅
- TC-005: POST /{windowName}/records (create with auto-set where_clause) ✅
- TC-006: PUT /{windowName}/records/{id} (update) ✅
- TC-007: DELETE /{windowName}/records/{id} (soft-delete) ✅
- TC-008: 401 for unauthenticated requests ✅
- TC-009: All 36 tests pass ✅

## Acceptance Criteria
- ✅ List records returns paginated data
- ✅ Single record returns record + child records for sub-tabs
- ✅ Create/Update/Delete operations function
- ✅ Tab where_clause applied to queries
- ✅ Child tab data uses parent_column FK filter
- ✅ All endpoints return ApiResponse<T> envelope

## Test Summary
| Metric | Value |
|--------|-------|
| Total Tests | 9 |
| Passed | 9 |
| Failed | 0 |
| Bugs Created | 0 |
