export const AUTH_STORAGE_KEY = 'erp-auth-session';

export const authStorage = {
  getToken: (): string | null => {
    try {
      const session = localStorage.getItem(AUTH_STORAGE_KEY);
      if (session) {
        const parsed = JSON.parse(session);
        return parsed.state?.token || null;
      }
    } catch {
      return null;
    }
    return null;
  },
  clear: (): void => {
    localStorage.removeItem(AUTH_STORAGE_KEY);
  },
};
