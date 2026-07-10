---
id: TASK-035

title: Seed Remaining Admin Forms — Rules, Validations, Layout, Role Access (Flyway Migration)

type: Database

status: PLANNED

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-10

updated: 2026-07-10

started:

completed:

estimated_hours: 3

actual_hours:

parent_prd: PRD-002

prd_version: 1.0.0

prd_branch: prd/PRD-002-admin-configuration-forms

base_branch: main

merge_target: prd/PRD-002-admin-configuration-forms

merge_strategy: merge

parent_task:

related_tasks: []

depends_on:
  - TASK-034

blocks: []

labels:
  - database
  - flyway
  - seed
  - admin
  - forms
  - sub-forms

review_required: true

test_required: true

automation_required: false

change_summary:

test_report:

history:
  - 2026-07-10 — Planner — Created task from PRD-002 v1.0.0

---

# Goal

Create the final Flyway migration for PRD-002 that defines the remaining 7 admin forms (Field Rules, Field Validations, Layout Sections, Section Fields, Sub-Form Configs, Tenant Role Access, Row Filters) and links Form Definition to its sub-forms (Fields, Rules, Validations).

---

# Description

Create file `backend/src/main/resources/db/migration/V{next}__seed_remaining_admin_forms.sql`.

### Part 1 — Clean Existing

```sql
DELETE FROM sys_form_sub_forms WHERE parent_form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('admin_form_definition','admin_layout_section'));
DELETE FROM sys_form_section_fields WHERE section_id IN (SELECT id FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('admin_field_rule','admin_field_validation','admin_layout_section','admin_section_field','admin_sub_form_config','admin_tenant_role_access','admin_row_filter')));
DELETE FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('admin_field_rule','admin_field_validation','admin_layout_section','admin_section_field','admin_sub_form_config','admin_tenant_role_access','admin_row_filter'));
DELETE FROM sys_form_fields WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('admin_field_rule','admin_field_validation','admin_layout_section','admin_section_field','admin_sub_form_config','admin_tenant_role_access','admin_row_filter'));
DELETE FROM sys_metadata_views WHERE name IN ('admin_field_rule','admin_field_validation','admin_layout_section','admin_section_field','admin_sub_form_config','admin_tenant_role_access','admin_row_filter');
```

### Part 2 — Insert Form Definitions

| name | model_name | description |
|------|-----------|-------------|
| admin_field_rule | sys_form_field_rules | Manage conditional field rules |
| admin_field_validation | sys_form_field_validations | Manage per-field validation constraints |
| admin_layout_section | sys_form_layout_sections | Manage form layout sections |
| admin_section_field | sys_form_section_fields | Manage field-to-section mappings |
| admin_sub_form_config | sys_form_sub_forms | Manage header-to-line sub-form links |
| admin_tenant_role_access | sys_form_tenant_role | Manage per-tenant role access assignments |
| admin_row_filter | sys_form_role_filters | Manage role-based row-level data filters |

All forms: `type='form'`, `scope='global'`, `tenant_id=NULL`.

### Part 3 — Insert Form Fields

#### admin_field_rule

| Pos | column_code | label_override | required | placeholder |
|:---:|------------|---------------|:---:|------------|
| 1 | condition_field | Condition Field | | e.g., customer_tier |
| 2 | condition_operator | Operator | | equals / not_equals / etc. |
| 3 | condition_value | Value | | e.g., Gold |
| 4 | action | Action | | show / hide / read_only / editable / required / optional |
| 5 | logic_group | Logic Group | | AND group number |
| 6 | position | Position | | |

#### admin_field_validation

| Pos | column_code | label_override | required | placeholder |
|:---:|------------|---------------|:---:|------------|
| 1 | type | Type | | required / min_length / max_length / min / max / pattern |
| 2 | value | Value | | Constraint value |
| 3 | message | Error Message | | e.g., "This field is required" |
| 4 | position | Position | | |

#### admin_layout_section

| Pos | column_code | label_override | required | placeholder |
|:---:|------------|---------------|:---:|------------|
| 1 | code | Code | | e.g., general |
| 2 | label | Label | | e.g., General Information |
| 3 | collapsible | Collapsible | | |
| 4 | columns | Columns | | 1 / 2 / 3 |
| 5 | position | Position | | |

#### admin_section_field

| Pos | column_code | label_override | required |
|:---:|------------|---------------|:---:|
| 1 | position | Position | |

#### admin_sub_form_config

