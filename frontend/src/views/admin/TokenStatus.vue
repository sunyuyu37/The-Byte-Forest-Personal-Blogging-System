<template>
  <div class="token-status">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Token状态监控</span>
          <el-button type="primary" @click="refreshStatus">刷新状态</el-button>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="登录状态">
          <el-tag :type="isLoggedIn ? 'success' : 'danger'">
            {{ isLoggedIn ? '已登录' : '未登录' }}
          </el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="Token存在">
          <el-tag :type="hasToken ? 'success' : 'danger'">
            {{ hasToken ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="Token是否过期">
          <el-tag :type="tokenExpired ? 'danger' : 'success'">
            {{ tokenExpired ? '已过期' : '有效' }}
          </el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="即将过期">
          <el-tag :type="tokenExpiringSoon ? 'warning' : 'success'">
            {{ tokenExpiringSoon ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="剩余时间">
          <span>{{ remainingTimeText }}</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="用户信息">
          <span>{{ user?.username || '未获取' }}</span>
        </el-descriptions-item>
      </el-descriptions>
      
      <div class="mt-20">
        <el-alert
          v-if="tokenExpired"
          title="Token已过期"
          type="error"
          description="您的登录已过期，请重新登录"
          show-icon
        />
        <el-alert
          v-else-if="tokenExpiringSoon"
          title="Token即将过期"
          type="warning"
          :description="`您的登录将在${Math.floor(remainingTime / (1000 * 60))}分钟后过期`"
          show-icon
        />
        <el-alert
          v-else-if="hasToken"
          title="Token状态正常"
          type="success"
          description="您的登录状态正常"
          show-icon
        />
      </div>
      
      <div class="mt-20">
        <el-button @click="testApi" type="primary">测试API调用</el-button>
        <el-button @click="simulateExpiredToken" type="danger">模拟过期Token</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getToken, setToken } from '@/utils/auth'
import { userApi } from '@/api/user'
import { getTokenStatus } from '@/api/security'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const hasToken = ref(false)
const tokenExpired = ref(false)
const tokenExpiringSoon = ref(false)
const remainingTime = ref(0)

const isLoggedIn = computed(() => userStore.isLoggedIn)
const user = computed(() => userStore.user)

const remainingTimeText = computed(() => {
  if (remainingTime.value <= 0) return '已过期'
  
  const hours = Math.floor(remainingTime.value / (1000 * 60 * 60))
  const minutes = Math.floor((remainingTime.value % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((remainingTime.value % (1000 * 60)) / 1000)
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  } else if (minutes > 0) {
    return `${minutes}分钟${seconds}秒`
  } else {
    return `${seconds}秒`
  }
})

let updateInterval = null

const refreshStatus = async () => {
  const token = getToken()
  hasToken.value = !!token

  try {
    const res = await getTokenStatus()
    const data = res?.data || res // 兼容不同封装
    // 后端Result封装：{ code, message, data }
    const payload = data?.data !== undefined ? data.data : data

    if (payload) {
      tokenExpired.value = !!(payload.isTokenExpired ?? payload.tokenExpired)
      tokenExpiringSoon.value = !!(payload.isTokenExpiringSoon ?? payload.tokenExpiringSoon)
      remainingTime.value = Number(payload.remainingTimeMillis ?? payload.remainingMillis ?? 0)
    } else {
      tokenExpired.value = true
      tokenExpiringSoon.value = true
      remainingTime.value = 0
    }
  } catch (e) {
    // 请求失败时，保守处理为过期
    tokenExpired.value = true
    tokenExpiringSoon.value = true
    remainingTime.value = 0
  }
}

const testApi = async () => {
  try {
    const response = await userApi.getCurrentUser()
    ElMessage.success('API调用成功')
    console.log('API响应:', response)
  } catch (error) {
    ElMessage.error('API调用失败: ' + error.message)
    console.error('API错误:', error)
  }
}

const simulateExpiredToken = () => {
  // 设置一个已过期的token进行测试
  const expiredToken = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzU0ODIxOTgzLCJleHAiOjE3NTQ4MjE5ODN9.expired'
  setToken(expiredToken)
  refreshStatus()
  ElMessage.warning('已设置过期Token，请测试系统行为')
}

onMounted(() => {
  refreshStatus()
  // 每30秒更新一次状态，避免频繁请求API
  updateInterval = setInterval(refreshStatus, 30000)
})

onUnmounted(() => {
  if (updateInterval) {
    clearInterval(updateInterval)
  }
})
</script>

<style scoped>
.token-status {
  min-height: calc(100vh - 140px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-20 {
  margin-top: 20px;
}
</style>