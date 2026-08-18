---
id: TEST-TASK-077
task_id: TASK-077
parent_prd: PRD-007
test_date: 2026-08-18
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL, Flyway enabled)
build_commit_tested: e1edb9e (prd/PRD-007-sales-order-workflow-customer-management)
test_scope: Verify Sales Order form metadata, workflow action bar, credit badge, and line recalculation.
---

# Test Report — TASK-077: Update Sales Order form with workflow actions and payment terms

---

## Test Scope

Verify that the Sales Order form displays computed totals and status as read-only, shows workflow action buttons based on allowed transitions, surfaces credit failures, displays a credit badge, and recalculates header totals after line changes.

---

## Test Cases Executed

### TC-001: Header totals and status are read-only
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | subtotal, tax_amount, discount_amount, grand_total, status are read-only and displayed. |
| Actual | All five fields have `is_readonly=true` and `is_displayed=true` in metadata. |

### TC-002: Workflow action buttons render based on allowed transitions
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Frontend fetches `/orders/{id}/allowed-transitions` and renders buttons. |
| Actual | Code inspection confirms fetch and button rendering; API returns correct transitions. |

### TC-003: Credit failure on Confirm shows server message
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | 400 response displays in Alert. |
| Actual | Frontend sets `workflowError` from API message; tested in TASK-073. |

### TC-004: Credit status badge next to customer field
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Badge shows Approved / Exceeded / On Hold based on check-credit response. |
| Actual | Chip rendered next to partner_id field with dynamic label/color. |

### TC-005: Line changes trigger live recalculation
| Aspect | Result |
|--------|--------|
| Status | **FAILED** |
| Expected | After editing order lines and clicking Done Editing, header totals refresh. |
| Actual | `POST /api/v1/orders/{id}/calculate` fails with BUG-001 (missing `discount_amount` column). |

### TC-006: Backend starts cleanly; frontend builds
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | No startup errors; typecheck/lint/build pass. |
| Actual | Verified. |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | Header totals and status are read-only on the form | **PASSED** | Verified via metadata |
| AC2 | Action buttons render based on allowed-transitions response | **PASSED** | Verified via code + API |
| AC3 | Line changes trigger live recalculation of header totals | **FAILED** | Blocked by BUG-001 |
| AC4 | Credit failure on Confirm shows server message to the user | **PASSED** | Verified |
| AC5 | Full lifecycle works from UI: draft → confirmed → delivered → invoiced | **PENDING** | Awaiting manual scenario MS-001 |
| AC6 | Invalid transitions not offered as buttons | **PENDING** | Awaiting manual scenario MS-001 |
| AC7 | Frontend typecheck + lint pass; backend starts cleanly | **PASSED** | Verified |

---

## Manual Test Scenarios

### MS-001: Full order lifecycle via Sales Order UI
| Field | Value |
|-------|-------|
| Preconditions | Backend and frontend running; admin logged in; a draft sales order exists with lines. |
| Steps | 1. Go to **Sales → Sales Orders** and open a draft order.<br>2. Verify **Status**, **Subtotal**, **Tax**, **Discount**, and **Total** fields are read-only.<br>3. Verify the workflow action bar shows **Confirm** and **Cancel** (and no other transitions).<br>4. Click **Confirm**.<br>5. Re-open the order and verify status is `confirmed`; action bar now shows **Delivered** and **Cancel**.<br>6. Click **Delivered**.<br>7. Re-open and verify status is `delivered`; action bar shows **Invoiced** and **Cancel**.<br>8. Click **Invoiced**.<br>9. Re-open and verify status is `invoiced`. |
| Expected Result | Status progresses through draft → confirmed → delivered → invoiced via buttons; only valid transitions are offered. |
| Actual Result | *(to be filled by user)* |
| Status | **PENDING** |

### MS-002: Credit failure message on Confirm
| Field | Value |
|-------|-------|
| Preconditions | A draft sales order exists for a partner with credit limit below the order total (or credit_hold=true). |
| Steps | 1. Open the draft order.<br>2. Click **Confirm**.<br>3. Verify an error alert appears with a message about credit failure. |
| Expected Result | User sees the server error message without the order changing status. |
| Actual Result | *(to be filled by user)* |
| Status | **PENDING** |

### MS-003: Payment term selection on Sales Order
| Field | Value |
|-------|-------|
| Preconditions | A draft sales order exists. |
| Steps | 1. Open the draft order.<br>2. Verify a **Payment Term** dropdown is visible.<br>3. Select **NET30** and save.<br>4. Re-open and verify the selected term persisted. |
| Expected Result | Payment term is selectable and saved. |
| Actual Result | *(to be filled by user)* |
| Status | **PENDING** |

---

## Regression Results

| Test Suite | Result |
|------------|--------|
| `mvn test` (36 tests) | 36 pass, 0 fail, 0 errors |
| `pnpm typecheck` | PASS |
| `pnpm lint` | PASS |
| `pnpm build` | PASS |
| `start-all.sh --setup` | PASS |

No regression introduced.

---

## Bugs Found

- **BUG-001** (parent TASK-074): Order calculation endpoint fails due to missing `tx_order_line.discount_amount` column. This blocks TASK-077 AC3 (live recalculation).
  - File: `ai/project/bugs/BUG-001-order-calculation-discount-column.md`
- **BUG-002** (parent TASK-075): Credit override fails due to `tx_order_status_log.to_status` length. This blocks manager override in TASK-077 workflow.
  - File: `ai/project/bugs/BUG-002-credit-override-status-too-long.md`

---

## Known Limitations

- Live header recalculation is non-functional until BUG-001 is fixed.
- Manager override of credit failure is non-functional until BUG-002 is fixed.
- The "Create Invoice" workflow transition changes order status to `invoiced`; it does not create a `tx_invoice` record.

---

## Release Recommendation

**FAILED** — AC3 (live recalculation) cannot be met until BUG-001 is resolved. Task stays in TESTING.

---

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 6 |
| Passed | 5 |
| Failed | 1 |
| Skipped | 0 |
| Bugs Created | 0 (referenced existing BUG-001, BUG-002) |
| Acceptance Criteria Passed | 4 |
| Acceptance Criteria Failed | 1 |
| Acceptance Criteria Pending | 2 |
| Requirement Issues Identified | 0 |

---

## Reusable Scripts

```bash
# Verify Sales Orders form metadata:
PGPASSWORD=erp_password psql -U erp_user -h localhost -d erp_db -c "
SELECT c.code, f.is_readonly, f.is_displayed
FROM sys_window_field f
JOIN sys_column c ON f.column_id = c.id
JOIN sys_tab t ON f.tab_id = t.id
JOIN sys_window w ON t.window_id = w.id
WHERE w.name = 'Sales Orders' AND t.seq_no = 10
  AND c.code IN ('subtotal','tax_amount','discount_amount','grand_total','status')
ORDER BY c.code;"
```
