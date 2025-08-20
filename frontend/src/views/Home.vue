<template>
  <div class="home">
    <!-- 英雄区域 -->
    <section class="hero">
      <!-- 动态背景装饰 -->
      <div class="hero-bg-decorations">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
        <div class="floating-shape shape-4"></div>
        <div class="floating-shape shape-5"></div>
      </div>
      
      <!-- 粒子效果 -->
      <div class="particles">
        <div class="particle" v-for="i in 20" :key="i" :style="{ 
          '--delay': Math.random() * 3 + 's',
          '--duration': (Math.random() * 10 + 10) + 's',
          '--x': Math.random() * 100 + '%',
          '--y': Math.random() * 100 + '%'
        }"></div>
      </div>
      
      <div class="container">
        <div class="hero-content">
          <div class="hero-badge">
            <span class="badge-text">✨ 欢迎访问</span>
          </div>
          <h1 class="hero-title">
             <span class="title-line">探索技术的</span>
             <span class="title-line highlight">无限可能</span>
           </h1>
          <p class="hero-subtitle">
            在这里，我们分享前沿技术、记录成长历程、传递创新思维
            <br>
            与志同道合的朋友一起，构建更美好的数字世界
          </p>
          <div class="hero-stats">
            <div class="stat-item">
              <span class="stat-number">{{ totalArticles }}+</span>
              <span class="stat-label">技术文章</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ totalViews }}+</span>
              <span class="stat-label">阅读量</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ totalCategories }}+</span>
              <span class="stat-label">分类专题</span>
            </div>
          </div>
          <div class="hero-actions">
            <el-button type="primary" size="large" class="primary-btn" @click="$router.push('/articles')">
              <el-icon><Reading /></el-icon>
              开始探索
            </el-button>
            <el-button size="large" class="secondary-btn" @click="$router.push('/about')">
              <el-icon><User /></el-icon>
              关于我们
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 滚动提示 -->
      <div class="scroll-indicator" @click="scrollToNextSection">
        <div class="scroll-arrow">
          <el-icon><ArrowDown /></el-icon>
        </div>
        <span class="scroll-text">向下滚动探索更多</span>
      </div>
    </section>

    <!-- 分类和标签 -->
    <section class="categories-tags">
      <div class="container">
        <!-- 分类标题 -->
        <div class="section-header">
          <div class="section-bg-decoration">
            <div class="floating-element floating-1"></div>
            <div class="floating-element floating-2"></div>
          </div>
          <div class="header-badge">
            <el-icon><Folder /></el-icon>
          </div>
          <h2>文章分类</h2>
          <p class="section-subtitle">探索不同领域的精彩内容</p>
        </div>
        
        <!-- 分类网格 - 横向排布 -->
        <div class="categories-grid">
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-card"
            @click="goToCategory(category.slug)"
            :style="{ 
              '--category-color': category.color || '#667eea',
              '--category-color-shadow': category.colorShadow || 'rgba(102, 126, 234, 0.3)',
              '--category-gradient': category.gradient || 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
            }"
          >
            <div class="category-header">
              <div class="category-icon">
                <el-icon size="24">
                  <component :is="getIconComponent(category.icon)" />
                </el-icon>
              </div>
              <div class="category-badge">
                <span class="article-count">{{ category.articleCount }}</span>
              </div>
            </div>
            <div class="category-content">
              <h3 class="category-name">{{ category.name }}</h3>
              <p class="category-description">{{ category.description || `${category.articleCount} 篇文章` }}</p>
            </div>
            <div class="category-footer">
              <span class="view-articles">查看文章 →</span>
            </div>
          </div>
        </div>
        
        <!-- 热门标签 - 小标签横向排布 -->
        <div class="tags-section">
          <div class="tags-header">
            <span class="tags-label">热门标签</span>
          </div>
          <div class="tags-cloud">
            <el-tag
              v-for="tag in popularTags"
              :key="tag.id"
              size="small"
              :color="getTagColor(tag)"
              style="color: white; border: none;"
              @click="$router.push(`/tag/${tag.slug}`)"
              class="tag-item"
            >
              {{ tag.name }}
            </el-tag>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新文章 -->
    <section class="latest-articles">
      <div class="section-bg-decoration"></div>
      <div class="container">
        <div class="section-header">
          <div class="header-content">
            <div class="header-badge">
              <span class="badge-icon">📚</span>
              <span class="badge-text">最新发布</span>
            </div>
            <h2>精选文章</h2>
            <p class="section-subtitle">探索最新的技术趋势和深度思考</p>
          </div>
          <router-link to="/articles" class="more-link">
            <span>查看全部</span>
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        
        <div class="articles-grid" v-loading="loading">
          <article-card
            v-for="article in latestArticles"
            :key="article.id"
            :article="article"
          />
        </div>
      </div>
    </section>

    <!-- 每日一句 -->
    <section class="daily-quote">
      <div class="container">
        <div class="quote-header">
          <div class="quote-title">
            <el-icon><Star /></el-icon>
            每日一句
          </div>
          <div class="refresh-btn" @click="refreshQuote">
            <el-icon><Refresh /></el-icon>
          </div>
        </div>
        <div class="quote-content">
          {{ dailyQuote.text }}
        </div>
        <div class="quote-author">— {{ dailyQuote.author }}</div>
      </div>
    </section>

    <!-- 留言板 -->
    <section class="message-board">
      <div class="container">
        <div class="section-header">
          <div class="header-content">
            <div class="header-badge">
              <span class="badge-icon">💬</span>
              <span class="badge-text">留言反馈</span>
            </div>
            <h2>留言板</h2>
            <p class="section-subtitle">分享您的想法和建议</p>
          </div>
        </div>
        
        <div class="message-form">
          <el-form :model="messageForm" ref="messageFormRef" :rules="messageRules">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item prop="name">
                  <el-input v-model="messageForm.name" placeholder="您的昵称" size="large" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="email">
                  <el-input v-model="messageForm.email" placeholder="邮箱地址（可选）" size="large" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item prop="message">
              <el-input
                v-model="messageForm.message"
                type="textarea"
                :rows="4"
                placeholder="请输入您的留言或建议..."
                size="large"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" @click="submitMessage" :loading="submittingMessage">
                <el-icon><ChatDotRound /></el-icon>
                发送留言
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <div class="recent-messages">
          <h3>最近留言</h3>
          <div class="messages-list">
            <div v-for="message in recentMessages" :key="message.id" class="message-item">
              <div class="message-avatar">
                <el-avatar :size="40">{{ message.name.charAt(0) }}</el-avatar>
              </div>
              <div class="message-content">
                <div class="message-header">
                  <span class="message-name">{{ message.name }}</span>
                  <span class="message-time">{{ formatMessageTime(message.createdAt) }}</span>
                </div>
                <p class="message-text">{{ message.message }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 侧边栏组件 -->
    <div class="sidebar-widgets">
    </div>

    <!-- 回到顶部按钮 -->
    <div class="back-to-top" :class="{ 'show': showBackToTop }" @click="scrollToTop">
      <el-icon size="20"><Top /></el-icon>
    </div>

    <!-- 页面加载进度条 -->
    <div class="page-loading" :class="{ 'loading': pageLoading }">
      <div class="loading-bar"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { 
  Monitor, Edit, Notebook, Star, Document, Folder, 
  Cpu, Trophy, BellFilled, Tools, Reading, 
  ArrowDown, ArrowRight, User, CollectionTag,
  Promotion, DataAnalysis, MagicStick, Setting,
  Refresh, ChatDotRound, Top
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import ArticleCard from '@/components/ArticleCard.vue'

import { articleApi } from '@/api/article'
import tagApi, { getPublicAllTags } from '@/api/tag'
import { categoryApi } from '@/api/category'
import { commentApi } from '@/api/comment'
import { statsApi } from '@/api/stats'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const latestArticles = ref([])
const categories = ref([])
const popularTags = ref([])
const totalArticles = ref(0)
const totalViews = ref(0)
const totalCategories = ref(0)

// 每日一句相关
const dailyQuote = ref({
  text: '代码是写给人看的，只是偶尔在机器上运行。',
  author: 'Harold Abelson'
})

// 留言板相关
const messageForm = ref({
  name: '',
  email: '',
  message: ''
})
const messageFormRef = ref(null)
const submittingMessage = ref(false)
const recentMessages = ref([])

// 回到顶部相关
const showBackToTop = ref(false)

// 页面加载进度条
const pageLoading = ref(false)

// 表单验证规则
const messageRules = {
  name: [
    { required: true, message: '请输入您的昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  message: [
    { required: true, message: '请输入留言内容', trigger: 'blur' },
    { min: 5, max: 500, message: '留言长度在 5 到 500 个字符', trigger: 'blur' }
  ]
}

// 技术格言库
const quotes = [
  { text: '代码是写给人看的，只是偶尔在机器上运行。', author: 'Harold Abelson' },
  { text: '过早的优化是万恶之源。', author: 'Donald Knuth' },
  { text: '程序必须是为了给人看而写，给机器执行只是附带任务。', author: 'Hal Abelson' },
  { text: '简单是可靠的前提。', author: 'Edsger Dijkstra' },
  { text: '任何傻瓜都能写出计算机可以理解的代码。好程序员能够写出人能够理解的代码。', author: 'Martin Fowler' },
  { text: '调试的难度是最初写代码的两倍。因此，如果你尽可能聪明地写代码，那么你就没有足够的智慧来调试它。', author: 'Brian Kernighan' },
  { text: '计算机科学领域的任何问题都可以通过增加一个间接的中间层来解决。', author: 'David Wheeler' },
  { text: '软件设计有两种方式：一种是简单到明显没有缺陷，另一种是复杂到缺陷不明显。', author: 'C.A.R. Hoare' },
  { text: '编程不是科学，编程是手艺。', author: 'Richard Stallman' },
  { text: '好的代码本身就是最好的文档。', author: 'Steve McConnell' }
]

// 图标映射函数，为不同分类提供更丰富的图标
const getIconComponent = (iconName) => {
  const iconMap = {
    'Monitor': Monitor,
    'Edit': Edit,
    'Notebook': Notebook,
    'Star': Star,
    'Document': Document,
    'Folder': Folder,
    'Cpu': Cpu,
    'Trophy': Trophy,
    'BellFilled': BellFilled,
    'Tools': Tools,
    'Reading': Reading,
    'CollectionTag': CollectionTag,
    'Promotion': Promotion,
    'DataAnalysis': DataAnalysis,
    'MagicStick': MagicStick,
    'Setting': Setting
  }
  return iconMap[iconName] || Folder
}

// 为分类分配颜色主题
const getCategoryTheme = (categoryName, index) => {
  const themes = [
    { color: '#667eea', shadow: 'rgba(102, 126, 234, 0.3)', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
    { color: '#f093fb', shadow: 'rgba(240, 147, 251, 0.3)', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
    { color: '#4facfe', shadow: 'rgba(79, 172, 254, 0.3)', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
    { color: '#43e97b', shadow: 'rgba(67, 233, 123, 0.3)', gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
    { color: '#fa709a', shadow: 'rgba(250, 112, 154, 0.3)', gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
    { color: '#a8edea', shadow: 'rgba(168, 237, 234, 0.3)', gradient: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)' },
    { color: '#ff9a9e', shadow: 'rgba(255, 154, 158, 0.3)', gradient: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)' },
    { color: '#ffecd2', shadow: 'rgba(255, 236, 210, 0.3)', gradient: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)' }
  ]
  
  // 根据分类名称智能分配主题
  if (categoryName.includes('技术') || categoryName.includes('编程')) return themes[0]
  if (categoryName.includes('生活') || categoryName.includes('随笔')) return themes[1]
  if (categoryName.includes('学习') || categoryName.includes('笔记')) return themes[2]
  if (categoryName.includes('项目') || categoryName.includes('实战')) return themes[3]
  if (categoryName.includes('新闻') || categoryName.includes('资讯')) return themes[4]
  if (categoryName.includes('竞赛') || categoryName.includes('活动')) return themes[5]
  
  return themes[index % themes.length]
}

// 为分类智能分配图标
const getSmartIcon = (categoryName) => {
  if (categoryName.includes('技术') || categoryName.includes('编程')) return 'Cpu'
  if (categoryName.includes('生活') || categoryName.includes('随笔')) return 'Edit'
  if (categoryName.includes('学习') || categoryName.includes('笔记')) return 'Reading'
  if (categoryName.includes('项目') || categoryName.includes('实战')) return 'Tools'
  if (categoryName.includes('新闻') || categoryName.includes('资讯')) return 'BellFilled'
  if (categoryName.includes('竞赛') || categoryName.includes('活动')) return 'Trophy'
  if (categoryName.includes('分析') || categoryName.includes('数据')) return 'DataAnalysis'
  if (categoryName.includes('设置') || categoryName.includes('配置')) return 'Setting'
  if (categoryName.includes('推广') || categoryName.includes('营销')) return 'Promotion'
  if (categoryName.includes('收藏') || categoryName.includes('标签')) return 'CollectionTag'
  if (categoryName.includes('魔法') || categoryName.includes('特效')) return 'MagicStick'
  return 'Folder'
}

// 获取标签颜色（使用后台管理中的实际颜色）
const getTagColor = (tag) => {
  return tag.color || '#409EFF'
}

// 保留原有的getTagType方法作为备用
const getTagType = (count) => {
  if (count >= 20) return 'danger'
  if (count >= 10) return 'warning'
  if (count >= 5) return 'success'
  return 'info'
}

const goToCategory = (slug) => {
  if (!slug) return
  
  // 根据分类slug映射到对应的模块页面
  const categoryRouteMap = {
    'tech': '/tech-share',
    'life': '/life-essays', 
    'study': '/study-notes',
    'project': '/project-practice',
    'news': '/news-info',
    'contest': '/competition-events'
  }
  
  const targetRoute = categoryRouteMap[slug]
  if (targetRoute) {
    window.location.href = targetRoute
  } else {
    // 如果没有对应的模块页面，跳转到文章列表页面并按分类筛选
    window.location.href = `/articles?category=${slug}`
  }
}

const loadHomeData = async () => {
  loading.value = true
  try {
    // 1) 最新文章
    const latestRes = await articleApi.getPublishedArticles({ page: 0, size: 6, sortBy: 'publishedAt', sortDir: 'desc' })
    if (latestRes.code === 200 && latestRes.data && latestRes.data.content) {
      latestArticles.value = latestRes.data.content
      totalArticles.value = latestRes.data.totalElements || latestRes.data.content.length
    }

    // 2) 分类
    const catRes = await categoryApi.getAllCategories()
    if (catRes.code === 200 && Array.isArray(catRes.data)) {
      categories.value = catRes.data.map((c, index) => {
        const theme = getCategoryTheme(c.name, index)
        const smartIcon = getSmartIcon(c.name)
        
        return {
          id: c.id,
          name: c.name,
          slug: c.slug,
          description: c.description,
          articleCount: c.articleCount || 0,
          icon: smartIcon,
          color: theme.color,
          colorShadow: theme.shadow,
          gradient: theme.gradient
        }
      })
      totalCategories.value = categories.value.length
    }

    // 3) 热门标签
    const tagRes = await tagApi.getPopularTags(12)
    if (tagRes.code === 200 && Array.isArray(tagRes.data)) {
      popularTags.value = tagRes.data
    } else {
      // 兜底：取全部标签
      const allTagsRes = await getPublicAllTags()
      if (allTagsRes.code === 200 && Array.isArray(allTagsRes.data)) {
        popularTags.value = allTagsRes.data.slice(0, 12)
      }
    }

    // 4) 统计（基于最新文章计算总阅读量）
    totalViews.value = latestArticles.value.reduce((sum, a) => sum + (a.viewCount || 0), 0)
  } catch (e) {
    console.error('加载首页数据失败', e)
  } finally {
    loading.value = false
  }
}

const scrollToNextSection = () => {
  console.log('滚动按钮被点击了')
  
  // 尝试多个可能的目标元素
  const targets = [
    '.categories-tags',
    '.section-header',
    '.container'
  ]
  
  let targetElement = null
  for (const selector of targets) {
    targetElement = document.querySelector(selector)
    if (targetElement) {
      console.log(`找到目标元素: ${selector}`)
      break
    }
  }
  
  if (targetElement) {
    // 使用更兼容的滚动方法
    try {
      targetElement.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      })
      console.log('滚动执行成功')
    } catch (error) {
      console.error('scrollIntoView失败，使用备用方法:', error)
      // 备用滚动方法
      const targetTop = targetElement.offsetTop
      window.scrollTo({
        top: targetTop,
        behavior: 'smooth'
      })
    }
  } else {
    console.error('未找到目标滚动元素')
    // 如果找不到目标元素，就滚动一个固定距离
    window.scrollTo({
      top: window.innerHeight,
      behavior: 'smooth'
    })
  }
}

// 刷新每日一句
const refreshQuote = () => {
  const randomIndex = Math.floor(Math.random() * quotes.length)
  dailyQuote.value = quotes[randomIndex]
  
  // 更新本地存储
  const today = new Date().toDateString()
  localStorage.setItem('dailyQuoteDate', today)
  localStorage.setItem('dailyQuote', JSON.stringify(dailyQuote.value))
}

// 提交留言
const submitMessage = async () => {
  if (!messageFormRef.value) return
  
  try {
    const valid = await messageFormRef.value.validate()
    if (!valid) return
    
    submittingMessage.value = true
    
    // 调用评论API创建留言
    const commentData = {
      content: `【留言板】${messageForm.value.message}\n\n留言者：${messageForm.value.name}\n邮箱：${messageForm.value.email}`,
      articleId: null, // 留言板不关联具体文章
      parentId: null
    }
    
    await commentApi.createComment(commentData)
    
    // 重新加载最近留言
    await loadRecentMessages()
    
    // 重置表单
    messageForm.value = {
      name: '',
      email: '',
      message: ''
    }
    messageFormRef.value.resetFields()
    
    ElMessage.success('留言提交成功！')
  } catch (error) {
    console.error('提交留言失败:', error)
    ElMessage.error('留言提交失败，请稍后重试')
  } finally {
    submittingMessage.value = false
  }
}

// 格式化留言时间
const formatMessageTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return formatDate(date)
  }
}

// 回到顶部
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 监听滚动事件
const handleScroll = () => {
  showBackToTop.value = window.scrollY > 300
}

// 初始化每日一句
const initDailyQuote = () => {
  const today = new Date().toDateString()
  const savedDate = localStorage.getItem('dailyQuoteDate')
  
  if (savedDate !== today) {
    // 新的一天，随机选择一句
    refreshQuote()
    localStorage.setItem('dailyQuoteDate', today)
    localStorage.setItem('dailyQuote', JSON.stringify(dailyQuote.value))
  } else {
    // 同一天，使用保存的格言
    const savedQuote = localStorage.getItem('dailyQuote')
    if (savedQuote) {
      dailyQuote.value = JSON.parse(savedQuote)
    }
  }
}

// 加载最近留言
const loadRecentMessages = async () => {
  try {
    // 调用统计API获取最近评论
    const res = await statsApi.getRecentComments(10)
    if (res.code === 200 && Array.isArray(res.data)) {
      // 过滤出留言板的评论（包含【留言板】标识）
      const messageComments = res.data.filter(comment => 
        comment.content && comment.content.includes('【留言板】')
      )
      
      // 转换数据格式
      recentMessages.value = messageComments.map(comment => {
        // 解析留言内容
        const content = comment.content.replace('【留言板】', '').trim()
        const lines = content.split('\n')
        const message = lines[0] || ''
        const nameMatch = content.match(/留言者：(.+?)\n/)
        const emailMatch = content.match(/邮箱：(.+?)$/)
        
        return {
          id: comment.id,
          name: nameMatch ? nameMatch[1] : (comment.user?.nickname || '匿名用户'),
          email: emailMatch ? emailMatch[1] : '',
          message: message,
          createdAt: comment.createdAt
        }
      })
    } else {
      recentMessages.value = []
    }
  } catch (error) {
    console.error('加载最近留言失败:', error)
    recentMessages.value = []
  }
}

onMounted(() => {
  pageLoading.value = true
  loadHomeData().finally(() => {
    pageLoading.value = false
  })
  initDailyQuote()
  loadRecentMessages()
  
  // 添加滚动监听
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  // 清理滚动监听
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss" scoped>
.home {
  min-height: 100vh;
  background-image: url('/Home.png');
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  position: relative;
}

.home::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0,0,0,0.35), rgba(0,0,0,0.25));
  pointer-events: none;
}

.hero {
  background: transparent;
  color: white;
  padding: 140px 0 100px 0;
  text-align: center;
  position: relative;
  overflow: hidden;
  min-height: 100vh;
  display: flex;
  align-items: center;
  
  .hero-bg-decorations {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 1;
    
    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.15);
      backdrop-filter: blur(25px) saturate(180%);
      border: 1px solid rgba(255, 255, 255, 0.2);
      box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.3);
      animation: floatShape 15s ease-in-out infinite;
      
      &.shape-1 {
        width: 120px;
        height: 120px;
        top: 10%;
        left: 10%;
        animation-delay: 0s;
        background: linear-gradient(45deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.05));
      }
      
      &.shape-2 {
        width: 80px;
        height: 80px;
        top: 20%;
        right: 15%;
        animation-delay: 2s;
        background: linear-gradient(45deg, rgba(240, 147, 251, 0.35), rgba(240, 147, 251, 0.1));
      }
      
      &.shape-3 {
        width: 150px;
        height: 150px;
        bottom: 20%;
        left: 20%;
        animation-delay: 4s;
        background: linear-gradient(45deg, rgba(79, 172, 254, 0.3), rgba(79, 172, 254, 0.08));
      }
      
      &.shape-4 {
        width: 60px;
        height: 60px;
        top: 60%;
        right: 25%;
        animation-delay: 1s;
        background: linear-gradient(45deg, rgba(67, 233, 123, 0.35), rgba(67, 233, 123, 0.1));
      }
      
      &.shape-5 {
        width: 100px;
        height: 100px;
        bottom: 10%;
        right: 10%;
        animation-delay: 3s;
        background: linear-gradient(45deg, rgba(255, 200, 55, 0.3), rgba(255, 200, 55, 0.08));
      }
    }
  }
  
  .particles {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 1;
    
    .particle {
      position: absolute;
      width: 3px;
      height: 3px;
      background: rgba(255, 255, 255, 0.5);
      border-radius: 50%;
      animation: particleFloat var(--duration, 20s) ease-in-out infinite;
      animation-delay: var(--delay, 0s);
      left: var(--x, 50%);
      top: var(--y, 50%);
    }
  }

  .container {
    position: relative;
    z-index: 2;
  }

  .hero-content {
    max-width: 800px;
    margin: 0 auto;
    
    .hero-badge {
      display: inline-block;
      background: rgba(255, 255, 255, 0.15);
      backdrop-filter: blur(25px) saturate(180%);
      border: 1px solid rgba(255, 255, 255, 0.2);
      padding: 12px 24px;
      border-radius: 50px;
      margin-bottom: 30px;
      font-size: 0.9rem;
      font-weight: 600;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.3);
      
      .badge-text {
        background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }
    
    .hero-title {
      font-size: clamp(2.5rem, 5vw, 4rem);
      font-weight: 900;
      line-height: 1.1;
      margin-bottom: 25px;
      
      .title-line {
        display: block;
        background: linear-gradient(135deg, #fff 0%, #e3f2fd 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
      
      .highlight {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -10px;
          left: 0;
          width: 100%;
          height: 4px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
          border-radius: 2px;
          animation: glow 2s ease-in-out infinite;
        }
      }
    }
    
    .hero-subtitle {
      font-size: 1.2rem;
      line-height: 1.6;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 40px;
      font-weight: 400;
    }
    
    .hero-stats {
      display: flex;
      justify-content: center;
      gap: 40px;
      margin-bottom: 40px;
      
      .stat-item {
        text-align: center;
        
        .stat-number {
          display: block;
          font-size: 2rem;
          font-weight: 900;
          color: #fff;
          margin-bottom: 5px;
        }
        
        .stat-label {
          font-size: 0.9rem;
          color: rgba(255, 255, 255, 0.8);
          font-weight: 500;
        }
      }
    }
    
    .hero-actions {
      display: flex;
      justify-content: center;
      gap: 20px;
      flex-wrap: wrap;
      
      .primary-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        padding: 15px 30px;
        font-size: 1rem;
        font-weight: 600;
        border-radius: 50px;
        transition: all 0.3s ease;
        backdrop-filter: blur(25px) saturate(180%);
        box-shadow: 0 8px 32px rgba(102, 126, 234, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.2);
        
        &:hover {
          transform: translateY(-3px);
          box-shadow: 0 12px 40px rgba(102, 126, 234, 0.5);
        }
      }
      
      .secondary-btn {
        background: rgba(255, 255, 255, 0.15);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: white;
        padding: 15px 30px;
        font-size: 1rem;
        font-weight: 600;
        border-radius: 50px;
        backdrop-filter: blur(25px) saturate(180%);
        transition: all 0.3s ease;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.3);
        
        &:hover {
          background: rgba(255, 255, 255, 0.25);
          transform: translateY(-3px);
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
        }
      }
    }
  }
  
  .scroll-indicator {
    position: absolute;
    bottom: 30px;
    left: 50%;
    transform: translateX(-50%);
    text-align: center;
    color: rgba(255, 255, 255, 0.8);
    cursor: pointer;
    transition: all 0.3s ease;
    z-index: 100;
    pointer-events: auto;
    
    &:hover {
      color: rgba(255, 255, 255, 1);
      transform: translateX(-50%) translateY(-5px);
      
      .scroll-arrow {
        background: rgba(255, 255, 255, 0.2);
        border-color: rgba(255, 255, 255, 0.4);
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2), inset 0 1px 0 rgba(255, 255, 255, 0.4);
      }
    }
    
    .scroll-arrow {
      width: 50px;
      height: 50px;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(25px) saturate(180%);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 10px;
      animation: bounce 2s infinite;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.3);
      transition: all 0.3s ease;
      
      .el-icon {
        font-size: 1.2rem;
      }
    }
    
    .scroll-text {
      font-size: 0.85rem;
      font-weight: 500;
    }
  }
}

