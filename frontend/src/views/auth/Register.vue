<template>
  <div class="register-page">
    <form @submit.prevent="handleSubmit" class="register-form">
      <!-- 注册表单内容 -->
      <div class="form-content">
        <!-- 输入框区域 -->
        <div class="input-section">
          <!-- 用户名输入框 -->
          <div class="input-group" :class="{ 'focused': usernameFocused, 'error': errors.username, 'success': validations.username }">
            <div class="input-wrapper">
              <div class="input-icon">
                <ForestIcons name="leaf" color="currentColor" :size="20" />
              </div>
              <input
                v-model="form.username"
                type="text"
                placeholder="用户名"
                @focus="usernameFocused = true"
                @blur="usernameFocused = false; validateUsername()"
                :class="{ 'error': errors.username, 'success': validations.username }"
              />
              <div class="validation-icon" v-if="validations.username || errors.username">
                <svg v-if="validations.username" class="success-icon" width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <path d="M9 16.17l-3.59-3.58L4 14l5 5 12-12-1.41-1.42L9 16.17z" fill="#78FFD6"/>
                </svg>
                <svg v-else class="error-icon" width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" fill="#FF8C42"/>
                </svg>
              </div>
              <div class="focus-line"></div>
            </div>
            <div v-if="errors.username" class="error-message">
              <div class="error-icon">⚠</div>
              {{ errors.username }}
            </div>
            <div v-else-if="validations.username" class="success-message">
              <div class="success-icon">✓</div>
              用户名可用
            </div>
          </div>

          <!-- 昵称输入框 -->
          <div class="input-group" :class="{ 'focused': nicknameFocused, 'error': errors.nickname, 'success': validations.nickname }">
            <div class="input-wrapper">
              <div class="input-icon">
                <ForestIcons name="sapling" color="currentColor" :size="20" />
              </div>
              <input
                v-model="form.nickname"
                type="text"
                placeholder="昵称"
                @focus="nicknameFocused = true"
                @blur="nicknameFocused = false; validateNickname()"
                :class="{ 'error': errors.nickname, 'success': validations.nickname }"
              />
              <div class="validation-icon" v-if="validations.nickname || errors.nickname">
                <svg v-if="validations.nickname" class="success-icon" width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <path d="M9 16.17l-3.59-3.58L4 14l5 5 12-12-1.41-1.42L9 16.17z" fill="#78FFD6"/>
                </svg>
                <svg v-else class="error-icon" width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" fill="#FF8C42"/>
                </svg>
              </div>
              <div class="focus-line"></div>
            </div>
            <div v-if="errors.nickname" class="error-message">
              <div class="error-icon">⚠</div>
              {{ errors.nickname }}
            </div>
            <div v-else-if="validations.nickname" class="success-message">
              <div class="success-icon">✓</div>
              昵称可用
            </div>
          </div>

          <!-- 邮箱输入框 -->
          <div class="input-group" :class="{ 'focused': emailFocused, 'error': errors.email, 'success': validations.email }">
            <div class="input-wrapper">
              <div class="input-icon">
                <ForestIcons name="leaf" color="currentColor" :size="20" />
              </div>
              <input
                v-model="form.email"
                type="email"
                placeholder="邮箱地址"
                @focus="emailFocused = true"
                @blur="emailFocused = false; validateEmail()"
                :class="{ 'error': errors.email, 'success': validations.email }"
              />
              <div class="validation-icon" v-if="validations.email || errors.email">
                <svg v-if="validations.email" class="success-icon" width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <path d="M9 16.17l-3.59-3.58L4 14l5 5 12-12-1.41-1.42L9 16.17z" fill="#78FFD6"/>
                </svg>
                <svg v-else class="error-icon" width="16" height="16" viewBox="0 0 24 24" fill="none">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" fill="#FF8C42"/>
                </svg>
              </div>
              <div class="focus-line"></div>
            </div>
            <div v-if="errors.email" class="error-message">
              <div class="error-icon">⚠</div>
              {{ errors.email }}
            </div>
            <div v-else-if="validations.email" class="success-message">
              <div class="success-icon">✓</div>
              邮箱格式正确
            </div>
          </div>

          <!-- 密码输入框 -->
          <div class="input-group" :class="{ 'focused': passwordFocused, 'error': errors.password, 'success': validations.password }">
            <div class="input-wrapper">
              <div class="input-icon">
                <ForestIcons name="tree-lock" color="currentColor" :size="20" />
              </div>
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="密码"
                @focus="passwordFocused = true"
                @blur="passwordFocused = false; validatePassword()"
                :class="{ 'error': errors.password, 'success': validations.password }"
              />
              <button
                type="button"
                class="password-toggle"
                @click="showPassword = !showPassword"
              >
                <ForestIcons 
                  :name="showPassword ? 'forest' : 'leaf'" 
                  color="currentColor" 
                  :size="20" 
                />
              </button>
              <div class="focus-line"></div>
            </div>
            <div v-if="errors.password" class="error-message">
              <div class="error-icon">⚠</div>
              {{ errors.password }}
            </div>
            
            <!-- 密码强度指示器 -->
            <div class="password-strength" v-if="form.password">
              <div class="strength-label">密码强度：</div>
              <div class="strength-bar">
                <div 
                  class="strength-fill" 
                  :class="passwordStrength.class"
                  :style="{ width: passwordStrength.width }"
                ></div>
              </div>
              <div class="strength-text" :class="passwordStrength.class">
                {{ passwordStrength.text }}
              </div>
            </div>
          </div>

          <!-- 确认密码输入框 -->
          <div class="input-group" :class="{ 'focused': confirmPasswordFocused, 'error': errors.confirmPassword, 'success': validations.confirmPassword }">
            <div class="input-wrapper">
              <div class="input-icon">
                <ForestIcons name="tree-lock" color="currentColor" :size="20" />
              </div>
              <input
                v-model="form.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="确认密码"
                @focus="confirmPasswordFocused = true"
                @blur="confirmPasswordFocused = false; validateConfirmPassword()"
                :class="{ 'error': errors.confirmPassword, 'success': validations.confirmPassword }"
              />
              <button
                type="button"
                class="password-toggle"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <ForestIcons 
                  :name="showConfirmPassword ? 'forest' : 'leaf'" 
                  color="currentColor" 
                  :size="20" 
                />
              </button>
              <div class="focus-line"></div>
            </div>
            <div v-if="errors.confirmPassword" class="error-message">
              <div class="error-icon">⚠</div>
              {{ errors.confirmPassword }}
            </div>
            <div v-else-if="validations.confirmPassword" class="success-message">
              <div class="success-icon">✓</div>
              密码确认一致
            </div>
          </div>
        </div>

        <!-- 服务条款同意 -->
        <div class="terms-agreement">
          <label class="agreement-checkbox">
            <input type="checkbox" v-model="form.agreeToTerms" />
            <span class="custom-checkbox"></span>
            <span class="agreement-text">
              我已阅读并同意
              <router-link to="/terms" class="terms-link">《服务条款》</router-link>
              和
              <router-link to="/privacy" class="privacy-link">《隐私政策》</router-link>
            </span>
          </label>
          <div v-if="errors.agreeToTerms" class="error-message">
            <div class="error-icon">⚠</div>
            {{ errors.agreeToTerms }}
          </div>
        </div>

        <!-- 注册按钮 -->
        <button 
          type="submit" 
          class="register-button" 
          :class="{ 'loading': loading }"
          :disabled="loading || !form.agreeToTerms"
          @click="createRipple"
        >
          <span v-if="!loading" class="button-text forest-text">踏入字节森林</span>
          <div v-else class="button-loading">
            <div class="vine-loader"></div>
            <span>种植中...</span>
          </div>
          <div class="button-ripple" ref="rippleRef"></div>
        </button>

        <!-- 登录链接 -->
        <div class="login-link">
          <span>已经有账户了？</span>
          <router-link to="/auth/login" class="login-vine">
            返回森林
            <div class="vine-growth"></div>
          </router-link>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'
