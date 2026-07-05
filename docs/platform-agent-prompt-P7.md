You are implementing Phase P7 of the ERP Identity Platform.

P1–P6 are complete.

Now integrate Identity with the ERP Platform.

--------------------------------------------------
OBJECTIVE
--------------------------------------------------

Every platform module must consume Identity through a common SDK.

Business modules must never directly access authentication tables.

They must use platform services.

--------------------------------------------------
IMPLEMENT
--------------------------------------------------

IdentityFacade

CurrentUserProvider

CurrentContextProvider

PermissionProvider

SessionProvider

IdentityClient

AuthorizationHelper

ContextHelper

--------------------------------------------------
SPRING SUPPORT
--------------------------------------------------

Provide injectable services.

Example

CurrentUser

CurrentContext

PermissionEvaluator

No module should manually query User tables.

--------------------------------------------------
ANNOTATIONS
--------------------------------------------------

Create annotations

@CurrentUser

@CurrentContext

@RequirePermission

@EnableTenantFilter

--------------------------------------------------
PLATFORM INTEGRATION
--------------------------------------------------

Integrate with

Metadata Engine

Workflow Engine

Scheduler

Notification Engine

Audit Engine

Future Plugin Engine

--------------------------------------------------
MULTI TENANT
--------------------------------------------------

Support automatic tenant resolution.

Support automatic company filtering.

Support automatic organization filtering.

Business modules should not manually filter.

--------------------------------------------------
PLUGIN SUPPORT
--------------------------------------------------

Plugins should be able to

Register Permissions

Register Roles

Register Menus

Register Context Extensions

without modifying platform code.

--------------------------------------------------
DEVELOPER EXPERIENCE
--------------------------------------------------

Developers should be able to write

@Service

public class SalesService {

    private final CurrentContextProvider context;

}

instead of manually resolving

tenant

organization

company

role

--------------------------------------------------
DOCUMENTATION
--------------------------------------------------

Generate

Developer Guide

Architecture Guide

Extension Guide

Plugin Guide

Examples

--------------------------------------------------
TEST CASES
--------------------------------------------------

Inject CurrentContext

Inject CurrentUser

Permission Annotation

Tenant Isolation

Plugin Registration

Metadata Integration

Workflow Integration

--------------------------------------------------
ACCEPTANCE

✔ Platform SDK completed

✔ Identity fully integrated

✔ Developers use SDK

✔ Plugin-ready

✔ Future microservice-ready

✔ Enterprise architecture