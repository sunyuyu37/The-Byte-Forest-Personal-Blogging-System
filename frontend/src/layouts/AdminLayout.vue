<template>
  <div class="admin-layout">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
      </div>
    </div>
    
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="300px">
        <div class="sidebar">
          <div class="logo">
            <div class="logo-container">
              <div class="logo-icon">
                <div class="icon-wrapper">
                  <el-icon :size="32"><Setting /></el-icon>
                </div>
              </div>
              <div class="logo-text">
                <h2>博客管理</h2>
                <span>Blog Admin</span>
              </div>
            </div>
          </div>
          
          <div class="menu-section">
            <div class="menu-title">主要功能</div>
            <el-menu
              :default-active="activeMenu"
              router
              :collapse="false"
              class="admin-menu"
            >
              <el-menu-item index="/admin" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><Odometer /></el-icon>
                  </div>
                  <span>仪表盘</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
              <el-menu-item index="/admin/articles" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><Document /></el-icon>
                  </div>
                  <span>文章管理</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
              <el-menu-item index="/admin/categories" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><Folder /></el-icon>
                  </div>
                  <span>分类管理</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
              <el-menu-item index="/admin/tags" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><Collection /></el-icon>
                  </div>
                  <span>标签管理</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
              <el-menu-item index="/admin/comments" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><ChatDotRound /></el-icon>
                  </div>
                  <span>评论管理</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
              <el-menu-item index="/admin/users" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><User /></el-icon>
                  </div>
                  <span>用户管理</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
              <el-menu-item index="/admin/statistics" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><TrendCharts /></el-icon>
                  </div>
                  <span>数据统计</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
              <el-menu-item index="/admin/files" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><FolderOpened /></el-icon>
                  </div>
                  <span>文件管理</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
              <el-menu-item index="/admin/announcements" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><Bell /></el-icon>
                  </div>
                  <span>公告管理</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
            </el-menu>
          </div>
          
          <div class="menu-section">
            <div class="menu-title">系统管理</div>
            <el-menu
              :default-active="activeMenu"
              router
              :collapse="false"
              class="admin-menu"
            >
              <el-menu-item index="/admin/token-status" class="menu-item">
                <div class="menu-item-content">
                  <div class="menu-icon">
                    <el-icon><Key /></el-icon>
                  </div>
                  <span>Token监控</span>
                  <div class="menu-indicator"></div>
                </div>
              </el-menu-item>
            </el-menu>
          </div>
        </div>
      </el-aside>
      
      <!-- 主要内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header height="90px">
          <div class="header">
            <!-- 装饰背景 -->
            <div class="header-decoration">
              <div class="decoration-shape shape-1"></div>
              <div class="decoration-shape shape-2"></div>
              <div class="decoration-shape shape-3"></div>
            </div>
            
            <div class="header-content">
              <div class="header-left">
                <!-- 页面标题区域 -->
                <div class="page-title-section">
                  <div class="title-wrapper">
                    <div class="title-icon">
                      <el-icon :size="24"><component :is="currentPageIcon" /></el-icon>
                    </div>
                    <div class="title-content">
                      <h1 class="page-title">{{ pageTitle }}</h1>
                      <div class="page-subtitle">{{ pageSubtitle }}</div>
                    </div>
                  </div>
                  
                  <!-- 面包屑导航 -->
                  <el-breadcrumb separator="/" class="breadcrumb">
                    <el-breadcrumb-item to="/admin">
                      <el-icon><HomeFilled /></el-icon>
                      <span>控制台</span>
                    </el-breadcrumb-item>
                    <el-breadcrumb-item v-if="breadcrumb">
                      <span>{{ breadcrumb }}</span>
                    </el-breadcrumb-item>
                  </el-breadcrumb>
                </div>
                
                <!-- 快速操作按钮 -->
                <div class="quick-actions">
                  <el-tooltip content="新建文章" placement="bottom">
                    <div class="quick-btn primary" @click="quickAction('article')">
                      <el-icon><EditPen /></el-icon>
                      <span>写文章</span>
                    </div>
                  </el-tooltip>
                  <el-tooltip content="数据统计" placement="bottom">
                    <div class="quick-btn" @click="quickAction('stats')">
                      <el-icon><TrendCharts /></el-icon>
                    </div>
                  </el-tooltip>
                </div>
              </div>
              
              <div class="header-right">
                <div class="header-actions">
                  <!-- 数字时钟 -->
                  <div class="clock-container">
                    <div class="clock-box">
                      <el-icon class="clock-icon"><Clock /></el-icon>
                      <div class="clock-content">
                        <div class="time-display">{{ formatTime }}</div>
                        <div class="date-display">{{ formatDate }}</div>
                      </div>
                      <div class="clock-controls">
                        <el-tooltip :content="is24Hour ? '切换12小时制' : '切换24小时制'" placement="bottom">
                          <button class="time-format-btn" @click="toggleTimeFormat">
                            {{ is24Hour ? '24H' : '12H' }}
                          </button>
                        </el-tooltip>
                      </div>
                    </div>
                  </div>
                  
                  <!-- 系统状态 -->
                  <div class="system-status">
                    <el-tooltip content="系统状态良好" placement="bottom">
                      <div class="status-indicator online">
                        <div class="status-dot"></div>
                        <span>在线</span>
                      </div>
                    </el-tooltip>
                  </div>
                  
                  <!-- 通知中心 -->
                  <el-dropdown trigger="click" class="notification-dropdown" @visible-change="onNotificationDropdownChange">
                    <div class="action-btn notification-btn">
                      <el-badge :value="unreadNotificationCount" :hidden="unreadNotificationCount === 0" :max="99">
                        <el-icon :size="20"><Bell /></el-icon>
                      </el-badge>
                      <div class="btn-label">通知</div>
                    </div>
                    <template #dropdown>
                      <div class="notification-panel">
                        <div class="notification-header">
                          <h3>通知中心</h3>
                          <div class="header-actions">
                            <el-button text size="small" @click="markAllAsRead" :disabled="unreadNotificationCount === 0">
                              全部已读
                            </el-button>
                            <el-button text size="small" @click="(event) => clearAllNotifications(event)">
                              清空
                            </el-button>
                          </div>
                        </div>
                        <div class="notification-tabs">
                          <div 
                            class="tab-item" 
                            :class="{ active: activeNotificationTab === 'all' }"
                            @click="activeNotificationTab = 'all'"
                          >
                            全部 ({{ notifications.length }})
                          </div>
                          <div 
                            class="tab-item" 
                            :class="{ active: activeNotificationTab === 'unread' }"
                            @click="activeNotificationTab = 'unread'"
                          >
                            未读 ({{ unreadNotificationCount }})
                          </div>
                        </div>
                        <div class="notification-list" v-if="filteredNotifications.length > 0">
                          <div 
                            class="notification-item" 
                            v-for="item in filteredNotifications" 
                            :key="item.id"
                            :class="{ unread: !item.isRead }"
                            @click="markAsRead(item)"
                          >
                            <div class="notification-avatar" :class="item.type">
                              <el-icon>
                                <component :is="getNotificationIcon(item.type)" />
                              </el-icon>
                            </div>
                            <div class="notification-content">
                              <div class="notification-title">{{ item.title }}</div>
                              <div class="notification-desc">{{ item.description }}</div>
                              <div class="notification-time">{{ item.time }}</div>
                            </div>
                            <div class="notification-actions" v-if="!item.isRead">
                              <el-button size="small" text @click.stop="markAsRead(item)">
                                标记已读
                              </el-button>
                            </div>
                          </div>
                        </div>
                        <div class="notification-empty" v-else>
                          <el-icon><Bell /></el-icon>
                          <p>{{ activeNotificationTab === 'unread' ? '暂无未读通知' : '暂无通知' }}</p>
                        </div>
                        <div class="notification-footer">
                          <el-button text @click="viewAllNotifications">查看全部通知</el-button>
                        </div>
                      </div>
                    </template>
                  </el-dropdown>
                  
                  <!-- 主题切换 -->
                  <el-dropdown trigger="click" class="theme-dropdown">
                    <div class="action-btn theme-btn">
                      <el-icon :size="20"><component :is="currentTheme.icon" /></el-icon>
                      <div class="btn-label">主题</div>
                    </div>
                    <template #dropdown>
                      <div class="theme-panel">
                        <div class="theme-header">
                          <h3>选择主题</h3>
                          <p>个性化您的界面体验</p>
                        </div>
                        <div class="theme-list">
                          <div 
                            class="theme-item" 
                            v-for="theme in themes" 
                            :key="theme.key"
                            :class="{ active: currentThemeKey === theme.key }"
                            @click="switchTheme(theme.key)"
                          >
                            <div class="theme-preview" :style="{ background: theme.preview }">
                              <div class="theme-icon">
                                <el-icon><component :is="theme.icon" /></el-icon>
                              </div>
                            </div>
                            <div class="theme-info">
                              <div class="theme-name">{{ theme.name }}</div>
                              <div class="theme-desc">{{ theme.description }}</div>
                            </div>
                            <div class="theme-check" v-if="currentThemeKey === theme.key">
                              <el-icon><Check /></el-icon>
                            </div>
                          </div>
                        </div>
                      </div>
                    </template>
                  </el-dropdown>
                  
                  <!-- 全屏切换 -->
                  <el-tooltip content="全屏模式" placement="bottom">
                    <div class="action-btn fullscreen-btn" @click="toggleFullscreen">
                      <el-icon :size="20"><FullScreen /></el-icon>
                      <div class="btn-label">全屏</div>
                    </div>
                  </el-tooltip>
                  
                  <!-- 用户菜单 -->
                  <el-dropdown @command="handleCommand" class="user-dropdown" trigger="click">
                    <div class="user-profile">
                      <div class="user-avatar-container">
                        <el-avatar :src="userStore.user?.avatar" :size="44" class="user-avatar">
                          {{ userStore.user?.nickname?.charAt(0) || 'A' }}
                        </el-avatar>
                        <div class="online-indicator"></div>
                      </div>
                      <div class="user-info">
                        <div class="user-name">{{ userStore.user?.nickname || '管理员' }}</div>
                      </div>
                      <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
                    </div>
                    <template #dropdown>
                      <el-dropdown-menu class="user-dropdown-panel">
                        <div class="dropdown-header">
                          <div class="user-card">
                            <el-avatar :src="userStore.user?.avatar" :size="60">
                              {{ userStore.user?.nickname?.charAt(0) || 'A' }}
                            </el-avatar>
                            <div class="user-details">
                              <h4>{{ userStore.user?.nickname || '管理员' }}</h4>
                            </div>
                          </div>
                        </div>
                        <div class="dropdown-menu">
                          <el-dropdown-item command="home" class="menu-item">
                            <el-icon><HomeFilled /></el-icon>
                            <span>返回首页</span>
                          </el-dropdown-item>
                          <div class="dropdown-divider"></div>
                          <el-dropdown-item command="profile" class="menu-item">
                            <el-icon><User /></el-icon>
                            <span>个人资料</span>
                          </el-dropdown-item>
                          <el-dropdown-item command="settings" class="menu-item">
                            <el-icon><Setting /></el-icon>
                            <span>账户设置</span>
                          </el-dropdown-item>
                          <el-dropdown-item command="security" class="menu-item">
                            <el-icon><Lock /></el-icon>
                            <span>安全中心</span>
                          </el-dropdown-item>
                          <el-dropdown-item command="help" class="menu-item">
                            <el-icon><QuestionFilled /></el-icon>
                            <span>帮助中心</span>
                          </el-dropdown-item>
                          <div class="dropdown-divider"></div>
                          <el-dropdown-item command="logout" class="menu-item logout">
                            <el-icon><SwitchButton /></el-icon>
                            <span>退出登录</span>
                          </el-dropdown-item>
                        </div>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </div>
          </div>
        </el-header>
        
        <!-- 主要内容 -->
        <el-main>
          <div class="main-content">
            <router-view v-slot="{ Component, route }">
          <transition :name="getAdminTransitionName(route)" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  Odometer, 
  Document, 
  Folder, 
  FolderOpened,
  Collection, 
  ChatDotRound, 
  User,
  Key,
  Setting,
  HomeFilled,
  Bell,
  ArrowDown,
  SwitchButton,
  FullScreen,
  Search,
  EditPen,
  TrendCharts,
  Message,
  Sunny,
  Moon,
  Lock,
  QuestionFilled,
  Check,
  Warning,
  InfoFilled,
  CircleCheck,
  Monitor,
  Sunset,
  Star,
  Clock
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { showWarningConfirm } from '@/utils/positionedConfirm'
import { 
  getLatestNotifications, 
  getUnreadCount, 
  markAsRead as markNotificationAsRead, 
  markAllAsRead as markAllNotificationsAsRead, 
  clearAllNotifications as clearAllNotificationsAPI 
} from '@/api/notification'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 根据管理页面类型返回不同的过渡名称
const getAdminTransitionName = (route) => {
  // 文章编辑页面使用滑动效果
  if (route.name === 'CreateArticle' || route.name === 'EditArticle') {
    return 'slide-left'
  }
  // 统计页面使用缩放效果
  if (route.name === 'AdminStatistics' || route.name === 'TokenStatus') {
    return 'zoom-fade'
  }
  // 设置相关页面使用淡入淡出
  if (route.name === 'AccountSettings' || route.name === 'SecurityCenter' || route.name === 'HelpCenter') {
    return 'fade-slide'
  }
  // 列表页面使用垂直滑动
  return 'slide-up'
}

