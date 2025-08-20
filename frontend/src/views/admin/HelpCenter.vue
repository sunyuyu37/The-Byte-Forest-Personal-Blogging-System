<template>
  <div class="help-center">
    <div class="help-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <el-icon class="title-icon"><QuestionFilled /></el-icon>
            帮助中心
          </h1>
          <p class="page-subtitle">获取使用指南、常见问题解答和技术支持</p>
        </div>
        
        <!-- 搜索框 -->
        <div class="help-search">
          <el-input
            v-model="searchQuery"
            placeholder="搜索帮助内容..."
            size="large"
            class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>
    </div>

    <div class="help-content">
      <div class="help-grid">
        <!-- 快速导航 -->
        <div class="help-section">
          <h2 class="section-title">快速导航</h2>
          <div class="quick-nav">
            <div 
              v-for="nav in quickNavItems" 
              :key="nav.id"
              class="nav-item"
              @click="scrollToSection(nav.target)"
            >
              <div class="nav-icon">
                <el-icon><component :is="nav.icon" /></el-icon>
              </div>
              <div class="nav-info">
                <h4>{{ nav.title }}</h4>
                <p>{{ nav.description }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 常见问题 -->
        <div class="help-section" id="faq">
          <h2 class="section-title">常见问题</h2>
          <div class="faq-list">
            <el-collapse v-model="activeFAQ" accordion>
              <el-collapse-item 
                v-for="faq in faqItems" 
                :key="faq.id"
                :title="faq.question"
                :name="faq.id"
                class="faq-item"
              >
                <div class="faq-answer" v-html="faq.answer"></div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>

        <!-- 使用指南 -->
        <div class="help-section" id="guides">
          <h2 class="section-title">使用指南</h2>
          <div class="guide-grid">
            <div 
              v-for="guide in guideItems" 
              :key="guide.id"
              class="guide-card"
              @click="openGuide(guide)"
            >
              <div class="guide-icon">
                <el-icon><component :is="guide.icon" /></el-icon>
              </div>
              <div class="guide-content">
                <h4>{{ guide.title }}</h4>
                <p>{{ guide.description }}</p>
                <div class="guide-meta">
                  <span class="difficulty" :class="guide.difficulty">
                    {{ getDifficultyText(guide.difficulty) }}
                  </span>
                  <span class="duration">{{ guide.duration }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 联系支持 -->
        <div class="help-section" id="contact">
          <h2 class="section-title">联系支持</h2>
          <div class="contact-options">
            <div class="contact-card">
              <div class="contact-icon">
                <el-icon><Message /></el-icon>
              </div>
              <div class="contact-info">
                <h4>在线客服</h4>
                <p>工作日 9:00-18:00 在线为您服务</p>
                <el-button type="primary" @click="startChat">开始对话</el-button>
              </div>
            </div>
            
            <div class="contact-card">
              <div class="contact-icon">
                <el-icon><Phone /></el-icon>
              </div>
              <div class="contact-info">
                <h4>电话支持</h4>
                <p>400-123-4567</p>
                <p class="contact-time">工作日 9:00-18:00</p>
              </div>
            </div>
            
            <div class="contact-card">
              <div class="contact-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="contact-info">
                <h4>提交工单</h4>
                <p>详细描述您的问题，我们会尽快回复</p>
                <el-button @click="createTicket">创建工单</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 系统状态 -->
        <div class="help-section" id="status">
          <h2 class="section-title">系统状态</h2>
          <div class="status-overview">
            <div class="status-header">
              <div class="overall-status">
                <div class="status-indicator operational"></div>
                <span>所有系统运行正常</span>
              </div>
              <div class="last-updated">
                最后更新: {{ lastUpdated }}
              </div>
            </div>
            
            <div class="service-status">
              <div 
                v-for="service in systemServices" 
                :key="service.id"
                class="service-item"
              >
                <div class="service-info">
                  <h4>{{ service.name }}</h4>
                  <p>{{ service.description }}</p>
                </div>
                <div class="service-indicator">
                  <div class="status-dot" :class="service.status"></div>
                  <span>{{ getStatusText(service.status) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 反馈建议 -->
        <div class="help-section" id="feedback">
          <h2 class="section-title">反馈建议</h2>
          <div class="feedback-form">
            <el-form :model="feedbackForm" label-width="100px">
              <el-form-item label="反馈类型">
                <el-select v-model="feedbackForm.type" placeholder="请选择反馈类型">
                  <el-option label="功能建议" value="feature" />
                  <el-option label="问题反馈" value="bug" />
                  <el-option label="使用体验" value="experience" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="标题">
                <el-input v-model="feedbackForm.title" placeholder="请输入反馈标题" />
              </el-form-item>
              
              <el-form-item label="详细描述">
                <el-input 
                  v-model="feedbackForm.content" 
                  type="textarea" 
                  :rows="6"
                  placeholder="请详细描述您的反馈内容..."
                />
              </el-form-item>
              
              <el-form-item label="联系方式">
                <el-input v-model="feedbackForm.contact" placeholder="邮箱或手机号（可选）" />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="submitFeedback">提交反馈</el-button>
                <el-button @click="resetFeedback">重置</el-button>
              </el-form-item>
            </el-form>
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
  QuestionFilled, 
  Search, 
  Document, 
  VideoPlay, 
  Setting, 
  Lock, 
  Message, 
  Phone, 
  ChatDotRound,
  TrendCharts,
  User,
  Bell
} from '@element-plus/icons-vue'

// 搜索查询
const searchQuery = ref('')

// 活跃的FAQ项
const activeFAQ = ref('')

// 快速导航项
const quickNavItems = ref([
  {
    id: 1,
    title: '常见问题',
    description: '查看最常遇到的问题和解答',
    icon: QuestionFilled,
    target: 'faq'
  },
  {
    id: 2,
    title: '使用指南',
    description: '详细的功能使用教程',
    icon: Document,
    target: 'guides'
  },
  {
    id: 3,
    title: '联系支持',
    description: '获取人工客服帮助',
    icon: Message,
    target: 'contact'
  },
  {
    id: 4,
    title: '系统状态',
    description: '查看系统运行状态',
    icon: TrendCharts,
    target: 'status'
  }
])

// FAQ项目
const faqItems = ref([
  {
    id: '1',
    question: '如何重置密码？',
    answer: '您可以通过以下步骤重置密码：<br>1. 点击登录页面的"忘记密码"链接<br>2. 输入您的邮箱地址<br>3. 查收重置密码邮件<br>4. 点击邮件中的链接设置新密码'
  },
  {
    id: '2',
    question: '如何修改个人信息？',
    answer: '进入账户设置页面，您可以修改昵称、邮箱、手机号等个人信息。修改后请点击保存按钮确认更改。'
  },
  {
    id: '3',
    question: '如何启用两步验证？',
    answer: '在安全中心页面，找到两步验证选项，选择验证方式（短信或身份验证器应用），按照提示完成设置即可。'
  },
  {
    id: '4',
    question: '文章发布后可以修改吗？',
    answer: '是的，您可以随时编辑已发布的文章。在文章管理页面找到对应文章，点击编辑按钮即可修改内容。'
  },
  {
    id: '5',
    question: '如何管理评论？',
    answer: '在评论管理页面，您可以查看、审核、回复或删除评论。支持批量操作和筛选功能。'
  }
])

// 使用指南项目
const guideItems = ref([
  {
    id: 1,
    title: '快速入门指南',
    description: '了解系统基本功能和操作流程',
    icon: Document,
    difficulty: 'beginner',
    duration: '10分钟'
  },
  {
    id: 2,
    title: '文章管理教程',
    description: '学习如何创建、编辑和管理文章',
    icon: Document,
    difficulty: 'beginner',
    duration: '15分钟'
  },
  {
    id: 3,
    title: '用户权限设置',
    description: '配置用户角色和权限管理',
    icon: User,
    difficulty: 'intermediate',
    duration: '20分钟'
  },
  {
    id: 4,
    title: '安全设置指南',
    description: '保护账户安全的最佳实践',
    icon: Lock,
    difficulty: 'intermediate',
    duration: '25分钟'
  },
  {
    id: 5,
    title: '高级功能配置',
    description: '深入了解系统高级功能',
    icon: Setting,
    difficulty: 'advanced',
    duration: '30分钟'
  },
  {
    id: 6,
    title: '通知系统设置',
    description: '配置各种通知和提醒功能',
    icon: Bell,
    difficulty: 'intermediate',
    duration: '15分钟'
  }
])

// 系统服务状态
const systemServices = ref([
  {
    id: 1,
    name: 'Web服务',
    description: '网站主要功能服务',
    status: 'operational'
  },
  {
    id: 2,
    name: '数据库',
    description: '数据存储和查询服务',
    status: 'operational'
  },
  {
    id: 3,
    name: '文件存储',
    description: '图片和文件上传服务',
    status: 'operational'
  },
  {
    id: 4,
    name: '邮件服务',
    description: '邮件发送和通知服务',
    status: 'maintenance'
  }
])

// 最后更新时间
const lastUpdated = ref('2024-01-20 15:30:00')

// 反馈表单
const feedbackForm = reactive({
  type: '',
  title: '',
  content: '',
  contact: ''
})

// 滚动到指定区域
const scrollToSection = (target) => {
  const element = document.getElementById(target)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

// 打开指南
const openGuide = (guide) => {
  ElMessage.info(`正在打开《${guide.title}》指南...`)
}

// 获取难度文本
const getDifficultyText = (difficulty) => {
  const map = {
    beginner: '入门',
    intermediate: '中级',
    advanced: '高级'
  }
  return map[difficulty] || '未知'
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    operational: '正常',
    maintenance: '维护中',
    degraded: '性能下降',
    outage: '故障'
  }
  return map[status] || '未知'
}

// 开始聊天
const startChat = () => {
  ElMessage.info('在线客服功能开发中...')
}

// 创建工单
const createTicket = () => {
  ElMessage.info('工单系统功能开发中...')
}

// 提交反馈
const submitFeedback = () => {
  if (!feedbackForm.type || !feedbackForm.title || !feedbackForm.content) {
    ElMessage.error('请填写完整的反馈信息')
    return
  }
  
  ElMessage.success('反馈提交成功，感谢您的建议！')
  resetFeedback()
}

// 重置反馈表单
const resetFeedback = () => {
  Object.assign(feedbackForm, {
    type: '',
    title: '',
    content: '',
    contact: ''
  })
}
</script>

<style lang="scss" scoped>
.help-center {
  min-height: calc(100vh - 140px);
  
  .help-header {
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    color: white;
    padding: 40px 0;
    margin-bottom: 30px;
    border-radius: 12px;
    
    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 30px;
      
      .title-section {
        margin-bottom: 30px;
        
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
      
      .help-search {
        max-width: 500px;
        
        .search-input {
          :deep(.el-input__wrapper) {
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
            
            .el-input__inner {
              color: white;
              
              &::placeholder {
                color: rgba(255, 255, 255, 0.7);
              }
            }
            
            .el-input__prefix {
              color: rgba(255, 255, 255, 0.8);
            }
          }
        }
      }
    }
  }
  
  .help-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 30px;
    
    .help-grid {
      display: flex;
      flex-direction: column;
      gap: 40px;
      
      .help-section {
        .section-title {
          font-size: 24px;
          font-weight: 600;
          color: #1e293b;
          margin: 0 0 20px 0;
          padding-bottom: 10px;
          border-bottom: 2px solid #e2e8f0;
        }
        
        .quick-nav {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
          gap: 20px;
          
          .nav-item {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 20px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
            cursor: pointer;
            transition: all 0.3s ease;
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
            }
            
            .nav-icon {
              width: 50px;
              height: 50px;
              background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
              border-radius: 12px;
              display: flex;
              align-items: center;
              justify-content: center;
              color: white;
              font-size: 24px;
            }
            
            .nav-info {
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
        
        .faq-list {
          background: white;
          border-radius: 12px;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
          overflow: hidden;
          
          .faq-item {
            :deep(.el-collapse-item__header) {
              padding: 20px 25px;
              font-weight: 600;
              color: #1e293b;
              border-bottom: 1px solid #f1f5f9;
            }
            
            :deep(.el-collapse-item__content) {
              padding: 0 25px 20px 25px;
            }
            
            .faq-answer {
              color: #64748b;
              line-height: 1.6;
            }
          }
        }
        
        .guide-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
          gap: 20px;
          
          .guide-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
            padding: 25px;
            cursor: pointer;
            transition: all 0.3s ease;
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
            }
            
            .guide-icon {
              width: 60px;
              height: 60px;
              background: linear-gradient(135deg, #10b981 0%, #059669 100%);
              border-radius: 12px;
              display: flex;
              align-items: center;
              justify-content: center;
              color: white;
              font-size: 28px;
              margin-bottom: 15px;
            }
            
            .guide-content {
              h4 {
                margin: 0 0 10px 0;
                font-size: 18px;
                font-weight: 600;
                color: #1e293b;
              }
              
              p {
                margin: 0 0 15px 0;
                color: #64748b;
                line-height: 1.5;
              }
              
              .guide-meta {
                display: flex;
                gap: 15px;
                
                .difficulty {
                  padding: 4px 8px;
                  border-radius: 4px;
                  font-size: 12px;
                  font-weight: 500;
                  
                  &.beginner {
                    background: #dcfce7;
                    color: #16a34a;
                  }
                  
                  &.intermediate {
                    background: #fef3c7;
                    color: #d97706;
                  }
                  
                  &.advanced {
                    background: #fee2e2;
                    color: #dc2626;
                  }
                }
                
                .duration {
                  color: #64748b;
                  font-size: 12px;
                }
              }
            }
          }
        }
        
        .contact-options {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
          gap: 20px;
          
          .contact-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
            padding: 25px;
            text-align: center;
            
            .contact-icon {
              width: 60px;
              height: 60px;
              background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
              border-radius: 12px;
              display: flex;
              align-items: center;
              justify-content: center;
              color: white;
              font-size: 28px;
              margin: 0 auto 15px auto;
            }
            
            .contact-info {
              h4 {
                margin: 0 0 10px 0;
                font-size: 18px;
                font-weight: 600;
                color: #1e293b;
              }
              
              p {
                margin: 0 0 5px 0;
                color: #64748b;
                
                &.contact-time {
                  font-size: 12px;
                  margin-bottom: 15px;
                }
              }
            }
          }
        }
        
        .status-overview {
          background: white;
          border-radius: 12px;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
          padding: 25px;
          
          .status-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #f1f5f9;
            
            .overall-status {
              display: flex;
              align-items: center;
              gap: 10px;
              font-weight: 600;
              color: #1e293b;
              
              .status-indicator {
                width: 12px;
                height: 12px;
                border-radius: 50%;
                
                &.operational {
                  background: #16a34a;
                }
              }
            }
            
            .last-updated {
              color: #64748b;
              font-size: 14px;
            }
          }
          
          .service-status {
            .service-item {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 15px 0;
              border-bottom: 1px solid #f8fafc;
              
              &:last-child {
                border-bottom: none;
              }
              
              .service-info {
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
              
              .service-indicator {
                display: flex;
                align-items: center;
                gap: 8px;
                
                .status-dot {
                  width: 8px;
                  height: 8px;
                  border-radius: 50%;
                  
                  &.operational {
                    background: #16a34a;
                  }
                  
                  &.maintenance {
                    background: #d97706;
                  }
                  
                  &.degraded {
                    background: #dc2626;
                  }
                  
                  &.outage {
                    background: #991b1b;
                  }
                }
                
                span {
                  font-size: 14px;
                  color: #64748b;
                }
              }
            }
          }
        }
        
        .feedback-form {
          background: white;
          border-radius: 12px;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
          padding: 30px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .help-center {
    min-height: calc(100vh - 120px);
    
    .help-header {
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
    
    .help-content {
      padding: 0;
      
      .help-grid .help-section {
        .quick-nav {
          grid-template-columns: 1fr;
        }
        
        .guide-grid {
          grid-template-columns: 1fr;
        }
        
        .contact-options {
          grid-template-columns: 1fr;
        }
        
        .status-overview .status-header {
          flex-direction: column;
          align-items: flex-start;
          gap: 10px;
        }
      }
    }
  }
}
</style>