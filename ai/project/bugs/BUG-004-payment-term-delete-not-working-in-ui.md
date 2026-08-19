---
id: BUG-004
title: Delete button does not remove Payment Term record in runtime UI
status: READY_FOR_DEV
priority: High
severity: Medium
owner: Software Engineer
assigned_to:
assigned_branch:
locked: false
created: 2026-08-19
updated: 2026-08-19
started:
completed:
parent_prd: PRD-007
parent_task: TASK-070
reported_by: QA Engineer / User
detected_in: prd/PRD-007-sales-order-workflow-customer-management
related_test: TEST-TASK-070
fix_summary:
verification_report:
history:
  - created
---

# Summary

In the Payment Terms runtime window, creating and updating records works, but clicking the **Delete** button does not remove the record from the UI/database.

---

# Problem

Payment Term delete action is non-functional from the runtime window grid.

---

# Expected Behaviour

Clicking **Delete** on a Payment Terms row should soft-delete the record, remove it from the grid, and persist the soft-delete in `md_payment_term`.

---

# Actual Behaviour

After clicking **Delete**, the record remains visible in the Payment Terms grid and `is_active` stays `true` in the database.

---

# Steps To Reproduce

1. Start backend and frontend.
2. Log in as admin.
3. Navigate to **Master Data → Payment Terms**.
4. Create a new payment term (e.g., NET45).
5. Click **Delete** on the newly created row.
6. Confirm the delete in the browser dialog.
7. Observe the row is still present in the grid.

---

# Root Cause

*(Developer to fill)*

The backend `DELETE /api/v1/runtime/windows/Payment%20Terms/records/{id}` endpoint works correctly when called directly (returns 200 and soft-deletes the row). The issue is likely in the frontend `WindowPage` delete handler or in how the grid refreshes after deletion.

---

# Fix

*(Developer to fill)*

Suggested investigation:
1. Verify `handleDelete` in `frontend/src/routes/window/WindowPage.tsx` is actually invoked and receives the correct `record.id`.
2. Add `.catch()` to `deleteWindowRecord(...)` to surface any UI/API errors.
3. Ensure `queryClient.invalidateQueries` refreshes the `window-records` query after delete.
4. Check if the delete button click is being intercepted by the row `onClick` edit handler despite `stopPropagation`.

---

# Validation

*(Tester to fill after fix)*

- Create a new Payment Term.
- Click Delete and confirm.
- Verify the row disappears from the grid.
- Verify `is_active=false` in `md_payment_term` for that record.
- Verify no JavaScript errors in browser console.

---

# Files Changed

*(Developer to fill)*

---

# Related Documents

- Parent PRD: `ai/project/prd/PRD-007-sales-order-workflow-customer-management.md`
- Parent Task: `ai/project/tasks/TASK-070-create-payment-term-table.md`
- Test Report: `ai/project/tests/TEST-TASK-070.md`
