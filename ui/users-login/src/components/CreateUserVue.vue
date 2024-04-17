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

              <form>
                <div class="control has-icons-left">
                  <input
                    type="text"
                    class="input mb-4"
                    placeholder="Full name"
                    v-model="name"
                    required
                  />
                  <span class="icon is-small is-left">
                    <i class="fas fa-user"></i>
                  </span>
                </div>

                <div class="control has-icons-left">
                  <input
                    class="input mb-4"
                    type="email"
                    placeholder="Your e-mail"
                    v-model="firstEmail"
                    required
                  />
                  <span class="icon is-small is-left">
                    <i class="fas fa-envelope"></i>
                  </span>
                </div>

                <div class="control has-icons-left">
                  <input
                    class="input mb-4"
                    type="email"
                    placeholder="Confirm your e-mail"
                    v-model="secondEmail"
                    required
                  />
                  <span class="icon is-small is-left">
                    <i class="fas fa-envelope"></i>
                  </span>
                </div>

                <div class="control has-icons-left">
                  <input
                    class="input mb-4"
                    type="password"
                    placeholder="Choose a password"
                    v-model="password"
                    required
                    minlength="8"
                  />
                  <span class="icon is-small is-left">
                    <i class="fas fa-lock"></i>
                  </span>
                </div>

                <button class="button is-rounded" @click="create()">
                  <span class="icon">
                    <i class="fas fa-plus"></i>
                  </span>
                  <span>Create</span>
                </button>
              </form>
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
      password: ''
    }
  },
  methods: {
    async create() {
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
