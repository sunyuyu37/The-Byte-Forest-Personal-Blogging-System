<template>
  <div class="privacy-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>隐私政策</h1>
        <p class="subtitle">我们重视并保护您的个人隐私</p>
        <div class="update-info">
          <span>最后更新时间：{{ lastUpdated }}</span>
        </div>
      </div>

      <!-- 政策内容 -->
      <div class="privacy-content">
        <div class="content-nav">
          <h3>目录</h3>
          <ul>
            <li><a href="#section-1" @click="scrollToSection('section-1')">1. 信息收集</a></li>
            <li><a href="#section-2" @click="scrollToSection('section-2')">2. 信息使用</a></li>
            <li><a href="#section-3" @click="scrollToSection('section-3')">3. 信息共享</a></li>
            <li><a href="#section-4" @click="scrollToSection('section-4')">4. 信息安全</a></li>
            <li><a href="#section-5" @click="scrollToSection('section-5')">5. Cookie使用</a></li>
            <li><a href="#section-6" @click="scrollToSection('section-6')">6. 用户权利</a></li>
            <li><a href="#section-7" @click="scrollToSection('section-7')">7. 政策更新</a></li>
            <li><a href="#section-8" @click="scrollToSection('section-8')">8. 联系我们</a></li>
          </ul>
        </div>

        <div class="content-main">
          <!-- 引言 -->
          <div class="section intro">
            <p class="intro-text">
              欢迎使用我们的博客网站。我们深知个人信息对您的重要性，并会尽全力保护您的个人信息安全可靠。
              我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：权责一致原则、目的明确原则、
              选择同意原则、最少够用原则、确保安全原则、主体参与原则、公开透明原则等。
            </p>
          </div>

          <!-- 第一部分：信息收集 -->
          <div id="section-1" class="section">
            <h2>1. 我们收集的信息</h2>
            
            <h3>1.1 您主动提供的信息</h3>
            <ul>
              <li><strong>账户信息：</strong>当您注册账户时，我们会收集您的用户名、邮箱地址、密码等基本信息</li>
              <li><strong>个人资料：</strong>您可以选择提供头像、个人简介、联系方式等个人资料信息</li>
              <li><strong>内容信息：</strong>您发布的评论、反馈、联系表单等内容信息</li>
              <li><strong>通信信息：</strong>当您与我们联系时，我们可能会保存通信记录</li>
            </ul>

            <h3>1.2 自动收集的信息</h3>
            <ul>
              <li><strong>设备信息：</strong>设备型号、操作系统、浏览器类型和版本</li>
              <li><strong>访问信息：</strong>IP地址、访问时间、访问页面、停留时长</li>
              <li><strong>技术信息：</strong>Cookie、本地存储、会话信息</li>
            </ul>
          </div>

          <!-- 第二部分：信息使用 -->
          <div id="section-2" class="section">
            <h2>2. 我们如何使用您的信息</h2>
            
            <div class="usage-grid">
              <div class="usage-item">
                <h4>提供服务</h4>
                <p>为您提供博客浏览、评论、用户管理等基本功能</p>
              </div>
              <div class="usage-item">
                <h4>改善体验</h4>
                <p>分析用户行为，优化网站功能和用户体验</p>
              </div>
              <div class="usage-item">
                <h4>安全保护</h4>
                <p>检测和防范安全威胁，保护网站和用户安全</p>
              </div>
              <div class="usage-item">
                <h4>客户支持</h4>
                <p>回应您的咨询、投诉和技术支持请求</p>
              </div>
              <div class="usage-item">
                <h4>法律合规</h4>
                <p>遵守适用的法律法规和政府要求</p>
              </div>
              <div class="usage-item">
                <h4>通知推送</h4>
                <p>发送重要通知、更新信息和服务公告</p>
              </div>
            </div>
          </div>

          <!-- 第三部分：信息共享 -->
          <div id="section-3" class="section">
            <h2>3. 信息共享与披露</h2>
            
            <div class="sharing-policy">
              <div class="policy-item">
                <h4>我们不会出售您的个人信息</h4>
                <p>我们承诺不会向第三方出售、出租或交易您的个人信息。</p>
              </div>
              
              <div class="policy-item">
                <h4>有限的信息共享</h4>
                <p>在以下情况下，我们可能会共享您的信息：</p>
                <ul>
                  <li>获得您的明确同意</li>
                  <li>法律法规要求或政府部门要求</li>
                  <li>为保护我们或他人的合法权益</li>
                  <li>与可信的第三方服务提供商合作（如云存储服务）</li>
                </ul>
              </div>
            </div>
          </div>

          <!-- 第四部分：信息安全 -->
          <div id="section-4" class="section">
            <h2>4. 信息安全保护</h2>
            
            <div class="security-measures">
              <div class="measure-item">
                <el-icon class="measure-icon"><Lock /></el-icon>
                <h4>数据加密</h4>
                <p>使用SSL/TLS加密传输，确保数据传输安全</p>
              </div>
              <div class="measure-item">
                <el-icon class="measure-icon"><View /></el-icon>
                <h4>访问控制</h4>
                <p>严格限制数据访问权限，定期审查访问日志</p>
              </div>
              <div class="measure-item">
                <el-icon class="measure-icon"><Monitor /></el-icon>
                <h4>安全监控</h4>
                <p>24/7安全监控，及时发现和应对安全威胁</p>
              </div>
              <div class="measure-item">
                <el-icon class="measure-icon"><Document /></el-icon>
                <h4>数据备份</h4>
                <p>定期备份重要数据，确保数据可恢复性</p>
              </div>
            </div>
          </div>

          <!-- 第五部分：Cookie使用 -->
          <div id="section-5" class="section">
            <h2>5. Cookie和类似技术</h2>
            
            <div class="cookie-info">
              <h3>什么是Cookie</h3>
              <p>Cookie是存储在您设备上的小型文本文件，用于改善您的浏览体验。</p>
              
              <h3>我们如何使用Cookie</h3>
              <div class="cookie-types">
                <div class="cookie-type">
                  <h4>必要Cookie</h4>
                  <p>维持网站基本功能，如用户登录状态</p>
                </div>
                <div class="cookie-type">
                  <h4>功能Cookie</h4>
                  <p>记住您的偏好设置，如语言选择</p>
                </div>
                <div class="cookie-type">
                  <h4>分析Cookie</h4>
                  <p>帮助我们了解网站使用情况，改善服务</p>
                </div>
              </div>
              
              <h3>管理Cookie</h3>
              <p>您可以通过浏览器设置管理或删除Cookie。请注意，禁用某些Cookie可能会影响网站功能。</p>
            </div>
          </div>

          <!-- 第六部分：用户权利 -->
          <div id="section-6" class="section">
            <h2>6. 您的权利</h2>
            
            <div class="rights-grid">
              <div class="right-item">
                <h4>访问权</h4>
                <p>您有权了解我们收集、使用您个人信息的情况</p>
              </div>
              <div class="right-item">
                <h4>更正权</h4>
                <p>您有权要求我们更正或补充您的个人信息</p>
              </div>
              <div class="right-item">
                <h4>删除权</h4>
                <p>在特定情况下，您有权要求我们删除您的个人信息</p>
              </div>
              <div class="right-item">
                <h4>撤回同意</h4>
                <p>您有权随时撤回对个人信息处理的同意</p>
              </div>
              <div class="right-item">
                <h4>数据导出</h4>
                <p>您有权要求我们提供您的个人信息副本</p>
              </div>
              <div class="right-item">
                <h4>投诉举报</h4>
                <p>您有权向相关监管部门投诉举报</p>
              </div>
            </div>
          </div>

          <!-- 第七部分：政策更新 -->
          <div id="section-7" class="section">
            <h2>7. 隐私政策更新</h2>
            <p>
              我们可能会不时更新本隐私政策。当我们对隐私政策进行重大变更时，
              我们会通过网站公告、邮件通知等方式告知您。请您定期查看本政策，
              以了解我们如何保护您的信息。
            </p>
            <p>
              继续使用我们的服务即表示您同意更新后的隐私政策。
              如果您不同意更新后的政策，请停止使用我们的服务。
            </p>
          </div>

          <!-- 第八部分：联系我们 -->
          <div id="section-8" class="section">
            <h2>8. 联系我们</h2>
            <p>如果您对本隐私政策有任何疑问、意见或建议，请通过以下方式联系我们：</p>
            <div class="contact-info">
              <div class="contact-item">
                <strong>邮箱：</strong> sunyuyuku@outlook.com
              </div>
              <div class="contact-item">
                <strong>地址：</strong> 字节森林
              </div>
              <div class="contact-item">
                <strong>电话：</strong> +86 138-0000-0000
              </div>
            </div>
            <p>我们会在收到您的反馈后尽快回复您。</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Lock, View, Monitor, Document } from '@element-plus/icons-vue'

