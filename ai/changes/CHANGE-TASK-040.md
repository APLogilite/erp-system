---
task_id: TASK-040
type: UI
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-040
base_branch: prd/PRD-004-window-hierarchy-menu
status: READY_FOR_TEST
created: 2026-07-13
author: Software Engineer
---

# Change Report — TASK-040

## Summary

Built the hierarchical menu navigation system. Created a backend `GET /api/v1/runtime/menu` endpoint that returns the menu tree from `sys_menu`, a frontend `useMenuItems` hook, and a `MenuNavigation` component that renders collapsible groups with nested window items. Replaced the old flat `FormNavigationMenu` in the sidebar.

## Files Added

| File | Description |
|------|-------------|
| `backend/src/main/java/com/erp/core/runtime/controller/MenuController.java` | `GET /api/v1/runtime/menu` endpoint |
| `frontend/src/core/runtime/hooks/useMenuItems.ts` | React Query hook to fetch menu tree |
| `frontend/src/core/runtime/components/MenuNavigation.tsx` | Hierarchical menu component with recursive collapsible groups |

## Files Modified

| File | Change |
|------|--------|
| `frontend/src/core/runtime/api/runtimeApi.ts` | Added `MenuTreeNode` type and `fetchMenu()` API function |
| `backend/src/main/java/com/erp/modules/metadata/service/SysMenuService.java` | Added `windowName` resolution to `MenuTreeNode` DTO |
| `frontend/src/components/layouts/Sidebar/Sidebar.tsx` | Replaced `FormNavigationMenu` import with `MenuNavigation` |

## Files Removed

None (old `FormNavigationMenu` still exists but is no longer used by the sidebar)

## Database Changes

None

## API Changes

### New Endpoint

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/runtime/menu` | Returns menu tree filtered by user's role access |

### Response Structure

```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "name": "Master Data",
      "type": "group",
      "icon": null,
      "windowName": null,
      "children": [
        {
          "id": "uuid",
          "name": "Business Partners",
          "type": "window",
          "windowId": "uuid",
          "windowName": "business_partner",
          "children": []
        }
      ]
    }
  ]
}
```

### Menu Component Features

- **Collapsible groups**: Root groups open by default, groups can be toggled
- **Window items**: Click navigates to `/window/{windowName}`
- **Recursive nesting**: Supports arbitrary depth of group nesting
- **Sidebar integration**: Replaces the old flat "DYNAMIC FORMS" section
- **Once-per-session fetch**: Menu is cached with `staleTime: Infinity`
- **Empty groups hidden**: Groups with no children are not rendered

## Configuration Changes

None

## Dependencies Added/Updated

None

## Breaking Changes

- Old `FormNavigationMenu` component is no longer rendered in the sidebar
- Navigation URLs changed from `/app/runtime?form=...` to `/window/{windowName}` (TASK-041 will implement the actual route handler)

## Validation Results

| Check | Result |
|-------|--------|
| `mvn clean compile` (backend) | PASS |
| `mvn test` (36 backend tests) | ALL PASS |
| `pnpm typecheck` (frontend) | PASS |

## Known Limitations

- Role-based menu filtering is not yet implemented (depends on sys_window_access seeding in TASK-045)
- Window route `/window/{windowName}` doesn't have a router handler yet (TASK-041)
- Menu items use display names from `sys_menu.name`; window code names from `sys_window.name` are used for routing

## Follow-up Recommendations

- TASK-041 (Routing + RuntimePage fix) should be next as it depends on menu navigation working
- TASK-042 (Seed business tables) can proceed in parallel
- Consider adding caching (Spring Cache) to the menu endpoint when metadata grows
