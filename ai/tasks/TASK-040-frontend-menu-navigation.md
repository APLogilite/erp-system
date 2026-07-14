---
id: TASK-040

title: Frontend — Menu Component + Navigation

type: UI

status: COMPLETED

priority: Critical

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: feature/TASK-040

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 8

actual_hours: 2

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-037]

blocks: [TASK-041, TASK-045]

labels: [frontend, ui, navigation, menu]

history:
  - 2026-07-13: Status READY_FOR_DEV → IN_DEVELOPMENT. Assigned to Software Engineer. Started implementation.
  - 2026-07-13: Menu API endpoint + frontend MenuNavigation component created. Sidebar updated. Validation passed. Status → READY_FOR_TEST.
  - 2026-07-14: QA verification completed. 11/11 tests passed, 0 bugs. Status → TESTED.

review_required: true

test_required: true

test_report: ai/tests/TEST-TASK-040.md

change_report: ai/changes/CHANGE-TASK-040.md

---

# Goal

Build the hierarchical menu component that loads menu entries from the backend and replaces the flat "DYNAMIC FORMS" section.

---

# Description

## Menu API

Create a new backend endpoint:

```
GET /api/runtime/menu
```

Returns the menu tree filtered by the current user's role access:

```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "name": "Sales",
      "type": "group",
      "icon": null,
      "children": [
        {
          "id": "uuid",
          "name": "Sales Orders",
          "type": "window",
          "window_name": "sales_order",
          "icon": null,
          "children": []
        }
      ]
    }
  ]
}
```

- Only items with `type='window'` that the user has `sys_window_access` to are included
- Groups with no visible children are automatically hidden
- The tree is built from `sys_menu` parent-child relationships

## Frontend component

Create `MenuNavigation` component:

- Renders collapsible groups with expand/collapse icons (▶/▼)
- Window items are clickable and navigate to `/window/{windowName}`
- Groups with children show a nested indented list
- Empty groups are hidden
- Current active menu item is highlighted
- Fetches menu once per session (React Query with staleTime: Infinity)
- Integrates with the existing sidebar or top navigation

## Replace DYNAMIC FORMS section

- The old flat "DYNAMIC FORMS" section in the navigation is replaced by this menu component
- Remove the old `FormNavigationMenu` or replace it entirely

---

# Acceptance Criteria

- [ ] `GET /api/runtime/menu` endpoint returns filtered menu tree
- [ ] Groups with no accessible windows are hidden
- [ ] Menu component renders collapsible groups
- [ ] Clicking a window item navigates to `/window/{windowName}`
- [ ] Menu is role-aware (different roles see different items)
- [ ] Menu is fetched once per session and cached
- [ ] Old DYNAMIC FORMS section is replaced
- [ ] Works with existing sidebar/top nav layout

---

# Technical Notes

- Backend: See TASK-046 for menu API details (or implement inline)
- Frontend: Create component in `frontend/src/core/runtime/components/MenuNavigation.tsx`
- Use MUI `TreeView` or custom collapsible list
- Integrate with React Router `useNavigate()`
- Existing `FormNavigationMenu` component may need to be replaced or updated
