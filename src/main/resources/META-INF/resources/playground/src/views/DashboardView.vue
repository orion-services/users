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
          <!-- User Information -->
          <v-card variant="outlined" class="mb-4">
            <v-card-text>
              <div class="mb-2">
                <strong>Name:</strong> {{ authStore.user?.name || 'N/A' }}
              </div>
              <div class="mb-2">
                <strong>E-mail:</strong> {{ authStore.user?.email || 'N/A' }}
              </div>
              <div class="mb-2">
                <strong>E-mail Validated:</strong>
                <v-chip
                  :color="authStore.user?.emailValid ? 'success' : 'warning'"
                  size="small"
                  class="ml-2"
                >
                  {{ authStore.user?.emailValid ? 'Yes' : 'No' }}
                </v-chip>
              </div>
              <div>
                <strong>2FA Enabled:</strong>
                <v-chip
                  :color="authStore.user?.using2FA ? 'success' : 'default'"
                  size="small"
                  class="ml-2"
                >
                  {{ authStore.user?.using2FA ? 'Yes' : 'No' }}
                </v-chip>
              </div>
            </v-card-text>
          </v-card>

          <!-- 2FA Setup -->
          <v-card variant="outlined" class="mb-4">
            <v-card-title class="text-subtitle-1">
              <v-icon class="mr-2">mdi-shield-lock</v-icon>
              Setup Two-Factor Authentication (2FA)
            </v-card-title>
            <v-card-text>
              <div v-if="!authStore.user?.using2FA">
                <v-form ref="setup2FAForm" v-model="setup2FAValid" @submit.prevent="setup2FA">
                  <v-text-field
                    v-model="setup2FAPassword"
                    label="Confirm your password"
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
                    Setup 2FA
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
                  <v-alert type="warning" class="mb-4">
                    <strong>Important:</strong> After scanning the QR code, you will need to use the app code to log in next time.
                  </v-alert>
                  <v-btn
                    color="success"
                    block
                    @click="goToValidate2FA"
                    size="large"
                    class="mb-2"
                  >
                    Already scanned, test code
                  </v-btn>
                  <v-btn
                    color="secondary"
                    block
                    variant="outlined"
                    @click="qrCodeUrl = null"
                    size="large"
                  >
                    Cancel
                  </v-btn>
                </div>
              </div>

              <div v-else>
                <v-alert type="success" class="mb-4">
                  <strong>2FA is enabled!</strong> You will need to provide a code from your authenticator app every time you log in.
                </v-alert>
                <v-btn
                  color="info"
                  block
                  variant="outlined"
                  @click="goToValidate2FA"
                  size="large"
                >
                  Test 2FA Code
                </v-btn>
              </div>
            </v-card-text>
          </v-card>

          <!-- Password Update -->
          <v-card variant="outlined" class="mb-4">
            <v-card-title class="text-subtitle-1">
              <v-icon class="mr-2">mdi-lock-reset</v-icon>
              Update Password
            </v-card-title>
            <v-card-text>
              <v-form ref="updatePasswordForm" v-model="updatePasswordValid" @submit.prevent="updatePassword">
                <v-text-field
                  v-model="updatePasswordData.currentPassword"
                  label="Current Password"
                  type="password"
                  :rules="passwordRules"
                  required
                  prepend-inner-icon="mdi-lock"
                  variant="outlined"
                  class="mb-4"
                ></v-text-field>

                <v-text-field
                  v-model="updatePasswordData.newPassword"
                  label="New Password"
                  type="password"
                  :rules="newPasswordRules"
                  required
                  prepend-inner-icon="mdi-lock-outline"
                  variant="outlined"
                  class="mb-2"
                ></v-text-field>

                <PasswordStrengthIndicator
                  :password="updatePasswordData.newPassword"
                  class="mb-4"
                />

                <v-text-field
                  v-model="updatePasswordData.confirmPassword"
                  label="Confirm New Password"
                  type="password"
                  :rules="confirmPasswordRules"
                  required
                  prepend-inner-icon="mdi-lock-check"
                  variant="outlined"
                  class="mb-4"
                ></v-text-field>

                <v-btn
                  type="submit"
                  color="primary"
                  block
                  :loading="updatePasswordLoading"
                  size="large"
                >
                  Update Password
                </v-btn>
              </v-form>
            </v-card-text>
          </v-card>

          <!-- Email Update -->
          <v-card variant="outlined" class="mb-4">
            <v-card-title class="text-subtitle-1">
              <v-icon class="mr-2">mdi-email-edit</v-icon>
              Update Email
            </v-card-title>
            <v-card-text>
              <v-form ref="updateEmailForm" v-model="updateEmailValid" @submit.prevent="updateEmail">
                <v-text-field
                  v-model="updateEmailData.newEmail"
                  label="New Email"
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
                  :loading="updateEmailLoading"
                  size="large"
                >
                  Update Email
                </v-btn>
              </v-form>
            </v-card-text>
          </v-card>

          <!-- Quick Actions -->
          <v-card variant="outlined">
            <v-card-title class="text-subtitle-1">
              <v-icon class="mr-2">mdi-cog</v-icon>
              WebAuthn
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
                Setup WebAuthn
              </v-btn>
            </v-card-text>
          </v-card>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { userApi } from '../services/api'
