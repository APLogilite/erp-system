# T2 — UI Framework Setup

## Objective

Setup the enterprise UI foundation for the ERP runtime using:

- MUI
- Responsive layout shell
- Theme system
- Navigation structure
- Reusable layout architecture

This task creates the visual and structural foundation of the application.

---

# Target Outcome

After T2, developer should have:

```txt id="t2a"
✓ Enterprise layout shell
✓ Responsive sidebar
✓ Header/topbar
✓ Theme system
✓ Dark/light mode
✓ Global layout wrapper
✓ Navigation placeholders
✓ Layout architecture ready for dynamic menus
```

---

# Recommended Stack

| Area               | Technology                 |
| ------------------ | -------------------------- |
| UI Framework       | MUI                        |
| Icons              | MUI Icons                  |
| Styling            | MUI Theme System           |
| Layout             | Flex/Grid                  |
| Theme Persistence  | Zustand/localStorage later |
| Responsive Support | MUI Breakpoints            |

---

# STEP-BY-STEP TASKS

---

# T2.1 — Install UI Dependencies

## Objective

Install enterprise UI framework.

## Required Packages

```bash id="t2b"
pnpm add @mui/material
pnpm add @emotion/react
pnpm add @emotion/styled
pnpm add @mui/icons-material
```

Optional but recommended later:

```bash id="t2c"
pnpm add @fontsource/inter
```

---

## Acceptance Criteria

- packages installed
- no dependency conflicts
- app builds successfully

---

## Test Cases

### TC-1

Run:

```bash id="t2d"
pnpm build
```

Expected:

```txt id="t2e"
Build succeeds without errors
```

---

# T2.2 — Setup Global Theme System

## Objective

Create centralized theme architecture.

---

## Required Structure

```txt id="t2f"
src/themes/
 ├── index.ts
 ├── palette.ts
 ├── typography.ts
 ├── shadows.ts
 ├── lightTheme.ts
 └── darkTheme.ts
```

---

## Requirements

Theme must support:

- light mode
- dark mode
- future tenant branding
- future runtime customization

---

## Create Theme Factory

Example architecture:

```txt id="t2g"
createAppTheme(mode)
```

Should return:

- palette
- typography
- spacing
- breakpoints
- component overrides

---

## Acceptance Criteria

- theme centralized
- no inline hardcoded styles
- supports dark/light switching

---

## Test Cases

### TC-1

Switch theme mode.

Expected:

```txt id="t2h"
Entire app updates correctly
```

---

# T2.3 — Setup Typography System

## Objective

Create enterprise typography standards.

---

## Requirements

Define:

- headings
- body text
- captions
- monospace support
- table typography

Recommended:

- Inter font

---

## Acceptance Criteria

- typography consistent globally
- reusable typography scales

---

## Test Cases

### TC-1

Render:

- heading
- paragraph
- table text

Expected:

```txt id="t2i"
Consistent typography rendering
```

---

# T2.4 — Setup Theme Provider

## Objective

Wrap application with MUI ThemeProvider.

---

## Requirements

Create:

```txt id="t2j"
AppProviders.tsx
```

Should include:

- ThemeProvider
- CssBaseline
- future providers placeholder

---

## Acceptance Criteria

- global styles applied
- theme available everywhere

---

## Test Cases

### TC-1

Access theme inside component.

Expected:

```txt id="t2k"
Theme object accessible
```

---

# T2.5 — Create App Layout Shell

## Objective

Build reusable ERP application shell.

---

## Required Components

```txt id="t2l"
components/layouts/
 ├── AppLayout
 ├── Sidebar
 ├── Header
 ├── ContentArea
 └── Footer (optional)
```

---

## Layout Requirements

Must support:

- sidebar navigation
- header actions
- content container
- responsive collapse
- future dynamic menus

---

## Recommended Structure

```txt id="t2m"
┌───────────────────────────┐
│ Header                    │
├──────────┬────────────────┤
│ Sidebar  │ Content        │
│          │                │
└──────────┴────────────────┘
```

---

## Acceptance Criteria

- responsive layout works
- shell reusable
- content scroll isolated

---

## Test Cases

### TC-1

Resize browser.

Expected:

```txt id="t2n"
Sidebar adapts responsively
```

---

# T2.6 — Create Responsive Sidebar

## Objective

Build ERP-style navigation sidebar.

---

## Requirements

