import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { FileItem, FileListRequest, FileType } from '@xcd/shared';
import { fileApi } from '@xcd/shared';

export type ViewMode = 'list' | 'grid';
export type SortField = 'name' | 'size' | 'createdAt' | 'updatedAt';
export type SortOrder = 'asc' | 'desc';

export const useFileStore = defineStore('file', () => {
  const files = ref<FileItem[]>([]);
  const currentFolderId = ref<string | null>(null);
  const currentPath = ref<FileItem[]>([]);
  const isLoading = ref(false);
  const viewMode = ref<ViewMode>('list');
  const sortField = ref<SortField>('name');
  const sortOrder = ref<SortOrder>('asc');
  const searchKeyword = ref('');
  const selectedFiles = ref<Set<string>>(new Set());
  const uploadQueue = ref<any[]>([]);
  const uploadProgress = ref<Map<string, any>>(new Map());

  const hasSelection = computed(() => selectedFiles.value.size > 0);
  const selectedCount = computed(() => selectedFiles.value.size);

  async function fetchFiles(parentId: string | null = null, search?: string) {
    isLoading.value = true;
    currentFolderId.value = parentId;
    
    try {
      const params: FileListRequest = {
        parentId,
        sortBy: sortField.value,
        sortOrder: sortOrder.value,
        page: 1,
        pageSize: 1000
      };
      
      if (search) {
        params.search = search;
      }

      const response = search 
        ? await fileApi.search({ keyword: search, parentId, page: 1, pageSize: 1000 })
        : await fileApi.list(params);
      
      if (response.code === 0) {
        files.value = response.data.items;
      }
    } catch (error) {
      console.error('Failed to fetch files:', error);
    } finally {
      isLoading.value = false;
    }
  }

  async function createFolder(name: string, parentId?: string) {
    try {
      const response = await fileApi.createFolder(name, parentId);
      if (response.code === 0) {
        await fetchFiles(currentFolderId.value);
        return { success: true, file: response.data };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: '创建文件夹失败' };
    }
  }

  async function renameFile(fileId: string, filename: string) {
    try {
      const response = await fileApi.rename(fileId, filename);
      if (response.code === 0) {
        const index = files.value.findIndex(f => f.id === fileId);
        if (index !== -1) {
          files.value[index] = response.data;
        }
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: '重命名失败' };
    }
  }

  async function moveFiles(fileIds: string[], targetFolderId: string | null) {
    try {
      const response = await fileApi.move({ fileIds, targetParentId: targetFolderId });
      if (response.code === 0) {
        await fetchFiles(currentFolderId.value);
        selectedFiles.value.clear();
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: '移动失败' };
    }
  }

  async function deleteFiles(fileIds: string[]) {
    try {
      const response = await fileApi.delete(fileIds);
      if (response.code === 0) {
        await fetchFiles(currentFolderId.value);
        selectedFiles.value.clear();
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: '删除失败' };
    }
  }

  async function starFiles(fileIds: string[]) {
    try {
      const response = await fileApi.star(fileIds);
      if (response.code === 0) {
        await fetchFiles(currentFolderId.value);
        selectedFiles.value.clear();
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: '收藏失败' };
    }
  }

  async function unstarFiles(fileIds: string[]) {
    try {
      const response = await fileApi.unstar(fileIds);
      if (response.code === 0) {
        await fetchFiles(currentFolderId.value);
        selectedFiles.value.clear();
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: '取消收藏失败' };
    }
  }

  function toggleSelection(fileId: string) {
    if (selectedFiles.value.has(fileId)) {
      selectedFiles.value.delete(fileId);
    } else {
      selectedFiles.value.add(fileId);
    }
    selectedFiles.value = new Set(selectedFiles.value);
  }

  function selectAll() {
    selectedFiles.value = new Set(files.value.map(f => f.id));
  }

  function clearSelection() {
    selectedFiles.value.clear();
    selectedFiles.value = new Set();
  }

  function navigateToFolder(folderId: string | null) {
    if (folderId === null) {
      currentPath.value = [];
    } else {
      const folder = files.value.find(f => f.id === folderId);
      if (folder) {
        const existingIndex = currentPath.value.findIndex(f => f.id === folderId);
        if (existingIndex !== -1) {
          currentPath.value = currentPath.value.slice(0, existingIndex);
        } else {
          currentPath.value.push(folder);
        }
      }
    }
    fetchFiles(folderId);
  }

  function setSort(field: SortField, order: SortOrder) {
    sortField.value = field;
    sortOrder.value = order;
    fetchFiles(currentFolderId.value);
  }

  function setViewMode(mode: ViewMode) {
    viewMode.value = mode;
  }

  return {
    files,
    currentFolderId,
    currentPath,
    isLoading,
    viewMode,
    sortField,
    sortOrder,
    searchKeyword,
    selectedFiles,
    uploadQueue,
    uploadProgress,
    hasSelection,
    selectedCount,
    fetchFiles,
    createFolder,
    renameFile,
    moveFiles,
    deleteFiles,
    starFiles,
    unstarFiles,
    toggleSelection,
    selectAll,
    clearSelection,
    navigateToFolder,
    setSort,
    setViewMode
  };
});
