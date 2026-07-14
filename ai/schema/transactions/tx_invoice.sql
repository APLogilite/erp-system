-- Table: tx_invoice
-- Created: V20
CREATE TABLE tx_invoice (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE,
    invoice_type VARCHAR(20) NOT NULL,
    partner_id UUID NOT NULL,
    order_id UUID,
    currency VARCHAR(3) DEFAULT 'USD',
    subtotal NUMERIC(15,2) DEFAULT 0,
    tax_amount NUMERIC(15,2) DEFAULT 0,
    discount_amount NUMERIC(15,2) DEFAULT 0,
    grand_total NUMERIC(15,2) DEFAULT 0,
    paid_amount NUMERIC(15,2) DEFAULT 0,
    due_amount NUMERIC(15,2) DEFAULT 0,
    status VARCHAR(30) DEFAULT 'draft',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_inv_partner ON tx_invoice(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_inv_order ON tx_invoice(order_id);
CREATE INDEX IF NOT EXISTS idx_tx_inv_type ON tx_invoice(invoice_type);
CREATE INDEX IF NOT EXISTS idx_tx_inv_tenant ON tx_invoice(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_inv_status ON tx_invoice(status);
