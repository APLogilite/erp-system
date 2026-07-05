You are implementing the Identity Platform for an enterprise metadata-driven ERP.

IMPORTANT

This is NOT just a login module.

This is the foundation of the entire ERP platform.

The implementation must be enterprise-grade and extensible.

Current Stack

Backend
- Spring Boot 3
- Java 21
- Spring Security
- JWT
- JPA/Hibernate
- PostgreSQL
- Flyway

Frontend
- React
- TypeScript
- Zustand
- React Query
- MUI

DO NOT IMPLEMENT LOGIN YET.

This task is ONLY to design and implement the domain model, architecture, and database schema.

--------------------------------------------------
OBJECTIVE
--------------------------------------------------

Design the complete Identity Platform architecture.

This platform must support:

- Authentication
- Authorization
- Multi Tenant
- Organization Hierarchy
- Company
- Branch
- Department
- Users
- Roles
- Permissions
- Runtime Context
- Future SSO
- Future LDAP
- Future OAuth

--------------------------------------------------
ARCHITECTURE
--------------------------------------------------

Create a new platform module:

platform/
    identity/

The module must be completely independent from business modules.

Business modules must depend on Identity.

Identity must depend on nothing except shared/core.

--------------------------------------------------
DESIGN PRINCIPLES
--------------------------------------------------

Authentication

Who are you?

Authorization

What can you do?

Context

Where are you currently working?

Keep these completely separated.

--------------------------------------------------
DESIGN THE COMPLETE DOMAIN MODEL
--------------------------------------------------

Create entities (no services yet):

Tenant

Organization

Company

Branch

Department

UserAccount

Role

Permission

UserRole

RolePermission

UserOrganization

UserCompany

UserSession

UserPreference

--------------------------------------------------
RELATIONSHIPS
--------------------------------------------------

Tenant
    -> many Organizations

Organization
    -> hierarchy (parent)

Organization
    -> many Companies

Company
    -> many Branches

Branch
    -> many Departments

Department
    -> hierarchy

User
    -> many Roles

Role
    -> many Permissions

User
    -> many Organizations

User
    -> many Companies

User
    -> many Sessions

User
    -> one Preference

--------------------------------------------------
RUNTIME CONTEXT
--------------------------------------------------

Design a RuntimeContext model.

It is NOT a database entity.

It represents the active ERP session.

It should contain:

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

This object will be attached to every authenticated request.

--------------------------------------------------
PERMISSION MODEL
--------------------------------------------------

Permissions must support:

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

Future plugins

Do not implement logic.

Only design the model.

--------------------------------------------------
DATABASE
--------------------------------------------------

Create Flyway migration scripts.

Create all tables.

Create foreign keys.

Create indexes.

Use UUID primary keys.

Support soft delete where appropriate.

Include:

created_at

updated_at

created_by

updated_by

is_active

--------------------------------------------------
DELIVERABLES
--------------------------------------------------

Generate:

1. Package structure

2. Entity relationship diagram (text)

3. Java entities

4. Repository interfaces

5. Flyway migrations

6. UML-style relationship documentation

7. Architecture documentation

8. RuntimeContext class

9. Sequence diagram for login/context flow

--------------------------------------------------
DO NOT IMPLEMENT

Login

JWT

Spring Security

REST Controllers

Services

Authentication

Business Logic

Only architecture and persistence model.

--------------------------------------------------
ACCEPTANCE CRITERIA

✔ Identity module created

✔ Domain model finalized

✔ Database schema finalized

✔ RuntimeContext designed

✔ Flyway executes successfully

✔ No compilation errors

✔ Architecture documentation generated

This is an architecture-first task.

Code quality should be enterprise-grade and suitable as the permanent foundation of the ERP platform.