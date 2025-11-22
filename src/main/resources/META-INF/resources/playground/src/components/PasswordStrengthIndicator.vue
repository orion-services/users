<template>
  <div v-if="showIndicator" class="password-strength-indicator">
    <v-list density="compact" class="pa-0">
      <v-list-item class="px-0 py-1">
        <template v-slot:prepend>
          <v-icon
            :color="requirements.minLength ? 'success' : 'error'"
            size="small"
            class="mr-2"
          >
            {{ requirements.minLength ? 'mdi-check-circle' : 'mdi-close-circle' }}
          </v-icon>
        </template>
        <v-list-item-title class="text-body-2">
          Minimum of 8 characters
        </v-list-item-title>
      </v-list-item>

      <v-list-item class="px-0 py-1">
        <template v-slot:prepend>
          <v-icon
            :color="requirements.upperCase ? 'success' : 'error'"
            size="small"
            class="mr-2"
          >
            {{ requirements.upperCase ? 'mdi-check-circle' : 'mdi-close-circle' }}
          </v-icon>
        </template>
        <v-list-item-title class="text-body-2">
          At least one uppercase letter
        </v-list-item-title>
      </v-list-item>

      <v-list-item class="px-0 py-1">
        <template v-slot:prepend>
          <v-icon
            :color="requirements.lowerCase ? 'success' : 'error'"
            size="small"
            class="mr-2"
          >
            {{ requirements.lowerCase ? 'mdi-check-circle' : 'mdi-close-circle' }}
          </v-icon>
        </template>
        <v-list-item-title class="text-body-2">
          At least one lowercase letter
        </v-list-item-title>
      </v-list-item>

      <v-list-item class="px-0 py-1">
        <template v-slot:prepend>
          <v-icon
            :color="requirements.specialChar ? 'success' : 'error'"
            size="small"
            class="mr-2"
          >
            {{ requirements.specialChar ? 'mdi-check-circle' : 'mdi-close-circle' }}
          </v-icon>
        </template>
        <v-list-item-title class="text-body-2">
          At least one special character
        </v-list-item-title>
      </v-list-item>
    </v-list>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { validatePassword } from '../utils/passwordValidation'

const props = defineProps({
  password: {
    type: String,
    default: ''
  },
  showLabel: {
    type: Boolean,
    default: true
  }
})

const requirements = computed(() => {
  return validatePassword(props.password)
})

const showIndicator = computed(() => {
  return props.password && props.password.length > 0
})
</script>

<style scoped>
.password-strength-indicator {
  margin-top: 8px;
  margin-bottom: 8px;
}
</style>

