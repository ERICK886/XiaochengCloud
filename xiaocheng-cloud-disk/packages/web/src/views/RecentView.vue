<script setup lang="ts">
import { ref, onMounted } from 'vue';
import FileList from '@/components/FileList.vue';
import { useFileStore } from '@/stores/file';

const fileStore = useFileStore();
const recentFiles = ref<any[]>([]);
const isLoading = ref(false);

onMounted(async () => {
  isLoading.value = true;
  try {
    const response = await fetch('/api/files/recent?limit=50', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (response.ok) {
      const data = await response.json();
      recentFiles.value = data.data || [];
    }
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div class="recent-view">
    <div class="page-header">
      <h2>最近使用</h2>
    </div>

    <div class="empty-state" v-if="recentFiles.length === 0 && !isLoading">
      <div class="empty-icon">🕐</div>
      <h3>暂无最近文件</h3>
      <p>最近访问的文件会显示在这里</p>
    </div>

    <FileList
      v-else
      :files="recentFiles"
      :is-loading="isLoading"
      :view-mode="fileStore.viewMode"
      :selected-files="fileStore.selectedFiles"
      @select="fileStore.toggleSelection"
    />
  </div>
</template>

<style scoped>
.recent-view {
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
</style>
