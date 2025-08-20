<template>
  <div class="about-page">
    <div class="container">
      <!-- 个人介绍卡片 -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <el-avatar :size="120" :src="profile.avatar">
              {{ profile.name.charAt(0) }}
            </el-avatar>
          </div>
          <div class="info-section">
            <h1 class="name">{{ profile.name }}</h1>
            <p class="title">{{ profile.title }}</p>
            <p class="description">{{ profile.description }}</p>
            
            <div class="social-links">
              <el-button
                v-for="link in profile.socialLinks"
                :key="link.name"
                :icon="link.icon"
                circle
                @click="openLink(link.url)"
              />
            </div>
          </div>
        </div>
        
        <div class="profile-stats">
          <div class="stat-item">
            <div class="stat-number">{{ stats.articleCount }}</div>
            <div class="stat-label">文章</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.viewCount }}</div>
            <div class="stat-label">阅读</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.likeCount }}</div>
            <div class="stat-label">点赞</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.commentCount }}</div>
            <div class="stat-label">评论</div>
          </div>
        </div>
      </div>
      
      <!-- 技能栈 -->
      <div class="skills-section">
        <h2>技能栈</h2>
        <div class="skills-grid">
          <div
            v-for="skill in skills"
            :key="skill.name"
            class="skill-item"
          >
            <div class="skill-icon">
              <el-icon :size="40">
                <component :is="skill.icon" />
              </el-icon>
            </div>
            <h3 class="skill-name">{{ skill.name }}</h3>
            <p class="skill-description">{{ skill.description }}</p>
            <div class="skill-level">
              <el-progress
                :percentage="skill.level"
                :color="getSkillColor(skill.level)"
                :show-text="false"
              />
            </div>
          </div>
        </div>
      </div>
      
      <!-- 项目经历 -->
      <div class="projects-section">
        <h2>项目经历</h2>
        <div class="projects-timeline">
          <div
            v-for="project in projects"
            :key="project.id"
            class="project-item"
          >
            <div class="project-date">{{ project.date }}</div>
            <div class="project-content">
              <h3 class="project-title">{{ project.title }}</h3>
              <p class="project-description">{{ project.description }}</p>
              <div class="project-tech">
                <el-tag
                  v-for="tech in project.technologies"
                  :key="tech"
                  size="small"
                  type="info"
                >
                  {{ tech }}
                </el-tag>
              </div>
              <div v-if="project.link" class="project-link">
                <el-button
                  type="primary"
                  size="small"
                  @click="openLink(project.link)"
                >
                  查看项目
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 联系方式 -->
      <div class="contact-section">
        <h2>联系我</h2>
        <div class="contact-grid">
          <div
            v-for="contact in contacts"
            :key="contact.type"
            class="contact-item"
          >
            <div class="contact-icon">
              <el-icon :size="24">
                <component :is="contact.icon" />
              </el-icon>
            </div>
            <div class="contact-info">
              <div class="contact-label">{{ contact.label }}</div>
              <div class="contact-value">{{ contact.value }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 博客统计 -->
      <div class="blog-stats">
        <h2>博客统计</h2>
        <div class="stats-grid">
          <div class="stats-chart">
            <h3>文章发布趋势</h3>
            <!-- 这里可以集成图表库如 ECharts -->
            <div class="chart-placeholder">
              <el-icon :size="60"><TrendCharts /></el-icon>
              <p>图表数据加载中...</p>
            </div>
          </div>
          <div class="stats-summary">
            <h3>统计摘要</h3>
            <div class="summary-list">
              <div class="summary-item">
                <span class="label">建站时间：</span>
                <span class="value">{{ blogInfo.createDate }}</span>
              </div>
              <div class="summary-item">
                <span class="label">文章总数：</span>
                <span class="value">{{ stats.articleCount }} 篇</span>
              </div>
              <div class="summary-item">
                <span class="label">总访问量：</span>
                <span class="value">{{ formatNumber(stats.viewCount) }}</span>
              </div>
              <div class="summary-item">
                <span class="label">评论总数：</span>
                <span class="value">{{ stats.commentCount }} 条</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  User,
  Message,
  Phone,
  Location,
  Link,
  TrendCharts
} from '@element-plus/icons-vue'

