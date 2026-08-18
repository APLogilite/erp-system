---
id: CHANGE-TASK-066
task_id: TASK-066
parent_prd: PRD-006
type: Feature
status: IMPLEMENTED
related_files:
  - backend/.../V14__seed_prd006_forms.sql
---

# Summary
V14: seeded 4 windows (Document Sequences, Price Lists, Discount Rules, Sales Quotations) with tabs, fields, sys_admin access, and menu entries. Price Lists and Sales Quotations have child-tab Lines wired with parent_link_column_id.

# Validation
- mvn compile OK
