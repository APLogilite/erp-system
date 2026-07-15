-- ============================================================
-- V6 — Sample Data for All Business Tables
-- Realistic demo records for testing the UI
-- ============================================================

DO $$
DECLARE
  v_tenant_id CONSTANT UUID := '00000000-0000-0000-0000-000000000001';

  -- UOMs
  v_uom_pcs   UUID := gen_random_uuid();
  v_uom_kg    UUID := gen_random_uuid();
  v_uom_ltr   UUID := gen_random_uuid();
  v_uom_hr    UUID := gen_random_uuid();

  -- Partners
  v_partner_customer1 UUID := gen_random_uuid();
  v_partner_customer2 UUID := gen_random_uuid();
  v_partner_vendor1   UUID := gen_random_uuid();
  v_partner_vendor2   UUID := gen_random_uuid();

  -- Products
  v_product_laptop  UUID := gen_random_uuid();
  v_product_mouse   UUID := gen_random_uuid();
  v_product_service UUID := gen_random_uuid();
  v_product_raw_mat UUID := gen_random_uuid();
  v_product_pack    UUID := gen_random_uuid();
  v_product_freight UUID := gen_random_uuid();

  -- Warehouses
  v_warehouse_main UUID := gen_random_uuid();
  v_warehouse_sec  UUID := gen_random_uuid();

  -- Orders
  v_so_001 UUID := gen_random_uuid();
  v_so_002 UUID := gen_random_uuid();
  v_po_001 UUID := gen_random_uuid();
  v_po_002 UUID := gen_random_uuid();

  -- Invoices
  v_inv_s1 UUID := gen_random_uuid();
  v_inv_p1 UUID := gen_random_uuid();

  -- Payments
  v_pay_001 UUID := gen_random_uuid();
  v_pay_002 UUID := gen_random_uuid();

  -- Shipments
  v_ship_001 UUID := gen_random_uuid();
  v_ship_002 UUID := gen_random_uuid();

BEGIN

-- ============================================================
-- UOM
-- ============================================================
INSERT INTO md_uom (id, tenant_id, code, name, is_active, created_at, updated_at)
VALUES (v_uom_pcs, v_tenant_id, 'PCS', 'Piece', true, now(), now());
INSERT INTO md_uom (id, tenant_id, code, name, is_active, created_at, updated_at)
VALUES (v_uom_kg, v_tenant_id, 'KG', 'Kilogram', true, now(), now());
INSERT INTO md_uom (id, tenant_id, code, name, is_active, created_at, updated_at)
VALUES (v_uom_ltr, v_tenant_id, 'LTR', 'Litre', true, now(), now());
INSERT INTO md_uom (id, tenant_id, code, name, is_active, created_at, updated_at)
VALUES (v_uom_hr, v_tenant_id, 'HR', 'Hour', true, now(), now());

-- ============================================================
-- Business Partners
-- ============================================================
INSERT INTO md_business_partner (id, tenant_id, code, name, partner_type, email, phone, tax_id, is_active, created_at, updated_at)
VALUES (v_partner_customer1, v_tenant_id, 'CUST001', 'Acme Corporation', 'customer', 'info@acme.com', '+1-555-0101', 'TAX-AC-001', true, now(), now());
INSERT INTO md_business_partner (id, tenant_id, code, name, partner_type, email, phone, tax_id, is_active, created_at, updated_at)
VALUES (v_partner_customer2, v_tenant_id, 'CUST002', 'Globex Industries', 'customer', 'orders@globex.com', '+1-555-0102', 'TAX-GX-002', true, now(), now());
INSERT INTO md_business_partner (id, tenant_id, code, name, partner_type, email, phone, tax_id, is_active, created_at, updated_at)
VALUES (v_partner_vendor1, v_tenant_id, 'VEN001', 'TechSupply Co.', 'vendor', 'sales@techsupply.com', '+1-555-0201', 'TAX-TS-101', true, now(), now());
INSERT INTO md_business_partner (id, tenant_id, code, name, partner_type, email, phone, tax_id, is_active, created_at, updated_at)
VALUES (v_partner_vendor2, v_tenant_id, 'VEN002', 'RawMaterials Ltd.', 'vendor', 'info@rawmat.com', '+1-555-0202', 'TAX-RM-102', true, now(), now());

