<template>
  <div class="login-page">
    <form @submit.prevent="handleSubmit" class="login-form">
      <!-- 登录表单内容 -->
      <div class="form-content">
        <!-- 输入框区域 -->
        <div class="input-section">
          <!-- 用户名/邮箱输入框 -->
          <div class="input-group" :class="{ 'focused': usernameEmailFocused, 'error': errors.usernameOrEmail }">
            <div class="input-wrapper">
              <div class="input-icon">
                <ForestIcons name="leaf" :size="20" color="#2E8B57" />
              </div>
              <input
                v-model="form.usernameOrEmail"
                type="text"
                placeholder="用户名或邮箱"
                @focus="usernameEmailFocused = true"
                @blur="usernameEmailFocused = false"
                :class="{ 'error': errors.usernameOrEmail }"
              />
              <div class="focus-line"></div>
            </div>
            <div v-if="errors.usernameOrEmail" class="error-message">
              <div class="error-icon">⚠</div>
              {{ errors.usernameOrEmail }}
            </div>
          </div>

          <!-- 密码输入框 -->
          <div class="input-group" :class="{ 'focused': passwordFocused, 'error': errors.password }">
            <div class="input-wrapper">
              <div class="input-icon">
                <ForestIcons name="tree-lock" :size="20" color="#2E8B57" />
              </div>
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="密码"
                @focus="passwordFocused = true"
                @blur="passwordFocused = false"
                :class="{ 'error': errors.password }"
              />
              <button
                type="button"
                class="password-toggle"
                @click="showPassword = !showPassword"
              >
                <ForestIcons v-if="!showPassword" name="sapling" :size="20" color="#2E8B57" />
                <ForestIcons v-else name="forest" :size="20" color="#2E8B57" />
              </button>
              <div class="focus-line"></div>
            </div>
            <div v-if="errors.password" class="error-message">
              <div class="error-icon">⚠</div>
              {{ errors.password }}
            </div>
          </div>
        </div>

        <!-- 辅助功能区域 -->
        <div class="form-helpers">
          <!-- 记住我开关 -->
          <label class="remember-me">
            <input type="checkbox" v-model="form.rememberMe" />
            <span class="custom-checkbox"></span>
            <span class="remember-text">记住我</span>
          </label>

          <!-- 忘记密码链接 -->
          <router-link to="/auth/forgot-password" class="forgot-password">
            迷雾中迷路？
            <div class="firefly-container">
              <div class="firefly" v-for="i in 3" :key="i"></div>
            </div>
          </router-link>
        </div>

        <!-- 登录按钮 -->
        <button 
          type="submit" 
          class="login-button" 
          :class="{ 'loading': loading }"
          :disabled="loading"
          @click="createRipple"
        >
          <span v-if="!loading" class="button-text forest-text">登录字节森林</span>
          <div v-else class="button-loading">
            <div class="vine-loader"></div>
            <span>登录中...</span>
          </div>
          <div class="button-ripple" ref="rippleRef"></div>
        </button>

        <!-- 社交登录 -->
        <div class="social-login">
          <div class="divider">
            <span>或使用其他方式登录</span>
          </div>
          <div class="social-buttons">
            <!-- WeChat -->
            <button type="button" class="social-button wechat" @click="loginWithWeChat">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <!-- 大气泡 -->
                <circle cx="10" cy="11" r="6" fill="currentColor" opacity="0.85" />
                <circle cx="8.5" cy="10" r="0.8" fill="#fff" />
                <circle cx="11.5" cy="10" r="0.8" fill="#fff" />
                <path d="M7 16 L8.2 14.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
                <!-- 小气泡 -->
                <circle cx="16" cy="13" r="4.2" fill="currentColor" opacity="0.9" />
                <circle cx="15" cy="12.5" r="0.7" fill="#fff" />
                <circle cx="17" cy="12.5" r="0.7" fill="#fff" />
                <path d="M14.2 16.6 L15 15.4" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" />
              </svg>
            </button>
            
            <!-- QQ -->
            <button type="button" class="social-button qq" @click="loginWithQQ">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <!-- 企鹅简化形状 -->
                <circle cx="12" cy="7.5" r="3.2" fill="currentColor" />
                <ellipse cx="12" cy="13.5" rx="5" ry="4.8" fill="currentColor" />
                <ellipse cx="9.2" cy="13.2" rx="1.2" ry="2" fill="currentColor" opacity="0.8" />
                <ellipse cx="14.8" cy="13.2" rx="1.2" ry="2" fill="currentColor" opacity="0.8" />
                <ellipse cx="10.5" cy="18" rx="1.6" ry="0.8" fill="#fff" opacity="0.9" />
                <ellipse cx="13.5" cy="18" rx="1.6" ry="0.8" fill="#fff" opacity="0.9" />
              </svg>
            </button>
            
            <!-- Phone -->
            <button type="button" class="social-button phone" @click="loginWithPhone">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M17 2H7C5.895 2 5 2.895 5 4V20C5 21.105 5.895 22 7 22H17C18.105 22 19 21.105 19 20V4C19 2.895 18.105 2 17 2Z" stroke="currentColor" stroke-width="1.6"/>
                <rect x="9" y="4.8" width="6" height="0.8" fill="currentColor"/>
                <circle cx="12" cy="19" r="0.9" fill="currentColor"/>
              </svg>
            </button>
            
            <!-- GitHub -->
            <button type="button" class="social-button github" @click="loginWithGithub">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <!-- 简化的 GitHub 标记：圆形 + 猫耳轮廓 -->
                <circle cx="12" cy="12" r="9" fill="currentColor" opacity="0.15" />
                <path d="M8.5 10 C8.5 7.8 10.2 6 12 6 C13.8 6 15.5 7.8 15.5 10 V12.2 C16.3 12.9 16.8 14 16.8 15.2 C16.8 17.4 14.8 18.5 12 18.5 C9.2 18.5 7.2 17.4 7.2 15.2 C7.2 14 7.7 12.9 8.5 12.2 V10 Z" fill="currentColor" />
                <path d="M10 7 L11.5 5.8 L12 6.4 L12.5 5.8 L14 7" stroke="currentColor" stroke-width="1.2" fill="none" stroke-linecap="round" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 注册链接 -->
        <div class="register-link">
          <span>还没有账户？</span>
          <router-link to="/auth/register" class="register-vine">
            加入森林
            <div class="vine-growth"></div>
          </router-link>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import ForestIcons from '@/components/ForestIcons.vue'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const form = reactive({
  usernameOrEmail: '',
  password: '',
  rememberMe: false
})

