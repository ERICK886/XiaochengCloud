<script setup lang="ts">
import { ref, onMounted } from 'vue';

const shares = ref<any[]>([]);
const isLoading = ref(false);

onMounted(async () => {
  isLoading.value = true;
  try {
    const response = await fetch('/api/shares?pageSize=100', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (response.ok) {
      const data = await response.json();
      shares.value = data.data?.items || [];
    }
  } finally {
    isLoading.value = false;
  }
});

async function handleCopyLink(shareUrl: string) {
  await navigator.clipboard.writeText(shareUrl);
  alert('链接已复制');
}

async function handleDeleteShare(shareId: string) {
  if (!confirm('确定要取消分享吗？')) return;

  const response = await fetch(`/api/shares/${shareId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });

  if (response.ok) {
    shares.value = shares.value.filter(s => s.id !== shareId);
  }
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '永久有效';
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN');
}

function getShareTypeLabel(type: string): string {
  switch (type) {
    case 'public': return '公开链接';
    case 'password': return '密码链接';
    case 'private': return '指定好友';
    default: return type;
  }
}
</script>

<template>
  <div class="share-view">
    <div class="page-header">
      <h2>我的分享</h2>
    </div>

    <div class="empty-state" v-if="shares.length === 0 && !isLoading">
      <div class="empty-icon">🔗</div>
      <h3>暂无分享记录</h3>
      <p>分享的文件会显示在这里</p>
    </div>

    <div v-else class="share-list">
      <div v-for="share in shares" :key="share.id" class="share-card">
        <div class="share-info">
          <div class="share-name">{{ share.fileName }}</div>
          <div class="share-meta">
            <span class="share-type">{{ getShareTypeLabel(share.shareType) }}</span>
            <span class="share-expires">有效期至：{{ formatDate(share.expiresAt) }}</span>
          </div>
        </div>
        <div class="share-stats">
          <span>👁 {{ share.viewCount }}</span>
          <span>⬇ {{ share.downloadCount }}</span>
        </div>
        <div class="share-actions">
          <button class="btn btn-primary" @click="handleCopyLink(share.shareUrl)">复制链接</button>
          <button class="btn btn-default" @click="handleDeleteShare(share.id)">取消分享</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.share-view {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--color-text-secondary);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px;
  color: var(--color-text);
}

.empty-state p {
  margin: 0;
}

.share-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.share-card {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.share-info {
  flex: 1;
}

.share-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.share-meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.share-stats {
  display: flex;
  gap: 16px;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.share-actions {
  display: flex;
  gap: 8px;
}

.share-actions .btn {
  padding: 6px 12px;
  font-size: 13px;
}
</style>
