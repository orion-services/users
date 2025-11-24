<template>
  <v-row justify="center" align="center" style="min-height: 80vh">
    <v-col cols="12" sm="8" md="6" lg="4">
      <v-card>
        <v-card-title class="text-h5 pa-4 text-center">
          <v-icon class="mr-2">mdi-shield-account</v-icon>
          Orion Users Console
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
              label="E-mail"
              type="email"
              :rules="emailRules"
              required
              prepend-inner-icon="mdi-email"
              variant="outlined"
              class="mb-4"
            ></v-text-field>

            <v-text-field
              v-model="password"
              label="Senha"
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
              Entrar
            </v-btn>
          </v-form>

          <v-alert type="info" class="mt-4">
            <strong>Atenção:</strong> Apenas usuários com role "admin" podem acessar esta área.
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
  v => !!v || 'E-mail é obrigatório',
  v => /.+@.+\..+/.test(v) || 'E-mail deve ser válido'
]

const passwordRules = [
  v => !!v || 'Senha é obrigatória',
  v => (v && v.length >= 8) || 'Senha deve ter pelo menos 8 caracteres'
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
      errorMessage.value = 'Erro ao fazer login. Token não recebido.'
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
        errorMessage.value = 'Acesso negado. Apenas administradores podem acessar esta área.'
        loading.value = false
        return
      }
      
      // Login bem-sucedido e usuário é admin - redireciona para /console
      // Força o redirecionamento usando window.location para garantir que funcione
      // mesmo se o router guard estiver bloqueando
      const redirectPath = route.query.redirect 
        ? decodeURIComponent(route.query.redirect) 
        : '/console'
      
      // Usa window.location para garantir o redirecionamento
      window.location.href = redirectPath.startsWith('/console') 
        ? redirectPath 
        : `/console${redirectPath}`
    } catch (e) {
      // Erro ao decodificar token
      console.error('Erro ao decodificar token:', e)
      usersStore.logout()
      errorMessage.value = 'Token inválido. Tente fazer login novamente.'
      loading.value = false
    }
  } catch (error) {
    // Erro no login (credenciais inválidas, etc)
    errorMessage.value = error.response?.data?.message || error.message || 'Erro ao fazer login. Verifique suas credenciais.'
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

