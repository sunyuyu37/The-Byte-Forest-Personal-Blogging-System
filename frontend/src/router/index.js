import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
// 移除直接导入，改为动态导入以优化性能

// 配置NProgress
NProgress.configure({ showSpinner: false })

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/articles',
        name: 'Articles',
        component: () => import('@/views/Articles.vue'),
        meta: { title: '文章列表' }
      },
      {
        path: '/search',
        name: 'Search',
        component: () => import('@/views/Search.vue'),
        meta: { title: '搜索' }
      },
      {
        path: '/article/:slug',
        name: 'ArticleDetail',
        component: () => import('@/views/ArticleDetail.vue'),
        meta: { title: '文章详情' }
      },
      // 新的模块页面路由
      {
        path: '/tech-share',
        name: 'TechShare',
        component: () => import('@/views/TechShare.vue'),
        meta: { title: '技术分享' }
      },
      {
        path: '/life-essays',
        name: 'LifeEssays',
        component: () => import('@/views/LifeEssays.vue'),
        meta: { title: '生活随笔' }
      },
      {
        path: '/life-essays/:slug',
        name: 'LifeEssaysDetail',
        component: () => import('@/views/LifeEssaysDetail.vue'),
        meta: { title: '生活随笔详情' }
      },
      {
        path: '/study-notes',
        name: 'StudyNotes',
        component: () => import('@/views/StudyNotes.vue'),
        meta: { title: '学习笔记' }
      },
      {
        path: '/study-notes/:slug',
        name: 'StudyNotesDetail',
        component: () => import('@/views/StudyNotesDetail.vue'),
        meta: { title: '学习笔记详情' }
      },
      {
        path: '/project-practice',
        name: 'ProjectPractice',
        component: () => import('@/views/ProjectPractice.vue'),
        meta: { title: '项目实践' }
      },
      {
        path: '/project-practice/:slug',
        name: 'ProjectPracticeDetail',
        component: () => import('@/views/ProjectPracticeDetail.vue'),
        meta: { title: '项目实践详情' }
      },
      {
        path: '/news-info',
        name: 'NewsInfo',
        component: () => import('@/views/NewsInfo.vue'),
        meta: { title: '新闻资讯' }
      },
      {
        path: '/competition-events',
        name: 'CompetitionEvents',
        component: () => import('@/views/CompetitionEvents.vue'),
        meta: { title: '竞赛活动' }
      },
      {
        path: '/news-info/:slug',
        name: 'NewsInfoDetail',
        component: () => import('@/views/NewsInfoDetail.vue'),
        meta: { title: '新闻资讯详情' }
      },
      {
        path: '/tech-share/:slug',
        name: 'TechShareDetail',
        component: () => import('@/views/TechShareDetail.vue'),
        meta: { title: '技术分享详情' }
      },
      {
        path: '/competition-events/:slug',
        name: 'CompetitionEventsDetail',
        component: () => import('@/views/CompetitionEventsDetail.vue'),
        meta: { title: '竞赛活动详情' }
      },
      {
        path: '/tag/:slug',
        name: 'Tag',
        component: () => import('@/views/Tag.vue'),
        meta: { title: '标签' }
      },
      {
        path: '/about',
        name: 'About',
        component: () => import('@/views/About.vue'),
        meta: { title: '关于' }
      },
      {
        path: '/contact',
        name: 'Contact',
        component: () => import('@/views/Contact.vue'),
        meta: { title: '联系我们' }
      },
      {
        path: '/privacy',
        name: 'Privacy',
        component: () => import('@/views/Privacy.vue'),
        meta: { title: '隐私政策' }
      },
      {
        path: '/terms',
        name: 'Terms',
        component: () => import('@/views/Terms.vue'),
        meta: { title: '使用条款' }
      }
    ]
  },
  {
    path: '/auth',
    name: 'Auth',
    component: () => import('@/layouts/AuthLayout.vue'),
    redirect: { name: 'Login' },
    children: [
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录', guest: true }
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '注册', guest: true }
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理后台' }
      },
      {
        path: 'articles',
        name: 'AdminArticles',
        component: () => import('@/views/admin/Articles.vue'),
        meta: { title: '文章管理' }
      },
      {
        path: 'articles/create',
        name: 'CreateArticle',
        component: () => import('@/views/admin/ArticleForm.vue'),
        meta: { title: '创建文章' }
      },
      {
        path: 'articles/:id/edit',
        name: 'EditArticle',
        component: () => import('@/views/admin/ArticleForm.vue'),
        meta: { title: '编辑文章' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/Categories.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'tags',
        name: 'AdminTags',
        component: () => import('@/views/admin/Tags.vue'),
        meta: { title: '标签管理' }
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('@/views/admin/Comments.vue'),
        meta: { title: '评论管理' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '数据统计' }
      },
      {
        path: 'token-status',
        name: 'TokenStatus',
        component: () => import('@/views/admin/TokenStatus.vue'),
        meta: { title: 'Token状态监控' }
      },
      {
        path: 'account-settings',
        name: 'AccountSettings',
        component: () => import('@/views/admin/AccountSettings.vue'),
        meta: { title: '账户设置' }
      },
      {
        path: 'security-center',
        name: 'SecurityCenter',
        component: () => import('@/views/admin/SecurityCenter.vue'),
        meta: { title: '安全中心' }
      },
      {
        path: 'help-center',
        name: 'HelpCenter',
        component: () => import('@/views/admin/HelpCenter.vue'),
        meta: { title: '帮助中心' }
      },
      {
        path: 'notifications',
        name: 'AdminNotifications',
        component: () => import('@/views/admin/NotificationsView.vue'),
        meta: { title: '通知中心' }
      },
      {
        path: 'files',
        name: 'AdminFiles',
        component: () => import('@/views/admin/FileManager.vue'),
        meta: { title: '文件管理' }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('@/views/admin/AnnouncementManager.vue'),
        meta: { title: '公告管理' }
      }
    ]
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 个人博客`
  }
  
  // 如果用户状态未初始化且有token，先初始化用户状态
  if (!userStore.user && userStore.token) {
    try {
      await userStore.initUser()
    } catch (error) {
      console.warn('路由守卫中用户状态初始化失败:', error)
      // 如果初始化失败，清除无效token，但不立即跳转
      userStore.logout()
      
      // 如果是访问需要认证的页面，才跳转到登录页
      if (to.meta.requiresAuth || to.meta.requiresAdmin) {
        next('/auth/login')
        return
      }
    }
  }
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/auth/login')
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    // 如果用户已登录但不是管理员，显示友好提示
    if (userStore.isLoggedIn) {
      ElMessage.warning('您没有访问管理后台的权限')
    }
    next('/')
    return
  }
  
  // 已登录用户访问登录/注册页面时重定向到首页
  if (to.meta.guest && userStore.isLoggedIn) {
    next('/')
    return
  }
  
  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router