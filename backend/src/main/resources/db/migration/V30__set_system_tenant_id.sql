-- ============================================================
-- PRD-004 / BUG-009 — Set System Tenant ID on All Seed Records
--
-- The SYS tenant uses a fixed UUID (00000000-0000-0000-0000-000000000001)
-- so that all seed data inserted by Flyway migrations gets a valid
-- tenant_id. This ensures strict tenant isolation works for all records.
--
-- This migration updates any records with NULL tenant_id to the
-- SYS tenant UUID.
-- ============================================================

DO $$
DECLARE
    v_sys_tenant UUID := '00000000-0000-0000-0000-000000000001';
BEGIN
    -- V1 identity tables that have tenant_id column
    UPDATE identity_organizations   SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;
    UPDATE identity_roles           SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;
    UPDATE identity_user_sessions   SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;

    -- V24 metadata tables (all have tenant_id)
    UPDATE sys_table               SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;
    UPDATE sys_column              SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;
    UPDATE sys_window              SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;
    UPDATE sys_tab                 SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;
    UPDATE sys_window_field        SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;
    UPDATE sys_window_access       SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;
    UPDATE sys_menu                SET tenant_id = v_sys_tenant WHERE tenant_id IS NULL;
END;
$$ LANGUAGE plpgsql;
