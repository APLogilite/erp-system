-- Table: md_uom_conversion
-- Created: V19
CREATE TABLE md_uom_conversion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    from_uom_id UUID NOT NULL REFERENCES md_uom(id),
    to_uom_id UUID NOT NULL REFERENCES md_uom(id),
    product_id UUID REFERENCES md_product(id),
    factor NUMERIC(15,6) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_md_uom_conv_from ON md_uom_conversion(from_uom_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_to ON md_uom_conversion(to_uom_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_product ON md_uom_conversion(product_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_tenant ON md_uom_conversion(tenant_id);
