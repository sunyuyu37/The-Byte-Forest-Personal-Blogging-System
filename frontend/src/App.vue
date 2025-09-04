<template>
  <div id="app">
    <router-view v-slot="{ Component, route }">
    <transition :name="getTransitionName(route)" mode="out-in">
      <component :is="Component" :key="route.path" />
    </transition>
  </router-view>
    <!-- Token监控组件 -->
    <TokenMonitor />
  </div>
</template>

<script setup>
import { onMounted, onErrorCaptured } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import TokenMonitor from '@/components/TokenMonitor.vue'

// 根据路由动态返回过渡名称
const getTransitionName = (route) => {
  // 认证页面使用淡入淡出
  if (route.path.startsWith('/auth')) {
    return 'fade'
  }
  // 管理后台使用垂直滑动
  if (route.path.startsWith('/admin')) {
    return 'slide-vertical'
  }
  // 文章详情页使用缩放效果
  if (route.name === 'ArticleDetail' || route.name === 'Profile') {
    return 'zoom'
  }
  // 404页面使用弹跳效果
  if (route.name === 'NotFound') {
    return 'bounce'
  }
  // 前台页面使用水平滑动
  return 'slide-horizontal'
}

onMounted(async () => {
  try {
    // 在组件挂载后获取 userStore，确保 Pinia 已完全初始化
    const userStore = useUserStore()
    // 初始化用户信息，不阻塞应用启动
    userStore.initUser().catch(error => {
      console.warn('用户信息初始化失败:', error)
    })
  } catch (error) {
    console.warn('App初始化失败:', error)
  }
})

// 捕获Vue组件错误
onErrorCaptured((error, instance, info) => {
  console.error('Vue组件错误:', error, info)
  ElMessage.error('页面出现错误，请刷新重试')
  return false
})
</script>

<style lang="scss">
#app {
  min-height: 100vh;
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  line-height: 1.6;
}

a {
  text-decoration: none;
  color: inherit;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.text-center {
  text-align: center;
}

.text-right {
  text-align: right;
}

.mb-20 {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

/* 页面切换过渡动画 */
.page-enter-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  transform: translateZ(0); /* 启用硬件加速 */
}

.page-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.6, 1);
  transform: translateZ(0); /* 启用硬件加速 */
}

.page-enter-from {
  opacity: 0;
  transform: translateX(20px) scale(0.98) translateZ(0);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(-20px) scale(1.02) translateZ(0);
}

.page-enter-to,
.page-leave-from {
  opacity: 1;
  transform: translateX(0) scale(1) translateZ(0);
}

/* 为页面内容添加平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 优化渲染性能 */
.main-content,
.main {
  will-change: transform;
  backface-visibility: hidden;
  perspective: 1000px;
}

/* 淡入淡出动画 - 认证页面 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 水平滑动动画 - 前台页面 */
.slide-horizontal-enter-active,
.slide-horizontal-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.slide-horizontal-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.slide-horizontal-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* 垂直滑动动画 - 管理后台 */
.slide-vertical-enter-active,
.slide-vertical-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.slide-vertical-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.slide-vertical-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

/* 缩放动画 - 文章详情页 */
.zoom-enter-active,
.zoom-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.zoom-enter-from {
  opacity: 0;
  transform: scale(0.95);
}

.zoom-leave-to {
  opacity: 0;
  transform: scale(1.05);
}

/* 弹跳动画 - 404页面 */
.bounce-enter-active {
  animation: bounce-in 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.bounce-leave-active {
  animation: bounce-out 0.4s ease-in;
}

@keyframes bounce-in {
  0% {
    opacity: 0;
    transform: scale(0.3) translateY(-50px);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05) translateY(0);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes bounce-out {
  0% {
    opacity: 1;
    transform: scale(1);
  }
  100% {
    opacity: 0;
    transform: scale(0.8) translateY(20px);
  }
}
</style>