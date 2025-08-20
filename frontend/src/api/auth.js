import request from '@/utils/request'

export const authApi = {
  // 用户登录
  login(data) {
    return request({
      url: '/auth/login',
      method: 'post',
      data
    })
  },

  // 用户注册
  register(data) {
    return request({
      url: '/auth/register',
      method: 'post',
      data
    })
  },

  // 检查用户名是否可用
  checkUsername(username) {
    return request({
      url: `/auth/check-username?username=${username}`,
      method: 'get'
    })
  },

  // 检查邮箱是否可用
  checkEmail(email) {
    return request({
      url: `/auth/check-email?email=${email}`,
      method: 'get'
    })
  },

  // 发送验证邮件
  sendVerificationEmail(email) {
    return request({
      url: `/auth/send-verification-email?email=${email}`,
      method: 'post'
    })
  },

  // 验证邮箱
  verifyEmail(data) {
    return request({
      url: '/auth/verify-email',
      method: 'post',
      data
    })
  },

  // 发送重置密码邮件
  sendResetPasswordEmail(email) {
    return request({
      url: `/auth/reset-password?email=${email}`,
      method: 'post'
    })
  },

  // 确认重置密码
  confirmResetPassword(data) {
    return request({
      url: '/auth/confirm-reset-password',
      method: 'post',
      data
    })
  }
}