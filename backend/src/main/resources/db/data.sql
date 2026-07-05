-- Sample data for ERP Database

-- Insert sample products
INSERT INTO products (name, sku, description, category, uom, type, cost_price, sale_price) VALUES
('Laptop', 'LAP001', 'High-performance laptop', 'Electronics', 'Piece', 'STOCKABLE', 800.00, 1200.00),
('Mouse', 'MOU001', 'Wireless mouse', 'Electronics', 'Piece', 'STOCKABLE', 15.00, 25.00),
('Keyboard', 'KEY001', 'Mechanical keyboard', 'Electronics', 'Piece', 'STOCKABLE', 50.00, 80.00);

-- Insert sample warehouses
INSERT INTO warehouses (name, location) VALUES
('Main Warehouse', 'New York'),
('Secondary Warehouse', 'Los Angeles');

-- Insert sample orders
INSERT INTO orders (order_number, order_type, party_id, order_date, status, total_amount) VALUES
('SO001', 'SALES', gen_random_uuid(), CURRENT_TIMESTAMP, 'CONFIRMED', 1225.00),
('PO001', 'PURCHASE', gen_random_uuid(), CURRENT_TIMESTAMP, 'DRAFT', 865.00);

-- Insert sample order lines
INSERT INTO order_lines (order_id, product_id, quantity, unit_price, line_total) VALUES
((SELECT id FROM orders WHERE order_number = 'SO001'), (SELECT id FROM products WHERE sku = 'LAP001'), 1, 1200.00, 1200.00),
((SELECT id FROM orders WHERE order_number = 'SO001'), (SELECT id FROM products WHERE sku = 'MOU001'), 1, 25.00, 25.00),
((SELECT id FROM orders WHERE order_number = 'PO001'), (SELECT id FROM products WHERE sku = 'LAP001'), 1, 800.00, 800.00),
((SELECT id FROM orders WHERE order_number = 'PO001'), (SELECT id FROM products WHERE sku = 'KEY001'), 1, 50.00, 50.00),
((SELECT id FROM orders WHERE order_number = 'PO001'), (SELECT id FROM products WHERE sku = 'MOU001'), 3, 15.00, 45.00);

-- Insert sample stock movements
INSERT INTO stock_movements (product_id, warehouse_id, quantity, movement_type, reference_type, movement_date) VALUES
((SELECT id FROM products WHERE sku = 'LAP001'), (SELECT id FROM warehouses WHERE name = 'Main Warehouse'), 10, 'PURCHASE', 'PURCHASE_ORDER', CURRENT_TIMESTAMP),
((SELECT id FROM products WHERE sku = 'MOU001'), (SELECT id FROM warehouses WHERE name = 'Main Warehouse'), 20, 'PURCHASE', 'PURCHASE_ORDER', CURRENT_TIMESTAMP),
((SELECT id FROM products WHERE sku = 'KEY001'), (SELECT id FROM warehouses WHERE name = 'Main Warehouse'), 5, 'PURCHASE', 'PURCHASE_ORDER', CURRENT_TIMESTAMP);