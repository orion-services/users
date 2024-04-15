import { createApp } from 'vue'
import App from './App.vue'
import UsersLogin from './components/UsersLoginVue.vue'

const app = createApp(App)
app.component('users-login', UsersLogin)
app.mount('#app')