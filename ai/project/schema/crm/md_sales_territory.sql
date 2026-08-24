CREATE TABLE IF NOT EXISTS md_sales_territory (
  id         UUID NOT NULL DEFAULT gen_random_uuid(),
  code       VARCHAR(20)  NOT NULL,
  name       VARCHAR(100) NOT NULL,
  manager_id UUID,
  tenant_id  UUID         NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active  BOOLEAN      NOT NULL DEFAULT true,
  created_at TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at TIMESTAMP    NOT NULL DEFAULT now(),
  created_by UUID,
  updated_by UUID,
  deleted_at TIMESTAMP,
  CONSTRAINT pk_md_sales_territory PRIMARY KEY (id),
  CONSTRAINT uq_md_sales_territory_code UNIQUE (code, tenant_id),
  CONSTRAINT fk_md_sales_territory_manager FOREIGN KEY (manager_id) REFERENCES identity_users(id)
);