const lastUpdated = ref('2025年8月10日')

// 滚动到指定部分
const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId)
  if (element) {
    element.scrollIntoView({ 
      behavior: 'smooth',
      block: 'start'
    })
  }
}
</script>

<style lang="scss" scoped>
.privacy-page {
  min-height: calc(100vh - 140px);
  padding: 40px 0;
  background: #f8f9fa;
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
    margin: 0 0 20px 0;
  }
  
  .update-info {
    span {
      background: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
      padding: 8px 16px;
      border-radius: 20px;
      font-size: 0.9rem;
    }
  }
}

.privacy-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 40px;
  align-items: start;
}

.content-nav {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 20px;
  
  h3 {
    margin-bottom: 20px;
    color: var(--el-text-color-primary);
    font-size: 1.2rem;
  }
  
  ul {
    list-style: none;
    padding: 0;
    margin: 0;
    
    li {
      margin-bottom: 12px;
      
      a {
        color: var(--el-text-color-regular);
        text-decoration: none;
        padding: 8px 12px;
        border-radius: 6px;
        display: block;
        transition: all 0.3s ease;
        
        &:hover {
          background: var(--el-color-primary-light-9);
          color: var(--el-color-primary);
        }
      }
    }
  }
}

.content-main {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.section {
  margin-bottom: 50px;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  h2 {
    font-size: 1.8rem;
    color: var(--el-text-color-primary);
    margin-bottom: 25px;
    padding-bottom: 10px;
    border-bottom: 2px solid var(--el-color-primary);
  }
  
  h3 {
    font-size: 1.3rem;
    color: var(--el-text-color-primary);
    margin: 25px 0 15px 0;
  }
  
  h4 {
    font-size: 1.1rem;
    color: var(--el-text-color-primary);
    margin: 20px 0 10px 0;
  }
  
  p {
    line-height: 1.8;
    color: var(--el-text-color-regular);
    margin-bottom: 15px;
  }
  
  ul {
    margin: 15px 0;
    padding-left: 20px;
    
    li {
      line-height: 1.8;
      color: var(--el-text-color-regular);
      margin-bottom: 8px;
      
      strong {
        color: var(--el-text-color-primary);
      }
    }
  }
}

.intro {
  .intro-text {
    font-size: 1.1rem;
    line-height: 1.8;
    color: var(--el-text-color-regular);
    background: var(--el-color-info-light-9);
    padding: 25px;
    border-radius: 8px;
    border-left: 4px solid var(--el-color-primary);
  }
}

.usage-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.usage-item {
  background: var(--el-color-primary-light-9);
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid var(--el-color-primary);
  
  h4 {
    color: var(--el-color-primary);
    margin: 0 0 10px 0;
  }
  
  p {
    margin: 0;
    font-size: 0.95rem;
  }
}

.sharing-policy {
  .policy-item {
    background: #f8f9fa;
    padding: 25px;
    border-radius: 8px;
    margin-bottom: 20px;
    
    h4 {
      color: var(--el-color-primary);
      margin: 0 0 15px 0;
    }
    
    p {
      margin-bottom: 10px;
    }
  }
}

.security-measures {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
  margin-top: 20px;
}

.measure-item {
  text-align: center;
  padding: 25px;
  background: #f8f9fa;
  border-radius: 8px;
  
  .measure-icon {
    font-size: 2.5rem;
    color: var(--el-color-primary);
    margin-bottom: 15px;
  }
  
  h4 {
    margin: 0 0 10px 0;
    color: var(--el-text-color-primary);
  }
  
  p {
    margin: 0;
    font-size: 0.95rem;
  }
}

.cookie-info {
  .cookie-types {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin: 20px 0;
  }
  
  .cookie-type {
    background: var(--el-color-warning-light-9);
    padding: 20px;
    border-radius: 8px;
    border-left: 4px solid var(--el-color-warning);
    
    h4 {
      color: var(--el-color-warning-dark-2);
      margin: 0 0 10px 0;
    }
    
    p {
      margin: 0;
      font-size: 0.95rem;
    }
  }
}

.rights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.right-item {
  background: var(--el-color-success-light-9);
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid var(--el-color-success);
  
  h4 {
    color: var(--el-color-success-dark-2);
    margin: 0 0 10px 0;
  }
  
  p {
    margin: 0;
    font-size: 0.95rem;
  }
}

.contact-info {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 8px;
  margin: 20px 0;
  
  .contact-item {
    margin-bottom: 10px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    strong {
      color: var(--el-text-color-primary);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .privacy-page {
    padding: 20px 0;
  }
  
  .page-header {
    margin-bottom: 40px;
    
    h1 {
      font-size: 2rem;
    }
  }
  
  .privacy-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .content-nav {
    position: static;
    order: 2;
  }
  
  .content-main {
    padding: 20px;
    order: 1;
  }
  
  .usage-grid,
  .security-measures,
  .cookie-types,
  .rights-grid {
    grid-template-columns: 1fr;
  }
}
</style>