<template>
  <div class="project-practice">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <div class="header-icon">
            <el-icon><Tools /></el-icon>
          </div>
          <div class="header-text">
            <h1 class="page-title">项目实战</h1>
            <p class="page-subtitle">实战项目分享，从零到一的开发历程</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="articles-section">
      <div class="container">
        <div class="project-showcase">
          <article 
            v-for="(article, index) in articles" 
            :key="article.id"
            class="project-card"
            @click="goToArticle(article.slug)"
          >
              <!-- 项目封面 -->
              <div class="project-cover">
              <img :src="article.coverImage || '/default-project.jpg'" :alt="article.title" @error="onCoverError" />
              <div class="project-overlay">
                <div class="project-status">
                  <span class="status-badge" :class="getProjectStatus(article.status)">{{ getStatusText(article.status) }}</span>
                </div>
                <div class="project-actions">
                  <el-button type="primary" size="small" circle @click.stop="viewDemo(article.demoUrl)" v-if="article.demoUrl">
                    <el-icon><View /></el-icon>
                  </el-button>
                  <el-button type="success" size="small" circle @click.stop="viewCode(article.githubUrl)" v-if="article.githubUrl">
                    <el-icon><Link /></el-icon>
                  </el-button>
                </div>
              </div>
              <div class="article-overlay">
                <div class="article-meta">
                  <span class="article-date">{{ formatDate(article.createdAt) }}</span>
                  <span class="project-status" :class="`status-${article.status}`">
                    {{ getStatusText(article.status) }}
                  </span>
                </div>
              </div>
            </div>
            <!-- 项目信息 -->
            <div class="project-info">
              <h3 class="project-title">{{ article.title }}</h3>
              <p class="project-description">{{ article.excerpt }}</p>
              
              <!-- 技术栈 -->
              <div class="tech-stack" v-if="article.techStack">
                <div class="stack-header">
                  <el-icon><Tools /></el-icon>
                  <span>技术栈</span>
                </div>
                <div class="stack-tags">
                  <el-tag v-for="tech in article.techStack" :key="tech" :type="getTechType(tech)" size="small" class="tech-tag">
                    {{ tech }}
                  </el-tag>
                </div>
              </div>
              <div class="project-info">
                <div class="tech-stack">
                  <span class="label">技术栈：</span>
                  <div class="tech-tags">
                    <el-tag 
                      v-for="tech in article.techStack" 
                      :key="tech"
                      size="small"
                      type="info"
                    >
                      {{ tech }}
                    </el-tag>
                  </div>
                </div>
                <div class="project-links" v-if="article.links">
                  <el-button 
                    v-if="article.links.demo" 
                    size="small" 
                    type="primary" 
                    link
                    @click.stop="openLink(article.links.demo)"
                  >
                    <el-icon><View /></el-icon>
                    在线演示
                  </el-button>
                  <el-button 
                    v-if="article.links.github" 
                    size="small" 
                    type="success" 
                    link
                    @click.stop="openLink(article.links.github)"
                  >
                    <el-icon><Link /></el-icon>
                    源码
                  </el-button>
                </div>
              </div>
              <!-- 项目底部 -->
              <div class="project-footer">
                <div class="project-meta">
                  <img :src="article.author.avatar" :alt="article.author.name" class="author-avatar" @error="onAvatarError" />
                  <div class="author-details">
                    <p class="author-name">{{ article.author.name }}</p>
                    <p class="project-date">{{ formatDate(article.createdAt) }}</p>
                  </div>
                </div>
                <div class="project-stats">
                  <div class="stat-item stars">
                    <el-icon><Star /></el-icon>
                    <span>{{ article.stars || 0 }}</span>
                  </div>
                  <div class="stat-item likes">
                    <el-icon><Star /></el-icon>
                    <span>{{ article.likes || 0 }}</span>
                  </div>
                  <div class="stat-item comments">
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
          <h3>暂无项目实战</h3>
          <p>期待更多精彩的项目分享</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Tools, Star, ChatDotRound, Document, View, Link } from '@element-plus/icons-vue'
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
    const response = await articleApi.getArticlesByCategory('project', {
      page: page.value - 1,
      size: 10
    })
    
    if (response.code === 200) {
      const newArticles = response.data.content || []
      if (page.value === 1) {
        articles.value = newArticles
      } else {
        articles.value = [...articles.value, ...newArticles]
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
  router.push(`/project-practice/${slug}`)
}

// 图片错误兜底
const onCoverError = (e) => {
  e.target.src = '/vite.svg'
}
const onAvatarError = (e) => {
  e.target.src = '/vite.svg'
}

const openLink = (url) => {
  window.open(url, '_blank')
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

const getStatusText = (status) => {
  const statusMap = {
    'completed': '已完成',
    'in-progress': '进行中',
    'planning': '规划中'
  }
  return statusMap[status] || '未知'
}

// 获取项目状态样式
const getProjectStatus = (status) => {
  const statusMap = {
    'completed': 'status-completed',
    'in-progress': 'status-progress',
    'planning': 'status-planning'
  }
  return statusMap[status] || 'status-default'
}

// 获取技术栈标签类型
const getTechType = (tech) => {
  const techMap = {
    'Vue': 'success',
    'React': 'primary',
    'Angular': 'danger',
    'JavaScript': 'warning',
    'TypeScript': 'info',
    'Node.js': 'success',
    'Express': 'primary',
    'Spring Boot': 'success',
    'MySQL': 'warning',
    'MongoDB': 'success',
    'Redis': 'danger',
    'Docker': 'info',
    'Kubernetes': 'primary'
  }
  return techMap[tech] || 'default'
}

// 获取项目标签类型
const getProjectTagType = (tag) => {
  const tagMap = {
    '前端': 'primary',
    '后端': 'success',
    '全栈': 'warning',
    '移动端': 'info',
    '桌面应用': 'danger',
    '微服务': 'success',
    '大数据': 'warning',
    '人工智能': 'danger',
    '区块链': 'info',
    '物联网': 'primary'
  }
  return tagMap[tag] || 'default'
}

// 查看演示
const viewDemo = (url) => {
  if (url) {
    window.open(url, '_blank')
  }
}

// 查看代码
const viewCode = (url) => {
  if (url) {
    window.open(url, '_blank')
  }
}

// 生命周期
onMounted(() => {
  loadArticles()
})
</script>

<style lang="scss" scoped>
.project-practice {
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
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="project-pattern" x="0" y="0" width="20" height="20" patternUnits="userSpaceOnUse"><polygon points="10,2 18,10 10,18 2,10" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23project-pattern)"/></svg>');
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

.project-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 30px;
  margin-bottom: 60px;
}

.project-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  border: 2px solid transparent;
  
  &:hover {
    transform: translateY(-15px) scale(1.02);
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
    border-color: #667eea;
    
    .project-overlay {
      opacity: 1;
    }
    
    .project-cover img {
      transform: scale(1.1);
    }
  }
}

