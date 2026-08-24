---
change_id: CHANGE-TASK-073
task_id: TASK-073
parent_prd: PRD-007
author: Software Engineer
date: 2026-08-14
---

# Change Report — TASK-073: Implement Order status workflow backend service

## Summary
Implemented backend enforcement of the sales order status lifecycle with an audit log. Added REST endpoints to transition an order and query allowed next statuses.

## Files Added
- `backend/src/main/resources/db/migration/V20__create_order_status_log.sql`
- `backend/src/main/java/com/erp/modules/salesflow/service/OrderWorkflowService.java`
- `backend/src/main/java/com/erp/modules/salesflow/controller/OrderWorkflowController.java`
- `backend/src/main/java/com/erp/modules/salesflow/dto/TransitionRequest.java`
- `backend/src/main/java/com/erp/modules/salesflow/dto/TransitionResponse.java`
- `ai/project/schema/transactions/tx_order_status_log.sql`

## Files Modified
- `ai/project/scripts/verify-prd-007-data.sql`
- `ai/project/tasks/TASK-073-implement-order-status-workflow.md`

## Database Changes
- New table `tx_order_status_log` for audit trail of status transitions.
- Registered table/columns in `sys_table` / `sys_column`.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/orders/{id}/transition` | Transition order to target status |
| GET | `/api/v1/orders/{id}/allowed-transitions` | List valid next statuses |

### Transition rules
- `draft` → `confirmed`, `cancelled`
- `confirmed` → `delivered`, `cancelled`
- `delivered` → `invoiced`, `cancelled`
- `invoiced` → `paid`, `cancelled`
- `paid` → `cancelled`
- Cancel blocked when a non-cancelled invoice exists for the order.

## Validation Results
- `mvn clean compile` — PASS
- `mvn test` — 36 tests, 0 failures, BUILD SUCCESS

## Notes
- Status values are compared case-insensitively.
- Every successful transition inserts one row into `tx_order_status_log`.