| Pos | column_code | label_override | required | placeholder |
|:---:|------------|---------------|:---:|------------|
| 1 | relation_code | Relation Code | | FK column on child table |
| 2 | child_form_code | Child Form Code | | e.g., order_line |
| 3 | label | Tab Label | | e.g., Order Lines |
| 4 | display_as | Display As | | tab / inline_grid |
| 5 | position | Position | | Tab order |

#### admin_tenant_role_access

| Pos | column_code | label_override | required | placeholder |
|:---:|------------|---------------|:---:|------------|
| 1 | role_id | Role ID | ✓ | UUID of the role |

#### admin_row_filter

| Pos | column_code | label_override | required | placeholder |
|:---:|------------|---------------|:---:|------------|
| 1 | condition_field | Condition Field | | e.g., created_by |
| 2 | condition_operator | Operator | | equals / not_equals / etc. |
| 3 | condition_value | Value | | e.g., {current_user_id} |
| 4 | position | Position | | |

### Part 4 — Insert Layout Sections

All forms use a single 2-column section:

| Form | Section Code | Section Label | Cols | Pos |
|------|-------------|--------------|:---:|:---:|
| admin_field_rule | details | Rule Details | 2 | 1 |
| admin_field_validation | details | Validation Details | 2 | 1 |
| admin_layout_section | details | Section Details | 2 | 1 |
| admin_section_field | details | Mapping Details | 1 | 1 |
| admin_sub_form_config | details | Sub-Form Details | 2 | 1 |
| admin_tenant_role_access | details | Role Access | 1 | 1 |
| admin_row_filter | details | Filter Details | 2 | 1 |

### Part 5 — Insert Section-Field Mappings

All fields go into their form's "details" section in position order.

### Part 6 — Sub-Form Configs (Form Definition → Fields, Rules, Validations)

```sql
-- Form Definition → Form Fields tab
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'form_id', 'admin_form_field', 'Fields', 'tab', 1, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_form_definition';

-- Form Definition → Field Rules tab
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'form_id', 'admin_field_rule', 'Rules', 'tab', 2, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_form_definition';

-- Form Definition → Field Validations tab
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'form_id', 'admin_field_validation', 'Validations', 'tab', 3, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_form_definition';
```

**Important note:** The `relation_code = 'form_id'` is used for all three sub-forms. However, field rules and field validations actually link through `field_id` (via sys_form_fields), not directly through `form_id`. For the MVP, these tabs will list ALL rules/validations for the form by using a custom join. The Developer should handle this in the migration by either:
- Using a view that joins rules/validations through fields to expose a `form_id` column
- Or noting this as a known limitation and creating enhancement tasks later

### Part 7 — Sub-Form Config (Layout Section → Section Fields)

```sql
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'section_id', 'admin_section_field', 'Field Mappings', 'tab', 1, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_layout_section';
```

---

# Acceptance Criteria

- [ ] Flyway migration file exists
- [ ] Migration is idempotent
- [ ] 7 form rows inserted into `sys_metadata_views`
- [ ] ~28 field rows inserted into `sys_form_fields`
- [ ] 7 layout section rows inserted
- [ ] All section-field mappings complete
- [ ] 4 sub-form configs: Form Definition → Fields/Rules/Validations + Layout Section → Section Fields
- [ ] After migration + restart, all 11 admin forms appear in `GET /api/runtime/forms`
- [ ] Opening admin_form_definition shows Fields, Rules, and Validations tabs
- [ ] Opening admin_layout_section shows "Field Mappings" tab

---

# Technical Notes

### Form Definition Sub-Forms — FK Challenge
Field rules (`sys_form_field_rules`) and field validations (`sys_form_field_validations`) reference `field_id` (FK to `sys_form_fields`), not `form_id` directly. PRD-001's sub-form engine expects a direct FK column on the child table pointing to the parent. 

For the MVP, there are two approaches:
1. **Create a database VIEW** that joins `sys_form_field_rules` / `sys_form_field_validations` through `sys_form_fields` to expose a `form_id` column, then register the view as a static table
2. **Accept the limitation** — these tabs won't filter by parent form (they'll show ALL rules/validations). Document as a known limitation and create an enhancement task.

The Developer should implement option 1 if feasible, otherwise document as limitation.

### Flyway Version
Use `{next}` = TASK-034's version + 1.

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_remaining_admin_forms.sql` (new)

---

# Task History

| Date | Agent | Action |
|------|-------|--------|
| 2026-07-10 | Planner | Created task from PRD-002 v1.0.0 |

---

# Related Documents

- [PRD-002 — Admin Configuration Forms](../prd/PRD-002-admin-configuration-forms.md)
- [TASK-034 — Seed Core Admin Forms](../tasks/TASK-034-seed-core-admin-forms.md)
