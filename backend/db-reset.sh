#!/bin/bash
# ============================================================
# ERP System — Database Reset Script
# ============================================================
# Drops the existing erp_db and creates a fresh one, then
# runs Flyway migrations on next application startup.
#
# Requirements: PostgreSQL client tools (psql, dropdb, createdb)
# Usage:  bash db-reset.sh
# ============================================================

set -e

DB_NAME="erp_db"
DB_USER="erp_user"
DB_PASS="erp_password"
PGHOST="${PGHOST:-localhost}"

echo "========================================"
echo " ERP Database Reset"
echo "========================================"
echo "Database: $DB_NAME"
echo "User:     $DB_USER"
echo "Host:     $PGHOST"
echo ""
echo "WARNING: This will DROP the database and ALL data will be lost!"
echo ""

# Step 0: Kill any stale Java processes holding connections
echo "[0/5] Killing stale application processes..."
pkill -f "mvn spring-boot:run" 2>/dev/null || true
pkill -f "erp-system" 2>/dev/null || true
sleep 2

# Step 1: Terminate all connections to the target database
echo "[1/5] Terminating active connections to $DB_NAME..."
PGPASSWORD=$DB_PASS psql -h "$PGHOST" -U "$DB_USER" -d postgres -c "
  SELECT pg_terminate_backend(pid) FROM pg_stat_activity
  WHERE datname = '$DB_NAME' AND pid <> pg_backend_pid();
" 2>&1 || echo "  (no connections to terminate or database does not exist)"

# Step 2: Drop the database
echo "[2/5] Dropping database $DB_NAME (if exists)..."
dropdb --host="$PGHOST" --username="$DB_USER" --if-exists "$DB_NAME" 2>&1 || echo "  (database may not exist)"

# Step 3: Create fresh database
echo "[3/5] Creating fresh database $DB_NAME..."
createdb --host="$PGHOST" --username="$DB_USER" "$DB_NAME" 2>&1 || {
  echo "  createdb failed, trying via SQL..."
  PGPASSWORD=$DB_PASS psql -h "$PGHOST" -U "$DB_USER" -d postgres -c "CREATE DATABASE $DB_NAME;" 2>&1
}

# Step 4: Enable required extensions
echo "[4/5] Enabling uuid-ossp extension..."
PGPASSWORD=$DB_PASS psql -h "$PGHOST" -U "$DB_USER" -d "$DB_NAME" -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";" 2>&1

# Step 5: Grant permissions
echo "[5/5] Granting permissions..."
PGPASSWORD=$DB_PASS psql -h "$PGHOST" -U "$DB_USER" -d "$DB_NAME" -c "
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER;
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER;
  GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $DB_USER;
  GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $DB_USER;
" 2>&1

echo ""
echo "========================================"
echo " Database reset complete!"
echo "========================================"
echo "Next step:"
echo "  cd backend && ./start.sh"
echo ""
echo "Flyway will apply all 9 migrations (V1, V2, V19, V20, V24-V29)"
echo "on the first startup with a fresh database."
echo "========================================"
