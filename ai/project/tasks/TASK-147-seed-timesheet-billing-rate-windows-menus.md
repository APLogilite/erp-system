---
id: TASK-147
title: Seed timesheet and billing rate windows, tabs, fields, and menus
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
parent_prd: PRD-023
prd_version: 1.0.0
prd_branch: prd/PRD-023-timesheets-project-billing
base_branch: main
merge_target: prd/PRD-023-timesheets-project-billing
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-146
  - TASK-148
  - TASK-149
  - TASK-150
depends_on:
  - TASK-146
blocks:
  - TASK-148
  - TASK-149
labels:
  - project
  - timesheet
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

Make timesheet and billing rate forms available in the runtime UI.

---

# Description

- Create Timesheet window with lines tab (weekly grid)
- Create Billing Rate window
- Add action buttons: Submit, Approve, Reject
- Add Projects menu entries
- Configure access

---

# Acceptance Criteria

- [ ] `[SE]` Timesheet window renders
- [ ] `[SE]` Billing Rate window renders
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can log hours per project/task

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Weekly grid may be simple list in v1.0

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

- PRD-023 — Timesheets & Project Billing
