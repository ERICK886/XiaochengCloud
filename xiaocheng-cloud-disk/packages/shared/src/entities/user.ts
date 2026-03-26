export interface User {
  id: string;
  username: string;
  phone?: string;
  email?: string;
  avatar?: string;
  storageUsed: number;
  storageLimit: number;
  createdAt: string;
  updatedAt: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  phone?: string;
  email?: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: User;
}
