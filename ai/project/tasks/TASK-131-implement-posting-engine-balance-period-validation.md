---
id: TASK-131
title: Implement posting engine with balance and period validation
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
parent_prd: PRD-019
prd_version: 1.0.0
prd_branch: prd/PRD-019-general-ledger-journal-posting
base_branch: main
merge_target: prd/PRD-019-general-ledger-journal-posting
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-129
  - TASK-130
  - TASK-132
depends_on:
  - TASK-129
  - TASK-130
  - TASK-128
blocks:
  - TASK-132
  - TASK-134
  - TASK-135
labels:
  - accounting
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

Post journal entries to the ledger with validation for balance and open periods.

---

# Description

- Create `JournalEntryPostingService`
- On post:
  - Validate debits = credits
  - Validate fiscal period is open
  - Create immutable ledger postings
  - Update account balances
  - Status → Posted
  - Lock entry from editing
- Endpoint: `POST /api/v1/accounting/journal-entries/{id}/post`

---

# Acceptance Criteria

- [ ] `[SE]` Balanced entry posts successfully
- [ ] `[SE]` Unbalanced entry rejected
- [ ] `[SE]` Closed period entry rejected
- [ ] `[SE]` Ledger postings created
- [ ] `[QA]` Trial balance reflects posted entry

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Atomic posting transaction
- Ledger postings should not be updatable

---

# Files Expected

- `JournalEntryPostingService.java`
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

- PRD-019 — General Ledger & Journal Posting Engine
