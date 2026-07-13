---
id: TEST-BUG-005

task: BUG-005

title: Sidebar FormNavigationMenu shows model names and sub-forms

status: COMPLETED

qa_engineer: QA Engineer

test_date: 2026-07-13

test_scope:
  - FormNavigationMenu rendering
  - Sub-form filtering
  - Naviation behavior

---

# Test Results

| Test | Status | Notes |
|------|--------|-------|
| Forms listed by label only (no model/table group headers) | ✅ PASS | Clean form names |
| Sub-forms (line items) filtered out | ✅ PASS | Only top-level forms shown |
| Clicking form navigates to `/app/runtime?form=xxx` | ✅ PASS | |
| Selected form highlighted in sidebar | ✅ PASS | |
| Dynamic FORMS group has carrot collapse/expand | ✅ PASS | |
| 11 top-level forms visible for admin | ✅ PASS | |
| `pnpm typecheck` — 0 errors | ✅ PASS | TypeScript clean |
