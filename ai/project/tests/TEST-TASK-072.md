---
id: TEST-TASK-072
task_id: TASK-072
parent_prd: PRD-007
test_date: 2026-08-18
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL, Flyway enabled)
build_commit_tested: a849e89 (prd/PRD-007-sales-order-workflow-customer-management)
test_scope: Verify payment_term_id on tx_order/tx_invoice, form fields, and auto-enrichment logic.
---

# Test Report — TASK-072: Add payment term to Order and Invoice tables

---

## Test Scope

Verify that `payment_term_id` is added to `tx_order` and `tx_invoice`, registered in metadata, shown on Sales Orders / Sales Invoices forms, and that the backend auto-enriches order payment term from partner default and invoice due date from term days.

---

## Test Cases Executed

### TC-001: Columns added to tx_order and tx_invoice
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Both tables have `payment_term_id` uuid column. |
| Actual | Columns found on both tables. |

### TC-002: Metadata registration
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Columns registered as many2one to md_payment_term. |
| Actual | Both columns registered with correct relation_table. |

### TC-003: Form fields on Sales Orders and Sales Invoices
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | payment_term_id displayed on both forms. |
| Actual | Fields present at seq_no 100 on both windows. |

### TC-004: Backend auto-enrichment service exists and is wired
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | PaymentTermService implements order term pre-fill and invoice due-date calculation; called in RecordCrudService. |
| Actual | `PaymentTermService.enrichOrderPaymentTerm` and `enrichInvoiceDueDate` found; invoked in RecordCrudService. |

### TC-005: Backend starts cleanly
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | No Flyway or startup errors. |
| Actual | `start-all.sh --setup` completed cleanly. |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | payment_term_id column added to tx_order and tx_invoice with metadata registration | **PASSED** | Verified via SQL |
| AC2 | sales_order and sales_invoice forms show the payment term dropdown | **PASSED** | Verified via SQL |
| AC3 | Saving an invoice with a term auto-calculates due_date when empty | **PASSED** | Code inspection of PaymentTermService + RecordCrudService |
| AC4 | Creating an order pre-fills term from customer default | **PASSED** | Code inspection |
| AC5 | End-to-end: partner with NET30 → new order shows NET30 → invoice due_date = invoice_date + 30 | **PENDING** | Awaiting manual scenario MS-001 |
| AC6 | Backend starts cleanly | **PASSED** | Verified |

---

## Manual Test Scenarios

### MS-001: End-to-end payment term flow
| Field | Value |
|-------|-------|
| Preconditions | Backend and frontend running; Flyway applied; admin logged in; partner has payment term NET30 set (TASK-071). |
| Steps | 1. Navigate to **Master Data → Business Partners** and ensure a partner has **Payment Term** = `NET30`.<br>2. Go to **Sales → Sales Orders** and create a new order for that partner.<br>3. Verify the **Payment Term** field is pre-filled with `NET30`.<br>4. Save the order and note its order number.<br>5. Create a Sales Invoice from the order (or create a new invoice manually for the same partner, with invoice_date = today and Payment Term = NET30, leaving Due Date empty).<br>6. Save the invoice.<br>7. Re-open the invoice and verify **Due Date** = invoice_date + 30 days. |
| Expected Result | Order pre-fills NET30 from partner; invoice due date auto-calculates to 30 days after invoice date. |
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

- Invoice due-date auto-calculation only triggers when `due_date` is null/empty.
- Order payment-term pre-fill requires the partner record to have a default term.

---

## Release Recommendation

**CONDITIONAL PASS** — Automated checks and code inspection pass. Pending manual UI verification in MS-001.

---

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 5 |
| Passed | 5 |
| Failed | 0 |
| Skipped | 0 |
| Bugs Created | 0 |
| Acceptance Criteria Passed | 5 |
| Acceptance Criteria Pending | 1 |
| Requirement Issues Identified | 0 |

---

## Reusable Scripts

```bash
PGPASSWORD=erp_password psql -U erp_user -h localhost -d erp_db -f ai/project/scripts/verify-prd-007-data.sql
```
