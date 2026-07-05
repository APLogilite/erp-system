-- ERP System Database Setup Template
-- Copy this file to db-setup.sql and update with your credentials
-- Run this script as PostgreSQL superuser (usually 'postgres')

-- Create database
CREATE DATABASE erp_db
    WITH OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- Create user (CHANGE PASSWORD HERE)
CREATE USER erp_user WITH PASSWORD 'CHANGE_THIS_PASSWORD';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE erp_db TO erp_user;

-- Connect to the database and grant schema privileges
\c erp_db;

-- Grant permissions on future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO erp_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO erp_user;

-- Grant permissions on existing tables (if any)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO erp_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO erp_user;

-- Enable UUID extension (required for the application)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Verify setup
SELECT 'Database setup completed successfully!' as status;