You are implementing Phase P3 of the ERP Identity Platform.

P1 and P2 are complete.

Authentication already works.

JWT already works.

Now implement Runtime Context.

--------------------------------------------------
OBJECTIVE
--------------------------------------------------

A user may belong to

Multiple Tenants

Multiple Organizations

Multiple Companies

Multiple Branches

Multiple Roles

The ERP must know the active working context.

--------------------------------------------------
CONCEPT
--------------------------------------------------

Authentication

↓

Runtime Context

↓

ERP Workspace

The Runtime Context determines

Data Visibility

Menus

Permissions

Metadata

Workflows

Everything.

--------------------------------------------------
IMPLEMENT
--------------------------------------------------

RuntimeContext class

RuntimeContextResolver

RuntimeContextService

Context Cache

CurrentContext API

SwitchContext API

--------------------------------------------------
RUNTIME CONTEXT
--------------------------------------------------

Contains

User

Tenant

Organization

Company

Branch

Role

Language

Timezone

Currency

Date Format

Number Format

--------------------------------------------------
LOGIN FLOW
--------------------------------------------------

After successful authentication

Load all available

Tenants

Organizations

Companies

Branches

Roles

If only one valid context exists

Automatically create RuntimeContext.

If multiple exist

Return available contexts.

Frontend will ask user to choose.

--------------------------------------------------
CONTEXT SWITCH
--------------------------------------------------

Support changing

Tenant

Organization

Company

Branch

Role

Without logging out.

Create

POST /api/context/switch

GET /api/context/current

GET /api/context/options

--------------------------------------------------
SECURITY
--------------------------------------------------

Validate

User belongs to selected tenant

User belongs to selected organization

User belongs to selected company

User has selected role

Reject invalid context selection.

--------------------------------------------------
REQUEST PROCESSING
--------------------------------------------------

Every authenticated request

Resolve RuntimeContext

Attach RuntimeContext

Business modules must never manually query

Current Tenant

Current Company

Current Role

They should receive RuntimeContext directly.

--------------------------------------------------
SPRING
--------------------------------------------------

Implement

RuntimeContextHolder

RuntimeContextFilter

ThreadLocal Context

Automatic cleanup

--------------------------------------------------
DO NOT IMPLEMENT
--------------------------------------------------

Permission checks

Metadata filtering

Workflow filtering

Business rules

Only Runtime Context.

--------------------------------------------------
TEST CASES
--------------------------------------------------

Single Context Login

Multiple Context Login

Switch Organization

Switch Company

Switch Role

Unauthorized Context

Expired Context

Concurrent Requests

--------------------------------------------------
ACCEPTANCE
--------------------------------------------------

✔ Runtime Context created

✔ Context switching works

✔ Thread-safe

✔ Available on every request

✔ Business modules can inject RuntimeContext

✔ Enterprise architecture

Generate complete implementation.