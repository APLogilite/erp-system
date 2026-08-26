---
id: PRD-011
title: Purchase Requisition & Purchase Order
version: 1.0.0
status: APPROVED
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-011-purchase-requisition-order
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
  - PRD-006 (Sales Quotation & Price Management v1.0.0)
  - PRD-007 (Sales Order Workflow & Customer Management v1.0.0)
  - PRD-010 (Vendor Management & Purchase Price Lists)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-003 must be COMPLETED
  - PRD-006 must be COMPLETED
  - PRD-007 must be COMPLETED
  - PRD-010 must be COMPLETED (vendors and purchase price lists)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Purchase requisition and purchase order
  - 1.0.0 APPROVED — User approved for implementation
---

# Executive Summary

This PRD adds the core purchasing documents: purchase requisitions for internal approval and purchase orders sent to vendors. It mirrors the sales quotation/order pattern from PRD-006/007 but for procurement.

---

# Problem Statement

**What problem are we solving?**

- No internal requisition process
- No purchase order document sent to suppliers
- No link between requested items and vendor orders

**Who experiences this problem?**

- Procurement team
- Department requesters
- Warehouse planners

**Why is this feature required?**

Purchase orders authorize spending and drive receipts and invoices.

---

# Business Goals

- Enable requisition → approval → PO conversion
- Support direct PO creation
- Auto-calculate PO totals and default vendor prices
- Provide PO status workflow

---

# Functional Requirements

## FR-001 — Purchase Requisition

Create internal requisitions.

- Header: requester, department, required date, status (Draft → Pending → Approved → Rejected → Ordered)
- Lines: product, quantity, UOM, estimated price, notes
- Actions: Submit, Approve, Reject, Convert to PO

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` `pur_requisition` and `pur_requisition_line` tables created
- [ ] `[SE]` Requisition window and lines tab seeded
- [ ] `[SE]` Document sequence `PRQ-XXXX` generated
- [ ] `[QA]` Requisition can be submitted, approved, and converted to PO

---

## FR-002 — Purchase Order Header

Create purchase orders.

- Fields: PO number, vendor, order date, promised date, payment term, currency, status, notes
- Statuses: Draft → Ordered → Partially Received → Received → Invoiced → Paid → Cancelled

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `pur_order` table created and registered
- [ ] `[SE]` PO window seeded
- [ ] `[SE]` Document sequence `PO-XXXX` generated
- [ ] `[QA]` PO header can be created

---

## FR-003 — Purchase Order Lines

Add products to a PO.

- Fields: line no, product, UOM, quantity, unit price, line total, received qty, invoiced qty
- Auto-default unit price from purchase price list (PRD-010)
- Line total = qty × unit price

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `pur_order_line` table created and registered
- [ ] `[SE]` Lines tab seeded
- [ ] `[SE]` Price defaults from vendor price list
- [ ] `[QA]` Line totals calculate correctly

---

## FR-004 — PO Auto-Calculation

Compute PO totals.

- Subtotal = sum of line totals
- Tax = configurable tax amount per line
- Total = subtotal + tax

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `PurchaseOrderCalculationService` implemented
- [ ] `[SE]` Totals update on line change
- [ ] `[QA]` Totals match expected values

---

## FR-005 — PO Workflow Actions

Actions to advance PO status.

- Order: status → Ordered
- Cancel: status → Cancelled (only if not received)
- Reopen: status → Draft

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Workflow service implemented
- [ ] `[SE]` Validation prevents cancel after receipt
- [ ] `[QA]` Actions work end-to-end

---

## FR-006 — Requisition to PO Conversion

Convert approved requisition lines into PO lines.

- Select vendor during conversion
- Group by vendor if multiple requisition lines
- Preserve quantities and products

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Conversion service implemented
- [ ] `[SE]` Creates PO from requisition lines
- [ ] `[QA]` Conversion preserves data

---

# Non-Functional Requirements

- Tenant isolation
- Status transitions audited
- Concurrent edit protection via optimistic locking (BaseEntity `version`)

---

# User Stories

- As a department head, I want to submit a requisition so that procurement can order goods.
- As a buyer, I want to convert approved requisitions into POs so that I can send orders to vendors.
- As a warehouse planner, I want to see expected delivery dates on POs.

---

# User Flow

```
Purchase Requisition
  ↓
Submit for Approval
  ↓
Approve
  ↓
Convert to Purchase Order
  ↓
Add Vendor & Prices
  ↓
Order (send to vendor)
```

---

# Scope

## Included

- Purchase requisition
- Purchase order header and lines
- PO auto-calculation
- PO workflow actions
- Requisition to PO conversion

## Excluded

- Goods receipt (PRD-012)
- Supplier invoice (PRD-012)
- Advanced approval routing
- Purchase contracts

---

# UI / UX Requirements

- Requisition window with lines tab
- PO window with lines tab and totals footer
- Action buttons: Submit, Approve, Order, Cancel
- "Convert to PO" button on approved requisition

---

# API Requirements

- Runtime window CRUD for Requisition and Purchase Order
- `POST /api/v1/purchase/requisitions/{id}/submit`
- `POST /api/v1/purchase/requisitions/{id}/approve`
- `POST /api/v1/purchase/requisitions/{id}/reject`
- `POST /api/v1/purchase/requisitions/{id}/convert-to-order`
- `POST /api/v1/purchase/orders/{id}/order`
- `POST /api/v1/purchase/orders/{id}/cancel`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `pur_requisition`
- `pur_requisition_line`
- `pur_order`
- `pur_order_line`

## Relationships
- `pur_order.vendor_id` → `c_business_partner.id`
- `pur_order_line.order_id` → `pur_order.id`
- `pur_order_line.product_id` → `m_product.id`

---

# Security Requirements

- Roles:
  - `PURCHASE_REQUISITION_VIEW/CREATE`
  - `PURCHASE_ORDER_VIEW/CREATE/APPROVE`
- Tenant isolation

---

# Performance Requirements

- PO list < 1s for 10,000 records
- Calculation service < 200ms

---

# Logging Requirements

- Audit log for PO status changes
- Audit log for requisition approval

---

# Edge Cases

- Cancel partially received PO
- Change PO quantity after ordered
- Requisition with no approved lines

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Cancel after receipt | Medium | Status validation |

---

# Assumptions

- PRD-010 provides vendor and price list data
- Document sequence service available

---

# Dependencies

- PRD-001, PRD-003, PRD-006, PRD-007, PRD-010
- PRD-012 depends on this PRD

---

# Acceptance Criteria

- [ ] Requisition tables and forms created
- [ ] PO tables and forms created
- [ ] Auto-calculation works
- [ ] Workflow actions validated
- [ ] Requisition-to-PO conversion works

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed document sequences `PRQ`, `PO`
- Seed menu and access

---

# Testing Requirements

- Unit tests for calculation and workflow
- Integration test: requisition → approve → convert → order
- UI scenario: create and order a PO

---

# Future Enhancements

- Multi-level approval workflows
- Blanket purchase orders
- Drop-ship POs

---

# Open Questions

1. Should requisition approval be mandatory, or can POs be created directly?
2. Do we support drop-ship addresses on PO lines?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-010 — Vendor Management & Purchase Price Lists
- PRD-012 — Goods Receipt & Supplier Invoice Matching
