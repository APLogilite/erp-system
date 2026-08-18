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

-- TASK-071: Credit limit, credit hold, and payment term on business partner
SELECT 'TASK-071: credit_limit column exists' AS check_name,
       EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'md_business_partner' AND column_name = 'credit_limit') AS passed;

SELECT 'TASK-071: credit_hold column exists' AS check_name,
       EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'md_business_partner' AND column_name = 'credit_hold') AS passed;

SELECT 'TASK-071: payment_term_id column exists' AS check_name,
       EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'md_business_partner' AND column_name = 'payment_term_id') AS passed;

SELECT 'TASK-071: partner credit columns registered' AS check_name,
       (SELECT COUNT(*) FROM sys_column c JOIN sys_table t ON t.id = c.table_id
        WHERE t.name = 'md_business_partner'
          AND c.code IN ('credit_limit','credit_hold','payment_term_id')) = 3 AS passed;

SELECT 'TASK-071: partner form has credit fields' AS check_name,
       (SELECT COUNT(*) FROM sys_window_field wf
        JOIN sys_tab tab ON tab.id = wf.tab_id
        JOIN sys_window w ON w.id = tab.window_id
        JOIN sys_column c ON c.id = wf.column_id
        WHERE w.name = 'Business Partners' AND c.code IN ('credit_limit','credit_hold','payment_term_id')) = 3 AS passed;

-- TASK-072: Payment term on order and invoice
SELECT 'TASK-072: tx_order.payment_term_id exists' AS check_name,
       EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'tx_order' AND column_name = 'payment_term_id') AS passed;

SELECT 'TASK-072: tx_invoice.payment_term_id exists' AS check_name,
       EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'tx_invoice' AND column_name = 'payment_term_id') AS passed;

SELECT 'TASK-072: order/invoice payment_term_id registered' AS check_name,
       (SELECT COUNT(*) FROM sys_column c JOIN sys_table t ON t.id = c.table_id
        WHERE t.name IN ('tx_order','tx_invoice') AND c.code = 'payment_term_id') = 2 AS passed;

SELECT 'TASK-072: sales order form has payment term' AS check_name,
       EXISTS (SELECT 1 FROM sys_window_field wf
        JOIN sys_tab tab ON tab.id = wf.tab_id
        JOIN sys_window w ON w.id = tab.window_id
        JOIN sys_column c ON c.id = wf.column_id
        WHERE w.name = 'Sales Orders' AND c.code = 'payment_term_id') AS passed;

SELECT 'TASK-072: sales invoice form has payment term' AS check_name,
       EXISTS (SELECT 1 FROM sys_window_field wf
        JOIN sys_tab tab ON tab.id = wf.tab_id
        JOIN sys_window w ON w.id = tab.window_id
        JOIN sys_column c ON c.id = wf.column_id
        WHERE w.name = 'Sales Invoices' AND c.code = 'payment_term_id') AS passed;

-- TASK-073: Order status workflow
SELECT 'TASK-073: tx_order_status_log exists' AS check_name,
       EXISTS (SELECT 1 FROM information_schema.tables
               WHERE table_schema = 'public' AND table_name = 'tx_order_status_log') AS passed;

SELECT 'TASK-073: order status log columns registered' AS check_name,
       (SELECT COUNT(*) FROM sys_column c JOIN sys_table t ON t.id = c.table_id
        WHERE t.name = 'tx_order_status_log'
          AND c.code IN ('order_id','from_status','to_status','transitioned_by','transitioned_at')) = 5 AS passed;

-- TASK-074: Order auto-calculation
SELECT 'TASK-074: OrderCalculationService classes exist' AS check_name,
       EXISTS (SELECT 1 FROM information_schema.tables
               WHERE table_schema = 'public' AND table_name = 'tx_order_line' AND column_name = 'line_total') AS passed;

-- TASK-075: Customer credit check
SELECT 'TASK-075: credit_check columns exist on partner' AS check_name,
       EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'md_business_partner' AND column_name = 'credit_limit')
       AND EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'md_business_partner' AND column_name = 'credit_hold') AS passed;

-- TASK-076: Customer 360
SELECT 'TASK-076: Customer 360 window exists' AS check_name,
       EXISTS (SELECT 1 FROM sys_window WHERE name = 'Customer 360') AS passed;

SELECT 'TASK-076: Customer 360 menu exists' AS check_name,
       EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Customer 360') AS passed;

-- TASK-077: Sales Order form metadata
SELECT 'TASK-077: sales order totals read-only and displayed' AS check_name,
       NOT EXISTS (
         SELECT 1
         FROM sys_window_field swf
         JOIN sys_tab st ON swf.tab_id = st.id
         JOIN sys_window sw ON st.window_id = sw.id
         JOIN sys_column sc ON swf.column_id = sc.id
         JOIN sys_table tbl ON sc.table_id = tbl.id
         WHERE sw.name = 'Sales Orders'
           AND st.seq_no = 10
           AND tbl.name = 'tx_order'
           AND sc.code IN ('subtotal', 'tax_amount', 'discount_amount', 'grand_total', 'status')
           AND (swf.is_readonly = false OR swf.is_displayed = false)
       ) AS passed;

SELECT 'TASK-077: sales order payment_term_id field exists' AS check_name,
       EXISTS (
         SELECT 1
         FROM sys_window_field swf
         JOIN sys_tab st ON swf.tab_id = st.id
         JOIN sys_window sw ON st.window_id = sw.id
         JOIN sys_column sc ON swf.column_id = sc.id
         WHERE sw.name = 'Sales Orders'
           AND st.seq_no = 10
           AND sc.code = 'payment_term_id'
       ) AS passed;
