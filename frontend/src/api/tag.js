import request from '@/utils/request'

// 标签API
const tagApi = {
  // 获取标签列表（分页）
  getTags(params = {}) {
    return request({
      url: '/admin/tags',
      method: 'get',
      params: {
        page: params.page || 0,
        size: params.size || 10,
        sortBy: params.sortBy || 'createdAt',
        sortDir: params.sortDir || 'desc',
        keyword: params.keyword
      }
    })
  },

  // 获取所有标签（不分页）
  getAllTags() {
    return request({
      url: '/admin/tags/all',
      method: 'get'
    })
  },

  // 根据ID获取标签
  getTagById(id) {
    return request({
      url: `/admin/tags/${id}`,
      method: 'get'
    })
  },

  // 创建标签
  createTag(data) {
    return request({
      url: '/admin/tags',
      method: 'post',
      data
    })
  },

  // 更新标签
  updateTag(id, data) {
    return request({
      url: `/admin/tags/${id}`,
      method: 'put',
      data
    })
  },

  // 删除标签
  deleteTag(id) {
    return request({
      url: `/admin/tags/${id}`,
      method: 'delete'
    })
  },

  // 批量删除标签
  deleteTags(ids) {
    return request({
      url: '/admin/tags/batch',
      method: 'delete',
      data: { ids }
    })
  },

  // 获取热门标签
  getPopularTags(limit = 10) {
    return request({
      url: '/tags/popular',
      method: 'get',
      params: { limit }
    })
  },

  // 获取所有标签（公开）
  getPublicAllTags() {
    return request({
      url: '/tags',
      method: 'get'
    })
  },

  // 获取标签统计
  getTagStats() {
    return request({
      url: '/admin/tags/stats',
      method: 'get'
    })
  }
}

export default tagApi

// 导出具体方法
export const {
  getTags,
  getAllTags,
  getTagById,
  createTag,
  updateTag,
  deleteTag,
  deleteTags,
  getPopularTags,
  getTagStats
} = tagApi

// 单独导出公开接口（避免命名冲突）
export const getPublicAllTags = tagApi.getPublicAllTags