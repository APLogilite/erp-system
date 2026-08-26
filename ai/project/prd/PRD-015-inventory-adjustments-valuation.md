---
id: PRD-015
title: Inventory Adjustments, Counting & Valuation
version: 1.0.0
status: APPROVED
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-015-inventory-adjustments-valuation
target_branch: main
merge_strategy: merge

tech_stack:
  - Spring Boot 3.3.4
  - Java 17
  - PostgreSQL
  - Flyway (migrations only, disabled by default — enable for this PRD)
  - React 18
  - TypeScript (strict)
  - MUI 5

related_prds:
  - PRD-001 (Dynamic Form Configuration System v1.6.0)
  - PRD-003 (ERP Order Flow Forms v1.0.0)
  - PRD-014 (Stock Movements & Logistics)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-003 must be COMPLETED
  - PRD-014 must be COMPLETED (stock moves and balances exist)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Physical inventory, adjustments, and basic valuation
  - 1.0.0 APPROVED — User approved for implementation
---

# Executive Summary

This PRD adds physical inventory counting, stock adjustments, and basic inventory valuation. It enables cycle counts, stock corrections, and cost-of-goods-sold tracking for PRD-013/019.

---

# Problem Statement

**What problem are we solving?**

- No physical inventory count process
- No way to correct stock discrepancies
- No inventory valuation for accounting

**Who experiences this problem?**

- Warehouse managers
- Inventory controllers
- Accountants

**Why is this feature required?**

Physical counts and adjustments keep inventory accurate; valuation feeds the general ledger.

---

# Business Goals

- Support physical inventory counts
- Create adjustment stock moves for discrepancies
- Track inventory value using average cost (v1.0)
- Provide inventory valuation report

---

# Functional Requirements

## FR-001 — Physical Inventory Count Header

Create inventory count documents.

- Fields: count date, warehouse, status (Draft → In Progress → Completed → Posted)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `m_physical_inventory` table created
- [ ] `[SE]` Count window seeded
- [ ] `[QA]` Count document can be created

---

## FR-002 — Physical Inventory Count Lines

Record counted quantities per product/location.

- Fields: product, locator, UOM, system qty, counted qty, difference
- Difference auto-calculated

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `m_physical_inventory_line` table created
- [ ] `[SE]` Lines tab seeded
- [ ] `[QA]` Count lines can be entered

---

## FR-003 — Count Completion & Adjustments

On complete:

- Generate adjustment stock moves for differences
- Update inventory balances
- Status → Completed

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Completion service creates adjustment moves
- [ ] `[SE]` Balances updated
- [ ] `[QA]` Differences post to inventory

---

## FR-004 — Direct Stock Adjustment

Allow direct adjustment without full count.

- Fields: product, locator, UOM, adjustment qty, reason
- Creates ADJUSTMENT stock move

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Stock Adjustment window seeded
- [ ] `[SE]` Adjustment creates stock move
- [ ] `[QA]` Direct adjustment updates balance

---

## FR-005 — Average Cost Valuation

Track product cost using average cost method.

- Fields on `m_product`: cost_type (AVERAGE), average_cost
- Update average cost on receipt using weighted average

**Priority:** High

**Acceptance Criteria**
- [SE]` Average cost calculation service implemented
- [ ] `[SE]` Cost updated on receipt completion
- [ ] `[QA]` Average cost computes correctly

---

## FR-006 — Inventory Valuation Report

Report inventory value by product/warehouse.

- Value = on_hand_qty × average_cost
- Filter by warehouse, product category, date

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Valuation report API implemented
- [ ] `[SE]` Report window/screen seeded
- [ ] `[QA]` Report totals match expected value

---

# Non-Functional Requirements

- Adjustment moves transactional
- Valuation report < 2s for 100,000 balances
- Tenant isolation

---

# User Stories

- As a warehouse manager, I want to count stock and post adjustments so that inventory matches physical stock.
- As an accountant, I want inventory valuation so that I can report assets.

---

# User Flow

```
Physical Inventory Count
  ↓
Enter Counted Qty per Product/Location
  ↓
Complete Count
  ↓
Adjustment Moves Created
  ↓
Balances Updated
  ↓
Valuation Report
```

---

# Scope

## Included

- Physical inventory count
- Count line entry
- Adjustment move generation
- Direct stock adjustment
- Average cost valuation
- Inventory valuation report

## Excluded

- FIFO/LIFO/standard costing
- Landed costs
- Cycle count scheduling
- ABC analysis

---

# UI / UX Requirements

- Count window with system qty and counted qty side-by-side
- Adjustment reason dropdown
- Valuation report grid with export

---

# API Requirements

- Runtime window CRUD for Physical Inventory and Stock Adjustment
- `POST /api/v1/inventory/physical-inventories/{id}/complete`
- `GET /api/v1/inventory/valuation?warehouseId={id}&date={date}`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `m_physical_inventory`
- `m_physical_inventory_line`
- `m_adjustment_reason`

## Modified Tables
- `m_product` — add `cost_type`, `average_cost`
- `m_stock_move` — add `adjustment_reason_id`

## Relationships
- `m_physical_inventory_line.count_id` → `m_physical_inventory.id`
- `m_stock_move.adjustment_reason_id` → `m_adjustment_reason.id`

---

# Security Requirements

- Roles:
  - `PHYSICAL_INVENTORY_CREATE`
  - `STOCK_ADJUSTMENT_CREATE`
  - `INVENTORY_VALUATION_VIEW`
- Tenant isolation

---

# Performance Requirements

- Valuation report < 2s
- Count completion < 1s

---

# Logging Requirements

- Audit log for adjustments
- Audit log for average cost changes

---

# Edge Cases

- Counted qty = 0 (write-off)
- Negative adjustment exceeds on-hand
- Multiple counts for same locator/product

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Average cost rounding | Low | Use BigDecimal with scale 6 |

---

# Assumptions

- PRD-014 stock moves and balances exist
- Receipts provide cost for average cost update

---

# Dependencies

- PRD-001, PRD-003, PRD-014
- PRD-017 uses inventory valuation for production
- PRD-019 uses valuation for GL entries

---

# Acceptance Criteria

- [ ] Physical inventory tables and forms created
- [ ] Count completion creates adjustments
- [ ] Average cost updates on receipt
- [ ] Valuation report works

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed adjustment reasons
- Seed menu and access

---

# Testing Requirements

- Unit tests for average cost calculation
- Integration test: count → adjustment → balance → valuation
- UI scenario: physical inventory count

---

# Future Enhancements

- FIFO/LIFO/standard costing
- Landed costs
- Cycle count scheduling
- Inventory aging

---

# Open Questions

1. Should average cost be at product level or product+location level?
2. Do we allow negative stock adjustments that create negative on-hand?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-014 — Stock Movements & Logistics
- PRD-017 — Manufacturing Orders & Basic MRP
- PRD-019 — General Ledger & Journal Posting Engine
