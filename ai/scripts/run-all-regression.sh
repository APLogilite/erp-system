#!/bin/bash
# ============================================================
# PRD-001 + PRD-002 Regression Test Runner
# Runs all reusable verification scripts against PostgreSQL.
#
# Prerequisites:
#   - PostgreSQL running on localhost:5432
#   - erp_db database exists with erp_user/erp_password
#   - Application started (mvn spring-boot:run or ./start.sh)
#
# Usage: ./ai/scripts/run-all-regression.sh
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
DB_USER="${DB_USER:-erp_user}"
DB_PASS="${DB_PASSWORD:-erp_password}"
DB_HOST="${DB_HOST:-localhost}"
DB_NAME="${DB_NAME:-erp_db}"
DB_URL="jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}"
API_BASE="${API_BASE:-http://localhost:8081}"
PASS=0
FAIL=0

export PGPASSWORD="$DB_PASS"
PSQL="psql -U $DB_USER -h $DB_HOST -d $DB_NAME"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_section() { echo -e "\n${YELLOW}=== $1 ===${NC}"; }
log_pass()   { echo -e "${GREEN}PASS${NC} $1"; ((PASS++)); }
log_fail()   { echo -e "${RED}FAIL${NC} $1"; ((FAIL++)); }

# ── Prerequisite checks ─────────────────────────────────────
log_section "Pre-flight Checks"

# Check psql
if command -v psql &>/dev/null; then
  log_pass "psql found"
else
  log_fail "psql not found — install PostgreSQL client"
  exit 1
fi

# Check PostgreSQL connection
if $PSQL -c "SELECT 1" &>/dev/null; then
  log_pass "PostgreSQL connection OK ($DB_HOST:$DB_NAME as $DB_USER)"
else
  log_fail "PostgreSQL connection FAILED"
  echo "  Check: is PostgreSQL running? Credentials in \$DB_USER/\$DB_PASSWORD?"
  exit 1
fi

# Check backend is running
if curl -s -o /dev/null -w "%{http_code}" "$API_BASE/api/v1/auth/login" 2>/dev/null | grep -q "200\|401\|403\|405"; then
  log_pass "Backend responding ($API_BASE)"
else
  log_fail "Backend NOT responding on $API_BASE"
  echo "  Start with: cd backend && ./start.sh"
  echo "  Continuing without API tests..."
fi

# ── PRD-001: Schema Verification ─────────────────────────────
log_section "PRD-001: Schema Verification"
$PSQL -f "$SCRIPT_DIR/verify-prd-001-schema.sql" 2>&1 | tail -20

IDENTITY_COUNT=$($PSQL -t -c "SELECT count(*) FROM pg_tables WHERE schemaname='public' AND tablename LIKE 'identity_%'" 2>/dev/null || echo 0)
META_COUNT=$($PSQL -t -c "SELECT count(*) FROM pg_tables WHERE schemaname='public' AND tablename LIKE 'sys_%'" 2>/dev/null || echo 0)

if [ "$IDENTITY_COUNT" -ge 16 ] 2>/dev/null; then
  log_pass "Identity tables: $IDENTITY_COUNT (expected >= 16)"
else
  log_fail "Identity tables: $IDENTITY_COUNT (expected >= 16)"
fi

if [ "$META_COUNT" -ge 14 ] 2>/dev/null; then
  log_pass "Metadata tables: $META_COUNT (expected >= 14)"
else
  log_fail "Metadata tables: $META_COUNT (expected >= 14)"
fi

# ── PRD-002: Data Verification ───────────────────────────────
log_section "PRD-002: Data Verification"
$PSQL -f "$SCRIPT_DIR/verify-prd-002-data.sql" 2>&1 | tail -30

