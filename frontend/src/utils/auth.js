import Cookies from 'js-cookie'

const TOKEN_KEY = 'blog_token'
const TOKEN_EXPIRE_KEY = 'blog_token_expire'

export function getToken() {
  return Cookies.get(TOKEN_KEY)
}

export function setToken(token) {
  // JWT token后端设置为24小时过期，前端Cookie也设置为1天过期
  const expires = 1 // 1天
  Cookies.set(TOKEN_KEY, token, { expires })
  
  // 存储token过期时间（24小时后）
  const expireTime = new Date().getTime() + 24 * 60 * 60 * 1000
  Cookies.set(TOKEN_EXPIRE_KEY, expireTime.toString(), { expires })
}

export function removeToken() {
  Cookies.remove(TOKEN_KEY)
  Cookies.remove(TOKEN_EXPIRE_KEY)
}

// 检查token是否即将过期（提前5分钟）
export function isTokenExpiringSoon() {
  const expireTime = Cookies.get(TOKEN_EXPIRE_KEY)
  if (!expireTime) return true
  
  const now = new Date().getTime()
  const expire = parseInt(expireTime)
  const fiveMinutes = 5 * 60 * 1000
  
  return (expire - now) <= fiveMinutes
}

// 检查token是否已过期
export function isTokenExpired() {
  const expireTime = Cookies.get(TOKEN_EXPIRE_KEY)
  if (!expireTime) return true
  
  const now = new Date().getTime()
  const expire = parseInt(expireTime)
  
  return now >= expire
}

// 获取token剩余时间（毫秒）
export function getTokenRemainingTime() {
  const expireTime = Cookies.get(TOKEN_EXPIRE_KEY)
  if (!expireTime) return 0
  
  const now = new Date().getTime()
  const expire = parseInt(expireTime)
  
  return Math.max(0, expire - now)
}