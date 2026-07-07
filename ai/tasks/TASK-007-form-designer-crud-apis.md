---
id: TASK-007

title: Implement Form Designer CRUD APIs (Backend)

type: API

status: READY_FOR_TEST
status: PLANNED

priority: High

owner: developer

assigned_to: AI Developer Agent

assigned_branch: feature/TASK-007-v2

locked: true

created: 2026-07-07

updated: 2026-07-07

started: 2026-07-07

completed: 2026-07-07

estimated_hours: 10

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0

parent_task:

related_tasks:
  - TASK-002
  - TASK-008
  - TASK-009
  - TASK-010

depends_on:
  - TASK-002

blocks:
  - TASK-011
  - TASK-012
  - TASK-013

labels: [backend, api, rest, form-designer]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-007.md

test_report:

history:
  - created
  - implemented 2026-07-07 — Developer completed Form Designer CRUD APIs

---

# Goal

Implement REST API endpoints for the Form Designer so admins can create, read, update, delete, and clone form definitions with their fields.

---

# Description

Create `FormDesignerController`, `FormDesignerService`, `FormFieldService`, and `FormLayoutService`.

## Endpoints

### Form CRUD
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/metadata/forms` | List forms (filtered by scope/tenant) | System/ Tenant Admin |
| POST | `/api/metadata/forms` | Create form (global or tenant) | System/ Tenant Admin |
| GET | `/api/metadata/forms/{id}` | Get form with all config | System/ Tenant Admin |
| PUT | `/api/metadata/forms/{id}` | Update form header | System/ Tenant Admin |
| DELETE | `/api/metadata/forms/{id}` | Delete form + cascade fields/rules | System/ Tenant Admin |
| POST | `/api/metadata/forms/{id}/clone` | Clone form definition | System/ Tenant Admin |
| GET | `/api/metadata/forms/available-tables` | Get tables available for form creation | System/ Tenant Admin |

### Field CRUD
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/metadata/forms/{id}/fields` | Get all fields for a form |
| POST | `/api/metadata/forms/{id}/fields` | Add a field |
| PUT | `/api/metadata/forms/{id}/fields/{fieldId}` | Update field config |
| DELETE | `/api/metadata/forms/{id}/fields/{fieldId}` | Remove field |
| PUT | `/api/metadata/forms/{id}/fields/reorder` | Reorder fields |

### Layout CRUD
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/metadata/forms/{id}/layout` | Get layout sections |
| POST | `/api/metadata/forms/{id}/layout/sections` | Add section |
| PUT | `/api/metadata/forms/{id}/layout/sections/{sectionId}` | Update section |
| DELETE | `/api/metadata/forms/{id}/layout/sections/{sectionId}` | Remove section |
| PUT | `/api/metadata/forms/{id}/layout/sections/{sectionId}/fields` | Assign fields to section |

## Service Logic

### Create Form
1. Validate: form code unique (global or per-tenant), model exists
2. Save to `sys_metadata_views`
3. If scope='global' → tenant_id = null
4. If scope='tenant' → tenant_id = current tenant

### Clone Form
1. Load source form with all fields, rules, validations, layout, sub-forms
2. Create new form with new code/label
3. Deep-copy all field configs, rules, validations
4. Do NOT copy role assignments (tenant role entries)
5. Return new form ID

### Authorization
- System Admin: Can access all forms, create global forms, manage any tenant
- Tenant Admin: Can only access/modify forms scoped to their tenant (identified from JWT)

---

# Acceptance Criteria

- [ ] All endpoints work with valid payloads
- [ ] Create form stores header + fields + layout in normalized tables
- [ ] Clone form deep-copies all configuration except role assignments
- [ ] Tenant Admin cannot access or modify forms from another tenant
- [ ] System Admin can create global forms (tenant_id = null) and tenant forms
- [ ] All endpoints return standard `ApiResponse<T>` envelope
- [ ] Delete form cascades to delete fields, rules, validations, layout, sub-form entries

---

# Technical Notes

- Inject `FormFieldService`, `FormLayoutService`, `FormSubFormService` into `FormDesignerService`
- Use `@Transactional` for create/clone operations that touch multiple tables
- Fetch tenant context from JWT via `SecurityContextHolder` or existing utility

---

# Files Expected

- `backend/src/main/java/com/erp/core/metadata/controller/FormDesignerController.java`
- `backend/src/main/java/com/erp/core/metadata/service/FormDesignerService.java`
- `backend/src/main/java/com/erp/core/metadata/service/FormFieldService.java`
- `backend/src/main/java/com/erp/core/metadata/service/FormLayoutService.java`
- DTOs for form, field, layout create/update/response
