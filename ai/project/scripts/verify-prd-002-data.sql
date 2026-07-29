-- ============================================================
-- PRD-002 Data Verification (Reusable Regression Script)
--
-- *** SUPERSEDED 2026-07-28 — DO NOT USE FOR REGRESSION ***
-- This script targets the historical V24–V29 schema generation
-- (admin windows named 'sys_table'/'sys_window'/'sys_menu',
-- Flyway versions V24–V29). The current schema uses window names
-- 'Table Definitions'/'Window Definitions'/'Menu Configuration'
-- and Flyway V1–V8, so most checks return 0 rows.
-- Kept for historical reference. Current version:
--   ai/project/scripts/verify-prd-002-data-v2.sql
-- ============================================================
-- Verifies admin form data in the new V24-V29 schema
-- Usage: psql -U erp_user -h localhost -d erp_db -f ai/scripts/verify-prd-002-data.sql
-- ============================================================

\echo '========================================'
\echo 'PRD-002: Admin Configuration Forms (New Schema)'
\echo '========================================'

-- Part A: Admin Windows Registered in sys_window
\echo ''
\echo '--- A: Admin Windows in sys_window ---'
SELECT w.name, t.name AS table_name, w.description
FROM sys_window w
JOIN sys_table t ON w.table_id = t.id
WHERE w.name IN ('sys_table', 'sys_window', 'sys_menu')
ORDER BY w.name;

\echo 'Expected: 3 admin windows (sys_table, sys_window, sys_menu)'

\echo ''
\echo '--- B: Window Tabs in sys_tab ---'
SELECT w.name AS window_name, t.name AS tab_name,
       st.label AS table_label, t.seq_no, t.is_single_row,
       t.parent_link_column_id, t.where_clause
FROM sys_tab t
JOIN sys_window w ON t.window_id = w.id
JOIN sys_table st ON t.table_id = st.id
WHERE w.name IN ('sys_table', 'sys_window', 'sys_menu')
ORDER BY w.name, t.seq_no;

\echo 'Expected: sys_table has 2 tabs (Tables seq 10, Columns seq 20),'
\echo '         sys_window has 5+ tabs (Windows seq 10, Tabs seq 20, Fields seq 30, Access seq 40 plus child tabs),'
\echo '         sys_menu has 1+ tabs'

\echo ''
\echo '--- C: Window Fields in sys_window_field ---'
SELECT w.name AS window_name,
       t.seq_no AS tab_seq,
       count(wf.id) AS field_count
FROM sys_window_field wf
JOIN sys_tab t ON wf.tab_id = t.id
JOIN sys_window w ON t.window_id = w.id
WHERE w.name IN ('sys_table', 'sys_window', 'sys_menu')
GROUP BY w.name, t.seq_no
ORDER BY w.name, t.seq_no;

\echo 'Expected: Fields count per tab for each admin window'

\echo ''
\echo '--- D: Window Access in sys_window_access ---'
SELECT w.name AS window_name, wa.role_id, wa.is_active
FROM sys_window_access wa
JOIN sys_window w ON wa.window_id = w.id
WHERE w.name IN ('sys_table', 'sys_window', 'sys_menu')
ORDER BY w.name;

\echo 'Expected: 3 admin windows with access entries for system admin role'

\echo ''
\echo '--- E: V29 Consolidation — No stale windows remain ---'
SELECT name AS stale_admin_window
FROM sys_window
WHERE name IN ('admin_table_definitions', 'admin_table_columns',
               'admin_window_definitions', 'admin_window_tabs',
               'admin_window_fields', 'admin_window_access',
               'admin_menu_configuration', 'admin')
ORDER BY name;

\echo 'Expected: 0 rows (all old windows cleaned up by V29)'

\echo ''
\echo '--- F: Flyway Migration History ---'
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

\echo 'Expected: V24, V25, V26, V27, V28, V29 — all success=true'

\echo ''
\echo '========================================'
\echo 'PRD-002 (NEW SCHEMA) SUMMARY'
\echo '========================================'
SELECT
  (SELECT count(*) FROM sys_window WHERE name IN ('sys_table', 'sys_window', 'sys_menu')) AS admin_windows,
  (SELECT count(*) FROM sys_tab t JOIN sys_window w ON t.window_id = w.id WHERE w.name IN ('sys_table', 'sys_window', 'sys_menu')) AS admin_tabs,
  (SELECT count(*) FROM sys_window_field wf JOIN sys_tab t ON wf.tab_id = t.id JOIN sys_window w ON t.window_id = w.id WHERE w.name IN ('sys_table', 'sys_window', 'sys_menu')) AS admin_fields,
  (SELECT count(*) FROM sys_window_access wa JOIN sys_window w ON wa.window_id = w.id WHERE w.name IN ('sys_table', 'sys_window', 'sys_menu')) AS admin_access_entries,
  (SELECT max(installed_rank) FROM flyway_schema_history) AS flyway_max_rank;

\echo ''
\echo 'Expected: admin_windows=3, all with tabs, fields, and access entries'
