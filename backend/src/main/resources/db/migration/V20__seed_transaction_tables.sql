-- ============================================================
-- PRD-003 / TASK-029 — Seed Transaction Tables
-- Creates 9 transaction tables for the Order Flow:
--   - tx_order              (Purchase/Sales order header)
--   - tx_order_line         (Order line items)
--   - tx_invoice            (Invoice header)
--   - tx_invoice_line       (Invoice line items)
--   - tx_payment            (Payment records)
--   - tx_shipment           (Shipment header)
--   - tx_shipment_line      (Shipment line items)
--   - tx_material_receipt   (Material receipt header)
--   - tx_mr_line            (Material receipt line items)
-- Registers all tables + columns in the metadata engine.
--
-- DEPENDS ON: V19__seed_master_data_tables.sql (TASK-028)
-- IMPORTANT: Set spring.flyway.enabled=true in
-- application-local.properties before running this migration.
-- ============================================================

-- ============================================================
-- Part 1 — Drop Existing Tables (Idempotency)
-- Order matters: drop child (line) tables before parent headers.
-- ============================================================

DROP TABLE IF EXISTS tx_mr_line CASCADE;
DROP TABLE IF EXISTS tx_shipment_line CASCADE;
DROP TABLE IF EXISTS tx_invoice_line CASCADE;
DROP TABLE IF EXISTS tx_order_line CASCADE;
DROP TABLE IF EXISTS tx_material_receipt CASCADE;
DROP TABLE IF EXISTS tx_shipment CASCADE;
DROP TABLE IF EXISTS tx_payment CASCADE;
DROP TABLE IF EXISTS tx_invoice CASCADE;
DROP TABLE IF EXISTS tx_order CASCADE;

-- ============================================================
-- Part 2 — Create Header Tables
-- Each header table includes the 8 system columns required
-- by the BaseEntity / DynamicCrudService pattern.
-- ============================================================

-- -------------------------------------------------------------------
-- tx_order — Purchase and sales order header
-- -------------------------------------------------------------------
CREATE TABLE tx_order (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    order_number    VARCHAR(50) NOT NULL,
    order_date      DATE NOT NULL,
    order_type      VARCHAR(20) NOT NULL,
    partner_id      UUID NOT NULL,
    warehouse_id    UUID,
    currency        VARCHAR(3) DEFAULT 'USD',
    subtotal        NUMERIC(15,2) DEFAULT 0,
    tax_amount      NUMERIC(15,2) DEFAULT 0,
    discount_amount NUMERIC(15,2) DEFAULT 0,
    grand_total     NUMERIC(15,2) DEFAULT 0,
    status          VARCHAR(30) DEFAULT 'draft',
    expected_date   DATE,
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMP
);

-- -------------------------------------------------------------------
-- tx_invoice — Purchase and sales invoice header
-- -------------------------------------------------------------------
CREATE TABLE tx_invoice (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    invoice_number  VARCHAR(50) NOT NULL,
    invoice_date    DATE NOT NULL,
    due_date        DATE,
    invoice_type    VARCHAR(20) NOT NULL,
    partner_id      UUID NOT NULL,
    order_id        UUID,
    currency        VARCHAR(3) DEFAULT 'USD',
    subtotal        NUMERIC(15,2) DEFAULT 0,
    tax_amount      NUMERIC(15,2) DEFAULT 0,
    discount_amount NUMERIC(15,2) DEFAULT 0,
    grand_total     NUMERIC(15,2) DEFAULT 0,
    paid_amount     NUMERIC(15,2) DEFAULT 0,
    due_amount      NUMERIC(15,2) DEFAULT 0,
    status          VARCHAR(30) DEFAULT 'draft',
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMP
);

-- -------------------------------------------------------------------
-- tx_payment — Payment records
-- -------------------------------------------------------------------
CREATE TABLE tx_payment (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    payment_number  VARCHAR(50) NOT NULL,
    payment_date    DATE NOT NULL,
    payment_type    VARCHAR(20) NOT NULL,
    partner_id      UUID NOT NULL,
    payment_method  VARCHAR(30),
    currency        VARCHAR(3) DEFAULT 'USD',
    amount          NUMERIC(15,2) NOT NULL,
    reference       VARCHAR(100),
    notes           TEXT,
    status          VARCHAR(30) DEFAULT 'draft',
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMP
);

