---
id: TASK-093
title: Extend c_business_partner with vendor fields
type: Feature
scope: database
status: READY_FOR_TEST
priority: High
owner: planner
assigned_to: Software Engineer
assigned_branch: feature/TASK-093
locked: false
created: 2026-08-26
updated: 2026-08-26
started: 2026-08-26
completed: 2026-08-26
estimated_hours: 3
actual_hours: 1
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
change_summary: ai/project/changes/CHANGE-TASK-093.md
test_report:
test_script:
history:
  - created
  - 2026-08-26 — Software Engineer — Started implementation
  - 2026-08-26 — Software Engineer — Completed implementation, merged to main
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

- [x] `[SE]` `is_vendor`, `vendor_category_id`, `default_purchase_currency`, `payment_term_id` columns exist in `md_business_partner`
- [x] `[SE]` Columns registered in `sys_column`
- [x] `[SE]` Business Partner window shows vendor fields
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
