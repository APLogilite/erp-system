-- ============================================================
-- V18 — Add credit limit, credit hold, and payment term to Business Partner (PRD-007 TASK-071)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Add columns to md_business_partner (idempotent)
-- ------------------------------------------------------------
ALTER TABLE md_business_partner
  ADD COLUMN IF NOT EXISTS credit_limit    DECIMAL(15,2),
  ADD COLUMN IF NOT EXISTS credit_hold     BOOLEAN NOT NULL DEFAULT false,
  ADD COLUMN IF NOT EXISTS payment_term_id UUID REFERENCES md_payment_term(id);

-- ------------------------------------------------------------
-- Part 2 — Register new columns in sys_column metadata
-- ------------------------------------------------------------
SELECT ensure_column('md_business_partner', 'credit_limit',    'Credit Limit',    'decimal',  false, null, 7, null, false);
SELECT ensure_column('md_business_partner', 'credit_hold',     'Credit Hold',     'boolean',  false, null, 8, null, false);
SELECT ensure_column('md_business_partner', 'payment_term_id', 'Payment Term',    'many2one', false, null, 9, 'md_payment_term', false);

-- ------------------------------------------------------------
-- Part 3 — Add fields to Business Partners window (Credit & Terms section)
-- ------------------------------------------------------------
SELECT ensure_field('Business Partners', 10, 'credit_limit',    70, false, true, false, false);
SELECT ensure_field('Business Partners', 10, 'credit_hold',     80, false, true, false, false);
SELECT ensure_field('Business Partners', 10, 'payment_term_id', 90, false, true, false, false);
