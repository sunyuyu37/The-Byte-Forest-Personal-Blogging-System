<template>
  <div class="competition-events">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <div class="header-icon">
            <el-icon><Trophy /></el-icon>
          </div>
          <div class="header-text">
            <h1 class="page-title">竞赛活动</h1>
            <p class="page-subtitle">参与竞赛，展示才华，赢取荣誉</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 活动状态筛选 -->
    <div class="filter-section">
      <div class="container">
        <div class="filter-tabs">
          <el-button 
            v-for="status in statusFilters" 
            :key="status.value"
            :type="activeStatus === status.value ? 'primary' : ''"
            @click="filterByStatus(status.value)"
            class="filter-btn"
          >
            {{ status.label }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 活动列表 -->
    <div class="events-section">
      <div class="container">
        <div class="competition-showcase">
          <article 
            v-for="event in filteredEvents" 
            :key="event.id"
            class="competition-card"
            :class="`status-${event.status}`"
            @click="goToEvent(event.slug)"
          >
            <!-- 竞赛状态徽章 -->
            <div class="competition-status-badge">
              <span class="status-text">{{ getStatusText(event.status) }}</span>
            </div>
            
            <!-- 竞赛海报头部 -->
            <div class="competition-poster">
              <img :src="event.coverImage || '/default-competition.jpg'" :alt="event.title" @error="onCoverError" />
              <div class="poster-overlay">
                <div class="poster-header">
                  <div class="competition-type" :class="`type-${event.type}`">
                    {{ getTypeText(event.type) }}
                  </div>
                  <div class="prize-highlight" v-if="event.prizePool">
                    <el-icon><Trophy /></el-icon>
                    <span>{{ event.prizePool }}</span>
                  </div>
                </div>
                <div class="poster-title">
                  <h3>{{ event.title }}</h3>
                </div>
              </div>
            </div>
            
            <!-- 竞赛信息 -->
            <div class="competition-info">
              <div class="competition-description">
                {{ event.description }}
              </div>
              
              <!-- 关键信息 -->
              <div class="key-info">
                <div class="info-row">
                  <div class="info-item">
                    <el-icon><Calendar /></el-icon>
                    <div class="info-content">
                      <span class="label">报名截止</span>
                      <span class="value">{{ formatDate(event.registrationEnd) }}</span>
                    </div>
                  </div>
                  <div class="info-item">
                    <el-icon><Clock /></el-icon>
                    <div class="info-content">
                      <span class="label">比赛时间</span>
                      <span class="value">{{ formatDate(event.competitionStart) }}</span>
                    </div>
                  </div>
                </div>
                <div class="info-row">
                  <div class="info-item">
                    <el-icon><User /></el-icon>
                    <div class="info-content">
                      <span class="label">参赛人数</span>
                      <span class="value">{{ event.participants }}/{{ event.maxParticipants || '∞' }}</span>
                    </div>
                  </div>
                  <div class="info-item" v-if="event.organizer">
                    <el-icon><OfficeBuilding /></el-icon>
                    <div class="info-content">
                      <span class="label">主办方</span>
                      <span class="value">{{ event.organizer }}</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 竞赛标签 -->
              <div class="competition-tags">
                <el-tag 
                  v-for="tag in event.tags" 
                  :key="tag.id"
                  size="small"
                  :type="getCompetitionTagType(tag.name)"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
              
              <!-- 竞赛底部 -->
              <div class="competition-footer">
                <div class="competition-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ event.views }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ event.likes }}
                  </span>
                  <span class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ event.comments }}
                  </span>
                </div>
                <div class="action-button">
                  <el-button 
                    v-if="event.status === 'registration'"
                    type="primary" 
                    size="large"
                    @click.stop="registerEvent(event.id)"
                  >
                    立即报名
                  </el-button>
                  <el-button 
                    v-else-if="event.status === 'ongoing'"
                    type="warning" 
                    size="large"
                    @click.stop="viewProgress(event.id)"
                  >
                    查看进度
                  </el-button>
                  <el-button 
                    v-else-if="event.status === 'ended'"
                    type="info" 
                    size="large"
                    @click.stop="viewResults(event.id)"
                  >
                    查看结果
                  </el-button>
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
        <div v-if="!loading && filteredEvents.length === 0" class="empty-state">
          <el-icon class="empty-icon"><Trophy /></el-icon>
          <h3>暂无竞赛活动</h3>
          <p>期待更多精彩的竞赛活动</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Trophy, Calendar, Clock, User, OfficeBuilding, Star, ChatDotRound, View } from '@element-plus/icons-vue'
