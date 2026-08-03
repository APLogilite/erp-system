---
id: TASK-068
title: Implement Quotation to Order conversion
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
estimated_hours: 4
actual_hours:
parent_prd: PRD-006
prd_version: 1.0.0
prd_branch: prd/PRD-006-sales-quotation-price-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-060
  - TASK-067
depends_on:
  - TASK-060
  - TASK-067
blocks:
  - TASK-069
labels:
  - backend
  - prd-006
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

Convert an accepted quotation into a sales order (`tx_order` with `order_type='sales'`) with one API call, copying all header amounts and line items, generating the order number via the sequence service, and linking the quotation to the new order.

---

# Description

Add `QuotationService.convertToOrder(UUID quotationId)` and endpoint:

```
POST /api/v1/quotations/{id}/convert-to-order
Response: { "orderId": "uuid", "orderNumber": "SO-0001" }
```

Logic:
1. Load quotation; validate `status = 'accepted'` — otherwise 400 with message "Only accepted quotations can be converted"
2. Validate `converted_order_id IS NULL` — otherwise 400 "Quotation already converted"
3. Generate order number via DocumentSequenceService (`sales_order` sequence → SO-0001)
4. Insert `tx_order`: order_type='sales', order_date=today, partner_id from quotation, currency, subtotal/tax/discount/grand_total copied, status='draft'
5. Insert `tx_order_line` rows copied from quotation lines (product, description, quantity, uom, unit_price, line_total, tax_rate)
6. Update quotation: status='converted', converted_order_id=new order id
7. Run inside a single transaction (all-or-nothing)
8. Return order id + number

---

# Acceptance Criteria

- [ ] `[SE]` Accepted quotation converts successfully; order and lines created
- [ ] `[SE]` Order number auto-generated from sales_order sequence
- [ ] `[SE]` Quotation status updated to 'converted' with converted_order_id set
- [ ] `[SE]` Non-accepted quotation returns 400 with clear message
- [ ] `[SE]` Already-converted quotation returns 400 with clear message
- [ ] `[QA]` Rollback works: simulate failure mid-conversion leaves no partial order
- [ ] `[QA]` Converted order opens in the sales_order runtime form with all lines intact
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use `@Transactional` on the service method
- tx_order / tx_order_line are metadata-driven tables — write via JdbcTemplate/JPA following how existing business services persist to dynamic tables, or via PRD-001's DynamicCrudService if suitable
- Reference PRD-006 FR-006

---

# Files Expected

- `backend/src/main/java/com/erp/modules/quotation/service/QuotationService.java` (modified — add convertToOrder)
- `backend/src/main/java/com/erp/modules/quotation/controller/QuotationController.java` (modified — add endpoint)
- `backend/src/main/java/com/erp/modules/quotation/dto/ConvertToOrderResponse.java` (new)

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

Created Task (PLANNED) — PRD-006 approved by user

---

# Related Documents

- PRD-006 — Sales Quotation & Price Management (FR-006)
