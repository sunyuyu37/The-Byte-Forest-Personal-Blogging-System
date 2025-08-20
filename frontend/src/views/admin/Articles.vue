<template>
  <div class="admin-articles">
    <div class="page-header">
      <h1>文章管理</h1>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        新建文章
      </el-button>
    </div>
    
    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入文章标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.categoryId"
            placeholder="请选择分类"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="已发布" value="published" />
            <el-option label="草稿" value="draft" />
            <el-option label="已删除" value="deleted" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 文章列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="articles"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="article-title">
              <span>{{ row.title }}</span>
              <el-tag
                v-if="row.isTop"
                type="danger"
                size="small"
                style="margin-left: 8px"
              >
                置顶
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.category?.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120">
          <template #default="{ row }">
            {{ row.author?.nickname }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读量" width="100" />
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="commentCount" label="评论数" width="100" />
        <el-table-column prop="publishedAt" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.publishedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="isStatus(row.status, 'draft')"
              type="primary"
              size="small"
              @click="(event) => handlePublish(row, event)"
            >
              发布
            </el-button>
            <el-button
              v-else-if="isStatus(row.status, 'published')"
              type="warning"
              size="small"
              @click="(event) => handleUnpublish(row, event)"
            >
              下线
            </el-button>
            <el-button
              v-if="!isStatus(row.status, 'deleted')"
              type="danger"
              size="small"
              @click="(event) => handleDelete(row, event)"
            >
              删除
            </el-button>
            <el-tooltip
              v-else
              content="文章已被删除"
              placement="top"
            >
              <el-button
                type="info"
                size="small"
                disabled
              >
                已删除
              </el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div v-if="selectedArticles.length > 0" class="batch-actions">
        <span>已选择 {{ selectedArticles.length }} 项</span>
        <el-button 
          v-if="canBatchPublish"
          type="primary" 
          size="small" 
          @click="(event) => handleBatchPublish(event)"
        >
          批量发布 ({{ draftCount }})
        </el-button>
        <el-button 
          v-if="canBatchUnpublish"
          type="warning" 
          size="small" 
          @click="(event) => handleBatchUnpublish(event)"
        >
          批量下线 ({{ publishedCount }})
        </el-button>
        <el-button 
          v-if="canBatchDelete"
          type="danger" 
          size="small" 
          @click="(event) => handleBatchDelete(event)"
        >
          批量删除 ({{ notDeletedCount }})
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { 
  getAdminArticles, 
  publishArticle, 
  unpublishArticle, 
  deleteArticle,
  batchPublishArticles,
  batchUnpublishArticles,
  batchDeleteArticles
} from '@/api/article'
import { categoryApi } from '@/api/category'
import { showDeleteConfirm, showInfoConfirm, showWarningConfirm } from '@/utils/positionedConfirm'

const router = useRouter()

const loading = ref(false)
const articles = ref([])
const categories = ref([])
const selectedArticles = ref([])

const searchForm = reactive({
  title: '',
  categoryId: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 计算属性：批量操作相关
const draftCount = computed(() => {
  return selectedArticles.value.filter(article => 
    isStatus(article.status, 'draft')
  ).length
})

const publishedCount = computed(() => {
  return selectedArticles.value.filter(article => 
    isStatus(article.status, 'published')
  ).length
})

const notDeletedCount = computed(() => {
  return selectedArticles.value.filter(article => 
    !isStatus(article.status, 'deleted')
  ).length
})

const canBatchPublish = computed(() => draftCount.value > 0)
const canBatchUnpublish = computed(() => publishedCount.value > 0)
const canBatchDelete = computed(() => notDeletedCount.value > 0)

// 状态比较辅助函数
const isStatus = (status, targetStatus) => {
  const normalizedStatus = status ? status.toLowerCase() : ''
  const normalizedTarget = targetStatus ? targetStatus.toLowerCase() : ''
  return normalizedStatus === normalizedTarget
}

// 获取状态类型
const getStatusType = (status) => {
  const normalizedStatus = status ? status.toLowerCase() : ''
  const types = {
    published: 'success',
    draft: 'warning',
    deleted: 'danger',
    archived: 'info'
  }
  return types[normalizedStatus] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const normalizedStatus = status ? status.toLowerCase() : ''
  const texts = {
    published: '已发布',
    draft: '草稿',
    deleted: '已删除',
    archived: '已归档'
  }
  return texts[normalizedStatus] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedArticles.value = selection
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadArticles()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    title: '',
    categoryId: '',
    status: ''
  })
  pagination.page = 1
  loadArticles()
}

// 新建文章
const handleCreate = () => {
  router.push('/admin/articles/create')
}

// 编辑文章
const handleEdit = (row) => {
  router.push(`/admin/articles/${row.id}/edit`)
}

// 发布文章
const handlePublish = async (row, event) => {
  let loadingMessage = null
  try {
    // 检查文章状态
    if (isStatus(row.status, 'published')) {
      ElMessage.warning('文章已经是发布状态')
      return
    }
    
    await showInfoConfirm(
      `确定要发布文章《${row.title}》吗？\n\n发布后文章将在前台显示，用户可以浏览和评论。`, 
      '确认发布', 
      {
        confirmButtonText: '确定发布',
        cancelButtonText: '取消'
      },
      event
    )
    
    loadingMessage = ElMessage({
      message: '正在发布文章...',
      type: 'info',
      duration: 0
    })
    
    const response = await publishArticle(row.id)
    
    if (response.code === 200) {
      ElMessage.success('文章发布成功')
      loadArticles()
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('发布失败: ' + (error.message || error))
      console.error('发布文章失败:', error)
    }
  } finally {
    // 确保loading消息总是被关闭
    if (loadingMessage) {
      try {
        loadingMessage.close()
      } catch (e) {
        console.warn('关闭loading消息时出错:', e)
      }
    }
  }
}

// 下线文章
const handleUnpublish = async (row, event) => {
  let loadingMessage = null
  try {
    console.log('开始下线文章:', row)
    
    // 检查文章状态
    if (isStatus(row.status, 'draft')) {
      ElMessage.warning('文章已经是草稿状态')
      return
    }
    
    await showWarningConfirm(
      `确定下线文章《${row.title}》吗？`, 
      '确认下线', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      },
      event
    )
    
    console.log('用户确认下线，准备调用API，文章ID:', row.id)
    
    loadingMessage = ElMessage({
      message: '正在下线文章...',
      type: 'info',
      duration: 0
    })
    
    console.log('调用unpublishArticle API...')
    const response = await unpublishArticle(row.id)
    console.log('API响应:', response)
    
    if (response.code === 200) {
      ElMessage.success('文章下线成功')
      console.log('下线成功，刷新文章列表')
      loadArticles()
    } else {
      console.error('下线失败，响应:', response)
      ElMessage.error(response.message || '下线失败')
    }
  } catch (error) {
    console.error('下线过程中发生错误:', error)
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('下线失败: ' + (error.message || error))
      console.error('下线文章失败:', error)
    }
  } finally {
    // 确保loading消息总是被关闭
    if (loadingMessage) {
      try {
        loadingMessage.close()
      } catch (e) {
        console.warn('关闭loading消息时出错:', e)
      }
    }
  }
}

