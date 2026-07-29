#!/bin/bash
# ============================================================
# Regression Test Suite — Current Schema Generation (V1–V8)
# ============================================================
# Runs all current verification scripts in sequence to validate
# the ERP system metadata and data integrity.
#
# Superseded v1 scripts (verify-prd-002-data.sql, verify-prd-003-data.sql,
# verify-prd-004-schema.sql) are RETAINED on disk for history but are
# intentionally NOT run here — they target the old V19–V29 schema.
#
# This script assumes:
#   - PostgreSQL is running and accessible
#   - The application database exists
#   - psql client is installed
#   - Backend running on :8081 (for API smoke + BUG-013 checks)
#
# Env overrides: PG_CONN, PGPASS, PGUSER_DB, DB_NAME, API_BASE,
#                ADMIN_USER, ADMIN_PASS
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DB_NAME="${DB_NAME:-erp_db}"
PGUSER_DB="${PGUSER_DB:-postgres}"
export PGPASSWORD="${PGPASS:-postgres}"
PG_CONN="${PG_CONN:--U $PGUSER_DB -h localhost -d $DB_NAME}"
API_BASE="${API_BASE:-http://localhost:8081/api/v1}"
ADMIN_USER="${ADMIN_USER:-admin}"
ADMIN_PASS="${ADMIN_PASS:-Admin@123}"

echo "========================================"
echo "  ERP Regression Tests (schema V1–V8)"
echo "  Database: $DB_NAME"
echo "  Started:  $(date)"
echo "========================================"
echo ""

FAILURES=0

run_test() {
    local label="$1"
    local script="$2"
    echo "--- $label ---"
    if psql $PG_CONN -f "$SCRIPT_DIR/$script" 2>&1; then
        echo ""
        echo "[PASS] $label"
    else
        echo ""
        echo "[FAIL] $label"
        FAILURES=$((FAILURES + 1))
    fi
    echo ""
    echo "----------------------------------------"
    echo ""
}

# === PRD-001: Identity + Window Schema (current, texts updated 2026-07-28) ===
run_test "PRD-001 Schema" "verify-prd-001-schema.sql"

# === PRD-002 v2: Admin Configuration Windows ===
run_test "PRD-002 Admin Data (v2)" "verify-prd-002-data-v2.sql"

# === PRD-003 v2: ERP Order Flow Data ===
run_test "PRD-003 ERP Data (v2)" "verify-prd-003-data-v2.sql"

# === PRD-004 v2: Window Schema Structure ===
run_test "PRD-004 Schema (v2)" "verify-prd-004-schema-v2.sql"

# === Flyway Check (V1–V8 must be current) ===
echo "============================================="
echo "  Flyway Migration Check"
echo "============================================="

echo "--- Flyway Version Check ---"
psql $PG_CONN -c "
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
" 2>&1

echo ""
echo "--- Expected: V1..V8 (V7 rename parent column, V8 seed fk columns) ---"
echo ""

# === API Smoke Test (authenticated) ===
echo "============================================="
echo "  API Smoke Test"
echo "============================================="

echo "API Base: $API_BASE"
echo ""

TOKEN=$(curl -s -X POST "$API_BASE/auth/login" -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USER\",\"password\":\"$ADMIN_PASS\"}" \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['data'].get('accessToken') or '')" 2>/dev/null)

if [ -z "$TOKEN" ]; then
    echo "[FAIL] Login — cannot run API smoke tests"
    FAILURES=$((FAILURES + 1))
else
    echo "[PASS] Login ($ADMIN_USER)"
    echo ""

    check_endpoint() {
        local label="$1"
        local url="$2"
        local code=$(curl -s -o /dev/null -w '%{http_code}' -H "Authorization: Bearer $TOKEN" "$url")
        if [ "$code" = "200" ]; then
            echo "[PASS] $label (HTTP 200)"
        else
            echo "[FAIL] $label (HTTP $code)"
            FAILURES=$((FAILURES + 1))
        fi
    }

    check_endpoint "Runtime Menu" "$API_BASE/runtime/menu"
    check_endpoint "Window Definition (Sales Orders)" "$API_BASE/runtime/windows/Sales%20Orders/definition"
    check_endpoint "Window Records (Sales Orders)" "$API_BASE/runtime/windows/Sales%20Orders/records?page=0&size=1"
    echo ""
fi

# === BUG-013: Child Tab parent_link_column_id Verification (DB + API) ===
echo "============================================="
echo "  BUG-013 — Child Tab Link Verification"
echo "============================================="

if bash "$SCRIPT_DIR/verify-bug-013-child-tabs.sh"; then
    echo "[PASS] BUG-013 Child Tabs"
else
    echo "[FAIL] BUG-013 Child Tabs"
    FAILURES=$((FAILURES + 1))
fi
echo ""

# === Summary ===
echo "========================================"
echo "  Regression Results"
echo "========================================"
if [ $FAILURES -eq 0 ]; then
    echo "  ALL TESTS PASSED"
else
    echo "  $FAILURES TEST(S) FAILED"
fi
echo "  Completed: $(date)"
echo "========================================"
exit $FAILURES
