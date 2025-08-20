<template>
  <div class="contact-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>联系我们</h1>
        <p class="subtitle">有任何问题或建议，欢迎与我们联系</p>
      </div>

      <div class="contact-content">
        <!-- 联系方式 -->
        <div class="contact-info">
          <h2>联系方式</h2>
          <div class="contact-grid">
            <div 
              v-for="contact in contactMethods" 
              :key="contact.type"
              class="contact-item"
            >
              <div class="contact-icon">
                <el-icon :size="24">
                  <component :is="contact.icon" />
                </el-icon>
              </div>
              <div class="contact-details">
                <h3>{{ contact.title }}</h3>
                <p>{{ contact.value }}</p>
                <span class="contact-desc">{{ contact.description }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 联系表单 -->
        <div class="contact-form-section">
          <h2>发送消息</h2>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="80px"
            class="contact-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名" prop="name">
                  <el-input
                    v-model="form.name"
                    placeholder="请输入您的姓名"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="form.email"
                    placeholder="请输入您的邮箱"
                    type="email"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="主题" prop="subject">
              <el-input
                v-model="form.subject"
                placeholder="请输入消息主题"
              />
            </el-form-item>
            
            <el-form-item label="消息" prop="message">
              <el-input
                v-model="form.message"
                type="textarea"
                :rows="6"
                placeholder="请输入您的消息内容"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                @click="handleSubmit"
                :loading="submitting"
                size="large"
              >
                发送消息
              </el-button>
              <el-button @click="handleReset" size="large">
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 常见问题 -->
        <div class="faq-section">
          <h2>常见问题</h2>
          <el-collapse v-model="activeNames">
            <el-collapse-item
              v-for="faq in faqs"
              :key="faq.id"
              :title="faq.question"
              :name="faq.id"
            >
              <div v-html="faq.answer"></div>
            </el-collapse-item>
          </el-collapse>
        </div>

        <!-- 工作时间 -->
        <div class="business-hours">
          <h2>工作时间</h2>
          <div class="hours-grid">
            <div class="hours-item">
              <h3>在线客服</h3>
              <p>周一至周五：9:00 - 18:00</p>
              <p>周六至周日：10:00 - 16:00</p>
            </div>
            <div class="hours-item">
              <h3>邮件回复</h3>
              <p>24小时内回复</p>
              <p>节假日可能延迟</p>
            </div>
            <div class="hours-item">
              <h3>电话咨询</h3>
              <p>周一至周五：9:00 - 17:00</p>
              <p>紧急情况请发送邮件</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Message, 
  Phone, 
  Location, 
  Clock,
  User,
  ChatDotRound
} from '@element-plus/icons-vue'

const formRef = ref()
const submitting = ref(false)
const activeNames = ref(['1'])

// 联系方式
const contactMethods = ref([
  {
    type: 'email',
    title: '邮箱联系',
    value: 'contact@myblog.com',
    description: '我们会在24小时内回复您的邮件',
    icon: Message
  },
  {
    type: 'phone',
    title: '电话咨询',
    value: '+86 138-0000-0000',
    description: '工作时间内可直接拨打电话',
    icon: Phone
  },
  {
    type: 'address',
    title: '办公地址',
    value: '北京市朝阳区某某大厦',
    description: '欢迎预约后到访交流',
    icon: Location
  },
  {
    type: 'wechat',
    title: '微信客服',
    value: 'myblog_service',
    description: '添加微信获得更快回复',
    icon: ChatDotRound
  }
])

