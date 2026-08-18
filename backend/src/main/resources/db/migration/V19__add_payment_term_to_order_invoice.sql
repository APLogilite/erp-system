-- ============================================================
-- V19 — Add payment_term_id to tx_order and tx_invoice (PRD-007 TASK-072)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Add columns (idempotent)
-- ------------------------------------------------------------
ALTER TABLE tx_order
  ADD COLUMN IF NOT EXISTS payment_term_id UUID REFERENCES md_payment_term(id);

ALTER TABLE tx_invoice
  ADD COLUMN IF NOT EXISTS payment_term_id UUID REFERENCES md_payment_term(id);

-- ------------------------------------------------------------
-- Part 2 — Register columns in sys_column metadata
-- ------------------------------------------------------------
SELECT ensure_column('tx_order',   'payment_term_id', 'Payment Term', 'many2one', false, null, 100, 'md_payment_term', false);
SELECT ensure_column('tx_invoice', 'payment_term_id', 'Payment Term', 'many2one', false, null, 100, 'md_payment_term', false);

-- ------------------------------------------------------------
-- Part 3 — Add fields to Sales Orders and Sales Invoices windows
-- ------------------------------------------------------------
SELECT ensure_field('Sales Orders',   10, 'payment_term_id', 100, false, true, false, false);
SELECT ensure_field('Sales Invoices', 10, 'payment_term_id', 100, false, true, false, false);

-- Purchase Orders / Invoices also carry payment terms
SELECT ensure_field('Purchase Orders',   10, 'payment_term_id', 100, false, true, false, false);
SELECT ensure_field('Purchase Invoices', 10, 'payment_term_id', 100, false, true, false, false);
