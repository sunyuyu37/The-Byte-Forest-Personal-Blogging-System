<template>
  <div class="announcement-manager">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">
            <el-icon><Bell /></el-icon>
            公告管理
          </h1>
          <p class="page-description">管理系统公告，向用户发布重要信息</p>
        </div>
        <div class="header-right">
          <el-button type="primary" @click="showCreateDialog" :icon="Plus">
            发布公告
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-card shadow="never" class="filter-card">
        <div class="filter-content">
          <div class="filter-left">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索公告标题或内容"
              :prefix-icon="Search"
              clearable
              @input="handleSearch"
              style="width: 300px;"
            />
            <el-select
              v-model="filterType"
              placeholder="公告类型"
              clearable
              style="width: 150px; margin-left: 12px;"
              @change="handleFilter"
            >
              <el-option label="系统公告" value="SYSTEM" />
              <el-option label="功能更新" value="FEATURE" />
              <el-option label="维护通知" value="MAINTENANCE" />
              <el-option label="活动公告" value="ACTIVITY" />
            </el-select>
            <el-select
              v-model="filterStatus"
              placeholder="状态"
              clearable
              style="width: 120px; margin-left: 12px;"
              @change="handleFilter"
            >
              <el-option label="启用" value="true" />
              <el-option label="禁用" value="false" />
            </el-select>
          </div>
          <div class="filter-right">
            <el-button @click="resetFilters" :icon="Refresh">重置</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 公告列表 -->
    <div class="table-section">
      <el-card shadow="never" class="table-card">
        <el-table
          v-loading="loading"
          :data="announcements"
          stripe
          style="width: 100%"
          @sort-change="handleSortChange"
        >
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="{ row }">
              <div class="title-cell">
                <el-tag v-if="row.pinned" type="warning" size="small" class="pin-tag">
                  <el-icon><Star /></el-icon>
                  置顶
                </el-tag>
                <span class="title-text" :class="{ pinned: row.pinned }">{{ row.title }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="type" label="类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)" size="small">
                {{ getTypeLabel(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="priority" label="优先级" width="100">
            <template #default="{ row }">
              <el-tag :type="getPriorityTagType(row.priority)" size="small">
                {{ getPriorityLabel(row.priority) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="enabled" label="状态" width="80">
            <template #default="{ row }">
              <el-switch
                v-model="row.enabled"
                @change="toggleStatus(row)"
                :loading="row.statusLoading"
              />
            </template>
          </el-table-column>
          
          <el-table-column prop="readCount" label="已读数" width="100" align="center">
            <template #default="{ row }">
              <span class="read-count">{{ row.readCount || 0 }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="publisherName" label="发布者" width="120" />
          
          <el-table-column prop="createdAt" label="创建时间" width="180" sortable="custom">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip content="编辑" placement="top">
                  <el-button
                    type="primary"
                    size="small"
                    :icon="Edit"
                    @click="editAnnouncement(row)"
                    circle
                  />
                </el-tooltip>
                <el-tooltip :content="row.pinned ? '取消置顶' : '置顶'" placement="top">
                  <el-button
                    :type="row.pinned ? 'warning' : 'info'"
                    size="small"
                    :icon="Star"
                    @click="togglePin(row)"
                    circle
                  />
                </el-tooltip>
                <el-tooltip content="查看统计" placement="top">
                  <el-button
                    type="success"
                    size="small"
                    :icon="TrendCharts"
                    @click="viewStats(row)"
                    circle
                  />
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button
                    type="danger"
                    size="small"
                    :icon="Delete"
                    @click="deleteAnnouncement(row)"
                    circle
                  />
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 创建/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑公告' : '发布公告'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="公告标题" prop="title">
              <el-input
                v-model="form.title"
                placeholder="请输入公告标题"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="公告类型" prop="type">
              <el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
                <el-option label="系统公告" value="SYSTEM" />
                <el-option label="功能更新" value="FEATURE" />
                <el-option label="维护通知" value="MAINTENANCE" />
                <el-option label="活动公告" value="ACTIVITY" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="选择优先级" style="width: 100%">
                <el-option label="低" value="LOW" />
                <el-option label="中" value="MEDIUM" />
                <el-option label="高" value="HIGH" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设置">
              <div class="form-switches">
                <el-checkbox v-model="form.enabled">启用</el-checkbox>
                <el-checkbox v-model="form.pinned">置顶</el-checkbox>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ isEdit ? '更新' : '发布' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 统计对话框 -->
    <el-dialog
      v-model="statsDialogVisible"
      title="公告统计"
      width="600px"
    >
      <div v-if="currentStats" class="stats-content">
        <div class="stats-header">
          <h3>{{ currentStats.title }}</h3>
          <el-tag :type="getTypeTagType(currentStats.type)">{{ getTypeLabel(currentStats.type) }}</el-tag>
        </div>
        
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ currentStats.readCount || 0 }}</div>
            <div class="stat-label">已读用户</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ formatDate(currentStats.createdAt) }}</div>
            <div class="stat-label">发布时间</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ currentStats.publisherName }}</div>
            <div class="stat-label">发布者</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Bell, Plus, Search, Refresh, Edit, Delete, Star, TrendCharts
} from '@element-plus/icons-vue'
import { formatDate } from '@/utils/date'
import announcementApi from '@/api/announcement'

