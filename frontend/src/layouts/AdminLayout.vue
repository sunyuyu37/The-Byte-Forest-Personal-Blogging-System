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
      <el-aside :width="sidebarCollapsed ? '80px' : '300px'" class="sidebar-container" @wheel.prevent="handleSidebarWheel">
        <div class="sidebar" :class="{ collapsed: sidebarCollapsed, scrolling: isScrolling }" ref="sidebarRef" 
             @wheel.prevent="handleSidebarWheel"
             @touchstart="handleTouchStart"
             @touchmove="handleTouchMove"
             @touchend="handleTouchEnd"
             style="pointer-events: auto;">
          <!-- 滚动进度指示器 -->
          <div class="scroll-indicator" :class="{ active: isScrolling }">
            <div class="scroll-progress" :style="{ height: scrollProgress + '%' }"></div>
          </div>
          
          <!-- 滚动边界反馈 -->
          <div class="scroll-boundary-top" :class="{ active: scrollAtTop && isScrolling }"></div>
          <div class="scroll-boundary-bottom" :class="{ active: scrollAtBottom && isScrolling }"></div>
          
          <!-- 折叠按钮 -->
          <div class="collapse-btn" @click="toggleSidebar">
            <el-icon :size="18">
              <component :is="sidebarCollapsed ? 'Expand' : 'Fold'" />
            </el-icon>
          </div>
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
            <div class="menu-title" @click="toggleMenuGroup('main')">
              <span>主要功能</span>
              <el-icon class="menu-toggle-icon" :class="{ 'rotated': !menuGroups.main }">
                <ArrowDown />
              </el-icon>
            </div>
            <transition name="menu-collapse">
              <el-menu
                v-show="menuGroups.main"
                :default-active="activeMenu"
                router
                :collapse="false"
                class="admin-menu"
              >
              <el-tooltip content="仪表盘" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><Odometer /></el-icon>
                    </div>
                    <span>仪表盘</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="文章管理" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/articles" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><Document /></el-icon>
                    </div>
                    <span>文章管理</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="分类管理" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/categories" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><Folder /></el-icon>
                    </div>
                    <span>分类管理</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="标签管理" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/tags" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><Collection /></el-icon>
                    </div>
                    <span>标签管理</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="评论管理" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/comments" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><ChatDotRound /></el-icon>
                    </div>
                    <span>评论管理</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="留言管理" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/messages" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><Message /></el-icon>
                    </div>
                    <span>留言管理</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="用户管理" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/users" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><User /></el-icon>
                    </div>
                    <span>用户管理</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="数据统计" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/statistics" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><TrendCharts /></el-icon>
                    </div>
                    <span>数据统计</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="文件管理" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/files" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><FolderOpened /></el-icon>
                    </div>
                    <span>文件管理</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              <el-tooltip content="公告管理" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/announcements" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><Bell /></el-icon>
                    </div>
                    <span>公告管理</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
            </el-menu>
            </transition>
          </div>
          
          <div class="menu-section">
            <div class="menu-title" @click="toggleMenuGroup('system')">
              <span>系统管理</span>
              <el-icon class="menu-toggle-icon" :class="{ 'rotated': !menuGroups.system }">
                <ArrowDown />
              </el-icon>
            </div>
            <transition name="menu-collapse">
              <el-menu
                v-show="menuGroups.system"
                :default-active="activeMenu"
                router
                :collapse="false"
                class="admin-menu"
              >
              <el-tooltip content="Token监控" placement="right" :disabled="!sidebarCollapsed">
                <el-menu-item index="/admin/token-status" class="menu-item">
                  <div class="menu-item-content">
                    <div class="menu-icon">
                      <el-icon><Key /></el-icon>
                    </div>
                    <span>Token监控</span>
                    <div class="menu-indicator"></div>
                  </div>
                </el-menu-item>
              </el-tooltip>
              </el-menu>
            </transition>
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
  Clock,
  Expand,
  Fold
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

// 侧边栏折叠状态
const sidebarCollapsed = ref(false)

// 菜单组展开状态
const menuGroups = ref({
  main: true,
  system: true
})

// 切换侧边栏折叠状态
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
  // 保存状态到本地存储
  localStorage.setItem('sidebarCollapsed', sidebarCollapsed.value.toString())
}

// 切换菜单组展开状态
const toggleMenuGroup = (groupName) => {
  menuGroups.value[groupName] = !menuGroups.value[groupName]
  // 保存状态到本地存储
  localStorage.setItem('menuGroups', JSON.stringify(menuGroups.value))
}

// 侧边栏滚动相关
const sidebarRef = ref(null)
const scrollPosition = ref(0)
const isScrolling = ref(false)
const scrollVelocity = ref(0)
const lastScrollTime = ref(0)
const scrollAnimationId = ref(null)
const scrollProgress = ref(0)
const scrollAtTop = ref(true)
const scrollAtBottom = ref(false)

