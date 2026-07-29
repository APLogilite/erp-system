-- ============================================================
-- PRD-002 Data Verification v2 (Reusable Regression Script)
-- Verifies admin configuration windows against the CURRENT
-- schema generation (Flyway V1–V8, window names:
-- Table Definitions / Window Definitions / Menu Configuration).
-- Supersedes verify-prd-002-data.sql (2026-07-28).
-- Usage: psql -U postgres -h localhost -d erp_db -f ai/project/scripts/verify-prd-002-data-v2.sql
-- ============================================================

\echo '========================================'
\echo 'PRD-002 v2: Admin Configuration Forms (V1-V8 schema)'
\echo '========================================'

\echo ''
\echo '--- A: Admin Windows in sys_window ---'
SELECT w.name, t.name AS table_name, w.description
FROM sys_window w
JOIN sys_table t ON w.table_id = t.id
WHERE w.name IN ('Table Definitions', 'Window Definitions', 'Menu Configuration')
ORDER BY w.name;

\echo 'Expected: 3 admin windows (Menu Configuration, Table Definitions, Window Definitions)'

\echo ''
\echo '--- B: Admin Window Tabs (child tabs must have parent_link_column_id) ---'
SELECT w.name AS window_name, t.name AS tab_name, t.seq_no,
       st.label AS table_label,
       CASE WHEN t.parent_link_column_id IS NOT NULL THEN 'LINKED' ELSE '-' END AS parent_link,
       c.code AS link_column, c.relation_table
FROM sys_tab t
JOIN sys_window w ON t.window_id = w.id
JOIN sys_table st ON t.table_id = st.id
LEFT JOIN sys_column c ON c.id = t.parent_link_column_id
WHERE w.name IN ('Table Definitions', 'Window Definitions', 'Menu Configuration')
ORDER BY w.name, t.seq_no;

\echo 'Expected: Table Definitions = Tables(10,-), Columns(20,LINKED table_id>sys_table);'
\echo '          Window Definitions = Windows(10,-), Fields(15,LINKED tab_id>sys_tab),'
\echo '                               Tabs(20,LINKED window_id>sys_window), Access(30,LINKED window_id>sys_window);'
\echo '          Menu Configuration = Menu(10,-)'

\echo ''
\echo '--- C: Admin Window Field Counts ---'
SELECT w.name AS window_name, t.name AS tab_name, count(wf.id) AS field_count
FROM sys_tab t
JOIN sys_window w ON t.window_id = w.id
LEFT JOIN sys_window_field wf ON wf.tab_id = t.id AND wf.is_active = true
WHERE w.name IN ('Table Definitions', 'Window Definitions', 'Menu Configuration')
GROUP BY w.name, t.name, t.seq_no
ORDER BY w.name, t.seq_no;

\echo 'Expected: Menu/Menu=7, Table Definitions Tables=7 Columns=9,'
\echo '          Window Definitions Windows=3 Fields=8 Tabs=7 Access=1'

\echo ''
\echo '--- D: Window Access Entries ---'
SELECT w.name AS window_name, count(wa.id) AS access_entries
FROM sys_window w
LEFT JOIN sys_window_access wa ON wa.window_id = w.id
WHERE w.name IN ('Table Definitions', 'Window Definitions', 'Menu Configuration')
GROUP BY w.name
ORDER BY w.name;

\echo 'Expected: 3 admin windows with 1 access entry each'

\echo ''
\echo '--- E: Window Definitions Child-Tab Wiring (BUG-013 reference resolution) ---'
SELECT pt.name AS parent_tab, ct.name AS child_tab, c.code AS link_column, c.relation_table
FROM sys_tab ct
JOIN sys_tab pt ON pt.window_id = ct.window_id
JOIN sys_column c ON c.id = ct.parent_link_column_id AND c.relation_table = (SELECT name FROM sys_table WHERE id = pt.table_id)
JOIN sys_window w ON w.id = ct.window_id
WHERE w.name = 'Window Definitions' AND ct.id != pt.id
ORDER BY pt.seq_no, ct.seq_no;

\echo 'Expected: Windows>Tabs, Windows>Access, Tabs>Fields (grandchild)'

\echo ''
\echo '--- F: Flyway Migration History ---'
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

\echo 'Expected: V1..V8 — all success=true (V7 rename parent column, V8 seed fk columns)'

\echo ''
\echo '========================================'
\echo 'PRD-002 v2 SUMMARY'
\echo '========================================'
SELECT
  (SELECT count(*) FROM sys_window WHERE name IN ('Table Definitions', 'Window Definitions', 'Menu Configuration')) AS admin_windows,
  (SELECT count(*) FROM sys_tab t JOIN sys_window w ON t.window_id = w.id WHERE w.name IN ('Table Definitions', 'Window Definitions', 'Menu Configuration')) AS admin_tabs,
  (SELECT count(*) FROM sys_window_field wf JOIN sys_tab t ON wf.tab_id = t.id JOIN sys_window w ON t.window_id = w.id WHERE w.name IN ('Table Definitions', 'Window Definitions', 'Menu Configuration') AND wf.is_active = true) AS admin_fields,
  (SELECT count(*) FROM sys_tab t JOIN sys_window w ON t.window_id = w.id WHERE w.name IN ('Table Definitions', 'Window Definitions', 'Menu Configuration') AND t.parent_link_column_id IS NOT NULL) AS linked_child_tabs,
  (SELECT max(installed_rank) FROM flyway_schema_history) AS flyway_max_rank;

\echo 'Expected: admin_windows=3, admin_tabs=7, admin_fields=42, linked_child_tabs=4, flyway_max_rank=8'
