import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  // Base sem barra final na URL: http://host/dashboard
  base: '/dashboard',
  plugins: [
    vue(),
    vuetify({ autoImport: true })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  build: {
    // Saída em META-INF/resources/dashboard/ — URL: http://localhost:8080/dashboard
    outDir: '../dashboard',
    emptyOutDir: false, // Não esvaziar o diretório pois está fora do projeto root
    rollupOptions: {
      output: {
        manualChunks(id) {
          // Separar bibliotecas grandes em chunks próprios
          if (id.includes('node_modules')) {
            // Vuetify é grande e pode ser separado
            if (id.includes('vuetify')) {
              return 'vuetify'
            }
            // Vue, Vue Router e Pinia são necessários desde o início
            if (id.includes('vue') || id.includes('vue-router') || id.includes('pinia')) {
              return 'vue-vendor'
            }
            // Outras dependências
            if (id.includes('axios')) {
              return 'vendor'
            }
          }
        }
      }
    },
    // Desabilitar preload automático de módulos para evitar avisos
    modulePreload: {
      polyfill: false,
      resolveDependencies: () => []
    },
    chunkSizeWarningLimit: 1000
  },
  server: {
    // Em desenvolvimento, Vite roda em porta diferente do Quarkus
    port: 3001,
    proxy: {
      '/users': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})

