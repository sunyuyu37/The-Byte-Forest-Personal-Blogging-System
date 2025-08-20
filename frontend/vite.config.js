import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: [
        'vue',
        'vue-router',
        'pinia',
        {
          'element-plus': [
            'ElMessage',
            'ElMessageBox',
            'ElNotification',
            'ElLoading'
          ]
        }
      ],
      dts: true,
      eslintrc: {
        enabled: true
      }
    }),
    Components({
      resolvers: [ElementPlusResolver({
        importStyle: true, // 自动导入样式
        directives: true, // 自动导入指令
        version: '^2.3.0' // 指定版本
      })],
      dts: true,
      dirs: ['src/components'], // 自动导入组件目录
      extensions: ['vue'], // 组件文件扩展名
      deep: true // 深度搜索
    })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  optimizeDeps: {
    force: true,
    include: [
      'vue',
      'vue-router',
      'pinia',
      'element-plus',
      'axios',
      'dayjs'
    ],
    exclude: ['@vueuse/core']
  },
  server: {
    port: 4090,
    host: 'localhost',
    open: false,
    cors: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    // 启用压缩
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    },
    // 设置chunk大小警告限制
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/[ext]/[name]-[hash].[ext]',
        // 手动分割代码
        manualChunks: {
          // 将Vue相关库打包到一个chunk
          vue: ['vue', 'vue-router', 'pinia'],
          // 将Element Plus打包到一个chunk
          'element-plus': ['element-plus', '@element-plus/icons-vue'],
          // 将工具库打包到一个chunk
          utils: ['axios', 'dayjs', 'js-cookie', 'nprogress'],
          // 将图表库单独打包
          charts: ['echarts', 'vue-echarts'],
          // 将编辑器相关库打包到一个chunk
          editor: ['marked', 'highlight.js']
        }
      }
    }
  }
})