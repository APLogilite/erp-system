---
id: TASK-065
title: Implement Discount Resolution backend service
type: Feature
scope: backend
status: PLANNED
priority: Medium
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
parent_prd: PRD-006
prd_version: 1.0.0
prd_branch: prd/PRD-006-sales-quotation-price-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-062
  - TASK-067
depends_on:
  - TASK-062
blocks:
  - TASK-067
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

Automatically find and apply the best applicable discount rule for a given customer + product + quantity context so quotation and order lines get correct discounts without manual calculation.

---

# Description

Implement `DiscountResolutionService` in the backend. Logic:

1. Find all active discount rules (`is_active = true`, valid today) matching:
   - `partner_id` = customer OR `partner_id` IS NULL
   - `product_id` = product OR `product_id` IS NULL
   - `min_quantity` IS NULL OR `min_quantity` ≤ quantity
2. Compute effective discount for each candidate:
   - percentage → `discount_amount = unit_price × quantity × value / 100`
   - fixed_amount → `discount_amount = value` (per line, not per unit)
3. Return the rule yielding the highest discount_amount

Expose via the pricing endpoint — extend `POST /api/v1/pricing/resolve` response (from TASK-064) to include discount fields:
```
Response: { "unitPrice": 95.00, "source": "...", "discountPercent": 10.0, "discountAmount": 95.00, "discountRuleId": "uuid" }
```

---

# Acceptance Criteria

- [ ] `[SE]` Percentage discount computed as unit_price × quantity × value/100
- [ ] `[SE]` Fixed amount discount applied once per line
- [ ] `[SE]` Highest-value discount wins when multiple rules match
- [ ] `[SE]` Rules with min_quantity above the requested quantity are ignored
- [ ] `[SE]` Expired rules (valid_to < today) are ignored
- [ ] `[QA]` No matching rule returns zero discount fields (not an error)
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Integrates with PricingController from TASK-064 (same endpoint response extension)
- Reference PRD-006 FR-003

---

# Files Expected

- `backend/src/main/java/com/erp/modules/pricing/service/DiscountResolutionService.java` (new)
- `backend/src/main/java/com/erp/modules/pricing/dto/PriceResolveResponse.java` (modified — add discount fields)

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

- PRD-006 — Sales Quotation & Price Management (FR-003)
