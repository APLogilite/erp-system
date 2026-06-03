import { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';

import { selectIsAuthenticated } from '@/core/auth/authSelectors';
import { useAuthStore } from '@/core/auth/authStore';

type AuthGuardProps = {
  children?: ReactNode;
};

export function AuthGuard({ children }: AuthGuardProps) {
  const isAuthenticated = useAuthStore(selectIsAuthenticated);
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return children ? <>{children}</> : null;
}
