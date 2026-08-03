---
id: TASK-067
title: Implement Quotation calculation API
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
  - TASK-063
  - TASK-064
  - TASK-065
depends_on:
  - TASK-063
  - TASK-064
  - TASK-065
blocks:
  - TASK-068
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

Provide a backend calculation endpoint that recomputes quotation line totals and header totals (subtotal, discount, tax, grand total) so the frontend can show live amounts as users edit lines, with all math authoritative on the server.

---

# Description

Implement `QuotationService.calculateTotals(UUID quotationId)` with REST endpoint:

```
POST /api/v1/quotations/{id}/calculate
Response: { "subtotal": 950.00, "discountAmount": 95.00, "taxAmount": 85.50, "grandTotal": 940.50 }
```

Logic:
1. Load all lines for the quotation
2. For each line: if unit_price not set or product/quantity changed, resolve price via PriceResolutionService (TASK-064) and discount via DiscountResolutionService (TASK-065)
3. `line_total = (quantity × unit_price) − discount_amount`
4. `subtotal = Σ line_total`
5. `discount_amount (header) = Σ line discount_amount`
6. `tax_amount = Σ (line_total × tax_rate / 100)`
7. `grand_total = subtotal − discount_amount + tax_amount`
8. Persist updated line totals and header totals to the database
9. Return the header totals

Also implement `POST /api/v1/quotations/calculate-line` for single-line preview (used by frontend when user is typing in a line before saving):
```
Request:  { "productId": "uuid", "partnerId": "uuid", "quantity": 10, "taxRate": 10 }
Response: { "unitPrice": 95.00, "discountPercent": 10.0, "discountAmount": 95.00, "lineTotal": 855.00, "taxAmount": 85.50 }
```

---

# Acceptance Criteria

- [ ] `[SE]` Calculate endpoint updates and returns all four header totals correctly
- [ ] `[SE]` Line totals persisted after calculation
- [ ] `[SE]` Price auto-resolved for lines missing unit_price
- [ ] `[SE]` Discount auto-applied from matching rules
- [ ] `[SE]` calculate-line endpoint previews a single line without persisting
- [ ] `[QA]` Calculation with zero lines returns all-zero totals (no error)
- [ ] `[QA]` Unknown quotation id returns 404
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- All money math uses BigDecimal with 2-decimal rounding (HALF_UP)
- Reference PRD-006 FR-005 (Live Total Calculation)

---

# Files Expected

- `backend/src/main/java/com/erp/modules/quotation/service/QuotationService.java` (new)
- `backend/src/main/java/com/erp/modules/quotation/controller/QuotationController.java` (new)
- `backend/src/main/java/com/erp/modules/quotation/dto/QuotationTotalsResponse.java` (new)
- `backend/src/main/java/com/erp/modules/quotation/dto/LineCalculateRequest.java` (new)
- `backend/src/main/java/com/erp/modules/quotation/dto/LineCalculateResponse.java` (new)

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

- PRD-006 — Sales Quotation & Price Management (FR-005)
