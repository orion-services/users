<template>
  <div v-if="logs.length === 0" class="text-center pa-8 text-grey">
    Nenhum log disponível
  </div>

  <v-expansion-panels v-else multiple>
    <v-expansion-panel
      v-for="log in logs"
      :key="log.id"
      :class="getLogClass(log)"
    >
      <v-expansion-panel-title>
        <div class="d-flex align-center" style="width: 100%">
          <v-chip
            :color="getStatusColor(log)"
            size="small"
            class="mr-3"
          >
            {{ getStatusText(log) }}
          </v-chip>
          <span class="text-truncate flex-grow-1">
            {{ log.request.method }} {{ log.request.url }}
          </span>
          <span class="text-caption text-grey ml-2">
            {{ formatTime(log.timestamp) }}
          </span>
        </div>
      </v-expansion-panel-title>

      <v-expansion-panel-text>
        <v-tabs v-model="logTabs[log.id]">
          <v-tab value="request">Requisição</v-tab>
          <v-tab value="response" v-if="log.response">Resposta</v-tab>
          <v-tab value="error" v-if="log.error">Erro</v-tab>
        </v-tabs>

        <v-tabs-window v-model="logTabs[log.id]" class="mt-4">
          <!-- Aba Requisição -->
          <v-tabs-window-item value="request">
            <v-card variant="outlined">
              <v-card-text>
                <div class="mb-2">
                  <strong>Método:</strong> {{ log.request.method }}
                </div>
                <div class="mb-2">
                  <strong>URL:</strong> {{ log.request.baseURL }}{{ log.request.url }}
                </div>
                <div v-if="log.request.data" class="mb-2">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <strong>Dados:</strong>
                    <v-btn
                      icon="mdi-content-copy"
                      size="x-small"
                      variant="text"
                      @click="copyToClipboard(formatData(log.request.data))"
                    >
                    </v-btn>
                  </div>
                  <pre class="mt-2 pa-2 bg-grey-lighten-4 rounded">{{ formatData(log.request.data) }}</pre>
                </div>
                <div v-if="log.request.headers">
                  <strong>Headers:</strong>
                  <pre class="mt-2 pa-2 bg-grey-lighten-4 rounded">{{ formatData(log.request.headers) }}</pre>
                </div>
              </v-card-text>
            </v-card>
          </v-tabs-window-item>

          <!-- Aba Resposta -->
          <v-tabs-window-item value="response" v-if="log.response">
            <v-card variant="outlined">
              <v-card-text>
                <div class="mb-2">
                  <strong>Status:</strong> 
                  <v-chip :color="log.response.status >= 400 ? 'error' : 'success'" size="small" class="ml-2">
                    {{ log.response.status }} {{ log.response.statusText }}
                  </v-chip>
                </div>
                <div v-if="log.response.data">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <strong>Dados:</strong>
                    <v-btn
                      icon="mdi-content-copy"
                      size="x-small"
                      variant="text"
                      @click="copyToClipboard(formatData(log.response.data))"
                    >
                    </v-btn>
                  </div>
                  <pre class="mt-2 pa-2 bg-grey-lighten-4 rounded">{{ formatData(log.response.data) }}</pre>
                </div>
                <div v-if="log.response.headers">
                  <strong>Headers:</strong>
                  <pre class="mt-2 pa-2 bg-grey-lighten-4 rounded">{{ formatData(log.response.headers) }}</pre>
                </div>
              </v-card-text>
            </v-card>
          </v-tabs-window-item>

          <!-- Aba Erro -->
          <v-tabs-window-item value="error" v-if="log.error">
            <v-card variant="outlined" color="error">
              <v-card-text>
                <div class="mb-2">
                  <strong>Mensagem:</strong> {{ log.error.message }}
                </div>
                <div v-if="log.error.response">
                  <strong>Status:</strong> 
                  <v-chip color="error" size="small" class="ml-2">
                    {{ log.error.response.status }} {{ log.error.response.statusText }}
                  </v-chip>
                </div>
                <div v-if="log.error.response?.data">
                  <div class="d-flex justify-space-between align-center mb-1">
                    <strong>Dados do Erro:</strong>
                    <v-btn
                      icon="mdi-content-copy"
                      size="x-small"
                      variant="text"
                      @click="copyToClipboard(formatData(log.error.response.data))"
                    >
                    </v-btn>
                  </div>
                  <pre class="mt-2 pa-2 bg-grey-lighten-4 rounded">{{ formatData(log.error.response.data) }}</pre>
                </div>
              </v-card-text>
            </v-card>
          </v-tabs-window-item>
        </v-tabs-window>
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  logs: {
    type: Array,
    required: true
  }
})

const logTabs = ref({})

const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text)
    // Você pode adicionar uma notificação aqui se quiser
  } catch (err) {
    console.error('Erro ao copiar:', err)
  }
}

onMounted(() => {
  // Inicializar tabs para cada log
  props.logs.forEach(log => {
    if (!logTabs.value[log.id]) {
      logTabs.value[log.id] = 'request'
    }
  })
})

const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('pt-BR')
}

const formatData = (data) => {
  if (data === null || data === undefined) {
    return 'null'
  }
  
  if (typeof data === 'string') {
    // Tentar fazer parse de JSON
    try {
      const parsed = JSON.parse(data)
      return JSON.stringify(parsed, null, 2)
    } catch {
      // Se não for JSON, retornar como está
      return data
    }
  }
  
  // Se for URLSearchParams ou FormData, converter para objeto
  if (data instanceof URLSearchParams) {
    const obj = {}
    data.forEach((value, key) => {
      obj[key] = value
    })
    return JSON.stringify(obj, null, 2)
  }
  
  if (data instanceof FormData) {
    const obj = {}
    data.forEach((value, key) => {
      obj[key] = value
    })
    return JSON.stringify(obj, null, 2)
  }
  
  // Tentar serializar como JSON
  try {
    return JSON.stringify(data, null, 2)
  } catch (e) {
    return String(data)
  }
}

const getStatusColor = (log) => {
  if (log.error) return 'error'
  if (log.response?.status >= 400) return 'error'
  if (log.response?.status >= 200 && log.response?.status < 300) return 'success'
  return 'info'
}

const getStatusText = (log) => {
  if (log.error) {
    return log.error.response?.status || 'Erro'
  }
  if (log.response) {
    return `${log.response.status} ${log.response.statusText}`
  }
  return 'Pendente'
}

const getLogClass = (log) => {
  if (log.error) return 'border-error'
  if (log.response?.status >= 400) return 'border-error'
  return ''
}
</script>

<style scoped>
.border-error {
  border-left: 4px solid rgb(var(--v-theme-error)) !important;
}
</style>

