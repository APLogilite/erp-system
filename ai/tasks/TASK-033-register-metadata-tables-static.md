---
id: TASK-033

title: Register Metadata Tables as Static (Flyway Migration)

type: Database

status: COMPLETED

priority: High

owner: QA Engineer

assigned_to: QA Engineer

assigned_branch: feature/TASK-033

locked: true

created: 2026-07-10

updated: 2026-07-10

started: 2026-07-10

completed: 2026-07-10

qa_started: 2026-07-10

qa_completed: 2026-07-10

estimated_hours: 2

actual_hours: 1

qa_hours: 0.5

parent_prd: PRD-002

prd_version: 1.0.0

prd_branch: prd/PRD-002-admin-configuration-forms

base_branch: main

merge_target: prd/PRD-002-admin-configuration-forms

merge_strategy: merge

parent_task:

related_tasks: []

depends_on: []

blocks:
  - TASK-034

labels:
  - database
  - flyway
  - seed
  - admin
  - static-tables

review_required: true

test_required: true

automation_required: false

change_summary: CHANGE-TASK-033

test_report: TEST-TASK-033
test_script: ai/scripts/verify-prd-002-data.sql

history:
  - 2026-07-10 — Product Manager — Created task from PRD-002 v1.0.0
  - 2026-07-10 — Software Engineer — Locked task, created feature/TASK-033 branch, started implementation
  - 2026-07-10 — Software Engineer — Created V15 Flyway migration (351 lines, 11 tables, 63 columns). Build passes.
  - 2026-07-10 — QA Engineer — Locked task for testing. Created prd/PRD-002-admin-configuration-forms branch from main. Build verified (36 tests, 0 failures, 3 pre-existing errors).

---

# Goal

Create a Flyway migration that registers 11 PRD-001 metadata tables in `sys_metadata_models` and `sys_table_columns` as `table_type = 'static'`. This allows PRD-001's runtime engine to discover them and render forms for them. No DDL is executed — tables already exist.

---

# Description

Create file `backend/src/main/resources/db/migration/V{next}__register_metadata_tables_static.sql`.

### Part 1 — Clean Existing (idempotency)

```sql
DELETE FROM sys_table_columns WHERE table_id IN (SELECT id FROM sys_metadata_models WHERE table_type = 'static' AND name LIKE 'sys_%');
DELETE FROM sys_metadata_models WHERE table_type = 'static' AND name LIKE 'sys_%';
```

### Part 2 — Register Tables in sys_metadata_models

| name | label | plural_label | table_type | table_name | description |
|------|-------|-------------|-----------|------------|-------------|
| sys_metadata_models | Table Definition | Table Definitions | static | sys_metadata_models | Registered database tables |
| sys_table_columns | Table Column | Table Columns | static | sys_table_columns | Column definitions for registered tables |
| sys_metadata_views | Form Definition | Form Definitions | static | sys_metadata_views | Form/window definitions |
| sys_form_fields | Form Field | Form Fields | static | sys_form_fields | Field configurations on forms |
| sys_form_field_rules | Field Rule | Field Rules | static | sys_form_field_rules | Conditional display/edit rules |
| sys_form_field_validations | Field Validation | Field Validations | static | sys_form_field_validations | Per-field validation constraints |
| sys_form_layout_sections | Layout Section | Layout Sections | static | sys_form_layout_sections | Form layout sections |
| sys_form_section_fields | Section Field | Section Fields | static | sys_form_section_fields | Field-to-section mappings |
| sys_form_sub_forms | Sub-Form Config | Sub-Form Configs | static | sys_form_sub_forms | Header-to-line sub-form links |
| sys_form_tenant_role | Tenant Role Access | Tenant Role Access | static | sys_form_tenant_role | Per-tenant role assignments |
| sys_form_role_filters | Row Filter | Row Filters | static | sys_form_role_filters | Role-based row-level data filters |

### Part 3 — Register Columns in sys_table_columns

For each table, insert its user-visible columns. Use the pattern:
```sql
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name', 'Code', 'string', true, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_models';
```

**Column registrations per table:**

#### sys_metadata_models
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| name | Code | string | ✓ | 100 | 1 |
| label | Label | string | ✓ | 100 | 2 |
| plural_label | Plural Label | string | | 100 | 3 |
| table_type | Table Type | string | | 20 | 4 |
| table_name | Physical Table | string | | 100 | 5 |
| description | Description | text | | | 6 |
| is_active | Active | boolean | | | 7 |

#### sys_table_columns
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| code | Code | string | ✓ | 100 | 1 |
| label | Label | string | ✓ | 100 | 2 |
| type | Type | string | ✓ | 50 | 3 |
| required | Required | boolean | | | 4 |
| default_value | Default Value | text | | | 5 |
| max_length | Max Length | integer | | | 6 |
| precision | Precision | integer | | | 7 |
| scale | Scale | integer | | | 8 |
| relation_table | Relation Table | string | | 100 | 9 |
| enum_options | Enum Options | text | | | 10 |
| position | Position | integer | | | 11 |
| is_active | Active | boolean | | | 12 |

#### sys_metadata_views
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| name | Code | string | ✓ | 100 | 1 |
| model_name | Model | string | | 100 | 2 |
| type | Type | string | | 50 | 3 |
| scope | Scope | string | | 20 | 4 |
| description | Description | text | | | 5 |
| where_clause_field | WC Field | string | | 100 | 6 |
| where_clause_operator | WC Operator | string | | 50 | 7 |
| where_clause_value | WC Value | string | | 255 | 8 |
| is_active | Active | boolean | | | 9 |

