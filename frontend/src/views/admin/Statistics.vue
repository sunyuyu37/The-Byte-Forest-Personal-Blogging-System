<template>
  <div class="statistics-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>数据统计</h1>
      <p>博客数据分析与可视化</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ dashboardStats.articleCount || 0 }}</div>
          <div class="stat-label">文章总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ dashboardStats.commentCount || 0 }}</div>
          <div class="stat-label">评论总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ dashboardStats.viewCount || 0 }}</div>
          <div class="stat-label">总访问量</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ dashboardStats.userCount || 0 }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 评论词云图 -->
      <div class="chart-item full-width">
        <CommentWordCloud />
      </div>
      
      <!-- 文章发布趋势 -->
      <div class="chart-item">
        <div class="chart-card">
          <div class="chart-header">
            <h3>文章发布趋势</h3>
            <el-select v-model="articleTrendPeriod" @change="loadArticleTrend" size="small">
              <el-option label="最近一周" value="week" />
              <el-option label="最近一月" value="month" />
              <el-option label="最近一年" value="year" />
            </el-select>
          </div>
          <div class="chart-container" v-loading="articleTrendLoading">
            <div ref="articleTrendRef" class="chart"></div>
          </div>
        </div>
      </div>
      
      <!-- 评论趋势 -->
      <div class="chart-item">
        <div class="chart-card">
          <div class="chart-header">
            <h3>评论趋势</h3>
            <el-select v-model="commentTrendPeriod" @change="loadCommentTrend" size="small">
              <el-option label="最近一周" value="week" />
              <el-option label="最近一月" value="month" />
              <el-option label="最近一年" value="year" />
            </el-select>
          </div>
          <div class="chart-container" v-loading="commentTrendLoading">
            <div ref="commentTrendRef" class="chart"></div>
          </div>
        </div>
      </div>
      
      <!-- 文章分类统计 -->
      <div class="chart-item">
        <div class="chart-card">
          <div class="chart-header">
            <h3>文章分类统计</h3>
            <el-button @click="loadCategoryStats" size="small" :loading="categoryStatsLoading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
          <div class="chart-container" v-loading="categoryStatsLoading">
            <div ref="categoryStatsRef" class="chart"></div>
          </div>
        </div>
      </div>
      
      <!-- 热门文章 -->
      <div class="chart-item">
        <div class="chart-card">
          <div class="chart-header">
            <h3>热门文章排行</h3>
            <el-select v-model="popularArticlesLimit" @change="loadPopularArticles" size="small">
              <el-option label="前5篇" :value="5" />
              <el-option label="前10篇" :value="10" />
              <el-option label="前20篇" :value="20" />
            </el-select>
          </div>
          <div class="chart-container" v-loading="popularArticlesLoading">
            <div ref="popularArticlesRef" class="chart"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Document, 
  ChatDotRound, 
  View, 
  User, 
  Refresh 
} from '@element-plus/icons-vue'
import { statsApi } from '@/api/stats'
import CommentWordCloud from '@/components/charts/CommentWordCloud.vue'
import * as echarts from 'echarts'

// 仪表盘统计数据
const dashboardStats = ref({})

// 图表引用
const articleTrendRef = ref(null)
const commentTrendRef = ref(null)
const categoryStatsRef = ref(null)
const popularArticlesRef = ref(null)

// 图表实例
let articleTrendChart = null
let commentTrendChart = null
let categoryStatsChart = null
let popularArticlesChart = null

// 加载状态
const articleTrendLoading = ref(false)
const commentTrendLoading = ref(false)
const categoryStatsLoading = ref(false)
const popularArticlesLoading = ref(false)

// 图表参数
const articleTrendPeriod = ref('month')
const commentTrendPeriod = ref('month')
const popularArticlesLimit = ref(10)

