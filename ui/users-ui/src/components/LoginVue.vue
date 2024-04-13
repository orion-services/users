<template>
  <div class="columns">
    <div class="column"></div>
    <div class="column">
      <div class="card">
        <div class="card-content">
          <div class="content">
            <h1 class="title is-4">Users</h1>
            <input class="input" type="text" placeholder="Name" v-model="name" />
            <input class="input" type="text" placeholder="E-mail" v-model="email" />
            <input class="input" type="password" placeholder="Password" v-model="password" />
            <button class="button" @click="login()">login</button>
          </div>
        </div>
      </div>
    </div>
    <div class="column"></div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      name: '',
      email: '',
      password: ''
    }
  },
  methods: {
    login() {
        const params = new URLSearchParams()
        params.append('name', this.name)
        params.append('email', this.email)
        params.append('password', this.password)

        axios.post('http://localhost:8080/api/users/createAuthenticate', params)
        .then((response) => {
          console.log(response.data)
        })
        .catch((error) => {
          console.log(error)
        })
    }
  }
}
</script>

<style></style>
