---
id: TASK-089
title: Implement Return workflow and Credit Memo generation
type: Feature
scope: backend
status: PLANNED
priority: High
owner: developer
assigned_to:
assigned_branch:
locked: false
created: 2026-07-29
updated: 2026-07-29
started:
completed:
estimated_hours: 5
actual_hours:
parent_prd: PRD-009
prd_version: 1.0.0
prd_branch: prd/PRD-009-sales-return-analytics
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-087
  - TASK-088
depends_on:
  - TASK-087
  - TASK-088
blocks:
  - TASK-090
labels:
  - backend
  - prd-009
review_required: true
test_required: true
automation_required: false
change_summary:
test_report:
test_script:
history:
  - created
---

# Goal

Enforce the RMA lifecycle (requested → approved → received → credited, or rejected) and generate a credit memo automatically when a return is credited — completing the return-to-refund chain.

---

# Description

Implement `ReturnService` (backend):

**Workflow endpoint** `POST /api/v1/returns/{id}/transition`:
- requested → approved / rejected
- approved → received / rejected
- received → credited (triggers credit memo generation)
- Invalid → 400

**Credit memo generation** (on received → credited, single transaction):
1. Generate credit memo number (CM- sequence)
2. Create tx_credit_memo: partner, return_id, invoice_id from return, subtotal/tax/credit_amount copied from return totals, status='validated'
3. If the return references an invoice with due_amount > 0: reduce invoice due_amount by credit_amount (floor at 0); update invoice status to partially_paid/paid accordingly
4. Return credit memo id + number

**Auto-numbering:** return_number assigned from RMA- sequence on create.

**Totals endpoint** `POST /api/v1/returns/{id}/calculate`: subtotal = Σ line_total, tax = Σ(line_total × tax rate if present), credit_amount = subtotal + tax.

---

# Acceptance Criteria

- [ ] `[SE]` Valid transitions succeed; invalid → 400
- [ ] `[SE]` Crediting a return auto-generates a validated credit memo with correct amounts
- [ ] `[SE]` Invoice due_amount reduced when credit applies
- [ ] `[SE]` return_number auto-assigned from sequence
- [ ] `[SE]` Calculate endpoint returns correct totals
- [ ] `[QA]` Transactional: failure during credit memo creation leaves return in received status
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Depends on tx_return (TASK-087), tx_credit_memo (TASK-088), sequences (TASK-060)
- Reference PRD-009 FR-001, FR-002

---

# Files Expected

- `backend/src/main/java/com/erp/modules/returns/service/ReturnService.java` (new)
- `backend/src/main/java/com/erp/modules/returns/controller/ReturnController.java` (new)
- `backend/src/main/java/com/erp/modules/returns/dto/CreditMemoResponse.java` (new)

---

# Developer Notes

*(maintained by SE)*

---

# Tester Notes

*(maintained by QA)*

---

# Review Notes

*(maintained by reviewer)*

---

# Task History

2026-07-29

Product Manager

Created Task (PLANNED) — PRD-009 approved by user

---

# Related Documents

- PRD-009 — Sales Return & Analytics (FR-001, FR-002)