// 数字时钟
const currentTime = ref(new Date())
const is24Hour = ref(true)
const weekDays = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']

const updateClock = () => {
  currentTime.value = new Date()
}

const formatTime = computed(() => {
  if (!currentTime.value) return ''
  const now = currentTime.value
  let hours = now.getHours()
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  
  if (!is24Hour.value) {
    const ampm = hours >= 12 ? '下午' : '上午'
    hours = hours % 12 || 12
    const h = String(hours).padStart(2, '0')
    return `${ampm} ${h}:${minutes}:${seconds}`
  } else {
    const h = String(hours).padStart(2, '0')
    return `${h}:${minutes}:${seconds}`
  }
})

const formatDate = computed(() => {
  if (!currentTime.value) return ''
  const now = currentTime.value
  const y = now.getFullYear()
  const M = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  const w = weekDays[now.getDay()]
  return `${y}年${M}月${d}日 ${w}`
})

const toggleTimeFormat = () => {
  is24Hour.value = !is24Hour.value
}

let clockTimer
onMounted(() => {
  updateClock()
  clockTimer = setInterval(updateClock, 1000)
})
onUnmounted(() => {
  if (clockTimer) clearInterval(clockTimer)
})

// 主题相关
const currentThemeKey = ref('light')

// 主题配置
const themes = ref([
  {
    key: 'light',
    name: '浅色主题',
    description: '经典的浅色界面，适合日间使用',
    icon: Sunny,
    preview: 'linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)'
  },
  {
    key: 'dark',
    name: '深色主题',
    description: '护眼的深色界面，适合夜间使用',
    icon: Moon,
    preview: 'linear-gradient(135deg, #1e293b 0%, #334155 100%)'
  },
  {
    key: 'warm',
    name: '暖色主题',
    description: '温暖的橙色调，营造舒适氛围',
    icon: Sunset,
    preview: 'linear-gradient(135deg, #fed7aa 0%, #fb923c 100%)'
  },
  {
    key: 'ocean',
    name: '海洋主题',
    description: '清新的蓝色调，带来宁静感受',
    icon: Star,
    preview: 'linear-gradient(135deg, #bfdbfe 0%, #3b82f6 100%)'
  },
  {
    key: 'auto',
    name: '跟随系统',
    description: '自动跟随系统主题设置',
    icon: Monitor,
    preview: 'linear-gradient(135deg, #f8fafc 0%, #1e293b 100%)'
  }
])

