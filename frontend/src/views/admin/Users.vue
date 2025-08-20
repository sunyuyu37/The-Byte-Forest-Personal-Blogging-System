<template>
  <div class="admin-users">
    <div class="page-header">
      <h1>用户管理</h1>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        新建用户
      </el-button>
    </div>
    
    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input
            v-model="searchForm.nickname"
            placeholder="请输入昵称"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="searchForm.email"
            placeholder="请输入邮箱"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="searchForm.role"
            placeholder="请选择角色"
            clearable
            style="width: 120px"
          >
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" value="active" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 用户列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="users"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="user" label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.avatar" />
              <div class="user-details">
                <div class="username">{{ row.username }}</div>
                <div class="nickname">{{ row.nickname }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getRoleType(row.role)"
              size="small"
            >
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="articleCount" label="文章数" width="100" />
        <el-table-column prop="commentCount" label="评论数" width="100" />
        <el-table-column prop="lastLoginAt" label="最后登录" width="180">
          <template #default="{ row }">
            {{ formatTime(row.lastLoginAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="active"
              inactive-value="disabled"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
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
              type="warning"
              size="small"
              @click="(event) => handleResetPassword(row, event)"
            >
              重置密码
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="(event) => handleDelete(row, event)"
              :disabled="row.role === 'admin'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div v-if="selectedUsers.length > 0" class="batch-actions">
        <span>已选择 {{ selectedUsers.length }} 项</span>
        <el-button type="success" size="small" @click="(event) => handleBatchEnable(event)">
          批量启用
        </el-button>
        <el-button type="warning" size="small" @click="(event) => handleBatchDisable(event)">
          批量禁用
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
    
    <!-- 用户表单对话框 -->
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
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :disabled="!!form.id"
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio label="user">普通用户</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-input v-model="form.avatar" placeholder="请输入头像URL" />
        </el-form-item>
        <el-form-item label="个人简介" prop="bio">
          <el-input
            v-model="form.bio"
            type="textarea"
            :rows="3"
            placeholder="请输入个人简介"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="active">正常</el-radio>
            <el-radio label="disabled">禁用</el-radio>
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
import { Plus } from '@element-plus/icons-vue'
import { showWarningConfirm, showDeleteConfirm } from '@/utils/positionedConfirm'
import dayjs from 'dayjs'
import { generateAvatarPlaceholder } from '@/utils/placeholder'
import { userApi } from '@/api/user'

const loading = ref(false)
const users = ref([])
const selectedUsers = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()

const searchForm = reactive({
  username: '',
  nickname: '',
  email: '',
  role: '',
  status: ''
})

const form = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  password: '',
  role: 'user',
  avatar: '',
  bio: '',
  status: 'active'
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 50, message: '昵称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 对话框标题
const dialogTitle = computed(() => {
  return form.id ? '编辑用户' : '新建用户'
})

// 获取角色类型
const getRoleType = (role) => {
  const types = {
    admin: 'danger',
    user: 'primary'
  }
  return types[role] || 'info'
}

// 获取角色文本
const getRoleText = (role) => {
  const texts = {
    admin: '管理员',
    user: '普通用户'
  }
  return texts[role] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 状态变化
const handleStatusChange = async (row) => {
  try {
    const result = await userApi.toggleUserStatus(row.id)
    if (result.code === 200) {
      ElMessage.success('状态更新成功')
      // 更新本地状态
      row.status = result.data.status ? 'active' : 'disabled'
    } else {
      throw new Error(result.message || '状态更新失败')
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error(error.message || '状态更新失败')
    // 恢复原状态
    row.status = row.status === 'active' ? 'disabled' : 'active'
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadUsers()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    nickname: '',
    email: '',
    role: '',
    status: ''
  })
  pagination.page = 1
  loadUsers()
}

// 新建用户
const handleCreate = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    username: row.username,
    nickname: row.nickname,
    email: row.email,
    password: '',
    role: row.role,
    avatar: row.avatar,
    bio: row.bio,
    status: row.status
  })
  dialogVisible.value = true
}

// 重置密码
const handleResetPassword = async (row, event) => {
  try {
    await showWarningConfirm(`确定要重置用户"${row.username}"的密码吗？`, '确认', event)
    
    const result = await userApi.resetPassword(row.id)
    if (result.code === 200) {
      ElMessage.success('密码重置成功')
    } else {
      throw new Error(result.message || '密码重置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error(error.message || '重置密码失败')
    }
  }
}

// 删除用户
const handleDelete = async (row, event) => {
  if (row.role === 'admin') {
    ElMessage.warning('管理员用户无法删除')
    return
  }
  
  try {
    await showDeleteConfirm(row.username, event)
    
    const result = await userApi.deleteUser(row.id)
    if (result.code === 200) {
      ElMessage.success('删除成功')
      await loadUsers()
    } else {
      throw new Error(result.message || '删除用户失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量启用
const handleBatchEnable = async (event) => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要启用的用户')
    return
  }
  
  try {
    await showWarningConfirm(`确定要启用选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }, event)
    
    const userIds = selectedUsers.value.map(user => user.id)
    const result = await userApi.batchEnableUsers(userIds)
    if (result.code === 200) {
      ElMessage.success('批量启用成功')
      selectedUsers.value = []
      await loadUsers()
    } else {
      throw new Error(result.message || '批量启用失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量启用失败:', error)
      ElMessage.error(error.message || '批量启用失败')
    }
  }
}

// 批量禁用
const handleBatchDisable = async (event) => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要禁用的用户')
    return
  }
  
  try {
    await showWarningConfirm(`确定要禁用选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }, event)
    
    const userIds = selectedUsers.value.map(user => user.id)
    const result = await userApi.batchDisableUsers(userIds)
    if (result.code === 200) {
      ElMessage.success('批量禁用成功')
      selectedUsers.value = []
      await loadUsers()
    } else {
      throw new Error(result.message || '批量禁用失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量禁用失败:', error)
      ElMessage.error(error.message || '批量禁用失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async (event) => {
  const canDelete = selectedUsers.value.filter(user => user.role !== 'admin')
  if (canDelete.length === 0) {
    ElMessage.warning('选中的用户中包含管理员，无法删除')
    return
  }
  
  try {
    await showDeleteConfirm(`确定要删除选中的 ${canDelete.length} 个用户吗？删除后无法恢复！`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }, event)
    
    const userIds = canDelete.map(user => user.id)
    const result = await userApi.batchDeleteUsers(userIds)
    if (result.code === 200) {
      ElMessage.success('批量删除成功')
      selectedUsers.value = []
      await loadUsers()
    } else {
      throw new Error(result.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    const userData = {
      username: form.username,
      nickname: form.nickname,
      email: form.email,
      role: form.role.toUpperCase(),
      avatar: form.avatar,
      bio: form.bio,
      status: form.status === 'active'
    }
    
    // 如果是创建用户，添加密码
    if (!form.id && form.password) {
      userData.password = form.password
    }
    
    let result
    if (form.id) {
      result = await userApi.updateUserByAdmin(form.id, userData)
    } else {
      result = await userApi.createUser(userData)
    }
    
    if (result.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadUsers()
    } else {
      throw new Error(result.message || (form.id ? '更新用户失败' : '创建用户失败'))
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '操作失败')
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
    username: '',
    nickname: '',
    email: '',
    password: '',
    role: 'user',
    avatar: '',
    bio: '',
    status: 'active'
  })
}

// 页面大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadUsers()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page
  loadUsers()
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      page: pagination.page - 1,
      size: pagination.size
    }
    
    // 构建关键词搜索（包含用户名、昵称、邮箱）
    const keywords = []
    if (searchForm.username) keywords.push(searchForm.username)
    if (searchForm.nickname) keywords.push(searchForm.nickname)
    if (searchForm.email) keywords.push(searchForm.email)
    if (keywords.length > 0) {
      params.keyword = keywords.join(' ')
    }
    
    // 角色筛选
    if (searchForm.role) params.role = searchForm.role
    
    const result = await userApi.getUsers(params)
    
    if (result.code === 200) {
      users.value = result.data.content.map(user => ({
        id: user.id,
        username: user.username,
        nickname: user.nickname || user.username,
        email: user.email,
        role: user.role?.toLowerCase() || 'user',
        avatar: user.avatar || generateAvatarPlaceholder(user.nickname || user.username, 40),
        bio: user.bio || '',
        articleCount: user.articleCount || 0,
        commentCount: 0, // 评论数暂时设为0，后续可以从后端获取
        status: user.status ? 'active' : 'disabled',
        lastLoginAt: user.lastLoginTime,
        createdAt: user.createdAt
      }))
      
      pagination.total = result.data.totalElements
    } else {
      throw new Error(result.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error(`加载用户列表失败: ${error.message}`)
    
    // 使用模拟数据作为后备
    users.value = [
      {
        id: 1,
        username: 'admin',
        nickname: '管理员',
        email: 'admin@example.com',
        role: 'admin',
        avatar: generateAvatarPlaceholder('管理员', 40),
        bio: '系统管理员',
        articleCount: 15,
        commentCount: 25,
        status: 'active',
        lastLoginAt: '2025-08-10T10:00:00Z',
        createdAt: '2025-08-10T10:00:00Z'
      }
    ]
    pagination.total = 1
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style lang="scss" scoped>
.admin-users {
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

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .user-details {
    .username {
      font-weight: 500;
      color: var(--el-text-color-primary);
      margin-bottom: 4px;
    }
    
    .nickname {
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }
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

.dialog-footer {
  text-align: right;
}

// 响应式设计
@media (max-width: 768px) {
  .admin-users {
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