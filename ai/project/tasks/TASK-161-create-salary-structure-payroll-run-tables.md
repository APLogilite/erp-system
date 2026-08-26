---
id: TASK-161
title: Create salary structure and payroll run tables
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
  - TASK-160
  - TASK-162
  - TASK-163
  - TASK-164
  - TASK-165
depends_on:
  - TASK-160
blocks:
  - TASK-162
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

Create the database tables for salary structures and payroll runs.

---

# Description

- Create `hr_salary_structure` and `hr_salary_structure_line` tables
- Create `hr_payroll_run` table
- Register tables in metadata
- Add FK from contract to salary structure

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[SE]` Contract table updated with salary structure FK
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Salary structure lines link to salary rules

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
