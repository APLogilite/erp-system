import { Navigate, Outlet, useLocation } from 'react-router-dom';

import { useAuthStore } from '@/core/auth/authStore';

export function AdminRoute() {
  const user = useAuthStore((s) => s.user);
  const location = useLocation();
  const isAdmin = user?.roles?.some((r) => r === 'sys_admin' || r === 'tnt_admin');

  if (!isAdmin) {
    return <Navigate to="/app/dashboard" replace state={{ from: location }} />;
  }

  return <Outlet />;
}
