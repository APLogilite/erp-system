---
id: TASK-123
title: Implement material consumption and finished goods production
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
estimated_hours: 6
actual_hours:
parent_prd: PRD-017
prd_version: 1.0.0
prd_branch: prd/PRD-017-manufacturing-orders-mrp
base_branch: main
merge_target: prd/PRD-017-manufacturing-orders-mrp
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-120
  - TASK-121
  - TASK-122
  - TASK-124
depends_on:
  - TASK-120
  - TASK-121
  - TASK-122
  - TASK-110
  - TASK-115
blocks:
  - TASK-124
labels:
  - manufacturing
  - inventory
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

Record material issue to production and finished goods receipt into inventory.

---

# Description

- Create `MaterialConsumptionService`
  - Creates PRODUCTION stock move outbound
  - Updates MO line consumed qty
- Create `FinishedGoodsProductionService`
  - Creates PRODUCTION stock move inbound
  - Reduces reserved qty, increases on-hand
- Create `ManufacturingOrderCompletionService`
  - Releases remaining reservations
  - Status → Completed
- Endpoints for issue, produce, complete

---

# Acceptance Criteria

- [ ] `[SE]` Consumption reduces component on-hand
- [ ] `[SE]` Production increases finished goods on-hand
- [ ] `[SE]` MO completion releases reservations
- [ ] `[QA]` End-to-end MO issue → produce → complete updates inventory

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use stock move completion service
- Update average cost of finished goods (PRD-015)

---

# Files Expected

- `MaterialConsumptionService.java`
- `FinishedGoodsProductionService.java`
- `ManufacturingOrderCompletionService.java`
- Controller endpoints
- Integration tests

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

- PRD-017 — Manufacturing Orders & Basic MRP
