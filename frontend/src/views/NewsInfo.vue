<template>
  <div class="news-info">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <div class="header-icon">
            <el-icon><Notification /></el-icon>
          </div>
          <div class="header-text">
            <h1 class="page-title">新闻资讯</h1>
            <p class="page-subtitle">关注行业动态，掌握前沿资讯</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="articles-section">
      <div class="container">
        <div class="news-waterfall">
          <article 
            v-for="article in articles" 
            :key="article.id"
            class="news-card"
            @click="goToArticle(article.slug)"
          >
            <div class="article-image">
              <img :src="article.coverImage || '/default-news.jpg'" :alt="article.title" @error="onCoverError" />
              <div class="article-overlay">
                <div class="article-meta">
                  <span class="article-date">{{ formatDate(article.createdAt) }}</span>
                  <span class="news-category" :class="`category-${article.category}`">
                    {{ getCategoryText(article.category) }}
                  </span>
                </div>
              </div>
            </div>
            <div class="article-content">
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-excerpt">{{ article.excerpt }}</p>
              <div class="news-info-bar">
                <div class="source-info">
                  <span class="source">来源：{{ article.source }}</span>
                  <div class="detail-item">
                    <el-icon><Clock /></el-icon>
                    <span>{{ formatDate(article.createdAt) }}</span>
                  </div>
                  <div class="detail-item">
                    <el-icon><Location /></el-icon>
                    <span>{{ article.location || '全球' }}</span>
                  </div>
                </div>
                <div class="article-tags">
                  <el-tag v-for="tag in article.tags" :key="tag" :type="getNewsTagType(tag)" size="small" class="news-tag">
                    {{ tag.name }}
                  </el-tag>
                </div>
              </div>
              <div class="article-footer">
                <div class="author-info">
                  <img class="reporter-avatar" :src="article.author.avatar" :alt="article.author.name" @error="onAvatarError" />
                  <span class="author-name">{{ article.author.name }}</span>
                </div>
                <div class="article-stats">
                  <span class="views">
                    <el-icon><View /></el-icon>
                    {{ article.views }}
                  </span>
                  <div class="stat-item">
                    <el-icon><Star /></el-icon>
                    <span>{{ article.likes || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><Share /></el-icon>
                    <span>{{ article.shares || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    <span>{{ article.comments || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </article>
        </div>

        <!-- 加载更多 -->
        <div class="load-more" v-if="hasMore">
          <el-button 
            type="primary" 
            size="large"
            :loading="loading"
            @click="loadMore"
          >
            加载更多
          </el-button>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && articles.length === 0" class="empty-state">
          <el-icon class="empty-icon"><Document /></el-icon>
          <h3>暂无新闻资讯</h3>
          <p>期待更多精彩的资讯内容</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Notification, Star, ChatDotRound, Document, View, Clock, Location, Share } from '@element-plus/icons-vue'
import { articleApi } from '@/api/article'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 响应式数据
const articles = ref([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)

// 方法
const loadArticles = async () => {
  loading.value = true
  try {
    const response = await articleApi.getArticlesByCategory('news', {
      page: page.value - 1,
      size: 10
    })
    
    if (response.code === 200) {
      if (page.value === 1) {
        articles.value = response.data.content || []
      } else {
        articles.value.push(...(response.data.content || []))
      }
      hasMore.value = !response.data.last
    } else {
      ElMessage.error(response.message || '加载文章失败')
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  page.value++
  loadArticles()
}

const goToArticle = (slug) => {
  router.push(`/news-info/${slug}`)
}

// 图片错误兜底
const onCoverError = (e) => {
  e.target.src = '/vite.svg'
}
const onAvatarError = (e) => {
  e.target.src = '/vite.svg'
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatDateTime = (datetime) => {
  return new Date(datetime).toLocaleString('zh-CN')
}

const getCategoryText = (category) => {
  const categoryMap = {
    tech: '科技',
    ai: '人工智能',
    industry: '行业动态',
    policy: '政策法规'
  }
  return categoryMap[category] || '其他'
}

// 获取时间差显示
const getTimeAgo = (date) => {
  const now = new Date()
  const publishTime = new Date(date)
  const diffMs = now - publishTime
  const diffMins = Math.floor(diffMs / (1000 * 60))
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
  
  if (diffMins < 1) return '刚刚'
  if (diffMins < 60) return `${diffMins}分钟前`
  if (diffHours < 24) return `${diffHours}小时前`
  if (diffDays < 7) return `${diffDays}天前`
  return formatDate(date)
}

// 获取新闻标签类型
const getNewsTagType = (tag) => {
  const tagMap = {
    '突发': 'danger',
    '热点': 'warning',
    '独家': 'success',
    '深度': 'info',
    '快讯': 'primary',
    '科技': 'primary',
    '财经': 'success',
    '政策': 'warning',
    '国际': 'info',
    '社会': 'danger'
  }
  return tagMap[tag] || 'default'
}

// 生命周期
onMounted(() => {
  loadArticles()
})
</script>

<style lang="scss" scoped>
.news-info {
  min-height: 100vh;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
}

.page-header {
  padding: 80px 0 60px;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.9) 0%, rgba(254, 207, 239, 0.9) 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="news-pattern" x="0" y="0" width="15" height="15" patternUnits="userSpaceOnUse"><circle cx="7.5" cy="7.5" r="2" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23news-pattern)"/></svg>');
    opacity: 0.3;
  }
  
  .container {
    position: relative;
    z-index: 1;
  }
  
  .header-content {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
    text-align: center;
    color: #be185d;
  }
  
  .header-icon {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
    border: 2px solid rgba(255, 255, 255, 0.4);
    
    .el-icon {
      font-size: 40px;
      color: #ec4899;
    }
  }
  
  .page-title {
    font-size: 3rem;
    font-weight: 700;
    margin: 0 0 10px 0;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .page-subtitle {
    font-size: 1.2rem;
    opacity: 0.8;
    margin: 0;
  }
}

.articles-section {
  padding: 80px 0;
  background: #fef7f7;
  min-height: 60vh;
}

.news-waterfall {
  columns: 3;
  column-gap: 30px;
  margin-bottom: 60px;
  
  @media (max-width: 1200px) {
    columns: 2;
  }
  
  @media (max-width: 768px) {
    columns: 1;
  }
}

.news-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
  break-inside: avoid;
  page-break-inside: avoid;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
    border-color: rgba(255, 154, 158, 0.3);
  }
}

.news-cover {
  position: relative;
  height: auto;
  min-height: 180px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  
  .news-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3) 0%, rgba(0, 0, 0, 0.7) 100%);
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 15px;
  }
  
  .news-badges {
    display: flex;
    gap: 8px;
    align-self: flex-start;
    
    .news-badge {
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 0.75rem;
      font-weight: 600;
      color: white;
      backdrop-filter: blur(10px);
      
      &.breaking {
        background: rgba(239, 68, 68, 0.9);
      }
      
      &.hot {
        background: rgba(245, 158, 11, 0.9);
      }
    }
  }
  
  .news-meta {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    color: white;
    font-size: 0.85rem;
    
    .news-source {
      font-weight: 500;
    }
    
    .news-time {
      opacity: 0.9;
    }
  }
}

.news-content {
  padding: 20px;
}

.news-title {
  font-size: 1.2rem;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #1f2937;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-summary {
  color: #6b7280;
  line-height: 1.5;
  margin: 0 0 15px 0;
  font-size: 0.9rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 15px;
}

.news-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #e5e7eb;
  font-size: 0.85rem;
}

.reporter-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  
  .reporter-avatar {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    object-fit: cover;
  }
  
  .reporter-name {
    font-weight: 500;
  }
}

.news-stats {
  display: flex;
  gap: 12px;
  color: #9ca3af;
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    
    &:hover {
      color: #6b7280;
    }
  }
}

.news-location {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #9ca3af;
  font-size: 0.8rem;
  margin-bottom: 8px;
}

.load-more {
  text-align: center;
  margin-top: 40px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #ec4899;
  
  .empty-icon {
    font-size: 4rem;
    margin-bottom: 20px;
    opacity: 0.5;
  }
  
  h3 {
    font-size: 1.5rem;
    margin: 0 0 10px 0;
    color: #be185d;
  }
  
  p {
    margin: 0;
    font-size: 1rem;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-header {
    padding: 60px 0 40px;
    
    .header-content {
      flex-direction: column;
      gap: 15px;
    }
    
    .header-icon {
      width: 60px;
      height: 60px;
      
      .el-icon {
        font-size: 30px;
      }
    }
    
    .page-title {
      font-size: 2.2rem;
    }
    
    .page-subtitle {
      font-size: 1rem;
    }
  }
  
  .articles-section {
    padding: 60px 0;
  }
  
  .articles-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .article-card {
    margin: 0 10px;
  }
  
  .source-info {
    flex-direction: column;
    gap: 5px;
  }
}
</style>