const currentTheme = computed(() => 
  themes.value.find(theme => theme.key === currentThemeKey.value) || themes.value[0]
)

// 通知相关
const activeNotificationTab = ref('all')
const notifications = ref([])
const notificationLoading = ref(false)
const unreadCount = ref(0)

const unreadNotificationCount = computed(() => unreadCount.value)

const filteredNotifications = computed(() => {
  if (activeNotificationTab.value === 'unread') {
    return notifications.value.filter(n => !n.isRead)
  }
  return notifications.value
})

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 页面标题和图标
const pageTitle = computed(() => {
  const path = route.path
  const titleMap = {
    '/admin': '仪表盘',
    '/admin/articles': '文章管理',
    '/admin/categories': '分类管理',
    '/admin/tags': '标签管理',
    '/admin/comments': '评论管理',
    '/admin/users': '用户管理',
    '/admin/statistics': '数据统计',
    '/admin/files': '文件管理',
    '/admin/announcements': '公告管理',
    '/admin/token-status': 'Token监控'
  }
  return titleMap[path] || '管理后台'
})

// 页面副标题
const pageSubtitle = computed(() => {
  const path = route.path
  const subtitleMap = {
    '/admin': '欢迎回来，查看您的网站概况',
    '/admin/articles': '管理您的所有文章内容',
    '/admin/categories': '组织和管理文章分类',
    '/admin/tags': '管理文章标签系统',
    '/admin/comments': '审核和管理用户评论',
    '/admin/users': '管理注册用户信息',
    '/admin/statistics': '查看博客数据分析与可视化',
    '/admin/files': '管理上传的图片、视频等文件资源',
    '/admin/announcements': '管理系统公告，向用户发布重要信息',
    '/admin/token-status': '监控系统Token状态'
  }
  return subtitleMap[path] || '管理后台控制面板'
})

// 当前页面图标
const currentPageIcon = computed(() => {
  const path = route.path
  const iconMap = {
    '/admin': Odometer,
    '/admin/articles': Document,
    '/admin/categories': Folder,
    '/admin/tags': Collection,
    '/admin/comments': ChatDotRound,
    '/admin/users': User,
    '/admin/statistics': TrendCharts,
    '/admin/announcements': Bell,
    '/admin/files': FolderOpened,
    '/admin/token-status': Key
  }
  return iconMap[path] || Odometer
})

// 面包屑
const breadcrumb = computed(() => {
  const path = route.path
  const breadcrumbMap = {
    '/admin': '',
    '/admin/articles': '文章管理',
    '/admin/categories': '分类管理',
    '/admin/tags': '标签管理',
    '/admin/comments': '评论管理',
    '/admin/users': '用户管理',
    '/admin/statistics': '数据统计',
    '/admin/files': '文件管理',
    '/admin/token-status': 'Token监控'
  }
  return breadcrumbMap[path] || ''
})

// 通知功能方法
const getNotificationIcon = (type) => {
  const iconMap = {
    comment: ChatDotRound,
    system: Setting,
    user: User,
    success: CircleCheck,
    warning: Warning,
    info: InfoFilled
  }
  return iconMap[type] || Bell
}

const markAsRead = async (notification) => {
  try {
    await markNotificationAsRead(notification.id)
    notification.isRead = true
    unreadCount.value = Math.max(0, unreadCount.value - 1)
    ElMessage.success('已标记为已读')
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

const markAllAsRead = async () => {
  try {
    await markAllNotificationsAsRead()
    notifications.value.forEach(n => n.isRead = true)
    unreadCount.value = 0
    ElMessage.success('所有通知已标记为已读')
  } catch (error) {
    console.error('标记所有已读失败:', error)
    ElMessage.error('标记所有已读失败')
  }
}

const clearAllNotifications = async (event) => {
  try {
    await showWarningConfirm(
      '确定要清空所有通知吗？此操作不可恢复。',
      '确认清空',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      },
      event
    )
    await clearAllNotificationsAPI()
    notifications.value = []
    unreadCount.value = 0
    ElMessage.success('所有通知已清空')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空通知失败:', error)
      ElMessage.error('清空通知失败')
    }
  }
}

const viewAllNotifications = () => {
  router.push('/admin/notifications')
}

// 加载通知数据
const loadNotifications = async () => {
  try {
    notificationLoading.value = true
    const [notificationsRes, unreadCountRes] = await Promise.all([
      getLatestNotifications(10),
      getUnreadCount()
    ])
    
    if (notificationsRes && (notificationsRes.code === 200 || notificationsRes.success)) {
      const list = notificationsRes.data || []
      notifications.value = list.map(item => ({
        id: item.id,
        title: item.title,
        description: item.content,
        time: item.timeAgo,
        type: item.type,
        isRead: item.isRead
      }))
    }
    
    if (unreadCountRes && (unreadCountRes.code === 200 || unreadCountRes.success)) {
      unreadCount.value = unreadCountRes.data
    }
  } catch (error) {
    console.error('加载通知失败:', error)
    // 静默失败，不显示错误消息
  } finally {
    notificationLoading.value = false
  }
}

const onNotificationDropdownChange = (visible) => {
  if (visible) {
    // 当打开通知面板时，加载最新通知
    loadNotifications()
  }
}

// 主题切换功能
const switchTheme = (themeKey) => {
  currentThemeKey.value = themeKey
  const theme = themes.value.find(t => t.key === themeKey)
  
  // 应用主题到文档根元素
  applyTheme(themeKey)
  
  // 保存到本地存储
  localStorage.setItem('theme', themeKey)
  
  ElMessage.success(`已切换到${theme.name}`)
}

const applyTheme = (themeKey) => {
  const root = document.documentElement
  
  // 移除所有主题类
  root.classList.remove('theme-light', 'theme-dark', 'theme-warm', 'theme-ocean')
  
  // 如果是跟随系统主题
  if (themeKey === 'auto') {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    themeKey = prefersDark ? 'dark' : 'light'
  }
  
  // 添加对应主题类
  root.classList.add(`theme-${themeKey}`)
}

// 快速操作
const quickAction = (action) => {
  switch (action) {
    case 'article':
      router.push('/admin/articles/create')
      break
    case 'stats':
      router.push('/admin/statistics')
      break
  }
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    ElMessage.success('已进入全屏模式')
  } else {
    document.exitFullscreen()
    ElMessage.success('已退出全屏模式')
  }
}

// 处理用户菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'home':
      console.log('Navigating to home')
      router.push('/')
      ElMessage.success('已返回首页')
      break
    case 'profile':
      console.log('Navigating to profile')
      router.push('/profile')
      break
    case 'settings':
      console.log('Navigating to account-settings')
      router.push('/admin/account-settings')
      break
    case 'security':
      console.log('Navigating to security-center')
      router.push('/admin/security-center')
      break
    case 'help':
      console.log('Navigating to help-center')
      router.push('/admin/help-center')
      break
    case 'logout':
      console.log('Logging out')
      userStore.logout()
      ElMessage.success('退出登录成功')
      router.push('/')
      break
    default:
      console.log('Unknown command:', command)
  }
}

