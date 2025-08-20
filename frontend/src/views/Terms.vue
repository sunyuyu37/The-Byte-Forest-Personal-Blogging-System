<template>
  <div class="terms-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>使用条款</h1>
        <p class="subtitle">请仔细阅读以下使用条款和条件</p>
        <div class="update-info">
          <span>最后更新时间：{{ lastUpdated }}</span>
        </div>
      </div>

      <!-- 条款内容 -->
      <div class="terms-content">
        <div class="content-nav">
          <h3>目录</h3>
          <ul>
            <li><a href="#section-1" @click="scrollToSection('section-1')">1. 接受条款</a></li>
            <li><a href="#section-2" @click="scrollToSection('section-2')">2. 服务描述</a></li>
            <li><a href="#section-3" @click="scrollToSection('section-3')">3. 用户账户</a></li>
            <li><a href="#section-4" @click="scrollToSection('section-4')">4. 用户行为</a></li>
            <li><a href="#section-5" @click="scrollToSection('section-5')">5. 内容政策</a></li>
            <li><a href="#section-6" @click="scrollToSection('section-6')">6. 知识产权</a></li>
            <li><a href="#section-7" @click="scrollToSection('section-7')">7. 免责声明</a></li>
            <li><a href="#section-8" @click="scrollToSection('section-8')">8. 服务变更</a></li>
            <li><a href="#section-9" @click="scrollToSection('section-9')">9. 终止服务</a></li>
            <li><a href="#section-10" @click="scrollToSection('section-10')">10. 法律适用</a></li>
          </ul>
        </div>

        <div class="content-main">
          <!-- 引言 -->
          <div class="section intro">
            <div class="intro-box">
              <h3>重要提示</h3>
              <p>
                欢迎使用我们的博客网站！在使用我们的服务之前，请仔细阅读并理解本使用条款。
                通过访问或使用我们的网站，您表示同意遵守这些条款和条件。
                如果您不同意这些条款，请不要使用我们的服务。
              </p>
            </div>
          </div>

          <!-- 第一部分：接受条款 -->
          <div id="section-1" class="section">
            <h2>1. 接受条款</h2>
            <p>
              通过访问和使用本网站，您确认您已阅读、理解并同意受本使用条款的约束。
              这些条款构成您与我们之间具有法律约束力的协议。
            </p>
            <div class="highlight-box">
              <h4>条款生效</h4>
              <ul>
                <li>本条款自您首次使用本网站时生效</li>
                <li>您的持续使用表示对条款的持续同意</li>
                <li>我们保留随时修改这些条款的权利</li>
              </ul>
            </div>
          </div>

          <!-- 第二部分：服务描述 -->
          <div id="section-2" class="section">
            <h2>2. 服务描述</h2>
            <p>
              我们提供一个博客平台，用户可以在此阅读文章、发表评论、分享内容。
              我们的服务包括但不限于：
            </p>
            
            <div class="service-grid">
              <div class="service-item">
                <h4>内容浏览</h4>
                <p>浏览和阅读博客文章、分类和标签内容</p>
              </div>
              <div class="service-item">
                <h4>用户互动</h4>
                <p>发表评论、点赞、分享等互动功能</p>
              </div>
              <div class="service-item">
                <h4>个人账户</h4>
                <p>注册账户、管理个人资料和偏好设置</p>
              </div>
              <div class="service-item">
                <h4>搜索功能</h4>
                <p>搜索文章、分类、标签等内容</p>
              </div>
            </div>
          </div>

          <!-- 第三部分：用户账户 -->
          <div id="section-3" class="section">
            <h2>3. 用户账户</h2>
            
            <h3>3.1 账户注册</h3>
            <ul>
              <li>您必须提供准确、完整的注册信息</li>
              <li>您有责任维护账户信息的准确性</li>
              <li>您必须年满18岁或在法定监护人同意下使用服务</li>
              <li>每个用户只能注册一个账户</li>
            </ul>

            <h3>3.2 账户安全</h3>
            <div class="security-tips">
              <div class="tip-item">
                <el-icon class="tip-icon"><Key /></el-icon>
                <div>
                  <h4>密码安全</h4>
                  <p>使用强密码并定期更换，不要与他人分享</p>
                </div>
              </div>
              <div class="tip-item">
                <el-icon class="tip-icon"><Warning /></el-icon>
                <div>
                  <h4>异常活动</h4>
                  <p>如发现账户异常活动，请立即联系我们</p>
                </div>
              </div>
              <div class="tip-item">
                <el-icon class="tip-icon"><User /></el-icon>
                <div>
                  <h4>账户责任</h4>
                  <p>您对账户下的所有活动承担责任</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 第四部分：用户行为 -->
          <div id="section-4" class="section">
            <h2>4. 用户行为规范</h2>
            
            <div class="behavior-section">
              <div class="allowed-section">
                <h3>✅ 允许的行为</h3>
                <ul>
                  <li>发表建设性的评论和反馈</li>
                  <li>分享有价值的内容和观点</li>
                  <li>尊重其他用户的意见和观点</li>
                  <li>报告不当内容或行为</li>
                  <li>合理使用网站功能</li>
                </ul>
              </div>
              
              <div class="prohibited-section">
                <h3>❌ 禁止的行为</h3>
                <ul>
                  <li>发布违法、有害、威胁、辱骂、骚扰、诽谤、粗俗、淫秽或其他令人反感的内容</li>
                  <li>侵犯他人的知识产权、隐私权或其他权利</li>
                  <li>发布垃圾信息、广告或未经授权的商业内容</li>
                  <li>传播病毒、恶意软件或其他有害代码</li>
                  <li>尝试未经授权访问系统或其他用户账户</li>
                  <li>干扰或破坏网站的正常运行</li>
                  <li>冒充他人或虚假陈述身份</li>
                </ul>
              </div>
            </div>
          </div>

          <!-- 第五部分：内容政策 -->
          <div id="section-5" class="section">
            <h2>5. 内容政策</h2>
            
            <h3>5.1 用户生成内容</h3>
            <p>您对发布的所有内容承担全部责任，包括评论、反馈等。</p>
            
            <h3>5.2 内容审核</h3>
            <div class="moderation-policy">
              <div class="policy-item">
                <h4>审核权利</h4>
                <p>我们保留审核、编辑或删除任何内容的权利</p>
              </div>
              <div class="policy-item">
                <h4>举报机制</h4>
                <p>用户可以举报不当内容，我们会及时处理</p>
              </div>
              <div class="policy-item">
                <h4>处理时效</h4>
                <p>我们会在24小时内处理举报的内容</p>
              </div>
            </div>

            <h3>5.3 内容许可</h3>
            <p>
              通过在我们的网站上发布内容，您授予我们非独占的、全球性的、免版税的许可，
              以使用、复制、修改、分发您的内容，仅限于提供和改善我们的服务。
            </p>
          </div>

          <!-- 第六部分：知识产权 -->
          <div id="section-6" class="section">
            <h2>6. 知识产权</h2>
            
            <div class="ip-grid">
              <div class="ip-item">
                <h4>网站内容</h4>
                <p>网站上的所有内容，包括文本、图片、设计等，均受知识产权法保护</p>
              </div>
              <div class="ip-item">
                <h4>商标权</h4>
                <p>网站名称、标志等商标归我们所有，未经许可不得使用</p>
              </div>
              <div class="ip-item">
                <h4>用户内容</h4>
                <p>用户保留其原创内容的知识产权，但需遵守我们的使用许可</p>
              </div>
              <div class="ip-item">
                <h4>侵权处理</h4>
                <p>如发现侵权行为，请及时联系我们，我们会依法处理</p>
              </div>
            </div>
          </div>

          <!-- 第七部分：免责声明 -->
          <div id="section-7" class="section">
            <h2>7. 免责声明</h2>
            
            <div class="disclaimer-box">
              <h3>服务免责</h3>
              <ul>
                <li>我们的服务按"现状"提供，不提供任何明示或暗示的保证</li>
                <li>我们不保证服务的连续性、安全性或无错误</li>
                <li>我们不对用户生成的内容承担责任</li>
                <li>我们不对因使用服务而产生的任何损失承担责任</li>
              </ul>
            </div>

            <div class="limitation-box">
              <h3>责任限制</h3>
              <p>
                在法律允许的最大范围内，我们的总责任不超过您在过去12个月内
                为使用我们服务而支付的费用（如有）。
              </p>
            </div>
          </div>

          <!-- 第八部分：服务变更 -->
          <div id="section-8" class="section">
            <h2>8. 服务变更和中断</h2>
            
            <h3>8.1 服务修改</h3>
            <p>我们保留随时修改、暂停或终止服务的权利，恕不另行通知。</p>
            
            <h3>8.2 维护和更新</h3>
            <div class="maintenance-info">
              <div class="maintenance-item">
                <h4>定期维护</h4>
                <p>我们可能会定期进行系统维护，期间服务可能暂时不可用</p>
              </div>
              <div class="maintenance-item">
                <h4>紧急维护</h4>
                <p>在紧急情况下，我们可能需要立即中断服务进行维护</p>
              </div>
              <div class="maintenance-item">
                <h4>通知方式</h4>
                <p>我们会尽量提前通知计划中的维护活动</p>
              </div>
            </div>
          </div>

          <!-- 第九部分：终止服务 -->
          <div id="section-9" class="section">
            <h2>9. 服务终止</h2>
            
            <h3>9.1 用户终止</h3>
            <p>您可以随时停止使用我们的服务并删除您的账户。</p>
            
            <h3>9.2 我们的终止权利</h3>
            <div class="termination-reasons">
              <h4>我们可能在以下情况下终止您的账户：</h4>
              <ul>
                <li>违反本使用条款</li>
                <li>从事非法或有害活动</li>
                <li>长期不活跃的账户</li>
                <li>技术或安全原因</li>
              </ul>
            </div>
            
            <h3>9.3 终止后果</h3>
            <p>账户终止后，您将失去访问服务的权利，您的内容可能被删除。</p>
          </div>

          <!-- 第十部分：法律适用 -->
          <div id="section-10" class="section">
            <h2>10. 法律适用和争议解决</h2>
            
            <h3>10.1 适用法律</h3>
            <p>本条款受中华人民共和国法律管辖，不考虑法律冲突原则。</p>
            
            <h3>10.2 争议解决</h3>
            <div class="dispute-resolution">
              <div class="resolution-step">
                <h4>第一步：友好协商</h4>
                <p>任何争议首先通过友好协商解决</p>
              </div>
              <div class="resolution-step">
                <h4>第二步：调解</h4>
                <p>协商不成的，可申请第三方调解</p>
              </div>
              <div class="resolution-step">
                <h4>第三步：仲裁/诉讼</h4>
                <p>最终通过仲裁或向有管辖权的法院提起诉讼</p>
              </div>
            </div>
            
            <h3>10.3 可分割性</h3>
            <p>
              如果本条款的任何部分被认定为无效或不可执行，
              其余部分仍然有效并可执行。
            </p>
            
            <h3>10.4 联系信息</h3>
            <div class="contact-info">
              <p>如果您对本使用条款有任何疑问，请联系我们：</p>
              <div class="contact-details">
                <div><strong>邮箱：</strong> legal@myblog.com</div>
                <div><strong>地址：</strong> 北京市朝阳区某某大厦</div>
                <div><strong>电话：</strong> +86 138-0000-0000</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Key, Warning, User } from '@element-plus/icons-vue'

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
.terms-page {
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

.terms-content {
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
      margin-bottom: 8px;
      
      a {
        color: var(--el-text-color-regular);
        text-decoration: none;
        padding: 6px 12px;
        border-radius: 6px;
        display: block;
        transition: all 0.3s ease;
        font-size: 0.9rem;
        
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
    }
  }
}

