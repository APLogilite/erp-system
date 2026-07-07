import { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

import { parseApiError } from './errors';

import { useAuthStore } from '@/core/auth/authStore';

export function requestInterceptor(config: InternalAxiosRequestConfig): InternalAxiosRequestConfig {
  try {
    // Retrieve token from Zustand auth store dynamically
    const token = useAuthStore.getState().token;
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  } catch {
    // Store not initialized yet, proceed
  }
  return config;
}

export function responseInterceptor(response: AxiosResponse): AxiosResponse {
  return response;
}

export async function responseErrorInterceptor(error: AxiosError): Promise<never> {
  const normalizedError = parseApiError(error);

  // Handle 401 Unauthorized globally by clearing auth session and redirecting
  if (normalizedError.status === 401) {
    try {
      useAuthStore.getState().logout();
    } catch {
      // Fallback
    }
    if (window.location.pathname !== '/login') {
      window.location.href = '/login';
      return Promise.reject(normalizedError);
    }
  }

  return Promise.reject(normalizedError);
}
