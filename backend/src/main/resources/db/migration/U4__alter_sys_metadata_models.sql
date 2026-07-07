-- ============================================================
-- UNDO: Remove columns from sys_metadata_models
-- ============================================================

ALTER TABLE sys_metadata_models
    DROP COLUMN IF EXISTS table_type,
    DROP COLUMN IF EXISTS table_name,
    DROP COLUMN IF EXISTS description;
