<template>
  <div>
    <v-row>
      <v-col cols="12" md="10" lg="8" class="mx-auto">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-account-details</v-icon>
            Detalhes do Usuário
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
              v-if="loading"
              indeterminate
              color="primary"
              class="mb-4"
            ></v-progress-linear>

            <div v-else-if="user">
              <!-- Basic Information -->
              <v-card variant="outlined" class="mb-4">
                <v-card-title class="text-subtitle-1">
                  <v-icon class="mr-2">mdi-information</v-icon>
                  Informações Básicas
                </v-card-title>
                <v-card-text>
                  <v-row>
                    <v-col cols="12" md="6">
                      <div class="mb-2">
                        <strong>Nome:</strong> {{ user.name || 'N/A' }}
                      </div>
                    </v-col>
                    <v-col cols="12" md="6">
                      <div class="mb-2">
                        <strong>E-mail:</strong> {{ user.email || 'N/A' }}
                      </div>
                    </v-col>
                    <v-col cols="12" md="6">
                      <div class="mb-2">
                        <strong>Hash:</strong> 
                        <code class="text-caption">{{ user.hash || 'N/A' }}</code>
                      </div>
                    </v-col>
                    <v-col cols="12" md="6">
                      <div class="mb-2">
                        <strong>E-mail Validado:</strong>
                        <v-chip
                          :color="user.emailValid ? 'success' : 'warning'"
                          size="small"
                          class="ml-2"
                        >
                          {{ user.emailValid ? 'Sim' : 'Não' }}
                        </v-chip>
                      </div>
                    </v-col>
                  </v-row>
                </v-card-text>
              </v-card>

              <!-- 2FA Information -->
              <v-card variant="outlined" class="mb-4">
                <v-card-title class="text-subtitle-1">
                  <v-icon class="mr-2">mdi-shield-lock</v-icon>
                  Autenticação de Dois Fatores (2FA)
                </v-card-title>
                <v-card-text>
                  <v-row>
                    <v-col cols="12" md="6">
                      <div class="mb-2">
                        <strong>2FA Ativado:</strong>
                        <v-chip
                          :color="user.using2FA ? 'success' : 'default'"
                          size="small"
                          class="ml-2"
                        >
                          {{ user.using2FA ? 'Sim' : 'Não' }}
                        </v-chip>
                      </div>
                    </v-col>
                    <v-col cols="12" md="6" v-if="user.using2FA">
                      <div class="mb-2">
                        <strong>Requer 2FA para Login Básico:</strong>
                        <v-chip
                          :color="user.require2FAForBasicLogin ? 'success' : 'default'"
                          size="small"
                          class="ml-2"
                        >
                          {{ user.require2FAForBasicLogin ? 'Sim' : 'Não' }}
                        </v-chip>
                      </div>
                    </v-col>
                    <v-col cols="12" md="6" v-if="user.using2FA">
                      <div class="mb-2">
                        <strong>Requer 2FA para Login Social:</strong>
                        <v-chip
                          :color="user.require2FAForSocialLogin ? 'success' : 'default'"
                          size="small"
                          class="ml-2"
                        >
                          {{ user.require2FAForSocialLogin ? 'Sim' : 'Não' }}
                        </v-chip>
                      </div>
                    </v-col>
                  </v-row>
                </v-card-text>
              </v-card>

              <!-- Roles Information -->
              <v-card variant="outlined" class="mb-4" v-if="user.roles && user.roles.length > 0">
                <v-card-title class="text-subtitle-1">
                  <v-icon class="mr-2">mdi-account-key</v-icon>
                  Roles
                </v-card-title>
                <v-card-text>
                  <div class="d-flex flex-wrap gap-2">
                    <v-chip
                      v-for="role in user.roles"
                      :key="role"
                      color="primary"
                      size="small"
                    >
                      {{ role }}
                    </v-chip>
                  </div>
                </v-card-text>
              </v-card>

              <!-- Actions -->
              <v-card variant="outlined">
                <v-card-title class="text-subtitle-1">
                  <v-icon class="mr-2">mdi-cog</v-icon>
                  Ações
                </v-card-title>
                <v-card-text>
                  <v-btn
                    color="primary"
                    prepend-icon="mdi-pencil"
                    class="mr-2 mb-2"
                    @click="editUser"
                  >
                    Editar Usuário
                  </v-btn>
                  <v-btn
                    color="error"
                    prepend-icon="mdi-delete"
                    class="mb-2"
                    @click="confirmDelete"
                  >
                    Deletar Usuário
                  </v-btn>
                </v-card-text>
              </v-card>
            </div>

            <v-alert v-else type="error">
              Usuário não encontrado
            </v-alert>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Delete Confirmation Dialog -->
    <DeleteUserDialog
      v-model="deleteDialog.show"
      :user="user"
      @confirm="handleDelete"
    />

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
import DeleteUserDialog from '../components/DeleteUserDialog.vue'

const router = useRouter()
const route = useRoute()
const usersStore = useUsersStore()

const loading = ref(true)
const user = ref(null)

const deleteDialog = ref({
  show: false
})

const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

const editUser = () => {
  if (user.value?.email) {
    router.push({ name: 'EditUser', params: { email: encodeURIComponent(user.value.email) } })
  }
}

const confirmDelete = () => {
  deleteDialog.value.show = true
}

const handleDelete = async (email) => {
  try {
    await usersStore.deleteUser(email)
    showMessage('Usuário deletado com sucesso!', 'success')
    setTimeout(() => {
      router.push({ name: 'UsersList' })
    }, 1500)
  } catch (error) {
    showMessage(error.message || 'Erro ao deletar usuário', 'error')
  } finally {
    deleteDialog.value.show = false
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
    user.value = await usersStore.fetchUserByEmail(email)
  } catch (error) {
    showMessage(error.message || 'Erro ao carregar usuário', 'error')
    setTimeout(() => {
      router.push({ name: 'UsersList' })
    }, 2000)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
code {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.85em;
}
</style>

