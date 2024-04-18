<template>
  <div class="container">
    <div class="columns is-centered">
      <div class="column is-half">
        <template v-for="user in users" v-bind:key="user.id">
          <div class="card">
            <div class="card-content">
              <div class="fa-regular fa-user"></div>
              <span class="ml-3">{{ user.name }}</span>
              <span class="ml-3 mr-6">{{ user.email }}</span>
              <i class="fa-regular fa-pen-to-square ml-5"></i>
              <i class="fa-regular fa-trash ml-2"></i>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      url: 'http://localhost:8080/api/users/list',
      users: []
    }
  },
  mounted() {
    this.listUsers()
  },
  methods: {
    async listUsers() {
      try {
        let response = await axios.get(this.url)
        this.users = response.data
      } catch (error) {
        console.log(error)
      }
    }
  }
}
</script>

<style></style>
