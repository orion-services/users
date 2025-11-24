import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  // Configurar base para servir em /console/ tanto em desenvolvimento quanto em produção
  base: '/console/',
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
    // Gerar arquivos diretamente em META-INF/resources/console/
    // para serem servidos pelo Quarkus em http://localhost:8080/console/
    outDir: '../console',
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

