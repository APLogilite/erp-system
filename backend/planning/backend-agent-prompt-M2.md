# AI Code Agent Prompt — M2 Sales Order & Inventory Transaction Modules

You are a principal ERP architect.

Your task is to implement the first transactional modules of the ERP:

1. Sales Order
2. Inventory Transaction

These modules validate:

✓ Nested forms  
✓ Child grids  
✓ Complex relations  
✓ Document workflows  
✓ Inventory movements  
✓ Runtime actions  
✓ Approval processes

IMPORTANT:

Continue following the metadata-driven philosophy.

Avoid hardcoded screens, CRUD controllers, and business-specific UI.

All screens should render through the Runtime Engine.

---

# CONTEXT

Completed:

✓ Phase 0 – Architecture Freeze  
✓ T1–T6 Frontend Runtime  
✓ B1–B5 Backend Runtime  
✓ M1 Foundation Modules

Current Goal:

Build transactional modules.

---

# TARGET OUTCOME

After M2:

✓ Sales Orders operational  
✓ Order Lines operational  
✓ Inventory Transactions operational  
✓ Nested forms operational  
✓ Document workflows operational  
✓ Inventory movements operational  
✓ Runtime actions operational

---

# MODULES

```txt
sales
inventory
```

Backend:

```txt
com.erp.modules.sales
com.erp.modules.inventory
```

Frontend:

```txt
src/modules/sales
src/modules/inventory
```

---

# M2.1 — Sales Order

Purpose:

Customer order management.

---

## Sales Order Header

Fields:

```txt
documentNo
documentDate
customer
warehouse
status
description
totalAmount
currency
```

---

## Relations

```txt
many2one -> business_partner
many2one -> warehouse
one2many -> sales_order_lines
```

---

# M2.2 — Sales Order Line

Fields:

```txt
lineNo
product
description
quantity
uom
unitPrice
discount
lineAmount
```

Relations:

```txt
many2one -> product
many2one -> uom
```

---

# Calculated Fields

```txt
lineAmount = quantity * unitPrice
totalAmount = sum(lines)
```

Use:

```txt
JSON Logic expressions
```

where possible.

---

# M2.3 — Sales Order Workflow

States:

```txt
Draft
↓
Completed
↓
Approved
↓
Closed
```

Transitions:

```txt
COMPLETE
APPROVE
CLOSE
REOPEN
VOID
```

---

# Rules

Cannot approve:

```txt
No lines
Invalid customer
Zero amount
```

---

# M2.4 — Runtime Actions

Actions:

```txt
Add Line
Recalculate
Complete
Approve
Close
Print
```

---

# M2.5 — Nested Form Support

The following should work:

```txt
Sales Order
      ↓
Order Lines Grid
      ↓
Save Parent + Children
```

No custom save logic.

Must use:

```txt
Relation Engine
```

---

# M2.6 — Inventory Transaction Module

Purpose:

Track stock movement.

---

## Header

Fields:

```txt
documentNo
transactionDate
warehouse
transactionType
status
description
```

---

## Types

```txt
IN
OUT
TRANSFER
ADJUSTMENT
```

---

## Inventory Transaction Line

Fields:

```txt
product
location
quantity
uom
```

Relations:

```txt
many2one -> product
many2one -> warehouse_location
```

---

# M2.7 — Inventory Workflow

States:

```txt
Draft
↓
Completed
↓
Posted
↓
Closed
```

Transitions:

```txt
COMPLETE
POST
CLOSE
VOID
```

---

# Posting Rules

When posting:

```txt
Create stock movement
Update balances
Publish inventory events
```

---

# M2.8 — Stock Movement Service

Create:

```txt
StockMovementService
```

Responsibilities:

```txt
increase stock
decrease stock
transfer stock
adjust stock
```

Prepare for:

```txt
future reservation engine
```

---

# M2.9 — Inventory Events

Publish:

```txt
inventory.received
inventory.issued
inventory.transferred
inventory.adjusted
inventory.posted
```

---

# M2.10 — Runtime Rendering Validation

The following should render entirely from metadata:

```txt
Sales Order Form
Sales Order Grid
Inventory Transaction Form
Inventory Transaction Grid
```

---

# M2.11 — Search Validation

Support:

```txt
document search
customer search
date filtering
status filtering
pagination
sorting
```

---

# M2.12 — Permissions

Roles:

---

## Sales User

```txt
Create Orders
Edit Draft Orders
Cannot Approve
```

---

## Sales Manager

```txt
Approve Orders
Close Orders
```

---

## Inventory User

```txt
Create Inventory Transactions
Post Inventory Transactions
```

---

## Admin

```txt
Full Access
```

---

# M2.13 — Seed Data

Create:

```txt
Sample Products
Sample Customer
Sample Warehouse
Sample Orders
Sample Inventory Transactions
```

---

# M2.14 — Acceptance Tests

## Sales Order Form

Expected:

```txt
Form renders from metadata.
```

---

## Nested Save

Expected:

```txt
Header and lines saved together.
```

---

## Workflow

Expected:

```txt
Transitions execute correctly.
```

---

## Inventory Posting

Expected:

```txt
Stock balances updated.
```

---

## Permissions

Expected:

```txt
Security enforced.
```

---

## Search

Expected:

```txt
Pagination and filters work.
```

---

# SUCCESS CRITERIA

After M2:

```txt
Metadata
     ↓
Runtime Engine
     ↓
Transactional Modules
     ↓
Working ERP Documents
```

The platform now supports:

✓ Master Data  
✓ Transactions  
✓ Workflows  
✓ Relations  
✓ Permissions  
✓ Inventory Logic

---

# FINAL DELIVERABLE

Produce:

✓ Sales Order Module  
✓ Sales Order Lines  
✓ Inventory Transaction Module  
✓ Stock Movement Service  
✓ Workflows  
✓ Actions  
✓ Permissions  
✓ Metadata Definitions  
✓ Runtime Forms and Grids  
✓ End-to-End Validation
