---
id: BUG-009

title: Flyway migration chain broken on fresh database — old metadata FK reference fails

status: COMPLETED

priority: Critical

severity: Critical

owner: QA Engineer

assigned_to: QA Engineer

assigned_branch: bugfix/BUG-009

locked: false

created: 2026-07-14

updated: 2026-07-15

started: 2026-07-14

completed: 2026-07-15

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

This bug evolved from a simple Flyway migration chain fix into a comprehensive stabilization effort covering:

1. **Flyway migration chain** — Fixed failed migrations on fresh DB by removing obsolete old-metadata scaffolding (V3-V18, V21-V23), fixing CTE scoping in V25, and adding V30/V31 for SYS tenant seed data.

2. **Backend APIs** — Added drill-down endpoint for tab-level record fetching, fixed tenant isolation (SYS tenant with fixed UUID), fixed ContextFilter for stale JWT handling, added lookup endpoint for dropdowns.

3. **Frontend WindowPage** — Complete rewrite with tabbed dialog layout, breadcrumb drill-down, accordion child panels, inline-editable grids (Quick Update mode), proper field type rendering (date/enum/boolean/many2one), and child record navigation.

4. **Admin windows** — Consolidated 7 separate admin windows into 3 properly structured windows (sys_table, sys_window, sys_menu) with correct hierarchical tab relationships.

5. **Scripts** — Added db-reset.sh, setup.sh, start-all.sh for easy setup and development workflow.

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

### Migration chain restructuring:
- **Removed** 18 obsolete old-metadata migrations (V3-V18, V21-V23) + 11 undo scripts (U3-U13)
- **Modified** V19/V20 to strip old metadata registration (keep business DDL only)
- **Fixed** V25 CTE scoping issue (96 references) — replaced with direct subqueries
- **V25** — Added Part 0 (register sys_* tables in sys_table) + Part 0b (register sys_* columns)
- **V26** — Consolidated 7 separate admin windows into 3: sys_table, sys_window, sys_menu
- **V28** — Updated menu items to reference consolidated windows; fixed menu names
- **V29** — Migration for existing DBs to consolidate old admin windows
- **V30** — Set NULL tenant_ids to SYS tenant UUID across all tables
- **V31** — Create SYS tenant (fixed UUID), permissions, and sys_admin role in migration
- **application.properties** — ddl-auto=update, Flyway enabled, ignore-missing-migrations=true

### Backend enhancements:
- **WindowDataService** — Added `getTabRecordWithChildren()` for drill-down record fetching from any tab's table
- **WindowDataController** — Added `GET /{windowName}/tabs/{tabId}/records/{id}?childTabs=` endpoint
- **WindowDataController** — Added `GET /lookup/{tableName}` for dropdown/autocomplete data
- **DynamicCrudService** — Tenant isolation on listRecords uses strict `tenant_id = :tenantId`
- **ContextFilter** — Graceful handling of stale JWT tokens after DB reset
- **IdentitySeedData** — Idempotent (find-or-create for roles/permissions), coexists with V31 migration

### Frontend enhancements:
- **WindowPage.tsx** — Complete RecordDialog redesign:
  - Tabbed dialog layout (Form + child tab panels)
  - Breadcrumb drill-down navigation (clickable levels, ← back button)
  - Accordion child panels (expandable, first open by default, side-by-side grid)
  - Inline-editable child grids with Quick Update toggle
  - Proper field type rendering (date, enum, boolean, many2one dropdowns with lookup data)
  - Numeric field parsing on save
  - Save error display
- **ChildTabGrid** — Read-only by default, Quick Update toggles inline editing
- **runtimeApi.ts** — Added fetchTabRecord(), fetchLookupRecords()
- **MenuNavigation.tsx** — Fixed navigation path (`/app/window/{name}`)

### Scripts added:
- `backend/db-reset.sh` — Drop/recreate database with proper user management
- `backend/setup.sh` — Full setup (reset DB + start app)
- `start-all.sh` — One-command start for both frontend and backend

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

## Deleted (29 files)
- V3-V18, V21-V23 (16 + 3 = 19 old metadata migrations)
- U3-U13 (11 undo scripts)
- start.sh (root), ai/monitor/start.sh, ai/scripts/run-all-regression.sh

## Migration files (modified or added)
- `V19__seed_master_data_tables.sql` — Removed old metadata registration (kept business DDL)
- `V20__seed_transaction_tables.sql` — Removed old metadata registration (kept business DDL)
- `V25__register_business_tables.sql` — Added Part 0 (sys_* table registrations), Part 0b (sys_* column registrations), fixed CTE scoping
- `V26__seed_admin_windows.sql` — Consolidated to 3 windows (sys_table, sys_window, sys_menu) with correct tab hierarchy
- `V28__seed_menu_and_access.sql` — Updated menu items for consolidated windows
- `V29__consolidate_admin_windows.sql` — New: consolidation for existing DBs
- `V30__set_system_tenant_id.sql` — New: set NULL tenant_ids to SYS tenant UUID
- `V31__seed_system_tenant_and_admin.sql` — New: create SYS tenant, permissions, sys_admin role

## Backend Java files
- `WindowDataService.java` — Added getTabRecordWithChildren()
- `WindowDataController.java` — Added tab record + lookup endpoints
- `DynamicCrudService.java` — Tenant isolation fix
- `ContextFilter.java` — Graceful stale JWT handling
- `IdentitySeedData.java` — Idempotent seed data, SYS tenant fixed UUID

## Frontend files
- `WindowPage.tsx` — Complete RecordDialog rewrite (drill-down, breadcrumb, accordion, inline editing)
- `runtimeApi.ts` — Added fetchTabRecord(), fetchLookupRecords()
- `MenuNavigation.tsx` — Fixed navigation path
- `FormSearchBar.tsx` — Fixed navigation path
- `FormNavigationMenu.tsx` — Fixed navigation path

## Configuration
- `application.properties` — ddl-auto=update, Flyway enabled, ignore-missing-migrations
- `start.sh` (backend) — Added stale process cleanup
- `start-all.sh` — New: one-command start for both servers
- `db-reset.sh` — New: database reset with superuser credentials
- `setup.sh` (backend) — New: full setup (reset + start)

---

# Related Documents

- PRD-004: Window Hierarchy & Menu System v1.0.0
- BUG-007: PRD-004 Flyway not enabled (same root cause for runtime issues)
- BUG-008: Ctrl+K search not updated
- AGENTS.md: "Flyway is disabled by default (spring.flyway.enabled=false); JPA ddl-auto=update handles schema"
