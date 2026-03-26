<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useFileStore } from '@/stores/file';
import FileList from '@/components/FileList.vue';
import FileToolbar from '@/components/FileToolbar.vue';

const fileStore = useFileStore();

onMounted(async () => {
  const response = await fetch('/api/files/starred', {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  if (response.ok) {
    const data = await response.json();
    fileStore.files = data.data || [];
  }
});
</script>

<template>
  <div class="star-view">
    <FileToolbar @search="fileStore.searchKeyword = $event" />

    <div class="empty-state" v-if="fileStore.files.length === 0 && !fileStore.isLoading">
      <div class="empty-icon">⭐</div>
      <h3>暂无收藏文件</h3>
      <p>收藏的文件会显示在这里</p>
    </div>

    <FileList
      v-else
      :files="fileStore.files"
      :is-loading="fileStore.isLoading"
      :view-mode="fileStore.viewMode"
      :selected-files="fileStore.selectedFiles"
      @select="fileStore.toggleSelection"
    />
  </div>
</template>

<style scoped>
.star-view {
  display: flex;
  flex-direction: column;
  height: 100%;
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
