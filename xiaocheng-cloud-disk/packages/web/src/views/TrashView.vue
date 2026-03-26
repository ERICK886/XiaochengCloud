<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useFileStore } from '@/stores/file';

const fileStore = useFileStore();
const trashFiles = ref<any[]>([]);
const isLoading = ref(false);
const selectedCount = ref(0);

onMounted(async () => {
  isLoading.value = true;
  try {
    const response = await fetch('/api/files/trash', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (response.ok) {
      const data = await response.json();
      trashFiles.value = data.data || [];
    }
  } finally {
    isLoading.value = false;
  }
});

async function handleRestore(fileId: string) {
  const response = await fetch(`/api/files/${fileId}/restore`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  if (response.ok) {
    trashFiles.value = trashFiles.value.filter(f => f.id !== fileId);
  }
}

async function handlePermanentDelete(fileId: string) {
  if (!confirm('确定要永久删除吗？此操作不可恢复。')) return;
  
  const response = await fetch(`/api/files/${fileId}/permanent`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  if (response.ok) {
    trashFiles.value = trashFiles.value.filter(f => f.id !== fileId);
  }
}

async function handleRestoreAll() {
  const response = await fetch('/api/files/restore', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  if (response.ok) {
    trashFiles.value = [];
  }
}

async function handleClearAll() {
  if (!confirm('确定要清空回收站吗？此操作不可恢复。')) return;
  
  const response = await fetch('/api/files/trash/clear', {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  if (response.ok) {
    trashFiles.value = [];
  }
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN');
}
</script>

<template>
  <div class="trash-view">
    <div class="page-header">
      <h2>回收站</h2>
      <div class="header-actions" v-if="trashFiles.length > 0">
        <button class="btn btn-default" @click="handleRestoreAll">恢复全部</button>
        <button class="btn btn-default" @click="handleClearAll">清空回收站</button>
      </div>
    </div>

    <div class="empty-state" v-if="trashFiles.length === 0 && !isLoading">
      <div class="empty-icon">🗑️</div>
      <h3>回收站是空的</h3>
      <p>删除的文件将在回收站保留30天</p>
    </div>

    <div v-else class="file-table">
      <div class="table-header">
        <div class="col-name">文件名</div>
        <div class="col-size">大小</div>
        <div class="col-date">删除日期</div>
        <div class="col-actions">操作</div>
      </div>

      <div
        v-for="file in trashFiles"
        :key="file.id"
        class="table-row"
      >
        <div class="col-name">
          <span class="file-icon">{{ file.isFolder ? '📁' : '📄' }}</span>
          <span class="file-name">{{ file.filename }}</span>
        </div>
        <div class="col-size">
          {{ file.isFolder ? '-' : (file.fileSize / 1024 / 1024).toFixed(2) + ' MB' }}
        </div>
        <div class="col-date">{{ formatDate(file.deletedAt) }}</div>
        <div class="col-actions">
          <button class="action-btn" @click="handleRestore(file.id)">恢复</button>
          <button class="action-btn danger" @click="handlePermanentDelete(file.id)">永久删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.trash-view {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
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

.file-table {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.table-header {
  display: flex;
  padding: 12px 16px;
  background: var(--color-bg);
  font-weight: 500;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.table-row {
  display: flex;
  padding: 12px 16px;
  border-bottom: 1px solid var(--color-border);
  align-items: center;
}

.table-row:last-child {
  border-bottom: none;
}

.table-row:hover {
  background: var(--color-bg);
}

.col-name {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.col-size {
  width: 100px;
  text-align: right;
  color: var(--color-text-secondary);
}

.col-date {
  width: 120px;
  text-align: center;
  color: var(--color-text-secondary);
}

.col-actions {
  width: 160px;
  text-align: right;
}

.action-btn {
  padding: 4px 12px;
  font-size: 13px;
  color: var(--color-primary);
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-sm);
  margin-left: 8px;
}

.action-btn:hover {
  background: var(--color-primary-light);
}

.action-btn.danger {
  color: var(--color-error);
  border-color: var(--color-error);
}

.action-btn.danger:hover {
  background: #fff1f0;
}
</style>
