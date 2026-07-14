-- Table: tx_invoice_line
-- Created: V20
CREATE TABLE tx_invoice_line (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    invoice_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    product_id UUID NOT NULL,
    description TEXT,
    quantity NUMERIC(15,3),
    uom_id UUID,
    unit_price NUMERIC(15,2),
    line_total NUMERIC(15,2),
    tax_rate NUMERIC(5,2),
    order_line_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_il_invoice ON tx_invoice_line(invoice_id);
CREATE INDEX IF NOT EXISTS idx_tx_il_product ON tx_invoice_line(product_id);
CREATE INDEX IF NOT EXISTS idx_tx_il_ol ON tx_invoice_line(order_line_id);
CREATE INDEX IF NOT EXISTS idx_tx_il_uom ON tx_invoice_line(uom_id);
CREATE INDEX IF NOT EXISTS idx_tx_il_tenant ON tx_invoice_line(tenant_id);