// 删除文章
const handleDelete = async (row, event) => {
  let loadingMessage = null
  try {
    console.log('开始删除文章:', row)
    
    await showDeleteConfirm(row.title, event)
    
    console.log('用户确认删除，准备调用API，文章ID:', row.id)
    
    loadingMessage = ElMessage({
      message: '正在删除文章...',
      type: 'info',
      duration: 0
    })
    
    console.log('调用deleteArticle API...')
    const response = await deleteArticle(row.id)
    console.log('API响应:', response)
    
    if (response.code === 200) {
      ElMessage.success('文章删除成功')
      console.log('删除成功，刷新文章列表')
      loadArticles()
    } else {
      console.error('删除失败，响应:', response)
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    console.error('删除过程中发生错误:', error)
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('删除失败: ' + (error.message || error))
      console.error('删除文章失败:', error)
    }
  } finally {
    // 确保loading消息总是被关闭
    if (loadingMessage) {
      try {
        loadingMessage.close()
      } catch (e) {
        console.warn('关闭loading消息时出错:', e)
      }
    }
  }
}

// 批量发布
const handleBatchPublish = async (event) => {
  let loadingMessage = null
  
  try {
    if (selectedArticles.value.length === 0) {
      ElMessage.warning('请先选择要发布的文章')
      return
    }
    
    // 检查是否有已经发布的文章
    const publishedArticles = selectedArticles.value.filter(article => 
      isStatus(article.status, 'published')
    )
    if (publishedArticles.length > 0) {
      ElMessage.warning(`选中的文章中有 ${publishedArticles.length} 篇已经是发布状态`)
      return
    }
    
    await showInfoConfirm(
      `确定要发布选中的 ${selectedArticles.value.length} 篇文章吗？\n\n发布后这些文章将在前台显示，用户可以浏览和评论。`, 
      '确认批量发布', 
      event
    )
    
    loadingMessage = ElMessage({
      message: `正在发布 ${selectedArticles.value.length} 篇文章...`,
      type: 'info',
      duration: 0
    })
    
    const ids = selectedArticles.value.map(item => item.id)
    const response = await batchPublishArticles(ids)
    
    if (response.code === 200) {
      ElMessage.success(`成功发布 ${selectedArticles.value.length} 篇文章`)
      selectedArticles.value = []
      loadArticles()
    } else {
      ElMessage.error(response.message || '批量发布失败')
    }
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('批量发布失败: ' + (error.message || error))
      console.error('批量发布失败:', error)
    }
  } finally {
    // 确保loading消息总是被关闭
    if (loadingMessage) {
      try {
        loadingMessage.close()
      } catch (e) {
        console.warn('关闭loading消息时出错:', e)
      }
    }
  }
}