import ForestIcons from '@/components/ForestIcons.vue'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const form = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreeToTerms: false
})

const errors = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreeToTerms: ''
})

const validations = reactive({
  username: false,
  nickname: false,
  email: false,
  password: false,
  confirmPassword: false
})

const loading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const usernameFocused = ref(false)
const nicknameFocused = ref(false)
const emailFocused = ref(false)
const passwordFocused = ref(false)
const confirmPasswordFocused = ref(false)
const rippleRef = ref(null)

// 防抖延迟
const debounceTimeouts = reactive({
  username: null,
  email: null,
  nickname: null
})

// 防抖检查昵称
const debounceCheckNickname = (nickname) => {
  if (debounceTimeouts.nickname) {
    clearTimeout(debounceTimeouts.nickname)
  }
  debounceTimeouts.nickname = setTimeout(() => {
    if (nickname && nickname.trim().length >= 2) {
      // 暂无后端昵称唯一性检查接口，这里只做前端即时校验显示
      validations.nickname = true
      errors.nickname = ''
    }
  }, 400)
}

// 监听昵称变化
watch(() => form.nickname, (newValue) => {
  validations.nickname = false
  errors.nickname = ''
  if (newValue) {
    debounceCheckNickname(newValue)
  }
})

// 验证昵称
const validateNickname = () => {
  const value = form.nickname?.trim() || ''
  if (!value) {
    errors.nickname = '请输入昵称'
    validations.nickname = false
    return
  }
  if (value.length < 2) {
    errors.nickname = '昵称至少2个字符'
    validations.nickname = false
    return
  }
  if (value.length > 20) {
    errors.nickname = '昵称最多20个字符'
    validations.nickname = false
    return
  }
  // 可以包含中文、字母、数字、下划线、连字符
  if (!/^[\u4e00-\u9fa5\w-]+$/.test(value)) {
    errors.nickname = '昵称只能包含中文、字母、数字、下划线或连字符'
    validations.nickname = false
    return
  }
  errors.nickname = ''
  validations.nickname = true
}