// 动量滚动参数
const SCROLL_CONFIG = {
  friction: 0.92,           // 摩擦系数，控制减速
  minVelocity: 0.5,        // 最小速度阈值
  maxVelocity: 50,         // 最大速度限制
  scrollMultiplier: 1.2,   // 滚动倍数
  touchScrollMultiplier: 2, // 触摸滚动倍数
  smoothDuration: 300      // 平滑滚动持续时间
}

// 更新滚动状态和进度
const updateScrollState = () => {
  const sidebar = sidebarRef.value
  if (!sidebar) return
  
  const currentScroll = sidebar.scrollTop
  const maxScroll = sidebar.scrollHeight - sidebar.clientHeight
  
  // 更新滚动进度（0-100%）
  scrollProgress.value = maxScroll > 0 ? (currentScroll / maxScroll) * 100 : 0
  
  // 更新边界状态
  scrollAtTop.value = currentScroll <= 5
  scrollAtBottom.value = currentScroll >= maxScroll - 5
  
  // 更新滚动位置
  scrollPosition.value = currentScroll
}

// 动量滚动动画
const momentumScroll = () => {
  const sidebar = sidebarRef.value
  if (!sidebar || Math.abs(scrollVelocity.value) < SCROLL_CONFIG.minVelocity) {
    scrollVelocity.value = 0
    isScrolling.value = false
    scrollAnimationId.value = null
    updateScrollState()
    return
  }
  
  const currentScroll = sidebar.scrollTop
  const maxScroll = sidebar.scrollHeight - sidebar.clientHeight
  const newScroll = Math.max(0, Math.min(maxScroll, currentScroll + scrollVelocity.value))
  
  sidebar.scrollTop = newScroll
  scrollVelocity.value *= SCROLL_CONFIG.friction
  
  // 更新滚动状态
  updateScrollState()
  
  scrollAnimationId.value = requestAnimationFrame(momentumScroll)
}

// 处理侧边栏滚轮事件
const handleSidebarWheel = (event) => {
  event.preventDefault()
  event.stopPropagation()
  
  const sidebar = sidebarRef.value
  if (!sidebar) return
  
  // 如果内容不够长，无需滚动
  if (sidebar.scrollHeight <= sidebar.clientHeight) {
    return
  }
  
  const now = Date.now()
  const deltaTime = now - lastScrollTime.value
  lastScrollTime.value = now
  
  // 计算滚动速度
  let deltaY = event.deltaY
  
  // 根据滚动类型调整速度
  if (event.deltaMode === 1) { // 行滚动
    deltaY *= 16
  } else if (event.deltaMode === 2) { // 页滚动
    deltaY *= sidebar.clientHeight
  }
  
  // 应用滚动倍数
  const scrollMultiplier = event.ctrlKey ? SCROLL_CONFIG.touchScrollMultiplier : SCROLL_CONFIG.scrollMultiplier
  deltaY *= scrollMultiplier
  
  // 限制最大速度
  deltaY = Math.max(-SCROLL_CONFIG.maxVelocity, Math.min(SCROLL_CONFIG.maxVelocity, deltaY))
  
  // 如果正在进行动量滚动，取消它
  if (scrollAnimationId.value) {
    cancelAnimationFrame(scrollAnimationId.value)
    scrollAnimationId.value = null
  }
  
  // 设置新的滚动速度
  scrollVelocity.value = deltaY
  
  // 设置滚动状态
  isScrolling.value = true
  
  // 开始动量滚动
  momentumScroll()
  
  // 更新滚动状态
  updateScrollState()
  
  // 延迟重置滚动状态
  setTimeout(() => {
    if (Math.abs(scrollVelocity.value) < SCROLL_CONFIG.minVelocity) {
      isScrolling.value = false
    }
  }, SCROLL_CONFIG.smoothDuration)
}

// 处理触摸滚动（移动端优化）
const handleTouchStart = (event) => {
  const sidebar = sidebarRef.value
  if (!sidebar) return
  
  sidebar.touchStartY = event.touches[0].clientY
  sidebar.touchStartTime = Date.now()
  sidebar.touchStartScrollTop = sidebar.scrollTop
  
  // 取消任何正在进行的动量滚动
  if (scrollAnimationId.value) {
    cancelAnimationFrame(scrollAnimationId.value)
    scrollAnimationId.value = null
  }
  
  scrollVelocity.value = 0
}

const handleTouchMove = (event) => {
  event.preventDefault()
  const sidebar = sidebarRef.value
  if (!sidebar || !sidebar.touchStartY) return
  
  const currentY = event.touches[0].clientY
  const deltaY = sidebar.touchStartY - currentY
  const newScrollTop = sidebar.touchStartScrollTop + deltaY
  
  const maxScroll = sidebar.scrollHeight - sidebar.clientHeight
  sidebar.scrollTop = Math.max(0, Math.min(maxScroll, newScrollTop))
  
  isScrolling.value = true
  updateScrollState()
}

