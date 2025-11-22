import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('auth_token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)

  function setAuth(authToken, userData) {
    token.value = authToken
    user.value = userData
    localStorage.setItem('auth_token', authToken)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user')
  }

  return {
    token,
    user,
    isAuthenticated,
    setAuth,
    logout
  }
})

