-- ============================================================
-- PRD-003 / TASK-032 — Seed Line Forms and Sub-Form Configs
-- Defines 4 line-item forms and 7 sub-form tab links:
--   - order_line       → used by purchase_order + sales_order
--   - invoice_line     → used by purchase_invoice + sales_invoice
--   - shipment_line    → used by purchase_shipment + sales_shipment
--   - mr_line          → used by material_receipt
--
-- DEPENDS ON: V22__seed_transaction_header_forms.sql (TASK-031)
-- IMPORTANT: Set spring.flyway.enabled=true before running.
-- ============================================================

-- ============================================================
-- Part 1 — Clean Existing (Idempotency)
-- FK-safe order: sub-forms first, then line form data.
-- ============================================================

DELETE FROM sys_form_sub_forms
    WHERE parent_form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice',
                       'purchase_shipment','sales_shipment','material_receipt')
    );

DELETE FROM sys_form_section_fields
    WHERE section_id IN (
        SELECT id FROM sys_form_layout_sections
        WHERE form_id IN (
            SELECT id FROM sys_metadata_views
            WHERE name IN ('order_line','invoice_line','shipment_line','mr_line')
        )
    );

DELETE FROM sys_form_layout_sections
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('order_line','invoice_line','shipment_line','mr_line')
    );

DELETE FROM sys_form_fields
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('order_line','invoice_line','shipment_line','mr_line')
    );

DELETE FROM sys_metadata_views
    WHERE name IN ('order_line','invoice_line','shipment_line','mr_line');

-- ============================================================
-- Part 2 — Insert Line Form Definitions (sys_metadata_views)
-- ============================================================

INSERT INTO sys_metadata_views (id, name, model_name, type, scope, tenant_id, description, definition, where_clause_field, where_clause_operator, where_clause_value, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'order_line',     'tx_order_line',      'form', 'global', NULL, 'Line items for orders',                   '{}', NULL, NULL, NULL, true, now(), now()),
    (gen_random_uuid(), 'invoice_line',   'tx_invoice_line',    'form', 'global', NULL, 'Line items for invoices',                 '{}', NULL, NULL, NULL, true, now(), now()),
    (gen_random_uuid(), 'shipment_line',  'tx_shipment_line',   'form', 'global', NULL, 'Line items for shipments',                '{}', NULL, NULL, NULL, true, now(), now()),
    (gen_random_uuid(), 'mr_line',        'tx_mr_line',         'form', 'global', NULL, 'Line items for material receipts',        '{}', NULL, NULL, NULL, true, now(), now())
ON CONFLICT (name) DO UPDATE SET
    model_name = EXCLUDED.model_name,
    type = EXCLUDED.type,
    scope = EXCLUDED.scope,
    tenant_id = EXCLUDED.tenant_id,
    description = EXCLUDED.description,
    updated_at = now();

-- ============================================================
-- Part 3 — Insert Line Form Fields (sys_form_fields)
-- Parent FK included (auto-populated when opened as sub-form tab)
-- ============================================================

