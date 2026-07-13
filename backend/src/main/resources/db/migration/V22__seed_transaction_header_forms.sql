-- ============================================================
-- PRD-003 / TASK-031 — Seed Transaction Header Forms
-- Defines 9 transaction header forms with purchase/sales
-- variants using where_clause filtering:
--   - purchase_order / sales_order         (tx_order)
--   - purchase_invoice / sales_invoice     (tx_invoice)
--   - purchase_payment / sales_payment     (tx_payment)
--   - purchase_shipment / sales_shipment   (tx_shipment)
--   - material_receipt                     (tx_material_receipt)
--
-- DEPENDS ON: V20__seed_transaction_tables.sql (TASK-029)
--             V21__seed_master_data_forms.sql (TASK-030)
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
            WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice',
                           'purchase_payment','sales_payment','purchase_shipment','sales_shipment',
                           'material_receipt')
        )
    );

DELETE FROM sys_form_layout_sections
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice',
                       'purchase_payment','sales_payment','purchase_shipment','sales_shipment',
                       'material_receipt')
    );

DELETE FROM sys_form_fields
    WHERE form_id IN (
        SELECT id FROM sys_metadata_views
        WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice',
                       'purchase_payment','sales_payment','purchase_shipment','sales_shipment',
                       'material_receipt')
    );

DELETE FROM sys_metadata_views
    WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice',
                   'purchase_payment','sales_payment','purchase_shipment','sales_shipment',
                   'material_receipt');

-- ============================================================
-- Part 2 — Insert Form Definitions (sys_metadata_views)
-- Purchase/sales variants use where_clause to filter by type.
-- Type discriminator fields are excluded from form fields.
-- ============================================================

INSERT INTO sys_metadata_views (id, name, model_name, type, scope, tenant_id, description, definition, where_clause_field, where_clause_operator, where_clause_value, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'purchase_order',     'tx_order',             'form', 'global', NULL, 'Purchase orders to suppliers',     '{}', 'order_type', 'equals', 'purchase', true, now(), now()),
    (gen_random_uuid(), 'sales_order',        'tx_order',             'form', 'global', NULL, 'Sales orders from customers',      '{}', 'order_type', 'equals', 'sales',    true, now(), now()),
    (gen_random_uuid(), 'purchase_invoice',   'tx_invoice',           'form', 'global', NULL, 'Invoices from suppliers',          '{}', 'invoice_type', 'equals', 'purchase', true, now(), now()),
    (gen_random_uuid(), 'sales_invoice',      'tx_invoice',           'form', 'global', NULL, 'Invoices to customers',            '{}', 'invoice_type', 'equals', 'sales',    true, now(), now()),
    (gen_random_uuid(), 'purchase_payment',   'tx_payment',           'form', 'global', NULL, 'Payments to suppliers',            '{}', 'payment_type', 'equals', 'purchase', true, now(), now()),
    (gen_random_uuid(), 'sales_payment',      'tx_payment',           'form', 'global', NULL, 'Payments from customers',          '{}', 'payment_type', 'equals', 'sales',    true, now(), now()),
    (gen_random_uuid(), 'purchase_shipment',  'tx_shipment',          'form', 'global', NULL, 'Inbound shipments from suppliers', '{}', 'shipment_type', 'equals', 'purchase', true, now(), now()),
    (gen_random_uuid(), 'sales_shipment',     'tx_shipment',          'form', 'global', NULL, 'Outbound shipments to customers',  '{}', 'shipment_type', 'equals', 'sales',    true, now(), now()),
    (gen_random_uuid(), 'material_receipt',   'tx_material_receipt',   'form', 'global', NULL, 'Goods receipt from receiving',     '{}', NULL, NULL, NULL, true, now(), now())
ON CONFLICT (name) DO UPDATE SET
    model_name = EXCLUDED.model_name,
    type = EXCLUDED.type,
    scope = EXCLUDED.scope,
    tenant_id = EXCLUDED.tenant_id,
    description = EXCLUDED.description,
    where_clause_field = EXCLUDED.where_clause_field,
    where_clause_operator = EXCLUDED.where_clause_operator,
    where_clause_value = EXCLUDED.where_clause_value,
    updated_at = now();

-- ============================================================
-- Part 3 — Insert Form Fields (sys_form_fields)
-- Type discriminator fields are deliberately excluded.
-- ============================================================

