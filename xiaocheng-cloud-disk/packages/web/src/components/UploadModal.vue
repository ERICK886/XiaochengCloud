<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
  parentId?: string | null;
}>();

const emit = defineEmits<{
  close: [];
}>();

const isDragging = ref(false);
const selectedFiles = ref<File[]>([]);
const uploadList = ref<any[]>([]);

function handleDragOver(e: DragEvent) {
  e.preventDefault();
  isDragging.value = true;
}

function handleDragLeave() {
  isDragging.value = false;
}

function handleDrop(e: DragEvent) {
  e.preventDefault();
  isDragging.value = false;
  
  const files = e.dataTransfer?.files;
  if (files) {
    addFiles(Array.from(files));
  }
}

function handleFileSelect(e: Event) {
  const input = e.target as HTMLInputElement;
  if (input.files) {
    addFiles(Array.from(input.files));
  }
}

function addFiles(files: File[]) {
  files.forEach(file => {
    uploadList.value.push({
      id: Date.now() + Math.random(),
      name: file.name,
      size: file.size,
      progress: 0,
      status: 'pending'
    });
  });
}

function removeFile(id: number) {
  uploadList.value = uploadList.value.filter(f => f.id !== id);
}

function formatBytes(bytes: number): string {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

async function startUpload() {
  for (const file of uploadList.value) {
    if (file.status === 'pending') {
      file.status = 'uploading';
      await simulateUpload(file);
    }
  }
}

async function simulateUpload(file: any) {
  for (let i = 0; i <= 100; i += 10) {
    await new Promise(resolve => setTimeout(resolve, 100));
    file.progress = i;
  }
  file.status = 'completed';
}

function handleClose() {
  emit('close');
}
</script>

<template>
  <div class="modal-overlay" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3>上传文件</h3>
        <button class="close-btn" @click="handleClose">×</button>
      </div>

      <div
        :class="['upload-area', { dragging: isDragging }]"
        @dragover="handleDragOver"
        @dragleave="handleDragLeave"
        @drop="handleDrop"
      >
        <div class="upload-icon">⬆</div>
        <p>拖拽文件到此处，或 <label class="upload-link">点击选择<input type="file" multiple @change="handleFileSelect" /></label></p>
        <p class="upload-tip">支持所有文件类型，单个文件最大 10GB</p>
      </div>

      <div v-if="uploadList.length > 0" class="upload-list">
        <div v-for="file in uploadList" :key="file.id" class="upload-item">
          <div class="file-info">
            <span class="file-name">{{ file.name }}</span>
            <span class="file-size">{{ formatBytes(file.size) }}</span>
          </div>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: file.progress + '%' }"></div>
          </div>
          <div class="upload-actions">
            <span v-if="file.status === 'uploading'">{{ file.progress }}%</span>
            <span v-else-if="file.status === 'completed'" class="success">已完成</span>
            <button v-if="file.status !== 'uploading'" class="remove-btn" @click="removeFile(file.id)">×</button>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-default" @click="handleClose">取消</button>
        <button class="btn btn-primary" @click="startUpload" :disabled="uploadList.length === 0">开始上传</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 100%;
  max-width: 560px;
  max-height: 80vh;
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-border);
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
}

.close-btn {
  font-size: 24px;
  color: var(--color-text-secondary);
  padding: 0;
  line-height: 1;
}

.upload-area {
  margin: 20px;
  padding: 40px;
  border: 2px dashed var(--color-border);
  border-radius: var(--radius-lg);
  text-align: center;
  transition: all 0.2s;
}

.upload-area.dragging {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.upload-area p {
  margin: 0 0 8px;
}

.upload-link {
  color: var(--color-primary);
  cursor: pointer;
}

.upload-link input {
  display: none;
}

.upload-tip {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.upload-list {
  flex: 1;
  overflow: auto;
  padding: 0 20px;
}

.upload-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
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

.file-size {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.progress-bar {
  width: 100px;
  height: 4px;
  background: var(--color-border);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: var(--color-primary);
  transition: width 0.2s;
}

.upload-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.success {
  color: var(--color-success);
}

.remove-btn {
  font-size: 16px;
  color: var(--color-text-secondary);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid var(--color-border);
}

.btn {
  padding: 8px 20px;
  border-radius: var(--radius-md);
  font-size: 14px;
}

.btn-default {
  background: var(--color-bg-white);
  border: 1px solid var(--color-border);
}

.btn-primary {
  background: var(--color-primary);
  color: #fff;
}

.btn:disabled {
  opacity: 0.5;
}
</style>
