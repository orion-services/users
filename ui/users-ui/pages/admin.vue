<template>
  <template v-for="user in users" :key="user.id">
    <User :name="user.name" :email="user.email" />
  </template>
</template>

<script>
export default {
  data() {
    return {
      users: [],
    };
  },

  mounted() {
    this.load();
  },

  methods: {
    async load() {
      try {
        let response = await fetch("http://localhost:8080/api/users/list");
        this.users = await response.json();
      } catch (error) {
        this.error = error.response.data.message;
      }
    },
  },
};
</script>
