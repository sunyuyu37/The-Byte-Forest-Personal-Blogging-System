<template>
  <div class="main-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-content">
          <!-- Logo -->
          <div class="logo">
            <router-link to="/">
              <h1 class="handwriting">字节森林</h1>
            </router-link>
          </div>
          
          <!-- 导航菜单 -->
          <nav class="nav">
            <el-menu
              :default-active="activeMenu"
              mode="horizontal"
              :ellipsis="false"
              background-color="transparent"
              text-color="#333"
              active-text-color="#409eff"
              @select="handleMenuSelect"
            >
              <el-menu-item index="/">
                <el-icon><Grape /></el-icon>
                <span>首页</span>
              </el-menu-item>
              <el-sub-menu index="modules">
                <template #title>
                  <el-icon><Grid /></el-icon>
                  <span>模块</span>
                </template>
                <el-menu-item index="/tech-share">
                  <el-icon><Monitor /></el-icon>
                  <span>技术分享</span>
                </el-menu-item>
                <el-menu-item index="/life-essays">
                  <el-icon><Edit /></el-icon>
                  <span>生活随笔</span>
                </el-menu-item>
                <el-menu-item index="/study-notes">
                  <el-icon><Notebook /></el-icon>
                  <span>学习笔记</span>
                </el-menu-item>
                <el-menu-item index="/project-practice">
                  <el-icon><Tools /></el-icon>
                  <span>项目实战</span>
                </el-menu-item>
                <el-menu-item index="/news-info">
                  <el-icon><BellFilled /></el-icon>
                  <span>新闻资讯</span>
                </el-menu-item>
                <el-menu-item index="/competition-events">
                  <el-icon><Trophy /></el-icon>
                  <span>竞赛活动</span>
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item index="/articles">
                <el-icon><Document /></el-icon>
                <span>文章</span>
              </el-menu-item>
              <el-menu-item index="/search">
                <el-icon><Orange /></el-icon>
                <span>搜索</span>
              </el-menu-item>
              <el-menu-item index="/about">
                <el-icon><Star /></el-icon>
                <span>关于</span>
              </el-menu-item>
            </el-menu>
          </nav>
          
          <!-- 用户操作区 -->
          <div class="user-actions">
            <!-- 搜索 -->
            <div class="search">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索文章..."
                :prefix-icon="Search"
                @keyup.enter="handleSearch"
                clearable
              />
            </div>
            
            <!-- 主题切换 -->
            <el-button
              :icon="isDark ? Sunny : Moon"
              circle
              @click="toggleTheme"
            />
            
            <!-- 用户菜单 -->
            <div v-if="userStore.isLoggedIn" class="user-menu">
              <el-dropdown @command="handleUserCommand">
                <span class="user-info">
                  <el-avatar :src="userStore.user?.avatar" :size="32">
                    {{ userStore.user?.nickname?.charAt(0) }}
                  </el-avatar>
                  <span class="username">{{ userStore.user?.nickname }}</span>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item v-if="userStore.isAdmin" command="admin">管理后台</el-dropdown-item>
                    <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            
            <!-- 登录/注册 -->
            <div v-else class="auth-buttons">
              <el-button @click="$router.push('/auth/login')">登录</el-button>
              <el-button type="primary" @click="$router.push('/auth/register')">注册</el-button>
            </div>
          </div>
        </div>
      </div>
    </header>
    
    <!-- 主要内容区 -->
    <main class="main">
      <router-view v-slot="{ Component, route }">
          <transition :name="getMainTransitionName(route)" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
    </main>
    
    <!-- 底部 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <p>&copy; 2025 字节森林. All rights reserved.</p>
          <div class="footer-links">
            <router-link to="/privacy" class="footer-link">隐私政策</router-link>
            <router-link to="/terms" class="footer-link">使用条款</router-link>
            <router-link to="/contact" class="footer-link">联系我们</router-link>
          </div>
        </div>
      </div>
    </footer>
    
    <!-- 公告弹窗 -->
    <AnnouncementModal
      ref="announcementModalRef"
      v-model="showAnnouncementModal"
      :announcements="announcements"
      @close="handleAnnouncementClose"
      @all-read="handleAllRead"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Search, Moon, Sunny, Grape, Grid, Document, Orange, Star, Monitor, Edit, Notebook, Tools, BellFilled, Trophy } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import AnnouncementModal from '@/components/AnnouncementModal.vue'
