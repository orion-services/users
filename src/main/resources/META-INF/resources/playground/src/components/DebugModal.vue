<template>
  <v-dialog v-model="debugStore.showModal" max-width="90%" scrollable>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        <span>Debug - Request/Response Logs</span>
        <div>
          <v-btn
            icon="mdi-delete"
            variant="text"
            size="small"
            @click="debugStore.clearLogs()"
            class="mr-2"
          >
          </v-btn>
          <v-btn
            icon="mdi-close"
            variant="text"
            size="small"
            @click="debugStore.toggleModal()"
          >
          </v-btn>
        </div>
      </v-card-title>

      <v-divider></v-divider>

      <v-card-text style="max-height: 70vh">
        <v-tabs v-model="tab" class="mb-4">
          <v-tab value="all">All</v-tab>
          <v-tab value="success">Success</v-tab>
          <v-tab value="error">Errors</v-tab>
        </v-tabs>

        <v-tabs-window v-model="tab">
          <v-tabs-window-item value="all">
            <LogList :logs="completedLogs" />
          </v-tabs-window-item>
          <v-tabs-window-item value="success">
            <LogList :logs="successLogs" />
          </v-tabs-window-item>
          <v-tabs-window-item value="error">
            <LogList :logs="errorLogs" />
          </v-tabs-window-item>
        </v-tabs-window>
      </v-card-text>

      <v-divider></v-divider>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="debugStore.toggleModal()">
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useDebugStore } from '../stores/debug'
import LogList from './LogList.vue'

const debugStore = useDebugStore()
const tab = ref('all')

// Filter only completed logs (with response or error), excluding pending ones
const completedLogs = computed(() => {
  return debugStore.logs.filter(log => log.response || log.error)
})

const successLogs = computed(() => {
  return debugStore.logs.filter(log => !log.error && log.response)
})

const errorLogs = computed(() => {
  return debugStore.logs.filter(log => log.error)
})
</script>

