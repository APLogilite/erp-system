---
id: TASK-017

title: Implement Record Data APIs with Sub-Form & Breadcrumb Support (Backend)

type: API

status: PLANNING

priority: Critical

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 10

actual_hours:

parent_prd: PRD-001

prd_version: 1.5.0

parent_task:

related_tasks:
  - TASK-015
  - TASK-016
  - TASK-018

depends_on:
  - TASK-015
  - TASK-016

blocks:
  - TASK-019
  - TASK-020
  - TASK-023

labels: [backend, api, runtime, crud, sub-forms]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Implement the record data endpoints with tenant isolation, where clause enforcement, sub-form child record inclusion, and breadcrumb/parent context.

---

# Description

Add endpoints to `RuntimeFormController`.

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/runtime/forms` | List forms accessible to current user |
| GET | `/api/runtime/forms/{formCode}/records` | Paginated records list |
| GET | `/api/runtime/forms/{formCode}/records/{id}` | Single record + sub-form children + parent context |
| POST | `/api/runtime/forms/{formCode}/records` | Create record |
| PUT | `/api/runtime/forms/{formCode}/records/{id}` | Update record |
| DELETE | `/api/runtime/forms/{formCode}/records/{id}` | Soft-delete record |

## Service Logic

### List Forms (`GET /api/runtime/forms`)
1. Extract user's tenant ID and role IDs from JWT
2. Query `sys_metadata_views` WHERE:
   - (scope = 'global' AND form_id IN (SELECT form_id FROM sys_form_tenant_role WHERE tenant_id = ? AND role_id IN ?))
   - OR (scope = 'tenant' AND tenant_id = ? AND form_id IN (SELECT form_id FROM sys_form_tenant_role WHERE tenant_id = ? AND role_id IN ?))
3. Return form code + label + model info for each

### List Records (`GET /api/runtime/forms/{formCode}/records`)
1. Load form definition to get model name + where clause
2. Load user's roles from JWT
3. Load row filters from `sys_form_role_filters` WHERE form_id = ? AND role_id IN (user's roles)
4. Resolve dynamic variables in row filters (replace `{current_user_id}`, `{current_user_role}`, etc. with JWT values)
5. Call `DynamicCrudService.listRecords()` with tenant_id, where clause, and resolved rowFilters
6. Return paginated results

### Get Record (`GET /api/runtime/forms/{formCode}/records/{id}`)
1. Load form definition + model definition
2. Load user's roles from JWT, resolve row filters with dynamic variables
3. Call `DynamicCrudService.getRecord()` with tenant isolation AND row filters
4. If record is null (filtered out), return 404
5. Load sub-form child records: for each sub-form config, call `DynamicCrudService.getChildRecords()` with tenant isolation AND child form's row filters
6. Build parent context (form_code + record_id + label)
7. Build breadcrumb from sub-form chain (call `BreadcrumbService`)
8. Return record + parent + breadcrumb + sub_form_records

### Create Record (`POST /api/runtime/forms/{formCode}/records`)
1. Validate payload against field definitions (required, type, read-only)
2. Auto-set where clause field value if configured (e.g., `order_type = 'sales'`)
3. Auto-set `tenant_id` from JWT
4. Call `DynamicCrudService.createRecord()`
5. Return created record

### Update Record (`PUT /api/runtime/forms/{formCode}/records/{id}`)
1. Validate payload (strip read-only fields, validate types)
2. Call `DynamicCrudService.updateRecord()` with read-only field list
3. Return updated record

### Delete Record (`DELETE /api/runtime/forms/{formCode}/records/{id}`)
1. Verify record exists and belongs to tenant
2. Call `DynamicCrudService.deleteRecord()`
3. Audit log entry

---

# Acceptance Criteria

- [ ] `GET /api/runtime/forms` returns only forms the user has role access to
- [ ] `GET .../records` returns data filtered by where clause AND tenant_id
- [ ] `GET .../records/{id}` returns record + sub-form child records
- [ ] Creating a record auto-assigns tenant_id and where clause value
- [ ] Read-only fields are stripped from update payloads
- [ ] Backend re-validates required fields and data types
- [ ] Delete is soft-delete
- [ ] 403 returned for unauthorized form access
- [ ] 404 returned for non-existent records (or records from other tenants)
- [ ] All endpoints use the standard `ApiResponse<T>` envelope

---

# Technical Notes

- Validation: load form field configs, check required fields, validate types, enforce read-only
- Where clause enforcement: append to ALL list/get queries on the backend
- Read-only enforcement: strip fields marked as read_only from the form's field config from update payloads

---

# Files Expected

- Added in existing `RuntimeFormController.java` (from TASK-016)
- Added in existing `FormDefinitionAssemblyService.java`
- `backend/src/main/java/com/erp/core/runtime/service/RecordValidationService.java`
- `backend/src/main/java/com/erp/core/runtime/service/RecordCrudService.java`
- DTOs for record request/response