-- -------------------------------------------------------------------
-- tx_shipment — Shipment header
-- -------------------------------------------------------------------
CREATE TABLE tx_shipment (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    shipment_number VARCHAR(50) NOT NULL,
    shipment_date   DATE NOT NULL,
    shipment_type   VARCHAR(20) NOT NULL,
    partner_id      UUID NOT NULL,
    warehouse_id    UUID,
    order_id        UUID,
    tracking_number VARCHAR(100),
    carrier         VARCHAR(100),
    status          VARCHAR(30) DEFAULT 'draft',
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMP
);

-- -------------------------------------------------------------------
-- tx_material_receipt — Material receipt header
-- -------------------------------------------------------------------
CREATE TABLE tx_material_receipt (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    receipt_number  VARCHAR(50) NOT NULL,
    receipt_date    DATE NOT NULL,
    partner_id      UUID NOT NULL,
    warehouse_id    UUID,
    order_id        UUID,
    shipment_id     UUID,
    reference       VARCHAR(100),
    status          VARCHAR(30) DEFAULT 'draft',
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMP
);

-- ============================================================
-- Part 3 — Create Line Tables
-- Each line table includes system columns for consistency
-- with the DynamicCrudService.
-- ============================================================

-- -------------------------------------------------------------------
-- tx_order_line — Order line items
-- -------------------------------------------------------------------
CREATE TABLE tx_order_line (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    order_id        UUID NOT NULL,
    line_number     INTEGER NOT NULL,
    product_id      UUID NOT NULL,
    description     TEXT,
    quantity        NUMERIC(15,3),
    uom_id          UUID,
    unit_price      NUMERIC(15,2),
    line_total      NUMERIC(15,2),
    tax_rate        NUMERIC(5,2),
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMP
);

-- -------------------------------------------------------------------
-- tx_invoice_line — Invoice line items
-- -------------------------------------------------------------------
CREATE TABLE tx_invoice_line (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    invoice_id      UUID NOT NULL,
    line_number     INTEGER NOT NULL,
    product_id      UUID NOT NULL,
    description     TEXT,
    quantity        NUMERIC(15,3),
    uom_id          UUID,
    unit_price      NUMERIC(15,2),
    line_total      NUMERIC(15,2),
    tax_rate        NUMERIC(5,2),
    order_line_id   UUID,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMP
);

-- -------------------------------------------------------------------
-- tx_shipment_line — Shipment line items
-- -------------------------------------------------------------------
CREATE TABLE tx_shipment_line (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    shipment_id     UUID NOT NULL,
    line_number     INTEGER NOT NULL,
    product_id      UUID NOT NULL,
    description     TEXT,
    quantity        NUMERIC(15,3),
    uom_id          UUID,
    order_line_id   UUID,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMP
);

-- -------------------------------------------------------------------
-- tx_mr_line — Material receipt line items
-- -------------------------------------------------------------------
CREATE TABLE tx_mr_line (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    receipt_id      UUID NOT NULL,
    line_number     INTEGER NOT NULL,
    product_id      UUID NOT NULL,
    description     TEXT,
    ordered_qty     NUMERIC(15,3),
    received_qty    NUMERIC(15,3),
    uom_id          UUID,
    order_line_id   UUID,
    shipment_line_id UUID,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMP
);

-- ============================================================
-- Part 4 — Foreign Key Indexes
-- NOTE: No hard PostgreSQL REFERENCES constraints per PRD-003
-- spec (dynamic tables). UUID columns store reference IDs
-- without FK enforcement. Indexes are added for performance.
-- ============================================================

