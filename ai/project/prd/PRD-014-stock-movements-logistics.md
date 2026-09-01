---
id: PRD-014
title: Stock Movements & Logistics
version: 1.0.0
status: IN_DEVELOPMENT
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-31
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-014-stock-movements-logistics
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
  - PRD-007 (Sales Order Workflow & Customer Management v1.0.0)
  - PRD-009 (Sales Return & Analytics v1.0.0)
  - PRD-012 (Goods Receipt & Supplier Invoice Matching)
  - PRD-013 (Warehouse Structure & Location Management)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-003 must be COMPLETED
  - PRD-007 must be COMPLETED
  - PRD-009 must be COMPLETED (return inventory receipt patterns)
  - PRD-012 must be COMPLETED (goods receipt creates stock moves)
  - PRD-013 must be COMPLETED (locations exist)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Stock movements, receipts, deliveries, and transfers
  - 1.0.0 APPROVED — User approved for implementation
  - 1.0.0 IN_DEVELOPMENT — Started implementation (TASK-108..111)
---

# Executive Summary

This PRD implements stock movement records and logistics flows: inbound receipts, outbound deliveries, internal transfers between locations, and inventory allocation/reservation hooks.

---

# Problem Statement

**What problem are we solving?**

- No unified stock move record
- Goods receipt does not persist a traceable movement
- Cannot move stock between locations
- Cannot track allocated vs available inventory

**Who experiences this problem?**

- Warehouse operators
- Inventory planners
- Fulfillment staff

**Why is this feature required?**

Every inventory change must be traceable through a stock move.

---

# Business Goals

- Record every stock movement with source/destination location, product, quantity, and reference document
- Support movement types: Receipt, Delivery, Internal Transfer, Adjustment, Production, Return
- Provide available vs on-hand vs reserved quantity visibility
- Enable location-to-location transfers

---

# Functional Requirements

## FR-001 — Stock Move Table

Create the central stock movement table.

- Fields: movement type, product, UOM, quantity, source locator, destination locator, status (Draft → Completed → Cancelled), reference document, reference line, movement date
- Movement types: RECEIPT, DELIVERY, TRANSFER, ADJUSTMENT, PRODUCTION, RETURN_CUSTOMER, RETURN_VENDOR

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `m_stock_move` table created and registered
- [ ] `[SE]` Stock move window seeded
- [ ] `[QA]` Move records can be created

---

## FR-002 — Inventory Balance Table

Track on-hand quantity per product/locator.

- Fields: product, locator, UOM, on_hand_qty, reserved_qty, available_qty
- `available_qty = on_hand_qty - reserved_qty`

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `m_inventory_balance` table created
- [ ] `[SE]` Balance update service implemented
- [ ] `[QA]` Balance reflects completed moves

---

## FR-003 — Receipt Move Creation

Goods receipt completion creates a RECEIPT move.

- Source: null/virtual supplier
- Destination: receipt location
- Quantity: accepted qty

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Receipt completion creates stock move
- [ ] `[SE]` Inventory balance updated
- [ ] `[QA]` On-hand increases after receipt

---

## FR-004 — Delivery Move Creation

Sales shipment creates a DELIVERY move.

- Source: shipping location
- Destination: null/virtual customer
- Quantity: shipped qty
- Decreases on-hand

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Shipment completion creates delivery move
- [ ] `[SE]` Inventory balance decreased
- [ ] `[QA]` On-hand decreases after shipment

---

## FR-005 — Internal Transfer

Move stock between locations.

- Header: source warehouse, destination warehouse, movement date, status
- Lines: product, quantity, source locator, destination locator
- On complete: decrement source, increment destination

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Internal transfer window seeded
- [ ] `[SE]` Transfer completion updates balances
- [ ] `[QA]` Stock moves between locations

---

## FR-006 — Reservation/Allocation Hook

Reserve inventory for sales orders and manufacturing orders.

- Reserved qty tracked separately
- Reservation created on order confirmation
- Released on shipment or cancellation

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Reservation service implemented
- [ ] `[SE]` Available qty excludes reserved
- [ ] `[QA]` Reservation reduces available qty

---

# Non-Functional Requirements

- All moves transactional
- Balance updates atomic
- Tenant isolation

---

# User Stories

- As a warehouse clerk, I want to record internal transfers so that stock is in the right location.
- As a sales coordinator, I want to reserve stock for an order so that it is not sold twice.
- As an inventory manager, I want to see on-hand and available quantities.

---

# User Flow

```
Receipt / Delivery / Transfer
  ↓
Create Stock Move
  ↓
Complete Move
  ↓
Update Inventory Balance
  ↓
Visible in Stock Inquiry
```

---

# Scope

## Included

- Stock move table
- Inventory balance table
- Receipt and delivery move creation
- Internal transfers
- Reservation hook

## Excluded

- Physical inventory counting (PRD-015)
- Costing/valuation (PRD-015)
- Advanced allocation strategies
- Picking waves

---

# UI / UX Requirements

- Stock move list with filters by type and product
- Inventory balance inquiry by product/location
- Internal transfer window with source/destination locators

---

# API Requirements

- Runtime window CRUD for Stock Move and Internal Transfer
- `GET /api/v1/inventory/balance?productId={id}&locatorId={id}`
- `POST /api/v1/inventory/transfers/{id}/complete`
- `POST /api/v1/inventory/reservations/create`
- `POST /api/v1/inventory/reservations/release`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `m_stock_move`
- `m_inventory_balance`
- `m_inventory_reservation`

## Relationships
- `m_stock_move.product_id` → `m_product.id`
- `m_stock_move.source_locator_id` → `m_locator.id`
- `m_stock_move.destination_locator_id` → `m_locator.id`
- `m_inventory_balance.product_id` → `m_product.id`
- `m_inventory_balance.locator_id` → `m_locator.id`

---

# Security Requirements

- Roles:
  - `STOCK_MOVE_VIEW/CREATE`
  - `INVENTORY_BALANCE_VIEW`
  - `TRANSFER_CREATE`
- Tenant isolation

---

# Performance Requirements

- Balance lookup < 100ms
- Move list < 1s for 100,000 moves

---

# Logging Requirements

- Audit log for balance changes
- Error log for negative balance attempts

---

# Edge Cases

- Negative on-hand prevention
- Transfer to same location
- Cancel completed move
- Reservation exceeds available

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Concurrency on balance updates | High | Database-level optimistic locking |

---

# Assumptions

- PRD-013 locations exist
- PRD-012 receipt process creates moves
- Sales shipment process updated to create moves

---

# Dependencies

- PRD-001, PRD-003, PRD-007, PRD-009, PRD-012, PRD-013
- PRD-015 depends on this PRD
- PRD-017 depends on this PRD

---

# Acceptance Criteria

- [ ] Stock move and balance tables created
- [ ] Receipt and delivery moves create balances
- [ ] Internal transfers work
- [ ] Reservation hook implemented
- [ ] No negative on-hand allowed

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed menu and access
- Backfill balances from existing inventory data if any

---

# Testing Requirements

- Unit tests for balance service
- Integration test: receipt → balance → transfer → balance
- UI scenario: internal transfer

---

# Future Enhancements

- Picking waves
- Advanced allocation (FIFO, FEFO)
- Lot/serial tracking

---

# Open Questions

1. Should negative inventory be allowed for any product or location?
2. Do we need lot/serial tracking in v1.0?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-012 — Goods Receipt & Supplier Invoice Matching
- PRD-013 — Warehouse Structure & Location Management
- PRD-015 — Inventory Adjustments, Counting & Valuation
- PRD-017 — Manufacturing Orders & Basic MRP
