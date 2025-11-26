import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import legacy from '@vitejs/plugin-legacy'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    legacy({
      targets: ['defaults', 'not IE 11', '> 0.25%', 'last 2 versions'],
      renderLegacyChunks: true,
      polyfills: ['es.promise.finally', 'es/map', 'es/set'],
      modernPolyfills: ['es.promise.finally']
    })
  ],
  server: {
    port: 3100,
    host: '0.0.0.0', // allow access from local network
    proxy: {
      '/api': {
        target: 'http://192.168.0.197:3200',
        changeOrigin: true,
      },
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.js',
  },
})