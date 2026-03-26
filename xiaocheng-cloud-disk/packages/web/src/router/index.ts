import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/LayoutView.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/HomeView.vue'),
      },
      {
        path: 'folder/:id',
        name: 'Folder',
        component: () => import('@/views/HomeView.vue'),
      },
      {
        path: 'star',
        name: 'Star',
        component: () => import('@/views/StarView.vue'),
      },
      {
        path: 'recent',
        name: 'Recent',
        component: () => import('@/views/RecentView.vue'),
      },
      {
        path: 'trash',
        name: 'Trash',
        component: () => import('@/views/TrashView.vue'),
      },
      {
        path: 'share',
        name: 'Share',
        component: () => import('@/views/ShareView.vue'),
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/SettingsView.vue'),
      },
    ]
  },
  {
    path: '/s/:shareId',
    name: 'SharedFile',
    component: () => import('@/views/SharedFileView.vue'),
    meta: { requiresAuth: false }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore();
  const requiresAuth = to.meta.requiresAuth !== false;

  if (requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
  } else if (!requiresAuth && authStore.isAuthenticated && (to.name === 'Login' || to.name === 'Register')) {
    next({ name: 'Home' });
  } else {
    next();
  }
});

export default router;
