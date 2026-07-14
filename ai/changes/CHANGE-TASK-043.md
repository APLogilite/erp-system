---
task_id: TASK-043
type: Database
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-043
status: READY_FOR_TEST
created: 2026-07-13
author: software_engineer
---

# Change Report — TASK-043

## Summary

Created V26 Flyway migration that seeds 7 admin windows with tabs and fields for managing all metadata tables through the runtime UI.

## Files Added

`backend/src/main/resources/db/migration/V26__seed_admin_windows.sql`

## Key Data Seeded

- 7 sys_window entries (admin_table_definitions, admin_table_columns, admin_window_definitions, admin_window_tabs, admin_window_fields, admin_window_access, admin_menu_configuration)
- 11 sys_tab entries with proper parent-child relationships (Table → Columns, Window → Tabs → Access, Tab → Fields)
- 60+ sys_window_field entries with seq_no, is_same_line, is_displayed, is_readonly, is_mandatory settings