const profile = ref({
  name: '孙玉玉',
  title: '字节森林 - 全栈开发工程师',
  description: '字节森林是一个现代化的个人博客系统，提供文章发布、分类管理、标签系统、用户评论、后台管理等完整功能。系统采用前后端分离架构，致力于为用户提供优雅的写作和阅读体验。',
  avatar: '',
  socialLinks: [
    { name: 'GitHub', icon: Link, url: 'https://github.com' },
    { name: 'Email', icon: Message, url: 'mailto:sunyuyu@email.com' },
    { name: 'Blog', icon: Link, url: 'https://blog.sunyuyu.com' }
  ]
})

const stats = ref({
  articleCount: 45,
  viewCount: 12580,
  likeCount: 892,
  commentCount: 156
})

const skills = ref([
  {
    name: 'Vue.js',
    description: '现代前端框架，熟练掌握 Vue 2/3',
    level: 90,
    icon: 'Document'
  },
  {
    name: 'JavaScript',
    description: 'ES6+，TypeScript，Node.js',
    level: 85,
    icon: 'Document'
  },
  {
    name: 'Java',
    description: 'Spring Boot，Spring Cloud 微服务',
    level: 80,
    icon: 'Document'
  },
  {
    name: 'Database',
    description: 'MySQL，Redis，MongoDB',
    level: 75,
    icon: 'Document'
  },
  {
    name: 'DevOps',
    description: 'Docker，Kubernetes，CI/CD',
    level: 70,
    icon: 'Document'
  },
  {
    name: 'Cloud',
    description: 'AWS，阿里云，腾讯云',
    level: 65,
    icon: 'Document'
  }
])

const projects = ref([
  {
    id: 1,
    title: '字节森林博客系统',
    description: '字节森林是一个功能完整的现代化博客平台，包含文章发布与编辑、分类与标签管理、用户评论互动、后台管理系统、响应式设计等核心功能。采用Vue 3 + Element Plus前端框架，Spring Boot后端架构，提供流畅的用户体验和强大的内容管理能力。',
    date: '2024.01',
    technologies: ['Vue 3', 'Element Plus', 'Spring Boot', 'MySQL', 'Redis'],
    link: 'https://github.com/sunyuyu/bytewood-blog'
  },
  {
    id: 2,
    title: '电商管理平台',
    description: '企业级电商后台管理系统，包含商品管理、订单处理、用户管理等模块。',
    date: '2023.10',
    technologies: ['React', 'Ant Design', 'Java', 'PostgreSQL'],
    link: 'https://github.com/example/ecommerce'
  },
  {
    id: 3,
    title: '微服务架构实践',
    description: '基于 Spring Cloud 的微服务架构项目，实现了服务注册发现、配置中心、网关等。',
    date: '2023.06',
    technologies: ['Spring Cloud', 'Docker', 'Kubernetes', 'Consul']
  }
])

const contacts = ref([
  {
    type: 'email',
    label: '邮箱',
    value: 'sunyuyu@email.com',
    icon: Message
  },
  {
    type: 'phone',
    label: '电话',
    value: '+86 138-0000-0000',
    icon: Phone
  },
  {
    type: 'location',
    label: '位置',
    value: '中国',
    icon: Location
  },
  {
    type: 'website',
    label: '字节森林博客',
    value: 'https://bytewood.blog',
    icon: Link
  }
])

const blogInfo = ref({
  createDate: '2024年1月'
})

// 获取技能等级颜色
const getSkillColor = (level) => {
  if (level >= 80) return '#67c23a'
  if (level >= 60) return '#e6a23c'
  return '#f56c6c'
}

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 打开链接
const openLink = (url) => {
  window.open(url, '_blank')
}

onMounted(() => {
  // 这里可以加载实际的数据
})
</script>

<style lang="scss" scoped>
.about-page {
  min-height: 100vh;
  background: var(--el-bg-color-page);
  padding: 20px 0;
}

