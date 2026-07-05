You are implementing Phase P4 of the ERP Identity Platform.

Current Status

✔ P1 Identity Domain Model completed

✔ P2 Authentication completed

✔ P3 Runtime Context completed

Now implement the Authorization Engine.

--------------------------------------------------
OBJECTIVE
--------------------------------------------------

Implement a metadata-driven enterprise authorization engine.

Authentication already answers:

Who are you?

Runtime Context answers:

Where are you working?

Authorization must answer:

What are you allowed to do?

The authorization engine must be reusable by every module and plugin.

--------------------------------------------------
ARCHITECTURE
--------------------------------------------------

Create a new package

platform.identity.authorization

The authorization engine must not depend on any business module.

Business modules must depend on the authorization engine.

--------------------------------------------------
IMPLEMENT
--------------------------------------------------

AuthorizationService

PermissionResolver

PermissionEvaluator

RoleResolver

PermissionCache

PermissionExpression

AuthorizationException

AuthorizationInterceptor

--------------------------------------------------
PERMISSION MODEL
--------------------------------------------------

Support permissions for

Module

Menu

Window

Tab

Field

Action

Workflow

Process

Report

Dashboard

Attachment

Comment

API Endpoint

Future Plugin Resources

--------------------------------------------------
PERMISSION TYPES
--------------------------------------------------

Support

Read

Create

Update

Delete

Approve

Reject

Execute

Export

Import

Print

Assign

Manage

Admin

Custom Permissions

--------------------------------------------------
ROLE MODEL
--------------------------------------------------

Support

System Roles

Business Roles

Inherited Roles (future-ready)

Multiple Roles per User

Role Priority

Role Activation

Default Role

--------------------------------------------------
PERMISSION RESOLUTION
--------------------------------------------------

Authorization must evaluate permissions in this order

Runtime Context

↓

User

↓

Assigned Roles

↓

Role Permissions

↓

Metadata Rules

↓

Future Plugin Permissions

Return final effective permission set.

--------------------------------------------------
FIELD SECURITY
--------------------------------------------------

Support

Visible

Hidden

Read Only

Editable

Required Override

Future Conditional Rules

--------------------------------------------------
ROW LEVEL SECURITY
--------------------------------------------------

Design architecture for

Own Records

Organization Records

Company Records

Branch Records

Tenant Records

Custom Expression Rules

Only create architecture.

Do not implement business filtering yet.

--------------------------------------------------
SPRING SECURITY
--------------------------------------------------

Integrate with Spring Security.

Provide

@RequirePermission

PermissionEvaluator

Method Security

Future SpEL support

--------------------------------------------------
PERMISSION CACHE
--------------------------------------------------

Cache resolved permissions.

Invalidate automatically when

Roles change

Permissions change

Context changes

--------------------------------------------------
API
--------------------------------------------------

Create

GET /api/auth/permissions

Returns effective permissions for the current RuntimeContext.

--------------------------------------------------
DO NOT IMPLEMENT
--------------------------------------------------

Menu filtering

Metadata filtering

Workflow filtering

UI filtering

These belong to later phases.

--------------------------------------------------
TEST CASES
--------------------------------------------------

Multiple Roles

Permission Merge

Permission Denied

Permission Cache

Role Change

Context Change

Unauthorized Action

--------------------------------------------------
ACCEPTANCE
--------------------------------------------------

✔ Authorization Engine completed

✔ Effective permissions resolved

✔ Spring Security integrated

✔ Permission cache working

✔ Enterprise architecture

✔ Plugin-ready

Generate complete implementation with documentation.