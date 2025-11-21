import axios from 'axios'
import { useDebugStore } from '../stores/debug'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
})

// Interceptor to add JWT token to requests and log requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // Log the request
    try {
      const debugStore = useDebugStore()
      debugStore.addLog(config, null, null)
    } catch (e) {
      // Store may not be initialized yet, ignore error
      console.warn('Debug store not available:', e)
    }
    
    return config
  },
  (error) => {
    try {
      const debugStore = useDebugStore()
      debugStore.addLog(error.config, null, error)
    } catch (e) {
      console.warn('Debug store not available:', e)
    }
    return Promise.reject(error)
  }
)

// Interceptor to handle response errors and log responses
api.interceptors.response.use(
  (response) => {
    // Log successful response
    try {
      const debugStore = useDebugStore()
      // Create a copy of the response for logging (without blob if it's a blob)
      const logResponse = {
        ...response,
        data: response.config.responseType === 'blob' 
          ? '[Blob - ' + response.data.size + ' bytes]' 
          : response.data
      }
      debugStore.addLog(response.config, logResponse, null)
    } catch (e) {
      console.warn('Debug store not available:', e)
    }
    return response
  },
  (error) => {
    // Log the error
    try {
      const debugStore = useDebugStore()
      debugStore.addLog(error.config || {}, null, error)
    } catch (e) {
      console.warn('Debug store not available:', e)
    }
    
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('user')
    }
    return Promise.reject(error)
  }
)

// Helper function to convert object to FormData
const toFormData = (data) => {
  const formData = new URLSearchParams()
  Object.keys(data).forEach(key => {
    if (data[key] !== null && data[key] !== undefined) {
      formData.append(key, data[key])
    }
  })
  return formData
}

export const userApi = {
  // Registration
  createUser: (name, email, password) => {
    return api.post('/users/create', toFormData({ name, email, password }))
  },

  createAndAuthenticate: (name, email, password) => {
    return api.post('/users/createAuthenticate', toFormData({ name, email, password }))
  },

  // Login
  login: (email, password) => {
    return api.post('/users/login', toFormData({ email, password }))
  },

  // 2FA
  generate2FAQRCode: (email, password) => {
    return api.post('/users/google/2FAuth/qrCode', toFormData({ email, password }), {
      responseType: 'blob'
    })
  },

  validate2FACode: (email, code) => {
    return api.post('/users/google/2FAuth/validate', toFormData({ email, code }))
  },

  loginWith2FA: (email, code) => {
    return api.post('/users/login/2fa', toFormData({ email, code }))
  },

  // WebAuthn
  startWebAuthnRegistration: (email, origin) => {
    return api.post('/users/webauthn/register/start', toFormData({ 
      email,
      origin: origin || null
    }))
  },

  finishWebAuthnRegistration: (email, response, origin, deviceName) => {
    return api.post('/users/webauthn/register/finish', toFormData({ 
      email, 
      response, 
      origin,
      deviceName: deviceName || null 
    }))
  },

  startWebAuthnAuthentication: (email) => {
    return api.post('/users/webauthn/authenticate/start', toFormData({ email }))
  },

  finishWebAuthnAuthentication: (email, response) => {
    return api.post('/users/webauthn/authenticate/finish', toFormData({ email, response }))
  },

  // Email Validation
  validateEmail: (email, code) => {
    return api.get('/users/validateEmail', {
      params: { email, code }
    })
  },

  // Password Recovery
  recoverPassword: (email) => {
    return api.post('/users/recoverPassword', toFormData({ email }))
  },

  // User Update (email and/or password)
  updateUser: (email, newEmail, password, newPassword) => {
    const data = { email }
    if (newEmail) data.newEmail = newEmail
    if (password) data.password = password
    if (newPassword) data.newPassword = newPassword
    
    return api.put('/users/update', toFormData(data), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },

  // Social Auth
  loginWithGoogle: (idToken) => {
    return api.post('/users/login/google', toFormData({ idToken }))
  },

  loginWithApple: (idToken) => {
    return api.post('/users/login/apple', toFormData({ idToken }))
  }
}

export default api

