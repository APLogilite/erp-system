-- ============================================================
-- Remove Shipments child tab from Sales Orders window
--
-- Sales Orders should only have Lines as a child tab.
-- Shipments is a standalone window with its own Lines child tab.
-- ============================================================

DO $$
DECLARE
  v_tab_id UUID;
  v_window_id UUID;
BEGIN
  -- Find the Shipments tab under Sales Orders
  SELECT st.id, st.window_id INTO v_tab_id, v_window_id
  FROM sys_tab st
  JOIN sys_window sw ON st.window_id = sw.id
  WHERE sw.name = 'Sales Orders' AND st.name = 'Shipments';

  IF v_tab_id IS NOT NULL THEN
    -- Delete fields for this tab
    DELETE FROM sys_window_field WHERE tab_id = v_tab_id;
    -- Delete the tab itself
    DELETE FROM sys_tab WHERE id = v_tab_id;
    RAISE NOTICE 'Removed Shipments child tab from Sales Orders window';
  ELSE
    RAISE NOTICE 'Shipments child tab not found in Sales Orders (already removed)';
  END IF;
END $$;
