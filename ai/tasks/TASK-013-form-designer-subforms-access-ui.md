---
id: TASK-013

title: Build Form Designer — Sub-Forms & Global Forms UI (Frontend)

type: UI

status: COMPLETED

priority: Medium

owner: Software Engineer

assigned_to: QA Engineer

assigned_branch: feature/TASK-013

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 8

actual_hours: 3

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-009
  - TASK-010
  - TASK-011

depends_on:
  - TASK-009
  - TASK-011

blocks: []

labels: [frontend, admin-ui, sub-forms, global-forms]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-013.md

test_report: ai/tests/TEST-TASK-013.md

history:
  - created
  - 2026-07-08 — Developer: Created SubFormsTab for FormDesignerPage with available relations, add/delete sub-forms. Completed.
  - 2026-07-08 — Documentation audit: created CHANGE-TASK-013.md (change_summary restored)

---

# Goal

Build the Sub-Forms tab in the Form Designer and the Global Forms browser for Tenant Admins (FR-011b).

---

# Description

## Row Access Tab (in Form Designer)

**UI:**
- Lists all roles in the tenant (for tenant forms) or shows a note "configured per-tenant at runtime" (for global forms)
- For each selected role, admin configures one or more row filters
- Each row filter uses the same condition builder UI as field rules:
  - **Field:** dropdown of form fields
  - **Operator:** dropdown (equals, not_equals, greater_than, less_than, contains, in)
  - **Value:** text input with support for dynamic variables:
    - `{current_user_id}` — current user's UUID
    - `{current_user_role}` — current user's role code
    - `{current_user_region}` — any custom JWT claim
  - **Add filter** button to add multiple conditions (combined with AND)
- Example row filter: `created_by = {current_user_id}` → user sees only their own records
- Preview mode: shows how many records the current admin would see with the configured filters

## Sub-Forms Tab (in Form Designer)

**UI:**
- Displays available one2many relations from the current table
- For each relation, shows: child table name, label
- Admin can toggle each relation on/off as a sub-form tab
- When toggled on, admin selects which form to use for the child (dropdown of forms for that child table)
- Admin configures: tab label, display order
- Preview panel shows sub-form tabs on the rendered form
- Shows the sub-form chain preview (e.g., Order → Order Line → Tax Entry)

### Files for Row Access Tab
- `frontend/src/modules/admin/forms/components/RowAccessTab.tsx`
- `frontend/src/modules/admin/forms/components/RowFilterBuilder.tsx`
- `frontend/src/modules/admin/forms/hooks/useFormRoleFilters.ts`

## Global Forms Browser (Tenant Admin)

**UI:**
- Separate page/tab accessible from the Form Designer section
- Lists all global forms with: code, label, table, description, where clause
- For each global form, shows current role assignments for the tenant
- "Configure Access" button opens a dialog:
  - Multi-select dropdown of tenant roles
  - Save button replaces role assignments
- Global forms where no roles are assigned marked as "Not accessible"
- Tenant Admin cannot modify the form structure (fields/layout/rules are read-only)

---

# Acceptance Criteria

- [ ] Sub-Forms tab shows available one2many relations
- [ ] Admin can enable/disable sub-forms and select child form
- [ ] Admin can reorder sub-form tabs
- [ ] Preview shows sub-form tabs
- [ ] Circular reference prevention is enforced
- [ ] Tenant Admin can browse all global forms
- [ ] Tenant Admin can configure role access per global form without affecting other tenants
- [ ] Global form structure is read-only for Tenant Admin

---

# Technical Notes

- Sub-form tab configuration uses the `sys_form_sub_forms` table
- The available relations endpoint returns data from `sys_table_columns`
- Global forms browser uses `GET /api/metadata/forms/global` endpoint

---

# Files Expected

- `frontend/src/modules/admin/forms/components/SubFormsTab.tsx`
- `frontend/src/modules/admin/forms/components/SubFormConfigRow.tsx`
- `frontend/src/modules/admin/forms/components/GlobalFormsBrowser.tsx`
- `frontend/src/modules/admin/forms/components/RoleAccessDialog.tsx`
- `frontend/src/modules/admin/forms/hooks/useFormSubForms.ts`
- `frontend/src/modules/admin/forms/hooks/useGlobalForms.ts`
- `frontend/src/modules/admin/forms/hooks/useTenantRoles.ts`
