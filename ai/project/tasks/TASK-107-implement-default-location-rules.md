---
id: TASK-107
title: Implement default location rules and location lookup service
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
estimated_hours: 4
actual_hours:
parent_prd: PRD-013
prd_version: 1.0.0
prd_branch: prd/PRD-013-warehouse-location-management
base_branch: main
merge_target: prd/PRD-013-warehouse-location-management
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-105
  - TASK-106
depends_on:
  - TASK-105
  - TASK-106
blocks:
  - TASK-103
  - TASK-110
labels:
  - inventory
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

Enforce default receipt/shipping location uniqueness per warehouse and provide a lookup service for downstream processes.

---

# Description

- Validation: only one default receipt location and one default shipping location per warehouse
- Create `LocationLookupService`
- Methods:
  - `findDefaultReceiptLocation(warehouseId)`
  - `findDefaultShippingLocation(warehouseId)`
  - `findLocationsByType(warehouseId, type)`
- Expose endpoint `GET /api/v1/inventory/locations/default`

---

# Acceptance Criteria

- [ ] `[SE]` Default location uniqueness enforced
- [ ] `[SE]` Lookup service returns correct locations
- [ ] `[SE]` Endpoint returns `ApiResponse<T>`
- [ ] `[QA]` Validation prevents multiple defaults
- [ ] `[QA]` Lookup returns expected default

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use database constraints where possible
- Cache defaults per tenant

---

# Files Expected

- `LocationLookupService.java`
- Controller endpoint
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

- PRD-013 — Warehouse Structure & Location Management
