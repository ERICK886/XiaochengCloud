<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useFileStore } from '@/stores/file';
import FileList from '@/components/FileList.vue';
import FileToolbar from '@/components/FileToolbar.vue';
import UploadModal from '@/components/UploadModal.vue';
import NewFolderModal from '@/components/NewFolderModal.vue';

const route = useRoute();
const router = useRouter();
const fileStore = useFileStore();

const showUploadModal = ref(false);
const showNewFolderModal = ref(false);

const currentFolderId = computed(() => {
  return route.params.id as string || null;
});

onMounted(async () => {
  await fileStore.fetchFiles(currentFolderId.value);
});

watch(() => route.params.id, async (newId) => {
  await fileStore.fetchFiles(newId as string || null);
});

watch(() => fileStore.searchKeyword, async (keyword) => {
  if (keyword) {
    await fileStore.fetchFiles(null, keyword);
  } else {
    await fileStore.fetchFiles(currentFolderId.value);
  }
});

function handleUpload() {
  showUploadModal.value = true;
}

function handleNewFolder() {
  showNewFolderModal.value = true;
}

async function handleSearch(keyword: string) {
  fileStore.searchKeyword = keyword;
}

async function handleNavigate(file: any) {
  if (file.isFolder) {
    router.push(`/folder/${file.id}`);
  }
}

async function handleRefresh() {
  await fileStore.fetchFiles(currentFolderId.value);
}

async function handleCreateFolder(name: string) {
  const result = await fileStore.createFolder(name, currentFolderId.value || undefined);
  if (result.success) {
    showNewFolderModal.value = false;
  }
}
</script>

<template>
  <div class="home-view">
    <FileToolbar
      @upload="handleUpload"
      @new-folder="handleNewFolder"
      @search="handleSearch"
      @refresh="handleRefresh"
    />

    <FileList
      :files="fileStore.files"
      :is-loading="fileStore.isLoading"
      :view-mode="fileStore.viewMode"
      :selected-files="fileStore.selectedFiles"
      @navigate="handleNavigate"
      @select="fileStore.toggleSelection"
    />

    <UploadModal
      v-if="showUploadModal"
      :parent-id="currentFolderId"
      @close="showUploadModal = false"
    />

    <NewFolderModal
      v-if="showNewFolderModal"
      @close="showNewFolderModal = false"
      @create="handleCreateFolder"
    />
  </div>
</template>

<style scoped>
.home-view {
  display: flex;
  flex-direction: column;
  height: 100%;
}
</style>
