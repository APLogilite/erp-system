-- ============================================================
-- V10 — Document Sequence table + seed data (PRD-006 TASK-060)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Create table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_document_sequence (
  id              UUID NOT NULL DEFAULT gen_random_uuid(),
  sequence_name   VARCHAR(100) NOT NULL,
  prefix          VARCHAR(10)  NOT NULL,
  next_value      INTEGER      NOT NULL DEFAULT 1,
  padding         INTEGER      NOT NULL DEFAULT 4,
  current_year    INTEGER,
  tenant_id       UUID         NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active       BOOLEAN      NOT NULL DEFAULT true,
  created_at      TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMP    NOT NULL DEFAULT now(),
  created_by      UUID,
  updated_by      UUID,
  deleted_at      TIMESTAMP,
  CONSTRAINT pk_sys_document_sequence PRIMARY KEY (id),
  CONSTRAINT uq_sys_document_sequence_name UNIQUE (sequence_name, tenant_id)
);

-- ------------------------------------------------------------
-- Part 2 — Seed default sequences
-- ------------------------------------------------------------
INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'quotation',       'QT-',  1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='quotation' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'sales_order',     'SO-',  1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='sales_order' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'sales_invoice',   'INV-', 1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='sales_invoice' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'purchase_order',  'PO-',  1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='purchase_order' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'purchase_invoice','PIN-', 1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='purchase_invoice' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'payment',         'PAY-', 1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='payment' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'shipment',        'SHP-', 1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='shipment' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'lead',            'LD-',  1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='lead' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'opportunity',     'OPP-', 1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='opportunity' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'return',          'RMA-', 1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='return' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO sys_document_sequence (id, sequence_name, prefix, next_value, padding, tenant_id)
SELECT gen_random_uuid(), 'credit_memo',     'CM-',  1, 4, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM sys_document_sequence WHERE sequence_name='credit_memo' AND tenant_id='00000000-0000-0000-0000-000000000001');

-- ------------------------------------------------------------
-- Part 3 — Register in sys_table metadata (for form engine)
-- ------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_document_sequence', 'Document Sequence', 'Document Sequences', 'static', 'sys_document_sequence',
       'Auto-numbering sequences for business documents', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_document_sequence');

-- Part 3b — Register columns in sys_column
DO $$
DECLARE
  v_table_id UUID;
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'sys_document_sequence';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'sequence_name', 'Name',       'string', true,  100, 1, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'sequence_name');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'prefix',        'Prefix',     'string', true,  10,  2, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'prefix');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'next_value',    'Next Value', 'integer', true, null, 3, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'next_value');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'padding',       'Padding',    'integer', false, null, 4, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'padding');
  END IF;
END
$$;
