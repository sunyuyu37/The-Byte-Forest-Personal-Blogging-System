<template>
  <div class="admin-comments">
    <div class="page-header">
      <h1>评论管理</h1>
    </div>
    
    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="评论内容">
          <el-input
            v-model="searchForm.content"
            placeholder="请输入评论内容"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input
            v-model="searchForm.userNickname"
            placeholder="请输入用户昵称"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="文章标题">
          <el-input
            v-model="searchForm.articleTitle"
            placeholder="请输入文章标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 评论列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="comments"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="user" label="用户" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="30" :src="row.user?.avatar" />
              <span>{{ row.user?.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="300">
          <template #default="{ row }">
            <div class="comment-content">
              <p>{{ row.content }}</p>
              <div v-if="row.parentComment" class="reply-to">
                回复：{{ row.parentComment.content }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="article" label="文章" width="200">
          <template #default="{ row }">
            <el-link
              v-if="row.articleTitle"
              type="primary"
              @click="viewArticle(row.articleId)"
            >
              {{ row.articleTitle }}
            </el-link>
            <span v-else class="text-gray-400">文章已删除</span>
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
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="createdAt" label="评论时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="warning"
              size="small"
              @click="(event) => handleReject(row, event)"
            >
              拒绝
            </el-button>
            <el-button
              type="primary"
              size="small"
              @click="handleReply(row)"
            >
              回复
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="(event) => handleDelete(row, event)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div v-if="selectedComments.length > 0" class="batch-actions">
        <span>已选择 {{ selectedComments.length }} 项</span>
        <el-button type="success" size="small" @click="handleBatchApprove">
          批量通过
        </el-button>
        <el-button type="warning" size="small" @click="(event) => handleBatchReject(event)">
          批量拒绝
        </el-button>
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
    
    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="回复评论"
      width="600px"
      @close="handleReplyDialogClose"
    >
      <div v-if="currentComment" class="original-comment">
        <h4>原评论：</h4>
        <div class="comment-info">
          <div class="user-info">
            <el-avatar :size="30" :src="currentComment.user?.avatar" />
            <span>{{ currentComment.user?.nickname }}</span>
            <span class="time">{{ formatTime(currentComment.createdAt) }}</span>
          </div>
          <p class="content">{{ currentComment.content }}</p>
        </div>
      </div>
      
      <el-form
        ref="replyFormRef"
        :model="replyForm"
        :rules="replyRules"
        label-width="80px"
      >
        <el-form-item label="回复内容" prop="content">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitReply" :loading="replying">
            发送回复
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { generateAvatarPlaceholder } from '@/utils/placeholder'
import { commentApi } from '@/api/comment'
import { showDeleteConfirm, showWarningConfirm } from '@/utils/positionedConfirm'

const router = useRouter()

const loading = ref(false)
const comments = ref([])
const selectedComments = ref([])
const replyDialogVisible = ref(false)
const replying = ref(false)
const currentComment = ref(null)
const replyFormRef = ref()

const searchForm = reactive({
  content: '',
  userNickname: '',
  articleTitle: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const replyForm = reactive({
  content: ''
})

const replyRules = {
  content: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 1, max: 500, message: '回复内容长度在 1 到 500 个字符', trigger: 'blur' }
  ]
}

// 获取状态类型
const getStatusType = (status) => {
  const statusLower = status ? status.toLowerCase() : ''
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    spam: 'danger'
  }
  return types[statusLower] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusLower = status ? status.toLowerCase() : ''
  const texts = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    spam: '垃圾评论'
  }
  return texts[statusLower] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedComments.value = selection
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadComments()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    content: '',
    userNickname: '',
    articleTitle: '',
    status: ''
  })
  pagination.page = 1
  loadComments()
}

// 查看文章
const viewArticle = async (articleIdOrSlug) => {
  if (!articleIdOrSlug) return
  
  // 如果传入的是数字ID，则先获取文章详情拿到 slug
  if (/^\d+$/.test(String(articleIdOrSlug))) {
    try {
      const res = await articleApi.getArticleById(articleIdOrSlug)
      const slug = res.data?.slug
      if (slug) {
        router.push(`/article/${slug}`)
      } else {
        router.push(`/article/${articleIdOrSlug}`) // 兜底
      }
    } catch (e) {
      router.push(`/article/${articleIdOrSlug}`)
    }
  } else {
    router.push(`/article/${articleIdOrSlug}`)
  }
}

// 通过评论
const handleApprove = async (row) => {
  try {
    const result = await commentApi.approveComment(row.id)
    if (result.code === 200) {
      ElMessage.success('评论已通过')
      loadComments()
    } else {
      throw new Error(result.message || '通过评论失败')
    }
  } catch (error) {
    console.error('通过评论失败:', error)
    ElMessage.error('操作失败')
  }
}

