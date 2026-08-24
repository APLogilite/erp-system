-- ============================================================
-- V32 — Add territory_id to md_business_partner (PRD-008 TASK-078)
-- ============================================================

ALTER TABLE md_business_partner
  ADD COLUMN IF NOT EXISTS territory_id UUID REFERENCES md_sales_territory(id);

CREATE INDEX IF NOT EXISTS idx_md_business_partner_territory ON md_business_partner(territory_id);

DO $$
DECLARE
  v_table_id UUID;
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'md_business_partner';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'territory_id', 'Territory', 'many2one', false, null,
      COALESCE((SELECT MAX(position) FROM sys_column WHERE table_id = v_table_id), 0) + 1,
      false, 'md_sales_territory', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'territory_id');
  END IF;
END
$$;
