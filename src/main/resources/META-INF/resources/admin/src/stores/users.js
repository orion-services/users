import { defineStore } from 'pinia'
import { userApi } from '../services/api'

export const useUsersStore = defineStore('users', {
  state: () => ({
    users: [],
    currentUser: null,
    loading: false,
    error: null,
    searchQuery: '',
    filterEmailValid: null, // null = all, true = valid, false = invalid
    filter2FA: null // null = all, true = enabled, false = disabled
  }),

  getters: {
    filteredUsers: (state) => {
      let filtered = [...state.users]

      // Filter by search query (name or email)
      if (state.searchQuery) {
        const query = state.searchQuery.toLowerCase()
        filtered = filtered.filter(user => 
          (user.name && user.name.toLowerCase().includes(query)) ||
          (user.email && user.email.toLowerCase().includes(query))
        )
      }

      // Filter by email validation status
      if (state.filterEmailValid !== null) {
        filtered = filtered.filter(user => user.emailValid === state.filterEmailValid)
      }

      // Filter by 2FA status
      if (state.filter2FA !== null) {
        filtered = filtered.filter(user => user.using2FA === state.filter2FA)
      }

      return filtered
    },

    isAuthenticated: () => {
      const token = localStorage.getItem('auth_token')
      const user = localStorage.getItem('user')
      return !!(token && user)
    },

    currentAdminUser: () => {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        try {
          return JSON.parse(userStr)
        } catch (e) {
          return null
        }
      }
      return null
    }
  },

  actions: {
    async fetchUsers() {
      this.loading = true
      this.error = null
      try {
        const response = await userApi.listUsers()
        this.users = response.data || []
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erro ao carregar usuários'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchUserByEmail(email) {
      this.loading = true
      this.error = null
      try {
        const response = await userApi.getUserByEmail(email)
        this.currentUser = response.data
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erro ao carregar usuário'
        throw error
      } finally {
        this.loading = false
      }
    },

    async createUser(userData) {
      this.loading = true
      this.error = null
      try {
        const response = await userApi.createUser(
          userData.name,
          userData.email,
          userData.password
        )
        // Refresh users list
        await this.fetchUsers()
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erro ao criar usuário'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateUser(userData) {
      this.loading = true
      this.error = null
      try {
        // Normaliza campos: converte strings vazias para null/undefined
        const name = userData.name && userData.name.trim() !== '' ? userData.name.trim() : null
        const newEmail = userData.newEmail && userData.newEmail.trim() !== '' ? userData.newEmail.trim() : null
        const password = userData.password && userData.password !== '' ? userData.password : null
        const newPassword = userData.newPassword && userData.newPassword !== '' ? userData.newPassword : null
        
        const response = await userApi.updateUser(
          userData.email,
          name,
          newEmail,
          password,
          newPassword
        )
        // Refresh users list
        await this.fetchUsers()
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erro ao atualizar usuário'
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteUser(email) {
      this.loading = true
      this.error = null
      try {
        await userApi.deleteUser(email)
        // Refresh users list
        await this.fetchUsers()
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erro ao deletar usuário'
        throw error
      } finally {
        this.loading = false
      }
    },

    async login(email, password) {
      this.loading = true
      this.error = null
      try {
        const response = await userApi.login(email, password)
        
        if (response.data?.authentication) {
          const authData = response.data.authentication
          localStorage.setItem('auth_token', authData.token)
          localStorage.setItem('user', JSON.stringify(authData.user))
          return authData
        } else if (response.data?.requires2FA) {
          // Handle 2FA requirement
          throw new Error('2FA é necessário para este usuário')
        } else {
          throw new Error('Resposta de autenticação inválida')
        }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Erro ao fazer login'
        throw error
      } finally {
        this.loading = false
      }
    },

    logout() {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('user')
      this.currentUser = null
      this.users = []
    },

    setSearchQuery(query) {
      this.searchQuery = query
    },

    setFilterEmailValid(value) {
      this.filterEmailValid = value
    },

    setFilter2FA(value) {
      this.filter2FA = value
    },

    clearFilters() {
      this.searchQuery = ''
      this.filterEmailValid = null
      this.filter2FA = null
    }
  }
})

