<template>
  <div class="profile-page">
    <div class="profile-container">
      <!-- 个人信息卡片 -->
      <el-card class="profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <el-avatar :size="120" :src="userInfo.avatar" />
            <el-upload
              ref="avatarUploadRef"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              accept="image/*"
            >
              <el-button type="primary" size="small" :loading="avatarUploading">
                {{ avatarUploading ? '上传中...' : '更换头像' }}
              </el-button>
            </el-upload>
          </div>
          <div class="user-info">
            <h2>{{ userInfo.nickname }}</h2>
            <p class="username">@{{ userInfo.username }}</p>
            <p class="bio">{{ userInfo.bio || '这个人很懒，什么都没有留下...' }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.articleCount }}</span>
                <span class="stat-label">文章</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.followersCount }}</span>
                <span class="stat-label">粉丝</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.followingCount }}</span>
                <span class="stat-label">关注</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.likesCount }}</span>
                <span class="stat-label">获赞</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
      
      <!-- 标签页内容 -->
      <el-card class="content-card">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <!-- 个人信息 -->
          <el-tab-pane label="个人信息" name="info">
            <el-form
              ref="infoFormRef"
              :model="infoForm"
              :rules="infoRules"
              label-width="100px"
              class="profile-form"
            >
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="用户名" prop="username">
                    <el-input v-model="infoForm.username" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="infoForm.nickname" placeholder="请输入昵称" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="手机号" prop="phone">
                    <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item label="个人简介" prop="bio">
                <el-input
                  v-model="infoForm.bio"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入个人简介"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
              
              <el-form-item label="个人网站" prop="website">
                <el-input v-model="infoForm.website" placeholder="请输入个人网站" />
              </el-form-item>
              
              <el-form-item label="GitHub" prop="github">
                <el-input v-model="infoForm.github" placeholder="请输入GitHub地址" />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleUpdateInfo" :loading="updating">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          
          <!-- 修改密码 -->
          <el-tab-pane label="修改密码" name="password">
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              class="profile-form"
              style="max-width: 400px"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleUpdatePassword" :loading="updatingPassword">
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          
          <!-- 我的文章 -->
          <el-tab-pane label="我的文章" name="articles">
            <div class="articles-section">
              <div class="articles-header">
                <el-input
                  v-model="articleSearch"
                  placeholder="搜索我的文章"
                  style="width: 300px"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                <el-select v-model="articleStatus" placeholder="文章状态" style="width: 120px">
                  <el-option label="全部" value="" />
                  <el-option label="已发布" value="published" />
                  <el-option label="草稿" value="draft" />
                </el-select>
              </div>
              
              <div class="articles-list">
                <div
                  v-for="article in filteredArticles"
                  :key="article.id"
                  class="article-item"
                  @click="handleViewArticle(article)"
                >
                  <div class="article-info">
                    <h3>{{ article.title }}</h3>
                    <p class="article-summary">{{ article.summary }}</p>
                    <div class="article-meta">
                      <el-tag :type="getStatusType(article.status)" size="small">
                        {{ getStatusText(article.status) }}
                      </el-tag>
                      <span class="article-date">{{ formatTime(article.createdAt) }}</span>
                      <span class="article-stats">
                        <el-icon><View /></el-icon>
                        {{ article.viewCount }}
                        <el-icon><ChatDotRound /></el-icon>
                        {{ article.commentCount }}
                        <el-icon><Star /></el-icon>
                        {{ article.likeCount }}
                      </span>
                    </div>
                  </div>
                  <div class="article-actions">
                    <el-button type="primary" size="small" @click.stop="handleEditArticle(article)">
                      编辑
                    </el-button>
                    <el-button type="danger" size="small" @click.stop="(event) => handleDeleteArticle(article, event)">
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
              
              <div v-if="filteredArticles.length === 0" class="empty-state">
                <el-empty description="暂无文章" />
              </div>
            </div>
          </el-tab-pane>
          
          <!-- 我的评论 -->
          <el-tab-pane label="我的评论" name="comments">
            <div class="comments-section">
              <div class="comments-list">
                <div
                  v-for="comment in userComments"
                  :key="comment.id"
                  class="comment-item"
                >
                  <div class="comment-content">
                    <p>{{ comment.content }}</p>
                    <div class="comment-meta">
                      <span class="comment-article">
                        评论于：<a @click="handleViewArticle(comment.article)">{{ comment.article.title }}</a>
                      </span>
                      <span class="comment-date">{{ formatTime(comment.createdAt) }}</span>
                    </div>
                  </div>
                  <div class="comment-actions">
                    <el-button type="danger" size="small" @click="(event) => handleDeleteComment(comment, event)">
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
              
              <div v-if="userComments.length === 0" class="empty-state">
                <el-empty description="暂无评论" />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, View, ChatDotRound, Star } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import dayjs from 'dayjs'
import axios from 'axios'
import { showDeleteConfirm } from '@/utils/positionedConfirm'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('info')
const updating = ref(false)
const updatingPassword = ref(false)
const articleSearch = ref('')
const articleStatus = ref('')
const avatarUploading = ref(false)

