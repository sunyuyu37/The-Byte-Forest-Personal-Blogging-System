<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1>欢迎回来，{{ userStore.user?.nickname }}！</h1>
          <p>今天是 {{ currentDate }}，让我们开始创作吧</p>
        </div>
        <div class="welcome-actions">
          <el-button type="primary" size="large" @click="quickAction('article')">
            <el-icon><EditPen /></el-icon>
            写新文章
          </el-button>
          <el-button size="large" @click="quickAction('stats')">
            <el-icon><DataAnalysis /></el-icon>
            查看统计
          </el-button>
        </div>
      </div>
      <div class="welcome-decoration">
        <div class="floating-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="floating-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="floating-icon">
          <el-icon><Star /></el-icon>
        </div>
      </div>
    </div>

    <!-- 核心统计卡片 -->
    <div class="stats-overview">
      <div class="stat-card primary">
        <div class="stat-header">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-trend">
            <el-icon><TrendCharts /></el-icon>
            <span>+{{ stats.articleGrowth }}%</span>
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.articleCount }}</div>
          <div class="stat-label">文章总数</div>
          <div class="stat-desc">本月新增 {{ stats.monthlyArticles }} 篇</div>
        </div>
      </div>

      <div class="stat-card success">
        <div class="stat-header">
          <div class="stat-icon">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-trend">
            <el-icon><TrendCharts /></el-icon>
            <span>+{{ stats.viewGrowth }}%</span>
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(stats.viewCount) }}</div>
          <div class="stat-label">总阅读量</div>
          <div class="stat-desc">今日新增 {{ stats.todayViews }} 次</div>
        </div>
      </div>

      <div class="stat-card warning">
        <div class="stat-header">
          <div class="stat-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-trend">
            <el-icon><TrendCharts /></el-icon>
            <span>+{{ stats.commentGrowth }}%</span>
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.commentCount }}</div>
          <div class="stat-label">评论总数</div>
          <div class="stat-desc">待审核 {{ stats.pendingComments }} 条</div>
        </div>
      </div>

      <div class="stat-card info">
        <div class="stat-header">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-trend">
            <el-icon><TrendCharts /></el-icon>
            <span>+{{ stats.userGrowth }}%</span>
          </div>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.userCount }}</div>
          <div class="stat-label">用户总数</div>
          <div class="stat-desc">本周新增 {{ stats.weeklyUsers }} 人</div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧内容 -->
      <div class="content-left">
        <!-- 数据图表 -->
        <div class="chart-section">
          <div class="section-header">
            <h3>数据趋势</h3>
            <el-segmented v-model="chartPeriod" :options="chartOptions" @change="onPeriodChange" />
          </div>
          <div class="chart-container">
            <el-loading v-if="chartLoading" element-loading-text="加载图表数据...">
              <div style="height: 350px;"></div>
            </el-loading>
            <TrendChart v-else :data="trendData" :period="chartPeriod" />
          </div>
        </div>

        <!-- 分类统计和访问量图表 -->
        <div class="charts-row">
          <div class="chart-item">
            <div class="section-header">
              <h3>分类分布</h3>
            </div>
            <div class="chart-container">
              <el-loading v-if="categoryLoading" element-loading-text="加载数据...">
                <div style="height: 300px;"></div>
              </el-loading>
              <StatsOverviewChart v-else :data="categoryData" />
            </div>
          </div>
          <div class="chart-item">
            <div class="section-header">
              <h3>今日访问</h3>
            </div>
            <div class="chart-container">
              <el-loading v-if="visitLoading" element-loading-text="加载数据...">
                <div style="height: 280px;"></div>
              </el-loading>
              <VisitChart v-else :data="visitData" />
            </div>
          </div>
        </div>

        <!-- 热门文章 -->
        <div class="popular-articles">
          <div class="section-header">
            <h3>热门文章</h3>
            <el-button type="primary" link @click="$router.push('/admin/articles')">
              查看全部
            </el-button>
          </div>
          <div class="article-grid">
            <div
              v-for="article in popularArticles"
              :key="article.id"
              class="article-card"
            >
              <div class="article-rank">{{ article.rank }}</div>
              <div class="article-info">
                <h4>{{ article.title }}</h4>
                <div class="article-stats">
                  <span><el-icon><View /></el-icon> {{ formatNumber(article.views) }}</span>
                  <span><el-icon><ChatDotRound /></el-icon> {{ article.comments }}</span>
                  <span><el-icon><Star /></el-icon> {{ article.likes }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧内容 -->
      <div class="content-right">
        <!-- 快速操作 -->
        <div class="quick-actions">
          <h3>快速操作</h3>
          <div class="action-grid">
            <div class="action-item" @click="quickAction('article')">
              <el-icon><EditPen /></el-icon>
              <span>写文章</span>
            </div>
            <div class="action-item" @click="quickAction('category')">
              <el-icon><Folder /></el-icon>
              <span>管理分类</span>
            </div>
            <div class="action-item" @click="quickAction('comment')">
              <el-icon><ChatDotRound /></el-icon>
              <span>审核评论</span>
            </div>
            <div class="action-item" @click="quickAction('user')">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </div>
          </div>
        </div>

        <!-- 最新动态 -->
        <div class="recent-activities">
          <h3>最新动态</h3>
          <div class="activity-list">
            <div
              v-for="activity in recentActivities"
              :key="activity.id"
              class="activity-item"
            >
              <div class="activity-icon" :class="activity.type">
                <el-icon>
                  <component :is="activity.icon" />
                </el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-text">{{ activity.text }}</div>
                <div class="activity-time">{{ formatTime(activity.time) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 系统状态 -->
        <div class="system-status">
          <h3>系统状态</h3>
          <div class="status-list">
            <div class="status-item">
              <div class="status-label">服务器状态</div>
              <el-tag type="success">正常</el-tag>
            </div>
            <div class="status-item">
              <div class="status-label">数据库连接</div>
              <el-tag type="success">正常</el-tag>
            </div>
            <div class="status-item">
              <div class="status-label">缓存服务</div>
              <el-tag type="success">正常</el-tag>
            </div>
            <div class="status-item">
              <div class="status-label">存储空间</div>
              <el-tag type="warning">75% 已使用</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Document,
  View,
  ChatDotRound,
  User,
  TrendCharts,
  DataAnalysis,
  EditPen,
  Star,
  Folder
} from '@element-plus/icons-vue'
import { statsApi } from '@/api/stats'
import { articleApi } from '@/api/article'
import TrendChart from '@/components/charts/TrendChart.vue'
import StatsOverviewChart from '@/components/charts/StatsOverviewChart.vue'
import VisitChart from '@/components/charts/VisitChart.vue'

const userStore = useUserStore()
const router = useRouter()

// 当前日期
const currentDate = computed(() => {
  const date = new Date()
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 图表时间段选择
const chartPeriod = ref('week')
const chartOptions = [
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本年', value: 'year' }
]

// 统计数据
const stats = ref({
  articleCount: 0,
  viewCount: 0,
  commentCount: 0,
  userCount: 0,
  articleGrowth: 0,
  viewGrowth: 0,
  commentGrowth: 0,
  userGrowth: 0,
  monthlyArticles: 0,
  todayViews: 0,
  pendingComments: 0,
  weeklyUsers: 0
})

// 热门文章
const popularArticles = ref([])

// 最新动态
const recentActivities = ref([])

// 图表数据和加载状态
const chartLoading = ref(false)
const categoryLoading = ref(false)
const visitLoading = ref(false)

const trendData = ref({
  dates: [],
  articles: [],
  views: [],
  comments: []
})

const categoryData = ref({
  categories: [],
  articles: [],
  views: []
})

const visitData = ref({
  hours: [],
  visits: [],
  uniqueVisits: []
})

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前'
  } else if (diff < 86400000) {
    return Math.floor(diff / 3600000) + '小时前'
  } else {
    return Math.floor(diff / 86400000) + '天前'
  }
}

// 快速操作
const quickAction = (type) => {
  switch (type) {
    case 'article':
      router.push('/admin/articles/create')
      break
    case 'category':
      router.push('/admin/categories')
      break
    case 'comment':
      router.push('/admin/comments')
      break
    case 'user':
      router.push('/admin/users')
      break
    case 'stats':
      router.push('/admin/statistics')
      break
    default:
      console.log('未知操作:', type)
  }
}

// 加载仪表板数据
const loadData = async () => {
  try {
    // 加载统计数据
    const statsResponse = await statsApi.getDashboardStats()
    if (statsResponse.code === 200) {
      const realStats = statsResponse.data
      stats.value = {
        articleCount: realStats.articleCount || 0,
        articleGrowth: 12.5, // 暂时保留模拟数据，后续可以计算真实增长率
        monthlyArticles: 3, // 暂时保留模拟数据
        viewCount: realStats.viewCount || 0,
        viewGrowth: 8.3, // 暂时保留模拟数据
        todayViews: 89, // 暂时保留模拟数据
        commentCount: realStats.commentCount || 0,
        commentGrowth: 15.2, // 暂时保留模拟数据
        pendingComments: 2, // 暂时保留模拟数据
        userCount: realStats.userCount || 0,
        userGrowth: 6.7, // 暂时保留模拟数据
        weeklyUsers: 8 // 暂时保留模拟数据
      }
    }

    // 加载热门文章数据
    const popularResponse = await articleApi.getPopularArticles({ page: 1, size: 4 })
    if (popularResponse.code === 200 && popularResponse.data.content) {
      popularArticles.value = popularResponse.data.content.map((article, index) => ({
        id: article.id,
        rank: index + 1,
        title: article.title,
        views: article.viewCount || 0,
        comments: article.commentCount || 0,
        likes: article.likeCount || 0
      }))
    }

    // 加载最新动态数据
    const recentArticlesResponse = await statsApi.getRecentArticles(2)
    const recentCommentsResponse = await statsApi.getRecentComments(2)
    
    const activities = []
    
    // 添加最新文章动态
    if (recentArticlesResponse.code === 200 && recentArticlesResponse.data) {
      recentArticlesResponse.data.forEach(article => {
        activities.push({
          id: `article_${article.id}`,
          type: 'article',
          icon: 'Document',
          text: `用户 ${article.author?.username || '未知'} 发布了新文章《${article.title}》`,
          time: new Date(article.publishTime || article.createTime)
        })
      })
    }
    
    // 添加最新评论动态
    if (recentCommentsResponse.code === 200 && recentCommentsResponse.data) {
      recentCommentsResponse.data.forEach(comment => {
        activities.push({
          id: `comment_${comment.id}`,
          type: 'comment',
          icon: 'ChatDotRound',
          text: `用户 ${comment.author?.username || '匿名'} 评论了文章`,
          time: new Date(comment.createTime)
        })
      })
    }
    
    // 按时间排序并取前4条
    recentActivities.value = activities
      .sort((a, b) => new Date(b.time) - new Date(a.time))
      .slice(0, 4)
    
  } catch (error) {
    console.error('加载仪表板数据失败:', error)
    ElMessage.error('加载数据失败，请稍后重试')
    
    // 如果API调用失败，使用默认数据
    stats.value = {
      articleCount: 0,
      articleGrowth: 0,
      monthlyArticles: 0,
      viewCount: 0,
      viewGrowth: 0,
      todayViews: 0,
      commentCount: 0,
      commentGrowth: 0,
      pendingComments: 0,
      userCount: 0,
      userGrowth: 0,
      weeklyUsers: 0
    }
    popularArticles.value = []
    recentActivities.value = []
  }
}

// 加载趋势图表数据
const loadTrendData = async (period = 'week') => {
  chartLoading.value = true
  try {
    const response = await statsApi.getTrendData(period)
    if (response.code === 200 && response.data) {
      trendData.value = response.data
    } else {
      console.error('获取趋势数据失败:', response.message)
      ElMessage.error('获取趋势数据失败: ' + (response.message || '未知错误'))
      // 初始化空数据，避免图表错误
      trendData.value = {
        dates: [],
        articles: [],
        views: [],
        comments: []
      }
    }
  } catch (error) {
    console.error('加载趋势数据失败:', error)
    ElMessage.error('加载趋势数据失败')
    // 初始化空数据
    trendData.value = {
      dates: [],
      articles: [],
      views: [],
      comments: []
    }
  } finally {
    chartLoading.value = false
  }
}

// 加载分类统计数据
const loadCategoryData = async () => {
  categoryLoading.value = true
  try {
    const response = await statsApi.getCategoryStats()
    if (response.code === 200 && response.data) {
      categoryData.value = response.data
    } else {
      console.error('获取分类数据失败:', response.message)
      ElMessage.error('获取分类数据失败: ' + (response.message || '未知错误'))
      // 初始化空数据
      categoryData.value = {
        categories: [],
        articles: [],
        views: []
      }
    }
  } catch (error) {
    console.error('加载分类数据失败:', error)
    ElMessage.error('加载分类数据失败')
    // 初始化空数据
    categoryData.value = {
      categories: [],
      articles: [],
      views: []
    }
  } finally {
    categoryLoading.value = false
  }
}

// 加载访问量数据
const loadVisitData = async () => {
  visitLoading.value = true
  try {
    const response = await statsApi.getVisitStats('today')
    if (response.code === 200 && response.data) {
      visitData.value = response.data
    } else {
      console.error('获取访问数据失败:', response.message)
      ElMessage.error('获取访问数据失败: ' + (response.message || '未知错误'))
      // 初始化空数据
      visitData.value = {
        hours: [],
        visits: [],
        uniqueVisits: []
      }
    }
  } catch (error) {
    console.error('加载访问数据失败:', error)
    ElMessage.error('加载访问数据失败')
    // 初始化空数据
    visitData.value = {
      hours: [],
      visits: [],
      uniqueVisits: []
    }
  } finally {
    visitLoading.value = false
  }
}

// 时间段变化处理
const onPeriodChange = (period) => {
  loadTrendData(period)
}

onMounted(() => {
  loadData()
  loadTrendData(chartPeriod.value)
  loadCategoryData()
  loadVisitData()
})
</script>

<style scoped>
.dashboard {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 60px);
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 32px;
  color: white;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

.welcome-content {
  flex: 1;
}

.welcome-text h1 {
  font-size: 2.5rem;
  margin-bottom: 8px;
  font-weight: 700;
}

.welcome-text p {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 24px;
}

.welcome-actions {
  display: flex;
  gap: 16px;
}

.welcome-decoration {
  position: relative;
  width: 200px;
  height: 150px;
}

.floating-icon {
  position: absolute;
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  animation: bounce 3s ease-in-out infinite;
}

.floating-icon:nth-child(1) {
  top: 0;
  right: 0;
  animation-delay: 0s;
}

.floating-icon:nth-child(2) {
  top: 50px;
  right: 80px;
  animation-delay: 1s;
}

.floating-icon:nth-child(3) {
  top: 100px;
  right: 20px;
  animation-delay: 2s;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

/* 统计卡片 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.stat-card.primary::before {
  background: linear-gradient(90deg, #409eff, #79bbff);
}

.stat-card.success::before {
  background: linear-gradient(90deg, #67c23a, #95d475);
}

.stat-card.warning::before {
  background: linear-gradient(90deg, #e6a23c, #ebb563);
}

.stat-card.info::before {
  background: linear-gradient(90deg, #909399, #b1b3b8);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #409eff, #79bbff);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #67c23a, #95d475);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
}

.stat-card.info .stat-icon {
  background: linear-gradient(135deg, #909399, #b1b3b8);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #67c23a;
  font-weight: 600;
}

.stat-number {
  font-size: 2.2rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 1rem;
  color: #606266;
  font-weight: 500;
  margin-bottom: 8px;
}

.stat-desc {
  font-size: 0.85rem;
  color: #909399;
}

/* 主要内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 32px;
}

.content-left {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.content-right {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 通用卡片样式 */
.chart-section,
.popular-articles,
.quick-actions,
.recent-activities,
.system-status {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.chart-section:hover,
.popular-articles:hover,
.quick-actions:hover,
.recent-activities:hover,
.system-status:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f5f7fa;
}

.section-header h3 {
  color: #2c3e50;
  font-weight: 600;
  font-size: 1.2rem;
}

/* 图表区域 */
.chart-container {
  height: 300px;
}

.chart-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  background: #f5f7fa;
  border-radius: 12px;
  border: 2px dashed #dcdfe6;
}

.chart-placeholder p {
  margin: 16px 0;
  font-size: 1.1rem;
}

/* 图表行布局 */
.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.charts-row-full {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

/* 图表加载状态 */
.chart-loading {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 12px;
}

/* 时间段选择器 */
.period-selector {
  display: flex;
  gap: 8px;
}

.period-selector .el-button {
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 12px;
}

/* 热门文章 */
.article-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.article-card:hover {
  background: #e6f7ff;
  transform: translateX(4px);
}

.article-rank {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #79bbff);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
}

.article-info {
  flex: 1;
}

.article-info h4 {
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 1rem;
  font-weight: 600;
}

.article-stats {
  display: flex;
  gap: 16px;
  font-size: 0.85rem;
  color: #909399;
}

.article-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 快速操作 */
.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.action-item:hover {
  background: #e6f7ff;
  border-color: #409eff;
  transform: translateY(-2px);
}

.action-item .el-icon {
  font-size: 24px;
  color: #409eff;
}

.action-item span {
  font-size: 0.9rem;
  color: #606266;
  font-weight: 500;
}

/* 最新动态 */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.activity-item:hover {
  background: #e6f7ff;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  flex-shrink: 0;
}

.activity-icon.article {
  background: linear-gradient(135deg, #409eff, #79bbff);
}

.activity-icon.comment {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
}

.activity-icon.user {
  background: linear-gradient(135deg, #67c23a, #95d475);
}

.activity-icon.like {
  background: linear-gradient(135deg, #f56c6c, #f78989);
}

.activity-content {
  flex: 1;
}

.activity-text {
  color: #2c3e50;
  font-size: 0.9rem;
  margin-bottom: 4px;
  line-height: 1.4;
}

.activity-time {
  color: #909399;
  font-size: 0.8rem;
}

/* 系统状态 */
.status-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.status-label {
  color: #606266;
  font-size: 0.9rem;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .content-right {
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    display: grid;
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }
  
  .welcome-banner {
    flex-direction: column;
    text-align: center;
    padding: 24px;
  }
  
  .welcome-decoration {
    display: none;
  }
  
  .welcome-actions {
    justify-content: center;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .main-content {
    gap: 20px;
  }
  
  .action-grid {
    grid-template-columns: 1fr;
  }
  
  .content-right {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .welcome-text h1 {
    font-size: 2rem;
  }
  
  .welcome-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .welcome-actions .el-button {
    width: 100%;
  }
}
</style>