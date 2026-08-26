-- ============================================================
-- V33 — Add vendor fields to Business Partner (PRD-010 TASK-093)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Add columns to md_business_partner (idempotent)
-- ------------------------------------------------------------
ALTER TABLE md_business_partner
  ADD COLUMN IF NOT EXISTS is_vendor               BOOLEAN NOT NULL DEFAULT false,
  ADD COLUMN IF NOT EXISTS vendor_category_id      UUID,
  ADD COLUMN IF NOT EXISTS default_purchase_currency VARCHAR(3) DEFAULT 'USD';

CREATE INDEX IF NOT EXISTS idx_md_business_partner_vendor_category ON md_business_partner(vendor_category_id);

-- Note: payment_term_id was added in V18; ensure_column below is safe to re-run.

-- ------------------------------------------------------------
-- Part 2 — Register new columns in sys_column metadata
-- ------------------------------------------------------------
SELECT ensure_column('md_business_partner', 'is_vendor',               'Is Vendor',               'boolean',  false, null, 100, null, false);
SELECT ensure_column('md_business_partner', 'vendor_category_id',      'Vendor Category',         'many2one', false, null, 110, 'pur_vendor_category', false);
SELECT ensure_column('md_business_partner', 'default_purchase_currency', 'Default Purchase Currency', 'string', false, 3, 120, null, false);
SELECT ensure_column('md_business_partner', 'payment_term_id',         'Payment Term',            'many2one', false, null, 130, 'md_payment_term', false);

-- ------------------------------------------------------------
-- Part 3 — Add fields to Business Partners window (Vendor section)
-- ------------------------------------------------------------
SELECT ensure_field('Business Partners', 10, 'is_vendor',               100, false, true, false, false);
SELECT ensure_field('Business Partners', 10, 'vendor_category_id',      110, false, true, false, false);
SELECT ensure_field('Business Partners', 10, 'default_purchase_currency', 120, false, true, false, false);
SELECT ensure_field('Business Partners', 10, 'payment_term_id',         130, false, true, false, false);
