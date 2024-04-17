import { createApp } from 'vue'
import App from './App.vue'
import CreateUser from './components/CreateUserVue.vue'
import ListUser from './components/ListUsersVue.vue'
import Login from './components/LoginVue.vue'

const app = createApp(App)
app.component('users-create', CreateUser)
app.component('users-list', ListUser)
app.component('users-login', Login)

app.mount('#app')