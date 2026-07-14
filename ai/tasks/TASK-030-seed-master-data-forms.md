---
id: TASK-030

title: Seed Master Data Forms (Flyway Migration)

type: Database

status: TESTED

priority: High

owner: QA Engineer

assigned_to: QA Engineer

assigned_branch: feature/TASK-030

locked: true

created: 2026-07-10

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 3

actual_hours: 1

parent_prd: PRD-003

prd_version: 1.0.0

prd_branch: prd/PRD-003-erp-order-flow-forms

base_branch: main

merge_target: prd/PRD-003-erp-order-flow-forms

merge_strategy: merge

parent_task:

related_tasks: []

depends_on:
  - TASK-028

blocks: []

labels:
  - database
  - flyway
  - seed
  - forms
  - master-data

review_required: true

test_required: true

automation_required: false

change_summary: ai/changes/CHANGE-TASK-030.md

test_report:

history:
  - 2026-07-10 — Product Manager — Created task from PRD-002 v1.0.0
  - 2026-07-13 — Software Engineer — Activated to IN_DEVELOPMENT, started implementation
  - 2026-07-13 — Software Engineer — Created V21 Flyway migration with 5 master data forms + metadata

---

# Goal

Create a Flyway migration that defines 5 master data forms (Business Partner, Product, UOM, UOM Conversion, Warehouse) as metadata. These are single-screen CRUD forms with no sub-forms.

---

# Description

Create file `backend/src/main/resources/db/migration/V{next}__seed_master_data_forms.sql`.

### Part 1 — Clean Existing (idempotency)

Delete existing form metadata for these 5 form codes in FK-safe order:
```sql
DELETE FROM sys_form_section_fields WHERE section_id IN (SELECT id FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('business_partner','product','uom','uom_conversion','warehouse')));
DELETE FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('business_partner','product','uom','uom_conversion','warehouse'));
DELETE FROM sys_form_fields WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('business_partner','product','uom','uom_conversion','warehouse'));
DELETE FROM sys_metadata_views WHERE name IN ('business_partner','product','uom','uom_conversion','warehouse');
```

### Part 2 — Insert Form Definitions (sys_metadata_views)

For each form:
```sql
INSERT INTO sys_metadata_views (id, name, model_name, type, scope, tenant_id, description, where_clause_field, where_clause_operator, where_clause_value, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), 'business_partner', 'md_business_partner', 'form', 'global', NULL, 'Manage customers, suppliers, and business contacts', NULL, NULL, NULL, true, now(), now());
```

**Form definitions to insert:**

| name | model_name | description |
|------|-----------|-------------|
| business_partner | md_business_partner | Manage customers, suppliers, and business contacts |
| product | md_product | Manage products and services |
| uom | md_uom | Manage units of measure |
| uom_conversion | md_uom_conversion | Define UOM conversion factors |
| warehouse | md_warehouse | Manage warehouse locations |

All forms: `type='form'`, `scope='global'`, `tenant_id=NULL`.

### Part 3 — Insert Form Fields (sys_form_fields)

Use the pattern: `SELECT id FROM sys_metadata_views WHERE name = '...'` to resolve form_id.  
Use position numbers starting at 1.  
All fields: `visible=true`, `read_only=false`, `required` per table below.  
`column_code` must match the `code` in `sys_table_columns`.

#### Form: business_partner

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | code | Code | ✓ | Enter partner code |
| 2 | name | Name | ✓ | Enter partner name |
| 3 | partner_type | Partner Type | ✓ | |
| 4 | email | Email | | |
| 5 | phone | Phone | | |
| 6 | address | Address | | |
| 7 | tax_id | Tax ID | | VAT/GST number |

#### Form: product

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | code | SKU | ✓ | Enter product code |
| 2 | name | Name | ✓ | Enter product name |
| 3 | description | Description | | |
| 4 | product_type | Product Type | ✓ | |
| 5 | uom_id | Default UOM | | |
| 6 | unit_price | Unit Price | | |
| 7 | is_active | Active | | |

#### Form: uom

| Pos | column_code | label_override | required |
|:---:|-------------|---------------|:---:|
| 1 | code | Code | ✓ |
| 2 | name | Name | ✓ |

#### Form: uom_conversion

| Pos | column_code | label_override | required |
|:---:|-------------|---------------|:---:|
| 1 | from_uom_id | From UOM | ✓ |
| 2 | to_uom_id | To UOM | ✓ |
| 3 | product_id | Product | |
| 4 | factor | Conversion Factor | ✓ |