Support:

- nested menus
- collapse mode
- icons
- active route highlighting
- future dynamic menu loading

---

## Must Prepare For

Future:

- permissions
- favorites
- recent items
- plugin menus

---

## Acceptance Criteria

- responsive behavior works
- navigation state maintained

---

## Test Cases

### TC-1

Open mobile width.

Expected:

```txt id="t2o"
Sidebar becomes drawer
```

---

### TC-2

Select route.

Expected:

```txt id="t2p"
Active menu highlighted
```

---

# T2.7 — Create Top Header

## Objective

Create enterprise header/topbar.

---

## Requirements

Support:

- page title
- theme toggle
- notifications placeholder
- profile menu
- breadcrumb placeholder

---

## Acceptance Criteria

- reusable globally
- responsive
- clean spacing

---

## Test Cases

### TC-1

Toggle theme.

Expected:

```txt id="t2q"
Theme changes globally
```

---

# T2.8 — Setup Dark/Light Theme Toggle

## Objective

Implement runtime theme switching.

---

## Requirements

Store:

- selected theme mode

Initial implementation:

- local component state

Later:

- Zustand persistence

---

## Acceptance Criteria

- theme switches instantly
- no reload required

---

## Test Cases

### TC-1

Toggle theme.

Expected:

```txt id="t2r"
Entire UI updates dynamically
```

---

# T2.9 — Create Reusable Page Container

## Objective

Standardize ERP page layouts.

---

## Required Component

```txt id="t2s"
PageContainer
```

Should support:

- title
- actions
- breadcrumbs
- tabs placeholder
- toolbar placeholder

---

## Acceptance Criteria

- pages visually consistent

---

# T2.10 — Setup Basic Dashboard Page

## Objective

Verify layout architecture.

---

## Requirements

Create placeholder:

- dashboard page
- sample cards
- sample widgets

---

## Acceptance Criteria

- layout renders correctly
- responsive behavior verified

---

## Test Cases

### TC-1

Open dashboard.

Expected:

```txt id="t2t"
Dashboard renders inside layout shell
```

---

# T2.11 — Setup Global Spacing & Design Tokens

## Objective

Standardize spacing and sizing.

---

## Requirements

Define:

- spacing scale
- border radius
- shadows
- z-indexes
- container widths

---

## Acceptance Criteria

- no random spacing values
- design system centralized

---

# T2.12 — Setup Loading + Empty State Components

## Objective

Prepare reusable UX states.

---

## Required Components

```txt id="t2u"
- Loader
- FullPageLoader
- EmptyState
- ErrorState
- NoDataState
```

---

## Acceptance Criteria

- reusable globally
- visually consistent

---

# T2.13 — Setup Notification System Placeholder

## Objective

Prepare future runtime notifications.

---

## Requirements

Prepare:

- snackbar provider
- toast architecture

Recommended:

- notistack OR MUI Snackbar

---

## Acceptance Criteria

- notification API callable globally

---

# T2.14 — Setup Error Boundary

## Objective

Prevent runtime crashes.

---

## Requirements

Create:

- ErrorBoundary
- fallback UI
- error logging placeholder

---

## Acceptance Criteria

- app survives component crash

---

## Test Cases

### TC-1

Throw component error.

Expected:

```txt id="t2v"
Fallback UI shown
```

---

# T2.15 — Setup Layout Route Integration

## Objective

Integrate routing with layout shell.

---

## Requirements

Protected structure:

```txt id="t2w"
/login
/app/*
```

ERP shell only inside:

- authenticated routes

---

## Acceptance Criteria

- layout wraps app routes
- public routes excluded

---

# FINAL ACCEPTANCE CRITERIA FOR T2

Developer is DONE only when:

```txt id="t2x"
✓ MUI integrated
✓ Theme system centralized
✓ Dark/light mode works
✓ App shell works
✓ Sidebar responsive
✓ Header implemented
✓ Page container reusable
✓ Layout routing integrated
✓ Error boundary works
✓ Loading states reusable
✓ Build passes
```

---

# FINAL VALIDATION COMMANDS

```bash id="t2y"
pnpm lint
pnpm typecheck
pnpm build
```

Expected:

```txt id="t2z"
All commands pass successfully
```

---

# OUTPUT OF T2

After T2 we should have:

```txt id="t2aa"
Enterprise-ready ERP UI shell
```
