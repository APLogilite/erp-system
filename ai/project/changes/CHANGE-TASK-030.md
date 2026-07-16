---
id: CHANGE-TASK-030

task_id: TASK-030

parent_prd: PRD-003

branch: feature/TASK-030

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-13

completed: 2026-07-13

duration: 1h

related_commits:
  - (pending commit)

related_files:
  - backend/src/main/resources/db/migration/V21__seed_master_data_forms.sql

review_required: true

test_required: true

---

# Summary

Created Flyway migration `V21__seed_master_data_forms.sql` that defines 5 master data forms (Business Partner, Product, UOM, UOM Conversion, Warehouse) as metadata. Each form is a single-screen global-scope CRUD form with layout sections and field mappings. No sub-forms, rules, or validations.

---

# Business Requirements Implemented

- [x] 5 rows inserted into `sys_metadata_views` (business_partner, product, uom, uom_conversion, warehouse)
- [x] 23 field rows inserted into `sys_form_fields` (7+7+2+4+3)
- [x] 7 layout sections inserted into `sys_form_layout_sections` (2+2+1+1+1)
- [x] 23 section-field mappings inserted into `sys_form_section_fields`
- [x] All fields reference correct `column_code` values matching `sys_table_columns`
- [x] `required` flag per TASK-030 spec
- [x] All forms are `scope='global'`, `tenant_id=NULL`
- [x] Idempotent — DELETE before INSERT

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/resources/db/migration/V21__seed_master_data_forms.sql` | Flyway migration defining 5 master data forms |

---

# Validation

## Build

PASS — `mvn clean compile` succeeded.

## Existing Automated Tests

PASS — same baseline (3 pre-existing H2 errors, unchanged).

---

# Related Documents

- [TASK-030](../tasks/TASK-030-seed-master-data-forms.md)
- [PRD-003](../prd/PRD-003-erp-order-flow-forms.md)
