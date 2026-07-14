-- Table: tx_payment
-- Created: V20
CREATE TABLE tx_payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    payment_number VARCHAR(50) NOT NULL,
    payment_date DATE NOT NULL,
    payment_type VARCHAR(20) NOT NULL,
    partner_id UUID NOT NULL,
    payment_method VARCHAR(30),
    currency VARCHAR(3) DEFAULT 'USD',
    amount NUMERIC(15,2) NOT NULL,
    reference VARCHAR(100),
    notes TEXT,
    status VARCHAR(30) DEFAULT 'draft',
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_pay_partner ON tx_payment(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_pay_type ON tx_payment(payment_type);
CREATE INDEX IF NOT EXISTS idx_tx_pay_tenant ON tx_payment(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tx_pay_status ON tx_payment(status);