// 拒绝评论
const handleReject = async (row, event) => {
  try {
    await showWarningConfirm('确定要拒绝这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }, event)
    
    const result = await commentApi.rejectComment(row.id)
    if (result.code === 200) {
      ElMessage.success('评论已拒绝')
      loadComments()
    } else {
      throw new Error(result.message || '拒绝评论失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('拒绝评论失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 回复评论
const handleReply = (row) => {
  currentComment.value = row
  replyForm.content = ''
  replyDialogVisible.value = true
}

// 删除评论
const handleDelete = async (row, event) => {
  try {
    await showDeleteConfirm('这条评论', event)
    
    const result = await commentApi.deleteComment(row.id)
    if (result.code === 200) {
      ElMessage.success('删除成功')
      loadComments()
    } else {
      throw new Error(result.message || '删除评论失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量通过
const handleBatchApprove = async () => {
  try {
    const result = await commentApi.batchApprove(selectedComments.value.map(item => item.id))
    if (result.code === 200) {
      ElMessage.success('批量通过成功')
      selectedComments.value = []
      loadComments()
    } else {
      throw new Error(result.message || '批量通过失败')
    }
  } catch (error) {
    console.error('批量通过失败:', error)
    ElMessage.error('批量通过失败')
  }
}

// 批量拒绝
const handleBatchReject = async (event) => {
  try {
    await showWarningConfirm(`确定要拒绝选中的 ${selectedComments.value.length} 条评论吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }, event)
    
    const result = await commentApi.batchReject(selectedComments.value.map(item => item.id))
    if (result.code === 200) {
      ElMessage.success('批量拒绝成功')
      selectedComments.value = []
      loadComments()
    } else {
      throw new Error(result.message || '批量拒绝失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量拒绝失败:', error)
      ElMessage.error('批量拒绝失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async (event) => {
  try {
    await showDeleteConfirm(`确定要删除选中的 ${selectedComments.value.length} 条评论吗？删除后无法恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }, event)
    
    const result = await commentApi.batchDelete(selectedComments.value.map(item => item.id))
    if (result.code === 200) {
      ElMessage.success('批量删除成功')
      selectedComments.value = []
      loadComments()
    } else {
      throw new Error(result.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 提交回复
const handleSubmitReply = async () => {
  try {
    await replyFormRef.value.validate()
    replying.value = true
    
    const result = await commentApi.createComment({
      articleId: currentComment.value.article.id,
      content: replyForm.content,
      parentId: currentComment.value.id
    })
    
    if (result.code === 200) {
      ElMessage.success('回复成功')
      replyDialogVisible.value = false
      loadComments()
    } else {
      throw new Error(result.message || '回复失败')
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  } finally {
    replying.value = false
  }
}

// 关闭回复对话框
const handleReplyDialogClose = () => {
  replyFormRef.value?.resetFields()
  replyForm.content = ''
  currentComment.value = null
}

// 页面大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadComments()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page
  loadComments()
}

// 加载评论列表
const loadComments = async () => {
  try {
    loading.value = true
    
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    
    const result = await commentApi.getComments(params)
    
    if (result.code === 200) {
      comments.value = result.data.records || []
      pagination.total = result.data.total || 0
    } else {
      throw new Error(result.message || '加载评论列表失败')
    }
  } catch (error) {
    console.error('加载评论列表失败:', error)
    ElMessage.error('加载失败')
    comments.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadComments()
})
</script>

<style lang="scss" scoped>
.admin-comments {
  min-height: calc(100vh - 140px);
}

.page-header {
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

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  
  span {
    font-size: 14px;
  }
}

.comment-content {
  p {
    margin: 0 0 8px 0;
    line-height: 1.5;
  }
  
  .reply-to {
    padding: 8px;
    background: var(--el-bg-color);
    border-radius: 4px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
    border-left: 3px solid var(--el-color-primary);
  }
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

.original-comment {
  margin-bottom: 20px;
  padding: 15px;
  background: var(--el-bg-color);
  border-radius: 8px;
  
  h4 {
    margin: 0 0 10px 0;
    color: var(--el-text-color-primary);
  }
  
  .comment-info {
    .user-info {
      margin-bottom: 10px;
      
      .time {
        margin-left: auto;
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
    
    .content {
      margin: 0;
      line-height: 1.5;
      color: var(--el-text-color-regular);
    }
  }
}

.dialog-footer {
  text-align: right;
}

// 响应式设计
@media (max-width: 768px) {
  .admin-comments {
    min-height: calc(100vh - 120px);
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