-- ============================================================
-- Products
-- ============================================================
INSERT INTO md_product (id, tenant_id, code, name, description, product_type, uom_id, unit_price, is_active, created_at, updated_at)
VALUES (v_product_laptop, v_tenant_id, 'PROD-LAP-001', 'Business Laptop 15"', '15.6" laptop, 16GB RAM, 512GB SSD', 'item', v_uom_pcs, 899.00, true, now(), now());
INSERT INTO md_product (id, tenant_id, code, name, description, product_type, uom_id, unit_price, is_active, created_at, updated_at)
VALUES (v_product_mouse, v_tenant_id, 'PROD-MOU-001', 'Wireless Mouse', 'Ergonomic wireless mouse', 'item', v_uom_pcs, 29.99, true, now(), now());
INSERT INTO md_product (id, tenant_id, code, name, description, product_type, uom_id, unit_price, is_active, created_at, updated_at)
VALUES (v_product_service, v_tenant_id, 'PROD-SVC-001', 'Consulting Service', 'IT consulting per hour', 'service', v_uom_hr, 150.00, true, now(), now());
INSERT INTO md_product (id, tenant_id, code, name, description, product_type, uom_id, unit_price, is_active, created_at, updated_at)
VALUES (v_product_raw_mat, v_tenant_id, 'PROD-RM-001', 'Steel Sheets', 'Galvanized steel sheets 2mm', 'item', v_uom_kg, 45.00, true, now(), now());
INSERT INTO md_product (id, tenant_id, code, name, description, product_type, uom_id, unit_price, is_active, created_at, updated_at)
VALUES (v_product_pack, v_tenant_id, 'PROD-PKG-001', 'Packaging Material', 'Corrugated cardboard boxes (set of 50)', 'item', v_uom_pcs, 35.00, true, now(), now());
INSERT INTO md_product (id, tenant_id, code, name, description, product_type, uom_id, unit_price, is_active, created_at, updated_at)
VALUES (v_product_freight, v_tenant_id, 'PROD-FRT-001', 'Freight Service', 'Standard freight delivery service', 'service', v_uom_pcs, 200.00, true, now(), now());

-- ============================================================
-- Warehouses
-- ============================================================
INSERT INTO md_warehouse (id, tenant_id, code, name, is_active, created_at, updated_at)
VALUES (v_warehouse_main, v_tenant_id, 'WH-MAIN', 'Main Warehouse', true, now(), now());
INSERT INTO md_warehouse (id, tenant_id, code, name, is_active, created_at, updated_at)
VALUES (v_warehouse_sec, v_tenant_id, 'WH-SEC', 'Secondary Warehouse', true, now(), now());

-- ============================================================
-- Sales Orders with Lines
-- ============================================================

-- SO-001 (Acme Corporation)
INSERT INTO tx_order (id, tenant_id, order_number, order_date, order_type, partner_id, warehouse_id, subtotal, tax_amount, grand_total, status, is_active, created_at, updated_at)
VALUES (v_so_001, v_tenant_id, 'SO-001', '2026-07-10', 'sales', v_partner_customer1, v_warehouse_main, 958.97, 95.90, 1054.87, 'completed', true, now(), now());
INSERT INTO tx_order_line (id, tenant_id, order_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_so_001, 10, v_product_laptop, 'Business Laptop 15"', 1, v_uom_pcs, 899.00, 899.00, true, now(), now());
INSERT INTO tx_order_line (id, tenant_id, order_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_so_001, 20, v_product_mouse, 'Wireless Mouse', 2, v_uom_pcs, 29.99, 59.98, true, now(), now());

