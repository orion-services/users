<template>
  <v-app>
    <v-app-bar
      v-if="isAuthenticated"
      color="primary"
      prominent
      elevation="2"
    >
      <v-app-bar-nav-icon
        variant="text"
        @click.stop="drawer = !drawer"
      ></v-app-bar-nav-icon>

      <v-toolbar-title>
        <router-link to="/users" style="text-decoration: none; color: inherit;">
          Orion Users
        </router-link>
      </v-toolbar-title>

      <v-spacer></v-spacer>

      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn
            icon="mdi-account-circle"
            variant="text"
            v-bind="props"
          >
            <v-icon>mdi-account-circle</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item>
            <v-list-item-title>{{ currentUser?.email || 'Admin' }}</v-list-item-title>
            <v-list-item-subtitle>Administrador</v-list-item-subtitle>
          </v-list-item>
          <v-divider></v-divider>
          <v-list-item @click="logout">
            <v-list-item-title>
              <v-icon start>mdi-logout</v-icon>
              Sair
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>

    <v-navigation-drawer
      v-if="isAuthenticated"
      v-model="drawer"
      temporary
    >
      <v-list>
        <v-list-item
          prepend-icon="mdi-view-list"
          title="Lista de Usuários"
          :to="{ name: 'UsersList' }"
        ></v-list-item>
        <v-list-item
          prepend-icon="mdi-account-plus"
          title="Criar Usuário"
          :to="{ name: 'CreateUser' }"
        ></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-main>
      <v-container fluid>
        <router-view />
      </v-container>
    </v-main>

    <!-- Global snackbar for notifications -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="5000"
      top
    >
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="snackbar.show = false"
        >
          Fechar
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUsersStore } from './stores/users'

const router = useRouter()
const usersStore = useUsersStore()

const drawer = ref(false)

const isAuthenticated = computed(() => usersStore.isAuthenticated)
const currentUser = computed(() => usersStore.currentAdminUser)

const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

const logout = () => {
  usersStore.logout()
  // Força o redirecionamento para /console após logout
  window.location.href = '/console'
}

// Listen for error messages from store
onMounted(() => {
  // This could be enhanced to listen to store errors
})
</script>

<style>
#app {
  font-family: 'Roboto', sans-serif;
}
</style>

