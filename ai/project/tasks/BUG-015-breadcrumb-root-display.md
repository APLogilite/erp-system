---
id: BUG-015

title: Breadcrumb root level shows child record _display instead of parent

status: READY_FOR_TEST

priority: Medium

severity: Medium

owner: QA Engineer

assigned_to: Software Engineer

assigned_branch: prd/PRD-005-v2

locked: false

created: 2026-07-30

updated: 2026-07-30

parent_prd: PRD-005

parent_task: TASK-046

reported_by: QA Engineer

detected_in: Manual QA — opening a Sales Invoices record with child Lines tab shows breadcrumb "Sales Invoices (10) > Lines (10)" instead of "Sales Invoices (INV-S-001) > Lines (10)"

fix_summary: Store the root record _display in a ref before drilling; use it in the root breadcrumb instead of effectiveFormRecord (which points to the child record when drilled).

history:
  - 2026-07-30 — QA Engineer — Created (user report). Root cause: WindowPage.tsx line 154 — effectiveFormRecord uses currentDrillLevel.recordData when drilled; breadcrumb line 299 uses effectiveFormRecord for the ROOT level display, so the root shows the child record's _display.

---

# Summary

When viewing a child record (e.g., clicking a Lines row), the breadcrumb shows:

```
Sales Invoices (10) > Lines (10)
```

But should show:

```
Sales Invoices (INV-S-001) > Lines (10)
```

Both show "10" because `effectiveFormRecord` points to the drilled child record data.

# Root Cause

`WindowPage.tsx:154`: `effectiveFormRecord` is set to `currentDrillLevel.recordData` when drilled — the **child** record. The breadcrumb root level at line 299 uses `effectiveFormRecord` for display, so it renders the child's `_display` (line_number "10") instead of the parent's `_display` (invoice_number "INV-S-001").

# Fix

In `RecordDialog`, capture the root record's `_display` in a `useRef` before drilling:

```typescript
const rootRecordDisplayRef = useRef<string>('');

useEffect(() => {
  if (!isDrilled && recordData) {
    const rec = (recordData as { record?: Record<string, unknown> }).record;
    rootRecordDisplayRef.current = (rec?._display as string) ?? '';
  }
}, [isDrilled, recordData]);
```

Then in the root breadcrumb, prefer `rootRecordDisplayRef.current` over `effectiveFormRecord._display`.

# Acceptance Criteria

- [ ] Breadcrumb shows parent record's `_display` on the root level (e.g., "Sales Invoices (INV-S-001)")
- [ ] Child drill level still shows correct `_display` (e.g., "Lines (10)")
- [ ] Undrilled root (single form) still works correctly
- [ ] `tsc --noEmit` clean