-- SO-002 (Globex Industries)
INSERT INTO tx_order (id, tenant_id, order_number, order_date, order_type, partner_id, warehouse_id, subtotal, tax_amount, grand_total, status, is_active, created_at, updated_at)
VALUES (v_so_002, v_tenant_id, 'SO-002', '2026-07-12', 'sales', v_partner_customer2, v_warehouse_main, 450.00, 45.00, 495.00, 'draft', true, now(), now());
INSERT INTO tx_order_line (id, tenant_id, order_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_so_002, 10, v_product_service, 'Consulting Service', 3, v_uom_hr, 150.00, 450.00, true, now(), now());

-- ============================================================
-- Purchase Orders with Lines
-- ============================================================

-- PO-001 (TechSupply Co.)
INSERT INTO tx_order (id, tenant_id, order_number, order_date, order_type, partner_id, warehouse_id, subtotal, tax_amount, grand_total, status, is_active, created_at, updated_at)
VALUES (v_po_001, v_tenant_id, 'PO-001', '2026-07-08', 'purchase', v_partner_vendor1, v_warehouse_main, 2924.00, 292.40, 3216.40, 'completed', true, now(), now());
INSERT INTO tx_order_line (id, tenant_id, order_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_po_001, 10, v_product_laptop, 'Business Laptop 15"', 3, v_uom_pcs, 899.00, 2697.00, true, now(), now());
INSERT INTO tx_order_line (id, tenant_id, order_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_po_001, 20, v_product_mouse, 'Wireless Mouse', 5, v_uom_pcs, 29.99, 149.95, true, now(), now());
INSERT INTO tx_order_line (id, tenant_id, order_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_po_001, 30, v_product_freight, 'Freight Service', 1, v_uom_pcs, 200.00, 200.00, true, now(), now());

-- PO-002 (RawMaterials Ltd.)
INSERT INTO tx_order (id, tenant_id, order_number, order_date, order_type, partner_id, warehouse_id, subtotal, tax_amount, grand_total, status, notes, is_active, created_at, updated_at)
VALUES (v_po_002, v_tenant_id, 'PO-002', '2026-07-14', 'purchase', v_partner_vendor2, v_warehouse_sec, 2125.00, 212.50, 2337.50, 'draft', 'Urgent — production line waiting', true, now(), now());
INSERT INTO tx_order_line (id, tenant_id, order_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_po_002, 10, v_product_raw_mat, 'Steel Sheets', 45, v_uom_kg, 45.00, 2025.00, true, now(), now());
INSERT INTO tx_order_line (id, tenant_id, order_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_po_002, 20, v_product_pack, 'Packaging Material', 2, v_uom_pcs, 35.00, 70.00, true, now(), now());

-- ============================================================
-- Invoices with Lines
-- ============================================================

-- INV-S-001 from SO-001 (Acme)
INSERT INTO tx_invoice (id, tenant_id, invoice_number, invoice_date, due_date, invoice_type, partner_id, order_id, subtotal, tax_amount, grand_total, paid_amount, due_amount, status, is_active, created_at, updated_at)
VALUES (v_inv_s1, v_tenant_id, 'INV-S-001', '2026-07-11', '2026-08-10', 'sales', v_partner_customer1, v_so_001, 958.97, 95.90, 1054.87, 0, 1054.87, 'open', true, now(), now());
INSERT INTO tx_invoice_line (id, tenant_id, invoice_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_inv_s1, 10, v_product_laptop, 'Business Laptop 15"', 1, v_uom_pcs, 899.00, 899.00, true, now(), now());
INSERT INTO tx_invoice_line (id, tenant_id, invoice_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_inv_s1, 20, v_product_mouse, 'Wireless Mouse', 2, v_uom_pcs, 29.99, 59.98, true, now(), now());

