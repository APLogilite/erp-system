---
id: TASK-148
title: Implement timesheet approval workflow
type: Feature
scope: backend
status: PLANNED
priority: Medium
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
parent_prd: PRD-023
prd_version: 1.0.0
prd_branch: prd/PRD-023-timesheets-project-billing
base_branch: main
merge_target: prd/PRD-023-timesheets-project-billing
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-146
  - TASK-147
  - TASK-149
  - TASK-150
depends_on:
  - TASK-146
  - TASK-147
blocks:
  - TASK-149
labels:
  - project
  - timesheet
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

Enable employees to submit timesheets and managers to approve or reject them.

---

# Description

- Create `TimesheetWorkflowService`
- Statuses: Draft → Submitted → Approved → Rejected → Cancelled
- On approve: lines become billable
- On reject: status → Rejected with reason
- Endpoints:
  - `POST /api/v1/timesheets/{id}/submit`
  - `POST /api/v1/timesheets/{id}/approve`
  - `POST /api/v1/timesheets/{id}/reject`

---

# Acceptance Criteria

- [ ] `[SE]` Submit/approve/reject actions work
- [ ] `[SE]` Approved timesheets locked for editing
- [ ] `[QA]` Workflow transitions enforced

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Only approver can approve

---

# Files Expected

- `TimesheetWorkflowService.java`
- Controller endpoints
- Unit tests

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

- PRD-023 — Timesheets & Project Billing
