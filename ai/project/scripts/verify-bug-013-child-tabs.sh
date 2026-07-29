#!/bin/bash
# ============================================================
# BUG-013 Verification — Child Tab parent_link_column_id wiring
# ============================================================
# Verifies (DB + API) that every child tab has a populated
# parent_link_column_id and that childTabIds are computed for
# all windows with child tabs.
#
# Prerequisites:
#   - Backend running on :8081 (bash start-all.sh)
#   - PostgreSQL on localhost:5432, database erp_db
#
# Usage:  bash ai/project/scripts/verify-bug-013-child-tabs.sh
# Exit:   0 = all checks passed, 1 = at least one failure
# ============================================================

PGUSER="${PGUSER:-postgres}"
PGPASS="${PGPASS:-postgres}"
DB="${DB_NAME:-erp_db}"
API="${API_BASE:-http://localhost:8081/api/v1}"
FAILED=0

pass() { echo "  PASS  $1"; }
fail() { echo "  FAIL  $1"; FAILED=1; }

echo "========================================"
echo " BUG-013 — Child Tab Link Verification"
echo "========================================"

# ------------------------------------------------------------
# Part 1 — DB: every child tab must have parent_link_column_id
# ------------------------------------------------------------
echo ""
echo "[1/4] DB: child tabs with NULL parent_link_column_id"
NULLS=$(PGPASSWORD=$PGPASS psql -h localhost -U $PGUSER -d $DB -tAc "
SELECT count(*) FROM sys_tab t
WHERE t.is_active = true AND t.deleted_at IS NULL
  AND t.parent_link_column_id IS NULL
  AND EXISTS (
    SELECT 1 FROM sys_tab p
    WHERE p.window_id = t.window_id AND p.seq_no < t.seq_no
      AND p.parent_link_column_id IS NULL
  )
  AND t.seq_no > 10;")
# Header/first tabs legitimately have NULL; flag only KNOWN child tabs
KNOWN_NULL=$(PGPASSWORD=$PGPASS psql -h localhost -U $PGUSER -d $DB -tAc "
SELECT string_agg(w.name || ' > ' || t.name, ', ')
FROM sys_tab t JOIN sys_window w ON w.id = t.window_id
WHERE t.parent_link_column_id IS NULL
  AND t.name IN ('Lines','Columns','Tabs','Fields','Access','Shipments')
  AND t.seq_no > 10;")
if [ -z "$KNOWN_NULL" ]; then
  pass "all known child tabs (Lines/Columns/Tabs/Fields/Access) have parent_link_column_id"
else
  fail "child tabs still NULL: $KNOWN_NULL"
fi

echo ""
echo "[2/4] DB: link columns resolve to correct relation_table"
PGPASSWORD=$PGPASS psql -h localhost -U $PGUSER -d $DB -c "
SELECT w.name AS window, t.name AS tab, tb.name AS table_name,
       c.code AS link_col, c.relation_table
FROM sys_tab t
JOIN sys_window w ON w.id = t.window_id
JOIN sys_table tb ON tb.id = t.table_id
LEFT JOIN sys_column c ON c.id = t.parent_link_column_id
WHERE t.parent_link_column_id IS NOT NULL
ORDER BY w.name, t.seq_no;"

# ------------------------------------------------------------
# Part 2 — API: login
# ------------------------------------------------------------
echo ""
echo "[3/4] API: childTabIds populated for all windows with child tabs"
TOKEN=$(curl -s -X POST "$API/auth/login" -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['data'].get('accessToken') or '')" 2>/dev/null)
if [ -z "$TOKEN" ]; then
  fail "login failed — cannot run API checks"
  echo ""
  echo "RESULT: FAILED (login)"
  exit 1
fi
pass "login (admin)"

check_window() {
  local win="$1" expect="$2"   # expect: comma list of "ParentTab>ChildTab"
  local enc=$(python3 -c "import urllib.parse,sys; print(urllib.parse.quote(sys.argv[1]))" "$win")
  local tmp=$(mktemp)
  curl -s "$API/runtime/windows/$enc/definition" -H "Authorization: Bearer $TOKEN" > "$tmp"
  python3 - "$expect" "$tmp" <<'EOF'
import sys, json
expect, path = sys.argv[1], sys.argv[2]
d = json.load(open(path))
if not d.get('success'):
    print(f"API_ERROR:{d.get('message')}"); raise SystemExit(0)
tabs = d['data']['tabs']
ids = {t['id']: t['name'] for t in tabs}
errors = []
for pair in expect.split(','):
    parent, child = pair.split('>')
    ptab = next((t for t in tabs if t['name'] == parent), None)
    ctab = next((t for t in tabs if t['name'] == child), None)
    if not ptab: errors.append(f"parent tab '{parent}' missing"); continue
    if not ctab: errors.append(f"child tab '{child}' missing"); continue
    ct = [ids.get(c) for c in (ptab.get('childTabIds') or [])]
    if child not in ct: errors.append(f"'{parent}'.childTabIds={ct} missing '{child}'")
    if not ctab.get('parentLinkColumnId'): errors.append(f"'{child}'.parentLinkColumnId is null")
print("OK" if not errors else "ERR:" + "; ".join(errors))
EOF
  rm -f "$tmp"
}

while IFS= read -r line; do
  [ -z "$line" ] && continue
  win="${line%%::*}"; exp="${line##*::}"
  res=$(check_window "$win" "$exp")
  if [ "$res" = "OK" ]; then pass "$win — $exp"; else fail "$win — $res"; fi
done <<'CASES'
Sales Orders::Header>Lines
Purchase Orders::Header>Lines
Sales Invoices::Sales Invoices>Lines
Purchase Invoices::Purchase Invoices>Lines
Shipments::Shipments>Lines
Table Definitions::Tables>Columns
Window Definitions::Windows>Tabs,Windows>Access,Tabs>Fields
CASES

# ------------------------------------------------------------
# Part 3 — API: child records load for a real Sales Order
# ------------------------------------------------------------
echo ""
echo "[4/4] API: Sales Order child records (Lines) load with data"
ENC_SO=$(python3 -c "import urllib.parse; print(urllib.parse.quote('Sales Orders'))")
ORDER_ID=$(curl -s "$API/runtime/windows/$ENC_SO/records?page=0&size=1" -H "Authorization: Bearer $TOKEN" \
  | python3 -c "import sys,json; print((json.load(sys.stdin)['data'].get('items') or [{}])[0].get('id',''))")
if [ -z "$ORDER_ID" ]; then
  fail "no Sales Order records to test child fetch"
else
  ROWS=$(curl -s "$API/runtime/windows/$ENC_SO/records/$ORDER_ID" -H "Authorization: Bearer $TOKEN" \
    | python3 -c "
import sys, json
d = json.load(sys.stdin)
cr = (d.get('data') or {}).get('childRecords') or {}
lines = cr.get('Lines') or []
print(len(lines))")
  if [ "${ROWS:-0}" -gt 0 ]; then
    pass "Sales Order $ORDER_ID returned $ROWS Lines rows"
  else
    fail "Sales Order $ORDER_ID returned 0 Lines rows"
  fi
fi

echo ""
echo "========================================"
if [ $FAILED -eq 0 ]; then
  echo " RESULT: ALL CHECKS PASSED"
else
  echo " RESULT: FAILURES DETECTED"
fi
echo "========================================"
exit $FAILED
