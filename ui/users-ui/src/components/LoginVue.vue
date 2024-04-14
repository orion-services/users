<template>
  <div class="container">
    <div class="columns is-centered">
      <div class="column is-half">
        <div class="card">
          <div class="card-content mt-6">
            <div class="content">
              <figure class="image is-64x64 mb-5">
                <img class="is-rounded" src="../../assets/logo.png" alt="Logo" />
              </figure>
              <input class="input mb-3" type="text" placeholder="Name" v-model="name" />
              <input class="input mb-3" type="text" placeholder="E-mail" v-model="email" />
              <input class="input mb-3" type="password" placeholder="Password" v-model="password" />
              <button class="button" @click="createUser()">Create user</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      api: 'http://localhost:8080/api/users/createAuthenticate',
      name: '',
      email: '',
      password: '',
      authenticated: false
    }
  },
  methods: {
    async createUser() {
      const params = new URLSearchParams()
      params.append('name', this.name)
      params.append('email', this.email)
      params.append('password', this.password)

      try {
        let response = await axios.post(this.api, params)
        sessionStorage.setItem('users', JSON.stringify(response.data))
      } catch (error) {
        console.log(error)
      }
    }
  }
}
</script>

<style></style>
