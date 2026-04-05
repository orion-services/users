<template>
  <v-row justify="center" align="center" style="min-height: 80vh">
    <v-col cols="12" sm="8" md="6" lg="4">
      <v-card>
        <v-card-title class="text-h5 pa-4 text-center">
          <v-icon class="mr-2">mdi-shield-account</v-icon>
          Orion Users
        </v-card-title>

        <v-divider></v-divider>

        <v-card-text class="pa-6">
          <v-alert
            v-if="errorMessage"
            type="error"
            class="mb-4"
            closable
            @click:close="errorMessage = ''"
          >
            {{ errorMessage }}
          </v-alert>

          <v-form ref="loginForm" v-model="valid" @submit.prevent="handleLogin">
            <v-text-field
              v-model="email"
              label="Email"
              type="email"
              :rules="emailRules"
              required
              prepend-inner-icon="mdi-email"
              variant="outlined"
              class="mb-4"
            ></v-text-field>

            <v-text-field
              v-model="password"
              label="Password"
              type="password"
              :rules="passwordRules"
              required
              prepend-inner-icon="mdi-lock"
              variant="outlined"
              class="mb-4"
            ></v-text-field>

            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              :loading="loading"
              :disabled="!valid"
            >
              Sign in
            </v-btn>
          </v-form>

          <v-alert type="info" class="mt-4">
            <strong>Notice:</strong> Only users with the &quot;admin&quot; role can access this area.
          </v-alert>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUsersStore } from '../stores/users'

const router = useRouter()
const route = useRoute()
const usersStore = useUsersStore()

const loginForm = ref(null)
const valid = ref(false)
const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

const emailRules = [
  v => !!v || 'Email is required',
  v => /.+@.+\..+/.test(v) || 'Email must be valid'
]

const passwordRules = [
  v => !!v || 'Password is required',
  v => (v && v.length >= 8) || 'Password must be at least 8 characters'
]

const handleLogin = async () => {
  if (!valid.value) return

  loading.value = true
  errorMessage.value = ''

  try {
    // Faz o login - o store já salva o token no localStorage
    const authData = await usersStore.login(email.value, password.value)
    
    // Verifica se recebemos o token com sucesso
    if (!authData || !authData.token) {
      errorMessage.value = 'Sign-in failed. No token received.'
      loading.value = false
      return
    }

    // Verifica se o usuário tem role admin no token
    try {
      const payload = JSON.parse(atob(authData.token.split('.')[1]))
      const groups = payload.groups || []
      
      if (!groups.includes('admin')) {
        // Usuário não é admin, faz logout e mostra erro
        usersStore.logout()
        errorMessage.value = 'Access denied. Only administrators can access this area.'
        loading.value = false
        return
      }
      
      const redirectPath = route.query.redirect 
        ? decodeURIComponent(route.query.redirect) 
        : '/dashboard'
      
      window.location.href = redirectPath.startsWith('/dashboard') 
        ? redirectPath 
        : `/dashboard${redirectPath}`
    } catch (e) {
      // Erro ao decodificar token
      console.error('Failed to decode token:', e)
      usersStore.logout()
      errorMessage.value = 'Invalid token. Please sign in again.'
      loading.value = false
    }
  } catch (error) {
    // Erro no login (credenciais inválidas, etc)
    errorMessage.value = error.response?.data?.message || error.message || 'Sign-in failed. Check your credentials.'
    loading.value = false
  }
}

onMounted(() => {
  // Check for error message in query params
  if (route.query.error) {
    errorMessage.value = route.query.error
  }

  // If already authenticated, redirect to users list
  if (usersStore.isAuthenticated) {
    router.push({ name: 'UsersList' })
  }
})
</script>

