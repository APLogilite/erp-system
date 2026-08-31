---
id: PRD-013
title: Warehouse Structure & Location Management
version: 1.0.0
status: IN_DEVELOPMENT
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-31
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-013-warehouse-location-management
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
  - PRD-004 (Window Hierarchy & Menu System v1.1.0)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-003 must be COMPLETED (warehouse master data)
  - PRD-004 must be COMPLETED
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Warehouse and inventory location structure
  - 1.0.0 APPROVED — User approved for implementation
  - 1.0.0 IN_DEVELOPMENT — Started implementation (TASK-105..107)
---

# Executive Summary

This PRD defines the warehouse and location hierarchy for inventory management. It supports multiple warehouses, zones, aisles, racks, bins, and location types (receipt, storage, picking, shipping, quarantine).

---

# Problem Statement

**What problem are we solving?**

- Only a flat warehouse table exists
- No location hierarchy for precise stock tracking
- Cannot route receipts to specific locations

**Who experiences this problem?**

- Warehouse managers
- Inventory controllers
- Receivers and pickers

**Why is this feature required?**

Locations are required for accurate stock movements, counting, and fulfillment.

---

# Business Goals

- Support multi-warehouse, multi-location hierarchy
- Define location types and usage rules
- Link locations to warehouses and branches
- Provide location lookup for receipts, shipments, and stock moves

---

# Functional Requirements

## FR-001 — Warehouse Enhancements

Extend warehouse master data.

- Fields: code, name, branch, address, manager, is_active
- Link to branch/company from identity module

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `m_warehouse` table extended
- [ ] `[SE]` Warehouse window updated
- [ ] `[QA]` Warehouse CRUD works

---

## FR-002 — Location Type

Define location types.

- Types: Receipt, Storage, Picking, Shipping, Quarantine, Scrap, Transit
- Fields: code, name, is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `m_locator_type` table created
- [ ] `[SE]` Location type window seeded
- [ ] `[QA]` Types can be maintained

---

## FR-003 — Location Hierarchy

Create locator/locations within warehouses.

- Fields: code, name, warehouse, parent location, location type, aisle, rack, bin, is_default_receipt, is_default_shipping, is_active
- Tree structure: Warehouse → Zone → Aisle → Rack → Bin

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `m_locator` table created with self-referencing parent
- [ ] `[SE]` Location window seeded
- [ ] `[SE]` Tree/hierarchy displayed
- [ ] `[QA]` Locations can be created in hierarchy

---

## FR-004 — Default Location Rules

Mark default receipt and shipping locations per warehouse.

- At most one default receipt location per warehouse
- At most one default shipping location per warehouse

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Validation for default location uniqueness
- [ ] `[QA]` Defaults enforced

---

## FR-005 — Location Lookup Service

Backend service to find locations by warehouse and type.

- Used by receipt, shipment, and stock move processes

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `LocationLookupService` implemented
- [ ] `[QA]` Service returns correct defaults

---

# Non-Functional Requirements

- Tenant isolation
- Soft-delete
- Location tree query performance < 300ms

---

# User Stories

- As a warehouse manager, I want to define bin locations so that stock can be tracked precisely.
- As a receiver, I want a default receipt location so that goods are put away correctly.

---

# User Flow

```
Warehouse
  ↓
Define Location Types
  ↓
Create Locations (Zone → Aisle → Rack → Bin)
  ↓
Set Default Receipt/Shipping Locations
  ↓
Used in Receipts, Shipments, Moves
```

---

# Scope

## Included

- Warehouse enhancements
- Location types
- Location hierarchy
- Default location rules
- Location lookup service

## Excluded

- Stock moves (PRD-014)
- Inventory counting (PRD-015)
- Barcode/RF scanning

---

# UI / UX Requirements

- Warehouse window with location child tab
- Location tree view or hierarchy-aware list
- Default location indicators

---

# API Requirements

- Runtime window CRUD for Warehouse, Location Type, Location
- `GET /api/v1/inventory/locations/default?warehouseId={id}&type={type}`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `m_locator_type`
- `m_locator`

## Modified Tables
- `m_warehouse` — add branch, manager, address fields

## Relationships
- `m_locator.warehouse_id` → `m_warehouse.id`
- `m_locator.parent_id` → `m_locator.id`
- `m_locator.location_type_id` → `m_locator_type.id`

---

# Security Requirements

- Roles:
  - `WAREHOUSE_VIEW/CREATE`
  - `LOCATION_VIEW/CREATE`
- Tenant isolation

---

# Performance Requirements

- Location tree load < 500ms
- Lookup service < 100ms

---

# Logging Requirements

- Audit log for location changes

---

# Edge Cases

- Circular parent references
- Deleting location with stock
- Multiple default locations

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Location hierarchy complexity | Medium | Limit depth to 5 levels |

---

# Assumptions

- `m_warehouse` exists from PRD-003
- Branch/company context from identity module

---

# Dependencies

- PRD-001, PRD-003, PRD-004
- PRD-014 depends on this PRD

---

# Acceptance Criteria

- [ ] Warehouse enhanced
- [ ] Location type and location tables created
- [ ] Hierarchy and defaults enforced
- [ ] Lookup service available

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed location types
- Seed menu and access

---

# Testing Requirements

- Unit tests for default location validation
- UI scenario: create warehouse → locations → defaults

---

# Future Enhancements

- Barcode per location
- Location capacity/volume
- Put-away strategies

---

# Open Questions

1. Maximum location hierarchy depth?
2. Do we need warehouse-zone-aisle-rack-bin explicit levels or free-form tree?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-012 — Goods Receipt & Supplier Invoice Matching
- PRD-014 — Stock Movements & Logistics
- PRD-015 — Inventory Adjustments, Counting & Valuation
