-- ============================================================
-- V23 — Add discount_amount column to tx_order_line (BUG-001)
-- ============================================================

ALTER TABLE tx_order_line
ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(15,2) DEFAULT 0;

-- Register column in metadata
SELECT ensure_column('tx_order_line', 'discount_amount', 'Discount', 'decimal', false, null, 8);

-- Add field to Sales Orders → Lines tab
SELECT ensure_field('Sales Orders', 20, 'discount_amount', 75, false, true, false, false);

-- Add field to Purchase Orders → Lines tab
SELECT ensure_field('Purchase Orders', 20, 'discount_amount', 75, false, true, false, false);
