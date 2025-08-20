<template>
  <div class="security-center">
    <div class="security-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <el-icon class="title-icon"><Lock /></el-icon>
            安全中心
          </h1>
          <p class="page-subtitle">保护您的账户安全，管理登录和验证设置</p>
        </div>
      </div>
    </div>

    <div class="security-content">
      <div class="security-grid">
        <!-- 密码管理 -->
        <div class="security-card">
          <div class="card-header">
            <h3>密码管理</h3>
            <p>定期更新密码以保护账户安全</p>
          </div>
          <div class="card-content">
            <el-form :model="passwordForm" label-width="120px" class="password-form">
              <el-form-item label="当前密码">
                <el-input 
                  v-model="passwordForm.currentPassword" 
                  type="password" 
                  placeholder="请输入当前密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="新密码">
                <el-input 
                  v-model="passwordForm.newPassword" 
                  type="password" 
                  placeholder="请输入新密码"
                  show-password
                />
                <div class="password-strength">
                  <div class="strength-bar">
                    <div class="strength-fill" :class="passwordStrength.level"></div>
                  </div>
                  <span class="strength-text">{{ passwordStrength.text }}</span>
                </div>
              </el-form-item>
              
              <el-form-item label="确认新密码">
                <el-input 
                  v-model="passwordForm.confirmPassword" 
                  type="password" 
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword" :loading="loading">更新密码</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 两步验证 -->
        <div class="security-card">
          <div class="card-header">
            <h3>两步验证</h3>
            <p>为您的账户添加额外的安全保护</p>
          </div>
          <div class="card-content">
            <div class="two-factor-status">
              <div class="status-info">
                <div class="status-icon" :class="twoFactorEnabled ? 'enabled' : 'disabled'">
                  <el-icon v-if="twoFactorEnabled"><CircleCheckFilled /></el-icon>
                  <el-icon v-else><CircleCloseFilled /></el-icon>
                </div>
                <div class="status-text">
                  <h4>{{ twoFactorEnabled ? '已启用' : '未启用' }}</h4>
                  <p>{{ twoFactorEnabled ? '您的账户已受到两步验证保护' : '启用两步验证以增强账户安全' }}</p>
                </div>
              </div>
              <el-button 
                :type="twoFactorEnabled ? 'danger' : 'primary'" 
                @click="(event) => toggleTwoFactor(event)"
                :loading="loading"
              >
                {{ twoFactorEnabled ? '禁用' : '启用' }}
              </el-button>
            </div>
            
            <div v-if="!twoFactorEnabled" class="two-factor-options">
              <h4>选择验证方式</h4>
              <div class="option-list">
                <div class="option-item" @click="setupAuthenticator">
                  <div class="option-icon">
                    <el-icon><Cellphone /></el-icon>
                  </div>
                  <div class="option-info">
                    <h5>身份验证器应用</h5>
                    <p>使用 Google Authenticator 或类似应用</p>
                  </div>
                  <el-icon class="arrow"><ArrowRight /></el-icon>
                </div>
                
                <div class="option-item" @click="setupSMS">
                  <div class="option-icon">
                    <el-icon><Message /></el-icon>
                  </div>
                  <div class="option-info">
                    <h5>短信验证</h5>
                    <p>通过手机短信接收验证码</p>
                  </div>
                  <el-icon class="arrow"><ArrowRight /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 登录活动 -->
        <div class="security-card">
          <div class="card-header">
            <h3>登录活动</h3>
            <p>查看最近的登录记录和设备信息</p>
          </div>
          <div class="card-content">
            <div class="activity-list">
              <div 
                v-for="activity in loginActivities" 
                :key="activity.id" 
                class="activity-item"
              >
                <div class="activity-icon">
                  <el-icon><Monitor /></el-icon>
                </div>
                <div class="activity-info">
                  <div class="activity-header">
                    <h4>{{ activity.device }}</h4>
                    <span class="activity-time">{{ activity.time }}</span>
                  </div>
                  <p class="activity-location">{{ activity.location }}</p>
                  <p class="activity-ip">IP: {{ activity.ip }}</p>
                </div>
                <div class="activity-status">
                  <el-tag :type="activity.current ? 'success' : 'info'" size="small">
                    {{ activity.current ? '当前会话' : '已结束' }}
                  </el-tag>
                  <el-button 
                    v-if="activity.current && activity.sessionId" 
                    type="danger" 
                    size="small" 
                    @click="endSpecificSession(activity.sessionId)"
                    :loading="loading"
                    style="margin-left: 10px;"
                  >
                    结束会话
                  </el-button>
                </div>
              </div>
            </div>
            
            <div class="activity-actions">
              <el-button @click="refreshActivities" :loading="loading">刷新记录</el-button>
              <el-button type="danger" @click="(event) => logoutAllDevices(event)" :loading="loading">注销所有设备</el-button>
            </div>
          </div>
        </div>

        <!-- 安全设置 -->
        <div class="security-card">
          <div class="card-header">
            <h3>安全设置</h3>
            <p>配置其他安全相关选项</p>
          </div>
          <div class="card-content">
            <div class="security-options">
              <div class="option-item">
                <div class="option-info">
                  <h4>登录通知</h4>
                  <p>新设备登录时发送邮件通知</p>
                </div>
                <el-switch v-model="securitySettings.loginNotification" />
              </div>
              
              <div class="option-item">
                <div class="option-info">
                  <h4>异常登录保护</h4>
                  <p>检测到异常登录时要求额外验证</p>
                </div>
                <el-switch v-model="securitySettings.anomalyProtection" />
              </div>
              
              <div class="option-item">
                <div class="option-info">
                  <h4>会话超时</h4>
                  <p>设置自动登出时间</p>
                </div>
                <el-select v-model="securitySettings.sessionTimeout" placeholder="选择超时时间">
                  <el-option label="30分钟" value="30" />
                  <el-option label="1小时" value="60" />
                  <el-option label="2小时" value="120" />
                  <el-option label="4小时" value="240" />
                  <el-option label="8小时" value="480" />
                </el-select>
              </div>
            </div>
            
            <div class="security-actions">
              <el-button type="primary" @click="saveSecuritySettings" :loading="loading">保存设置</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Lock, 
  CircleCheckFilled, 
  CircleCloseFilled, 
  Cellphone, 
  Message, 
  ArrowRight, 
  Monitor 
} from '@element-plus/icons-vue'
import { showWarningConfirm } from '@/utils/positionedConfirm'
import {
  getSecurityOverview,
  getSecuritySettings,
  updateSecuritySettings,
  getLoginActivities,
  toggleTwoFactor as toggleTwoFactorAPI,
  endSession,
  endAllOtherSessions,
  changePassword
} from '@/api/security'

