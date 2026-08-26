---
id: TASK-162
title: Create payslip tables
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
parent_prd: PRD-026
prd_version: 1.0.0
prd_branch: prd/PRD-026-payroll-rules-payslips-compliance
base_branch: main
merge_target: prd/PRD-026-payroll-rules-payslips-compliance
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-160
  - TASK-161
  - TASK-163
  - TASK-164
  - TASK-165
depends_on:
  - TASK-161
blocks:
  - TASK-163
  - TASK-164
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

Create the database tables for payslips and payslip lines.

---

# Description

- Create `hr_payslip` and `hr_payslip_line` tables
- Register tables in metadata
- Add FKs to payroll run and employee

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

- Payslip line links to salary rule

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
