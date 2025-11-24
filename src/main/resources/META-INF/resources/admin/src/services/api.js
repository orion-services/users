import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
})

// Interceptor to add JWT token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Interceptor to handle response errors
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('user')
      // Redirect to login if not already there
      if (window.location.pathname !== '/console/login') {
        window.location.href = '/console/login'
      }
    }
    return Promise.reject(error)
  }
)

// Helper function to convert object to FormData
const toFormData = (data) => {
  const formData = new URLSearchParams()
  Object.keys(data).forEach(key => {
    const value = data[key]
    // Não inclui null, undefined ou strings vazias
    if (value !== null && value !== undefined && value !== '') {
      formData.append(key, value)
    }
  })
  return formData
}

export const userApi = {
  // List all users (admin only)
  listUsers: () => {
    return api.get('/users/list')
  },

  // Get user by email (admin only)
  getUserByEmail: (email) => {
    return api.get('/users/by-email', {
      params: { email }
    })
  },

  // Create user
  createUser: (name, email, password) => {
    return api.post('/users/create', toFormData({ name, email, password }))
  },

  // Update user
  updateUser: (email, name, newEmail, password, newPassword) => {
    const data = { email }
    // Só inclui campos que têm valores (não null, undefined ou string vazia)
    if (name && name.trim() !== '') {
      data.name = name.trim()
    }
    if (newEmail && newEmail.trim() !== '') {
      data.newEmail = newEmail.trim()
    }
    if (password && password !== '') {
      data.password = password
    }
    if (newPassword && newPassword !== '') {
      data.newPassword = newPassword
    }
    
    return api.put('/users/update', toFormData(data), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },

  // Delete user (admin only)
  deleteUser: (email) => {
    return api.post('/users/delete', toFormData({ email }))
  },

  // Login
  login: (email, password) => {
    return api.post('/users/login', toFormData({ email, password }))
  }
}

export default api

