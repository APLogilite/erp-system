---
id: TASK-038

title: Backend — Runtime Window Definition API (Bundle)

type: API

status: READY_FOR_TEST

priority: Critical

owner: software_engineer

assigned_to: software_engineer

assigned_branch: feature/TASK-038

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 8

actual_hours: 1

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-037]

blocks: [TASK-041]

labels: [backend, api, runtime]

history:
  - 2026-07-13: Status READY_FOR_DEV → IN_DEVELOPMENT. Assigned to software_engineer. Started implementation.
  - 2026-07-13: Window definition API endpoint created. Validation passed. Status → READY_FOR_TEST.

review_required: true

test_required: true

change_report: ai/changes/CHANGE-TASK-038.md

---

# Goal

Create the runtime API endpoint that returns the full window definition as a single JSON bundle (window + tabs + fields + columns).

---

# Description

Replace the old `/api/runtime/forms/{formCode}/definition` endpoint with:

```
GET /api/runtime/windows/{windowName}/definition
```

This endpoint returns a single JSON bundle containing everything needed to render the window on the frontend. The response is designed to be cached aggressively on the frontend.

## Response structure

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
      },
      {
        "id": "uuid",
        "name": "Lines",
        "seq_no": 20,
        "parent_column": "order_id",
        "where_clause": "order_id = @id@",
        "is_single_row": false,
        "table": {
          "id": "uuid",
          "name": "tx_order_lines",
          "label": "Order Line"
        },
        "fields": [...]
      }
    ]
  },
  "message": "Window definition loaded."
}
```

## Authentication

- Requires authenticated user
- Returns 404 if window not found or user has no access

## Caching

- Response should be cacheable (ETag or Cache-Control)
- Frontend caches aggressively (React Query staleTime)
- Only re-fetched when window definition changes

---

# Acceptance Criteria

- [ ] `GET /api/runtime/windows/{windowName}/definition` returns full window bundle
- [ ] Bundle includes window metadata + all tabs + all fields + column definitions
- [ ] Field display/readonly/mandatory settings included
- [ ] Column type info included (for UI rendering)
- [ ] 404 returned for unknown or inaccessible windows
- [ ] Response follows `ApiResponse<T>` envelope
- [ ] Endpoint is authenticated

---

# Technical Notes

- Replace old PRD-001 endpoint: `GET /api/runtime/forms/{formCode}/definition`
- Define a new API version prefix if needed, or update in place
- Use `@RestController` + `@RequestMapping("/api/runtime/windows")`
- Assemble the response from JPA entities: SysWindow → SysTab → SysWindowField → SysColumn
