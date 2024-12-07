import { fileURLToPath, URL } from 'node:url'
import fs from 'fs'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    https: {
      key: fs.readFileSync('public/ssvep_decrypted.key','utf8'),  // 私钥路径
      cert: fs.readFileSync('public/ssvep.crt','utf8'),  // 证书路径
      passphrase: 'ssvep'
    },
    proxy: {
      '/api': {
        target: 'https://localhost:8080',  // 后端地址，使用 HTTPS
        changeOrigin: true,
        secure: false,  // 如果是自签名证书，可以设置为 false
        rewrite: (path) => path.replace(/^\/api/, '')  // 可选：重写路径
      },
    },
  },

  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  build:{
    chunkSizeWarningLimit:1500,
    emptyOutDir:true,
    rollupOptions:{
      output:{
        manualChunks(id){
          if(id.includes('node_modules')){
            return id.toString().split('node_modules/')[1].split('/')[0].toString();
          }
        }
      }
    }
  }
})
