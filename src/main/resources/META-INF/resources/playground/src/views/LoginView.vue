<template>
  <v-row justify="center" align="center" style="min-height: 80vh">
    <v-col cols="12" sm="8" md="6" lg="4">
      <v-card>
        <v-tabs v-model="tab" bg-color="primary">
          <v-tab value="login">Login</v-tab>
          <v-tab value="register">Register</v-tab>
        </v-tabs>

        <v-card-text>
          <v-tabs-window v-model="tab">
            <!-- Login Tab -->
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
                  :loading="loginLoading"
                  size="large"
                  class="mb-4"
                >
                  Login
                </v-btn>

                <v-btn
                  color="secondary"
                  block
                  variant="outlined"
                  @click="$router.push('/webauthn')"
                  class="mb-2"
                >
                  Login with WebAuthn
                </v-btn>

                <v-divider class="my-4">
                  <span class="text-body-2 text-medium-emphasis px-2">ou</span>
                </v-divider>

                <v-btn
                  color="primary"
                  block
                  variant="outlined"
                  @click="handleGoogleLogin"
                  :loading="googleLoading"
                  class="mb-2"
                  prepend-icon="mdi-google"
                  size="large"
                >
                  Login with Google
                </v-btn>

                <div class="text-center mt-4">
                  <a
                    href="#"
                    @click.prevent="$router.push('/recover-password')"
                    class="text-decoration-none text-body-2"
                  >
                    Forgot your password?
                  </a>
                </div>
              </v-form>
            </v-tabs-window-item>

            <!-- Register Tab -->
            <v-tabs-window-item value="register">
              <v-form ref="registerForm" v-model="registerValid" @submit.prevent="handleRegister">
                <v-text-field
                  v-model="registerName"
                  label="Name"
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
                  label="Password"
                  type="password"
                  :rules="passwordRules"
                  required
                  prepend-inner-icon="mdi-lock"
                  variant="outlined"
                  class="mb-2"
                ></v-text-field>

                <PasswordStrengthIndicator
                  :password="registerPassword"
                  class="mb-4"
                />

                <v-btn
                  type="submit"
                  color="primary"
                  block
                  :loading="registerLoading"
                  size="large"
                >
                  Register
                </v-btn>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { userApi } from '../services/api'
import PasswordStrengthIndicator from '../components/PasswordStrengthIndicator.vue'
import { getPasswordRules } from '../utils/passwordValidation'

const router = useRouter()
const authStore = useAuthStore()

const tab = ref('login')
const loginForm = ref(null)
const registerForm = ref(null)
const loginValid = ref(false)
const registerValid = ref(false)

// Login form data
const loginEmail = ref('')
const loginPassword = ref('')
const loginLoading = ref(false)
const googleLoading = ref(false)

// Registration form data
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

// Validation rules
const emailRules = [
  v => !!v || 'Email is required',
  v => /.+@.+\..+/.test(v) || 'Email must be valid'
]

const passwordRules = getPasswordRules()

