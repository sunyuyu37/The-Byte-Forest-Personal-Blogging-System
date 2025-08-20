import request from '@/utils/request'

// 获取安全中心概览
export function getSecurityOverview() {
  return request({
    url: '/security/overview',
    method: 'get'
  })
}

// 获取安全设置
export function getSecuritySettings() {
  return request({
    url: '/security/settings',
    method: 'get'
  })
}

// 更新安全设置
export function updateSecuritySettings(data) {
  return request({
    url: '/security/settings',
    method: 'put',
    data
  })
}

// 获取登录活动记录
export function getLoginActivities(params = {}) {
  return request({
    url: '/security/activities',
    method: 'get',
    params
  })
}

// 启用/禁用两步验证
export function toggleTwoFactor(data) {
  return request({
    url: '/security/two-factor',
    method: 'post',
    data
  })
}

// 结束指定会话
export function endSession(sessionId) {
  return request({
    url: `/security/sessions/${sessionId}`,
    method: 'delete'
  })
}

// 结束所有其他会话
export function endAllOtherSessions() {
  return request({
    url: '/security/sessions/others',
    method: 'delete'
  })
}

// 修改密码（从现有的user.js API复用）
export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

// 获取Token状态监控信息
export function getTokenStatus() {
  return request({
    url: '/security/token/status',
    method: 'get'
  })
}