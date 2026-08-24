---
id: TEST-TASK-085
task_id: TASK-085
parent_prd: PRD-008
test_date: 2026-08-24
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, H2 test DB)
build_commit_tested: 2c25bff (prd/PRD-008-crm-pipeline-sales-team)
test_scope: Verify CRM form metadata seeds and menu entries
---

# Test Report — TASK-085: Seed CRM forms (lead, opportunity, territory, commission)

## Test Scope

Validate that CRM windows/tabs/fields and menu entries are seeded by Flyway. Manual UI verification is deferred.

## Test Cases Executed

### TC-001: Backend automated test suite
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `mvn test` passes with no failures |
| Actual | 36 tests pass, BUILD SUCCESS |

### TC-002: Frontend automated checks
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `pnpm lint`, `pnpm typecheck`, `pnpm build` pass |
| Actual | All pass |

### TC-003: Code review of seed migration
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | V30 creates windows for Leads, Opportunities, Commissions; grants access; adds CRM menu group and entries |
| Actual | V30 implements all requirements |

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | `[SE]` All 4 forms present in runtime forms list | **PASSED** | V30 creates Leads, Opportunities, Commissions, Sales Territories windows |
| AC2 | `[SE]` Sections render as specified; read-only fields configured | **PASSED** | Read-only flags set for lead_number, score, opportunity_number |
| AC3 | `[SE]` Menu entries visible after login | **PENDING** | Manual UI test deferred per PM |
| AC4 | `[QA]` Full CRUD on each form via runtime renderer | **PENDING** | Manual UI test deferred per PM |
| AC5 | `[SE][QA]` Backend starts cleanly with migration applied | **PASSED** | `mvn test` BUILD SUCCESS |

## Manual Test Scenarios

### MS-001: CRM menu and forms visible
| Field | Value |
|-------|-------|
| Preconditions | Backend running with Flyway enabled; admin user logged in |
| Steps | 1. Verify CRM menu group exists in sidebar<br>2. Verify Leads, Opportunities, CRM Dashboard entries<br>3. Verify Master Data → Sales Territories<br>4. Verify Sales → Commissions<br>5. Open each form and verify fields render |
| Expected Result | All menus and forms visible and functional |
| Actual Result | *(pending user execution)* |
| Status | **PENDING** |

## Regression Results

| Test Suite | Result |
|------------|--------|
| `mvn test` (36 tests) | 36 pass, 0 fail, 0 errors |
| `mvn clean compile` | PASS |
| `pnpm lint` | PASS |
| `pnpm typecheck` | PASS |
| `pnpm build` | PASS |

No regression introduced.

## Bugs Found

None.

## Known Limitations

- Manual UI scenarios not executed; will be run together with PM later.

## Release Recommendation

**PASSED (automated)** — All automated checks pass. Manual UI verification pending.

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 3 |
| Passed | 3 |
| Failed | 0 |
| Skipped | 0 |
| Bugs Created | 0 |
| Acceptance Criteria Passed | 3 |
| Acceptance Criteria Skipped | 2 (manual) |

## Reusable Scripts

None. Manual verification only.
