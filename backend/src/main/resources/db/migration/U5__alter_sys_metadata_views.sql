-- ============================================================
-- UNDO: Remove columns from sys_metadata_views
-- ============================================================

ALTER TABLE sys_metadata_views
    DROP COLUMN IF EXISTS scope,
    DROP COLUMN IF EXISTS tenant_id,
    DROP COLUMN IF EXISTS description,
    DROP COLUMN IF EXISTS where_clause_field,
    DROP COLUMN IF EXISTS where_clause_operator,
    DROP COLUMN IF EXISTS where_clause_value;
