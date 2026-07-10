---
id: ENH-002

title: Add tenant_id Field to All Admin Forms (Tenant Isolation Safeguard)

type: Enhancement

status: TESTED

priority: Critical

owner: QA Engineer

assigned_to: QA Engineer

assigned_branch: enhancement/ENH-002

locked: true

created: 2026-07-10

updated: 2026-07-10

started: 2026-07-10

completed: 2026-07-10

estimated_hours: 2

actual_hours: 1.5

qa_started: 2026-07-10

qa_completed: 2026-07-10

qa_hours: 0.5

estimated_hours: 2

actual_hours:

parent_prd: PRD-002

prd_version: 1.1.0

prd_branch: prd/PRD-002-admin-configuration-forms

base_branch: prd/PRD-002-admin-configuration-forms

merge_target: prd/PRD-002-admin-configuration-forms

merge_strategy: merge

parent_tasks:
  - TASK-034
  - TASK-035

previous_prd_version: 1.0.0

current_prd_version: 1.1.0

reason: |
  PRD-002 v1.0.0 specified tenant_id (read-only) on all 11 admin forms as a tenant-isolation safeguard.
  TASK-034 and TASK-035 specifications omitted tenant_id from 10 of 11 forms.
  QA identified this as REQ-ISSUE-001 during PRD-002 testing.
  This enhancement adds the missing tenant_id column registrations and form fields.

depends_on:
  - TASK-033
  - TASK-034
  - TASK-035

blocks: []

related_bugs: []

labels:
  - enhancement
  - database
  - flyway
  - tenant-isolation
  - admin
  - forms

review_required: true

test_required: true

automation_required: false

change_summary: CHANGE-ENH-002

test_report: TEST-ENH-002

history:
  - 2026-07-10 — Planner — Created ENH-002 from PRD-002 v1.1.0 (REQ-ISSUE-001). Parent tasks: TASK-034, TASK-035.
  - 2026-07-10 — Software Engineer — Locked, created enhancement/ENH-002 branch, implemented V18 migration (320 lines: 13 column registrations + 10 form fields + 10 section-field mappings). Build passes, tests unchanged (36 total, 0 new failures).
  - 2026-07-10 — QA Engineer — Locked for testing. 10/12 structural tests passed, 2 deferred (PostgreSQL). All 10 REQ-ISSUE-001 gaps closed. 0 bugs found.
---

# Goal

Create a Flyway migration that adds `tenant_id` as a **read-only** field to all 11 PRD-002 admin forms AND registers `tenant_id` in `sys_table_columns` for all 11 metadata tables. This closes REQ-ISSUE-001 identified during QA testing.

**Rationale:** In a multi-tenant platform, tenant isolation is enforced at the database row level via `tenant_id`. An administrator viewing a config record without visible tenant context creates a data-leakage risk. Every admin form must unambiguously display tenant ownership.

---

# Description

Create file `backend/src/main/resources/db/migration/V18__add_tenant_id_to_admin_forms.sql`.

The migration has two parts:

## Part 1 — Register tenant_id Column in sys_table_columns

For each of the 11 metadata tables, insert `tenant_id` into `sys_table_columns` if not already present. Use idempotent INSERT (ON CONFLICT or WHERE NOT EXISTS pattern).

| Table | type | position (after last existing) |
|-------|------|:---:|
| sys_metadata_models | string | 8 |
| sys_table_columns | string | 13 |
| sys_metadata_views | string | 10 |
| sys_form_fields | string | 10 |
| sys_form_field_rules | string | 7 |
| sys_form_field_validations | string | 5 |
| sys_form_layout_sections | string | 6 |
| sys_form_section_fields | string | 2 |
| sys_form_sub_forms | string | 6 |
| sys_form_tenant_role | string | 2 |
| sys_form_role_filters | string | 5 |

Pattern:
```sql
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, <position>, true, now(), now()
FROM sys_metadata_models WHERE name = '<table_name>'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');
```

## Part 2 — Add tenant_id Form Field to Missing Forms

