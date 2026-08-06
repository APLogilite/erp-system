---
id: CHANGE-TASK-062
task_id: TASK-062
parent_prd: PRD-006
branch: feature/TASK-062
type: Feature
status: IMPLEMENTED
duration: 1 hour
related_files:
  - backend/src/main/resources/db/migration/V13__create_discount_table.sql
---

# Summary
Created md_discount_rule table via V13. Columns registered with enum dropdown for discount_type. 2 sample rules seeded (10% volume, $50 bulk).

# Validation
- V13 applied, mvn test 36/36 BUILD SUCCESS
