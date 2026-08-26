---
id: TASK-146
title: Create timesheet and billing rate tables
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
parent_prd: PRD-023
prd_version: 1.0.0
prd_branch: prd/PRD-023-timesheets-project-billing
base_branch: main
merge_target: prd/PRD-023-timesheets-project-billing
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-147
  - TASK-148
  - TASK-149
  - TASK-150
depends_on:
  - TASK-145
blocks:
  - TASK-147
  - TASK-148
labels:
  - project
  - timesheet
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

Create the database tables for timesheets and billing rates.

---

# Description

- Create `tm_timesheet` and `tm_timesheet_line` tables
- Create `tm_billing_rate` table
- Register tables in metadata
- Add FKs to employee, project, task

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

- Timesheet line tracks billable flag and hours

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

- PRD-023 — Timesheets & Project Billing
