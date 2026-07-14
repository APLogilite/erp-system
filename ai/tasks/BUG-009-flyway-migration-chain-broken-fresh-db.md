---
id: BUG-009

title: Flyway migration chain broken on fresh database — old metadata FK reference fails

status: IN_DEVELOPMENT

priority: Critical

severity: Critical

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: bugfix/BUG-009

locked: true

created: 2026-07-14

updated: 2026-07-14

started: 2026-07-14

completed:

parent_prd: PRD-004

parent_task: TASK-036

reported_by: User

detected_in: Flyway migration startup (fresh PostgreSQL DB)

related_test:

fix_summary:

verification_report:

history:
  - 2026-07-14 — Product Manager — Created. V3 fails on fresh DB (FK to non-existent sys_metadata_models).

---

# Summary

When `spring.flyway.enabled=true` on a **fresh database**, Flyway fails at **V3__create_sys_table_columns.sql** with:
```
ERROR: relation "sys_metadata_models" does not exist
Position: 101
```

The root cause: V3 creates `sys_table_columns` with `REFERENCES sys_metadata_models(id)`, but no migration in the chain ever creates `sys_metadata_models` — it was originally expected to be created by JPA `ddl-auto=update` (which is now disabled for Flyway-based setups).

Additionally, the old V1–V23 migration chain was designed to work **with** JPA `ddl-auto=update` creating the base tables first. Since PRD-004 now replaces the entire metadata schema via V24–V28, the old metadata-scaffolding migrations (V3–V23) are partially obsolete and interleaved with essential business table creation.

---

# Problem

The migration chain has three layers of issues:

## 1. FK Reference to Non-Existent Table (V3 — BLOCKING)

```sql
CREATE TABLE IF NOT EXISTS sys_table_columns (
    ...
    table_id UUID NOT NULL REFERENCES sys_metadata_models(id) ON DELETE CASCADE,
    ...
);
```

`sys_metadata_models` was never created by any Flyway migration — it was expected to exist from JPA `ddl-auto=update`. On a fresh DB, this FK constraint causes Flyway to fail.

## 2. Old Metadata Schema Is Scaffolding for Obsolete Migrations

V4–V13 create/alter old metadata tables (`sys_metadata_models`, `sys_metadata_views`, `sys_form_fields`, etc.). V14–V18 seed admin form data into these tables. V19–V20 create business tables AND register them in old metadata. V21–V23 seed business form metadata.

**V24 drops ALL these old metadata tables and creates the new schema from scratch.** Every old migration that creates, alters, or seeds the old metadata tables is scaffolding that V24 undoes.

## 3. Business Table DDL Interleaved with Old Metadata Registration

V19 and V20:
- **Part 1**: Create actual business tables (`md_business_partner`, `md_product`, `tx_order`, etc.) — ESSENTIAL, must keep
- **Part 3-4**: Register those tables in OLD `sys_metadata_models` and `sys_table_columns` — OBSOLETE (V24 drops these tables, V25 re-registers in new schema)

This means V19–V20 cannot be kept as-is on a fresh DB because the metadata registration parts reference `sys_metadata_models` which doesn't exist.

---

# Expected Behaviour

Running `spring.flyway.enabled=true` on a **fresh PostgreSQL database** should:
1. Create identity tables (tenants, orgs, users, roles, permissions — currently in V1–V2)
2. Create business tables (`md_*`, `tx_*` — currently in V19–V20)
3. Create new metadata schema (`sys_table`, `sys_column`, `sys_window`, etc. — currently in V24)
4. Seed all metadata (business tables, admin windows, ERP windows, menu — currently in V25–V28)
5. Start successfully with no errors

---

# Actual Behaviour

On a fresh DB with `spring.flyway.enabled=true`:
1. V1 ✅ — Identity schema creates
2. V2 ✅ — Audit tables create
3. **V3 ❌** — `ERROR: relation "sys_metadata_models" does not exist`
4. Flyway aborts, application fails to start

---

# Steps To Reproduce

1. Create a fresh PostgreSQL database
2. Set `spring.flyway.enabled=true` in `application.properties`
3. Start the application (`mvn spring-boot:run`)
4. Observe: Flyway fails at V3 with FK error

---

# Root Cause

(To be filled by Software Engineer)

The old V1–V23 migration chain was designed assuming JPA `ddl-auto=update` would create base tables first. Since PRD-004 now replaces all old metadata (V24 drops old schema, V25–V28 seed new schema), but the old migrations still run first (V3 is before V24 in version order), the chain breaks.

---

# Fix

(To be filled by Software Engineer)

**Suggested approach — Create a consolidated migration chain:**

The current files (38 files: V1–V28 + U3–U13) need to be restructured so a fresh DB works cleanly. The recommended strategy:

### Keep these migrations (essential base tables):
| File | Content | Why Keep |
|------|---------|----------|
| V1__init_identity_schema.sql | `identity_tenants`, `identity_orgs`, `identity_users`, `identity_roles`, `identity_permissions`, etc. | Core auth entities |
| V2__identity_audit_events.sql | `identity_audit_records` | Audit trail |