@keyframes floatShape {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

@keyframes particleFloat {
  0%, 100% { transform: translateY(0px) translateX(0px); opacity: 0.5; }
  50% { transform: translateY(-30px) translateX(20px); opacity: 1; }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  60% { transform: translateY(-5px); }
}

@keyframes glow {
  from { box-shadow: 0 0 20px rgba(102, 126, 234, 0.5); }
  to { box-shadow: 0 0 40px rgba(118, 75, 162, 0.8); }
}

.latest-articles {
  padding: 100px 0;
  background: transparent !important;
  position: relative;
  
  &::before {
    content: none;
    // 保留定位属性以便需要时恢复，但隐藏背景与透明度
    background: none !important;
    opacity: 0 !important;
  }
  
  .section-bg-decoration {
    display: none;
  }
  
  .container {
    position: relative;
    z-index: 2;
  }
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 50px;
    flex-wrap: wrap;
    gap: 30px;
    
    .header-content {
      flex: 1;
      
      .header-badge {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
        padding: 8px 20px;
        border-radius: 25px;
        font-size: 0.85rem;
        font-weight: 600;
        color: var(--el-color-primary);
        margin-bottom: 20px;
        border: 1px solid rgba(102, 126, 234, 0.2);
      }
      
      h2 {
        font-size: 3rem;
        font-weight: 800;
        color: var(--el-text-color-primary);
        margin: 0 0 15px 0;
        line-height: 1.2;
      }
      
      .section-subtitle {
        color: var(--el-text-color-regular);
        font-size: 1.1rem;
        font-weight: 500;
        margin: 0;
      }
    }
    
    .more-link {
      display: flex;
      align-items: center;
      gap: 10px;
      color: var(--el-color-primary);
      font-weight: 600;
      padding: 15px 25px;
      border-radius: 30px;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
      border: 2px solid rgba(102, 126, 234, 0.2);
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
        transition: left 0.6s ease;
      }
      
      &:hover {
        color: white;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        transform: translateY(-3px);
        box-shadow: 0 12px 35px rgba(102, 126, 234, 0.3);
        border-color: transparent;
        
        &::before {
          left: 100%;
        }
      }
      
      .el-icon {
        font-size: 1.1rem;
        transition: transform 0.3s ease;
      }
      
      &:hover .el-icon {
        transform: translateX(3px);
      }
    }
  }
  
  .articles-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 35px;
    margin-top: 50px;
  }
}

