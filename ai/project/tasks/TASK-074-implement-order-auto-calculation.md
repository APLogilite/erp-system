---
id: TASK-074
title: Implement Order auto-calculation backend service
type: Feature
scope: backend
status: PLANNED
priority: High
owner: developer
assigned_to:
assigned_branch:
locked: false
created: 2026-07-29
updated: 2026-07-29
started:
completed:
estimated_hours: 3
actual_hours:
parent_prd: PRD-007
prd_version: 1.0.0
prd_branch: prd/PRD-007-sales-order-workflow-customer-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-077
depends_on: []
blocks:
  - TASK-077
labels:
  - backend
  - prd-007
review_required: true
test_required: true
automation_required: false
change_summary:
test_report:
test_script:
history:
  - created
---

# Goal

Make order header totals (subtotal, tax, discount, grand_total) server-calculated from line items — eliminating manual math and keeping tx_order consistent with its tx_order_line rows.

---

# Description

Implement `OrderCalculationService` with endpoint:

```
POST /api/v1/orders/{id}/calculate
Response: { "subtotal": 950.00, "discountAmount": 50.00, "taxAmount": 90.00, "grandTotal": 990.00 }
```

Logic (mirrors TASK-067 quotation calculation):
1. Load all tx_order_line rows for the order
2. line_total = (quantity × unit_price) − (line discount if any)
3. subtotal = Σ line_total; discount_amount = Σ line discounts; tax_amount = Σ(line_total × tax_rate/100); grand_total = subtotal − discount + tax
4. Persist header totals to tx_order
5. Return totals

Money math with BigDecimal, 2-decimal HALF_UP rounding. Empty order → all-zero totals.

---

# Acceptance Criteria

- [ ] `[SE]` Calculate endpoint returns correct totals for multi-line orders
- [ ] `[SE]` Totals persisted to tx_order
- [ ] `[SE]` Zero-line order returns zeros without error
- [ ] `[QA]` Unknown order id returns 404
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-007 FR-002

---

# Files Expected

- `backend/src/main/java/com/erp/modules/salesflow/service/OrderCalculationService.java` (new)
- `backend/src/main/java/com/erp/modules/salesflow/controller/OrderCalculationController.java` (new)
- `backend/src/main/java/com/erp/modules/salesflow/dto/OrderTotalsResponse.java` (new)

---

# Developer Notes

*(maintained by SE)*

---

# Tester Notes

*(maintained by QA)*

---

# Review Notes

*(maintained by reviewer)*

---

# Task History

2026-07-29

Product Manager

Created Task (PLANNED) — PRD-007 approved by user

---

# Related Documents

- PRD-007 — Sales Order Workflow & Customer Management (FR-002)
