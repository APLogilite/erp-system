
---

# ERP Identity Hierarchy

```text
ERP Platform
│
├── Tenant (Business Entity / Customer)
│
├── Organization (Legal / Regional Structure)
│
├── Company (Legal Company)
│
├── Branch (Physical Location)
│
├── Department (Operational Unit)
│
├── User
│
└── Role
```

---

# Real World Example

Imagine your ERP platform serves multiple customers.

```text
ERP Platform
│
├── ABC Industries
│
├── XYZ Pharmaceuticals
│
└── Global Logistics
```

Each one is a Tenant.

They never see each other's data.

---

# Tenant

The highest business boundary.

Example:

```text
Tenant

Code : ABC

Name : ABC Industries Pvt Ltd

Status : Active

Timezone : Asia/Kolkata

Currency : INR

Language : English

Default Theme : Light
```

A tenant owns:

```text
Organizations

Users

Roles

Permissions

Metadata

Products

Sales Orders

Inventory

Accounting

Reports
```

Nothing crosses tenants.

---

# Organization

Represents the business structure.

Example:

```text
ABC Industries

│

├── India

├── USA

└── Europe
```

Organizations can be hierarchical.

Example:

```text
India

├── Gujarat Region

├── Maharashtra Region

└── Delhi Region
```

Organizations are used for:

* reporting
* security
* workflow
* approval hierarchy

---

# Company

A legal company.

Example:

```text
Organization

India

↓

Companies

ABC Manufacturing Ltd

ABC Retail Ltd

ABC Services Ltd
```

Each company has:

```text
GST Number

PAN

Address

Currency

Fiscal Calendar

Financial Books

Warehouse

Tax Rules
```

Accounting is always company-specific.

---

# Branch

Physical office or warehouse.

Example:

```text
ABC Manufacturing Ltd

↓

Ahmedabad

Mumbai

Pune

Delhi
```

Each branch has:

```text
Address

Warehouse

Employees

Cash Counter

Stock

Local Manager
```

Inventory belongs to Branch.

---

# Department

Operational team.

Example:

```text
Ahmedabad

↓

Sales

Purchase

HR

Finance

Production

QA

IT
```

Departments are mainly for:

* approvals
* employee assignment
* reporting

---

# User

Example

```text
John Smith

↓

Email

Username

Password

Language

Timezone

Status
```

A user does NOT own data.

The user owns assignments.

---

# Role

A user may have many roles.

Example

```text
Sales Executive

Sales Manager

Finance Manager

Administrator

Warehouse Operator
```

Each role has permissions.

---

# User Assignment Model

This is how I would model it.

```text
John Smith

↓

Tenant

ABC Industries

↓

Organizations

India

USA

↓

Companies

ABC Manufacturing

ABC Retail

↓

Branches

Ahmedabad

Mumbai

↓

Roles

Sales Manager

Finance Viewer
```

Notice:

The user can belong to multiple companies.

---

# Login Flow

Step 1

```text
Username

Password
```

↓

Authenticated

---

Step 2

System loads:

```text
Accessible Tenants

Accessible Organizations

Accessible Companies

Accessible Branches

Accessible Roles
```

---

### Scenario A

Only one option exists.

```text
Tenant

ABC

Only one

↓

Auto Select
```

Organization

```text
India

Only one

↓

Auto Select
```

Company

```text
Manufacturing

Only one

↓

Auto Select
```

Branch

```text
Ahmedabad

Only one

↓

Auto Select
```

Role

```text
Sales Manager

Only one

↓

Open ERP
```

The user never sees a selection screen.

---

### Scenario B

Multiple options exist.

Example

```text
John

↓

Tenant

ABC Industries

XYZ Pharma

```

User chooses:

```text
ABC Industries
```

Next

```text
Organization

India

USA
```

Choose

```text
India
```

Next

```text
Company

Manufacturing

Retail
```

Choose

```text
Manufacturing
```

Next

```text
Branch

Ahmedabad

Mumbai
```

Choose

```text
Ahmedabad
```

Next

```text
Role

Sales Manager

Inventory Manager
```

Choose

```text
Sales Manager
```

Then ERP opens.

---

# Runtime Context

The system now creates a Runtime Context.

```json
{
  "tenant": "ABC Industries",
  "organization": "India",
  "company": "ABC Manufacturing",
  "branch": "Ahmedabad",
  "department": "Sales",
  "role": "Sales Manager",
  "language": "English",
  "timezone": "Asia/Kolkata",
  "currency": "INR"
}
```

Every API automatically receives this context.

---

# Changing Context

Without logging out:

```
Current Context

ABC Industries

India

Manufacturing

Ahmedabad

Sales Manager

▼ Change Context
```

User changes:

```
Role

Inventory Manager
```

or

```
Branch

Mumbai
```

Everything refreshes automatically.

---

# Language

Language belongs to the user.

Example

```
English

Hindi

Gujarati

German

French
```

It changes:

* UI labels
* Metadata labels
* Reports
* Date formatting

---

# Timezone

Timezone also belongs to the user.

Example

```
Asia/Kolkata

Europe/London

America/New_York
```

This affects:

* Audit timestamps
* Scheduler execution display
* Calendar
* Reports
* Workflow history

The database should continue storing timestamps in UTC, converting them to the user's timezone only for display.

---

# Administrator Workflow

The complete setup process for a new customer should be:

```text
Create Tenant
        │
        ▼
Create Organization(s)
        │
        ▼
Create Company(ies)
        │
        ▼
Create Branch(es)
        │
        ▼
Create Department(s)
        │
        ▼
Create Roles
        │
        ▼
Create Permissions
        │
        ▼
Create Users
        │
        ▼
Assign Users to Organizations
        │
        ▼
Assign Users to Companies
        │
        ▼
Assign Users to Branches
        │
        ▼
Assign Roles
        │
        ▼
Set Default Language
        │
        ▼
Set Default Timezone
        │
        ▼
User Logs In
        │
        ▼
Context Created
        │
        ▼
ERP Workspace Opens
```

## One enhancement I'd make

Rather than making the user pick **Tenant → Organization → Company → Branch → Role** one by one every time, create a **Context Profile**.

For example:

```text
Available Contexts

✓ Sales Manager - Ahmedabad Manufacturing
  ABC Industries / India / Manufacturing / Ahmedabad

✓ Inventory Manager - Mumbai Warehouse
  ABC Industries / India / Manufacturing / Mumbai

✓ Finance Manager - USA HQ
  ABC Industries / USA / Finance / New York
```

The user selects **one profile**, and the platform automatically resolves Tenant, Organization, Company, Branch, Department, Role, Language, Timezone, and Currency in a single step.

This provides a much better user experience while still preserving the flexibility of the underlying hierarchy. It's a pattern used in many mature enterprise systems because users typically think in terms of their **job assignment**, not individual hierarchy levels.
