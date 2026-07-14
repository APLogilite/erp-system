---
id: BUG-009

title: Flyway migration chain broken on fresh database — old metadata FK reference fails

status: READY_FOR_TEST

priority: Critical

severity: Critical

owner: QA Engineer

assigned_to: Software Engineer

assigned_branch: bugfix/BUG-009

locked: false

created: 2026-07-14

updated: 2026-07-14

started: 2026-07-14

completed: 2026-07-14

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

The old V1–V23 migration chain was designed assuming JPA `ddl-auto=update` would create base tables (like `sys_metadata_models`) first. Since BUG-007 switched to `ddl-auto=validate` and enabled Flyway, the migrations run without JPA pre-creating tables. V3 (`sys_table_columns`) has `REFERENCES sys_metadata_models(id)` but no migration in the chain ever creates `sys_metadata_models` — it was exclusively created by JPA. On a fresh DB, this FK constraint causes Flyway to fail at V3.

Additionally, V3–V18 and V21–V23 all create/alter/seed the old PRD-001 metadata schema (11 tables), which is entirely dropped and replaced by V24. These migrations are obsolete scaffolding — they exist only to create schema that V24 immediately destroys.

---

# Fix

Removed all obsolete old-metadata migrations and their undo scripts. Stripped old metadata registration from business table migrations.

### Removed (old metadata scaffolding — 18 files):
**V3–V18, V21–V23**: All create/alter/seed the old PRD-001 metadata schema (`sys_metadata_models`, `sys_metadata_views`, `sys_form_fields`, etc.) which is entirely dropped by V24. These migrations are superseded by V24–V28 (the new Window/Menu schema).

### Removed (undo scripts — 11 files):
**U3–U13**: Undo scripts for the deleted migrations.

### Modified (kept business DDL, removed obsolescent metadata registration):

- **V19__seed_master_data_tables.sql**: Removed Parts 3–4 (INSERT into `sys_metadata_models` / `sys_table_columns`). Kept Parts 1–2 (CREATE TABLE for md_business_partner, md_product, md_uom, md_uom_conversion, md_warehouse + indexes). Business tables are re-registered in the new schema by V25.

- **V20__seed_transaction_tables.sql**: Removed Parts 5–6 (INSERT into `sys_metadata_models` / `sys_table_columns`). Kept Parts 1–4 (CREATE TABLE for tx_order, tx_invoice, tx_payment, tx_shipment, tx_material_receipt + line tables + indexes).

### Updated configuration:
**application.properties**: Commented out `baseline-on-migrate` / `baseline-version=24` by default. Fresh DBs now run the full chain (V1 → V2 → V19 → V20 → V24 → V25 → V26 → V27 → V28). Existing DBs with old flyway_schema_history should uncomment these to skip V1-V23.

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

## Deleted (29 files):
- V3__create_sys_table_columns.sql through V18__add_tenant_id_to_admin_forms.sql (16 old metadata migrations)
- V21__seed_master_data_forms.sql through V23__seed_line_forms_and_sub_forms.sql (3 old metadata seeding migrations)
- U3__create_sys_table_columns.sql through U13__create_sys_form_tenant_role.sql (11 undo scripts)

## Modified:
- `backend/src/main/resources/db/migration/V19__seed_master_data_tables.sql` — Removed Parts 3–4 (old metadata registration)
- `backend/src/main/resources/db/migration/V20__seed_transaction_tables.sql` — Removed Parts 5–6 (old metadata registration)
- `backend/src/main/resources/application.properties` — Commented out baseline config for fresh-DB default; updated comments

---

# Related Documents

- PRD-004: Window Hierarchy & Menu System v1.0.0
- BUG-007: PRD-004 Flyway not enabled (same root cause for runtime issues)
- BUG-008: Ctrl+K search not updated
- AGENTS.md: "Flyway is disabled by default (spring.flyway.enabled=false); JPA ddl-auto=update handles schema"
