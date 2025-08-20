<template>
  <el-dialog
    v-model="visible"
    title="系统公告"
    width="600px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="false"
    class="announcement-modal"
    @close="handleClose"
  >
    <div class="announcement-content">
      <!-- 公告列表 -->
      <div v-if="announcements.length > 1" class="announcement-tabs">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane
            v-for="(announcement, index) in announcements"
            :key="announcement.id"
            :label="announcement.title"
            :name="index.toString()"
          >
            <div class="announcement-item">
              <div class="announcement-header">
                <h3 class="announcement-title">{{ announcement.title }}</h3>
                <div class="announcement-meta">
                  <el-tag
                    :type="getTypeColor(announcement.type)"
                    size="small"
                  >
                    {{ getTypeText(announcement.type) }}
                  </el-tag>
                  <el-tag
                    v-if="announcement.priority === 'HIGH'"
                    type="danger"
                    size="small"
                  >
                    重要
                  </el-tag>
                  <span class="announcement-time">
                    {{ formatTime(announcement.createdAt) }}
                  </span>
                </div>
              </div>
              <div class="announcement-body">
                <div v-html="announcement.content" class="content-html"></div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <!-- 单个公告 -->
      <div v-else-if="announcements.length === 1" class="single-announcement">
        <div class="announcement-item">
          <div class="announcement-header">
            <h3 class="announcement-title">{{ announcements[0].title }}</h3>
            <div class="announcement-meta">
              <el-tag
                :type="getTypeColor(announcements[0].type)"
                size="small"
              >
                {{ getTypeText(announcements[0].type) }}
              </el-tag>
              <el-tag
                v-if="announcements[0].priority === 'HIGH'"
                type="danger"
                size="small"
              >
                重要
              </el-tag>
              <span class="announcement-time">
                {{ formatTime(announcements[0].createdAt) }}
              </span>
            </div>
          </div>
          <div class="announcement-body">
            <div v-html="announcements[0].content" class="content-html"></div>
          </div>
        </div>
      </div>
      
      <!-- 无公告 -->
      <div v-else class="no-announcements">
        <el-empty description="暂无新公告" />
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <div class="footer-left">
          <el-checkbox
            v-model="dontShowToday"
            label="今日不再显示"
            size="small"
          />
        </div>
        <div class="footer-right">
          <el-button @click="handleMarkAllRead" type="primary" size="default">
            全部已读
          </el-button>
          <el-button @click="handleClose" size="default">
            关闭
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import announcementApi from '@/api/announcement'
import { formatDistanceToNow } from 'date-fns'
import { zhCN } from 'date-fns/locale'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  announcements: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'close', 'allRead'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const activeTab = ref('0')
const dontShowToday = ref(false)
const readAnnouncements = ref(new Set())

// 监听公告变化，重置状态
watch(() => props.announcements, () => {
  activeTab.value = '0'
  readAnnouncements.value.clear()
}, { immediate: true })

// 获取公告类型颜色
const getTypeColor = (type) => {
  const colorMap = {
    'SYSTEM': 'info',
    'MAINTENANCE': 'warning',
    'FEATURE': 'success',
    'SECURITY': 'danger',
    'EVENT': 'primary'
  }
  return colorMap[type] || 'info'
}

// 获取公告类型文本
const getTypeText = (type) => {
  const textMap = {
    'SYSTEM': '系统',
    'MAINTENANCE': '维护',
    'FEATURE': '功能',
    'SECURITY': '安全',
    'EVENT': '活动'
  }
  return textMap[type] || '系统'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  try {
    return formatDistanceToNow(new Date(time), {
      addSuffix: true,
      locale: zhCN
    })
  } catch (error) {
    return time
  }
}

// 处理标签页切换
const handleTabChange = (tabName) => {
  const index = parseInt(tabName)
  const announcement = props.announcements[index]
  if (announcement && !readAnnouncements.value.has(announcement.id)) {
    markAsRead(announcement.id)
  }
}

