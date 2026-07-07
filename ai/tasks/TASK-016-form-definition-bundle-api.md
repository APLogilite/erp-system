---
id: TASK-016

title: Implement Form Definition Bundle API (Backend)

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

estimated_hours: 8

actual_hours:

parent_prd: PRD-001

prd_version: 1.5.0

parent_task:

related_tasks:
  - TASK-007
  - TASK-015
  - TASK-017

depends_on:
  - TASK-007
  - TASK-015

blocks:
  - TASK-019
  - TASK-020

labels: [backend, api, runtime, definition]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Implement the form definition bundle endpoint that assembles form structure + model definition from normalized tables and returns it in a single JSON response (FR-014).

---

# Description

Create `RuntimeFormController` and `FormDefinitionAssemblyService`.

## Endpoint

`GET /api/runtime/forms/{formCode}/definition`

## Assembly Logic

The service assembles the response by querying all normalized tables:

1. **Form header:** `sys_metadata_views` WHERE name = formCode AND is_active = true
2. **Fields:** `sys_form_fields` WHERE form_id = ? ORDER BY position
3. **Rules:** For each field, `sys_form_field_rules` WHERE field_id = ?
4. **Validations:** For each field, `sys_form_field_validations` WHERE field_id = ?
5. **Layout sections:** `sys_form_layout_sections` WHERE form_id = ? ORDER BY position
6. **Section-field mapping:** For each section, `sys_form_section_fields` WHERE section_id = ?
7. **Sub-forms:** `sys_form_sub_forms` WHERE parent_form_id = ? ORDER BY position
   - For each sub-form, fetch the child form definition (recursive, but only one level)
8. **Model/table columns:** `sys_table_columns` WHERE table_id = ? AND is_active = true ORDER BY position
9. **Model header:** `sys_metadata_models` WHERE id/form matches the form's model

Assemble all into the response JSON format as specified in the PRD.

## Security
- Verify the user has access to this form (role-based check using `sys_form_tenant_role`)
- For tenant forms, verify the form belongs to the user's tenant

## Caching
- Set `Cache-Control: max-age=300` header (5 minutes)
- ETag support for conditional requests

---

# Acceptance Criteria

- [ ] `GET /api/runtime/forms/{formCode}/definition` returns complete form structure
- [ ] Response includes: form fields, rules, validations, layout, sub-forms, model columns
- [ ] Fields include their type information from the model definition
- [ ] Sub-form definitions are included (one level deep)
- [ ] Unauthorized access returns 403
- [ ] Non-existent form returns 404
- [ ] Response time < 500ms for typical forms (with caching)
- [ ] Response follows the standard `ApiResponse<T>` envelope

---

# Technical Notes

- Use the repositories from TASK-002 to query each table
- Assemble the JSON response manually using Maps/Lists (or use a dedicated response DTO)
- For performance, consider using batch queries (IN clauses) rather than N+1 queries
- Cache the assembled response at the service level (Caffeine cache)

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/controller/RuntimeFormController.java`
- `backend/src/main/java/com/erp/core/runtime/service/FormDefinitionAssemblyService.java`
- `backend/src/main/java/com/erp/core/runtime/dto/FormDefinitionBundleResponse.java`
- `backend/src/main/java/com/erp/core/runtime/dto/FieldDefinitionResponse.java`
- `backend/src/main/java/com/erp/core/runtime/dto/LayoutDefinitionResponse.java`
- `backend/src/main/java/com/erp/core/runtime/dto/SubFormDefinitionResponse.java`