import announcementApi from '@/api/announcement'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 根据前台页面类型返回不同的过渡名称
const getMainTransitionName = (route) => {
  // 首页使用特殊的缩放效果
  if (route.name === 'Home') {
    return 'scale-fade'
  }
  // 文章详情页使用深度效果
  if (route.name === 'ArticleDetail') {
    return 'depth-slide'
  }
  // 搜索页面使用快速滑动
  if (route.name === 'Search') {
    return 'quick-slide'
  }
  // 模块页面使用卡片翻转效果
  if (['TechShare', 'LifeEssays', 'StudyNotes', 'ProjectPractice', 'NewsInfo', 'CompetitionEvents'].includes(route.name)) {
    return 'card-flip'
  }
  // 其他页面使用平滑滑动
  return 'smooth-slide'
}

const searchKeyword = ref('')
const isDark = ref(false)

// 公告相关状态
const showAnnouncementModal = ref(false)
const announcements = ref([])
const announcementModalRef = ref(null)

// 当前激活的菜单项
const activeMenu = computed(() => {
  const path = route.path
  if (path === '/') return '/'
  if (path.startsWith('/articles')) return '/articles'
  if (path === '/search') return '/search'
  if (path === '/about') return '/about'
  // 新的模块页面
  if (['/tech-share', '/life-essays', '/study-notes', '/project-practice', '/news-info', '/competition-events'].includes(path)) {
    return path
  }
  return path
})

// 搜索处理
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { q: searchKeyword.value.trim() }
    })
  }
}

// 主题切换
const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

// 菜单选择处理
const handleMenuSelect = (index) => {
  if (route.path !== index) {
    router.push(index)
  }
}

// 用户菜单命令处理
const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('退出登录成功')
      router.push('/')
      break
  }
}

// 检查并显示公告
const checkAnnouncements = async () => {
  try {
    // 检查用户是否已登录
    if (!userStore.isLoggedIn) {
      return
    }
    
    // 检查是否应该显示公告（今日未隐藏）
    if (announcementModalRef.value && !announcementModalRef.value.shouldShowAnnouncements()) {
      return
    }
    
    // 获取未读公告
    const response = await announcementApi.getUnreadAnnouncements()
    if (response.data && response.data.length > 0) {
      announcements.value = response.data
      showAnnouncementModal.value = true
    }
  } catch (error) {
    console.error('获取公告失败:', error)
  }
}

// 处理公告弹窗关闭
const handleAnnouncementClose = () => {
  showAnnouncementModal.value = false
}

// 处理全部已读
const handleAllRead = () => {
  announcements.value = []
  showAnnouncementModal.value = false
}

// 监听用户登录状态变化
watch(() => userStore.isLoggedIn, (newValue) => {
  if (newValue) {
    // 用户登录后延迟检查公告，确保页面加载完成
    setTimeout(() => {
      checkAnnouncements()
    }, 1000)
  }
}, { immediate: true })

// 初始化主题
onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    isDark.value = true
    document.documentElement.classList.add('dark')
  }
  
  // 如果用户已登录，检查公告
  if (userStore.isLoggedIn) {
    setTimeout(() => {
      checkAnnouncements()
    }, 1500)
  }
})
</script>

