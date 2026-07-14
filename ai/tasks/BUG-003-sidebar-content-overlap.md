---
id: BUG-003

title: Sidebar and main content overlap — AppLayout missing margin compensation for permanent Drawer

status: IN_DEVELOPMENT

priority: High

severity: High

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: bugfix/BUG-003

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed:

parent_prd: PRD-001

parent_task: TASK-011

reported_by: User

detected_in: UI layout (localhost:5173)

related_test:

fix_summary: ai/changes/CHANGE-BUG-003.md

verification_report:

history:
  - 2026-07-13 — Product Manager — Created bug task. Sidebar drawer overlaps main content area; was previously responsive.
  - 2026-07-13 — Software Engineer — Phase 1 done: added marginLeft compensation in AppLayout. Sidebar no longer overlaps content. Phase 2 (page responsiveness audit) and Phase 3 (ContentArea Container) still pending.

---

# Summary

The entire application layout is broken — sidebar overlaps main content on desktop, and individual pages have responsive issues on mobile. Full review of ALL pages for desktop + mobile responsiveness is required.

# Problem

**Layer 1 — AppLayout sidebar overlap (critical):**
The `AppLayout` uses `display: flex` with a permanent MUI `Drawer`. The Drawer uses `position: fixed` by default, which removes it from the normal document flow, causing it to overlap the main content.

The `ContentArea` MUI Container has `maxWidth: 'lg'` which may create gaps/overflow issues when combined with the drawer width.

**Layer 2 — Individual page responsiveness:**
Every page component needs to be reviewed for:
- Mobile viewport rendering (width < 900px)
- Tablet viewport rendering (900px - 1200px)
- Desktop viewport rendering (> 1200px)
- Content overflow, padding, margins at each breakpoint

# Pages to Audit for Responsiveness

| Page | Component | Notes |
|------|-----------|-------|
| **Dashboard** | `DashboardPage.tsx` | First page after login |
| **Table Designer** | `TableListPage.tsx`, `CreateTablePage.tsx`, `TableDetailPage.tsx` | Grid, create form, detail view |
| **Form Designer** | `FormListPage.tsx`, `FormDesignerPage.tsx` | 5-tab interface, complex layouts |
| **Login** | `LoginPage.tsx` | Should be centered, full viewport |
| **Admin pages** | All identity admin pages | Data tables, filters, dialogs |
| **Profile** | `ProfilePage.tsx` | Form layout |
| **Preferences** | `PreferencesPage.tsx` | Settings layout |
| **Context Select** | `ContextSelectPage.tsx` | Card grid |
| **Runtime** | `RuntimePage.tsx` | Dynamic form rendering |
| **Coming Soon** | Products, Orders, Users, Settings | Placeholder divs |

# Expected Behaviour

**Desktop (> 1200px):**
- Sidebar (280px) visible on the left, content to the right — no overlap
- All page content properly laid out within the remaining space
- No horizontal scrollbar
- AG Grid columns fit within viewport

**Tablet (900px - 1200px):**
- Sidebar visible, may be narrower if needed
- Content adjusts to narrower width
- All form fields, tables remain usable

**Mobile (< 900px):**
- Sidebar is hidden by default (temporary variant), opens via hamburger menu
- Content uses full viewport width
- Forms stack vertically (single column)
- AG Grid shows horizontal scroll or simplified columns
- Dialogs use full width on mobile

# Actual Behaviour

**Desktop:** Sidebar overlaps content. Dashboard and admin pages are partially hidden behind the sidebar.

**Mobile:** Hamburger menu toggles sidebar on/off, but content may still have layout issues.

(Complete audit results to be filled during fix.)

# Root Cause

**AppLayout:** The permanent MUI Drawer uses `position: fixed` by default, removing it from the flex flow. The main content has no compensating `marginLeft`.

**General pages:** Each page uses theme breakpoints inconsistently. Some pages use MUI `Container` with `maxWidth` which can cause clipping when `Container` width > viewport minus drawer width.

# Fix

## Phase 1 — AppLayout (Critical)

Add `marginLeft` to the content container in `AppLayout.tsx`:

```tsx
<Box sx={{ 
  flexGrow: 1, 
  display: 'flex', 
  flexDirection: 'column',
  ml: { md: `${drawerWidth}px` },  // 280px on desktop
  xs: 0                             // 0 on mobile
}}>
```

## Phase 2 — Individual Pages (Must fix)

Review and fix each page for:
1. Uses MUI responsive Grid (`Grid2`) or Box with breakpoint props
2. ContentArea Container sets appropriate `maxWidth` (or `false`) per page
3. Data tables (AG Grid) have responsive column sizing
4. Forms use full width on mobile (inputs stack vertically)
5. Dialogs use `fullScreen` on mobile via `useMediaQuery`
6. Sidebar items wrap properly on narrow desktop widths

## Phase 3 — ContentArea Container

The `maxWidth: 'lg'` on `ContentArea.tsx` may cause content to be narrower than the available space. Consider:
- Removing `maxWidth` constraint (let content fill available space)
- Or using a responsive `maxWidth` that adapts to viewport minus drawer width

# Validation

- [ ] Desktop: Sidebar visible, content to the right — no overlap
- [ ] Desktop: All pages render correctly (Dashboard, Table Designer, Form Designer, Admin pages, Runtime)
- [ ] Desktop: No horizontal scrollbar
- [ ] Tablet (900-1200px): Content adjusts smoothly
- [ ] Mobile: Sidebar hidden, hamburger menu works
- [ ] Mobile: Forms stack vertically, no horizontal overflow
- [ ] Mobile: Dialogs use full width
- [ ] Mobile: AG Grid scrolls horizontally or shows simplified columns
- [ ] All page transitions preserve responsive layout
- [ ] Header search bar (Ctrl+K) is accessible on all viewports

# Files Changed

- `frontend/src/components/layouts/AppLayout/AppLayout.tsx` — Add marginLeft to content container
- Potentially `frontend/src/components/layouts/Sidebar/Sidebar.tsx` — Ensure permanent drawer uses fixed positioning

# Related Documents

- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
