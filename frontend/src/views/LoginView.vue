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
import { ref } from 'vue'
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

    if (data.token && data.user) {
      authStore.setAuth(data.token, data.user)
      showMessage('Registration successful!')
      router.push('/dashboard')
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Error registering user'
    showMessage(message, 'error')
  } finally {
    registerLoading.value = false
  }
}
</script>

