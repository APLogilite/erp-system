# PHASE 0 — Architecture Freeze

## Objective

Freeze ALL critical ERP runtime architecture decisions BEFORE major implementation begins.

This phase prevents:

* massive rewrites later
* incompatible frontend/backend contracts
* runtime instability
* plugin architecture failures
* metadata inconsistencies

---

# MOST IMPORTANT RULE

After Phase 0:

```txt id="p0a"
Core architecture decisions should NOT change frequently
```

because:

* frontend runtime depends on backend contracts
* metadata affects everything
* workflows affect permissions
* relations affect layouts
* plugins affect module architecture

---

# TARGET OUTCOME

After Phase 0:

```txt id="p0b"
✓ Runtime architecture finalized
✓ Metadata contracts frozen
✓ API conventions finalized
✓ Module structure finalized
✓ Relation strategy finalized
✓ Workflow strategy finalized
✓ Naming conventions standardized
✓ Frontend/backend contracts aligned
✓ Runtime rendering philosophy finalized
✓ ERP module strategy defined
```

---

# STEP-BY-STEP TASKS

---

# P0.1 — Define ERP Runtime Philosophy

## Objective

Freeze the core platform philosophy.

---

# MUST FINALIZE

## This platform IS:

```txt id="p0c"
Metadata-driven ERP runtime platform
```

---

## This platform is NOT:

```txt id="p0d"
Hardcoded CRUD application
```

---

# Runtime Principles

## Backend Responsibilities

Backend becomes:

```txt id="p0e"
ERP runtime execution engine
```

Responsibilities:

* metadata generation
* dynamic CRUD
* workflow execution
* permission enforcement
* runtime actions
* inventory logic

---

## Frontend Responsibilities

Frontend becomes:

```txt id="p0f"
ERP runtime renderer
```

Responsibilities:

* render metadata dynamically
* render forms/grids/layouts
* execute runtime UI logic
* workflow UI
* runtime navigation

---

# Acceptance Criteria

* all developers understand runtime philosophy
* no hardcoded screen mindset remains

---

# P0.2 — Freeze Metadata Architecture

## Objective

Finalize metadata-driven architecture contracts.

---

# MUST FINALIZE

## Metadata Domains

| Domain     | Responsibility              |
| ---------- | --------------------------- |
| Model      | data structure              |
| Field      | data definition             |
| View       | runtime rendering           |
| Layout     | UI arrangement              |
| Workflow   | state machine               |
| Action     | executable runtime behavior |
| Permission | access control              |
| Expression | dynamic logic               |

---

# Freeze Metadata Strategy

## Metadata Format

```txt id="p0g"
JSON
```

---

## Validation Strategy

```txt id="p0h"
Zod + TypeScript
```

---

## Backend Storage

```txt id="p0i"
PostgreSQL JSONB + relational tables
```

---

## Runtime Flow

```txt id="p0j"
Backend Metadata
→ API
→ Frontend Registry
→ Runtime Renderer
```

---

# Acceptance Criteria

* metadata boundaries clearly separated
* metadata responsibilities finalized

---

# P0.3 — Freeze Frontend Architecture

## Objective

Finalize frontend runtime architecture.

---

# MUST FINALIZE

## Core Stack

| Area         | Technology   |
| ------------ | ------------ |
| Framework    | React        |
| Language     | TypeScript   |
| Build Tool   | Vite         |
| UI           | MUI          |
| State        | Zustand      |
| Server State | React Query  |
| Grid         | AG Grid      |
| Validation   | Zod          |
| Routing      | React Router |

---

# Runtime Architecture

## Dynamic Rendering Strategy

Frontend should:

* load metadata
* resolve component registry
* render dynamically

---

## Registry Architecture

Must support:

* field registry
* layout registry
* action registry
* workflow registry

---

## Layout Philosophy

Support:

* recursive layouts
* tabs
* grids
* sections
* responsive layouts

---

# Acceptance Criteria

* frontend stack frozen
* runtime rendering strategy finalized

---

# P0.4 — Freeze Backend Architecture

## Objective

Finalize backend runtime architecture.

---

# MUST FINALIZE

## Core Stack

| Area       | Technology                   |
| ---------- | ---------------------------- |
| Framework  | Spring Boot                  |
| Language   | Java                         |
| ORM        | JPA/Hibernate                |
| Database   | PostgreSQL                   |
| Migration  | Flyway                       |
| Security   | Spring Security + JWT        |
| Cache      | Caffeine/Redis later         |
| Validation | Jakarta + runtime validation |

---

# Backend Runtime Philosophy

Backend should:

* avoid hardcoded CRUD controllers
* avoid hardcoded workflows
* avoid hardcoded permissions

Instead:

* metadata-driven runtime execution

---

# Module Architecture

Recommended:

```txt id="p0k"
core/
modules/
plugins/
shared/
```

---

# Acceptance Criteria

* backend runtime philosophy finalized
* module boundaries finalized

---

# P0.5 — Freeze API Standards

## Objective

Standardize all API contracts.

---

# MUST DEFINE

## API Structure

Example:

```txt id="p0l"
/api/runtime/*
/api/metadata/*
/api/auth/*
```

---

# Response Standard

## Success Response

```json id="p0m"
{
  "success": true,
  "data": {},
  "message": null
}
```

---

## Error Response

```json id="p0n"
{
  "success": false,
  "errorCode": "VALIDATION_ERROR",
  "message": "Invalid field"
}
```

---

# Pagination Standard

```json id="p0o"
{
  "items": [],
  "page": 1,
  "size": 20,
  "total": 200
}
```

---

# Acceptance Criteria

* API responses standardized
* frontend/backend aligned

---

# P0.6 — Freeze Naming Conventions

## Objective

Prevent naming chaos later.