// 加载仪表盘统计数据
const loadDashboardStats = async () => {
  try {
    const response = await statsApi.getDashboardStats()
    if (response.code === 200) {
      dashboardStats.value = response.data
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
}

// 加载文章发布趋势
const loadArticleTrend = async () => {
  articleTrendLoading.value = true
  try {
    const response = await statsApi.getArticleTrendData(articleTrendPeriod.value)
    if (response.code === 200) {
      renderArticleTrend(response.data)
    }
  } catch (error) {
    console.error('获取文章趋势数据失败:', error)
    ElMessage.error('获取文章趋势数据失败')
  } finally {
    articleTrendLoading.value = false
  }
}

// 渲染文章发布趋势图
const renderArticleTrend = (data) => {
  if (!articleTrendRef.value || !data) return
  
  if (!articleTrendChart) {
    articleTrendChart = echarts.init(articleTrendRef.value)
  }
  
  const dates = data.map(item => item[0])
  const counts = data.map(item => item[1])
  
  const option = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#667eea',
      borderWidth: 1,
      textStyle: {
        color: '#333',
        fontSize: 14
      },
      extraCssText: 'backdrop-filter: blur(10px); border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.1);',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#667eea',
          opacity: 0.6
        },
        lineStyle: {
          color: '#667eea',
          opacity: 0.6
        }
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#e2e8f0',
          width: 2
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: 500
      },
      splitLine: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: 500
      },
      splitLine: {
        lineStyle: {
          color: '#f1f5f9',
          type: 'dashed'
        }
      }
    },
    series: [{
      name: '文章数量',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      data: counts,
      itemStyle: {
        color: '#4facfe',
        borderWidth: 3,
        borderColor: '#fff',
        shadowColor: 'rgba(79, 172, 254, 0.4)',
        shadowBlur: 12
      },
      lineStyle: {
        width: 4,
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#4facfe' },
          { offset: 0.5, color: '#00f2fe' },
          { offset: 1, color: '#43e97b' }
        ]),
        shadowColor: 'rgba(79, 172, 254, 0.4)',
        shadowBlur: 12,
        shadowOffsetY: 6
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(79, 172, 254, 0.5)' },
          { offset: 0.5, color: 'rgba(0, 242, 254, 0.3)' },
          { offset: 1, color: 'rgba(67, 233, 123, 0.1)' }
        ])
      },
      emphasis: {
        focus: 'series',
        itemStyle: {
          shadowBlur: 20,
          shadowColor: 'rgba(102, 126, 234, 0.5)'
        }
      },
      animationDuration: 2000,
      animationEasing: 'cubicOut'
    }]
  }
  
  articleTrendChart.setOption(option)
}

// 加载评论趋势
const loadCommentTrend = async () => {
  commentTrendLoading.value = true
  try {
    const response = await statsApi.getCommentTrendData(commentTrendPeriod.value)
    if (response.code === 200) {
      if (response.data && response.data.length > 0) {
        renderCommentTrend(response.data)
      } else {
        ElMessage.warning('暂无评论趋势数据')
        // 清空图表
        if (commentTrendChart) {
          commentTrendChart.clear()
        }
      }
    } else {
      ElMessage.error(response.message || '获取评论趋势数据失败')
    }
  } catch (error) {
    console.error('获取评论趋势数据失败:', error)
    ElMessage.error('获取评论趋势数据失败')
  } finally {
    commentTrendLoading.value = false
  }
}

// 渲染评论趋势图
const renderCommentTrend = (data) => {
  if (!commentTrendRef.value || !data) return
  
  if (!commentTrendChart) {
    commentTrendChart = echarts.init(commentTrendRef.value)
  }
  
  const dates = data.map(item => item[0])
  const counts = data.map(item => item[1])
  
  const option = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#f093fb',
      borderWidth: 1,
      textStyle: {
        color: '#333',
        fontSize: 14
      },
      extraCssText: 'backdrop-filter: blur(10px); border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.1);',
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(240, 147, 251, 0.2)'
        }
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#e2e8f0',
          width: 2
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: 500
      },
      splitLine: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: 500
      },
      splitLine: {
        lineStyle: {
          color: '#f1f5f9',
          type: 'dashed'
        }
      }
    },
    series: [{
      name: '评论数量',
      type: 'bar',
      data: counts,
      barWidth: '60%',
      itemStyle: {
        color: function(params) {
          const colors = [
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#f093fb' },
              { offset: 1, color: '#f5576c' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#4facfe' },
              { offset: 1, color: '#00f2fe' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#43e97b' },
              { offset: 1, color: '#38f9d7' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#fa709a' },
              { offset: 1, color: '#fee140' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#a8edea' },
              { offset: 1, color: '#fed6e3' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#ff9a9e' },
              { offset: 1, color: '#fecfef' }
            ])
          ];
          return colors[params.dataIndex % colors.length];
        },
        borderRadius: [8, 8, 0, 0],
        shadowColor: 'rgba(102, 126, 234, 0.4)',
        shadowBlur: 10,
        shadowOffsetY: 5
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 20,
          shadowColor: 'rgba(240, 147, 251, 0.6)'
        }
      },
      animationDuration: 1500,
      animationEasing: 'elasticOut'
    }]
  }
  
  commentTrendChart.setOption(option)
}