STATIC_COUNT=$($PSQL -t -c "SELECT count(*) FROM sys_metadata_models WHERE table_type='static' AND name LIKE 'sys_%'" 2>/dev/null || echo 0)
FORMS_COUNT=$($PSQL -t -c "SELECT count(*) FROM sys_metadata_views WHERE name LIKE 'admin_%'" 2>/dev/null || echo 0)
FIELDS_COUNT=$($PSQL -t -c "SELECT count(*) FROM sys_form_fields" 2>/dev/null || echo 0)
TENANT_PASS=$($PSQL -t -c "SELECT count(*) FROM (SELECT v.name, bool_or(ff.column_code='tenant_id') AS has FROM sys_metadata_views v JOIN sys_form_fields ff ON ff.form_id=v.id WHERE v.name LIKE 'admin_%' GROUP BY v.name) sub WHERE has=true" 2>/dev/null || echo 0)

if [ "$STATIC_COUNT" -eq 11 ] 2>/dev/null; then
  log_pass "Static models: $STATIC_COUNT (expected 11)"
else
  log_fail "Static models: $STATIC_COUNT (expected 11)"
fi

if [ "$FORMS_COUNT" -eq 11 ] 2>/dev/null; then
  log_pass "Admin forms: $FORMS_COUNT (expected 11)"
else
  log_fail "Admin forms: $FORMS_COUNT (expected 11)"
fi

if [ "$FIELDS_COUNT" -ge 74 ] 2>/dev/null; then
  log_pass "Form fields: $FIELDS_COUNT (expected >= 74)"
else
  log_fail "Form fields: $FIELDS_COUNT (expected >= 74)"
fi

if [ "$TENANT_PASS" -eq 11 ] 2>/dev/null; then
  log_pass "tenant_id on all forms: $TENANT_PASS/11"
else
  log_fail "tenant_id missing: $TENANT_PASS/11 have it"
fi

# ── PRD-002: ENH-002 tenant_id Verification ──────────────────
log_section "ENH-002: tenant_id Detail Check"
$PSQL <<SQL
SELECT v.name AS form,
  MAX(CASE WHEN ff.column_code='tenant_id' THEN ff.position::text END) AS pos,
  MAX(CASE WHEN ff.column_code='tenant_id' THEN
    CASE WHEN ff.read_only THEN 'YES' ELSE 'NO!' END END) AS read_only
FROM sys_metadata_views v
JOIN sys_form_fields ff ON ff.form_id = v.id
WHERE v.name LIKE 'admin_%'
GROUP BY v.name ORDER BY v.name;
SQL

# ── Flyway Validation ─────────────────────────────────────────
log_section "Flyway History"
$PSQL -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"

# ── API Smoke Test (if backend running) ───────────────────────
log_section "API Smoke Test"
LOGIN_RESP=$(curl -s -X POST "$API_BASE/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' 2>/dev/null || echo '{}')
TOKEN=$(echo "$LOGIN_RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('data',{}).get('accessToken',''))" 2>/dev/null || echo "")

if [ -n "$TOKEN" ] && [ ${#TOKEN} -gt 100 ]; then
  log_pass "Login OK (token: ${#TOKEN} chars)"
  FORMS_RESP=$(curl -s "$API_BASE/api/runtime/forms" -H "Authorization: Bearer $TOKEN" 2>/dev/null || echo '{}')
  if echo "$FORMS_RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); exit(0 if d.get('success') else 1)" 2>/dev/null; then
    log_pass "API /api/runtime/forms responding"
  else
    log_fail "API /api/runtime/forms error (pre-existing role-matching issue)"
  fi
else
  log_fail "Login failed — is backend running and identity seed data present?"
fi

# ── Final Summary ─────────────────────────────────────────────
log_section "RESULTS"
echo -e "  ${GREEN}Passed:${NC} $PASS"
echo -e "  ${RED}Failed:${NC} $FAIL"
echo ""
if [ "$FAIL" -eq 0 ]; then
  echo -e "${GREEN}All regression tests PASSED.${NC}"
  exit 0
else
  echo -e "${RED}$FAIL test(s) FAILED — review output above.${NC}"
  exit 1
fi
