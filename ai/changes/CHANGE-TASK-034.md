---
id: CHANGE-TASK-034

task_id: TASK-034

parent_prd: PRD-002

branch: feature/TASK-034

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-10

completed: 2026-07-10

duration: 1h

related_commits: []

related_files:
  - backend/src/main/resources/db/migration/V16__seed_core_admin_forms.sql

review_required: true

test_required: true

---

# Summary

Created Flyway migration `V16__seed_core_admin_forms.sql` that defines 4 core admin form definitions in `sys_metadata_views` with complete field configurations, layout sections, and section-field mappings. Includes the sub-form link from Table Definition to Table Columns. These forms allow administrators to manage metadata entities (tables, columns, forms, fields) through the same runtime form engine used for business data.

---

# Business Requirements Implemented

- FR-002: Table Definition Form (admin_table_definition) — 8 fields, 2-column layout, tenant_id marked read-only
- FR-003: Table Column Form (admin_table_column) — 12 fields, 2-column layout, sub-form of Table Definition via `table_id`
- FR-004: Form Definition Form (admin_form_definition) — 9 fields, 2-column layout
- FR-005: Form Field Form (admin_form_field) — 9 fields, 2-column layout (sub-forms for rules/validations deferred to TASK-035)

---

# Files Added

| File | Purpose |
|------|---------|
| backend/src/main/resources/db/migration/V16__seed_core_admin_forms.sql | Flyway migration to seed 4 core admin forms with fields, layouts, and sub-form config |

---

# Files Modified

None

---

# Files Removed

None

---

# Database Changes

## Tables Modified

None (INSERT-only into existing tables: sys_metadata_views, sys_form_fields, sys_form_layout_sections, sys_form_section_fields, sys_form_sub_forms)

## Migrations

- V16: `V16__seed_core_admin_forms.sql` — 256 lines, 4 forms, 38 fields, 4 layout sections, 4 section-field mappings, 1 sub-form config

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

# Configuration

None

---

# Dependencies

None

---

# Validation

## Build

PASS — `mvn clean compile` exits clean

## Existing Automated Tests

N/A (SQL-only migration; requires PostgreSQL to execute)

## Insert Count Verification

- 4 form definitions (sys_metadata_views) ✓
- 38 form fields (sys_form_fields) ✓
- 4 layout sections (sys_form_layout_sections) ✓
- 4 section-field mappings (sys_form_section_fields) ✓
- 1 sub-form config (sys_form_sub_forms) ✓

---

# Breaking Changes

None

---

# Known Issues

- No sub-form link from admin_form_definition to admin_form_field (deferred to TASK-035)
- Navigation grouping ("Administration" section) deferred to TASK-035
- Form-specific sub-forms (rules, validations, role access) deferred to TASK-035

---

# Developer Notes

- Section-field mappings use 4-way JOINs (form → section → field) to dynamically resolve IDs without subqueries
- `ON CONFLICT (name) DO UPDATE` on form definitions provides double idempotency beyond DELETE cleanup
- All layout sections use `collapsible=false` and `columns=2` matching PRD-002 spec
- `tenant_id` field set to `read_only=true` as specified in PRD-002 column visibility rules

---

# QA Handoff

- Verify migration executes successfully against PostgreSQL with V15 already applied
- Verify 4 forms returned by runtime form engine (GET /api/runtime/forms)
- Verify admin_table_definition form shows "Columns" sub-form tab
- Verify tenant_id field is read-only on admin_table_definition
- Verify system columns (id, created_at, etc.) are NOT shown in form fields
- Verify migration is re-entrant (run twice, same result)

---

# Related Documents

- [TASK-034 — Seed Core Admin Forms](../tasks/TASK-034-seed-core-admin-forms.md)
- [PRD-002 — Admin Configuration Forms](../prd/PRD-002-admin-configuration-forms.md)
- [TASK-033 — Register Metadata Tables Static](../tasks/TASK-033-register-metadata-tables-static.md)
