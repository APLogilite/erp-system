---
id: BUG-004

title: FormSearchBar shows empty results — needs default form list and visible header button

status: IN_DEVELOPMENT

priority: Medium

severity: Medium

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: bugfix/BUG-004

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed:

parent_prd: PRD-001

parent_task: TASK-025

reported_by: User

detected_in: Frontend Header

related_test:

fix_summary:

verification_report:

history:
  - 2026-07-13 — Planner — Created bug task. Ctrl+K opens dialog but shows "No forms found". User requests default form list on open and visible search button in header.

---

# Summary

The Ctrl+K form search (`FormSearchBar`) has three issues:
1. **Dialog opens but list is empty** — Ctrl+K triggers the dialog, but "No forms found." shows because the API call to fetch accessible forms fails (blocked by BUG-002)
2. **No default form list** — When the dialog opens, it should show ALL forms the user has access to (not require typing first). The code already handles this (`!query` returns all), but the API failure means the list is always empty
3. **No visible search button in header** — There is no clickable button/icon in the Header to open the search. Users have no way to discover the Ctrl+K shortcut

# Problem

**Issue A — Empty results:**
- Ctrl+K dialog opens
- `useAccessibleForms()` calls `apiClient.get('/runtime/forms')` 
- This hits `GET /api/v1/runtime/forms` which fails with 500 because the `RuntimeFormController` maps to `/api/runtime/forms` (BUG-002)
- `useQuery` returns an error, data is `undefined`
- `(forms ?? []).filter(...)` → empty array
- Dialog shows "No forms found."

**Issue B — No default list:**
When query is empty (`!query`), the filter expression `!query || f.formLabel.includes(...)` evaluates to `true` for all forms. So once the API works, the default list will show automatically. **No code fix needed for this** — it's already correct.

**Issue C — No visible trigger:**
`FormSearchBar.tsx` only renders a `<Dialog>` component. There is no `<Button>` or `<IconButton>` visible in the Header. Users don't know the search exists unless they happen to press Ctrl+K.

# Expected Behaviour

1. **Ctrl+K opens dialog** ✅ Works — no change needed
2. **Default form list**: When dialog opens, show ALL accessible forms without requiring typing
3. **Visible button**: A search icon (magnifying glass) should be visible in the Header so users can click to open
4. **Results load**: After BUG-002 is fixed, the API should return accessible forms

# Actual Behaviour

- Ctrl+K opens an empty dialog showing "No forms found."
- No visible button/icon in the Header
- Users cannot discover the feature

# Root Cause

**Empty results:** Blocked by BUG-002 — `RuntimeFormController` uses `/api/runtime/forms` but frontend calls `/api/v1/runtime/forms`.

**No visible button:** `FormSearchBar.tsx` only renders a `<Dialog>`. There is no trigger component (IconButton) to open it. The `Header.tsx` renders `<FormSearchBar />` which produces no visible UI element.

**Default list:** The filtering logic in `FormSearchBar.tsx` line 37-43 already handles this correctly:
```tsx
const filtered = (forms ?? []).filter(
  (f) => !query || ...  // When query is empty, !query = true, so ALL forms pass
);
```
No change needed here.

# Dependencies

- **Blocked by BUG-002** — Without the API path fix, the form list cannot be fetched
- **Partially blocked by BUG-003** — The sidebar overlap may hide the header on desktop

# Fix

## Fix 1 — Add visible search button to FormSearchBar

In `FormSearchBar.tsx`, add an `IconButton` that serves as the dialog trigger:

```tsx
<>
  <IconButton
    onClick={() => setOpen(true)}
    sx={{ color: 'text.secondary' }}
    aria-label="Search forms (Ctrl+K)"
  >
    <Search />
    <Typography variant="caption" sx={{ ml: 0.5, display: { xs: 'none', sm: 'inline' } }}>
      Search (Ctrl+K)
    </Typography>
  </IconButton>
  <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
    {/* existing dialog content */}
  </Dialog>
</>
```

## Fix 2 — Resolve BUG-002

Fix `ApiVersionConfig.API_BASE` so the `GET /api/v1/runtime/forms` endpoint works. The form list will automatically populate once the API returns results.

## Fix 3 — Default list (already correct)

No code change needed. The `!query` filter already shows all forms when dialog opens.

## Fix 4 — Loading state

Add a loading indicator in the dialog while forms are being fetched:
```tsx
{isLoading && <CircularProgress sx={{ display: 'block', mx: 'auto', my: 2 }} />}
```

# Validation

- [ ] Search icon button visible in the Header bar
- [ ] Clicking the search icon opens the search dialog
- [ ] Ctrl+K opens the search dialog
- [ ] Dialog shows default list of ALL accessible forms on open
- [ ] Typing in search field filters the form list
- [ ] Selecting a form navigates to `/app/runtime?form=...`
- [ ] Loading state shows spinner while forms fetch
- [ ] Works on desktop and mobile

# Files Changed

- `frontend/src/core/runtime/components/FormSearchBar.tsx` — Add IconButton trigger + loading state
- `backend/src/main/java/com/erp/config/ApiVersionConfig.java` — Fix API_BASE (BUG-002 dependency)

# Related Documents

- [TASK-025 — Header Form Search Bar](../tasks/TASK-025-header-form-search-bar.md)
- [BUG-002 — ApiVersionConfig API_BASE mismatch](../tasks/BUG-002-api-base-path-mismatch.md)
- [BUG-003 — Sidebar content overlap](../tasks/BUG-003-sidebar-content-overlap.md)
