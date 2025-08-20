<template>
  <div class="tech-share-detail">
    <div class="container">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="12" animated />
      </div>
      
      <div v-else-if="article" class="article-content">
        <!-- 技术头部 -->
        <header class="tech-header">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
              <el-breadcrumb-item to="/tech-share">技术分享</el-breadcrumb-item>
              <el-breadcrumb-item>{{ article.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="tech-category">
            <el-tag type="info" size="large">
              <el-icon><Cpu /></el-icon>
              技术分享
            </el-tag>
          </div>
          
          <h1 class="title">{{ article.title }}</h1>
          
          <div class="tech-meta">
            <div class="author-info">
              <el-avatar :src="article.author?.avatar" :size="48">
                {{ article.author?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="author-details">
                <div class="name">{{ article.author?.nickname }}</div>
                <div class="time">{{ formatTime(article.publishedAt) }}</div>
              </div>
            </div>
            
            <div class="tech-stats">
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
                <el-icon><CollectionTag /></el-icon>
                <span>{{ article.collectCount || 0 }} 收藏</span>
              </div>
            </div>
          </div>
          
          <div class="tech-tags" v-if="article.tags && article.tags.length">
            <el-tag
              v-for="tag in article.tags"
              :key="tag.id"
              type="primary"
              effect="dark"
              size="large"
              @click="$router.push(`/tag/${tag.slug}`)"
            >
              <el-icon><Flag /></el-icon>
              {{ tag.name }}
            </el-tag>
          </div>
        </header>
        
        <!-- 技术内容主体 -->
        <main class="tech-body">
          <div class="content-wrapper">
            <!-- 内容 -->
            <div class="article-content">
              <div class="content" v-html="article.content"></div>
            </div>
            
            <!-- 技术边栏 -->
            <aside class="tech-sidebar">
              <div class="tech-info-card">
                <h3>技术要点</h3>
                <div class="tech-points">
                  <div v-if="article.difficulty" class="difficulty">
                    <span class="label">难度等级：</span>
                    <el-rate v-model="article.difficulty" disabled show-score />
                  </div>
                  <div v-if="article.techStack" class="tech-stack">
                    <span class="label">技术栈：</span>
                    <div class="tech-tags">
                      <el-tag 
                        v-for="tech in article.techStack" 
                        :key="tech"
                        size="small"
                        type="success"
                      >
                        {{ tech }}
                      </el-tag>
                    </div>
                  </div>
                  <div v-if="article.readTime" class="read-time">
                    <span class="label">预计阅读：</span>
                    <span class="value">{{ article.readTime }} 分钟</span>
                  </div>
                </div>
              </div>
              
              <!-- 相关文章 -->
              <div class="related-articles" v-if="relatedArticles.length">
                <h3>相关技术文章</h3>
                <div class="related-list">
                  <div 
                    v-for="related in relatedArticles" 
                    :key="related.id"
                    class="related-item"
                    @click="$router.push(`/tech-share/${related.slug}`)"
                  >
                    <img :src="related.coverImage || '/default-tech.jpg'" :alt="related.title" @error="onRelatedCoverError" />
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
        
        <!-- 技术操作栏 -->
        <footer class="tech-footer">
          <div class="actions">
            <el-button
              :type="isLiked ? 'primary' : 'default'"
              :icon="Star"
              size="large"
              @click="toggleLike"
            >
              {{ isLiked ? '已点赞' : '点赞' }} ({{ article.likeCount }})
            </el-button>
            
            <el-button :icon="Share" size="large" @click="shareArticle">
              分享技术
            </el-button>
            
            <el-button :icon="CollectionTag" size="large" @click="collectArticle">
              收藏
            </el-button>
          </div>
        </footer>
        
        <!-- 技术评论区 -->
        <section class="tech-comments-section">
          <h3>技术讨论 ({{ article.commentCount || 0 }})</h3>
          
          <!-- 评论表单 -->
          <div v-if="userStore.isLoggedIn" class="comment-form">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="4"
              placeholder="参与技术讨论，分享你的见解..."
              maxlength="500"
              show-word-limit
            />
            <div class="form-actions">
              <el-button
                type="primary"
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
              后参与技术讨论
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
                    :type="comment.isLiked ? 'primary' : 'default'"
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
          title="技术文章不存在"
          sub-title="抱歉，您访问的技术文章不存在或已被删除"
        >
          <template #extra>
            <el-button type="primary" @click="$router.push('/tech-share')">
              返回技术分享
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
import { View, Star, ChatDotRound, Share, Cpu, Flag, CollectionTag } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(true)
const article = ref(null)
const comments = ref([])
const relatedArticles = ref([])
const isLiked = ref(false)
const commentContent = ref('')
const submittingComment = ref(false)

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 加载文章详情
const loadArticle = async () => {
  loading.value = true
  try {
    const slug = route.params.slug
    const res = await articleApi.getArticleBySlug(slug)
    article.value = res.data

    // 查询点赞状态
    if (userStore.isLoggedIn && article.value?.id) {
      try {
        const likedRes = await articleApi.isArticleLiked(article.value.id)
        isLiked.value = !!likedRes.data
      } catch (e) {
        isLiked.value = false
      }
    }
    
    // 加载评论和相关文章
    await Promise.all([
      loadComments(),
      loadRelatedArticles()
    ])
  } catch (error) {
    console.error('加载技术文章失败:', error)
    article.value = null
    ElMessage.error('加载技术文章失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 加载评论
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

// 加载相关文章
const loadRelatedArticles = async () => {
  try {
    let list = []

    // 优先按标签获取
    if (article.value?.tags?.length) {
      try {
        const tagSlug = article.value.tags[0].slug
        const resByTag = await articleApi.getArticlesByTag(tagSlug, { page: 0, size: 5 })
        list = resByTag.data?.content || resByTag.data || []
      } catch (e) {
        // 标签接口失败时，继续回退到分类
      }
    }

    // 若无标签或标签查询为空/失败，回退到分类 tech
    if (!list.length) {
      const resByCat = await articleApi.getArticlesByCategory('tech', { page: 0, size: 5 })
      list = resByCat.data?.content || resByCat.data || []
    }

    relatedArticles.value = list
      .filter(item => item.id !== article.value?.id)
      .slice(0, 3)
  } catch (error) {
    console.error('加载相关文章失败:', error)
  }
}

// 点赞功能
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

// 分享功能
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

// 收藏功能
const collectArticle = () => {
  ElMessage.success('收藏功能开发中...')
}

// 提交评论
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
    ElMessage.error('评论发表失败')
  } finally {
    submittingComment.value = false
  }
}

// 评论点赞
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

onMounted(() => {
  loadArticle()
})
</script>

<style scoped>
.tech-share-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  min-height: 100vh;
  backdrop-filter: blur(10px);
}

.loading {
  padding: 40px;
}

/* 技术头部样式 */
.tech-header {
  padding: 30px 0;
  border-bottom: 3px solid #3f51b5;
  margin-bottom: 30px;
  background: linear-gradient(45deg, #f5f7ff, #e8f2ff);
  border-radius: 12px;
  padding: 30px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.tech-category {
  margin-bottom: 15px;
}

.title {
  font-size: 32px;
  color: #1a1a1a;
  margin: 20px 0;
  line-height: 1.3;
  font-weight: 700;
}

.tech-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 25px 0;
  flex-wrap: wrap;
  gap: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-details .name {
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.author-details .time {
  color: #666;
  font-size: 14px;
}

.tech-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #3f51b5;
  font-weight: 500;
}

.tech-tags {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 内容主体样式 */
.tech-body {
  margin: 30px 0;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
}

.article-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.content {
  line-height: 1.8;
  font-size: 16px;
  color: #333;
}

/* 技术边栏样式 */
.tech-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.tech-info-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(63, 81, 181, 0.1);
  border-left: 4px solid #3f51b5;
}

.tech-info-card h3 {
  color: #3f51b5;
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: 600;
}

.tech-points .label {
  font-weight: 600;
  color: #666;
}

.tech-points > div {
  margin-bottom: 15px;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 5px;
}

.read-time .value {
  color: #3f51b5;
  font-weight: 600;
}

.related-articles {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.related-articles h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: 600;
}

.related-item {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 10px;
}

.related-item:hover {
  background: #f5f7ff;
}

.related-item img {
  width: 60px;
  height: 45px;
  object-fit: cover;
  border-radius: 6px;
}

.related-content h4 {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  line-height: 1.3;
}

.related-content p {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

/* 操作栏样式 */
.tech-footer {
  margin: 30px 0;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

/* 评论区样式 */
.tech-comments-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.tech-comments-section h3 {
  color: #3f51b5;
  font-size: 24px;
  margin-bottom: 20px;
  font-weight: 600;
}

.comment-form {
  margin-bottom: 30px;
}

.form-actions {
  margin-top: 15px;
  text-align: right;
}

.comments-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.username {
  font-weight: 600;
  color: #333;
}

.time {
  color: #999;
  font-size: 14px;
}

.comment-text {
  color: #333;
  line-height: 1.6;
  margin-bottom: 10px;
}

.login-prompt {
  text-align: center;
  padding: 30px;
  color: #666;
}

.login-prompt a {
  color: #3f51b5;
  text-decoration: none;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }
  
  .tech-sidebar {
    order: -1;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 10px;
  }
  
  .tech-header {
    padding: 20px 15px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .tech-meta {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .article-content {
    padding: 20px 15px;
  }
  
  .actions {
    flex-direction: column;
  }
}
</style>

const onRelatedCoverError = (e) => {
  e.target.src = '/vite.svg'
}