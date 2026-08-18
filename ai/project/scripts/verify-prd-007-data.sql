-- ============================================================
-- PRD-007 verification script
-- Run against the target PostgreSQL database after migrations.
-- ============================================================

-- TASK-070: Payment Term table and seed data
SELECT 'TASK-070: md_payment_term exists' AS check_name,
       EXISTS (SELECT 1 FROM information_schema.tables
               WHERE table_schema = 'public' AND table_name = 'md_payment_term') AS passed;

SELECT 'TASK-070: 5 standard payment terms seeded' AS check_name,
       (SELECT COUNT(*) FROM md_payment_term WHERE code IN ('NET0','NET15','NET30','NET60','NET90')) = 5 AS passed;

SELECT 'TASK-070: payment_term registered in sys_table' AS check_name,
       EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_payment_term') AS passed;

SELECT 'TASK-070: payment_term columns registered' AS check_name,
       (SELECT COUNT(*) FROM sys_column c JOIN sys_table t ON t.id = c.table_id
        WHERE t.name = 'md_payment_term'
          AND c.code IN ('code','name','days','discount_percent','discount_days')) = 5 AS passed;

SELECT 'TASK-070: Payment Terms window exists' AS check_name,
       EXISTS (SELECT 1 FROM sys_window WHERE name = 'Payment Terms') AS passed;

SELECT 'TASK-070: Payment Terms menu exists' AS check_name,
       EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Payment Terms') AS passed;