.categories-tags {
  background: transparent;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: transparent;
    opacity: 0;
  }
  
  .container {
    position: relative;
    z-index: 2;
  }
  
  .section-header {
    position: relative;
    text-align: center;
    margin-bottom: 60px;
    
    .section-bg-decoration {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
      
      .floating-element {
        position: absolute;
        border-radius: 50%;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
        animation: particleFloat 6s ease-in-out infinite;
        
        &.floating-1 {
          width: 80px;
          height: 80px;
          top: -20px;
          left: 10%;
          animation-delay: 0s;
        }
        
        &.floating-2 {
          width: 60px;
          height: 60px;
          top: -10px;
          right: 15%;
          animation-delay: 2s;
        }
      }
    }
    
    .header-badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 60px;
      height: 60px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 20px;
      color: white;
      font-size: 1.5rem;
      margin-bottom: 20px;
      box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
    }
    
    h2 {
      font-size: 2.5rem;
      font-weight: 800;
      color: white;
      margin: 0 0 15px 0;
      line-height: 1.2;
    }
    
    .section-subtitle {
      color: white;
      font-size: 1.1rem;
      font-weight: 500;
      margin: 0;
    }
  }
  
  .categories-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 30px;
    margin-bottom: 60px;
  }
  
  .category-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 30px 25px;
    text-align: center;
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    border: 2px solid rgba(255, 255, 255, 0.3);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 4px 16px rgba(0, 0, 0, 0.05),
      inset 0 1px 0 rgba(255, 255, 255, 0.8);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
      transition: left 0.6s ease;
      z-index: 1;
    }
    
    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: radial-gradient(circle at 50% 50%, var(--category-color, #667eea) 0%, transparent 70%);
      opacity: 0;
      transition: opacity 0.4s ease;
      z-index: 0;
      border-radius: 20px;
    }
    
    &:hover {
      transform: translateY(-12px) scale(1.08);
      box-shadow: 
        0 25px 50px var(--category-color-shadow, rgba(102, 126, 234, 0.5)),
        0 15px 35px var(--category-color-shadow, rgba(102, 126, 234, 0.4)),
        0 0 40px var(--category-color-shadow, rgba(102, 126, 234, 0.4)),
        0 0 60px var(--category-color-shadow, rgba(102, 126, 234, 0.3)),
        0 0 80px var(--category-color-shadow, rgba(102, 126, 234, 0.2)),
        0 0 100px var(--category-color-shadow, rgba(102, 126, 234, 0.1)),
        inset 0 2px 0 rgba(255, 255, 255, 0.5),
        inset 0 -1px 0 rgba(0, 0, 0, 0.1);
      border-color: var(--category-color, #667eea);
      border-width: 3px;
      filter: brightness(1.05) saturate(1.1);
      
      &::before {
        left: 100%;
      }
      
      &::after {
        opacity: 0.2;
        animation: cardGlowPulse 2s ease-in-out infinite;
      }
      
      .category-icon {
        transform: scale(1.1) rotate(5deg);
        box-shadow: 
          0 8px 25px var(--category-color-shadow, rgba(102, 126, 234, 0.5)),
          0 0 20px var(--category-color-shadow, rgba(102, 126, 234, 0.4)),
          0 0 40px var(--category-color-shadow, rgba(102, 126, 234, 0.3));
        
        .el-icon {
          transform: scale(1.2) rotate(-5deg);
          filter: 
            drop-shadow(0 0 8px rgba(255, 255, 255, 0.8))
            drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
          animation: iconPulse 2s ease-in-out infinite;
        }
      }
      
      .category-name {
        color: var(--category-color, #667eea);
        transform: translateY(-2px);
        text-shadow: 
          0 2px 8px var(--category-color-shadow, rgba(102, 126, 234, 0.3)),
          0 0 10px var(--category-color-shadow, rgba(102, 126, 234, 0.2));
      }
      
      .view-articles {
        color: var(--category-color, #667eea);
        transform: translateX(5px);
        text-shadow: 0 0 8px var(--category-color-shadow, rgba(102, 126, 234, 0.3));
        
        .el-icon {
          transform: translateX(3px) scale(1.2);
          filter: drop-shadow(0 0 6px var(--category-color, #667eea));
        }
      }
      
      .category-badge {
        transform: scale(1.05) translateY(-2px);
        box-shadow: 
          0 6px 20px var(--category-color-shadow, rgba(102, 126, 234, 0.4)),
          0 0 15px var(--category-color-shadow, rgba(102, 126, 234, 0.3)),
          0 0 30px var(--category-color-shadow, rgba(102, 126, 234, 0.2));
      }
    }
  }
  
  .category-header {
    padding: 30px 25px 20px 25px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    position: relative;
  }
  
  .category-icon {
    width: 60px;
    height: 60px;
    border-radius: 16px;
    background: var(--category-gradient, linear-gradient(135deg, #667eea 0%, #764ba2 100%));
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 
      0 4px 20px var(--category-color-shadow, rgba(102, 126, 234, 0.4)),
      0 8px 32px var(--category-color-shadow, rgba(102, 126, 234, 0.2)),
      0 2px 10px rgba(0, 0, 0, 0.15),
      0 0 0 1px rgba(255, 255, 255, 0.1),
      inset 0 1px 0 rgba(255, 255, 255, 0.3),
      inset 0 -1px 0 rgba(0, 0, 0, 0.1);
    position: relative;
    overflow: hidden;
    transform-style: preserve-3d;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(45deg, rgba(255, 255, 255, 0.1) 0%, transparent 50%, rgba(255, 255, 255, 0.1) 100%);
      opacity: 0;
      transition: all 0.4s ease;
      border-radius: 16px;
    }
    
    &::after {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, var(--category-color, #667eea) 0%, transparent 70%);
      opacity: 0;
      animation: iconGlowPulse 4s ease-in-out infinite;
      z-index: -1;
      border-radius: 50%;
    }
    
    // 悬停时的动画效果
    &:hover {
      animation: iconHoverBounce 0.6s ease-out;
      
      &::before {
        opacity: 1;
        background: linear-gradient(45deg, 
          rgba(255, 255, 255, 0.2) 0%, 
          rgba(255, 255, 255, 0.4) 25%,
          transparent 50%, 
          rgba(255, 255, 255, 0.4) 75%,
          rgba(255, 255, 255, 0.2) 100%);
        animation: iconShimmer 1.5s ease-in-out infinite;
      }
      
      &::after {
        opacity: 0.4;
        animation: iconGlowIntense 1s ease-in-out infinite;
      }
    }
    
    .el-icon {
      font-size: 24px;
      color: white;
      transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
      z-index: 1;
      position: relative;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
      transform-origin: center;
      
      // 悬停时图标的增强发光动画
      .category-card:hover & {
        animation: iconSpinScale 0.8s ease-out, iconFloat 2s ease-in-out infinite 0.8s;
        filter: 
          drop-shadow(0 0 8px rgba(255, 255, 255, 1.0))
          drop-shadow(0 0 16px rgba(255, 255, 255, 0.8))
          drop-shadow(0 4px 12px rgba(0, 0, 0, 0.5))
          drop-shadow(0 0 24px var(--category-color, #667eea))
          drop-shadow(0 0 36px var(--category-color, rgba(102, 126, 234, 0.8)))
          drop-shadow(0 0 48px var(--category-color, rgba(102, 126, 234, 0.6)))
          brightness(1.2)
          saturate(1.3);
        text-shadow: 
          0 0 10px rgba(255, 255, 255, 0.8),
          0 0 20px var(--category-color, rgba(102, 126, 234, 0.6)),
          0 0 30px var(--category-color, rgba(102, 126, 234, 0.4));
      }
    }
    
    // 增强悬停时的发光效果和阴影层次
    &:hover {
      box-shadow: 
        0 8px 40px var(--category-color-shadow, rgba(102, 126, 234, 0.6)),
        0 16px 64px var(--category-color-shadow, rgba(102, 126, 234, 0.4)),
        0 4px 20px rgba(0, 0, 0, 0.2),
        0 0 0 2px rgba(255, 255, 255, 0.2),
        0 0 20px var(--category-color, rgba(102, 126, 234, 0.8)),
        0 0 40px var(--category-color, rgba(102, 126, 234, 0.6)),
        0 0 60px var(--category-color, rgba(102, 126, 234, 0.4)),
        inset 0 2px 0 rgba(255, 255, 255, 0.4),
        inset 0 -2px 0 rgba(0, 0, 0, 0.15),
        0 0 0 0 var(--category-color, #667eea);
      animation: iconHoverBounce 0.6s ease-out, iconRippleBox 1.2s ease-out infinite 0.3s, iconGlowPulseHover 2s ease-in-out infinite;
    }
  }
  
  .category-badge {
    background: var(--category-gradient, linear-gradient(135deg, #667eea 0%, #764ba2 100%));
    color: white;
    padding: 8px 16px;
    border-radius: 25px;
    font-size: 13px;
    font-weight: 700;
    box-shadow: 0 4px 12px var(--category-color-shadow, rgba(102, 126, 234, 0.3));
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
      transition: left 0.5s ease;
    }
    
    &:hover::before {
      left: 100%;
    }
    
    .article-count {
      font-weight: 700;
      position: relative;
      z-index: 1;
    }
  }
  
  .category-content {
    padding: 0 25px;
    margin-bottom: 20px;
  }
  
  .category-name {
    font-size: 1.4rem;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 8px 0;
    transition: all 0.3s ease;
    line-height: 1.3;
  }
  
  .category-description {
    color: #64748b;
    font-size: 0.95rem;
    font-weight: 500;
    margin: 0;
    opacity: 0.8;
    line-height: 1.6;
  }
  
  .category-footer {
    padding: 0 25px 25px 25px;
    display: flex;
    justify-content: flex-end;
  }
  
  .view-articles {
    color: #64748b;
    font-size: 14px;
    font-weight: 600;
    transition: all 0.3s ease;
  }
  
  .tags-section {
    text-align: center;
    
    .tags-header {
      margin-bottom: 25px;
      
      .tags-label {
        display: inline-block;
        font-size: 1.2rem;
        font-weight: 600;
        color: var(--el-text-color-regular);
        padding: 8px 20px;
        background: rgba(255, 255, 255, 0.8);
        backdrop-filter: blur(25px) saturate(180%);
        border-radius: 25px;
        border: 1px solid rgba(255, 255, 255, 0.3);
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.4);
      }
    }
  }
  
  .tags-cloud {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
    padding: 25px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(35px) saturate(180%);
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.3);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.6);
    max-width: 800px;
    margin: 0 auto;
    
    .tag-item {
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      border-radius: 20px;
      font-weight: 500;
      padding: 6px 14px;
      border: 2px solid transparent;
      position: relative;
      overflow: hidden;
      font-size: 13px;
      background: rgba(255, 255, 255, 0.7);
      backdrop-filter: blur(25px) saturate(180%);
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.5);
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
        transition: left 0.5s ease;
      }
      
      &:hover {
        transform: translateY(-2px) scale(1.05);
        background: rgba(255, 255, 255, 0.85);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.7);
        border-color: rgba(102, 126, 234, 0.3);
        
        &::before {
          left: 100%;
        }
      }
    }
  }
}

// 强制白色文字覆盖
.latest-articles .section-header h2 {
  color: white !important;
}

.latest-articles .section-header .section-subtitle {
  color: white !important;
}

// 响应式设计
@media (max-width: 768px) {
  .hero {
    padding: 100px 0 80px 0;
    min-height: 80vh;
    
    .hero-content {
      .hero-title {
        font-size: 2.5rem;
      }
      
      .hero-subtitle {
        font-size: 1rem;
        margin-bottom: 30px;
      }
      
      .hero-stats {
        flex-direction: column;
        gap: 20px;
        margin-bottom: 30px;
        
        .stat-item {
          .stat-number {
            font-size: 1.5rem;
          }
        }
      }
      
      .hero-actions {
        flex-direction: column;
        align-items: center;
        
        .el-button {
          width: 100%;
          max-width: 250px;
        }
      }
    }
  }
  
  .latest-articles {
    padding: 60px 0;
    
    .section-header {
      flex-direction: column;
      align-items: flex-start;
      text-align: center;
      
      .header-content h2 {
        font-size: 2rem;
      }
    }
    
    .articles-grid {
      grid-template-columns: 1fr;
      gap: 25px;
    }
  }
}

// 平板设备响应式设计
@media (max-width: 1024px) {
  .latest-articles {
    .articles-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 30px;
    }
  }
  
  .categories-tags {
    .section-header {
      margin-bottom: 40px;
      
      h2 {
        font-size: 2rem;
      }
    }
    
    .categories-grid {
      grid-template-columns: 1fr;
      gap: 20px;
      margin-bottom: 40px;
    }
    
    .tags-cloud {
      padding: 20px;
      gap: 8px;
      
      .tag-item {
        font-size: 12px;
        padding: 5px 12px;
      }
    }
  }
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1.2) rotate(-5deg);
  }
  50% {
    transform: scale(1.3) rotate(-3deg);
  }
}

@keyframes iconBounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0) scale(1);
  }
  40% {
    transform: translateY(-3px) scale(1.05);
  }
  60% {
    transform: translateY(-1px) scale(1.02);
  }
}

