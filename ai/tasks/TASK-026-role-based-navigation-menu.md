---
id: TASK-026

title: Build Role-Based Navigation Menu (Frontend)

type: UI

status: PLANNED

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 4

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0

parent_task:

related_tasks:
  - TASK-019

depends_on:
  - TASK-019

blocks: []

labels: [frontend, component, navigation, roles]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Build the navigation menu that dynamically populates with forms the current user has access to (based on their tenant + role assignments).

---

# Description

Create `FormNavigationMenu` component.

## Behavior
- On app load, fetch `GET /api/runtime/forms` to get all accessible forms
- Group forms logically (by model/table or as a flat list)
- Display in the left sidebar or top navigation
- Each menu item shows: form label, optional icon
- Clicking a menu item navigates to that form's list view
- Menu is role-aware: different users see different forms based on their tenant's role assignments

## Integration with Existing Navigation
- If the app has an existing navigation system, integrate forms as a new section
- Forms appear under a "Forms" or "Modules" section
- Pre-defined core forms appear alongside user-created forms

## Caching
- The form list is fetched once per session and cached
- Use React Query with `staleTime: Infinity` (only refreshes on page reload)
- On page load, the list is fetched before rendering the navigation

---

# Acceptance Criteria

- [ ] Navigation menu shows only forms the user has access to
- [ ] Menu items show form labels
- [ ] Clicking a menu item navigates to the correct form list view
- [ ] Navigation is dynamic (no hardcoded menu items for dynamic forms)
- [ ] Works with existing navigation structure
- [ ] Form list is cached for the session

---

# Technical Notes

- Use the same endpoint as the search bar (`GET /api/runtime/forms`)
- If the app uses React Router, integrate with `useNavigate()` for navigation
- Group forms by their model/table label for better organization
- Future enhancement: Tenant Admin can configure menu grouping/ordering

---

# Files Expected

- `frontend/src/core/runtime/components/FormNavigationMenu.tsx`
- `frontend/src/core/runtime/hooks/useAccessibleForms.ts`
- Modified: navigation container component in `frontend/src/core/router/`
