import { apiClient } from '../client';
import { ENDPOINTS } from '../endpoints';

export interface LoginPayload {
  username: string;
  password?: string;
}

export interface BackendLoginData {
  accessToken: string;
  refreshToken: string;
  expiresAt?: string;
  sessionId?: string;
  user: {
    id: string;
    username: string;
    email: string;
    firstName?: string;
    lastName?: string;
    displayName?: string;
    avatarUrl?: string;
    status?: string;
    roles: string[];
    permissions: string[];
  };
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  errorCode?: string;
}

export const authService = {
  login: async (payload: LoginPayload): Promise<BackendLoginData> => {
    const response = await apiClient.post<ApiResponse<BackendLoginData>>(
      ENDPOINTS.auth.login,
      payload
    );
    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.message || 'Login failed');
    }
    return response.data.data;
  },

  logout: async (): Promise<void> => {
    await apiClient.post(ENDPOINTS.auth.logout);
  },

  refreshSession: async (
    refreshToken: string
  ): Promise<{ accessToken: string; refreshToken: string }> => {
    const response = await apiClient.post<
      ApiResponse<{ accessToken: string; refreshToken: string }>
    >(ENDPOINTS.auth.refresh, { refreshToken });
    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.message || 'Token refresh failed');
    }
    return response.data.data;
  },

  getCurrentUser: async (): Promise<BackendLoginData['user']> => {
    const response = await apiClient.get<ApiResponse<BackendLoginData['user']>>(ENDPOINTS.auth.me);
    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.message || 'Failed to fetch user');
    }
    return response.data.data;
  },

  changePassword: async (currentPassword: string, newPassword: string): Promise<void> => {
    const response = await apiClient.post<ApiResponse<void>>(ENDPOINTS.auth.changePassword, {
      currentPassword,
      newPassword,
    });
    if (!response.data.success) {
      throw new Error(response.data.message || 'Password change failed');
    }
  },
};