// 响应式数据
const loading = ref(false)
const announcements = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')
const sortField = ref('createdAt')
const sortOrder = ref('desc')

// 对话框相关
const dialogVisible = ref(false)
const statsDialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()
const currentStats = ref(null)

// 表单数据
const form = reactive({
  id: null,
  title: '',
  content: '',
  type: 'SYSTEM',
  priority: 'MEDIUM',
  enabled: true,
  pinned: false,
  startTime: '',
  endTime: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 1, max: 2000, message: '内容长度在 1 到 2000 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ]
}

// 方法
const loadAnnouncements = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sortBy: sortField.value,
      sortDir: sortOrder.value
    }
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    if (filterType.value) {
      params.type = filterType.value
    }
    if (filterStatus.value) {
      params.enabled = filterStatus.value === 'true'
    }
    
    const response = await announcementApi.getAdminAnnouncements(params)
    announcements.value = response.data.content
    total.value = response.data.totalElements
  } catch (error) {
    ElMessage.error('加载公告列表失败')
    console.error('Load announcements error:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadAnnouncements()
}

const handleFilter = () => {
  currentPage.value = 1
  loadAnnouncements()
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = ''
  filterStatus.value = ''
  currentPage.value = 1
  loadAnnouncements()
}

const handleSortChange = ({ prop, order }) => {
  sortField.value = prop || 'createdAt'
  sortOrder.value = order === 'ascending' ? 'asc' : 'desc'
  loadAnnouncements()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadAnnouncements()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadAnnouncements()
}

const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const editAnnouncement = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    title: row.title,
    content: row.content,
    type: row.type,
    priority: row.priority,
    enabled: row.enabled,
    pinned: row.pinned,
    startTime: row.startTime,
    endTime: row.endTime
  })
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    title: '',
    content: '',
    type: 'SYSTEM',
    priority: 'MEDIUM',
    enabled: true,
    pinned: false,
    startTime: '',
    endTime: ''
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (isEdit.value) {
      await announcementApi.updateAnnouncement(form.id, form)
      ElMessage.success('公告更新成功')
    } else {
      await announcementApi.createAnnouncement(form)
      ElMessage.success('公告发布成功')
    }
    
    dialogVisible.value = false
    loadAnnouncements()
  } catch (error) {
    if (error.errors) {
      // 表单验证错误
      return
    }
    ElMessage.error(isEdit.value ? '更新公告失败' : '发布公告失败')
    console.error('Submit form error:', error)
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (row) => {
  row.statusLoading = true
  try {
    await announcementApi.toggleStatus(row.id, row.enabled)
    ElMessage.success(row.enabled ? '公告已启用' : '公告已禁用')
  } catch (error) {
    row.enabled = !row.enabled // 回滚状态
    ElMessage.error('状态切换失败')
    console.error('Toggle status error:', error)
  } finally {
    row.statusLoading = false
  }
}

const togglePin = async (row) => {
  try {
    const newPinned = !row.pinned
    await announcementApi.togglePin(row.id, newPinned)
    row.pinned = newPinned
    ElMessage.success(newPinned ? '公告已置顶' : '公告已取消置顶')
  } catch (error) {
    ElMessage.error('置顶状态切换失败')
    console.error('Toggle pin error:', error)
  }
}

const viewStats = async (row) => {
  try {
    const response = await announcementApi.getReadStats(row.id)
    currentStats.value = {
      ...row,
      readCount: response.data
    }
    statsDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取统计信息失败')
    console.error('View stats error:', error)
  }
}

const deleteAnnouncement = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公告「${row.title}」吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await announcementApi.deleteAnnouncement(row.id)
    ElMessage.success('公告删除成功')
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除公告失败')
      console.error('Delete announcement error:', error)
    }
  }
}

// 工具方法
const getTypeLabel = (type) => {
  const labels = {
    SYSTEM: '系统公告',
    FEATURE: '功能更新',
    MAINTENANCE: '维护通知',
    ACTIVITY: '活动公告'
  }
  return labels[type] || type
}

const getTypeTagType = (type) => {
  const types = {
    SYSTEM: 'primary',
    FEATURE: 'success',
    MAINTENANCE: 'warning',
    ACTIVITY: 'info'
  }
  return types[type] || 'info'
}

const getPriorityLabel = (priority) => {
  const labels = {
    LOW: '低',
    MEDIUM: '中',
    HIGH: '高'
  }
  return labels[priority] || priority
}

const getPriorityTagType = (priority) => {
  const types = {
    LOW: 'info',
    MEDIUM: 'warning',
    HIGH: 'danger'
  }
  return types[priority] || 'info'
}

// 生命周期
onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.announcement-manager {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-description {
  color: #909399;
  margin: 0;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-card {
  border-radius: 12px;
}

.filter-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-left {
  display: flex;
  align-items: center;
}

.table-section {
  margin-bottom: 24px;
}

.table-card {
  border-radius: 12px;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pin-tag {
  display: flex;
  align-items: center;
  gap: 4px;
}

.title-text.pinned {
  font-weight: 600;
  color: #e6a23c;
}

.read-count {
  font-weight: 600;
  color: #409eff;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.form-switches {
  display: flex;
  gap: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.stats-content {
  padding: 16px 0;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.stats-header h3 {
  margin: 0;
  color: #303133;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .announcement-manager {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
  }
  
  .filter-content {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .filter-left {
    flex-direction: column;
    gap: 12px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>