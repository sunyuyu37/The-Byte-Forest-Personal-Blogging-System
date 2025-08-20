<template>
  <div class="notifications-view">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>通知中心</h1>
          <p>管理您的所有通知消息</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="markAllAsRead" :disabled="unreadCount === 0">
            <el-icon><Check /></el-icon>
            全部标记已读
          </el-button>
          <el-button @click="(event) => clearAllNotifications(event)">
            <el-icon><Delete /></el-icon>
            清空通知
          </el-button>
        </div>
      </div>
    </div>

    <div class="notifications-content">
      <div class="filter-section">
        <div class="filter-tabs">
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'all' }"
            @click="activeTab = 'all'"
          >
            全部通知 ({{ notifications.length }})
          </div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'unread' }"
            @click="activeTab = 'unread'"
          >
            未读通知 ({{ unreadCount }})
          </div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'comment' }"
            @click="activeTab = 'comment'"
          >
            评论通知
          </div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'system' }"
            @click="activeTab = 'system'"
          >
            系统通知
          </div>
        </div>
        
        <div class="filter-actions">
          <el-input
            v-model="searchQuery"
            placeholder="搜索通知..."
            prefix-icon="Search"
            clearable
            style="width: 300px"
          />
        </div>
      </div>

      <div class="notifications-list" v-if="filteredNotifications.length > 0" v-loading="loading">
        <div 
          class="notification-card" 
          v-for="notification in paginatedNotifications" 
          :key="notification.id"
          :class="{ unread: !notification.isRead }"
          @click="handleNotificationAction(notification)"
        >
          <div class="notification-avatar" :class="notification.type">
            <el-icon>
              <component :is="getNotificationIcon(notification.type)" />
            </el-icon>
          </div>
          
          <div class="notification-content">
            <div class="notification-header">
              <h3 class="notification-title">{{ notification.title }}</h3>
              <div class="notification-meta">
                <span class="notification-time">{{ notification.timeAgo || notification.createdAt }}</span>
                <el-tag :type="getNotificationTagType(notification.type)" size="small">
                  {{ getNotificationTypeText(notification.type) }}
                </el-tag>
              </div>
            </div>
            
            <p class="notification-description">{{ notification.content }}</p>
            
            <div class="notification-actions">
              <el-button 
                v-if="!notification.isRead" 
                size="small" 
                type="primary" 
                text
                @click="markAsRead(notification)"
              >
                标记已读
              </el-button>
              <el-button 
                size="small" 
                text
                @click="(event) => deleteNotification(notification, event)"
              >
                删除
              </el-button>
              <el-button 
                v-if="notification.actionUrl" 
                size="small" 
                type="primary"
                @click="handleNotificationAction(notification)"
              >
                查看详情
              </el-button>
            </div>
          </div>
          
          <div class="notification-status" v-if="!notification.isRead">
            <div class="unread-indicator"></div>
          </div>
        </div>
      </div>
      
      <div class="empty-state" v-else>
        <div class="empty-icon">
          <el-icon><Bell /></el-icon>
        </div>
        <h3>暂无通知</h3>
        <p>{{ getEmptyStateText() }}</p>
      </div>
      
      <div class="pagination-section" v-if="totalCount > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="totalCount"
          layout="prev, pager, next, jumper, total"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Bell,
  Check,
  Delete,
  Search,
  ChatDotRound,
  Setting,
  User,
  CircleCheck,
  Warning,
  InfoFilled
} from '@element-plus/icons-vue'
import { showDeleteConfirm, showWarningConfirm } from '@/utils/positionedConfirm'
import { 
  getNotifications, 
  markAsRead as markAsReadApi, 
  markAllAsRead as markAllAsReadApi, 
  deleteNotification as deleteNotificationApi, 
  clearAllNotifications as clearAllNotificationsApi 
} from '@/api/notification'

const router = useRouter()

// 响应式数据
const activeTab = ref('all')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 通知数据
const notifications = ref([])
const totalCount = ref(0) // 总数据量

// 计算属性
const unreadCount = computed(() => 
  notifications.value.filter(n => !n.isRead).length
)

// 直接使用后端返回的数据，不需要前端分页
const filteredNotifications = computed(() => {
  return notifications.value
})

const paginatedNotifications = computed(() => {
  return notifications.value
})

// 方法
const getNotificationIcon = (type) => {
  const iconMap = {
    comment: ChatDotRound,
    system: Setting,
    user: User,
    like: CircleCheck,
    success: CircleCheck,
    warning: Warning,
    info: InfoFilled
  }
  return iconMap[type] || Bell
}

const getNotificationTagType = (type) => {
  const typeMap = {
    comment: 'primary',
    system: 'info',
    user: 'success',
    like: 'success',
    success: 'success',
    warning: 'warning',
    info: 'info'
  }
  return typeMap[type] || 'info'
}

const getNotificationTypeText = (type) => {
  const textMap = {
    comment: '评论',
    system: '系统',
    user: '用户',
    like: '点赞',
    success: '成功',
    warning: '警告',
    info: '信息'
  }
  return textMap[type] || '通知'
}

const getEmptyStateText = () => {
  if (activeTab.value === 'unread') {
    return '所有通知都已阅读'
  } else if (activeTab.value !== 'all') {
    return `暂无${getNotificationTypeText(activeTab.value)}通知`
  } else if (searchQuery.value) {
    return '没有找到匹配的通知'
  }
  return '暂时没有任何通知'
}