Add `tenant_id` as a **read-only** field at the **last position** on each form that is missing it.

**Admin forms already compliant (no change needed):**
- `admin_table_definition` — already has tenant_id at position 8 (V16)

**Admin forms needing correction (10 forms):**

| Form | Position | column_code | label_override | read_only | placeholder |
|------|:---:|------------|---------------|:---:|------------|
| admin_table_column | 13 | tenant_id | Tenant ID | true | Auto-managed |
| admin_form_definition | 10 | tenant_id | Tenant ID | true | Auto-managed |
| admin_form_field | 10 | tenant_id | Tenant ID | true | Auto-managed |
| admin_field_rule | 7 | tenant_id | Tenant ID | true | Auto-managed |
| admin_field_validation | 5 | tenant_id | Tenant ID | true | Auto-managed |
| admin_layout_section | 6 | tenant_id | Tenant ID | true | Auto-managed |
| admin_section_field | 2 | tenant_id | Tenant ID | true | Auto-managed |
| admin_sub_form_config | 6 | tenant_id | Tenant ID | true | Auto-managed |
| admin_tenant_role_access | 2 | tenant_id | Tenant ID | true | Auto-managed |
| admin_row_filter | 5 | tenant_id | Tenant ID | true | Auto-managed |

Pattern (idempotent):
```sql
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, <position>, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = '<form_code>'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');
```

## Part 3 — Add tenant_id to Layout Section-Field Mappings

For each corrected form, ensure the new `tenant_id` field is mapped to the form's `details` section. Use the same JOIN pattern as V16/V17.

```sql
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = '<form_code>' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);
```

---

# Acceptance Criteria

## Implementation Verification (Software Engineer)

- [ ] Flyway migration file `V18__add_tenant_id_to_admin_forms.sql` created
- [ ] Migration is idempotent (can run multiple times safely)
- [ ] 11 `tenant_id` column registrations inserted into `sys_table_columns` (one per table, type='string')
- [ ] 10 `tenant_id` form fields added (one per form, read_only=true, last position)
- [ ] admin_table_definition NOT modified (already has tenant_id)
- [ ] 10 section-field mappings added for new tenant_id fields
- [ ] All tenant_id fields use `read_only=true`, `visible=true`, `placeholder='Auto-managed'`
- [ ] `mvn clean compile` passes
- [ ] Existing tests still pass (no regression)

## QA Verification (from QA Test Reports)

- [ ] **TC-V18-001 — File existence:** `V18__add_tenant_id_to_admin_forms.sql` exists
- [ ] **TC-V18-002 — Idempotency:** Migration uses `NOT EXISTS` guards or equivalent; re-runnable
- [ ] **TC-V18-003 — Column registrations:** 11 tenant_id rows in sys_table_columns INSERT statements, correct positions per table
- [ ] **TC-V18-004 — Form fields count:** 10 tenant_id INSERTs into sys_form_fields (all forms except admin_table_definition)
- [ ] **TC-V18-005 — read_only:** All tenant_id form fields have `read_only=true`
- [ ] **TC-V18-006 — Position ordering:** Each tenant_id field is at last position (position = max existing + 1)
- [ ] **TC-V18-007 — admin_table_definition NOT modified:** No duplicate tenant_id on this form
- [ ] **TC-V18-008 — Section-field mappings:** 10 section-field mapping INSERTs for tenant_id
- [ ] **TC-V18-009 — Label consistency:** All use `label_override='Tenant ID'`, `placeholder='Auto-managed'`
- [ ] **TC-V18-010 — Build verification:** `mvn clean compile` passes; existing tests unchanged
- [ ] **TC-V18-011 — PostgreSQL runtime:** Migration executes successfully against PostgreSQL (deferred if PostgreSQL unavailable)
- [ ] **TC-V18-012 — Form rendering:** All 11 admin forms display tenant_id as read-only field (deferred if PostgreSQL unavailable)

## SE Pre-Commit Verification Checklist

