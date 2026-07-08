---
id: TASK-001

title: Create Flyway Migrations for Normalized Metadata Storage

type: Database

status: READY_FOR_TEST

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started: 2026-07-07

completed: 2026-07-07

estimated_hours: 6

actual_hours: 0.5

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-002

depends_on: []

blocks:
  - TASK-002

labels: [database, migration, metadata]

review_required: true

test_required: false

automation_required: false

change_summary: ai/changes/CHANGE-TASK-001.md

test_report:

history:
  - created
  - implemented 2026-07-07 — Developer completed migration files V3–V13 with rollback scripts U3–U13
  - 2026-07-08 — Planning audit: prd_version corrected 1.5.0 → 1.6.0 (implementation scope already covered 1.6.0 requirements)

---

# Goal

Create the Flyway migration scripts that establish all normalized metadata tables for the Dynamic Form Configuration System.

---

# Description

Create Flyway SQL migration files in `backend/src/main/resources/db/migration/` that create the following tables. All tables should use UUID primary keys and include `created_at`/`updated_at` columns with appropriate defaults.

## Tables to Create

### 1. `sys_table_columns` — Normalized storage for table columns

| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| table_id | UUID NOT NULL FK → sys_metadata_models.id | |
| code | VARCHAR(100) NOT NULL | snake_case |
| label | VARCHAR(200) NOT NULL | |
| type | VARCHAR(50) NOT NULL | string, text, integer, decimal, boolean, date, datetime, many2one, enum |
| required | BOOLEAN DEFAULT false | |
| default_value | TEXT | |
| max_length | INTEGER | For string type |
| precision | INTEGER | For decimal type |
| scale | INTEGER | For decimal type |
| relation_table | VARCHAR(100) | For many2one |
| enum_options | JSONB | For enum type |
| position | INTEGER NOT NULL | |
| is_active | BOOLEAN DEFAULT true | |
| created_at | TIMESTAMP NOT NULL | |
| updated_at | TIMESTAMP NOT NULL | |
| created_by | UUID | |
| updated_by | UUID | |
| deleted_at | TIMESTAMP | |
| UNIQUE | (table_id, code) | |

### 2. Add columns to `sys_metadata_models`

- `table_type` VARCHAR(20) — `'dynamic'` or `'static'`
- `table_name` VARCHAR(100) — Physical PostgreSQL table name
- `description` TEXT

### 3. Add columns to `sys_metadata_views`

- `scope` VARCHAR(20) — `'global'` or `'tenant'`
- `tenant_id` UUID (nullable)
- `description` TEXT
- `where_clause_field` VARCHAR(100)
- `where_clause_operator` VARCHAR(50)
- `where_clause_value` VARCHAR(255)

### 4. `sys_form_fields` — Normalized form field configurations

| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| form_id | UUID NOT NULL FK → sys_metadata_views | |
| column_code | VARCHAR(100) NOT NULL | References sys_table_columns.code |
| label_override | VARCHAR(200) | Nullable — use column label if null |
| visible | BOOLEAN DEFAULT true | |
| read_only | BOOLEAN DEFAULT false | |
| required | BOOLEAN DEFAULT false | |
| position | INTEGER NOT NULL | |
| default_value | TEXT | |
| placeholder | VARCHAR(255) | |
| is_active | BOOLEAN DEFAULT true | |
| created_at, updated_at, created_by, updated_by, deleted_at | | BaseEntity fields |
| UNIQUE | (form_id, column_code) | |

### 5. `sys_form_field_rules` — Per-field condition/action rules

| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| field_id | UUID NOT NULL FK → sys_form_fields | |
| condition_field | VARCHAR(100) NOT NULL | Source field to check |
| condition_operator | VARCHAR(50) NOT NULL | equals, not_equals, greater_than, less_than, contains, is_empty, is_not_empty, in |
| condition_value | VARCHAR(255) | |
| action | VARCHAR(50) NOT NULL | show, hide, read_only, editable, required, optional |
| logic_group | INTEGER DEFAULT 0 | For AND/OR grouping |
| position | INTEGER | Order within group |
| + BaseEntity fields | | |

### 6. `sys_form_field_validations` — Per-field validation constraints

| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| field_id | UUID NOT NULL FK → sys_form_fields | |
| type | VARCHAR(50) NOT NULL | required, min_length, max_length, min, max, pattern, custom_expression |
| value | VARCHAR(255) | |
| message | VARCHAR(500) | Error message |
| position | INTEGER | |
| + BaseEntity fields | | |

