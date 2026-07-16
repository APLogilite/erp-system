---
id: TASK-034

title: Seed Core Admin Forms — Tables, Columns, Forms, Fields (Flyway Migration)

type: Database

status: COMPLETED

priority: High

owner: QA Engineer

assigned_to: QA Engineer

assigned_branch: feature/TASK-034

locked: true

created: 2026-07-10

updated: 2026-07-10

started: 2026-07-10

completed: 2026-07-10

qa_started: 2026-07-10

estimated_hours: 3

actual_hours: 1

parent_prd: PRD-002

prd_version: 1.0.0

prd_branch: prd/PRD-002-admin-configuration-forms

base_branch: main

merge_target: prd/PRD-002-admin-configuration-forms

merge_strategy: merge

parent_task:

related_tasks: []

depends_on:
  - TASK-033

blocks:
  - TASK-035

labels:
  - database
  - flyway
  - seed
  - admin
  - forms

review_required: true

test_required: true

automation_required: false

change_summary: CHANGE-TASK-034

test_report: TEST-TASK-034
test_script: ai/project/scripts/verify-prd-002-data.sql

qa_completed: 2026-07-10

history:
  - 2026-07-10 — Product Manager — Created task from PRD-002 v1.0.0
  - 2026-07-10 — Software Engineer — Auto-activated from PLANNED (TASK-033 completed). Locked, created feature/TASK-034 branch, started implementation.
  - 2026-07-10 — Software Engineer — Created V16 Flyway migration (256 lines, 4 forms, 38 fields, 4 sections, 1 sub-form). Build passes.
  - 2026-07-10 — QA Engineer — Locked for testing. 15/15 structural tests passed. Identified tenant_id field visibility discrepancy between PRD-002 and task spec (Requirement Issue — Product Manager review needed).

---

# Goal

Create a Flyway migration that defines the 4 core admin forms: Table Definition, Table Column, Form Definition, and Form Field. These are the primary entities administrators need to manage. Includes sub-form link: Table Definition → Table Columns.

---

# Description

Create file `backend/src/main/resources/db/migration/V{next}__seed_core_admin_forms.sql`.

### Part 1 — Clean Existing

```sql
DELETE FROM sys_form_sub_forms WHERE parent_form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('admin_table_definition','admin_form_definition'));
DELETE FROM sys_form_section_fields WHERE section_id IN (SELECT id FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('admin_table_definition','admin_table_column','admin_form_definition','admin_form_field')));
DELETE FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('admin_table_definition','admin_table_column','admin_form_definition','admin_form_field'));
DELETE FROM sys_form_fields WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('admin_table_definition','admin_table_column','admin_form_definition','admin_form_field'));
DELETE FROM sys_metadata_views WHERE name IN ('admin_table_definition','admin_table_column','admin_form_definition','admin_form_field');
```

### Part 2 — Insert Form Definitions

| name | model_name | description |
|------|-----------|-------------|
| admin_table_definition | sys_metadata_models | Manage registered database table definitions |
| admin_table_column | sys_table_columns | Manage column definitions for tables |
| admin_form_definition | sys_metadata_views | Manage form/window definitions |
| admin_form_field | sys_form_fields | Manage field configurations on forms |

All forms: `type='form'`, `scope='global'`, `tenant_id=NULL`.

### Part 3 — Insert Form Fields

#### admin_table_definition (sys_metadata_models)

| Pos | column_code | label_override | required | read_only | placeholder |
|:---:|------------|---------------|:---:|:---:|------------|
| 1 | name | Code | ✓ | | e.g., tx_order |
| 2 | label | Label | ✓ | | e.g., Order |
| 3 | plural_label | Plural Label | | | e.g., Orders |
| 4 | table_type | Table Type | | | dynamic / static |
| 5 | table_name | Physical Table | | | |
| 6 | description | Description | | | |
| 7 | is_active | Active | | | |
| 8 | tenant_id | Tenant ID | | ✓ | Auto-managed |

#### admin_table_column (sys_table_columns)

