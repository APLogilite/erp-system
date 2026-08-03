---
id: TASK-072
title: Add payment term to Order and Invoice tables
type: Feature
scope: database
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
estimated_hours: 2
actual_hours:
parent_prd: PRD-007
prd_version: 1.0.0
prd_branch: prd/PRD-007-sales-order-workflow-customer-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-070
depends_on:
  - TASK-070
blocks:
  - TASK-077
labels:
  - database
  - prd-007
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

Allow orders and invoices to carry a payment term so invoice due dates are derived from terms rather than typed manually.

---

# Description

Flyway migration:
- `tx_order` ADD COLUMN `payment_term_id` UUID NULL REFERENCES md_payment_term(id)
- `tx_invoice` ADD COLUMN `payment_term_id` UUID NULL REFERENCES md_payment_term(id)

Register both columns in `sys_column` metadata for their tables. Update seeded `sales_order` and `sales_invoice` forms (from PRD-003) to include the payment_term_id dropdown in the header section.

Also add backend auto-population logic (in the runtime save path or a small service): when an invoice is saved with a payment_term_id and invoice_date, auto-set `due_date = invoice_date + term.days` if due_date is empty. When an order is created for a partner with a default payment_term_id, pre-fill the order's payment_term_id.

---

# Acceptance Criteria

- [ ] `[SE]` payment_term_id column added to tx_order and tx_invoice with metadata registration
- [ ] `[SE]` sales_order and sales_invoice forms show the payment term dropdown
- [ ] `[SE]` Saving an invoice with a term auto-calculates due_date when empty
- [ ] `[SE]` Creating an order pre-fills term from customer default
- [ ] `[QA]` End-to-end: partner with NET30 → new order shows NET30 → invoice due_date = invoice_date + 30
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-007 FR-003

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__add_payment_term_to_order_invoice.sql`
- Backend hook for due-date auto-calculation (service or runtime save interceptor)

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

Created Task (PLANNED) — PRD-007 approved by user

---

# Related Documents

- PRD-007 — Sales Order Workflow & Customer Management (FR-003)
