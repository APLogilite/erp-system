---
id: BUG-005

title: Sidebar FormNavigationMenu shows model names and sub-forms — should show only top-level form labels

status: READY_FOR_TEST

priority: Medium

severity: Medium

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: (merged to main)

locked: true

created: 2026-07-13

updated: 2026-07-13

started:

completed:

parent_prd: PRD-001

parent_task: TASK-025

reported_by: User

detected_in: Frontend sidebar (localhost:5173)

related_test:

fix_summary:

verification_report:

history:
  - 2026-07-13 — Planner — Created bug task. User reported sidebar DYNAMIC FORMS section shows table/model names and sub-forms.

---

# Summary

The sidebar DYNAMIC FORMS section (`FormNavigationMenu.tsx`) has two issues:
1. Shows model/table names as group headers — should show only form names
2. Shows sub-forms (line items) in the sidebar — only top-level forms should appear

# Problem

When logged in as admin, the sidebar shows a DYNAMIC FORMS section with forms grouped by model (table) name. Users see sub-headers like "Product", "Order", "Warehouse" which are the underlying database table names, not the form names.

Additionally, line-item forms (e.g., order lines, invoice lines) appear as separate navigation items. These are sub-forms that should only be accessible as tabs within their parent form, not as standalone navigation items.

# Expected Behaviour

- The DYNAMIC FORMS section should list only form labels (no model/table group headers)
- Only top-level forms should appear (forms that are not configured as sub-forms)
- Line-item forms should be excluded from the sidebar (they appear as tabs in parent forms)
- The sidebar should be clean and easy to scan

# Actual Behaviour

- Forms are grouped by model/table name with sub-headers
- Sub-forms (e.g., order_line, invoice_line) appear as separate navigation items
- Long list of forms makes the sidebar cluttered

# Root Cause

The `FormNavigationMenu.tsx` groups accessible forms by `modelLabel`/`modelName` and renders them with model name sub-headers. It also includes ALL accessible forms without filtering out sub-forms.

The `RuntimeFormController.listAccessibleForms()` returns all forms the user has access to, including sub-forms. There is no flag in the response to distinguish top-level forms from sub-forms.

# Fix

**Frontend fix (FormNavigationMenu.tsx):**
1. Remove the model grouping/sub-headers — list forms by formLabel only
2. Filter out sub-forms. Sub-forms can be identified by:
   - **Option A**: Form code contains `_line` or `_line_item` suffix (heuristic)
   - **Option B**: Add an `isSubForm` flag to the API response (more robust)

**Backend enhancement (optional but recommended):**
- In `RuntimeFormController.listAccessibleForms()`, query `FormSubFormRepository` to check if each form is referenced as a child form, and add an `isSubForm: boolean` field to the response
- This allows the frontend to reliably filter without relying on naming conventions

# Validation

- [ ] Sidebar shows only form labels (no model/table group headers)
- [ ] Line-item forms do not appear in the sidebar
- [ ] Top-level forms are clickable and navigate to `/app/runtime?form=xxx`
- [ ] Sub-forms still appear as tabs inside their parent form (separate concern)

# Files Changed

- `frontend/src/core/runtime/components/FormNavigationMenu.tsx` — Remove model grouping, filter sub-forms
- (Optional) `backend/.../RuntimeFormController.java` — Add `isSubForm` to response

# Related Documents

- [BUG-004 — Search bar issues](../tasks/BUG-004-search-bar-not-loading.md)
- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