// 页面挂载时的初始化
onMounted(() => {
  // 初始化主题
  const savedTheme = localStorage.getItem('theme') || 'light'
  currentThemeKey.value = savedTheme
  applyTheme(savedTheme)
  
  // 监听系统主题变化（当选择跟随系统时）
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  mediaQuery.addEventListener('change', (e) => {
    if (currentThemeKey.value === 'auto') {
      applyTheme('auto')
    }
  })
  
  // 加载通知数据
  loadNotifications()
})
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 30%, #ec4899 70%, #f59e0b 100%);
  position: relative;
  overflow: hidden;
  
  /* 背景装饰 */
  .bg-decoration {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 0;
    overflow: hidden;
    
    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.08) 0%, rgba(139, 92, 246, 0.08) 100%);
      animation: float 20s infinite linear;
      
      &:nth-child(1) {
        width: 300px;
        height: 300px;
        top: 10%;
        right: 10%;
        animation-delay: 0s;
      }
      
      &:nth-child(2) {
        width: 200px;
        height: 200px;
        bottom: 20%;
        left: 15%;
        animation-delay: -10s;
      }
      
      &:nth-child(3) {
        width: 150px;
        height: 150px;
        top: 60%;
        right: 30%;
        animation-delay: -5s;
      }
    }
  }
  
  .el-container {
    height: 100%;
    position: relative;
    z-index: 1;
  }
  
  /* 侧边栏样式 */
    .sidebar {
      height: 100%;
      background: rgba(255, 255, 255, 0.98);
      backdrop-filter: blur(20px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
      border-right: 1px solid rgba(99, 102, 241, 0.1);
      position: relative;
      
      /* Logo 区域 */
      .logo {
        padding: 30px 25px;
        border-bottom: 1px solid rgba(99, 102, 241, 0.1);
        background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
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
        transition: left 0.8s ease;
      }
      
      &:hover::before {
        left: 100%;
      }
      
      .logo-container {
        display: flex;
        align-items: center;
        gap: 15px;
        
        .logo-icon {
          .icon-wrapper {
            width: 50px;
            height: 50px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
            color: white;
            transition: all 0.3s ease;
            
            &:hover {
              transform: scale(1.1) rotate(5deg);
              background: rgba(255, 255, 255, 0.3);
            }
          }
        }
        
        .logo-text {
          h2 {
            color: #ffffff;
            margin: 0 0 5px 0;
            font-size: 24px;
            font-weight: 700;
            text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
            letter-spacing: 0.5px;
          }
          
          span {
            color: rgba(255, 255, 255, 0.9);
            font-size: 12px;
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 1px;
          }
        }
      }
    }
    
    /* 菜单区域 */
    .menu-section {
      padding: 25px 20px 15px;
      
      .menu-title {
          font-size: 12px;
          font-weight: 600;
          color: #64748b;
          text-transform: uppercase;
          letter-spacing: 1px;
          margin-bottom: 15px;
          padding-left: 15px;
          position: relative;
          
          &::before {
            content: '';
            position: absolute;
            left: 0;
            top: 50%;
            width: 3px;
            height: 12px;
            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
            border-radius: 2px;
            transform: translateY(-50%);
          }
        }
      
      .el-menu {
        border-right: none;
        background: transparent;
        
        .menu-item {
          margin: 0 0 8px 0;
          border-radius: 15px;
          overflow: hidden;
          position: relative;
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          
          &::before {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              width: 0;
              height: 100%;
              background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
              transition: width 0.4s ease;
              z-index: 0;
            }
            
            &:hover::before {
              width: 100%;
            }
            
            &:hover {
              transform: translateX(8px);
              box-shadow: 0 8px 25px rgba(99, 102, 241, 0.25);
              
              .menu-item-content {
                color: white;
                
                .menu-icon {
                  background: rgba(255, 255, 255, 0.25);
                  color: white;
                  transform: scale(1.1);
                }
                
                .menu-indicator {
                  opacity: 1;
                  transform: translateX(0);
                }
              }
            }
            
            &.is-active {
              background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
              box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
              transform: translateX(8px);
              
              &::before {
                width: 100%;
              }
              
              .menu-item-content {
                color: white;
                
                .menu-icon {
                  background: rgba(255, 255, 255, 0.25);
                  color: white;
                  transform: scale(1.1);
                }
                
                .menu-indicator {
                  opacity: 1;
                  transform: translateX(0);
                }
              }
            }
          
          .menu-item-content {
            display: flex;
            align-items: center;
            gap: 15px;
            font-weight: 500;
            font-size: 15px;
            color: #495057;
            padding: 15px 20px;
            position: relative;
            z-index: 1;
            transition: all 0.3s ease;
            
            .menu-icon {
              width: 40px;
              height: 40px;
              background: #f8f9fa;
              border-radius: 12px;
              display: flex;
              align-items: center;
              justify-content: center;
              transition: all 0.3s ease;
              font-size: 18px;
              color: #667eea;
            }
            
            span {
              flex: 1;
              font-weight: 600;
            }
            
            .menu-indicator {
              width: 6px;
              height: 6px;
              background: white;
              border-radius: 50%;
              opacity: 0;
              transform: translateX(-10px);
              transition: all 0.3s ease;
            }
          }
        }
      }
    }
  }
  
  /* 顶部导航栏 */
  .header {
    height: 90px;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-bottom: 1px solid rgba(99, 102, 241, 0.08);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    padding: 0 30px;
    position: relative;
    z-index: 10;
    
    /* 装饰背景 */
    .header-decoration {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.03) 0%, rgba(139, 92, 246, 0.03) 100%);
      pointer-events: none;
    }
    
    .header-content {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
      position: relative;
      z-index: 1;
      
      /* 左侧页面标题区域 */
      .page-title-section {
        display: flex;
        align-items: center;
        gap: 20px;
        flex: 1;
        
        .page-title-area {
          display: flex;
          align-items: center;
          gap: 15px;
          
          .page-icon {
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 20px;
            box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
            transition: all 0.3s ease;
            
            &:hover {
              transform: translateY(-2px) scale(1.05);
              box-shadow: 0 12px 35px rgba(99, 102, 241, 0.4);
            }
          }
          
          .title-text {
            .main-title {
              font-size: 24px;
              font-weight: 700;
              color: #1f2937;
              margin: 0 0 4px 0;
              line-height: 1.2;
            }
            
            .sub-title {
              font-size: 14px;
              color: #6b7280;
              margin: 0;
              font-weight: 500;
            }
          }
        }
        
        /* 面包屑导航 */
        .breadcrumb-nav {
          margin-left: 20px;
          padding-left: 20px;
          border-left: 2px solid rgba(99, 102, 241, 0.1);
          
          .el-breadcrumb {
            font-weight: 500;
            
            :deep(.el-breadcrumb__item) {
              .el-breadcrumb__inner {
                color: #6b7280;
                font-weight: 500;
                transition: color 0.3s ease;
                
                &:hover {
                  color: #6366f1;
                }
              }
              
              &:last-child .el-breadcrumb__inner {
                color: #1f2937;
                font-weight: 600;
              }
            }
          }
        }
      }
      
      /* 中间快速操作区域 */
      .quick-actions {
        display: flex;
        align-items: center;
        gap: 12px;
        margin: 0 30px;
        
        .quick-btn {
          height: 42px;
          padding: 0 18px;
          border-radius: 12px;
          border: 1px solid rgba(99, 102, 241, 0.2);
          background: rgba(255, 255, 255, 0.8);
          color: #6366f1;
          font-weight: 600;
          font-size: 14px;
          display: flex;
          align-items: center;
          gap: 8px;
          transition: all 0.3s ease;
          cursor: pointer;
          backdrop-filter: blur(10px);
          
          &:hover {
            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
            border-color: transparent;
          }
          
          .el-icon {
            font-size: 16px;
          }
        }
      }
      
      /* 右侧操作区域 */
      .header-actions {
        display: flex;
        align-items: center;
        gap: 15px;
        
        /* 全局搜索 */
        .global-search {
          position: relative;
          
          .search-input {
            width: 280px;
            height: 42px;
            border-radius: 12px;
            border: 1px solid rgba(99, 102, 241, 0.2);
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;
            
            :deep(.el-input__wrapper) {
              background: transparent;
              border: none;
              box-shadow: none;
              padding: 0 15px;
              
              .el-input__inner {
                color: #374151;
                font-weight: 500;
                
                &::placeholder {
                  color: #9ca3af;
                  font-weight: 400;
                }
              }
              
              .el-input__prefix {
                color: #6b7280;
              }
            }
            
            &.is-focus {
              border-color: #6366f1;
              background: rgba(255, 255, 255, 0.95);
              box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
              transform: translateY(-1px);
            }
          }
          
          .search-shortcut {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            background: rgba(99, 102, 241, 0.1);
            color: #6366f1;
            padding: 4px 8px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
            pointer-events: none;
          }
        }
        
        /* 系统状态指示器 */
        .system-status {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 8px 12px;
          background: rgba(34, 197, 94, 0.1);
          border-radius: 10px;
          border: 1px solid rgba(34, 197, 94, 0.2);
          
          .status-dot {
            width: 8px;
            height: 8px;
            background: #22c55e;
            border-radius: 50%;
            animation: pulse 2s infinite;
          }
          
          .status-text {
            font-size: 12px;
            color: #16a34a;
            font-weight: 600;
          }
        }
        
        /* 通知中心 */
        .notification-center {
          position: relative;
          
          .notification-btn {
            width: 42px;
            height: 42px;
            border-radius: 12px;
            border: 1px solid rgba(99, 102, 241, 0.2);
            background: rgba(255, 255, 255, 0.8);
            color: #6b7280;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
            backdrop-filter: blur(10px);
            position: relative;
            
            &:hover {
              background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
              color: white;
              transform: translateY(-2px);
              box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
              border-color: transparent;
            }
            
            .notification-badge {
              position: absolute;
              top: -5px;
              right: -5px;
              background: #ef4444;
              color: white;
              border-radius: 50%;
              width: 18px;
              height: 18px;
              font-size: 10px;
              font-weight: 700;
              display: flex;
              align-items: center;
              justify-content: center;
              border: 2px solid white;
            }
          }
        }
        
        /* 主题切换按钮 */
        .theme-toggle {
          width: 42px;
          height: 42px;
          border-radius: 12px;
          border: 1px solid rgba(99, 102, 241, 0.2);
          background: rgba(255, 255, 255, 0.8);
          color: #6b7280;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s ease;
          backdrop-filter: blur(10px);
          
          &:hover {
            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
            border-color: transparent;
          }
        }
        
        /* 全屏按钮 */
        .fullscreen-btn {
          width: 42px;
          height: 42px;
          border-radius: 12px;
          border: 1px solid rgba(99, 102, 241, 0.2);
          background: rgba(255, 255, 255, 0.8);
          color: #6b7280;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s ease;
          backdrop-filter: blur(10px);
          
          &:hover {
            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
            border-color: transparent;
          }
        }
        
        /* 用户菜单 */
        .user-menu {
          .user-avatar {
            width: 42px;
            height: 42px;
            border-radius: 12px;
            border: 2px solid rgba(99, 102, 241, 0.2);
            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 700;
            font-size: 16px;
            
            &:hover {
              transform: translateY(-2px) scale(1.05);
              box-shadow: 0 12px 35px rgba(99, 102, 241, 0.4);
              border-color: #6366f1;
            }
          }
        }
      }
    }
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #f5576c);
    }
    
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 100%;
      padding: 0 30px;
      
      .header-left {
        .page-title {
          h1 {
            font-size: 28px;
            font-weight: 700;
            color: #2c3e50;
            margin: 0 0 5px 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            letter-spacing: -0.5px;
          }
          
          .el-breadcrumb {
            font-size: 14px;
            font-weight: 500;
            color: #8e9aaf;
            
            :deep(.el-breadcrumb__item) {
              .el-breadcrumb__inner {
                color: #8e9aaf;
                font-weight: 500;
                
                &:hover {
                  color: #667eea;
                }
              }
            }
          }
        }
      }
      
      .header-right {
        .header-actions {
          display: flex;
          align-items: center;
          gap: 20px;
          
          .search-box {
            .search-input {
              width: 280px;
              
              :deep(.el-input__wrapper) {
                background: rgba(248, 249, 250, 0.9);
                border: 1px solid rgba(99, 102, 241, 0.1);
                border-radius: 25px;
                padding: 0 20px;
                backdrop-filter: blur(10px);
                transition: all 0.3s ease;
                
                &:hover {
                  border-color: #6366f1;
                  background: rgba(255, 255, 255, 0.9);
                }
                
                &.is-focus {
                  border-color: #6366f1;
                  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
                  background: rgba(255, 255, 255, 0.95);
                }
                
                .el-input__inner {
                  color: #495057;
                  font-weight: 500;
                  
                  &::placeholder {
                    color: #8e9aaf;
                  }
                }
              }
            }
          }
          
          .action-btn {
            width: 40px;
            height: 40px;
            border-radius: 12px;
            background: rgba(248, 250, 252, 0.9);
            border: 1px solid rgba(99, 102, 241, 0.1);
            color: #64748b;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
            
            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
              opacity: 0;
              transition: opacity 0.3s ease;
            }
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 8px 25px rgba(99, 102, 241, 0.2);
              border-color: transparent;
              
              &::before {
                opacity: 1;
              }
              
              .el-icon {
                color: white;
                transform: scale(1.1);
                z-index: 1;
                position: relative;
              }
            }
            
            .el-icon {
              font-size: 18px;
              transition: all 0.3s ease;
            }
            
            &.notification-btn {
              .notification-badge {
                :deep(.el-badge__content) {
                  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
                  border: 2px solid white;
                  font-size: 10px;
                  font-weight: 700;
                }
              }
            }
          }
          
          .user-dropdown {
            .user-info {
              display: flex;
              align-items: center;
              gap: 15px;
              cursor: pointer;
              padding: 8px 20px;
              border-radius: 30px;
              transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
              background: rgba(248, 249, 250, 0.9);
              backdrop-filter: blur(10px);
              border: 1px solid rgba(99, 102, 241, 0.1);
              position: relative;
              overflow: hidden;
              
              &::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
                transition: left 0.4s ease;
                z-index: 0;
              }
              
              &:hover::before {
                left: 0;
              }
              
              &:hover {
                transform: translateY(-3px);
                box-shadow: 0 8px 25px rgba(99, 102, 241, 0.2);
                border-color: transparent;
                
                .username,
                .user-role,
                .dropdown-icon {
                  color: white;
                }
                
                .user-avatar {
                  transform: scale(1.1);
                  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);
                }
              }
              
              .user-avatar {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                border: 3px solid rgba(255, 255, 255, 0.9);
                font-weight: 700;
                color: white;
                transition: all 0.3s ease;
                position: relative;
                z-index: 1;
              }
              
              .user-details {
                display: flex;
                flex-direction: column;
                position: relative;
                z-index: 1;
                
                .username {
                  font-size: 16px;
                  font-weight: 700;
                  color: #2c3e50;
                  transition: color 0.3s ease;
                  margin-bottom: 2px;
                }
                
                .user-role {
                  font-size: 12px;
                  color: #8e9aaf;
                  font-weight: 500;
                  transition: color 0.3s ease;
                }
              }
              
              .dropdown-icon {
                color: #8e9aaf;
                transition: all 0.3s ease;
                position: relative;
                z-index: 1;
              }
            }
            
            :deep(.user-dropdown-menu) {
              background: rgba(255, 255, 255, 0.95);
              backdrop-filter: blur(20px);
              border: 1px solid rgba(0, 0, 0, 0.1);
              border-radius: 15px;
              box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
              padding: 10px;
              
              .el-dropdown-menu__item {
                border-radius: 10px;
                margin: 2px 0;
                padding: 12px 15px;
                transition: all 0.3s ease;
                
                &:hover {
                  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                  color: white;
                  transform: translateX(5px);
                }
              }
            }
          }
        }
      }
    }
  }
  
  /* 主内容区域 */
  .el-main {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(20px);
    padding: 30px;
    border-radius: 25px 0 0 25px;
    margin: 15px 15px 15px 0;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.3), transparent);
    }
    
    .main-content {
      height: 100%;
      overflow-y: auto;
      
      &::-webkit-scrollbar {
        width: 6px;
      }
      
      &::-webkit-scrollbar-track {
        background: rgba(0, 0, 0, 0.05);
        border-radius: 3px;
      }
      
      &::-webkit-scrollbar-thumb {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 3px;
        transition: all 0.3s ease;
        
        &:hover {
          background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
        }
      }
    }
  }
}

