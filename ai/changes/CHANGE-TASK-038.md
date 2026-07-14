---
task_id: TASK-038
type: API
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-038
base_branch: prd/PRD-004-window-hierarchy-menu
status: READY_FOR_TEST
created: 2026-07-13
author: software_engineer
---

# Change Report — TASK-038

## Summary

Created the runtime window definition API endpoint `GET /api/v1/runtime/windows/{windowName}/definition` that returns a full window definition bundle (window + tabs + fields + column metadata) as a single JSON response.

## Files Added

| File | Description |
|------|-------------|
| `backend/src/main/java/com/erp/core/runtime/dto/window/WindowDefinitionResponse.java` | Top-level DTO with WindowInfo inner class |
| `backend/src/main/java/com/erp/core/runtime/dto/window/TabDefinitionResponse.java` | Tab DTO with TableInfo inner class |
| `backend/src/main/java/com/erp/core/runtime/dto/window/FieldDefinitionResponse.java` | Field DTO with ColumnInfo inner class |
| `backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java` | Assembles window bundle from JPA entities |
| `backend/src/main/java/com/erp/core/runtime/controller/WindowDefinitionController.java` | REST controller for window definition endpoint |

## Files Modified

None

## Files Removed

None

## Database Changes

None

## API Changes

### New Endpoint

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/runtime/windows/{windowName}/definition` | Returns full window definition bundle |

### Response Structure

```json
{
  "success": true,
  "data": {
    "window": {
      "id": "uuid",
      "name": "sales_order",
      "table_id": "uuid",
      "description": "Manage sales orders"
    },
    "tabs": [
      {
        "id": "uuid",
        "name": "Header",
        "seq_no": 10,
        "is_single_row": false,
        "where_clause": null,
        "parent_column": null,
        "table": {
          "id": "uuid",
          "name": "tx_orders",
          "label": "Order"
        },
        "fields": [
          {
            "id": "uuid",
            "seq_no": 10,
            "is_same_line": false,
            "num_lines": 1,
            "column_width": 12,
            "is_displayed": true,
            "is_readonly": false,
            "is_mandatory": true,
            "display_logic": null,
            "readonly_logic": null,
            "default_value": null,
            "label_override": null,
            "column": {
              "code": "order_number",
              "label": "Order Number",
              "type": "string",
              "required": true,
              "max_length": 50
            }
          }
        ]
      }
    ]
  },
  "message": "Window definition loaded."
}
```

### Error Responses

| Status | Condition |
|--------|-----------|
| 401 UNAUTHORIZED | No authenticated user context |
| 404 NOT_FOUND | Window name doesn't exist |
| 304 NOT_MODIFIED | ETag matches (cached response) |

## Configuration Changes

None

## Dependencies Added/Updated

None

## Breaking Changes

- This endpoint replaces the old `GET /api/v1/runtime/forms/{formCode}/definition` (old endpoint still exists but should be deprecated)
- Response structure is different from the old `FormDefinitionBundleResponse`

## Validation Results

| Check | Result |
|-------|--------|
| `mvn clean compile` | PASS |
| `mvn test` (36 tests) | ALL PASS |

## Known Limitations

- No role-based access filtering yet (access check will be added in TASK-040/TASK-045)
- ETag is simple (windowName + tenantId) — no content hash
- Assembly loads each related entity individually (N+1 potential; acceptable for typical metadata size)

## Follow-up Recommendations

- TASK-039 (Window Data API) should start next to provide record CRUD
- TASK-040 (Menu Navigation) will integrate the menu API
- Consider adding a cache layer (e.g., Spring Cache) when metadata grows