// 加载分类统计
const loadCategoryStats = async () => {
  categoryStatsLoading.value = true
  try {
    const response = await statsApi.getArticleCategoryStats()
    if (response.code === 200) {
      renderCategoryStats(response.data)
    }
  } catch (error) {
    console.error('获取分类统计数据失败:', error)
    ElMessage.error('获取分类统计数据失败')
  } finally {
    categoryStatsLoading.value = false
  }
}

// 渲染分类统计图
const renderCategoryStats = (data) => {
  if (!categoryStatsRef.value || !data) return
  
  if (!categoryStatsChart) {
    categoryStatsChart = echarts.init(categoryStatsRef.value)
  }
  
  const colors = [
    ['#667eea', '#764ba2'],
    ['#f093fb', '#f5576c'],
    ['#4facfe', '#00f2fe'],
    ['#43e97b', '#38f9d7'],
    ['#fa709a', '#fee140'],
    ['#a8edea', '#fed6e3'],
    ['#ff9a9e', '#fecfef'],
    ['#ffecd2', '#fcb69f']
  ]
  
  const pieData = data.map((item, index) => ({
    name: item[0],
    value: item[1],
    itemStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
        { offset: 0, color: colors[index % colors.length][0] },
        { offset: 1, color: colors[index % colors.length][1] }
      ]),
      shadowColor: 'rgba(0, 0, 0, 0.2)',
      shadowBlur: 10,
      shadowOffsetY: 5
    }
  }))
  
  const option = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#667eea',
      borderWidth: 1,
      textStyle: {
        color: '#333',
        fontSize: 14
      },
      extraCssText: 'backdrop-filter: blur(10px); border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.1);',
      formatter: '{a} <br/>{b}: {c} 篇 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      textStyle: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: 500
      },
      itemGap: 12,
      icon: 'circle',
      itemWidth: 12,
      itemHeight: 12
    },
    series: [{
      name: '文章分类',
      type: 'pie',
      radius: ['45%', '75%'],
      center: ['65%', '50%'],
      avoidLabelOverlap: false,
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold',
          color: '#333'
        },
        itemStyle: {
          shadowBlur: 20,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        },
        scaleSize: 5
      },
      labelLine: {
        show: false
      },
      data: pieData,
      animationType: 'scale',
      animationEasing: 'elasticOut',
      animationDelay: function (idx) {
        return idx * 100
      }
    }]
  }
  
  categoryStatsChart.setOption(option)
}

// 加载热门文章
const loadPopularArticles = async () => {
  popularArticlesLoading.value = true
  try {
    const response = await statsApi.getPopularArticlesData(popularArticlesLimit.value)
    if (response.code === 200) {
      renderPopularArticles(response.data)
    }
  } catch (error) {
    console.error('获取热门文章数据失败:', error)
    ElMessage.error('获取热门文章数据失败')
  } finally {
    popularArticlesLoading.value = false
  }
}

