<template>
  <div class="account-settings">
    <div class="settings-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <el-icon class="title-icon"><Setting /></el-icon>
            账户设置
          </h1>
          <p class="page-subtitle">管理您的个人信息和账户偏好设置</p>
        </div>
      </div>
    </div>

    <div class="settings-content">
      <div class="settings-grid">
        <!-- 基本信息 -->
        <div class="settings-card">
          <div class="card-header">
            <h3>基本信息</h3>
            <p>更新您的个人基本信息</p>
          </div>
          <div class="card-content">
            <el-form 
              :model="userForm" 
              :rules="userFormRules"
              ref="userFormRef"
              label-width="100px" 
              class="user-form"
            >
              <el-form-item label="头像">
                <div class="avatar-upload">
                  <el-avatar :src="userForm.avatar" :size="80" class="avatar-preview">
                    {{ userForm.nickname?.charAt(0) || 'A' }}
                  </el-avatar>
                  <div class="upload-actions">
                    <el-upload
                      class="avatar-uploader"
                      :action="`${baseUrl}/files/upload/avatar`"
                      :headers="{ Authorization: `Bearer ${getToken()}` }"
                      :show-file-list="false"
                      :on-success="handleAvatarSuccess"
                      :on-error="handleAvatarError"
                      :before-upload="beforeAvatarUpload"
                      accept="image/*"
                    >
                      <el-button type="primary" size="small" :loading="avatarUploading">
                        <el-icon><Upload /></el-icon>
                        {{ avatarUploading ? '上传中...' : '上传头像' }}
                      </el-button>
                    </el-upload>
                    <el-button size="small" @click="removeAvatar" :disabled="!userForm.avatar">移除</el-button>
                  </div>
                </div>
              </el-form-item>
              
              <el-form-item label="昵称" prop="nickname">
                <el-input 
                  v-model="userForm.nickname" 
                  placeholder="请输入昵称" 
                  maxlength="50"
                  show-word-limit
                />
              </el-form-item>
              
              <el-form-item label="邮箱" prop="email">
                <el-input 
                  v-model="userForm.email" 
                  placeholder="请输入邮箱" 
                  type="email" 
                  readonly
                />
                <div class="field-note">邮箱不支持修改</div>
              </el-form-item>
              
              <el-form-item label="个人简介" prop="bio">
                <el-input 
                  v-model="userForm.bio" 
                  type="textarea" 
                  :rows="4" 
                  placeholder="介绍一下自己吧..."
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="个人网站" prop="website">
                <el-input 
                  v-model="userForm.website" 
                  placeholder="请输入个人网站地址"
                  maxlength="200"
                />
              </el-form-item>

              <el-form-item label="GitHub" prop="github">
                <el-input 
                  v-model="userForm.github" 
                  placeholder="请输入GitHub地址"
                  maxlength="200"
                />
              </el-form-item>
              
              <el-form-item>
                <el-button 
                  type="primary" 
                  @click="saveBasicInfo" 
                  :loading="saveLoading"
                >
                  {{ saveLoading ? '保存中...' : '保存更改' }}
                </el-button>
                <el-button @click="resetBasicInfo">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 偏好设置 -->
        <div class="settings-card">
          <div class="card-header">
            <h3>偏好设置</h3>
            <p>自定义您的使用体验</p>
          </div>
          <div class="card-content">
            <div class="preference-item">
              <div class="preference-info">
                <h4>邮件通知</h4>
                <p>接收系统邮件通知</p>
              </div>
              <el-switch 
                v-model="preferences.emailNotifications" 
                @change="updatePreference('emailNotifications')"
              />
            </div>
            
            <div class="preference-item">
              <div class="preference-info">
                <h4>桌面通知</h4>
                <p>接收浏览器桌面通知</p>
              </div>
              <el-switch 
                v-model="preferences.desktopNotifications" 
                @change="updatePreference('desktopNotifications')"
              />
            </div>
            
            <div class="preference-actions">
              <el-button 
                type="primary" 
                @click="savePreferences"
                :loading="preferencesLoading"
              >
                {{ preferencesLoading ? '保存中...' : '保存偏好' }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- 密码修改 -->
        <div class="settings-card">
          <div class="card-header">
            <h3>密码修改</h3>
            <p>定期更改密码可提高账户安全性</p>
          </div>
          <div class="card-content">
            <el-form 
              :model="passwordForm" 
              :rules="passwordFormRules"
              ref="passwordFormRef"
              label-width="100px"
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
                <el-button 
                  type="primary" 
                  @click="changePassword"
                  :loading="passwordLoading"
                >
                  {{ passwordLoading ? '修改中...' : '修改密码' }}
                </el-button>
                <el-button @click="resetPasswordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 账户状态 -->
        <div class="settings-card">
          <div class="card-header">
            <h3>账户状态</h3>
            <p>查看您的账户信息和状态</p>
          </div>
          <div class="card-content">
            <div class="status-grid">
              <div class="status-item">
                <div class="status-icon success">
                  <el-icon><CircleCheckFilled /></el-icon>
                </div>
                <div class="status-info">
                  <h4>账户状态</h4>
                  <p>{{ userStore.user?.status ? '正常' : '已停用' }}</p>
                </div>
              </div>
              
              <div class="status-item">
                <div class="status-icon success">
                  <el-icon><CircleCheckFilled /></el-icon>
                </div>
                <div class="status-info">
                  <h4>邮箱验证</h4>
                  <p>已验证</p>
                </div>
              </div>
              
              <div class="status-item">
                <div class="status-icon info">
                  <el-icon><InfoFilled /></el-icon>
                </div>
                <div class="status-info">
                  <h4>用户角色</h4>
                  <p>{{ userStore.user?.role === 'ADMIN' ? '管理员' : '普通用户' }}</p>
                </div>
              </div>
              
              <div class="status-item">
                <div class="status-icon info">
                  <el-icon><InfoFilled /></el-icon>
                </div>
                <div class="status-info">
                  <h4>注册时间</h4>
                  <p>{{ formatDate(userStore.user?.createdAt) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { getSecuritySettings, updateSecuritySettings } from '@/api/security'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { 
  Setting, 
  Upload, 
  CircleCheckFilled, 
  InfoFilled 
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const userFormRef = ref()
const passwordFormRef = ref()

// 基础URL
const baseUrl = '/api'

// 加载状态
const saveLoading = ref(false)
const avatarUploading = ref(false)
const preferencesLoading = ref(false)
const passwordLoading = ref(false)

// 用户表单数据
const userForm = reactive({
  avatar: '',
  nickname: '',
  email: '',
  bio: '',
  website: '',
  github: ''
})

// 表单验证规则
const userFormRules = {
  nickname: [
    { max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }
  ],
  bio: [
    { max: 200, message: '个人简介长度不能超过200个字符', trigger: 'blur' }
  ],
  website: [
    { max: 200, message: '网站地址长度不能超过200个字符', trigger: 'blur' }
  ],
  github: [
    { max: 200, message: 'GitHub地址长度不能超过200个字符', trigger: 'blur' }
  ]
}

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码表单验证规则
const passwordFormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度必须在6-100个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
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

// 偏好设置
const preferences = reactive({
  emailNotifications: true,
  desktopNotifications: false
})

// 初始化用户信息
const initUserInfo = () => {
  if (userStore.user) {
    Object.assign(userForm, {
      avatar: userStore.user.avatar || '',
      nickname: userStore.user.nickname || '',
      email: userStore.user.email || '',
      bio: userStore.user.bio || '',
      website: userStore.user.website || '',
      github: userStore.user.github || ''
    })
  }
}

// 加载安全设置
const loadSecuritySettings = async () => {
  try {
    const response = await getSecuritySettings()
    if (response.code === 200) {
      preferences.emailNotifications = response.data.loginNotificationEnabled || false
      preferences.desktopNotifications = response.data.anomalyProtectionEnabled || false
    }
  } catch (error) {
    console.error('加载安全设置失败:', error)
  }
}

// 保存基本信息
const saveBasicInfo = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    saveLoading.value = true
    
    const updateData = {
      nickname: userForm.nickname,
      bio: userForm.bio,
      website: userForm.website,
      github: userForm.github
    }
    
    const response = await userApi.updateUser(userStore.user.id, updateData)
    if (response.code === 200) {
      userStore.updateUser(response.data)
      ElMessage.success('基本信息保存成功')
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存基本信息失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saveLoading.value = false
  }
}

// 重置基本信息
const resetBasicInfo = () => {
  initUserInfo()
  ElMessage.info('已重置为原始信息')
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.indexOf('image') === 0
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
const handleAvatarSuccess = async (response) => {
  avatarUploading.value = false
  
  if (response.code === 200) {
    const avatarUrl = response.data.fileUrl
    userForm.avatar = avatarUrl
    
    // 更新用户头像
    try {
      const updateResponse = await userApi.updateAvatar(userStore.user.id, avatarUrl)
      if (updateResponse.code === 200) {
        userStore.updateUser({ avatar: avatarUrl })
        ElMessage.success('头像上传成功')
      } else {
        ElMessage.error('头像更新失败')
      }
    } catch (error) {
      console.error('更新头像失败:', error)
      ElMessage.error('头像更新失败')
    }
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 头像上传失败
const handleAvatarError = () => {
  avatarUploading.value = false
  ElMessage.error('头像上传失败')
}

// 移除头像
const removeAvatar = async () => {
  try {
    const response = await userApi.updateAvatar(userStore.user.id, '')
    if (response.code === 200) {
      userForm.avatar = ''
      userStore.updateUser({ avatar: '' })
      ElMessage.success('头像已移除')
    } else {
      ElMessage.error('移除头像失败')
    }
  } catch (error) {
    console.error('移除头像失败:', error)
    ElMessage.error('移除头像失败')
  }
}

// 更新单个偏好设置
const updatePreference = async (key) => {
  try {
    const settings = {
      loginNotificationEnabled: preferences.emailNotifications,
      anomalyProtectionEnabled: preferences.desktopNotifications
    }
    
    const response = await updateSecuritySettings(settings)
    if (response.code === 200) {
      ElMessage.success('设置已更新')
    } else {
      ElMessage.error('设置更新失败')
      // 回滚设置
      preferences[key] = !preferences[key]
    }
  } catch (error) {
    console.error('更新偏好设置失败:', error)
    ElMessage.error('设置更新失败')
    // 回滚设置
    preferences[key] = !preferences[key]
  }
}

// 保存偏好设置
const savePreferences = async () => {
  try {
    preferencesLoading.value = true
    
    const settings = {
      loginNotificationEnabled: preferences.emailNotifications,
      anomalyProtectionEnabled: preferences.desktopNotifications
    }
    
    const response = await updateSecuritySettings(settings)
    if (response.code === 200) {
      ElMessage.success('偏好设置保存成功')
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存偏好设置失败:', error)
    ElMessage.error('保存失败')
  } finally {
    preferencesLoading.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true
    
    const response = await userApi.updatePassword(userStore.user.id, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    if (response.code === 200) {
      ElMessage.success('密码修改成功')
      resetPasswordForm()
    } else {
      ElMessage.error(response.message || '密码修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('密码修改失败')
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 页面加载时初始化
onMounted(() => {
  initUserInfo()
  loadSecuritySettings()
})
</script>

<style lang="scss" scoped>
.account-settings {
  min-height: calc(100vh - 140px);
  
  .settings-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 40px 0;
    margin-bottom: 30px;
    border-radius: 12px;
    
    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 30px;
      
      .title-section {
        .page-title {
          display: flex;
          align-items: center;
          gap: 15px;
          font-size: 32px;
          font-weight: 700;
          margin: 0 0 10px 0;
          
          .title-icon {
            font-size: 36px;
          }
        }
        
        .page-subtitle {
          font-size: 16px;
          opacity: 0.9;
          margin: 0;
        }
      }
    }
  }
  
  .settings-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 30px;
    
    .settings-grid {
      display: grid;
      gap: 30px;
      
      .settings-card {
        background: white;
        border-radius: 12px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        overflow: hidden;
        
        .card-header {
          padding: 25px 30px;
          background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
          border-bottom: 1px solid #e2e8f0;
          
          h3 {
            margin: 0 0 8px 0;
            font-size: 20px;
            font-weight: 600;
            color: #1e293b;
          }
          
          p {
            margin: 0;
            color: #64748b;
            font-size: 14px;
          }
        }
        
        .card-content {
          padding: 30px;
          
          .user-form {
            .avatar-upload {
              display: flex;
              align-items: center;
              gap: 20px;
              
              .avatar-preview {
                border: 3px solid #e2e8f0;
              }
              
              .upload-actions {
                display: flex;
                flex-direction: column;
                gap: 10px;
              }
            }
          }
          
          .preference-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px 0;
            border-bottom: 1px solid #f1f5f9;
            
            &:last-child {
              border-bottom: none;
            }
            
            .preference-info {
              h4 {
                margin: 0 0 5px 0;
                font-size: 16px;
                font-weight: 600;
                color: #1e293b;
              }
              
              p {
                margin: 0;
                color: #64748b;
                font-size: 14px;
              }
            }
            
            .theme-options {
              display: flex;
              gap: 15px;
            }
          }
          
          .preference-actions {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #f1f5f9;
          }
          
          .status-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            
            .status-item {
              display: flex;
              align-items: center;
              gap: 15px;
              padding: 20px;
              background: #f8fafc;
              border-radius: 8px;
              
              .status-icon {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 20px;
                
                &.success {
                  background: #dcfce7;
                  color: #16a34a;
                }
                
                &.warning {
                  background: #fef3c7;
                  color: #d97706;
                }
                
                &.info {
                  background: #dbeafe;
                  color: #2563eb;
                }
              }
              
              .status-info {
                h4 {
                  margin: 0 0 5px 0;
                  font-size: 14px;
                  font-weight: 600;
                  color: #1e293b;
                }
                
                p {
                  margin: 0 0 5px 0;
                  color: #64748b;
                  font-size: 13px;
                }
              }
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .account-settings {
    min-height: calc(100vh - 120px);
    
    .settings-header {
      margin: -24px -24px 20px -24px;
      border-radius: 0;
      
      .header-content {
        padding: 0 24px;
        
        .title-section .page-title {
          font-size: 24px;
          
          .title-icon {
            font-size: 28px;
          }
        }
      }
    }
    
    .settings-content {
      padding: 0;
      
      .settings-grid .settings-card {
        .card-content {
          padding: 20px;
          
          .preference-item {
            flex-direction: column;
            align-items: flex-start;
            gap: 15px;
            
            .theme-options {
              flex-direction: column;
              gap: 10px;
            }
          }
          
          .status-grid {
            grid-template-columns: 1fr;
          }
        }
      }
    }
  }
}
</style>