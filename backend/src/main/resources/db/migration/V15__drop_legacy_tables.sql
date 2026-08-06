-- ============================================================
-- V15 — Drop legacy hardcoded sales/order tables (PRD-006 TASK-069)
-- ============================================================
DROP TABLE IF EXISTS sales_orders CASCADE;
DROP TABLE IF EXISTS sales_order_lines CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS order_lines CASCADE;
