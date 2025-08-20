import request from '@/utils/request'

// 获取用户通知列表
export function getNotifications(params) {
  return request({
    url: '/notifications',
    method: 'get',
    params
  })
}

// 获取最新通知
export function getLatestNotifications(limit = 5) {
  return request({
    url: '/notifications/latest',
    method: 'get',
    params: { limit }
  })
}

// 获取未读通知数量
export function getUnreadCount() {
  return request({
    url: '/notifications/unread-count',
    method: 'get'
  })
}

// 标记通知为已读
export function markAsRead(id) {
  return request({
    url: `/notifications/${id}/read`,
    method: 'put'
  })
}

// 标记所有通知为已读
export function markAllAsRead() {
  return request({
    url: '/notifications/read-all',
    method: 'put'
  })
}

// 删除通知
export function deleteNotification(id) {
  return request({
    url: `/notifications/${id}`,
    method: 'delete'
  })
}

// 清空所有通知
export function clearAllNotifications() {
  return request({
    url: '/notifications/clear',
    method: 'delete'
  })
}

// 创建系统通知（管理员功能）
export function createSystemNotification(data) {
  return request({
    url: '/notifications/system',
    method: 'post',
    data
  })
}