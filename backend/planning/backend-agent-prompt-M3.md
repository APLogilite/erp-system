# AI Code Agent Prompt — M3 Purchasing, Stock Reservation & Advanced Inventory

You are a principal ERP architect.

Your task is to implement the next phase of the ERP:

1. Purchase Management
2. Stock Reservation Engine
3. Advanced Inventory
4. Inventory Availability Engine

These modules validate:

✓ Complex document flows  
✓ Cross-module workflows  
✓ Inventory reservations  
✓ Availability calculations  
✓ Stock allocation  
✓ Future manufacturing readiness

IMPORTANT:

Continue following the metadata-driven philosophy.

Avoid module-specific hacks and hardcoded business flows.

Everything should integrate with the Runtime Engine.

---

# CONTEXT

Completed:

✓ Phase 0 – Architecture Freeze  
✓ T1–T6 Frontend Runtime  
✓ B1–B5 Backend Runtime  
✓ M1 Foundation Modules  
✓ M2 Sales & Inventory Modules

Current Goal:

Build Purchasing and Advanced Inventory.

---

# TARGET OUTCOME

After M3:

✓ Purchase Orders operational  
✓ Reservation Engine operational  
✓ Available-to-Promise calculations operational  
✓ Inventory balances operational  
✓ Allocation engine operational  
✓ Procurement foundation operational

---

# MODULES

```txt
purchase
inventory
reservation
```

Backend:

```txt
com.erp.modules.purchase
com.erp.modules.inventory
com.erp.modules.reservation
```

Frontend:

```txt
src/modules/purchase
src/modules/inventory
```

---

# M3.1 — Purchase Order

Purpose:

Vendor procurement management.

---

## Purchase Order Header

Fields:

```txt
documentNo
documentDate
vendor
warehouse
status
description
currency
totalAmount
expectedDate
```

Relations:

```txt
many2one -> business_partner
many2one -> warehouse
one2many -> purchase_order_lines
```

---

# M3.2 — Purchase Order Line

Fields:

```txt
lineNo
product
description
quantity
receivedQuantity
unitPrice
discount
lineAmount
expectedDate
```

Relations:

```txt
many2one -> product
```

---

# Calculated Fields

```txt
lineAmount = quantity × unitPrice
totalAmount = sum(lines)
```

---

# M3.3 — Purchase Workflow

States:

```txt
Draft
↓
Completed
↓
Approved
↓
Received
↓
Closed
```

Transitions:

```txt
COMPLETE
APPROVE
RECEIVE
CLOSE
VOID
REOPEN
```

---

# Validation Rules

Cannot approve:

```txt
No lines
Invalid vendor
Zero amount
```

Cannot receive:

```txt
Not approved
```

---

# M3.4 — Goods Receipt Integration

Receiving a PO should:

```txt
Create inventory transaction
Increase stock
Update received quantities
Publish events
```

---

# M3.5 — Stock Reservation Engine

Purpose:

Reserve inventory for future demand.

Examples:

```txt
Sales Orders
Manufacturing Orders
Transfers
```

---

# Reservation Fields

```txt
product
warehouse
location
quantity
reservedQuantity
sourceDocument
sourceLine
status
```

---

# Reservation Status

```txt
Draft
Reserved
Released
Consumed
Cancelled
```

---

# Reservation Service

Create:

```txt
ReservationService
```

Responsibilities:

```txt
reserve()
release()
consume()
recalculate()
```

---

# M3.6 — Inventory Availability Engine

Create:

```txt
InventoryAvailabilityService
```

Responsibilities:

```txt
getOnHand()
getReserved()
getAvailable()
getIncoming()
getOutgoing()
getATP()
```

---

# Formula

```txt
Available =
OnHand
- Reserved
+ Incoming
- Outgoing
```

---

# M3.7 — Stock Allocation Engine

Create:

```txt
AllocationService
```

Responsibilities:

```txt
allocate()
reallocate()
deallocate()
```

Support:

```txt
FIFO
manual allocation
future strategies
```

---

# M3.8 — Inventory Balance

Create:

```txt
InventoryBalance
```

Fields:

```txt
product
warehouse
location
onHand
reserved
available
```

---

# M3.9 — Inventory Events

Publish:

```txt
stock.reserved
stock.released
stock.consumed
stock.allocated
stock.received
stock.balance.updated
```

---

# M3.10 — Runtime Rendering Validation

The following should render entirely from metadata:

```txt
Purchase Order Form
Purchase Order Grid
Reservation Form
Inventory Balance View
```

---

# M3.11 — Search Validation

Support:

```txt
vendor search
product search
inventory search
availability search
date filters
pagination
sorting
```

---

# M3.12 — Permissions

Roles:

---

## Purchase User

```txt
Create Purchase Orders
Edit Draft Purchase Orders
```

---

## Purchase Manager

```txt
Approve Purchase Orders
Receive Goods
Close Orders
```

---

## Inventory User

```txt
Reserve Inventory
Release Inventory
```

---

## Admin

```txt
Full Access
```

---

# M3.13 — Seed Data

Create:

```txt
Sample Vendors
Sample Purchase Orders
Sample Reservations
Sample Inventory Balances
```

---

# M3.14 — Acceptance Tests

## Purchase Order Form

Expected:

```txt
Rendered from metadata.
```

---

## Approval Workflow

Expected:

```txt
Transitions execute correctly.
```

---

## Goods Receipt

Expected:

```txt
Inventory updated.
```

---

## Reservation

Expected:

```txt
Stock reserved correctly.
```

---

## Availability

Expected:

```txt
ATP calculated correctly.
```

---

## Allocation

Expected:

```txt
Inventory allocated correctly.
```

---

## Permissions

Expected:

```txt
Security enforced.
```

---

# SUCCESS CRITERIA

After M3:

```txt
Metadata
      ↓
Runtime Engine
      ↓
Procurement
      ↓
Reservations
      ↓
Advanced Inventory
```

The platform now supports:

✓ Sales  
✓ Purchasing  
✓ Inventory  
✓ Reservations  
✓ Availability Calculations  
✓ Allocation Logic

---

# FINAL DELIVERABLE

Produce:

✓ Purchase Order Module  
✓ Goods Receipt Integration  
✓ Reservation Engine  
✓ Availability Engine  
✓ Allocation Engine  
✓ Inventory Balances  
✓ Metadata Definitions  
✓ Workflows  
✓ Permissions  
✓ End-to-End Validation
