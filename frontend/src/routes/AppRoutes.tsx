import { Navigate, Route, Routes } from 'react-router-dom';

import { LoginPage } from './auth/LoginPage';
import { DashboardPage } from './dashboard/DashboardPage';
import { RuntimePage } from './runtime/RuntimePage';

import { GuestRoute } from '@/core/router/guards/GuestRoute';
import { ProtectedRoute } from '@/core/router/guards/ProtectedRoute';

export function AppRoutes() {
  return (
    <Routes>
      {/* Public redirect to dashboard */}
      <Route path="/" element={<Navigate to="/app/dashboard" replace />} />

      {/* Guest only routes (unauthenticated) */}
      <Route
        path="/login"
        element={
          <GuestRoute>
            <LoginPage />
          </GuestRoute>
        }
      />

      {/* Protected routes (requires auth, renders AppLayout shell) */}
      <Route path="/app" element={<ProtectedRoute />}>
        <Route index element={<Navigate to="/app/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route
          path="products"
          element={
            <div style={{ padding: 24 }}>
              <h3>Products Module</h3>
              <p>Coming Soon...</p>
            </div>
          }
        />
        <Route
          path="orders"
          element={
            <div style={{ padding: 24 }}>
              <h3>Orders Module</h3>
              <p>Coming Soon...</p>
            </div>
          }
        />
        <Route
          path="users"
          element={
            <div style={{ padding: 24 }}>
              <h3>Users Module</h3>
              <p>Coming Soon...</p>
            </div>
          }
        />
        <Route
          path="settings"
          element={
            <div style={{ padding: 24 }}>
              <h3>Settings Module</h3>
              <p>Coming Soon...</p>
            </div>
          }
        />
        <Route path="runtime" element={<RuntimePage />} />
      </Route>

      {/* Fallback route */}
      <Route path="*" element={<Navigate to="/app/dashboard" replace />} />
    </Routes>
  );
}
