-- ============================================================
-- V28 — Opportunity table + seed data + metadata (PRD-008 TASK-080)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Create table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tx_opportunity (
  id                      UUID NOT NULL DEFAULT gen_random_uuid(),
  opportunity_number      VARCHAR(50)   NOT NULL,
  name                    VARCHAR(200)  NOT NULL,
  partner_id              UUID          NOT NULL,
  lead_id                 UUID,
  stage                   VARCHAR(20)   NOT NULL DEFAULT 'qualification',
  probability             INTEGER,
  expected_revenue        DECIMAL(15,2) NOT NULL,
  expected_close_date     DATE,
  salesperson_id          UUID,
  territory_id            UUID,
  notes                   TEXT,
  converted_quotation_id  UUID,
  converted_order_id      UUID,
  tenant_id               UUID          NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active               BOOLEAN       NOT NULL DEFAULT true,
  created_at              TIMESTAMP     NOT NULL DEFAULT now(),
  updated_at              TIMESTAMP     NOT NULL DEFAULT now(),
  created_by              UUID,
  updated_by              UUID,
  deleted_at              TIMESTAMP,
  CONSTRAINT pk_tx_opportunity PRIMARY KEY (id),
  CONSTRAINT uq_tx_opportunity_number UNIQUE (opportunity_number, tenant_id),
  CONSTRAINT fk_tx_opportunity_partner FOREIGN KEY (partner_id) REFERENCES md_business_partner(id),
  CONSTRAINT fk_tx_opportunity_lead FOREIGN KEY (lead_id) REFERENCES tx_lead(id),
  CONSTRAINT fk_tx_opportunity_salesperson FOREIGN KEY (salesperson_id) REFERENCES identity_users(id),
  CONSTRAINT fk_tx_opportunity_territory FOREIGN KEY (territory_id) REFERENCES md_sales_territory(id)
);

CREATE INDEX IF NOT EXISTS idx_tx_opportunity_stage ON tx_opportunity(stage);
CREATE INDEX IF NOT EXISTS idx_tx_opportunity_partner ON tx_opportunity(partner_id);
CREATE INDEX IF NOT EXISTS idx_tx_opportunity_lead ON tx_opportunity(lead_id);
CREATE INDEX IF NOT EXISTS idx_tx_opportunity_salesperson ON tx_opportunity(salesperson_id);
CREATE INDEX IF NOT EXISTS idx_tx_opportunity_territory ON tx_opportunity(territory_id);

-- ------------------------------------------------------------
-- Part 2 — Seed sample opportunities
-- ------------------------------------------------------------
INSERT INTO tx_opportunity (id, opportunity_number, name, partner_id, stage, probability, expected_revenue, expected_close_date, tenant_id)
SELECT gen_random_uuid(), 'OPP-0001', 'Acme CRM Upgrade', p.id, 'qualification', 25, 50000.00, CURRENT_DATE + INTERVAL '30 days', '00000000-0000-0000-0000-000000000001'
FROM md_business_partner p WHERE p.code='ACME-CUST'
AND NOT EXISTS (SELECT 1 FROM tx_opportunity WHERE opportunity_number='OPP-0001' AND tenant_id='00000000-0000-0000-0000-000000000001');

-- ------------------------------------------------------------
-- Part 3 — Register in sys_table / sys_column metadata
-- ------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_opportunity', 'Opportunity', 'Opportunities', 'transaction', 'tx_opportunity',
       'CRM sales opportunity', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_opportunity');

DO $$
DECLARE
  v_table_id UUID;
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'tx_opportunity';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'opportunity_number',     'Opportunity Number',  'string',   true,  50,  1, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'opportunity_number');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'name',                   'Name',                'string',   true, 200,  2, true,  null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'name');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'partner_id',             'Customer',            'many2one', true,  null, 3, false, 'md_business_partner', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'partner_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'lead_id',                'Source Lead',         'many2one', false, null, 4, false, 'tx_lead',         true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'lead_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, enum_options, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'stage',                  'Stage',               'enum',     true,  20,  5, false, null,              '["qualification","proposal","negotiation","closed_won","closed_lost"]', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'stage');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'probability',            'Probability %',       'integer',  false, null, 6, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'probability');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'expected_revenue',       'Expected Revenue',    'decimal',  true,  null, 7, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'expected_revenue');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'expected_close_date',    'Expected Close Date', 'date',     false, null, 8, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'expected_close_date');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'salesperson_id',         'Salesperson',         'many2one', false, null, 9, false, 'identity_users', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'salesperson_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'territory_id',           'Territory',           'many2one', false, null, 10, false, 'md_sales_territory', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'territory_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'notes',                  'Notes',               'text',     false, null, 11, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'notes');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'converted_quotation_id', 'Converted Quotation', 'many2one', false, null, 12, false, 'tx_quotation',    true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'converted_quotation_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'converted_order_id',     'Converted Order',     'many2one', false, null, 13, false, 'tx_order',        true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'converted_order_id');
  END IF;
END
$$;
