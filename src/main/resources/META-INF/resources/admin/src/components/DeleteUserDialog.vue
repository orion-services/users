<template>
  <v-dialog
    :model-value="modelValue"
    max-width="500"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <v-card>
      <v-card-title class="text-h5 pa-4">
        <v-icon color="error" class="mr-2">mdi-alert</v-icon>
        Confirm deletion
      </v-card-title>

      <v-divider></v-divider>

      <v-card-text class="pa-6">
        <p class="text-body-1 mb-4">
          Are you sure you want to delete the user below?
        </p>
        
        <v-card variant="outlined" class="pa-4">
          <div class="mb-2">
            <strong>Name:</strong> {{ user?.name || 'N/A' }}
          </div>
          <div class="mb-2">
            <strong>Email:</strong> {{ user?.email || 'N/A' }}
          </div>
          <div>
            <strong>Hash:</strong> {{ user?.hash || 'N/A' }}
          </div>
        </v-card>

        <v-alert type="warning" class="mt-4">
          <strong>Warning:</strong> This action cannot be undone!
        </v-alert>
      </v-card-text>

      <v-divider></v-divider>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          variant="text"
          @click="$emit('update:modelValue', false)"
        >
          Cancel
        </v-btn>
        <v-btn
          color="error"
          variant="flat"
          @click="confirm"
          :loading="loading"
        >
          Delete
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  user: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const loading = ref(false)

const confirm = () => {
  if (props.user?.email) {
    emit('confirm', props.user.email)
  }
}
</script>

