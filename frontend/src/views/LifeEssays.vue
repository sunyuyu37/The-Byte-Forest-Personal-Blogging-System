<template>
  <div class="life-essays">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <div class="header-icon">
            <el-icon><Edit /></el-icon>
          </div>
          <div class="header-text">
            <h1 class="page-title">生活随笔</h1>
            <p class="page-subtitle">记录生活点滴，分享人生感悟</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="articles-section">
      <div class="container">
        <div class="diary-timeline">
          <article 
            v-for="(article, index) in articles" 
            :key="article.id"
            class="diary-entry"
            :class="{ 'diary-left': index % 2 === 0, 'diary-right': index % 2 === 1 }"
            @click="goToArticle(article.slug)"
          >
            <div class="diary-date-marker">
              <div class="date-circle">
                <span class="date-day">{{ getDay(article.createdAt) }}</span>
                <span class="date-month">{{ getMonth(article.createdAt) }}</span>
              </div>
            </div>
            
            <div class="diary-card">
              <div class="diary-header">
                <div class="diary-meta">
                  <div class="diary-time">
                    <el-icon><Clock /></el-icon>
                    <span>{{ getTime(article.createdAt) }}</span>
                  </div>
                  <div class="diary-weather" v-if="article.weather">
                    <el-icon><Sunny /></el-icon>
                    <span>{{ article.weather }}</span>
                  </div>
                  <div class="diary-mood" :class="`mood-${article.mood || 'happy'}`">
                    <el-icon><Smile /></el-icon>
                    <span>{{ getMoodText(article.mood) }}</span>
                  </div>
                </div>
              </div>
              
              <div class="diary-content">
                <h3 class="diary-title">{{ article.title }}</h3>
                <div class="diary-excerpt">{{ article.excerpt }}</div>
                
                <!-- 心情图片 -->
                <div class="diary-image" v-if="article.coverImage">
                  <img :src="article.coverImage" :alt="article.title" @error="onCoverError" />
                  <div class="image-caption" v-if="article.imageCaption">{{ article.imageCaption }}</div>
                </div>
                
                <!-- 情感标签 -->
                <div class="diary-tags">
                  <el-tag 
                    v-for="tag in article.tags" 
                    :key="tag.id"
                    size="small"
                    :type="getLifeTagType(tag.name)"
                    effect="plain"
                    round
                  >
                    {{ tag.name }}
                  </el-tag>
                </div>
              </div>
              
              <div class="diary-footer">
                <div class="diary-author">
                  <img :src="article.author.avatar" :alt="article.author.name" class="diary-avatar" @error="onAvatarError" />
                  <div class="author-info">
                    <span class="author-name">{{ article.author.name }}</span>
                    <span class="author-feeling">{{ article.author.feeling || '记录生活' }}</span>
                  </div>
                </div>
                <div class="diary-stats">
                  <span class="stat-item">
                    <el-icon><Heart /></el-icon>
                    {{ article.likes }}
                  </span>
                  <span class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ article.comments }}
                  </span>
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
          <h3>暂无生活随笔</h3>
          <p>期待更多温暖的生活故事</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Edit, Star, ChatDotRound, Document, Clock, Sunny } from '@element-plus/icons-vue'
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
    const response = await articleApi.getArticlesByCategory('life', {
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
  router.push(`/life-essays/${slug}`)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

const getTagType = (tagName) => {
  const types = ['', 'success', 'info', 'warning', 'danger']
  return types[tagName.length % types.length]
}

// 生活随笔模块专用方法
const getDay = (date) => {
  return new Date(date).getDate().toString().padStart(2, '0')
}

const getMonth = (date) => {
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  return months[new Date(date).getMonth()]
}

const getTime = (date) => {
  return new Date(date).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const getMoodText = (mood) => {
  const moodMap = {
    'happy': '开心',
    'sad': '难过',
    'excited': '兴奋',
    'calm': '平静',
    'thoughtful': '沉思',
    'grateful': '感恩',
    'nostalgic': '怀念'
  }
  return moodMap[mood] || '平静'
}

const getLifeTagType = (tagName) => {
  const tagTypeMap = {
    '生活': 'success',
    '感悟': 'info',
    '旅行': 'warning',
    '美食': 'danger',
    '读书': 'primary',
    '电影': 'info',
    '音乐': 'success'
  }
  return tagTypeMap[tagName] || 'info'
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
.life-essays {
  min-height: 100vh;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.page-header {
  padding: 80px 0 60px;
  background: linear-gradient(135deg, rgba(255, 236, 210, 0.9) 0%, rgba(252, 182, 159, 0.9) 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="life-pattern" x="0" y="0" width="30" height="30" patternUnits="userSpaceOnUse"><path d="M15,5 Q20,10 15,15 Q10,10 15,5" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23life-pattern)"/></svg>');
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
    color: #8b4513;
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
      color: #d2691e;
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
  background: #fef7f0;
  min-height: 60vh;
}

.diary-timeline {
  position: relative;
  max-width: 1000px;
  margin: 0 auto;
  
  &::before {
    content: '';
    position: absolute;
    left: 50%;
    top: 0;
    bottom: 0;
    width: 4px;
    background: linear-gradient(to bottom, #fcb69f, #ffecd2, #fcb69f);
    transform: translateX(-50%);
    border-radius: 2px;
  }
}

.diary-entry {
  position: relative;
  margin-bottom: 60px;
  width: 45%;
  
  &.diary-left {
    left: 0;
    
    .diary-date-marker {
      right: -35px;
    }
    
    .diary-card {
      margin-right: 40px;
      
      &::after {
        content: '';
        position: absolute;
        right: -15px;
        top: 30px;
        width: 0;
        height: 0;
        border: 15px solid transparent;
        border-left-color: white;
      }
    }
  }
  
  &.diary-right {
    left: 55%;
    
    .diary-date-marker {
      left: -35px;
    }
    
    .diary-card {
      margin-left: 40px;
      
      &::after {
        content: '';
        position: absolute;
        left: -15px;
        top: 30px;
        width: 0;
        height: 0;
        border: 15px solid transparent;
        border-right-color: white;
      }
    }
  }
}

.diary-date-marker {
  position: absolute;
  top: 20px;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #fcb69f, #ffecd2);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8b4513;
  font-weight: bold;
  box-shadow: 0 4px 15px rgba(252, 182, 159, 0.3);
  border: 3px solid white;
  
  .day {
    font-size: 18px;
    line-height: 1;
  }
  
  .month {
    font-size: 10px;
    line-height: 1;
  }
}

.diary-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(210, 105, 30, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid rgba(252, 182, 159, 0.2);
  position: relative;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 40px rgba(210, 105, 30, 0.15);
    border-color: rgba(252, 182, 159, 0.4);
  }
}

.diary-header {
  padding: 20px 20px 15px;
  background: linear-gradient(135deg, #fef7f0, #fff);
  border-bottom: 1px solid rgba(252, 182, 159, 0.1);
  
  .diary-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
    
    .time-info {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #8b4513;
      font-size: 14px;
      
      .el-icon {
        font-size: 16px;
      }
    }
    
    .weather-mood {
      display: flex;
      align-items: center;
      gap: 15px;
      
      .weather, .mood {
        display: flex;
        align-items: center;
        gap: 5px;
        color: #d2691e;
        font-size: 14px;
        
        .el-icon {
          font-size: 16px;
        }
      }
    }
  }
}

.diary-content {
  padding: 20px;
  
  .diary-title {
    font-size: 20px;
    font-weight: 600;
    color: #8b4513;
    margin-bottom: 12px;
    line-height: 1.4;
  }
  
  .diary-excerpt {
    color: #a0522d;
    line-height: 1.6;
    margin-bottom: 15px;
    font-size: 15px;
  }
  
  .mood-image {
    width: 100%;
    height: 150px;
    object-fit: cover;
    border-radius: 12px;
    margin-bottom: 15px;
  }
  
  .emotion-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 15px;
    
    .el-tag {
      border-radius: 20px;
      font-size: 12px;
      padding: 4px 12px;
    }
  }
}

.diary-footer {
  padding: 15px 20px 20px;
  background: #fefefe;
  
  .author-section {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .author-info {
      display: flex;
      align-items: center;
      gap: 10px;
      
      .author-avatar {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid rgba(252, 182, 159, 0.3);
      }
      
      .author-details {
        .author-name {
          font-size: 14px;
          font-weight: 500;
          color: #8b4513;
          margin: 0;
        }
        
        .author-role {
          font-size: 12px;
          color: #d2691e;
          margin: 0;
        }
      }
    }
    
    .diary-stats {
      display: flex;
      align-items: center;
      gap: 15px;
      
      .stat-item {
        display: flex;
        align-items: center;
        gap: 4px;
        color: #d2691e;
        font-size: 14px;
        
        .el-icon {
          font-size: 16px;
        }
      }
    }
  }
}

.article-image {
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
    background: linear-gradient(to bottom, transparent 0%, rgba(139, 69, 19, 0.7) 100%);
    display: flex;
    align-items: flex-end;
    padding: 20px;
  }
  
  .article-meta {
    display: flex;
    gap: 15px;
    color: white;
    font-size: 0.9rem;
    align-items: center;
  }
  
  .article-mood {
    background: rgba(255, 255, 255, 0.2);
    padding: 4px 8px;
    border-radius: 12px;
    backdrop-filter: blur(10px);
  }
}

.article-content {
  padding: 25px;
}

.article-title {
  font-size: 1.3rem;
  font-weight: 600;
  margin: 0 0 15px 0;
  color: #8b4513;
  line-height: 1.4;
}

.article-excerpt {
  color: #a0522d;
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
  border-top: 1px solid rgba(252, 182, 159, 0.3);
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
  color: #8b4513;
}

.article-stats {
  display: flex;
  gap: 15px;
  color: #a0522d;
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
  color: #a0522d;
  
  .empty-icon {
    font-size: 4rem;
    margin-bottom: 20px;
    opacity: 0.5;
  }
  
  h3 {
    font-size: 1.5rem;
    margin: 0 0 10px 0;
    color: #8b4513;
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