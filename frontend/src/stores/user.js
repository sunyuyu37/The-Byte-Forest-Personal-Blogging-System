import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import { userApi } from '@/api/user'
import { getToken, setToken, removeToken, isTokenExpired, isTokenExpiringSoon } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  // 在store初始化时就从Cookie中获取token
  const token = ref(getToken())
  const isInitializing = ref(false)
  const hasInitialized = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // 初始化用户信息
  const initUser = async () => {
    if (isInitializing.value) {
      // 如果正在初始化，等待完成
      return new Promise((resolve) => {
        const checkInterval = setInterval(() => {
          if (!isInitializing.value) {
            clearInterval(checkInterval)
            resolve()
          }
        }, 100)
      })
    }
    
    isInitializing.value = true
    
    try {
      // 如果没有token，直接返回
      if (!token.value) {
        hasInitialized.value = true
        return
      }
      
      // 检查token是否过期
      if (isTokenExpired()) {
        console.log('Token已过期，清除登录状态')
        logout()
        hasInitialized.value = true
        return
      }
      
      // 如果已经有用户信息且token未过期，不需要重新获取
      if (user.value && !isTokenExpiringSoon()) {
        hasInitialized.value = true
        return
      }
      
      // 获取用户信息
      const response = await userApi.getCurrentUser()
      if (response.code === 200) {
        user.value = response.data
        console.log('用户信息初始化成功:', user.value)
      } else {
        console.warn('获取用户信息失败:', response.message)
        // 只有在明确的认证错误时才登出
        if (response.code === 401) {
          logout()
        }
      }
    } catch (error) {
      console.error('初始化用户信息失败:', error)
      // 只有在明确的认证错误时才登出，避免网络错误导致的误登出
      if (error.response?.status === 401 || error.message === 'Token已过期') {
        logout()
      } else {
        // 其他错误（如网络错误）不清除登录状态，但标记初始化完成
        console.warn('用户信息初始化遇到网络错误，保持登录状态')
      }
    } finally {
      isInitializing.value = false
      hasInitialized.value = true
    }
  }

  // 登录
  const login = async (credentials) => {
    try {
      const response = await authApi.login(credentials)
      const newToken = response.data.token
      
      token.value = newToken
      setToken(newToken)
      
      // 登录成功后获取用户信息
      await initUser()
      
      return response
    } catch (error) {
      throw error
    }
  }

  // 注册
  const register = async (userData) => {
    try {
      const response = await authApi.register(userData)
      return response
    } catch (error) {
      throw error
    }
  }

  // 登出
  const logout = () => {
    token.value = null
    user.value = null
    hasInitialized.value = false
    isInitializing.value = false
    removeToken()
  }

  // 更新用户信息
  const updateUser = (userData) => {
    user.value = { ...user.value, ...userData }
  }

  return {
    user,
    token,
    isLoggedIn,
    isAdmin,
    initUser,
    login,
    register,
    logout,
    updateUser
  }
})