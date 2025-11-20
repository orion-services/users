<template>
  <v-row justify="center" align="center" style="min-height: 80vh">
    <v-col cols="12" sm="8" md="6" lg="4">
      <v-card>
        <v-card-title class="text-h5 text-center pa-4">
          Autenticação em Dois Fatores
        </v-card-title>

        <v-card-text>
          <!-- Modo: Configurar 2FA -->
          <div v-if="mode === 'setup'">
            <v-alert type="info" class="mb-4">
              Configure a autenticação em dois fatores escaneando o QR code com seu aplicativo autenticador.
            </v-alert>

            <v-form ref="setupForm" v-model="setupValid" @submit.prevent="generateQRCode">
              <v-text-field
                v-model="setupEmail"
                label="Email"
                type="email"
                :rules="emailRules"
                required
                prepend-inner-icon="mdi-email"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="setupPassword"
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
                :loading="qrLoading"
                size="large"
                class="mb-4"
              >
                Gerar QR Code
              </v-btn>
            </v-form>

            <!-- Exibir QR Code -->
            <div v-if="qrCodeUrl" class="text-center mt-4">
              <v-img
                :src="qrCodeUrl"
                max-width="300"
                class="mx-auto mb-4"
                contain
              ></v-img>
              <v-alert type="success" class="mb-4">
                Escaneie o QR code com seu aplicativo autenticador (Google Authenticator, Authy, etc.)
              </v-alert>
              <v-btn
                color="success"
                block
                @click="mode = 'validate'"
                size="large"
              >
                Já escaneei, validar código
              </v-btn>
            </div>
          </div>

          <!-- Modo: Validar código 2FA -->
          <div v-if="mode === 'validate'">
            <v-alert type="info" class="mb-4">
              Digite o código de 6 dígitos do seu aplicativo autenticador.
            </v-alert>

            <v-form ref="validateForm" v-model="validateValid" @submit.prevent="handleValidateCode">
              <v-text-field
                v-model="validateEmail"
                label="Email"
                type="email"
                :rules="emailRules"
                required
                prepend-inner-icon="mdi-email"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="validateCode"
                label="Código de 6 dígitos"
                :rules="codeRules"
                required
                prepend-inner-icon="mdi-shield-lock"
                variant="outlined"
                class="mb-4"
                maxlength="6"
                @input="validateCode = validateCode.replace(/[^0-9]/g, '')"
              ></v-text-field>

              <v-btn
                type="submit"
                color="primary"
                block
                :loading="validateLoading"
                size="large"
                class="mb-4"
              >
                Validar Código
              </v-btn>

              <v-btn
                color="secondary"
                block
                variant="outlined"
                @click="mode = 'setup'"
              >
                Configurar 2FA
              </v-btn>
            </v-form>
          </div>
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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { userApi } from '../services/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const mode = ref(route.query.mode || 'setup')
const setupForm = ref(null)
const validateForm = ref(null)
const setupValid = ref(false)
const validateValid = ref(false)

// Dados para configuração
const setupEmail = ref('')
const setupPassword = ref('')
const qrLoading = ref(false)
const qrCodeUrl = ref(null)

// Dados para validação
const validateEmail = ref('')
const validateCode = ref('')
const validateLoading = ref(false)

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

const codeRules = [
  v => !!v || 'Código é obrigatório',
  v => (v && v.length === 6) || 'Código deve ter 6 dígitos',
  v => /^\d{6}$/.test(v) || 'Código deve conter apenas números'
]

onMounted(() => {
  // Se veio do login, usar o email pendente
  const pendingEmail = localStorage.getItem('pending_email')
  if (pendingEmail) {
    validateEmail.value = pendingEmail
  }
})

const showMessage = (message, color = 'success') => {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

const generateQRCode = async () => {
  if (!setupValid.value) return

  qrLoading.value = true
  try {
    const response = await userApi.generate2FAQRCode(setupEmail.value, setupPassword.value)
    const blob = new Blob([response.data], { type: 'image/png' })
    qrCodeUrl.value = URL.createObjectURL(blob)
    validateEmail.value = setupEmail.value
    showMessage('QR Code gerado com sucesso!')
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Erro ao gerar QR code'
    showMessage(message, 'error')
  } finally {
    qrLoading.value = false
  }
}

const handleValidateCode = async () => {
  if (!validateValid.value) return

  validateLoading.value = true
  try {
    const response = await userApi.loginWith2FA(validateEmail.value, validateCode.value)
    const data = response.data

    if (data.token && data.user) {
      authStore.setAuth(data.token, data.user)
      localStorage.removeItem('pending_email')
      showMessage('Autenticação realizada com sucesso!')
      router.push('/dashboard')
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Código inválido'
    showMessage(message, 'error')
  } finally {
    validateLoading.value = false
  }
}
</script>

