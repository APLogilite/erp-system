---
id: TASK-156
title: Create attendance table
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
estimated_hours: 3
actual_hours:
parent_prd: PRD-025
prd_version: 1.0.0
prd_branch: prd/PRD-025-attendance-leave-expenses
base_branch: main
merge_target: prd/PRD-025-attendance-leave-expenses
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-155
  - TASK-157
  - TASK-158
  - TASK-159
depends_on:
  - TASK-152
blocks:
  - TASK-158
  - TASK-159
labels:
  - hr
  - attendance
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

Create the database table for daily attendance records.

---

# Description

- Create `hr_attendance` table
- Register table in metadata
- Add FK to employee

---

# Acceptance Criteria

- [ ] `[SE]` Table created
- [ ] `[SE]` Metadata registered
- [ ] `[QA]` Table verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Attendance status: Present, Absent, Late, On Leave

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

- PRD-025 — Attendance, Leave & Expenses
