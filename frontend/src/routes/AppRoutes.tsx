import { Navigate, Route, Routes } from 'react-router-dom';

import { AdminDashboardPage } from '../modules/identity/admin/AdminDashboardPage';
import { AuditPage } from '../modules/identity/admin/audit/AuditPage';
import { BranchesAdminPage } from '../modules/identity/admin/branches/BranchesAdminPage';
import { CompaniesAdminPage } from '../modules/identity/admin/companies/CompaniesAdminPage';
import { DepartmentsAdminPage } from '../modules/identity/admin/departments/DepartmentsAdminPage';
import { OrganizationsAdminPage } from '../modules/identity/admin/organizations/OrganizationsAdminPage';
import { PermissionsAdminPage } from '../modules/identity/admin/permissions/PermissionsAdminPage';
import { RolesAdminPage } from '../modules/identity/admin/roles/RolesAdminPage';
import { SessionsAdminPage } from '../modules/identity/admin/sessions/SessionsAdminPage';
import { TenantsAdminPage } from '../modules/identity/admin/tenants/TenantsAdminPage';
import { UsersAdminPage } from '../modules/identity/admin/users/UsersAdminPage';
import { ContextSelectPage } from '../modules/identity/context/ContextSelectPage';
import { PreferencesPage } from '../modules/identity/preferences/PreferencesPage';
import { ProfilePage } from '../modules/identity/profile/ProfilePage';
import { SessionsPage } from '../modules/identity/sessions/SessionsPage';

import { ChangePasswordPage } from './auth/change-password/ChangePasswordPage';
import { ForgotPasswordPage } from './auth/forgot-password/ForgotPasswordPage';
import { LoginPage } from './auth/LoginPage';
import { ResetPasswordPage } from './auth/reset-password/ResetPasswordPage';
import { DashboardPage } from './dashboard/DashboardPage';
import { RuntimePage } from './runtime/RuntimePage';

import { AdminRoute } from '@/core/router/guards/AdminRoute';
import { AuthGuard } from '@/core/router/guards/AuthGuard';
import { ContextGuard } from '@/core/router/guards/ContextGuard';
import { GuestRoute } from '@/core/router/guards/GuestRoute';
import { ProtectedRoute } from '@/core/router/guards/ProtectedRoute';

export function AppRoutes() {
  return (
    <Routes>
      {/* Public redirect */}
      <Route path="/" element={<Navigate to="/app/dashboard" replace />} />

      {/* Guest only routes */}
      <Route
        path="/login"
        element={
          <GuestRoute>
            <LoginPage />
          </GuestRoute>
        }
      />
      <Route
        path="/forgot-password"
        element={
          <GuestRoute>
            <ForgotPasswordPage />
          </GuestRoute>
        }
      />
      <Route
        path="/reset-password"
        element={
          <GuestRoute>
            <ResetPasswordPage />
          </GuestRoute>
        }
      />

      {/* Standalone context selection (no sidebar/header) */}
      <Route
        path="/select-context"
        element={
          <AuthGuard>
            <ContextSelectPage />
          </AuthGuard>
        }
      />

      {/* Protected routes — context selection required before access */}
      <Route path="/app" element={<ProtectedRoute />}>
        <Route element={<ContextGuard />}>
          <Route index element={<Navigate to="/app/dashboard" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />

          {/* Identity user pages */}
          <Route path="profile" element={<ProfilePage />} />
          <Route path="preferences" element={<PreferencesPage />} />
          <Route path="change-password" element={<ChangePasswordPage />} />
          <Route path="sessions" element={<SessionsPage />} />

          {/* Identity admin pages (admin role required) */}
          <Route path="admin" element={<AdminRoute />}>
            <Route index element={<AdminDashboardPage />} />
            <Route path="tenants" element={<TenantsAdminPage />} />
            <Route path="organizations" element={<OrganizationsAdminPage />} />
            <Route path="companies" element={<CompaniesAdminPage />} />
            <Route path="branches" element={<BranchesAdminPage />} />
            <Route path="departments" element={<DepartmentsAdminPage />} />
            <Route path="users" element={<UsersAdminPage />} />
            <Route path="roles" element={<RolesAdminPage />} />
            <Route path="permissions" element={<PermissionsAdminPage />} />
            <Route path="sessions" element={<SessionsAdminPage />} />
            <Route path="audit" element={<AuditPage />} />
          </Route>

          {/* Legacy placeholders */}
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
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/app/dashboard" replace />} />
    </Routes>
  );
}
