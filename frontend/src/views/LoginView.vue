<template>
  <v-row justify="center" align="center" style="min-height: 80vh">
    <v-col cols="12" sm="8" md="6" lg="4">
      <v-card>
        <v-tabs v-model="tab" bg-color="primary">
          <v-tab value="login">Login</v-tab>
          <v-tab value="register">Cadastro</v-tab>
        </v-tabs>

        <v-card-text>
          <v-tabs-window v-model="tab">
            <!-- Aba de Login -->
            <v-tabs-window-item value="login">
              <v-form ref="loginForm" v-model="loginValid" @submit.prevent="handleLogin">
                <v-text-field
                  v-model="loginEmail"
                  label="Email"
                  type="email"
                  :rules="emailRules"
                  required
                  prepend-inner-icon="mdi-email"
                  variant="outlined"
                  class="mb-4"
                ></v-text-field>

                <v-text-field
                  v-model="loginPassword"
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
                  :loading="loginLoading"
                  size="large"
                  class="mb-4"
                >
                  Entrar
                </v-btn>

                <v-btn
                  color="secondary"
                  block
                  variant="outlined"
                  @click="$router.push('/webauthn')"
                  class="mb-2"
                >
                  Login com WebAuthn
                </v-btn>
              </v-form>
            </v-tabs-window-item>

            <!-- Aba de Cadastro -->
            <v-tabs-window-item value="register">
              <v-form ref="registerForm" v-model="registerValid" @submit.prevent="handleRegister">
                <v-text-field
                  v-model="registerName"
                  label="Nome"
                  :rules="nameRules"
                  required
                  prepend-inner-icon="mdi-account"
                  variant="outlined"
                  class="mb-4"
                ></v-text-field>

                <v-text-field
                  v-model="registerEmail"
                  label="Email"
                  type="email"
                  :rules="emailRules"
                  required
                  prepend-inner-icon="mdi-email"
                  variant="outlined"
                  class="mb-4"
                ></v-text-field>

                <v-text-field
                  v-model="registerPassword"
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
                  :loading="registerLoading"
                  size="large"
                >
                  Cadastrar
                </v-btn>
              </v-form>
            </v-tabs-window-item>
          </v-tabs-window>
        </v-card-text>

        <!-- Snackbar para mensagens -->
        <v-snackbar
          v-model="snackbar.show"
          :color="snackbar.color"
          :timeout="5000"
          top
        >
          {{ snackbar.message }}
        </v-snackbar>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { userApi } from '../services/api'

const router = useRouter()
const authStore = useAuthStore()

const tab = ref('login')
const loginForm = ref(null)
const registerForm = ref(null)
const loginValid = ref(false)
const registerValid = ref(false)

// Dados do formulário de login
const loginEmail = ref('')
const loginPassword = ref('')
const loginLoading = ref(false)

// Dados do formulário de cadastro
const registerName = ref('')
const registerEmail = ref('')
const registerPassword = ref('')
const registerLoading = ref(false)

// Snackbar
const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

// Regras de validação
const emailRules = [
  v => !!v || 'Email é obrigatório',
  v => /.+@.+\..+/.test(v) || 'Email deve ser válido'
]

const passwordRules = [
  v => !!v || 'Senha é obrigatória',
  v => (v && v.length >= 8) || 'Senha deve ter no mínimo 8 caracteres'
]

const nameRules = [
  v => !!v || 'Nome é obrigatório'
]

const showMessage = (message, color = 'success') => {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

const handleLogin = async () => {
  if (!loginValid.value) return

  loginLoading.value = true
  try {
    const response = await userApi.login(loginEmail.value, loginPassword.value)
    const data = response.data

    // Quando requer 2FA, o backend retorna LoginResponseDTO completo
    if (data.requires2FA === true) {
      // Usuário precisa de 2FA
      localStorage.setItem('pending_email', loginEmail.value)
      router.push('/2fa?mode=validate')
    } else if (data.token && data.user) {
      // Login bem-sucedido sem 2FA - backend retorna AuthenticationDTO diretamente
      authStore.setAuth(data.token, data.user)
      showMessage('Login realizado com sucesso!')
      router.push('/dashboard')
    } else if (data.authentication) {
      // Fallback: caso o backend retorne LoginResponseDTO sem requires2FA
      authStore.setAuth(data.authentication.token, data.authentication.user)
      showMessage('Login realizado com sucesso!')
      router.push('/dashboard')
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Erro ao fazer login'
    showMessage(message, 'error')
  } finally {
    loginLoading.value = false
  }
}

const handleRegister = async () => {
  if (!registerValid.value) return

  registerLoading.value = true
  try {
    const response = await userApi.createAndAuthenticate(
      registerName.value,
      registerEmail.value,
      registerPassword.value
    )
    const data = response.data

    if (data.token && data.user) {
      authStore.setAuth(data.token, data.user)
      showMessage('Cadastro realizado com sucesso!')
      router.push('/dashboard')
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Erro ao cadastrar usuário'
    showMessage(message, 'error')
  } finally {
    registerLoading.value = false
  }
}
</script>

