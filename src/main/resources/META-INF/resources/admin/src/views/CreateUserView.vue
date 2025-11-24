<template>
  <div>
    <v-row>
      <v-col cols="12" md="8" lg="6" class="mx-auto">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-account-plus</v-icon>
            Criar Novo Usuário
            <v-spacer></v-spacer>
            <v-btn
              icon="mdi-arrow-left"
              variant="text"
              @click="$router.push({ name: 'UsersList' })"
            >
              <v-icon>mdi-arrow-left</v-icon>
              <v-tooltip activator="parent">Voltar</v-tooltip>
            </v-btn>
          </v-card-title>

          <v-divider></v-divider>

          <v-card-text class="pa-6">
            <v-form ref="userForm" v-model="valid" @submit.prevent="handleSubmit">
              <v-text-field
                v-model="formData.name"
                label="Nome"
                :rules="nameRules"
                required
                prepend-inner-icon="mdi-account"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="formData.email"
                label="E-mail"
                type="email"
                :rules="emailRules"
                required
                prepend-inner-icon="mdi-email"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="formData.password"
                label="Senha"
                type="password"
                :rules="passwordRules"
                required
                prepend-inner-icon="mdi-lock"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="formData.confirmPassword"
                label="Confirmar Senha"
                type="password"
                :rules="confirmPasswordRules"
                required
                prepend-inner-icon="mdi-lock-check"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-alert type="info" class="mb-4">
                <strong>Informação:</strong> O usuário receberá um e-mail com o código de validação após a criação.
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                block
                size="large"
                :loading="loading"
                :disabled="!valid"
              >
                Criar Usuário
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
          Fechar
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUsersStore } from '../stores/users'

const router = useRouter()
const usersStore = useUsersStore()

const userForm = ref(null)
const valid = ref(false)
const loading = ref(false)

const formData = ref({
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

const nameRules = [
  v => !!v || 'Nome é obrigatório',
  v => (v && v.trim().length > 0) || 'Nome não pode estar vazio'
]

const emailRules = [
  v => !!v || 'E-mail é obrigatório',
  v => /.+@.+\..+/.test(v) || 'E-mail deve ser válido'
]

const passwordRules = [
  v => !!v || 'Senha é obrigatória',
  v => (v && v.length >= 8) || 'Senha deve ter pelo menos 8 caracteres'
]

const confirmPasswordRules = [
  v => !!v || 'Confirmação de senha é obrigatória',
  v => v === formData.value.password || 'As senhas não coincidem'
]

const handleSubmit = async () => {
  if (!valid.value) return

  loading.value = true

  try {
    await usersStore.createUser({
      name: formData.value.name,
      email: formData.value.email,
      password: formData.value.password
    })

    showMessage('Usuário criado com sucesso!', 'success')
    
    // Reset form
    formData.value = {
      name: '',
      email: '',
      password: '',
      confirmPassword: ''
    }
    userForm.value?.reset()

    // Redirect to users list after a short delay
    setTimeout(() => {
      router.push({ name: 'UsersList' })
    }, 1500)
  } catch (error) {
    showMessage(error.message || 'Erro ao criar usuário', 'error')
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
</script>