// 批量下线
const handleBatchUnpublish = async (event) => {
  let loadingMessage = null
  try {
    if (selectedArticles.value.length === 0) {
      ElMessage.warning('请先选择要下线的文章')
      return
    }
    
    // 检查是否有已经是草稿状态的文章
    const draftArticles = selectedArticles.value.filter(article => 
      isStatus(article.status, 'draft')
    )
    if (draftArticles.length > 0) {
      ElMessage.warning(`选中的文章中有 ${draftArticles.length} 篇已经是草稿状态`)
      return
    }
    
    await showWarningConfirm(
      `确定下线选中的 ${selectedArticles.value.length} 篇文章吗？`, 
      '批量下线', 
      event
    )
    
    loadingMessage = ElMessage({
      message: `正在下线 ${selectedArticles.value.length} 篇文章...`,
      type: 'info',
      duration: 0
    })
    
    const ids = selectedArticles.value.map(item => item.id)
    const response = await batchUnpublishArticles(ids)
    
    if (response.code === 200) {
      ElMessage.success(`成功下线 ${selectedArticles.value.length} 篇文章`)
      selectedArticles.value = []
      loadArticles()
    } else {
      ElMessage.error(response.message || '批量下线失败')
    }
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('批量下线失败: ' + (error.message || error))
      console.error('批量下线失败:', error)
    }
  } finally {
    // 确保loading消息总是被关闭
    if (loadingMessage) {
      try {
        loadingMessage.close()
      } catch (e) {
        console.warn('关闭loading消息时出错:', e)
      }
    }
  }
}

