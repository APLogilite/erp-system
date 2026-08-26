---
id: TASK-135
title: Implement supplier invoice and credit memo posting service
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
parent_prd: PRD-020
prd_version: 1.0.0
prd_branch: prd/PRD-020-accounts-payable-receivable
base_branch: main
merge_target: prd/PRD-020-accounts-payable-receivable
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-133
  - TASK-134
  - TASK-136
depends_on:
  - TASK-133
  - TASK-131
  - TASK-104
blocks:
  - TASK-136
labels:
  - accounting
  - ap
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

Generate and post GL journal entries for supplier invoices and credit memos.

---

# Description

- Create `SupplierInvoicePostingService`
- On supplier invoice validation/post:
  - Debit Expense/COGS/Inventory account
  - Debit Tax receivable account
  - Credit AP account
  - Create journal entry and post
- Create credit memo posting service for AP and AR credit memos
- Mark invoice as posted

---

# Acceptance Criteria

- [ ] `[SE]` GL entry created for supplier invoice
- [ ] `[SE]` AP and expense accounts updated
- [ ] `[SE]` Credit memo reverses invoice accounts
- [ ] `[QA]` Trial balance reflects supplier invoice

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Determine expense/COGS/inventory account based on context

---

# Files Expected

- `SupplierInvoicePostingService.java`
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

- PRD-020 — Accounts Payable & Receivable
