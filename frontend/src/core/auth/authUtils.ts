import { useAuthStore } from './authStore';

export function useHasRole(...roles: string[]): boolean {
  const userRoles = useAuthStore((s) => s.user?.roles || []);
  return roles.some((r) => userRoles.includes(r));
}

export function useIsAdmin(): boolean {
  return useHasRole('sys_admin', 'tnt_admin');
}
