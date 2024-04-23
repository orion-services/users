<template>
  <div class="container">
    <div class="columns is-centered">
      <div class="column is-half">
        <div class="card">
          <div class="card-content mt-6">
            <div class="content">
              <figure class="image is-64x64 mb-5">
                <img class="is-rounded" src="@/assets/logo.png" alt="Logo" />
              </figure>

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
                  v-model="email"
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
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      url: "http://localhost:8080/api/users/create",
      name: "",
      email: "",
      password: "",
      user: {},
    };
  },
  methods: {
    async create() {
      try {
        let response = await fetch(this.url, {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
          // Convert the data to a URL-encoded query string
          body: new URLSearchParams({
            name: this.name,
            email: this.email,
            password: this.password,
          }),
        });
        this.user = await response.json();
        console.log(this.user);
      } catch (error) {
        console.error(error);
      }
    },
  },
};
</script>

<style></style>
