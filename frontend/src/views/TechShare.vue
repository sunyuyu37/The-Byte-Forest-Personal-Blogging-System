<template>
  <div class="tech-share">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <div class="header-icon">
            <el-icon><Cpu /></el-icon>
          </div>
          <div class="header-text">
            <h1 class="page-title">技术分享</h1>
            <p class="page-subtitle">分享前沿技术、开发经验与技术见解</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="articles-section">
      <div class="container">
        <div class="tech-articles-grid">
          <article 
            v-for="article in articles" 
            :key="article.id"
            class="tech-article-card"
            @click="goToArticle(article.slug)"
          >
            <div class="tech-card-header">
              <div class="tech-meta">
                <div class="tech-category">
                  <el-icon><Cpu /></el-icon>
                  <span>{{ article.category || '技术分享' }}</span>
                </div>
                <div class="tech-difficulty" :class="`difficulty-${article.difficulty || 'medium'}`">
                  {{ getDifficultyText(article.difficulty || 'medium') }}
                </div>
              </div>
              <div class="tech-date">{{ formatDate(article.createdAt) }}</div>
            </div>
            <div class="tech-content">
              <h3 class="tech-title">{{ article.title }}</h3>
              <p class="tech-excerpt">{{ article.excerpt }}</p>
              
              <!-- 代码片段预览 -->
              <div class="code-preview" v-if="article.codeSnippet">
                <div class="code-header">
                  <span class="code-lang">{{ article.codeLanguage || 'javascript' }}</span>
                  <el-icon><DocumentCopy /></el-icon>
                </div>
                <pre class="code-content"><code>{{ article.codeSnippet }}</code></pre>
              </div>
              
              <!-- 技术标签 -->
              <div class="tech-tags">
                <el-tag 
                  v-for="tag in article.tags" 
                  :key="tag.id"
                  size="small"
                  :type="getTechTagType(tag.name)"
                  effect="dark"
                >
                  <el-icon><Code /></el-icon>
                  {{ tag.name }}
                </el-tag>
              </div>
            </div>
            
            <div class="tech-footer">
              <div class="author-section">
                <img :src="article.author.avatar" :alt="article.author.name" class="tech-avatar" @error="onAvatarError" />
                <div class="author-details">
                  <span class="author-name">{{ article.author.name }}</span>
                  <span class="author-role">{{ article.author.role || '开发者' }}</span>
                </div>
              </div>
              <div class="tech-stats">
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ article.views }}
                </span>
                <span class="stat-item">
                  <el-icon><Star /></el-icon>
                  {{ article.likes }}
                </span>
                <span class="stat-item">
                  <el-icon><ChatDotRound /></el-icon>
                  {{ article.comments }}
                </span>
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
          <h3>暂无技术分享文章</h3>
          <p>期待更多精彩的技术分享内容</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Cpu, Star, ChatDotRound, Document, DocumentCopy } from '@element-plus/icons-vue'
import { articleApi } from '@/api/article'
import { ElMessage } from 'element-plus'
// 新增：引入本地 SVG 占位图工具
import { generateArticleCoverPlaceholder, generateAvatarPlaceholder } from '@/utils/placeholder'

const router = useRouter()

// 响应式数据
const articles = ref([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)

// 新增：预清洗工具，过滤 via.placeholder.com 等无效外链
const isBadPlaceholderUrl = (url) => typeof url === 'string' && /(^https?:)?\/\/via\.placeholder\.com/i.test(url)
const sanitizeArticle = (a) => {
  if (!a) return a
  // 覆盖封面
  if (!a.coverImage || isBadPlaceholderUrl(a.coverImage)) {
    a.coverImage = generateArticleCoverPlaceholder(300, 200)
  }
  // 覆盖作者头像
  a.author = a.author || {}
  if (!a.author.avatar || isBadPlaceholderUrl(a.author.avatar)) {
    a.author.avatar = generateAvatarPlaceholder(a.author.name || '用户', 40)
  }
  return a
}

// 方法
const loadArticles = async () => {
  loading.value = true
  try {
    const response = await articleApi.getArticlesByCategory('tech', {
      page: page.value - 1,
      size: 10
    })
    
    if (response.code === 200) {
      const data = response.data
      const list = (data.content || []).map(sanitizeArticle)
      if (page.value === 1) {
        articles.value = list
      } else {
        articles.value.push(...list)
      }
      hasMore.value = !data.last
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
  router.push(`/tech-share/${slug}`)
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

// 技术分享模块专用方法
const getDifficultyText = (difficulty) => {
  const difficultyMap = {
    'easy': '入门',
    'medium': '进阶',
    'hard': '高级',
    'expert': '专家'
  }
  return difficultyMap[difficulty] || '进阶'
}

const getTechTagType = (tagName) => {
  const tagTypeMap = {
    'JavaScript': 'warning',
    'Vue': 'success',
    'React': 'info',
    'Node.js': 'success',
    'Python': 'primary',
    'Java': 'danger',
    'CSS': 'info',
    'HTML': 'warning'
  }
  return tagTypeMap[tagName] || 'primary'
}

// 生命周期
onMounted(() => {
  loadArticles()
})
</script>

<style lang="scss" scoped>
.tech-share {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.page-header {
  padding: 80px 0 60px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.9) 0%, rgba(118, 75, 162, 0.9) 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="tech-pattern" x="0" y="0" width="20" height="20" patternUnits="userSpaceOnUse"><circle cx="10" cy="10" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23tech-pattern)"/></svg>');
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
    color: white;
  }
  
  .header-icon {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
    border: 2px solid rgba(255, 255, 255, 0.3);
    
    .el-icon {
      font-size: 40px;
    }
  }
  
  .page-title {
    font-size: 3rem;
    font-weight: 700;
    margin: 0 0 10px 0;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  }
  
  .page-subtitle {
    font-size: 1.2rem;
    opacity: 0.9;
    margin: 0;
  }
}

.articles-section {
  padding: 80px 0;
  background: #f8fafc;
  min-height: 60vh;
}

.tech-articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  gap: 25px;
  margin-bottom: 60px;
}

.tech-article-card {
  background: #1a1a1a;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #333;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #00d4aa, #00a8ff, #7b68ee);
    opacity: 0;
    transition: opacity 0.3s ease;
  }
  
  &:hover {
    transform: translateY(-5px);
    border-color: #555;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
    
    &::before {
      opacity: 1;
    }
  }
}

