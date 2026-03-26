<script setup lang="ts">
import { ref } from 'vue';

const emit = defineEmits<{
  close: [];
  create: [name: string];
}>();

const folderName = ref('');

function handleCreate() {
  if (!folderName.value.trim()) {
    return;
  }
  emit('create', folderName.value.trim());
  folderName.value = '';
}

function handleClose() {
  emit('close');
}
</script>

<template>
  <div class="modal-overlay" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3>新建文件夹</h3>
        <button class="close-btn" @click="handleClose">×</button>
      </div>

      <div class="modal-body">
        <input
          v-model="folderName"
          type="text"
          placeholder="请输入文件夹名称"
          autofocus
          @keyup.enter="handleCreate"
        />
      </div>

      <div class="modal-footer">
        <button class="btn btn-default" @click="handleClose">取消</button>
        <button class="btn btn-primary" @click="handleCreate" :disabled="!folderName.trim()">创建</button>
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
  max-width: 400px;
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
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

.modal-body {
  padding: 20px;
}

.modal-body input {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
}

.modal-body input:focus {
  outline: none;
  border-color: var(--color-primary);
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
