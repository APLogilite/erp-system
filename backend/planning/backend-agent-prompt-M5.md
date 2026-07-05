# AI Code Agent Prompt — M5 Manufacturing, MRP & Production Engine

You are a principal ERP architect.

Your task is to implement the Manufacturing (MRP) platform for the metadata-driven ERP.

IMPORTANT:

This is not just a Manufacturing module.

This phase introduces the production engine that connects:

- Products
- Inventory
- Warehouses
- Reservations
- Purchasing
- Accounting
- Workflows

The implementation must remain metadata-driven and reusable.

Avoid hardcoded manufacturing logic tied to specific products or industries.

---

# CONTEXT

Completed:

✓ Phase 0 – Architecture Freeze
✓ T1–T6 Frontend Runtime
✓ B1–B5 Backend Runtime
✓ M1 Foundation Modules
✓ M2 Sales & Inventory
✓ M3 Purchasing & Advanced Inventory
✓ M4 Accounting Foundation

Current Goal:

Build Manufacturing & MRP.

---

# TARGET OUTCOME

After M5:

✓ Bill of Materials (BOM) operational
✓ Manufacturing Orders operational
✓ Work Orders operational
✓ Routing operational
✓ Material Requirement Planning (MRP) operational
✓ Production Consumption operational
✓ Finished Goods Production operational
✓ Manufacturing Cost foundation operational

---

# MODULES

```txt
manufacturing
mrp
production
routing
```

Backend:

```txt
com.erp.modules.manufacturing
```

Frontend:

```txt
src/modules/manufacturing
```

---

# M5.1 — Bill of Materials (BOM)

Purpose:

Define how finished products are manufactured.

---

## BOM Header

Fields:

```txt
code
name
product
revision
version
status
effectiveFrom
effectiveTo
description
```

Relations:

```txt
many2one -> product
one2many -> bom_lines
```

---

# BOM Line

Fields:

```txt
lineNo
component
quantity
uom
scrapPercentage
operation
```

Relations:

```txt
many2one -> product
many2one -> routing_operation
```

---

Support:

```txt
Multi-level BOM
Alternate BOM
Versioning
Effective Dates
```

---

# M5.2 — Routing

Purpose:

Manufacturing process definition.

---

Routing Header

Fields:

```txt
code
name
description
```

---

Routing Operation

Fields:

```txt
sequence
workCenter
operationName
setupTime
runTime
queueTime
```

Relations:

```txt
many2one -> work_center
```

---

# M5.3 — Work Center

Fields:

```txt
code
name
capacity
costPerHour
efficiency
calendar
```

---

# M5.4 — Manufacturing Order (MO)

Purpose:

Execute production.

---

Fields:

```txt
documentNo
product
bom
routing
warehouse
plannedQuantity
completedQuantity
plannedStart
plannedEnd
status
priority
```

Relations:

```txt
many2one -> product
many2one -> bom
many2one -> routing
one2many -> work_orders
```

---

# M5.5 — Work Orders

Generated automatically.

Fields:

```txt
sequence
operation
workCenter
plannedStart
plannedEnd
actualStart
actualEnd
status
```

Workflow:

```txt
Planned
↓
Ready
↓
In Progress
↓
Completed
↓
Closed
```

---

# M5.6 — Material Requirement Planning (MRP)

Create:

```txt
MRPService
```

Responsibilities:

```txt
explode BOM
calculate demand
calculate shortages
generate purchase suggestions
generate production suggestions
```

Support:

```txt
multi-level explosion
lead time
safety stock
minimum stock
```

---

# M5.7 — Material Reservation

When MO is released:

Automatically:

```txt
reserve components
allocate inventory
check availability
```

Integrate with:

```txt
Reservation Engine
```

---

# M5.8 — Material Consumption

Support:

```txt
planned consumption
actual consumption
scrap
returns
```

Posting:

```txt
Inventory OUT
```

---

# M5.9 — Finished Goods Receipt

After production completion:

Automatically:

```txt
Inventory IN
update balances
close reservations
```

Publish:

```txt
production.completed
```

---

# M5.10 — Manufacturing Workflow

Manufacturing Order:

```txt
Draft
↓
Planned
↓
Released
↓
In Production
↓
Completed
↓
Closed
```

Transitions:

```txt
PLAN
RELEASE
START
COMPLETE
CLOSE
VOID
```

---

# M5.11 — Production Cost Foundation

Track:

```txt
material cost
labor cost
machine cost
overhead
```

Create extension points for future costing.

---

# M5.12 — Manufacturing Events

Publish:

```txt
production.planned
production.released
production.started
production.completed
material.consumed
finished.goods.received
```

---

# M5.13 — Runtime Rendering Validation

Render entirely from metadata:

```txt
BOM Form
Routing Form
Manufacturing Order Form
Work Order Grid
MRP Dashboard
```

---

# M5.14 — Search Validation

Support:

```txt
product search
BOM search
routing search
manufacturing order search
status filters
date filters
pagination
sorting
```

---

# M5.15 — Permissions

Roles:

---

Manufacturing User

```txt
View BOM
Create Manufacturing Orders
Start Production
```

---

Production Supervisor

```txt
Release Orders
Complete Orders
Manage Work Orders
```

---

Production Manager

```txt
Approve BOM
Approve Routing
Close Manufacturing Orders
```

---

Admin

```txt
Full Access
```

---

# M5.16 — Seed Data

Create:

```txt
Finished Product
Raw Materials
Sample BOM
Sample Routing
Sample Work Center
Sample Manufacturing Order
```

---

# M5.17 — Acceptance Tests

## BOM

Expected:

```txt
Multi-level BOM loads correctly.
```

---

## Manufacturing Order

Expected:

```txt
Generated from BOM.
```

---

## Material Reservation

Expected:

```txt
Inventory reserved.
```

---

## Production Start

Expected:

```txt
Work Orders created.
```

---

## Production Completion

Expected:

```txt
Components consumed.
Finished goods received.
Inventory updated.
```

---

## MRP

Expected:

```txt
Purchase and production recommendations generated.
```

---

## Runtime Rendering

Expected:

```txt
All manufacturing screens rendered entirely from metadata.
```

---

## Permissions

Expected:

```txt
Security enforced across manufacturing workflow.
```

---

# SUCCESS CRITERIA

After M5:

```txt
Metadata
      ↓
Runtime Engine
      ↓
Manufacturing
      ↓
MRP
      ↓
Production Execution
      ↓
Inventory Integration
```

The ERP now supports:

✓ Sales
✓ Purchasing
✓ Inventory
✓ Reservations
✓ Accounting Foundation
✓ Manufacturing
✓ Production Planning
✓ Material Requirement Planning

---

# FINAL DELIVERABLE

Produce:

✓ Bill of Materials Module
✓ Routing Module
✓ Work Center Module
✓ Manufacturing Order Module
✓ Work Order Module
✓ MRP Engine
✓ Material Reservation Integration
✓ Production Consumption
✓ Finished Goods Receipt
✓ Manufacturing Workflows
✓ Metadata Definitions
✓ Runtime Forms & Grids
✓ Seed Data
✓ End-to-End Validation

This prepares the platform for:

# M6 — CRM, Projects, Service Management, HR & Enterprise Modules