---
id: ENH-003

title: RuntimePage needs proper API integration for dynamic form rendering

status: TESTED

priority: High

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: (merged to main)

locked: false

created: 2026-07-13

updated: 2026-07-13

started:

completed:

parent_prd: PRD-001

parent_task: TASK-026

reason: PRD-001 FR-014 requires dynamic runtime form rendering via API, but RuntimePage uses hardcoded sample bundles

fix_summary:

verification_report: TEST-ENH-003

history:
  - 2026-07-13 — Product Manager — Created enhancement task. RuntimePage needs proper API-driven form rendering.
  - 2026-07-14 — QA Engineer — TESTED. All 10 test cases passed. See TEST-ENH-003.md for details.

---

# Summary

The `RuntimePage` currently uses hardcoded sample bundles (25+ imports from `schema/sample/`). PRD-001 FR-014 requires it to fetch form definitions from the runtime API and render them dynamically. A partial rewrite was done on main, but the `FormDefinition` type from the API doesn't match the `RuntimeMetadataBundle` type expected by `RuntimeRenderer`, causing a type compatibility gap.

# Problem

- The sidebar DYNAMIC FORMS section navigates to `/app/runtime?form=xxx`
- The `RuntimePage` fetches the form definition via `fetchFormDefinition(formCode)` 
- But the returned `FormDefinition` format differs from the `RuntimeMetadataBundle` format expected by `RuntimeRenderer`
- The `as any` cast bypasses type safety
- Fields, inputs, and CRUD toolbar may not render correctly

# Expected Behaviour

- Navigating to a dynamic form from the sidebar should render the actual form with fields, inputs, layout sections, etc.
- CRUD operations (create, save, delete, refresh) should work
- Sub-form tabs should render inline grids
- The form should be usable end-to-end

# Acceptance Criteria

- [ ] RuntimePage reads `form` query param from URL
- [ ] Form definition is fetched from API via `fetchFormDefinition(formCode)`
- [ ] FormDefinition is properly adapted/mapped to RuntimeMetadataBundle
- [ ] Form renders with fields, inputs, sections matching the metadata definition
- [ ] CRUD toolbar is visible (Save, Delete, Refresh, Prev/Next)
- [ ] Sub-form tabs render correctly
- [ ] Data loads for existing records
- [ ] No TypeScript errors (remove `as any` casts)

# Technical Notes

The key challenge is mapping the API response format (`FormDefinition` from `runtimeApi.ts`) to the renderer format (`RuntimeMetadataBundle` from `schema/`). Options:

1. **Adapter function**: Write a mapper that transforms `FormDefinition` → `RuntimeMetadataBundle`
2. **Update RuntimeRenderer**: Make it accept the API format directly
3. **Use DynamicFormRenderer**: The engine in `src/engine/forms/DynamicFormRenderer.tsx` already uses the metadata-driven approach

# Related Documents

- [PRD-001 FR-014](../prd/PRD-001-dynamic-form-configuration-system.md)
- [BUG-004 — Search bar](../tasks/BUG-004-search-bar-not-loading.md)
- [BUG-005 — Sidebar forms](../tasks/BUG-005-sidebar-form-navigation-issues.md)
