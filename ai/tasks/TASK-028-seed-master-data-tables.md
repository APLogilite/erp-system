---
id: TASK-028

title: Seed Master Data Tables (Flyway Migration)

type: Database

status: READY_FOR_DEV

priority: High

owner: developer

assigned_to:

assigned_branch:

locked: false

created: 2026-07-10

updated: 2026-07-10

started:

completed:

estimated_hours: 3

actual_hours:

parent_prd: PRD-003

prd_version: 1.0.0

prd_branch: prd/PRD-003-erp-order-flow-forms

base_branch: main

merge_target: prd/PRD-003-erp-order-flow-forms

merge_strategy: merge

parent_task:

related_tasks: []

depends_on: []

blocks:
  - TASK-029
  - TASK-030

labels:
  - database
  - flyway
  - seed
  - master-data

review_required: true

test_required: true

automation_required: false

change_summary:

test_report:

history:
  - 2026-07-10 — Planner — Created task from PRD-002 v1.0.0
  - 2026-07-10 — Planner — Activated to READY_FOR_DEV (PRD-002 APPROVED, no task dependencies)

---

# Goal

Create a Flyway migration that seeds the 5 master data tables: Business Partner, Product, Unit of Measure, UOM Conversion, and Warehouse. This includes both the physical PostgreSQL table creation (DDL) and the metadata inserts so PRD-001's runtime engine can discover and render forms for these tables.

---

# Description

Create file `backend/src/main/resources/db/migration/V{next}__seed_master_data_tables.sql` with these sections:

### Part 1 — Enable Flyway (if needed)
- Document that `spring.flyway.enabled=true` must be set in `application-local.properties` before running

### Part 2 — Drop Existing Tables
```sql
DROP TABLE IF EXISTS md_uom_conversion CASCADE;
DROP TABLE IF EXISTS md_product CASCADE;
DROP TABLE IF EXISTS md_warehouse CASCADE;
DROP TABLE IF EXISTS md_business_partner CASCADE;
DROP TABLE IF EXISTS md_uom CASCADE;
```
Order matters: drop tables with foreign keys first.

### Part 3 — Create Physical Tables

Each table must include system columns: `id UUID PRIMARY KEY DEFAULT gen_random_uuid()`, `tenant_id UUID NOT NULL`, `created_at TIMESTAMP NOT NULL DEFAULT now()`, `updated_at TIMESTAMP NOT NULL DEFAULT now()`, `created_by UUID`, `updated_by UUID`, `is_active BOOLEAN NOT NULL DEFAULT true`, `deleted_at TIMESTAMP`.

#### md_business_partner
| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| code | VARCHAR(50) | NOT NULL, UNIQUE |
| name | VARCHAR(200) | NOT NULL |
| partner_type | VARCHAR(20) | NOT NULL |
| email | VARCHAR(100) | |
| phone | VARCHAR(30) | |
| address | TEXT | |
| tax_id | VARCHAR(50) | |

#### md_product
| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| code | VARCHAR(50) | NOT NULL, UNIQUE |
| name | VARCHAR(200) | NOT NULL |
| description | TEXT | |
| product_type | VARCHAR(20) | NOT NULL |
| uom_id | UUID | REFERENCES md_uom(id) |
| unit_price | NUMERIC(15,2) | |
| is_active | BOOLEAN | DEFAULT true |

#### md_uom
| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| code | VARCHAR(10) | NOT NULL, UNIQUE |
| name | VARCHAR(50) | NOT NULL |

#### md_uom_conversion
| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| from_uom_id | UUID | NOT NULL, REFERENCES md_uom(id) |
| to_uom_id | UUID | NOT NULL, REFERENCES md_uom(id) |
| product_id | UUID | REFERENCES md_product(id) |
| factor | NUMERIC(15,6) | NOT NULL |

#### md_warehouse
| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| code | VARCHAR(20) | NOT NULL, UNIQUE |
| name | VARCHAR(100) | NOT NULL |
| address | TEXT | |

### Part 4 — Insert Metadata

For each table, insert into `sys_metadata_models`:
```sql
-- Delete existing metadata for this table (for idempotency)
DELETE FROM sys_table_columns WHERE table_id IN (SELECT id FROM sys_metadata_models WHERE name = 'md_business_partner');
DELETE FROM sys_metadata_models WHERE name = 'md_business_partner';

-- Insert table definition
INSERT INTO sys_metadata_models (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), 'md_business_partner', 'Business Partner', 'Business Partners', 'dynamic', 'md_business_partner', 'Customers, suppliers, and other business contacts', true, now(), now());
```