// 密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码强度计算
const passwordStrength = computed(() => {
  const password = passwordForm.newPassword
  if (!password) return { level: '', text: '' }
  
  let score = 0
  if (password.length >= 8) score++
  if (/[a-z]/.test(password)) score++
  if (/[A-Z]/.test(password)) score++
  if (/[0-9]/.test(password)) score++
  if (/[^A-Za-z0-9]/.test(password)) score++
  
  const levels = [
    { level: 'weak', text: '弱' },
    { level: 'fair', text: '一般' },
    { level: 'good', text: '良好' },
    { level: 'strong', text: '强' },
    { level: 'very-strong', text: '很强' }
  ]
  
  return levels[Math.min(score - 1, 4)] || levels[0]
})

// 加载状态
const loading = ref(false)

// 两步验证状态
const twoFactorEnabled = ref(false)

// 登录活动记录
const loginActivities = ref([])

// 安全设置
const securitySettings = reactive({
  loginNotification: false,
  anomalyProtection: false,
  sessionTimeout: '120'
})

// 安全概览数据
const securityOverview = ref({
  totalLogins: 0,
  failedAttempts: 0,
  activeSessions: 0,
  lastLogin: null
})

// 加载安全概览数据
const loadSecurityOverview = async () => {
  console.log('=== 开始加载安全概览 ===')
  try {
    console.log('调用 getSecurityOverview API')
    console.log('请求URL将是:', '/api/security/overview')
    const response = await getSecurityOverview()
    console.log('getSecurityOverview 响应:', response)
    if (response.code === 200) {
      securityOverview.value = response.data
      console.log('安全概览数据加载成功:', response.data)
    }
  } catch (error) {
    console.error('加载安全概览失败:', error)
    console.error('错误详情:', {
      message: error.message,
      response: error.response,
      status: error.response?.status,
      data: error.response?.data
    })
  }
}

