---
id: BUG-010

title: Saving Sales Order data returns HTTP 500 — POST /runtime/windows/Sales Orders/records fails

status: READY_FOR_DEV

priority: Critical

severity: Critical

owner: Product Manager

assigned_to:

assigned_branch:

locked: false

created: 2026-07-15

updated: 2026-07-15

started:

completed:

parent_prd: PRD-004

parent_task: TASK-039

reported_by: User

detected_in: Runtime — Sales Orders window record creation

related_test:

fix_summary:

verification_report:

history:
  - 2026-07-15 — Product Manager — Created. POST to runtime window data API returns 500 when saving Sales Order record.

---

# Summary

Attempting to create a new Sales Order record via the runtime window data API results in a 500 Internal Server Error. The request is never persisted to the database.

---

# Problem

**Request:**
```
POST http://localhost:8081/api/v1/runtime/windows/Sales%20Orders/records
Content-Type: application/json
Authorization: Bearer <valid JWT>
```

**Response:**
```
HTTP/1.1 500
Content-Type: application/json
Transfer-Encoding: chunked
```
The response body is ~134 bytes of JSON (presumably the `ApiResponse` error envelope), returned with a 29ms response time.

The `windowName` parameter is URL-encoded as `Sales%20Orders` (with a space), which maps to the "Sales Orders" window seeded in PRD-004's metadata.

---

# Expected Behaviour

`POST /api/v1/runtime/windows/Sales%20Orders/records` with a valid JSON body should:
1. Resolve the "Sales Orders" window from `sys_window` by name
2. Identify the main tab (first tab where `parent_column IS NULL`)
3. Locate the physical table for that tab via `sys_table.table_name`
4. Validate and persist the record into the physical table
5. Return `ApiResponse<T>` with HTTP 200 and the created record

---

# Actual Behaviour

The server returns HTTP 500 with an error response. The record is not created. Common root causes for this type of 500 error include:

1. **Window name resolution failure** — `windowName` contains a space (`Sales%20Orders`) and the lookup logic may not handle spaces in window names
2. **Table resolution failure** — The window's main tab → table mapping returns null or incorrect physical table name
3. **Missing/invalid field mapping** — Request body fields don't match `sys_column.code` entries for the tab
4. **Null pointer / missing tenant context** — Tenant isolation filter or `RuntimeContext` not properly initialized for the create flow
5. **where_clause auto-set failure** — The create logic attempts to auto-set a where_clause value (e.g. `order_type = 'sales'`) but the column or value is missing
6. **Database constraint violation** — Required column missing, FK violation, or unique constraint failure

---

# Steps To Reproduce

1. Login as admin (or any user with Sales Orders window access)
2. Navigate to the "Sales Orders" window via the menu
3. Click "Create" to open the new record form
4. Fill in required fields
5. Click "Save"
6. Observe: HTTP 500 error — record is not saved

Alternatively, send a direct API request:
```bash
curl -X POST 'http://localhost:8081/api/v1/runtime/windows/Sales%20Orders/records' \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{"doc_no": "SO-001", "date_ordered": "2026-07-15"}'
```

---

# Root Cause

(To be determined by Software Engineer)

Likely candidates:
- The space in `windowName` (`Sales Orders`) is not properly handled in the controller/service lookup logic
- The where_clause auto-set logic in `WindowDataService` fails when attempting to set `order_type = 'sales'` on the Sales Orders tab
- A null pointer exception in `DynamicCrudService` or `WindowDataService` during record creation
- Missing or incorrect physical table mapping for the Sales Orders window's main tab

**⚠ Cross-location check required:** The root cause likely affects ALL windows with spaces in their names. Investigate every seeded window name that contains a space and fix the underlying issue once for all locations. Affected windows include:
- "Sales Orders" (tx_orders, where type=sales)
- "Purchase Orders" (tx_orders, where type=purchase)
- "Business Partners" (md_business_partner)
- "Table Definitions" (sys_table)
- "Table Columns" (sys_column)
- "Window Definitions" (sys_window)
- "Window Tabs" (sys_tab)
- "Window Fields" (sys_window_field)
- "Window Access" (sys_window_access)
- "Menu Configuration" (sys_menu)

Also check the lookup/query side: `GET` list, `GET /{id}`, `PUT`, and `DELETE` endpoints may also be affected by spaces in window names, even if they currently appear to work (e.g. if the frontend URL-encodes them but the backend doesn't decode properly). Ensure all five CRUD operations work for ALL space-containing window names after the fix.

---

# Fix

(To be determined by Software Engineer)

When fixing, address the root cause for ALL windows — not just Sales Orders. The fix should handle window names with spaces generically (e.g., at the controller parameter binding level, or in the window lookup service).

---

# Validation

(To be filled by QA Engineer)

After fix:
- [ ] `POST /api/v1/runtime/windows/Sales%20Orders/records` returns HTTP 200 with the created record
- [ ] `POST /api/v1/runtime/windows/Purchase%20Orders/records` also works (same pattern)
- [ ] `POST` works for ALL other windows with spaces in names (Business Partners, Table Definitions, Window Definitions, etc.)
- [ ] `GET` list, `GET /{id}`, `PUT`, and `DELETE` also work for all space-containing window names
- [ ] Record is persisted in the correct physical database table
- [ ] All existing CRUD operations (GET, PUT, DELETE) still work
- [ ] All 36 backend tests pass
- [ ] Frontend typecheck passes

---

# Related Documents

- PRD-004: Window Hierarchy & Menu System v1.0.0
- TASK-039: Backend — Runtime Window Data API (CRUD Records)
- CHANGE-TASK-039: Change report for WindowDataService + WindowDataController
- TEST-TASK-039: 9/9 tests passed (structural verification)
