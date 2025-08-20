import axios from 'axios'
import { ElMessage, ElNotification } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getToken, isTokenExpired, isTokenExpiringSoon, getTokenRemainingTime } from '@/utils/auth'
import apiCache from '@/utils/cache'

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 通过 Vite 代理转发到后端，并与后端 context-path /api 对齐
  timeout: 300000, // 5分钟超时，支持大文件上传
})

// 用于防止重复的token过期检查
let lastTokenCheck = 0
const TOKEN_CHECK_INTERVAL = 30 * 1000 // 30秒内不重复检查

// 请求拦截器
request.interceptors.request.use(
  async config => {
    // 检查缓存（仅对GET请求）
    if (config.method && config.method.toLowerCase() === 'get' && config.cache !== false) {
      const cacheKey = apiCache.generateKey(config.url, config.params)
      const cachedData = apiCache.get(cacheKey)
      if (cachedData) {
        // 直接返回缓存数据，模拟axios响应
        throw {
          isAxiosError: false,
          isCacheResponse: true,
          data: cachedData,
          status: 200,
          statusText: 'OK',
          headers: {},
          config
        }
      }
      // 将缓存键添加到配置中，供响应拦截器使用
      config.cacheKey = cacheKey
    }

    const token = getToken()
    if (token) {
      const now = Date.now()
      
      // 只在间隔时间后才检查token状态，避免每个请求都检查
      if (now - lastTokenCheck > TOKEN_CHECK_INTERVAL) {
        lastTokenCheck = now
        
        // 检查token是否已过期
        if (isTokenExpired()) {
          const userStore = useUserStore()
          userStore.logout()
          ElMessage.warning('登录已过期，请重新登录')
          import('@/router').then(({ default: router }) => {
            router.push('/auth/login')
          })
          return Promise.reject(new Error('Token已过期'))
        }
      }
      
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 用于防止重复显示登录过期提示
let isLoggingOut = false

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，则显示错误信息
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401表示未授权，需要重新登录
      if (res.code === 401) {
        handleUnauthorized('登录已过期，请重新登录')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    // 缓存GET请求的成功响应
    if (response.config && response.config.method && response.config.method.toLowerCase() === 'get' && response.config.cache !== false && response.config.cacheKey) {
      const cacheTTL = response.config.cacheTTL || apiCache.defaultTTL
      apiCache.set(response.config.cacheKey, res, cacheTTL)
    }
    
    return res
  },
  error => {
    // 处理缓存响应
    if (error.isCacheResponse) {
      return error.data
    }
    
    console.error('响应错误:', error)
    
    let message = '网络错误'
    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 400:
          message = data.message || '请求参数错误'
          break
        case 401:
          // 401错误可能是token过期或无效，统一处理
          handleUnauthorized('登录状态已失效，请重新登录')
          return Promise.reject(error)
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = data?.message || `连接错误${status}`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    } else if (error.message === 'Token已过期') {
      // 这是在请求拦截器中抛出的错误，不需要再次处理
      return Promise.reject(error)
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 统一处理未授权错误
function handleUnauthorized(message) {
  if (isLoggingOut) return
  
  isLoggingOut = true
  const userStore = useUserStore()
  userStore.logout()
  
  ElMessage.warning(message)
  
  // 使用Vue Router进行导航，避免页面刷新
  import('@/router').then(({ default: router }) => {
    router.push('/auth/login')
    // 重置标志，允许下次登录
    setTimeout(() => {
      isLoggingOut = false
    }, 1000)
  })
}

export default request