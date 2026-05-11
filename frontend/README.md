# Dynamic ERP Frontend

React-based frontend for the Dynamic ERP system built with modern web technologies.

## Overview

A responsive web application for managing ERP operations with an intuitive user interface.

## Technology Stack

- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **Styling**: CSS Modules / Global CSS
- **Routing**: React Router v6
- **State Management**: React Context (extensible to Redux/Zustand)
- **UI Components**: Custom component library
- **Code Quality**: ESLint, Prettier, Husky, lint-staged

## Project Structure

```
frontend/
├── src/
│   ├── main.tsx                     # Application entry point
│   ├── app/                         # Main application components
│   │   ├── App.tsx                  # Root component
│   │   ├── ErrorBoundary.tsx        # Error handling
│   │   └── providers/               # Context providers
│   ├── core/                        # Core functionality
│   │   ├── api/                     # API client and utilities
│   │   ├── auth/                    # Authentication logic
│   │   ├── runtime/                 # Environment and runtime config
│   │   ├── store/                   # State management
│   │   └── metadata/                # Application metadata
│   ├── engine/                      # Business logic engine
│   │   ├── actions/                 # Business actions
│   │   ├── forms/                   # Form configurations
│   │   ├── grids/                   # Data grid configurations
│   │   ├── layouts/                 # Layout configurations
│   │   └── workflows/               # Workflow definitions
│   ├── modules/                     # Feature modules
│   ├── components/                  # Reusable UI components
│   │   ├── widgets/                 # Widget components
│   │   ├── layouts/                 # Layout components
│   │   ├── fields/                  # Form field components
│   │   ├── tables/                  # Table components
│   │   └── dialogs/                 # Dialog/modal components
│   ├── routes/                      # Routing configuration
│   ├── themes/                      # Theme configurations
│   ├── styles/                      # Global styles
│   ├── hooks/                       # Custom React hooks
│   ├── utils/                       # Utility functions
│   └── assets/                      # Static assets
├── .local/                          # Local development tools (ignored)
├── node_modules/                    # Dependencies (ignored)
├── dist/                            # Build output (ignored)
├── package.json                     # Project dependencies and scripts
├── tsconfig.json                    # TypeScript configuration
├── vite.config.ts                   # Vite configuration
├── setup.sh                         # Environment setup script
└── .env*                            # Environment variables (ignored)
```

## Getting Started

### Prerequisites

- Node.js 22+ and pnpm (or use the setup script)

### Environment Setup

Run the automated setup script:

```bash
./setup.sh
```

This will:
- Download and install Node.js 22 locally
- Install pnpm globally
- Install project dependencies

### Manual Setup (Alternative)

If you prefer manual setup:

```bash
# Install dependencies
pnpm install

# Start development server
pnpm dev
```

### Available Scripts

```bash
# Development
pnpm dev          # Start development server
pnpm build        # Build for production
pnpm preview      # Preview production build

# Code Quality
pnpm lint         # Run ESLint
pnpm typecheck    # Run TypeScript type checking
pnpm format       # Format code with Prettier

# Git Hooks
pnpm prepare      # Set up Husky git hooks
```

## Development

### Environment Variables

Create `.env.development` for development settings:

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=Dynamic ERP
```

### Project Architecture

The application follows a modular architecture:

- **Core Layer**: API clients, authentication, configuration
- **Engine Layer**: Business logic, forms, workflows
- **Modules Layer**: Feature-specific components
- **Components Layer**: Reusable UI components

### Key Features

- **Responsive Design**: Works on desktop and mobile
- **Type Safety**: Full TypeScript support
- **Hot Reload**: Fast development with Vite
- **Code Quality**: Automated linting and formatting
- **Error Boundaries**: Graceful error handling

### Routing

Routes are configured in `src/routes/AppRoutes.tsx` with lazy loading for performance.

### State Management

Currently uses React Context. Can be extended to use Redux Toolkit or Zustand for complex state needs.

### API Integration

API calls are handled through `src/core/api/apiClient.ts` with proper error handling and TypeScript types.

## Building for Production

```bash
pnpm build
```

The build output will be in the `dist/` directory.

## Deployment

The application can be deployed to any static hosting service or served by the backend.

## Contributing

1. Follow the existing project structure
2. Use TypeScript for all new code
3. Run `pnpm lint` and `pnpm typecheck` before committing
4. Add tests for new features (when test framework is added)
5. Follow the established naming conventions

## Configuration

### Vite Configuration

Located in `vite.config.ts` - includes path aliases, plugins, and build settings.

### ESLint & Prettier

Configuration files:
- `.eslintrc.cjs` - ESLint rules
- `.prettierrc` - Prettier formatting
- `.lintstagedrc.json` - Pre-commit hooks

### Environment Files

- `.env` - Default environment variables
- `.env.development` - Development overrides
- `.env.production` - Production overrides