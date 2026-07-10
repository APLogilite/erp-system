-- ============================================================
-- PRD-001 Schema Verification (Reusable Regression Script)
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
\echo '=== PRD-001: Metadata Tables (V3-V14) ==='
SELECT tablename AS "Metadata Tables"
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename LIKE 'sys_%'
ORDER BY tablename;

\echo 'Expected: 14 metadata tables'

\echo ''
\echo '=== PRD-001: Key Columns Check ==='
SELECT table_name, column_name, data_type
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN ('sys_metadata_models', 'sys_table_columns', 'sys_metadata_views',
                     'sys_form_fields', 'sys_form_field_rules', 'sys_form_field_validations')
ORDER BY table_name, ordinal_position;

\echo ''
\echo '=== PRD-001: VIEWs Check (V17 created) ==='
SELECT table_name AS "VIEWs"
FROM information_schema.views
WHERE table_schema = 'public'
  AND table_name LIKE 'v_admin_%';

\echo 'Expected: v_admin_field_rules, v_admin_field_validations'

\echo ''
\echo '=== PRD-001: Summary ==='
SELECT
  (SELECT count(*) FROM pg_tables WHERE schemaname='public' AND tablename LIKE 'identity_%') AS identity_tables,
  (SELECT count(*) FROM pg_tables WHERE schemaname='public' AND tablename LIKE 'sys_%') AS metadata_tables,
  (SELECT count(*) FROM information_schema.views WHERE table_schema='public' AND table_name LIKE 'v_admin_%') AS admin_views;