const handleTouchEnd = (event) => {
  const sidebar = sidebarRef.value
  if (!sidebar || !sidebar.touchStartY) return
  
  const endTime = Date.now()
  const deltaTime = endTime - sidebar.touchStartTime
  const deltaY = sidebar.touchStartY - (event.changedTouches[0]?.clientY || sidebar.touchStartY)
  
  // 计算触摸滑动速度
  if (deltaTime > 0 && Math.abs(deltaY) > 10) {
    const velocity = (deltaY / deltaTime) * 16 // 转换为像素/帧
    scrollVelocity.value = Math.max(-SCROLL_CONFIG.maxVelocity, Math.min(SCROLL_CONFIG.maxVelocity, velocity))
    
    // 开始动量滚动
    if (Math.abs(scrollVelocity.value) > SCROLL_CONFIG.minVelocity) {
      momentumScroll()
    }
  }
  
  // 清理触摸数据
  delete sidebar.touchStartY
  delete sidebar.touchStartTime
  delete sidebar.touchStartScrollTop
  
  setTimeout(() => {
    if (Math.abs(scrollVelocity.value) < SCROLL_CONFIG.minVelocity) {
      isScrolling.value = false
    }
  }, SCROLL_CONFIG.smoothDuration)
}

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
    '/admin/messages': '留言管理',
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
    '/admin/messages': '审核和管理用户留言',
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
  
  // 初始化侧边栏折叠状态
  const savedCollapsed = localStorage.getItem('sidebarCollapsed')
  if (savedCollapsed !== null) {
    sidebarCollapsed.value = savedCollapsed === 'true'
  }
  
  // 初始化菜单组展开状态
  const savedMenuGroups = localStorage.getItem('menuGroups')
  if (savedMenuGroups) {
    try {
      menuGroups.value = JSON.parse(savedMenuGroups)
    } catch (e) {
      console.warn('Failed to parse saved menu groups:', e)
    }
  }
  
  // 初始化侧边栏滚动位置
  const savedScrollPosition = localStorage.getItem('sidebarScrollPosition')
  if (savedScrollPosition && sidebarRef.value) {
    setTimeout(() => {
      sidebarRef.value.scrollTop = parseInt(savedScrollPosition)
      updateScrollState()
    }, 100)
  }
  
  // 添加滚动事件监听器
  nextTick(() => {
    if (sidebarRef.value) {
      sidebarRef.value.addEventListener('scroll', updateScrollState, { passive: true })
      updateScrollState() // 初始化滚动状态
    }
  })
  
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

