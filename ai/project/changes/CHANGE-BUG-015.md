---
id: CHANGE-BUG-015
task_id: BUG-015
parent_prd: PRD-005
branch: prd/PRD-005-v2
type: Bug
status: IMPLEMENTED
developer: Software Engineer
started: 2026-07-30
completed: 2026-07-30
duration: 30 min
related_files:
  - frontend/src/routes/window/WindowPage.tsx
review_required: false
test_required: true
---

# Summary

Fixed the root breadcrumb displaying the child record's `_display` instead of the parent's when drilled down. Added a `useRef` (`rootDisplayRef`) that captures the root record's `_display` before drilling, and the breadcrumb now uses it instead of `effectiveFormRecord` (which holds child data when drilled).

# Files Modified
- `frontend/src/routes/window/WindowPage.tsx` — added `useRef` import, `rootDisplayRef` + capture effect, updated root breadcrumb condition

# Validation
- `tsc --noEmit` clean
- Code review: root _display captured when `!isDrilled`; persisted in ref across drill operations; breadcrumb uses ref when drilled, `effectiveFormRecord` at root
