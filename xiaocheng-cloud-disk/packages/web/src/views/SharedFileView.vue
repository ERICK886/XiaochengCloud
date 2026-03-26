<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const shareId = route.params.shareId as string;

const isLoading = ref(true);
const error = ref('');
const shareInfo = ref<any>(null);
const password = ref('');
const requirePassword = ref(false);

onMounted(async () => {
  await fetchShareInfo();
});

async function fetchShareInfo(pwd?: string) {
  isLoading.value = true;
  error.value = '';

  try {
    const response = await fetch('/api/shares/public/' + shareId, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ password: pwd })
    });

    if (response.ok) {
      const data = await response.json();
      shareInfo.value = data.data;
      requirePassword.value = data.data.requirePassword;
    } else {
      const data = await response.json();
      if (response.status === 403) {
        requirePassword.value = true;
        error.value = data.message || '请输入密码';
      } else {
        error.value = data.message || '获取分享信息失败';
      }
    }
  } catch (e) {
    error.value = '网络错误';
  } finally {
    isLoading.value = false;
  }
}

function handleSubmitPassword() {
  if (!password.value) {
    error.value = '请输入密码';
    return;
  }
  fetchShareInfo(password.value);
}

async function handleSaveToDisk() {
  const response = await fetch(`/api/shares/${shareId}/save`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });

  if (response.ok) {
    alert('已保存到我的网盘');
  } else {
    alert('保存失败，请先登录');
  }
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
  <div class="shared-file-view">
    <div class="container">
      <div v-if="isLoading" class="loading">加载中...</div>

      <div v-else-if="error && requirePassword" class="password-card">
        <h2>请输入提取码</h2>
        <p class="error" v-if="error">{{ error }}</p>
        <input
          v-model="password"
          type="password"
          placeholder="请输入提取码"
          @keyup.enter="handleSubmitPassword"
        />
        <button class="btn btn-primary" @click="handleSubmitPassword">确定</button>
      </div>

      <div v-else-if="error" class="error-card">
        <h2>出错了</h2>
        <p>{{ error }}</p>
      </div>

      <div v-else-if="shareInfo" class="share-card">
        <div class="share-header">
          <div class="share-icon">📄</div>
          <div class="share-title">
            <h2>{{ shareInfo.file?.filename || '分享文件' }}</h2>
            <p>分享人：{{ shareInfo.createdBy }}</p>
          </div>
        </div>

        <div class="file-info">
          <div class="info-item">
            <span class="label">文件大小</span>
            <span class="value">{{ formatBytes(shareInfo.file?.fileSize || 0) }}</span>
          </div>
          <div class="info-item">
            <span class="label">有效期</span>
            <span class="value">{{ shareInfo.expiresAt || '永久有效' }}</span>
          </div>
        </div>

        <div class="actions">
          <button class="btn btn-primary" @click="handleSaveToDisk">保存到我的网盘</button>
        </div>

        <div class="share-tips">
          <p>提示：登录后可将文件保存到您的网盘</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.shared-file-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  padding: 20px;
}

.container {
  width: 100%;
  max-width: 500px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: var(--color-text-secondary);
}

.password-card,
.error-card,
.share-card {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: 32px;
  text-align: center;
}

.password-card h2,
.error-card h2,
.share-card h2 {
  margin: 0 0 16px;
  font-size: 20px;
}

.password-card input {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  margin-bottom: 16px;
  font-size: 14px;
}

.password-card .error {
  color: var(--color-error);
  margin-bottom: 16px;
}

.error-card p {
  color: var(--color-text-secondary);
}

.share-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  text-align: left;
}

.share-icon {
  font-size: 48px;
}

.share-title h2 {
  margin: 0 0 4px;
}

.share-title p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.file-info {
  display: flex;
  gap: 24px;
  justify-content: center;
  margin-bottom: 24px;
  padding: 16px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.info-item .value {
  font-size: 14px;
  font-weight: 500;
}

.share-tips {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.share-tips p {
  margin: 0;
  font-size: 13px;
  color: var(--color-text-secondary);
}
</style>
