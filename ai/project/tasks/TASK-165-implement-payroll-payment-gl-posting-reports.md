---
id: TASK-165
title: Implement payroll payment, GL posting, and payroll reports
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
  - TASK-164
depends_on:
  - TASK-160
  - TASK-161
  - TASK-162
  - TASK-163
  - TASK-164
  - TASK-140
blocks: []
labels:
  - hr
  - payroll
  - accounting
  - backend
  - reporting
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

Pay employees, post payroll journal to GL, and provide payroll reports.

---

# Description

- Create `PayslipPaymentService`
  - On pay: create payment in PRD-021 and post payroll journal
  - Mark payslip as Paid
- Create `PayrollJournalService`
  - Debit salary expense accounts
  - Credit tax/insurance payable and cash/bank
- Create `PayrollReportService`
  - Payslip report
  - Payroll summary by department
  - Deduction/contributions summary
- Endpoints for pay, post journal, summary

---

# Acceptance Criteria

- [ ] `[SE]` Payslip payment creates outgoing payment
- [ ] `[SE]` Payroll journal posted to GL
- [ ] `[SE]` Payroll reports work
- [ ] `[QA]` Trial balance reflects payroll

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use PRD-021 payment service
- Use PRD-019 GL posting engine

---

# Files Expected

- `PayslipPaymentService.java`
- `PayrollJournalService.java`
- `PayrollReportService.java`
- Controller endpoints
- Report window seeds
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