For each column, insert into `sys_table_columns`:
```sql
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, default_value, max_length, precision, scale, relation_table, enum_options, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code', 'Code', 'string', true, null, 50, null, null, null, null, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'md_business_partner';
```

Use the `SELECT ... FROM sys_metadata_models WHERE name = ...` pattern to resolve the table_id without hardcoding UUIDs.

### Complete Column List for Metadata

**md_business_partner**: code (string, pos 1), name (string, pos 2), partner_type (enum: customer/supplier/both, pos 3), email (string, pos 4), phone (string, pos 5), address (text, pos 6), tax_id (string, pos 7)

**md_product**: code (string, pos 1), name (string, pos 2), description (text, pos 3), product_type (enum: goods/service, pos 4), uom_id (many2one: md_uom, pos 5), unit_price (decimal:15/2, pos 6), is_active (boolean, pos 7)

**md_uom**: code (string, pos 1), name (string, pos 2)

**md_uom_conversion**: from_uom_id (many2one: md_uom, pos 1), to_uom_id (many2one: md_uom, pos 2), product_id (many2one: md_product, pos 3), factor (decimal:15/6, pos 4)

**md_warehouse**: code (string, pos 1), name (string, pos 2), address (text, pos 3)

### Part 5 — Foreign Key Indexes

Add indexes on foreign key columns for query performance:
```sql
CREATE INDEX IF NOT EXISTS idx_product_uom ON md_product(uom_id);
CREATE INDEX IF NOT EXISTS idx_uom_conv_from ON md_uom_conversion(from_uom_id);
CREATE INDEX IF NOT EXISTS idx_uom_conv_to ON md_uom_conversion(to_uom_id);
CREATE INDEX IF NOT EXISTS idx_uom_conv_product ON md_uom_conversion(product_id);
```

---

# Acceptance Criteria

- [ ] Flyway migration file exists at `backend/src/main/resources/db/migration/V{next}__seed_master_data_tables.sql`
- [ ] Migration drops existing tables before creating (idempotent)
- [ ] All 5 tables are created with correct PostgreSQL column types
- [ ] All tables include the 8 system columns (id, tenant_id, created_at, updated_at, created_by, updated_by, is_active, deleted_at)
- [ ] All metadata rows are inserted into `sys_metadata_models` (5 rows)
- [ ] All column metadata is inserted into `sys_table_columns` (7 + 7 + 2 + 4 + 3 = 23 rows)
- [ ] Enum column types use PostgreSQL VARCHAR with the enum options stored in the `enum_options` JSONB metadata column
- [ ] Foreign key indexes exist on all many2one columns
- [ ] Migration runs successfully with `spring.flyway.enabled=true`
- [ ] Migration is idempotent — running twice does not error (due to DELETE before INSERT)
- [ ] After migration, all 5 tables are queryable in PostgreSQL
- [ ] `spring.flyway.enabled=true` is documented in migration comments

---

# Technical Notes

### Flyway Version Number
The `{next}` version number must be determined by checking the last existing migration in `backend/src/main/resources/db/migration/`. If no Flyway migrations exist yet, start at `V1`.

### Enum Columns
In PostgreSQL, enum columns are stored as `VARCHAR`. The valid values are recorded in `sys_table_columns.enum_options` as a JSONB array: `'["customer","supplier","both"]'`.

### many2one Columns
Stored as `UUID` in PostgreSQL with a comment indicating the referenced table. No hard foreign key constraint (since these are dynamic tables and the referenced table may not exist yet at creation time — however for this migration, master data tables are created first so FKs work). Use proper `REFERENCES` constraints where safe.

### UUID Generation
Use `gen_random_uuid()` for all UUID columns in INSERT statements (not hardcoded UUIDs).

### Soft Delete
The `is_active` and `deleted_at` columns are handled by PRD-001's existing BaseEntity pattern in the DynamicCrudService. The migration just creates the physical columns.

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_master_data_tables.sql` (new)

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
| 2026-07-10 | Planner | Created task from PRD-002 v1.0.0 |

---

# Related Documents

- [PRD-002 — ERP Order Flow Forms](../prd/PRD-002-erp-order-flow-forms.md)
- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
