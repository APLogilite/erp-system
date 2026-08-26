---
id: TASK-095
title: Seed vendor and purchase price list windows, tabs, fields, and menus
type: Feature
scope: both
status: IN_DEVELOPMENT
priority: High
owner: planner
assigned_to: Software Engineer
assigned_branch: feature/TASK-095
locked: true
created: 2026-08-26
updated: 2026-08-26
started: 2026-08-26
completed:
estimated_hours: 5
actual_hours: 0
parent_prd: PRD-010
prd_version: 1.0.0
prd_branch: prd/PRD-010-vendor-management-price-lists
base_branch: main
merge_target: prd/PRD-010-vendor-management-price-lists
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-093
  - TASK-094
  - TASK-096
depends_on:
  - TASK-093
  - TASK-094
blocks:
  - TASK-096
labels:
  - vendor
  - price-list
  - metadata
  - ui
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

Make vendor management and purchase price list forms available in the runtime UI through the metadata-driven window system.

---

# Description

- Create Vendor window with filter `is_vendor = true`
- Add Vendor Category window
- Create Purchase Price List window with lines tab
- Add fields to `sys_window_field` for all windows/tabs
- Create Purchasing menu group and menu entries
- Configure window access for procurement roles

---

# Acceptance Criteria

- [ ] `[SE]` Vendor window renders in runtime UI
- [ ] `[SE]` Vendor Category window renders
- [ ] `[SE]` Purchase Price List window with lines tab renders
- [ ] `[SE]` Menu entries visible under Purchasing
- [ ] `[QA]` User can navigate to Vendor window
- [ ] `[QA]` User can create a vendor category
- [ ] `[QA]` User can create a price list with lines

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reuse existing address/contact tabs for Vendor window
- Price list lines tab uses product lookup

---

# Files Expected

- Flyway seed migration for windows, tabs, fields, menus, access

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