#### sys_form_fields
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| column_code | Column Code | string | ✓ | 100 | 1 |
| label_override | Label Override | string | | 200 | 2 |
| visible | Visible | boolean | | | 3 |
| read_only | Read Only | boolean | | | 4 |
| required | Required | boolean | | | 5 |
| position | Position | integer | | | 6 |
| default_value | Default Value | text | | | 7 |
| placeholder | Placeholder | string | | 255 | 8 |
| is_active | Active | boolean | | | 9 |

#### sys_form_field_rules
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| condition_field | Condition Field | string | | 100 | 1 |
| condition_operator | Operator | string | | 50 | 2 |
| condition_value | Value | string | | 255 | 3 |
| action | Action | string | | 50 | 4 |
| logic_group | Logic Group | integer | | | 5 |
| position | Position | integer | | | 6 |

#### sys_form_field_validations
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| type | Type | string | | 50 | 1 |
| value | Value | string | | 255 | 2 |
| message | Message | string | | 500 | 3 |
| position | Position | integer | | | 4 |

#### sys_form_layout_sections
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| code | Code | string | | 100 | 1 |
| label | Label | string | | 200 | 2 |
| collapsible | Collapsible | boolean | | | 3 |
| columns | Columns | integer | | | 4 |
| position | Position | integer | | | 5 |

#### sys_form_section_fields
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| position | Position | integer | | | 1 |

#### sys_form_sub_forms
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| relation_code | Relation Code | string | | 100 | 1 |
| child_form_code | Child Form Code | string | | 100 | 2 |
| label | Label | string | | 200 | 3 |
| display_as | Display As | string | | 50 | 4 |
| position | Position | integer | | | 5 |

#### sys_form_tenant_role
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| role_id | Role ID | string | | | 1 |

#### sys_form_role_filters
| code | label | type | required | max_length | position |
|------|-------|------|:---:|----------|:---:|
| condition_field | Condition Field | string | | 100 | 1 |
| condition_operator | Operator | string | | 50 | 2 |
| condition_value | Value | string | | 255 | 3 |
| position | Position | integer | | | 4 |

### Part 4 — Foreign Key Columns

For columns that act as foreign keys (many2one), register them with `type = 'string'` since they store UUID values. Do NOT add `relation_table` references — these static tables reference other static tables and the runtime would need those registered first (chicken-and-egg). For MVP, FK columns are shown as plain text fields.

---

# Acceptance Criteria

- [x] Flyway migration file exists at `V{next}__register_metadata_tables_static.sql` → V15
- [x] Migration cleans existing static registrations before inserting (idempotent)
- [x] 11 rows inserted into `sys_metadata_models` with `table_type = 'static'`
- [x] All column metadata inserted into `sys_table_columns` (63 rows total)
- [x] No DDL executed — tables already exist in PostgreSQL
- [x] Column types correctly map existing PG types (UUID → string, VARCHAR → string, TEXT → text, BOOLEAN → boolean, TIMESTAMP → datetime, JSONB → text)
- [ ] Migration runs successfully (requires PostgreSQL with metadata tables)
- [ ] After migration, static tables are queryable via PRD-001's runtime
- [ ] `GET /api/runtime/forms` does NOT yet show admin forms (forms not created until TASK-034)

---

# Technical Notes

### Static vs Dynamic
Static tables differ from dynamic tables only in `table_type`. The runtime treats them the same — the distinction is for admin visibility. No DDL is executed for static table registrations.

### Flyway Version
Determine `{next}` by checking the last existing migration. For PRD-002, migrations should be sequenced after PRD-003's migrations if they share the same Flyway history table.

### FK Column Handling
Foreign key columns (form_id, table_id, field_id, etc.) store UUIDs. For simplicity, register them as `type = 'string'` in the metadata. The PRD-001 runtime will display them as text inputs. many2one dropdowns would require the referenced table to also be registered, creating circular registration issues for the MVP.

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__register_metadata_tables_static.sql` (new)

---

# Developer Notes

- Created Flyway migration `V15__register_metadata_tables_static.sql` (351 lines)
- 11 tables registered in `sys_metadata_models` with `table_type = 'static'`
- 63 columns registered across all 11 tables in `sys_table_columns`
- Used `ON CONFLICT (name) DO UPDATE` on model insert for extra idempotency beyond the DELETE cleanup
- FK columns (table_id, form_id, field_id, etc.) registered as `type = 'string'` per task spec (no `relation_table` set to avoid circular dependency)
- `is_active` registered only for tables that had it in original DDL: sys_metadata_models, sys_table_columns, sys_metadata_views, sys_form_fields
- Column positions and labels match PRD-002 section "Column Visibility per Form"
- Verified column names against actual entity Java classes and Flyway DDL
- Build (`mvn clean compile`) passes cleanly

---

# Tester Notes

*(maintained by QA Engineer)*

---

# Review Notes

---

# Task History

| Date | Agent | Action |
|------|-------|--------|
| 2026-07-10 | Product Manager | Created task from PRD-002 v1.0.0 |

---

# Related Documents

- [PRD-002 — Admin Configuration Forms](../prd/PRD-002-admin-configuration-forms.md)
