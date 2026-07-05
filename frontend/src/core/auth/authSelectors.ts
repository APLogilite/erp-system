import { AuthStore, AuthUser } from './authTypes';

export const selectCurrentUser = (state: AuthStore): AuthUser | null => state.user;
export const selectIsAuthenticated = (state: AuthStore): boolean => state.isAuthenticated;
export const selectAuthToken = (state: AuthStore): string | null => state.token;
export const selectAuthRoles = (state: AuthStore): string[] => state.user?.roles || [];
export const selectAuthPermissions = (state: AuthStore): string[] => state.user?.permissions || [];
export const selectIsRefreshing = (state: AuthStore): boolean => state.isRefreshing;
