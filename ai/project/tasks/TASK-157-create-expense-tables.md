---
id: TASK-157
title: Create expense category and claim tables
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
  - TASK-155
  - TASK-156
  - TASK-158
  - TASK-159
depends_on:
  - TASK-152
blocks:
  - TASK-158
  - TASK-159
labels:
  - hr
  - expense
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

Create the database tables for expense categories and claims.

---

# Description

- Create `hr_expense_category` table
- Create `hr_expense_claim` and `hr_expense_claim_line` tables
- Register tables in metadata
- Seed default expense categories

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[SE]` Default categories seeded
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Expense category links to GL account

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
