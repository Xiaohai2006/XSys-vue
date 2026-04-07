import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
    },
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, admin: true },
      children: [
        {
          path: '',
          name: 'admin-dashboard',
          component: () => import('@/views/admin/DashboardView.vue'),
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/admin/UsersView.vue'),
          meta: { permission: 'system:user:read' },
        },
        {
          path: 'roles',
          name: 'admin-roles',
          component: () => import('@/views/admin/RolesView.vue'),
          meta: { permission: 'system:role:read' },
        },
        {
          path: 'permissions',
          name: 'admin-permissions',
          component: () => import('@/views/admin/PermissionsView.vue'),
          meta: { permission: 'system:role:read' },
        },
        {
          path: 'settings',
          name: 'admin-settings',
          component: () => import('@/views/admin/SettingsView.vue'),
          meta: { permission: 'system:settings:read' },
        },
        {
          path: 'menus',
          name: 'admin-menus',
          component: () => import('@/views/admin/MenusView.vue'),
          meta: { permission: 'system:menu:read' },
        },
      ],
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth) {
    if (!auth.token) {
      return next({ name: 'login', query: { redirect: to.fullPath } })
    }
    if (!auth.user) {
      try {
        await auth.fetchMe()
      } catch {
        return next({ name: 'login' })
      }
    }
    if (to.meta.admin && !auth.canAccessAdmin()) {
      return next({ name: 'home' })
    }
    if (to.meta.permission && !auth.hasPermission(to.meta.permission)) {
      return next({ name: 'admin-dashboard' })
    }
  }
  next()
})

export default router
