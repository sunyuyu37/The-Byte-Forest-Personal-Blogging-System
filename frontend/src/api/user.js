import request from '@/utils/request'

export const userApi = {
  // 获取当前用户信息
  getCurrentUser() {
    return request({
      url: '/users/me',
      method: 'get'
    })
  },

  // 更新用户信息
  updateUser(id, data) {
    return request({
      url: `/users/${id}`,
      method: 'put',
      data
    })
  },

  // 更新用户头像
  updateAvatar(id, avatar) {
    return request({
      url: `/users/${id}/avatar`,
      method: 'put',
      data: { avatar }
    })
  },

  // 修改密码
  updatePassword(id, data) {
    return request({
      url: `/users/${id}/password`,
      method: 'put',
      data
    })
  },

  // 获取用户详情
  getUser(id) {
    return request({
      url: `/users/${id}`,
      method: 'get'
    })
  },

  // 管理员功能
  // 获取用户列表（管理员）
  getUsers(params) {
    return request({
      url: '/users',
      method: 'get',
      params
    })
  },

  // 创建用户（管理员）
  createUser(data) {
    return request({
      url: '/users',
      method: 'post',
      data
    })
  },

  // 删除用户（管理员）
  deleteUser(id) {
    return request({
      url: `/users/${id}`,
      method: 'delete'
    })
  },

  // 重置用户密码（管理员）
  resetPassword(id) {
    return request({
      url: `/users/${id}/reset-password`,
      method: 'put'
    })
  },

  // 批量启用用户（管理员）
  batchEnable(userIds) {
    return request({
      url: '/users/batch',
      method: 'post',
      data: { 
        operation: 'enable',
        userIds 
      }
    })
  },

  // 批量禁用用户（管理员）
  batchDisable(userIds) {
    return request({
      url: '/users/batch',
      method: 'post',
      data: { 
        operation: 'disable',
        userIds 
      }
    })
  },

  // 批量删除用户（管理员）
  batchDelete(userIds) {
    return request({
      url: '/users/batch',
      method: 'post',
      data: { 
        operation: 'delete',
        userIds 
      }
    })
  },

  // 更新用户（管理员）
  updateUserByAdmin(id, data) {
    return request({
      url: `/users/admin/${id}`,
      method: 'put',
      data
    })
  },

  // 切换用户状态（管理员）
  toggleUserStatus(id) {
    return request({
      url: `/users/${id}/status`,
      method: 'put'
    })
  }
}