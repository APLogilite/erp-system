---
id: TASK-111
title: Implement inventory reservation hook
type: Feature
scope: backend
status: PLANNED
priority: Medium
owner: planner
assigned_to:
assigned_branch:
locked: false
created: 2026-08-26
updated: 2026-08-26
started:
completed:
estimated_hours: 5
actual_hours:
parent_prd: PRD-014
prd_version: 1.0.0
prd_branch: prd/PRD-014-stock-movements-logistics
base_branch: main
merge_target: prd/PRD-014-stock-movements-logistics
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-108
  - TASK-109
  - TASK-110
depends_on:
  - TASK-108
  - TASK-110
blocks:
  - TASK-122
labels:
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

Reserve inventory for sales orders and manufacturing orders so available quantity is accurate.

---

# Description

- Create `InventoryReservationService`
- Methods:
  - `reserve(orderType, orderId, productId, locatorId, qty)`
  - `release(orderType, orderId, productId, locatorId, qty)`
- Update `m_inventory_reservation` and `m_inventory_balance.reserved_qty`
- `available_qty = on_hand_qty - reserved_qty`
- Integrate with sales order confirmation and MO confirmation

---

# Acceptance Criteria

- [ ] `[SE]` Reservation reduces available qty
- [ ] `[SE]` Release restores available qty
- [ ] `[SE]` No negative available qty allowed
- [ ] `[QA]` Reservation decreases available balance

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use same transaction model as stock moves
- May be called from PRD-007 sales order and PRD-017 MO workflows

---

# Files Expected

- `InventoryReservationService.java`
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

- PRD-014 — Stock Movements & Logistics
