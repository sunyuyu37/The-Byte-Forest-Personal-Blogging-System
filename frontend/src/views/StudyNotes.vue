<template>
  <div class="study-notes">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <div class="header-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="header-text">
            <h1 class="page-title">学习笔记</h1>
            <p class="page-subtitle">记录学习历程，分享知识心得</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="articles-section">
      <div class="container">
        <div class="notebook-list">
          <article 
            v-for="(article, index) in articles" 
            :key="article.id"
            class="note-item"
            @click="goToArticle(article.id)"
          >
              <!-- 笔记序号 -->
              <div class="note-number">{{ String(index + 1).padStart(2, '0') }}</div>
              
              <!-- 笔记本页面 -->
              <div class="notebook-page">
              
                <!-- 笔记头部 -->
                <div class="note-header">
                  <div class="note-meta">
                    <div class="difficulty-badge">
                      <el-icon><Reading /></el-icon>
                      <span>{{ getDifficultyText(article.difficulty || 'beginner') }}</span>
                    </div>
                    <div class="progress-indicator">
                      <div class="progress-bar">
                        <div class="progress-fill" :style="{ width: (article.progress || 0) + '%' }"></div>
                      </div>
                      <span class="progress-text">{{ article.progress || 0 }}%</span>
                    </div>
                  </div>
                  <div class="note-date">{{ formatDate(article.createdAt) }}</div>
                </div>
              
                <!-- 笔记内容 -->
                <div class="note-content">
                  <h3 class="note-title">{{ article.title }}</h3>
                  <p class="note-excerpt">{{ article.excerpt }}</p>
                  
                  <!-- 学习要点 -->
                  <div class="study-points" v-if="article.keyPoints">
                    <div class="points-header">
                      <el-icon><Document /></el-icon>
                      <span>学习要点</span>
                    </div>
                    <ul class="points-list">
                      <li v-for="point in article.keyPoints.slice(0, 3)" :key="point">{{ point }}</li>
                    </ul>
                  </div>
                  
                  <!-- 知识标签 -->
                  <div class="knowledge-tags">
                    <el-tag v-for="tag in article.tags" :key="tag.id" :type="getStudyTagType(tag)" size="small" class="study-tag">
                      {{ tag.name }}
                    </el-tag>
                  </div>
                </div>
                
                <!-- 笔记底部 -->
                <div class="note-footer">
                  <div class="note-info">
                    <img class="author-avatar" :src="article.author.avatar" :alt="article.author.name" @error="onAvatarError" />
                    <div class="author-details">
                      <p class="author-name">{{ article.author.name }}</p>
                      <p class="study-category">{{ article.category || '学习笔记' }}</p>
                    </div>
                  </div>
                  
                  <div class="note-stats">
                    <div class="stat-item notes">
                      <el-icon><Reading /></el-icon>
                      <span>{{ article.studyCount || 0 }}</span>
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
              </div> <!-- notebook-page end -->
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
          <h3>暂无学习笔记</h3>
          <p>期待更多精彩的学习分享</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Reading, Star, ChatDotRound, Document } from '@element-plus/icons-vue'
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
    const response = await articleApi.getArticlesByCategory('study', {
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

// 学习笔记模块专用方法
const getDifficultyText = (difficulty) => {
  const difficultyMap = {
    'beginner': '入门',
    'intermediate': '进阶',
    'advanced': '高级',
    'expert': '专家'
  }
  return difficultyMap[difficulty] || '入门'
}

const getStudyTagType = (tag) => {
  const tagTypeMap = {
    'JavaScript': 'warning',
    'Vue': 'success',
    'React': 'info',
    'Node.js': 'success',
    'CSS': 'primary',
    'HTML': 'danger',
    'TypeScript': 'info',
    'Python': 'warning',
    'Java': 'danger',
    '算法': 'primary',
    '数据结构': 'info'
  }
  return tagTypeMap[tag.name || tag] || 'info'
}

const goToArticle = (slug) => {
  router.push(`/study-notes/${slug}`)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadArticles()
})
// 图片错误兜底
const onCoverError = (e) => {
  e.target.src = '/vite.svg'
}
const onAvatarError = (e) => {
  e.target.src = '/vite.svg'
}
</script>

<style lang="scss" scoped>
.study-notes {
  min-height: 100vh;
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.page-header {
  padding: 80px 0 60px;
  background: linear-gradient(135deg, rgba(168, 237, 234, 0.9) 0%, rgba(254, 214, 227, 0.9) 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="study-pattern" x="0" y="0" width="25" height="25" patternUnits="userSpaceOnUse"><rect x="10" y="10" width="5" height="5" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23study-pattern)"/></svg>');
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
    color: #2d5a87;
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
      color: #4a90a4;
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
  background: #f0fffe;
  min-height: 60vh;
}

.notebook-list {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.note-item {
  position: relative;
  background: #fff;
  border-radius: 15px;
  box-shadow: 0 8px 25px rgba(74, 144, 164, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  overflow: hidden;
  border-left: 5px solid #4a90a4;
  
  &:hover {
    transform: translateX(10px);
    box-shadow: 0 15px 35px rgba(74, 144, 164, 0.15);
    border-left-color: #2d5a87;
  }
  
  &:nth-child(even) {
    border-left-color: #a8edea;
    
    &:hover {
      border-left-color: #4a90a4;
    }
  }
}

.note-number {
  position: absolute;
  top: 20px;
  left: -2px;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #4a90a4, #2d5a87);
  color: white;
  border-radius: 0 20px 20px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  box-shadow: 0 4px 10px rgba(74, 144, 164, 0.3);
  z-index: 2;
}

.notebook-page {
  background: linear-gradient(to right, #fafafa 0%, #fafafa 40px, #fff 40px);
  position: relative;
  padding: 25px 25px 25px 60px;
  
  &::before {
    content: '';
    position: absolute;
    left: 40px;
    top: 0;
    bottom: 0;
    width: 2px;
    background: #e8f4f8;
  }
  
  &::after {
    content: '';
    position: absolute;
    left: 50px;
    top: 25px;
    bottom: 25px;
    width: 1px;
    background: repeating-linear-gradient(
      to bottom,
      transparent 0px,
      transparent 25px,
      #e8f4f8 25px,
      #e8f4f8 26px
    );
  }
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  
  .note-meta {
    display: flex;
    align-items: center;
    gap: 15px;
    
    .difficulty-badge {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      background: linear-gradient(135deg, #e8f4f8, #a8edea);
      border-radius: 20px;
      color: #2d5a87;
      font-size: 13px;
      font-weight: 500;
      
      .el-icon {
        font-size: 14px;
      }
    }
    
    .progress-indicator {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .progress-bar {
        width: 80px;
        height: 6px;
        background: #e8f4f8;
        border-radius: 3px;
        overflow: hidden;
        
        .progress-fill {
          height: 100%;
          background: linear-gradient(90deg, #4a90a4, #a8edea);
          border-radius: 3px;
          transition: width 0.3s ease;
        }
      }
      
      .progress-text {
        font-size: 12px;
        color: #4a90a4;
        font-weight: 500;
      }
    }
  }
  
  .note-date {
    color: #6b7280;
    font-size: 13px;
    background: #f8fafc;
    padding: 4px 10px;
    border-radius: 12px;
  }
}

.note-content {
  margin-bottom: 20px;
  
  .note-title {
    font-size: 20px;
    font-weight: 600;
    color: #2d5a87;
    margin-bottom: 12px;
    line-height: 1.4;
  }
  
  .note-excerpt {
    color: #4b5563;
    line-height: 1.6;
    margin-bottom: 15px;
    font-size: 15px;
  }
  
  .study-points {
    background: #f8fafc;
    border-radius: 12px;
    padding: 15px;
    margin-bottom: 15px;
    border-left: 4px solid #4a90a4;
    
    .points-header {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #2d5a87;
      font-weight: 500;
      margin-bottom: 10px;
      font-size: 14px;
      
      .el-icon {
        font-size: 16px;
      }
    }
    
    .points-list {
      margin: 0;
      padding-left: 20px;
      
      li {
        color: #4b5563;
        font-size: 14px;
        line-height: 1.5;
        margin-bottom: 5px;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }
  
  .knowledge-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    
    .study-tag {
      border-radius: 15px;
      font-size: 12px;
      padding: 4px 10px;
    }
  }
}

.note-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 15px;
  border-top: 1px solid #e8f4f8;
  
  .note-info {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .author-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid #e8f4f8;
    }
    
    .author-details {
      .author-name {
        font-size: 14px;
        font-weight: 500;
        color: #2d5a87;
        margin: 0 0 2px 0;
      }
      
      .study-category {
        font-size: 12px;
        color: #6b7280;
        margin: 0;
      }
    }
  }
  
  .note-stats {
    display: flex;
    align-items: center;
    gap: 15px;
    
    .stat-item {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #4a90a4;
      font-size: 13px;
      
      .el-icon {
        font-size: 14px;
      }
    }
  }
}



.load-more {
  text-align: center;
  margin-top: 40px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #4a90a4;
  
  .empty-icon {
    font-size: 4rem;
    margin-bottom: 20px;
    opacity: 0.5;
  }
  
  h3 {
    font-size: 1.5rem;
    margin: 0 0 10px 0;
    color: #2d5a87;
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