import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDebugStore = defineStore('debug', () => {
  const logs = ref([])
  const showModal = ref(false)

  function addLog(request, response, error = null) {
    const log = {
      id: Date.now(),
      timestamp: new Date().toISOString(),
      request: {
        method: request?.method,
        url: request?.url,
        baseURL: request?.baseURL,
        data: request?.data,
        headers: request?.headers
      },
      response: response ? {
        status: response?.status,
        statusText: response?.statusText,
        data: response?.data,
        headers: response?.headers
      } : null,
      error: error ? {
        message: error?.message,
        response: error?.response ? {
          status: error.response.status,
          statusText: error.response.statusText,
          data: error.response.data,
          headers: error.response.headers
        } : null
      } : null
    }
    
    logs.value.unshift(log)
    // Manter apenas os últimos 50 logs
    if (logs.value.length > 50) {
      logs.value = logs.value.slice(0, 50)
    }
  }

  function clearLogs() {
    logs.value = []
  }

  function toggleModal() {
    showModal.value = !showModal.value
  }

  return {
    logs,
    showModal,
    addLog,
    clearLogs,
    toggleModal
  }
})

