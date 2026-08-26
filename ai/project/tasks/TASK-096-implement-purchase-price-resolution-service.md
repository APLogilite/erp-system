---
id: TASK-096
title: Implement purchase price resolution service
type: Feature
scope: backend
status: IN_DEVELOPMENT
priority: High
owner: planner
assigned_to: Software Engineer
assigned_branch: feature/TASK-096
locked: true
created: 2026-08-26
updated: 2026-08-26
started: 2026-08-26
completed:
estimated_hours: 5
actual_hours: 0
parent_prd: PRD-010
prd_version: 1.0.0
prd_branch: prd/PRD-010-vendor-management-price-lists
base_branch: main
merge_target: prd/PRD-010-vendor-management-price-lists
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-094
  - TASK-095
depends_on:
  - TASK-094
  - TASK-095
blocks:
  - TASK-099
labels:
  - price-list
  - backend
review_required: true
test_required: true
automation_required: true
change_summary:
test_report:
test_script:
history:
  - created
---

# Goal

Provide a backend service that resolves the correct purchase unit price for a product from a vendor's active price list.

---

# Description

- Create `PurchasePriceResolutionService`
- Inputs: vendorId, productId, quantity, date
- Find active price list for vendor with valid date range
- Find matching product/UOM line with minimum quantity <= requested quantity
- Return highest applicable min-qty price
- Fallback to product base purchase price if no price list match
- Expose endpoint `POST /api/v1/purchase/price-lists/resolve`

---

# Acceptance Criteria

- [ ] `[SE]` Service returns correct price by quantity break
- [ ] `[SE]` Fallback to product price works
- [ ] `[SE]` Endpoint returns `ApiResponse<T>`
- [ ] `[QA]` Price lookup returns expected value for matching price list
- [ ] `[QA]` Fallback returns product price when no price list exists
- [ ] `[QA]` Tenant isolation enforced

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Handle date overlap validation at data layer
- Use BigDecimal for prices

---

# Files Expected

- `PurchasePriceResolutionService.java`
- Controller endpoint
- Unit tests

---

# Developer Notes

---

# Tester Notes

---

# Review Notes

---

# Task History

2026-08-26 — Product Manager — Created task

---

# Related Documents

- PRD-010 — Vendor Management & Purchase Price Lists
