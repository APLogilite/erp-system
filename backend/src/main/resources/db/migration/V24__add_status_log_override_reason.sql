-- ============================================================
-- V24 — Add override_reason to tx_order_status_log (BUG-002)
-- ============================================================

ALTER TABLE tx_order_status_log
ADD COLUMN IF NOT EXISTS override_reason TEXT;

-- Register column in metadata
SELECT ensure_column('tx_order_status_log', 'override_reason', 'Override Reason', 'text', false, null, 20);
