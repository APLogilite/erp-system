# AI Code Agent Prompt — B3 Relation Engine

You are a principal ERP architect and senior Spring Boot developer.

Your task is to build the **ERP Relation Engine**.

IMPORTANT:

This is NOT simple JPA relationships.

This is the runtime relation system that powers:

- lookups
- dropdowns
- autocomplete
- nested forms
- child grids
- tree structures
- batch loading
- metadata-driven relationships

The engine must work for ALL current and future ERP modules.

Architecture:

Metadata
      ↓
Relation Definition
      ↓
Relation Engine
      ↓
Runtime CRUD
      ↓
Frontend Runtime Renderer

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

Current Goal:

Build ERP Relation Engine.

---

# TARGET OUTCOME

After B3:

✓ many2one operational
✓ one2many operational
✓ many2many operational
✓ tree relations operational
✓ lookup APIs operational
✓ autocomplete operational
✓ nested save operational
✓ batch relation loading operational
✓ relation validation operational
✓ frontend relation support ready

---

# PACKAGE STRUCTURE

```txt
com.erp.core.relation

├── controller
├── service
├── repository
├── dto
├── resolver
├── validator
├── mapper
├── cache
├── query
└── exception
```

---

# B3.1 — Supported Relations

The engine MUST support:

## many2one

Examples:

```txt
sales_order.customer
product.category
warehouse.organization
```

---

## one2many

Examples:

```txt
sales_order.lines
warehouse.locations
```

---

## many2many

Examples:

```txt
product.tags
user.roles
```

---

## tree

Examples:

```txt
product_category.parent
organization.parent
account.parent
```

---

# B3.2 — Relation Metadata Contract

Use T4 RelationDefinition.

Required:

```java
relationType
targetModel
displayField
valueField
cascadeSave
loadingStrategy
```

Loading strategies:

```txt
LAZY
EAGER
PAGINATED
```

---

# B3.3 — Relation Service

Create:

```java
RelationService
RelationServiceImpl
```

Responsibilities:

```java
resolveRelation()

loadChildren()

loadParents()

saveRelations()

validateRelations()

batchLoad()
```

---

# B3.4 — Lookup API

Create:

```txt
GET /api/runtime/{model}/lookup
```

Query params:

```txt
search
page
size
```

Example:

```txt
/api/runtime/business_partner/lookup?search=john
```

Purpose:

Populate:

- dropdowns
- autocomplete fields
- relation selectors

---

# Response

```json
[
  {
    "id": "uuid",
    "value": "uuid",
    "label": "John Doe"
  }
]
```

---

# B3.5 — Autocomplete API

Create:

```txt
GET /api/runtime/{model}/autocomplete
```

Requirements:

- pagination
- server filtering
- configurable display field
- configurable search fields

---

# B3.6 — Nested Save Engine

Support:

```txt
sales_order
    ↓
sales_order_lines
```

Example:

```json
{
  "customer": "...",
  "lines": [
    {},
    {},
    {}
  ]
}
```

Requirements:

```txt
create children
update children
delete children
orphan removal
```

Must be transactional.

---

# B3.7 — Batch Relation Loading

Create:

```txt
POST /api/runtime/relations/batch
```

Purpose:

Prevent:

```txt
N+1 queries
```

Input:

```json
{
  "model": "business_partner",
  "ids": []
}
```

---

# B3.8 — Tree Support

Support:

```txt
parent
children
ancestors
descendants
```

APIs:

```txt
GET /tree
GET /children
GET /parents
```

Future:

```txt
drag-drop hierarchy
```

---

# B3.9 — Relation Validation

Validate:

```txt
invalid references
circular references
missing records
invalid parent
duplicate relations
```

Create:

```java
RelationValidationException
```

---

# B3.10 — Loading Strategy

Implement:

## LAZY

Default.

---

## EAGER

Small datasets only.

---

## PAGINATED

Large child collections.

Examples:

```txt
sales_order_lines
inventory_transactions
```

---

# B3.11 — Relation Cache

Create:

```java
RelationCache
```

Initially:

```java
ConcurrentHashMap
```

Future:

```txt
Caffeine
Redis
```

---

# B3.12 — Relation DTOs

Create:

```java
LookupDto
```

```java
id
value
label
```

---

Create:

```java
RelationRequestDto
```

---

Create:

```java
RelationResponseDto
```

---

Create:

```java
BatchRelationRequestDto
```

---

Create:

```java
TreeNodeDto
```

---

# B3.13 — Relation Events

Publish:

```txt
relation.created
relation.updated
relation.deleted
```

Future consumers:

```txt
workflow engine
audit engine
notifications
inventory engine
```

---

# B3.14 — Exception Handling

Create:

```java
RelationNotFoundException
RelationValidationException
CircularRelationException
```

Integrate with global exception handler.

---

# B3.15 — Acceptance Tests

## many2one lookup

Expected:

```txt
Dropdown populated.
```

---

## one2many nested save

Expected:

```txt
Parent and children saved.
```

---

## many2many

Expected:

```txt
Join relations persisted.
```

---

## tree structure

Expected:

```txt
Hierarchy returned correctly.
```

---

## batch load

Expected:

```txt
No N+1 issue.
```

---

## invalid relation

Expected:

```txt
RelationValidationException
```

---

# FRONTEND INTEGRATION CONTRACT

The following metadata field types must now work:

```txt
MANY_TO_ONE
ONE_TO_MANY
MANY_TO_MANY
TREE
```

This prepares:

- Relation Field Components
- Nested Forms
- Child Grids
- Tree Selectors

inside the Runtime Renderer.

---

# CODE QUALITY REQUIREMENTS

Use:

- Constructor injection
- Transaction boundaries
- SOLID principles
- Generic relation services
- Metadata-driven resolution
- Extension points

Avoid:

- Module-specific relation logic
- Hardcoded joins
- Reflection abuse
- Business-specific assumptions

---

# FINAL DELIVERABLE

Produce:

✓ Relation Service
✓ Lookup APIs
✓ Autocomplete APIs
✓ Nested Save Engine
✓ Batch Loading
✓ Tree Support
✓ Relation Validation
✓ Relation Cache
✓ Relation Events
✓ Exception Handling

Result:

```txt
Metadata
      ↓
Relation Engine
      ↓
Runtime CRUD
      ↓
ERP Relationships
```