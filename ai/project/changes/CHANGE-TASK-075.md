---
change_id: CHANGE-TASK-075
task_id: TASK-075
parent_prd: PRD-007
author: Software Engineer
date: 2026-08-14
---

# Change Report — TASK-075: Implement Customer Credit Check backend service

## Summary
Implemented customer credit evaluation for sales orders. The check runs automatically when an order transitions from `draft` to `confirmed`, and manager override is supported with audit logging.

## Files Added
- `backend/src/main/java/com/erp/modules/salesflow/service/CreditCheckService.java`
- `backend/src/main/java/com/erp/modules/salesflow/controller/CreditCheckController.java`
- `backend/src/main/java/com/erp/modules/salesflow/dto/CreditCheckResponse.java`

## Files Modified
- `backend/src/main/java/com/erp/modules/salesflow/service/OrderWorkflowService.java`
- `backend/src/main/java/com/erp/modules/salesflow/dto/TransitionRequest.java`
- `backend/src/main/java/com/erp/modules/salesflow/controller/OrderWorkflowController.java`
- `ai/project/scripts/verify-prd-007-data.sql`
- `ai/project/tasks/TASK-075-implement-customer-credit-check.md`

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/orders/{id}/check-credit` | Evaluate credit for an order |
| POST | `/api/v1/orders/{id}/transition` | Now blocks `draft → confirmed` on credit failure unless override is supplied |

## Business Rules
- outstanding = Σ open order totals (confirmed/delivered) + Σ unpaid invoice due amounts (validated/partially_paid)
- approved = not on credit_hold AND (credit_limit IS NULL OR outstanding + orderAmount ≤ credit_limit)
- Override requires `sys_admin`, `tnt_admin`, or `sales_manager` role.
- Overrides are audit-logged with reason in `tx_order_status_log`.

## Validation Results
- `mvn clean compile` — PASS
- `mvn test` — 36 tests, 0 failures, BUILD SUCCESS

## Notes
- Null credit_limit means no limit is enforced.
