-- Basic ERP Database Schema and Sample Data

-- Create tables based on entities

-- Base entity fields are inherited, but for SQL we need to define them

-- Since using JPA with UUID, we need PostgreSQL extensions

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Products table
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    category VARCHAR(255),
    uom VARCHAR(50),
    type VARCHAR(50),
    cost_price DECIMAL(10,2),
    sale_price DECIMAL(10,2)
);

-- Warehouses table
CREATE TABLE warehouses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255)
);

-- Orders table
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    order_number VARCHAR(255) UNIQUE NOT NULL,
    order_type VARCHAR(50) NOT NULL,
    party_id UUID NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    total_amount DECIMAL(10,2) NOT NULL
);

-- Order lines table
CREATE TABLE order_lines (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    order_id UUID NOT NULL REFERENCES orders(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL
);

-- Stock movements table
CREATE TABLE stock_movements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    product_id UUID NOT NULL REFERENCES products(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    quantity DECIMAL(10,2) NOT NULL,
    movement_type VARCHAR(50) NOT NULL,
    reference_id UUID,
    reference_type VARCHAR(50),
    movement_date TIMESTAMP NOT NULL
);

-- Auth entities table (placeholder)
CREATE TABLE auth_entities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP
);

-- User entities table (placeholder)
CREATE TABLE user_entities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP
);

-- Sample data

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
('SO001', 'SALES', uuid_generate_v4(), CURRENT_TIMESTAMP, 'CONFIRMED', 1225.00),
('PO001', 'PURCHASE', uuid_generate_v4(), CURRENT_TIMESTAMP, 'DRAFT', 865.00);

-- Get order IDs for order lines
-- Assuming the first order is sales, second is purchase

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