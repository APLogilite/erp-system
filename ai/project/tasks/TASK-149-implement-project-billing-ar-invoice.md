---
id: TASK-149
title: Implement project billing to AR invoice
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
estimated_hours: 6
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
  - TASK-148
  - TASK-150
depends_on:
  - TASK-146
  - TASK-147
  - TASK-148
  - TASK-134
blocks:
  - TASK-150
labels:
  - project
  - billing
  - ar
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

Generate customer invoices from approved timesheet lines.

---

# Description

- Create `ProjectBillingService`
- Select approved, unbilled timesheet lines by project
- Resolve billing rate per line
- Group into invoice lines
- Create AR invoice (PRD-007/020)
- Mark timesheet lines as billed
- Endpoint: `POST /api/v1/projects/{id}/bill-timesheets`

---

# Acceptance Criteria

- [ ] `[SE]` AR invoice created from timesheet lines
- [ ] `[SE]` Invoice total = billable hours × rate
- [ ] `[SE]` Timesheet lines marked billed
- [ ] `[QA]` Billing generates correct invoice

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use billing rate resolution hierarchy
- Use AR invoice creation from PRD-007

---

# Files Expected

- `ProjectBillingService.java`
- Controller endpoint
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

- PRD-023 — Timesheets & Project Billing
