<template>
  <div class="about-page">
    <div class="container">
      <!-- 个人介绍卡片 -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <el-avatar :size="120" :src="profile.avatar" class="animated-avatar">
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
  Link
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
    date: '2025.07',
    technologies: ['Vue 3', 'Element Plus', 'Spring Boot', 'MySQL', 'Redis'],
    link: 'https://github.com/sunyuyu/bytewood-blog'
  }
])

const contacts = ref([
  {
    type: 'email',
    label: '邮箱',
    value: 'sunyuyuku@foxmail.com',
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



// 获取技能等级颜色
const getSkillColor = (level) => {
  if (level >= 80) return '#67c23a'
  if (level >= 60) return '#e6a23c'
  return '#f56c6c'
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
  background: linear-gradient(rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.3)), url('/about.png');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  padding: 20px 0;
  position: relative;
  animation: fadeIn 1s ease-out;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(74, 144, 226, 0.1), rgba(80, 200, 120, 0.1));
    pointer-events: none;
    animation: gradientShift 8s ease-in-out infinite;
  }
}

.profile-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 2;
  animation: slideInUp 0.8s ease-out 0.2s both;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  }
  
  .profile-header {
    display: flex;
    gap: 30px;
    margin-bottom: 30px;
    
    .avatar-section {
      flex-shrink: 0;
      
      .animated-avatar {
        animation: pulse 3s ease-in-out infinite;
        border: 3px solid rgba(255, 255, 255, 0.8);
        box-shadow: 0 0 20px rgba(74, 144, 226, 0.3);
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.1);
          box-shadow: 0 0 30px rgba(74, 144, 226, 0.5);
        }
      }
    }
    
    .info-section {
      flex: 1;
      
      .name {
        font-size: 2.5rem;
        font-weight: 600;
        background: linear-gradient(135deg, #4a90e2, #50c878, #e24a90);
        background-size: 200% 200%;
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        margin-bottom: 10px;
        animation: gradientText 3s ease-in-out infinite;
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
  

}

.skills-section,
.projects-section,
.contact-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 2;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  }
}

.skills-section {
  animation: slideInUp 0.8s ease-out 0.4s both;
}

.projects-section {
  animation: slideInUp 0.8s ease-out 0.6s both;
}

.contact-section {
  animation: slideInUp 0.8s ease-out 0.8s both;
  
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
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 12px;
    text-align: center;
    transition: all 0.3s ease;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(5px);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
      transition: left 0.5s ease;
    }
    
    &:hover {
      border-color: var(--el-color-primary);
      transform: translateY(-5px) scale(1.02);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
      background: rgba(255, 255, 255, 0.2);
      
      &::before {
        left: 100%;
      }
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
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(5px);
    transition: all 0.3s ease;
    cursor: pointer;
    
    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
      background: rgba(255, 255, 255, 0.2);
      border-color: var(--el-color-primary);
    }
    
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



// 动画关键帧
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes gradientShift {
  0%, 100% {
    background: linear-gradient(135deg, rgba(74, 144, 226, 0.1), rgba(80, 200, 120, 0.1));
  }
  50% {
    background: linear-gradient(135deg, rgba(80, 200, 120, 0.1), rgba(226, 74, 144, 0.1));
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes gradientText {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
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
  

  
  .skills-section,
  .projects-section,
  .contact-section {
    padding: 20px;
  }
}
</style>