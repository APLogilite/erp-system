-- ============================================================
-- PRD-003 Data Verification (Reusable Regression Script)
-- Verifies V19-V20 (physical tables) + V25-V27 (registrations) applied correctly
-- Usage: psql -U erp_user -h localhost -d erp_db -f ai/scripts/verify-prd-003-data.sql
-- ============================================================

\echo '========================================'
\echo 'PRD-003: ERP Order Flow (New Schema)'
\echo '========================================'

-- Part A: Master Data Tables in sys_table (table_type = 'master_data')
\echo ''
\echo '--- A: Master Data Tables (table_type = master_data) ---'
SELECT t.name, t.label, t.plural_label, t.table_name
FROM sys_table t
WHERE t.table_type = 'master_data'
ORDER BY t.name;

\echo 'Expected: 5 master data tables (md_business_partner, md_product, md_uom, md_uom_conversion, md_warehouse)'

\echo ''
\echo '--- A2: Master Data Table Columns ---'
SELECT t.name AS table_name, count(c.id) AS registered_columns
FROM sys_table t
JOIN sys_column c ON c.table_id = t.id
WHERE t.table_type = 'master_data'
GROUP BY t.name
ORDER BY t.name;

\echo 'Expected: Column counts for each master data table'

-- Part B: Transaction Tables in sys_table (table_type = 'transaction')
\echo ''
\echo '--- B: Transaction Tables (table_type = transaction) ---'
SELECT t.name, t.label, t.plural_label, t.table_name
FROM sys_table t
WHERE t.table_type = 'transaction'
ORDER BY t.name;

\echo 'Expected: 7 transaction tables (tx_order, tx_order_line, tx_invoice, tx_invoice_line, tx_payment, tx_shipment, tx_shipment_line)'

\echo ''
\echo '--- B2: Transaction Table Columns ---'
SELECT t.name AS table_name, count(c.id) AS registered_columns
FROM sys_table t
JOIN sys_column c ON c.table_id = t.id
WHERE t.table_type = 'transaction'
GROUP BY t.name
ORDER BY t.name;

\echo 'Expected: Column counts for each transaction table'

-- Part C: Master Data Windows (from V27)
\echo ''
\echo '--- C: Master Data Windows ---'
SELECT w.name, t.name AS table_name, w.description
FROM sys_window w
JOIN sys_table t ON w.table_id = t.id
WHERE t.table_type = 'master_data'
ORDER BY w.name;

\echo 'Expected: 4 master data windows (Business Partners, Products, UOM, Warehouses)'

\echo ''
\echo '--- C2: Master Data Window Tabs ---'
SELECT w.name AS window_name, tab.name AS tab_name,
       st.label AS table_label, tab.seq_no, tab.parent_column
FROM sys_tab tab
JOIN sys_window w ON tab.window_id = w.id
JOIN sys_table st ON tab.table_id = st.id
WHERE w.name IN ('Business Partners', 'Products', 'UOM', 'Warehouses')
ORDER BY w.name, tab.seq_no;

\echo ''
\echo '--- C3: Master Data Window Field Counts ---'
SELECT w.name AS window_name, count(wf.id) AS field_count
FROM sys_window_field wf
JOIN sys_tab t ON wf.tab_id = t.id
JOIN sys_window w ON t.window_id = w.id
WHERE w.name IN ('Business Partners', 'Products', 'UOM', 'Warehouses')
GROUP BY w.name
ORDER BY w.name;

\echo 'Expected: Business Partners=7, Products=6, UOM=2, Warehouses=3'

-- Part D: Transaction Windows (from V27)
\echo ''
\echo '--- D: Transaction Windows ---'
SELECT w.name, t.name AS table_name, w.description
FROM sys_window w
JOIN sys_table t ON w.table_id = t.id
WHERE t.table_type = 'transaction'
ORDER BY w.name;

\echo 'Expected: 6 transaction windows (Sales Orders, Purchase Orders, Sales Invoices, Purchase Invoices, Payments, Shipments)'

\echo ''
\echo '--- D2: Transaction Window Tabs ---'
SELECT w.name AS window_name, tab.name AS tab_name,
       st.label AS table_label, tab.seq_no, tab.parent_column
FROM sys_tab tab
JOIN sys_window w ON tab.window_id = w.id
JOIN sys_table st ON tab.table_id = st.id
WHERE w.name IN ('Sales Orders', 'Purchase Orders', 'Sales Invoices',
                 'Purchase Invoices', 'Payments', 'Shipments')
ORDER BY w.name, tab.seq_no;

\echo 'Expected: Header tabs at seq 10, child tabs (Lines, Shipments) at seq 20/30'

\echo ''
\echo '--- D3: Transaction Window Field Counts ---'
SELECT w.name AS window_name, count(wf.id) AS field_count
FROM sys_window_field wf
JOIN sys_tab t ON wf.tab_id = t.id
JOIN sys_window w ON t.window_id = w.id
WHERE w.name IN ('Sales Orders', 'Purchase Orders', 'Sales Invoices',
                 'Purchase Invoices', 'Payments', 'Shipments')
GROUP BY w.name
ORDER BY w.name;

\echo ''
\echo '--- E: Physical Table Existence ---'
SELECT tablename AS "Physical Business Tables"
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('md_business_partner','md_product','md_uom','md_uom_conversion','md_warehouse',
                    'tx_order','tx_order_line','tx_invoice','tx_invoice_line',
                    'tx_payment','tx_shipment','tx_shipment_line')
ORDER BY tablename;

\echo 'Expected: 12 physical tables (5 md_* + 7 tx_*)'

\echo ''
\echo '========================================'
\echo 'PRD-003 (NEW SCHEMA) SUMMARY'
\echo '========================================'
SELECT
  (SELECT count(*) FROM sys_table WHERE table_type = 'master_data') AS master_data_tables,
  (SELECT count(*) FROM sys_table WHERE table_type = 'transaction') AS transaction_tables,
  (SELECT count(*) FROM sys_window w JOIN sys_table t ON w.table_id = t.id WHERE t.table_type = 'master_data') AS master_data_windows,
  (SELECT count(*) FROM sys_window w JOIN sys_table t ON w.table_id = t.id WHERE t.table_type = 'transaction') AS transaction_windows,
  (SELECT count(*) FROM sys_column c JOIN sys_table t ON c.table_id = t.id WHERE t.table_type IN ('master_data', 'transaction')) AS total_registered_columns;

\echo ''
\echo 'Expected: master_data_tables=5, transaction_tables=7,'
\echo '         master_data_windows=4, transaction_windows=6'
