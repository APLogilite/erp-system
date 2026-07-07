-- ============================================================
-- METADATA — Add columns to sys_metadata_models
-- ============================================================

ALTER TABLE sys_metadata_models
    ADD COLUMN IF NOT EXISTS table_type VARCHAR(20),
    ADD COLUMN IF NOT EXISTS table_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS description TEXT;

CREATE INDEX IF NOT EXISTS idx_sys_metadata_models_table_type ON sys_metadata_models(table_type);
