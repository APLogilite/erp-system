---
id: TEST-XXX
task_id: TASK-XXX
parent_prd: PRD-XXX
test_date: YYYY-MM-DD
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, H2 test DB)
build_commit_tested: <commit-hash> (<branch>)
test_scope: <brief description of what was tested>
---

# Test Report — <title>

---

## Test Scope

<Define what is being tested and what is out of scope.>

---

## Test Cases Executed

List each test case with expected vs actual results. Use tables for clarity.

### TC-001: <Test Case Name>
| Aspect | Result |
|--------|--------|
| Status | **PASSED** / **FAILED** |
| Expected | <expected behavior> |
| Actual | <actual result> |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | <criterion> | **PASSED** / **FAILED** / **SKIPPED** | <notes / reason if skipped> |
| AC2 | <criterion> | ... | ... |

---

## Manual Test Scenarios

Scenarios requiring human execution via browser. QA writes the steps, user executes them and reports back results.

### MS-001: <Scenario Name>
| Field | Value |
|-------|-------|
| Preconditions | <what must be set up before testing> |
| Steps | 1. <step 1> |
| | 2. <step 2> |
| | 3. <step 3> |
| Expected Result | <what should happen> |
| Actual Result | <filled by user after execution> |
| Status | **PASSED** / **FAILED** |

---

## Regression Results

| Test Suite | Result |
|------------|--------|
| `mvn test` (<N> tests) | <P> pass, <F> fail, <E> errors |
| `mvn clean compile` | PASS / FAIL |

No/Yes regression introduced. <details if yes>.

---

## Bugs Found

List any bugs discovered during testing. If none, state "None".

---

## Known Limitations

- <limitation 1>
- <limitation 2>

---

## Release Recommendation

**PASSED** / **FAILED** — <brief assessment>

<Recommendation for next steps.>

---

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | <count> |
| Passed | <count> |
| Failed | <count> |
| Skipped | <count> |
| Bugs Created | <count> |
| Acceptance Criteria Passed | <count> |
| Acceptance Criteria Skipped | <count> |
| Requirement Issues Identified | <count> |

---

## Reusable Scripts

Reference any reusable scripts that can re-run this report's verification.

```bash
# Schema verification:
psql -U erp_user -h localhost -d erp_db -f ai/project/scripts/verify-<name>.sql

# Full regression suite:
./ai/project/scripts/run-all-regression.sh
```

If the test involves schema verification, reference the canonical DDL in `ai/project/schema/<table>.sql` for expected table structure.

If no reusable scripts were generated for this test, state "None. Manual verification only."
