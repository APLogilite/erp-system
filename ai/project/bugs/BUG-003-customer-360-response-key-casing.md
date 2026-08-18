---
id: BUG-003
title: Customer 360 API returns list item keys in lowercase instead of camelCase, breaking frontend display
status: READY_FOR_DEV
priority: High
severity: Medium
owner: Software Engineer
assigned_to:
assigned_branch:
locked: false
created: 2026-08-18
updated: 2026-08-18
started:
completed:
parent_prd: PRD-007
parent_task: TASK-076
reported_by: QA Engineer
detected_in: prd/PRD-007-sales-order-workflow-customer-management
related_test: TEST-TASK-076
fix_summary:
verification_report:
history:
  - created
---

# Summary

`GET /api/v1/customers/{partnerId}/360` returns collection items with snake_case/lowercase keys (`ordernumber`, `grandtotal`, `duedate`, etc.) instead of the documented camelCase keys (`orderNumber`, `grandTotal`, `dueDate`, etc.). The React dashboard references camelCase properties, so tables render blanks/zeroes.

---

# Problem

Customer 360 dashboard tables show empty values for recent orders, invoices, payments, and quotations.

---

# Expected Behaviour

API response list items should use camelCase keys matching the task spec and the frontend interface:
- `orderNumber`, `orderDate`, `grandTotal`
- `invoiceNumber`, `invoiceDate`, `grandTotal`, `dueAmount`
- `paymentNumber`, `paymentDate`, `amount`
- `quoteNumber`, `quoteDate`, `grandTotal`, `validUntil`

---

# Actual Behaviour

API returns lowercase keys:
```json
{
  "recentOrders": [{ "ordernumber": "SO-001", "orderdate": "...", "grandtotal": 1054.87 }]
}
```

---

# Steps To Reproduce

1. Start backend.
2. Log in as admin and obtain a token.
3. Call `GET /api/v1/customers/{partnerId}/360` for any partner that has orders/invoices.
4. Observe response keys are lowercase.
5. Open the Customer 360 page in the browser and select the same partner.
6. Observe table cells are blank or show '-'.

---

# Root Cause

`Customer360Service` uses Spring JDBC `queryForList` with aliases like `order_number as orderNumber`. PostgreSQL lowercases unquoted column labels, so map keys become `ordernumber`, `grandtotal`, etc.

---

# Fix

*(Developer to fill)*

Suggested options:
1. In `Customer360Service`, build result maps manually with explicit camelCase keys after querying.
2. Or, return strongly-typed DTOs/records with `@JsonProperty` annotations instead of `Map<String, Object>`.
3. Or, update frontend interface to use lowercase keys (not recommended — diverges from spec).

---

# Validation

*(Tester to fill after fix)*

- API returns camelCase keys for all list items.
- Customer 360 dashboard renders order/invoice/payment/quotation data correctly.
- Unknown partner returns 404.
- New partner with no history shows empty sections without errors.

---

# Files Changed

*(Developer to fill)*

---

# Related Documents

- Parent PRD: `ai/project/prd/PRD-007-sales-order-workflow-customer-management.md`
- Parent Task: `ai/project/tasks/TASK-076-implement-customer-360-view.md`
- Test Report: `ai/project/tests/TEST-TASK-076.md`
