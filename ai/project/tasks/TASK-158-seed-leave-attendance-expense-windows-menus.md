---
id: TASK-158
title: Seed leave, attendance, and expense windows, tabs, fields, and menus
type: Feature
scope: both
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
estimated_hours: 6
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
  - TASK-156
  - TASK-157
  - TASK-159
depends_on:
  - TASK-155
  - TASK-156
  - TASK-157
blocks:
  - TASK-159
labels:
  - hr
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

Make leave, attendance, and expense forms available in the runtime UI.

---

# Description

- Create Leave Type and Leave Request windows
- Create Attendance window
- Create Expense Category and Expense Claim windows
- Add Human Resources menu entries
- Add action buttons for approve/reject/reimburse

---

# Acceptance Criteria

- [ ] `[SE]` All windows render
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can request leave
- [ ] `[QA]` User can enter attendance
- [ ] `[QA]` User can submit expense claim

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Users see own data unless HR/approver

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

- PRD-025 — Attendance, Leave & Expenses
