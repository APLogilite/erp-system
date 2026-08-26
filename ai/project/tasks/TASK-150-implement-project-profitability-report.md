---
id: TASK-150
title: Implement project cost and profitability report
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
  - TASK-148
  - TASK-149
depends_on:
  - TASK-146
  - TASK-149
blocks: []
labels:
  - project
  - reporting
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

Report project labor cost, revenue, and profit.

---

# Description

- Create `ProjectProfitabilityService`
- Cost = hours × employee cost rate (from contract)
- Revenue = hours × billing rate
- Profit = revenue - cost
- Endpoint: `GET /api/v1/projects/{id}/profitability`
- Seed report screen

---

# Acceptance Criteria

- [ ] `[SE]` Profitability query implemented
- [ ] `[SE]` Report endpoint returns `ApiResponse<T>`
- [ ] `[QA]` Profitability numbers are correct

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Employee cost rate from PRD-024 contract

---

# Files Expected

- `ProjectProfitabilityService.java`
- Controller endpoint
- Report window seed
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
