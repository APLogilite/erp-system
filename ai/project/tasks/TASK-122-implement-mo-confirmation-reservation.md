---
id: TASK-122
title: Implement manufacturing order confirmation and component reservation
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
estimated_hours: 5
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
  - TASK-123
  - TASK-124
depends_on:
  - TASK-120
  - TASK-121
  - TASK-111
blocks:
  - TASK-123
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

Confirm manufacturing orders and reserve component inventory.

---

# Description

- Create `ManufacturingOrderConfirmationService`
- On confirm:
  - Validate components available (optional hard block)
  - Reserve component inventory
  - Status → Confirmed
- Endpoint: `POST /api/v1/manufacturing/orders/{id}/confirm`
- Release reservation on cancel

---

# Acceptance Criteria

- [ ] `[SE]` MO confirmation reserves components
- [ ] `[SE]` Available inventory decreases by reserved amount
- [ ] `[SE]` Cancel releases reservation
- [ ] `[QA]` Confirmed MO reduces available qty

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use reservation service from PRD-014
- Single transaction

---

# Files Expected

- `ManufacturingOrderConfirmationService.java`
- Controller endpoint
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