### 7. `sys_form_layout_sections` — Layout sections

| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| form_id | UUID NOT NULL FK → sys_metadata_views | |
| code | VARCHAR(100) NOT NULL | |
| label | VARCHAR(200) NOT NULL | |
| collapsible | BOOLEAN DEFAULT false | |
| columns | INTEGER DEFAULT 1 | 1, 2, or 3 |
| position | INTEGER NOT NULL | |
| + BaseEntity fields | | |

### 8. `sys_form_section_fields` — Maps fields to sections

| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| section_id | UUID NOT NULL FK → sys_form_layout_sections | |
| field_id | UUID NOT NULL FK → sys_form_fields | |
| position | INTEGER | |
| UNIQUE | (section_id, field_id) | |
| UNIQUE | (field_id) | A field belongs to one section only |

### 9. `sys_form_role_filters` — Role-based row-level data filters

| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| form_id | UUID NOT NULL FK → sys_metadata_views | |
| role_id | UUID NOT NULL | Which role this filter applies to |
| condition_field | VARCHAR(100) NOT NULL | Field to filter on (supports `{current_user_id}`, `{current_user_role}`, etc.) |
| condition_operator | VARCHAR(50) NOT NULL | equals, not_equals, greater_than, less_than, contains, in |
| condition_value | VARCHAR(255) | Static value or dynamic variable |
| position | INTEGER | |
| + BaseEntity fields | | |
| INDEX | (form_id, role_id) | For efficient query lookup |

### 10. `sys_form_sub_forms` — Sub-form tab references

| Column | Type | Notes |
|--------|------|-------|
| id | UUID PK | |
| parent_form_id | UUID NOT NULL FK → sys_metadata_views | |
| relation_code | VARCHAR(100) NOT NULL | Column code on child table referencing parent |
| child_form_code | VARCHAR(100) NOT NULL | References sys_metadata_views.name |
| label | VARCHAR(200) NOT NULL | Tab label |
| display_as | VARCHAR(50) DEFAULT 'tab' | tab or inline_grid |
| position | INTEGER | |
| + BaseEntity fields | | |

### 10. `sys_form_tenant_role` — Per-tenant role assignments

```sql
CREATE TABLE sys_form_tenant_role (
    id UUID PK,
    form_id UUID NOT NULL FK → sys_metadata_views,
    tenant_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (form_id, tenant_id, role_id)
);
CREATE INDEX idx_ftr_form ON sys_form_tenant_role(form_id);
CREATE INDEX idx_ftr_tenant ON sys_form_tenant_role(tenant_id);
CREATE INDEX idx_ftr_role ON sys_form_tenant_role(role_id);
```

---

# Acceptance Criteria

- [x] All 10+ tables/table modifications are created in Flyway migration files (V3–V13)
- [ ] Migrations run successfully against a PostgreSQL database
- [x] All foreign key relationships are properly defined (ON DELETE CASCADE)
- [x] All unique constraints and indexes are created
- [x] Migrations are idempotent (IF NOT EXISTS / ADD COLUMN IF NOT EXISTS)
- [x] Rollback scripts are provided for each migration (U3–U13)

---

# Technical Notes

- Use sequential Flyway version numbers (e.g., V7__, V8__, etc.) — check existing migrations first
- All tables use UUID primary keys matching the existing BaseEntity pattern
- Foreign keys use `ON DELETE CASCADE` where appropriate (e.g., form fields cascade on form delete)
- Index all foreign key columns for query performance
- The `enum_options` JSONB column is acceptable since it's rarely changed metadata

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_sys_table_columns.sql`
- `backend/src/main/resources/db/migration/V{next+1}__alter_sys_metadata_models.sql`
- `backend/src/main/resources/db/migration/V{next+2}__alter_sys_metadata_views.sql`
- `backend/src/main/resources/db/migration/V{next+3}__create_sys_form_fields.sql`
- `backend/src/main/resources/db/migration/V{next+4}__create_sys_form_field_rules.sql`
- `backend/src/main/resources/db/migration/V{next+5}__create_sys_form_field_validations.sql`
- `backend/src/main/resources/db/migration/V{next+6}__create_sys_form_layout_sections.sql`
- `backend/src/main/resources/db/migration/V{next+7}__create_sys_form_section_fields.sql`
- `backend/src/main/resources/db/migration/V{next+8}__create_sys_form_sub_forms.sql`
- `backend/src/main/resources/db/migration/V{next+9}__create_sys_form_tenant_role.sql`
