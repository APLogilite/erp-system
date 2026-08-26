---
id: TASK-164
title: Implement payslip generation and approval
type: Feature
scope: backend
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
  - TASK-162
  - TASK-163
  - TASK-165
depends_on:
  - TASK-160
  - TASK-161
  - TASK-162
  - TASK-163
  - TASK-159
blocks:
  - TASK-165
labels:
  - hr
  - payroll
  - backend
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

Generate payslips for employees in a payroll run using salary rules and attendance/contract data.

---

# Description

- Create `PayslipGenerationService`
- Inputs: payroll run, employees
- Evaluate salary rules per employee contract and attendance
- Create `hr_payslip` and `hr_payslip_line` records
- Payslip status: Draft → Approved → Paid
- Endpoint: `POST /api/v1/hr/payroll-runs/{id}/generate-payslips`
- Approve endpoint: `POST /api/v1/hr/payslips/{id}/approve`

---

# Acceptance Criteria

- [ ] `[SE]` Payslips generated for all employees in run
- [ ] `[SE]` Rule calculations correct
- [ ] `[SE]` Payslip approval locks lines
- [ ] `[QA]` Payslip totals match expected net pay

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Simple computation engine: fixed amount / percentage of base salary
- Use attendance/leave data from PRD-025

---

# Files Expected

- `PayslipGenerationService.java`
- Controller endpoints
- Integration tests

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
