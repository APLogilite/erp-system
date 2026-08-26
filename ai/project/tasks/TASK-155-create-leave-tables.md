---
id: TASK-155
title: Create leave type, balance, and request tables
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
parent_prd: PRD-025
prd_version: 1.0.0
prd_branch: prd/PRD-025-attendance-leave-expenses
base_branch: main
merge_target: prd/PRD-025-attendance-leave-expenses
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-156
  - TASK-157
  - TASK-158
  - TASK-159
depends_on:
  - TASK-152
blocks:
  - TASK-158
labels:
  - hr
  - leave
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

Create the database tables for leave types, balances, and requests.

---

# Description

- Create `hr_leave_type` table
- Create `hr_leave_balance` table
- Create `hr_leave_request` table
- Register tables in metadata
- Seed default leave types

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[SE]` Default leave types seeded
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Leave balance links employee and leave type

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
