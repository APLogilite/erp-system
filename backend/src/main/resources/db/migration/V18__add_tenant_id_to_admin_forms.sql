-- ============================================================
-- PRD-002 / ENH-002 — Add tenant_id to All Admin Forms
-- Closes REQ-ISSUE-001: 10 forms missing tenant_id field.
-- Registers tenant_id in sys_table_columns for all 11 metadata
-- tables + 2 view models, then adds tenant_id (read-only) as
-- a form field and section-field mapping on 10 forms.
-- admin_table_definition already has tenant_id (V16) — excluded.
-- ============================================================

-- ============================================================
-- Part 1 — Register tenant_id Column in sys_table_columns
-- ============================================================
-- For each metadata table and view model, add tenant_id as a
-- 'string' type column at the last position. Uses AND NOT EXISTS
-- for independent idempotency (no DELETE cleanup needed).
-- ============================================================

-- -------------------------------------------------------------------
-- sys_metadata_models (position 8, after is_active at 7)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 8, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_models'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_table_columns (position 13, after is_active at 12)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 13, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_metadata_views (position 10, after is_active at 9)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 10, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_form_fields (position 10, after is_active at 9)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 10, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_form_field_rules (position 7, after position at 6)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 7, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_rules'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_form_field_validations (position 5, after position at 4)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_validations'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_form_layout_sections (position 6, after position at 5)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 6, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_layout_sections'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_form_section_fields (position 2, after position at 1)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_section_fields'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_form_sub_forms (position 6, after position at 5)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 6, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_sub_forms'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_form_tenant_role (position 2, after role_id at 1)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_tenant_role'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- sys_form_role_filters (position 5, after position at 4)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_role_filters'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- v_admin_field_rules (view model — position 7, after position at 6)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 7, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_rules'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- -------------------------------------------------------------------
-- v_admin_field_validations (view model — position 5, after position at 4)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', 'string', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_validations'
AND NOT EXISTS (SELECT 1 FROM sys_table_columns tc WHERE tc.table_id = sys_metadata_models.id AND tc.code = 'tenant_id');

-- ============================================================
-- Part 2 — Add tenant_id Form Field to 10 Missing Forms
-- ============================================================
-- admin_table_definition already has tenant_id at position 8 (V16).
-- All other forms need tenant_id added as read-only, last position.
-- ============================================================

-- -------------------------------------------------------------------
-- admin_table_column (position 13)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 13, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_table_column'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- -------------------------------------------------------------------
-- admin_form_definition (position 10)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 10, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_form_definition'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- -------------------------------------------------------------------
-- admin_form_field (position 10)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 10, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_form_field'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- -------------------------------------------------------------------
-- admin_field_rule (position 7)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 7, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_field_rule'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- -------------------------------------------------------------------
-- admin_field_validation (position 5)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 5, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_field_validation'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- -------------------------------------------------------------------
-- admin_layout_section (position 6)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 6, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_layout_section'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- -------------------------------------------------------------------
-- admin_section_field (position 2)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 2, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_section_field'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- -------------------------------------------------------------------
-- admin_sub_form_config (position 6)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 6, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_sub_form_config'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- -------------------------------------------------------------------
-- admin_tenant_role_access (position 2)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 2, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_tenant_role_access'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- -------------------------------------------------------------------
-- admin_row_filter (position 5)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id', 'Tenant ID', true, true, false, 5, 'Auto-managed', true, now(), now()
FROM sys_metadata_views WHERE name = 'admin_row_filter'
AND NOT EXISTS (SELECT 1 FROM sys_form_fields ff WHERE ff.form_id = sys_metadata_views.id AND ff.column_code = 'tenant_id');

-- ============================================================
-- Part 3 — Add tenant_id to Layout Section-Field Mappings
-- ============================================================
-- Map the new tenant_id field to each form's 'details' section.
-- Uses 3-way JOIN (section → form → field) matching V16/V17 pattern.
-- ============================================================

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_table_column' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_form_definition' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_form_field' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_field_rule' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_field_validation' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_layout_section' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_section_field' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_sub_form_config' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_tenant_role_access' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_row_filter' AND s.code = 'details' AND f.column_code = 'tenant_id'
AND NOT EXISTS (SELECT 1 FROM sys_form_section_fields sf WHERE sf.section_id = s.id AND sf.field_id = f.id);

-- ============================================================
-- Validation — Verify Row Counts
-- ============================================================
-- Expected after migration:
--   sys_table_columns: +13 tenant_id registrations (11 base + 2 views)
--   sys_form_fields: +10 tenant_id fields (all forms except admin_table_definition)
--   sys_form_section_fields: +10 section-field mappings

-- Verify column registrations:
-- SELECT tc.code, tc.position, m.name AS table_name
-- FROM sys_table_columns tc
-- JOIN sys_metadata_models m ON m.id = tc.table_id
-- WHERE tc.code = 'tenant_id'
-- ORDER BY m.name;

-- Verify form fields (should return 11 rows — 10 new + 1 existing):
-- SELECT v.name AS form, ff.position, ff.read_only
-- FROM sys_form_fields ff
-- JOIN sys_metadata_views v ON v.id = ff.form_id
-- WHERE ff.column_code = 'tenant_id'
-- ORDER BY v.name;

-- ============================================================
-- End of V18
-- ============================================================
