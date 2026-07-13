-- ============================================================
-- PRD-003 Data Verification (Reusable Regression Script)
-- Verifies V19-V23 Flyway migrations applied correctly
-- Usage: psql -U erp_user -h localhost -d erp_db -f ai/scripts/verify-prd-003-data.sql
-- ============================================================

\echo '========================================'
\echo 'PRD-003: ERP Order Flow — Transaction Forms'
\echo '========================================'

-- Part A: V19 — Master Data Tables
\echo ''
\echo '--- A: V19 — Master Data Tables ---'
SELECT table_name, name AS model_name, label
FROM sys_metadata_models
WHERE name IN ('md_business_partner','md_product','md_uom','md_uom_conversion','md_warehouse')
ORDER BY name;

\echo 'Expected: 5 master data tables'

\echo ''
SELECT m.name AS table_name, count(tc.code) AS registered_columns
FROM sys_metadata_models m
JOIN sys_table_columns tc ON tc.table_id = m.id
WHERE m.name IN ('md_business_partner','md_product','md_uom','md_uom_conversion','md_warehouse')
GROUP BY m.name ORDER BY m.name;

\echo 'Expected: 7,7,2,4,3 columns respectively (23 total)'

-- Part B: V20 — Transaction Tables
\echo ''
\echo '--- B: V20 — Transaction Tables ---'
SELECT table_name, name AS model_name, label
FROM sys_metadata_models
WHERE name LIKE 'tx_%'
ORDER BY name;

\echo 'Expected: 9 transaction tables (tx_order, tx_order_line, tx_invoice, tx_invoice_line, tx_payment, tx_shipment, tx_shipment_line, tx_material_receipt, tx_mr_line)'

-- Part C: V21 — Master Data Forms
\echo ''
\echo '--- C: V21 — Master Data Forms ---'
SELECT name, model_name, type, scope
FROM sys_metadata_views
WHERE name IN ('business_partner','product','uom','uom_conversion','warehouse')
ORDER BY name;

\echo 'Expected: 5 master data forms'

\echo ''
SELECT v.name AS form_name, count(f.id) AS field_count
FROM sys_metadata_views v
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name IN ('business_partner','product','uom','uom_conversion','warehouse')
GROUP BY v.name ORDER BY v.name;

\echo 'Expected: 7,7,2,4,3 fields respectively (23 total)'

-- Part D: V22 — Transaction Header Forms
\echo ''
\echo '--- D: V22 — Transaction Header Forms ---'
SELECT name, model_name, where_clause_field, where_clause_value
FROM sys_metadata_views
WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice',
               'purchase_payment','sales_payment','purchase_shipment','sales_shipment',
               'material_receipt')
ORDER BY name;

\echo 'Expected: 9 forms with correct where_clause values'

-- Verify material_receipt has NO where_clause
\echo ''
SELECT name, where_clause_field IS NULL AS no_where_clause
FROM sys_metadata_views
WHERE name = 'material_receipt';

\echo 'Expected: no_where_clause = t (true)'

-- Part E: V23 — Line Forms + Sub-Form Configs
\echo ''
\echo '--- E: V23 — Line Forms + Sub-Form Configs ---'
SELECT name, model_name
FROM sys_metadata_views
WHERE name IN ('order_line','invoice_line','shipment_line','mr_line')
ORDER BY name;

\echo 'Expected: 4 line forms'

\echo ''
SELECT child_form_code, parent_form_name, relation_code
FROM (
  SELECT sub.child_form_code, p.name AS parent_form_name, sub.relation_code
  FROM sys_form_sub_forms sub
  JOIN sys_metadata_views p ON p.id = sub.parent_form_id
) subq
ORDER BY child_form_code, parent_form_name;

\echo 'Expected: 7 sub-form links (order_line used by both purchase_order and sales_order)'
