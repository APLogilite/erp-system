---
id: TASK-117
title: Create routing and BOM tables
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
estimated_hours: 5
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
  - TASK-118
  - TASK-119
depends_on:
  - TASK-116
blocks:
  - TASK-118
  - TASK-119
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

Create the database tables for routings and bills of materials.

---

# Description

- Create `mfg_routing` and `mfg_routing_operation` tables
- Create `mfg_bom` and `mfg_bom_line` tables
- Register all tables in metadata
- Add FKs to product, operation, work center

---

# Acceptance Criteria

- [ ] `[SE]` Routing and BOM tables created
- [ ] `[SE]` Metadata registered
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- BOM line component can be raw material or sub-assembly
- Routing operation sequence uses integer order

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

- PRD-016 — Product BOM, Work Centers & Routings
