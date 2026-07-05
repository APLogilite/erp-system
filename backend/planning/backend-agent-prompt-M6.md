# AI Code Agent Prompt — M6 Enterprise Modules (CRM, Projects, Service Management, HR & Assets)

You are a principal ERP architect.

Your task is to implement the Enterprise Business Modules that extend the ERP platform beyond core operations.

IMPORTANT:

These modules must be built on top of the existing metadata-driven runtime.

Do NOT build isolated applications.

Everything must use:

- Runtime Metadata
- Runtime Renderer
- Runtime CRUD Engine
- Workflow Engine
- Permission Engine
- Relation Engine
- Notification Engine (future-ready)

The implementation must remain completely metadata-driven.

---

# CONTEXT

Completed:

✓ Phase 0 – Architecture Freeze
✓ T1–T6 Frontend Runtime
✓ B1–B5 Backend Runtime
✓ M1 Foundation Modules
✓ M2 Sales & Inventory
✓ M3 Purchasing & Inventory
✓ M4 Accounting Foundation
✓ M5 Manufacturing & MRP

Current Goal:

Build Enterprise Modules.

---

# TARGET OUTCOME

After M6:

✓ CRM operational
✓ Lead Management operational
✓ Opportunity Management operational
✓ Project Management operational
✓ Task Management operational
✓ Service Management operational
✓ HR Foundation operational
✓ Employee Management operational
✓ Asset Management operational

---

# MODULES

```txt
crm
projects
service
hr
assets
```

Backend:

```txt
com.erp.modules.crm
com.erp.modules.projects
com.erp.modules.service
com.erp.modules.hr
com.erp.modules.assets
```

Frontend:

```txt
src/modules/crm
src/modules/projects
src/modules/service
src/modules/hr
src/modules/assets
```

---

# M6.1 — CRM Module

Purpose:

Manage customer acquisition and sales pipeline.

---

## Lead

Fields:

```txt
leadNumber
company
contactName
email
phone
source
status
owner
expectedValue
```

Workflow:

```txt
New
↓
Qualified
↓
Converted
↓
Closed
```

---

## Opportunity

Fields:

```txt
opportunityNumber
businessPartner
stage
probability
expectedRevenue
expectedCloseDate
salesperson
```

Workflow:

```txt
Open
↓
Proposal
↓
Negotiation
↓
Won
↓
Lost
```

Relations:

```txt
many2one -> business_partner
many2one -> employee
```

---

# M6.2 — Project Management

Purpose:

Manage projects and execution.

---

## Project

Fields:

```txt
projectCode
name
customer
manager
startDate
endDate
status
budget
```

Relations:

```txt
many2one -> business_partner
many2one -> employee
one2many -> tasks
```

---

## Task

Fields:

```txt
taskNumber
title
description
priority
assignedTo
plannedHours
actualHours
status
```

Workflow:

```txt
Open
↓
Assigned
↓
In Progress
↓
Completed
↓
Closed
```

---

# M6.3 — Service Management

Purpose:

Handle customer support and field service.

---

## Service Request

Fields:

```txt
ticketNumber
customer
priority
category
assignedEngineer
status
description
resolution
```

Workflow:

```txt
New
↓
Assigned
↓
In Progress
↓
Resolved
↓
Closed
```

---

# M6.4 — HR Foundation

Purpose:

Manage employees and organization.

---

## Employee

Fields:

```txt
employeeCode
firstName
lastName
email
phone
department
designation
manager
joiningDate
status
```

Relations:

```txt
many2one -> department
many2one -> employee
```

---

## Department

Fields:

```txt
departmentCode
name
parentDepartment
manager
```

Support:

```txt
Tree hierarchy
```

---

# M6.5 — Asset Management

Purpose:

Track company assets.

---

## Asset

Fields:

```txt
assetCode
assetName
assetType
purchaseDate
purchaseCost
currentValue
assignedTo
location
status
```

Workflow:

```txt
Draft
↓
Active
↓
Maintenance
↓
Disposed
```

---

# M6.6 — Metadata

Each module must provide:

```txt
Model Definitions
Field Definitions
Views
Layouts
Actions
Permissions
Workflows
Expressions
```

Everything must render dynamically.

---

# M6.7 — Cross Module Relations

Support:

CRM

```txt
Lead
→ Opportunity
→ Customer
→ Sales Order
```

Projects

```txt
Project
→ Tasks
→ Customer
→ Employees
```

Service

```txt
Ticket
→ Customer
→ Asset
→ Employee
```

HR

```txt
Employee
→ Department
→ Manager
```

Assets

```txt
Asset
→ Employee
→ Warehouse
```

---

# M6.8 — Runtime Actions

Examples:

CRM

```txt
Convert Lead
Create Opportunity
Generate Sales Order
```

Projects

```txt
Create Task
Assign Employee
Complete Project
```

Service

```txt
Assign Engineer
Resolve Ticket
Close Ticket
```

Assets

```txt
Assign Asset
Transfer Asset
Dispose Asset
```

---

# M6.9 — Search Validation

Support:

```txt
Global search
Advanced filters
Saved filters
Sorting
Pagination
Quick search
```

---

# M6.10 — Permissions

CRM User

```txt
Manage Leads
Manage Opportunities
```

Project Manager

```txt
Manage Projects
Approve Tasks
```

Service Engineer

```txt
Manage Assigned Tickets
```

HR Manager

```txt
Manage Employees
Departments
```

Asset Manager

```txt
Manage Assets
Transfers
```

Admin

```txt
Full Access
```

---

# M6.11 — Seed Data

Create:

CRM

```txt
Sample Leads
Sample Opportunities
```

Projects

```txt
Internal ERP Project
Customer Implementation Project
```

HR

```txt
Departments
Employees
```

Service

```txt
Sample Tickets
```

Assets

```txt
Laptops
Servers
Printers
```

---

# M6.12 — Acceptance Tests

CRM

Expected:

```txt
Lead converts into Opportunity.
```

---

Projects

Expected:

```txt
Project generates tasks.
```

---

HR

Expected:

```txt
Department hierarchy works.
```

---

Assets

Expected:

```txt
Assets assigned to employees.
```

---

Service

Expected:

```txt
Ticket workflow executes correctly.
```

---

Runtime

Expected:

```txt
All screens rendered entirely from metadata.
```

---

Permissions

Expected:

```txt
Role-based security enforced.
```

---

Cross Module

Expected:

```txt
Relations between modules function correctly.
```

---

# SUCCESS CRITERIA

After M6:

```txt
Metadata
      ↓
Runtime Engine
      ↓
Enterprise Modules
      ↓
CRM
Projects
Service
HR
Assets
```

The ERP now supports:

✓ Sales
✓ Purchasing
✓ Inventory
✓ Manufacturing
✓ Accounting
✓ CRM
✓ Project Management
✓ Service Management
✓ Human Resources
✓ Asset Management

---

# FINAL DELIVERABLE

Produce:

✓ CRM Module
✓ Lead & Opportunity Management
✓ Project Management Module
✓ Task Management Module
✓ Service Management Module
✓ HR Foundation
✓ Employee & Department Modules
✓ Asset Management Module
✓ Metadata Definitions
✓ Runtime Forms & Grids
✓ Workflows
✓ Permissions
✓ Seed Data
✓ End-to-End Validation

This prepares the platform for:

# M7 — Business Intelligence, Reporting, Dashboard Engine & Analytics Platform