// 密码强度计算
const passwordStrength = computed(() => {
  const password = form.password
  if (!password) return { width: '0%', class: '', text: '' }
  
  let score = 0
  let text = ''
  let className = ''
  
  // 长度检查
  if (password.length >= 8) score += 2
  else if (password.length >= 6) score += 1
  
  // 复杂度检查
  if (/[a-z]/.test(password)) score += 1
  if (/[A-Z]/.test(password)) score += 1
  if (/[0-9]/.test(password)) score += 1
  if (/[^A-Za-z0-9]/.test(password)) score += 1
  
  if (score <= 2) {
    text = '弱'
    className = 'weak'
    return { width: '33%', class: className, text }
  } else if (score <= 4) {
    text = '中等'
    className = 'medium'
    return { width: '66%', class: className, text }
  } else {
    text = '强'
    className = 'strong'
    return { width: '100%', class: className, text }
  }
})

// 防抖检查用户名
const debounceCheckUsername = (username) => {
  if (debounceTimeouts.username) {
    clearTimeout(debounceTimeouts.username)
  }
  
  debounceTimeouts.username = setTimeout(async () => {
    if (username && username.length >= 3) {
      try {
        const response = await authApi.checkUsername(username)
        if (response.data) {
          validations.username = true
          errors.username = ''
        } else {
          validations.username = false
          errors.username = '用户名已被使用'
        }
      } catch (error) {
        validations.username = false
        errors.username = '检查用户名失败'
      }
    }
  }, 500)
}

// 防抖检查邮箱
const debounceCheckEmail = (email) => {
  if (debounceTimeouts.email) {
    clearTimeout(debounceTimeouts.email)
  }
  
  debounceTimeouts.email = setTimeout(async () => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (email && emailRegex.test(email)) {
      try {
        const response = await authApi.checkEmail(email)
        if (response.data) {
          validations.email = true
          errors.email = ''
        } else {
          validations.email = false
          errors.email = '邮箱已被注册'
        }
      } catch (error) {
        validations.email = false
        errors.email = '检查邮箱失败'
      }
    }
  }, 500)
}

