---
id: TASK-132
title: Implement trial balance report and journal entry reversal
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
  - TASK-131
depends_on:
  - TASK-129
  - TASK-131
blocks:
  - TASK-136
labels:
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

Provide a trial balance report and allow reversal of posted journal entries.

---

# Description

- Create `TrialBalanceReportService`
- Columns: account, opening debit/credit, period debit/credit, closing balance
- Filter by fiscal period
- Create `JournalEntryReversalService`
- On reverse: create reversing entry, mark original as Reversed
- Endpoints:
  - `GET /api/v1/accounting/trial-balance`
  - `POST /api/v1/accounting/journal-entries/{id}/reverse`

---

# Acceptance Criteria

- [ ] `[SE]` Trial balance query implemented
- [ ] `[SE]` Reversal creates reversing entry
- [ ] `[QA]` Trial balance totals are correct
- [ ] `[QA]` Reversal clears original balances

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Trial balance computed from ledger postings
- Reversal uses same accounts with opposite signs

---

# Files Expected

- `TrialBalanceReportService.java`
- `JournalEntryReversalService.java`
- Controller endpoints
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

- PRD-019 — General Ledger & Journal Posting Engine
