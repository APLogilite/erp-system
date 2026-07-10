-- ============================================================
-- PRD-002 Data Verification (Reusable Regression Script)
-- Verifies V15-V18 Flyway migrations applied correctly
-- Usage: psql -U erp_user -h localhost -d erp_db -f ai/scripts/verify-prd-002-data.sql
-- ============================================================

\echo '========================================'
\echo 'PRD-002: Admin Configuration Forms'
\echo '========================================'

-- Part A: V15 — Static Table Registrations
\echo ''
\echo '--- A: V15 — Static Table Registrations ---'
SELECT name, label, table_type, table_name
FROM sys_metadata_models
WHERE table_type = 'static' AND name LIKE 'sys_%'
ORDER BY name;

\echo ''
\echo 'Expected: 11 tables (sys_metadata_models through sys_form_role_filters)'

SELECT m.name AS table_name, count(tc.code) AS registered_columns
FROM sys_metadata_models m
JOIN sys_table_columns tc ON tc.table_id = m.id
WHERE m.table_type = 'static' AND m.name LIKE 'sys_%'
GROUP BY m.name ORDER BY m.name;

\echo 'Expected: 7-13 columns per table (includes tenant_id from V18)'

-- Part B: V16-V17 — Admin Form Definitions
\echo ''
\echo '--- B: V16-V17 — Admin Form Definitions ---'
SELECT name, model_name, type, scope, description
FROM sys_metadata_views
WHERE name LIKE 'admin_%'
ORDER BY name;

\echo 'Expected: 11 admin forms'

-- Part C: Form Field Counts
\echo ''
\echo '--- C: Form Field Counts ---'
SELECT v.name AS form, count(ff.id) AS fields
FROM sys_metadata_views v
JOIN sys_form_fields ff ON ff.form_id = v.id
WHERE v.name LIKE 'admin_%'
GROUP BY v.name ORDER BY v.name;

\echo 'Expected: admin_table_definition=8, admin_table_column=13, admin_form_definition=10,'
\echo '         admin_form_field=10, admin_field_rule=7, admin_field_validation=5,'
\echo '         admin_layout_section=6, admin_section_field=2, admin_sub_form_config=6,'
\echo '         admin_tenant_role_access=2, admin_row_filter=5'

-- Part D: ENH-002 — tenant_id on ALL forms
\echo ''
\echo '--- D: ENH-002 — tenant_id Presence (ALL forms must have it) ---'
SELECT v.name AS form,
  CASE WHEN bool_or(ff.column_code = 'tenant_id')
    THEN 'PASS' ELSE 'FAIL!' END AS has_tenant_id,
  MAX(CASE WHEN ff.column_code = 'tenant_id' THEN ff.position::text END) AS position,
  MAX(CASE WHEN ff.column_code = 'tenant_id' THEN
    CASE WHEN ff.read_only THEN 'YES' ELSE 'NO!' END END) AS read_only
FROM sys_metadata_views v
JOIN sys_form_fields ff ON ff.form_id = v.id
WHERE v.name LIKE 'admin_%'
GROUP BY v.name ORDER BY v.name;

\echo 'Expected: ALL rows show PASS for has_tenant_id'

-- Part E: Sub-Form Configs
\echo ''
\echo '--- E: Sub-Form Configs ---'
SELECT pv.name AS parent_form, sf.relation_code, sf.child_form_code,
       sf.label, sf.display_as, sf.position
FROM sys_form_sub_forms sf
JOIN sys_metadata_views pv ON pv.id = sf.parent_form_id
ORDER BY pv.name, sf.position;

\echo 'Expected: 5 sub-form links'
\echo '  admin_form_definition → Fields/Rules/Validations'
\echo '  admin_layout_section → Field Mappings'
\echo '  admin_table_definition → Columns'

-- Part F: Flyway Version Check
\echo ''
\echo '--- F: Flyway Migration History ---'
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

\echo 'Expected: version=14 (baseline), 15, 16, 17, 18 — all success=true'

-- Part G: Summary
\echo ''
\echo '========================================'
\echo 'PRD-002 SUMMARY'
\echo '========================================'
SELECT
  (SELECT count(*) FROM sys_metadata_models WHERE table_type='static' AND name LIKE 'sys_%') AS static_models,
  (SELECT count(*) FROM sys_metadata_views WHERE name LIKE 'admin_%') AS admin_forms,
  (SELECT count(*) FROM sys_form_fields) AS total_form_fields,
  (SELECT count(*) FROM sys_form_sub_forms) AS sub_form_configs,
  (SELECT max(installed_rank) FROM flyway_schema_history) AS flyway_max_rank;

\echo ''
\echo 'Expected: static_models=11, admin_forms=11,'
\echo '          total_form_fields=74, sub_form_configs=5,'
\echo '          flyway_max_rank=5 (baseline + V15-V18)'
