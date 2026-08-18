---
id: TEST-TASK-073
task_id: TASK-073
parent_prd: PRD-007
test_date: 2026-08-18
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL, Flyway enabled)
build_commit_tested: a60e251 (prd/PRD-007-sales-order-workflow-customer-management)
test_scope: Verify order status workflow service, transitions, audit logging, and allowed-transitions endpoint.
---

# Test Report — TASK-073: Implement Order status workflow backend service

---

## Test Scope

Verify that `OrderWorkflowService` enforces the order lifecycle, writes audit rows to `tx_order_status_log`, and exposes the `allowed-transitions` and `transition` endpoints.

---

## Test Cases Executed

### TC-001: allowed-transitions endpoint returns valid targets
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | For a draft order, endpoint returns `confirmed` and `cancelled`. |
| Actual | Returned `["confirmed", "cancelled"]` for a draft sales order. |

### TC-002: Invalid transition returns 400
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `draft → paid` returns 400 with explicit message naming current and target status. |
| Actual | `400 BAD_REQUEST` with message `"Cannot transition from draft to paid"`. |

### TC-003: Valid transition updates order status
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `draft → confirmed` succeeds and updates `tx_order.status`. |
| Actual | API returned success; `tx_order.status` changed to `confirmed`. |

### TC-004: Transition writes audit log
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Row in `tx_order_status_log` records from_status, to_status, transitioned_by, transitioned_at. |
| Actual | Audit row found with `draft → confirmed`, admin user id, and timestamp. |

### TC-005: Audit log table and metadata exist
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `tx_order_status_log` table has required columns and is registered in `sys_table`. |
| Actual | Table found with all required columns plus system columns; metadata registered. |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | Valid transitions succeed and update tx_order.status | **PASSED** | Verified via API + DB |
| AC2 | Invalid transitions return 400 with explicit message | **PASSED** | Verified via API |
| AC3 | Every transition logged to tx_order_status_log | **PASSED** | Verified via DB |
| AC4 | allowed-transitions endpoint returns correct list per status | **PASSED** | Verified via API |
| AC5 | Cancel blocked when an invoice exists for the order | **PENDING** | Awaiting manual scenario MS-001 |
| AC6 | Audit log rows show user, timestamp, from/to | **PASSED** | Verified via DB |
| AC7 | Backend starts cleanly | **PASSED** | Verified |

---

## Manual Test Scenarios

### MS-001: Cancel blocked when invoice exists
| Field | Value |
|-------|-------|
| Preconditions | Backend and frontend running; admin logged in; a confirmed/delivered sales order exists; an active invoice has been created for that order. |
| Steps | 1. Create or locate a sales order and transition it to `delivered`.<br>2. Create a sales invoice linked to that order (so `tx_invoice.order_id` is set and invoice status is not `cancelled`).<br>3. Attempt to transition the order to `cancelled` via the UI workflow button or API.<br>4. Verify the operation fails with a message like "Cannot cancel order with active invoices". |
| Expected Result | Cancel is rejected while a non-cancelled invoice exists for the order. |
| Actual Result | *(to be filled by user)* |
| Status | **PENDING** |

---

## Regression Results

| Test Suite | Result |
|------------|--------|
| `mvn test` (36 tests) | 36 pass, 0 fail, 0 errors |
| `start-all.sh --setup` | PASS |

No regression introduced.

---

## Bugs Found

None (automated checks passed).

---

## Known Limitations

- The audit log stores `transitioned_by` as a UUID; the API response does not include the audit log.
- Credit check on `draft → confirmed` may block confirmation depending on partner credit state.

---

## Release Recommendation

**CONDITIONAL PASS** — Automated API and DB checks pass. Pending manual UI verification in MS-001.

---

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 5 |
| Passed | 5 |
| Failed | 0 |
| Skipped | 0 |
| Bugs Created | 0 |
| Acceptance Criteria Passed | 6 |
| Acceptance Criteria Pending | 1 |
| Requirement Issues Identified | 0 |

---

## Reusable Scripts

```bash
# Login and call workflow endpoints:
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['accessToken'])")
ORDER_ID=<draft-sales-order-id>
curl -s "http://localhost:8081/api/v1/orders/$ORDER_ID/allowed-transitions" -H "Authorization: Bearer $TOKEN"
curl -s -X POST "http://localhost:8081/api/v1/orders/$ORDER_ID/transition" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"targetStatus":"confirmed"}'
```
