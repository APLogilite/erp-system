CREATE TABLE IF NOT EXISTS md_payment_term (
  id                UUID NOT NULL DEFAULT gen_random_uuid(),
  code              VARCHAR(20)  NOT NULL,
  name              VARCHAR(100) NOT NULL,
  days              INTEGER      NOT NULL,
  discount_percent  DECIMAL(5,2),
  discount_days     INTEGER,
  tenant_id         UUID         NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active         BOOLEAN      NOT NULL DEFAULT true,
  created_at        TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at        TIMESTAMP    NOT NULL DEFAULT now(),
  created_by        UUID,
  updated_by        UUID,
  deleted_at        TIMESTAMP,
  CONSTRAINT pk_md_payment_term PRIMARY KEY (id),
  CONSTRAINT uq_md_payment_term_code UNIQUE (code, tenant_id)
);
