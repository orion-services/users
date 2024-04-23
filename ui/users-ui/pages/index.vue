<template>
  <div class="container">
    <div class="content has-text-centered">
      <CreateUser v-if="createComponent" />
      <Login v-else />
      <button
        class="button is-text mt-3 is-pulled-right"
        @click="createComponent = !createComponent"
      >
        <span class="icon">
          <i class="fas fa-user-plus"></i>
        </span>
        <span v-if="createComponent">Login</span>
        <span v-else>Create user</span>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      createComponent: false,
    };
  },
};
</script>
