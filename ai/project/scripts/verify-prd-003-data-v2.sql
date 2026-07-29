-- ============================================================
-- PRD-003 Data Verification v2 (Reusable Regression Script)
-- Verifies ERP window/table/sample-data registrations against
-- the CURRENT schema generation (Flyway V1–V8; sys_table rows
-- all use table_type 'static').
-- Supersedes verify-prd-003-data.sql (2026-07-28).
-- Usage: psql -U postgres -h localhost -d erp_db -f ai/project/scripts/verify-prd-003-data-v2.sql
-- ============================================================

\echo '========================================'
\echo 'PRD-003 v2: ERP Order Flow (V1-V8 schema)'
\echo '========================================'

\echo ''
\echo '--- A: Registered Tables in sys_table (all table_type = static) ---'
SELECT table_type, count(*) FROM sys_table GROUP BY table_type;

\echo 'Expected: static = 18 (7 sys_* + 4 md_* + 7 tx_*)'

\echo ''
\echo '--- A2: Registered Business Tables ---'
SELECT name, label, table_type FROM sys_table
WHERE name LIKE 'md\_%' OR name LIKE 'tx\_%'
ORDER BY name;

\echo 'Expected: 4 md_* (business_partner, product, uom, warehouse) + 7 tx_*'
\echo '          (invoice, invoice_line, order, order_line, payment, shipment, shipment_line)'

\echo ''
\echo '--- B: ERP Windows ---'
SELECT w.name, t.name AS primary_table
FROM sys_window w JOIN sys_table t ON w.table_id = t.id
WHERE w.name NOT IN ('Table Definitions', 'Window Definitions', 'Menu Configuration')
ORDER BY w.name;

\echo 'Expected: 10 ERP windows (4 master data + 6 transaction)'

\echo ''
\echo '--- C: Transaction Window Tabs (Lines must be LINKED) ---'
SELECT w.name AS window_name, t.name AS tab_name, t.seq_no,
       CASE WHEN t.parent_link_column_id IS NOT NULL THEN 'LINKED' ELSE '-' END AS parent_link,
       c.code AS link_column, c.relation_table
FROM sys_tab t
JOIN sys_window w ON t.window_id = w.id
LEFT JOIN sys_column c ON c.id = t.parent_link_column_id
WHERE w.name IN ('Sales Orders', 'Purchase Orders', 'Sales Invoices',
                 'Purchase Invoices', 'Payments', 'Shipments')
ORDER BY w.name, t.seq_no;

\echo 'Expected: header tabs seq 10 unlinked; Lines tabs seq 20 LINKED'
\echo '          (order_id>tx_order, invoice_id>tx_invoice, shipment_id>tx_shipment)'

\echo ''
\echo '--- D: Window Field Counts ---'
SELECT w.name AS window_name, count(wf.id) AS field_count
FROM sys_window w
JOIN sys_tab t ON t.window_id = w.id
JOIN sys_window_field wf ON wf.tab_id = t.id AND wf.is_active = true
WHERE w.name NOT IN ('Table Definitions', 'Window Definitions', 'Menu Configuration')
GROUP BY w.name
ORDER BY w.name;

\echo 'Expected: Business Partners=6, Payments=8, Products=6, Purchase Invoices=12,'
\echo '          Purchase Orders=17, Sales Invoices=12, Sales Orders=17, Shipments=11, UOM=2, Warehouses=2'

\echo ''
\echo '--- E: Physical Business Tables ---'
SELECT tablename AS "Physical Business Tables"
FROM pg_tables
WHERE schemaname = 'public' AND (tablename LIKE 'md\_%' OR tablename LIKE 'tx\_%')
ORDER BY tablename;

\echo 'Expected: 12 physical tables (5 md_* incl. md_uom_conversion + 7 tx_*)'

\echo ''
\echo '--- F: Sample Data (V6 seed) ---'
SELECT (SELECT count(*) FROM md_business_partner) AS partners,
       (SELECT count(*) FROM md_product) AS products,
       (SELECT count(*) FROM md_uom) AS uoms,
       (SELECT count(*) FROM md_warehouse) AS warehouses,
       (SELECT count(*) FROM tx_order) AS orders,
       (SELECT count(*) FROM tx_order_line) AS order_lines,
       (SELECT count(*) FROM tx_invoice) AS invoices,
       (SELECT count(*) FROM tx_invoice_line) AS invoice_lines,
       (SELECT count(*) FROM tx_shipment) AS shipments,
       (SELECT count(*) FROM tx_shipment_line) AS shipment_lines,
       (SELECT count(*) FROM tx_payment) AS payments;

\echo 'Expected: partners=4, products=6, orders=4, order_lines=8, invoices=2,'
\echo '          invoice_lines=4, shipments=2, shipment_lines=4, payments=2 (all > 0)'

\echo ''
\echo '--- G: FK Column Metadata for Child Tabs (BUG-013 / V8) ---'
SELECT t.name AS table_name, c.code, c.relation_table
FROM sys_column c JOIN sys_table t ON t.id = c.table_id
WHERE c.code IN ('order_id', 'invoice_id', 'shipment_id')
  AND t.name IN ('tx_order_line', 'tx_invoice_line', 'tx_shipment_line')
ORDER BY t.name;

\echo 'Expected: tx_invoice_line.invoice_id>tx_invoice, tx_order_line.order_id>tx_order,'
\echo '          tx_shipment_line.shipment_id>tx_shipment'

\echo ''
\echo '========================================'
\echo 'PRD-003 v2 SUMMARY'
\echo '========================================'
SELECT
  (SELECT count(*) FROM sys_table WHERE name LIKE 'md\_%') AS md_tables,
  (SELECT count(*) FROM sys_table WHERE name LIKE 'tx\_%') AS tx_tables,
  (SELECT count(*) FROM sys_window w JOIN sys_table t ON w.table_id = t.id WHERE t.name LIKE 'md\_%') AS master_windows,
  (SELECT count(*) FROM sys_window w JOIN sys_table t ON w.table_id = t.id WHERE t.name LIKE 'tx\_%') AS transaction_windows,
  (SELECT count(*) FROM sys_tab t JOIN sys_window w ON w.id = t.window_id WHERE t.parent_link_column_id IS NOT NULL AND w.name NOT IN ('Table Definitions', 'Window Definitions', 'Menu Configuration')) AS erp_child_tabs_linked;

\echo 'Expected: md_tables=4, tx_tables=7, master_windows=4, transaction_windows=6, erp_child_tabs_linked=5'
