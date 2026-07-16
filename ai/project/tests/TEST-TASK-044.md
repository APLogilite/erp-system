---
id: TEST-TASK-044
task_id: TASK-044
parent_prd: PRD-004
test_date: 2026-07-14
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, H2 test DB)
build_commit_tested: 2448c33 (prd/PRD-004-window-hierarchy-menu)
test_scope: Structural verification of V27__seed_erp_windows.sql — ERP windows, tabs, fields
status: PASSED
---

# Test Report — TASK-044: Seed Data — ERP Windows

## Test Cases
- TC-001: V27 migration file exists ✅
- TC-002: 4 Master Data windows: Business Partners, Products, UOM, Warehouses ✅
- TC-003: 6 Transaction windows: Sales Orders, Purchase Orders, Sales Invoices, Purchase Invoices, Payments, Shipments ✅
- TC-004: Sales Orders has where_clause = order_type='sales' ✅
- TC-005: Purchase Orders has where_clause = order_type='purchase' ✅
- TC-006: Sales Invoices has where_clause = invoice_type='sales' ✅
- TC-007: Purchase Invoices has where_clause = invoice_type='purchase' ✅
- TC-008: Lines child tabs use parent_column = order_id or invoice_id ✅
- TC-009: Sales Orders has Shipments child tab with shipment_type='outbound' ✅
- TC-010: Master data windows have correct fields with proper ordering ✅
- TC-011: Transaction header tabs have proper field order (order_number, order_date, partner_id, etc.) ✅
- TC-012: Idempotency (WHERE NOT EXISTS) ✅
- TC-013: Helper functions create_window, add_child_tab, ensure_field used ✅
- TC-014: All 36 backend tests pass ✅

## Test Summary
| Metric | Value |
|--------|-------|
| Total Tests | 14 |
| Passed | 14 |
| Failed | 0 |
| Bugs Created | 0 |
