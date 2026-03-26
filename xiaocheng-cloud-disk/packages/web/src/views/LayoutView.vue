<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute, RouterView } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useFileStore } from '@/stores/file';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const fileStore = useFileStore();

const isCollapsed = ref(false);
const showUserMenu = ref(false);

const navItems = [
  { path: '/', icon: 'folder', label: '全部文件' },
  { path: '/star', icon: 'star', label: '我的收藏' },
  { path: '/recent', icon: 'clock', label: '最近使用' },
  { path: '/trash', icon: 'trash', label: '回收站' },
  { path: '/share', icon: 'share', label: '我的分享' },
];

onMounted(async () => {
  await authStore.checkAuth();
  if (authStore.isAuthenticated) {
    await fileStore.fetchFiles(null);
  }
});

function handleLogout() {
  authStore.logout();
  router.push('/login');
}

function formatBytes(bytes: number): string {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function getStoragePercentage(): number {
  return authStore.storagePercentage;
}
</script>

<template>
  <div class="layout">
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <div class="logo" v-if="!isCollapsed">
          <span class="logo-icon">☁</span>
          <span class="logo-text">小程网盘</span>
        </div>
        <button class="collapse-btn" @click="isCollapsed = !isCollapsed">
          {{ isCollapsed ? '→' : '←' }}
        </button>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: route.path === item.path }"
        >
          <span class="nav-icon">{{ item.icon === 'folder' ? '📁' : item.icon === 'star' ? '⭐' : item.icon === 'clock' ? '🕐' : item.icon === 'trash' ? '🗑️' : item.icon === 'share' ? '🔗' : '' }}</span>
          <span class="nav-label" v-if="!isCollapsed">{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer" v-if="!isCollapsed">
        <router-link to="/settings" class="nav-item">
          <span class="nav-icon">⚙️</span>
          <span class="nav-label">设置</span>
        </router-link>
      </div>
    </aside>

    <main class="main-content">
      <header class="top-bar">
        <div class="search-box">
          <input type="text" placeholder="搜索文件..." v-model="fileStore.searchKeyword" />
          <button class="search-btn">🔍</button>
        </div>

        <div class="user-area">
          <div class="storage-info" @click="router.push('/settings')">
            <div class="storage-bar">
              <div class="storage-used" :style="{ width: getStoragePercentage() + '%' }"></div>
            </div>
            <span class="storage-text">{{ formatBytes(authStore.storageUsed) }} / {{ formatBytes(authStore.storageLimit) }}</span>
          </div>

          <div class="user-avatar" @click="showUserMenu = !showUserMenu">
            <span>{{ authStore.user?.username?.[0]?.toUpperCase() || 'U' }}</span>
            
            <div v-if="showUserMenu" class="user-menu">
              <div class="user-info">
                <strong>{{ authStore.user?.username }}</strong>
                <span v-if="authStore.user?.phone">{{ authStore.user.phone }}</span>
              </div>
              <router-link to="/settings" class="menu-item">账号设置</router-link>
              <button class="menu-item" @click="handleLogout">退出登录</button>
            </div>
          </div>
        </div>
      </header>

      <div class="content-area">
        <RouterView />
      </div>
    </main>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 220px;
  background: var(--color-bg-white);
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-header {
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--color-border);
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
}

.collapse-btn {
  padding: 4px 8px;
  color: var(--color-text-secondary);
}

.sidebar-nav {
  flex: 1;
  padding: 12px 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  color: var(--color-text);
  text-decoration: none;
  transition: background 0.2s;
}

.nav-item:hover {
  background: var(--color-bg);
}

.nav-item.active {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.nav-icon {
  font-size: 18px;
}

.nav-label {
  font-size: 14px;
}

.sidebar-footer {
  padding: 12px 8px;
  border-top: 1px solid var(--color-border);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.top-bar {
  height: 60px;
  padding: 0 24px;
  background: var(--color-bg-white);
  border-bottom: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.search-box {
  display: flex;
  align-items: center;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  padding: 0 12px;
}

.search-box input {
  border: none;
  background: none;
  padding: 8px;
  width: 240px;
  outline: none;
}

.search-btn {
  padding: 4px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 20px;
}

.storage-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  cursor: pointer;
}

.storage-bar {
  width: 100px;
  height: 4px;
  background: var(--color-border);
  border-radius: 2px;
  overflow: hidden;
}

.storage-used {
  height: 100%;
  background: var(--color-primary);
  transition: width 0.3s;
}

.storage-text {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  cursor: pointer;
  position: relative;
}

.user-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  width: 200px;
  background: var(--color-bg-white);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
  z-index: 100;
}

.user-info {
  padding: 12px 16px;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
}

.user-info span {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.menu-item {
  display: block;
  width: 100%;
  padding: 10px 16px;
  text-align: left;
  color: var(--color-text);
  text-decoration: none;
  font-size: 14px;
}

.menu-item:hover {
  background: var(--color-bg);
}

.content-area {
  flex: 1;
  padding: 24px;
  overflow: auto;
}
</style>