import { articleApi } from '@/api/article'
import { ElMessage } from 'element-plus'
import { generatePlaceholderImage } from '@/utils/placeholder'

const router = useRouter()

// 响应式数据
const events = ref([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)
const activeStatus = ref('all')

// 状态筛选选项
const statusFilters = [
  { value: 'all', label: '全部活动' },
  { value: 'registration', label: '报名中' },
  { value: 'ongoing', label: '进行中' },
  { value: 'ended', label: '已结束' }
]

// 竞赛活动数据
const mockEvents = [
  {
    id: 1,
    title: '2024年全国大学生程序设计竞赛',
    description: '面向全国大学生的程序设计竞赛，考察算法设计、编程实现和问题解决能力。',
    coverImage: generatePlaceholderImage('ACM Contest', 300, 200, '#f56c6c'),
    slug: 'acm-programming-contest-2024',
    status: 'registration',
    type: 'programming',
    prizePool: '50万元',
    registrationStart: '2024-02-01',
    registrationEnd: '2024-03-15',
    competitionStart: '2024-04-01',
    competitionEnd: '2024-04-03',
    participants: 1250,
    maxParticipants: 2000,
    organizer: '中国计算机学会',
    views: 5680,
    likes: 423,
    comments: 156,
    tags: [
      { id: 1, name: '算法' },
      { id: 2, name: 'C++' },
      { id: 3, name: '数据结构' }
    ]
  },
  {
    id: 2,
    title: 'AI创新应用挑战赛',
    description: '探索人工智能在各行业的创新应用，展示AI技术的无限可能。',
    coverImage: generatePlaceholderImage('AI Challenge', 300, 200, '#409eff'),
    slug: 'ai-innovation-challenge-2024',
    status: 'ongoing',
    type: 'ai',
    prizePool: '100万元',
    registrationStart: '2024-01-01',
    registrationEnd: '2024-02-28',
    competitionStart: '2024-03-01',
    competitionEnd: '2024-05-31',
    participants: 890,
    maxParticipants: 1500,
    organizer: '人工智能产业联盟',
    views: 4320,
    likes: 367,
    comments: 89,
    tags: [
      { id: 4, name: '人工智能' },
      { id: 5, name: '机器学习' },
      { id: 6, name: '深度学习' }
    ]
  }
]

// 计算属性
const filteredEvents = computed(() => {
  if (activeStatus.value === 'all') {
    return events.value
  }
  return events.value.filter(event => event.status === activeStatus.value)
})

// 方法
const loadEvents = async () => {
  loading.value = true
  try {
    const response = await articleApi.getArticlesByCategory('contest', {
      page: page.value - 1,
      size: 10
    })
    
    if (response.code === 200) {
      const articles = response.data.content || []
      // 将文章数据转换为竞赛活动格式
      const eventData = articles.map(article => ({
        id: article.id,
        title: article.title,
        description: article.summary,
        coverImage: article.coverImage,
        slug: article.slug,
        status: 'registration', // 默认状态
        type: 'programming',
        views: article.viewCount || 0,
        likes: article.likeCount || 0,
        comments: article.commentCount || 0,
        organizer: article.author?.nickname || '主办方',
        tags: article.tags || []
      }))
      
      if (page.value === 1) {
        events.value = eventData
      } else {
        events.value.push(...eventData)
      }
      hasMore.value = !response.data.last
    } else {
      ElMessage.error(response.message || '加载活动失败')
    }
  } catch (error) {
    console.error('加载活动失败:', error)
    ElMessage.error('加载活动失败')
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  page.value++
  loadEvents()
}

const filterByStatus = (status) => {
  activeStatus.value = status
  page.value = 1
  loadEvents()
}

const goToEvent = (slug) => {
  router.push(`/competition-events/${slug}`)
}

const registerEvent = (eventId) => {
  // 处理报名逻辑
  console.log('报名活动:', eventId)
}

const viewProgress = (eventId) => {
  // 查看比赛进度
  console.log('查看进度:', eventId)
}

const viewResults = (eventId) => {
  // 查看比赛结果
  console.log('查看结果:', eventId)
}

const formatDateRange = (start, end) => {
  const startDate = new Date(start).toLocaleDateString('zh-CN')
  const endDate = new Date(end).toLocaleDateString('zh-CN')
  return `${startDate} - ${endDate}`
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
  })
}