| Pos | column_code | label_override | required | placeholder |
|:---:|------------|---------------|:---:|------------|
| 1 | code | Code | ✓ | e.g., order_number |
| 2 | label | Label | ✓ | e.g., Order Number |
| 3 | type | Type | ✓ | string/integer/decimal/etc. |
| 4 | required | Required | | |
| 5 | default_value | Default Value | | |
| 6 | max_length | Max Length | | For string type |
| 7 | precision | Precision | | For decimal type |
| 8 | scale | Scale | | For decimal type |
| 9 | relation_table | Relation Table | | For many2one |
| 10 | enum_options | Enum Options | | JSON array |
| 11 | position | Position | | |
| 12 | is_active | Active | | |

#### admin_form_definition (sys_metadata_views)

| Pos | column_code | label_override | required | placeholder |
|:---:|------------|---------------|:---:|------------|
| 1 | name | Code | ✓ | e.g., sales_order |
| 2 | model_name | Model | | Table code |
| 3 | type | Type | | form |
| 4 | scope | Scope | | global / tenant |
| 5 | description | Description | | |
| 6 | where_clause_field | WC Field | | |
| 7 | where_clause_operator | WC Operator | | |
| 8 | where_clause_value | WC Value | | |
| 9 | is_active | Active | | |

#### admin_form_field (sys_form_fields)

| Pos | column_code | label_override | required | placeholder |
|:---:|------------|---------------|:---:|------------|
| 1 | column_code | Column Code | ✓ | |
| 2 | label_override | Label Override | | |
| 3 | visible | Visible | | |
| 4 | read_only | Read Only | | |
| 5 | required | Required | | |
| 6 | position | Position | | |
| 7 | default_value | Default Value | | |
| 8 | placeholder | Placeholder | | |
| 9 | is_active | Active | | |

### Part 4 — Insert Layout Sections

All forms use a single 2-column section "Details":

| Form | Section Code | Section Label | Columns | Position |
|------|-------------|--------------|:---:|:---:|
| admin_table_definition | details | Table Information | 2 | 1 |
| admin_table_column | details | Column Details | 2 | 1 |
| admin_form_definition | details | Form Information | 2 | 1 |
| admin_form_field | details | Field Details | 2 | 1 |

### Part 5 — Insert Section-Field Mappings

All fields go into their form's "details" section, in position order matching the field position above.

### Part 6 — Sub-Form Config (Table → Columns)

```sql
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'table_id', 'admin_table_column', 'Columns', 'tab', 1, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_table_definition';
```

This links the Table Definition form to its Column sub-form via the `table_id` FK on sys_table_columns.

---

# Acceptance Criteria

- [x] Flyway migration file exists → V16 (256 lines)
- [x] Migration is idempotent (DELETE-before-INSERT + ON CONFLICT)
- [x] 4 form rows inserted into `sys_metadata_views`
- [x] 38 field rows inserted into `sys_form_fields`
- [x] 4 layout section rows inserted
- [x] All section-field mappings complete (4 joins, one per form)
- [x] 1 sub-form config: admin_table_definition → admin_table_column via table_id
- [x] `tenant_id` field on admin_table_definition marked `read_only=true`
- [x] System columns excluded from forms (id, created_at, updated_at, created_by, updated_by, deleted_at)
- [ ] After migration + restart, forms appear in `GET /api/runtime/forms` (requires PostgreSQL runtime)
- [ ] Opening admin_table_definition shows a "Columns" tab with the table's column records (requires PostgreSQL runtime)

---

# Technical Notes

### Flyway Version
Use `{next}` = TASK-033's version + 1.

### tenant_id — Read Only
The `tenant_id` field is included on admin_table_definition but marked `read_only=true` so administrators can see it but not modify it. PRD-001's backend strips read-only fields on save.

### No Sub-Form for Form Definition → Fields Yet
The admin_form_definition to admin_form_field sub-form link will be added in TASK-035, along with the remaining forms.

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_core_admin_forms.sql` (new)

---

# Task History

| Date | Agent | Action |
|------|-------|--------|
| 2026-07-10 | Product Manager | Created task from PRD-002 v1.0.0 |

---

# Related Documents

- [PRD-002 — Admin Configuration Forms](../prd/PRD-002-admin-configuration-forms.md)
- [TASK-033 — Register Metadata Tables Static](../tasks/TASK-033-register-metadata-tables-static.md)