// 加载安全设置
const loadSecuritySettings = async () => {
  console.log('=== 开始加载安全设置 ===')
  try {
    console.log('调用 getSecuritySettings API')
    const response = await getSecuritySettings()
    console.log('getSecuritySettings 响应:', response)
    if (response.code === 200) {
      const settings = response.data
      Object.assign(securitySettings, {
        loginNotification: settings.loginNotificationEnabled,
        anomalyProtection: settings.anomalyProtectionEnabled,
        sessionTimeout: settings.sessionTimeoutMinutes.toString()
      })
      twoFactorEnabled.value = settings.twoFactorEnabled
      console.log('安全设置数据加载成功:', settings)
    }
  } catch (error) {
    console.error('加载安全设置失败:', error)
    console.error('错误详情:', error.message, error.stack)
    ElMessage.error('加载安全设置失败')
  }
}

// 加载登录活动记录
const loadLoginActivities = async () => {
  try {
    const response = await getLoginActivities()
    if (response.code === 200) {
      loginActivities.value = response.data.map(activity => ({
        id: activity.id,
        device: activity.deviceInfo || 'Unknown Device',
        location: activity.location || '未知位置',
        ip: activity.ipAddress,
        time: activity.loginTime,
        current: activity.isCurrentSession,
        sessionId: activity.sessionId
      }))
    }
  } catch (error) {
    console.error('加载登录活动失败:', error)
    ElMessage.error('加载登录活动失败')
  }
}