-- Order
CREATE INDEX IF NOT EXISTS idx_tx_order_partner    ON tx_order(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_order_warehouse  ON tx_order(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_tx_order_type       ON tx_order(order_type);
CREATE INDEX IF NOT EXISTS idx_tx_order_tenant     ON tx_order(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_order_status     ON tx_order(status);

-- Order Line
CREATE INDEX IF NOT EXISTS idx_tx_ol_order         ON tx_order_line(order_id);
CREATE INDEX IF NOT EXISTS idx_tx_ol_product       ON tx_order_line(product_id);
CREATE INDEX IF NOT EXISTS idx_tx_ol_uom           ON tx_order_line(uom_id);
CREATE INDEX IF NOT EXISTS idx_tx_ol_tenant        ON tx_order_line(tenant_id);

-- Invoice
CREATE INDEX IF NOT EXISTS idx_tx_inv_partner      ON tx_invoice(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_inv_order        ON tx_invoice(order_id);
CREATE INDEX IF NOT EXISTS idx_tx_inv_type         ON tx_invoice(invoice_type);
CREATE INDEX IF NOT EXISTS idx_tx_inv_tenant       ON tx_invoice(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_inv_status       ON tx_invoice(status);

-- Invoice Line
CREATE INDEX IF NOT EXISTS idx_tx_il_invoice       ON tx_invoice_line(invoice_id);
CREATE INDEX IF NOT EXISTS idx_tx_il_product       ON tx_invoice_line(product_id);
CREATE INDEX IF NOT EXISTS idx_tx_il_ol            ON tx_invoice_line(order_line_id);
CREATE INDEX IF NOT EXISTS idx_tx_il_uom           ON tx_invoice_line(uom_id);
CREATE INDEX IF NOT EXISTS idx_tx_il_tenant        ON tx_invoice_line(tenant_id);

-- Payment
CREATE INDEX IF NOT EXISTS idx_tx_pay_partner      ON tx_payment(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_pay_type         ON tx_payment(payment_type);
CREATE INDEX IF NOT EXISTS idx_tx_pay_tenant       ON tx_payment(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_pay_status       ON tx_payment(status);

-- Shipment
CREATE INDEX IF NOT EXISTS idx_tx_ship_partner     ON tx_shipment(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_ship_warehouse   ON tx_shipment(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_tx_ship_order       ON tx_shipment(order_id);
CREATE INDEX IF NOT EXISTS idx_tx_ship_type        ON tx_shipment(shipment_type);
CREATE INDEX IF NOT EXISTS idx_tx_ship_tenant      ON tx_shipment(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_ship_status      ON tx_shipment(status);

-- Shipment Line
CREATE INDEX IF NOT EXISTS idx_tx_sl_shipment      ON tx_shipment_line(shipment_id);
CREATE INDEX IF NOT EXISTS idx_tx_sl_product       ON tx_shipment_line(product_id);
CREATE INDEX IF NOT EXISTS idx_tx_sl_tenant        ON tx_shipment_line(tenant_id);

-- Material Receipt
CREATE INDEX IF NOT EXISTS idx_tx_mr_partner       ON tx_material_receipt(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_warehouse     ON tx_material_receipt(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_order         ON tx_material_receipt(order_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_shipment      ON tx_material_receipt(shipment_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_tenant        ON tx_material_receipt(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_status        ON tx_material_receipt(status);

-- MR Line
CREATE INDEX IF NOT EXISTS idx_tx_mrl_receipt      ON tx_mr_line(receipt_id);
CREATE INDEX IF NOT EXISTS idx_tx_mrl_product      ON tx_mr_line(product_id);
CREATE INDEX IF NOT EXISTS idx_tx_mrl_tenant       ON tx_mr_line(tenant_id);

-- ============================================================
-- Part 5 — Register Tables in sys_metadata_models
-- Uses DELETE + INSERT pattern for idempotency.
-- ============================================================

-- Clean existing metadata registrations for these tables
DELETE FROM sys_table_columns
    WHERE table_id IN (SELECT id FROM sys_metadata_models WHERE name IN (
        'tx_order', 'tx_order_line', 'tx_invoice', 'tx_invoice_line',
        'tx_payment', 'tx_shipment', 'tx_shipment_line',
        'tx_material_receipt', 'tx_mr_line'
    ));

DELETE FROM sys_metadata_models WHERE name IN (
    'tx_order', 'tx_order_line', 'tx_invoice', 'tx_invoice_line',
    'tx_payment', 'tx_shipment', 'tx_shipment_line',
    'tx_material_receipt', 'tx_mr_line'
);

-- Insert table definitions
INSERT INTO sys_metadata_models (id, name, label, plural_label, table_type, table_name, description, definition, is_active, created_at, updated_at) VALUES
    (gen_random_uuid(), 'tx_order',             'Order',            'Orders',             'dynamic', 'tx_order',             'Purchase and sales order header',                                                                   '{}', true, now(), now()),
    (gen_random_uuid(), 'tx_order_line',        'Order Line',       'Order Lines',        'dynamic', 'tx_order_line',        'Order line items',                                                                                  '{}', true, now(), now()),
    (gen_random_uuid(), 'tx_invoice',           'Invoice',          'Invoices',           'dynamic', 'tx_invoice',           'Purchase and sales invoice header',                                                                '{}', true, now(), now()),
    (gen_random_uuid(), 'tx_invoice_line',      'Invoice Line',     'Invoice Lines',      'dynamic', 'tx_invoice_line',      'Invoice line items',                                                                                '{}', true, now(), now()),
    (gen_random_uuid(), 'tx_payment',           'Payment',          'Payments',           'dynamic', 'tx_payment',           'Payment records',                                                                                    '{}', true, now(), now()),
    (gen_random_uuid(), 'tx_shipment',          'Shipment',         'Shipments',          'dynamic', 'tx_shipment',          'Shipment header',                                                                                    '{}', true, now(), now()),
    (gen_random_uuid(), 'tx_shipment_line',     'Shipment Line',    'Shipment Lines',     'dynamic', 'tx_shipment_line',     'Shipment line items',                                                                                '{}', true, now(), now()),
    (gen_random_uuid(), 'tx_material_receipt',  'Material Receipt', 'Material Receipts',  'dynamic', 'tx_material_receipt',  'Material receipt header (goods received)',                                                          '{}', true, now(), now()),
    (gen_random_uuid(), 'tx_mr_line',           'MR Line',          'MR Lines',           'dynamic', 'tx_mr_line',           'Material receipt line items',                                                                        '{}', true, now(), now())
ON CONFLICT (name) DO UPDATE SET
    label = EXCLUDED.label,
    plural_label = EXCLUDED.plural_label,
    table_type = EXCLUDED.table_type,
    table_name = EXCLUDED.table_name,
    description = EXCLUDED.description,
    updated_at = now();

-- ============================================================
-- Part 6 — Register Columns in sys_table_columns
-- ============================================================

-- -------------------------------------------------------------------
-- tx_order columns (14 business + system cols handled by engine)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_number',   'Order Number',    'string',  true,  50,  1, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_date',     'Order Date',      'date',    true,       2, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_type',     'Order Type',      'enum',    true,  20,  3, '["purchase","sales"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',     'Partner',         'many2one', true,  4, 'md_business_partner', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'warehouse_id',   'Warehouse',       'many2one', false, 5, 'md_warehouse', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'currency',       'Currency',        'string',  false, 3,  6, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'subtotal',        'Subtotal',        'decimal', false, 15, 2,  7, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_amount',     'Tax Amount',      'decimal', false, 15, 2,  8, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'discount_amount','Discount Amount', 'decimal', false, 15, 2,  9, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'grand_total',    'Grand Total',     'decimal', false, 15, 2, 10, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',         'Status',          'enum',    false, 30, 11, '["draft","confirmed","received","billed","cancelled"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'expected_date',  'Expected Date',   'date',    false,     12, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',          'Notes',           'text',    false,     13, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order';

-- -------------------------------------------------------------------
-- tx_order_line columns (9 business)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_id',      'Order',           'many2one', true,  1, 'tx_order', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_number',   'Line Number',     'integer', true,      2, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',    'Product',         'many2one', true,  3, 'md_product', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',   'Description',     'text',    false,      4, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'quantity',      'Quantity',        'decimal', false, 15, 3, 5, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',        'UOM',             'many2one', false, 6, 'md_uom', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'unit_price',    'Unit Price',      'decimal', false, 15, 2, 7, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_total',    'Line Total',      'decimal', false, 15, 2, 8, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_rate',      'Tax Rate',        'decimal', false,  5, 2, 9, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_order_line';

-- -------------------------------------------------------------------
-- tx_invoice columns (16 business)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'invoice_number','Invoice Number',  'string',  true,  50,  1, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'invoice_date',  'Invoice Date',    'date',    true,       2, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'due_date',      'Due Date',        'date',    false,      3, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'invoice_type',  'Invoice Type',    'enum',    true,  20,  4, '["purchase","sales"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',    'Partner',         'many2one', true,  5, 'md_business_partner', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_id',      'Order',           'many2one', false, 6, 'tx_order', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'currency',      'Currency',        'string',  false, 3,  7, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'subtotal',       'Subtotal',        'decimal', false, 15, 2,  8, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_amount',    'Tax Amount',      'decimal', false, 15, 2,  9, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'discount_amount','Discount Amount','decimal', false, 15, 2, 10, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'grand_total',   'Grand Total',     'decimal', false, 15, 2, 11, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'paid_amount',   'Paid Amount',     'decimal', false, 15, 2, 12, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'due_amount',    'Due Amount',      'decimal', false, 15, 2, 13, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',        'Status',          'enum',    false, 30, 14, '["draft","validated","paid","partially_paid","cancelled"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',         'Notes',           'text',    false,     15, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice';

-- -------------------------------------------------------------------
-- tx_invoice_line columns (10 business)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'invoice_id',    'Invoice',         'many2one', true,  1, 'tx_invoice', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_number',   'Line Number',     'integer', true,      2, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',    'Product',         'many2one', true,  3, 'md_product', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',   'Description',     'text',    false,      4, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'quantity',      'Quantity',        'decimal', false, 15, 3, 5, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',        'UOM',             'many2one', false, 6, 'md_uom', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'unit_price',    'Unit Price',      'decimal', false, 15, 2, 7, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_total',    'Line Total',      'decimal', false, 15, 2, 8, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_rate',      'Tax Rate',        'decimal', false,  5, 2, 9, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_line_id', 'Order Line',      'many2one', false, 10, 'tx_order_line', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_invoice_line';

-- -------------------------------------------------------------------
-- tx_payment columns (10 business)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'payment_number','Payment Number',  'string',  true,  50,  1, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'payment_date',  'Payment Date',    'date',    true,       2, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'payment_type',  'Payment Type',    'enum',    true,  20,  3, '["purchase","sales"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',    'Partner',         'many2one', true,  4, 'md_business_partner', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'payment_method','Payment Method',  'enum',    false, 30,  5, '["cash","check","bank_transfer","credit_card"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'currency',      'Currency',        'string',  false, 3,  6, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'amount',        'Amount',          'decimal', true,  15,  2, 7, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'reference',     'Reference',       'string',  false, 100, 8, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',         'Notes',           'text',    false,      9, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',        'Status',          'enum',    false, 30, 10, '["draft","posted","reconciled","cancelled"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_payment';

-- -------------------------------------------------------------------
-- tx_shipment columns (11 business)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_number','Shipment Number','string',  true,  50,  1, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_date', 'Shipment Date',   'date',    true,       2, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_type', 'Shipment Type',   'enum',    true,  20,  3, '["purchase","sales"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',    'Partner',         'many2one', true,  4, 'md_business_partner', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'warehouse_id',  'Warehouse',       'many2one', false, 5, 'md_warehouse', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_id',      'Order',           'many2one', false, 6, 'tx_order', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tracking_number','Tracking Number','string',  false, 100, 7, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'carrier',       'Carrier',         'string',  false, 100, 8, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',        'Status',          'enum',    false, 30,  9, '["draft","in_transit","delivered","cancelled"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',         'Notes',           'text',    false,     10, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment';

-- -------------------------------------------------------------------
-- tx_shipment_line columns (7 business)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_id',   'Shipment',        'many2one', true,  1, 'tx_shipment', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_number',   'Line Number',     'integer', true,      2, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',    'Product',         'many2one', true,  3, 'md_product', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',   'Description',     'text',    false,      4, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'quantity',      'Quantity',        'decimal', false, 15, 3, 5, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',        'UOM',             'many2one', false, 6, 'md_uom', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_line_id', 'Order Line',      'many2one', false, 7, 'tx_order_line', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_shipment_line';

-- -------------------------------------------------------------------
-- tx_material_receipt columns (10 business)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'receipt_number','Receipt Number',  'string',  true,  50,  1, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_material_receipt';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'receipt_date',  'Receipt Date',    'date',    true,       2, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_material_receipt';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_id',    'Partner',         'many2one', true,  3, 'md_business_partner', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_material_receipt';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'warehouse_id',  'Warehouse',       'many2one', false, 4, 'md_warehouse', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_material_receipt';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_id',      'Order',           'many2one', false, 5, 'tx_order', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_material_receipt';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_id',   'Shipment',        'many2one', false, 6, 'tx_shipment', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_material_receipt';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'reference',     'Reference',       'string',  false, 100, 7, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_material_receipt';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'status',        'Status',          'enum',    false, 30,  8, '["draft","received","inspected","cancelled"]', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_material_receipt';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'notes',         'Notes',           'text',    false,      9, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_material_receipt';

-- -------------------------------------------------------------------
-- tx_mr_line columns (9 business)
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'receipt_id',    'Receipt',         'many2one', true,  1, 'tx_material_receipt', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_mr_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'line_number',   'Line Number',     'integer', true,      2, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_mr_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',    'Product',         'many2one', true,  3, 'md_product', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_mr_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',   'Description',     'text',    false,      4, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_mr_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'ordered_qty',   'Ordered Qty',     'decimal', false, 15, 3, 5, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_mr_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'received_qty',  'Received Qty',    'decimal', false, 15, 3, 6, true, now(), now() FROM sys_metadata_models WHERE name = 'tx_mr_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',        'UOM',             'many2one', false, 7, 'md_uom', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_mr_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'order_line_id',  'Order Line',     'many2one', false, 8, 'tx_order_line', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_mr_line';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'shipment_line_id', 'Shipment Line','many2one', false, 9, 'tx_shipment_line', true, now(), now() FROM sys_metadata_models WHERE name = 'tx_mr_line';
