import axios from 'axios'
import { useDebugStore } from '../stores/debug'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
})

// Interceptor para adicionar token JWT nas requisições e logar requisições
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // Log da requisição
    try {
      const debugStore = useDebugStore()
      debugStore.addLog(config, null, null)
    } catch (e) {
      // Store pode não estar inicializado ainda, ignorar erro
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

// Interceptor para tratar erros de resposta e logar respostas
api.interceptors.response.use(
  (response) => {
    // Log da resposta bem-sucedida
    try {
      const debugStore = useDebugStore()
      // Criar uma cópia da resposta para log (sem o blob se for blob)
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
    // Log do erro
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

// Função auxiliar para converter objeto em FormData
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
  // Cadastro
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
  startWebAuthnRegistration: (email) => {
    return api.post('/users/webauthn/register/start', toFormData({ email }))
  },

  finishWebAuthnRegistration: (email, response, deviceName) => {
    return api.post('/users/webauthn/register/finish', toFormData({ 
      email, 
      response, 
      deviceName: deviceName || null 
    }))
  },

  startWebAuthnAuthentication: (email) => {
    return api.post('/users/webauthn/authenticate/start', toFormData({ email }))
  },

  finishWebAuthnAuthentication: (email, response) => {
    return api.post('/users/webauthn/authenticate/finish', toFormData({ email, response }))
  },

  // Validação de Email
  validateEmail: (email, code) => {
    return api.get('/users/validateEmail', {
      params: { email, code }
    })
  }
}

export default api