-- -------------------------------------------------------------------
-- purchase_order / sales_order — 12 fields (order_type excluded)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Supplier',     true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'purchase_order';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Customer',     true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'sales_order';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_date',     'Order Date',   true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'expected_date',  'Expected Date',true, false, false, 3, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'warehouse_id',   'Warehouse',    true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'currency',       'Currency',     true, false, false, 5, 'Default: USD', true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_number',   'Order Number', true, false, false, 6, 'Enter manually or auto', true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',         'Status',       true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'subtotal',       'Subtotal',     true, false, false, 8, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_amount',     'Tax Amount',   true, false, false, 9, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'discount_amount','Discount',     true, false, false,10, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'grand_total',    'Grand Total',  true, false, false,11, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',          'Notes',        true, false, false,12, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

-- -------------------------------------------------------------------
-- purchase_invoice / sales_invoice — 14 fields (invoice_type excluded)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Supplier',      true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'purchase_invoice';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Customer',      true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'sales_invoice';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'invoice_date',   'Invoice Date',  true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'due_date',       'Due Date',      true, false, false, 3, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_id',       'Order',         true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'currency',       'Currency',      true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'invoice_number', 'Invoice Number',true, false, false, 6, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',         'Status',        true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'subtotal',       'Subtotal',      true, false, false, 8, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_amount',     'Tax Amount',    true, false, false, 9, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'discount_amount','Discount',      true, false, false,10, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'grand_total',    'Grand Total',   true, false, false,11, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'paid_amount',    'Paid Amount',   true, false, false,12, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'due_amount',     'Due Amount',    true, false, false,13, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',          'Notes',         true, false, false,14, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

-- -------------------------------------------------------------------
-- purchase_payment / sales_payment — 9 fields (payment_type excluded)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Supplier',      true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'purchase_payment';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Customer',      true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'sales_payment';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'payment_date',   'Payment Date',  true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_payment','sales_payment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'amount',         'Amount',        true, false, true,  3, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_payment','sales_payment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'payment_method', 'Payment Method',true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_payment','sales_payment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'currency',       'Currency',      true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_payment','sales_payment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'reference',      'Reference',     true, false, false, 6, 'Check/transaction number', true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_payment','sales_payment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'payment_number', 'Payment Number',true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_payment','sales_payment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',         'Status',        true, false, false, 8, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_payment','sales_payment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',          'Notes',         true, false, false, 9, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_payment','sales_payment');

-- -------------------------------------------------------------------
-- purchase_shipment / sales_shipment — 9 fields (shipment_type excluded)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Supplier',      true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'purchase_shipment';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Customer',      true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'sales_shipment';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_date',  'Shipment Date', true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'warehouse_id',   'Warehouse',     true, false, false, 3, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_id',       'Order',         true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tracking_number','Tracking Number',true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'carrier',        'Carrier',       true, false, false, 6, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_number','Shipment Number',true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',         'Status',        true, false, false, 8, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',          'Notes',         true, false, false, 9, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

-- -------------------------------------------------------------------
-- material_receipt — 9 fields (no where_clause)
-- -------------------------------------------------------------------
INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Supplier',       true, false, true,  1, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'receipt_date',   'Receipt Date',   true, false, true,  2, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'warehouse_id',   'Warehouse',      true, false, false, 3, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_id',       'Purchase Order', true, false, false, 4, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_id',    'Shipment',       true, false, false, 5, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, placeholder, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'reference',      'Reference',      true, false, false, 6, 'Supplier delivery note', true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'receipt_number', 'Receipt Number', true, false, false, 7, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',         'Status',         true, false, false, 8, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

INSERT INTO sys_form_fields (id, form_id, column_code, label_override, visible, read_only, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',          'Notes',          true, false, false, 9, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

-- ============================================================
-- Part 4 — Insert Layout Sections (sys_form_layout_sections)
-- Payment forms get 1 section; all others get 2 sections.
-- ============================================================

-- purchase_order / sales_order — 2 sections
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'General Information', false, 2, 1, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Details',             false, 2, 2, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order');

-- purchase_invoice / sales_invoice — 2 sections
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'General Information', false, 2, 1, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Details',             false, 2, 2, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_invoice','sales_invoice');

-- purchase_payment / sales_payment — 1 section (fewer fields)
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'Payment Details',     false, 2, 1, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_payment','sales_payment');

-- purchase_shipment / sales_shipment — 2 sections
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'General Information', false, 2, 1, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Details',             false, 2, 2, true, now(), now() FROM sys_metadata_views WHERE name IN ('purchase_shipment','sales_shipment');

-- material_receipt — 2 sections
INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'general', 'General Information', false, 2, 1, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

