export const apiBaseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:3000/api';
export const appName = import.meta.env.VITE_APP_NAME ?? 'Dynamic ERP Frontend';

export function getAppName() {
  return appName;
}
