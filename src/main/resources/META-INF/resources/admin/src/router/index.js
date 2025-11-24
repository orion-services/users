import { createRouter, createWebHistory } from 'vue-router'
import { useUsersStore } from '../stores/users'

const router = createRouter({
  history: createWebHistory('/console/'),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/users',
      name: 'UsersList',
      component: () => import('../views/UsersListView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/users/create',
      name: 'CreateUser',
      component: () => import('../views/CreateUserView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/users/:email',
      name: 'UserDetail',
      component: () => import('../views/UserDetailView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      props: true
    },
    {
      path: '/users/:email/edit',
      name: 'EditUser',
      component: () => import('../views/EditUserView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      props: true
    }
  ]
})

// Navigation guard to check authentication and admin role
router.beforeEach((to, from, next) => {
  const usersStore = useUsersStore()
  
  // Verifica autenticação diretamente do localStorage
  const token = localStorage.getItem('auth_token')
  const user = localStorage.getItem('user')
  const isAuthenticated = !!(token && user)

  // Se está na rota raiz (/), redireciona baseado na autenticação
  if (to.path === '/' || to.path === '') {
    if (isAuthenticated) {
      next({ name: 'UsersList' })
      return
    } else {
      next({ name: 'Login' })
      return
    }
  }

  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    if (!isAuthenticated) {
      // Redirect to login if not authenticated
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }

    // Check if route requires admin role
    if (to.meta.requiresAdmin) {
      // Check if user has admin role in JWT token
      const token = localStorage.getItem('auth_token')
      if (token) {
        try {
          // Decode JWT token (simple base64 decode, not full validation)
          const payload = JSON.parse(atob(token.split('.')[1]))
          const groups = payload.groups || []
          
          if (!groups.includes('admin')) {
            // User is not admin, redirect to login
            usersStore.logout()
            next({ name: 'Login', query: { error: 'Acesso negado. Apenas administradores podem acessar esta área.' } })
            return
          }
        } catch (e) {
          // Invalid token, logout and redirect to login
          usersStore.logout()
          next({ name: 'Login', query: { error: 'Token inválido.' } })
          return
        }
      } else {
        next({ name: 'Login', query: { redirect: to.fullPath } })
        return
      }
    }
  }

  // If already authenticated and trying to access login, redirect to users list
  // Mas só se realmente estiver autenticado (verifica novamente para evitar loops)
  if (to.name === 'Login') {
    const token = localStorage.getItem('auth_token')
    const user = localStorage.getItem('user')
    if (token && user) {
      // Usuário ainda está autenticado, redireciona para users list
      next({ name: 'UsersList' })
      return
    }
    // Usuário não está autenticado, permite acesso à tela de login
    next()
    return
  }

  next()
})

export default router

