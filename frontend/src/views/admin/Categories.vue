<template>
  <div class="admin-categories">
    <div class="page-header">
      <h1>分类管理</h1>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        新建分类
      </el-button>
    </div>
    
    <!-- 分类列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="categories"
        style="width: 100%"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="name" label="分类名称" min-width="200">
          <template #default="{ row }">
            <div class="category-name">
              <el-icon v-if="row.icon" :size="16" style="margin-right: 8px">
                <component :is="row.icon" />
              </el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="slug" label="别名" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="handleAddChild(row)"
            >
              添加子分类
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
    </div>
    
    <!-- 分类表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类别名" prop="slug">
          <el-input v-model="form.slug" placeholder="请输入分类别名（用于URL）" />
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="categoryTree"
            :props="{ label: 'name', value: 'id' }"
            placeholder="请选择父分类（可选）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <div class="icon-selector">
            <el-input v-model="form.icon" placeholder="请输入图标名称" style="margin-bottom: 10px;" />
            <div class="icon-preview" v-if="form.icon">
              <el-icon :size="20" style="margin-right: 8px;">
                <component :is="form.icon" />
              </el-icon>
              <span>{{ form.icon }}</span>
            </div>
            <div class="icon-suggestions">
              <div class="suggestion-title">常用图标：</div>
              <div class="icon-grid">
                <div 
                  v-for="iconName in commonIcons" 
                  :key="iconName" 
                  class="icon-item" 
                  :class="{ active: form.icon === iconName }"
                  @click="form.icon = iconName"
                >
                  <el-icon :size="16">
                    <component :is="iconName" />
                  </el-icon>
                  <span>{{ iconName }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
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
import { 
  Plus, Monitor, Edit, Notebook, Folder, Document, Trophy,
  Star, Tools, Setting, User, Bell,
  ChatDotRound, Picture, VideoCamera, Link, Search,
  Calendar, Clock, Location, Phone, Message
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { showDeleteConfirm, showWarningConfirm } from '@/utils/positionedConfirm'

const loading = ref(false)
const categories = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  name: '',
  slug: '',
  parentId: null,
  icon: '',
  description: '',
  sort: 0,
  status: 1
})

// 常用图标列表
const commonIcons = ref([
  'Monitor', 'Edit', 'Notebook', 'Folder', 'Document', 'Trophy',
  'Star', 'Tools', 'Setting', 'User', 'Bell',
  'ChatDotRound', 'Picture', 'VideoCamera', 'Link', 'Search',
  'Calendar', 'Clock', 'Location', 'Phone', 'Message'
])

const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '分类名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  slug: [
    { required: true, message: '请输入分类别名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9-_]+$/, message: '别名只能包含字母、数字、横线和下划线', trigger: 'blur' }
  ]
}

// 对话框标题
const dialogTitle = computed(() => {
  return form.id ? '编辑分类' : '新建分类'
})

// 分类树形数据（用于父分类选择）
const categoryTree = computed(() => {
  const buildTree = (items, parentId = null) => {
    return items
      .filter(item => item.parentId === parentId && item.id !== form.id)
      .map(item => ({
        ...item,
        children: buildTree(items, item.id)
      }))
  }
  return buildTree(categories.value)
})

// 格式化时间
const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

// 状态变化
const handleStatusChange = async (row) => {
  try {
    // 这里应该调用实际的API
    console.log('更新分类状态:', row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 新建分类
const handleCreate = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    name: row.name,
    slug: row.slug,
    parentId: row.parentId,
    icon: row.icon,
    description: row.description,
    sort: row.sort,
    status: row.status
  })
  dialogVisible.value = true
}

// 添加子分类
const handleAddChild = (row) => {
  resetForm()
  form.parentId = row.id
  dialogVisible.value = true
}

