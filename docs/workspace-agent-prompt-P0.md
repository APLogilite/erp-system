````md id="p0prompt"
# AI Code Agent Prompt — PHASE 0 Architecture Freeze

You are a principal ERP platform architect.

Your task is to finalize and freeze the complete architecture foundation for a metadata-driven ERP runtime platform BEFORE large-scale implementation begins.

IMPORTANT:
This is NOT a coding-only task.

This is:
- architecture finalization
- runtime philosophy definition
- contract standardization
- metadata strategy design
- scalability planning
- extensibility planning

Your output will become the permanent blueprint for the ERP system.

This phase exists to PREVENT:
- massive rewrites
- incompatible frontend/backend contracts
- runtime instability
- metadata inconsistencies
- plugin architecture failures
- workflow redesigns later

---

# MOST IMPORTANT RULE

After this phase:

```txt
Core architecture decisions should NOT change frequently
````

because:

* frontend runtime depends on backend contracts
* metadata affects everything
* workflows affect permissions
* relations affect layouts
* plugins affect module architecture

You must think LONG TERM.

---

# SYSTEM GOAL

This platform is:

```txt
Metadata-driven ERP runtime platform
```

This platform is NOT:

```txt
Hardcoded CRUD application
```

The system should eventually support:

* runtime-generated forms
* runtime-generated grids
* dynamic workflows
* runtime permissions
* plugin modules
* dynamic layouts
* metadata rendering
* runtime actions
* expression-driven UI logic

---

# REQUIRED OUTPUT FORMAT

Generate:

* architecture decisions
* folder structures
* runtime flow diagrams (markdown/text)
* API conventions
* naming conventions
* metadata contracts
* module boundaries
* runtime philosophy
* implementation guidelines
* future extensibility rules

DO NOT generate shallow summaries.

Generate production-grade architecture documentation.

---

# P0.1 — DEFINE ERP RUNTIME PHILOSOPHY

You MUST finalize the core platform philosophy.

---

# BACKEND ROLE

Backend becomes:

```txt
ERP runtime execution engine
```

Responsibilities:

* metadata generation
* dynamic CRUD
* workflow execution
* permission enforcement
* runtime actions
* inventory logic
* relation resolution
* audit logging

Backend should NOT become:

* screen-specific CRUD backend
* hardcoded business UI backend

---

# FRONTEND ROLE

Frontend becomes:

```txt
ERP runtime renderer
```

Responsibilities:

* render metadata dynamically
* resolve runtime registries
* render forms/grids/layouts dynamically
* runtime workflow UI
* runtime actions
* runtime navigation
* conditional UI rendering

Frontend should NOT:

* hardcode ERP screens heavily
* duplicate backend logic
* contain business rules that belong to backend runtime

---

# DELIVERABLE

Create:

* runtime philosophy document
* backend/frontend responsibility matrix
* architectural principles
* anti-patterns to avoid

---

# P0.2 — FREEZE METADATA ARCHITECTURE

Finalize metadata-driven architecture.

---

# REQUIRED METADATA DOMAINS

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

# REQUIRED DECISIONS

Freeze:

## Metadata Format

```txt
JSON
```

## Validation Strategy

```txt
Zod + TypeScript
```

## Backend Storage

```txt
PostgreSQL JSONB + relational tables
```

## Runtime Flow

```txt
Backend Metadata
→ API
→ Frontend Registry
→ Runtime Renderer
```

---

# REQUIRED OUTPUT

Generate:

* metadata architecture blueprint
* metadata schema philosophy
* metadata ownership boundaries
* metadata lifecycle
* metadata loading strategy
* metadata caching strategy
* metadata invalidation strategy

---

# P0.3 — FREEZE FRONTEND ARCHITECTURE

Finalize frontend runtime architecture.

---

# REQUIRED STACK

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

# FRONTEND RUNTIME REQUIREMENTS

Frontend must support:

* dynamic rendering
* recursive layouts
* registry-based rendering
* metadata-driven forms
* metadata-driven grids
* runtime workflow rendering
* runtime action rendering
* dynamic menus
* dynamic routing

---

# REQUIRED REGISTRIES

Design:

* field registry
* layout registry
* action registry
* workflow registry
* component registry

---

# REQUIRED OUTPUT

Generate:

* frontend architecture blueprint
* runtime rendering flow
* registry architecture
* folder structure
* component boundaries
* dynamic rendering strategy
* frontend anti-patterns

---

# P0.4 — FREEZE BACKEND ARCHITECTURE

Finalize backend runtime engine architecture.

---

# REQUIRED STACK

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

# REQUIRED BACKEND PHILOSOPHY

Backend should:

* avoid hardcoded CRUD controllers
* avoid hardcoded workflows
* avoid hardcoded permissions
* avoid screen-specific APIs

Instead:

* metadata-driven runtime execution

---

# REQUIRED MODULE STRUCTURE

Recommended:

```txt
core/
modules/
plugins/
shared/
```

---

# REQUIRED OUTPUT

Generate:

* backend architecture blueprint
* runtime execution flow
* service boundaries
* module boundaries
* shared kernel strategy
* plugin loading strategy
* backend anti-patterns

---

# P0.5 — FREEZE API STANDARDS

Standardize all API contracts.

---

# REQUIRED API STRUCTURE

Example:

```txt
/api/runtime/*
/api/metadata/*
/api/auth/*
```

---

# REQUIRED RESPONSE FORMAT

## Success

```json
{
  "success": true,
  "data": {},
  "message": null
}
```

## Error

```json
{
  "success": false,
  "errorCode": "VALIDATION_ERROR",
  "message": "Invalid field"
}
```

## Pagination

```json
{
  "items": [],
  "page": 1,
  "size": 20,
  "total": 200
}
```

---

# REQUIRED OUTPUT

Generate:

* API conventions
* endpoint naming rules
* versioning strategy
* pagination standards
* filtering standards
* sorting standards
* error standards
* auth token strategy

---

# P0.6 — FREEZE NAMING CONVENTIONS

Standardize naming globally.

---

# REQUIRED CONVENTIONS

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

# REQUIRED MODULE EXAMPLES

```txt
sales_order
business_partner
inventory_transaction
```

---

# REQUIRED OUTPUT

Generate:

* naming convention guide
* package naming rules
* metadata code rules
* database naming rules
* frontend naming rules

---

# P0.7 — FREEZE RELATION STRATEGY

Define ERP relation architecture.

---

# REQUIRED RELATIONS

| Relation  | Example            |
| --------- | ------------------ |
| many2one  | order.customer     |
| one2many  | order.lines        |
| many2many | product.tags       |
| tree      | category hierarchy |

---

# REQUIRED LOADING STRATEGIES

| Strategy  | Use               |
| --------- | ----------------- |
| lazy      | default           |
| eager     | small datasets    |
| paginated | large child grids |

---

# REQUIRED API FEATURES

Support:

* lookup
* autocomplete
* nested save
* batch loading

---

# REQUIRED OUTPUT

Generate:

* relation architecture blueprint
* nested save strategy
* frontend relation rendering strategy
* lookup API strategy
* relation performance guidelines

---

# P0.8 — FREEZE WORKFLOW STRATEGY

Finalize workflow runtime architecture.

---

# REQUIRED STRUCTURE

```txt
states
transitions
guards
actions
permissions
```

---

# EXAMPLE FLOW

```txt
Draft
→ Completed
→ Approved
→ Closed
```

---

# REQUIRED BEHAVIOR

Transitions may:

* trigger actions
* trigger validations
* update inventory
* send notifications

---

# REQUIRED OUTPUT

Generate:

* workflow architecture
* transition execution flow
* workflow metadata structure
* runtime workflow enforcement strategy

---

# P0.9 — FREEZE PERMISSION STRATEGY

Define ERP security architecture.

---

# REQUIRED LEVELS

| Level  | Example          |
| ------ | ---------------- |
| module | sales access     |
| menu   | menu visibility  |
| view   | screen access    |
| field  | readonly amount  |
| action | approve button   |
| row    | own records only |

---

# REQUIRED RESOLUTION ORDER

```txt
User
→ Roles
→ Permissions
→ Metadata Rules
→ Runtime Enforcement
```

---

# REQUIRED OUTPUT

Generate:

* permission architecture
* permission evaluation flow
* frontend/backend enforcement responsibilities
* runtime security principles

---

# P0.10 — FREEZE EXPRESSION STRATEGY

Finalize runtime expression engine.

---

# REQUIRED ENGINE

```txt
JSON Logic
```

---

# REQUIRED USE CASES

```txt
- visibility
- readonly
- formulas
- validations
- workflow guards
- conditional layouts
```

---

# EXAMPLE

```json
{
  ">": [
    { "var": "amount" },
    1000
  ]
}
```

---

# REQUIRED OUTPUT

Generate:

* expression engine strategy
* evaluation lifecycle
* frontend/backend evaluation responsibilities
* security constraints
* supported operators

---

# P0.11 — FREEZE ERP MODULE ROADMAP

Define initial ERP module evolution.

---

# INITIAL MODULES

| Module                | Purpose             |
| --------------------- | ------------------- |
| Product               | inventory base      |
| Business Partner      | customer/vendor     |
| Warehouse             | inventory structure |
| Sales Order           | transactional flow  |
| Inventory Transaction | stock movement      |

---

# REQUIRED OUTPUT

Generate:

* module dependency map
* implementation order
* why these modules validate architecture
* runtime validation matrix

---

# P0.12 — FREEZE PLUGIN ARCHITECTURE

Prepare long-term extensibility.

---

# REQUIRED PLUGIN SUPPORT

```txt
- models
- fields
- views
- workflows
- menus
- actions
- permissions
```

---

# REQUIRED STRUCTURE

```txt
plugin.json
metadata/
backend/
frontend/
```

---

# REQUIRED OUTPUT

Generate:

* plugin lifecycle
* plugin loading flow
* extension point architecture
* plugin isolation strategy
* version compatibility strategy

---

# P0.13 — FREEZE MULTI-TENANT STRATEGY

Finalize tenant architecture.

---

# REQUIRED DECISION

| Strategy              | Decision     |
| --------------------- | ------------ |
| DB per tenant         | no           |
| Schema per tenant     | maybe future |
| Shared DB + tenant_id | YES          |

---

# REQUIRED OUTPUT

Generate:

* tenant isolation strategy
* tenant filtering strategy
* security implications
* indexing considerations

---

# P0.14 — FREEZE AUDIT STRATEGY

Define enterprise traceability.

---

# REQUIRED TRACKING

```txt
- createdBy
- updatedBy
- workflow history
- field changes
- actions executed
```

---

# REQUIRED OUTPUT

Generate:

* audit architecture
* history strategy
* field change strategy
* workflow history strategy

---

# P0.15 — FREEZE TESTING STRATEGY

Define testing philosophy.

---

# REQUIRED TOOLS

| Type                | Tool           |
| ------------------- | -------------- |
| Frontend Unit       | Vitest         |
| Frontend E2E        | Playwright     |
| Backend Unit        | JUnit          |
| Backend Integration | Testcontainers |
| API Testing         | REST Assured   |

---

# REQUIRED OUTPUT

Generate:

* testing pyramid
* test boundaries
* runtime testing philosophy
* metadata testing strategy
* workflow testing strategy

---

# FINAL DELIVERABLE

Generate a complete:

```txt
Stable ERP Runtime Architecture Blueprint
```

The blueprint must:

* be implementation-ready
* define long-term architecture boundaries
* support metadata-driven runtime rendering
* support plugin extensibility
* support future scalability
* support enterprise ERP complexity

---

# IMPORTANT CONSTRAINTS

DO NOT:

* overengineer microservices
* introduce premature distributed systems
* create unnecessary abstractions
* tightly couple frontend and backend
* design screen-specific architecture

DO:

* optimize for long-term ERP scalability
* optimize for runtime extensibility
* optimize for metadata-driven execution
* optimize for maintainability
* optimize for plugin architecture
* optimize for enterprise workflows

---

# FINAL VALIDATION

Ensure architecture supports:

* dynamic forms
* dynamic grids
* workflow runtime
* permissions runtime
* relation runtime
* metadata-driven rendering
* plugin extensibility
* multi-tenant future
* auditability
* scalable ERP growth

Your output should read like a real enterprise architecture blueprint created by a senior ERP platform architect.

```
```
