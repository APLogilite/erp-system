-- ============================================================
-- METADATA — Add columns to sys_metadata_versions
-- for schema history tracking
-- ============================================================

ALTER TABLE sys_metadata_versions
    ADD COLUMN IF NOT EXISTS table_id UUID,
    ADD COLUMN IF NOT EXISTS definition_snapshot JSONB,
    ADD COLUMN IF NOT EXISTS changed_by UUID;

-- Drop the existing unique constraint on version (it should be unique per table_id, not globally)
-- and replace with a unique constraint on (table_id, version)
-- Note: DROP CONSTRAINT IF EXISTS is PostgreSQL 9.x+ compatible
ALTER TABLE sys_metadata_versions DROP CONSTRAINT IF EXISTS sys_metadata_versions_version_key;

-- Add new unique constraint scoped to table_id
ALTER TABLE sys_metadata_versions ADD CONSTRAINT sys_metadata_versions_table_version_key UNIQUE (table_id, version);

CREATE INDEX IF NOT EXISTS idx_sys_metadata_versions_table ON sys_metadata_versions(table_id);
CREATE INDEX IF NOT EXISTS idx_sys_metadata_versions_changed_by ON sys_metadata_versions(changed_by);