// 组件卸载时保存滚动位置
onUnmounted(() => {
  if (sidebarRef.value) {
    localStorage.setItem('sidebarScrollPosition', sidebarRef.value.scrollTop.toString())
    // 清理滚动事件监听器
    sidebarRef.value.removeEventListener('scroll', updateScrollState)
  }
  
  // 清理动画帧
  if (scrollAnimationId.value) {
    cancelAnimationFrame(scrollAnimationId.value)
  }
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
  
  /* 侧边栏容器样式 */
  .sidebar-container {
    transition: width 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    
    &.expanding {
      animation: sidebarExpand 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    }
    
    &.collapsing {
      animation: sidebarCollapse 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    }
  }
  
  /* 侧边栏样式 */
  .sidebar {
    height: 100%;
    background: var(--sidebar-bg, linear-gradient(145deg, #667eea 0%, #764ba2 100%));
    position: relative;
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    overflow-y: auto !important;
    overflow-x: hidden;
    box-shadow: var(--sidebar-shadow, 8px 0 32px rgba(102, 126, 234, 0.15));
    border-right: 1px solid var(--sidebar-border, rgba(255, 255, 255, 0.1));
    animation: float 6s ease-in-out infinite;
    
    /* 确保滚动功能正常 */
    pointer-events: auto !important;
    user-select: none;
    
    /* 滚动边界弹性效果 */
    overscroll-behavior: contain;
    
    /* 增强的滚动平滑度 */
    -webkit-overflow-scrolling: touch;
    scrollbar-width: thin;
    
    /* 滚动容器优化 */
    will-change: scroll-position;
    transform: translateZ(0);
    
    /* 滚动动画过渡 */
    &.smooth-scrolling {
      scroll-behavior: smooth;
      animation: smoothScroll 0.3s ease-out;
    }
    
    /* 现代化自定义滚动条样式 */
    &::-webkit-scrollbar {
      width: 8px;
      background: transparent;
    }
    
    &::-webkit-scrollbar-track {
      background: rgba(255, 255, 255, 0.05);
      border-radius: 6px;
      margin: 8px 0;
      border: 1px solid rgba(255, 255, 255, 0.08);
    }
    
    &::-webkit-scrollbar-thumb {
      background: linear-gradient(180deg, 
        rgba(255, 255, 255, 0.4) 0%, 
        rgba(255, 255, 255, 0.25) 50%,
        rgba(255, 255, 255, 0.3) 100%
      );
      border-radius: 6px;
      border: 1px solid rgba(255, 255, 255, 0.1);
      box-shadow: 
        0 2px 8px rgba(0, 0, 0, 0.1),
        inset 0 1px 0 rgba(255, 255, 255, 0.3);
      transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
      position: relative;
      
      &:hover {
        background: linear-gradient(180deg, 
          rgba(255, 255, 255, 0.6) 0%, 
          rgba(255, 255, 255, 0.4) 50%,
          rgba(255, 255, 255, 0.5) 100%
        );
        box-shadow: 
          0 4px 15px rgba(0, 0, 0, 0.15),
          inset 0 1px 0 rgba(255, 255, 255, 0.5),
          0 0 0 1px rgba(255, 255, 255, 0.2);
        transform: scaleX(1.2);
      }
      
      &:active {
        background: linear-gradient(180deg, 
          rgba(255, 255, 255, 0.7) 0%, 
          rgba(255, 255, 255, 0.5) 50%,
          rgba(255, 255, 255, 0.6) 100%
        );
        transform: scaleX(1.1);
      }
    }
    
    /* 增强的滚动状态视觉反馈 */
    &.scrolling {
      &::before {
        opacity: 0.9;
        animation: scrollGlow 0.6s ease-out;
      }
      
      &::-webkit-scrollbar-track {
        background: rgba(255, 255, 255, 0.12);
        border-color: rgba(255, 255, 255, 0.15);
      }
      
      &::-webkit-scrollbar-thumb {
        background: linear-gradient(180deg, 
          rgba(255, 255, 255, 0.8) 0%, 
          rgba(255, 255, 255, 0.6) 50%,
          rgba(255, 255, 255, 0.7) 100%
        );
        box-shadow: 
          0 4px 20px rgba(0, 0, 0, 0.2),
          inset 0 1px 0 rgba(255, 255, 255, 0.6),
          0 0 0 2px rgba(255, 255, 255, 0.3),
          0 0 15px rgba(255, 255, 255, 0.4);
        transform: scaleX(1.3);
      }
    }
    
    /* 增强的玻璃态背景效果 */
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: var(--sidebar-glass-bg, 
        linear-gradient(145deg, rgba(255, 255, 255, 0.12) 0%, rgba(255, 255, 255, 0.06) 50%, rgba(255, 255, 255, 0.03) 100%),
        radial-gradient(circle at 20% 20%, rgba(99, 102, 241, 0.08) 0%, transparent 50%),
        radial-gradient(circle at 80% 80%, rgba(139, 92, 246, 0.06) 0%, transparent 50%),
        radial-gradient(circle at 50% 10%, rgba(236, 72, 153, 0.04) 0%, transparent 40%)
      );
      backdrop-filter: blur(25px) saturate(1.2);
      -webkit-backdrop-filter: blur(25px) saturate(1.2);
      border: 1px solid rgba(255, 255, 255, 0.08);
      border-radius: 0 20px 20px 0;
      pointer-events: none;
      box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.1), inset 0 -1px 0 rgba(0, 0, 0, 0.05);
    }
    
    /* 动态光效 */
    &::after {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: var(--sidebar-light-effect, conic-gradient(from 0deg, transparent, rgba(255, 255, 255, 0.1), transparent));
      animation: rotate 20s linear infinite;
      pointer-events: none;
      opacity: var(--sidebar-light-opacity, 0.3);
    }
    
    /* 折叠按钮 */
    .collapse-btn {
      position: absolute;
      top: 20px;
      right: -15px;
      width: 32px;
      height: 32px;
      background: var(--collapse-btn-bg, linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%));
      border: 1px solid var(--collapse-btn-border, rgba(255, 255, 255, 0.3));
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      z-index: 100;
      color: var(--collapse-btn-color, #667eea);
      box-shadow: var(--collapse-btn-shadow, 0 8px 25px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.6));
      backdrop-filter: blur(10px);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      
      &:hover {
        transform: scale(1.15) rotate(-5deg);
        box-shadow: var(--collapse-btn-hover-shadow, 0 12px 35px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.8));
        background: var(--collapse-btn-hover-bg, linear-gradient(135deg, rgba(255, 255, 255, 1) 0%, rgba(255, 255, 255, 0.95) 100%));
        color: var(--collapse-btn-hover-color, #764ba2);
      }
      
      &:active {
        transform: scale(0.95);
      }
    }
    
    /* 滚动进度指示器 */
    .scroll-indicator {
      position: absolute;
      top: 60px;
      right: 2px;
      width: 4px;
      height: calc(100% - 120px);
      background: rgba(255, 255, 255, 0.1);
      border-radius: 2px;
      opacity: 0;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      z-index: 50;
      
      &.active {
        opacity: 1;
        animation: scrollIndicator 0.6s ease-out;
      }
      
      .scroll-progress {
        width: 100%;
        background: linear-gradient(180deg, 
          rgba(255, 255, 255, 0.8) 0%, 
          rgba(255, 255, 255, 0.6) 50%,
          rgba(255, 255, 255, 0.7) 100%
        );
        border-radius: 2px;
        transition: height 0.2s cubic-bezier(0.25, 0.46, 0.45, 0.94);
        box-shadow: 
          0 2px 8px rgba(0, 0, 0, 0.2),
          inset 0 1px 0 rgba(255, 255, 255, 0.4);
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: linear-gradient(180deg, transparent 0%, rgba(255, 255, 255, 0.3) 100%);
          border-radius: 2px;
        }
      }
    }
    
    /* 滚动边界反馈 */
    .scroll-boundary-top,
    .scroll-boundary-bottom {
      position: absolute;
      left: 0;
      width: 100%;
      height: 20px;
      pointer-events: none;
      opacity: 0;
      transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
      z-index: 40;
      
      &.active {
        opacity: 1;
        animation: scrollBounce 0.6s ease-out;
      }
    }
    
    .scroll-boundary-top {
      top: 0;
      background: linear-gradient(180deg, 
        rgba(255, 255, 255, 0.3) 0%, 
        rgba(255, 255, 255, 0.1) 50%,
        transparent 100%
      );
      border-radius: 0 0 10px 10px;
      
      &.active {
        box-shadow: 
          inset 0 10px 20px rgba(255, 255, 255, 0.2),
          0 5px 15px rgba(0, 0, 0, 0.1);
      }
    }
    
    .scroll-boundary-bottom {
      bottom: 0;
      background: linear-gradient(0deg, 
        rgba(255, 255, 255, 0.3) 0%, 
        rgba(255, 255, 255, 0.1) 50%,
        transparent 100%
      );
      border-radius: 10px 10px 0 0;
      
      &.active {
        box-shadow: 
          inset 0 -10px 20px rgba(255, 255, 255, 0.2),
          0 -5px 15px rgba(0, 0, 0, 0.1);
      }
    }
    
    /* 折叠状态 */
    &.collapsed {
      .logo {
        padding: 25px 15px;
        
        .logo-container {
          justify-content: center;
          
          .logo-text {
            display: none;
          }
          
          .logo-icon .icon-wrapper {
            width: 48px;
            height: 48px;
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0.15) 100%);
            border: 2px solid rgba(255, 255, 255, 0.4);
            box-shadow: 0 12px 35px rgba(0, 0, 0, 0.15), inset 0 2px 0 rgba(255, 255, 255, 0.5);
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
            
            &:hover {
              transform: scale(1.15) rotate(-8deg);
              box-shadow: 0 16px 45px rgba(0, 0, 0, 0.2), inset 0 2px 0 rgba(255, 255, 255, 0.7);
              background: linear-gradient(135deg, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.2) 100%);
            }
          }
        }
      }
      
      .menu-section {
        padding: 20px 8px;
        
        .menu-title {
          display: none;
        }
        
        .el-menu {
          .menu-item {
            margin: 0 0 16px 0;
            border-radius: 20px;
            transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
            
            &:hover {
              transform: translateX(8px) scale(1.08);
              box-shadow: 0 12px 40px rgba(99, 102, 241, 0.25), 0 6px 20px rgba(139, 92, 246, 0.15);
              
              .menu-item-content .menu-icon {
                transform: scale(1.3) rotate(-10deg);
                background: linear-gradient(135deg, rgba(99, 102, 241, 0.9) 0%, rgba(139, 92, 246, 0.8) 100%);
                box-shadow: 0 15px 40px rgba(99, 102, 241, 0.4), 0 8px 20px rgba(139, 92, 246, 0.3), inset 0 2px 0 rgba(255, 255, 255, 0.6);
                border-color: rgba(255, 255, 255, 0.5);
                
                &::before {
                  opacity: 1;
                  left: 100%;
                }
                
                &::after {
                  opacity: 1;
                }
              }
            }
            
            &.is-active {
              transform: translateX(8px) scale(1.05);
              
              .menu-item-content .menu-icon {
                background: linear-gradient(135deg, rgba(99, 102, 241, 1) 0%, rgba(139, 92, 246, 0.9) 100%);
                transform: scale(1.25);
                box-shadow: 0 12px 35px rgba(99, 102, 241, 0.45), 0 6px 18px rgba(139, 92, 246, 0.35), inset 0 2px 0 rgba(255, 255, 255, 0.7);
                animation: pulse 2s infinite;
              }
            }
            
            .menu-item-content {
              justify-content: center;
              padding: 18px 12px;
              
              span {
                display: none;
              }
              
              .menu-indicator {
                display: none;
              }
              
              .menu-icon {
                width: 52px;
                height: 52px;
                margin: 0;
                background: linear-gradient(135deg, rgba(99, 102, 241, 0.8) 0%, rgba(139, 92, 246, 0.7) 100%);
                border: 2px solid rgba(255, 255, 255, 0.3);
                border-radius: 18px;
                font-size: 24px;
                color: rgba(255, 255, 255, 0.95);
                box-shadow: 0 10px 30px rgba(99, 102, 241, 0.2), 0 5px 15px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.4);
                transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
                position: relative;
                overflow: hidden;
                
                &::before {
                  content: '';
                  position: absolute;
                  top: 0;
                  left: -100%;
                  width: 100%;
                  height: 100%;
                  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.5) 50%, transparent 100%);
                  opacity: 0;
                  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
                }
                
                &::after {
                  content: '';
                  position: absolute;
                  top: 0;
                  left: 0;
                  width: 100%;
                  height: 100%;
                  background: linear-gradient(135deg, rgba(255, 255, 255, 0.25) 0%, transparent 100%);
                  opacity: 0;
                  transition: opacity 0.4s ease;
                  border-radius: 16px;
                }
              }
            }
          }
        }
      }
      
      .collapse-btn {
        right: -15px;
        background: linear-gradient(135deg, rgba(99, 102, 241, 0.9) 0%, rgba(139, 92, 246, 0.8) 100%);
        box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
        
        &:hover {
          transform: scale(1.1);
          box-shadow: 0 12px 35px rgba(99, 102, 241, 0.4);
        }
      }
    }
      
      /* Logo 区域 - 现代化设计 */
      .logo {
        padding: 30px 25px;
        margin: 15px;
        border-radius: 20px;
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(255, 255, 255, 0.1);
        position: relative;
        overflow: hidden;
        backdrop-filter: blur(15px);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.1);
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        
        /* 装饰光效 */
        &::after {
          content: '';
          position: absolute;
          bottom: 8px;
          left: 25px;
          right: 25px;
          height: 2px;
          background: var(--logo-decoration, linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.4) 20%, rgba(99, 102, 241, 0.6) 50%, rgba(255, 255, 255, 0.4) 80%, transparent 100%));
          border-radius: 1px;
          opacity: 0.8;
        }
      
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: var(--logo-hover-effect, linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent));
          transition: left 0.8s ease;
          border-radius: 20px;
        }
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.15);
          border-color: rgba(255, 255, 255, 0.2);
          
          &::before {
            left: 100%;
          }
        }
      
      .logo-container {
        display: flex;
        align-items: center;
        gap: 15px;
        
        .logo-icon {
          .icon-wrapper {
            width: 52px;
            height: 52px;
            background: var(--logo-icon-bg, linear-gradient(135deg, rgba(255, 255, 255, 0.25) 0%, rgba(255, 255, 255, 0.1) 100%));
            border: 1px solid var(--logo-icon-border, rgba(255, 255, 255, 0.3));
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            backdrop-filter: blur(10px);
            color: var(--logo-icon-color, white);
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
            box-shadow: var(--logo-icon-shadow, 0 8px 25px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.4));
            position: relative;
            overflow: hidden;
            
            &::before {
              content: '';
              position: absolute;
              top: -50%;
              left: -50%;
              width: 200%;
              height: 200%;
              background: var(--logo-icon-shine, linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.2) 50%, transparent 70%));
              transform: rotate(-45deg);
              transition: all 0.6s ease;
              opacity: 0;
            }
            
            &:hover {
              transform: scale(1.08) rotate(-3deg);
              box-shadow: var(--logo-icon-hover-shadow, 0 12px 35px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.6));
              
              &::before {
                opacity: 1;
                transform: rotate(-45deg) translate(100%, 100%);
              }
            }
          }
        }
        
        .logo-text {
          h2 {
            color: var(--logo-title-color, #ffffff);
            margin: 0 0 5px 0;
            font-size: 24px;
            font-weight: 800;
            text-shadow: var(--logo-title-shadow, 0 2px 4px rgba(0, 0, 0, 0.1));
            letter-spacing: -0.5px;
          }
          
          span {
            color: var(--logo-subtitle-color, rgba(255, 255, 255, 0.85));
            font-size: 12px;
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 1px;
            text-shadow: var(--logo-subtitle-shadow, 0 1px 2px rgba(0, 0, 0, 0.1));
          }
        }
      }
    }
    
    /* 菜单区域 */
    .menu-section {
      padding: 25px 20px 15px;
      
      .menu-title {
        font-size: 11px;
        font-weight: 800;
        color: var(--menu-title-color, rgba(255, 255, 255, 0.7));
        text-transform: uppercase;
        letter-spacing: 1.5px;
        margin: 0 15px 15px 15px;
        padding: 12px 18px;
        position: relative;
        cursor: pointer;
        border-radius: 16px;
        background: rgba(255, 255, 255, 0.03);
        border: 1px solid rgba(255, 255, 255, 0.08);
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        display: flex;
        align-items: center;
        justify-content: space-between;
        backdrop-filter: blur(15px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05), inset 0 1px 0 rgba(255, 255, 255, 0.05);
        
        &:hover {
          background: var(--menu-title-hover-bg, rgba(255, 255, 255, 0.1));
          color: var(--menu-title-hover-color, rgba(255, 255, 255, 0.95));
          transform: translateX(3px);
          box-shadow: var(--menu-title-hover-shadow, 0 4px 15px rgba(0, 0, 0, 0.1));
        }
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          width: 3px;
          height: 14px;
          background: var(--menu-title-indicator, linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.4) 100%));
          border-radius: 2px;
          transform: translateY(-50%);
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          box-shadow: var(--menu-title-indicator-shadow, 0 0 8px rgba(255, 255, 255, 0.3));
        }
        
        &:hover::before {
          width: 4px;
          height: 18px;
          box-shadow: var(--menu-title-indicator-hover-shadow, 0 0 12px rgba(255, 255, 255, 0.5));
        }
        
        .menu-toggle-icon {
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          color: var(--menu-toggle-color, rgba(255, 255, 255, 0.6));
          filter: var(--menu-toggle-filter, drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1)));
          
          &.rotated {
            transform: rotate(-90deg);
          }
          
          &:hover {
            color: var(--menu-toggle-hover-color, rgba(255, 255, 255, 0.9));
            transform: scale(1.1);
          }
        }
      }
      
      .el-menu {
        border-right: none;
        background: transparent;
        
        .menu-item {
          margin: 0 0 12px 0;
          border-radius: 18px;
          overflow: hidden;
          position: relative;
          transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
          cursor: pointer;
          backdrop-filter: blur(15px);
          border: 1px solid rgba(99, 102, 241, 0.15);
          box-shadow: var(--menu-item-shadow);
          
          /* 背景渐变动画 */
          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 0;
            height: 100%;
            background: var(--menu-item-hover-bg);
            transition: width 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
            z-index: 0;
            border-radius: 18px;
          }
          
          /* 波纹效果 */
          &::after {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            background: radial-gradient(circle, rgba(255, 255, 255, 0.4) 0%, transparent 70%);
            border-radius: 50%;
            transform: translate(-50%, -50%);
            transition: all 0.8s ease;
            z-index: 1;
            opacity: 0;
          }
          
          /* 悬停状态 */
          &:hover {
            transform: translateX(12px) scale(1.04);
            box-shadow: var(--menu-item-hover-shadow);
            border-color: rgba(99, 102, 241, 0.25);
            
            &::before {
              width: 100%;
            }
            
            &::after {
              width: 140px;
              height: 140px;
              opacity: 1;
              animation: rippleEffect 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
            }
            
            .menu-item-content {
              color: rgba(255, 255, 255, 0.95);
              
              .menu-icon {
                background: var(--menu-icon-bg);
                color: var(--menu-icon-color);
                transform: scale(1.25) rotate(-5deg);
                box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3), 0 4px 12px rgba(139, 92, 246, 0.2), inset 0 1px 0 rgba(255, 255, 255, 0.4);
              }
              
              .menu-indicator {
                opacity: 1;
                transform: translateX(0) scale(1.3);
                box-shadow: 0 0 15px rgba(255, 255, 255, 0.9);
              }
              
              span {
                transform: translateX(4px);
                text-shadow: 0 2px 6px rgba(0, 0, 0, 0.4), 0 1px 3px rgba(0, 0, 0, 0.3);
              }
            }
          }
          
          /* 激活状态 */
          &.is-active {
            background: var(--menu-item-active-bg);
            box-shadow: var(--menu-item-hover-shadow);
            transform: translateX(12px) scale(1.04);
            border-color: rgba(99, 102, 241, 0.3);
            
            &::before {
              width: 100%;
            }
            
            .menu-item-content {
              color: rgba(255, 255, 255, 1);
              
              .menu-icon {
                background: var(--menu-icon-bg);
                color: var(--menu-icon-color);
                transform: scale(1.2);
                box-shadow: 0 8px 25px rgba(99, 102, 241, 0.35), 0 4px 12px rgba(139, 92, 246, 0.25), inset 0 1px 0 rgba(255, 255, 255, 0.5);
              }
              
              .menu-indicator {
                opacity: 1;
                transform: translateX(0) scale(1.4);
                box-shadow: 0 0 20px rgba(255, 255, 255, 1);
                animation: pulse 2s infinite;
              }
            }
          }
          
          /* 点击效果 */
          &:active {
            transform: translateX(6px) scale(0.98);
            transition: all 0.1s ease;
          }
          
          .menu-item-content {
            display: flex;
            align-items: center;
            gap: 18px;
            font-weight: 600;
            font-size: 15px;
            color: var(--menu-item-color, rgba(255, 255, 255, 0.85));
            padding: 18px 24px;
            position: relative;
            z-index: 2;
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3), 0 1px 2px rgba(0, 0, 0, 0.2);
            
            .menu-icon {
              width: 46px;
              height: 46px;
              background: var(--menu-icon-bg);
              border: 1px solid var(--menu-icon-border);
              border-radius: 16px;
              display: flex;
              align-items: center;
              justify-content: center;
              transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
              font-size: 21px;
              color: var(--menu-icon-color);
              position: relative;
              overflow: hidden;
              backdrop-filter: blur(15px);
              box-shadow: 0 6px 20px rgba(99, 102, 241, 0.15), 0 3px 8px rgba(0, 0, 0, 0.1);
              
              &::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.4) 50%, transparent 100%);
                opacity: 0;
                transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
              }
              
              &:hover::before {
                opacity: 1;
                left: 100%;
                animation: shimmer 1.5s ease-in-out infinite;
              }
              
              &::after {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, transparent 100%);
                opacity: 0;
                transition: opacity 0.3s ease;
              }
              
              &:hover::after {
                opacity: 1;
              }
            }
            
            span {
              flex: 1;
              font-weight: 600;
              transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
              position: relative;
              
              &::after {
                content: '';
                position: absolute;
                bottom: -2px;
                left: 0;
                width: 0;
                height: 2px;
                background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.8) 50%, transparent 100%);
                transition: width 0.3s ease;
              }
            }
            
            .menu-indicator {
              width: 10px;
              height: 10px;
              background: radial-gradient(circle, rgba(255, 255, 255, 1) 0%, rgba(255, 255, 255, 0.8) 100%);
              border-radius: 50%;
              opacity: 0;
              transform: translateX(-12px) scale(0.4);
              transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
              position: relative;
              box-shadow: 0 0 8px rgba(255, 255, 255, 0.6);
              
              &::before {
                content: '';
                position: absolute;
                top: -3px;
                left: -3px;
                width: 16px;
                height: 16px;
                border: 1px solid rgba(255, 255, 255, 0.4);
                border-radius: 50%;
                opacity: 0;
                transition: all 0.4s ease;
              }
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

/* 动画关键帧 */
@keyframes pulse {
  0% {
    transform: translateX(0) scale(1.2);
    box-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
  }
  50% {
    transform: translateX(0) scale(1.4);
    box-shadow: 0 0 20px rgba(255, 255, 255, 1);
  }
  100% {
    transform: translateX(0) scale(1.2);
    box-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
  }
}

@keyframes menuItemSlideIn {
  0% {
    opacity: 0;
    transform: translateX(-20px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes iconRotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: translateX(100%);
    opacity: 0;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-4px);
  }
}

@keyframes glow {
  0%, 100% {
    box-shadow: 0 0 20px rgba(99, 102, 241, 0.3);
  }
  50% {
    box-shadow: 0 0 35px rgba(99, 102, 241, 0.6), 0 0 50px rgba(139, 92, 246, 0.4);
  }
}

@keyframes rippleEffect {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
    transform: translate(-50%, -50%) scale(0);
  }
  50% {
    opacity: 0.6;
    transform: translate(-50%, -50%) scale(0.8);
  }
  100% {
    width: 140px;
    height: 140px;
    opacity: 0;
    transform: translate(-50%, -50%) scale(1.2);
  }
}

