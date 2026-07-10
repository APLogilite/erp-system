-- ============================================================
-- PRD-002 / TASK-033 — Register Metadata Tables as Static
-- Registers 11 PRD-001 metadata tables in sys_metadata_models
-- and sys_table_columns for runtime engine discovery.
-- No DDL executed — tables already exist.
-- ============================================================

-- ============================================================
-- Part 1 — Clean Existing Registrations (Idempotency)
-- ============================================================
DELETE FROM sys_table_columns
    WHERE table_id IN (SELECT id FROM sys_metadata_models WHERE table_type = 'static' AND name LIKE 'sys_%');

DELETE FROM sys_metadata_models
    WHERE table_type = 'static' AND name LIKE 'sys_%';

-- ============================================================
-- Part 2 — Register Tables in sys_metadata_models
-- ============================================================

INSERT INTO sys_metadata_models (id, name, label, plural_label, table_type, table_name, description, definition, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'sys_metadata_models',    'Table Definition',     'Table Definitions',     'static', 'sys_metadata_models',    'Registered database tables',                      '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_table_columns',       'Table Column',         'Table Columns',         'static', 'sys_table_columns',       'Column definitions for registered tables',        '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_metadata_views',      'Form Definition',      'Form Definitions',      'static', 'sys_metadata_views',      'Form/window definitions',                         '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_form_fields',         'Form Field',           'Form Fields',           'static', 'sys_form_fields',         'Field configurations on forms',                   '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_form_field_rules',    'Field Rule',           'Field Rules',           'static', 'sys_form_field_rules',    'Conditional display/edit rules',                  '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_form_field_validations','Field Validation',   'Field Validations',     'static', 'sys_form_field_validations','Per-field validation constraints',               '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_form_layout_sections', 'Layout Section',     'Layout Sections',       'static', 'sys_form_layout_sections', 'Form layout sections',                            '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_form_section_fields',  'Section Field',      'Section Fields',        'static', 'sys_form_section_fields',  'Field-to-section mappings',                       '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_form_sub_forms',       'Sub-Form Config',    'Sub-Form Configs',      'static', 'sys_form_sub_forms',       'Header-to-line sub-form links',                   '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_form_tenant_role',     'Tenant Role Access', 'Tenant Role Access',    'static', 'sys_form_tenant_role',     'Per-tenant role assignments',                     '{}', true, now(), now()),
    (gen_random_uuid(), 'sys_form_role_filters',    'Row Filter',         'Row Filters',           'static', 'sys_form_role_filters',    'Role-based row-level data filters',               '{}', true, now(), now())
ON CONFLICT (name) DO UPDATE SET
    label = EXCLUDED.label,
    plural_label = EXCLUDED.plural_label,
    table_type = EXCLUDED.table_type,
    table_name = EXCLUDED.table_name,
    description = EXCLUDED.description,
    updated_at = now();

-- ============================================================
-- Part 3 — Register Columns in sys_table_columns
-- ============================================================

-- -------------------------------------------------------------------
-- sys_metadata_models (Table Definition)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name', 'Code', 'string', true, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_models';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label', 'Label', 'string', true, 100, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_models';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'plural_label', 'Plural Label', 'string', false, 100, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_models';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'table_type', 'Table Type', 'string', false, 20, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_models';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'table_name', 'Physical Table', 'string', false, 100, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_models';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description', 'Description', 'text', false, 6, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_models';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active', 'Active', 'boolean', false, 7, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_models';

-- -------------------------------------------------------------------
-- sys_table_columns (Table Column)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code', 'Code', 'string', true, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label', 'Label', 'string', true, 100, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'type', 'Type', 'string', true, 50, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'required', 'Required', 'boolean', false, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'default_value', 'Default Value', 'text', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'max_length', 'Max Length', 'integer', false, 6, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'precision', 'Precision', 'integer', false, 7, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'scale', 'Scale', 'integer', false, 8, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'relation_table', 'Relation Table', 'string', false, 100, 9, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'enum_options', 'Enum Options', 'text', false, 10, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 11, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active', 'Active', 'boolean', false, 12, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_table_columns';

-- -------------------------------------------------------------------
-- sys_metadata_views (Form Definition)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name', 'Code', 'string', true, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'model_name', 'Model', 'string', false, 100, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'type', 'Type', 'string', false, 50, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'scope', 'Scope', 'string', false, 20, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description', 'Description', 'text', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'where_clause_field', 'WC Field', 'string', false, 100, 6, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'where_clause_operator', 'WC Operator', 'string', false, 50, 7, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'where_clause_value', 'WC Value', 'string', false, 255, 8, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active', 'Active', 'boolean', false, 9, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_metadata_views';

-- -------------------------------------------------------------------
-- sys_form_fields (Form Field)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'column_code', 'Column Code', 'string', true, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label_override', 'Label Override', 'string', false, 200, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'visible', 'Visible', 'boolean', false, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'read_only', 'Read Only', 'boolean', false, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'required', 'Required', 'boolean', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 6, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'default_value', 'Default Value', 'text', false, 7, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'placeholder', 'Placeholder', 'string', false, 255, 8, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active', 'Active', 'boolean', false, 9, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_fields';

-- -------------------------------------------------------------------
-- sys_form_field_rules (Field Rule)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_field', 'Condition Field', 'string', false, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_operator', 'Operator', 'string', false, 50, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_value', 'Value', 'string', false, 255, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'action', 'Action', 'string', false, 50, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'logic_group', 'Logic Group', 'integer', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 6, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_rules';

-- -------------------------------------------------------------------
-- sys_form_field_validations (Field Validation)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'type', 'Type', 'string', false, 50, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_validations';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'value', 'Value', 'string', false, 255, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_validations';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'message', 'Message', 'string', false, 500, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_validations';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_field_validations';

-- -------------------------------------------------------------------
-- sys_form_layout_sections (Layout Section)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code', 'Code', 'string', false, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_layout_sections';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label', 'Label', 'string', false, 200, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_layout_sections';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'collapsible', 'Collapsible', 'boolean', false, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_layout_sections';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'columns', 'Columns', 'integer', false, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_layout_sections';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_layout_sections';

-- -------------------------------------------------------------------
-- sys_form_section_fields (Section Field)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_section_fields';

-- -------------------------------------------------------------------
-- sys_form_sub_forms (Sub-Form Config)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'relation_code', 'Relation Code', 'string', false, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_sub_forms';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'child_form_code', 'Child Form Code', 'string', false, 100, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_sub_forms';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label', 'Label', 'string', false, 200, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_sub_forms';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'display_as', 'Display As', 'string', false, 50, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_sub_forms';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_sub_forms';

-- -------------------------------------------------------------------
-- sys_form_tenant_role (Tenant Role Access)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'role_id', 'Role ID', 'string', false, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_tenant_role';

-- -------------------------------------------------------------------
-- sys_form_role_filters (Row Filter)
-- -------------------------------------------------------------------

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_field', 'Condition Field', 'string', false, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_role_filters';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_operator', 'Operator', 'string', false, 50, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_role_filters';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_value', 'Value', 'string', false, 255, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_role_filters';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'sys_form_role_filters';

-- ============================================================
-- Validation — Verify Row Counts
-- ============================================================
-- Expected: 11 tables, ~50+ columns (actual count depends on dedup behavior)
-- SELECT count(*) AS model_count FROM sys_metadata_models WHERE table_type = 'static';
-- SELECT count(*) AS column_count FROM sys_table_columns WHERE table_id IN (SELECT id FROM sys_metadata_models WHERE table_type = 'static');

-- ============================================================
-- End of V15
-- ============================================================
