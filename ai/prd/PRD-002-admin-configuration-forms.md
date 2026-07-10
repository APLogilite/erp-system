---
id: PRD-002

title: Admin Configuration Forms — Metadata Table Management

version: 1.0.0

status: APPROVED

priority: High

owner: planner

created: 2026-07-10

updated: 2026-07-10

approved_by: user

project: Dynamic ERP Platform

repository: erp-system

prd_branch: prd/PRD-002-admin-configuration-forms

target_branch: main

merge_strategy: merge

tech_stack:
  - Spring Boot 3.3.4
  - Java 17
  - PostgreSQL
  - Flyway (migrations only)
  - React 18
  - TypeScript (strict)
  - MUI 5

related_prds:
  - PRD-001 (Dynamic Form Configuration System v1.6.0) — runtime engine dependency

related_tasks:
  - TASK-033
  - TASK-034
  - TASK-035

related_bugs: []

dependencies:
  - PRD-001 must be TESTED/COMPLETED (metadata tables must exist)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial Draft: Admin forms for metadata tables

---

# Executive Summary

PRD-001 built the dynamic form engine, but the **configuration itself** is managed through hand-coded admin UIs (Table Designer, Form Designer). This PRD takes the "configure, don't code" philosophy one step further: it creates **dynamic forms for the metadata tables themselves**.

This means the same runtime form renderer that powers business forms (Orders, Invoices, etc.) can also manage its own configuration — tables, columns, form definitions, fields, layouts, rules, validations, sub-forms, and role access.

**Zero new UI code. Zero new API endpoints.** This PRD registers the system metadata tables in `sys_metadata_models` (as `static` type tables) and creates CRUD form definitions for them.

---

# Problem Statement

PRD-001's admin functionality requires navigating to hand-coded admin screens (Table Designer, Form Designer) which are entirely separate from the runtime form experience. Administrators must learn two different interfaces.

Additionally, certain metadata entities (field rules, validations, row filters) can only be managed through the Form Designer's complex tabbed interface — there's no simple flat CRUD form for them.

---

# Business Goals

1. Register all PRD-001 metadata tables as `static` tables in `sys_metadata_models`
2. Create simple CRUD forms for each metadata entity
3. Allow administrators to manage configuration through the same form interface used for business data
4. Provide an alternative management path alongside the existing Form Designer admin UI
5. Demonstrate the dynamic form engine managing its own configuration ("eat your own dog food")

---

# Functional Requirements

## FR-001: Register Metadata Tables

**Description:** Insert rows into `sys_metadata_models` and `sys_table_columns` to register the existing system tables so the runtime engine can discover and render forms for them. All tables are registered as `table_type = 'static'`.

**Priority:** High

**Tables to register:**

| Table Name | Label | Plural Label |
|-----------|-------|-------------|
| sys_metadata_models | Table Definition | Table Definitions |
| sys_table_columns | Table Column | Table Columns |
| sys_metadata_views | Form Definition | Form Definitions |
| sys_form_fields | Form Field | Form Fields |
| sys_form_field_rules | Field Rule | Field Rules |
| sys_form_field_validations | Field Validation | Field Validations |
| sys_form_layout_sections | Layout Section | Layout Sections |
| sys_form_section_fields | Section Field | Section Fields |
| sys_form_sub_forms | Sub-Form Config | Sub-Form Configs |
| sys_form_tenant_role | Tenant Role Access | Tenant Role Access |
| sys_form_role_filters | Row Filter | Row Filters |

**Acceptance Criteria:**
- All 11 tables registered in `sys_metadata_models` with `table_type = 'static'`
- All columns registered in `sys_table_columns` (match existing PostgreSQL columns)
- No DDL executed (tables already exist — static registration only)
- Column metadata correctly maps types: UUID → string, VARCHAR → string, TEXT → text, BOOLEAN → boolean, TIMESTAMP → datetime, JSONB → text

---

## FR-002: Table Definition Form

**Description:** CRUD form for `sys_metadata_models` — view, create, edit, delete table definitions.

**Priority:** High

**Form Code:** `admin_table_definition`

**Fields:** name (code), label, plural_label, table_type, table_name, description, is_active

**Layout:** 2-column, single section "Table Information"

---

## FR-003: Table Column Form

**Description:** CRUD form for `sys_table_columns` — manage columns of a table definition. Linked as a sub-form of the Table Definition form.

**Priority:** High

**Form Code:** `admin_table_column`

**Fields:** table_id (hidden/parent), code, label, type, required, default_value, max_length, precision, scale, relation_table, enum_options, position, is_active

**Layout:** 2-column, single section "Column Details"

**Sub-Form Config:** `admin_table_definition` → relation_code `table_id` → child form `admin_table_column`, tab label "Columns"

---

## FR-004: Form Definition Form

**Description:** CRUD form for `sys_metadata_views` — view, create, edit, delete form definitions (window tabs).

**Priority:** High

**Form Code:** `admin_form_definition`

**Fields:** name (code), model_name, type, scope, tenant_id, description, where_clause_field, where_clause_operator, where_clause_value, is_active

**Layout:** 2-column, section "Form Information"

---

## FR-005: Form Fields, Rules, Validations (Sub-Forms)

**Description:** Create flat CRUD forms for form field configurations, field rules, and field validations. These link as sub-forms under the Form Definition form.

**Priority:** Medium

**Forms:**

| Form Code | Table | Sub-Form Of |
|-----------|-------|------------|
| `admin_form_field` | sys_form_fields | admin_form_definition (via form_id) |
| `admin_field_rule` | sys_form_field_rules | admin_form_definition (via field → form) |
| `admin_field_validation` | sys_form_field_validations | admin_form_definition (via field → form) |

---

## FR-006: Layout Section Form

