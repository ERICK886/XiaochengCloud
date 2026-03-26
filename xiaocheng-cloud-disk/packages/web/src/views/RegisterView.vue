<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const router = useRouter();

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  phone: ''
});
const isLoading = ref(false);
const errorMessage = ref('');

async function handleRegister() {
  if (!registerForm.value.username || !registerForm.value.password) {
    errorMessage.value = '请输入用户名和密码';
    return;
  }

  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    errorMessage.value = '两次密码输入不一致';
    return;
  }

  if (registerForm.value.password.length < 6) {
    errorMessage.value = '密码长度至少6位';
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';

  const result = await authStore.register({
    username: registerForm.value.username,
    password: registerForm.value.password,
    phone: registerForm.value.phone || undefined
  });
  
  isLoading.value = false;

  if (result.success) {
    router.push('/');
  } else {
    errorMessage.value = result.message || '注册失败';
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <h1 class="logo">小程网盘</h1>
        <p class="subtitle">注册新账号</p>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-item">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="registerForm.username"
            type="text"
            placeholder="请输入用户名"
            autocomplete="username"
          />
        </div>

        <div class="form-item">
          <label for="phone">手机号（可选）</label>
          <input
            id="phone"
            v-model="registerForm.phone"
            type="tel"
            placeholder="请输入手机号"
            autocomplete="tel"
          />
        </div>

        <div class="form-item">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            autocomplete="new-password"
          />
        </div>

        <div class="form-item">
          <label for="confirmPassword">确认密码</label>
          <input
            id="confirmPassword"
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            autocomplete="new-password"
          />
        </div>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <button type="submit" class="btn btn-primary register-btn" :disabled="isLoading">
          {{ isLoading ? '注册中...' : '注册' }}
        </button>
      </form>

      <div class="register-footer">
        <router-link to="/login">已有账号？立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  font-size: 28px;
  color: var(--color-primary);
  margin: 0 0 8px;
}

.subtitle {
  color: var(--color-text-secondary);
  margin: 0;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.form-item input {
  padding: 12px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-item input:focus {
  outline: none;
  border-color: var(--color-primary);
}

.error-message {
  color: var(--color-error);
  font-size: 14px;
  text-align: center;
}

.register-btn {
  width: 100%;
  padding: 12px;
  font-size: 16px;
}

.register-footer {
  margin-top: 24px;
  text-align: center;
}

.register-footer a {
  color: var(--color-primary);
}
</style>
