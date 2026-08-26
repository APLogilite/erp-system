---
id: TASK-116
title: Create work center and operation tables
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
parent_prd: PRD-016
prd_version: 1.0.0
prd_branch: prd/PRD-016-bom-work-centers-routings
base_branch: main
merge_target: prd/PRD-016-bom-work-centers-routings
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-117
  - TASK-118
  - TASK-119
depends_on: []
blocks:
  - TASK-117
  - TASK-118
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

Create the database tables for work centers and operations.

---

# Description

- Create `mfg_work_center` table
- Create `mfg_operation` table
- Register tables in metadata
- Add FKs to warehouse and default work center

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Work center has capacity and cost rates
- Operation has setup/run times

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