.profile-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  
  .profile-header {
    display: flex;
    gap: 30px;
    margin-bottom: 30px;
    
    .avatar-section {
      flex-shrink: 0;
    }
    
    .info-section {
      flex: 1;
      
      .name {
        font-size: 2.5rem;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin-bottom: 10px;
      }
      
      .title {
        font-size: 1.2rem;
        color: var(--el-color-primary);
        margin-bottom: 15px;
        font-weight: 500;
      }
      
      .description {
        color: var(--el-text-color-regular);
        line-height: 1.6;
        margin-bottom: 20px;
      }
      
      .social-links {
        display: flex;
        gap: 10px;
      }
    }
  }
  
  .profile-stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    padding-top: 20px;
    border-top: 1px solid var(--el-border-color-lighter);
    
    .stat-item {
      text-align: center;
      
      .stat-number {
        font-size: 2rem;
        font-weight: 600;
        color: var(--el-color-primary);
        margin-bottom: 5px;
      }
      
      .stat-label {
        color: var(--el-text-color-secondary);
        font-size: 14px;
      }
    }
  }
}

.skills-section,
.projects-section,
.contact-section,
.blog-stats {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  
  h2 {
    font-size: 1.5rem;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin-bottom: 25px;
    padding-bottom: 10px;
    border-bottom: 2px solid var(--el-color-primary);
  }
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  
  .skill-item {
    padding: 20px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    text-align: center;
    transition: all 0.3s ease;
    
    &:hover {
      border-color: var(--el-color-primary);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    .skill-icon {
      color: var(--el-color-primary);
      margin-bottom: 15px;
    }
    
    .skill-name {
      font-size: 1.1rem;
      font-weight: 600;
      margin-bottom: 10px;
      color: var(--el-text-color-primary);
    }
    
    .skill-description {
      color: var(--el-text-color-secondary);
      margin-bottom: 15px;
      line-height: 1.5;
    }
    
    .skill-level {
      margin-top: 10px;
    }
  }
}

.projects-timeline {
  .project-item {
    display: flex;
    gap: 20px;
    margin-bottom: 30px;
    padding-bottom: 30px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    
    &:last-child {
      border-bottom: none;
      margin-bottom: 0;
      padding-bottom: 0;
    }
    
    .project-date {
      flex-shrink: 0;
      width: 80px;
      font-weight: 600;
      color: var(--el-color-primary);
    }
    
    .project-content {
      flex: 1;
      
      .project-title {
        font-size: 1.2rem;
        font-weight: 600;
        margin-bottom: 10px;
        color: var(--el-text-color-primary);
      }
      
      .project-description {
        color: var(--el-text-color-regular);
        line-height: 1.6;
        margin-bottom: 15px;
      }
      
      .project-tech {
        margin-bottom: 15px;
        
        .el-tag {
          margin-right: 8px;
          margin-bottom: 5px;
        }
      }
    }
  }
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  
  .contact-item {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 20px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    
    .contact-icon {
      color: var(--el-color-primary);
    }
    
    .contact-info {
      .contact-label {
        font-weight: 500;
        color: var(--el-text-color-primary);
        margin-bottom: 5px;
      }
      
      .contact-value {
        color: var(--el-text-color-secondary);
      }
    }
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  
  .stats-chart {
    .chart-placeholder {
      height: 300px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background: var(--el-bg-color);
      border-radius: 8px;
      color: var(--el-text-color-secondary);
    }
  }
  
  .stats-summary {
    .summary-list {
      .summary-item {
        display: flex;
        justify-content: space-between;
        padding: 15px 0;
        border-bottom: 1px solid var(--el-border-color-lighter);
        
        &:last-child {
          border-bottom: none;
        }
        
        .label {
          color: var(--el-text-color-secondary);
        }
        
        .value {
          font-weight: 500;
          color: var(--el-text-color-primary);
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-card {
    padding: 20px;
    
    .profile-header {
      flex-direction: column;
      text-align: center;
      gap: 20px;
      
      .info-section {
        .name {
          font-size: 2rem;
        }
      }
    }
    
    .profile-stats {
      grid-template-columns: repeat(2, 1fr);
    }
  }
  
  .skills-grid {
    grid-template-columns: 1fr;
  }
  
  .projects-timeline {
    .project-item {
      flex-direction: column;
      gap: 10px;
    }
  }
  
  .contact-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .skills-section,
  .projects-section,
  .contact-section,
  .blog-stats {
    padding: 20px;
  }
}
</style>