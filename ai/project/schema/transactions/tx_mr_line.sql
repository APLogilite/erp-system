-- Table: tx_mr_line
-- Created: V20
CREATE TABLE tx_mr_line (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    receipt_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    product_id UUID NOT NULL,
    description TEXT,
    ordered_qty NUMERIC(15,3),
    received_qty NUMERIC(15,3),
    uom_id UUID,
    order_line_id UUID,
    shipment_line_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_mrl_receipt ON tx_mr_line(receipt_id);
CREATE INDEX IF NOT EXISTS idx_tx_mrl_product ON tx_mr_line(product_id);
CREATE INDEX IF NOT EXISTS idx_tx_mrl_tenant ON tx_mr_line(tenant_id);
