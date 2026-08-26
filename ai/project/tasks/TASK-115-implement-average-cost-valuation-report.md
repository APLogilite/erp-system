---
id: TASK-115
title: Implement average cost valuation and inventory valuation report
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
parent_prd: PRD-015
prd_version: 1.0.0
prd_branch: prd/PRD-015-inventory-adjustments-valuation
base_branch: main
merge_target: prd/PRD-015-inventory-adjustments-valuation
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-112
  - TASK-113
  - TASK-114
depends_on:
  - TASK-112
  - TASK-114
blocks:
  - TASK-123
labels:
  - inventory
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

Track inventory value using average cost and provide a valuation report.

---

# Description

- Create `AverageCostService` to update `m_product.average_cost` on receipt
- Weighted average formula: ((old_on_hand × old_avg_cost) + (received_qty × unit_cost)) / (old_on_hand + received_qty)
- Create `InventoryValuationReportService`
- Value = on_hand_qty × average_cost
- Filter by warehouse, product category
- Endpoint: `GET /api/v1/inventory/valuation`

---

# Acceptance Criteria

- [ ] `[SE]` Average cost updates on receipt
- [ ] `[SE]` Valuation report query implemented
- [ ] `[SE]` Report endpoint returns `ApiResponse<T>`
- [ ] `[QA]` Average cost calculation is correct
- [ ] `[QA]` Valuation report totals match expected value

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use BigDecimal with scale 6
- Valuation by product+locator or product only in v1.0

---

# Files Expected

- `AverageCostService.java`
- `InventoryValuationReportService.java`
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

- PRD-015 — Inventory Adjustments, Counting & Valuation
