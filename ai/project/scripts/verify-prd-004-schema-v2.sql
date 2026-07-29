-- ============================================================
-- PRD-004 Schema Verification v2 (Reusable Regression Script)
-- Verifies the window-schema (sys_*) structure against the
-- CURRENT schema generation (Flyway V1–V8, includes V7/V8
-- parent_link_column_id changes from BUG-013).
-- Supersedes verify-prd-004-schema.sql (2026-07-28).
-- Usage: psql -U postgres -h localhost -d erp_db -f ai/project/scripts/verify-prd-004-schema-v2.sql
-- ============================================================

\echo '========================================'
\echo 'PRD-004 v2: Window Schema Structure (V1-V8)'
\echo '========================================'

\echo ''
\echo '=== A: NEW Metadata Tables (must exist: 7) ==='
SELECT tablename AS "New Tables"
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('sys_table','sys_column','sys_window','sys_tab',
                    'sys_window_field','sys_window_access','sys_menu')
ORDER BY tablename;

\echo 'Expected: 7 rows'

\echo ''
\echo '=== B: LEGACY PRD-001 Tables (informational — retained by design) ==='
SELECT tablename AS "Legacy Tables (retained)"
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('sys_metadata_models','sys_table_columns','sys_metadata_views',
                    'sys_form_fields','sys_form_sub_forms','sys_form_tenant_role',
                    'sys_form_field_rules','sys_form_field_validations',
                    'sys_form_layout_sections','sys_form_section_fields','sys_form_role_filters')
ORDER BY tablename;

\echo 'Expected: 11 rows — LEGACY tables are INTENTIONALLY retained per PRD-005'
\echo '          (old form designer stays until admin designer UIs are rebuilt;'
\echo '           Hibernate recreates them from legacy entities on startup)'

\echo ''
\echo '=== C: sys_tab Columns (must include parent_link_column_id uuid) ==='
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'sys_tab'
ORDER BY ordinal_position;

\echo 'Expected: id, window_id, name, table_id, seq_no, is_single_row, where_clause,'
\echo '          parent_link_column_id (uuid, nullable), is_active, tenant_id, audit cols'
\echo '          (NO parent_column varchar — dropped by V7)'

\echo ''
\echo '=== D: Foreign Key Constraints on sys_* tables ==='
SELECT con.conname AS constraint_name, rel.relname AS table_name,
       frel.relname AS foreign_table_name
FROM pg_constraint con
JOIN pg_class rel ON rel.oid = con.conrelid
JOIN pg_class frel ON frel.oid = con.confrelid
JOIN pg_namespace n ON n.oid = rel.relnamespace
WHERE con.contype = 'f' AND n.nspname = 'public'
  AND rel.relname IN ('sys_table','sys_column','sys_window','sys_tab',
                      'sys_window_field','sys_window_access','sys_menu')
ORDER BY rel.relname, con.conname;

\echo 'Expected: sys_column>sys_table, sys_menu>sys_menu, sys_menu>sys_window,'
\echo '          sys_tab>sys_table, sys_tab>sys_window, sys_tab>sys_column (parent_link),'
\echo '          sys_window>sys_table, sys_window_access>sys_window,'
\echo '          sys_window_field>sys_column, sys_window_field>sys_tab'
\echo '          NOTE: sys_tab>sys_column appears TWICE (fk_sys_tab_parent_link_column from'
\echo '          V7 + sys_tab_parent_link_column_id_fkey auto-added by Hibernate) — known, harmless.'

\echo ''
\echo '=== E: Unique Constraints ==='
SELECT con.conname AS constraint_name, rel.relname AS table_name
FROM pg_constraint con
JOIN pg_class rel ON rel.oid = con.conrelid
JOIN pg_namespace n ON n.oid = rel.relnamespace
WHERE con.contype = 'u' AND n.nspname = 'public'
  AND rel.relname LIKE 'sys\_%'
ORDER BY rel.relname;

\echo 'Expected: sys_column_table_id_code_key, sys_table_name_key, sys_window_name_key,'
\echo '          sys_window_access_window_id_tenant_id_role_id_key'

\echo ''
\echo '=== F: Indexes on sys_* tables ==='
SELECT tablename, indexname FROM pg_indexes
WHERE schemaname = 'public' AND tablename LIKE 'sys\_%'
  AND indexname LIKE 'idx\_%'
ORDER BY tablename, indexname;

\echo 'Expected: idx_* indexes on name/FK/is_active columns incl. idx_sys_tab_parent_link'

\echo ''
\echo '=== G: Registered Windows + Access (V4/V5) ==='
SELECT w.name AS window_name, count(wa.id) AS access_entries
FROM sys_window w
LEFT JOIN sys_window_access wa ON wa.window_id = w.id
GROUP BY w.name
ORDER BY w.name;

\echo 'Expected: 13 windows (3 admin + 10 ERP), 1 access entry each'

\echo ''
\echo '=== H: Menu Tree (V4/V5) ==='
SELECT m.name, m.type, m.seq_no, p.name AS parent_name
FROM sys_menu m
LEFT JOIN sys_menu p ON m.parent_id = p.id
ORDER BY p.seq_no NULLS FIRST, m.seq_no;

\echo 'Expected: 18 entries — Administration/Master Data/Transactions groups + children'

\echo ''
\echo '========================================'
\echo 'PRD-004 v2 SUMMARY'
\echo '========================================'
SELECT
  (SELECT count(*) FROM pg_tables WHERE schemaname='public' AND tablename IN ('sys_table','sys_column','sys_window','sys_tab','sys_window_field','sys_window_access','sys_menu')) AS new_metadata_tables,
  (SELECT count(*) FROM information_schema.columns WHERE table_schema='public' AND table_name='sys_tab' AND column_name='parent_link_column_id' AND data_type='uuid') AS has_parent_link_column,
  (SELECT count(*) FROM information_schema.columns WHERE table_schema='public' AND table_name='sys_tab' AND column_name='parent_column') AS stale_parent_column,
  (SELECT count(*) FROM sys_window) AS windows,
  (SELECT count(*) FROM sys_menu) AS menu_entries,
  (SELECT max(installed_rank) FROM flyway_schema_history) AS flyway_max_rank;

\echo 'Expected: new_metadata_tables=7, has_parent_link_column=1, stale_parent_column=0,'
\echo '          windows=13, menu_entries=18, flyway_max_rank=8'
