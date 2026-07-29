-- ============================================================
-- PRD-004 Schema Verification (Reusable Regression Script)
--
-- *** SUPERSEDED 2026-07-28 — DO NOT USE FOR REGRESSION ***
-- Stale expectations: (1) the "old metadata tables should NOT
-- exist" check contradicts the documented PRD-005 decision to
-- KEEP the PRD-001 legacy tables until the old designer UIs are
-- rebuilt (Hibernate recreates them from legacy entities);
-- (2) expected window/table counts predate the V4–V8 schema
-- generation (13 windows, 18 registered tables, not 17/12).
-- Kept for historical reference. Current version:
--   ai/project/scripts/verify-prd-004-schema-v2.sql
-- ============================================================
-- Usage: psql -U erp_user -h localhost -d erp_db -f ai/scripts/verify-prd-004-schema.sql
-- ============================================================

\echo '=== PRD-004: OLD Metadata Tables (should NOT exist) ==='
SELECT tablename AS "Dropped Old Tables"
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('sys_metadata_models','sys_table_columns','sys_metadata_views',
                    'sys_form_fields','sys_form_sub_forms','sys_form_tenant_role',
                    'sys_form_field_rules','sys_form_field_validations',
                    'sys_form_layout_sections','sys_form_section_fields',
                    'sys_form_role_filters')
ORDER BY tablename;
\echo 'Expected: 0 rows (all old tables dropped)'

\echo ''
\echo '=== PRD-004: NEW Metadata Tables ==='
SELECT tablename AS "New Tables"
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('sys_table','sys_column','sys_window','sys_tab',
                    'sys_window_field','sys_window_access','sys_menu')
ORDER BY tablename;
\echo 'Expected: 7 rows (all new metadata tables)'

\echo ''
\echo '=== PRD-004: sys_table Columns ==='
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'sys_table'
ORDER BY ordinal_position;
\echo 'Expected: id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at, created_by, updated_by, deleted_at'

\echo ''
\echo '=== PRD-004: sys_column Columns ==='
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'sys_column'
ORDER BY ordinal_position;
\echo 'Expected: id, table_id, code, label, type, required, default_value, max_length, precision, scale, relation_table, enum_options, position, is_active ...'

\echo ''
\echo '=== PRD-004: sys_window Columns ==='
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'sys_window'
ORDER BY ordinal_position;

\echo ''
\echo '=== PRD-004: sys_tab Columns ==='
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'sys_tab'
ORDER BY ordinal_position;
\echo 'Expected: id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_link_column_id ...'

\echo ''
\echo '=== PRD-004: sys_window_field Columns ==='
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'sys_window_field'
ORDER BY ordinal_position;

\echo ''
\echo '=== PRD-004: sys_window_access Columns ==='
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'sys_window_access'
ORDER BY ordinal_position;

\echo ''
\echo '=== PRD-004: sys_menu Columns ==='
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'sys_menu'
ORDER BY ordinal_position;

\echo ''
\echo '=== PRD-004: Foreign Key Constraints ==='
SELECT
    con.conname AS constraint_name,
    con.contype AS constraint_type,
    tbl.relname AS table_name,
    f_tbl.relname AS foreign_table_name
FROM pg_constraint con
JOIN pg_class tbl ON con.conrelid = tbl.oid
JOIN pg_class f_tbl ON con.confrelid = f_tbl.oid
WHERE tbl.relname IN ('sys_table','sys_column','sys_window','sys_tab',
                      'sys_window_field','sys_window_access','sys_menu')
  AND con.contype = 'f'
ORDER BY tbl.relname, con.conname;
\echo 'Expected: sys_column→sys_table, sys_window→sys_table, sys_tab→sys_window, sys_tab→sys_table, sys_window_field→sys_tab, sys_window_field→sys_column, sys_window_access→sys_window, sys_menu→sys_menu (self-ref), sys_menu→sys_window'

\echo ''
\echo '=== PRD-004: Unique Constraints ==='
SELECT
    con.conname AS constraint_name,
    tbl.relname AS table_name
FROM pg_constraint con
JOIN pg_class tbl ON con.conrelid = tbl.oid
WHERE tbl.relname IN ('sys_table','sys_column','sys_window','sys_tab',
                      'sys_window_field','sys_window_access','sys_menu')
  AND con.contype = 'u'
ORDER BY tbl.relname, con.conname;

\echo ''
\echo '=== PRD-004: Indexes on FK Columns ==='
SELECT
    tablename AS table_name,
    indexname AS index_name
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN ('sys_table','sys_column','sys_window','sys_tab',
                    'sys_window_field','sys_window_access','sys_menu')
  AND indexname LIKE 'idx_%'
ORDER BY tablename, indexname;

\echo ''
\echo '=== PRD-004: Registered Business Tables (V25) ==='
SELECT t.name AS table_name, t.label, t.plural_label, t.table_type, t.table_name AS physical_table
FROM sys_table t
ORDER BY t.name;
\echo 'Expected: 12 business tables (5 md_* + 7 tx_*)'

\echo ''
\echo '=== PRD-004: Registered Windows (V26-V27) ==='
SELECT w.name AS window_name, t.label AS primary_table
FROM sys_window w
JOIN sys_table t ON w.table_id = t.id
ORDER BY w.name;
\echo 'Expected: 7 admin windows + 10 ERP windows = 17 windows'

\echo ''
\echo '=== PRD-004: Menu Entries (V28) ==='
SELECT m.name, m.type, m.seq_no, m2.name AS parent_name
FROM sys_menu m
LEFT JOIN sys_menu m2 ON m.parent_id = m2.id
ORDER BY COALESCE(m2.seq_no, 0), m.seq_no;
\echo 'Expected: hierarchical menu tree with Administration, Master Data, Transactions groups'

\echo ''
\echo '=== PRD-004: Window Access (V28) ==='
SELECT w.name AS window_name, COUNT(wa.id) AS access_entries
FROM sys_window_access wa
JOIN sys_window w ON wa.window_id = w.id
GROUP BY w.name
ORDER BY w.name;
\echo 'Expected: 17 windows with at least 1 access entry each'