// 批量删除
const handleBatchDelete = async (event) => {
  let loadingMessage = null
  try {
    if (selectedArticles.value.length === 0) {
      ElMessage.warning('请先选择要删除的文章')
      return
    }
    
    // 检查是否有已经删除的文章
    const deletedArticles = selectedArticles.value.filter(article => 
      isStatus(article.status, 'deleted')
    )
    if (deletedArticles.length > 0) {
      ElMessage.warning(`选中的文章中有 ${deletedArticles.length} 篇已经是删除状态`)
      return
    }
    
    await showDeleteConfirm(
      `选中的 ${selectedArticles.value.length} 篇文章`, 
      event
    )
    
    loadingMessage = ElMessage({
      message: `正在删除 ${selectedArticles.value.length} 篇文章...`,
      type: 'info',
      duration: 0
    })
    
    const ids = selectedArticles.value.map(item => item.id)
    const response = await batchDeleteArticles(ids)
    
    if (response.code === 200) {
      ElMessage.success(`成功删除 ${selectedArticles.value.length} 篇文章`)
      selectedArticles.value = []
      loadArticles()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('批量删除失败: ' + (error.message || error))
      console.error('批量删除失败:', error)
    }
  } finally {
    // 确保loading消息总是被关闭
    if (loadingMessage) {
      try {
        loadingMessage.close()
      } catch (e) {
        console.warn('关闭loading消息时出错:', e)
      }
    }
  }
}

// 页面大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadArticles()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page
  loadArticles()
}

// 加载文章列表
const loadArticles = async () => {
  loading.value = true
  try {
    // 根据分类ID查找分类名称
    let categoryName = null
    if (searchForm.categoryId) {
      const category = categories.value.find(cat => cat.id == searchForm.categoryId)
      categoryName = category ? category.name : null
    }
    
    const response = await getAdminArticles({
      page: pagination.page - 1,
      size: pagination.size,
      keyword: searchForm.title,
      status: searchForm.status,
      category: categoryName,
      sortBy: 'createdAt',
      sortDir: 'desc'
    })
    
    if (response.code === 200) {
      articles.value = response.data.content
      pagination.total = response.data.totalElements
    } else {
      ElMessage.error(response.message || '加载文章列表失败')
    }
  } catch (error) {
    console.error('加载文章列表失败:', error)
    ElMessage.error('加载文章列表失败')
    
    // 如果API调用失败，使用模拟数据作为后备
    const mockArticles = [
      {
        id: 1,
        title: 'Vue 3 Composition API 深度解析',
        category: { id: 1, name: '技术分享' },
        author: { id: 1, nickname: '管理员' },
        status: 'published',
        isTop: true,
        viewCount: 1234,
        likeCount: 56,
        commentCount: 23,
        publishedAt: '2024-01-15T10:00:00Z'
      },
      {
        id: 2,
        title: '2024年前端技术发展趋势报告',
        category: { id: 5, name: '新闻资讯' },
        author: { id: 1, nickname: '管理员' },
        status: 'published',
        isTop: false,
        viewCount: 987,
        likeCount: 43,
        commentCount: 18,
        publishedAt: '2024-01-14T15:30:00Z'
      }
    ]
    
    articles.value = mockArticles
    pagination.total = mockArticles.length
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await categoryApi.getAllCategories()
    if (response.code === 200) {
      categories.value = response.data
    } else {
      // 如果API调用失败，使用模拟数据作为后备
      categories.value = [
        { id: 1, name: '技术分享' },
        { id: 2, name: '生活随笔' },
        { id: 3, name: '学习笔记' },
        { id: 4, name: '项目实战' },
        { id: 5, name: '新闻资讯' },
        { id: 6, name: '竞赛活动' }
      ]
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
    // 如果API调用失败，使用模拟数据作为后备
    categories.value = [
      { id: 1, name: '技术分享' },
      { id: 2, name: '生活随笔' },
      { id: 3, name: '学习笔记' },
      { id: 4, name: '项目实战' },
      { id: 5, name: '新闻资讯' },
      { id: 6, name: '竞赛活动' }
    ]
  }
}

// 页面激活时刷新数据
onActivated(() => {
  loadArticles()
})

onMounted(() => {
  loadArticles()
  loadCategories()
})
</script>

<style lang="scss" scoped>
.admin-articles {
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

.article-title {
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

// 响应式设计
@media (max-width: 768px) {
  .admin-articles {
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