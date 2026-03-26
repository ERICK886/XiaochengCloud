import { apiClient } from './client';
import type {
  User,
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  StorageInfo
} from '../entities/user';
import type { ApiResponse } from '../index';

export const authApi = {
  async login(request: LoginRequest): Promise<ApiResponse<AuthResponse>> {
    return apiClient.post('/auth/login', request);
  },

  async register(request: RegisterRequest): Promise<ApiResponse<AuthResponse>> {
    return apiClient.post('/auth/register', request);
  },

  async logout(): Promise<ApiResponse<void>> {
    return apiClient.post('/auth/logout');
  },

  async refreshToken(refreshToken: string): Promise<ApiResponse<AuthResponse>> {
    return apiClient.post('/auth/refresh-token', { refreshToken });
  },

  async getCurrentUser(): Promise<ApiResponse<User>> {
    return apiClient.get('/auth/me');
  },

  async updateProfile(data: Partial<User>): Promise<ApiResponse<User>> {
    return apiClient.put('/auth/profile', data);
  },

  async changePassword(oldPassword: string, newPassword: string): Promise<ApiResponse<void>> {
    return apiClient.post('/auth/change-password', { oldPassword, newPassword });
  },

  async getStorageInfo(): Promise<ApiResponse<StorageInfo>> {
    return apiClient.get('/auth/storage');
  },
};