// 渲染热门文章图
const renderPopularArticles = (data) => {
  if (!popularArticlesRef.value || !data) return
  
  if (!popularArticlesChart) {
    popularArticlesChart = echarts.init(popularArticlesRef.value)
  }
  
  const titles = data.map(item => item[0])
  const views = data.map(item => item[1])
  
  const option = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#667eea',
      borderWidth: 1,
      textStyle: {
        color: '#333',
        fontSize: 14
      },
      extraCssText: 'backdrop-filter: blur(10px); border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.1);',
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(102, 126, 234, 0.1)'
        }
      },
      formatter: function(params) {
        return `${params[0].name}<br/>访问量: ${params[0].value} 次`
      }
    },
    grid: {
      left: '5%',
      right: '8%',
      bottom: '8%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: 500
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(100, 116, 139, 0.1)',
          type: 'dashed'
        }
      }
    },
    yAxis: {
      type: 'category',
      data: titles,
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: 500,
        formatter: function(value) {
          return value.length > 15 ? value.substring(0, 15) + '...' : value
        }
      }
    },
    series: [{
      name: '访问量',
      type: 'bar',
      data: views,
      barWidth: '60%',
      itemStyle: {
        color: function(params) {
          const colors = [
            new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#f093fb' },
              { offset: 1, color: '#f5576c' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#4facfe' },
              { offset: 1, color: '#00f2fe' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#43e97b' },
              { offset: 1, color: '#38f9d7' }
            ]),
            new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#fa709a' },
              { offset: 1, color: '#fee140' }
            ])
          ];
          return colors[params.dataIndex % colors.length];
        },
        borderRadius: [0, 8, 8, 0],
        shadowColor: 'rgba(102, 126, 234, 0.3)',
        shadowBlur: 10,
        shadowOffsetY: 2
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#7c3aed' },
            { offset: 1, color: '#a855f7' }
          ]),
          shadowBlur: 15,
          shadowOffsetY: 4
        }
      },
      animationDelay: function (idx) {
        return idx * 100
      },
      animationEasing: 'elasticOut'
    }]
  }
  
  popularArticlesChart.setOption(option)
}

// 初始化所有图表
const initCharts = async () => {
  await nextTick()
  loadArticleTrend()
  loadCommentTrend()
  loadCategoryStats()
  loadPopularArticles()
}

// 处理窗口大小变化
const handleResize = () => {
  if (articleTrendChart) articleTrendChart.resize()
  if (commentTrendChart) commentTrendChart.resize()
  if (categoryStatsChart) categoryStatsChart.resize()
  if (popularArticlesChart) popularArticlesChart.resize()
}

// 组件挂载
onMounted(() => {
  loadDashboardStats()
  initCharts()
  window.addEventListener('resize', handleResize)
})

// 组件卸载
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (articleTrendChart) articleTrendChart.dispose()
  if (commentTrendChart) commentTrendChart.dispose()
  if (categoryStatsChart) categoryStatsChart.dispose()
  if (popularArticlesChart) popularArticlesChart.dispose()
})
</script>

<style scoped>
.statistics-page {
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  position: relative;
}

.statistics-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.2) 0%, transparent 50%);
  pointer-events: none;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
  position: relative;
  z-index: 1;
}

.page-header h1 {
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff, #e3f2fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 12px 0;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.page-header p {
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  font-size: 16px;
  font-weight: 300;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 28px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
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
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #f5576c);
  background-size: 300% 100%;
  animation: gradientShift 3s ease-in-out infinite;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.15),
    0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-card:nth-child(1) .stat-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-card:nth-child(2) .stat-icon {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-card:nth-child(3) .stat-icon {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.stat-card:nth-child(4) .stat-icon {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease;
}

.stat-card:hover .stat-icon {
  transform: rotate(5deg) scale(1.1);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 6px;
  line-height: 1;
}

.stat-label {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 24px;
  position: relative;
  z-index: 1;
}

.chart-item.full-width {
  grid-column: 1 / -1;
}

.chart-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 28px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.05);
  height: 450px;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.chart-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-4px);
  box-shadow: 
    0 16px 48px rgba(0, 0, 0, 0.15),
    0 4px 12px rgba(0, 0, 0, 0.1);
}

.chart-card:hover::before {
  opacity: 1;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
  position: relative;
}

.chart-header::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 1px;
}

.chart-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #1e293b, #475569);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.chart-container {
  flex: 1;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 美化选择器和按钮 */
:deep(.el-select) {
  --el-select-border-color-hover: #667eea;
  --el-select-border-color-focus: #667eea;
}

:deep(.el-button) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  color: white;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  background: linear-gradient(135deg, #5a67d8, #6b46c1);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  }
}

@media (max-width: 768px) {
  .statistics-page {
    padding: 16px;
  }
  
  .page-header h1 {
    font-size: 28px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .chart-card {
    height: 350px;
    padding: 20px;
  }
  
  .chart-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
    font-size: 24px;
  }
  
  .stat-number {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .statistics-page {
    padding: 12px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .chart-card {
    height: 300px;
    padding: 16px;
  }
}
</style>