// 标记单个公告为已读
const markAsRead = async (announcementId) => {
  try {
    await announcementApi.markAsRead(announcementId)
    readAnnouncements.value.add(announcementId)
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 标记所有公告为已读
const handleMarkAllRead = async () => {
  try {
    const unreadIds = props.announcements
      .filter(a => !readAnnouncements.value.has(a.id))
      .map(a => a.id)
    
    if (unreadIds.length > 0) {
      await announcementApi.markBatchAsRead(unreadIds)
      unreadIds.forEach(id => readAnnouncements.value.add(id))
    }
    
    // 保存今日不再显示的设置
    if (dontShowToday.value) {
      const today = new Date().toDateString()
      localStorage.setItem('announcement_hide_date', today)
    }
    
    ElMessage.success('已标记为已读')
    emit('allRead')
    handleClose()
  } catch (error) {
    console.error('批量标记已读失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 关闭弹窗
const handleClose = () => {
  // 自动标记当前查看的公告为已读
  const currentIndex = parseInt(activeTab.value)
  const currentAnnouncement = props.announcements[currentIndex]
  if (currentAnnouncement && !readAnnouncements.value.has(currentAnnouncement.id)) {
    markAsRead(currentAnnouncement.id)
  }
  
  emit('close')
  visible.value = false
}

// 检查是否应该显示公告弹窗
const shouldShowAnnouncements = () => {
  const hideDate = localStorage.getItem('announcement_hide_date')
  const today = new Date().toDateString()
  return hideDate !== today
}

// 暴露方法给父组件
defineExpose({
  shouldShowAnnouncements
})
</script>

<style scoped>
.announcement-modal {
  .announcement-content {
    max-height: 500px;
    overflow-y: auto;
  }
  
  .announcement-item {
    margin-bottom: 20px;
  }
  
  .announcement-header {
    margin-bottom: 15px;
    
    .announcement-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 10px 0;
      line-height: 1.4;
    }
    
    .announcement-meta {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;
      
      .announcement-time {
        font-size: 12px;
        color: #909399;
        margin-left: auto;
      }
    }
  }
  
  .announcement-body {
    .content-html {
      line-height: 1.6;
      color: #606266;
      font-size: 14px;
      
      :deep(p) {
        margin: 0 0 10px 0;
        
        &:last-child {
          margin-bottom: 0;
        }
      }
      
      :deep(ul), :deep(ol) {
        margin: 10px 0;
        padding-left: 20px;
      }
      
      :deep(li) {
        margin: 5px 0;
      }
      
      :deep(strong) {
        font-weight: 600;
        color: #303133;
      }
      
      :deep(a) {
        color: #409eff;
        text-decoration: none;
        
        &:hover {
          text-decoration: underline;
        }
      }
      
      :deep(code) {
        background: #f5f7fa;
        padding: 2px 4px;
        border-radius: 3px;
        font-family: 'Courier New', monospace;
        font-size: 13px;
      }
      
      :deep(blockquote) {
        border-left: 4px solid #dcdfe6;
        padding-left: 15px;
        margin: 15px 0;
        color: #909399;
        font-style: italic;
      }
    }
  }
  
  .no-announcements {
    text-align: center;
    padding: 40px 20px;
  }
  
  .dialog-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .footer-left {
      flex: 1;
    }
    
    .footer-right {
      display: flex;
      gap: 10px;
    }
  }
}

/* 标签页样式优化 */
:deep(.el-tabs__header) {
  margin-bottom: 20px;
}

:deep(.el-tabs__nav-wrap) {
  padding: 0 10px;
}

:deep(.el-tabs__item) {
  font-size: 14px;
  padding: 0 15px;
}

:deep(.el-tabs__content) {
  padding: 0 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .announcement-modal {
    :deep(.el-dialog) {
      width: 95% !important;
      margin: 5vh auto;
    }
    
    .announcement-header {
      .announcement-title {
        font-size: 16px;
      }
      
      .announcement-meta {
        .announcement-time {
          margin-left: 0;
          margin-top: 5px;
          width: 100%;
        }
      }
    }
    
    .dialog-footer {
      flex-direction: column;
      gap: 15px;
      
      .footer-left {
        order: 2;
      }
      
      .footer-right {
        order: 1;
        width: 100%;
        justify-content: center;
      }
    }
  }
}
</style>