<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const profileForm = ref({
  username: '',
  phone: '',
  email: ''
});

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

onMounted(() => {
  if (authStore.user) {
    profileForm.value = {
      username: authStore.user.username,
      phone: authStore.user.phone || '',
      email: authStore.user.email || ''
    };
  }
});

async function handleUpdateProfile() {
  alert('功能开发中...');
}

async function handleChangePassword() {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    alert('两次密码输入不一致');
    return;
  }
  if (passwordForm.value.newPassword.length < 6) {
    alert('密码长度至少6位');
    return;
  }
  alert('功能开发中...');
}

function formatBytes(bytes: number): string {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}
</script>

<template>
  <div class="settings-view">
    <h2>账号设置</h2>

    <section class="settings-section">
      <h3>基本信息</h3>
      <div class="form-card">
        <div class="form-item">
          <label>用户名</label>
          <input v-model="profileForm.username" type="text" />
        </div>
        <div class="form-item">
          <label>手机号</label>
          <input v-model="profileForm.phone" type="tel" placeholder="请输入手机号" />
        </div>
        <div class="form-item">
          <label>邮箱</label>
          <input v-model="profileForm.email" type="email" placeholder="请输入邮箱" />
        </div>
        <button class="btn btn-primary" @click="handleUpdateProfile">保存修改</button>
      </div>
    </section>

    <section class="settings-section">
      <h3>修改密码</h3>
      <div class="form-card">
        <div class="form-item">
          <label>原密码</label>
          <input v-model="passwordForm.oldPassword" type="password" />
        </div>
        <div class="form-item">
          <label>新密码</label>
          <input v-model="passwordForm.newPassword" type="password" />
        </div>
        <div class="form-item">
          <label>确认新密码</label>
          <input v-model="passwordForm.confirmPassword" type="password" />
        </div>
        <button class="btn btn-primary" @click="handleChangePassword">修改密码</button>
      </div>
    </section>

    <section class="settings-section">
      <h3>存储空间</h3>
      <div class="form-card">
        <div class="storage-info">
          <div class="storage-used-bar">
            <div
              class="storage-used-fill"
              :style="{ width: authStore.storagePercentage + '%' }"
            ></div>
          </div>
          <div class="storage-details">
            <span>已使用：{{ formatBytes(authStore.storageUsed) }}</span>
            <span>总容量：{{ formatBytes(authStore.storageLimit) }}</span>
          </div>
        </div>
        <button class="btn btn-default">扩容存储空间</button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.settings-view {
  max-width: 600px;
}

.settings-view h2 {
  margin: 0 0 24px;
  font-size: 20px;
  font-weight: 600;
}

.settings-section {
  margin-bottom: 32px;
}

.settings-section h3 {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 500;
}

.form-card {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: 24px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 500;
}

.form-item input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
}

.form-item input:focus {
  outline: none;
  border-color: var(--color-primary);
}

.storage-info {
  margin-bottom: 20px;
}

.storage-used-bar {
  height: 8px;
  background: var(--color-border);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.storage-used-fill {
  height: 100%;
  background: var(--color-primary);
  transition: width 0.3s;
}

.storage-details {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--color-text-secondary);
}
</style>
