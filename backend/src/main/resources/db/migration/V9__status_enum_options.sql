-- ============================================================
-- V9 — Set status columns to enum type with valid options
--
-- FormFieldRenderer renders a dropdown only when sys_column.type
-- is 'enum' and enum_options is a populated JSONB array. The
-- 4 transaction status columns were seeded as type='string' with
-- no enum_options, so users saw a plain text input instead of a
-- dropdown. This migration corrects them.
--
-- IDEMPOTENT: UPSERT-style via subquery; safe on any DB.
-- ============================================================

-- tx_order.status (Sales Orders / Purchase Orders)
UPDATE sys_column
SET type = 'enum',
    enum_options = '["draft","confirmed","shipped","delivered","cancelled"]'::jsonb
WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order')
  AND code = 'status';

-- tx_invoice.status (Sales Invoices / Purchase Invoices)
UPDATE sys_column
SET type = 'enum',
    enum_options = '["draft","posted","paid","cancelled"]'::jsonb
WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice')
  AND code = 'status';

-- tx_payment.status
UPDATE sys_column
SET type = 'enum',
    enum_options = '["pending","completed","failed","refunded"]'::jsonb
WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment')
  AND code = 'status';

-- tx_shipment.status
UPDATE sys_column
SET type = 'enum',
    enum_options = '["draft","in_transit","delivered","returned"]'::jsonb
WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment')
  AND code = 'status';
