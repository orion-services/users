<template>
  <div class="card">
    <div class="card-content">
      <div class="content">
        <p>
          <strong>{{ name }}</strong>
          <br />
          <small>{{ email }}</small>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    name: String,
    email: String,
  },
};
</script>
