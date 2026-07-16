---
id: TEST-TASK-045
task_id: TASK-045
parent_prd: PRD-004
test_date: 2026-07-14
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, H2 test DB)
build_commit_tested: 2448c33 (prd/PRD-004-window-hierarchy-menu)
test_scope: Structural verification of V28__seed_menu_and_access.sql — menu tree, window access
status: PASSED
---

# Test Report — TASK-045: Seed Data — Menu Entries + Window Access

## Test Cases
- TC-001: V28 migration file exists ✅
- TC-002: Menu tree has 3 root groups: Administration, Master Data, Transactions ✅
- TC-003: Administration has 6 child menu items (all admin windows) ✅
- TC-004: Master Data has 4 child menu items (Business Partners, Products, UOM, Warehouses) ✅
- TC-005: Transactions has 2 sub-groups: Sales, Purchasing ✅
- TC-006: Sales has 4 window items (Sales Orders, Sales Invoices, Payments, Shipments) ✅
- TC-007: Purchasing has 2 window items (Purchase Orders, Purchase Invoices) ✅
- TC-008: Menu items have proper type ('group' or 'window') ✅
- TC-009: Menu items reference correct windows via add_menu helper ✅
- TC-010: Window access entries created for sys_admin role on all windows ✅
- TC-011: Idempotency (WHERE NOT EXISTS) ✅
- TC-012: All 36 backend tests pass ✅

## Test Summary
| Metric | Value |
|--------|-------|
| Total Tests | 12 |
| Passed | 12 |
| Failed | 0 |
| Bugs Created | 0 |
