<template>
  <div class="container">
    <div class="columns is-centered">
      <div class="column is-half">
        <div class="card"></div>
        <div class="card mt-6">
          <div class="card-content has-text-centered">
            <figure class="image is-64x64 mb-3">
              <img
                class="is-rounded"
                src="@/assets/logo.png"
                alt="Orion logo"
              />
            </figure>

            <div class="control has-icons-left">
              <input
                class="input mb-3"
                type="email"
                placeholder="E-mail"
                required
                v-model="email"
              />
              <span class="icon is-small is-left">
                <i class="fas fa-envelope"></i>
              </span>
            </div>

            <div class="control has-icons-left">
              <input
                class="input mb-3"
                type="password"
                placeholder="Password"
                v-model="password"
                required
              />
              <span class="icon is-small is-left">
                <i class="fas fa-lock"></i>
              </span>
            </div>

            <button
              class="button is-rounded""
              @click="authenticate()"
            >
              <span class="icon">
                <i class="fas fa-right-to-bracket"></i>
              </span>
              <span>Login</span>
            </button>
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
      api: "http://localhost:8080/api/users/authenticate",
      email: "",
      password: "",
      user: {},
    };
  },
  methods: {
    async authenticate() {
      try {
        if (this.email === "" || this.password === "") {
          alert("Please fill in all fields.");
          // redirect to admin page
          this.$router.push("/admin");
        } else {
          let response = await fetch(this.api, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              email: this.email,
              password: this.password,
            }),
          });
          let user = await response.json();
        }
      } catch (error) {
        alert("Invalid e-mail or password.");
      }
    },
  },
};
</script>