.project-cover {
  position: relative;
  height: 220px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
  }
  
  .project-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.8) 0%, rgba(118, 75, 162, 0.8) 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 20px;
  }
  
  .project-status {
    align-self: flex-start;
    
    .status-badge {
      padding: 6px 12px;
      border-radius: 20px;
      backdrop-filter: blur(10px);
      font-size: 0.8rem;
      font-weight: 600;
      color: white;
      border: 1px solid rgba(255, 255, 255, 0.3);
      
      &.status-completed {
        background: rgba(34, 197, 94, 0.9);
      }
      
      &.status-progress {
        background: rgba(251, 191, 36, 0.9);
      }
      
      &.status-planning {
        background: rgba(156, 163, 175, 0.9);
      }
    }
  }
  
  .project-actions {
    align-self: flex-end;
    display: flex;
    gap: 10px;
    
    .el-button {
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
  }
}

.project-info {
  padding: 25px;
}

.project-title {
  font-size: 1.4rem;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #1a202c;
  line-height: 1.3;
}

.project-description {
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 20px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 0.95rem;
}

// 技术栈样式
.tech-stack {
  margin-bottom: 20px;
  padding: 15px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  
  .stack-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
    font-weight: 600;
    color: #374151;
    font-size: 0.9rem;
    
    .el-icon {
      color: #667eea;
    }
  }
  
  .stack-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .tech-tag {
    font-weight: 500;
    border-radius: 6px;
  }
}

// 项目标签样式
.project-tags {
  margin-bottom: 20px;
  
  .project-tag {
    margin-right: 8px;
    margin-bottom: 8px;
    font-weight: 500;
    border-radius: 6px;
  }
}

// 项目底部样式
.project-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.project-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .author-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #e2e8f0;
  }
  
  .author-details {
    .author-name {
      font-weight: 600;
      color: #1a202c;
      margin: 0 0 4px 0;
      font-size: 0.9rem;
    }
    
    .project-date {
      color: #64748b;
      margin: 0;
      font-size: 0.8rem;
    }
  }
}

.project-stats {
  display: flex;
  gap: 20px;
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #64748b;
    font-size: 0.9rem;
    
    .el-icon {
      font-size: 16px;
    }
    
    &.stars .el-icon {
      color: #fbbf24;
    }
    
    &.likes .el-icon {
      color: #ef4444;
    }
    
    &.comments .el-icon {
      color: #3b82f6;
    }
  }
}

.project-links {
  display: flex;
  gap: 10px;
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
  
  .project-links {
    flex-direction: column;
    gap: 8px;
  }
}
</style>