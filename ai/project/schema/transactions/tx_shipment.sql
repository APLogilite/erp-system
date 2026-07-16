-- Table: tx_shipment
-- Created: V20
CREATE TABLE tx_shipment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    shipment_number VARCHAR(50) NOT NULL,
    shipment_date DATE NOT NULL,
    shipment_type VARCHAR(20) NOT NULL,
    partner_id UUID NOT NULL,
    warehouse_id UUID,
    order_id UUID,
    tracking_number VARCHAR(100),
    carrier VARCHAR(100),
    status VARCHAR(30) DEFAULT 'draft',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_ship_partner ON tx_shipment(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_ship_warehouse ON tx_shipment(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_tx_ship_order ON tx_shipment(order_id);
CREATE INDEX IF NOT EXISTS idx_tx_ship_type ON tx_shipment(shipment_type);
CREATE INDEX IF NOT EXISTS idx_tx_ship_tenant ON tx_shipment(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_ship_status ON tx_shipment(status);
