-- ============================================================
-- V20 — Order status log table + metadata (PRD-007 TASK-073)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Create tx_order_status_log table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tx_order_status_log (
  id               UUID NOT NULL DEFAULT gen_random_uuid(),
  order_id         UUID NOT NULL REFERENCES tx_order(id),
  from_status      VARCHAR(30),
  to_status        VARCHAR(30) NOT NULL,
  transitioned_by  UUID,
  transitioned_at  TIMESTAMP NOT NULL DEFAULT now(),
  tenant_id        UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active        BOOLEAN NOT NULL DEFAULT true,
  created_at       TIMESTAMP NOT NULL DEFAULT now(),
  updated_at       TIMESTAMP NOT NULL DEFAULT now(),
  created_by       UUID,
  updated_by       UUID,
  deleted_at       TIMESTAMP,
  CONSTRAINT pk_tx_order_status_log PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_tx_order_status_log_order ON tx_order_status_log(order_id);
CREATE INDEX IF NOT EXISTS idx_tx_order_status_log_tenant ON tx_order_status_log(tenant_id);

-- ------------------------------------------------------------
-- Part 2 — Register in sys_table / sys_column metadata
-- ------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_order_status_log', 'Order Status Log', 'Order Status Logs', 'static', 'tx_order_status_log',
       'Audit log of order status transitions', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_order_status_log');

DO $$
DECLARE
  v_table_id UUID;
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'tx_order_status_log';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'order_id',        'Order',        'many2one', true, null, 1, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'order_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'from_status',     'From Status',  'string',   false, 30, 2, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'from_status');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'to_status',       'To Status',    'string',   true,  30, 3, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'to_status');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'transitioned_by', 'Transitioned By', 'many2one', false, null, 4, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'transitioned_by');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'transitioned_at', 'Transitioned At', 'datetime', false, null, 5, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'transitioned_at');
  END IF;
END
$$;
