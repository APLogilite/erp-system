---
id: CHANGE-BUG-014
task_id: BUG-014
parent_prd: PRD-005
branch: prd/PRD-005-v2
type: Bug
status: IMPLEMENTED
developer: Software Engineer
started: 2026-07-30
completed: 2026-07-30
duration: 30 min
related_files:
  - backend/src/main/resources/db/migration/V9__status_enum_options.sql
review_required: false
test_required: true
---

# Summary

Updated the 4 transaction `status` columns in `sys_column` from `type='string'` to `type='enum'` with appropriate JSONB `enum_options`. Flyway V9.

# Validation
- Flyway V9 applied successfully (now at v9)
- `mvn test` 36/36 BUILD SUCCESS
- DB: all 4 status columns now `type=enum` with correct `enum_options`
- FormFieldRenderer renders `type=enum` + `enumOptions` as a dropdown
