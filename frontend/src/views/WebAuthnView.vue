<template>
  <v-row justify="center" align="center" style="min-height: 80vh">
    <v-col cols="12" sm="8" md="6" lg="4">
      <v-card>
        <v-card-title class="text-h5 text-center pa-4">
          WebAuthn
        </v-card-title>

        <v-card-text>
          <v-tabs v-model="tab" bg-color="primary" class="mb-4">
            <v-tab value="register">Registrar Dispositivo</v-tab>
            <v-tab value="authenticate">Autenticar</v-tab>
          </v-tabs>

          <v-tabs-window v-model="tab">
            <!-- Aba de Registro -->
            <v-tabs-window-item value="register">
              <v-alert type="info" class="mb-4">
                Registre seu dispositivo para usar autenticação biométrica ou chave de segurança.
              </v-alert>

              <v-form ref="registerForm" v-model="registerValid" @submit.prevent="handleRegister">
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
                  v-model="deviceName"
                  label="Nome do Dispositivo (opcional)"
                  prepend-inner-icon="mdi-devices"
                  variant="outlined"
                  class="mb-4"
                ></v-text-field>

                <v-btn
                  type="submit"
                  color="primary"
                  block
                  :loading="registerLoading"
                  size="large"
                  :disabled="!isWebAuthnSupported"
                >
                  Registrar Dispositivo
                </v-btn>

                <v-alert
                  v-if="!isWebAuthnSupported"
                  type="warning"
                  class="mt-4"
                >
                  Seu navegador não suporta WebAuthn. Use um navegador moderno como Chrome, Firefox ou Edge.
                </v-alert>
              </v-form>
            </v-tabs-window-item>

            <!-- Aba de Autenticação -->
            <v-tabs-window-item value="authenticate">
              <v-alert type="info" class="mb-4">
                Autentique-se usando seu dispositivo registrado (biometria ou chave de segurança).
              </v-alert>

              <v-form ref="authForm" v-model="authValid" @submit.prevent="handleAuthenticate">
                <v-text-field
                  v-model="authEmail"
                  label="Email"
                  type="email"
                  :rules="emailRules"
                  required
                  prepend-inner-icon="mdi-email"
                  variant="outlined"
                  class="mb-4"
                ></v-text-field>

                <v-btn
                  type="submit"
                  color="primary"
                  block
                  :loading="authLoading"
                  size="large"
                  :disabled="!isWebAuthnSupported"
                >
                  Autenticar com WebAuthn
                </v-btn>

                <v-alert
                  v-if="!isWebAuthnSupported"
                  type="warning"
                  class="mt-4"
                >
                  Seu navegador não suporta WebAuthn. Use um navegador moderno como Chrome, Firefox ou Edge.
                </v-alert>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { userApi } from '../services/api'

const router = useRouter()
const authStore = useAuthStore()

const tab = ref('register')
const registerForm = ref(null)
const authForm = ref(null)
const registerValid = ref(false)
const authValid = ref(false)

// Dados para registro
const registerEmail = ref('')
const deviceName = ref('')
const registerLoading = ref(false)

// Dados para autenticação
const authEmail = ref('')
const authLoading = ref(false)

// Snackbar
const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

// Verificar suporte a WebAuthn
const isWebAuthnSupported = computed(() => {
  return typeof window !== 'undefined' && 
         'PublicKeyCredential' in window &&
         typeof window.PublicKeyCredential !== 'undefined'
})

// Regras de validação
const emailRules = [
  v => !!v || 'Email é obrigatório',
  v => /.+@.+\..+/.test(v) || 'Email deve ser válido'
]

const showMessage = (message, color = 'success') => {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

// Função auxiliar para converter base64url para ArrayBuffer
const base64UrlToArrayBuffer = (base64url) => {
  const base64 = base64url.replace(/-/g, '+').replace(/_/g, '/')
  const binary = atob(base64)
  const bytes = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i)
  }
  return bytes.buffer
}

// Função auxiliar para converter ArrayBuffer para base64url
const arrayBufferToBase64Url = (buffer) => {
  const bytes = new Uint8Array(buffer)
  let binary = ''
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i])
  }
  return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '')
}

