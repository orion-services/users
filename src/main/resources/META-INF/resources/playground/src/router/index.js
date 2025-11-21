import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/2fa',
    name: '2fa',
    component: () => import('../views/TwoFactorView.vue')
  },
  {
    path: '/webauthn',
    name: 'webauthn',
    component: () => import('../views/WebAuthnView.vue')
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/recover-password',
    name: 'recover-password',
    component: () => import('../views/RecoverPasswordView.vue')
  }
]

const router = createRouter({
  history: createWebHistory('/test'),
  routes
})

// Guard de navegação para rotas protegidas
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router

