---
id: TEST-TASK-075
task_id: TASK-075
parent_prd: PRD-007
test_date: 2026-08-18
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL, Flyway enabled)
build_commit_tested: 0e2c590 (prd/PRD-007-sales-order-workflow-customer-management)
test_scope: Verify customer credit check service and integration with order workflow.
---

# Test Report — TASK-075: Implement Customer Credit Check backend service

---

## Test Scope

Verify that `POST /api/v1/orders/{id}/check-credit` calculates outstanding exposure correctly, blocks confirmation for credit hold or exceeded limits, supports manager override, and integrates with the order workflow transition.

---

## Test Cases Executed

### TC-001: check-credit returns approved for customer with no credit limit
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | approved=true when credit_limit is null. |
| Actual | approved=true, reason=Approved, creditLimit=null. |

### TC-002: Credit hold blocks confirmation
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | check-credit returns approved=false with reason "Customer is on credit hold"; confirm transition fails. |
| Actual | Both check-credit and transition returned failure with correct message. |

### TC-003: Credit limit exceeded blocks confirmation
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | check-credit returns approved=false with reason "Credit limit exceeded"; confirm transition fails. |
| Actual | Both returned failure; outstanding + order > limit detected. |

### TC-004: Manager override succeeds and is audit-logged
| Aspect | Result |
|--------|--------|
| Status | **FAILED** |
| Expected | Override transition succeeds and writes audit row. |
| Actual | `DATABASE_ERROR`: value too long for type character varying(30). |

### TC-005: Override rejected for non-manager role
| Aspect | Result |
|--------|--------|
| Status | **PENDING** |
| Expected | Non-manager user receives 403/role error. |
| Actual | Not tested because override path is broken (BUG-002). |

### TC-006: Backend starts cleanly
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | No startup errors. |
| Actual | Backend started cleanly. |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | check-credit returns correct outstanding calculation | **PASSED** | Verified |
| AC2 | Confirmation blocked when credit_hold = true | **PASSED** | Verified |
| AC3 | Confirmation blocked when outstanding + order > credit_limit | **PASSED** | Verified |
| AC4 | Customer with null credit_limit passes | **PASSED** | Verified |
| AC5 | Override works for manager role and is audit-logged | **FAILED** | BUG-002: status text too long for audit log column |
| AC6 | Override rejected for non-manager role | **PENDING** | Blocked by BUG-002 |
| AC7 | Backend starts cleanly | **PASSED** | Verified |

---

## Manual Test Scenarios

None — this is a backend/API-only task.

---

## Regression Results

| Test Suite | Result |
|------------|--------|
| `mvn test` (36 tests) | 36 pass, 0 fail, 0 errors |
| `start-all.sh --setup` | PASS |

No regression introduced by testing.

---

## Bugs Found

- **BUG-002**: Credit override transition fails because generated `to_status` text exceeds `tx_order_status_log.to_status VARCHAR(30)`.
  - Severity: High
  - Status: READY_FOR_DEV
  - File: `ai/project/bugs/BUG-002-credit-override-status-too-long.md`

---

## Known Limitations

- Until BUG-002 is fixed, manager credit override is non-functional.

---

## Release Recommendation

**FAILED** — AC5 (manager override + audit logging) is a core requirement that fails. Task stays in TESTING until BUG-002 is resolved.

---

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 6 |
| Passed | 4 |
| Failed | 1 |
| Skipped | 1 |
| Bugs Created | 1 |
| Acceptance Criteria Passed | 5 |
| Acceptance Criteria Failed | 1 |
| Acceptance Criteria Pending | 1 |
| Requirement Issues Identified | 0 |

---

## Re-test After Fix (BUG-002 resolved)

Fix: V24 migration added `override_reason TEXT` to `tx_order_status_log`; `OrderWorkflowService` now stores the reason separately and keeps `to_status` as plain status.

### TC-005R: Manager override succeeds and is audit-logged
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Override transition succeeds and audit row records override reason. |
| Actual | Transition returned success; audit row had `to_status=confirmed`, `override_reason=QA re-test`. |

### TC-006R: Override rejected for non-manager role
| Aspect | Result |
|--------|--------|
| Status | **PASSED** *(code path verified)* |
| Expected | Non-manager role cannot override. |
| Actual | `OrderWorkflowService.hasManagerRole` requires `sys_admin`, `tnt_admin`, or `sales_manager`; other roles are rejected. Multi-tenancy prevents cross-user record access in test environment. |

### Regression
| Test Suite | Result |
|------------|--------|
| `mvn test` (36 tests) | 36 pass, 0 fail, 0 errors |

---

## Reusable Scripts

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['accessToken'])")
ORDER_ID=<draft-sales-order-id>
# Set partner credit_limit below order total first
curl -s -X POST "http://localhost:8081/api/v1/orders/$ORDER_ID/check-credit" -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
curl -s -X POST "http://localhost:8081/api/v1/orders/$ORDER_ID/transition" -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d '{"targetStatus":"confirmed","override":true,"overrideReason":"Manager approved"}' | python3 -m json.tool
```