.intro {
  .intro-box {
    background: linear-gradient(135deg, var(--el-color-warning-light-9), var(--el-color-primary-light-9));
    padding: 30px;
    border-radius: 12px;
    border-left: 4px solid var(--el-color-warning);
    
    h3 {
      color: var(--el-color-warning-dark-2);
      margin: 0 0 15px 0;
      display: flex;
      align-items: center;
      gap: 10px;
      
      &::before {
        content: "⚠️";
        font-size: 1.2rem;
      }
    }
    
    p {
      margin: 0;
      font-size: 1.05rem;
      line-height: 1.8;
    }
  }
}

.highlight-box {
  background: var(--el-color-info-light-9);
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid var(--el-color-info);
  margin: 20px 0;
  
  h4 {
    color: var(--el-color-info-dark-2);
    margin: 0 0 15px 0;
  }
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.service-item {
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

.security-tips {
  display: grid;
  gap: 15px;
  margin: 20px 0;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  
  .tip-icon {
    font-size: 1.5rem;
    color: var(--el-color-warning);
    flex-shrink: 0;
    margin-top: 2px;
  }
  
  h4 {
    margin: 0 0 8px 0;
    color: var(--el-text-color-primary);
  }
  
  p {
    margin: 0;
    font-size: 0.95rem;
  }
}

.behavior-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin: 20px 0;
}

.allowed-section,
.prohibited-section {
  padding: 25px;
  border-radius: 8px;
  
  h3 {
    margin: 0 0 20px 0;
    font-size: 1.2rem;
  }
}

.allowed-section {
  background: var(--el-color-success-light-9);
  border-left: 4px solid var(--el-color-success);
  
  h3 {
    color: var(--el-color-success-dark-2);
  }
}

.prohibited-section {
  background: var(--el-color-danger-light-9);
  border-left: 4px solid var(--el-color-danger);
  
  h3 {
    color: var(--el-color-danger-dark-2);
  }
}

.moderation-policy {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.policy-item {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  
  h4 {
    color: var(--el-color-primary);
    margin: 0 0 10px 0;
  }
  
  p {
    margin: 0;
    font-size: 0.95rem;
  }
}

.ip-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.ip-item {
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

.disclaimer-box,
.limitation-box {
  background: var(--el-color-danger-light-9);
  padding: 25px;
  border-radius: 8px;
  border-left: 4px solid var(--el-color-danger);
  margin: 20px 0;
  
  h3 {
    color: var(--el-color-danger-dark-2);
    margin: 0 0 15px 0;
  }
}

.maintenance-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.maintenance-item {
  background: var(--el-color-info-light-9);
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid var(--el-color-info);
  
  h4 {
    color: var(--el-color-info-dark-2);
    margin: 0 0 10px 0;
  }
  
  p {
    margin: 0;
    font-size: 0.95rem;
  }
}

.termination-reasons {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin: 15px 0;
  
  h4 {
    margin: 0 0 15px 0;
    color: var(--el-text-color-primary);
  }
}

.dispute-resolution {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.resolution-step {
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
  
  .contact-details {
    margin-top: 15px;
    
    div {
      margin-bottom: 8px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      strong {
        color: var(--el-text-color-primary);
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .terms-page {
    padding: 20px 0;
  }
  
  .page-header {
    margin-bottom: 40px;
    
    h1 {
      font-size: 2rem;
    }
  }
  
  .terms-content {
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
  
  .behavior-section {
    grid-template-columns: 1fr;
  }
  
  .service-grid,
  .moderation-policy,
  .ip-grid,
  .maintenance-info,
  .dispute-resolution {
    grid-template-columns: 1fr;
  }
}
</style>