/* 动画关键帧 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.7;
  }
  33% {
    transform: translateY(-30px) rotate(120deg);
    opacity: 0.9;
  }
  66% {
    transform: translateY(-60px) rotate(240deg);
    opacity: 0.5;
  }
}

/* 通知下拉菜单样式 */
.notification-dropdown {
  .notification-panel {
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(99, 102, 241, 0.1);
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    padding: 0;
    min-width: 320px;
    max-height: 400px;
    overflow: hidden;
    
    .notification-header {
      padding: 20px 20px 15px;
      border-bottom: 1px solid rgba(99, 102, 241, 0.1);
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.05) 0%, rgba(139, 92, 246, 0.05) 100%);
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 700;
        color: #1f2937;
      }
    }
    
    .notification-list {
      max-height: 300px;
      overflow-y: auto;
      
      .notification-item {
        padding: 15px 20px;
        border-bottom: 1px solid rgba(99, 102, 241, 0.05);
        transition: all 0.3s ease;
        cursor: pointer;
        display: flex;
        align-items: flex-start;
        gap: 12px;
        
        &:hover {
          background: rgba(99, 102, 241, 0.05);
        }
        
        &:last-child {
          border-bottom: none;
        }
        
        .notification-avatar {
          width: 36px;
          height: 36px;
          border-radius: 10px;
          background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 14px;
          flex-shrink: 0;
        }
        
        .notification-content {
          flex: 1;
          
          .notification-title {
            font-size: 14px;
            font-weight: 600;
            color: #1f2937;
            margin: 0 0 4px 0;
            line-height: 1.4;
          }
          
          .notification-time {
            font-size: 11px;
            color: #9ca3af;
            margin: 0;
          }
        }
      }
    }
    
    .notification-footer {
      padding: 15px 20px;
      border-top: 1px solid rgba(99, 102, 241, 0.1);
      text-align: center;
    }
  }
}