---

# MUST DEFINE

## Naming Standards

| Item             | Convention |
| ---------------- | ---------- |
| Database Tables  | snake_case |
| Columns          | snake_case |
| Java Classes     | PascalCase |
| React Components | PascalCase |
| API Routes       | kebab-case |
| Metadata Codes   | snake_case |
| Variables        | camelCase  |

---

# Module Naming

Examples:

```txt id="p0p"
sales_order
business_partner
inventory_transaction
```

---

# Acceptance Criteria

* naming standards documented
* enforced consistently

---

# P0.7 — Freeze Relation Strategy

## Objective

Define ERP relation architecture.

---

# MUST SUPPORT

| Relation  | Example            |
| --------- | ------------------ |
| many2one  | order.customer     |
| one2many  | order.lines        |
| many2many | product.tags       |
| tree      | category hierarchy |

---

# MUST DEFINE

## Relation Loading Strategy

| Strategy  | Use               |
| --------- | ----------------- |
| lazy      | default           |
| eager     | small datasets    |
| paginated | large child grids |

---

# Relation APIs

Must support:

* lookup
* autocomplete
* nested save
* batch loading

---

# Acceptance Criteria

* relation architecture finalized
* frontend/backend aligned

---

# P0.8 — Freeze Workflow Strategy

## Objective

Finalize ERP workflow philosophy.

---

# MUST DEFINE

## Workflow Structure

```txt id="p0q"
states
transitions
guards
actions
permissions
```

---

# Example

```txt id="p0r"
Draft
→ Completed
→ Approved
→ Closed
```

---

# MUST DEFINE

## Transition Strategy

Transitions may:

* trigger actions
* trigger validations
* update inventory
* send notifications

---

# Acceptance Criteria

* workflow runtime structure finalized

---

# P0.9 — Freeze Permission Strategy

## Objective

Define ERP security architecture.

---

# MUST SUPPORT

| Level  | Example          |
| ------ | ---------------- |
| module | sales access     |
| menu   | menu visibility  |
| view   | screen access    |
| field  | readonly amount  |
| action | approve button   |
| row    | own records only |

---

# MUST DEFINE

## Permission Resolution Order

```txt id="p0s"
User
→ Roles
→ Permissions
→ Metadata Rules
→ Runtime Enforcement
```

---

# Acceptance Criteria

* permission strategy finalized

---

# P0.10 — Freeze Expression Strategy

## Objective

Define runtime expression engine.

---

# MUST FINALIZE

## Engine Choice

```txt id="p0t"
JSON Logic
```

---

# Supported Use Cases

```txt id="p0u"
- visibility
- readonly
- formulas
- validations
- workflow guards
- conditional layouts
```

---

# Example

```json id="p0v"
{
  ">": [
    { "var": "amount" },
    1000
  ]
}
```

---

# Acceptance Criteria

* expression engine standardized

---

# P0.11 — Freeze ERP Module Strategy

## Objective

Define initial business module evolution.

---

# FIRST MODULES

| Module                | Purpose             |
| --------------------- | ------------------- |
| Product               | inventory base      |
| Business Partner      | customer/vendor     |
| Warehouse             | inventory structure |
| Sales Order           | transactional flow  |
| Inventory Transaction | stock movement      |

---

# WHY THESE FIRST?

They validate:

* relations
* nested forms
* grids
* workflows
* permissions
* inventory logic

---

# Acceptance Criteria

* initial module roadmap frozen

---

# P0.12 — Freeze Plugin Architecture

## Objective

Prepare long-term extensibility.

---

# Plugins Must Support

```txt id="p0w"
- models
- fields
- views
- workflows
- menus
- actions
- permissions
```

---

# Plugin Structure

Recommended:

```txt id="p0x"
plugin.json
metadata/
backend/
frontend/
```

---

# Acceptance Criteria

* extension points identified

---

# P0.13 — Freeze Multi-Tenant Strategy

## Objective

Prepare future tenant scaling.

---

# MUST DECIDE

| Strategy              | Decision     |
| --------------------- | ------------ |
| DB per tenant         | no           |
| Schema per tenant     | maybe future |
| Shared DB + tenant_id | YES          |

---

# Acceptance Criteria

* tenant strategy frozen

---

# P0.14 — Freeze Audit Strategy

## Objective

Prepare enterprise traceability.

---

# MUST TRACK

```txt id="p0y"
- createdBy
- updatedBy
- workflow history
- field changes
- actions executed
```

---

# Acceptance Criteria

* audit strategy finalized

---

# P0.15 — Freeze Testing Strategy

## Objective

Define testing philosophy early.

---

# MUST DEFINE

| Type                | Tool           |
| ------------------- | -------------- |
| Frontend Unit       | Vitest         |
| Frontend E2E        | Playwright     |
| Backend Unit        | JUnit          |
| Backend Integration | Testcontainers |
| API Testing         | REST Assured   |

---

# Acceptance Criteria

* testing standards frozen

---

# FINAL ACCEPTANCE CRITERIA FOR PHASE 0

Phase 0 is DONE only when:

```txt id="p0z"
✓ Runtime philosophy frozen
✓ Metadata architecture frozen
✓ Frontend architecture frozen
✓ Backend architecture frozen
✓ API standards frozen
✓ Naming conventions frozen
✓ Relation strategy frozen
✓ Workflow strategy frozen
✓ Permission strategy frozen
✓ Expression strategy frozen
✓ ERP module roadmap frozen
✓ Plugin strategy frozen
✓ Multi-tenant strategy frozen
✓ Audit strategy frozen
✓ Testing strategy frozen
```

---

# OUTPUT OF PHASE 0

After Phase 0:

```txt id="p0aa"
Stable ERP Runtime Architecture Blueprint
```
