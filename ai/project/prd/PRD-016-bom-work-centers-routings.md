---
id: PRD-016
title: Product BOM, Work Centers & Routings
version: 1.0.0
status: IN_DEVELOPMENT
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-09-01
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-016-bom-work-centers-routings
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

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-003 must be COMPLETED (products, UOMs)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Bill of materials, work centers, and routings
  - 1.0.0 APPROVED — User approved for implementation
---

# Executive Summary

This PRD defines the manufacturing master data required before production can be planned: bills of materials (BOM), work centers, and routings. It supports single-level BOMs with components and operations.

---

# Problem Statement

**What problem are we solving?**

- No way to define what components make up a finished product
- No work center or operation definitions
- No routing to sequence operations

**Who experiences this problem?**

- Production planners
- Manufacturing engineers
- Cost accountants

**Why is this feature required?**

BOMs, work centers, and routings are prerequisites for manufacturing orders and cost calculation.

---

# Business Goals

- Define multi-version BOMs per product
- Define work centers with capacity and cost rates
- Define routings with operation sequences
- Support BOM explosion for material requirements

---

# Functional Requirements

## FR-001 — Work Center

Create work centers.

- Fields: code, name, warehouse, cost center, capacity per day, setup time rate, run time rate, is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `mfg_work_center` table created and registered
- [ ] `[SE]` Work center window seeded
- [ ] `[QA]` Work centers can be maintained

---

## FR-002 — Operation Master

Define operations.

- Fields: code, name, description, default work center, setup time, run time per unit

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `mfg_operation` table created and registered
- [ ] `[SE]` Operation window seeded
- [ ] `[QA]` Operations can be maintained

---

## FR-003 — Routing Header

Create routings for finished products.

- Fields: product, name, version, is_default, is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `mfg_routing` table created and registered
- [ ] `[SE]` Routing window seeded
- [ ] `[QA]` Routings can be created

---

## FR-004 — Routing Operations

Sequence operations in a routing.

- Fields: routing, operation, sequence no, work center, setup time, run time, description

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `mfg_routing_operation` table created and registered
- [ ] `[SE]` Operations tab seeded
- [ ] `[QA]` Operations can be sequenced

---

## FR-005 — BOM Header

Create bill of materials.

- Fields: product (finished good), name, version, is_default, is_active, UOM, quantity (base quantity)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `mfg_bom` table created and registered
- [ ] `[SE]` BOM window seeded
- [ ] `[QA]` BOMs can be created

---

## FR-006 — BOM Lines

Add components to BOM.

- Fields: BOM, component product, quantity, UOM, scrap %, is_critical
- Component can be raw material or sub-assembly

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `mfg_bom_line` table created and registered
- [ ] `[SE]` Lines tab seeded
- [ ] `[QA]` Components can be added

---

## FR-007 — BOM Explosion Service

Compute component requirements for a given finished product quantity.

- Single-level explosion in v1.0
- Returns list of component products and quantities

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `BomExplosionService` implemented
- [ ] `[SE]` Uses default BOM
- [ ] `[QA]` Explosion returns correct quantities

---

# Non-Functional Requirements

- Tenant isolation
- Default BOM uniqueness per product
- Soft-delete

---

# User Stories

- As a manufacturing engineer, I want to define BOMs so that production knows what materials to consume.
- As a production planner, I want routings so that I know the operation sequence.

---

# User Flow

```
Define Work Centers
  ↓
Define Operations
  ↓
Create Routing (sequence operations)
  ↓
Create BOM (finished good + components)
  ↓
Explode BOM for Material Requirements
```

---

# Scope

## Included

- Work centers
- Operations
- Routings
- BOM header and lines
- Single-level BOM explosion

## Excluded

- Multi-level BOM explosion
- Manufacturing orders (PRD-017)
- Capacity planning
- Standard cost roll-up
- Quality control

---

# UI / UX Requirements

- BOM window with component lines tab
- Routing window with operations tab
- Work center window with capacity fields
- Menu group: **Manufacturing**

---

# API Requirements

- Runtime window CRUD for Work Center, Operation, Routing, BOM
- `POST /api/v1/manufacturing/boms/{id}/explode?quantity={qty}`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `mfg_work_center`
- `mfg_operation`
- `mfg_routing`
- `mfg_routing_operation`
- `mfg_bom`
- `mfg_bom_line`

## Relationships
- `mfg_routing.product_id` → `m_product.id`
- `mfg_bom.product_id` → `m_product.id`
- `mfg_bom_line.component_product_id` → `m_product.id`
- `mfg_routing_operation.operation_id` → `mfg_operation.id`
- `mfg_routing_operation.work_center_id` → `mfg_work_center.id`

---

# Security Requirements

- Roles:
  - `BOM_VIEW/CREATE`
  - `ROUTING_VIEW/CREATE`
  - `WORK_CENTER_VIEW/CREATE`
- Tenant isolation

---

# Performance Requirements

- BOM explosion < 500ms for 50 components
- Routing list < 1s

---

# Logging Requirements

- Audit log for BOM changes

---

# Edge Cases

- Multiple BOM versions for same product
- BOM component is the same as finished product (recursive)
- Component UOM different from product UOM

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Recursive BOM | Medium | Detect and reject cycles |

---

# Assumptions

- Products and UOMs exist from PRD-003
- Single-level BOM sufficient for v1.0

---

# Dependencies

- PRD-001, PRD-003
- PRD-017 depends on this PRD

---

# Acceptance Criteria

- [ ] Work center, operation, routing tables created
- [ ] BOM tables created
- [ ] BOM explosion service works
- [ ] Forms seeded and functional

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed menu and access

---

# Testing Requirements

- Unit tests for BOM explosion
- UI scenario: create BOM and explode

---

# Future Enhancements

- Multi-level BOM explosion
- Phantom BOMs
- Routing time/cost rollup
- Quality control checkpoints

---

# Open Questions

1. Do we need by-products or co-products in BOMs?
2. Should BOM support multiple UOMs per component?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-017 — Manufacturing Orders & Basic MRP
