import { apiBaseUrl } from '@/core/runtime/env';

export const apiConfig = {
  baseURL: apiBaseUrl,
  timeout: 15000, // 15 seconds timeout
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
};
