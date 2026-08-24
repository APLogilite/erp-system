---
id: BUG-001
title: Order calculation endpoint fails because tx_order_line has no discount_amount column
status: RESOLVED
priority: High
severity: High
owner: Software Engineer
assigned_to: Software Engineer
assigned_branch: fix/BUG-001
locked: false
created: 2026-08-18
updated: 2026-08-19
started: 2026-08-18
completed: 2026-08-19
parent_prd: PRD-007
parent_task: TASK-074
reported_by: QA Engineer
detected_in: prd/PRD-007-sales-order-workflow-customer-management
related_test: TEST-TASK-074
fix_summary:
verification_report:
history:
  - created
---

# Summary

`POST /api/v1/orders/{id}/calculate` returns a `DATABASE_ERROR` because `OrderCalculationService` queries `tx_order_line.discount_amount`, a column that does not exist in the current schema.

---

# Problem

Order recalculation fails for any sales order with line items, blocking the TASK-077 live-recalculation feature.

---

# Expected Behaviour

The calculate endpoint should compute header totals from order lines without database errors, returning `OrderTotalsResponse` and persisting values to `tx_order`.

---

# Actual Behaviour

API response:
```json
{
  "success": false,
  "data": null,
  "message": "ERROR: column \"discount_amount\" does not exist\n  Position: 34",
  "errorCode": "DATABASE_ERROR",
  "details": []
}
```

---

# Steps To Reproduce

1. Start backend and frontend (`bash start-all.sh --setup`).
2. Log in as admin.
3. Obtain a JWT token.
4. Find any sales order UUID: `SELECT id FROM tx_order WHERE order_type='sales' LIMIT 1;`
5. Call `POST /api/v1/orders/{id}/calculate`.
6. Observe the `DATABASE_ERROR` response.

---

# Root Cause

`OrderCalculationService.calculateAndPersist` selects `discount_amount` (and relies on it for `totalDiscount`) from `tx_order_line`, but the table was only created with `quantity`, `unit_price`, `line_total`, and `tax_rate`.

---

# Fix

Added `discount_amount NUMERIC(15,2) DEFAULT 0` to `tx_order_line` via V23 migration, registered it in `sys_column`, and added it to the Sales Orders and Purchase Orders line tabs.

Suggested options:
1. Add `discount_amount` column to `tx_order_line` via a new Flyway migration and register it in `sys_column` / window fields. **(selected)**
2. Or, treat missing per-line discount as zero and compute header `discount_amount` as `subtotal - grand_total + tax` or default to 0 until a line-level discount feature is added.

---

# Validation

*(Tester to fill after fix)*

- `POST /api/v1/orders/{id}/calculate` returns success for orders with lines.
- Header totals are persisted.
- Zero-line order returns all-zero totals.
- Unknown order returns 404.

---

# Files Changed

- `backend/src/main/resources/db/migration/V23__add_order_line_discount_column.sql`
- `ai/project/schema/transactions/tx_order_line.sql`
- `ai/project/scripts/verify-prd-007-data.sql`

---

# Related Documents

- Parent PRD: `ai/project/prd/PRD-007-sales-order-workflow-customer-management.md`
- Parent Task: `ai/project/tasks/TASK-074-implement-order-auto-calculation.md`
- Change Summary: `ai/project/changes/CHANGE-TASK-074.md`
- Test Report: `ai/project/tests/TEST-TASK-074.md`