// 获取竞赛标签类型
const getCompetitionTagType = (tag) => {
  const tagMap = {
    '算法': 'primary',
    'C++': 'success',
    '数据结构': 'info',
    '人工智能': 'warning',
    '机器学习': 'danger',
    '深度学习': 'primary',
    '编程': 'success',
    '设计': 'warning',
    '创新': 'info'
  }
  return tagMap[tag] || 'default'
}

// 图片错误兜底
const onCoverError = (e) => {
  e.target.src = '/vite.svg'
}

const getStatusText = (status) => {
  const statusMap = {
    registration: '报名中',
    ongoing: '进行中',
    ended: '已结束'
  }
  return statusMap[status] || '未知'
}

const getTypeText = (type) => {
  const typeMap = {
    programming: '编程竞赛',
    ai: 'AI竞赛',
    design: '设计竞赛',
    innovation: '创新竞赛'
  }
  return typeMap[type] || '其他'
}

// 生命周期
onMounted(() => {
  loadEvents()
})
</script>

<style lang="scss" scoped>
.competition-events {
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
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="trophy-pattern" x="0" y="0" width="20" height="20" patternUnits="userSpaceOnUse"><polygon points="10,5 12,8 16,8 13,11 14,15 10,13 6,15 7,11 4,8 8,8" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23trophy-pattern)"/></svg>');
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
      color: #ffd700;
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

.filter-section {
  padding: 30px 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.filter-tabs {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-btn {
  border-radius: 25px;
  padding: 10px 20px;
  font-weight: 500;
}

.events-section {
  padding: 80px 0;
  background: #f8fafc;
  min-height: 60vh;
}

.competition-showcase {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 40px;
  margin-bottom: 60px;
  
  @media (max-width: 1200px) {
    grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
    gap: 30px;
  }
  
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: 25px;
  }
}

.competition-card {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.12);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 2px solid transparent;
  position: relative;
  min-height: 600px;
  
  &:hover {
    transform: translateY(-8px) scale(1.02);
    box-shadow: 0 25px 50px rgba(102, 126, 234, 0.2);
    border-color: rgba(102, 126, 234, 0.3);
  }
  
  &.status-registration {
    border-left: 6px solid #10b981;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #10b981, #34d399);
      z-index: 1;
    }
  }
  
  &.status-ongoing {
    border-left: 6px solid #f59e0b;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #f59e0b, #fbbf24);
      z-index: 1;
    }
  }
  
  &.status-ended {
    border-left: 6px solid #6b7280;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #6b7280, #9ca3af);
      z-index: 1;
    }
  }
}

