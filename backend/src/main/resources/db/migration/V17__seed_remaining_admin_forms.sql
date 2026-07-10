-- ============================================================
-- PRD-002 / TASK-035 — Seed Remaining Admin Forms
-- Defines 7 remaining admin forms for managing metadata entities:
--   - admin_field_rule         (Field Rules)
--   - admin_field_validation   (Field Validations)
--   - admin_layout_section     (Layout Sections)
--   - admin_section_field      (Section Fields)
--   - admin_sub_form_config    (Sub-Form Configs)
--   - admin_tenant_role_access (Tenant Role Access)
--   - admin_row_filter         (Row Filters)
-- Includes sub-form links for Form Definition → Fields/Rules/Validations
-- and Layout Section → Section Fields.
-- ============================================================

-- ============================================================
-- Part 0 — Create Views for FK Resolution
-- ============================================================
-- Problem: sys_form_field_rules and sys_form_field_validations
-- reference field_id (FK to sys_form_fields), not form_id directly.
-- PRD-001 sub-form engine requires a direct parent FK column.
-- Solution: Create views that join through sys_form_fields to
-- expose form_id. Forms reference views instead of base tables.

CREATE OR REPLACE VIEW v_admin_field_rules AS
SELECT
    ffr.id,
    ffr.field_id,
    ffr.condition_field,
    ffr.condition_operator,
    ffr.condition_value,
    ffr.action,
    ffr.logic_group,
    ffr.position,
    ffr.created_at,
    ffr.updated_at,
    ffr.created_by,
    ffr.updated_by,
    ffr.deleted_at,
    ff.form_id
FROM sys_form_field_rules ffr
JOIN sys_form_fields ff ON ff.id = ffr.field_id;

CREATE OR REPLACE VIEW v_admin_field_validations AS
SELECT
    ffv.id,
    ffv.field_id,
    ffv.type,
    ffv.value,
    ffv.message,
    ffv.position,
    ffv.created_at,
    ffv.updated_at,
    ffv.created_by,
    ffv.updated_by,
    ffv.deleted_at,
    ff.form_id
FROM sys_form_field_validations ffv
JOIN sys_form_fields ff ON ff.id = ffv.field_id;

-- ============================================================
-- Part 1 — Clean Existing (Idempotency)
-- ============================================================

-- Remove sub-form configs that reference these forms
DELETE FROM sys_form_sub_forms
    WHERE parent_form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('admin_form_definition', 'admin_layout_section')
    )
    OR child_form_code IN (
        'admin_field_rule', 'admin_field_validation', 'admin_layout_section',
        'admin_section_field', 'admin_sub_form_config', 'admin_tenant_role_access',
        'admin_row_filter'
    );

-- Remove section-field mappings
DELETE FROM sys_form_section_fields
    WHERE section_id IN (
        SELECT id FROM sys_form_layout_sections
        WHERE form_id IN (
            SELECT id FROM sys_metadata_views
            WHERE name IN (
                'admin_field_rule', 'admin_field_validation', 'admin_layout_section',
                'admin_section_field', 'admin_sub_form_config', 'admin_tenant_role_access',
                'admin_row_filter'
            )
        )
    );

-- Remove layout sections
DELETE FROM sys_form_layout_sections
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN (
            'admin_field_rule', 'admin_field_validation', 'admin_layout_section',
            'admin_section_field', 'admin_sub_form_config', 'admin_tenant_role_access',
            'admin_row_filter'
        )
    );

-- Remove form fields
DELETE FROM sys_form_fields
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN (
            'admin_field_rule', 'admin_field_validation', 'admin_layout_section',
            'admin_section_field', 'admin_sub_form_config', 'admin_tenant_role_access',
            'admin_row_filter'
        )
    );

-- Remove form definitions
DELETE FROM sys_metadata_views
    WHERE name IN (
        'admin_field_rule', 'admin_field_validation', 'admin_layout_section',
        'admin_section_field', 'admin_sub_form_config', 'admin_tenant_role_access',
        'admin_row_filter'
    );

-- Clean view registrations from metadata tables (if any from previous runs)
DELETE FROM sys_table_columns
    WHERE table_id IN (
        SELECT id FROM sys_metadata_models
        WHERE name IN ('v_admin_field_rules', 'v_admin_field_validations')
    );

DELETE FROM sys_metadata_models
    WHERE name IN ('v_admin_field_rules', 'v_admin_field_validations');

-- ============================================================
-- Part 2 — Register View Models in sys_metadata_models
-- ============================================================

INSERT INTO sys_metadata_models (id, name, label, plural_label, table_type, table_name, description, definition, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'v_admin_field_rules',       'Field Rule (View)',       'Field Rules (View)',       'static', 'v_admin_field_rules',       'View: sys_form_field_rules joined with form_id',       '{}', true, now(), now()),
    (gen_random_uuid(), 'v_admin_field_validations', 'Field Validation (View)', 'Field Validations (View)', 'static', 'v_admin_field_validations', 'View: sys_form_field_validations joined with form_id', '{}', true, now(), now())