// 新增的图标动画效果
@keyframes iconSpinScale {
  0% {
    transform: scale(1) rotate(0deg);
  }
  25% {
    transform: scale(1.1) rotate(90deg);
  }
  50% {
    transform: scale(1.2) rotate(180deg);
  }
  75% {
    transform: scale(1.1) rotate(270deg);
  }
  100% {
    transform: scale(1.15) rotate(360deg);
  }
}

@keyframes iconFloat {
  0%, 100% {
    transform: scale(1.15) rotate(360deg) translateY(0px);
  }
  25% {
    transform: scale(1.18) rotate(360deg) translateY(-2px);
  }
  50% {
    transform: scale(1.2) rotate(360deg) translateY(-4px);
  }
  75% {
    transform: scale(1.18) rotate(360deg) translateY(-2px);
  }
}

@keyframes iconHoverBounce {
  0% {
    transform: scale(1) translateY(0);
  }
  20% {
    transform: scale(1.05) translateY(-3px);
  }
  40% {
    transform: scale(1.1) translateY(-6px);
  }
  60% {
    transform: scale(1.08) translateY(-4px);
  }
  80% {
    transform: scale(1.12) translateY(-2px);
  }
  100% {
    transform: scale(1.1) translateY(0);
  }
}

@keyframes iconShimmer {
  0% {
    background-position: -200% 0;
    opacity: 0.8;
  }
  50% {
    background-position: 0% 0;
    opacity: 1;
  }
  100% {
    background-position: 200% 0;
    opacity: 0.8;
  }
}

