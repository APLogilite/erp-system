#!/bin/bash
# ============================================================
# ERP System — Database Reset Script
# ============================================================
# Drops the existing erp_db and creates a fresh one, then
# runs Flyway migrations on next application startup.
#
# Requirements: PostgreSQL client tools (psql, dropdb, createdb)
# Usage:  bash db-reset.sh
#
# Configuration (via environment variables):
#   PGHOST       - PostgreSQL host (default: localhost)
#   PGPORT       - PostgreSQL port (default: 5432)
#   PGSU_USER    - PostgreSQL superuser (default: postgres)
#   PGSU_PASS    - PostgreSQL superuser password (default: postgres)
#   DB_NAME      - Database name (default: erp_db)
#   DB_USER      - Application database user (default: erp_user)
#   DB_PASS      - Application database user password (default: erp_password)
# ============================================================

set -e

# --- Configuration ---
DB_NAME="${DB_NAME:-erp_db}"
DB_USER="${DB_USER:-erp_user}"
DB_PASS="${DB_PASS:-erp_password}"
PGSU_USER="${PGSU_USER:-postgres}"
PGSU_PASS="${PGSU_PASS:-postgres}"
PGHOST="${PGHOST:-localhost}"
PGPORT="${PGPORT:-5432}"
PG_CONN="-h $PGHOST -p $PGPORT"

echo "========================================"
echo " ERP Database Reset"
echo "========================================"
echo "Database: $DB_NAME"
echo "App user: $DB_USER"
echo "Superuser: $PGSU_USER@$PGHOST:$PGPORT"
echo ""
echo "WARNING: This will DROP the database and ALL data will be lost!"
echo ""

# Step 0: Kill any stale Java processes holding connections
echo "[0/6] Killing stale application processes..."
pkill -f "mvn spring-boot:run" 2>/dev/null || true
pkill -f "erp-system" 2>/dev/null || true
sleep 2

# Step 1: Terminate all connections to the target database
echo "[1/6] Terminating active connections to $DB_NAME..."
PGPASSWORD=$PGSU_PASS psql $PG_CONN -U "$PGSU_USER" -d postgres -c "
  SELECT pg_terminate_backend(pid) FROM pg_stat_activity
  WHERE datname = '$DB_NAME' AND pid <> pg_backend_pid();
" 2>&1 || echo "  (no connections or database does not exist)"

# Step 2: Drop the database
echo "[2/6] Dropping database $DB_NAME (if exists)..."
PGPASSWORD=$PGSU_PASS psql $PG_CONN -U "$PGSU_USER" -d postgres -c "DROP DATABASE IF EXISTS $DB_NAME;" 2>&1

# Step 3: Create the application user if it doesn't exist, and set password
echo "[3/6] Ensuring application user $DB_USER exists..."
PGPASSWORD=$PGSU_PASS psql $PG_CONN -U "$PGSU_USER" -d postgres -c "
  DO \$\$
  BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '$DB_USER') THEN
      CREATE ROLE $DB_USER LOGIN PASSWORD '$DB_PASS';
    ELSE
      ALTER ROLE $DB_USER WITH PASSWORD '$DB_PASS';
    END IF;
  END
  \$\$;
" 2>&1

# Step 4: Create fresh database
echo "[4/6] Creating fresh database $DB_NAME..."
PGPASSWORD=$PGSU_PASS psql $PG_CONN -U "$PGSU_USER" -d postgres -c "
  CREATE DATABASE $DB_NAME
    WITH OWNER = $DB_USER
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;
" 2>&1

# Step 5: Enable required extensions
echo "[5/6] Enabling uuid-ossp extension..."
PGPASSWORD=$PGSU_PASS psql $PG_CONN -U "$PGSU_USER" -d "$DB_NAME" -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";" 2>&1

# Step 6: Grant permissions
echo "[6/6] Granting permissions..."
PGPASSWORD=$PGSU_PASS psql $PG_CONN -U "$PGSU_USER" -d "$DB_NAME" -c "
  GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
  GRANT ALL ON SCHEMA public TO $DB_USER;
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER;
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER;
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