ON CONFLICT (name) DO UPDATE SET
    label = EXCLUDED.label,
    plural_label = EXCLUDED.plural_label,
    table_type = EXCLUDED.table_type,
    table_name = EXCLUDED.table_name,
    description = EXCLUDED.description,
    updated_at = now();

-- ============================================================
-- Part 3 — Register View Columns in sys_table_columns
-- ============================================================

-- v_admin_field_rules columns (base columns + form_id)
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_field', 'Condition Field', 'string', false, 100, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_operator', 'Operator', 'string', false, 50, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_value', 'Value', 'string', false, 255, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'action', 'Action', 'string', false, 50, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'logic_group', 'Logic Group', 'integer', false, 5, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_rules';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 6, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_rules';

-- v_admin_field_validations columns (base columns + form_id)
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'type', 'Type', 'string', false, 50, 1, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_validations';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'value', 'Value', 'string', false, 255, 2, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_validations';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'message', 'Error Message', 'string', false, 500, 3, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_validations';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', 'integer', false, 4, true, now(), now()
FROM sys_metadata_models WHERE name = 'v_admin_field_validations';

-- ============================================================
-- Part 4 — Insert Form Definitions (sys_metadata_views)
-- ============================================================
-- Note: admin_field_rule and admin_field_validation use VIEW model names
-- so that the sub-form engine can resolve form_id for filtering.

INSERT INTO sys_metadata_views (id, name, model_name, type, scope, tenant_id, description, definition, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'admin_field_rule',         'v_admin_field_rules',       'form', 'global', NULL, 'Manage conditional field rules',                       '{}', true, now(), now()),
    (gen_random_uuid(), 'admin_field_validation',   'v_admin_field_validations', 'form', 'global', NULL, 'Manage per-field validation constraints',              '{}', true, now(), now()),
    (gen_random_uuid(), 'admin_layout_section',     'sys_form_layout_sections',  'form', 'global', NULL, 'Manage form layout sections',                         '{}', true, now(), now()),
    (gen_random_uuid(), 'admin_section_field',      'sys_form_section_fields',   'form', 'global', NULL, 'Manage field-to-section mappings',                    '{}', true, now(), now()),
    (gen_random_uuid(), 'admin_sub_form_config',    'sys_form_sub_forms',        'form', 'global', NULL, 'Manage header-to-line sub-form links',                '{}', true, now(), now()),
    (gen_random_uuid(), 'admin_tenant_role_access', 'sys_form_tenant_role',      'form', 'global', NULL, 'Manage per-tenant role access assignments',           '{}', true, now(), now()),
    (gen_random_uuid(), 'admin_row_filter',         'sys_form_role_filters',     'form', 'global', NULL, 'Manage role-based row-level data filters',            '{}', true, now(), now())
ON CONFLICT (name) DO UPDATE SET
    model_name = EXCLUDED.model_name,
    type = EXCLUDED.type,
    scope = EXCLUDED.scope,
    tenant_id = EXCLUDED.tenant_id,
    description = EXCLUDED.description,
    updated_at = now();

-- ============================================================
-- Part 5 — Insert Form Fields (sys_form_fields)
-- ============================================================

-- -------------------------------------------------------------------
-- admin_field_rule
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_field',    'Condition Field', true, false, false, 1, 'e.g., customer_tier',            true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_rule';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_operator', 'Operator',        true, false, false, 2, 'equals / not_equals / etc.',      true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_rule';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_value',    'Value',           true, false, false, 3, 'e.g., Gold',                      true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_rule';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'action',             'Action',          true, false, false, 4, 'show / hide / read_only / etc.',   true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_rule';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'logic_group',        'Logic Group',     true, false, false, 5, 'AND group number',                true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_rule';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position',           'Position',        true, false, false, 6, NULL,                              true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_rule';

-- -------------------------------------------------------------------
-- admin_field_validation
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'type',     'Type',          true, false, false, 1, 'required / min_length / max_length / min / max / pattern', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_validation';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'value',    'Value',         true, false, false, 2, 'Constraint value',                                         true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_validation';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'message',  'Error Message', true, false, false, 3, 'e.g., "This field is required"',                            true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_validation';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position',      true, false, false, 4, NULL,                                                        true, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_validation';

-- -------------------------------------------------------------------
-- admin_layout_section
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',         'Code',        true, false, false, 1, 'e.g., general',               true, now(), now() FROM sys_metadata_views WHERE name = 'admin_layout_section';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label',        'Label',       true, false, false, 2, 'e.g., General Information',   true, now(), now() FROM sys_metadata_views WHERE name = 'admin_layout_section';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'collapsible',  'Collapsible', true, false, false, 3, NULL,                           true, now(), now() FROM sys_metadata_views WHERE name = 'admin_layout_section';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'columns',      'Columns',     true, false, false, 4, '1 / 2 / 3',                   true, now(), now() FROM sys_metadata_views WHERE name = 'admin_layout_section';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position',     'Position',    true, false, false, 5, NULL,                           true, now(), now() FROM sys_metadata_views WHERE name = 'admin_layout_section';

