-- ============================================================
-- PRD-002 / TASK-034 — Seed Core Admin Forms
-- Defines 4 admin forms for managing metadata entities:
--   - admin_table_definition  (Table Definition)
--   - admin_table_column      (Table Column)
--   - admin_form_definition   (Form Definition)
--   - admin_form_field        (Form Field)
-- Includes sub-form link: admin_table_definition → admin_table_column
-- ============================================================

-- ============================================================
-- Part 1 — Clean Existing (Idempotency)
-- ============================================================

DELETE FROM sys_form_sub_forms
    WHERE parent_form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('admin_table_definition', 'admin_form_definition'));

DELETE FROM sys_form_section_fields
    WHERE section_id IN (
        SELECT id FROM sys_form_layout_sections
        WHERE form_id IN (
            SELECT id FROM sys_metadata_views
            WHERE name IN ('admin_table_definition', 'admin_table_column', 'admin_form_definition', 'admin_form_field')
        )
    );

DELETE FROM sys_form_layout_sections
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('admin_table_definition', 'admin_table_column', 'admin_form_definition', 'admin_form_field')
    );

DELETE FROM sys_form_fields
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('admin_table_definition', 'admin_table_column', 'admin_form_definition', 'admin_form_field')
    );

DELETE FROM sys_metadata_views
    WHERE name IN ('admin_table_definition', 'admin_table_column', 'admin_form_definition', 'admin_form_field');

-- ============================================================
-- Part 2 — Insert Form Definitions (sys_metadata_views)
-- ============================================================

INSERT INTO sys_metadata_views (id, name, model_name, type, scope, tenant_id, description, definition, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'admin_table_definition', 'sys_metadata_models', 'form', 'global', NULL, 'Manage registered database table definitions', '{}', true, now(), now()),
    (gen_random_uuid(), 'admin_table_column',     'sys_table_columns',    'form', 'global', NULL, 'Manage column definitions for tables',              '{}', true, now(), now()),
    (gen_random_uuid(), 'admin_form_definition',  'sys_metadata_views',   'form', 'global', NULL, 'Manage form/window definitions',                    '{}', true, now(), now()),
    (gen_random_uuid(), 'admin_form_field',        'sys_form_fields',      'form', 'global', NULL, 'Manage field configurations on forms',              '{}', true, now(), now())
ON CONFLICT (name) DO UPDATE SET
    model_name = EXCLUDED.model_name,
    type = EXCLUDED.type,
    scope = EXCLUDED.scope,
    tenant_id = EXCLUDED.tenant_id,
    description = EXCLUDED.description,
    updated_at = now();

-- ============================================================
-- Part 3 — Insert Form Fields (sys_form_fields)
-- ============================================================

-- -------------------------------------------------------------------
-- admin_table_definition (sys_metadata_models)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',         'Code',          true, false, true,  1, 'e.g., tx_order',        true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label',        'Label',         true, false, true,  2, 'e.g., Sales Order',     true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'plural_label', 'Plural Label',  true, false, false, 3, 'e.g., Sales Orders',    true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'table_type',   'Table Type',    true, false, false, 4, 'dynamic / static',      true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'table_name',   'Physical Table',true, false, false, 5, NULL,                    true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',  'Description',   true, false, false, 6, NULL,                    true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active',    'Active',        true, false, false, 7, NULL,                    true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tenant_id',    'Tenant ID',     true, true,  false, 8, 'Auto-managed',          true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_definition';

-- -------------------------------------------------------------------
-- admin_table_column (sys_table_columns)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',           'Code',           true, false, true,  1,  'e.g., order_number', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label',          'Label',          true, false, true,  2,  'e.g., Order Number', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'type',           'Type',           true, false, true,  3,  'string/integer/decimal/boolean/text/datetime', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'required',       'Required',       true, false, false, 4,  NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'default_value',  'Default Value',  true, false, false, 5,  NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'max_length',     'Max Length',     true, false, false, 6,  'For string type', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'precision',      'Precision',      true, false, false, 7,  'For decimal type', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'scale',          'Scale',          true, false, false, 8,  'For decimal type', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'relation_table', 'Relation Table', true, false, false, 9,  'For many2one', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'enum_options',   'Enum Options',   true, false, false, 10, 'JSON array', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position',       'Position',       true, false, false, 11, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active',      'Active',         true, false, false, 12, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_table_column';

-- -------------------------------------------------------------------
-- admin_form_definition (sys_metadata_views)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',                  'Code',       true, false, true,  1, 'e.g., sales_order', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'model_name',            'Model',      true, false, false, 2, 'Table code',        true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'type',                  'Type',       true, false, false, 3, 'form',              true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'scope',                 'Scope',      true, false, false, 4, 'global / tenant',   true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',           'Description',true, false, false, 5, NULL,                true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'where_clause_field',    'WC Field',   true, false, false, 6, NULL,                true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'where_clause_operator', 'WC Operator',true, false, false, 7, NULL,                true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'where_clause_value',    'WC Value',   true, false, false, 8, NULL,                true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_definition';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active',             'Active',     true, false, false, 9, NULL,                true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_definition';

-- -------------------------------------------------------------------
-- admin_form_field (sys_form_fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'column_code',    'Column Code',   true, false, true,  1, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_field';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label_override', 'Label Override',true, false, false, 2, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_field';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'visible',        'Visible',       true, false, false, 3, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_field';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'read_only',      'Read Only',     true, false, false, 4, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_field';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'required',       'Required',      true, false, false, 5, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_field';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position',       'Position',      true, false, false, 6, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_field';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'default_value',  'Default Value', true, false, false, 7, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_field';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'placeholder',    'Placeholder',   true, false, false, 8, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_field';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active',      'Active',        true, false, false, 9, NULL, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_form_field';

-- ============================================================
-- Part 4 — Insert Layout Sections (sys_form_layout_sections)
-- ============================================================

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Table Information', false, 2, 1, now(), now()
FROM sys_metadata_views WHERE name = 'admin_table_definition';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Column Details', false, 2, 1, now(), now()
FROM sys_metadata_views WHERE name = 'admin_table_column';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Form Information', false, 2, 1, now(), now()
FROM sys_metadata_views WHERE name = 'admin_form_definition';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Field Details', false, 2, 1, now(), now()
FROM sys_metadata_views WHERE name = 'admin_form_field';

-- ============================================================
-- Part 5 — Insert Section-Field Mappings (sys_form_section_fields)
-- ============================================================

-- admin_table_definition → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_table_definition' AND s.code = 'details';

-- admin_table_column → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_table_column' AND s.code = 'details';

-- admin_form_definition → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_form_definition' AND s.code = 'details';

-- admin_form_field → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_form_field' AND s.code = 'details';

-- ============================================================
-- Part 6 — Sub-Form Config: Table Definition → Table Columns
-- ============================================================

INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'table_id', 'admin_table_column', 'Columns', 'tab', 1, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_table_definition';

-- ============================================================
-- End of V16
-- ============================================================
