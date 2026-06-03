# AI Code Agent Prompt — B1 Runtime Metadata API Foundation

You are a senior ERP platform architect and Spring Boot developer.

Your task is to implement:

# B1 — Runtime Metadata API Foundation

This is the FIRST backend runtime engine component.

The purpose is to provide metadata to the frontend runtime renderer.

IMPORTANT:

This is NOT business logic.

This is NOT CRUD.

This is the metadata delivery layer that powers:

- dynamic forms
- dynamic grids
- dynamic layouts
- dynamic workflows
- dynamic permissions
- dynamic actions
- plugin metadata

Future frontend flow:

Backend Metadata
→ Metadata API
→ React Query
→ Metadata Store
→ Registry Resolution
→ Runtime Renderer

---

# EXISTING PROJECT CONTEXT

Already available:

- Spring Boot
- Java 21
- PostgreSQL
- Flyway
- BaseEntity
- BaseService
- JWT Security
- Modular architecture

DO NOT modify existing architecture.

Follow project conventions.

---

# TARGET OUTCOME

After B1:

✓ Metadata API operational
✓ Metadata Registry operational
✓ Metadata DTOs defined
✓ Metadata Service operational
✓ Metadata Cache ready
✓ Metadata Validation ready
✓ Frontend can load metadata
✓ Foundation ready for T4

---

# PACKAGE STRUCTURE

Create:

```txt
com.erp.core.metadata

├── controller
├── service
├── registry
├── dto
├── model
├── validator
├── cache
└── mapper
```

DO NOT place inside business modules.

Metadata is platform-level infrastructure.

---

# B1.1 — Metadata Domain Model

Create metadata contracts.

---

# ModelMetadataDto

Represents ERP model definition.

Required fields:

```java
String code;
String name;
String description;

List<FieldMetadataDto> fields;

boolean auditable;
boolean workflowEnabled;
boolean active;
```

---

# FieldMetadataDto

Required fields:

```java
String code;
String name;
String type;

boolean required;
boolean readonly;
boolean searchable;
boolean filterable;

String defaultValue;

Map<String,Object> properties;
```

---

# ViewMetadataDto

Required fields:

```java
String code;
String modelCode;
String viewType;

String title;

LayoutMetadataDto layout;
```

Supported view types:

```txt
FORM
GRID
KANBAN
DETAIL
```

---

# LayoutMetadataDto

Required fields:

```java
String code;
String type;

Map<String,Object> config;

List<LayoutMetadataDto> children;
```

Must support recursive layouts.

---

# WorkflowMetadataDto

Required fields:

```java
String code;
String modelCode;

List<WorkflowStateDto> states;
List<WorkflowTransitionDto> transitions;
```

---

# ActionMetadataDto

Required fields:

```java
String code;
String name;
String actionType;
Map<String,Object> config;
```

---

# PermissionMetadataDto

Required fields:

```java
String code;
String resource;
String permissionType;
```

---

# B1.2 — Metadata Registry

Create:

```java
MetadataRegistry
```

Purpose:

Central runtime metadata access.

---

Required methods:

```java
registerModel()

registerView()

registerWorkflow()

registerAction()

findModel()

findView()

findWorkflow()

findAction()

refresh()
```

Requirements:

- thread-safe
- cache-friendly
- singleton Spring bean

Use ConcurrentHashMap.

---

# B1.3 — Metadata Service

Create:

```java
MetadataService
MetadataServiceImpl
```

Responsibilities:

- metadata lookup
- metadata aggregation
- metadata validation
- metadata cache integration

Required methods:

```java
getModel(String code)

getView(String code)

getWorkflow(String code)

getAction(String code)

getMetadataBundle(String modelCode)
```

---

# Metadata Bundle DTO

Create:

```java
MetadataBundleDto
```

Purpose:

Single payload for frontend.

Structure:

```java
ModelMetadataDto model;

List<ViewMetadataDto> views;

WorkflowMetadataDto workflow;

List<ActionMetadataDto> actions;

List<PermissionMetadataDto> permissions;
```

Frontend should eventually call:

```http
GET /api/metadata/bundle/{modelCode}
```

---

# B1.4 — Metadata Validation

Create:

```java
MetadataValidator
```

Responsibilities:

Validate:

- duplicate field codes
- duplicate model codes
- invalid references
- missing workflow states
- invalid layouts

Throw:

```java
MetadataValidationException
```

---

# B1.5 — Metadata Cache

Create:

```java
MetadataCache
```

Requirements:

Use:

```java
ConcurrentHashMap
```

Initially.

Design for future:

```txt
Caffeine
Redis
```

without breaking API.

Required methods:

```java
get()

put()

evict()

clear()
```

---

# B1.6 — Metadata REST API

Base path:

```txt
/api/metadata
```

Create controller:

```java
MetadataController
```

Endpoints:

---

GET

```txt
/api/metadata/models/{code}
```

returns:

```java
ModelMetadataDto
```

---

GET

```txt
/api/metadata/views/{code}
```

returns:

```java
ViewMetadataDto
```

---

GET

```txt
/api/metadata/workflows/{code}
```

returns:

```java
WorkflowMetadataDto
```

---

GET

```txt
/api/metadata/actions/{code}
```

returns:

```java
ActionMetadataDto
```

---

GET

```txt
/api/metadata/bundle/{modelCode}
```

returns:

```java
MetadataBundleDto
```

---

# API RESPONSE STANDARD

Use project standard.

Success:

```json
{
  "success": true,
  "data": {},
  "message": null
}
```

Error:

```json
{
  "success": false,
  "errorCode": "METADATA_NOT_FOUND",
  "message": "Metadata not found"
}
```

---

# B1.7 — Sample Bootstrap Metadata

Create bootstrap configuration.

Register:

```txt
business_partner
product
warehouse
sales_order
inventory_transaction
```

Only metadata.

NOT entities.

NOT business services.

Example:

```java
@PostConstruct
void initializeMetadata()
```

Register sample model metadata.

Purpose:

Allow frontend testing immediately.

---

# B1.8 — Exception Handling

Create:

```java
MetadataNotFoundException
MetadataValidationException
```

Integrate with global exception handler.

Error codes:

```txt
METADATA_NOT_FOUND
METADATA_VALIDATION_ERROR
```

---

# B1.9 — Future Plugin Readiness

Registry must support future:

```java
plugin.registerMetadata()
```

Do NOT implement plugins.

Only prepare extension points.

---

# ACCEPTANCE TESTS

## Test 1

```http
GET /api/metadata/models/business_partner
```

Expected:

```json
{
  "success": true,
  "data": {
    "code": "business_partner"
  }
}
```

---

## Test 2

```http
GET /api/metadata/bundle/business_partner
```

Expected:

Complete metadata bundle returned.

---

## Test 3

Unknown metadata.

Expected:

```json
{
  "success": false,
  "errorCode": "METADATA_NOT_FOUND"
}
```

---

## Test 4

Duplicate registration.

Expected:

```txt
MetadataValidationException
```

---

# CODE QUALITY REQUIREMENTS

Use:

- Constructor injection
- Lombok
- Records where appropriate
- Immutable DTOs where possible
- SOLID principles
- Clean Architecture principles

Avoid:

- hardcoded business logic
- module-specific dependencies
- static utility abuse
- tight coupling

---

# FINAL DELIVERABLE

Produce:

✓ Metadata DTOs
✓ Metadata Registry
✓ Metadata Cache
✓ Metadata Service
✓ Metadata Validation
✓ Metadata APIs
✓ Bootstrap Metadata
✓ Exception Handling
✓ Plugin Extension Preparation

Result:

Backend Metadata Delivery Engine operational.

This becomes the foundation for:

T4 — Metadata Schema Design
T5 — Registry System
T6 — Runtime Renderer