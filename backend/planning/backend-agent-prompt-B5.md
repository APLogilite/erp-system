# AI Code Agent Prompt — B5 Permission & Security Engine

You are a principal ERP architect and senior Spring Boot developer.

Your task is to build the **Metadata-Driven Permission & Security Engine**.

IMPORTANT:

This is NOT simple Spring Security role checks.

The engine must support:

- module permissions
- menu permissions
- view permissions
- field permissions
- action permissions
- row-level security
- workflow permissions
- metadata-driven access control

Everything in the ERP must be secured through metadata and runtime evaluation.

Architecture:

User
    ↓
Roles
    ↓
Permissions
    ↓
Metadata Rules
    ↓
Runtime Enforcement
    ↓
Frontend Runtime

The engine must be generic and work for all current and future ERP modules.

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

Current Goal:

Build Permission & Security Engine.

---

# TARGET OUTCOME

After B5:

✓ Authentication integration operational
✓ Role system operational
✓ Permission engine operational
✓ Metadata permissions operational
✓ Row security operational
✓ Field security operational
✓ Action security operational
✓ Permission cache operational
✓ Runtime evaluation operational
✓ Frontend permission integration ready

---

# PACKAGE STRUCTURE

```txt
com.erp.core.security

├── auth
├── permission
├── role
├── evaluator
├── expression
├── cache
├── dto
├── event
├── repository
├── exception
├── mapper
└── service
```

---

# B5.1 — Security Philosophy

Permission resolution order:

```txt
User
 ↓
Roles
 ↓
Permissions
 ↓
Metadata Rules
 ↓
Runtime Enforcement
```

No hardcoded:

```txt
if (admin)
if (salesManager)
```

Everything must be metadata-driven.

---

# B5.2 — Permission Levels

Support:

```txt
MODULE
MENU
VIEW
FIELD
ACTION
ROW
WORKFLOW
```

---

# B5.3 — Core Entities

Create:

```java
Role
Permission
RolePermission
UserRole
```

Prepare for future:

```java
PermissionGroup
```

---

# B5.4 — Permission Definition

Use T4:

```java
PermissionDefinition
```

Fields:

```java
resource
permissionType
expression
```

---

# B5.5 — Permission Service

Create:

```java
PermissionService
PermissionServiceImpl
```

Responsibilities:

```java
hasPermission()

getPermissions()

evaluate()

getFieldPermissions()

getActionPermissions()

getRowPermissions()
```

---

# B5.6 — Permission Evaluator

Create:

```java
PermissionEvaluator
```

Responsibilities:

```java
evaluateUser()
evaluateRoles()
evaluateMetadata()
evaluateExpressions()
```

---

# B5.7 — Field Security

Support:

```txt
hidden
readonly
editable
required
```

Examples:

```txt
amount readonly
cost hidden
discount editable
```

---

# API

```txt
GET /api/security/model/{modelCode}/fields
```

---

# B5.8 — Action Security

Support:

```txt
Approve button
Delete button
Custom actions
```

API:

```txt
GET /api/security/model/{modelCode}/actions
```

---

# B5.9 — View Security

Support:

```txt
form access
grid access
dashboard access
menu visibility
```

---

# B5.10 — Row-Level Security

Support:

Examples:

```txt
Own records only
Department records only
Organization hierarchy
```

Examples:

```json
{
  "==": [
    { "var": "createdBy" },
    { "var": "currentUser.id" }
  ]
}
```

Create:

```java
RowSecurityService
```

Responsibilities:

```java
buildQueryFilters()
evaluateRecord()
```

---

# B5.11 — Expression Engine

Engine:

```txt
JSON Logic
```

Support:

```txt
field permissions
row permissions
workflow permissions
action permissions
```

---

# B5.12 — Permission Cache

Create:

```java
PermissionCache
```

Initial:

```java
ConcurrentHashMap
```

Future:

```txt
Caffeine
Redis
```

---

# B5.13 — Runtime Security Context

Create:

```java
RuntimeSecurityContext
```

Contains:

```java
user
roles
permissions
tenant
organization
```

---

# B5.14 — Security APIs

Create:

```txt
GET /api/security/me
GET /api/security/permissions
GET /api/security/modules
GET /api/security/menus
GET /api/security/views
```

---

# B5.15 — Permission Events

Publish:

```txt
permission.granted
permission.revoked
role.assigned
role.removed
```

Future consumers:

```txt
audit
notifications
plugins
```

---

# B5.16 — Exceptions

Create:

```java
AccessDeniedException
PermissionNotFoundException
RoleNotFoundException
SecurityEvaluationException
```

Integrate with global exception handler.

---

# B5.17 — Frontend Integration Contract

Frontend Runtime must support:

```txt
hidden fields
readonly fields
hidden actions
disabled actions
menu visibility
view permissions
workflow permissions
row permissions
```

Permissions should arrive as metadata/runtime payload.

---

# B5.18 — Sample Permissions

Create:

## Sales User

```txt
View Sales Orders
Create Sales Orders
Cannot Approve
```

---

## Sales Manager

```txt
View Sales Orders
Approve Sales Orders
Close Sales Orders
```

---

## Admin

```txt
Full Access
```

---

# B5.19 — Acceptance Tests

## Module Permission

Expected:

```txt
Unauthorized module access denied.
```

---

## Field Permission

Expected:

```txt
Readonly field returned.
```

---

## Action Permission

Expected:

```txt
Approve button hidden.
```

---

## Row Security

Expected:

```txt
Only permitted records returned.
```

---

## Workflow Permission

Expected:

```txt
Transition denied.
```

---

## Permission Cache

Expected:

```txt
Permissions served from cache.
```

---

# CODE QUALITY REQUIREMENTS

Use:

- Spring Security integration
- Strategy pattern
- Metadata-driven evaluation
- Constructor injection
- SOLID principles
- Event-driven architecture

Avoid:

- Hardcoded role names
- Business-specific security logic
- Massive if/else permission checks
- Module-specific assumptions

---

# FINAL DELIVERABLE

Produce:

✓ Permission Engine
✓ Role System
✓ Metadata Security
✓ Row Security
✓ Field Security
✓ Action Security
✓ Permission Cache
✓ Runtime Security Context
✓ Security APIs
✓ Permission Events
✓ Frontend Integration Contract

Result:

```txt
User
  ↓
Roles
  ↓
Permissions
  ↓
Metadata Rules
  ↓
Runtime Enforcement
  ↓
Secure ERP Platform
```

This completes the core backend runtime engine.