/* 用户下拉菜单样式 */
.user-dropdown {
  .user-profile {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px 16px;
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.8);
    border: 1px solid rgba(99, 102, 241, 0.2);
    cursor: pointer;
    transition: all 0.3s ease;
    backdrop-filter: blur(10px);
    
    &:hover {
      background: rgba(255, 255, 255, 0.95);
      border-color: #6366f1;
      transform: translateY(-1px);
      box-shadow: 0 4px 15px rgba(99, 102, 241, 0.15);
    }
    
    .user-avatar-container {
      position: relative;
      
      .user-avatar {
        background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
        color: white;
        font-weight: 700;
        border: 2px solid rgba(255, 255, 255, 0.8);
      }
      
      .online-indicator {
        position: absolute;
        bottom: 2px;
        right: 2px;
        width: 12px;
        height: 12px;
        background: #22c55e;
        border: 2px solid white;
        border-radius: 50%;
      }
    }
    
    .user-info {
      flex: 1;
      
      .user-name {
        font-size: 14px;
        font-weight: 600;
        color: #1f2937;
        margin: 0;
        line-height: 1.2;
      }
    }
    
    .dropdown-arrow {
      color: #6b7280;
      font-size: 14px;
      transition: transform 0.3s ease;
    }
  }
  
  .user-dropdown-panel {
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(99, 102, 241, 0.1);
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    padding: 0;
    min-width: 280px;
    overflow: hidden;
    
    .dropdown-header {
      padding: 20px;
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.05) 0%, rgba(139, 92, 246, 0.05) 100%);
      border-bottom: 1px solid rgba(99, 102, 241, 0.1);
      
      .user-card {
        display: flex;
        align-items: center;
        gap: 15px;
        
        .user-details {
          flex: 1;
          
          h4 {
            margin: 0;
            font-size: 16px;
            font-weight: 700;
            color: #1f2937;
          }
        }
      }
    }
    
    .dropdown-menu {
      padding: 8px 0;
      
      .menu-item {
        padding: 12px 20px;
        font-size: 14px;
        color: #374151;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        gap: 12px;
        cursor: pointer;
        
        &:hover {
          background: rgba(99, 102, 241, 0.05);
          color: #6366f1;
        }
        
        .el-icon {
          width: 20px;
          height: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 16px;
        }
        
        &.logout {
          color: #ef4444;
          
          &:hover {
            background: rgba(239, 68, 68, 0.05);
            color: #dc2626;
          }
        }
      }
      
      .dropdown-divider {
        height: 1px;
        background: rgba(99, 102, 241, 0.1);
        margin: 8px 0;
      }
    }
  }
}

/* 搜索框样式 */
.search-container {
  .search-box {
    position: relative;
    width: 280px;
    
    &.search-focused {
      .search-input {
        border-color: #6366f1;
        box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
        background: rgba(255, 255, 255, 0.95);
      }
    }
    
    .search-icon {
      position: absolute;
      left: 12px;
      top: 50%;
      transform: translateY(-50%);
      color: #6b7280;
      font-size: 16px;
      z-index: 1;
    }
    
    .search-input {
      width: 100%;
      height: 42px;
      border-radius: 12px;
      border: 1px solid rgba(99, 102, 241, 0.2);
      background: rgba(255, 255, 255, 0.8);
      backdrop-filter: blur(10px);
      padding: 0 80px 0 40px;
      font-size: 14px;
      color: #374151;
      transition: all 0.3s ease;
      outline: none;
      
      &::placeholder {
        color: #9ca3af;
      }
      
      &:hover {
        border-color: rgba(99, 102, 241, 0.4);
      }
    }
    
    .search-shortcut {
      position: absolute;
      right: 12px;
      top: 50%;
      transform: translateY(-50%);
      background: rgba(99, 102, 241, 0.1);
      color: #6366f1;
      padding: 4px 8px;
      border-radius: 6px;
      font-size: 12px;
      font-weight: 600;
      pointer-events: none;
    }
  }
}

