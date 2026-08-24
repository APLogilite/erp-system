-- ============================================================
-- V27 — Lead table + seed data + metadata (PRD-008 TASK-079)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Create table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tx_lead (
  id                      UUID NOT NULL DEFAULT gen_random_uuid(),
  lead_number             VARCHAR(50)   NOT NULL,
  company_name            VARCHAR(200)  NOT NULL,
  contact_name            VARCHAR(200)  NOT NULL,
  email                   VARCHAR(100),
  phone                   VARCHAR(30),
  source                  VARCHAR(30),
  status                  VARCHAR(20)   NOT NULL DEFAULT 'new',
  score                   VARCHAR(10),
  salesperson_id          UUID,
  territory_id            UUID,
  expected_revenue        DECIMAL(15,2),
  notes                   TEXT,
  converted_partner_id    UUID,
  converted_opportunity_id UUID,
  tenant_id               UUID          NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active               BOOLEAN       NOT NULL DEFAULT true,
  created_at              TIMESTAMP     NOT NULL DEFAULT now(),
  updated_at              TIMESTAMP     NOT NULL DEFAULT now(),
  created_by              UUID,
  updated_by              UUID,
  deleted_at              TIMESTAMP,
  CONSTRAINT pk_tx_lead PRIMARY KEY (id),
  CONSTRAINT uq_tx_lead_number UNIQUE (lead_number, tenant_id),
  CONSTRAINT fk_tx_lead_salesperson FOREIGN KEY (salesperson_id) REFERENCES identity_users(id),
  CONSTRAINT fk_tx_lead_territory FOREIGN KEY (territory_id) REFERENCES md_sales_territory(id),
  CONSTRAINT fk_tx_lead_partner FOREIGN KEY (converted_partner_id) REFERENCES md_business_partner(id)
);

CREATE INDEX IF NOT EXISTS idx_tx_lead_status ON tx_lead(status);
CREATE INDEX IF NOT EXISTS idx_tx_lead_territory ON tx_lead(territory_id);
CREATE INDEX IF NOT EXISTS idx_tx_lead_salesperson ON tx_lead(salesperson_id);

-- ------------------------------------------------------------
-- Part 2 — Seed sample leads
-- ------------------------------------------------------------
INSERT INTO tx_lead (id, lead_number, company_name, contact_name, email, phone, source, status, score, expected_revenue, tenant_id)
SELECT gen_random_uuid(), 'LD-0001', 'Acme Prospects Inc', 'Wile E. Coyote', 'wile@acme.example', '555-0101', 'website', 'new', 'warm', 25000.00, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM tx_lead WHERE lead_number='LD-0001' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO tx_lead (id, lead_number, company_name, contact_name, email, phone, source, status, score, expected_revenue, tenant_id)
SELECT gen_random_uuid(), 'LD-0002', 'Globex Referrals LLC', 'Hank Scorpio', 'hank@globex.example', '555-0102', 'referral', 'contacted', 'hot', 75000.00, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM tx_lead WHERE lead_number='LD-0002' AND tenant_id='00000000-0000-0000-0000-000000000001');

-- ------------------------------------------------------------
-- Part 3 — Register in sys_table / sys_column metadata
-- ------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_lead', 'Lead', 'Leads', 'transaction', 'tx_lead',
       'CRM lead / prospect', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_lead');

DO $$
DECLARE
  v_table_id UUID;
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'tx_lead';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'lead_number',             'Lead Number',            'string',  true,  50,  1, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'lead_number');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'company_name',            'Company Name',           'string',  true, 200,  2, true,  null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'company_name');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'contact_name',            'Contact Name',           'string',  true, 200,  3, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'contact_name');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'email',                   'Email',                  'string',  false, 100,  4, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'email');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'phone',                   'Phone',                  'string',  false,  30,  5, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'phone');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, enum_options, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'source',                  'Source',                 'enum',    false,  30,  6, false, null,              '["website","referral","cold_call","trade_show","advertisement","other"]', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'source');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, enum_options, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'status',                  'Status',                 'enum',    true,   20,  7, false, null,              '["new","contacted","qualified","converted","lost"]', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'status');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, enum_options, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'score',                   'Score',                  'enum',    false,  10,  8, false, null,              '["hot","warm","cold"]', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'score');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'salesperson_id',          'Salesperson',            'many2one', false, null, 9, false, 'identity_users', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'salesperson_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'territory_id',            'Territory',              'many2one', false, null, 10, false, 'md_sales_territory', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'territory_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'expected_revenue',        'Expected Revenue',       'decimal', false, null, 11, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'expected_revenue');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'notes',                   'Notes',                  'text',    false, null, 12, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'notes');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'converted_partner_id',    'Converted Customer',     'many2one', false, null, 13, false, 'md_business_partner', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'converted_partner_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'converted_opportunity_id','Converted Opportunity',  'many2one', false, null, 14, false, 'tx_opportunity',  true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'converted_opportunity_id');
  END IF;
END
$$;
