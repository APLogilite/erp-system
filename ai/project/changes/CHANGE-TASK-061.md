---
id: CHANGE-TASK-061
task_id: TASK-061
parent_prd: PRD-006
branch: feature/TASK-061
type: Feature
status: IMPLEMENTED
developer: Software Engineer
started: 2026-08-05
completed: 2026-08-05
duration: 1 hour
related_files:
  - backend/src/main/resources/db/migration/V11__create_price_list_tables.sql
review_required: true
test_required: true
---

# Summary
Created `md_price_list` and `md_price_list_line` tables via V11 Flyway migration. Both registered in sys_table/sys_column. Default STANDARD price list seeded with 3 sample lines (Business Laptop $1200, Wireless Mouse $25, Consulting Service $150).

# Validation
- V11 applied, Flyway now at v11, 11 migrations validated
- DB: STANDARD price list created, 3 lines with correct prices
- `mvn test` 36/36 BUILD SUCCESS
