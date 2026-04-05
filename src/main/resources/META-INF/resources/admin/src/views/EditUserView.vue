<template>
  <div>
    <v-row>
      <v-col cols="12" md="8" lg="6" class="mx-auto">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-account-edit</v-icon>
            Edit user
            <v-spacer></v-spacer>
            <v-btn
              icon="mdi-arrow-left"
              variant="text"
              @click="$router.push({ name: 'UsersList' })"
            >
              <v-icon>mdi-arrow-left</v-icon>
              <v-tooltip activator="parent">Back</v-tooltip>
            </v-btn>
          </v-card-title>

          <v-divider></v-divider>

          <v-card-text class="pa-6">
            <v-progress-linear
              v-if="loadingUser"
              indeterminate
              color="primary"
              class="mb-4"
            ></v-progress-linear>

            <v-form v-else ref="userForm" v-model="valid" @submit.prevent="handleSubmit">
              <v-text-field
                v-model="formData.name"
                label="Name"
                :rules="nameRules"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                class="mb-4"
                required
              ></v-text-field>

              <v-text-field
                v-model="formData.currentEmail"
                label="Current email"
                disabled
                prepend-inner-icon="mdi-email"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="formData.newEmail"
                label="New email (optional)"
                type="email"
                :rules="newEmailRules"
                prepend-inner-icon="mdi-email-outline"
                variant="outlined"
                class="mb-4"
                hint="Leave blank to keep the current email"
                persistent-hint
              ></v-text-field>

              <v-divider class="my-4"></v-divider>

              <v-alert type="info" class="mb-4">
                To change your password, fill in both fields below. Otherwise leave them blank.
              </v-alert>

              <v-text-field
                v-model="formData.password"
                label="Current password (required if changing password)"
                type="password"
                :rules="passwordRules"
                prepend-inner-icon="mdi-lock"
                variant="outlined"
                class="mb-4"
                :hint="formData.newPassword ? 'Required' : 'Optional'"
                persistent-hint
              ></v-text-field>

              <v-text-field
                v-model="formData.newPassword"
                label="New password (optional)"
                type="password"
                :rules="newPasswordRules"
                prepend-inner-icon="mdi-lock-outline"
                variant="outlined"
                class="mb-4"
                hint="At least 8 characters"
                persistent-hint
              ></v-text-field>

              <v-text-field
                v-if="formData.newPassword"
                v-model="formData.confirmPassword"
                label="Confirm new password"
                type="password"
                :rules="confirmPasswordRules"
                prepend-inner-icon="mdi-lock-check"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-btn
                type="submit"
                color="primary"
                block
                size="large"
                :loading="loading"
                :disabled="!valid"
              >
                Update user
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Snackbar for notifications -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="5000"
      top
    >
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="snackbar.show = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUsersStore } from '../stores/users'

const router = useRouter()
const route = useRoute()
const usersStore = useUsersStore()

const userForm = ref(null)
const valid = ref(false)
const loading = ref(false)
const loadingUser = ref(true)

const formData = ref({
  name: '',
  currentEmail: '',
  newEmail: '',
  password: '',
  newPassword: '',
  confirmPassword: ''
})

const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

const nameRules = [
  v => !!v || 'Name is required',
  v => (v && v.trim().length > 0) || 'Name cannot be empty'
]

const newEmailRules = [
  v => !v || /.+@.+\..+/.test(v) || 'Email must be valid'
]

const passwordRules = [
  v => {
    if (formData.value.newPassword && !v) {
      return 'Current password is required when changing password'
    }
    return true
  }
]

const newPasswordRules = [
  v => {
    if (!v) return true // Optional
    if (v.length < 8) return 'Password must be at least 8 characters'
    return true
  }
]

const confirmPasswordRules = [
  v => {
    if (!formData.value.newPassword) return true
    if (!v) return 'Password confirmation is required'
    if (v !== formData.value.newPassword) return 'Passwords do not match'
    return true
  }
]

const handleSubmit = async () => {
  if (!valid.value) return

  // Validate that if newPassword is provided, password must also be provided
  if (formData.value.newPassword && !formData.value.password) {
    showMessage('Current password is required when changing password', 'error')
    return
  }

  loading.value = true

  try {
    await usersStore.updateUser({
      email: formData.value.currentEmail,
      name: formData.value.name || null,
      newEmail: formData.value.newEmail || null,
      password: formData.value.password || null,
      newPassword: formData.value.newPassword || null
    })

    showMessage('User updated successfully.', 'success')
    
    // Redirect to users list after a short delay
    setTimeout(() => {
      router.push({ name: 'UsersList' })
    }, 1500)
  } catch (error) {
    showMessage(error.message || 'Failed to update user', 'error')
  } finally {
    loading.value = false
  }
}

const showMessage = (message, color = 'success') => {
  snackbar.value = {
    show: true,
    message,
    color
  }
}

onMounted(async () => {
  // Decode email from URL (in case it's encoded)
  const email = decodeURIComponent(route.params.email)
  
  try {
    const user = await usersStore.fetchUserByEmail(email)
    formData.value.name = user.name || ''
    formData.value.currentEmail = user.email || email
  } catch (error) {
    showMessage(error.message || 'Failed to load user', 'error')
    setTimeout(() => {
      router.push({ name: 'UsersList' })
    }, 2000)
  } finally {
    loadingUser.value = false
  }
})
</script>