@keyframes iconGlowPulse {
  0%, 100% {
    opacity: 0.1;
    transform: scale(1);
  }
  25% {
    opacity: 0.2;
    transform: scale(1.1);
  }
  50% {
    opacity: 0.3;
    transform: scale(1.2);
  }
  75% {
    opacity: 0.2;
    transform: scale(1.1);
  }
}

@keyframes iconGlowIntense {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1.2);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.4);
  }
}

@keyframes iconRipple {
  0% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.2);
  }
  100% {
    opacity: 0;
    transform: scale(1.5);
  }
}

@keyframes iconRippleBox {
  0% {
    box-shadow: 
      0 8px 40px var(--category-color-shadow, rgba(102, 126, 234, 0.6)),
      0 16px 64px var(--category-color-shadow, rgba(102, 126, 234, 0.4)),
      0 4px 20px rgba(0, 0, 0, 0.2),
      0 0 0 2px rgba(255, 255, 255, 0.2),
      0 0 20px var(--category-color, rgba(102, 126, 234, 0.8)),
      0 0 40px var(--category-color, rgba(102, 126, 234, 0.6)),
      0 0 60px var(--category-color, rgba(102, 126, 234, 0.4)),
      inset 0 2px 0 rgba(255, 255, 255, 0.4),
      inset 0 -2px 0 rgba(0, 0, 0, 0.15),
      0 0 0 0 var(--category-color, #667eea);
  }
  50% {
    box-shadow: 
      0 8px 40px var(--category-color-shadow, rgba(102, 126, 234, 0.6)),
      0 16px 64px var(--category-color-shadow, rgba(102, 126, 234, 0.4)),
      0 4px 20px rgba(0, 0, 0, 0.2),
      0 0 0 2px rgba(255, 255, 255, 0.2),
      0 0 20px var(--category-color, rgba(102, 126, 234, 0.8)),
      0 0 40px var(--category-color, rgba(102, 126, 234, 0.6)),
      0 0 60px var(--category-color, rgba(102, 126, 234, 0.4)),
      inset 0 2px 0 rgba(255, 255, 255, 0.4),
      inset 0 -2px 0 rgba(0, 0, 0, 0.15),
      0 0 0 12px rgba(102, 126, 234, 0.4);
  }
  100% {
    box-shadow: 
      0 8px 40px var(--category-color-shadow, rgba(102, 126, 234, 0.6)),
      0 16px 64px var(--category-color-shadow, rgba(102, 126, 234, 0.4)),
      0 4px 20px rgba(0, 0, 0, 0.2),
      0 0 0 2px rgba(255, 255, 255, 0.2),
      0 0 20px var(--category-color, rgba(102, 126, 234, 0.8)),
      0 0 40px var(--category-color, rgba(102, 126, 234, 0.6)),
      0 0 60px var(--category-color, rgba(102, 126, 234, 0.4)),
      inset 0 2px 0 rgba(255, 255, 255, 0.4),
      inset 0 -2px 0 rgba(0, 0, 0, 0.15),
      0 0 0 20px rgba(102, 126, 234, 0);
  }
}

@keyframes iconGlowPulseHover {
  0%, 100% {
    filter: brightness(1.2) saturate(1.3) drop-shadow(0 0 20px var(--category-color, rgba(102, 126, 234, 0.6)));
  }
  25% {
    filter: brightness(1.3) saturate(1.4) drop-shadow(0 0 30px var(--category-color, rgba(102, 126, 234, 0.8)));
  }
  50% {
    filter: brightness(1.4) saturate(1.5) drop-shadow(0 0 40px var(--category-color, rgba(102, 126, 234, 1.0)));
  }
  75% {
    filter: brightness(1.3) saturate(1.4) drop-shadow(0 0 30px var(--category-color, rgba(102, 126, 234, 0.8)));
  }
}

@keyframes cardGlowPulse {
  0%, 100% {
    opacity: 0.15;
    transform: scale(1);
  }
  25% {
    opacity: 0.25;
    transform: scale(1.02);
  }
  50% {
    opacity: 0.3;
    transform: scale(1.05);
  }
  75% {
    opacity: 0.25;
    transform: scale(1.02);
  }
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

@keyframes messageSlideIn {
  0% {
    opacity: 0;
    transform: translateX(-20px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes formFadeIn {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes quotePulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
}

@keyframes buttonGlow {
  0%, 100% {
    box-shadow: 
      0 8px 25px rgba(102, 126, 234, 0.3),
      0 4px 15px rgba(118, 75, 162, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
  }
  50% {
    box-shadow: 
      0 12px 35px rgba(102, 126, 234, 0.4),
      0 6px 20px rgba(118, 75, 162, 0.3),
      0 0 25px rgba(102, 126, 234, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  }
}

@keyframes glow {
  0%, 100% {
    box-shadow: 0 0 5px var(--category-color-shadow, rgba(102, 126, 234, 0.3));
  }
  50% {
    box-shadow: 0 0 20px var(--category-color-shadow, rgba(102, 126, 234, 0.6));
  }
}

/* 每日一句样式 */
.daily-quote {
  background: transparent;
  border-radius: 24px;
  padding: 40px;
  margin: 50px 0;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: none;
  backdrop-filter: none;
  border: none;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: formFadeIn 0.8s ease-out, quotePulse 6s ease-in-out infinite;
}

.daily-quote::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255,255,255,0.15), transparent);
  transform: rotate(45deg);
  animation: shimmer 4s infinite;
}

.daily-quote::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.daily-quote:hover {
  transform: translateY(-5px) scale(1.02);
  box-shadow: none;
}

.quote-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  position: relative;
  z-index: 2;
}

.quote-title {
  font-size: 1.8rem;
  font-weight: 400;
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
  font-family: 'Brush Script MT', 'Lucida Handwriting', 'Segoe Script', cursive;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.quote-title .el-icon {
  background: transparent;
  color: white;
  padding: 8px;
  border-radius: 12px;
  backdrop-filter: none;
  border: none;
  box-shadow: none;
}

.refresh-btn {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    0 8px 25px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.refresh-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: rotate(180deg) scale(1.1);
  box-shadow: 
    0 12px 35px rgba(0, 0, 0, 0.15),
    0 0 30px rgba(255, 255, 255, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
}

.refresh-btn:hover::before {
  opacity: 1;
}

.refresh-btn:active {
  transform: rotate(180deg) scale(0.95);
}

.quote-content {
  font-size: 1.4rem;
  line-height: 1.8;
  font-style: italic;
  text-align: center;
  position: relative;
  z-index: 2;
  font-weight: 400;
  letter-spacing: 0.5px;
  color: white;
  font-family: 'Brush Script MT', 'Lucida Handwriting', 'Segoe Script', cursive;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  padding: 0 20px;
}

.quote-content::before {
  content: '"';
  position: absolute;
  left: -10px;
  top: -20px;
  font-size: 4rem;
  opacity: 0.3;
  font-family: serif;
  color: rgba(255, 255, 255, 0.6);
}

.quote-content::after {
  content: '"';
  position: absolute;
  right: -10px;
  bottom: -40px;
  font-size: 4rem;
  opacity: 0.3;
  font-family: serif;
  color: rgba(255, 255, 255, 0.6);
}

.quote-author {
  text-align: center;
  margin-top: 20px;
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.8);
  font-family: 'Brush Script MT', 'Lucida Handwriting', 'Segoe Script', cursive;
  font-style: italic;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

/* 留言板样式 */
.message-board {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px) saturate(180%);
  border-radius: 24px;
  padding: 40px;
  margin: 50px 0;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.08),
    0 10px 30px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.8),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: formFadeIn 0.8s ease-out 0.2s both;
}

.message-board::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02) 0%, rgba(118, 75, 162, 0.02) 100%);
  pointer-events: none;
}

