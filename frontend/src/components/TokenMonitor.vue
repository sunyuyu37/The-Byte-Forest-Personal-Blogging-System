<template>
  <!-- 这是一个无UI的监控组件 -->
</template>

<script setup>
import { onMounted, onUnmounted, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { isTokenExpired, isTokenExpiringSoon, getTokenRemainingTime } from '@/utils/auth'
import { ElNotification } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()
let checkInterval = null
let hasShownExpiringWarning = false
let hasShownFinalWarning = false
let isHandlingLogout = false

// 检查token状态
const checkTokenStatus = () => {
  // 如果用户未登录或正在处理登出，跳过检查
  if (!userStore.isLoggedIn || isHandlingLogout) {
    return
  }

  // 检查是否已过期
  if (isTokenExpired()) {
    handleTokenExpired()
    return
  }

  // 检查是否即将过期
  if (isTokenExpiringSoon()) {
    const remainingTime = getTokenRemainingTime()
    const minutes = Math.floor(remainingTime / (1000 * 60))
    
    // 最后1分钟警告
    if (minutes <= 1 && !hasShownFinalWarning) {
      hasShownFinalWarning = true
      ElNotification({
        title: '登录即将过期',
        message: '您的登录将在1分钟内过期，请立即保存工作并重新登录！',
        type: 'error',
        duration: 0, // 不自动关闭
        showClose: true
      })
    }
    // 5分钟内警告
    else if (minutes <= 5 && !hasShownExpiringWarning) {
      hasShownExpiringWarning = true
      ElNotification({
        title: '登录即将过期',
        message: `您的登录将在${minutes}分钟后过期，请及时保存工作`,
        type: 'warning',
        duration: 10000
      })
    }
  } else {
    // 重置警告标志
    hasShownExpiringWarning = false
    hasShownFinalWarning = false
  }
}

// 处理token过期
const handleTokenExpired = () => {
  if (isHandlingLogout) return
  
  isHandlingLogout = true
  console.log('Token已过期，自动登出')
  
  // 清除定时器
  if (checkInterval) {
    clearInterval(checkInterval)
    checkInterval = null
  }
  
  userStore.logout()
  
  // 只在管理后台页面时显示通知并跳转
  if (router.currentRoute.value.path.startsWith('/admin')) {
    ElNotification({
      title: '登录已过期',
      message: '您的登录已过期，即将跳转到登录页面',
      type: 'warning',
      duration: 3000
    })
    
    // 延迟跳转，避免闪退
    setTimeout(() => {
      router.push('/auth/login')
      isHandlingLogout = false
    }, 1000)
  } else {
    isHandlingLogout = false
  }
}

// 监听登录状态变化
watch(() => userStore.isLoggedIn, (newValue) => {
  if (newValue) {
    // 用户登录时重置状态并开始监控
    hasShownExpiringWarning = false
    hasShownFinalWarning = false
    isHandlingLogout = false
    
    if (!checkInterval) {
      // 增加检查间隔到60秒，减少频率
      checkInterval = setInterval(checkTokenStatus, 60 * 1000)
    }
  } else {
    // 用户登出时清除定时器
    if (checkInterval) {
      clearInterval(checkInterval)
      checkInterval = null
    }
  }
})

onMounted(() => {
  // 只有在用户已登录时才开始监控
  if (userStore.isLoggedIn) {
    // 增加检查间隔到60秒，减少频率
    checkInterval = setInterval(checkTokenStatus, 60 * 1000)
    
    // 延迟首次检查，避免页面加载时的冲突
    setTimeout(checkTokenStatus, 2000)
  }
})

onUnmounted(() => {
  if (checkInterval) {
    clearInterval(checkInterval)
  }
})
</script>