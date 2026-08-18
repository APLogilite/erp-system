-- ============================================================
-- V22 — Update Sales Order form for workflow and payment terms (PRD-007 TASK-077)
-- ============================================================

-- 1. Ensure discount_amount column is registered on tx_order.
SELECT ensure_column('tx_order', 'discount_amount', 'Discount', 'decimal', false, null, 11);

-- 2. Register discount_amount on Sales Orders header (read-only, displayed).
SELECT ensure_field('Sales Orders', 10, 'discount_amount', 75, false, true, true, false);

-- 3. Ensure computed totals and status are displayed and read-only.
UPDATE sys_window_field
SET is_readonly = true, is_displayed = true, is_mandatory = false
WHERE id IN (
  SELECT swf.id
  FROM sys_window_field swf
  JOIN sys_tab st ON swf.tab_id = st.id
  JOIN sys_window sw ON st.window_id = sw.id
  JOIN sys_column sc ON swf.column_id = sc.id
  JOIN sys_table tbl ON sc.table_id = tbl.id
  WHERE sw.name = 'Sales Orders'
    AND st.seq_no = 10
    AND tbl.name = 'tx_order'
    AND sc.code IN ('subtotal', 'tax_amount', 'discount_amount', 'grand_total', 'status')
);

-- 4. Ensure payment_term_id is present on Sales Orders header (V19 already seeds it; this is idempotent).
SELECT ensure_field('Sales Orders', 10, 'payment_term_id', 105, false, true, false, false);
