<template>
  <v-row justify="center" align="center" style="min-height: 80vh">
    <v-col cols="12" sm="8" md="6" lg="4">
      <v-card>
        <v-card-title class="text-h5 text-center pa-4">
          Recover Password
        </v-card-title>

        <v-card-text>
          <v-form ref="recoverForm" v-model="recoverValid" @submit.prevent="handleRecoverPassword">
            <p class="text-body-2 mb-4">
              Enter your email and we'll send you a new password.
            </p>

            <v-text-field
              v-model="email"
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
              :loading="loading"
              size="large"
              class="mb-4"
            >
              Send New Password
            </v-btn>

            <v-btn
              color="secondary"
              block
              variant="text"
              @click="$router.push('/')"
            >
              Back to Login
            </v-btn>
          </v-form>
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
import { userApi } from '../services/api'

const router = useRouter()

const recoverForm = ref(null)
const recoverValid = ref(false)
const email = ref('')
const loading = ref(false)

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

const showMessage = (message, color = 'success') => {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

const handleRecoverPassword = async () => {
  if (!recoverValid.value) return

  loading.value = true
  try {
    await userApi.recoverPassword(email.value)
    showMessage('New password sent to your email! Check your inbox.', 'success')
    // Clear field after success
    email.value = ''
    // Optional: redirect after a few seconds
    setTimeout(() => {
      router.push('/')
    }, 3000)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Error recovering password'
    showMessage(message, 'error')
  } finally {
    loading.value = false
  }
}
</script>