-- -------------------------------------------------------------------
-- admin_section_field
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position', 'Position', true, false, false, 1, true, now(), now() FROM sys_metadata_views WHERE name = 'admin_section_field';

-- -------------------------------------------------------------------
-- admin_sub_form_config
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'relation_code',   'Relation Code',   true, false, false, 1, 'FK column on child table',   true, now(), now() FROM sys_metadata_views WHERE name = 'admin_sub_form_config';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'child_form_code', 'Child Form Code', true, false, false, 2, 'e.g., order_line',           true, now(), now() FROM sys_metadata_views WHERE name = 'admin_sub_form_config';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'label',           'Tab Label',       true, false, false, 3, 'e.g., Order Lines',          true, now(), now() FROM sys_metadata_views WHERE name = 'admin_sub_form_config';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'display_as',      'Display As',      true, false, false, 4, 'tab / inline_grid',          true, now(), now() FROM sys_metadata_views WHERE name = 'admin_sub_form_config';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position',        'Position',        true, false, false, 5, 'Tab order',                  true, now(), now() FROM sys_metadata_views WHERE name = 'admin_sub_form_config';

-- -------------------------------------------------------------------
-- admin_tenant_role_access
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'role_id', 'Role ID', true, false, true, 1, 'UUID of the role', true, now(), now() FROM sys_metadata_views WHERE name = 'admin_tenant_role_access';

-- -------------------------------------------------------------------
-- admin_row_filter
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_field',    'Condition Field', true, false, false, 1, 'e.g., created_by',              true, now(), now() FROM sys_metadata_views WHERE name = 'admin_row_filter';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_operator', 'Operator',        true, false, false, 2, 'equals / not_equals / etc.',    true, now(), now() FROM sys_metadata_views WHERE name = 'admin_row_filter';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'condition_value',    'Value',           true, false, false, 3, 'e.g., {current_user_id}',       true, now(), now() FROM sys_metadata_views WHERE name = 'admin_row_filter';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'position',           'Position',        true, false, false, 4, NULL,                            true, now(), now() FROM sys_metadata_views WHERE name = 'admin_row_filter';

-- ============================================================
-- Part 6 — Insert Layout Sections (sys_form_layout_sections)
-- ============================================================

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Rule Details',       false, 2, 1, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_rule';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Validation Details', false, 2, 1, now(), now() FROM sys_metadata_views WHERE name = 'admin_field_validation';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Section Details',    false, 2, 1, now(), now() FROM sys_metadata_views WHERE name = 'admin_layout_section';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Mapping Details',    false, 1, 1, now(), now() FROM sys_metadata_views WHERE name = 'admin_section_field';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Sub-Form Details',   false, 2, 1, now(), now() FROM sys_metadata_views WHERE name = 'admin_sub_form_config';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Role Access',        false, 1, 1, now(), now() FROM sys_metadata_views WHERE name = 'admin_tenant_role_access';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Filter Details',     false, 2, 1, now(), now() FROM sys_metadata_views WHERE name = 'admin_row_filter';

-- ============================================================
-- Part 7 — Insert Section-Field Mappings (sys_form_section_fields)
-- ============================================================

-- admin_field_rule → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_field_rule' AND s.code = 'details';

-- admin_field_validation → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_field_validation' AND s.code = 'details';

-- admin_layout_section → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_layout_section' AND s.code = 'details';

-- admin_section_field → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_section_field' AND s.code = 'details';

-- admin_sub_form_config → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_sub_form_config' AND s.code = 'details';

-- admin_tenant_role_access → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_tenant_role_access' AND s.code = 'details';

-- admin_row_filter → details section
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'admin_row_filter' AND s.code = 'details';

-- ============================================================
-- Part 8 — Sub-Form Configs
-- ============================================================

-- Form Definition → Form Fields (form_id FK exists directly on sys_form_fields)
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'form_id', 'admin_form_field', 'Fields', 'tab', 1, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_form_definition';

-- Form Definition → Field Rules (via VIEW v_admin_field_rules which exposes form_id)
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'form_id', 'admin_field_rule', 'Rules', 'tab', 2, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_form_definition';

-- Form Definition → Field Validations (via VIEW v_admin_field_validations which exposes form_id)
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'form_id', 'admin_field_validation', 'Validations', 'tab', 3, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_form_definition';

-- Layout Section → Section Fields (section_id FK exists directly on sys_form_section_fields)
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'section_id', 'admin_section_field', 'Field Mappings', 'tab', 1, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'admin_layout_section';

-- ============================================================
-- Validation — Verify Row Counts
-- ============================================================
-- Expected after migration:
--   sys_metadata_views: +7 forms (11 total for PRD-002)
--   sys_form_fields: ~26 field rows
--   sys_form_layout_sections: 7 sections
--   sys_form_section_fields: 7 section-field mappings
--   sys_form_sub_forms: 4 sub-form configs
--   sys_metadata_models: +2 view registrations

-- ============================================================
-- End of V17
-- ============================================================