// 监听输入变化
watch(() => form.username, (newValue) => {
  validations.username = false
  errors.username = ''
  if (newValue) {
    debounceCheckUsername(newValue)
  }
})

watch(() => form.email, (newValue) => {
  validations.email = false
  errors.email = ''
  if (newValue) {
    debounceCheckEmail(newValue)
  }
})

watch(() => form.password, () => {
  validations.password = false
  errors.password = ''
  if (form.confirmPassword) {
    validateConfirmPassword()
  }
})

watch(() => form.confirmPassword, () => {
  validateConfirmPassword()
})

// 表单验证方法
const validateUsername = () => {
  if (!form.username.trim()) {
    errors.username = '请输入用户名'
    validations.username = false
  } else if (form.username.length < 3) {
    errors.username = '用户名至少3个字符'
    validations.username = false
  } else if (form.username.length > 20) {
    errors.username = '用户名最多20个字符'
    validations.username = false
  } else if (!/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(form.username)) {
    errors.username = '用户名只能包含字母、数字、下划线和中文'
    validations.username = false
  }
}

const validateEmail = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!form.email.trim()) {
    errors.email = '请输入邮箱地址'
    validations.email = false
  } else if (!emailRegex.test(form.email)) {
    errors.email = '邮箱格式不正确'
    validations.email = false
  }
}

const validatePassword = () => {
  if (!form.password) {
    errors.password = '请输入密码'
    validations.password = false
  } else if (form.password.length < 6) {
    errors.password = '密码至少6个字符'
    validations.password = false
  } else if (form.password.length > 50) {
    errors.password = '密码最多50个字符'
    validations.password = false
  } else {
    errors.password = ''
    validations.password = passwordStrength.value.class !== 'weak'
  }
}

const validateConfirmPassword = () => {
  if (!form.confirmPassword) {
    errors.confirmPassword = '请确认密码'
    validations.confirmPassword = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    validations.confirmPassword = false
  } else {
    errors.confirmPassword = ''
    validations.confirmPassword = true
  }
}

// 表单提交验证
const validateForm = () => {
  validateUsername()
  validateNickname()
  validateEmail()
  validatePassword()
  validateConfirmPassword()
  
  // 检查服务条款同意
  if (!form.agreeToTerms) {
    errors.agreeToTerms = '请同意服务条款和隐私政策'
  } else {
    errors.agreeToTerms = ''
  }
  
  // 检查是否所有验证都通过
  return !Object.values(errors).some(error => error) &&
         Object.values(validations).every(valid => valid) &&
         form.agreeToTerms
}

// 处理注册提交
const handleSubmit = async () => {
  if (!validateForm()) {
    ElMessage.warning('请填写完整且正确的信息')
    return
  }
  
  loading.value = true
  
  try {
    // 首次尝试：携带昵称注册（若后端未支持会在下方回退）
    let response
    try {
      response = await userStore.register({
        username: form.username,
        nickname: form.nickname,
        email: form.email,
        password: form.password
      })
    } catch (e) {
      // 如果后端因未知字段导致报错，则回退为不带昵称的注册请求
      const msg = e?.response?.data?.message || e?.message || ''
      const isUnknownField = /Unrecognized field|Unknown property|无法识别的字段|未知属性/.test(String(msg))
      if (e?.response?.status === 400 && isUnknownField) {
        response = await userStore.register({
          username: form.username,
          email: form.email,
          password: form.password
        })
        // 提示后端暂未支持昵称直传
        ElMessage.info('后端暂未支持注册时设置昵称，已先完成注册，您可在个人中心修改昵称')
      } else {
        throw e
      }
    }
    
    if (response.code === 200) {
      ElMessage.success('注册成功！欢迎加入字节森林')
      
      // 跳转到登录页面
      router.push('/auth/login')
    } else {
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error) {
    console.error('注册错误:', error)
    
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('注册失败，请检查网络连接')
    }
  } finally {
    loading.value = false
  }
}