- [ ] Generate change summary (`ai/changes/CHANGE-ENH-002.md`)
- [ ] Verify Flyway version sequencing (V18 follows V17)
- [ ] Verify `ON CONFLICT` or `NOT EXISTS` pattern is used for idempotency
- [ ] Verify all 11 table names match V15 registrations
- [ ] Verify all 10 form names match V16/V17 form definitions
- [ ] Verify column positions don't collide with existing columns
- [ ] Run `mvn clean compile` — must pass
- [ ] Run `mvn test` — 33 pass, 0 new failures, 3 pre-existing errors acceptable
- [ ] Update task document with branch, commit, and status

---

# Technical Notes

### Why tenant_id Matters

These admin forms manage the metadata that controls the entire dynamic form engine. If a tenant-specific configuration record (e.g., a row filter scoped to a specific tenant) is viewed without visible tenant context, an administrator could:
- Mistakenly modify another tenant's configuration
- Fail to recognize a mis-scoped config until runtime data leaks occur
- Lose audit traceability for who changed what in which tenant

Displaying `tenant_id` as read-only on every admin form is a **defense-in-depth** safeguard — it makes tenant ownership visible at all times, even when the system manages it automatically.

### Flyway Version

Use `V18` (next after V17). Verify no conflicts with PRD-003 migrations.

### Column Registration vs Form Field

The `tenant_id` must be registered in **both** `sys_table_columns` (so the runtime engine knows the column exists and its type) AND `sys_form_fields` (so it appears on the form). V15 registered table columns; V16/V17 registered form fields. This enhancement adds both where missing.

### admin_table_definition — Already Compliant

V16 already includes tenant_id on admin_table_definition (position 8, read_only=true). Do NOT add a duplicate. Verify with `NOT EXISTS` guard.

---

# Files Expected

- `backend/src/main/resources/db/migration/V18__add_tenant_id_to_admin_forms.sql` (new)
- `ai/changes/CHANGE-ENH-002.md` (new — change summary)

---

# Files Modified

None (new file only; no existing files changed)

---

# Effort Estimation

| Activity | Hours |
|----------|:---:|
| Write V18 migration | 1.0 |
| Verify idempotency & build | 0.5 |
| Create change summary | 0.5 |
| **Total** | **2.0** |

---

# Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|:---:|:---:|-----------|
| Position collision with existing columns | Low | Medium | Use `NOT EXISTS` guard; verify positions against V15/V16/V17 |
| Duplicate tenant_id on admin_table_definition | Low | Low | `NOT EXISTS` guard on form field INSERT |
| Flyway version conflict with PRD-003 | Low | High | Verify next available version before creating file |
| VIEW-backed forms (admin_field_rule, admin_field_validation) — tenant_id comes from base table, not view | Low | Low | tenant_id exists on sys_form_field_rules and sys_form_field_validations base tables; view inherits it. Register tenant_id on base table model, not view model. |

**Note on VIEW-backed forms:** The `admin_field_rule` and `admin_field_validation` forms use VIEW models (`v_admin_field_rules`, `v_admin_field_validations`). However, `tenant_id` is a column on the underlying base tables (`sys_form_field_rules`, `sys_form_field_validations`) and is included in the VIEW via `SELECT ... ffr.* ...`. The column registration for tenant_id should be on the **base table** model (`sys_form_field_rules`, `sys_form_field_validations`), not the view models, since the view models only expose user-meaningful columns. The form field tenant_id INSERT references the form, not the table — so it should work correctly through the VIEW.

---

# Related Documents

- [PRD-002 v1.1.0 — Admin Configuration Forms](../prd/PRD-002-admin-configuration-forms.md)
- [TASK-034 — Seed Core Admin Forms](../tasks/TASK-034-seed-core-admin-forms.md)
- [TASK-035 — Seed Remaining Admin Forms](../tasks/TASK-035-seed-remaining-admin-forms.md)
- [TEST-TASK-034 — QA Report (REQ-ISSUE-001)](../tests/TEST-TASK-034.md)
- [TEST-TASK-035 — QA Report (REQ-ISSUE-001)](../tests/TEST-TASK-035.md)
