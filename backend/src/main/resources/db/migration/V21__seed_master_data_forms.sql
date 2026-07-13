-- ============================================================
-- PRD-003 / TASK-030 — Seed Master Data Forms
-- Defines 5 master data forms for the Order Flow:
--   - business_partner   (md_business_partner)
--   - product            (md_product)
--   - uom                (md_uom)
--   - uom_conversion     (md_uom_conversion)
--   - warehouse          (md_warehouse)
-- Single-screen CRUD forms with no sub-forms.
-- No field rules or validations per PRD-003 spec.
--
-- DEPENDS ON: V19__seed_master_data_tables.sql (TASK-028)
-- IMPORTANT: Set spring.flyway.enabled=true before running.
-- ============================================================

-- ============================================================
-- Part 1 — Clean Existing (Idempotency)
-- ============================================================

DELETE FROM sys_form_section_fields
    WHERE section_id IN (
        SELECT id FROM sys_form_layout_sections
        WHERE form_id IN (
            SELECT id FROM sys_metadata_views
            WHERE name IN ('business_partner','product','uom','uom_conversion','warehouse')
        )
    );

DELETE FROM sys_form_layout_sections
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('business_partner','product','uom','uom_conversion','warehouse')
    );

DELETE FROM sys_form_fields
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('business_partner','product','uom','uom_conversion','warehouse')
    );

DELETE FROM sys_metadata_views
    WHERE name IN ('business_partner','product','uom','uom_conversion','warehouse');

-- ============================================================
-- Part 2 — Insert Form Definitions (sys_metadata_views)
-- ============================================================

