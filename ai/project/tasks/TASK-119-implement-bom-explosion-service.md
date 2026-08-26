---
id: TASK-119
title: Implement BOM explosion service
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
parent_prd: PRD-016
prd_version: 1.0.0
prd_branch: prd/PRD-016-bom-work-centers-routings
base_branch: main
merge_target: prd/PRD-016-bom-work-centers-routings
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-116
  - TASK-117
  - TASK-118
depends_on:
  - TASK-116
  - TASK-117
  - TASK-118
blocks:
  - TASK-121
labels:
  - manufacturing
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

Compute component requirements for a finished product quantity from its default BOM.

---

# Description

- Create `BomExplosionService`
- Input: productId, quantity
- Find default BOM for product
- Return list of component products and required quantities
- Single-level explosion only in v1.0
- Detect and reject recursive BOM references
- Endpoint: `POST /api/v1/manufacturing/boms/{id}/explode`

---

# Acceptance Criteria

- [ ] `[SE]` Service returns correct component quantities
- [ ] `[SE]` Uses default BOM
- [ ] `[SE]` Rejects recursive BOMs
- [ ] `[SE]` Endpoint returns `ApiResponse<T>`
- [ ] `[QA]` Explosion matches BOM quantities

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Scale quantities by requested finished good quantity
- Handle UOM conversions if needed

---

# Files Expected

- `BomExplosionService.java`
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

- PRD-016 — Product BOM, Work Centers & Routings
