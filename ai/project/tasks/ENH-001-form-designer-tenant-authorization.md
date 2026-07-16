---
id: ENH-001

title: Add Tenant Authorization to Form Designer API

type: Enhancement

status: COMPLETED

priority: High

owner: Software Engineer

assigned_to: QA Engineer

assigned_branch: feature/ENH-001

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 4

actual_hours: 3

parent_prd: PRD-001

prd_version: 1.6.0

prd_branch: prd/PRD-001-dynamic-form-configuration

base_branch: prd/PRD-001-dynamic-form-configuration

merge_target: prd/PRD-001-dynamic-form-configuration

merge_strategy: merge

parent_task: TASK-007

related_tasks:
  - TASK-007

depends_on:
  - TASK-007

blocks: []

labels: [backend, security, authorization, enhancement]

review_required: true

test_required: true

automation_required: true

change_summary: ai/project/changes/CHANGE-ENH-001.md

test_report: ai/project/tests/TEST-ENH-001.md

history:
  - created
  - 2026-07-07 — Created because TASK-007 shipped without tenant-scoped authorization. Developer explicitly deferred this.
  - 2026-07-08 — Planning audit: demoted READY_FOR_DEV → PLANNED (dependency TASK-007 is READY_FOR_TEST, not COMPLETED; current workflow requires COMPLETED for activation)
  - 2026-07-08 — Re-evaluated: restored PLANNED → READY_FOR_DEV. WORKFLOW.md allows READY_FOR_TEST or COMPLETED. Dependency TASK-007 is READY_FOR_TEST.
  - 2026-07-08 — Implemented: @PreAuthorize on FormDesignerController, tenant filtering in FormDesignerService. Merged into prd/PRD-001-dynamic-form-configuration. Set READY_FOR_TEST.

---

# Goal

Add tenant-scoped authorization to the Form Designer API endpoints so that Tenant Admins can only access and modify forms belonging to their own tenant.

---

# Description

The Form Designer CRUD APIs (TASK-007) were implemented without tenant authorization. The `FormDesignerController` currently has no `@PreAuthorize` annotations or tenant-scoped filtering.

## Required Changes

### 1. Controller-Level Authorization

Add the following authorization checks to `FormDesignerController`:

- **System Admin:** Can access all forms, create global forms, and manage any tenant's forms
- **Tenant Admin:** Can only access/modify forms with `scope='tenant'` AND `tenant_id = currentUser.tenantId`
- **Tenant Admin:** Can read global forms (`scope='global'`) but NOT modify them

Use `@PreAuthorize` annotations where applicable, or inject a security service.

### 2. Service-Level Tenant Filtering

Update `FormDesignerService` methods:

- `listForms(scope, tenantId)` — When called by Tenant Admin, always filter by their `tenant_id`
- `getForm(id)` — Verify the form belongs to the current tenant (or is global for read access)
- `createForm(request)` — For Tenant Admin, enforce `scope='tenant'` and set `tenant_id` from JWT
- `updateForm(id, request)` — Verify ownership before update
- `deleteForm(id)` — Verify ownership before delete

### 3. JWT Context Extraction

Use `SecurityContextHolder` to extract the current user's roles and tenant ID. Reuse any existing security utilities in the project.

---

# Acceptance Criteria

- [ ] System Admin can list, create, update, and delete any form (global or tenant)
- [ ] Tenant Admin can list and read global forms
- [ ] Tenant Admin cannot modify or delete global forms
- [ ] Tenant Admin can only manage forms scoped to their own tenant
- [ ] Tenant Admin cannot access another tenant's forms even with direct URL manipulation
- [ ] Unauthorized access attempts return 403 (Forbidden)
- [ ] All authorization checks are enforced server-side
- [ ] Existing functionality continues to work

---

# Technical Notes

- Check existing `@PreAuthorize` patterns in the codebase (e.g., `TableDesignerController`)
- Reuse any existing `SecurityUtils` or `TenantContext` utilities
- The JWT token should contain `tenant_id` and `roles` claims
- For service-level checks, throw `AccessDeniedException` for violations
- Follow the existing error handling pattern (ApiResponse with errorCode)

---

# Files Expected

- Modified: `FormDesignerController.java`
- Modified: `FormDesignerService.java`
- Possibly modified: `FormFieldService.java`, `FormLayoutService.java`
- Possibly new: `security/FormAuthorizationService.java`
