---
id: TASK-120
title: Create manufacturing order tables
type: Feature
scope: database
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
parent_prd: PRD-017
prd_version: 1.0.0
prd_branch: prd/PRD-017-manufacturing-orders-mrp
base_branch: main
merge_target: prd/PRD-017-manufacturing-orders-mrp
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-121
  - TASK-122
  - TASK-123
  - TASK-124
depends_on:
  - TASK-119
blocks:
  - TASK-121
  - TASK-122
labels:
  - manufacturing
  - metadata
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

Create the database tables for manufacturing orders and component lines.

---

# Description

- Create `mfg_order` and `mfg_order_line` tables
- Register tables in metadata
- Add document sequence `MO`
- Add FKs to product, BOM, routing, warehouse

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[SE]` Document sequence seeded
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- MO line tracks required and consumed quantities

---

# Files Expected

- Flyway migration

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