<style lang="scss" scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border-color-lighter);
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all 0.3s ease;
  
  &::before {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: var(--primary-gradient);
    transform: scaleX(0);
    transition: transform 0.3s ease;
  }
  
  &:hover::before {
    transform: scaleX(1);
  }
  
  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 70px;
    padding: 0 var(--spacing-lg);
  }
  
  .logo {
    h1 {
      background: var(--primary-gradient);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      font-size: 28px;
      font-weight: 700;
      letter-spacing: -0.5px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: scale(1.05);
      }
      
      &.handwriting {
        font-family: 'Ma Shan Zheng', 'Liu Jian Mao Cao', 'Zhi Mang Xing', cursive;
        font-size: 36px;
        font-weight: 500;
        letter-spacing: 6px;
        line-height: 1.1;
        background: linear-gradient(45deg, #10b981 0%, #059669 25%, #047857 50%, #065f46 75%, #064e3b 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        text-shadow: 0 3px 6px rgba(16, 185, 129, 0.3), 0 1px 2px rgba(5, 150, 105, 0.4);
        display: inline-block;
        transform-origin: center;
        position: relative;
        animation: gentle-glow 3s ease-in-out infinite alternate;
        white-space: nowrap;
        overflow: visible;
        
        &::before {
          content: '';
          position: absolute;
          top: -2px;
          left: -2px;
          right: -2px;
          bottom: -2px;
          background: linear-gradient(45deg, rgba(16, 185, 129, 0.1), rgba(5, 150, 105, 0.1));
          border-radius: 8px;
          z-index: -1;
          opacity: 0;
          transition: opacity 0.3s ease;
        }
        
        &:hover::before {
          opacity: 1;
        }
      }
      
      @keyframes gentle-glow {
        0% {
          filter: brightness(1) saturate(1);
        }
        100% {
          filter: brightness(1.1) saturate(1.2);
        }
      }
    }
  }
  
  .nav {
    flex: 1;
    margin: 0 var(--spacing-2xl);
    
    :deep(.el-menu) {
      border-bottom: none;
      
      .el-menu-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 0 20px;
        transition: all 0.3s ease;
        
        .el-icon {
          font-size: 16px;
          color: #666;
          transition: all 0.3s ease;
        }
        
        span {
          font-weight: 500;
        }
        
        &:hover {
          background-color: rgba(64, 158, 255, 0.1);
          
          .el-icon {
            color: #409eff;
            transform: scale(1.1);
          }
        }
        
        &.is-active {
          color: #409eff;
          
          .el-icon {
            color: #409eff;
            transform: scale(1.1);
          }
        }
      }
      
      .el-sub-menu {
        // 子菜单项样式
        .el-menu-item {
          padding-left: 40px;
          
          .el-icon {
            font-size: 14px;
            color: #666;
            transition: all 0.3s ease;
          }
          
          &:hover {
            background-color: rgba(64, 158, 255, 0.08);
            
            .el-icon {
              color: #409eff;
              transform: scale(1.1);
            }
          }
          
          &.is-active {
            background-color: rgba(64, 158, 255, 0.15);
            color: #409eff;
            
            .el-icon {
              color: #409eff;
              transform: scale(1.1);
            }
          }
        }
        
        .el-sub-menu__title {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 0 20px;
          position: relative;
          
          .el-icon {
            font-size: 16px;
            color: #666;
            transition: all 0.3s ease;
          }
          
          span {
            font-weight: 500;
          }
          
          // 调整下拉箭头位置
          .el-sub-menu__icon-arrow {
            position: absolute;
            right: 8px;
            top: 50%;
            transform: translateY(-50%) rotate(0deg);
            font-size: 12px;
            color: #999;
            transition: all 0.3s ease;
          }
          
          &:hover {
            .el-icon {
              color: #409eff;
              transform: scale(1.1);
            }
            
            .el-sub-menu__icon-arrow {
              color: #409eff;
              transform: translateY(-50%) rotate(180deg);
            }
          }
        }
        
        &.is-opened {
          .el-sub-menu__title {
             .el-sub-menu__icon-arrow {
               transform: translateY(-50%) rotate(180deg);
               color: #409eff;
             }
           }
        }
        
        &.is-active {
          .el-sub-menu__title {
            color: #409eff;
            
            .el-icon {
              color: #409eff;
              transform: scale(1.1);
            }
            
            .el-sub-menu__icon-arrow {
              color: #409eff;
            }
          }
        }
      }
    }
  }
  
  .user-actions {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    
    .search {
      width: 240px;
      
      :deep(.el-input) {
        .el-input__wrapper {
          border-radius: var(--border-radius-xl);
          box-shadow: var(--shadow-sm);
          transition: all 0.3s ease;
          
          &:hover {
            box-shadow: var(--shadow-md);
          }
          
          &.is-focus {
            box-shadow: var(--shadow-lg);
            border-color: var(--primary-color);
          }
        }
      }
    }
    
    .user-menu {
      .user-info {
        display: flex;
        align-items: center;
        gap: var(--spacing-sm);
        cursor: pointer;
        padding: var(--spacing-sm) var(--spacing-md);
        border-radius: var(--border-radius-lg);
        transition: all 0.3s ease;
        
        &:hover {
          background: var(--background-color-base);
          transform: translateY(-1px);
        }
        
        .username {
          font-size: 14px;
          font-weight: 500;
          color: var(--text-color-primary);
        }
      }
    }
    
    .auth-buttons {
      display: flex;
      gap: var(--spacing-sm);
    }
  }
}

