---
id: TASK-142
title: Create project type and project tables
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
  - TASK-143
  - TASK-144
  - TASK-145
depends_on: []
blocks:
  - TASK-143
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

Create the database tables for project types and projects.

---

# Description

- Create `pm_project_type` table
- Create `pm_project` table
- Register tables in metadata
- Add document sequence `PRJ`

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

- Project links to business partner (customer)

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
