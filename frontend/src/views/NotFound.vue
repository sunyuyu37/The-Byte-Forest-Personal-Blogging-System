<template>
  <div class="not-found-page">
    <div class="not-found-container">
      <div class="error-illustration">
        <div class="error-code">404</div>
        <div class="error-message">页面未找到</div>
      </div>
      
      <div class="error-content">
        <h2>抱歉，您访问的页面不存在</h2>
        <p class="error-description">
          可能是网址输入错误，或者该页面已被删除或移动。
        </p>
        
        <div class="error-actions">
          <el-button type="primary" @click="goHome">
            <el-icon><House /></el-icon>
            返回首页
          </el-button>
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回上页
          </el-button>
          <el-button @click="refresh">
            <el-icon><Refresh /></el-icon>
            刷新页面
          </el-button>
        </div>
        
        <div class="suggestions">
          <h3>您可以尝试：</h3>
          <ul>
            <li>检查网址是否输入正确</li>
            <li>返回首页重新导航</li>
            <li>使用搜索功能查找内容</li>
            <li>联系网站管理员</li>
          </ul>
        </div>
        
        <div class="search-section">
          <h3>搜索内容：</h3>
          <el-input
            v-model="searchKeyword"
            placeholder="输入关键词搜索文章"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
        
        <div class="popular-articles">
          <h3>热门文章：</h3>
          <div class="article-list">
            <div
              v-for="article in popularArticles"
              :key="article.id"
              class="article-item"
              @click="goToArticle(article.id)"
            >
              <div class="article-title">{{ article.title }}</div>
              <div class="article-meta">
                <span class="view-count">
                  <el-icon><View /></el-icon>
                  {{ article.viewCount }}
                </span>
                <span class="publish-date">{{ formatDate(article.publishTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, ArrowLeft, Refresh, Search, View } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { articleApi } from '@/api/article'

const router = useRouter()
const searchKeyword = ref('')
const popularArticles = ref([])

// 返回首页
const goHome = () => {
  router.push('/')
}

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    goHome()
  }
}

// 刷新页面
const refresh = () => {
  window.location.reload()
}

// 搜索功能
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  // 跳转到搜索页面
  router.push({
    path: '/search',
    query: { q: searchKeyword.value.trim() }
  })
}

// 跳转到文章详情
const goToArticle = async (articleIdOrSlug) => {
  if (!articleIdOrSlug) return
  if (/^\d+$/.test(String(articleIdOrSlug))) {
    try {
      const res = await articleApi.getArticleById(articleIdOrSlug)
      const slug = res.data?.slug
      router.push(`/article/${slug || articleIdOrSlug}`)
    } catch (e) {
      router.push(`/article/${articleIdOrSlug}`)
    }
  } else {
    router.push(`/article/${articleIdOrSlug}`)
  }
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 加载热门文章
const loadPopularArticles = async () => {
  try {
    const res = await articleApi.getPopularArticles({ page: 0, size: 5 })
    if (res.code === 200 && res.data && res.data.content) {
      popularArticles.value = res.data.content.map(a => ({
        id: a.id,
        title: a.title,
        viewCount: a.viewCount,
        publishTime: a.publishedAt || a.publishTime
      }))
    } else {
      popularArticles.value = []
    }
  } catch (error) {
    console.error('加载热门文章失败:', error)
    popularArticles.value = []
  }
}

onMounted(() => {
  loadPopularArticles()
})
</script>

<style lang="scss" scoped>
.not-found-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.not-found-container {
  max-width: 800px;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  text-align: center;
  position: relative;
  z-index: 2;
}

.error-illustration {
  margin-bottom: 30px;
  
  .error-code {
    font-size: 8rem;
    font-weight: 900;
    background: linear-gradient(45deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    line-height: 1;
    margin-bottom: 10px;
    text-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .error-message {
    font-size: 1.5rem;
    color: var(--el-text-color-primary);
    font-weight: 600;
  }
}

.error-content {
  h2 {
    font-size: 1.8rem;
    color: var(--el-text-color-primary);
    margin-bottom: 15px;
    font-weight: 600;
  }
  
  .error-description {
    font-size: 1.1rem;
    color: var(--el-text-color-regular);
    margin-bottom: 30px;
    line-height: 1.6;
  }
}

.error-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.suggestions {
  text-align: left;
  margin-bottom: 30px;
  
  h3 {
    font-size: 1.2rem;
    color: var(--el-text-color-primary);
    margin-bottom: 15px;
    font-weight: 600;
  }
  
  ul {
    list-style: none;
    padding: 0;
    
    li {
      padding: 8px 0;
      color: var(--el-text-color-regular);
      position: relative;
      padding-left: 20px;
      
      &::before {
        content: '•';
        color: var(--el-color-primary);
        font-weight: bold;
        position: absolute;
        left: 0;
      }
    }
  }
}

.search-section {
  margin-bottom: 30px;
  
  h3 {
    font-size: 1.2rem;
    color: var(--el-text-color-primary);
    margin-bottom: 15px;
    font-weight: 600;
    text-align: left;
  }
  
  .search-input {
    max-width: 400px;
  }
}

.popular-articles {
  text-align: left;
  
  h3 {
    font-size: 1.2rem;
    color: var(--el-text-color-primary);
    margin-bottom: 15px;
    font-weight: 600;
  }
  
  .article-list {
    .article-item {
      padding: 15px;
      border: 1px solid var(--el-border-color-light);
      border-radius: 8px;
      margin-bottom: 10px;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        border-color: var(--el-color-primary);
        background: var(--el-bg-color);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      .article-title {
        font-size: 1rem;
        color: var(--el-text-color-primary);
        font-weight: 500;
        margin-bottom: 8px;
        line-height: 1.4;
      }
      
      .article-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 0.875rem;
        color: var(--el-text-color-secondary);
        
        .view-count {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
  
  .floating-shape {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    animation: float 6s ease-in-out infinite;
    
    &.shape-1 {
      width: 80px;
      height: 80px;
      top: 10%;
      left: 10%;
      animation-delay: 0s;
    }
    
    &.shape-2 {
      width: 120px;
      height: 120px;
      top: 20%;
      right: 15%;
      animation-delay: 2s;
    }
    
    &.shape-3 {
      width: 60px;
      height: 60px;
      bottom: 20%;
      left: 20%;
      animation-delay: 4s;
    }
    
    &.shape-4 {
      width: 100px;
      height: 100px;
      bottom: 10%;
      right: 10%;
      animation-delay: 1s;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .not-found-page {
    padding: 15px;
  }
  
  .not-found-container {
    padding: 30px 20px;
  }
  
  .error-illustration {
    .error-code {
      font-size: 6rem;
    }
    
    .error-message {
      font-size: 1.2rem;
    }
  }
  
  .error-content {
    h2 {
      font-size: 1.5rem;
    }
    
    .error-description {
      font-size: 1rem;
    }
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
    
    .el-button {
      width: 200px;
    }
  }
  
  .popular-articles {
    .article-list {
      .article-item {
        .article-meta {
          flex-direction: column;
          align-items: flex-start;
          gap: 5px;
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .error-illustration {
    .error-code {
      font-size: 4rem;
    }
  }
  
  .error-content {
    h2 {
      font-size: 1.3rem;
    }
  }
}
</style>