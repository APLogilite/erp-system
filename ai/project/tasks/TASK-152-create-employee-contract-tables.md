---
id: TASK-152
title: Create employee and contract tables
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
estimated_hours: 5
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
  - TASK-153
  - TASK-154
depends_on:
  - TASK-151
blocks:
  - TASK-153
  - TASK-154
labels:
  - hr
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

Create the database tables for employees and employment contracts.

---

# Description

- Create `hr_employee` table
- Create `hr_contract` table
- Register tables in metadata
- Add document sequence `EMP`
- Add FKs to business partner, user, department, job position

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[SE]` Document sequence seeded
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Employee links to business partner for contact info
- Contract links to salary structure later (PRD-026)

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

- PRD-024 — Employees, Contracts & Organization