**Description:** CRUD form for layout sections and section-field mappings.

**Priority:** Medium

**Form Code:** `admin_layout_section`

**Fields:** form_id, code, label, collapsible, columns, position

**Sub-Form:** Section Fields (sys_form_section_fields) as child tab

---

## FR-007: Sub-Form Config Form

**Description:** CRUD form for sub-form configurations.

**Priority:** Medium

**Form Code:** `admin_sub_form_config`

**Fields:** parent_form_id, relation_code, child_form_code, label, display_as, position

---

## FR-008: Role Access Forms

**Description:** CRUD forms for tenant role assignments and row-level filters.

**Priority:** Medium

**Forms:**

| Form Code | Table |
|-----------|-------|
| `admin_tenant_role_access` | sys_form_tenant_role |
| `admin_row_filter` | sys_form_role_filters |

---

## FR-009: Navigation Grouping

**Description:** Group admin forms in the navigation menu under an "Administration" category.

**Priority:** Low

**Acceptance Criteria:**
- All admin forms available under an "Administration" menu section
- Distinct from the existing hand-coded admin UI (Table Designer, Form Designer)
- Accessible via Ctrl+K search (e.g., search "Table Definition")

---

# Scope

## Included

- Register 11 metadata tables in sys_metadata_models / sys_table_columns
- Create ~14 CRUD forms for metadata entities
- Sub-form links for parent-child metadata relationships
- All via Flyway migrations — no new code

## Excluded

- Replacing PRD-001's hand-coded admin UI
- Replicating the Form Designer's preview/drag-drop experience
- Custom admin-only navigation (uses same runtime navigation)
- Any business logic beyond basic CRUD
- Registering system tables that aren't part of the form engine (users, roles, tenants)

---

# User Stories

- As a System Admin, I want to view and edit table definitions through a simple form, so I can quickly fix typos without opening the Table Designer.
- As a Tenant Admin, I want to browse form definitions and field configurations in a table view, so I can audit what forms are configured.
- As a Developer, I want to use the runtime form engine to manage its own metadata, validating that the system is self-consistent.

---

# Open Questions

All questions resolved during planning:

1. **Static table registration:** Flyway migration (same approach as PRD-003). INSERT into sys_metadata_models + sys_table_columns. No DDL — tables already exist.

2. **Column visibility:** System columns hidden (id, created_at, created_by, updated_at, deleted_at). `tenant_id` shown as read-only on forms where relevant. Only user-meaningful columns displayed.

3. **Admin UI overlap:** Coexist with PRD-001's Table Designer / Form Designer. Admin forms placed under "Administration" navigation section to avoid confusion.

4. **Column count:** Only essential columns shown per form. Infrequently-used columns hidden (can be added later as needed).

### Column Visibility per Form

**admin_table_definition** (sys_metadata_models):
Visible: name, label, plural_label, table_type, table_name, description, is_active, tenant_id (read-only)
Hidden: id, created_at, updated_at, created_by, updated_by, deleted_at

**admin_table_column** (sys_table_columns):
Visible: code, label, type, required, default_value, max_length, precision, scale, relation_table, enum_options, position, is_active, tenant_id (read-only)
Hidden: table_id (parent sub-form context), id, created_at, updated_at, created_by, updated_by, deleted_at

**admin_form_definition** (sys_metadata_views):
Visible: name, model_name, type, scope, description, where_clause_field, where_clause_operator, where_clause_value, is_active, tenant_id (read-only)
Hidden: id, created_at, updated_at, created_by, updated_by, deleted_at

**admin_form_field** (sys_form_fields):
Visible: column_code, label_override, visible, read_only, required, position, default_value, placeholder, is_active, tenant_id (read-only)
Hidden: form_id (sub-form context), id, created_at, updated_at, created_by, updated_by, deleted_at

**admin_field_rule** (sys_form_field_rules):
Visible: condition_field, condition_operator, condition_value, action, logic_group, position, tenant_id (read-only)
Hidden: field_id, id, created_at, updated_at, created_by, updated_by, deleted_at

**admin_field_validation** (sys_form_field_validations):
Visible: type, value, message, position, tenant_id (read-only)
Hidden: field_id, id, created_at, updated_at, created_by, updated_by, deleted_at

**admin_layout_section** (sys_form_layout_sections):
Visible: code, label, collapsible, columns, position, tenant_id (read-only)
Hidden: form_id (sub-form context), id, created_at, updated_at, created_by, updated_by, deleted_at

**admin_section_field** (sys_form_section_fields):
Visible: position, tenant_id (read-only)
Hidden: section_id, field_id (sub-form context), id, created_at, updated_at

**admin_sub_form_config** (sys_form_sub_forms):
Visible: relation_code, child_form_code, label, display_as, position, tenant_id (read-only)
Hidden: parent_form_id (sub-form context), id, created_at, updated_at

**admin_tenant_role_access** (sys_form_tenant_role):
Visible: tenant_id, role_id, tenant_id (parent context)
Hidden: form_id (sub-form context), id, created_at, updated_at

**admin_row_filter** (sys_form_role_filters):
Visible: condition_field, condition_operator, condition_value, position, tenant_id (read-only)
Hidden: form_id, role_id (sub-form context), id, created_at, updated_at, created_by, updated_by, deleted_at

### Navigation

All admin forms grouped under **"Administration"** section in the runtime navigation menu, separate from business forms.

---

# Change History

| Version | Reason | Date |
|---------|--------|------|
| 1.0.0 | Initial Draft → Approved. Column visibility rules, navigation grouping, Flyway approach confirmed. | 2026-07-10 |

---

# Related Documents

- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
- [PRD-003 — ERP Order Flow Transaction Forms](../prd/PRD-003-erp-order-flow-forms.md)
