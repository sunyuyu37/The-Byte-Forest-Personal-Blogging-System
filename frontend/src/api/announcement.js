import request from '@/utils/request'

const announcementApi = {
  // ========== 管理员公告管理API ==========
  
  // 创建公告
  createAnnouncement(data) {
    return request({
      url: '/admin/announcements',
      method: 'post',
      data
    })
  },

  // 更新公告
  updateAnnouncement(id, data) {
    return request({
      url: `/admin/announcements/${id}`,
      method: 'put',
      data
    })
  },

  // 删除公告
  deleteAnnouncement(id) {
    return request({
      url: `/admin/announcements/${id}`,
      method: 'delete'
    })
  },

  // 获取公告详情（管理员）
  getAdminAnnouncementById(id) {
    return request({
      url: `/admin/announcements/${id}`,
      method: 'get'
    })
  },

  // 分页获取所有公告（管理员）
  getAdminAnnouncements(params) {
    return request({
      url: '/admin/announcements',
      method: 'get',
      params
    })
  },

  // 搜索公告（管理员）
  searchAdminAnnouncements(params) {
    return request({
      url: '/admin/announcements/search',
      method: 'get',
      params
    })
  },

  // 根据类型获取公告（管理员）
  getAdminAnnouncementsByType(type) {
    return request({
      url: `/admin/announcements/type/${type}`,
      method: 'get'
    })
  },

  // 启用/禁用公告
  toggleStatus(id, enabled) {
    return request({
      url: `/admin/announcements/${id}/status`,
      method: 'patch',
      params: { enabled }
    })
  },

  // 置顶/取消置顶公告
  togglePin(id, pinned) {
    return request({
      url: `/admin/announcements/${id}/pin`,
      method: 'patch',
      params: { pinned }
    })
  },

  // 获取公告统计信息
  getAnnouncementStats() {
    return request({
      url: '/admin/announcements/stats',
      method: 'get'
    })
  },

  // 获取公告已读统计
  getReadStats(id) {
    return request({
      url: `/admin/announcements/${id}/read-stats`,
      method: 'get'
    })
  },

  // ========== 用户公告API ==========
  
  // 获取当前活跃的公告
  getActiveAnnouncements() {
    return request({
      url: '/announcements/active',
      method: 'get'
    })
  },

  // 获取用户未读公告
  getUnreadAnnouncements() {
    return request({
      url: '/announcements/unread',
      method: 'get'
    })
  },

  // 标记公告为已读
  markAsRead(id) {
    return request({
      url: `/announcements/${id}/read`,
      method: 'post'
    })
  },

  // 批量标记公告为已读
  markBatchAsRead(announcementIds) {
    return request({
      url: '/announcements/read-batch',
      method: 'post',
      data: announcementIds
    })
  },

  // 获取公告详情（用户）
  getAnnouncementById(id) {
    return request({
      url: `/announcements/${id}`,
      method: 'get'
    })
  },

  // 根据类型获取公告（用户）
  getAnnouncementsByType(type) {
    return request({
      url: `/announcements/type/${type}`,
      method: 'get'
    })
  },

  // 获取用户未读公告数量
  getUnreadCount() {
    return request({
      url: '/announcements/unread/count',
      method: 'get'
    })
  },

  // 获取重要公告（高优先级且置顶）
  getImportantAnnouncements() {
    return request({
      url: '/announcements/important',
      method: 'get'
    })
  }
}

export default announcementApi