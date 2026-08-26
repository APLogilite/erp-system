---
id: TASK-143
title: Create task, milestone, and assignment tables
type: Feature
scope: database
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
estimated_hours: 4
actual_hours:
parent_prd: PRD-022
prd_version: 1.0.0
prd_branch: prd/PRD-022-projects-tasks-resource-planning
base_branch: main
merge_target: prd/PRD-022-projects-tasks-resource-planning
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-142
  - TASK-144
  - TASK-145
depends_on:
  - TASK-142
blocks:
  - TASK-144
labels:
  - project
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

Create the database tables for project tasks, milestones, and resource assignments.

---

# Description

- Create `pm_task` table with self-referencing parent
- Create `pm_milestone` table
- Create `pm_task_assignment` table
- Register tables in metadata

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

- Task supports hierarchical sub-tasks

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

- PRD-022 — Projects, Tasks & Resource Planning