const errors = reactive({
  usernameOrEmail: '',
  password: ''
})

const loading = ref(false)
const showPassword = ref(false)
const usernameEmailFocused = ref(false)
const passwordFocused = ref(false)
const rippleRef = ref(null)

// 表单验证
const validateForm = () => {
  // 清空之前的错误
  errors.usernameOrEmail = ''
  errors.password = ''
  
  let isValid = true
  
  if (!form.usernameOrEmail.trim()) {
    errors.usernameOrEmail = '请输入用户名或邮箱'
    isValid = false
  }
  
  if (!form.password.trim()) {
    errors.password = '请输入密码'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = '密码至少6位字符'
    isValid = false
  }
  
  return isValid
}

// 处理登录提交
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }
  
  loading.value = true
  
  try {
    // 调用store的登录方法
    const response = await userStore.login({
      usernameOrEmail: form.usernameOrEmail,
      password: form.password
    })
    
    if (response.code === 200) {
      ElMessage.success('登录成功！欢迎回到字节森林')
      
      // 跳转到主页或之前的页面
      const redirect = router.currentRoute.value.query.redirect || '/'
      router.push(redirect)
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('登录失败，请检查网络连接')
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

// 社交登录方法
const loginWithGithub = () => {
  ElMessage.info('GitHub登录功能开发中...')
}



const loginWithWeChat = () => {
  ElMessage.info('微信登录功能开发中...')
}

const loginWithQQ = () => {
  ElMessage.info('QQ登录功能开发中...')
}

const loginWithPhone = () => {
  ElMessage.info('手机登录功能开发中...')
}
</script>

<style scoped lang="scss">
.login-page {
  width: 100%;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form {
  width: 100%;
  position: relative;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

// 输入框区域
.input-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 0 16px;
  height: 52px;
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

.password-toggle {
  background: none;
  border: none;
  color: rgba(46, 139, 87, 0.7);
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s ease;
  
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

// 错误消息
.error-message {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 12px;
  color: #FF8C42;
  animation: errorFloat 0.5s ease-out;
  
  .error-icon {
    font-size: 14px;
    animation: errorPulse 1s ease-in-out infinite;
  }
}

@keyframes errorFloat {
  0% {
    opacity: 0;
    transform: translateY(-10px);
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

// 辅助功能区域
.form-helpers {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8px 0;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
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
  
  .remember-text {
    font-size: 14px;
    color: #FFFFFF;
    font-weight: 500;
    transition: color 0.3s ease, text-shadow 0.3s ease;
  }
  
  input:checked ~ .remember-text {
    color: #78FFD6;
    text-shadow: 0 0 6px rgba(120, 255, 214, 0.6), 0 0 12px rgba(120, 255, 214, 0.4);
  }
}

.forgot-password {
  position: relative;
  color: #FFFFFF;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.3s ease;
  
  &:hover {
    color: #78FFD6;
    
    .firefly {
      animation: fireflyFloat 2s ease-in-out infinite;
    }
  }
}

.firefly-container {
  position: absolute;
  top: -5px;
  right: -20px;
  width: 30px;
  height: 20px;
  pointer-events: none;
}

.firefly {
  position: absolute;
  width: 3px;
  height: 3px;
  background: #FFD700;
  border-radius: 50%;
  opacity: 0;
  
  &:nth-child(1) {
    top: 5px;
    left: 5px;
    animation-delay: 0s;
  }
  
  &:nth-child(2) {
    top: 12px;
    left: 15px;
    animation-delay: 0.7s;
  }
  
  &:nth-child(3) {
    top: 8px;
    left: 25px;
    animation-delay: 1.4s;
  }
}

@keyframes fireflyFloat {
  0%, 100% {
    opacity: 0;
    transform: translateY(0);
  }
  50% {
    opacity: 1;
    transform: translateY(-8px);
  }
}

// 登录按钮
.login-button {
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
    opacity: 0.7;
    cursor: not-allowed;
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
  background: rgba(255, 255, 255, 0.4);
  transform: scale(0);
  animation: ripple 0.6s linear;
  pointer-events: none;
  
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

// 社交登录
.social-login {
  margin-top: 8px;
}

.divider {
  position: relative;
  text-align: center;
  margin: 24px 0;
  
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, 
      transparent 0%, 
      rgba(74, 124, 89, 0.3) 20%, 
      rgba(74, 124, 89, 0.5) 50%, 
      rgba(74, 124, 89, 0.3) 80%, 
      transparent 100%
    );
  }
  
  span {
    background: rgba(255, 255, 255, 0.08);
    padding: 0 16px;
    font-size: 12px;
    color: #FFFFFF; /* 文字改成白色 */
    backdrop-filter: blur(4px);
    border-radius: 12px;
    border: 1px solid rgba(255, 255, 255, 0.25);
  }
}

.social-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.social-button {
  width: 48px;
  height: 48px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(8px);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: conic-gradient(from 0deg, transparent 0deg, transparent 340deg, currentColor 360deg);
    opacity: 0;
    transition: opacity 0.3s ease;
    border-radius: 50%;
    animation: treeRingRotate 3s linear infinite;
    animation-play-state: paused;
  }
  
  &:hover {
    transform: translateY(-2px);
    border-color: rgba(120, 255, 214, 0.4);
    background: rgba(255, 255, 255, 0.12);
    
    &::before {
      opacity: 0.3;
      animation-play-state: running;
    }
  }
  
  &.github {
    color: #4A7C59;
    &:hover {
      color: #78FFD6;
      box-shadow: 0 4px 20px rgba(120, 255, 214, 0.3);
    }
  }
  
  &.wechat {
    color: #1AAD19;
    &:hover {
      color: #2BD64A;
      box-shadow: 0 4px 20px rgba(43, 214, 74, 0.3);
    }
  }
  
  &.qq {
    color: #12B7F5;
    &:hover {
      color: #3CCBFF;
      box-shadow: 0 4px 20px rgba(60, 203, 255, 0.3);
    }
  }
  
  &.phone {
    color: #4A7C59;
    &:hover {
      color: #78FFD6;
      box-shadow: 0 4px 20px rgba(120, 255, 214, 0.3);
    }
  }
}
  
  svg {
    width: 20px;
    height: 20px;
    position: relative;
    z-index: 1;
  }

@keyframes treeRingRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

// 注册链接
.register-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #FFFFFF;
  
  span {
    margin-right: 8px;
  }
}

.register-vine {
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
  .form-helpers {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .social-buttons {
    gap: 16px;
  }
  
  .social-button {
    width: 52px;
    height: 52px;
  }
}
</style>