INSERT INTO sys_metadata_views (id, name, model_name, type, scope, tenant_id, description, definition, where_clause_field, where_clause_operator, where_clause_value, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'business_partner', 'md_business_partner', 'form', 'global', NULL, 'Manage customers, suppliers, and business contacts', '{}', NULL, NULL, NULL, true, now(), now()),
    (gen_random_uuid(), 'product',          'md_product',          'form', 'global', NULL, 'Manage products and services',                       '{}', NULL, NULL, NULL, true, now(), now()),
    (gen_random_uuid(), 'uom',              'md_uom',              'form', 'global', NULL, 'Manage units of measure',                             '{}', NULL, NULL, NULL, true, now(), now()),
    (gen_random_uuid(), 'uom_conversion',   'md_uom_conversion',   'form', 'global', NULL, 'Define UOM conversion factors',                      '{}', NULL, NULL, NULL, true, now(), now()),
    (gen_random_uuid(), 'warehouse',        'md_warehouse',        'form', 'global', NULL, 'Manage warehouse locations',                         '{}', NULL, NULL, NULL, true, now(), now())
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
-- business_partner (7 fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',         'Code',         true, false, true,  1, 'Enter partner code',  true, now(), now() FROM sys_metadata_views WHERE name = 'business_partner';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',         'Name',         true, false, true,  2, 'Enter partner name',  true, now(), now() FROM sys_metadata_views WHERE name = 'business_partner';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_type', 'Partner Type', true, false, true,  3, true, now(), now() FROM sys_metadata_views WHERE name = 'business_partner';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'email',        'Email',        true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name = 'business_partner';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'phone',        'Phone',        true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name = 'business_partner';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'address',      'Address',      true, false, false, 6, true, now(), now() FROM sys_metadata_views WHERE name = 'business_partner';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_id',       'Tax ID',       true, false, false, 7, 'VAT/GST number', true, now(), now() FROM sys_metadata_views WHERE name = 'business_partner';

-- -------------------------------------------------------------------
-- product (7 fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',         'SKU',          true, false, true,  1, 'Enter product code', true, now(), now() FROM sys_metadata_views WHERE name = 'product';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',         'Name',         true, false, true,  2, 'Enter product name', true, now(), now() FROM sys_metadata_views WHERE name = 'product';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',  'Description',  true, false, false, 3, true, now(), now() FROM sys_metadata_views WHERE name = 'product';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_type', 'Product Type', true, false, true,  4, true, now(), now() FROM sys_metadata_views WHERE name = 'product';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',       'Default UOM',  true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name = 'product';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'unit_price',   'Unit Price',   true, false, false, 6, true, now(), now() FROM sys_metadata_views WHERE name = 'product';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active',    'Active',       true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name = 'product';

-- -------------------------------------------------------------------
-- uom (2 fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',         'Code',         true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'uom';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',         'Name',         true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name = 'uom';

-- -------------------------------------------------------------------
-- uom_conversion (4 fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'from_uom_id',  'From UOM',      true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'uom_conversion';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'to_uom_id',    'To UOM',        true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name = 'uom_conversion';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',   'Product',       true, false, false, 3, true, now(), now() FROM sys_metadata_views WHERE name = 'uom_conversion';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'factor',       'Conversion Factor', true, false, true, 4, true, now(), now() FROM sys_metadata_views WHERE name = 'uom_conversion';

-- -------------------------------------------------------------------
-- warehouse (3 fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',         'Code',          true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'warehouse';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',         'Name',          true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name = 'warehouse';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'address',      'Address',       true, false, false, 3, true, now(), now() FROM sys_metadata_views WHERE name = 'warehouse';

-- ============================================================
-- Part 4 — Insert Layout Sections (sys_form_layout_sections)
-- ============================================================

-- business_partner — 2 sections
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'General Information', false, 2, 1, true, now(), now() FROM sys_metadata_views WHERE name = 'business_partner';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'contact', 'Contact Details',     false, 2, 2, true, now(), now() FROM sys_metadata_views WHERE name = 'business_partner';

-- product — 2 sections
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'General Information', false, 2, 1, true, now(), now() FROM sys_metadata_views WHERE name = 'product';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'pricing', 'Pricing',             false, 2, 2, true, now(), now() FROM sys_metadata_views WHERE name = 'product';

-- uom — 1 section
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'General Information', false, 1, 1, true, now(), now() FROM sys_metadata_views WHERE name = 'uom';

-- uom_conversion — 1 section
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'Conversion Details',  false, 2, 1, true, now(), now() FROM sys_metadata_views WHERE name = 'uom_conversion';

-- warehouse — 1 section
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'General Information', false, 1, 1, true, now(), now() FROM sys_metadata_views WHERE name = 'warehouse';

-- ============================================================
-- Part 5 — Insert Section-Field Mappings (sys_form_section_fields)
-- ============================================================

-- business_partner → general section (pos 1-5): code, name, partner_type, email, phone
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'code'
WHERE v.name = 'business_partner' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'name'
WHERE v.name = 'business_partner' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'partner_type'
WHERE v.name = 'business_partner' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'email'
WHERE v.name = 'business_partner' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 5, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'phone'
WHERE v.name = 'business_partner' AND s.code = 'general';

-- business_partner → contact section (pos 1-2): address, tax_id
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'address'
WHERE v.name = 'business_partner' AND s.code = 'contact';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'tax_id'
WHERE v.name = 'business_partner' AND s.code = 'contact';

-- product → general section (pos 1-5): code, name, description, product_type, uom_id
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'code'
WHERE v.name = 'product' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'name'
WHERE v.name = 'product' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'description'
WHERE v.name = 'product' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'product_type'
WHERE v.name = 'product' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 5, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'uom_id'
WHERE v.name = 'product' AND s.code = 'general';

-- product → pricing section (pos 1-2): unit_price, is_active
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'unit_price'
WHERE v.name = 'product' AND s.code = 'pricing';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'is_active'
WHERE v.name = 'product' AND s.code = 'pricing';

-- uom → general section (pos 1-2): code, name
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'code'
WHERE v.name = 'uom' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'name'
WHERE v.name = 'uom' AND s.code = 'general';

-- uom_conversion → general section (pos 1-4): from_uom_id, to_uom_id, product_id, factor
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'from_uom_id'
WHERE v.name = 'uom_conversion' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'to_uom_id'
WHERE v.name = 'uom_conversion' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'product_id'
WHERE v.name = 'uom_conversion' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'factor'
WHERE v.name = 'uom_conversion' AND s.code = 'general';

-- warehouse → general section (pos 1-3): code, name, address
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'code'
WHERE v.name = 'warehouse' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'name'
WHERE v.name = 'warehouse' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'address'
WHERE v.name = 'warehouse' AND s.code = 'general';

-- ============================================================
-- End of V21
-- ============================================================
