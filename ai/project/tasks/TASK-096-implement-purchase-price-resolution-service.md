---
id: TASK-096
title: Implement purchase price resolution service
type: Feature
scope: backend
status: READY_FOR_TEST
priority: High
owner: planner
assigned_to: Software Engineer
assigned_branch: feature/TASK-096
locked: false
created: 2026-08-26
updated: 2026-08-26
started: 2026-08-26
completed: 2026-08-26
estimated_hours: 5
actual_hours: 1.5
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
change_summary: ai/project/changes/CHANGE-TASK-096.md
test_report:
test_script:
history:
  - created
  - 2026-08-26 — Software Engineer — Started implementation
  - 2026-08-26 — Software Engineer — Completed implementation, all 36 backend tests pass, merged to main
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

- [x] `[SE]` Service returns correct price by quantity break
- [x] `[SE]` Fallback to product price works
- [x] `[SE]` Endpoint returns `ApiResponse<T>`
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
