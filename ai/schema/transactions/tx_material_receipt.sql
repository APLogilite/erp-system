-- Table: tx_material_receipt
-- Created: V20
CREATE TABLE tx_material_receipt (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    receipt_number VARCHAR(50) NOT NULL,
    receipt_date DATE NOT NULL,
    partner_id UUID NOT NULL,
    warehouse_id UUID,
    order_id UUID,
    shipment_id UUID,
    reference VARCHAR(100),
    status VARCHAR(30) DEFAULT 'draft',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_mr_partner ON tx_material_receipt(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_warehouse ON tx_material_receipt(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_order ON tx_material_receipt(order_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_shipment ON tx_material_receipt(shipment_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_tenant ON tx_material_receipt(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_mr_status ON tx_material_receipt(status);
