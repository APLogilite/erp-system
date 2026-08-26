---
id: TASK-159
title: Implement leave/expense approval workflows and attendance summary
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
estimated_hours: 5
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
  - TASK-158
depends_on:
  - TASK-155
  - TASK-156
  - TASK-157
  - TASK-158
blocks:
  - TASK-164
labels:
  - hr
  - workflow
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

Approve leave and expense claims, update balances, and compute attendance summaries.

---

# Description

- Create `LeaveWorkflowService`
  - Approve: deduct days from leave balance
  - Reject: no balance change
- Create `ExpenseClaimWorkflowService`
  - Approve/Reject
  - Reimburse: create payment (PRD-021)
- Create `AttendanceSummaryService`
  - Monthly summary by employee
- Endpoints for approve/reject/reimburse/summary

---

# Acceptance Criteria

- [ ] `[SE]` Approved leave reduces balance
- [ ] `[SE]` Expense reimbursement creates payment
- [ ] `[SE]` Attendance summary accurate
- [ ] `[QA]` Leave approval updates balance
- [ ] `[QA]` Expense claim can be reimbursed

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reimbursement payment uses PRD-021 payment service

---

# Files Expected

- `LeaveWorkflowService.java`
- `ExpenseClaimWorkflowService.java`
- `AttendanceSummaryService.java`
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

- PRD-025 — Attendance, Leave & Expenses