// 更改密码
const handleChangePassword = async () => {
  if (!passwordForm.currentPassword) {
    ElMessage.error('请输入当前密码')
    return
  }
  if (!passwordForm.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  try {
    loading.value = true
    const response = await changePassword({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })
    
    if (response.code === 200) {
      ElMessage.success('密码更新成功')
      Object.assign(passwordForm, {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      })
    } else {
      ElMessage.error(response.message || '密码更新失败')
    }
  } catch (error) {
    console.error('密码更新失败:', error)
    ElMessage.error('密码更新失败')
  } finally {
    loading.value = false
  }
}

// 切换两步验证
const toggleTwoFactor = async (event) => {
  if (twoFactorEnabled.value) {
    try {
      await showWarningConfirm('确定要禁用两步验证吗？这将降低您的账户安全性。', '确认禁用', event)
      
      loading.value = true
      const response = await toggleTwoFactorAPI({ enabled: false })
      
      if (response.code === 200) {
        twoFactorEnabled.value = false
        ElMessage.success('两步验证已禁用')
      } else {
        ElMessage.error(response.message || '禁用两步验证失败')
      }
    } catch (error) {
      if (error.message !== 'cancel') {
        console.error('禁用两步验证失败:', error)
        ElMessage.error('禁用两步验证失败')
      }
    } finally {
      loading.value = false
    }
  } else {
    ElMessage.info('请选择验证方式来启用两步验证')
  }
}

// 设置身份验证器
const setupAuthenticator = async () => {
  try {
    loading.value = true
    const response = await toggleTwoFactorAPI({ 
      enabled: true, 
      method: 'AUTHENTICATOR' 
    })
    
    if (response.code === 200) {
      twoFactorEnabled.value = true
      ElMessage.success('身份验证器已启用')
      // 这里可以显示二维码等设置步骤
    } else {
      ElMessage.error(response.message || '启用身份验证器失败')
    }
  } catch (error) {
    console.error('启用身份验证器失败:', error)
    ElMessage.error('启用身份验证器失败')
  } finally {
    loading.value = false
  }
}

// 设置短信验证
const setupSMS = async () => {
  try {
    loading.value = true
    const response = await toggleTwoFactorAPI({ 
      enabled: true, 
      method: 'SMS' 
    })
    
    if (response.code === 200) {
      twoFactorEnabled.value = true
      ElMessage.success('短信验证已启用')
    } else {
      ElMessage.error(response.message || '启用短信验证失败')
    }
  } catch (error) {
    console.error('启用短信验证失败:', error)
    ElMessage.error('启用短信验证失败')
  } finally {
    loading.value = false
  }
}

// 刷新活动记录
const refreshActivities = async () => {
  try {
    loading.value = true
    await loadLoginActivities()
    ElMessage.success('活动记录已刷新')
  } catch (error) {
    console.error('刷新活动记录失败:', error)
    ElMessage.error('刷新活动记录失败')
  } finally {
    loading.value = false
  }
}

// 注销所有设备
const logoutAllDevices = async (event) => {
  try {
    await showWarningConfirm('确定要注销所有设备吗？您需要重新登录。', '确认注销', event)
    
    loading.value = true
    const response = await endAllOtherSessions()
    
    if (response.code === 200) {
      ElMessage.success('已注销所有其他设备')
      await loadLoginActivities() // 刷新活动记录
    } else {
      ElMessage.error(response.message || '注销设备失败')
    }
  } catch (error) {
    if (error.message !== 'cancel') {
      console.error('注销设备失败:', error)
      ElMessage.error('注销设备失败')
    }
  } finally {
    loading.value = false
  }
}

// 结束指定会话
const endSpecificSession = async (sessionId) => {
  try {
    loading.value = true
    const response = await endSession(sessionId)
    
    if (response.code === 200) {
      ElMessage.success('会话已结束')
      await loadLoginActivities() // 刷新活动记录
    } else {
      ElMessage.error(response.message || '结束会话失败')
    }
  } catch (error) {
    console.error('结束会话失败:', error)
    ElMessage.error('结束会话失败')
  } finally {
    loading.value = false
  }
}

// 保存安全设置
const saveSecuritySettings = async () => {
  try {
    loading.value = true
    const response = await updateSecuritySettings({
      loginNotificationEnabled: securitySettings.loginNotification,
      anomalyProtectionEnabled: securitySettings.anomalyProtection,
      sessionTimeoutMinutes: parseInt(securitySettings.sessionTimeout)
    })
    
    if (response.code === 200) {
      ElMessage.success('安全设置保存成功')
    } else {
      ElMessage.error(response.message || '保存安全设置失败')
    }
  } catch (error) {
    console.error('保存安全设置失败:', error)
    ElMessage.error('保存安全设置失败')
  } finally {
    loading.value = false
  }
}

// 初始化数据
const initData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadSecurityOverview(),
      loadSecuritySettings(),
      loadLoginActivities()
    ])
  } catch (error) {
    console.error('初始化数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  initData()
})
</script>

