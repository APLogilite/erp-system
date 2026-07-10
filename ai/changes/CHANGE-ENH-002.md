---
id: CHANGE-ENH-002

task_id: ENH-002

parent_prd: PRD-002

branch: enhancement/ENH-002

type: Enhancement

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-10

completed: 2026-07-10

duration: 1.5h

related_commits: []

related_files:
  - backend/src/main/resources/db/migration/V18__add_tenant_id_to_admin_forms.sql

review_required: true

test_required: true

---

# Summary

Created Flyway migration `V18__add_tenant_id_to_admin_forms.sql` (320 lines) that closes REQ-ISSUE-001 identified during PRD-002 QA testing. Adds `tenant_id` as a **read-only** field to all 10 admin forms that were missing it, and registers `tenant_id` in `sys_table_columns` for all 11 metadata tables plus the 2 supporting VIEW models. This is a tenant-isolation safeguard required in a multi-tenant platform where row-level tenant isolation is enforced at the database level.

---

# Business Requirements Implemented

- PRD-002 v1.1.0 — Strengthened tenant_id requirement: ALL admin forms MUST display tenant_id (read-only) for tenant isolation auditability
- 10 forms corrected: admin_table_column, admin_form_definition, admin_form_field, admin_field_rule, admin_field_validation, admin_layout_section, admin_section_field, admin_sub_form_config, admin_tenant_role_access, admin_row_filter
- All tenant_id fields marked `read_only=true`, `visible=true`, with placeholder 'Auto-managed'
- admin_table_definition excluded (already compliant from V16)

---

# Files Added

| File | Purpose |
|------|---------|
| backend/src/main/resources/db/migration/V18__add_tenant_id_to_admin_forms.sql | Flyway migration: 13 column registrations + 10 form fields + 10 section-field mappings |

---

# Files Modified

None

---

# Files Removed

None

---

# Database Changes

## Tables Modified

None (INSERT-only into existing tables: sys_table_columns, sys_form_fields, sys_form_section_fields)

## Migrations

- V18: `V18__add_tenant_id_to_admin_forms.sql` — 320 lines

### INSERT Counts

| Target Table | Count | Description |
|-------------|:---:|------------|
| sys_table_columns | 13 | 11 base table models + 2 VIEW models (v_admin_field_rules, v_admin_field_validations) |
| sys_form_fields | 10 | One per form (all except admin_table_definition) |
| sys_form_section_fields | 10 | Section mappings for new tenant_id fields |

### Why 13 Column Registrations (not 11)

The task spec called for 11 registrations (one per metadata table). However, `admin_field_rule` and `admin_field_validation` forms reference VIEW models (`v_admin_field_rules`, `v_admin_field_validations`), not base tables. The PRD-001 runtime engine looks up columns by the model name referenced in the form definition. Registering tenant_id on the VIEW models ensures the runtime can discover and render the field for those two forms. 11 base table registrations + 2 VIEW model registrations = 13 total.

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

## Test Suite

PASS — `mvn test`: 36 tests, 0 failures, 3 pre-existing errors (H2/PostgreSQL incompatibility in DatabaseConnectionTest — unchanged)

## INSERT Count Verification

| Check | Count | Status |
|-------|:---:|:---:|
| Column registrations (Part 1) | 13 | ✅ |
| Form fields (Part 2) | 10 | ✅ |
| Section-field mappings (Part 3) | 10 | ✅ |
| Total lines | 320 | ✅ |

## Idempotency Verification

- All INSERTs use `AND NOT EXISTS (SELECT 1 FROM ... WHERE ...)` guard pattern
- Migration can run multiple times safely without duplicates
- No DELETE cleanup needed (independent idempotency)

## Position Verification

| Table/Form | tenant_id Position | After Existing Last | Status |
|-----------|:---:|-----|:---:|
| sys_metadata_models | 8 | is_active (7) | ✅ |
| sys_table_columns | 13 | is_active (12) | ✅ |
| sys_metadata_views | 10 | is_active (9) | ✅ |
| sys_form_fields | 10 | is_active (9) | ✅ |
| sys_form_field_rules | 7 | position (6) | ✅ |
| sys_form_field_validations | 5 | position (4) | ✅ |
| sys_form_layout_sections | 6 | position (5) | ✅ |
| sys_form_section_fields | 2 | position (1) | ✅ |
| sys_form_sub_forms | 6 | position (5) | ✅ |
| sys_form_tenant_role | 2 | role_id (1) | ✅ |
| sys_form_role_filters | 5 | position (4) | ✅ |
| v_admin_field_rules | 7 | position (6) | ✅ |
| v_admin_field_validations | 5 | position (4) | ✅ |

---

# Breaking Changes

None

---

# Known Issues

- **VIEW-backed forms (admin_field_rule, admin_field_validation):** These forms use PostgreSQL JOIN views as their model. The V18 migration registers tenant_id on the VIEW models so the runtime can discover it. However, the underlying VIEW limitation (DML may fail — documented in CHANGE-TASK-035) still applies. tenant_id display on these forms is read-only, which aligns with the VIEW's natural limitation.
- **PostgreSQL runtime validation deferred:** Actual migration execution and form rendering require a running PostgreSQL instance.

---

# Future Improvements

- Consider adding tenant_id to the V15 column registrations as part of a consolidated migration (if V15-V18 are ever merged for a fresh deployment)
- Add automated Flyway migration tests with Testcontainers PostgreSQL for CI validation

---

# Developer Notes

- Used `AND NOT EXISTS` pattern instead of `ON CONFLICT` because `sys_table_columns` does not have a UNIQUE constraint on `(table_id, code)` — the `NOT EXISTS` pattern is safer and equally idempotent
- VIEW model registrations use `v_admin_field_rules` and `v_admin_field_validations` as model names, matching their V17 registrations
- Positions carefully verified against V15 (column registrations) and V16/V17 (form fields) to avoid collisions
- admin_table_definition excluded via `NOT EXISTS` guard — V16 already has tenant_id at position 8

---

# QA Handoff

- Verify migration executes successfully against PostgreSQL with V15-V17 already applied
- Verify 11 forms display tenant_id as read-only field (10 new + 1 existing on admin_table_definition)
- Verify `GET /api/runtime/forms` returns tenant_id in field lists for all 11 admin forms
- Verify migration is re-entrant (run twice, same result)
- Verify no duplicate tenant_id on admin_table_definition (NOT EXISTS guard confirmation)

---

# Related Documents

- [ENH-002 — Add tenant_id Field to All Admin Forms](../tasks/ENH-002-add-tenant-id-to-admin-forms.md)
- [PRD-002 v1.1.0 — Admin Configuration Forms](../prd/PRD-002-admin-configuration-forms.md)
- [TEST-TASK-034 — QA Report (REQ-ISSUE-001)](../tests/TEST-TASK-034.md)
- [TEST-TASK-035 — QA Report (REQ-ISSUE-001)](../tests/TEST-TASK-035.md)
