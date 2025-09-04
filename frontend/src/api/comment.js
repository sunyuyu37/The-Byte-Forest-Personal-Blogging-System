import request from '@/utils/request'

export const commentApi = {
  // 获取评论列表（管理员）
  getComments(params) {
    return request({
      url: '/comments/admin',
      method: 'get',
      params
    })
  },

  // 获取最近评论（公开接口）
  getRecentComments(limit = 5) {
    return request({
      url: '/comments/recent',
      method: 'get',
      params: { limit }
    })
  },

  // 获取文章评论（支持传入文章ID或slug）
  getArticleComments(articleKey, params) {
    const isId = typeof articleKey === 'number' || (typeof articleKey === 'string' && /^\d+$/.test(articleKey))
    const url = isId ? `/articles/${articleKey}/comments` : `/articles/slug/${articleKey}/comments`
    return request({
      url,
      method: 'get',
      params
    })
  },

  // 创建评论
  createComment(data) {
    return request({
      url: '/comments',
      method: 'post',
      data
    })
  },

  // 更新评论
  updateComment(id, data) {
    return request({
      url: `/comments/${id}`,
      method: 'put',
      data
    })
  },

  // 删除评论
  deleteComment(id) {
    return request({
      url: `/comments/${id}`,
      method: 'delete'
    })
  },

  // 审核评论
  approveComment(id) {
    return request({
      url: `/comments/${id}/approve`,
      method: 'put'
    })
  },

  // 拒绝评论
  rejectComment(id) {
    return request({
      url: `/comments/${id}/reject`,
      method: 'put'
    })
  },

  // 批量审核评论
  batchApprove(commentIds) {
    return request({
      url: '/comments/batch/approve',
      method: 'put',
      data: { ids: commentIds }
    })
  },

  // 批量拒绝评论
  batchReject(commentIds) {
    return request({
      url: '/comments/batch/reject',
      method: 'put',
      data: { ids: commentIds }
    })
  },

  // 批量删除评论
  batchDelete(commentIds) {
    return request({
      url: '/comments/batch',
      method: 'delete',
      data: { ids: commentIds }
    })
  },

  // ========== 评论点赞相关API ==========

  // 点赞评论
  likeComment(id) {
    return request({
      url: `/comments/${id}/like`,
      method: 'post'
    })
  },

  // 取消点赞评论
  unlikeComment(id) {
    return request({
      url: `/comments/${id}/like`,
      method: 'delete'
    })
  },

  // 检查评论是否已被当前用户点赞
  isCommentLiked(id) {
    return request({
      url: `/comments/${id}/liked`,
      method: 'get'
    })
  }
}