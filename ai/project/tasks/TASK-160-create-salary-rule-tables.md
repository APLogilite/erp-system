---
id: TASK-160
title: Create salary rule category and rule tables
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
parent_prd: PRD-026
prd_version: 1.0.0
prd_branch: prd/PRD-026-payroll-rules-payslips-compliance
base_branch: main
merge_target: prd/PRD-026-payroll-rules-payslips-compliance
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-161
  - TASK-162
  - TASK-163
  - TASK-164
  - TASK-165
depends_on:
  - TASK-128
blocks:
  - TASK-161
  - TASK-163
labels:
  - hr
  - payroll
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

Create the database tables for salary rule categories and salary rules.

---

# Description

- Create `hr_salary_rule_category` table
- Create `hr_salary_rule` table
- Register tables in metadata
- Seed default categories: Earnings, Deductions, Contributions, Net

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

- Rule computation types: Fixed Amount, Percentage

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

- PRD-026 — Payroll Rules, Payslips & Compliance