.message-board:hover {
  transform: translateY(-3px);
  box-shadow: 
    0 30px 80px rgba(0, 0, 0, 0.12),
    0 15px 40px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.9),
    0 0 0 1px rgba(255, 255, 255, 0.3);
}

.message-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 35px;
  font-size: 1.8rem;
  font-weight: 800;
  color: #2c3e50;
  position: relative;
  z-index: 2;
}

.message-header .el-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px;
  border-radius: 16px;
  box-shadow: 
    0 8px 25px rgba(102, 126, 234, 0.3),
    0 4px 15px rgba(118, 75, 162, 0.2);
  transition: all 0.3s ease;
}

.message-header .el-icon:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 
    0 12px 35px rgba(102, 126, 234, 0.4),
    0 6px 20px rgba(118, 75, 162, 0.3);
}

.message-form {
  margin-bottom: 40px;
  position: relative;
  z-index: 2;
}

.form-row {
  display: flex;
  gap: 25px;
  margin-bottom: 25px;
}

.form-row .el-form-item {
  flex: 1;
  margin-bottom: 0;
}

.form-row .el-input {
  border-radius: 12px;
  overflow: hidden;
}

.form-row .el-input__wrapper {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

.form-row .el-input__wrapper:hover {
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 
    0 6px 20px rgba(0, 0, 0, 0.08),
    0 0 20px rgba(102, 126, 234, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.form-row .el-input__wrapper.is-focus {
  border-color: #667eea;
  box-shadow: 
    0 8px 25px rgba(102, 126, 234, 0.15),
    0 0 30px rgba(102, 126, 234, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.el-textarea .el-textarea__inner {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  resize: none;
}

.el-textarea .el-textarea__inner:hover {
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 
    0 6px 20px rgba(0, 0, 0, 0.08),
    0 0 20px rgba(102, 126, 234, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.el-textarea .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 
    0 8px 25px rgba(102, 126, 234, 0.15),
    0 0 30px rgba(102, 126, 234, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 30px;
  padding: 15px 40px;
  color: white;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    0 8px 25px rgba(102, 126, 234, 0.3),
    0 4px 15px rgba(118, 75, 162, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
  animation: buttonGlow 3s ease-in-out infinite;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.submit-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 
    0 15px 40px rgba(102, 126, 234, 0.4),
    0 8px 25px rgba(118, 75, 162, 0.3),
    0 0 30px rgba(102, 126, 234, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:active {
  transform: translateY(-1px) scale(1.02);
}

.recent-messages {
  border-top: 2px solid rgba(102, 126, 234, 0.1);
  padding-top: 30px;
  position: relative;
}

.recent-messages::before {
  content: '';
  position: absolute;
  top: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 1px;
}

.messages-title {
  font-size: 1.4rem;
  font-weight: 700;
  margin-bottom: 25px;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 10px;
}

.messages-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.message-item {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 15px;
  border-left: 4px solid #667eea;
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.05),
    0 2px 8px rgba(0, 0, 0, 0.03),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  animation: messageSlideIn 0.6s ease-out;
}

.message-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.message-item:hover {
  transform: translateX(5px);
  box-shadow: 
    0 8px 25px rgba(0, 0, 0, 0.08),
    0 4px 15px rgba(0, 0, 0, 0.05),
    0 0 20px rgba(102, 126, 234, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.message-item:hover::before {
  width: 6px;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.message-author {
  font-weight: 700;
  color: #2c3e50;
  font-size: 1.1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.message-time {
  font-size: 0.9rem;
  color: #7f8c8d;
  background: rgba(102, 126, 234, 0.1);
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 500;
}

.message-content {
  color: #34495e;
  line-height: 1.6;
  font-size: 1rem;
  font-weight: 400;
  letter-spacing: 0.3px;
}

/* 回到顶部按钮样式 */
.back-to-top {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px);
}

.back-to-top:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.back-to-top.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.back-to-top.show:hover {
  transform: translateY(-3px);
}

/* 页面加载进度条样式 */
.page-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: rgba(102, 126, 234, 0.2);
  z-index: 9999;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.page-loading.loading {
  opacity: 1;
  visibility: visible;
}

.loading-bar {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
  animation: loading 2s ease-in-out infinite;
}

@keyframes loading {
  0% {
    width: 0%;
    margin-left: 0%;
  }
  50% {
    width: 75%;
    margin-left: 25%;
  }
  100% {
    width: 0%;
    margin-left: 100%;
  }
}

/* 侧边栏组件样式 */
.sidebar-widgets {
  position: fixed;
  top: 50%;
  left: 20px;
  transform: translateY(-50%);
  z-index: 999;
  display: flex;
  flex-direction: column;
  gap: 20px;
  pointer-events: none;
}

.sidebar-widgets > * {
  pointer-events: auto;
}

/* 英雄区域标题和天气组件样式 - 已移除天气组件相关样式 */

/* 响应式设计 */
@media (max-width: 768px) {
  .daily-quote,
  .message-board {
    margin: 20px 0;
    padding: 20px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .back-to-top {
    bottom: 20px;
    right: 20px;
    width: 45px;
    height: 45px;
  }
  
  .sidebar-widgets {
    left: 10px;
    gap: 15px;
  }
  
  .hero-title-section {
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
  }
  
  .quote-title {
    font-size: 1.3rem;
  }
  
  .quote-content {
    font-size: 1.1rem;
  }
}
</style>
