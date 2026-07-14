-- Table: tx_shipment_line
-- Created: V20
CREATE TABLE tx_shipment_line (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    shipment_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    product_id UUID NOT NULL,
    description TEXT,
    quantity NUMERIC(15,3),
    uom_id UUID,
    order_line_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_sl_shipment ON tx_shipment_line(shipment_id);
CREATE INDEX IF NOT EXISTS idx_tx_sl_product ON tx_shipment_line(product_id);
CREATE INDEX IF NOT EXISTS idx_tx_sl_tenant ON tx_shipment_line(tenant_id);
