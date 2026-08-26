---
id: PRD-017
title: Manufacturing Orders & Basic MRP
version: 1.0.0
status: APPROVED
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-017-manufacturing-orders-mrp
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
  - PRD-015 (Inventory Adjustments, Counting & Valuation)
  - PRD-016 (Product BOM, Work Centers & Routings)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-003 must be COMPLETED
  - PRD-014 must be COMPLETED (stock moves and balances)
  - PRD-015 must be COMPLETED (inventory valuation)
  - PRD-016 must be COMPLETED (BOMs, routings, work centers)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Manufacturing orders and basic material requirements planning
  - 1.0.0 APPROVED — User approved for implementation
---

# Executive Summary

This PRD implements manufacturing orders (MO) and basic MRP. It uses BOMs, routings, and inventory data to plan production, reserve materials, consume components, and produce finished goods.

---

# Problem Statement

**What problem are we solving?**

- No way to plan and execute production
- No material reservation for manufacturing
- No tracking of work-in-progress

**Who experiences this problem?**

- Production planners
- Shop floor operators
- Inventory controllers

**Why is this feature required?**

Manufacturing orders turn BOMs and routings into actual production activity and inventory changes.

---

# Business Goals

- Create manufacturing orders from BOM/routing defaults
- Reserve component inventory
- Record material consumption and finished goods production
- Provide basic MRP report: net requirements by product/date
- Track MO status: Draft → Confirmed → In Progress → Completed → Cancelled

---

# Functional Requirements

## FR-001 — Manufacturing Order Header

Create MOs.

- Fields: MO number, product, BOM, routing, quantity, UOM, planned start, planned end, status, warehouse
- Auto-populate default BOM and routing

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `mfg_order` table created and registered
- [ ] `[SE]` MO window seeded
- [ ] `[SE]` Document sequence `MO-XXXX` generated
- [ ] `[QA]` MO can be created

---

## FR-002 — Manufacturing Order Lines (Components)

Auto-explode BOM into component lines.

- Fields: component product, required qty, consumed qty, UOM, locator
- Reserved on confirmation

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `mfg_order_line` table created and registered
- [ ] `[SE]` Lines auto-generated from BOM explosion
- [ ] `[QA]` Component lines match BOM quantities

---

## FR-003 — MO Confirmation

On confirm:

- Status → Confirmed
- Reserve component inventory
- Create planned component stock moves

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Confirmation service implemented
- [ ] `[SE]` Component inventory reserved
- [ ] `[QA]` Available qty decreases by reserved amount

---

## FR-004 — Material Consumption

Record component usage.

- User enters actual consumed qty per component line
- Creates PRODUCTION stock move (outbound from storage)
- Updates consumed qty

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Consumption service implemented
- [ ] `[SE]` Stock move and balance updated
- [ ] `[QA]` Consumption reduces on-hand

---

## FR-005 — Finished Goods Production

Record finished goods output.

- Fields: product, quantity, locator
- Creates PRODUCTION stock move (inbound to storage)
- Reduces reserved qty and increases on-hand

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Production completion service implemented
- [ ] `[SE]` Finished goods balance increased
- [ ] `[QA]` MO completion updates inventory

---

## FR-006 — MO Completion

On complete:

- Release remaining reservations
- Status → Completed
- Lock MO from editing

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Completion service implemented
- [ ] `[SE]` Reservations released
- [ ] `[QA]` MO completed end-to-end

---

## FR-007 — Basic MRP Report

Compute net material requirements.

- Inputs: product, date range
- Outputs: required qty, on-hand, reserved, available, net requirement
- Based on MO component demand vs inventory balance

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` `BasicMRPService` implemented
- [ ] `[SE]` MRP report API available
- [ ] `[QA]` Report shows net requirements

---

# Non-Functional Requirements

- All MO operations transactional
- Reservation consistent with balance
- Tenant isolation

---

# User Stories

- As a production planner, I want to create an MO so that the shop floor knows what to produce.
- As a warehouse operator, I want to issue materials to production so that inventory is accurate.
- As a planner, I want an MRP report so that I know what to purchase.

---

# User Flow

```
Product + BOM + Routing
  ↓
Create Manufacturing Order
  ↓
Confirm → Reserve Components
  ↓
Issue Materials (consumption)
  ↓
Produce Finished Goods
  ↓
Complete MO
```

---

# Scope

## Included

- Manufacturing order header and component lines
- MO confirmation and reservation
- Material consumption
- Finished goods production
- MO completion
- Basic MRP report

## Excluded

- Capacity planning
- Shop floor control/operator terminals
- Quality inspection
- Outsourced manufacturing
- Advanced MRP (lead time, safety stock, planned orders)

---

# UI / UX Requirements

- MO window with component lines tab
- Buttons: Confirm, Issue Materials, Produce, Complete, Cancel
- MRP report grid

---

# API Requirements

- Runtime window CRUD for Manufacturing Order
- `POST /api/v1/manufacturing/orders/{id}/confirm`
- `POST /api/v1/manufacturing/orders/{id}/issue-materials`
- `POST /api/v1/manufacturing/orders/{id}/produce`
- `POST /api/v1/manufacturing/orders/{id}/complete`
- `GET /api/v1/manufacturing/mrp?productId={id}&start={date}&end={date}`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `mfg_order`
- `mfg_order_line`

## Modified Tables
- `m_stock_move` — add `mfg_order_id` reference

## Relationships
- `mfg_order.product_id` → `m_product.id`
- `mfg_order.bom_id` → `mfg_bom.id`
- `mfg_order.routing_id` → `mfg_routing.id`
- `mfg_order_line.mfg_order_id` → `mfg_order.id`
- `mfg_order_line.component_product_id` → `m_product.id`

---

# Security Requirements

- Roles:
  - `MANUFACTURING_ORDER_VIEW/CREATE`
  - `MANUFACTURING_ORDER_CONFIRM`
  - `MANUFACTURING_ORDER_COMPLETE`
- Tenant isolation

---

# Performance Requirements

- MRP report < 2s
- MO confirmation < 500ms

---

# Logging Requirements

- Audit log for MO status changes
- Audit log for material consumption

---

# Edge Cases

- Insufficient component inventory at confirmation
- Over-consumption vs BOM
- Partial production completion
- Cancel confirmed MO

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Reservation deadlock | Medium | Single transaction; release on cancel |

---

# Assumptions

- PRD-016 master data exists
- PRD-014/015 inventory exists
- Single-level BOM sufficient

---

# Dependencies

- PRD-001, PRD-003, PRD-014, PRD-015, PRD-016

---

# Acceptance Criteria

- [ ] MO tables and forms created
- [ ] Confirmation reserves components
- [ ] Consumption and production update inventory
- [ ] Completion releases reservations
- [ ] Basic MRP report works

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed document sequence `MO`
- Seed menu and access

---

# Testing Requirements

- Integration test: create MO → confirm → issue → produce → complete
- Unit tests for MRP calculation
- UI scenario: manufacturing order lifecycle

---

# Future Enhancements

- Capacity planning
- Advanced MRP with planned purchase/manufacturing orders
- Quality inspections
- Outsourced operations

---

# Open Questions

1. Should MO confirmation block if components are insufficient?
2. Do we support operation-level time tracking in v1.0?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-014 — Stock Movements & Logistics
- PRD-015 — Inventory Adjustments, Counting & Valuation
- PRD-016 — Product BOM, Work Centers & Routings