/* 数字时钟样式 */
.clock-container {
  .clock-box {
    position: relative;
    width: 320px;
    height: 42px;
    border-radius: 12px;
    border: 1px solid rgba(99, 102, 241, 0.2);
    background: linear-gradient(135deg, rgba(255,255,255,0.85) 0%, rgba(236, 240, 255, 0.85) 100%);
    backdrop-filter: blur(10px);
    display: flex;
    align-items: center;
    padding: 0 12px 0 40px;
    transition: all 0.3s ease;
    box-shadow: 0 2px 10px rgba(99, 102, 241, 0.08);

    &:hover {
      border-color: rgba(99, 102, 241, 0.45);
      box-shadow: 0 10px 30px rgba(99, 102, 241, 0.18);
      transform: translateY(-1px);
    }
  }

  .clock-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #6366f1;
    font-size: 16px;
    z-index: 1;
    filter: drop-shadow(0 2px 4px rgba(99, 102, 241, 0.2));
  }

  .clock-content {
    display: flex;
    flex-direction: column;
    line-height: 1.1;
  }

  .time-display {
    font-size: 16px;
    font-weight: 700;
    color: #1f2937;
    letter-spacing: 0.5px;
  }

  .date-display {
    font-size: 12px;
    color: #6b7280;
    margin-top: 2px;
  }

  .clock-controls {
    margin-left: auto;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .time-format-btn {
    height: 26px;
    padding: 0 10px;
    border-radius: 8px;
    border: 1px solid rgba(99, 102, 241, 0.35);
    background: rgba(99, 102, 241, 0.08);
    color: #4f46e5;
    font-size: 12px;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: rgba(99, 102, 241, 0.16);
      box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
    }
    &:active {
      transform: translateY(1px);
    }
  }

  @media (max-width: 1280px) {
    .clock-box {
      width: 280px;
    }
    .time-display { font-size: 15px; }
    .date-display { display: none; }
  }
}

/* 时间选择器样式 */
.datetime-container {
  .datetime-box {
    position: relative;
    width: 280px;
    
    &.datetime-focused {
      :deep(.el-date-editor) {
        border-color: #6366f1;
        box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
        background: rgba(255, 255, 255, 0.95);
      }
    }
    
    .datetime-icon {
      position: absolute;
      left: 12px;
      top: 50%;
      transform: translateY(-50%);
      color: #6b7280;
      font-size: 16px;
      z-index: 10;
      pointer-events: none;
    }
    
    .datetime-input {
      width: 100%;
      
      :deep(.el-date-editor) {
        width: 100%;
        height: 42px;
        border-radius: 12px;
        border: 1px solid rgba(99, 102, 241, 0.2);
        background: rgba(255, 255, 255, 0.8);
        backdrop-filter: blur(10px);
        font-size: 14px;
        color: #374151;
        transition: all 0.3s ease;
        
        .el-input__wrapper {
          background: transparent;
          border: none;
          box-shadow: none;
          padding: 0 12px 0 40px;
          
          .el-input__inner {
            color: #374151;
            font-weight: 500;
            
            &::placeholder {
              color: #9ca3af;
            }
          }
        }
        
        &:hover {
          border-color: rgba(99, 102, 241, 0.4);
        }
      }
    }
  }
}

/* 系统状态样式 */
.system-status {
  .status-indicator {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    border-radius: 10px;
    font-size: 12px;
    font-weight: 600;
    
    &.online {
      background: rgba(34, 197, 94, 0.1);
      border: 1px solid rgba(34, 197, 94, 0.2);
      color: #16a34a;
      
      .status-dot {
        width: 8px;
        height: 8px;
        background: #22c55e;
        border-radius: 50%;
        animation: pulse 2s infinite;
      }
    }
  }
}

/* 头部操作按钮样式 */
.header-actions {
  .action-btn {
    width: 42px;
    height: 42px;
    border-radius: 12px;
    border: 1px solid rgba(99, 102, 241, 0.2);
    background: rgba(255, 255, 255, 0.8);
    color: #6b7280;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
    backdrop-filter: blur(10px);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
      opacity: 0;
      transition: opacity 0.3s ease;
    }
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(99, 102, 241, 0.2);
      border-color: transparent;
      
      &::before {
        opacity: 1;
      }
      
      .el-icon,
      .btn-label {
        color: white;
        z-index: 1;
        position: relative;
      }
    }
    
    .btn-label {
      font-size: 10px;
      font-weight: 600;
      margin-top: 2px;
      transition: color 0.3s ease;
    }
    
    &.notification-btn {
      .el-badge {
        :deep(.el-badge__content) {
          background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
          border: 2px solid white;
          font-size: 10px;
          font-weight: 700;
        }
      }
    }
  }
}

/* 快速操作按钮样式 */
.quick-actions {
  .quick-btn {
    height: 42px;
    padding: 0 18px;
    border-radius: 12px;
    border: 1px solid rgba(99, 102, 241, 0.2);
    background: rgba(255, 255, 255, 0.8);
    color: #6366f1;
    font-weight: 600;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: all 0.3s ease;
    cursor: pointer;
    backdrop-filter: blur(10px);
    
    &:hover {
      background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
      color: white;
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
      border-color: transparent;
    }
    
    &.primary {
      background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
      color: white;
      border-color: transparent;
      box-shadow: 0 4px 15px rgba(99, 102, 241, 0.2);
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 12px 35px rgba(99, 102, 241, 0.4);
      }
    }
    
    .el-icon {
      font-size: 16px;
    }
  }
}

/* 页面标题区域样式 */
.page-title-section {
  .title-wrapper {
      display: flex;
      align-items: center;
      gap: 15px;
      margin-bottom: 10px;
      margin-top: 54px; /* 向下调整标题位置 */
    
    .title-icon {
      width: 50px;
      height: 50px;
      background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
      border-radius: 15px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px) scale(1.05);
        box-shadow: 0 12px 35px rgba(99, 102, 241, 0.4);
      }
    }
    
    .title-content {
      .page-title {
        font-size: 24px;
        font-weight: 700;
        color: #1f2937;
        margin: 0 0 4px 0;
        line-height: 1.2;
      }
      
      .page-subtitle {
        font-size: 14px;
        color: #6b7280;
        margin: 0;
        font-weight: 500;
      }
    }
  }
  
  .breadcrumb {
    font-weight: 500;
    margin-top: 35px; /* 向下移动面包屑导航 */
    
    :deep(.el-breadcrumb__item) {
      .el-breadcrumb__inner {
        color: #6b7280;
        font-weight: 500;
        transition: color 0.3s ease;
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px; /* 增大字体 */
        
        &:hover {
          color: #6366f1;
        }
        
        /* 放大图标 */
        .el-icon {
          font-size: 18px;
        }
        
        /* 特别针对第一个面包屑项（控制台）进行放大 */
        &:first-child {
          font-size: 18px;
          font-weight: 600;
          
          .el-icon {
            font-size: 20px;
          }
        }
      }
      
      /* 针对控制台项的特殊样式 */
      &:first-child .el-breadcrumb__inner {
        font-size: 18px;
        font-weight: 600;
        color: #374151;
        
        .el-icon {
          font-size: 20px;
        }
        
        &:hover {
          color: #6366f1;
        }
      }
      
      &:last-child .el-breadcrumb__inner {
        color: #1f2937;
        font-weight: 600;
      }
    }
  }
}

