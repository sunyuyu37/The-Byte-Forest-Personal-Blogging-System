import request from '@/utils/request'
import { CACHE_CONFIG } from '@/utils/cache'

export const articleApi = {
  // 获取已发布的文章列表
  getPublishedArticles(params) {
    return request({
      url: '/articles/published',
      method: 'get',
      params,
      cacheTTL: CACHE_CONFIG.ARTICLES
    })
  },

  // 根据分类获取文章
  getArticlesByCategory(categorySlug, params) {
    return request({
      url: `/articles/category/${categorySlug}`,
      method: 'get',
      params
    })
  },

  // 根据标签获取文章
  getArticlesByTag(tagSlug, params) {
    return request({
      url: `/articles/tag/${tagSlug}`,
      method: 'get',
      params
    })
  },

  // 获取置顶文章
  getTopArticles() {
    return request({
      url: '/articles/top',
      method: 'get',
      cacheTTL: CACHE_CONFIG.TOP_ARTICLES
    })
  },

  // 获取推荐文章
  getFeaturedArticles(params) {
    return request({
      url: '/articles/featured',
      method: 'get',
      params,
      cacheTTL: CACHE_CONFIG.FEATURED_ARTICLES
    })
  },

  // 获取最受欢迎的文章
  getPopularArticles(params) {
    return request({
      url: '/articles/popular',
      method: 'get',
      params
    })
  },

  // 根据ID获取文章详情
  getArticleById(id) {
    return request({
      url: `/articles/${id}`,
      method: 'get',
      cacheTTL: CACHE_CONFIG.ARTICLE_DETAIL
    })
  },

  // 根据slug获取文章详情
  getArticleBySlug(slug) {
    return request({
      url: `/articles/slug/${slug}`,
      method: 'get',
      cacheTTL: CACHE_CONFIG.ARTICLE_DETAIL
    })
  },

  // 搜索文章
  searchArticles(params) {
    return request({
      url: '/articles/search',
      method: 'get',
      params
    })
  },

  // 获取热门搜索关键词
  getHotKeywords() {
    return request({
      url: '/search/hot-keywords',
      method: 'get',
      cacheTTL: CACHE_CONFIG.HOT_KEYWORDS
    })
  },

  // 获取搜索建议
  getSearchSuggestions(keyword) {
    return request({
      url: '/search/suggestions',
      method: 'get',
      params: { keyword }
    })
  },

  // ========== 管理员文章管理API ==========
  
  // 获取所有文章（管理员）
  getAdminArticles(params) {
    return request({
      url: '/admin/articles',
      method: 'get',
      params
    })
  },

  // 获取文章详情（管理员）
  getAdminArticleById(id) {
    return request({
      url: `/admin/articles/${id}`,
      method: 'get'
    })
  },

  // 创建文章
  createArticle(data) {
    return request({
      url: '/admin/articles',
      method: 'post',
      data
    })
  },

  // 更新文章
  updateArticle(id, data) {
    return request({
      url: `/admin/articles/${id}`,
      method: 'put',
      data
    })
  },

  // 删除文章
  deleteArticle(id) {
    return request({
      url: `/admin/articles/${id}`,
      method: 'delete'
    })
  },

  // 批量删除文章
  batchDeleteArticles(ids) {
    return request({
      url: '/admin/articles/batch',
      method: 'delete',
      data: ids
    })
  },

  // 发布文章
  publishArticle(id) {
    return request({
      url: `/admin/articles/${id}/publish`,
      method: 'post'
    })
  },

  // 下线文章
  unpublishArticle(id) {
    return request({
      url: `/admin/articles/${id}/unpublish`,
      method: 'post'
    })
  },

  // 批量发布文章
  batchPublishArticles(ids) {
    return request({
      url: '/admin/articles/batch/publish',
      method: 'post',
      data: ids
    })
  },

  // 批量下线文章
  batchUnpublishArticles(ids) {
    return request({
      url: '/admin/articles/batch/unpublish',
      method: 'post',
      data: ids
    })
  },

  // 置顶文章
  topArticle(id) {
    return request({
      url: `/admin/articles/${id}/top`,
      method: 'post'
    })
  },

  // 取消置顶文章
  untopArticle(id) {
    return request({
      url: `/admin/articles/${id}/untop`,
      method: 'post'
    })
  },

  // 推荐文章
  featureArticle(id) {
    return request({
      url: `/admin/articles/${id}/feature`,
      method: 'post'
    })
  },

  // 取消推荐文章
  unfeatureArticle(id) {
    return request({
      url: `/admin/articles/${id}/unfeature`,
      method: 'post'
    })
  },

  // ========== 文章点赞相关API ==========
  
  // 点赞文章
  likeArticle(id) {
    return request({
      url: `/articles/${id}/like`,
      method: 'post'
    })
  },

  // 取消点赞文章
  unlikeArticle(id) {
    return request({
      url: `/articles/${id}/like`,
      method: 'delete'
    })
  },

  // 检查文章是否已被当前用户点赞
  isArticleLiked(id) {
    return request({
      url: `/articles/${id}/liked`,
      method: 'get'
    })
  }
}

// 导出单独的函数以支持 import * as articleApi 语法
export const getAdminArticles = articleApi.getAdminArticles
export const getAdminArticle = articleApi.getAdminArticleById
export const createAdminArticle = articleApi.createArticle
export const updateAdminArticle = articleApi.updateArticle
export const deleteArticle = articleApi.deleteArticle
export const batchDeleteArticles = articleApi.batchDeleteArticles
export const publishArticle = articleApi.publishArticle
export const unpublishArticle = articleApi.unpublishArticle
export const batchPublishArticles = articleApi.batchPublishArticles
export const batchUnpublishArticles = articleApi.batchUnpublishArticles
export const topArticle = articleApi.topArticle
export const untopArticle = articleApi.untopArticle
export const featureArticle = articleApi.featureArticle
export const unfeatureArticle = articleApi.unfeatureArticle

// 默认导出
export default articleApi