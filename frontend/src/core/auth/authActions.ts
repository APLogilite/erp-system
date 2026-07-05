import { useAuthStore } from './authStore';
import { AuthUser } from './authTypes';

export const authActions = {
  login: (user: AuthUser, token: string, refreshToken: string) =>
    useAuthStore.getState().login(user, token, refreshToken),
  logout: () => useAuthStore.getState().logout(),
  setUser: (user: AuthUser | null) => useAuthStore.getState().setUser(user),
  setToken: (token: string | null, refreshToken?: string | null) =>
    useAuthStore.getState().setToken(token, refreshToken),
  setRefreshing: (isRefreshing: boolean) => useAuthStore.getState().setRefreshing(isRefreshing),
};