const infoFormRef = ref()
const passwordFormRef = ref()
const avatarUploadRef = ref()

// 上传配置
const uploadUrl = ref('/api/files/upload/avatar')
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.token}`
}))

const userInfo = computed(() => userStore.user || {})

const infoForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  bio: '',
  website: '',
  github: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const userArticles = ref([])
const userComments = ref([])

const infoRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 50, message: '昵称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 过滤后的文章列表
const filteredArticles = computed(() => {
  let articles = userArticles.value
  
  if (articleSearch.value) {
    articles = articles.filter(article =>
      article.title.toLowerCase().includes(articleSearch.value.toLowerCase()) ||
      article.summary.toLowerCase().includes(articleSearch.value.toLowerCase())
    )
  }
  
  if (articleStatus.value) {
    articles = articles.filter(article => article.status === articleStatus.value)
  }
  
  return articles
})

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    published: 'success',
    draft: 'warning'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    published: '已发布',
    draft: '草稿'
  }
  return texts[status] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 头像上传前的验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  
  avatarUploading.value = true
  return true
}

// 头像上传成功
const handleAvatarSuccess = (response) => {
  avatarUploading.value = false
  if (response.code === 200) {
    userInfo.value.avatar = response.data.fileUrl
    ElMessage.success('头像更新成功!')
    
    // 更新用户头像到后端
    updateUserAvatar(response.data.fileUrl)
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 头像上传失败
const handleAvatarError = (error) => {
  avatarUploading.value = false
  console.error('头像上传失败:', error)
  ElMessage.error('头像上传失败，请重试')
}

// 更新用户头像
const updateUserAvatar = async (avatarUrl) => {
  try {
    const response = await userApi.updateAvatar(userInfo.value.id, avatarUrl)
    
    if (response.code === 200) {
      // 更新用户store中的头像
      userStore.updateUser({ avatar: response.data.avatar })
    } else {
      ElMessage.error(response.message || '头像更新失败')
    }
  } catch (error) {
    console.error('更新用户头像失败:', error)
    ElMessage.error('头像保存失败')
  }
}

// 更新个人信息
const handleUpdateInfo = async () => {
  try {
    await infoFormRef.value.validate()
    updating.value = true
    
    const response = await userApi.updateUser(userInfo.value.id, infoForm)
    
    if (response.code === 200) {
      // 更新用户store中的信息
      userStore.updateUser(response.data)
      ElMessage.success('个人信息更新成功')
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    if (error !== 'validation failed') {
      ElMessage.error('更新失败')
    }
  } finally {
    updating.value = false
  }
}

// 修改密码
const handleUpdatePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    updatingPassword.value = true
    
    const response = await userApi.updatePassword(userInfo.value.id, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    if (response.code === 200) {
      // 重置表单
      passwordFormRef.value.resetFields()
      Object.assign(passwordForm, {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      })
      ElMessage.success('密码修改成功')
    } else {
      ElMessage.error(response.message || '修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    if (error !== 'validation failed') {
      ElMessage.error('修改失败')
    }
  } finally {
    updatingPassword.value = false
  }
}

// 查看文章
const handleViewArticle = (article) => {
  router.push(`/article/${article.slug}`)
}

// 编辑文章
const handleEditArticle = (article) => {
  router.push(`/admin/articles/edit/${article.id}`)
}

// 删除文章
const handleDeleteArticle = async (article, event) => {
  try {
    await showDeleteConfirm(`确定要删除文章"${article.title}"吗？`, event)
    
    // 这里应该调用实际的API
    console.log('删除文章:', article.id)
    
    // 从列表中移除
    const index = userArticles.value.findIndex(item => item.id === article.id)
    if (index > -1) {
      userArticles.value.splice(index, 1)
    }
    
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 删除评论
const handleDeleteComment = async (comment, event) => {
  try {
    await showDeleteConfirm('确定要删除这条评论吗？', event)
    
    // 这里应该调用实际的API
    console.log('删除评论:', comment.id)
    
    // 从列表中移除
    const index = userComments.value.findIndex(item => item.id === comment.id)
    if (index > -1) {
      userComments.value.splice(index, 1)
    }
    
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 加载用户文章
const loadUserArticles = async () => {
  try {
    // 这里应该调用实际的API
    userArticles.value = [
      {
        id: 1,
        title: 'Vue.js 3.0 新特性详解',
        summary: '本文详细介绍了Vue.js 3.0的新特性和改进...',
        status: 'published',
        viewCount: 1250,
        commentCount: 15,
        likeCount: 89,
        createdAt: '2025-01-15T10:00:00Z'
      },
      {
        id: 2,
        title: 'JavaScript ES2023 新特性',
        summary: '探索JavaScript ES2023版本中的新特性...',
        status: 'published',
        viewCount: 890,
        commentCount: 8,
        likeCount: 56,
        createdAt: '2025-01-10T14:30:00Z'
      },
      {
        id: 3,
        title: 'React Hooks 最佳实践',
        summary: '分享React Hooks的使用技巧和最佳实践...',
        status: 'draft',
        viewCount: 0,
        commentCount: 0,
        likeCount: 0,
        createdAt: '2025-01-08T09:15:00Z'
      }
    ]
  } catch (error) {
    console.error('加载用户文章失败:', error)
  }
}

// 加载用户评论
const loadUserComments = async () => {
  try {
    // 这里应该调用实际的API
    userComments.value = [
      {
        id: 1,
        content: '这篇文章写得很好，学到了很多新知识！',
        article: {
          id: 10,
          title: 'TypeScript 进阶指南'
        },
        createdAt: '2025-01-14T16:20:00Z'
      },
      {
        id: 2,
        content: '感谢分享，代码示例很实用。',
        article: {
          id: 11,
          title: 'Node.js 性能优化技巧'
        },
        createdAt: '2025-01-12T11:45:00Z'
      }
    ]
  } catch (error) {
    console.error('加载用户评论失败:', error)
  }
}

onMounted(async () => {
  // 确保用户信息已加载
  if (!userStore.user) {
    await userStore.initUser()
  }
  
  // 初始化表单数据
  if (userInfo.value) {
    Object.assign(infoForm, {
      username: userInfo.value.username || '',
      nickname: userInfo.value.nickname || '',
      email: userInfo.value.email || '',
      phone: userInfo.value.phone || '',
      bio: userInfo.value.bio || '',
      website: userInfo.value.website || '',
      github: userInfo.value.github || ''
    })
  }
  
  // 加载数据
  await Promise.all([
    loadUserArticles(),
    loadUserComments()
  ])
})
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: calc(100vh - 60px);
  background: var(--el-bg-color-page);
  padding: 20px;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card {
  margin-bottom: 20px;
  
  .profile-header {
    display: flex;
    gap: 30px;
    
    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 15px;
    }
    
    .user-info {
      flex: 1;
      
      h2 {
        margin: 0 0 8px 0;
        font-size: 1.8rem;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
      
      .username {
        margin: 0 0 12px 0;
        color: var(--el-text-color-secondary);
        font-size: 1rem;
      }
      
      .bio {
        margin: 0 0 20px 0;
        color: var(--el-text-color-regular);
        line-height: 1.6;
      }
      
      .user-stats {
        display: flex;
        gap: 30px;
        
        .stat-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          
          .stat-number {
            font-size: 1.5rem;
            font-weight: 600;
            color: var(--el-color-primary);
          }
          
          .stat-label {
            font-size: 0.875rem;
            color: var(--el-text-color-secondary);
            margin-top: 4px;
          }
        }
      }
    }
  }
}

.content-card {
  .profile-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 20px;
    }
  }
}

.profile-form {
  max-width: 600px;
}

.articles-section {
  .articles-header {
    display: flex;
    gap: 15px;
    margin-bottom: 20px;
    align-items: center;
  }
  
  .articles-list {
    .article-item {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      padding: 20px;
      border: 1px solid var(--el-border-color);
      border-radius: 8px;
      margin-bottom: 15px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        border-color: var(--el-color-primary);
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      }
      
      .article-info {
        flex: 1;
        
        h3 {
          margin: 0 0 8px 0;
          font-size: 1.1rem;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }
        
        .article-summary {
          margin: 0 0 12px 0;
          color: var(--el-text-color-regular);
          line-height: 1.5;
        }
        
        .article-meta {
          display: flex;
          align-items: center;
          gap: 15px;
          font-size: 0.875rem;
          color: var(--el-text-color-secondary);
          
          .article-stats {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .el-icon {
              font-size: 14px;
            }
          }
        }
      }
      
      .article-actions {
        display: flex;
        gap: 8px;
      }
    }
  }
}

.comments-section {
  .comments-list {
    .comment-item {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      padding: 15px;
      border: 1px solid var(--el-border-color);
      border-radius: 8px;
      margin-bottom: 15px;
      
      .comment-content {
        flex: 1;
        
        p {
          margin: 0 0 10px 0;
          color: var(--el-text-color-primary);
          line-height: 1.6;
        }
        
        .comment-meta {
          font-size: 0.875rem;
          color: var(--el-text-color-secondary);
          
          .comment-article {
            margin-right: 15px;
            
            a {
              color: var(--el-color-primary);
              text-decoration: none;
              cursor: pointer;
              
              &:hover {
                text-decoration: underline;
              }
            }
          }
        }
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

// 响应式设计
@media (max-width: 768px) {
  .profile-page {
    padding: 15px;
  }
  
  .profile-card {
    .profile-header {
      flex-direction: column;
      text-align: center;
      
      .user-info {
        .user-stats {
          justify-content: center;
          gap: 20px;
        }
      }
    }
  }
  
  .articles-section {
    .articles-header {
      flex-direction: column;
      align-items: stretch;
      
      .el-input,
      .el-select {
        width: 100% !important;
      }
    }
    
    .articles-list {
      .article-item {
        flex-direction: column;
        gap: 15px;
        
        .article-actions {
          align-self: flex-end;
        }
      }
    }
  }
  
  .comments-section {
    .comments-list {
      .comment-item {
        flex-direction: column;
        gap: 15px;
        
        .comment-actions {
          align-self: flex-end;
        }
      }
    }
  }
}
</style>