-- ============================================================
-- PRD-001 Schema Verification (Reusable Regression Script)
-- Updated: V24-V29 new schema (sys_table/sys_column/sys_window/...)
-- Usage: psql -U erp_user -h localhost -d erp_db -f ai/scripts/verify-prd-001-schema.sql
-- ============================================================

\echo '=== PRD-001: Identity Tables (V1-V2) ==='
SELECT tablename AS "Identity Tables"
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename LIKE 'identity_%'
ORDER BY tablename;

\echo 'Expected: 16+ identity tables'

\echo ''
\echo '=== PRD-001: NEW Metadata Tables (V24) ==='
SELECT tablename AS "New Metadata Tables"
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('sys_table','sys_column','sys_window','sys_tab',
                    'sys_window_field','sys_window_access','sys_menu')
ORDER BY tablename;

\echo 'Expected: 7 metadata tables (sys_table, sys_column, sys_window, sys_tab, sys_window_field, sys_window_access, sys_menu)'

\echo ''
\echo '=== PRD-001: Column Counts per Table ==='
SELECT table_name, count(*) AS column_count
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN ('sys_table','sys_column','sys_window','sys_tab',
                     'sys_window_field','sys_window_access','sys_menu')
GROUP BY table_name
ORDER BY table_name;

\echo ''
\echo '=== PRD-001: Foreign Key Constraints ==='
SELECT
    con.conname AS constraint_name,
    tbl.relname AS table_name,
    f_tbl.relname AS foreign_table_name
FROM pg_constraint con
JOIN pg_class tbl ON con.conrelid = tbl.oid
JOIN pg_class f_tbl ON con.confrelid = f_tbl.oid
WHERE tbl.relname IN ('sys_table','sys_column','sys_window','sys_tab',
                      'sys_window_field','sys_window_access','sys_menu')
  AND con.contype = 'f'
ORDER BY tbl.relname, con.conname;

\echo 'Expected: sys_column→sys_table, sys_window→sys_table, sys_tab→sys_window, sys_tab→sys_table, sys_window_field→sys_tab, sys_window_field→sys_column, sys_window_access→sys_window, sys_menu→sys_menu, sys_menu→sys_window'

\echo ''
\echo '=== PRD-001: Registered Business Tables (V25) ==='
SELECT t.name AS table_name, t.label, t.table_type, t.table_name AS physical_table
FROM sys_table t
ORDER BY t.table_type, t.name;

\echo 'Expected: 7 sys_* metadata + 4 md_* master + 7 tx_* transaction = 18 tables'
\echo '          (md_uom_conversion exists physically but is not registered — it has no window)'

\echo ''
\echo '=== PRD-001: Registered Windows (V26-V27) ==='
SELECT w.name AS window_name, t.label AS primary_table
FROM sys_window w
JOIN sys_table t ON w.table_id = t.id
ORDER BY w.name;

\echo 'Expected: 3 admin windows (Table Definitions, Window Definitions, Menu Configuration) + 10 ERP windows = 13 windows'

\echo ''
\echo '=== PRD-001: Menu Entries (V28) ==='
SELECT m.name, m.type, m.seq_no, m2.name AS parent_name
FROM sys_menu m
LEFT JOIN sys_menu m2 ON m.parent_id = m2.id
ORDER BY COALESCE(m2.seq_no, 0), m.seq_no;

\echo 'Expected: hierarchical menu tree with Administration, Master Data, Transactions groups'

\echo ''
\echo '=== PRD-001: Window Access Entries (V28) ==='
SELECT w.name AS window_name, COUNT(wa.id) AS access_entries
FROM sys_window_access wa
JOIN sys_window w ON wa.window_id = w.id
GROUP BY w.name
ORDER BY w.name;

\echo 'Expected: 13 windows with at least 1 access entry each'

\echo ''
\echo '=== PRD-001: Summary ==='
SELECT
  (SELECT count(*) FROM pg_tables WHERE schemaname='public' AND tablename LIKE 'identity_%') AS identity_tables,
  (SELECT count(*) FROM pg_tables WHERE schemaname='public' AND tablename IN ('sys_table','sys_column','sys_window','sys_tab','sys_window_field','sys_window_access','sys_menu')) AS new_metadata_tables,
  (SELECT count(*) FROM sys_table) AS registered_tables,
  (SELECT count(*) FROM sys_window) AS windows,
  (SELECT count(*) FROM sys_menu) AS menu_entries;