const markAsRead = async (notification) => {
  try {
    await markAsReadApi(notification.id)
    notification.isRead = true
    ElMessage.success('已标记为已读')
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

const markAllAsRead = async () => {
  try {
    await markAllAsReadApi()
    notifications.value.forEach(n => n.isRead = true)
    ElMessage.success('所有通知已标记为已读')
  } catch (error) {
    console.error('标记所有已读失败:', error)
    ElMessage.error('标记所有已读失败')
  }
}

const deleteNotification = async (notification, event) => {
  try {
    await showDeleteConfirm('这条通知', event)
    
    await deleteNotificationApi(notification.id)
    const index = notifications.value.findIndex(n => n.id === notification.id)
    if (index > -1) {
      notifications.value.splice(index, 1)
      ElMessage.success('通知已删除')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除通知失败:', error)
      ElMessage.error('删除通知失败')
    }
  }
}

const clearAllNotifications = async (event) => {
  try {
    await showWarningConfirm('确定要清空所有通知吗？此操作不可恢复。', '确认清空', event)
    
    await clearAllNotificationsApi()
    notifications.value = []
    ElMessage.success('所有通知已清空')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空通知失败:', error)
      ElMessage.error('清空通知失败')
    }
  }
}

const handleNotificationAction = (notification) => {
  if (notification.actionUrl) {
    router.push(notification.actionUrl)
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
}

// 加载通知数据
const loadNotifications = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value - 1, // 后端分页从0开始，前端从1开始
      size: pageSize.value
    }
    
    // 只有非 'all' 的情况下才传递 type 参数
    if (activeTab.value !== 'all') {
      params.type = activeTab.value
    }
    
    // 只有有搜索关键词时才传递 keyword 参数
    if (searchQuery.value) {
      params.keyword = searchQuery.value
    }
    
    const res = await getNotifications(params)
    // 拦截器已返回 Result 对象，这里直接从 res.data 读取分页数据
    const pageResult = res.data || {}
    notifications.value = pageResult.content || []
    totalCount.value = pageResult.total || 0
    console.log('通知数据加载成功:', notifications.value, '总数:', totalCount.value)
  } catch (error) {
    console.error('加载通知失败:', error)
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

// 监听筛选条件变化
watch([activeTab, searchQuery], () => {
  currentPage.value = 1 // 重置到第一页
  loadNotifications()
})

// 监听页码变化
watch(currentPage, () => {
  loadNotifications()
})

// 生命周期
onMounted(() => {
  loadNotifications()
})
</script>

<style lang="scss" scoped>
.notifications-view {
  padding: 24px;
  
  .page-header {
    margin-bottom: 24px;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      
      .title-section {
        h1 {
          margin: 0 0 8px 0;
          font-size: 28px;
          font-weight: 700;
          color: #1f2937;
        }
        
        p {
          margin: 0;
          font-size: 16px;
          color: #6b7280;
        }
      }
      
      .header-actions {
        display: flex;
        gap: 12px;
      }
    }
  }
  
  .notifications-content {
    background: white;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    
    .filter-section {
      padding: 20px 24px;
      border-bottom: 1px solid #f1f5f9;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .filter-tabs {
        display: flex;
        gap: 8px;
        
        .tab-item {
          padding: 8px 16px;
          border-radius: 8px;
          font-size: 14px;
          font-weight: 500;
          color: #6b7280;
          cursor: pointer;
          transition: all 0.3s ease;
          
          &:hover {
            background: #f8fafc;
            color: #374151;
          }
          
          &.active {
            background: #6366f1;
            color: white;
          }
        }
      }
    }
    
    .notifications-list {
      .notification-card {
        display: flex;
        align-items: flex-start;
        gap: 16px;
        padding: 20px 24px;
        border-bottom: 1px solid #f8fafc;
        transition: all 0.3s ease;
        position: relative;
        
        &:hover {
          background: #f8fafc;
        }
        
        &.unread {
          background: rgba(99, 102, 241, 0.02);
          border-left: 4px solid #6366f1;
          
          .notification-title {
            font-weight: 600;
          }
        }
        
        .notification-avatar {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 20px;
          flex-shrink: 0;
          
          &.comment { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }
          &.system { background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%); }
          &.user { background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%); }
          &.success { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }
          &.warning { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); }
          &.info { background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); }
        }
        
        .notification-content {
          flex: 1;
          
          .notification-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 8px;
            
            .notification-title {
              margin: 0;
              font-size: 16px;
              color: #1f2937;
              line-height: 1.4;
            }
            
            .notification-meta {
              display: flex;
              align-items: center;
              gap: 12px;
              flex-shrink: 0;
              
              .notification-time {
                font-size: 13px;
                color: #9ca3af;
              }
            }
          }
          
          .notification-description {
            margin: 0 0 12px 0;
            font-size: 14px;
            color: #6b7280;
            line-height: 1.5;
          }
          
          .notification-actions {
            display: flex;
            gap: 8px;
          }
        }
        
        .notification-status {
          .unread-indicator {
            width: 8px;
            height: 8px;
            background: #6366f1;
            border-radius: 50%;
            margin-top: 8px;
          }
        }
      }
    }
    
    .empty-state {
      padding: 60px 24px;
      text-align: center;
      
      .empty-icon {
        font-size: 64px;
        color: #d1d5db;
        margin-bottom: 16px;
      }
      
      h3 {
        margin: 0 0 8px 0;
        font-size: 18px;
        color: #374151;
      }
      
      p {
        margin: 0;
        font-size: 14px;
        color: #9ca3af;
      }
    }
    
    .pagination-section {
      padding: 20px 24px;
      border-top: 1px solid #f1f5f9;
      display: flex;
      justify-content: center;
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .notifications-view {
    padding: 16px;
    
    .page-header .header-content {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
    }
    
    .filter-section {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
      
      .filter-tabs {
        flex-wrap: wrap;
      }
    }
    
    .notification-card {
      .notification-header {
        flex-direction: column;
        gap: 8px;
        align-items: stretch;
      }
    }
  }
}
</style>