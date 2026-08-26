---
id: TASK-145
title: Implement project dashboard API
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
  - TASK-143
  - TASK-144
depends_on:
  - TASK-142
  - TASK-143
  - TASK-144
blocks:
  - TASK-149
labels:
  - project
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

Provide project health KPIs for the dashboard.

---

# Description

- Create `ProjectDashboardService`
- KPIs: total tasks, completed tasks, overdue tasks, budget vs actual
- Endpoint: `GET /api/v1/projects/{id}/dashboard`
- Seed dashboard screen/window

---

# Acceptance Criteria

- [ ] `[SE]` Dashboard API returns KPIs
- [ ] `[SE]` Dashboard screen renders
- [ ] `[QA]` KPIs match project data

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Actual cost may be zero until timesheets (PRD-023)

---

# Files Expected

- `ProjectDashboardService.java`
- Controller endpoint
- Dashboard window seed
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

- PRD-022 — Projects, Tasks & Resource Planning
