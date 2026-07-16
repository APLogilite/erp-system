#!/bin/bash
# ============================================================
# Regression Test Suite — PRD-004 + PRD-003
# ============================================================
# Runs all verification scripts in sequence to validate
# the ERP system metadata and data integrity.
#
# This script assumes:
#   - PostgreSQL is running and accessible
#   - The application database exists
#   - psql client is installed
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PG_CONN="${PG_CONN:--U erp_user -h localhost -d erp_db}"

echo "========================================"
echo "  PRD-004 + PRD-003 Regression Tests"
echo "  Database: $(echo $PG_CONN | grep -oP '(?<=-d\s)\S+' || echo 'erp_db')"
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

# === PRD-004: Schema Verification (V24-V29) ===
echo "============================================="
echo "  PRD-004 — New Metadata Schema Verification"
echo "============================================="

run_test "PRD-004 Schema" "verify-prd-004-schema.sql"

# === PRD-003: Data Verification (Business Tables + Windows) ===
echo "============================================="
echo "  PRD-003 — ERP Data Verification"
echo "============================================="

run_test "PRD-003 Data" "verify-prd-003-data.sql"

# === Flyway Check (V24-V29 must be current) ===
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
echo "--- Expected: V1, V2, V19, V20, V24, V25, V26, V27, V28, V29 ---"
echo ""

# === API Smoke Test ===
echo "============================================="
echo "  API Smoke Test"
echo "============================================="

API_BASE="${API_BASE:-http://localhost:8081/api/v1}"
echo "API Base: $API_BASE"
echo ""

check_endpoint() {
    local label="$1"
    local url="$2"
    echo "--- $label ---"
    if curl -sf "$url" > /dev/null 2>&1; then
        echo "$(curl -s -o /dev/null -w '%{http_code}' "$url") $(curl -s -o /dev/null -w '' "$url" || true) — OK"
        echo "[PASS] $label"
    else
        echo "[FAIL] $label (HTTP $(curl -s -o /dev/null -w '%{http_code}' "$url" 2>/dev/null || echo 'Connection refused'))"
        FAILURES=$((FAILURES + 1))
    fi
    echo ""
}

check_endpoint "Health Check" "$API_BASE/actuator/health"
check_endpoint "Tables" "$API_BASE/tables"
check_endpoint "Windows" "$API_BASE/windows"

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
