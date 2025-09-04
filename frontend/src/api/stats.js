import request from '@/utils/request'

export const statsApi = {
  // 获取仪表盘统计数据
  getDashboardStats() {
    return request({
      url: '/stats/dashboard',
      method: 'get'
    })
  },

  // 获取最新文章列表
  getRecentArticles(limit = 5) {
    return request({
      url: '/stats/recent-articles',
      method: 'get',
      params: { limit }
    })
  },

  // 获取最新评论列表
  getRecentComments(limit = 5) {
    return request({
      url: '/stats/recent-comments',
      method: 'get',
      params: { limit }
    })
  },

  // ========== 图表数据API ==========
  
  // 获取文章发布趋势数据
  getArticleTrendData(period = 'month') {
    return request({
      url: '/stats/chart/article-trend',
      method: 'get',
      params: { period }
    })
  },

  // 获取文章分类统计数据
  getArticleCategoryStats() {
    return request({
      url: '/stats/chart/article-category',
      method: 'get'
    })
  },

  // 获取文章访问量趋势数据
  getArticleViewTrendData(period = 'month') {
    return request({
      url: '/stats/chart/article-view-trend',
      method: 'get',
      params: { period }
    })
  },

  // 获取热门文章数据
  getPopularArticlesData(limit = 10) {
    return request({
      url: '/stats/chart/popular-articles',
      method: 'get',
      params: { limit }
    })
  },

  // 获取评论趋势数据
  getCommentTrendData(period = 'month') {
    return request({
      url: '/stats/chart/comment-trend',
      method: 'get',
      params: { period }
    })
  },

  // 获取用户注册趋势数据
  getUserRegistrationTrendData(period = 'month') {
    return request({
      url: '/stats/chart/user-registration-trend',
      method: 'get',
      params: { period }
    })
  },

  // 获取评论词云数据
  getCommentWordCloudData(limit = 100) {
    return request({
      url: '/stats/chart/comment-wordcloud',
      method: 'get',
      params: { limit }
    })
  },

  // ========== 兼容性API（保留原有接口） ==========
  
  // 获取数据趋势图表数据（兼容性接口）
  getTrendData(period = 'week') {
    // 组合多个数据源
    return Promise.all([
      this.getArticleTrendData(period),
      this.getArticleViewTrendData(period),
      this.getCommentTrendData(period)
    ]).then(([articleData, viewData, commentData]) => {
      // 处理和组合数据
      return {
        code: 200,
        data: {
          dates: articleData.data?.map(item => item[0]) || [],
          articles: articleData.data?.map(item => item[1]) || [],
          views: viewData.data?.map(item => item[1]) || [],
          comments: commentData.data?.map(item => item[1]) || []
        }
      }
    }).catch(error => {
      console.error('获取趋势数据失败:', error)
      return { code: 500, message: '获取趋势数据失败' }
    })
  },

  // 获取分类统计数据（兼容性接口）
  getCategoryStats() {
    return this.getArticleCategoryStats().then(response => {
      if (response.code === 200 && response.data) {
        return {
          code: 200,
          data: {
            categories: response.data.map(item => item[0]),
            articles: response.data.map(item => item[1]),
            views: response.data.map(item => item[2] || 0)
          }
        }
      }
      return response
    })
  },

  // 获取访问量统计数据
  getVisitStats(period = 'today') {
    return request({
      url: '/stats/chart/visit-stats',
      method: 'get',
      params: { period }
    })
  },

  // 获取文章阅读排行（兼容性接口）
  getPopularArticles(limit = 10) {
    return this.getPopularArticlesData(limit).then(response => {
      if (response.code === 200 && response.data) {
        return {
          code: 200,
          data: response.data.map((item, index) => ({
            rank: index + 1,
            title: item[0],
            views: item[1],
            comments: item[2] || 0,
            likes: item[3] || 0
          }))
        }
      }
      return response
    })
  }
}