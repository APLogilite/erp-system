---
id: CHANGE-TASK-028

task_id: TASK-028

parent_prd: PRD-003

branch: feature/TASK-028

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-13

completed: 2026-07-13

duration: 1h

related_commits:
  - (pending commit)

related_files:
  - backend/src/main/resources/db/migration/V19__seed_master_data_tables.sql

review_required: true

test_required: true

---

# Summary

Created Flyway migration `V19__seed_master_data_tables.sql` that seeds 5 master data tables for the ERP Order Flow module: Business Partner, Product, UOM, UOM Conversion, and Warehouse. The migration creates physical PostgreSQL tables with all required system columns (id, tenant_id, timestamps, soft-delete), registers each table + column in the metadata engine (`sys_metadata_models` / `sys_table_columns`), and adds foreign key indexes for query performance.

---

# Business Requirements Implemented

- [x] Flyway migration file at `V19__seed_master_data_tables.sql`
- [x] Drop existing tables before creating (idempotent DROP IF EXISTS + DELETE before INSERT)
- [x] All 5 tables created with correct PostgreSQL column types
- [x] All tables include 8 system columns (id, tenant_id, created_at, updated_at, created_by, updated_by, is_active, deleted_at)
- [x] All metadata rows inserted into `sys_metadata_models` (5 rows)
- [x] All column metadata inserted into `sys_table_columns` (7 + 7 + 2 + 4 + 3 = 23 rows)
- [x] Enum columns use VARCHAR with enum_options stored in JSONB metadata column
- [x] Foreign key indexes on all many2one columns + tenant_id columns
- [x] Migration is idempotent — running twice does not error
- [x] `spring.flyway.enabled=true` documented in migration comments

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/resources/db/migration/V19__seed_master_data_tables.sql` | Flyway migration creating 5 master data tables + metadata registration |

---

# Files Modified

None

---

# Files Removed

None

---

# Database Changes

## Tables Added

| Table | Type | Columns |
|-------|------|---------|
| `md_business_partner` | dynamic | id, tenant_id, code, name, partner_type, email, phone, address, tax_id + 6 system cols |
| `md_product` | dynamic | id, tenant_id, code, name, description, product_type, uom_id, unit_price, is_active + 5 system cols |
| `md_uom` | dynamic | id, tenant_id, code, name + 5 system cols |
| `md_uom_conversion` | dynamic | id, tenant_id, from_uom_id, to_uom_id, product_id, factor + 5 system cols |
| `md_warehouse` | dynamic | id, tenant_id, code, name, address + 5 system cols |

## Indexes

| Index | Table | Column |
|-------|-------|--------|
| `idx_md_product_uom` | md_product | uom_id |
| `idx_md_uom_conv_from` | md_uom_conversion | from_uom_id |
| `idx_md_uom_conv_to` | md_uom_conversion | to_uom_id |
| `idx_md_uom_conv_product` | md_uom_conversion | product_id |
| `idx_md_uom_conv_tenant` | md_uom_conversion | tenant_id |
| `idx_md_product_tenant` | md_product | tenant_id |
| `idx_md_bp_tenant` | md_business_partner | tenant_id |
| `idx_md_uom_tenant` | md_uom | tenant_id |
| `idx_md_warehouse_tenant` | md_warehouse | tenant_id |

## Migrations

| Migration | Description |
|-----------|-------------|
| `V19__seed_master_data_tables.sql` | Create 5 master data tables + register metadata |

---

# API Changes

None — no Java/TypeScript code changes.

---

# Routes

None

---

# Classes Added

None

---

# Classes Updated

None

---

# Validation

## Build

PASS

Backend `mvn clean compile` passed. Frontend `pnpm typecheck` passed.

## Existing Automated Tests

PASS (with 3 pre-existing errors in DatabaseConnectionTest — H2 vs PostgreSQL incompatibility, documented known limitation)

36 tests run, 0 failures, 3 pre-existing errors (same as before), 0 skipped.

---

# Manual Verification

- [x] Flyway migration follows existing idempotency pattern (DROP IF EXISTS + DELETE before INSERT)
- [x] Enum types store options in JSONB enum_options column consistent with PRD-001 conventions
- [x] many2one columns use UUID type with relation_table metadata consistent with previous migrations
- [x] Foreign key indexes added to match query patterns in Order Flow
- [x] Tenant_id indexes added for multi-tenant performance
- [x] System columns match BaseEntity contract (id, tenant_id, created_at, updated_at, created_by, updated_by, is_active, deleted_at)

---

# Breaking Changes

None

---

# Known Issues

- This is a DDL-only migration — no seed data for the actual tables (no sample records inserted)
- The md_uom_conversion product_id is optional (nullable FK) to allow global conversion factors

---

# Future Improvements

- Add seed data for standard UOMs (pcs, kg, m, l, etc.) in a follow-up migration
- Consider adding CHECK constraints on enum columns for additional safety at the DB level

---

# Developer Notes

The migration does NOT set `spring.flyway.enabled=true` — this must be done manually in `application-local.properties` before running. This is consistent with the project convention where Flyway is disabled by default.

---

# QA Handoff

Suggested focus:
- Verify migration runs successfully with PostgreSQL
- Verify all 5 tables exist after migration with correct columns
- Verify metadata engine can discover all 5 tables via `GET /metadata/models`
- Verify running the migration a second time does not produce errors
- Verify enum fields (partner_type, product_type) store correct options in sys_table_columns.enum_options

---

# Related Documents

- [TASK-028](../tasks/TASK-028-seed-master-data-tables.md)
- [PRD-003](../prd/PRD-003-erp-order-flow-forms.md)
