-- Table: tx_order_status_log
-- Created: V20 (PRD-007)
CREATE TABLE tx_order_status_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES tx_order(id),
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    transitioned_by UUID,
    transitioned_at TIMESTAMP NOT NULL DEFAULT now(),
    tenant_id UUID NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_order_status_log_order ON tx_order_status_log(order_id);
CREATE INDEX IF NOT EXISTS idx_tx_order_status_log_tenant ON tx_order_status_log(tenant_id);
