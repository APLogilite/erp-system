export interface AuthUser {
  id: string;
  email: string;
  username: string;
  roles: string[];
  permissions: string[];
}

export interface AuthState {
  user: AuthUser | null;
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isRefreshing: boolean;
}

export interface AuthActions {
  login: (user: AuthUser, token: string, refreshToken: string) => void;
  logout: () => void;
  setUser: (user: AuthUser | null) => void;
  setToken: (token: string | null, refreshToken?: string | null) => void;
  setRefreshing: (isRefreshing: boolean) => void;
}

export type AuthStore = AuthState & AuthActions;