import PasswordStrengthIndicator from '../components/PasswordStrengthIndicator.vue'
import { getPasswordRules } from '../utils/passwordValidation'

const router = useRouter()
const authStore = useAuthStore()

const setup2FAForm = ref(null)
const setup2FAValid = ref(false)
const setup2FAPassword = ref('')
const setup2FALoading = ref(false)
const qrCodeUrl = ref(null)

// Email Update
const updateEmailForm = ref(null)
const updateEmailValid = ref(false)
const updateEmailData = ref({
  newEmail: ''
})
const updateEmailLoading = ref(false)

// Password Update
const updatePasswordForm = ref(null)
const updatePasswordValid = ref(false)
const updatePasswordData = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const updatePasswordLoading = ref(false)

// Snackbar
const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

// Validation rules
const passwordRules = [
  v => !!v || 'Password is required',
  v => (v && v.length >= 8) || 'Password must be at least 8 characters'
]

const emailRules = [
  v => !!v || 'Email is required',
  v => /.+@.+\..+/.test(v) || 'Email must be valid'
]

const newPasswordRules = [
  ...getPasswordRules().map(rule => {
    // Customize messages for "New password"
    return (v) => {
      const result = rule(v)
      if (typeof result === 'string' && result.includes('Password')) {
        return result.replace('Password', 'New password')
      }
      return result
    }
  })
]

const confirmPasswordRules = [
  v => !!v || 'Password confirmation is required',
  v => v === updatePasswordData.value.newPassword || 'Passwords do not match'
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
    
    // Update 2FA status in store (backend already enables it when generating QR code)
    if (authStore.user) {
      authStore.user.using2FA = true
    }
    
    showMessage('QR Code generated successfully! Scan it with your authenticator app.')
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Error generating QR code'
    showMessage(message, 'error')
  } finally {
    setup2FALoading.value = false
  }
}

const goToValidate2FA = () => {
  localStorage.setItem('pending_email', authStore.user.email)
  router.push('/2fa?mode=validate')
}

const updateEmail = async () => {
  if (!updateEmailValid.value) return

  updateEmailLoading.value = true
  try {
    const response = await userApi.updateUser(
      authStore.user.email,
      updateEmailData.value.newEmail,
      null,
      null
    )
    
    // Update token and user in store from LoginResponseDTO
    if (response.data?.authentication) {
      const authData = response.data.authentication
      authStore.setAuth(authData.token, authData.user)
    }
    
    // Clear form
    updateEmailData.value.newEmail = ''
    updateEmailForm.value?.reset()
    
    showMessage('Email updated successfully! A validation email has been sent to the new address.', 'success')
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Error updating email'
    showMessage(message, 'error')
  } finally {
    updateEmailLoading.value = false
  }
}

const updatePassword = async () => {
  if (!updatePasswordValid.value) return

  updatePasswordLoading.value = true
  try {
    const response = await userApi.updateUser(
      authStore.user.email,
      null,
      updatePasswordData.value.currentPassword,
      updatePasswordData.value.newPassword
    )
    
    // Update token and user in store from LoginResponseDTO
    if (response.data?.authentication) {
      const authData = response.data.authentication
      authStore.setAuth(authData.token, authData.user)
    }
    
    // Clear form
    updatePasswordData.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
    updatePasswordForm.value?.reset()
    
    showMessage('Password updated successfully!', 'success')
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Error updating password'
    showMessage(message, 'error')
  } finally {
    updatePasswordLoading.value = false
  }
}

const logout = () => {
  authStore.logout()
  router.push('/')
}
</script>

