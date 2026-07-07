---
id: TASK-014

title: Build Global Forms Role Access UI for System Admin (Frontend)

type: UI

status: PLANNING

priority: Medium

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

prd_version: 1.5.0

parent_task:

related_tasks:
  - TASK-010

depends_on:
  - TASK-010

blocks: []

labels: [frontend, admin-ui, roles, system-admin]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Build the System Admin view for browsing all tenants' role assignments on global forms.

---

# Description

## System Admin — Global Form Tenant Visibility

**UI:**
- Accessible from the Form Designer → Global Forms section
- For each global form, System Admin can expand to see a table of all tenants and their assigned roles
- Columns: Tenant name, role names assigned, actions
- Read-only view — System Admin cannot modify tenant role assignments (that's the Tenant Admin's responsibility)
- Shows tenants that have NOT configured access as "Not configured"

---

# Acceptance Criteria

- [ ] System Admin can see all tenants' role assignments per global form
- [ ] Tenants with no role assignments are listed as "Not configured"
- [ ] The view is read-only
- [ ] Shows tenant name and assigned role names

---

# Technical Notes

- Uses `GET /api/metadata/forms/{formId}/global-tenant-roles` endpoint
- Requires `SYSTEM_ADMIN` role

---

# Files Expected

- `frontend/src/modules/admin/forms/components/GlobalFormTenantAccessTable.tsx`
