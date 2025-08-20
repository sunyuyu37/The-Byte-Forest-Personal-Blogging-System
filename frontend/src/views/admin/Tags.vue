<template>
  <div class="admin-tags">
    <div class="page-header">
      <h1>标签管理</h1>
      <div class="header-actions">
        <el-button type="warning" @click="(event) => handleRecalculateCounts(event)" :loading="recalculating">
          <el-icon><Refresh /></el-icon>
          重新计算文章数
        </el-button>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建标签
        </el-button>
      </div>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="标签名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入标签名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 标签列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="tags"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="标签名称" min-width="150">
          <template #default="{ row }">
            <el-tag :color="row.color" style="color: white; border: none;">
              {{ row.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="slug" label="别名" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="color" label="颜色" width="100">
          <template #default="{ row }">
            <div class="color-preview" :style="{ backgroundColor: row.color }"></div>
          </template>
        </el-table-column>
        <el-table-column prop="articleCount" label="文章数" width="100" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="(event) => handleDelete(row, event)"
              :disabled="row.articleCount > 0"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div v-if="selectedTags.length > 0" class="batch-actions">
        <span>已选择 {{ selectedTags.length }} 项</span>
        <el-button type="danger" size="small" @click="(event) => handleBatchDelete(event)">
          批量删除
        </el-button>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
    
    <!-- 标签表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签别名" prop="slug">
          <el-input v-model="form.slug" placeholder="请输入标签别名（用于URL）" />
        </el-form-item>
        <el-form-item label="标签颜色" prop="color">
          <div class="color-input">
            <el-color-picker v-model="form.color" />
            <el-input
              v-model="form.color"
              placeholder="请选择或输入颜色值"
              style="margin-left: 10px"
            />
          </div>
        </el-form-item>
        <el-form-item label="标签描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入标签描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="form.sort"
            :min="0"
            :max="999"
            placeholder="数字越小越靠前"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { getToken } from '@/utils/auth'
import { showDeleteConfirm, showWarningConfirm } from '@/utils/positionedConfirm'

const loading = ref(false)
const tags = ref([])
const selectedTags = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)
const recalculating = ref(false)
const formRef = ref()

const searchForm = reactive({
  name: ''
})

const form = reactive({
  id: null,
  name: '',
  slug: '',
  color: '#409eff',
  description: '',
  sort: 0,
  status: 1
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const rules = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { min: 1, max: 20, message: '标签名称长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  slug: [
    { required: true, message: '请输入标签别名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9-_]+$/, message: '别名只能包含字母、数字、横线和下划线', trigger: 'blur' }
  ],
  color: [
    { required: true, message: '请选择标签颜色', trigger: 'blur' }
  ]
}

// 对话框标题
const dialogTitle = computed(() => {
  return form.id ? '编辑标签' : '新建标签'
})

// 格式化时间
const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedTags.value = selection
}

// 状态变化
const handleStatusChange = async (row) => {
  try {
    // 这里应该调用实际的API
    console.log('更新标签状态:', row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadTags()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  pagination.page = 1
  loadTags()
}

// 新建标签
const handleCreate = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑标签
const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    name: row.name,
    slug: row.slug,
    color: row.color,
    description: row.description,
    sort: row.sort,
    status: row.status
  })
  dialogVisible.value = true
}

// 删除标签
const handleDelete = async (row, event) => {
  if (row.articleCount > 0) {
    ElMessage.warning('该标签下还有文章，无法删除')
    return
  }
  
  try {
    await showDeleteConfirm(row.name, event)
    
    const response = await fetch(`/api/admin/tags/${row.id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      }
    })
    
    const result = await response.json()
    
    if (result.code === 200) {
      ElMessage.success(result.message || '删除成功')
      loadTags()
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async (event) => {
  const canDelete = selectedTags.value.filter(tag => tag.articleCount === 0)
  if (canDelete.length === 0) {
    ElMessage.warning('选中的标签都有关联文章，无法删除')
    return
  }
  
  try {
    await showDeleteConfirm(`确定要删除选中的 ${canDelete.length} 个标签吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }, event)
    
    const response = await fetch('/api/admin/tags/batch', {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        ids: canDelete.map(item => item.id)
      })
    })
    
    const result = await response.json()
    
    if (result.code === 200) {
      ElMessage.success(result.message || '批量删除成功')
      loadTags()
      selectedTags.value = []
    } else {
      ElMessage.error(result.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 重新计算标签文章数
const handleRecalculateCounts = async (event) => {
  try {
    await showWarningConfirm(
      '此操作将重新计算所有标签的文章数量（只统计已发布的文章），确定要继续吗？',
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      },
      event
    )
    
    recalculating.value = true
    
    const response = await fetch('/api/admin/tags/recalculate-counts', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      }
    })
    
    const result = await response.json()
    
    if (result.code === 200) {
      ElMessage.success(result.message || '重新计算完成')
      // 重新加载标签列表以显示更新后的文章数
      loadTags()
    } else {
      ElMessage.error(result.message || '重新计算失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重新计算失败:', error)
      ElMessage.error('重新计算失败')
    }
  } finally {
    recalculating.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const tagData = {
      name: form.name,
      description: form.description,
      color: form.color
    }
    
    let response
    if (form.id) {
      // 更新标签
      response = await fetch(`/api/admin/tags/${form.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify(tagData)
      })
    } else {
      // 创建标签
      response = await fetch('/api/admin/tags', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify(tagData)
      })
    }
    
    const result = await response.json()
    
    if (result.code === 200) {
      ElMessage.success(result.message || (form.id ? '更新成功' : '创建成功'))
      dialogVisible.value = false
      loadTags()
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    slug: '',
    color: '#409eff',
    description: '',
    sort: 0,
    status: 1
  })
}

// 页面大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadTags()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page
  loadTags()
}

// 加载标签列表
const loadTags = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      sortBy: 'createdAt',
      sortDir: 'desc'
    }
    
    if (searchForm.name) {
      params.keyword = searchForm.name
    }
    
    const response = await fetch('/api/admin/tags?' + new URLSearchParams(params), {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      }
    })
    
    const result = await response.json()
    
    if (result.code === 200) {
      tags.value = result.data.content.map(tag => ({
        ...tag,
        status: tag.deleted ? 0 : 1 // 将deleted字段转换为status字段
      }))
      pagination.total = result.data.totalElements
    } else {
      ElMessage.error(result.message || '加载标签列表失败')
    }
  } catch (error) {
    console.error('加载标签列表失败:', error)
    ElMessage.error('加载标签列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTags()
})
</script>

<style lang="scss" scoped>
.admin-tags {
  min-height: calc(100vh - 140px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h1 {
    font-size: 1.5rem;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin: 0;
  }
  
  .header-actions {
    display: flex;
    gap: 10px;
  }
}

.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.table-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.color-preview {
  width: 30px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid var(--el-border-color);
}

.color-input {
  display: flex;
  align-items: center;
}

.batch-actions {
  margin: 20px 0;
  padding: 15px;
  background: var(--el-bg-color);
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  
  span {
    color: var(--el-text-color-regular);
    margin-right: 10px;
  }
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  text-align: right;
}

// 响应式设计
@media (max-width: 768px) {
  .admin-tags {
    min-height: calc(100vh - 120px);
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .search-section {
    padding: 15px;
    
    :deep(.el-form--inline) {
      .el-form-item {
        display: block;
        margin-bottom: 15px;
        margin-right: 0;
      }
    }
  }
  
  .table-section {
    padding: 15px;
    overflow-x: auto;
  }
}
</style>