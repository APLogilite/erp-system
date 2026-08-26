---
id: TASK-099
title: Implement purchase order auto-calculation service
type: Feature
scope: backend
status: PLANNED
priority: High
owner: planner
assigned_to:
assigned_branch:
locked: false
created: 2026-08-26
updated: 2026-08-26
started:
completed:
estimated_hours: 4
actual_hours:
parent_prd: PRD-011
prd_version: 1.0.0
prd_branch: prd/PRD-011-purchase-requisition-order
base_branch: main
merge_target: prd/PRD-011-purchase-requisition-order
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-097
  - TASK-098
  - TASK-100
depends_on:
  - TASK-097
  - TASK-098
blocks:
  - TASK-100
labels:
  - purchase
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

Compute purchase order totals automatically when lines are added or updated.

---

# Description

- Create `PurchaseOrderCalculationService`
- Calculate line total = quantity × unit price
- Calculate subtotal = sum of line totals
- Calculate tax per line (if tax rate available)
- Calculate total = subtotal + tax
- Update PO header totals on line changes
- Reuse calculation on both backend save and frontend display

---

# Acceptance Criteria

- [ ] `[SE]` Service calculates line totals, subtotal, tax, total correctly
- [ ] `[SE]` Totals update when PO lines change
- [ ] `[QA]` PO total matches sum of lines
- [ ] `[QA]` Tax calculation is correct

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use BigDecimal
- Tax calculation may be simple percentage in v1.0

---

# Files Expected

- `PurchaseOrderCalculationService.java`
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

- PRD-011 — Purchase Requisition & Purchase Order
