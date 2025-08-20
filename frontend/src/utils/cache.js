// API缓存工具
class ApiCache {
  constructor() {
    this.cache = new Map()
    this.timeouts = new Map()
    this.defaultTTL = 5 * 60 * 1000 // 默认5分钟过期
  }

  // 生成缓存键
  generateKey(url, params = {}) {
    const sortedParams = Object.keys(params)
      .sort()
      .reduce((result, key) => {
        result[key] = params[key]
        return result
      }, {})
    return `${url}?${JSON.stringify(sortedParams)}`
  }

  // 设置缓存
  set(key, data, ttl = this.defaultTTL) {
    // 清除之前的定时器
    if (this.timeouts.has(key)) {
      clearTimeout(this.timeouts.get(key))
    }

    // 设置缓存数据
    this.cache.set(key, {
      data,
      timestamp: Date.now(),
      ttl
    })

    // 设置过期定时器
    const timeout = setTimeout(() => {
      this.delete(key)
    }, ttl)
    
    this.timeouts.set(key, timeout)
  }

  // 获取缓存
  get(key) {
    const cached = this.cache.get(key)
    if (!cached) {
      return null
    }

    // 检查是否过期
    if (Date.now() - cached.timestamp > cached.ttl) {
      this.delete(key)
      return null
    }

    return cached.data
  }

  // 删除缓存
  delete(key) {
    this.cache.delete(key)
    if (this.timeouts.has(key)) {
      clearTimeout(this.timeouts.get(key))
      this.timeouts.delete(key)
    }
  }

  // 清空所有缓存
  clear() {
    this.cache.clear()
    this.timeouts.forEach(timeout => clearTimeout(timeout))
    this.timeouts.clear()
  }

  // 获取缓存大小
  size() {
    return this.cache.size
  }

  // 检查是否存在缓存
  has(key) {
    return this.cache.has(key) && this.get(key) !== null
  }
}

// 创建全局缓存实例
const apiCache = new ApiCache()

// 缓存配置
export const CACHE_CONFIG = {
  // 文章列表缓存5分钟
  ARTICLES: 5 * 60 * 1000,
  // 文章详情缓存10分钟
  ARTICLE_DETAIL: 10 * 60 * 1000,
  // 分类和标签缓存30分钟
  CATEGORIES: 30 * 60 * 1000,
  TAGS: 30 * 60 * 1000,
  // 热门关键词缓存15分钟
  HOT_KEYWORDS: 15 * 60 * 1000,
  // 推荐文章缓存10分钟
  FEATURED_ARTICLES: 10 * 60 * 1000,
  // 置顶文章缓存30分钟
  TOP_ARTICLES: 30 * 60 * 1000
}

export default apiCache