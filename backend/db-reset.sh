#!/bin/bash
# ============================================================
# ERP System — Database Reset Script
# ============================================================
# Drops the existing erp_db and creates a fresh one, then
# runs Flyway migrations on next application startup.
#
# Usage:  bash db-reset.sh
# ============================================================

set -e

DB_NAME="erp_db"
DB_USER="erp_user"
DB_PASS="erp_password"

echo "========================================"
echo " ERP Database Reset"
echo "========================================"
echo "Database: $DB_NAME"
echo "User:     $DB_USER"
echo ""
echo "WARNING: This will DROP the database and ALL data will be lost!"
echo ""

# Kill any Java process holding connections
pkill -f "mvn spring-boot:run" 2>/dev/null || true
sleep 1

echo "[1/4] Dropping database $DB_NAME..."
dropdb --if-exists "$DB_NAME" 2>/dev/null || {
  # If dropdb fails (e.g. connections still open), terminate connections first
  PGPASSWORD=$DB_PASS psql -h localhost -U "$DB_USER" -d postgres -c "
    SELECT pg_terminate_backend(pid) FROM pg_stat_activity
    WHERE datname = '$DB_NAME' AND pid <> pg_backend_pid();
  " 2>/dev/null || true
  dropdb --if-exists "$DB_NAME" 2>/dev/null || echo "  (database may not exist yet)"
}

echo "[2/4] Creating fresh database $DB_NAME..."
createdb "$DB_NAME" 2>/dev/null || {
  createdb "$DB_NAME" -U "$DB_USER" 2>/dev/null || {
    # If createdb fails, try via SQL
    PGPASSWORD=$DB_PASS psql -h localhost -U "$DB_USER" -d postgres -c "CREATE DATABASE $DB_NAME;" 2>/dev/null || echo "  (database may already exist)"
  }
}

echo "[3/4] Enabling uuid-ossp extension..."
PGPASSWORD=$DB_PASS psql -h localhost -U "$DB_USER" -d "$DB_NAME" -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";" 2>/dev/null || echo "  (extension may already exist)"

echo "[4/4] Granting permissions..."
PGPASSWORD=$DB_PASS psql -h localhost -U "$DB_USER" -d "$DB_NAME" -c "
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER;
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER;
  GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $DB_USER;
  GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $DB_USER;
" 2>/dev/null || true

echo ""
echo "========================================"
echo " Database reset complete!"
echo "========================================"
echo "Run './start.sh' to start the application."
echo "Flyway will apply all migrations automatically."
echo "========================================"
