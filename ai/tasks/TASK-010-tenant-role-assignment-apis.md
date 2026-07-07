---
id: TASK-010

title: Implement Per-Tenant Role Assignment APIs (Backend)

type: API

status: PLANNING

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 4

actual_hours:

parent_prd: PRD-001

prd_version: 1.5.0

parent_task:

related_tasks:
  - TASK-007

depends_on:
  - TASK-007

blocks:
  - TASK-014

labels: [backend, api, security, roles]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Implement APIs for Tenant Admins to configure which of their roles can access global forms, and for System Admins to view cross-tenant role assignments.

---

# Description

## APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/metadata/forms/{formId}/tenant-roles` | Get role assignments for current tenant | Tenant Admin |
| PUT | `/api/metadata/forms/{formId}/tenant-roles` | Set role assignments for current tenant | Tenant Admin |
| GET | `/api/metadata/forms/{formId}/global-tenant-roles` | List all tenants' role assignments | System Admin |
| GET | `/api/metadata/forms/global` | List all global forms available to current tenant | Tenant Admin |

## Service Logic

### Get Tenant Roles
1. Extract tenant ID from JWT
2. Query `sys_form_tenant_role` WHERE form_id = ? AND tenant_id = ?
3. Return list of role IDs

### Set Tenant Roles
1. Accept a list of role IDs
2. Delete all existing entries for (form_id, tenant_id)
3. Insert new entries for each role ID
4. Return updated list
(Replace strategy — simple and avoids update conflicts)

### List Global Forms (for Tenant Admin)
1. Query `sys_metadata_views` WHERE scope = 'global' AND is_active = true
2. For each form, include whether the current tenant has configured role access

### Authorization
- Tenant Admin: Can only read/update roles for their own tenant (from JWT)
- System Admin: Can view all tenant role assignments

---

# Acceptance Criteria

- [ ] Tenant Admin can view current role assignments for their tenant on any form
- [ ] Tenant Admin can replace role assignments (delete-all + insert)
- [ ] Tenant Admin sees only their own tenant's assignments
- [ ] System Admin can view all tenants' role assignments across all forms
- [ ] Tenant role changes do not affect other tenants' assignments
- [ ] Global forms list endpoint shows which forms have configured access for the current tenant

---

# Technical Notes

- Use replace strategy for role updates (DELETE all existing + INSERT new) to avoid concurrency issues
- Role IDs come from the existing role management system
- The `global-tenant-roles` endpoint is read-only for System Admin visibility

---

# Files Expected

- `backend/src/main/java/com/erp/core/metadata/controller/FormTenantRoleController.java`
- `backend/src/main/java/com/erp/core/metadata/service/FormTenantRoleService.java`
- `backend/src/main/java/com/erp/core/metadata/dto/TenantRoleRequest.java`
- `backend/src/main/java/com/erp/core/metadata/dto/TenantRoleResponse.java`
