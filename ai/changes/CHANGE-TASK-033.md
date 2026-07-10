---
id: CHANGE-TASK-033

task_id: TASK-033

parent_prd: PRD-002

branch: feature/TASK-033

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-10

completed: 2026-07-10

duration: 1h

related_commits: []

related_files:
  - backend/src/main/resources/db/migration/V15__register_metadata_tables_static.sql

review_required: true

test_required: true

---

# Summary

Created Flyway migration `V15__register_metadata_tables_static.sql` that registers all 11 PRD-001 metadata tables in `sys_metadata_models` as `table_type = 'static'` and inserts 63 column definitions into `sys_table_columns`. This allows the PRD-001 runtime engine to discover and render these system metadata tables through the same dynamic form mechanism used for business data. No DDL is executed — the tables already exist. The migration is idempotent via DELETE-before-INSERT cleanup.

---

# Business Requirements Implemented

- FR-001: Register Metadata Tables — All 11 tables and their columns registered in sys_metadata_models and sys_table_columns
- 11 tables registered: sys_metadata_models, sys_table_columns, sys_metadata_views, sys_form_fields, sys_form_field_rules, sys_form_field_validations, sys_form_layout_sections, sys_form_section_fields, sys_form_sub_forms, sys_form_tenant_role, sys_form_role_filters
- 63 column definitions registered with correct types, labels, and positions as specified in PRD-002

---

# Files Added

| File | Purpose |
|------|---------|
| backend/src/main/resources/db/migration/V15__register_metadata_tables_static.sql | Flyway migration to register static metadata tables and columns |

---

# Files Modified

None

---

# Files Removed

None

---

# Database Changes

## Tables Added

None

## Tables Modified

None (INSERT-only migration into existing sys_metadata_models and sys_table_columns)

## Columns Added

None

## Columns Updated

None

## Columns Removed

None

## Indexes

None

## Constraints

None

## Migrations

- V15: `V15__register_metadata_tables_static.sql` — 351 lines, 11 model inserts + 63 column inserts

---

# API Changes

None

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

# Methods Added

None

---

# Methods Updated

None

---

# Models

None

---

# Services

None

---

# Repositories

None

---

# DTOs

None

---

# Requests

None

---

# Policies

None

---

# Events

None

---

# Jobs

None

---

# Configuration

None

---

# Dependencies

None

---

# Validation

## Build

PASS — `mvn clean compile` exits clean

## Lint

N/A (SQL-only migration)

## Static Analysis

N/A (SQL-only migration)

## Existing Automated Tests

N/A (no tests were modified; migration requires PostgreSQL to execute)

---

# Manual Verification

- Verified column names match actual Java entity definitions for all 11 tables
- Verified column types map correctly (UUID→string, VARCHAR→string, TEXT→text, BOOLEAN→boolean)
- Verified gen_random_uuid() is available via uuid-ossp extension (loaded in V1)
- Verified sys_metadata_models has UNIQUE constraint on `name` column (ON CONFLICT clause valid)
- Verified is_active column exists on sys_table_columns DDL (used in all INSERTs)
- Counted 63 column INSERTs + 1 model INSERT against PRD-002 specification
- Flyway version V15 correctly follows existing sequence (V14 is last existing)

---

# Breaking Changes

None

---

# Known Issues

- The migration will fail if no PostgreSQL connection is available (expected — this is a database migration)
- Some metadata tables (V7-V13) do not have `is_active` in their DDL, but do have it via JPA's BaseEntity. Column registrations follow the PRD-002 spec which excludes `is_active` for those tables.
- FK columns (table_id, form_id, field_id, section_id, etc.) are registered as plain `string` type fields. No `relation_table` references are set to avoid circular dependency issues during MVP.

---

# Future Improvements

- Add relation_table references to FK columns once the circular registration issue is resolved (requires form definitions to exist first, creating chicken-and-egg problem)
- Consider registering `tenant_id` as a column on relevant tables for multi-tenant visibility

---

# Developer Notes

- The migration uses DELETE-before-INSERT + ON CONFLICT for double idempotency
- `definition` column (NOT NULL JSONB) set to `'{}'` for all model inserts
- All column registrations use `SELECT gen_random_uuid(), id, ... FROM sys_metadata_models WHERE name = '...'` pattern to dynamically resolve table IDs
- Column positions follow the exact order specified in PRD-002 "Column Visibility per Form" section

---

# QA Handoff

- Verify migration executes successfully against PostgreSQL with existing metadata tables
- Verify 11 rows exist in sys_metadata_models WHERE table_type = 'static' AND name LIKE 'sys_%'
- Verify 63 rows exist in sys_table_columns for these tables
- Verify migration is re-entrant (run twice, same result)
- Verify `GET /api/runtime/forms` does NOT show admin forms yet (forms will be created in TASK-034)

---

# Related Documents

- [TASK-033 — Register Metadata Tables as Static](../tasks/TASK-033-register-metadata-tables-static.md)
- [PRD-002 — Admin Configuration Forms](../prd/PRD-002-admin-configuration-forms.md)
