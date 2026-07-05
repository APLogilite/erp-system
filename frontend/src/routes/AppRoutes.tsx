import { Navigate, Route, Routes } from 'react-router-dom';

import { ChangePasswordPage } from './auth/change-password/ChangePasswordPage';
import { ForgotPasswordPage } from './auth/forgot-password/ForgotPasswordPage';
import { LoginPage } from './auth/LoginPage';
import { ResetPasswordPage } from './auth/reset-password/ResetPasswordPage';
import { DashboardPage } from './dashboard/DashboardPage';
import { AuditPage } from './identity/admin/audit/AuditPage';
import { BranchesAdminPage } from './identity/admin/branches/BranchesAdminPage';
import { CompaniesAdminPage } from './identity/admin/companies/CompaniesAdminPage';
import { DepartmentsAdminPage } from './identity/admin/departments/DepartmentsAdminPage';
import { OrganizationsAdminPage } from './identity/admin/organizations/OrganizationsAdminPage';
import { PermissionsAdminPage } from './identity/admin/permissions/PermissionsAdminPage';
import { RolesAdminPage } from './identity/admin/roles/RolesAdminPage';
import { SessionsAdminPage } from './identity/admin/sessions/SessionsAdminPage';
import { TenantsAdminPage } from './identity/admin/tenants/TenantsAdminPage';
import { UsersAdminPage } from './identity/admin/users/UsersAdminPage';
import { PreferencesPage } from './identity/preferences/PreferencesPage';
import { ProfilePage } from './identity/profile/ProfilePage';
import { SessionsPage } from './identity/sessions/SessionsPage';
import { RuntimePage } from './runtime/RuntimePage';

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

      {/* Protected routes */}
      <Route path="/app" element={<ProtectedRoute />}>
        <Route index element={<Navigate to="/app/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />

        {/* Identity user pages */}
        <Route path="profile" element={<ProfilePage />} />
        <Route path="preferences" element={<PreferencesPage />} />
        <Route path="change-password" element={<ChangePasswordPage />} />
        <Route path="sessions" element={<SessionsPage />} />

        {/* Identity admin pages */}
        <Route path="admin/tenants" element={<TenantsAdminPage />} />
        <Route path="admin/organizations" element={<OrganizationsAdminPage />} />
        <Route path="admin/companies" element={<CompaniesAdminPage />} />
        <Route path="admin/branches" element={<BranchesAdminPage />} />
        <Route path="admin/departments" element={<DepartmentsAdminPage />} />
        <Route path="admin/users" element={<UsersAdminPage />} />
        <Route path="admin/roles" element={<RolesAdminPage />} />
        <Route path="admin/permissions" element={<PermissionsAdminPage />} />
        <Route path="admin/sessions" element={<SessionsAdminPage />} />
        <Route path="admin/audit" element={<AuditPage />} />

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

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/app/dashboard" replace />} />
    </Routes>
  );
}
