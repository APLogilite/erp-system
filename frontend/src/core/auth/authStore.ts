import { create } from 'zustand';
import { persist } from 'zustand/middleware';

import { AuthStore } from './authTypes';
import { AUTH_STORAGE_KEY } from './storage';

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      refreshToken: null,
      isAuthenticated: false,
      isRefreshing: false,

      login: (user, token, refreshToken) =>
        set({
          user,
          token,
          refreshToken,
          isAuthenticated: true,
        }),

      logout: () =>
        set({
          user: null,
          token: null,
          refreshToken: null,
          isAuthenticated: false,
          isRefreshing: false,
        }),

      setUser: (user) =>
        set({
          user,
          isAuthenticated: !!user,
        }),

      setToken: (token, refreshToken = null) =>
        set((state) => ({
          token,
          refreshToken: refreshToken !== null ? refreshToken : state.refreshToken,
        })),

      setRefreshing: (isRefreshing) => set({ isRefreshing }),
    }),
    {
      name: AUTH_STORAGE_KEY,
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