### Remove or absorb these (old metadata scaffolding — superseded by V24):
| File | Reason |
|------|--------|
| V3__create_sys_table_columns.sql | FK to non-existent table; schema dropped by V24 |
| V4__alter_sys_metadata_models.sql | References old table dropped by V24 |
| V5__alter_sys_metadata_views.sql | References old table dropped by V24 |
| V6__create_sys_form_fields.sql | Obsolete schema, dropped by V24 |
| V7__create_sys_form_field_rules.sql | Obsolete schema, dropped by V24 |
| V8__create_sys_form_field_validations.sql | Obsolete schema, dropped by V24 |
| V9__create_sys_form_layout_sections.sql | Obsolete schema, dropped by V24 |
| V10__create_sys_form_section_fields.sql | Obsolete schema, dropped by V24 |
| V11__create_sys_form_role_filters.sql | Obsolete schema, dropped by V24 |
| V12__create_sys_form_sub_forms.sql | Obsolete schema, dropped by V24 |
| V13__create_sys_form_tenant_role.sql | Obsolete schema, dropped by V24 |
| V14__alter_sys_metadata_versions.sql | References old schema |
| V15__register_metadata_tables_static.sql | Seeds OLD metadata — superseded by V25–V28 |
| V16__seed_core_admin_forms.sql | Seeds OLD admin forms — superseded by V26 |
| V17__seed_remaining_admin_forms.sql | Seeds OLD admin forms — superseded by V26 |
| V18__add_tenant_id_to_admin_forms.sql | Modifies OLD admin forms — superseded by V26 |
| V21__seed_master_data_forms.sql | Seeds OLD metadata — superseded by V27 |
| V22__seed_transaction_header_forms.sql | Seeds OLD metadata — superseded by V27 |
| V23__seed_line_forms_and_sub_forms.sql | Seeds OLD metadata — superseded by V27 |

### Modify these (keep business DDL, remove old metadata registration):
| File | Action |
|------|--------|
| V19__seed_master_data_tables.sql | Keep Part 1 (CREATE TABLE md_*), Part 2 (indexes). Remove Parts 3–4 (INSERT INTO sys_metadata_models / sys_table_columns). These business tables are still needed. |
| V20__seed_transaction_tables.sql | Keep Part 1 (CREATE TABLE tx_*), Part 2 (indexes). Remove Parts 5–6 (INSERT INTO sys_metadata_models / sys_table_columns). These transaction tables are still needed. |

### Keep these as-is (new schema — PRD-004):
| File | Content |
|------|---------|
| V24__drop_old_metadata_create_new_schema.sql | Drop old metadata, create new schema (now safe since old scaffolding migrations are removed) |
| V25__register_business_tables.sql | Register business tables in new schema |
| V26__seed_admin_windows.sql | Seed admin windows |
| V27__seed_erp_windows.sql | Seed ERP windows |
| V28__seed_menu_and_access.sql | Seed menu tree + access |

### Undo scripts
The U3–U13 undo scripts reference the same old schema. They should be removed alongside their corresponding V files.

### Alternative approach — Consolidate into a single V1__init.sql
Instead of removing individual files, the SE could create a single `V1__init.sql` that contains ALL current table definitions (identity + business + metadata), then remove V1–V28 and replace with just the init file. This is cleaner but more work.

### Also remove:
The old `U*.sql` undo scripts (U3–U13) that reference the old schema — they are no longer relevant.

---

# Validation

(To be filled by QA Engineer)

After fix:
- [ ] `spring.flyway.enabled=true` on a **fresh PostgreSQL database** starts successfully
- [ ] `V1__init_identity_schema.sql` creates identity tables (tenants, orgs, users, roles)
- [ ] Business tables exist (`md_business_partner`, `md_product`, `md_uom`, `md_warehouse`, `tx_order`, `tx_order_line`, `tx_invoice`, etc.)
- [ ] New metadata schema tables exist (`sys_table`, `sys_column`, `sys_window`, `sys_tab`, `sys_window_field`, `sys_window_access`, `sys_menu`)
- [ ] Menu tree is seeded (Administration, Master Data, Transactions groups)
- [ ] Window access is configured for sys_admin
- [ ] Sidebar shows the hierarchical menu
- [ ] All 36 backend tests pass
- [ ] Frontend typecheck passes
- [ ] Existing development database is not broken (if upgrading, already-applied migrations in flyway_schema_history are respected)

---

# Files Changed

(To be filled by Software Engineer)

Likely files (in `backend/src/main/resources/db/migration/`):
- V3–V18: Removed or consolidated
- V19–V20: Modified (remove old metadata registration sections)
- V21–V23: Removed or consolidated
- V24–V28: Renumbered or kept as-is
- U3–U13: Removed
- New `V1__init.sql` or similar baseline if consolidation approach is chosen

---

# Related Documents

- PRD-004: Window Hierarchy & Menu System v1.0.0
- BUG-007: PRD-004 Flyway not enabled (same root cause for runtime issues)
- BUG-008: Ctrl+K search not updated
- AGENTS.md: "Flyway is disabled by default (spring.flyway.enabled=false); JPA ddl-auto=update handles schema"
