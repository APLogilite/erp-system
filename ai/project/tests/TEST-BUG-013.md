---
id: TEST-BUG-013
task_id: BUG-013
parent_prd: PRD-005
test_date: 2026-07-28
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL 13, fresh erp_db via start-all.sh --setup, Flyway V1–V8)
build_commit_tested: 305ed3b (prd/PRD-005-v2)
test_scope: Verify BUG-013 fix — child tabs (Lines/Columns/Tabs/Access/Fields) appear and load data via reference-based parent_link_column_id resolution; regression on single-tab windows.
---

# Test Report — BUG-013: Child tab (Lines) does not appear — parentLinkColumn_ID resolution

---

## Test Scope

**In scope:**
- DB-level verification of `sys_tab.parent_link_column_id` population and `sys_column.relation_table` wiring
- API-level verification of `childTabIds` on all 7 windows that have child tabs
- Child record data fetching (`GET /runtime/windows/{name}/records/{id}` → `childRecords`)
- Regression: single-tab windows unchanged; admin hierarchy (grandchild Tabs → Fields)
- Backend test suite; legacy QA regression scripts (updated for the schema change)
- Manual UI scenarios for all user-facing acceptance criteria

**Out of scope:**
- Creating/editing child records via UI (not part of BUG-013 acceptance criteria)
- Performance testing
- The "Shipments" child tab under Sales Orders mentioned in the bug doc's expected behavior — not present in the current V5 seed (see Known Limitations)

---

## Test Cases Executed

### TC-001: DB — all child tabs have `parent_link_column_id` populated
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Every child tab (Lines, Columns, Tabs, Fields, Access) has a non-NULL `parent_link_column_id` pointing at the correct `sys_column` row |
| Actual | 9/9 child tabs populated: Sales/Purchase Orders → Lines (`order_id`→tx_order), Sales/Purchase Invoices → Lines (`invoice_id`→tx_invoice), Shipments → Lines (`shipment_id`→tx_shipment), Table Defs → Columns (`table_id`→sys_table), Window Defs → Tabs/Access (`window_id`→sys_window), Fields (`tab_id`→sys_tab) |

### TC-002: API — `childTabIds` populated for all windows with child tabs
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `GET /runtime/windows/{name}/definition` returns each parent tab with `childTabIds` containing the child tab id, and each child tab with non-null `parentLinkColumnId` |
| Actual | Sales Orders (Header→Lines), Purchase Orders (Header→Lines), Sales Invoices (Sales Invoices→Lines), Purchase Invoices (Purchase Invoices→Lines), Shipments (Shipments→Lines), Table Definitions (Tables→Columns), Window Definitions (Windows→Tabs+Access, Tabs→Fields grandchild) — all correct |

### TC-003: API — child records load filtered by parent id
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Opening a parent record returns `childRecords` with the child rows filtered by the resolved FK column |
| Actual | SO-001 → Lines: 2 rows (filtered `order_id`); PO-001 → 3 rows; INV-S-001 → 2 rows; INV-P-001 → 2 rows; SHP-001 → 2 rows |

### TC-004: Regression — single-tab windows unchanged
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Business Partners, Products, UOM, Warehouses, Payments, Menu Configuration: 1 tab each, empty `childTabIds`, null `parentLinkColumnId`, record list loads |
| Actual | All 6 windows: tabs=1, childTabIds=0, parentLinks=0; record lists return data |

### TC-005: Regression — admin hierarchy data cascade (incl. grandchild)
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Table Definitions → record shows Columns; Window Definitions → record shows Tabs/Access; drilling a Tab record shows its Fields |
| Actual | Table record → Columns: 6; Window record → Tabs: 1, Access: 1; Tab record "Business Partners" → Fields: 6 (Code, Name, Type, …) filtered by `tab_id` |

### TC-006: Backend test suite
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | All 36 backend tests pass |
| Actual | 36/36 — Tests run: 36, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS |

### TC-007: Legacy QA regression scripts updated and re-run
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `verify-prd-002-data.sql`, `verify-prd-003-data.sql`, `verify-prd-004-schema.sql` execute without SQL errors on the current schema |
| Actual | All 3 run clean after updating stale references (`parent_column` → `parent_link_column_id`; removed `wa.is_read_only` which no longer exists in `sys_window_access`) |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | Sales Orders record: child tabs visible and show data | **PASSED** | API verified (TC-002/003); UI confirmed MS-001. Note: seed has "Lines" only — no "Shipments" child tab (see Known Limitations) |
| AC2 | Purchase Orders record: Lines visible, shows data | **PASSED** | API verified; UI confirmed MS-002 |
| AC3 | Sales Invoices record: Lines (+Payments) visible, show data | **PASSED** | API verified for Lines; seed has no Payments child tab under Sales Invoices (Payments is a standalone window); UI confirmed MS-003 |
| AC4 | Purchase Invoices record: Lines visible, shows data | **PASSED** | API verified; UI confirmed MS-004 |
| AC5 | Shipments record: Lines visible, shows data | **PASSED** | API verified; UI confirmed MS-005 |
| AC6 | Payments record: single tab only (no regression) | **PASSED** | TC-004 (API: 1 tab, no children); UI confirmed MS-006 |
| AC7 | Master data windows — single tab, no regressions | **PASSED** | TC-004; UI confirmed MS-007 |
| AC8 | Admin windows — hierarchical child tabs still work | **PASSED** | TC-005 incl. grandchild; UI confirmed MS-008 |
| AC9 | All [SE] criteria (schema/seed, JPA/DTO, childTabIds, child queries, build/tests) | **PASSED** | Verified by SE during rework (see BUG-013 doc checkboxes) and independently re-verified by QA in TC-001/002/006 |

