<template>
  <v-row justify="center">
    <v-col cols="12" md="8" lg="6">
      <v-card>
        <v-card-title class="text-h5 pa-4">
          <v-icon class="mr-2">mdi-account</v-icon>
          Dashboard
        </v-card-title>

        <v-divider></v-divider>

        <v-card-text class="pa-6">
          <!-- Informações do Usuário -->
          <v-card variant="outlined" class="mb-4">
            <v-card-title class="text-subtitle-1">Informações do Usuário</v-card-title>
            <v-card-text>
              <div class="mb-2">
                <strong>Nome:</strong> {{ authStore.user?.name || 'N/A' }}
              </div>
              <div class="mb-2">
                <strong>Email:</strong> {{ authStore.user?.email || 'N/A' }}
              </div>
              <div class="mb-2">
                <strong>Email Validado:</strong>
                <v-chip
                  :color="authStore.user?.emailValid ? 'success' : 'warning'"
                  size="small"
                  class="ml-2"
                >
                  {{ authStore.user?.emailValid ? 'Sim' : 'Não' }}
                </v-chip>
              </div>
              <div>
                <strong>2FA Habilitado:</strong>
                <v-chip
                  :color="authStore.user?.using2FA ? 'success' : 'default'"
                  size="small"
                  class="ml-2"
                >
                  {{ authStore.user?.using2FA ? 'Sim' : 'Não' }}
                </v-chip>
              </div>
            </v-card-text>
          </v-card>

          <!-- Configuração de 2FA -->
          <v-card variant="outlined" class="mb-4">
            <v-card-title class="text-subtitle-1">
              <v-icon class="mr-2">mdi-shield-lock</v-icon>
              Autenticação em Dois Fatores (2FA)
            </v-card-title>
            <v-card-text>
              <div v-if="!authStore.user?.using2FA">
                <v-alert type="info" class="mb-4">
                  Configure a autenticação em dois fatores para aumentar a segurança da sua conta.
                </v-alert>

                <v-form ref="setup2FAForm" v-model="setup2FAValid" @submit.prevent="setup2FA">
                  <v-text-field
                    v-model="setup2FAPassword"
                    label="Confirme sua senha"
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
                    :loading="setup2FALoading"
                    size="large"
                  >
                    Configurar 2FA
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
                  <v-alert type="warning" class="mb-4">
                    <strong>Importante:</strong> Após escanear o QR code, você precisará usar o código do aplicativo para fazer login nas próximas vezes.
                  </v-alert>
                  <v-btn
                    color="success"
                    block
                    @click="goToValidate2FA"
                    size="large"
                    class="mb-2"
                  >
                    Já escaneei, testar código
                  </v-btn>
                  <v-btn
                    color="secondary"
                    block
                    variant="outlined"
                    @click="qrCodeUrl = null"
                    size="large"
                  >
                    Cancelar
                  </v-btn>
                </div>
              </div>

              <div v-else>
                <v-alert type="success" class="mb-4">
                  <strong>2FA está habilitado!</strong> Você precisará fornecer um código do seu aplicativo autenticador sempre que fizer login.
                </v-alert>
                <v-btn
                  color="info"
                  block
                  variant="outlined"
                  @click="goToValidate2FA"
                  size="large"
                >
                  Testar Código 2FA
                </v-btn>
              </div>
            </v-card-text>
          </v-card>

          <!-- Ações Rápidas -->
          <v-card variant="outlined">
            <v-card-title class="text-subtitle-1">
              <v-icon class="mr-2">mdi-cog</v-icon>
              Ações Rápidas
            </v-card-title>
            <v-card-text>
              <v-btn
                color="primary"
                block
                variant="outlined"
                @click="$router.push('/webauthn')"
                class="mb-2"
              >
                <v-icon start>mdi-fingerprint</v-icon>
                Configurar WebAuthn
              </v-btn>
              <v-btn
                color="secondary"
                block
                variant="outlined"
                @click="logout"
              >
                <v-icon start>mdi-logout</v-icon>
                Sair
              </v-btn>
            </v-card-text>
          </v-card>
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

const setup2FAForm = ref(null)
const setup2FAValid = ref(false)
const setup2FAPassword = ref('')
const setup2FALoading = ref(false)
const qrCodeUrl = ref(null)

// Snackbar
const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

// Regras de validação
const passwordRules = [
  v => !!v || 'Senha é obrigatória',
  v => (v && v.length >= 8) || 'Senha deve ter no mínimo 8 caracteres'
]

const showMessage = (message, color = 'success') => {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

const setup2FA = async () => {
  if (!setup2FAValid.value) return

  setup2FALoading.value = true
  try {
    const response = await userApi.generate2FAQRCode(
      authStore.user.email,
      setup2FAPassword.value
    )
    const blob = new Blob([response.data], { type: 'image/png' })
    qrCodeUrl.value = URL.createObjectURL(blob)
    setup2FAPassword.value = ''
    
    // Atualizar o status do 2FA no store (o backend já habilita ao gerar o QR code)
    if (authStore.user) {
      authStore.user.using2FA = true
    }
    
    showMessage('QR Code gerado com sucesso! Escaneie com seu aplicativo autenticador.')
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Erro ao gerar QR code'
    showMessage(message, 'error')
  } finally {
    setup2FALoading.value = false
  }
}

const goToValidate2FA = () => {
  localStorage.setItem('pending_email', authStore.user.email)
  router.push('/2fa?mode=validate')
}

const logout = () => {
  authStore.logout()
  router.push('/')
}
</script>

