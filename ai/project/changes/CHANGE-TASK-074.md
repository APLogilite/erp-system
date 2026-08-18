---
change_id: CHANGE-TASK-074
task_id: TASK-074
parent_prd: PRD-007
author: Software Engineer
date: 2026-08-14
---

# Change Report — TASK-074: Implement Order auto-calculation backend service

## Summary
Implemented an order totals calculation service that recalculates header totals from line items and persists the results.

## Files Added
- `backend/src/main/java/com/erp/modules/salesflow/service/OrderCalculationService.java`
- `backend/src/main/java/com/erp/modules/salesflow/controller/OrderCalculationController.java`
- `backend/src/main/java/com/erp/modules/salesflow/dto/OrderTotalsResponse.java`

## Files Modified
- `ai/project/scripts/verify-prd-007-data.sql`
- `ai/project/tasks/TASK-074-implement-order-auto-calculation.md`

## Database Changes
None — uses existing `tx_order` and `tx_order_line` tables.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/orders/{id}/calculate` | Recalculate and persist order totals |

### Response
```json
{
  "subtotal": 950.00,
  "discountAmount": 50.00,
  "taxAmount": 90.00,
  "grandTotal": 990.00
}
```

## Validation Results
- `mvn clean compile` — PASS
- `mvn test` — 36 tests, 0 failures, BUILD SUCCESS

## Notes
- Empty orders return all-zero totals.
- Rounding is HALF_UP to 2 decimal places.
- Unknown order id returns 404.
