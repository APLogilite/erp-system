---
id: TASK-XXX

title:

type: Feature
# Feature | Bug | Enhancement | Refactor | Documentation | Research

scope: both
# frontend | backend | both | database

status: PLANNING
# PLANNING
# PLANNED
# READY_FOR_DEV
# IN_DEVELOPMENT
# READY_FOR_TEST
# TESTING
# TESTED
# COMPLETED
# PENDING_APPROVAL
# BLOCKED
# ON_HOLD
# CANCELLED

priority: Medium
# Critical
# High
# Medium
# Low

owner:
# planner
# developer
# tester
# supervisor

assigned_to:

assigned_branch:

locked: false

created:

updated:

started:

completed:

estimated_hours:

actual_hours:

parent_prd:

prd_version:

prd_branch:

base_branch:

merge_target:

merge_strategy:

parent_task:

related_tasks: []

depends_on: []

blocks: []

labels: []

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

test_script:
# Path to reusable test script (e.g., ai/project/scripts/verify-prd-002-data.sql)
# QA populates this when creating reusable scripts. Empty if none.

history:
  - created

---

# Goal

Describe the business goal of this task.

---

# Description

Describe what needs to be implemented.

Include enough detail so another AI agent can complete the task without asking additional questions.

---

# Acceptance Criteria

Prefix each criterion with the agent responsible:
- `[SE]` — validated by Software Engineer during development
- `[QA]` — validated by QA Engineer during testing
- `[SE][QA]` — validated by both

- [ ] `[SE]`

- [ ] `[QA]`

- [ ] `[SE][QA]`

---

# Unmet Criteria

If any acceptance criteria cannot be met, document here with explanation. This section is reviewed by PM who discusses with the user to decide next steps.

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |
| | | |

---

# Technical Notes

Architecture decisions

Known limitations

Implementation notes

References

---

# Files Expected

List the files expected to be created or modified.

Example

- app/Services/AuthService.php
- app/Http/Controllers/LoginController.php

---

# Developer Notes

This section is maintained only by the Developer Agent.

Include

- Important implementation decisions
- New classes
- Updated classes
- New methods
- Updated methods
- Database changes
- API changes
- Breaking changes

---

# Tester Notes

This section is maintained only by the Tester Agent.

Include

- Test scenarios
- Manual tests
- Automated tests
- Edge cases
- Performance concerns
- **Reusable Test Scripts**: Reference any scripts in `ai/project/scripts/` that can re-run this task's verification. If a reusable script exists, QA MUST update the `test_script` field in the frontmatter above and reference the script here. When retesting, QA MUST check `test_script` and run existing scripts before performing new manual verification.

---

# Review Notes

Used by future Review Agent.

Code quality

Architecture

Suggestions

Security

Performance

---

# Task History

Every important action must be recorded.

Example

2026-07-07 09:15

Product Manager

Created Task

---

2026-07-07 09:32

Product Manager

Approved

---

2026-07-07 10:05

Developer

Started Implementation

---

2026-07-07 11:20

Developer

Implementation Complete

---

2026-07-07 11:35

Tester

Testing Started

---

2026-07-07 11:55

Tester

Testing Passed

---

# Related Documents

PRD

Change Summary

Test Report

Bug Reports

Automation Script

Documentation