-- -------------------------------------------------------------------
-- order_line (9 fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_id',     'Order',            true, false, true,  1, 'Auto-set from parent', true, now(), now() FROM sys_metadata_views WHERE name = 'order_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_number',  'Line #',           true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name = 'order_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',   'Product',          true, false, true,  3, true, now(), now() FROM sys_metadata_views WHERE name = 'order_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',  'Description',      true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name = 'order_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'quantity',     'Quantity',         true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name = 'order_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',       'UOM',              true, false, false, 6, true, now(), now() FROM sys_metadata_views WHERE name = 'order_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'unit_price',   'Unit Price',       true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name = 'order_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_total',   'Line Total',       true, false, false, 8, true, now(), now() FROM sys_metadata_views WHERE name = 'order_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_rate',     'Tax Rate (%)',     true, false, false, 9, true, now(), now() FROM sys_metadata_views WHERE name = 'order_line';

-- -------------------------------------------------------------------
-- invoice_line (10 fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'invoice_id',   'Invoice',          true, false, true,  1, 'Auto-set from parent', true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_number',  'Line #',           true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',   'Product',          true, false, true,  3, true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',  'Description',      true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'quantity',     'Quantity',         true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',       'UOM',              true, false, false, 6, true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'unit_price',   'Unit Price',       true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_total',   'Line Total',       true, false, false, 8, true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_rate',     'Tax Rate (%)',     true, false, false, 9, true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_line_id','Source Order Line',true, false, false,10, true, now(), now() FROM sys_metadata_views WHERE name = 'invoice_line';

-- -------------------------------------------------------------------
-- shipment_line (7 fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_id',  'Shipment',         true, false, true,  1, 'Auto-set from parent', true, now(), now() FROM sys_metadata_views WHERE name = 'shipment_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_number',  'Line #',           true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name = 'shipment_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',   'Product',          true, false, true,  3, true, now(), now() FROM sys_metadata_views WHERE name = 'shipment_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',  'Description',      true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name = 'shipment_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'quantity',     'Quantity',         true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name = 'shipment_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',       'UOM',              true, false, false, 6, true, now(), now() FROM sys_metadata_views WHERE name = 'shipment_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_line_id','Source Order Line',true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name = 'shipment_line';

-- -------------------------------------------------------------------
-- mr_line (9 fields)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'receipt_id',      'Receipt',              true, false, true,  1, 'Auto-set from parent', true, now(), now() FROM sys_metadata_views WHERE name = 'mr_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_number',     'Line #',               true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name = 'mr_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',      'Product',              true, false, true,  3, true, now(), now() FROM sys_metadata_views WHERE name = 'mr_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',     'Description',          true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name = 'mr_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'ordered_qty',     'Ordered Qty',          true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name = 'mr_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'received_qty',    'Received Qty',         true, false, false, 6, true, now(), now() FROM sys_metadata_views WHERE name = 'mr_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',          'UOM',                  true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name = 'mr_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_line_id',   'Source Order Line',    true, false, false, 8, true, now(), now() FROM sys_metadata_views WHERE name = 'mr_line';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_line_id','Source Shipment Line', true, false, false, 9, true, now(), now() FROM sys_metadata_views WHERE name = 'mr_line';

-- ============================================================
-- Part 4 — Insert Layout Sections (sys_form_layout_sections)
-- All line forms use a single flat "Line Details" section.
-- ============================================================

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'items', 'Line Details', false, 2, 1, true, now(), now() FROM sys_metadata_views WHERE name IN ('order_line','invoice_line','shipment_line','mr_line');

-- ============================================================
-- Part 5 — Insert Section-Field Mappings (sys_form_section_fields)
-- ============================================================

-- order_line → items section (pos 1-9)
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'order_line' AND s.code = 'items';

-- invoice_line → items section (pos 1-10)
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'invoice_line' AND s.code = 'items';

-- shipment_line → items section (pos 1-7)
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'shipment_line' AND s.code = 'items';

-- mr_line → items section (pos 1-9)
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, f.position, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id
WHERE v.name = 'mr_line' AND s.code = 'items';

-- ============================================================
-- Part 6 — Insert Sub-Form Configurations (sys_form_sub_forms)
-- Links each header form to its line form as a tab.
-- relation_code = FK column on the child table.
-- ============================================================

INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'order_id',     'order_line',     'Order Lines',     'tab', 1, true, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'purchase_order';

INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'order_id',     'order_line',     'Order Lines',     'tab', 1, true, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'sales_order';

INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'invoice_id',   'invoice_line',   'Invoice Lines',   'tab', 1, true, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'purchase_invoice';

INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'invoice_id',   'invoice_line',   'Invoice Lines',   'tab', 1, true, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'sales_invoice';

INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'shipment_id',  'shipment_line',  'Shipment Lines',  'tab', 1, true, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'purchase_shipment';

INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'shipment_id',  'shipment_line',  'Shipment Lines',  'tab', 1, true, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'sales_shipment';

INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'receipt_id',   'mr_line',        'MR Lines',        'tab', 1, true, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'material_receipt';

-- ============================================================
-- End of V23 — PRD-003 Complete
-- ============================================================