<style lang="scss" scoped>
.security-center {
  min-height: calc(100vh - 140px);
  
  .security-header {
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
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
  
  .security-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 30px;
    
    .security-grid {
      display: grid;
      gap: 30px;
      
      .security-card {
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
          
          .password-form {
            .password-strength {
              margin-top: 8px;
              display: flex;
              align-items: center;
              gap: 10px;
              
              .strength-bar {
                flex: 1;
                height: 4px;
                background: #e2e8f0;
                border-radius: 2px;
                overflow: hidden;
                
                .strength-fill {
                  height: 100%;
                  transition: all 0.3s ease;
                  
                  &.weak { width: 20%; background: #ef4444; }
                  &.fair { width: 40%; background: #f97316; }
                  &.good { width: 60%; background: #eab308; }
                  &.strong { width: 80%; background: #22c55e; }
                  &.very-strong { width: 100%; background: #16a34a; }
                }
              }
              
              .strength-text {
                font-size: 12px;
                color: #64748b;
                min-width: 30px;
              }
            }
          }
          
          .two-factor-status {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px;
            background: #f8fafc;
            border-radius: 8px;
            margin-bottom: 20px;
            
            .status-info {
              display: flex;
              align-items: center;
              gap: 15px;
              
              .status-icon {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 20px;
                
                &.enabled {
                  background: #dcfce7;
                  color: #16a34a;
                }
                
                &.disabled {
                  background: #fee2e2;
                  color: #dc2626;
                }
              }
              
              .status-text {
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
            }
          }
          
          .two-factor-options {
            h4 {
              margin: 0 0 15px 0;
              font-size: 16px;
              font-weight: 600;
              color: #1e293b;
            }
            
            .option-list {
              display: flex;
              flex-direction: column;
              gap: 10px;
              
              .option-item {
                display: flex;
                align-items: center;
                gap: 15px;
                padding: 15px;
                border: 1px solid #e2e8f0;
                border-radius: 8px;
                cursor: pointer;
                transition: all 0.3s ease;
                
                &:hover {
                  border-color: #6366f1;
                  background: #f8fafc;
                }
                
                .option-icon {
                  width: 40px;
                  height: 40px;
                  background: #f1f5f9;
                  border-radius: 8px;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  color: #6366f1;
                }
                
                .option-info {
                  flex: 1;
                  
                  h5 {
                    margin: 0 0 5px 0;
                    font-size: 14px;
                    font-weight: 600;
                    color: #1e293b;
                  }
                  
                  p {
                    margin: 0;
                    color: #64748b;
                    font-size: 12px;
                  }
                }
                
                .arrow {
                  color: #94a3b8;
                }
              }
            }
          }
          
          .activity-list {
            display: flex;
            flex-direction: column;
            gap: 15px;
            margin-bottom: 20px;
            
            .activity-item {
              display: flex;
              align-items: center;
              gap: 15px;
              padding: 15px;
              background: #f8fafc;
              border-radius: 8px;
              
              .activity-icon {
                width: 40px;
                height: 40px;
                background: #e2e8f0;
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #64748b;
              }
              
              .activity-info {
                flex: 1;
                
                .activity-header {
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                  margin-bottom: 5px;
                  
                  h4 {
                    margin: 0;
                    font-size: 14px;
                    font-weight: 600;
                    color: #1e293b;
                  }
                  
                  .activity-time {
                    font-size: 12px;
                    color: #64748b;
                  }
                }
                
                .activity-location,
                .activity-ip {
                  margin: 0;
                  font-size: 12px;
                  color: #64748b;
                }
              }
              
              .activity-status {
                margin-left: auto;
              }
            }
          }
          
          .activity-actions,
          .security-actions {
            display: flex;
            gap: 10px;
            padding-top: 20px;
            border-top: 1px solid #f1f5f9;
          }
          
          .security-options {
            .option-item {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 20px 0;
              border-bottom: 1px solid #f1f5f9;
              
              &:last-child {
                border-bottom: none;
              }
              
              .option-info {
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
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .security-center {
    min-height: calc(100vh - 120px);
    
    .security-header {
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
    
    .security-content {
      padding: 0;
      
      .security-grid .security-card {
        .card-content {
          padding: 20px;
          
          .two-factor-status {
            flex-direction: column;
            align-items: flex-start;
            gap: 15px;
          }
          
          .activity-actions,
          .security-actions {
            flex-direction: column;
          }
        }
      }
    }
  }
}
</style>