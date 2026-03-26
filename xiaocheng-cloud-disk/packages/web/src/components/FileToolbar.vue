<script setup lang="ts">
import { ref } from 'vue';
import { useFileStore } from '@/stores/file';

const emit = defineEmits<{
  upload: [];
  'new-folder': [];
  search: [keyword: string];
  refresh: [];
}>();

const fileStore = useFileStore();
const searchInput = ref('');

function handleSearch() {
  emit('search', searchInput.value);
}

function handleSort(field: string) {
  const currentField = fileStore.sortField;
  const currentOrder = fileStore.sortOrder;
  
  if (field === currentField) {
    fileStore.setSort(field as any, currentOrder === 'asc' ? 'desc' : 'asc');
  } else {
    fileStore.setSort(field as any, 'asc');
  }
}

function getSortIcon(field: string): string {
  if (fileStore.sortField !== field) return '';
  return fileStore.sortOrder === 'asc' ? '↑' : '↓';
}
</script>

<template>
  <div class="file-toolbar">
    <div class="toolbar-left">
      <button class="btn btn-primary" @click="emit('upload')">
        <span>⬆</span> 上传
      </button>
      <button class="btn btn-default" @click="emit('new-folder')">
        <span>📁</span> 新建文件夹
      </button>

      <div v-if="fileStore.hasSelection" class="selection-info">
        <span>已选择 {{ fileStore.selectedCount }} 个项目</span>
        <button class="link-btn" @click="fileStore.clearSelection">取消选择</button>
      </div>
    </div>

    <div class="toolbar-right">
      <div class="search-box">
        <input
          v-model="searchInput"
          type="text"
          placeholder="搜索文件..."
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch">🔍</button>
      </div>

      <div class="sort-buttons">
        <button
          :class="['sort-btn', { active: fileStore.sortField === 'name' }]"
          @click="handleSort('name')"
        >
          名称 {{ getSortIcon('name') }}
        </button>
        <button
          :class="['sort-btn', { active: fileStore.sortField === 'size' }]"
          @click="handleSort('size')"
        >
          大小 {{ getSortIcon('size') }}
        </button>
        <button
          :class="['sort-btn', { active: fileStore.sortField === 'updatedAt' }]"
          @click="handleSort('updatedAt')"
        >
          修改时间 {{ getSortIcon('updatedAt') }}
        </button>
      </div>

      <div class="view-toggle">
        <button
          :class="['view-btn', { active: fileStore.viewMode === 'list' }]"
          @click="fileStore.setViewMode('list')"
        >
          ☰
        </button>
        <button
          :class="['view-btn', { active: fileStore.viewMode === 'grid' }]"
          @click="fileStore.setViewMode('grid')"
        >
          ⊞
        </button>
      </div>

      <button class="btn btn-default" @click="emit('refresh')">
        🔄
      </button>
    </div>
  </div>
</template>

<style scoped>
.file-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-primary {
  background-color: var(--color-primary);
  color: #fff;
}

.btn-primary:hover {
  background-color: var(--color-primary-hover);
}

.btn-default {
  background-color: var(--color-bg-white);
  color: var(--color-text);
  border: 1px solid var(--color-border);
}

.btn-default:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.selection-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: var(--color-primary-light);
  border-radius: var(--radius-md);
  font-size: 13px;
  color: var(--color-primary);
}

.link-btn {
  color: var(--color-primary);
  text-decoration: underline;
  font-size: 13px;
}

.search-box {
  display: flex;
  align-items: center;
  background: var(--color-bg-white);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.search-box input {
  padding: 8px 12px;
  border: none;
  background: none;
  width: 180px;
  outline: none;
}

.search-box button {
  padding: 8px 12px;
  background: none;
}

.sort-buttons {
  display: flex;
  gap: 4px;
}

.sort-btn {
  padding: 6px 10px;
  font-size: 13px;
  color: var(--color-text-secondary);
  border-radius: var(--radius-sm);
}

.sort-btn:hover {
  background: var(--color-bg);
}

.sort-btn.active {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.view-toggle {
  display: flex;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.view-btn {
  padding: 6px 12px;
  font-size: 16px;
  background: var(--color-bg-white);
}

.view-btn:first-child {
  border-right: 1px solid var(--color-border);
}

.view-btn:hover {
  background: var(--color-bg);
}

.view-btn.active {
  background: var(--color-primary);
  color: #fff;
}
</style>
