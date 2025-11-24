<template>
  <div>
    <v-row class="mb-4">
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-account-group</v-icon>
            Gerenciamento de Usuários
            <v-spacer></v-spacer>
            <v-btn
              color="primary"
              prepend-icon="mdi-account-plus"
              @click="$router.push({ name: 'CreateUser' })"
            >
              Criar Usuário
            </v-btn>
          </v-card-title>

          <v-divider></v-divider>

          <v-card-text>
            <!-- Search and Filters -->
            <v-row class="mb-4">
              <v-col cols="12" md="4">
                <v-text-field
                  v-model="searchQuery"
                  label="Buscar (nome ou e-mail)"
                  prepend-inner-icon="mdi-magnify"
                  variant="outlined"
                  density="compact"
                  clearable
                  @update:model-value="usersStore.setSearchQuery($event || '')"
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="4">
                <v-select
                  v-model="filterEmailValid"
                  :items="emailValidOptions"
                  label="Filtrar por validação de e-mail"
                  variant="outlined"
                  density="compact"
                  clearable
                  @update:model-value="usersStore.setFilterEmailValid($event)"
                ></v-select>
              </v-col>
              <v-col cols="12" md="4">
                <v-select
                  v-model="filter2FA"
                  :items="twoFAOptions"
                  label="Filtrar por 2FA"
                  variant="outlined"
                  density="compact"
                  clearable
                  @update:model-value="usersStore.setFilter2FA($event)"
                ></v-select>
              </v-col>
            </v-row>

            <!-- Users Table -->
            <v-data-table
              :headers="headers"
              :items="filteredUsers"
              :loading="usersStore.loading"
              :items-per-page="10"
              class="elevation-1"
            >
              <template v-slot:item.emailValid="{ item }">
                <v-chip
                  :color="item.emailValid ? 'success' : 'warning'"
                  size="small"
                >
                  {{ item.emailValid ? 'Sim' : 'Não' }}
                </v-chip>
              </template>

              <template v-slot:item.using2FA="{ item }">
                <v-chip
                  :color="item.using2FA ? 'success' : 'default'"
                  size="small"
                >
                  {{ item.using2FA ? 'Sim' : 'Não' }}
                </v-chip>
              </template>

              <template v-slot:item.actions="{ item }">
                <v-btn
                  icon="mdi-eye"
                  size="small"
                  variant="text"
                  @click="viewUser(item.email)"
                >
                  <v-icon>mdi-eye</v-icon>
                  <v-tooltip activator="parent">Visualizar</v-tooltip>
                </v-btn>
                <v-btn
                  icon="mdi-pencil"
                  size="small"
                  variant="text"
                  @click="editUser(item.email)"
                >
                  <v-icon>mdi-pencil</v-icon>
                  <v-tooltip activator="parent">Editar</v-tooltip>
                </v-btn>
                <v-btn
                  icon="mdi-delete"
                  size="small"
                  variant="text"
                  color="error"
                  @click="confirmDelete(item)"
                >
                  <v-icon>mdi-delete</v-icon>
                  <v-tooltip activator="parent">Deletar</v-tooltip>
                </v-btn>
              </template>

              <template v-slot:no-data>
                <div class="text-center pa-4">
                  <v-icon size="48" color="grey">mdi-account-off</v-icon>
                  <p class="text-grey mt-2">Nenhum usuário encontrado</p>
                </div>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Delete Confirmation Dialog -->
    <DeleteUserDialog
      v-model="deleteDialog.show"
      :user="deleteDialog.user"
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUsersStore } from '../stores/users'
import DeleteUserDialog from '../components/DeleteUserDialog.vue'

const router = useRouter()
const usersStore = useUsersStore()

const searchQuery = ref('')
const filterEmailValid = ref(null)
const filter2FA = ref(null)

const deleteDialog = ref({
  show: false,
  user: null
})

const snackbar = ref({
  show: false,
  message: '',
  color: 'success'
})

const headers = [
  { title: 'Nome', key: 'name', sortable: true },
  { title: 'E-mail', key: 'email', sortable: true },
  { title: 'E-mail Validado', key: 'emailValid', sortable: true },
  { title: '2FA Ativado', key: 'using2FA', sortable: true },
  { title: 'Hash', key: 'hash', sortable: false },
  { title: 'Ações', key: 'actions', sortable: false, align: 'end' }
]

const emailValidOptions = [
  { title: 'Validado', value: true },
  { title: 'Não Validado', value: false }
]

const twoFAOptions = [
  { title: 'Ativado', value: true },
  { title: 'Desativado', value: false }
]

const filteredUsers = computed(() => usersStore.filteredUsers)

const viewUser = (email) => {
  router.push({ name: 'UserDetail', params: { email: encodeURIComponent(email) } })
}

const editUser = (email) => {
  router.push({ name: 'EditUser', params: { email: encodeURIComponent(email) } })
}

const confirmDelete = (user) => {
  deleteDialog.value.user = user
  deleteDialog.value.show = true
}

const handleDelete = async (email) => {
  try {
    await usersStore.deleteUser(email)
    showMessage('Usuário deletado com sucesso!', 'success')
  } catch (error) {
    showMessage(error.message || 'Erro ao deletar usuário', 'error')
  } finally {
    deleteDialog.value.show = false
    deleteDialog.value.user = null
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
  try {
    await usersStore.fetchUsers()
  } catch (error) {
    showMessage(error.message || 'Erro ao carregar usuários', 'error')
  }
})
</script>

