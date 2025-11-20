<template>
  <v-app>
    <v-app-bar color="primary" prominent>
      <v-app-bar-title>
        <router-link to="/" style="text-decoration: none; color: inherit;">
          Orion Users
        </router-link>
      </v-app-bar-title>
      <v-spacer></v-spacer>
      <v-btn
        icon="mdi-bug"
        variant="text"
        @click="debugStore.toggleModal()"
        class="mr-2"
      >
        <v-icon>mdi-bug</v-icon>
        <v-tooltip activator="parent">Debug</v-tooltip>
      </v-btn>
      <v-btn
        v-if="authStore.isAuthenticated"
        @click="$router.push('/dashboard')"
        variant="text"
        class="mr-2"
      >
        <v-icon start>mdi-account</v-icon>
        Dashboard
      </v-btn>
      <v-btn
        v-if="authStore.isAuthenticated"
        @click="logout"
        variant="text"
      >
        Sair
      </v-btn>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <router-view />
      </v-container>
    </v-main>

    <DebugModal />
  </v-app>
</template>

<script setup>
import { useAuthStore } from './stores/auth'
import { useDebugStore } from './stores/debug'
import { useRouter } from 'vue-router'
import DebugModal from './components/DebugModal.vue'

const authStore = useAuthStore()
const debugStore = useDebugStore()
const router = useRouter()

const logout = () => {
  authStore.logout()
  router.push('/')
}
</script>

