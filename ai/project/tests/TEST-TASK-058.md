---
id: TEST-TASK-058
task_id: TASK-058
parent_prd: PRD-005
status: TESTED
qa_engineer: QA Engineer
test_date: 2026-07-17
duration: 10 minutes
---

# Test Report: TASK-058 — Move Window Schema to core/layout/

## Summary
All acceptance criteria pass. 21 files moved from `modules/metadata/` to `core/layout/`, 4 consumer imports updated.

## Acceptance Criteria
| # | Criterion | Result |
|---|-----------|--------|
| 1 | 7 entities moved to `core/layout/entity/` | ✅ PASS |
| 2 | 7 repositories moved to `core/layout/repository/` | ✅ PASS |
| 3 | 7 services moved to `core/layout/service/` | ✅ PASS |
| 4 | `modules/metadata/` directory deleted | ✅ PASS |
| 5 | Consumer imports updated | ✅ PASS |
| 6 | `mvn clean compile` succeeds | ✅ PASS |
| 7 | All 36 tests pass | ✅ PASS |
