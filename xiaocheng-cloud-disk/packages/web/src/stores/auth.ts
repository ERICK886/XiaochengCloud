import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { User, LoginRequest, RegisterRequest } from '@xcd/shared';
import { authApi } from '@xcd/shared';
import { apiClient } from '@xcd/shared';

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null);
  const token = ref<string | null>(localStorage.getItem('token'));
  const isLoading = ref(false);

  const isAuthenticated = computed(() => !!token.value && !!user.value);
  const storageUsed = computed(() => user.value?.storageUsed ?? 0);
  const storageLimit = computed(() => user.value?.storageLimit ?? 5 * 1024 * 1024 * 1024);
  const storagePercentage = computed(() => {
    if (storageLimit.value === 0) return 0;
    return Math.round((storageUsed.value / storageLimit.value) * 100);
  });

  function setToken(newToken: string | null) {
    token.value = newToken;
    if (newToken) {
      localStorage.setItem('token', newToken);
      apiClient.setToken(newToken);
    } else {
      localStorage.removeItem('token');
      apiClient.clearToken();
    }
  }

  async function login(credentials: LoginRequest) {
    isLoading.value = true;
    try {
      const response = await authApi.login(credentials);
      if (response.code === 0 && response.data) {
        setToken(response.data.token);
        user.value = response.data.user;
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: '登录失败，请检查网络连接' };
    } finally {
      isLoading.value = false;
    }
  }

  async function register(data: RegisterRequest) {
    isLoading.value = true;
    try {
      const response = await authApi.register(data);
      if (response.code === 0 && response.data) {
        setToken(response.data.token);
        user.value = response.data.user;
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: '注册失败，请检查网络连接' };
    } finally {
      isLoading.value = false;
    }
  }

  async function logout() {
    try {
      await authApi.logout();
    } catch {
      // ignore error
    } finally {
      setToken(null);
      user.value = null;
    }
  }

  async function fetchCurrentUser() {
    if (!token.value) return;
    try {
      const response = await authApi.getCurrentUser();
      if (response.code === 0) {
        user.value = response.data;
      }
    } catch {
      setToken(null);
    }
  }

  async function checkAuth() {
    if (token.value) {
      apiClient.setToken(token.value);
      await fetchCurrentUser();
      if (!user.value) {
        setToken(null);
      }
    }
  }

  return {
    user,
    token,
    isLoading,
    isAuthenticated,
    storageUsed,
    storageLimit,
    storagePercentage,
    login,
    register,
    logout,
    fetchCurrentUser,
    checkAuth,
    setToken
  };
});
