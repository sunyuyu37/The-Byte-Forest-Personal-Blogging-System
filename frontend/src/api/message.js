import request from '@/utils/request'

// 留言管理API
export const messageApi = {
  // 获取留言列表（管理员）
  getMessagesForAdmin(params) {
    return request({
      url: '/comments/admin',
      method: 'get',
      params: {
        ...params,
        messageOnly: true // 只获取留言（article_id为null的评论）
      }
    })
  },

  // 审核通过留言
  approveMessage(id) {
    return request({
      url: `/comments/${id}/approve`,
      method: 'put'
    })
  },

  // 拒绝留言
  rejectMessage(id) {
    return request({
      url: `/comments/${id}/reject`,
      method: 'put'
    })
  },

  // 删除留言
  deleteMessage(id) {
    return request({
      url: `/comments/${id}`,
      method: 'delete'
    })
  },

  // 回复留言
  replyToMessage(data) {
    return request({
      url: '/comments',
      method: 'post',
      data: {
        ...data,
        articleId: null // 留言回复也是留言
      }
    })
  },

  // 批量审核通过
  batchApproveMessages(ids) {
    return request({
      url: '/comments/batch/approve',
      method: 'put',
      data: { ids }
    })
  },

  // 批量拒绝
  batchRejectMessages(ids) {
    return request({
      url: '/comments/batch/reject',
      method: 'put',
      data: { ids }
    })
  },

  // 批量删除
  batchDeleteMessages(ids) {
    return request({
      url: '/comments/batch',
      method: 'delete',
      data: { ids }
    })
  },

  // 获取留言统计信息
  getMessageStats() {
    return request({
      url: '/comments/messages/stats',
      method: 'get'
    })
  },

  // 获取最新留言
  getRecentMessages(limit = 5) {
    return request({
      url: '/comments/messages/recent',
      method: 'get',
      params: { limit }
    })
  },

  // 导出留言数据
  exportMessages(params) {
    return request({
      url: '/comments/messages/export',
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  // 获取留言详情
  getMessageDetail(id) {
    return request({
      url: `/comments/messages/${id}/detail`,
      method: 'get'
    })
  },

  // 回复管理相关API
  // 审核通过回复
  approveReply(id) {
    return request({
      url: `/comments/${id}/approve`,
      method: 'put'
    })
  },

  // 拒绝回复
  rejectReply(id) {
    return request({
      url: `/comments/${id}/reject`,
      method: 'put'
    })
  },

  // 更新回复
  updateReply(id, data) {
    return request({
      url: `/comments/${id}`,
      method: 'put',
      data
    })
  },

  // 删除回复
  deleteReply(id) {
    return request({
      url: `/comments/${id}`,
      method: 'delete'
    })
  },

  // 获取留言趋势统计
  getMessageTrend(days = 7) {
    return request({
      url: '/comments/messages/trend',
      method: 'get',
      params: { days }
    })
  }
}

export default messageApi