-- INV-P-001 from PO-001 (TechSupply)
INSERT INTO tx_invoice (id, tenant_id, invoice_number, invoice_date, due_date, invoice_type, partner_id, order_id, subtotal, tax_amount, grand_total, paid_amount, due_amount, status, is_active, created_at, updated_at)
VALUES (v_inv_p1, v_tenant_id, 'INV-P-001', '2026-07-09', '2026-08-08', 'purchase', v_partner_vendor1, v_po_001, 2924.00, 292.40, 3216.40, 3216.40, 0, 'paid', true, now(), now());
INSERT INTO tx_invoice_line (id, tenant_id, invoice_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_inv_p1, 10, v_product_laptop, 'Business Laptop 15"', 3, v_uom_pcs, 899.00, 2697.00, true, now(), now());
INSERT INTO tx_invoice_line (id, tenant_id, invoice_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_inv_p1, 20, v_product_mouse, 'Wireless Mouse', 5, v_uom_pcs, 29.99, 149.95, true, now(), now());

-- ============================================================
-- Payments
-- ============================================================
INSERT INTO tx_payment (id, tenant_id, payment_number, payment_date, payment_type, partner_id, payment_method, amount, reference, status, is_active, created_at, updated_at)
VALUES (v_pay_001, v_tenant_id, 'PAY-001', '2026-07-11', 'incoming', v_partner_customer1, 'wire_transfer', 500.00, 'Partial payment for INV-S-001', 'completed', true, now(), now());
INSERT INTO tx_payment (id, tenant_id, payment_number, payment_date, payment_type, partner_id, payment_method, amount, reference, status, is_active, created_at, updated_at)
VALUES (v_pay_002, v_tenant_id, 'PAY-002', '2026-07-10', 'outgoing', v_partner_vendor1, 'wire_transfer', 3216.40, 'Full payment for INV-P-001', 'completed', true, now(), now());

-- ============================================================
-- Shipments with Lines
-- ============================================================

-- Outbound shipment for SO-001
INSERT INTO tx_shipment (id, tenant_id, shipment_number, shipment_date, shipment_type, partner_id, warehouse_id, order_id, tracking_number, carrier, status, is_active, created_at, updated_at)
VALUES (v_ship_001, v_tenant_id, 'SHP-001', '2026-07-12', 'outbound', v_partner_customer1, v_warehouse_main, v_so_001, 'TRACK-1Z999AA1', 'FastShip Courier', 'completed', true, now(), now());
INSERT INTO tx_shipment_line (id, tenant_id, shipment_id, line_number, product_id, description, quantity, uom_id, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_ship_001, 10, v_product_laptop, 'Business Laptop 15"', 1, v_uom_pcs, true, now(), now());
INSERT INTO tx_shipment_line (id, tenant_id, shipment_id, line_number, product_id, description, quantity, uom_id, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_ship_001, 20, v_product_mouse, 'Wireless Mouse', 2, v_uom_pcs, true, now(), now());

-- Inbound receipt for PO-001
INSERT INTO tx_shipment (id, tenant_id, shipment_number, shipment_date, shipment_type, partner_id, warehouse_id, order_id, status, is_active, created_at, updated_at)
VALUES (v_ship_002, v_tenant_id, 'REC-001', '2026-07-10', 'inbound', v_partner_vendor1, v_warehouse_main, v_po_001, 'completed', true, now(), now());
INSERT INTO tx_shipment_line (id, tenant_id, shipment_id, line_number, product_id, description, quantity, uom_id, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_ship_002, 10, v_product_laptop, 'Business Laptop 15"', 3, v_uom_pcs, true, now(), now());
INSERT INTO tx_shipment_line (id, tenant_id, shipment_id, line_number, product_id, description, quantity, uom_id, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), v_tenant_id, v_ship_002, 20, v_product_mouse, 'Wireless Mouse', 5, v_uom_pcs, true, now(), now());

END $$;
