---
id: BUG-007

title: PRD-004 schema and seed data not applied — Flyway disabled

status: READY_FOR_DEV

priority: Critical

severity: Critical

owner: Software Engineer

assigned_to:

assigned_branch:

locked: false

created: 2026-07-14

updated: 2026-07-14

started:

completed:

parent_prd: PRD-004

parent_task: TASK-036

reported_by: User

detected_in: Runtime app (localhost)

related_test: TEST-TASK-036, TEST-TASK-040, TEST-TASK-045

fix_summary:

verification_report:

history:
  - 2026-07-14 — Product Manager — Created. Flyway disabled, PRD-004 changes never applied.

---

# Summary

Flyway migrations V24–V28 (PRD-004) were never applied because `spring.flyway.enabled=false`. The old PRD-001 metadata schema is still active. The new `sys_table`/`sys_column`/`sys_window`/`sys_tab`/`sys_window_field`/`sys_window_access`/`sys_menu` tables were never created (or are empty if created by Hibernate ddl-auto). The 11 old metadata tables were never dropped. Seed data for admin windows, ERP windows, menu tree, and window access was never inserted.

---

# Problem

All PRD-004 features depend on the V24–V28 Flyway migrations:

- **V24** — Drops 11 old metadata tables, creates 7 new schema tables
- **V25** — Registers 12 business tables (md_*, tx_*) as sys_table + sys_column entries
- **V26** — Seeds 7 admin windows with tabs/fields
- **V27** — Seeds 10 ERP windows with tabs/fields
- **V28** — Seeds menu tree and window access entries

None of these have run. The application is still running on the old PRD-001 metadata schema.

---

# Expected Behaviour

After enabling Flyway and applying V24–V28:
- Old metadata tables (`sys_metadata_models`, `sys_table_columns`, `sys_metadata_views`, `sys_form_fields`, `sys_form_sub_forms`, `sys_form_tenant_role`, `sys_form_field_rules`, `sys_form_field_validations`, `sys_form_layout_sections`, `sys_form_section_fields`, `sys_form_role_filters`) are dropped
- New tables (`sys_table`, `sys_column`, `sys_window`, `sys_tab`, `sys_window_field`, `sys_window_access`, `sys_menu`) are created with proper FK constraints
- Menu navigation in sidebar shows the seeded menu tree (Administration, Master Data, Transactions)
- Window access is configured for the sys_admin role

---

# Actual Behaviour

- **Sidebar shows no menu** — old `FormNavigationMenu` was replaced by `MenuNavigation`, but with no seed data, the menu API returns empty. The sidebar has no navigation.
- **Old tables still exist** — V24 never ran to drop them
- **Old form data still visible** — search shows old PRD-001 form names because the old schema is still intact

---

# Steps To Reproduce

1. Start the application with default config (`spring.flyway.enabled=false`)
2. Log in as sys_admin
3. Observe sidebar — no menu section visible
4. Open Ctrl+K search — shows old form names from PRD-001 schema
5. Check database — old `sys_metadata_models`, `sys_form_fields` etc. still exist

---

# Root Cause

(To be filled by Software Engineer)

`spring.flyway.enabled=false` in the application configuration prevents Flyway from running migrations V24–V28.

---

# Fix

(To be filled by Software Engineer)

---

# Validation

(To be filled by QA Engineer)

After fix:
- [ ] Sidebar shows the hierarchical menu (Administration, Master Data, Transactions)
- [ ] Old metadata tables are gone from the database
- [ ] New schema tables (sys_table, sys_column, sys_window, sys_tab, sys_window_field, sys_window_access, sys_menu) exist
- [ ] Menu items navigate to `/window/{windowName}` routes
- [ ] All 36 backend tests still pass
- [ ] Frontend typecheck passes

---

# Files Changed

(To be filled by Software Engineer)

---

# Related Documents

- PRD-004: Window Hierarchy & Menu System v1.0.0
- TASK-036: Create New Metadata Schema (V24)
- TASK-045: Seed Menu Entries + Window Access (V28)
