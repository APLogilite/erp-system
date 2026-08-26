---
id: TASK-110
title: Implement stock move completion and balance update service
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
  - TASK-111
depends_on:
  - TASK-108
  - TASK-109
blocks:
  - TASK-111
  - TASK-114
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

Process stock moves and maintain accurate inventory balances atomically.

---

# Description

- Create `StockMoveCompletionService`
- On complete:
  - Validate source location has sufficient on-hand
  - Update source balance (decrement)
  - Update destination balance (increment)
  - Status → Completed
- Support RECEIPT, DELIVERY, TRANSFER, ADJUSTMENT, PRODUCTION moves
- Prevent negative on-hand
- Endpoint: `POST /api/v1/inventory/transfers/{id}/complete`

---

# Acceptance Criteria

- [ ] `[SE]` Source balance decremented
- [ ] `[SE]` Destination balance incremented
- [ ] `[SE]` Negative on-hand prevented
- [ ] `[SE]` Completion transactional
- [ ] `[QA]` Internal transfer updates both locations
- [ ] `[QA]` Over-issue is blocked

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use optimistic locking on balance rows
- Single transaction

---

# Files Expected

- `StockMoveCompletionService.java`
- Controller endpoint
- Unit/integration tests

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