@keyframes sidebarExpand {
  0% {
    width: 80px;
    opacity: 0.8;
    transform: translateX(-10px);
  }
  50% {
    opacity: 0.9;
    transform: translateX(5px);
  }
  100% {
    width: 280px;
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes sidebarCollapse {
  0% {
    width: 280px;
    opacity: 1;
    transform: translateX(0);
  }
  50% {
    opacity: 0.9;
    transform: translateX(-5px);
  }
  100% {
    width: 80px;
    opacity: 0.8;
    transform: translateX(-10px);
  }
}

/* 滚动发光动画 */
@keyframes scrollGlow {
  0% {
    box-shadow: inset 0 0 0 rgba(255, 255, 255, 0.3);
  }
  50% {
    box-shadow: inset 0 0 20px rgba(255, 255, 255, 0.6);
  }
  100% {
    box-shadow: inset 0 0 0 rgba(255, 255, 255, 0.3);
  }
}

/* 脉冲动画 */
@keyframes pulse {
  0% {
    transform: scale(1.25);
    box-shadow: 0 12px 35px rgba(99, 102, 241, 0.45), 0 6px 18px rgba(139, 92, 246, 0.35), inset 0 2px 0 rgba(255, 255, 255, 0.7);
  }
  50% {
    transform: scale(1.3);
    box-shadow: 0 15px 40px rgba(99, 102, 241, 0.5), 0 8px 22px rgba(139, 92, 246, 0.4), inset 0 2px 0 rgba(255, 255, 255, 0.8);
  }
  100% {
    transform: scale(1.25);
    box-shadow: 0 12px 35px rgba(99, 102, 241, 0.45), 0 6px 18px rgba(139, 92, 246, 0.35), inset 0 2px 0 rgba(255, 255, 255, 0.7);
  }
}

/* 闪光动画 */
@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

/* 平滑滚动动画 */
@keyframes smoothScroll {
  0% {
    transform: translateY(-2px);
  }
  100% {
    transform: translateY(0);
  }
}

/* 滚动边界弹性动画 */
@keyframes scrollBounce {
  0% {
    transform: translateY(0);
  }
  25% {
    transform: translateY(-8px);
  }
  50% {
    transform: translateY(-4px);
  }
  75% {
    transform: translateY(-2px);
  }
  100% {
    transform: translateY(0);
  }
}

/* 滚动惯性缓动 */
@keyframes scrollEasing {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.02);
    opacity: 0.95;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 滚动指示器动画 */
@keyframes scrollIndicator {
  0% {
    opacity: 0;
    transform: translateX(10px);
  }
  50% {
    opacity: 1;
    transform: translateX(0);
  }
  100% {
    opacity: 0;
    transform: translateX(-10px);
  }
}

/* 菜单折叠过渡动画 */
.menu-collapse-enter-active,
.menu-collapse-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: top;
}

.menu-collapse-enter-from {
  opacity: 0;
  transform: scaleY(0) translateY(-10px);
  max-height: 0;
}

.menu-collapse-leave-to {
  opacity: 0;
  transform: scaleY(0) translateY(-10px);
  max-height: 0;
}

.menu-collapse-enter-to,
.menu-collapse-leave-from {
  opacity: 1;
  transform: scaleY(1) translateY(0);
  max-height: 1000px;
}
</style>