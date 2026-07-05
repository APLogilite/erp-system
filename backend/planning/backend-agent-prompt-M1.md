# AI Code Agent Prompt — M1 Foundation Business Modules

You are a principal ERP architect.

Your task is to implement the **Foundation Business Modules** that will validate the entire metadata-driven ERP platform.

IMPORTANT:

This is not just module development.

These modules are used to prove that:

✓ Metadata Engine works  
✓ Runtime Renderer works  
✓ Runtime CRUD works  
✓ Relation Engine works  
✓ Workflow Engine works  
✓ Permission Engine works

The implementation should use the runtime platform as much as possible and avoid hardcoded screens and APIs.

---

# CONTEXT

Completed:

✓ Phase 0 – Architecture Freeze  
✓ T1 – Project Structure  
✓ T2 – Design System  
✓ T3 – State Management  
✓ B1 – Metadata API Foundation  
✓ T4 – Metadata Schema Design  
✓ T5 – Registry System  
✓ T6 – Runtime Renderer  
✓ B2 – Runtime CRUD Engine  
✓ B3 – Relation Engine  
✓ B4 – Workflow Engine  
✓ B5 – Permission Engine

Current Goal:

Implement Foundation Business Modules.

---

# TARGET MODULES

Implement:

```txt
1. Product
2. Business Partner
3. Warehouse
```

These modules will become dependencies for:

```txt
Sales
Purchase
Inventory
Manufacturing
Accounting
CRM
```

---

# MODULE PACKAGE STRUCTURE

Backend:

```txt
com.erp.modules

├── product
├── businesspartner
└── warehouse
```

Frontend:

```txt
src/modules

├── product
├── business-partner
└── warehouse
```

---

# M1.1 — Product Module

Purpose:

Master data for all inventory items.

---

## Product Entity

Fields:

```txt
code
name
description
sku
barcode
uom
productType
isStocked
isSold
isPurchased
isActive
```

---

## Product Types

```txt
ITEM
SERVICE
EXPENSE
DIGITAL
```

---

## Relations

```txt
many2one -> product_category
many2one -> uom
many2many -> tags
```

---

## Metadata

Create:

```txt
model
fields
views
layouts
permissions
workflow
actions
```

---

# M1.2 — Product Category

Fields:

```txt
code
name
parent
description
```

Relation:

```txt
tree
```

---

# M1.3 — Business Partner Module

Purpose:

Customers, Vendors, Employees.

---

## Business Partner Fields

```txt
code
name
partnerType
email
phone
mobile
website
taxId
isCustomer
isVendor
isEmployee
isActive
```

---

## Address

Support:

```txt
billing address
shipping address
multiple addresses
```

---

## Relations

```txt
one2many -> addresses
many2many -> contacts
```

---

# M1.4 — Address

Fields:

```txt
line1
line2
city
state
postalCode
country
```

---

# M1.5 — Warehouse Module

Purpose:

Inventory structure.

---

## Warehouse Fields

```txt
code
name
description
isActive
```

---

## Relations

```txt
one2many -> locations
```

---

# M1.6 — Location

Fields:

```txt
code
name
parent
locationType
```

---

## Location Types

```txt
WAREHOUSE
STORAGE
BIN
TRANSIT
```

---

## Tree Support

Must use:

```txt
parent-child hierarchy
```

---

# M1.7 — Metadata

Each module must provide:

```txt
ModelDefinition
FieldDefinitions
Views
Layouts
Actions
Permissions
Workflow
```

No hardcoded forms.

---

# M1.8 — Runtime Rendering Validation

The following should work:

```txt
Load metadata
        ↓
Runtime Renderer
        ↓
Working Form
```

for:

```txt
Product
Business Partner
Warehouse
```

---

# M1.9 — Search Validation

Runtime CRUD should support:

```txt
search
filter
pagination
sorting
```

for all modules.

---

# M1.10 — Relation Validation

Must validate:

```txt
many2one
one2many
many2many
tree
```

---

# M1.11 — Workflow Validation

Create simple workflows:

---

## Product

```txt
Draft
↓
Active
↓
Archived
```

---

## Business Partner

```txt
Draft
↓
Active
↓
Inactive
```

---

## Warehouse

```txt
Draft
↓
Active
↓
Closed
```

---

# M1.12 — Permissions

Create:

## User

```txt
View
Create
Edit
```

---

## Manager

```txt
Approve
Delete
```

---

## Admin

```txt
Full Access
```

---

# M1.13 — Seed Data

Create sample data:

Products:

```txt
Laptop
Keyboard
Consulting Service
```

Business Partners:

```txt
ABC Customer
XYZ Supplier
```

Warehouse:

```txt
Main Warehouse
Finished Goods
```

---

# M1.14 — Acceptance Tests

## Product Form

Expected:

```txt
Renders entirely from metadata.
```

---

## Business Partner Form

Expected:

```txt
Renders entirely from metadata.
```

---

## Warehouse Form

Expected:

```txt
Renders entirely from metadata.
```

---

## Search

Expected:

```txt
Filtering and pagination work.
```

---

## Relations

Expected:

```txt
Relations load correctly.
```

---

## Workflow

Expected:

```txt
Transitions execute correctly.
```

---

## Permissions

Expected:

```txt
Security enforced.
```

---

# SUCCESS CRITERIA

After M1, the platform should prove:

```txt
Metadata
     ↓
Runtime Engine
     ↓
Business Modules
     ↓
Working ERP
```

If M1 succeeds, the architecture is validated.

---

# FINAL DELIVERABLE

Produce:

✓ Product Module  
✓ Business Partner Module  
✓ Warehouse Module  
✓ Metadata Definitions  
✓ Runtime Forms  
✓ Runtime Grids  
✓ Workflows  
✓ Permissions  
✓ Seed Data  
✓ End-to-End Validation