/* 动画效果 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .admin-layout {
    .header-actions {
      .search-box {
        .search-input {
          width: 200px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .admin-layout {
    .sidebar {
      width: 280px;
    }
    
    .el-header .header {
      padding: 0 20px;
      
      .header-actions {
        gap: 10px;
        
        .search-box {
          display: none;
        }
        
        .action-btn {
          width: 40px;
          height: 40px;
        }
      }
    }
    
    .el-main {
      padding: 20px;
      margin: 10px 10px 10px 0;
    }
  }
}

@media (max-width: 480px) {
  .admin-layout {
    .sidebar {
      width: 260px;
      
      .logo {
        padding: 20px 15px;
        
        .logo-container {
          gap: 10px;
          
          .logo-icon .icon-wrapper {
            width: 40px;
            height: 40px;
          }
          
          .logo-text h2 {
            font-size: 20px;
          }
        }
      }
      
      .menu-section {
        padding: 20px 15px 10px;
        
        .menu-item .menu-item-content {
          padding: 12px 15px;
          font-size: 14px;
          
          .menu-icon {
            width: 35px;
            height: 35px;
          }
        }
      }
    }
    
    .el-header .header {
      padding: 0 15px;
      
      .page-title h1 {
        font-size: 24px;
      }
    }
  }
}

/* 主题系统样式 */
.theme-panel {
  width: 320px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  
  .theme-header {
    margin-bottom: 20px;
    
    h3 {
      margin: 0 0 8px 0;
      font-size: 18px;
      font-weight: 600;
      color: #1f2937;
    }
    
    p {
      margin: 0;
      font-size: 14px;
      color: #6b7280;
    }
  }
  
  .theme-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .theme-item {
      display: flex;
      align-items: center;
      gap: 15px;
      padding: 15px;
      border-radius: 10px;
      border: 2px solid transparent;
      cursor: pointer;
      transition: all 0.3s ease;
      background: #f8fafc;
      
      &:hover {
        background: #f1f5f9;
        transform: translateY(-1px);
      }
      
      &.active {
        border-color: #6366f1;
        background: rgba(99, 102, 241, 0.05);
        
        .theme-preview {
          box-shadow: 0 4px 20px rgba(99, 102, 241, 0.3);
        }
      }
      
      .theme-preview {
        width: 50px;
        height: 50px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
        
        .theme-icon {
          color: white;
          font-size: 20px;
          z-index: 1;
          position: relative;
        }
      }
      
      .theme-info {
        flex: 1;
        
        .theme-name {
          font-size: 16px;
          font-weight: 600;
          color: #1f2937;
          margin-bottom: 4px;
        }
        
        .theme-desc {
          font-size: 13px;
          color: #6b7280;
          line-height: 1.4;
        }
      }
      
      .theme-check {
        color: #6366f1;
        font-size: 18px;
      }
    }
  }
}

/* 通知面板样式增强 */
.notification-panel {
  width: 380px;
  max-height: 500px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  
  .notification-header {
    padding: 20px 20px 15px;
    border-bottom: 1px solid #f1f5f9;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #1f2937;
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
    }
  }
  
  .notification-tabs {
    display: flex;
    padding: 0 20px;
    border-bottom: 1px solid #f1f5f9;
    
    .tab-item {
      padding: 12px 16px;
      font-size: 14px;
      font-weight: 500;
      color: #6b7280;
      cursor: pointer;
      border-bottom: 2px solid transparent;
      transition: all 0.3s ease;
      
      &:hover {
        color: #374151;
      }
      
      &.active {
        color: #6366f1;
        border-bottom-color: #6366f1;
      }
    }
  }
  
  .notification-list {
    max-height: 300px;
    overflow-y: auto;
    
    .notification-item {
      display: flex;
      align-items: flex-start;
      gap: 15px;
      padding: 15px 20px;
      border-bottom: 1px solid #f8fafc;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        background: #f8fafc;
      }
      
      &.unread {
        background: rgba(99, 102, 241, 0.02);
        border-left: 3px solid #6366f1;
        
        .notification-title {
          font-weight: 600;
        }
      }
      
      .notification-avatar {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 16px;
        
        &.comment { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }
        &.system { background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%); }
        &.user { background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%); }
        &.success { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }
        &.warning { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); }
        &.info { background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); }
      }
      
      .notification-content {
        flex: 1;
        
        .notification-title {
          font-size: 14px;
          color: #1f2937;
          margin-bottom: 4px;
          line-height: 1.4;
        }
        
        .notification-desc {
          font-size: 13px;
          color: #6b7280;
          line-height: 1.4;
          margin-bottom: 6px;
        }
        
        .notification-time {
          font-size: 12px;
          color: #9ca3af;
        }
      }
      
      .notification-actions {
        opacity: 0;
        transition: opacity 0.3s ease;
      }
      
      &:hover .notification-actions {
        opacity: 1;
      }
    }
  }
  
  .notification-empty {
    padding: 40px 20px;
    text-align: center;
    color: #9ca3af;
    
    .el-icon {
      font-size: 48px;
      margin-bottom: 12px;
      opacity: 0.5;
    }
    
    p {
      margin: 0;
      font-size: 14px;
    }
  }
  
  .notification-footer {
    padding: 15px 20px;
    border-top: 1px solid #f1f5f9;
    text-align: center;
  }
}

/* 主题变量定义 */
:root {
  /* 浅色主题 */
  &.theme-light {
    --bg-primary: #ffffff;
    --bg-secondary: #f8fafc;
    --text-primary: #1f2937;
    --text-secondary: #6b7280;
    --border-color: #e5e7eb;
  }
  
  /* 深色主题 */
  &.theme-dark {
    --bg-primary: #1f2937;
    --bg-secondary: #111827;
    --text-primary: #f9fafb;
    --text-secondary: #d1d5db;
    --border-color: #374151;
    
    .admin-layout {
      background: linear-gradient(135deg, #1f2937 0%, #111827 30%, #0f172a 70%, #020617 100%);
      
      .sidebar {
        background: rgba(31, 41, 55, 0.98);
        border-right-color: rgba(75, 85, 99, 0.3);
        
        .menu-item {
          &:hover {
            background: rgba(99, 102, 241, 0.1);
          }
          
          &.is-active {
            background: rgba(99, 102, 241, 0.2);
          }
        }
      }
      
      .header {
        background: rgba(31, 41, 55, 0.95);
        
        .page-title {
          color: #f9fafb !important;
        }
        
        .page-subtitle {
          color: #d1d5db !important;
        }
      }
      
      .main-content {
        background: rgba(31, 41, 55, 0.95);
        color: #f9fafb;
      }
    }
  }
  
  /* 暖色主题 */
  &.theme-warm {
    --bg-primary: #fef7ed;
    --bg-secondary: #fed7aa;
    --text-primary: #9a3412;
    --text-secondary: #c2410c;
    --border-color: #fdba74;
    
    .admin-layout {
      background: linear-gradient(135deg, #fed7aa 0%, #fb923c 30%, #ea580c 70%, #c2410c 100%);
    }
  }
  
  /* 海洋主题 */
  &.theme-ocean {
    --bg-primary: #eff6ff;
    --bg-secondary: #bfdbfe;
    --text-primary: #1e3a8a;
    --text-secondary: #3730a3;
    --border-color: #93c5fd;
    
    .admin-layout {
      background: linear-gradient(135deg, #bfdbfe 0%, #3b82f6 30%, #1d4ed8 70%, #1e3a8a 100%);
    }
  }
}

/* 管理后台专用过渡动画 */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(50px);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-50px);
}

.zoom-fade-enter-active,
.zoom-fade-leave-active {
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.zoom-fade-enter-from {
  opacity: 0;
  transform: scale(0.9) rotateY(10deg);
}

.zoom-fade-leave-to {
  opacity: 0;
  transform: scale(1.1) rotateY(-10deg);
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(40px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-40px);
}
</style>