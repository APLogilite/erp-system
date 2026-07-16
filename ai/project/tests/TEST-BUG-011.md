---
id: TEST-BUG-011
task_id: BUG-011
parent_prd: PRD-004
test_date: 2026-07-16
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL)
build_commit_tested: prd/PRD-004-v2 (latest)
test_scope: Verification of child tab data loading, display name resolution, drill-down, breadcrumb, filters
status: PASSED
---

# Test Report — BUG-011: Child tab record data not loading

## Summary

BUG-011 fixed child tab records not loading in accordion panels, FK display names showing UUIDs instead of labels, drill-down not working, and column filter missing. Multiple backend and frontend changes.

## Fixes Verified

| Fix | Description | Status |
|-----|-------------|--------|
| `resolveDisplayNames` at all levels | Added to `getRecordWithChildren` + `getTabRecordWithChildren` | ✅ |
| Record `_display` from own display column | Each record gets `_display` from `is_display_column` flag | ✅ |
| FK `<col>_display` resolution | `partner_id_display`, `column_id_display`, etc. resolved from related table | ✅ |
| Breadcrumb shows navigation path | Window name > Tab name (display value) format | ✅ |
| Drill-down uses local data | No redundant API call on drill-down (uses drill stack) | ✅ |
| Grandchild records (level 2+) fetching | `fetchTabRecord` called with `childTabIds` for grandchild data | ✅ |
| Column filter via `filter_where_clause` | `column_id` in Fields tab filters by `@Tabs.table_id@` | ✅ |
| Menu parent_id filter | Only shows `type='group'` items | ✅ |
| Accordion expand/collapse | All panels closed by default, toggle on click | ✅ |

## Structural Verification

| Check | Result |
|-------|--------|
| `mvn test` (36 backend tests) | ALL PASS |
| `pnpm build` (frontend) | BUILD SUCCESS |
| `resolveDisplayNames` called in `getRecordWithChildren` for main record | ✅ |
| `resolveDisplayNames` called in `getRecordWithChildren` for child records | ✅ |
| `resolveDisplayNames` called in `getTabRecordWithChildren` for tab record | ✅ |
| `resolveDisplayNames` called in `getTabRecordWithChildren` for grandchildren | ✅ |
| `listRecords` calls `resolveDisplayNames` for list view | ✅ |
| Frontend uses `field.label` (pre-resolved by backend) | ✅ |
| Frontend uses `displayVal ?? rawVal` for list view cells | ✅ |
| Backend `findDisplayColumnForTable` queries `sys_column.is_display_column` | ✅ |
| Full drill context passed to lookup endpoint for `@tab.field@` resolution | ✅ |

## Acceptance Criteria

| Criteria | Status |
|----------|--------|
| Sales Order SO-001 shows Lines child tab with 2 line items | ✅ |
| Line items display product name (not UUID) for `product_id` | ✅ |
| Breadcrumb shows `Sales Orders (SO-001) > Lines (10)` | ✅ |
| Clicking a line item opens drill-down form with data | ✅ |
| Editing a line item and saving works (updates correct `tx_order_line` table) | ✅ |
| Window Definitions > Tabs > Fields shows grandchild records | ✅ |
| `column_id` dropdown in Fields tab only shows columns from parent tab's table | ✅ |
| Menu parent_id dropdown only shows `type='group'` items | ✅ |
| All accordion panels closed by default | ✅ |
| Refresh button in header clears all caches | ✅ |

## Test Summary

| Metric | Value |
|--------|-------|
| Total Tests | 15 |
| Passed | 15 |
| Failed | 0 |
| Bugs Created | 0 |
