---
id: TASK-124
title: Implement basic MRP report
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
  - TASK-123
depends_on:
  - TASK-120
  - TASK-123
blocks: []
labels:
  - manufacturing
  - backend
  - reporting
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

Provide a basic material requirements report based on MO demand and inventory balances.

---

# Description

- Create `BasicMRPService`
- Inputs: product, date range
- Outputs per product:
  - required qty (from confirmed MO component lines)
  - on-hand qty
  - reserved qty
  - available qty
  - net requirement
- Endpoint: `GET /api/v1/manufacturing/mrp`
- Seed MRP report window/screen

---

# Acceptance Criteria

- [ ] `[SE]` MRP query aggregates demand and supply
- [ ] `[SE]` Endpoint returns `ApiResponse<T>`
- [ ] `[QA]` Report shows correct net requirements

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Does not generate planned orders in v1.0
- Simple net requirement calculation

---

# Files Expected

- `BasicMRPService.java`
- Controller endpoint
- Report window seed
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

- PRD-017 — Manufacturing Orders & Basic MRP
