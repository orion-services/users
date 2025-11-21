<template>
  <v-row justify="center" align="center" style="min-height: 80vh">
    <v-col cols="12" sm="8" md="6" lg="4">
      <v-card>
        <v-card-title class="text-h5 text-center pa-4">
          WebAuthn
        </v-card-title>

        <v-card-text>
          <v-tabs v-model="tab" bg-color="primary" class="mb-4">
            <v-tab value="register">Register Device</v-tab>
            <v-tab value="authenticate">Authenticate</v-tab>
          </v-tabs>

          <v-tabs-window v-model="tab">
            <!-- Register Tab -->
            <v-tabs-window-item value="register">
              <v-alert type="info" class="mb-4">
                Register your device to use biometric authentication or security key.
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
                  label="Device Name (optional)"
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
                  Register Device
                </v-btn>

                <v-alert
                  v-if="!isWebAuthnSupported"
                  type="warning"
                  class="mt-4"
                >
                  Your browser does not support WebAuthn. Use a modern browser like Chrome, Firefox or Edge.
                </v-alert>
              </v-form>
            </v-tabs-window-item>

            <!-- Authenticate Tab -->
            <v-tabs-window-item value="authenticate">
              <v-alert type="info" class="mb-4">
                Authenticate using your registered device (biometrics or security key).
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
                  Authenticate with WebAuthn
                </v-btn>

                <v-alert
                  v-if="!isWebAuthnSupported"
                  type="warning"
                  class="mt-4"
                >
                  Your browser does not support WebAuthn. Use a modern browser like Chrome, Firefox or Edge.
                </v-alert>
              </v-form>
            </v-tabs-window-item>
          </v-tabs-window>
        </v-card-text>

        <!-- Snackbar for messages -->
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

// Data for registration
const registerEmail = ref('')
const deviceName = ref('')
const registerLoading = ref(false)

// Data for authentication
const authEmail = ref('')
const authLoading = ref(false)

// Snackbar
const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

// Check WebAuthn support
const isWebAuthnSupported = computed(() => {
  return typeof window !== 'undefined' && 
         'PublicKeyCredential' in window &&
         typeof window.PublicKeyCredential !== 'undefined'
})

// Validation rules
const emailRules = [
  v => !!v || 'Email is required',
  v => /.+@.+\..+/.test(v) || 'Email must be valid'
]

const showMessage = (message, color = 'success') => {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

// Helper function to convert base64url to ArrayBuffer
const base64UrlToArrayBuffer = (base64url) => {
  const base64 = base64url.replace(/-/g, '+').replace(/_/g, '/')
  const binary = atob(base64)
  const bytes = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i)
  }
  return bytes.buffer
}

// Helper function to convert ArrayBuffer to base64url
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
    // Capturar origin antes de iniciar para garantir consistência do rpId
    const origin = window.location.origin
    
    // 1. Start registration on server
    const startResponse = await userApi.startWebAuthnRegistration(registerEmail.value, origin)
    // Backend returns a JSON string, so we need to parse it
    const responseData = typeof startResponse.data === 'string' 
      ? JSON.parse(startResponse.data) 
      : startResponse.data
    const { options, challenge } = responseData

    // 2. Convert challenge from base64url to ArrayBuffer
    const challengeBuffer = base64UrlToArrayBuffer(challenge)

    // 3. Convert user.id from base64url to ArrayBuffer
    const userIdBuffer = base64UrlToArrayBuffer(options.user.id)

    // 4. Create credential in browser
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

    // 5. Convert response to JSON
    const response = {
      id: credential.id,
      rawId: arrayBufferToBase64Url(credential.rawId),
      type: credential.type,
      response: {
        attestationObject: arrayBufferToBase64Url(credential.response.attestationObject),
        clientDataJSON: arrayBufferToBase64Url(credential.response.clientDataJSON)
      }
    }

    // 6. Finish registration on server
    await userApi.finishWebAuthnRegistration(
      registerEmail.value,
      JSON.stringify(response),
      origin,
      deviceName.value || 'WebAuthn Device'
    )

    showMessage('Device registered successfully!')
    tab.value = 'authenticate'
    authEmail.value = registerEmail.value
  } catch (error) {
    let message = 'Error registering device'
    if (error.name === 'NotAllowedError') {
      message = 'Registration cancelled by user'
    } else if (error.name === 'InvalidStateError') {
      message = 'This device is already registered'
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
    // 1. Start authentication on server
    const startResponse = await userApi.startWebAuthnAuthentication(authEmail.value)
    // Backend returns a JSON string, so we need to parse it
    const responseData = typeof startResponse.data === 'string' 
      ? JSON.parse(startResponse.data) 
      : startResponse.data
    const { options, challenge } = responseData

    // 2. Convert challenge from base64url to ArrayBuffer
    const challengeBuffer = base64UrlToArrayBuffer(challenge)

    // 3. Convert allowCredentials IDs from base64url to ArrayBuffer
    const allowCredentials = options.allowCredentials.map(cred => ({
      ...cred,
      id: base64UrlToArrayBuffer(cred.id)
    }))

    // 4. Authenticate in browser
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

    // 5. Convert response to JSON
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

    // 6. Finish authentication on server
    const authResponse = await userApi.finishWebAuthnAuthentication(
      authEmail.value,
      JSON.stringify(response)
    )

    const data = authResponse.data
    if (data.token && data.user) {
      authStore.setAuth(data.token, data.user)
      showMessage('Authentication successful!')
      router.push('/')
    }
  } catch (error) {
    let message = 'Error authenticating'
    if (error.name === 'NotAllowedError') {
      message = 'Authentication cancelled by user'
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

