# AI Code Agent Prompt — B2 Runtime CRUD Engine

You are a principal ERP platform architect and senior Spring Boot developer.

Your task is to build the **Runtime CRUD Engine**.

IMPORTANT:

This is NOT module-specific CRUD.

Do NOT create:

```txt
CustomerController
ProductController
SalesOrderController
```

The entire ERP must operate through a generic runtime engine.

Architecture:

Frontend Runtime
        ↓
Runtime Metadata
        ↓
Generic CRUD API
        ↓
Runtime Service
        ↓
Dynamic Persistence Layer
        ↓
PostgreSQL

The engine must support all future ERP modules through metadata.

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

Current Goal:

Build Runtime CRUD Engine.

---

# TARGET OUTCOME

After B2:

✓ Generic CRUD API operational
✓ Dynamic query engine operational
✓ Metadata-driven validation operational
✓ Dynamic persistence operational
✓ Pagination operational
✓ Filtering operational
✓ Sorting operational
✓ Soft delete operational
✓ Audit integration ready
✓ Multi-tenant hooks ready

---

# PACKAGE STRUCTURE

```txt
com.erp.core.runtime

├── controller
├── service
├── repository
├── query
├── dto
├── mapper
├── validator
├── exception
├── event
└── util
```

---

# B2.1 — Runtime Philosophy

All business modules use:

```txt
/api/runtime/{modelCode}
```

Examples:

```txt
/api/runtime/business_partner
/api/runtime/product
/api/runtime/sales_order
```

No module-specific CRUD controllers.

---

# B2.2 — Runtime Controller

Create:

```java
RuntimeController
```

Endpoints:

---

GET

```txt
/api/runtime/{modelCode}/{id}
```

---

POST

```txt
/api/runtime/{modelCode}
```

---

PUT

```txt
/api/runtime/{modelCode}/{id}
```

---

DELETE

```txt
/api/runtime/{modelCode}/{id}
```

---

POST

```txt
/api/runtime/{modelCode}/search
```

---

POST

```txt
/api/runtime/{modelCode}/batch
```

---

POST

```txt
/api/runtime/{modelCode}/validate
```

---

# B2.3 — Runtime Service

Create:

```java
RuntimeService
RuntimeServiceImpl
```

Methods:

```java
findById()
create()
update()
delete()
search()
validate()
batchSave()
```

---

# B2.4 — Runtime DTOs

Create:

```java
RuntimeRecordDto
```

Structure:

```java
String modelCode;

UUID id;

Map<String,Object> values;
```

---

Create:

```java
RuntimeSearchRequest
```

```java
filters
sort
page
size
```

---

Create:

```java
RuntimePageResponse
```

```java
items
page
size
total
```

---

# B2.5 — Dynamic Persistence Strategy

Do NOT hardcode entities.

The engine must use metadata.

Supported operations:

```txt
create
update
delete
find
search
```

Prepare architecture for:

```txt
JPA
QueryDSL
jOOQ (future)
```

without breaking contracts.

---

# B2.6 — Search Engine

Support:

```txt
equals
contains
startsWith
endsWith
greaterThan
lessThan
between
in
isNull
```

---

# Filtering Example

```json
{
  "field": "name",
  "operator": "contains",
  "value": "ABC"
}
```

---

# Sorting

Support:

```json
{
  "field": "name",
  "direction": "ASC"
}
```

---

# Pagination

Standard:

```json
{
  "items": [],
  "page": 1,
  "size": 20,
  "total": 100
}
```

---

# B2.7 — Validation Engine

Validation driven by:

```txt
FieldDefinition
```

Support:

```txt
required
minLength
maxLength
pattern
minValue
maxValue
```

Future:

```txt
expression validation
cross-field validation
```

---

# B2.8 — Soft Delete

Support:

```java
isActive
deletedAt
deletedBy
```

Default:

```txt
records are never physically deleted
```

---

# B2.9 — Audit Hooks

Prepare:

```java
createdBy
createdAt
updatedBy
updatedAt
```

Future:

```txt
field history
workflow history
```

---

# B2.10 — Multi-Tenant Hooks

Prepare:

```java
tenantId
```

Requirements:

- automatic filtering
- automatic save population

Do NOT fully implement tenancy.

Prepare extension points only.

---

# B2.11 — Runtime Events

Publish:

```txt
record.created
record.updated
record.deleted
```

Create:

```java
RuntimeEvent
RuntimeEventPublisher
```

Future:

```txt
workflow integration
notification integration
inventory integration
```

---

# B2.12 — Exception Handling

Create:

```java
ModelNotFoundException
RecordNotFoundException
RuntimeValidationException
InvalidSearchRequestException
```

Integrate with global exception handler.

---

# B2.13 — API Response Standard

Success:

```json
{
  "success": true,
  "data": {}
}
```

Error:

```json
{
  "success": false,
  "errorCode": "RUNTIME_VALIDATION_ERROR",
  "message": "Validation failed"
}
```

---

# B2.14 — Acceptance Tests

## Create Record

```http
POST /api/runtime/business_partner
```

Expected:

```txt
Record created successfully.
```

---

## Search Records

```http
POST /api/runtime/business_partner/search
```

Expected:

```txt
Paginated result returned.
```

---

## Validation Failure

Expected:

```txt
RuntimeValidationException
```

---

## Soft Delete

Expected:

```txt
Record marked inactive.
```

---

## Multi-Tenant Hook

Expected:

```txt
Tenant filter extension point exists.
```

---

# CODE QUALITY REQUIREMENTS

Use:

- Constructor injection
- SOLID
- Clean Architecture
- Generic services
- Metadata-driven logic
- Event-driven extension points

Avoid:

- Module-specific CRUD
- Hardcoded entities
- Business-specific services
- Reflection abuse
- Massive if/else chains

---

# FINAL DELIVERABLE

Produce:

✓ Runtime Controller
✓ Runtime Service
✓ Dynamic Search Engine
✓ Validation Engine
✓ Pagination
✓ Sorting
✓ Filtering
✓ Soft Delete
✓ Audit Hooks
✓ Tenant Hooks
✓ Runtime Events
✓ Exception Handling

Result:

```txt
Metadata
      ↓
Generic Runtime CRUD Engine
      ↓
Persistent ERP Data
```