INSERT INTO sys_form_layout_sections (id, form_id, code, label, collapsible, columns, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'details', 'Details',             false, 2, 2, true, now(), now() FROM sys_metadata_views WHERE name = 'material_receipt';

-- ============================================================
-- Part 5 — Insert Section-Field Mappings (sys_form_section_fields)
-- ============================================================

-- ============================================================
-- purchase_order / sales_order — section-field mappings
-- ============================================================

-- General section (pos 1-5): partner_id, order_date, expected_date, warehouse_id, currency
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'partner_id'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'order_date'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'expected_date'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'warehouse_id'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 5, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'currency'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'general';

-- Details section (pos 1-7): order_number, status, subtotal, tax_amount, discount_amount, grand_total, notes
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'order_number'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'status'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'subtotal'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'tax_amount'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 5, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'discount_amount'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 6, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'grand_total'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 7, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'notes'
WHERE v.name IN ('purchase_order','sales_order') AND s.code = 'details';

-- ============================================================
-- purchase_invoice / sales_invoice — section-field mappings
-- ============================================================

-- General section (pos 1-5): partner_id, invoice_date, due_date, order_id, currency
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'partner_id'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'invoice_date'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'due_date'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'order_id'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 5, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'currency'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'general';

-- Details section (pos 1-9): invoice_number, status, subtotal, tax_amount, discount_amount, grand_total, paid_amount, due_amount, notes
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'invoice_number'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'status'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'subtotal'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'tax_amount'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 5, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'discount_amount'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 6, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'grand_total'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 7, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'paid_amount'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 8, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'due_amount'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 9, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'notes'
WHERE v.name IN ('purchase_invoice','sales_invoice') AND s.code = 'details';

-- ============================================================
-- purchase_payment / sales_payment — single section
-- ============================================================

-- General section (pos 1-9): partner_id, payment_date, amount, payment_method, currency, reference, payment_number, status, notes
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'partner_id'
WHERE v.name IN ('purchase_payment','sales_payment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'payment_date'
WHERE v.name IN ('purchase_payment','sales_payment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'amount'
WHERE v.name IN ('purchase_payment','sales_payment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'payment_method'
WHERE v.name IN ('purchase_payment','sales_payment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 5, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'currency'
WHERE v.name IN ('purchase_payment','sales_payment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 6, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'reference'
WHERE v.name IN ('purchase_payment','sales_payment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 7, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'payment_number'
WHERE v.name IN ('purchase_payment','sales_payment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 8, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'status'
WHERE v.name IN ('purchase_payment','sales_payment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 9, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'notes'
WHERE v.name IN ('purchase_payment','sales_payment') AND s.code = 'general';

-- ============================================================
-- purchase_shipment / sales_shipment — section-field mappings
-- ============================================================

-- General section (pos 1-6): partner_id, shipment_date, warehouse_id, order_id, tracking_number, carrier
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'partner_id'
WHERE v.name IN ('purchase_shipment','sales_shipment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'shipment_date'
WHERE v.name IN ('purchase_shipment','sales_shipment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'warehouse_id'
WHERE v.name IN ('purchase_shipment','sales_shipment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'order_id'
WHERE v.name IN ('purchase_shipment','sales_shipment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 5, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'tracking_number'
WHERE v.name IN ('purchase_shipment','sales_shipment') AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 6, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'carrier'
WHERE v.name IN ('purchase_shipment','sales_shipment') AND s.code = 'general';

-- Details section (pos 1-3): shipment_number, status, notes
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'shipment_number'
WHERE v.name IN ('purchase_shipment','sales_shipment') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'status'
WHERE v.name IN ('purchase_shipment','sales_shipment') AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'notes'
WHERE v.name IN ('purchase_shipment','sales_shipment') AND s.code = 'details';

-- ============================================================
-- material_receipt — section-field mappings
-- ============================================================

-- General section (pos 1-6): partner_id, receipt_date, warehouse_id, order_id, shipment_id, reference
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'partner_id'
WHERE v.name = 'material_receipt' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'receipt_date'
WHERE v.name = 'material_receipt' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'warehouse_id'
WHERE v.name = 'material_receipt' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 4, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'order_id'
WHERE v.name = 'material_receipt' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 5, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'shipment_id'
WHERE v.name = 'material_receipt' AND s.code = 'general';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 6, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'reference'
WHERE v.name = 'material_receipt' AND s.code = 'general';

-- Details section (pos 1-3): receipt_number, status, notes
INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 1, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'receipt_number'
WHERE v.name = 'material_receipt' AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 2, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'status'
WHERE v.name = 'material_receipt' AND s.code = 'details';

INSERT INTO sys_form_section_fields (id, section_id, field_id, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), s.id, f.id, 3, true, now(), now()
FROM sys_form_layout_sections s
JOIN sys_metadata_views v ON v.id = s.form_id
JOIN sys_form_fields f ON f.form_id = v.id AND f.column_code = 'notes'
WHERE v.name = 'material_receipt' AND s.code = 'details';

-- ============================================================
-- End of V22
-- ============================================================
