---
id: TASK-093
title: Extend c_business_partner with vendor fields
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
estimated_hours: 3
actual_hours:
parent_prd: PRD-010
prd_version: 1.0.0
prd_branch: prd/PRD-010-vendor-management-price-lists
base_branch: main
merge_target: prd/PRD-010-vendor-management-price-lists
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-094
  - TASK-095
depends_on: []
blocks:
  - TASK-094
  - TASK-095
labels:
  - vendor
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

Add vendor-specific fields to the existing `c_business_partner` table so suppliers can be distinguished from customers and linked to payment terms and price lists.

---

# Description

- Add `is_vendor` boolean column to `c_business_partner`
- Add `vendor_category_id` UUID foreign key
- Add `default_purchase_currency` VARCHAR
- Add `payment_term_id` UUID foreign key (reuse existing payment term if available)
- Update `sys_column` metadata for the new columns
- Update `c_business_partner` form/window fields to expose new columns
- No changes to runtime form engine

---

# Acceptance Criteria

- [ ] `[SE]` `is_vendor`, `vendor_category_id`, `default_purchase_currency`, `payment_term_id` columns exist in `c_business_partner`
- [ ] `[SE]` Columns registered in `sys_column`
- [ ] `[SE]` Business Partner window shows vendor fields
- [ ] `[QA]` User can mark an existing business partner as vendor
- [ ] `[QA]` Tenant isolation still enforced

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reuse existing `c_payment_term` if it exists from PRD-007
- Vendor category table created in TASK-094; this task only adds the FK column

---

# Files Expected

- Flyway migration adding columns
- `sys_column` seed updates
- `sys_window_field` seed updates for Business Partner window

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

- PRD-010 — Vendor Management & Purchase Price Lists
