---
id: TASK-144
title: Seed project, task, milestone, and assignment windows, tabs, fields, and menus
type: Feature
scope: both
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
parent_prd: PRD-022
prd_version: 1.0.0
prd_branch: prd/PRD-022-projects-tasks-resource-planning
base_branch: main
merge_target: prd/PRD-022-projects-tasks-resource-planning
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-142
  - TASK-143
  - TASK-145
depends_on:
  - TASK-142
  - TASK-143
blocks:
  - TASK-145
labels:
  - project
  - metadata
  - ui
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

Make project management forms available in the runtime UI.

---

# Description

- Create Project window with tasks, milestones, and assignments tabs
- Create Task window (optional standalone)
- Add Projects menu group and entries
- Configure access

---

# Acceptance Criteria

- [ ] `[SE]` Project window renders with tabs
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can create a project
- [ ] `[QA]` User can add tasks and assignments

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Assignment uses employee lookup (PRD-024)

---

# Files Expected

- Flyway seed migration

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
