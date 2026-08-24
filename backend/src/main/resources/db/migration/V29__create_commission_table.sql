-- ============================================================
-- V29 — Commission table + metadata (PRD-008 TASK-081)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Create table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tx_commission (
  id                  UUID NOT NULL DEFAULT gen_random_uuid(),
  salesperson_id      UUID          NOT NULL,
  opportunity_id      UUID          NOT NULL,
  order_id            UUID,
  commission_percent  DECIMAL(5,2)  NOT NULL,
  commission_amount   DECIMAL(15,2) NOT NULL,
  status              VARCHAR(20)   NOT NULL DEFAULT 'pending',
  payment_date        DATE,
  tenant_id           UUID          NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active           BOOLEAN       NOT NULL DEFAULT true,
  created_at          TIMESTAMP     NOT NULL DEFAULT now(),
  updated_at          TIMESTAMP     NOT NULL DEFAULT now(),
  created_by          UUID,
  updated_by          UUID,
  deleted_at          TIMESTAMP,
  CONSTRAINT pk_tx_commission PRIMARY KEY (id),
  CONSTRAINT fk_tx_commission_salesperson FOREIGN KEY (salesperson_id) REFERENCES identity_users(id),
  CONSTRAINT fk_tx_commission_opportunity FOREIGN KEY (opportunity_id) REFERENCES tx_opportunity(id),
  CONSTRAINT fk_tx_commission_order FOREIGN KEY (order_id) REFERENCES tx_order(id)
);

CREATE INDEX IF NOT EXISTS idx_tx_commission_status ON tx_commission(status);
CREATE INDEX IF NOT EXISTS idx_tx_commission_opportunity ON tx_commission(opportunity_id);
CREATE INDEX IF NOT EXISTS idx_tx_commission_salesperson ON tx_commission(salesperson_id);

-- ------------------------------------------------------------
-- Part 2 — Register in sys_table / sys_column metadata
-- ------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_commission', 'Commission', 'Commissions', 'transaction', 'tx_commission',
       'Salesperson commission tracking', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_commission');

DO $$
DECLARE
  v_table_id UUID;
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'tx_commission';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'salesperson_id',     'Salesperson',        'many2one', true,  null, 1, false, 'identity_users', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'salesperson_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'opportunity_id',     'Opportunity',        'many2one', true,  null, 2, false, 'tx_opportunity', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'opportunity_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'order_id',           'Order',              'many2one', false, null, 3, false, 'tx_order',       true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'order_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'commission_percent', 'Commission %',       'decimal',  true,  null, 4, false, null,             true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'commission_percent');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'commission_amount',  'Commission Amount',  'decimal',  true,  null, 5, false, null,             true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'commission_amount');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, enum_options, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'status',             'Status',             'enum',     true,  20,   6, false, null,             '["pending","approved","paid"]', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'status');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'payment_date',       'Payment Date',       'date',     false, null, 7, false, null,             true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'payment_date');
  END IF;
END
$$;
