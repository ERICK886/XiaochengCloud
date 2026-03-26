<script setup lang="ts">
import { computed } from 'vue';
import type { FileItem } from '@xcd/shared';

const props = defineProps<{
  files: FileItem[];
  isLoading: boolean;
  viewMode: 'list' | 'grid';
  selectedFiles: Set<string>;
}>();

const emit = defineEmits<{
  navigate: [file: FileItem];
  select: [fileId: string];
}>();

function isSelected(fileId: string): boolean {
  return props.selectedFiles.has(fileId);
}

function formatBytes(bytes: number): string {
  if (bytes === 0) return '-';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN');
}

function getFileIcon(fileType: string, isFolder: boolean): string {
  if (isFolder) return '📁';
  switch (fileType) {
    case 'image': return '🖼️';
    case 'video': return '🎬';
    case 'audio': return '🎵';
    case 'document': return '📄';
    case 'archive': return '📦';
    default: return '📄';
  }
}
</script>

<template>
  <div class="file-list-container">
    <div v-if="isLoading" class="loading">
      <span>加载中...</span>
    </div>

    <div v-else-if="files.length === 0" class="empty">
      <span class="empty-icon">📂</span>
      <p>文件夹为空</p>
    </div>

    <div v-else :class="['file-list', viewMode]">
      <div
        v-for="file in files"
        :key="file.id"
        :class="['file-item', { selected: isSelected(file.id) }]"
        @click="emit('navigate', file)"
        @dblclick="emit('navigate', file)"
      >
        <div class="file-checkbox" @click.stop="emit('select', file.id)">
          <input type="checkbox" :checked="isSelected(file.id)" @change="emit('select', file.id)" />
        </div>

        <span class="file-icon">{{ getFileIcon(file.fileType, file.isFolder) }}</span>

        <div class="file-info">
          <span class="file-name">{{ file.filename }}</span>
          <div class="file-meta">
            <span class="file-size">{{ file.isFolder ? '-' : formatBytes(file.fileSize) }}</span>
            <span class="file-date">{{ formatDate(file.updatedAt) }}</span>
          </div>
        </div>

        <div class="file-actions">
          <button v-if="file.isStarred" class="action-btn" title="已收藏">⭐</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.file-list-container {
  flex: 1;
  overflow: auto;
}

.loading,
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: var(--color-text-secondary);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 12px;
}

.file-list {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
}

.file-list.list .file-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--color-border);
  cursor: pointer;
}

.file-list.list .file-item:last-child {
  border-bottom: none;
}

.file-list.grid .file-item {
  display: flex;
  flex-direction: column;
  padding: 16px;
  border-radius: var(--radius-md);
  cursor: pointer;
  width: 120px;
}

.file-item:hover {
  background: var(--color-bg);
}

.file-item.selected {
  background: var(--color-primary-light);
}

.file-checkbox {
  margin-right: 12px;
}

.file-list.grid .file-checkbox {
  position: absolute;
  top: 8px;
  left: 8px;
}

.file-item {
  position: relative;
}

.file-icon {
  font-size: 24px;
  margin-right: 12px;
}

.file-list.grid .file-icon {
  font-size: 48px;
  margin-right: 0;
  margin-bottom: 8px;
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-list.grid .file-name {
  text-align: center;
  font-size: 13px;
}

.file-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.file-list.grid .file-meta {
  justify-content: center;
}

.file-actions {
  margin-left: 12px;
}

.action-btn {
  padding: 4px 8px;
  font-size: 14px;
}
</style>
