<template>
  <template v-if="create">
    <users-create></users-create>
  </template>
  <template v-else>
    <users-login></users-login>
  </template>
  <div class="container">
    <div class="columns is-centered">
      <div class="column is-half">
        <div class="content has-text-right mt-1">
          <button class="button is-text" @click="change()">{{ buttonText }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      create: false,
      buttonText: 'Create account'
    }
  },
  methods: {
    change() {
      if (this.create) {
        this.buttonText = 'Create account'
      } else {
        this.buttonText = 'Login'
      }
      this.create = !this.create
    }
  }
}
</script>

<style></style>