// 删除分类
const handleDelete = async (row, event) => {
  if (row.articleCount > 0) {
    ElMessage.warning('该分类下还有文章，无法删除')
    return
  }
  
  try {
    await showDeleteConfirm(row.name, event)
    
    // 这里应该调用实际的API
    console.log('删除分类:', row.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    // 这里应该调用实际的API
    if (form.id) {
      console.log('更新分类:', form)
      ElMessage.success('更新成功')
    } else {
      console.log('创建分类:', form)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    loadCategories()
  } catch (error) {
    console.error('提交失败:', error)
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
    parentId: null,
    icon: '',
    description: '',
    sort: 0,
    status: 1
  })
}

// 加载分类列表
const loadCategories = async () => {
  loading.value = true
  try {
    // 这里应该调用实际的API
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const flatCategories = [
      {
        id: 1,
        name: '技术分享',
        slug: 'tech-sharing',
        parentId: null,
        icon: 'Monitor',
        description: '技术分享和经验交流',
        articleCount: 15,
        sort: 1,
        status: 1,
        createdAt: '2025-08-10T10:00:00Z'
      },
      {
        id: 2,
        name: '生活随笔',
        slug: 'life-notes',
        parentId: null,
        icon: 'Edit',
        description: '生活感悟和随笔记录',
        articleCount: 8,
        sort: 2,
        status: 1,
        createdAt: '2025-08-10T10:00:00Z'
      },
      {
        id: 3,
        name: '学习笔记',
        slug: 'study-notes',
        parentId: null,
        icon: 'Notebook',
        description: '学习过程中的笔记整理',
        articleCount: 12,
        sort: 3,
        status: 1,
        createdAt: '2025-08-10T10:00:00Z'
      },
      {
        id: 4,
        name: '项目实战',
        slug: 'project-practice',
        parentId: null,
        icon: 'Folder',
        description: '项目开发实战经验',
        articleCount: 9,
        sort: 4,
        status: 1,
        createdAt: '2025-08-10T10:00:00Z'
      },
      {
        id: 5,
        name: '新闻资讯',
        slug: 'news',
        parentId: null,
        icon: 'Document',
        description: '行业新闻和技术资讯',
        articleCount: 6,
        sort: 5,
        status: 1,
        createdAt: '2025-08-10T10:00:00Z'
      },
      {
        id: 6,
        name: '竞赛活动',
        slug: 'competitions',
        parentId: null,
        icon: 'Trophy',
        description: '编程竞赛和技术活动',
        articleCount: 4,
        sort: 6,
        status: 1,
        createdAt: '2025-08-10T10:00:00Z'
      }
    ]
    
    // 构建树形结构
    const buildTree = (items, parentId = null) => {
      return items
        .filter(item => item.parentId === parentId)
        .map(item => ({
          ...item,
          children: buildTree(items, item.id)
        }))
    }
    
    categories.value = buildTree(flatCategories)
  } catch (error) {
    console.error('加载分类列表失败:', error)
    ElMessage.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style lang="scss" scoped>
.admin-categories {
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
}

.icon-selector {
  .icon-preview {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    background: var(--el-fill-color-light);
    border-radius: 6px;
    margin-bottom: 10px;
    
    span {
      color: var(--el-text-color-regular);
      font-size: 14px;
    }
  }
  
  .icon-suggestions {
    .suggestion-title {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-bottom: 8px;
    }
    
    .icon-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
      gap: 8px;
      max-height: 200px;
      overflow-y: auto;
      
      .icon-item {
        display: flex;
        align-items: center;
        padding: 8px 10px;
        border: 1px solid var(--el-border-color-light);
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.2s;
        background: var(--el-bg-color);
        
        &:hover {
          border-color: var(--el-color-primary);
          background: var(--el-color-primary-light-9);
        }
        
        &.active {
          border-color: var(--el-color-primary);
          background: var(--el-color-primary-light-8);
          color: var(--el-color-primary);
        }
        
        .el-icon {
          margin-right: 6px;
          flex-shrink: 0;
        }
        
        span {
          font-size: 12px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }
}

.table-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.category-name {
  display: flex;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}

// 响应式设计
@media (max-width: 768px) {
  .admin-categories {
    min-height: calc(100vh - 120px);
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .table-section {
    padding: 15px;
    overflow-x: auto;
  }
}
</style>