const nameRules = [
  v => !!v || 'Name is required'
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

    // When 2FA is required, backend returns complete LoginResponseDTO
    if (data.requires2FA === true) {
      // User needs 2FA
      localStorage.setItem('pending_email', loginEmail.value)
      router.push('/2fa?mode=validate')
    } else if (data.token && data.user) {
      // Successful login without 2FA - backend returns AuthenticationDTO directly
      authStore.setAuth(data.token, data.user)
      showMessage('Login successful!')
      router.push('/dashboard')
    } else if (data.authentication) {
      // Fallback: if backend returns LoginResponseDTO without requires2FA
      authStore.setAuth(data.authentication.token, data.authentication.user)
      showMessage('Login successful!')
      router.push('/dashboard')
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Error logging in'
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

    // Backend returns LoginResponseDTO with authentication object
    if (data.authentication) {
      authStore.setAuth(data.authentication.token, data.authentication.user)
      showMessage('Registration successful!')
      router.push('/dashboard')
    } else if (data.token && data.user) {
      // Fallback for old format (should not happen with current backend)
      authStore.setAuth(data.token, data.user)
      showMessage('Registration successful!')
      router.push('/dashboard')
    } else {
      showMessage('Unexpected response format', 'error')
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Error registering user'
    showMessage(message, 'error')
  } finally {
    registerLoading.value = false
  }
}

const handleGoogleLogin = async () => {
  googleLoading.value = true
  try {
    // Wait for Google Identity Services to load
    await new Promise((resolve) => {
      if (typeof google !== 'undefined' && google.accounts) {
        resolve()
      } else {
        const checkInterval = setInterval(() => {
          if (typeof google !== 'undefined' && google.accounts) {
            clearInterval(checkInterval)
            resolve()
          }
        }, 100)
        setTimeout(() => {
          clearInterval(checkInterval)
          resolve()
        }, 5000)
      }
    })

    if (typeof google === 'undefined' || !google.accounts) {
      showMessage('Google Sign-In library not loaded. Please refresh the page.', 'error')
      googleLoading.value = false
      return
    }

    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID || '[Google Client ID]'

    // Use Google Identity Services to get ID token
    google.accounts.id.initialize({
      client_id: clientId,
      callback: async (response) => {
        try {
          // Send the ID token (credential) to backend
          const apiResponse = await userApi.loginWithGoogle(response.credential)
          const data = apiResponse.data

          // When 2FA is required, backend returns LoginResponseDTO
          if (data.requires2FA === true) {
            // Extract email from Google token (simplified - in production, decode JWT properly)
            // For now, we'll need to get email from the response or store it temporarily
            // Since we don't have email in the response, we'll need to decode the token or store it
            // For simplicity, we'll store the token temporarily and extract email later
            localStorage.setItem('pending_social_login', 'google')
            localStorage.setItem('pending_google_token', response.credential)
            router.push('/2fa?mode=validate&source=social')
          } else if (data.token && data.user) {
            // Successful login without 2FA - backend returns AuthenticationDTO directly
            authStore.setAuth(data.token, data.user)
            showMessage('Login with Google successful!')
            router.push('/dashboard')
          } else if (data.authentication) {
            // Fallback: if backend returns LoginResponseDTO without requires2FA
            authStore.setAuth(data.authentication.token, data.authentication.user)
            showMessage('Login with Google successful!')
            router.push('/dashboard')
          }
        } catch (error) {
          const message = error.response?.data?.message || error.message || 'Error logging in with Google'
          showMessage(message, 'error')
        } finally {
          googleLoading.value = false
        }
      }
    })

    // Trigger the One Tap or popup
    google.accounts.id.prompt((notification) => {
      if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
        // If One Tap is not displayed, show a button or use popup
        google.accounts.oauth2.initTokenClient({
          client_id: clientId,
          scope: 'openid profile email',
          callback: async (tokenResponse) => {
            try {
              // Get user info and create a mock ID token for backend
              // In production, you should get the ID token from Google
              const userInfoResponse = await fetch(`https://www.googleapis.com/oauth2/v2/userinfo?access_token=${tokenResponse.access_token}`)
              const userInfo = await userInfoResponse.json()
              
              // For now, send access token - backend should handle validation
              // In production, use proper ID token from Google
              const apiResponse = await userApi.loginWithGoogle(tokenResponse.access_token)
              const data = apiResponse.data

              // When 2FA is required, backend returns LoginResponseDTO
              if (data.requires2FA === true) {
                localStorage.setItem('pending_social_login', 'google')
                localStorage.setItem('pending_google_token', tokenResponse.access_token)
                router.push('/2fa?mode=validate&source=social')
              } else if (data.token && data.user) {
                // Successful login without 2FA
                authStore.setAuth(data.token, data.user)
                showMessage('Login with Google successful!')
                router.push('/dashboard')
              } else if (data.authentication) {
                // Fallback: if backend returns LoginResponseDTO without requires2FA
                authStore.setAuth(data.authentication.token, data.authentication.user)
                showMessage('Login with Google successful!')
                router.push('/dashboard')
              }
            } catch (error) {
              const message = error.response?.data?.message || error.message || 'Error logging in with Google'
              showMessage(message, 'error')
            } finally {
              googleLoading.value = false
            }
          }
        }).requestAccessToken()
      }
    })
  } catch (error) {
    const message = error.message || 'Error initializing Google Sign-In'
    showMessage(message, 'error')
    googleLoading.value = false
  }
}

onMounted(() => {
  // Scripts are loaded in index.html, just wait for them to be available
})
</script>

