---
id: TASK-114
title: Implement count completion and adjustment service
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
parent_prd: PRD-015
prd_version: 1.0.0
prd_branch: prd/PRD-015-inventory-adjustments-valuation
base_branch: main
merge_target: prd/PRD-015-inventory-adjustments-valuation
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-112
  - TASK-113
  - TASK-115
depends_on:
  - TASK-112
  - TASK-113
  - TASK-110
blocks:
  - TASK-115
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

Generate adjustment stock moves from physical inventory count differences and direct adjustments.

---

# Description

- Create `PhysicalInventoryCompletionService`
- On count complete:
  - For each line, compute difference = counted qty - system qty
  - Create ADJUSTMENT stock move for non-zero differences
  - Update inventory balance
  - Status → Completed
- Direct adjustment also creates ADJUSTMENT stock move
- Endpoint: `POST /api/v1/inventory/physical-inventories/{id}/complete`

---

# Acceptance Criteria

- [ ] `[SE]` Count completion creates adjustment moves
- [ ] `[SE]` Balances updated correctly
- [ ] `[SE]` Direct adjustment creates stock move
- [ ] `[QA]` Difference posts to inventory
- [ ] `[QA]` Negative adjustment blocked if it would create negative on-hand

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reuse stock move completion service from PRD-014
- Single transaction

---

# Files Expected

- `PhysicalInventoryCompletionService.java`
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

- PRD-015 — Inventory Adjustments, Counting & Valuation
