---
id: TEST-TASK-074
task_id: TASK-074
parent_prd: PRD-007
test_date: 2026-08-18
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL, Flyway enabled)
build_commit_tested: a935790 (prd/PRD-007-sales-order-workflow-customer-management)
test_scope: Verify order auto-calculation endpoint computes and persists header totals.
---

# Test Report — TASK-074: Implement Order auto-calculation backend service

---

## Test Scope

Verify that `POST /api/v1/orders/{id}/calculate` computes header totals from order lines, persists them, handles empty orders, and returns proper errors for unknown orders.

---

## Test Cases Executed

### TC-001: Calculate endpoint returns correct totals for multi-line order
| Aspect | Result |
|--------|--------|
| Status | **FAILED** |
| Expected | Endpoint returns subtotal, discountAmount, taxAmount, grandTotal and persists to tx_order. |
| Actual | `DATABASE_ERROR`: `column "discount_amount" does not exist` in `tx_order_line`. |

### TC-002: Totals persisted to tx_order
| Aspect | Result |
|--------|--------|
| Status | **FAILED** |
| Expected | tx_order header totals updated after calculate. |
| Actual | Calculation failed; header not updated by this call. |

### TC-003: Zero-line order returns zeros without error
| Aspect | Result |
|--------|--------|
| Status | **SKIPPED** |
| Expected | Empty order returns all-zero totals. |
| Actual | Not tested because the endpoint fails on any order due to missing column. |

### TC-004: Unknown order id returns 404
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `404 NOT_FOUND` with explicit message. |
| Actual | `404 NOT_FOUND` returned: `"Order not found: ..."`. |

### TC-005: Backend starts cleanly
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | No startup errors. |
| Actual | Backend started cleanly. |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | Calculate endpoint returns correct totals for multi-line orders | **FAILED** | BUG-001: missing `discount_amount` column on `tx_order_line` |
| AC2 | Totals persisted to tx_order | **FAILED** | Blocked by BUG-001 |
| AC3 | Zero-line order returns zeros without error | **SKIPPED** | Blocked by BUG-001 |
| AC4 | Unknown order id returns 404 | **PASSED** | Verified via API |
| AC5 | Backend starts cleanly | **PASSED** | Verified |

---

## Manual Test Scenarios

None — this is a backend-only task. Any UI recalculation path is covered under TASK-077 manual scenarios.

---

## Regression Results

| Test Suite | Result |
|------------|--------|
| `mvn test` (36 tests) | 36 pass, 0 fail, 0 errors |
| `start-all.sh --setup` | PASS |

No regression introduced by test activity; existing defect discovered.

---

## Bugs Found

- **BUG-001**: `OrderCalculationService` queries non-existent `tx_order_line.discount_amount`, causing `POST /api/v1/orders/{id}/calculate` to fail.
  - Severity: High
  - Status: READY_FOR_DEV
  - File: `ai/project/bugs/BUG-001-order-calculation-discount-column.md`

---

## Known Limitations

- Until BUG-001 is fixed, order recalculation is non-functional.

---

## Release Recommendation

**FAILED** — Core acceptance criteria (AC1, AC2) cannot be met until BUG-001 is resolved. Task stays in TESTING.

---

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 5 |
| Passed | 2 |
| Failed | 2 |
| Skipped | 1 |
| Bugs Created | 1 |
| Acceptance Criteria Passed | 2 |
| Acceptance Criteria Failed | 2 |
| Acceptance Criteria Skipped | 1 |
| Requirement Issues Identified | 0 |

---

## Reusable Scripts

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['accessToken'])")
ORDER_ID=$(PGPASSWORD=erp_password psql -U erp_user -h localhost -d erp_db -tAc "SELECT id FROM tx_order WHERE order_type='sales' LIMIT 1;")
curl -s -X POST "http://localhost:8081/api/v1/orders/$ORDER_ID/calculate" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```
