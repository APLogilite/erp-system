---
id: BUG-002
title: Credit override transition fails because override status text exceeds tx_order_status_log.to_status varchar(30)
status: READY_FOR_TEST
priority: High
severity: High
owner: Software Engineer
assigned_to: Software Engineer
assigned_branch: fix/BUG-002
locked: false
created: 2026-08-18
updated: 2026-08-19
started: 2026-08-19
completed: 2026-08-19
parent_prd: PRD-007
parent_task: TASK-075
reported_by: QA Engineer
detected_in: prd/PRD-007-sales-order-workflow-customer-management
related_test: TEST-TASK-075
fix_summary:
verification_report:
history:
  - created
---

# Summary

When a manager attempts to confirm an order that fails credit check with `override: true`, the transition fails with a database error because the generated `to_status` string (e.g., `confirmed (credit override: Manager approved)`) exceeds the `tx_order_status_log.to_status` `VARCHAR(30)` limit.

---

# Problem

Manager credit override is non-functional and leaks a raw DB error to the API consumer.

---

# Expected Behaviour

Transition succeeds, the order status is updated to `confirmed`, and the audit log records the override (either by truncating/shortening the status text, increasing the column size, or adding a separate `override_reason` column).

---

# Actual Behaviour

API response:
```json
{
  "success": false,
  "data": null,
  "message": "ERROR: value too long for type character varying(30)",
  "errorCode": "DATABASE_ERROR",
  "details": []
}
```

---

# Steps To Reproduce

1. Start backend with fresh DB.
2. Log in as admin and obtain a token.
3. Locate a draft sales order.
4. Set the partner's `credit_limit` to a value lower than the order total.
5. Call `POST /api/v1/orders/{id}/transition` with body `{"targetStatus":"confirmed","override":true,"overrideReason":"Manager approved"}`.
6. Observe the `DATABASE_ERROR` response.

---

# Root Cause

`OrderWorkflowService.transition` constructs `toStatus = target + " (credit override: " + overrideReason + ")"` and inserts it into `tx_order_status_log.to_status`, which is defined as `VARCHAR(30)`.

---

# Fix

Added `override_reason TEXT` column to `tx_order_status_log` via V24 migration and updated `OrderWorkflowService` to store the override reason separately while keeping `to_status` as the plain status.

Suggested options:
1. Increase `tx_order_status_log.to_status` (and `from_status`) to `VARCHAR(100)` or `TEXT` via Flyway migration.
2. Or, keep `to_status` as the plain status and store `override_reason` in a new nullable column. **(selected)**

---

# Validation

*(Tester to fill after fix)*

- Set partner credit limit below order total.
- Attempt normal confirm → blocked with credit failure.
- Attempt confirm with `override: true` and `overrideReason` → succeeds.
- Verify order status becomes `confirmed`.
- Verify audit log row exists with override information.
- Verify non-manager role cannot override.

---

# Files Changed

- `backend/src/main/resources/db/migration/V24__add_status_log_override_reason.sql`
- `backend/src/main/java/com/erp/modules/salesflow/service/OrderWorkflowService.java`
- `ai/project/schema/transactions/tx_order_status_log.sql`
- `ai/project/scripts/verify-prd-007-data.sql`

---

# Related Documents

- Parent PRD: `ai/project/prd/PRD-007-sales-order-workflow-customer-management.md`
- Parent Task: `ai/project/tasks/TASK-075-implement-customer-credit-check.md`
- Related Bug: BUG-001
- Test Report: `ai/project/tests/TEST-TASK-075.md`
