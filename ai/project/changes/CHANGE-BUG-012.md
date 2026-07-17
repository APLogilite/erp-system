---
id: CHANGE-BUG-012

task_id: BUG-012

parent_prd: PRD-005

parent_task: TASK-047

branch: bugfix/BUG-012

type: Bug

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

related_commits:
  - fix(BUG-012): lookup dropdowns now respect filter_where_clause — skip eager loading when filter exists, fall back to dynamic API

related_files:
  - backend/.../WindowDefinitionAssemblyService.java
  - frontend/.../WindowPage.tsx

review_required: true

test_required: true

---

# Summary

Fixed BUG-012 where reference/many2one dropdowns ignored field-level and column-level `filter_where_clause`. Two changes made:

1. **Backend**: `WindowDefinitionAssemblyService.assembleField()` now checks for `filter_where_clause` (field-level from `sys_window_field` or column-level from `sys_column`). If a filter exists, eager loading of `lookupOptions` is skipped (left null). Also removed the hardcoded `is_active = true` condition from the lookup query since some tables may not have this column.

2. **Frontend**: `WindowPage.tsx` re-added drill context and fallback dynamic lookup fetching via `fetchLookupRecords()` for fields where `lookupOptions` is null but `relationTable` is set. The many2one rendering now merges both sources: eager-loaded options + dynamically fetched results.

This ensures simple lookups (no filter) are still eagerly loaded for performance, while filtered lookups fall back to the dynamic API that correctly applies `filter_where_clause` with `@tab.field@` placeholder resolution.
