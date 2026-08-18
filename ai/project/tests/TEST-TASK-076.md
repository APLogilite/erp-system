---
id: TEST-TASK-076
task_id: TASK-076
parent_prd: PRD-007
test_date: 2026-08-18
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL, Flyway enabled)
build_commit_tested: 031c87f (prd/PRD-007-sales-order-workflow-customer-management)
test_scope: Verify Customer 360 API response structure and dashboard rendering.
---

# Test Report — TASK-076: Implement Customer 360 API and dashboard form

---

## Test Scope

Verify that `GET /api/v1/customers/{partnerId}/360` returns all required sections, respects the 5-record limit, handles unknown partners, and that the dashboard UI renders correctly.

---

## Test Cases Executed

### TC-001: API returns all five sections
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Response contains partner, outstanding, recentOrders, recentInvoices, recentPayments, openQuotations. |
| Actual | All sections present. |

### TC-002: List item keys use documented camelCase
| Aspect | Result |
|--------|--------|
| Status | **FAILED** |
| Expected | Keys such as `orderNumber`, `orderDate`, `grandTotal`, `dueAmount`. |
| Actual | Keys returned as lowercase (`ordernumber`, `orderdate`, `grandtotal`, `dueamount`). |

### TC-003: Unknown partner returns 404
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | 404 NOT_FOUND with explicit message. |
| Actual | 404 returned: "Partner not found: ...". |

### TC-004: Dashboard page accessible from menu
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Menu entry "Customer 360" exists and routes to custom selector page. |
| Actual | Window/menu seeded; MenuNavigation intercepts window click. |

### TC-005: Backend starts cleanly; frontend builds
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `start-all.sh` starts cleanly; `pnpm typecheck`, `pnpm lint`, `pnpm build` pass. |
| Actual | Verified during SE handoff. |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | API returns all five sections with correct data and 5-record limits | **PASSED** | Verified via API |
| AC2 | outstanding matches credit check calculation | **PASSED** | Verified via API |
| AC3 | Dashboard accessible from business partner form | **PASSED** | Button + menu entry present |
| AC4 | New customer with no history shows empty sections (no errors) | **PENDING** | Awaiting manual scenario MS-001; blocked by BUG-003 display issue |
| AC5 | Unknown partner id returns 404 | **PASSED** | Verified via API |
| AC6 | Backend starts cleanly; frontend builds | **PASSED** | Verified |

---

## Manual Test Scenarios

### MS-001: Customer 360 dashboard renders data correctly
| Field | Value |
|-------|-------|
| Preconditions | Backend and frontend running; admin logged in; at least one partner has orders/invoices/payments/quotations. |
| Steps | 1. Navigate to **Master Data → Customer 360**.<br>2. Select a partner with history.<br>3. Verify the dashboard shows profile card, outstanding balance, and tables for Recent Orders, Invoices, Payments, and Open Quotations.<br>4. Verify values (order numbers, dates, totals) display correctly.<br>5. Navigate to **Master Data → Business Partners**, open a partner, and click **Customer 360**.<br>6. Verify the dashboard opens for that partner. |
| Expected Result | All sections populated with readable values; navigation works from both menu and partner form. |
| Actual Result | *(to be filled by user)* |
| Status | **PENDING** |

### MS-002: New partner with no history shows empty sections
| Field | Value |
|-------|-------|
| Preconditions | Backend and frontend running; admin logged in. |
| Steps | 1. Create a new Business Partner with no orders, invoices, payments, or quotations.<br>2. Open the Customer 360 dashboard for that partner.<br>3. Verify the page loads without errors and the Recent Orders/Invoices/Payments/Quotations tables are empty. |
| Expected Result | Dashboard loads with empty sections and no errors. |
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

- **BUG-003**: Customer 360 API returns list item keys in lowercase instead of camelCase, breaking frontend display.
  - Severity: Medium
  - Status: READY_FOR_DEV
  - File: `ai/project/bugs/BUG-003-customer-360-response-key-casing.md`

---

## Known Limitations

- Until BUG-003 is fixed, the dashboard tables will show blank values for historical records.

---

## Release Recommendation

**FAILED** — AC4 (dashboard rendering) cannot be verified until BUG-003 is fixed. Task stays in TESTING.

---

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 5 |
| Passed | 4 |
| Failed | 1 |
| Skipped | 0 |
| Bugs Created | 1 |
| Acceptance Criteria Passed | 5 |
| Acceptance Criteria Failed | 0 |
| Acceptance Criteria Pending | 1 |
| Requirement Issues Identified | 0 |

---

## Reusable Scripts

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['accessToken'])")
PARTNER_ID=<partner-id>
curl -s "http://localhost:8081/api/v1/customers/$PARTNER_ID/360" -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```
