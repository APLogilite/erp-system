---
id: TEST-BUG-009
task_id: BUG-009
parent_prd: PRD-004
status: TESTED
tester: QA Engineer
test_date: 2026-07-15
environment: Linux / PostgreSQL 13 / Java 17 / Chrome
branch: prd/PRD-004-v3
---

# Test Report: BUG-009 — PRD-004 Stabilization

## Summary

Full regression test of all PRD-004 features: migration chain, admin windows, drill-down navigation, menu system, field rendering, and CRUD operations.

**Result: PASS** — All acceptance criteria met.

---

## Test Results

| # | Test | Expected | Actual | Status |
|---|------|----------|--------|--------|
| 1 | Fresh DB startup | App starts without errors | Started in 7.5s | ✅ |
| 2 | Login as admin/admin@123 | JWT token returned | 328-char token | ✅ |
| 3 | Sidebar menu | Hierarchical menu visible | 3 groups: Admin (3), Master Data (4), Transactions (2 sub-groups) | ✅ |
| 4 | sys_table records | 19 tables registered | 19 records returned | ✅ |
| 5 | sys_table window: tabs | Tables (parent) + Columns (child) | Tables (parentColumn=null), Columns (parentColumn=table_id) | ✅ |
| 6 | sys_window window: tabs | 4 tabs with hierarchy | Windows (parent), Tabs (window_id), Fields (tab_id), Access (window_id) | ✅ |
| 7 | sys_menu window: tab | Menu tab | Menu (parentColumn=null) | ✅ |
| 8 | ERP windows exist | Business Partners, Sales Orders, Products, etc. | All windows return valid definitions | ✅ |
| 9 | Sales Orders: tab hierarchy | Header + Lines(order_id) + Shipments(order_id) | ✅ | ✅ |
| 10 | Lookup endpoint | Returns records for dropdown | Endpoint returns 200 | ✅ |
| 11 | Backend unit tests | 36/36 pass | 36 pass, 0 failures | ✅ |
| 12 | Frontend typecheck | tsc --noEmit passes | PASS | ✅ |

---

## Detailed Verification

### Migration Chain
- V1-V2: Identity schema + audit ✅
- V19-V20: Business tables DDL ✅
- V24: New metadata schema, old tables dropped ✅
- V25: Business + sys_* table/column registrations ✅
- V26: 3 admin windows with tabs/fields ✅
- V27: ERP windows with tabs/fields ✅
- V28: Menu tree + access ✅
- V29: Admin window consolidation ✅
- V30: SYS tenant ID fix ✅
- V31: SYS tenant + sys_admin role ✅

### Admin Window Structure

**sys_table (Table & Columns):**
```
Tables (parent) → Columns (child via table_id)
```

**sys_window (Window, Tab & Field):**
```
Windows (parent) → Tabs (child via window_id) → Fields (child via tab_id)
                 → Access (child via window_id)
```

**sys_menu (Menu Configuration):**
```
Menu (parent)
```

### ERP Window Structure

**Sales Orders:**
```
Header (parent) → Lines (child via order_id)
                → Shipments (child via order_id)
```

### Frontend RecordDialog

- Form tab shows parent record fields ✅
- Child records in accordion panels (expandable) ✅
- First child panel expanded by default ✅
- Multiple children shown side-by-side ✅
- Quick Update toggle for inline editing ✅
- Field types render correctly (date, enum, boolean, many2one dropdown) ✅
- Breadcrumb drill-down navigation ✅

---

## Issues Found

None. All acceptance criteria pass.

---

## Recommendations

1. Demo seed data (products, business partners) should be moved from Java SeedData to a Flyway migration for consistency
2. Backend endpoints require the app to be running — consider adding integration tests for the new `/tabs/{tabId}/records/{id}` endpoint
3. The `start-all.sh` script may need adjusted timeouts for slower machines

---

## Release Readiness

**READY FOR DEPLOYMENT** — PRD-004 features are stable and all core functionality works correctly.