// 按钮水波纹效果
const createRipple = (event) => {
  const button = event.currentTarget
  const ripple = rippleRef.value
  
  const rect = button.getBoundingClientRect()
  const size = Math.max(rect.width, rect.height)
  const x = event.clientX - rect.left - size / 2
  const y = event.clientY - rect.top - size / 2
  
  if (ripple) {
    ripple.style.width = ripple.style.height = size + 'px'
    ripple.style.left = x + 'px'
    ripple.style.top = y + 'px'
    ripple.classList.add('active')
    
    setTimeout(() => {
      ripple.classList.remove('active')
    }, 600)
  }
}
</script>

<style scoped lang="scss">
.register-page {
  width: 100%;
  min-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-form {
  width: 100%;
  position: relative;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

// 输入框区域
.input-section {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.input-group {
  position: relative;
  
  &.focused .input-wrapper {
    border-color: rgba(120, 255, 214, 0.6);
    box-shadow: 0 0 20px rgba(120, 255, 214, 0.2);
    
    .focus-line {
      width: 100%;
      opacity: 1;
    }
    
    .input-icon {
      color: #78FFD6;
    }
  }
  
  &.error .input-wrapper {
    border-color: rgba(255, 182, 119, 0.8);
    box-shadow: 0 0 20px rgba(255, 182, 119, 0.3);
  }
  
  &.success .input-wrapper {
    border-color: rgba(120, 255, 214, 0.6);
    box-shadow: 0 0 15px rgba(120, 255, 214, 0.2);
  }
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 0 16px;
  height: 48px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(8px);
  
  &:hover {
    border-color: rgba(120, 255, 214, 0.4);
    background: rgba(255, 255, 255, 0.12);
  }
  
  input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    font-size: 14px;
    color: white;
    margin-left: 12px;
    font-weight: 500;
    
    &::placeholder {
      color: rgba(255, 255, 255, 0.6);
    }
    
    &.error {
      color: #FF8C42;
    }
    
    &.success {
      color: #2E8B57;
    }
  }
}

.input-icon {
  display: flex;
  align-items: center;
  color: rgba(46, 139, 87, 0.7);
  transition: color 0.3s ease;
  
  svg {
    width: 20px;
    height: 20px;
  }
}

.validation-icon {
  display: flex;
  align-items: center;
  margin-left: 8px;
  
  .success-icon,
  .error-icon {
    animation: iconPop 0.3s ease-out;
  }
}

@keyframes iconPop {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.password-toggle {
  background: none;
  border: none;
  color: rgba(46, 139, 87, 0.7);
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s ease;
  margin-left: 8px;
  
  &:hover {
    color: #78FFD6;
    background: rgba(120, 255, 214, 0.1);
  }
  
  svg {
    width: 20px;
    height: 20px;
  }
}

.focus-line {
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #78FFD6 0%, #A8FF78 100%);
  border-radius: 1px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0;
}

// 消息样式
.error-message,
.success-message {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  font-size: 12px;
  animation: messageFloat 0.4s ease-out;
}

.error-message {
  color: #FF8C42;
  
  .error-icon {
    font-size: 14px;
    animation: errorPulse 1s ease-in-out infinite;
  }
}

.success-message {
  color: #78FFD6;
  
  .success-icon {
    font-size: 14px;
    animation: successGlow 1.5s ease-in-out infinite;
  }
}

@keyframes messageFloat {
  0% {
    opacity: 0;
    transform: translateY(-8px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes errorPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

@keyframes successGlow {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

// 密码强度指示器
.password-strength {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.strength-label {
  color: rgba(46, 139, 87, 0.8);
  white-space: nowrap;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
  
  &.weak {
    background: linear-gradient(90deg, #FF8C42 0%, #FF6B6B 100%);
  }
  
  &.medium {
    background: linear-gradient(90deg, #FFD700 0%, #FFA500 100%);
  }
  
  &.strong {
    background: linear-gradient(90deg, #78FFD6 0%, #A8FF78 100%);
  }
}

.strength-text {
  white-space: nowrap;
  font-weight: 600;
  
  &.weak { color: #FF8C42; }
  &.medium { color: #FFD700; }
  &.strong { color: #78FFD6; }
}

// 服务条款同意
.terms-agreement {
  margin: 12px 0;
}

.agreement-checkbox {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  cursor: pointer;
  user-select: none;
  
  input[type="checkbox"] {
    display: none;
  }
  
  .custom-checkbox {
    width: 18px;
    height: 18px;
    border: 2px solid rgba(46, 139, 87, 0.5);
    border-radius: 4px;
    position: relative;
    transition: all 0.3s ease;
    background: rgba(255, 255, 255, 0.1);
    flex-shrink: 0;
    margin-top: 2px;
    
    &::after {
      content: '';
      position: absolute;
      top: 2px;
      left: 6px;
      width: 4px;
      height: 8px;
      border: solid #78FFD6;
      border-width: 0 2px 2px 0;
      transform: rotate(45deg) scale(0);
      transition: transform 0.2s ease;
    }
  }
  
  input:checked + .custom-checkbox {
    background: linear-gradient(135deg, #78FFD6 0%, #A8FF78 100%);
    border-color: #78FFD6;
    
    &::after {
      transform: rotate(45deg) scale(1);
    }
  }
  
  .agreement-text {
    font-size: 13px;
    color: #FFFFFF;
    font-weight: 500;
    line-height: 1.4;
    
    .terms-link,
    .privacy-link {
      color: #78FFD6;
      text-decoration: none;
      transition: color 0.3s ease;
      
      &:hover {
        color: #A8FF78;
      }
    }
  }
}

// 注册按钮
.register-button {
  position: relative;
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, #78FFD6 0%, #A8FF78 100%);
  border: none;
  border-radius: 12px;
  color: #2E8B57;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  text-transform: none;
  margin-top: 8px;
  
  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 
      0 8px 25px rgba(120, 255, 214, 0.4),
      0 4px 12px rgba(168, 255, 120, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  }
  
  &:active:not(:disabled) {
    transform: translateY(-1px);
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    background: linear-gradient(135deg, rgba(120, 255, 214, 0.3) 0%, rgba(168, 255, 120, 0.3) 100%);
  }
  
  &.loading {
    background: linear-gradient(135deg, rgba(120, 255, 214, 0.8) 0%, rgba(168, 255, 120, 0.8) 100%);
  }
}

.button-text {
  position: relative;
  z-index: 2;
}

.button-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  
  span {
    font-size: 14px;
  }
}

.vine-loader {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(46, 139, 87, 0.3);
  border-top: 2px solid #2E8B57;
  border-radius: 50%;
  animation: vineGrow 1s linear infinite;
}

@keyframes vineGrow {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.button-ripple {
  position: absolute;
  border-radius: 50%;
  background: rgba(120, 255, 214, 0.4);
  pointer-events: none;
  transform: scale(0);
  animation: ripple 0.6s linear;
  
  &.active {
    animation: ripple 0.6s linear;
  }
}

@keyframes ripple {
  to {
    transform: scale(4);
    opacity: 0;
  }
}

// 登录链接
.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #FFFFFF;
  
  span {
    margin-right: 8px;
  }
}

.login-vine {
  position: relative;
  color: #78FFD6;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
  
  &:hover {
    color: #A8FF78;
    
    .vine-growth {
      animation: vineGrowth 1s ease-out forwards;
    }
  }
}

.vine-growth {
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #78FFD6 0%, #A8FF78 100%);
  border-radius: 1px;
}

@keyframes vineGrowth {
  0% {
    width: 0;
    opacity: 0.8;
  }
  50% {
    width: 100%;
    opacity: 1;
  }
  100% {
    width: 100%;
    opacity: 0.6;
  }
}

// 响应式设计
@media (max-width: 480px) {
  .input-section {
    gap: 16px;
  }
  
  .input-wrapper {
    height: 50px;
  }
  
  .register-button {
    height: 54px;
    font-size: 16px;
  }
  
  .agreement-text {
    font-size: 12px;
  }
}
</style>