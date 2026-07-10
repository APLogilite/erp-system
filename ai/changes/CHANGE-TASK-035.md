---
id: CHANGE-TASK-035

task_id: TASK-035

parent_prd: PRD-002

branch: feature/TASK-035

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-10

completed: 2026-07-10

duration: 1.5h

related_commits: []

related_files:
  - backend/src/main/resources/db/migration/V17__seed_remaining_admin_forms.sql

review_required: true

test_required: true

---

# Summary

Created Flyway migration `V17__seed_remaining_admin_forms.sql` that defines the final 7 admin form definitions with complete field configurations, layout sections, section-field mappings, and sub-form links. Implemented database VIEWs (`v_admin_field_rules`, `v_admin_field_validations`) to resolve the FK challenge where field rules and field validations reference `field_id` instead of `form_id`, enabling proper sub-form filtering. This completes PRD-002's requirement of 11 total admin forms for metadata table management.

---

# Business Requirements Implemented

- FR-005: Form Fields, Rules, Validations — admin_field_rule (6 fields), admin_field_validation (4 fields) with VIEW-backed sub-form links
- FR-006: Layout Section Form — admin_layout_section (5 fields) with sub-form link to admin_section_field
- FR-007: Sub-Form Config Form — admin_sub_form_config (5 fields)
- FR-008: Role Access Forms — admin_tenant_role_access (1 field), admin_row_filter (4 fields)
- FR-009: Navigation Grouping — all 11 admin forms ready for "Administration" section (navigation config handled by runtime)

---

# Files Added

| File | Purpose |
|------|---------|
| backend/src/main/resources/db/migration/V17__seed_remaining_admin_forms.sql | Flyway migration: 7 forms, 26 fields, 7 sections, 4 sub-forms, 2 VIEWs |

---

# Database Changes

## Tables Modified

None (INSERT-only)

## Views Created

- `v_admin_field_rules` — joins sys_form_field_rules with sys_form_fields to expose form_id
- `v_admin_field_validations` — joins sys_form_field_validations with sys_form_fields to expose form_id

## Models Registered

- `v_admin_field_rules` — static table registration with 6 column definitions
- `v_admin_field_validations` — static table registration with 4 column definitions

## Migrations

- V17: `V17__seed_remaining_admin_forms.sql` — 440 lines

---

# API Changes

None

---

# Validation

## Build

PASS — `mvn clean compile` exits clean

## Insert Count Verification

- 7 form definitions (sys_metadata_views) ✓
- 26 form fields (sys_form_fields) ✓
- 7 layout sections (sys_form_layout_sections) ✓
- 7 section-field mappings (sys_form_section_fields) ✓
- 4 sub-form configs (sys_form_sub_forms) ✓
- 2 view models registered (sys_metadata_models) ✓
- 10 view columns registered (sys_table_columns) ✓

---

# Breaking Changes

None

---

# Known Issues

- VIEW-backed forms (admin_field_rule, admin_field_validation) are read-only. PRD-001 runtime treats views and base tables the same for query purposes, but PostgreSQL views with JOINs may have limited updatability. INSERT/UPDATE/DELETE operations on these forms may fail if the runtime attempts direct DML on the view.
- Navigation grouping ("Administration" section) is not configured in this migration — the 11 forms appear alongside business forms in the runtime menu. This is handled by the frontend navigation configuration, not the database layer.

---

# Developer Notes

- **VIEW approach** (Option 1 from task spec) was chosen over the simpler approach to ensure sub-form filtering works correctly. Without views, admin_form_definition → Rules/Validations tabs would show ALL rules/validations across all forms instead of filtering to the selected form.
- Views are registered as `table_type = 'static'` in sys_metadata_models with their own column definitions
- The admin_field_rule and admin_field_validation forms use `model_name = 'v_admin_field_rules'` and `'v_admin_field_validations'` respectively (not the base table names)
- Sub-form configs use `relation_code = 'form_id'` which matches the VIEW's exposed column
- All forms use `scope = 'global'` and `tenant_id = NULL` as specified in PRD-002

---

# QA Handoff

- Verify migration executes successfully against PostgreSQL with V15-V16 already applied
- Verify 11 total admin forms (4 from V16 + 7 from V17) appear in runtime form list
- Verify admin_form_definition shows "Fields", "Rules", "Validations" tabs
- Verify Rules tab filters by parent form (view-backed sub-form filtering working)
- Verify Validations tab filters by parent form (view-backed sub-form filtering working)
- Verify admin_layout_section shows "Field Mappings" tab
- Verify migration is re-entrant (run twice, same result)
- Note: VIEW-backed forms may be read-only — test INSERT/UPDATE/DELETE on admin_field_rule and admin_field_validation forms

---

# Related Documents

- [TASK-035 — Seed Remaining Admin Forms](../tasks/TASK-035-seed-remaining-admin-forms.md)
- [PRD-002 — Admin Configuration Forms](../prd/PRD-002-admin-configuration-forms.md)
- [TASK-034 — Seed Core Admin Forms](../tasks/TASK-034-seed-core-admin-forms.md)
