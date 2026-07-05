import { apiClient } from '../client';
import { ENDPOINTS } from '../endpoints';

export interface LoginPayload {
  username: string;
  password?: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: {
    id: string;
    email: string;
    username: string;
    roles: string[];
    permissions: string[];
  };
}

export const authService = {
  login: async (payload: LoginPayload): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>(ENDPOINTS.auth.login, payload);
    return response.data;
  },

  logout: async (): Promise<void> => {
    await apiClient.post(ENDPOINTS.auth.logout);
  },

  refreshSession: async (
    refreshToken: string
  ): Promise<{ token: string; refreshToken: string }> => {
    const response = await apiClient.post<{ token: string; refreshToken: string }>(
      ENDPOINTS.auth.refresh,
      { refreshToken }
    );
    return response.data;
  },

  getCurrentUser: async (): Promise<LoginResponse['user']> => {
    const response = await apiClient.get<LoginResponse['user']>(ENDPOINTS.auth.me);
    return response.data;
  },
};