.main {
  flex: 1;
  background: var(--background-color-base);
  min-height: calc(100vh - 140px);
}

.footer {
  background: var(--background-color-light);
  border-top: 1px solid var(--border-color-lighter);
  padding: var(--spacing-2xl) 0;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: var(--primary-gradient);
  }
  
  .footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    p {
      color: var(--text-color-secondary);
      font-size: 14px;
      font-weight: 500;
    }
    
    .footer-links {
      display: flex;
      gap: var(--spacing-xl);
      
      .footer-link {
        color: var(--text-color-secondary);
        font-size: 14px;
        font-weight: 500;
        padding: var(--spacing-xs) var(--spacing-sm);
        border-radius: var(--border-radius-md);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 50%;
          width: 0;
          height: 2px;
          background: var(--primary-gradient);
          transition: all 0.3s ease;
          transform: translateX(-50%);
        }
        
        &:hover {
          color: var(--primary-color);
          transform: translateY(-2px);
          
          &::after {
            width: 100%;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .header {
    .header-content {
      flex-direction: column;
      height: auto;
      padding: 10px 0;
    }
    
    .nav {
      margin: 10px 0;
    }
    
    .user-actions {
      .search {
        width: 150px;
      }
    }
  }
  
  .footer {
    .footer-content {
      flex-direction: column;
      gap: 10px;
      text-align: center;
    }
  }
}

/* 前台页面专用过渡动画 */
.scale-fade-enter-active,
.scale-fade-leave-active {
  transition: all 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.scale-fade-enter-from {
  opacity: 0;
  transform: scale(0.8) translateY(30px);
}

.scale-fade-leave-to {
  opacity: 0;
  transform: scale(1.2) translateY(-30px);
}

.depth-slide-enter-active,
.depth-slide-leave-active {
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.depth-slide-enter-from {
  opacity: 0;
  transform: translateZ(-100px) rotateX(15deg);
}

.depth-slide-leave-to {
  opacity: 0;
  transform: translateZ(100px) rotateX(-15deg);
}

.quick-slide-enter-active,
.quick-slide-leave-active {
  transition: all 0.25s ease-out;
}

.quick-slide-enter-from {
  opacity: 0;
  transform: translateX(100px);
}

.quick-slide-leave-to {
  opacity: 0;
  transform: translateX(-100px);
}

.card-flip-enter-active,
.card-flip-leave-active {
  transition: all 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.card-flip-enter-from {
  opacity: 0;
  transform: rotateY(90deg) scale(0.8);
}

.card-flip-leave-to {
  opacity: 0;
  transform: rotateY(-90deg) scale(0.8);
}

.smooth-slide-enter-active,
.smooth-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.smooth-slide-enter-from {
  opacity: 0;
  transform: translateX(20px) scale(0.98);
}

.smooth-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px) scale(0.98);
}
</style>