**Note on first manual round (2026-07-29 morning):** initial failures (MS-001/002/003/005 no child tabs; MS-004/MS-008 "Tab not found" errors with ghost UUIDs `ebcf4cea…`/`8424a449…`) were root-caused to **stale browser cache from a previous DB generation** — backend logs proved the client sent tab ids that do not exist in the current DB; fresh `curl` sessions returned correct data. After a hard refresh, all scenarios passed. Not a product defect; robustness improvement tracked as ENH-004.

---

## Manual Test Scenarios

Scenarios requiring human execution via browser. **Servers are running**: frontend http://localhost:5173, login `admin` / `Admin@123`.

### MS-001: Sales Orders — Lines tab appears with data
| Field | Value |
|-------|-------|
| Preconditions | Logged in as admin; menu "Transactions / Sales / Sales Orders" visible |
| Steps | 1. Open http://localhost:5173 and log in (`admin` / `Admin@123`) |
| | 2. Navigate to Transactions → Sales → Sales Orders |
| | 3. Click the record "SO-001" to open its detail view |
| | 4. Observe the tab bar below/above the form |
| | 5. Click the "Lines" tab |
| Expected Result | Header tab renders the order form; a **"Lines" tab is visible** next to Header; clicking it shows a grid with **2 rows** (Business Laptop 15", Wireless Mouse) |
| Actual Result | User confirmed 2026-07-29 (after hard refresh cleared stale cache): works as expected |
| Status | **PASSED** |

### MS-002: Purchase Orders — Lines tab appears with data
| Field | Value |
|-------|-------|
| Preconditions | Logged in as admin |
| Steps | 1. Navigate to Transactions → Purchasing → Purchase Orders |
| | 2. Open record "PO-001" |
| | 3. Click the "Lines" tab |
| Expected Result | "Lines" tab visible and shows **3 rows** |
| Actual Result | User confirmed 2026-07-29 (after hard refresh cleared stale cache): works as expected |
| Status | **PASSED** |

### MS-003: Sales Invoices — Lines tab appears with data
| Field | Value |
|-------|-------|
| Preconditions | Logged in as admin |
| Steps | 1. Navigate to Transactions → Sales → Sales Invoices |
| | 2. Open record "INV-S-001" |
| | 3. Click the "Lines" tab |
| Expected Result | "Lines" tab visible and shows **2 rows** |
| Actual Result | User confirmed 2026-07-29 (after hard refresh cleared stale cache): works as expected |
| Status | **PASSED** |

### MS-004: Purchase Invoices — Lines tab appears with data
| Field | Value |
|-------|-------|
| Preconditions | Logged in as admin |
| Steps | 1. Navigate to Transactions → Purchasing → Purchase Invoices |
| | 2. Open record "INV-P-001" |
| | 3. Click the "Lines" tab |
| Expected Result | "Lines" tab visible and shows **2 rows** |
| Actual Result | User confirmed 2026-07-29 (after hard refresh cleared stale cache): works as expected |
| Status | **PASSED** |

### MS-005: Shipments — Lines tab appears with data
| Field | Value |
|-------|-------|
| Preconditions | Logged in as admin |
| Steps | 1. Navigate to Transactions → Shipments |
| | 2. Open record "SHP-001" |
| | 3. Click the "Lines" tab |
| Expected Result | "Lines" tab visible and shows **2 rows** |
| Actual Result | User confirmed 2026-07-29 (after hard refresh cleared stale cache): works as expected |
| Status | **PASSED** |

### MS-006: Payments — single tab, no child tabs (regression)
| Field | Value |
|-------|-------|
| Preconditions | Logged in as admin |
| Steps | 1. Navigate to Transactions → Sales → Payments |
| | 2. Open any record |
| Expected Result | Only one tab renders; **no child tab buttons** appear; no errors |
| Actual Result | User confirmed 2026-07-29 (after hard refresh cleared stale cache): works as expected |
| Status | **PASSED** |

### MS-007: Master data windows — single tab, no regression
| Field | Value |
|-------|-------|
| Preconditions | Logged in as admin |
| Steps | 1. Open Master Data → Business Partners, click any record |
| | 2. Repeat for Products, UOM, Warehouses |
| Expected Result | Each opens a single-tab form with data; no child tabs; no errors |
| Actual Result | User confirmed 2026-07-29 (after hard refresh cleared stale cache): works as expected |
| Status | **PASSED** |

### MS-008: Admin hierarchy — Table Definitions → Columns; Window Definitions → Tabs → Fields
| Field | Value |
|-------|-------|
| Preconditions | Logged in as admin |
| Steps | 1. Navigate to Administration → Table Definitions, open the "Order Line" (tx_order_line) table record |
| | 2. Click the "Columns" child tab — verify column rows load |
| | 3. Navigate to Administration → Window Definitions, open any window record |
| | 4. Verify "Tabs" and "Access" child tabs are visible; open "Tabs" and click a tab record |
| | 5. Verify "Fields" grandchild tab appears and shows field rows |
| Expected Result | Columns child tab shows columns; Window record shows Tabs/Access; drilling a Tab shows its Fields |
| Actual Result | User confirmed 2026-07-29 (after hard refresh cleared stale cache): works as expected |
| Status | **PASSED** |

---

## Regression Results

| Test Suite | Result |
|------------|--------|
| `mvn test` (36 tests) | 36 pass, 0 fail, 0 errors — BUILD SUCCESS |
| `verify-prd-002-data.sql` | PASS (0 SQL errors — after updating stale column refs) |
| `verify-prd-003-data.sql` | PASS (0 SQL errors — after updating stale column refs) |
| `verify-prd-004-schema.sql` | PASS (0 SQL errors — after updating stale echo text) |
| Single-tab windows API regression | PASS (6/6 windows) |
| Frontend dev server | `VITE ready`, no compile errors in log |

No regression introduced. The schema change was additive for data (new seed rows + backfill); the `parent_column` drop shipped with V7 and all consumers (backend, frontend, QA scripts) are now aligned on `parent_link_column_id`.

---

## Bugs Found

None.

---

## Known Limitations

- **Sales Orders has no "Shipments" child tab** in the current V5 seed, although the BUG-013 doc's expected behavior mentions it; likewise Sales Invoices has no "Payments" child tab (Payments is a standalone window). The reference-based resolution works for everything that IS seeded — adding those tabs is a seed-data decision for PM, not a code defect.
- **Duplicate FK constraint** on `sys_tab.parent_link_column_id`: `fk_sys_tab_parent_link_column` (Flyway V7) and `sys_tab_parent_link_column_id_fkey` (auto-created by Hibernate `ddl-auto=update`). Harmless redundancy; can be cleaned up in a future migration.
- Child record **creation** via UI (auto-setting parent FK) was listed in the change report's QA handoff as a focus area but is outside BUG-013's acceptance criteria; not covered here.

---

## Release Recommendation

**PASSED** — all automated verification (DB, API, regression, backend tests) and all 8 manual UI scenarios pass. BUG-013 fix is verified working end-to-end. Ready for RESOLVED.

---

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 7 automated + 8 manual |
| Passed | 15 |
| Failed | 0 |
| Skipped / Pending | 0 |
| Bugs Created | 0 |
| Acceptance Criteria Passed | 9/9 |
| Acceptance Criteria Skipped | 0 |
| Requirement Issues Identified | 1 (seed gap: no Shipments/Payments child tabs — referred to PM via Known Limitations) |
| Enhancements Raised | 1 (ENH-004: definition cache auto-invalidation after DB reseeds) |

---

## Reusable Scripts

```bash
# BUG-013 full verification (DB + API, requires backend running):
bash ai/project/scripts/verify-bug-013-child-tabs.sh

# Current-generation regression scripts (v2, aligned to Flyway V1–V8 schema):
psql -U postgres -h localhost -d erp_db -f ai/project/scripts/verify-prd-001-schema.sql
psql -U postgres -h localhost -d erp_db -f ai/project/scripts/verify-prd-002-data-v2.sql
psql -U postgres -h localhost -d erp_db -f ai/project/scripts/verify-prd-003-data-v2.sql
psql -U postgres -h localhost -d erp_db -f ai/project/scripts/verify-prd-004-schema-v2.sql

# Full regression suite (v2 scripts + BUG-013 script):
./ai/project/scripts/run-all-regression.sh
```

Note: `verify-prd-002-data.sql`, `verify-prd-003-data.sql`, `verify-prd-004-schema.sql` (v1) are retained for historical reference but SUPERSEDED — see their headers.

Canonical DDL reference: `ai/project/schema/metadata/sys_tab.sql` (includes `parent_link_column_id UUID REFERENCES sys_column(id)`).