// 表单数据
const form = reactive({
  name: '',
  email: '',
  subject: '',
  message: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  subject: [
    { required: true, message: '请输入消息主题', trigger: 'blur' },
    { min: 5, max: 100, message: '主题长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  message: [
    { required: true, message: '请输入消息内容', trigger: 'blur' },
    { min: 10, max: 1000, message: '消息内容长度在 10 到 1000 个字符', trigger: 'blur' }
  ]
}

// 常见问题
const faqs = ref([
  {
    id: '1',
    question: '如何注册账号？',
    answer: '点击页面右上角的"注册"按钮，填写必要信息即可完成注册。注册后您可以发表评论、收藏文章等。'
  },
  {
    id: '2',
    question: '忘记密码怎么办？',
    answer: '在登录页面点击"忘记密码"，输入您的邮箱地址，我们会发送重置密码的链接到您的邮箱。'
  },
  {
    id: '3',
    question: '如何投稿？',
    answer: '目前我们暂不接受外部投稿，但您可以通过联系我们的方式提交您的文章建议或合作意向。'
  },
  {
    id: '4',
    question: '网站更新频率如何？',
    answer: '我们会定期更新博客内容，通常每周发布2-3篇新文章。您可以订阅我们的RSS或关注我们的社交媒体获取最新更新。'
  },
  {
    id: '5',
    question: '如何举报不当内容？',
    answer: '如果您发现任何不当内容，请通过邮件或联系表单向我们举报，我们会在24小时内处理。'
  }
])

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('消息发送成功！我们会尽快回复您。')
    handleReset()
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    name: '',
    email: '',
    subject: '',
    message: ''
  })
}
</script>

<style lang="scss" scoped>
.contact-page {
  min-height: calc(100vh - 140px);
  padding: 40px 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.page-header {
  text-align: center;
  margin-bottom: 60px;
  
  h1 {
    font-size: 2.5rem;
    color: var(--el-text-color-primary);
    margin-bottom: 16px;
    font-weight: 600;
  }
  
  .subtitle {
    font-size: 1.1rem;
    color: var(--el-text-color-secondary);
    margin: 0;
  }
}

.contact-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  gap: 60px;
}

.contact-info {
  h2 {
    font-size: 1.8rem;
    margin-bottom: 30px;
    color: var(--el-text-color-primary);
    text-align: center;
  }
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.contact-item {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: flex-start;
  gap: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }
  
  .contact-icon {
    background: var(--el-color-primary);
    color: white;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  
  .contact-details {
    h3 {
      font-size: 1.2rem;
      margin-bottom: 8px;
      color: var(--el-text-color-primary);
    }
    
    p {
      font-size: 1rem;
      margin-bottom: 8px;
      color: var(--el-text-color-primary);
      font-weight: 500;
    }
    
    .contact-desc {
      font-size: 0.9rem;
      color: var(--el-text-color-secondary);
    }
  }
}

.contact-form-section {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  
  h2 {
    font-size: 1.8rem;
    margin-bottom: 30px;
    color: var(--el-text-color-primary);
    text-align: center;
  }
}

.contact-form {
  max-width: 800px;
  margin: 0 auto;
}

.faq-section {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  
  h2 {
    font-size: 1.8rem;
    margin-bottom: 30px;
    color: var(--el-text-color-primary);
    text-align: center;
  }
}

.business-hours {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  
  h2 {
    font-size: 1.8rem;
    margin-bottom: 30px;
    color: var(--el-text-color-primary);
    text-align: center;
  }
}

.hours-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.hours-item {
  text-align: center;
  padding: 20px;
  border: 2px solid var(--el-border-color-lighter);
  border-radius: 8px;
  transition: border-color 0.3s ease;
  
  &:hover {
    border-color: var(--el-color-primary);
  }
  
  h3 {
    font-size: 1.2rem;
    margin-bottom: 15px;
    color: var(--el-color-primary);
  }
  
  p {
    margin: 8px 0;
    color: var(--el-text-color-regular);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .contact-page {
    padding: 20px 0;
  }
  
  .page-header {
    margin-bottom: 40px;
    
    h1 {
      font-size: 2rem;
    }
  }
  
  .contact-content {
    gap: 40px;
  }
  
  .contact-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-item {
    padding: 20px;
  }
  
  .contact-form-section,
  .faq-section,
  .business-hours {
    padding: 20px;
  }
  
  .hours-grid {
    grid-template-columns: 1fr;
  }
}
</style>