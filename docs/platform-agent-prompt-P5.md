You are implementing Phase P5 of the ERP Identity Platform.

Current Status

✔ P1 Identity Domain Model

✔ P2 Authentication

✔ P3 Runtime Context

✔ P4 Authorization

Now implement the Identity Administration Platform.

--------------------------------------------------
OBJECTIVE
--------------------------------------------------

Build the complete administration module for Identity Management.

This is a platform module.

It must not depend on business modules.

--------------------------------------------------
MODULES
--------------------------------------------------

Create administration support for

Tenant

Organization

Company

Branch

Department

User

Role

Permission

User Preference

User Session

Runtime Context

--------------------------------------------------
BACKEND
--------------------------------------------------

Implement

REST Controllers

Services

DTOs

Validation

Search

Pagination

Audit Hooks

Soft Delete

--------------------------------------------------
FRONTEND
--------------------------------------------------

Create metadata-driven administration screens using the existing runtime engine.

Do NOT hardcode pages.

Use the metadata system wherever possible.

--------------------------------------------------
FEATURES
--------------------------------------------------

Tenant Management

Create

Update

Deactivate

Search

--------------------------------------------------

Organization Management

Hierarchy Tree

Create

Move

Deactivate

--------------------------------------------------

Company Management

CRUD

Organization Mapping

Default Currency

--------------------------------------------------

Branch Management

CRUD

Company Mapping

--------------------------------------------------

Department Management

Hierarchy

Manager Assignment

--------------------------------------------------

User Management

Create User

Edit User

Deactivate User

Reset Password

Unlock User

Assign Tenant

Assign Organization

Assign Company

Assign Branch

Assign Roles

Profile

Language

Timezone

Theme

--------------------------------------------------

Role Management

Create Roles

Assign Permissions

Clone Roles

Activate/Deactivate

--------------------------------------------------

Permission Management

Browse Permissions

Assign to Roles

Search

Category Filtering

--------------------------------------------------

Session Management

View Active Sessions

Force Logout

Revoke Refresh Tokens

Session History

--------------------------------------------------

User Preferences

Theme

Language

Timezone

Date Format

Number Format

Workspace Preferences (placeholder)

--------------------------------------------------

Context Management

View Available Contexts

Switch Context

Default Context

--------------------------------------------------
AUDIT
--------------------------------------------------

Track

Created By

Updated By

Password Reset

Role Changes

Permission Changes

Context Changes

Login History

--------------------------------------------------
SEARCH
--------------------------------------------------

Support global search across

Users

Roles

Organizations

Companies

Branches

Departments

--------------------------------------------------
VALIDATION
--------------------------------------------------

Prevent duplicate usernames

Prevent duplicate tenant codes

Prevent circular organization hierarchy

Prevent deleting referenced entities

--------------------------------------------------
TEST CASES
--------------------------------------------------

Create Tenant

Create Organization

Assign Company

Create User

Assign Roles

Switch Context

Reset Password

Deactivate User

Force Logout

Search Users

--------------------------------------------------
ACCEPTANCE
--------------------------------------------------

✔ Complete Identity Administration module

✔ Metadata-driven UI

✔ User lifecycle management

✔ Organization hierarchy management

✔ Role & Permission management

✔ Active session management

✔ Runtime context administration

✔ Enterprise-grade documentation

Generate complete backend and frontend implementation.