-- Centralized DDL reference for pur_price_list_line

CREATE TABLE pur_price_list_line (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    price_list_id   UUID NOT NULL REFERENCES pur_price_list(id),
    product_id      UUID NOT NULL REFERENCES md_product(id),
    uom_id          UUID REFERENCES md_uom(id),
    min_qty         NUMERIC(15,4) NOT NULL DEFAULT 0,
    unit_price      NUMERIC(15,4) NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    deleted_at      TIMESTAMP,
    CONSTRAINT uq_pur_price_list_line UNIQUE (price_list_id, product_id, uom_id, min_qty)
);

CREATE INDEX idx_pur_price_list_line_list ON pur_price_list_line(price_list_id);
CREATE INDEX idx_pur_price_list_line_product ON pur_price_list_line(product_id);
CREATE INDEX idx_pur_price_list_line_tenant ON pur_price_list_line(tenant_id);
