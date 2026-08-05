-- ============================================================
-- V12 — Quotation tables + metadata (PRD-006 TASK-063)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — tx_quotation (header)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tx_quotation (
  id                UUID NOT NULL DEFAULT gen_random_uuid(),
  quote_number      VARCHAR(50)  NOT NULL,
  quote_date        DATE         NOT NULL DEFAULT CURRENT_DATE,
  valid_until       DATE,
  partner_id        UUID         NOT NULL,
  price_list_id     UUID,
  currency          VARCHAR(3)   DEFAULT 'USD',
  subtotal          NUMERIC(15,2),
  discount_amount   NUMERIC(15,2),
  tax_amount        NUMERIC(15,2),
  grand_total       NUMERIC(15,2),
  status            VARCHAR(20)  NOT NULL DEFAULT 'draft',
  notes             TEXT,
  converted_order_id UUID,
  tenant_id         UUID         NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active         BOOLEAN      NOT NULL DEFAULT true,
  created_at        TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at        TIMESTAMP    NOT NULL DEFAULT now(),
  created_by        UUID,
  updated_by        UUID,
  deleted_at        TIMESTAMP,
  CONSTRAINT pk_tx_quotation PRIMARY KEY (id),
  CONSTRAINT fk_quotation_partner FOREIGN KEY (partner_id) REFERENCES md_business_partner(id),
  CONSTRAINT fk_quotation_price_list FOREIGN KEY (price_list_id) REFERENCES md_price_list(id),
  CONSTRAINT fk_quotation_converted_order FOREIGN KEY (converted_order_id) REFERENCES tx_order(id)
);

-- ------------------------------------------------------------
-- Part 2 — tx_quotation_line (lines)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tx_quotation_line (
  id                UUID NOT NULL DEFAULT gen_random_uuid(),
  quotation_id      UUID         NOT NULL,
  line_number       INTEGER      NOT NULL,
  product_id        UUID         NOT NULL,
  description       TEXT,
  quantity          NUMERIC(15,3) NOT NULL DEFAULT 1,
  uom_id            UUID,
  unit_price        NUMERIC(15,2) NOT NULL,
  discount_percent  NUMERIC(5,2),
  discount_amount   NUMERIC(15,2),
  line_total        NUMERIC(15,2),
  tax_rate          NUMERIC(5,2),
  tenant_id         UUID         NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active         BOOLEAN      NOT NULL DEFAULT true,
  created_at        TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at        TIMESTAMP    NOT NULL DEFAULT now(),
  created_by        UUID,
  updated_by        UUID,
  deleted_at        TIMESTAMP,
  CONSTRAINT pk_tx_quotation_line PRIMARY KEY (id),
  CONSTRAINT fk_quotation_line_header FOREIGN KEY (quotation_id) REFERENCES tx_quotation(id) ON DELETE CASCADE,
  CONSTRAINT fk_quotation_line_product FOREIGN KEY (product_id) REFERENCES md_product(id),
  CONSTRAINT fk_quotation_line_uom FOREIGN KEY (uom_id) REFERENCES md_uom(id)
);

CREATE INDEX IF NOT EXISTS idx_quotation_line_quotation ON tx_quotation_line(quotation_id);

-- ------------------------------------------------------------
-- Part 3 — Register in sys_table
-- ------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_quotation', 'Quotation', 'Quotations', 'static', 'tx_quotation',
       'Sales quotation header', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_quotation');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_quotation_line', 'Quotation Line', 'Quotation Lines', 'static', 'tx_quotation_line',
       'Individual line items within a quotation', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_quotation_line');

-- ------------------------------------------------------------
-- Part 4 — Register columns in sys_column
-- ------------------------------------------------------------
-- tx_quotation columns
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'quote_number', 'Quote #', 'string', true, 50, 1, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='quote_number');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'quote_date', 'Date', 'date', true, null, 2, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='quote_date');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'valid_until', 'Valid Until', 'date', false, null, 3, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='valid_until');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'partner_id', 'Customer', 'many2one', true, null, 4, 'md_business_partner', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='partner_id');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'price_list_id', 'Price List', 'many2one', false, null, 5, 'md_price_list', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='price_list_id');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'currency', 'Currency', 'string', false, 3, 6, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='currency');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'subtotal', 'Subtotal', 'decimal', false, null, 7, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='subtotal');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'grand_total', 'Grand Total', 'decimal', false, null, 8, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='grand_total');

-- status column: enum with dropdown options (BUG-014 pattern)
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'status', 'Status', 'enum', true, 20, 9, '["draft","sent","accepted","rejected","converted","expired"]'::jsonb, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='status');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'notes', 'Notes', 'text', false, null, 10, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='notes');

-- tx_quotation_line columns
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'quotation_id', 'Quotation', 'many2one', true, null, 1, 'tx_quotation', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation_line' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='quotation_id');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'line_number', 'Line #', 'integer', true, null, 2, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation_line' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='line_number');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'product_id', 'Product', 'many2one', true, null, 3, 'md_product', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation_line' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='product_id');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'description', 'Description', 'text', false, null, 4, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation_line' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='description');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'quantity', 'Quantity', 'decimal', true, null, 5, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation_line' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='quantity');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'uom_id', 'UOM', 'many2one', false, null, 6, 'md_uom', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation_line' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='uom_id');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'unit_price', 'Unit Price', 'decimal', true, null, 7, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation_line' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='unit_price');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'line_total', 'Line Total', 'decimal', false, null, 8, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='tx_quotation_line' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='line_total');