.tech-card-header {
  padding: 20px 20px 0;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  
  .tech-meta {
    display: flex;
    gap: 12px;
    align-items: center;
    
    .tech-category {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #00d4aa;
      font-size: 14px;
      font-weight: 500;
      
      .el-icon {
        font-size: 16px;
      }
    }
    
    .tech-difficulty {
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
      
      &.difficulty-easy {
        background: rgba(34, 197, 94, 0.2);
        color: #22c55e;
      }
      
      &.difficulty-medium {
        background: rgba(251, 191, 36, 0.2);
        color: #fbbf24;
      }
      
      &.difficulty-hard {
        background: rgba(239, 68, 68, 0.2);
        color: #ef4444;
      }
      
      &.difficulty-expert {
        background: rgba(147, 51, 234, 0.2);
        color: #9333ea;
      }
    }
  }
  
  .tech-date {
    color: #888;
    font-size: 13px;
  }
}

.tech-content {
  padding: 20px;
  
  .tech-title {
    color: #fff;
    font-size: 18px;
    font-weight: 600;
    margin: 0 0 12px 0;
    line-height: 1.4;
  }
  
  .tech-excerpt {
    color: #ccc;
    font-size: 14px;
    line-height: 1.6;
    margin: 0 0 16px 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.code-preview {
  background: #0d1117;
  border: 1px solid #30363d;
  border-radius: 8px;
  margin: 16px 0;
  overflow: hidden;
  
  .code-header {
    background: #161b22;
    padding: 8px 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #30363d;
    
    .code-lang {
      color: #7c3aed;
      font-size: 12px;
      font-weight: 500;
      text-transform: uppercase;
    }
    
    .el-icon {
      color: #8b949e;
      font-size: 14px;
      cursor: pointer;
      
      &:hover {
        color: #fff;
      }
    }
  }
  
  .code-content {
    padding: 12px;
    margin: 0;
    font-family: 'Fira Code', 'Consolas', monospace;
    font-size: 13px;
    line-height: 1.5;
    color: #e6edf3;
    background: transparent;
    overflow-x: auto;
    
    code {
      background: transparent;
      padding: 0;
      border-radius: 0;
    }
  }
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
  
  .el-tag {
    display: flex;
    align-items: center;
    gap: 4px;
    border: none;
    
    .el-icon {
      font-size: 12px;
    }
  }
}

.tech-footer {
  padding: 0 20px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .author-section {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .tech-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      object-fit: cover;
    }
    
    .author-details {
      display: flex;
      flex-direction: column;
      
      .author-name {
        color: #fff;
        font-size: 14px;
        font-weight: 500;
        line-height: 1;
      }
      
      .author-role {
        color: #888;
        font-size: 12px;
        line-height: 1;
        margin-top: 2px;
      }
    }
  }
  
  .tech-stats {
    display: flex;
    gap: 16px;
    
    .stat-item {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #888;
      font-size: 13px;
      
      .el-icon {
        font-size: 14px;
      }
      
      &:hover {
        color: #00d4aa;
      }
    }
  }
}

.old-article-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  
  .article-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(to bottom, transparent 0%, rgba(0, 0, 0, 0.7) 100%);
    display: flex;
    align-items: flex-end;
    padding: 20px;
  }
  
  .article-meta {
    display: flex;
    gap: 15px;
    color: white;
    font-size: 0.9rem;
  }
}

.article-content {
  padding: 25px;
}

.article-title {
  font-size: 1.3rem;
  font-weight: 600;
  margin: 0 0 15px 0;
  color: #1a202c;
  line-height: 1.4;
}

.article-excerpt {
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 20px 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-weight: 500;
  color: #374151;
}

.article-stats {
  display: flex;
  gap: 15px;
  color: #64748b;
  font-size: 0.9rem;
  
  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.load-more {
  text-align: center;
  margin-top: 40px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #64748b;
  
  .empty-icon {
    font-size: 4rem;
    margin-bottom: 20px;
    opacity: 0.5;
  }
  
  h3 {
    font-size: 1.5rem;
    margin: 0 0 10px 0;
    color: #374151;
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
}
</style>