.competition-status-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 3;
  
  .status-text {
    padding: 8px 16px;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 700;
    color: white;
    backdrop-filter: blur(15px);
    border: 2px solid rgba(255, 255, 255, 0.3);
    text-transform: uppercase;
    letter-spacing: 0.5px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  }
  
  .status-registration & .status-text {
    background: linear-gradient(135deg, #10b981, #34d399);
  }
  
  .status-ongoing & .status-text {
    background: linear-gradient(135deg, #f59e0b, #fbbf24);
  }
  
  .status-ended & .status-text {
    background: linear-gradient(135deg, #6b7280, #9ca3af);
  }
}

.competition-poster {
  position: relative;
  height: 280px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
  }
  
  .poster-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(
      to bottom,
      rgba(0, 0, 0, 0.1) 0%,
      rgba(0, 0, 0, 0.3) 50%,
      rgba(102, 126, 234, 0.9) 100%
    );
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 25px;
  }
  
  .poster-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 15px;
  }
  
  .poster-title {
    color: white;
    font-size: 1.1rem;
    font-weight: 700;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
    line-height: 1.3;
    flex: 1;
  }
  
  .competition-type {
    padding: 6px 12px;
    border-radius: 15px;
    backdrop-filter: blur(15px);
    font-size: 0.8rem;
    font-weight: 600;
    color: white;
    border: 1px solid rgba(255, 255, 255, 0.3);
    white-space: nowrap;
    
    &.type-programming {
      background: rgba(59, 130, 246, 0.4);
    }
    
    &.type-ai {
      background: rgba(168, 85, 247, 0.4);
    }
    
    &.type-design {
      background: rgba(236, 72, 153, 0.4);
    }
    
    &.type-innovation {
      background: rgba(34, 197, 94, 0.4);
    }
  }
  
  .prize-highlight {
    padding: 8px 16px;
    border-radius: 20px;
    background: linear-gradient(135deg, #ffd700, #ffed4e);
    color: #1a1a1a;
    font-size: 0.9rem;
    font-weight: 700;
    text-align: center;
    box-shadow: 0 4px 15px rgba(255, 215, 0, 0.4);
    border: 2px solid rgba(255, 255, 255, 0.5);
  }
}

.competition-info {
  padding: 30px;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
}

.competition-title {
  font-size: 1.4rem;
  font-weight: 700;
  margin: 0 0 18px 0;
  color: #1e293b;
  line-height: 1.3;
  text-align: center;
}

.competition-description {
  color: #64748b;
  line-height: 1.7;
  margin: 0 0 25px 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-align: center;
  font-size: 0.95rem;
}

.key-info {
  margin-bottom: 25px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  
  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .info-item {
    display: flex;
    align-items: center;
    gap: 10px;
    flex: 1;
    
    .el-icon {
      color: #667eea;
      font-size: 1.1rem;
      min-width: 20px;
    }
    
    .info-content {
      display: flex;
      flex-direction: column;
      gap: 2px;
      
      .label {
        font-size: 0.8rem;
        color: #64748b;
        font-weight: 500;
      }
      
      .value {
        font-size: 0.9rem;
        color: #1e293b;
        font-weight: 600;
      }
    }
  }
}

.competition-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 25px;
  justify-content: center;
}

.competition-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 25px;
  border-top: 2px solid #e2e8f0;
}

.competition-stats {
  display: flex;
  gap: 20px;
  color: #64748b;
  font-size: 0.9rem;
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 6px;
    
    .el-icon {
      color: #667eea;
    }
  }
}

.action-buttons {
  display: flex;
  gap: 12px;
  
  .action-button {
    border-radius: 20px;
    font-weight: 600;
    padding: 10px 20px;
    font-size: 0.9rem;
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
    color: #667eea;
  }
  
  h3 {
    font-size: 1.5rem;
    margin: 0 0 10px 0;
    color: #1e293b;
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
  
  .filter-section {
    padding: 20px 0;
  }
  
  .filter-tabs {
    gap: 10px;
  }
  
  .filter-btn {
    padding: 8px 16px;
    font-size: 0.9rem;
  }
  
  .events-section {
    padding: 60px 0;
  }
  
  .events-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .event-card {
    margin: 0 10px;
  }
  
  .event-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: center;
  }
}
</style>

// 图片错误兜底
const onCoverError = (e) => {
  e.target.src = '/vite.svg'
}