<template>
  <div>
    <v-row>
      <v-col cols="12" md="8" lg="6" class="mx-auto">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-account-edit</v-icon>
            Editar Usuário
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
            <v-progress-linear
              v-if="loadingUser"
              indeterminate
              color="primary"
              class="mb-4"
            ></v-progress-linear>

            <v-form v-else ref="userForm" v-model="valid" @submit.prevent="handleSubmit">
              <v-text-field
                v-model="formData.name"
                label="Nome"
                :rules="nameRules"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                class="mb-4"
                required
              ></v-text-field>

              <v-text-field
                v-model="formData.currentEmail"
                label="E-mail Atual"
                disabled
                prepend-inner-icon="mdi-email"
                variant="outlined"
                class="mb-4"
              ></v-text-field>

              <v-text-field
                v-model="formData.newEmail"
                label="Novo E-mail (opcional)"
                type="email"
                :rules="newEmailRules"
                prepend-inner-icon="mdi-email-outline"
                variant="outlined"
                class="mb-4"
                hint="Deixe em branco para manter o e-mail atual"
                persistent-hint
              ></v-text-field>

              <v-divider class="my-4"></v-divider>

              <v-alert type="info" class="mb-4">
                Para alterar a senha, preencha ambos os campos abaixo. Caso contrário, deixe em branco.
              </v-alert>

              <v-text-field
                v-model="formData.password"
                label="Senha Atual (obrigatório se alterar senha)"
                type="password"
                :rules="passwordRules"
                prepend-inner-icon="mdi-lock"
                variant="outlined"
                class="mb-4"
                :hint="formData.newPassword ? 'Obrigatório' : 'Opcional'"
                persistent-hint
              ></v-text-field>

              <v-text-field
                v-model="formData.newPassword"
                label="Nova Senha (opcional)"
                type="password"
                :rules="newPasswordRules"
                prepend-inner-icon="mdi-lock-outline"
                variant="outlined"
                class="mb-4"
                hint="Mínimo 8 caracteres"
                persistent-hint
              ></v-text-field>

              <v-text-field
                v-if="formData.newPassword"
                v-model="formData.confirmPassword"
                label="Confirmar Nova Senha"
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
                Atualizar Usuário
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
  v => !!v || 'Nome é obrigatório',
  v => (v && v.trim().length > 0) || 'Nome não pode estar vazio'
]

const newEmailRules = [
  v => !v || /.+@.+\..+/.test(v) || 'E-mail deve ser válido'
]

const passwordRules = [
  v => {
    if (formData.value.newPassword && !v) {
      return 'Senha atual é obrigatória quando alterar senha'
    }
    return true
  }
]

const newPasswordRules = [
  v => {
    if (!v) return true // Optional
    if (v.length < 8) return 'Senha deve ter pelo menos 8 caracteres'
    return true
  }
]

const confirmPasswordRules = [
  v => {
    if (!formData.value.newPassword) return true
    if (!v) return 'Confirmação de senha é obrigatória'
    if (v !== formData.value.newPassword) return 'As senhas não coincidem'
    return true
  }
]

const handleSubmit = async () => {
  if (!valid.value) return

  // Validate that if newPassword is provided, password must also be provided
  if (formData.value.newPassword && !formData.value.password) {
    showMessage('A senha atual é obrigatória quando alterar a senha', 'error')
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

    showMessage('Usuário atualizado com sucesso!', 'success')
    
    // Redirect to users list after a short delay
    setTimeout(() => {
      router.push({ name: 'UsersList' })
    }, 1500)
  } catch (error) {
    showMessage(error.message || 'Erro ao atualizar usuário', 'error')
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
    showMessage(error.message || 'Erro ao carregar usuário', 'error')
    setTimeout(() => {
      router.push({ name: 'UsersList' })
    }, 2000)
  } finally {
    loadingUser.value = false
  }
})
</script>

