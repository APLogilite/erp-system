-- ============================================================
-- V2 — Business Tables: Master Data (md_*) + Transactions (tx_*)
-- ============================================================

-- ============================================================
-- Part 1 — Master Data Tables
-- ============================================================

-- md_business_partner — Customers, suppliers, and other contacts
CREATE TABLE md_business_partner (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(50) NOT NULL,
    name        VARCHAR(200) NOT NULL,
    partner_type VARCHAR(20) NOT NULL,
    email       VARCHAR(100),
    phone       VARCHAR(30),
    address     TEXT,
    tax_id      VARCHAR(50),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_business_partner_code UNIQUE (code)
);

-- md_uom — Units of Measure
CREATE TABLE md_uom (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(10) NOT NULL,
    name        VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_uom_code UNIQUE (code)
);

-- md_product — Goods and services catalog
CREATE TABLE md_product (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(50) NOT NULL,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    product_type VARCHAR(20) NOT NULL,
    uom_id      UUID REFERENCES md_uom(id),
    unit_price  NUMERIC(15,2),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_product_code UNIQUE (code)
);

-- md_uom_conversion — Conversion factors between UOMs
CREATE TABLE md_uom_conversion (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    from_uom_id UUID NOT NULL REFERENCES md_uom(id),
    to_uom_id   UUID NOT NULL REFERENCES md_uom(id),
    product_id  UUID REFERENCES md_product(id),
    factor      NUMERIC(15,6) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP
);

-- md_warehouse — Physical storage locations
CREATE TABLE md_warehouse (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(20) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    address     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_warehouse_code UNIQUE (code)
);

-- ============================================================
-- Part 2 — Transaction Header Tables
-- ============================================================

-- tx_order — Purchase and sales order header
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

-- tx_invoice — Purchase and sales invoice header
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

-- tx_payment — Payment records
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

-- tx_shipment — Shipment / Receipt header (single table, movement_type = inbound/outbound)
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

-- tx_material_receipt — Material receipt header
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
-- Part 3 — Transaction Line Tables
-- ============================================================

-- tx_order_line — Order line items
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

-- tx_invoice_line — Invoice line items
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

-- tx_shipment_line — Shipment line items
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

-- tx_mr_line — Material receipt line items
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
-- Part 4 — Indexes (performance on FK and lookup columns)
-- ============================================================

-- Master data
CREATE INDEX IF NOT EXISTS idx_md_product_uom           ON md_product(uom_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_from         ON md_uom_conversion(from_uom_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_to           ON md_uom_conversion(to_uom_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_product      ON md_uom_conversion(product_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_tenant       ON md_uom_conversion(tenant_id);
CREATE INDEX IF NOT EXISTS idx_md_product_tenant        ON md_product(tenant_id);
CREATE INDEX IF NOT EXISTS idx_md_bp_tenant             ON md_business_partner(tenant_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_tenant            ON md_uom(tenant_id);
CREATE INDEX IF NOT EXISTS idx_md_warehouse_tenant      ON md_warehouse(tenant_id);

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
