---
id: TASK-154
title: Implement employee directory API
type: Feature
scope: backend
status: PLANNED
priority: Low
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
parent_prd: PRD-024
prd_version: 1.0.0
prd_branch: prd/PRD-024-employees-contracts-organization
base_branch: main
merge_target: prd/PRD-024-employees-contracts-organization
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-151
  - TASK-152
  - TASK-153
depends_on:
  - TASK-151
  - TASK-152
  - TASK-153
blocks:
  - TASK-144
labels:
  - hr
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

Provide a searchable employee directory.

---

# Description

- Create `EmployeeDirectoryService`
- Filters: department, job position, status
- Endpoint: `GET /api/v1/hr/employees/directory`
- Seed directory screen

---

# Acceptance Criteria

- [ ] `[SE]` Directory API supports filters
- [ ] `[SE]` Directory screen renders
- [ ] `[QA]` Directory searchable by department and status

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Exclude sensitive fields from directory response

---

# Files Expected

- `EmployeeDirectoryService.java`
- Controller endpoint
- Directory screen seed
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

- PRD-024 — Employees, Contracts & Organization
