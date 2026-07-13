---
task_id: TASK-036
type: Database
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-036
base_branch: prd/PRD-004-window-hierarchy-menu
status: READY_FOR_TEST
created: 2026-07-13
author: software_engineer
---

# Change Report — TASK-036

## Summary

Created the new iDempiere-inspired metadata schema via Flyway migration V24, replacing the old PRD-001 schema entirely. This is the foundation for the Window Hierarchy & Menu System.

## Files Added

| File | Description |
|------|-------------|
| `backend/src/main/resources/db/migration/V24__drop_old_metadata_create_new_schema.sql` | Flyway migration: drops 11 old metadata tables, creates 7 new ones |

## Files Modified

None

## Files Removed

None

## Database Changes

### Tables Dropped (11 old metadata tables)

| Old Table | Replacement |
|-----------|-------------|
| `sys_metadata_models` | → `sys_table` |
| `sys_table_columns` | → `sys_column` |
| `sys_metadata_views` | → `sys_window` |
| `sys_form_sub_forms` | → `sys_tab` |
| `sys_form_fields` | → `sys_window_field` |
| `sys_form_tenant_role` | → `sys_window_access` |
| `sys_form_field_rules` | — (logic moved inline on fields) |
| `sys_form_field_validations` | — (logic moved inline on fields) |
| `sys_form_layout_sections` | — (field ordering by seq_no) |
| `sys_form_section_fields` | — (field ordering by seq_no) |
| `sys_form_role_filters` | — (where_clause on tab level) |

### Tables Created (7 new metadata tables)

| Table | Layer | Description |
|-------|-------|-------------|
| `sys_table` | 1 — Database Schema | Table definitions (FK target for windows/tabs) |
| `sys_column` | 1 — Database Schema | Column definitions with type info |
| `sys_window` | 2 — Window Design | Window definitions (top-level form concept) |
| `sys_tab` | 2 — Window Design | Tab definitions within windows |
| `sys_window_field` | 2 — Window Design | Field mapping columns to positions on tabs |
| `sys_window_access` | 2 — Window Design | Role-based window access control |
| `sys_menu` | 3 — Menu System | Hierarchical menu entries (NEW) |

### Key Schema Details

- All tables include BaseEntity columns (id, created_at, updated_at, created_by, updated_by, is_active, deleted_at)
- `sys_table` adds `tenant_id` for multi-tenancy
- Foreign keys: `sys_column.table_id → sys_table.id`, `sys_window.table_id → sys_table.id`, `sys_tab.window_id → sys_window.id`, `sys_tab.table_id → sys_table.id`, `sys_window_field.tab_id → sys_tab.id`, `sys_window_field.column_id → sys_column.id`, `sys_window_access.window_id → sys_window.id`, `sys_menu.parent_id → sys_menu.id`, `sys_menu.window_id → sys_window.id`
- Unique constraints on `sys_table.name`, `sys_column(table_id, code)`, `sys_tab(window_id, seq_no)`, `sys_window_field(tab_id, seq_no)`, `sys_window_field(tab_id, column_id)`, `sys_window_access(window_id, tenant_id, role_id)`
- Indexes on all FK columns and `is_active` columns for query performance

## API Changes

None (this is a pure database migration — API changes come in TASK-037, TASK-038, TASK-039)

## Configuration Changes

- No application config changes needed
- Migration runs when `spring.flyway.enabled=true`

## Dependencies Added/Updated

None

## Breaking Changes

- **COMPLETE SCHEMA REPLACEMENT**: All old `sys_*` metadata tables are dropped
- Any code referencing old table names (`sys_metadata_models`, `sys_form_fields`, etc.) will break
- The old JPA entities matching the old schema will no longer work
- Old `IdentitySeedData` and `SeedData` classes that reference old schema are not affected (they use `identity_*` and `md_*`/`tx_*` tables)

## Validation Results

| Check | Result |
|-------|--------|
| `mvn clean compile` | PASS |
| `mvn test` (36 tests) | ALL PASS |

## Known Limitations

- The migration is designed for development environments with no production data
- Rollback scripts for the old tables are available as existing `U*` migrations
- TASK-037 must create JPA entities before any Java code can interact with these tables

## Follow-up Recommendations

- TASK-037 (JPA entities) should be started immediately after this task merges
- All subsequent PRD-004 tasks depend on this schema being available