const handleRegister = async () => {
  if (!registerValid.value || !isWebAuthnSupported.value) return

  registerLoading.value = true
  try {
    // 1. Iniciar registro no servidor
    const startResponse = await userApi.startWebAuthnRegistration(registerEmail.value)
    // O backend retorna uma string JSON, então precisamos fazer parse
    const responseData = typeof startResponse.data === 'string' 
      ? JSON.parse(startResponse.data) 
      : startResponse.data
    const { options, challenge } = responseData

    // 2. Converter challenge de base64url para ArrayBuffer
    const challengeBuffer = base64UrlToArrayBuffer(challenge)

    // 3. Converter user.id de base64url para ArrayBuffer
    const userIdBuffer = base64UrlToArrayBuffer(options.user.id)

    // 4. Criar credencial no navegador
    const publicKeyCredentialCreationOptions = {
      challenge: challengeBuffer,
      rp: options.rp,
      user: {
        id: userIdBuffer,
        name: options.user.name,
        displayName: options.user.displayName
      },
      pubKeyCredParams: options.pubKeyCredParams,
      authenticatorSelection: options.authenticatorSelection,
      timeout: options.timeout,
      attestation: options.attestation
    }

    const credential = await navigator.credentials.create({
      publicKey: publicKeyCredentialCreationOptions
    })

    // 5. Converter resposta para JSON
    const response = {
      id: credential.id,
      rawId: arrayBufferToBase64Url(credential.rawId),
      type: credential.type,
      response: {
        attestationObject: arrayBufferToBase64Url(credential.response.attestationObject),
        clientDataJSON: arrayBufferToBase64Url(credential.response.clientDataJSON)
      }
    }

    // 6. Finalizar registro no servidor
    await userApi.finishWebAuthnRegistration(
      registerEmail.value,
      JSON.stringify(response),
      deviceName.value || 'Dispositivo WebAuthn'
    )

    showMessage('Dispositivo registrado com sucesso!')
    tab.value = 'authenticate'
    authEmail.value = registerEmail.value
  } catch (error) {
    let message = 'Erro ao registrar dispositivo'
    if (error.name === 'NotAllowedError') {
      message = 'Registro cancelado pelo usuário'
    } else if (error.name === 'InvalidStateError') {
      message = 'Este dispositivo já está registrado'
    } else if (error.response?.data?.message) {
      message = error.response.data.message
    } else if (error.message) {
      message = error.message
    }
    showMessage(message, 'error')
  } finally {
    registerLoading.value = false
  }
}

const handleAuthenticate = async () => {
  if (!authValid.value || !isWebAuthnSupported.value) return

  authLoading.value = true
  try {
    // 1. Iniciar autenticação no servidor
    const startResponse = await userApi.startWebAuthnAuthentication(authEmail.value)
    // O backend retorna uma string JSON, então precisamos fazer parse
    const responseData = typeof startResponse.data === 'string' 
      ? JSON.parse(startResponse.data) 
      : startResponse.data
    const { options, challenge } = responseData

    // 2. Converter challenge de base64url para ArrayBuffer
    const challengeBuffer = base64UrlToArrayBuffer(challenge)

    // 3. Converter allowCredentials IDs de base64url para ArrayBuffer
    const allowCredentials = options.allowCredentials.map(cred => ({
      ...cred,
      id: base64UrlToArrayBuffer(cred.id)
    }))

    // 4. Autenticar no navegador
    const publicKeyCredentialRequestOptions = {
      challenge: challengeBuffer,
      rpId: options.rpId,
      allowCredentials: allowCredentials,
      userVerification: options.userVerification,
      timeout: options.timeout
    }

    const assertion = await navigator.credentials.get({
      publicKey: publicKeyCredentialRequestOptions
    })

    // 5. Converter resposta para JSON
    const response = {
      id: assertion.id,
      rawId: arrayBufferToBase64Url(assertion.rawId),
      type: assertion.type,
      response: {
        authenticatorData: arrayBufferToBase64Url(assertion.response.authenticatorData),
        clientDataJSON: arrayBufferToBase64Url(assertion.response.clientDataJSON),
        signature: arrayBufferToBase64Url(assertion.response.signature),
        userHandle: assertion.response.userHandle ? 
          arrayBufferToBase64Url(assertion.response.userHandle) : null
      }
    }

    // 6. Finalizar autenticação no servidor
    const authResponse = await userApi.finishWebAuthnAuthentication(
      authEmail.value,
      JSON.stringify(response)
    )

    const data = authResponse.data
    if (data.token && data.user) {
      authStore.setAuth(data.token, data.user)
      showMessage('Autenticação realizada com sucesso!')
      router.push('/')
    }
  } catch (error) {
    let message = 'Erro ao autenticar'
    if (error.name === 'NotAllowedError') {
      message = 'Autenticação cancelada pelo usuário'
    } else if (error.response?.data?.message) {
      message = error.response.data.message
    } else if (error.message) {
      message = error.message
    }
    showMessage(message, 'error')
  } finally {
    authLoading.value = false
  }
}
</script>

