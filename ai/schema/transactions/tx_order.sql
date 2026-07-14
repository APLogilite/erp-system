-- Table: tx_order
-- Created: V20
CREATE TABLE tx_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    order_number VARCHAR(50) NOT NULL,
    order_date DATE NOT NULL,
    order_type VARCHAR(20) NOT NULL,
    partner_id UUID NOT NULL,
    warehouse_id UUID,
    currency VARCHAR(3) DEFAULT 'USD',
    subtotal NUMERIC(15,2) DEFAULT 0,
    tax_amount NUMERIC(15,2) DEFAULT 0,
    discount_amount NUMERIC(15,2) DEFAULT 0,
    grand_total NUMERIC(15,2) DEFAULT 0,
    status VARCHAR(30) DEFAULT 'draft',
    expected_date DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_order_partner ON tx_order(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_order_warehouse ON tx_order(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_tx_order_type ON tx_order(order_type);
CREATE INDEX IF NOT EXISTS idx_tx_order_tenant ON tx_order(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_order_status ON tx_order(status);
