---
id: TASK-101
title: Create goods receipt and supplier invoice tables
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
parent_prd: PRD-012
prd_version: 1.0.0
prd_branch: prd/PRD-012-goods-receipt-supplier-invoice
base_branch: main
merge_target: prd/PRD-012-goods-receipt-supplier-invoice
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-102
  - TASK-103
  - TASK-104
depends_on:
  - TASK-100
blocks:
  - TASK-102
  - TASK-103
  - TASK-104
labels:
  - purchase
  - inventory
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

Create the database tables for goods receipts and supplier invoices (headers and lines).

---

# Description

- Create `m_goods_receipt` and `m_goods_receipt_line` tables
- Create `ap_invoice` and `ap_invoice_line` tables
- Register all tables in metadata
- Add document sequences `GR` and `AP`
- Add FKs to `pur_order`, `pur_order_line`, `c_business_partner`, `m_product`, `c_uom`

---

# Acceptance Criteria

- [ ] `[SE]` Tables created with `BaseEntity` columns
- [ ] `[SE]` Tables registered in metadata
- [ ] `[SE]` Document sequences seeded
- [ ] `[QA]` Tables and sequences verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Receipt line tracks ordered/received/accepted/rejected quantities
- Invoice line tracks invoiced quantity

---

# Files Expected

- Flyway migration
- Metadata seed entries

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

- PRD-012 — Goods Receipt & Supplier Invoice Matching
