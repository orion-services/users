<template>
  <v-row justify="center" align="center" style="min-height: 80vh">
    <v-col cols="12" sm="8" md="6" lg="4">
      <v-card>
        <v-card-title class="text-h5 text-center pa-4">
          Two-Factor Authentication
        </v-card-title>

        <v-card-text>
          <!-- Mode: Setup 2FA -->
          <div v-if="mode === 'setup'">
            <v-alert type="info" class="mb-4">
              Set up two-factor authentication by scanning the QR code with your authenticator app.
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
                label="Password"
                type="password"
                :rules="passwordRules"
                required
                prepend-inner-icon="mdi-lock"
                variant="outlined"
                class="mb-2"
              ></v-text-field>

              <PasswordStrengthIndicator
                :password="setupPassword"
                class="mb-4"
              />

              <v-btn
                type="submit"
                color="primary"
                block
                :loading="qrLoading"
                size="large"
                class="mb-4"
              >
                Generate QR Code
              </v-btn>
            </v-form>

            <!-- Display QR Code -->
            <div v-if="qrCodeUrl" class="text-center mt-4">
              <v-img
                :src="qrCodeUrl"
                max-width="300"
                class="mx-auto mb-4"
                contain
              ></v-img>
              <v-alert type="success" class="mb-4">
                Scan the QR code with your authenticator app (Google Authenticator, Authy, etc.)
              </v-alert>
              <v-btn
                color="success"
                block
                @click="mode = 'validate'"
                size="large"
              >
                Already scanned, validate code
              </v-btn>
            </div>
          </div>

          <!-- Mode: Validate 2FA code -->
          <div v-if="mode === 'validate'">
            <v-alert type="info" class="mb-4">
              Enter the 6-digit code from your authenticator app.
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
                label="6-digit code"
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
                Validate Code
              </v-btn>

              <v-btn
                color="secondary"
                block
                variant="outlined"
                @click="mode = 'setup'"
              >
                Setup 2FA
              </v-btn>
            </v-form>
          </div>
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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { userApi } from '../services/api'
import PasswordStrengthIndicator from '../components/PasswordStrengthIndicator.vue'
import { getPasswordRules } from '../utils/passwordValidation'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const mode = ref(route.query.mode || 'setup')
const setupForm = ref(null)
const validateForm = ref(null)
const setupValid = ref(false)
const validateValid = ref(false)

// Data for setup
const setupEmail = ref('')
const setupPassword = ref('')
const qrLoading = ref(false)
const qrCodeUrl = ref(null)

// Data for validation
const validateEmail = ref('')
const validateCode = ref('')
const validateLoading = ref(false)

// Snackbar
const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

// Validation rules
const emailRules = [
  v => !!v || 'Email is required',
  v => /.+@.+\..+/.test(v) || 'Email must be valid'
]

const passwordRules = getPasswordRules()

const codeRules = [
  v => !!v || 'Code is required',
  v => (v && v.length === 6) || 'Code must have 6 digits',
  v => /^\d{6}$/.test(v) || 'Code must contain only numbers'
]

onMounted(() => {
  // If coming from login, use pending email
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
    showMessage('QR Code generated successfully!')
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Error generating QR code'
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
      showMessage('Authentication successful!')
      router.push('/dashboard')
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Invalid code'
    showMessage(message, 'error')
  } finally {
    validateLoading.value = false
  }
}
</script>