#### Form: warehouse

| Pos | column_code | label_override | required |
|:---:|-------------|---------------|:---:|
| 1 | code | Code | ✓ |
| 2 | name | Name | ✓ |
| 3 | address | Address | |

### Part 4 — Insert Layout Sections (sys_form_layout_sections)

Each form gets one or two sections. Sections use `columns=2` where fields can be paired side-by-side.

#### Form: business_partner — 2 sections

| Section Code | Label | Columns | Position |
|-------------|-------|:---:|:---:|
| general | General Information | 2 | 1 |
| contact | Contact Details | 2 | 2 |

#### Form: product — 2 sections

| Section Code | Label | Columns | Position |
|-------------|-------|:---:|:---:|
| general | General Information | 2 | 1 |
| pricing | Pricing | 2 | 2 |

#### Form: uom — 1 section

| Section Code | Label | Columns | Position |
|-------------|-------|:---:|:---:|
| general | General Information | 1 | 1 |

#### Form: uom_conversion — 1 section

| Section Code | Label | Columns | Position |
|-------------|-------|:---:|:---:|
| general | Conversion Details | 2 | 1 |

#### Form: warehouse — 1 section

| Section Code | Label | Columns | Position |
|-------------|-------|:---:|:---:|
| general | General Information | 1 | 1 |

### Part 5 — Insert Section-Field Mappings (sys_form_section_fields)

Map each field to its section, using the pattern:
```sql
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), sec.id, fld.id, 1, now(), now()
FROM sys_form_layout_sections sec
JOIN sys_metadata_views v ON sec.form_id = v.id
JOIN sys_form_fields fld ON fld.form_id = v.id
WHERE v.name = 'business_partner' AND sec.code = 'general' AND fld.column_code = 'code';
```

**Field-to-section assignments:**

**business_partner**
- Section 'general' (pos 1-5): code, name, partner_type, email, phone
- Section 'contact' (pos 1-2): address, tax_id

**product**
- Section 'general' (pos 1-5): code, name, description, product_type, uom_id
- Section 'pricing' (pos 1-2): unit_price, is_active

**uom** — Section 'general' (pos 1-2): code, name

**uom_conversion** — Section 'general' (pos 1-4): from_uom_id, to_uom_id, product_id, factor

**warehouse** — Section 'general' (pos 1-3): code, name, address

---

# Acceptance Criteria

- [ ] Flyway migration file exists at `V{next}__seed_master_data_forms.sql`
- [ ] Migration cleans existing data before inserting (idempotent)
- [ ] 5 rows inserted into `sys_metadata_views`
- [ ] 23 field rows inserted into `sys_form_fields` (7+7+2+4+3)
- [ ] Layout sections inserted into `sys_form_layout_sections` (2+2+1+1+1 = 7 section rows)
- [ ] Section-field mappings inserted into `sys_form_section_fields` (23 rows — one per field)
- [ ] Fields reference correct `column_code` values matching `sys_table_columns`
- [ ] `required` flag matches PRD-002 FR-003
- [ ] All forms are `scope='global'`
- [ ] Migration runs successfully after TASK-028
- [ ] After migration + restart, `GET /api/runtime/forms` returns these 5 forms
- [ ] Each form's definition loads correctly via `GET /api/runtime/forms/{formCode}/definition`

---

# Technical Notes

### Form ID Resolution
All INSERTs use `SELECT ... FROM sys_metadata_views WHERE name = '...'` to resolve the form UUID. Never hardcode UUIDs.

### Field Order
The `position` column in `sys_form_fields` determines the default field order. `sys_form_section_fields.position` overrides within each section.

### No Rules / No Validations
This PRD creates forms with NO field rules and NO validations beyond the `required` flag. These can be added later via the Form Designer UI or enhancement tasks. This matches the "no fancy logic" requirement.

### Flyway Version
Use `{next}` = TASK-028's version + 2 (after TASK-029).

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_master_data_forms.sql` (new)

---

# Developer Notes

*(maintained by Software Engineer)*

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

- [PRD-002 — ERP Order Flow Forms](../prd/PRD-002-erp-order-flow-forms.md)
- [TASK-028 — Seed Master Data Tables](../tasks/TASK-028-seed-master-data-tables.md)
