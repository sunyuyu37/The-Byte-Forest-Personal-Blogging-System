<template>
  <div class="project-practice-detail">
    <div class="container">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="12" animated />
      </div>
      
      <div v-else-if="article" class="article-content">
        <!-- 项目头部 -->
        <header class="project-header">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
              <el-breadcrumb-item to="/project-practice">项目实践</el-breadcrumb-item>
              <el-breadcrumb-item>{{ article.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="project-category">
            <el-tag type="success" size="large" effect="dark">
              <el-icon><DataBoard /></el-icon>
              项目实践
            </el-tag>
          </div>
          
          <h1 class="title">{{ article.title }}</h1>
          
          <div class="project-meta">
            <div class="author-info">
              <el-avatar :src="article.author?.avatar" :size="48">
                {{ article.author?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="author-details">
                <div class="name">{{ article.author?.nickname }}</div>
                <div class="time">{{ formatTime(article.publishedAt) }}</div>
              </div>
            </div>
            
            <div class="project-stats">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ article.viewCount }} 阅读</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>{{ article.likeCount }} 点赞</span>
              </div>
              <div class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ article.commentCount || 0 }} 评论</span>
              </div>
              <div class="stat-item">
                <el-icon><FolderOpened /></el-icon>
                <span>{{ article.forkCount || 0 }} Fork</span>
              </div>
            </div>
          </div>
          
          <div class="project-tags" v-if="article.tags && article.tags.length">
            <el-tag
              v-for="tag in article.tags"
              :key="tag.id"
              type="success"
              effect="plain"
              size="large"
              @click="$router.push(`/tag/${tag.slug}`)"
            >
              <el-icon><Collection /></el-icon>
              {{ tag.name }}
            </el-tag>
          </div>
        </header>
        
        <!-- 项目内容主体 -->
        <main class="project-body">
          <div class="content-wrapper">
            <!-- 内容 -->
            <div class="article-content">
              <div class="content" v-html="article.content"></div>
            </div>
            
            <!-- 项目信息边栏 -->
            <aside class="project-sidebar">
              <div class="project-info-card">
                <h3>项目信息</h3>
                <div class="project-details">
                  <div v-if="article.projectType" class="project-type">
                    <span class="label">项目类型：</span>
                    <el-tag type="success" size="small">{{ article.projectType }}</el-tag>
                  </div>
                  <div v-if="article.techStack" class="tech-stack">
                    <span class="label">技术栈：</span>
                    <div class="tech-tags">
                      <el-tag 
                        v-for="tech in article.techStack" 
                        :key="tech"
                        size="small"
                        type="info"
                      >
                        {{ tech }}
                      </el-tag>
                    </div>
                  </div>
                  <div v-if="article.status" class="project-status">
                    <span class="label">项目状态：</span>
                    <el-tag 
                      :type="article.status === '进行中' ? 'warning' : 'success'"
                      size="small"
                    >
                      {{ article.status }}
                    </el-tag>
                  </div>
                  <div v-if="article.readTime" class="read-time">
                    <span class="label">阅读时长：</span>
                    <span class="value">{{ article.readTime }} 分钟</span>
                  </div>
                </div>
              </div>
              
              <!-- 项目链接 -->
              <div class="project-links" v-if="article.github || article.demo">
                <h3>项目链接</h3>
                <div class="links">
                  <el-button 
                    v-if="article.github" 
                    type="success" 
                    :icon="Link"
                    @click="window.open(article.github)"
                  >
                    GitHub 仓库
                  </el-button>
                  <el-button 
                    v-if="article.demo" 
                    type="primary" 
                    :icon="Monitor"
                    @click="window.open(article.demo)"
                  >
                    在线演示
                  </el-button>
                </div>
              </div>
              
              <!-- 相关项目 -->
              <div class="related-projects" v-if="relatedProjects.length">
                <h3>相关项目</h3>
                <div class="related-list">
                  <div 
                    v-for="related in relatedProjects" 
                    :key="related.id"
                    class="related-item"
                    @click="$router.push(`/project-practice/${related.slug}`)"
                  >
                    <img :src="related.coverImage || '/default-project.jpg'" :alt="related.title" @error="onRelatedCoverError" />
                    <div class="related-content">
                      <h4>{{ related.title }}</h4>
                      <p>{{ related.excerpt }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </aside>
          </div>
        </main>
        
        <!-- 项目操作栏 -->
        <footer class="project-footer">
          <div class="actions">
            <el-button
              :type="isLiked ? 'success' : 'default'"
              :icon="Star"
              size="large"
              @click="toggleLike"
            >
              {{ isLiked ? '已点赞' : '点赞' }} ({{ article.likeCount }})
            </el-button>
            
            <el-button :icon="Share" size="large" @click="shareArticle">
              分享项目
            </el-button>
            
            <el-button :icon="Download" size="large" @click="downloadProject">
              下载资源
            </el-button>
          </div>
        </footer>
        
        <!-- 项目讨论区 -->
        <section class="project-comments-section">
          <h3>项目讨论 ({{ article.commentCount || 0 }})</h3>
          
          <!-- 评论表单 -->
          <div v-if="userStore.isLoggedIn" class="comment-form">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="4"
              placeholder="分享你对这个项目的看法和建议..."
              maxlength="500"
              show-word-limit
            />
            <div class="form-actions">
              <el-button
                type="success"
                :loading="submittingComment"
                @click="submitComment"
              >
                发表讨论
              </el-button>
            </div>
          </div>
          
          <div v-else class="login-prompt">
            <p>
              <router-link to="/auth/login">登录</router-link>
              后参与项目讨论
            </p>
          </div>
          
          <!-- 评论列表 -->
          <div class="comments-list">
            <div
              v-for="comment in comments"
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-avatar">
                <el-avatar :src="comment.user?.avatar" :size="40">
                  {{ comment.user?.nickname?.charAt(0) }}
                </el-avatar>
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="username">{{ comment.user?.nickname }}</span>
                  <span class="time">{{ formatTime(comment.createdAt) }}</span>
                </div>
                <div class="comment-text">{{ comment.content }}</div>
                <div class="comment-actions">
                  <el-button
                    size="small"
                    text
                    :type="comment.isLiked ? 'success' : 'default'"
                    :icon="Star"
                    @click="toggleCommentLike(comment)"
                  >
                    {{ comment.isLiked ? '已赞' : '点赞' }}
                    <span v-if="comment.likeCount > 0">({{ comment.likeCount }})</span>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
      
      <div v-else class="not-found">
        <el-result
          icon="warning"
          title="项目不存在"
          sub-title="抱歉，您访问的项目实践不存在或已被删除"
        >
          <template #extra>
            <el-button type="success" @click="$router.push('/project-practice')">
              返回项目实践
            </el-button>
          </template>
        </el-result>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  View, Star, ChatDotRound, Share, DataBoard, Collection, 
  FolderOpened, Link, Monitor, Download
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(true)
const article = ref(null)
const comments = ref([])
const relatedProjects = ref([])
const isLiked = ref(false)
const commentContent = ref('')
const submittingComment = ref(false)

const formatTime = (time) => dayjs(time).format('YYYY-MM-DD HH:mm')

const loadArticle = async () => {
  loading.value = true
  try {
    const slug = route.params.slug
    const res = await articleApi.getArticleBySlug(slug)
    article.value = res.data
    
    if (userStore.isLoggedIn && article.value?.id) {
      try {
        const likedRes = await articleApi.isArticleLiked(article.value.id)
        isLiked.value = !!likedRes.data
      } catch (e) {
        isLiked.value = false
      }
    }
    await loadComments()
    await loadRelatedProjects()
  } catch (error) {
    console.error('加载项目实践失败:', error)
    article.value = null
    ElMessage.error('加载文章失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  if (!article.value) return
  try {
    const key = article.value.slug || article.value.id
    const res = await commentApi.getArticleComments(key)
    const list = res.data?.content || res.data || []
    comments.value = list.map(c => ({ ...c, isLiked: false, likeCount: c.likeCount || 0 }))
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

const loadRelatedProjects = async () => {
  try {
    // 模拟相关项目数据
    relatedProjects.value = [
      {
        id: 1,
        slug: 'vue-admin-system',
        title: 'Vue3 后台管理系统',
        excerpt: '基于 Vue3 + Element Plus 的后台管理系统',
        coverImage: '/images/vue-admin.jpg'
      },
      {
        id: 2,
        slug: 'react-blog-platform',
        title: 'React 博客平台',
        excerpt: '使用 React 和 Node.js 构建的博客系统',
        coverImage: '/images/react-blog.jpg'
      }
    ]
  } catch (error) {
    console.error('加载相关项目失败:', error)
  }
}

const toggleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (isLiked.value) {
      await articleApi.unlikeArticle(article.value.id)
      article.value.likeCount--
    } else {
      await articleApi.likeArticle(article.value.id)
      article.value.likeCount++
    }
    isLiked.value = !isLiked.value
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const shareArticle = () => {
  if (navigator.share) {
    navigator.share({ 
      title: article.value.title, 
      text: article.value.excerpt, 
      url: window.location.href 
    })
  } else {
    navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制到剪贴板')
  }
}

const downloadProject = () => {
  ElMessage.info('下载功能开发中...')
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  submittingComment.value = true
  try {
    await commentApi.createComment({ 
      articleId: article.value.id, 
      content: commentContent.value 
    })
    commentContent.value = ''
    ElMessage.success('评论发表成功')
    await loadComments()
  } catch (error) {
    ElMessage.error('评论失败')
  } finally {
    submittingComment.value = false
  }
}

const toggleCommentLike = async (comment) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (comment.isLiked) {
      await commentApi.unlikeComment(comment.id)
      comment.likeCount--
    } else {
      await commentApi.likeComment(comment.id)
      comment.likeCount++
    }
    comment.isLiked = !comment.isLiked
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(loadArticle)
</script>

<style scoped>
.project-practice-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #e8f5e8 0%, #d4edda 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading {
  padding: 30px;
}

.project-header {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(40, 167, 69, 0.1);
  border: 1px solid rgba(40, 167, 69, 0.1);
}

.breadcrumb {
  margin-bottom: 20px;
}

.project-category {
  margin-bottom: 16px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #28a745;
  margin: 16px 0;
  line-height: 1.3;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 24px 0;
  padding: 20px;
  background: #f8fffe;
  border-radius: 8px;
  border: 1px solid #d4edda;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-details .name {
  font-weight: 600;
  color: #28a745;
  font-size: 16px;
}

.author-details .time {
  color: #6c757d;
  font-size: 14px;
  margin-top: 4px;
}

.project-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #28a745;
  font-weight: 500;
}

.project-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 20px;
}

.project-tags .el-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.project-tags .el-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

.project-body {
  margin-bottom: 32px;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
}

.article-content {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(40, 167, 69, 0.1);
  border: 1px solid rgba(40, 167, 69, 0.1);
}

.content {
  line-height: 1.8;
  color: #2c3e50;
  font-size: 16px;
}

.project-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.project-info-card,
.project-links,
.related-projects {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(40, 167, 69, 0.1);
  border: 1px solid rgba(40, 167, 69, 0.1);
}

.project-info-card h3,
.project-links h3,
.related-projects h3 {
  color: #28a745;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  border-bottom: 2px solid #d4edda;
  padding-bottom: 8px;
}

.project-details > div {
  margin-bottom: 12px;
}

.label {
  font-weight: 600;
  color: #495057;
  margin-right: 8px;
}

.value {
  color: #28a745;
  font-weight: 500;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 4px;
}

.links {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e9ecef;
}

.related-item:hover {
  background: #f8fffe;
  border-color: #28a745;
}

.related-item img {
  width: 60px;
  height: 45px;
  object-fit: cover;
  border-radius: 6px;
}

.related-content h4 {
  font-size: 14px;
  color: #28a745;
  margin-bottom: 4px;
}

.related-content p {
  font-size: 12px;
  color: #6c757d;
  line-height: 1.4;
}

.project-footer {
  background: #fff;
  border-radius: 12px;
  padding: 24px 30px;
  margin-bottom: 32px;
  box-shadow: 0 4px 20px rgba(40, 167, 69, 0.1);
  border: 1px solid rgba(40, 167, 69, 0.1);
}

.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.project-comments-section {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(40, 167, 69, 0.1);
  border: 1px solid rgba(40, 167, 69, 0.1);
}

.project-comments-section h3 {
  color: #28a745;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  border-bottom: 2px solid #d4edda;
  padding-bottom: 12px;
}

.comment-form {
  margin-bottom: 24px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.login-prompt {
  text-align: center;
  padding: 20px;
  background: #f8fffe;
  border-radius: 8px;
  margin-bottom: 24px;
  border: 1px solid #d4edda;
}

.login-prompt a {
  color: #28a745;
  text-decoration: none;
  font-weight: 600;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f8fffe;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.username {
  font-weight: 600;
  color: #28a745;
}

.time {
  color: #6c757d;
  font-size: 14px;
}

.comment-text {
  color: #2c3e50;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.not-found {
  padding: 60px 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .content-wrapper {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .project-meta {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .project-stats {
    align-self: stretch;
    justify-content: space-around;
  }
  
  .actions {
    flex-direction: column;
  }
}
</style>

const onRelatedCoverError = (e) => {
  e.target.src = '/vite.svg'
}