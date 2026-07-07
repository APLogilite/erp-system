-- ============================================================
-- METADATA — Add columns to sys_metadata_views
-- ============================================================

ALTER TABLE sys_metadata_views
    ADD COLUMN IF NOT EXISTS scope VARCHAR(20),
    ADD COLUMN IF NOT EXISTS tenant_id UUID,
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS where_clause_field VARCHAR(100),
    ADD COLUMN IF NOT EXISTS where_clause_operator VARCHAR(50),
    ADD COLUMN IF NOT EXISTS where_clause_value VARCHAR(255);

CREATE INDEX IF NOT EXISTS idx_sys_metadata_views_scope ON sys_metadata_views(scope);
CREATE INDEX IF NOT EXISTS idx_sys_metadata_views_tenant ON sys_metadata_views(tenant_id);
