<template>
  <div class="study-notes-detail">
    <div class="container">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="10" animated />
      </div>

      <div v-else-if="article" class="article-content">
        <!-- 学术头部 -->
        <header class="academic-header">
          <div class="breadcrumb">
            <el-breadcrumb separator=">">
              <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
              <el-breadcrumb-item to="/study-notes">学习笔记</el-breadcrumb-item>
              <el-breadcrumb-item>{{ article.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <h1 class="academic-title">{{ article.title }}</h1>
          
          <div class="paper-meta">
            <div class="author-section">
              <el-avatar :src="article.author?.avatar" :size="40">
                {{ article.author?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="author-details">
                <div class="name">{{ article.author?.nickname }}</div>
                <div class="date">发布于 {{ formatTime(article.publishedAt) }}</div>
              </div>
            </div>
            <div class="academic-metrics">
              <span class="metric"><el-icon><View /></el-icon>{{ article.viewCount }} 阅读</span>
              <span class="metric"><el-icon><Star /></el-icon>{{ article.likeCount }} 收藏</span>
              <span class="metric"><el-icon><ChatDotRound /></el-icon>{{ article.commentCount || 0 }} 讨论</span>
            </div>
          </div>
          
          <div class="study-tags" v-if="article.tags && article.tags.length">
            <el-tag
              v-for="tag in article.tags"
              :key="tag.id"
              type="info"
              effect="plain"
              @click="$router.push(`/tag/${tag.slug}`)"
            >
              {{ tag.name }}
            </el-tag>
          </div>
        </header>
        
        <!-- 笔记正文 -->
        <main class="academic-body">
          <div class="note-content" v-html="article.content"></div>
        </main>
        
        <!-- 学术操作 -->
        <footer class="academic-footer">
          <div class="actions">
            <el-button :type="isLiked ? 'primary' : 'default'" :icon="Star" @click="toggleLike">
              {{ isLiked ? '已收藏' : '收藏' }} ({{ article.likeCount }})
            </el-button>
            <el-button :icon="Share" @click="shareArticle">分享笔记</el-button>
            <el-button :icon="Document" @click="exportNote">导出PDF</el-button>
          </div>
        </footer>
        
        <!-- 学术讨论区 -->
        <section class="discussion-section">
          <h3>学术讨论 ({{ article.commentCount || 0 }})</h3>
          
          <div v-if="userStore.isLoggedIn" class="discussion-form">
            <el-input 
              v-model="commentContent" 
              type="textarea" 
              :rows="4" 
              placeholder="发表学术观点或提出问题..." 
              maxlength="1000" 
              show-word-limit 
            />
            <div class="form-actions">
              <el-button type="primary" :loading="submittingComment" @click="submitComment">
                发布讨论
              </el-button>
            </div>
          </div>
          
          <div v-else class="login-prompt">
            <p>
              <router-link to="/auth/login">登录</router-link>
              后参与学术讨论
            </p>
          </div>
          
          <div class="discussion-list">
            <div v-for="comment in comments" :key="comment.id" class="discussion-item">
              <div class="discussion-avatar">
                <el-avatar :src="comment.user?.avatar" :size="36">{{ comment.user?.nickname?.charAt(0) }}</el-avatar>
              </div>
              <div class="discussion-content">
                <div class="discussion-header">
                  <span class="username">{{ comment.user?.nickname }}</span>
                  <span class="time">{{ formatTime(comment.createdAt) }}</span>
                </div>
                <div class="discussion-text">{{ comment.content }}</div>
                <div class="discussion-actions">
                  <el-button 
                    size="small" 
                    text 
                    :type="comment.isLiked ? 'primary' : 'default'" 
                    :icon="Star" 
                    @click="toggleCommentLike(comment)"
                  >
                    {{ comment.isLiked ? '已赞' : '赞同' }}
                    <span v-if="comment.likeCount > 0">({{ comment.likeCount }})</span>
                  </el-button>
                  <el-button size="small" text :icon="ChatDotRound">回复</el-button>
                </div>
              </div>
            </div>
          </div>
        </section>
        
        <!-- 相关笔记 -->
        <section class="related-notes" v-if="relatedNotes && relatedNotes.length">
          <h3>相关笔记</h3>
          <div class="notes-grid">
            <div 
              v-for="note in relatedNotes" 
              :key="note.id"
              class="note-card"
              @click="$router.push(`/study-notes/${note.slug}`)"
            >
              <h4>{{ note.title }}</h4>
              <p>{{ note.excerpt }}</p>
              <div class="note-meta">
                <span>{{ formatTime(note.publishedAt) }}</span>
                <span>{{ note.viewCount }} 阅读</span>
              </div>
            </div>
          </div>
        </section>
      </div>
      
      <div v-else class="not-found">
        <el-result icon="warning" title="笔记不存在" sub-title="抱歉，您访问的学习笔记不存在或已被删除">
          <template #extra>
            <el-button type="primary" @click="$router.push('/study-notes')">返回学习笔记</el-button>
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
import { View, Star, ChatDotRound, Share, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(true)
const article = ref(null)
const comments = ref([])
const relatedNotes = ref([])
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
    
    await Promise.all([loadComments(), loadRelatedNotes()])
  } catch (error) {
    console.error('加载笔记失败:', error)
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
    console.error('加载讨论失败:', error)
  }
}

const loadRelatedNotes = async () => {
  if (!article.value?.tags?.length) return
  try {
    const tagIds = article.value.tags.map(tag => tag.id)
    const res = await articleApi.getRelatedArticles(article.value.id, { tags: tagIds })
    relatedNotes.value = (res.data || []).slice(0, 3)
  } catch (error) {
    console.error('加载相关笔记失败:', error)
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

const exportNote = () => {
  ElMessage.info('PDF导出功能开发中...')
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入讨论内容')
    return
  }
  submittingComment.value = true
  try {
    await commentApi.createComment({ 
      articleId: article.value.id, 
      content: commentContent.value 
    })
    commentContent.value = ''
    ElMessage.success('讨论发表成功')
    await loadComments()
  } catch (error) {
    ElMessage.error('发表失败')
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
.study-notes-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  font-family: 'Georgia', 'Times New Roman', serif;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.loading { padding: 30px; }

.academic-header {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border-left: 4px solid #007bff;
  margin-bottom: 24px;
}

.academic-title {
  margin: 16px 0;
  font-size: 32px;
  color: #212529;
  font-weight: 600;
  line-height: 1.3;
}

.paper-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin: 20px 0;
}

.author-section { display: flex; align-items: center; gap: 12px; }
.author-details .name { font-weight: 600; color: #495057; }
.author-details .date { color: #6c757d; font-size: 14px; }

.academic-metrics { 
  display: flex; 
  gap: 16px; 
  color: #6c757d; 
  font-size: 14px;
}
.metric { display: inline-flex; align-items: center; gap: 4px; }

.study-tags { 
  margin-top: 16px; 
  display: flex; 
  gap: 8px; 
  flex-wrap: wrap; 
}

.academic-body {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.note-content { 
  line-height: 1.8; 
  color: #343a40; 
  font-size: 16px;
}

.note-content h1, .note-content h2, .note-content h3 {
  margin-top: 2em;
  margin-bottom: 1em;
  color: #212529;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 0.5em;
}

.note-content code {
  background: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
  color: #e83e8c;
  font-family: 'Consolas', 'Monaco', monospace;
}

.note-content blockquote {
  border-left: 4px solid #007bff;
  margin: 1.5em 0;
  padding: 1em 1.5em;
  background: #f8f9fa;
  color: #6c757d;
}

.academic-footer {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.actions { 
  display: flex; 
  gap: 12px; 
  justify-content: center; 
  flex-wrap: wrap;
}

.discussion-section {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.discussion-section h3 { 
  color: #495057; 
  margin-bottom: 20px; 
  font-size: 20px;
  border-bottom: 2px solid #007bff;
  padding-bottom: 8px;
}

.discussion-form { margin-bottom: 24px; }

.discussion-item { 
  display: flex; 
  gap: 12px; 
  padding: 20px 0; 
  border-bottom: 1px solid #dee2e6; 
}
.discussion-item:last-child { border-bottom: none; }

.username { font-weight: 600; color: #495057; }
.time { color: #6c757d; font-size: 13px; }
.discussion-text { 
  color: #343a40; 
  margin: 8px 0; 
  line-height: 1.6;
}

.login-prompt { 
  text-align: center; 
  color: #6c757d; 
  margin: 20px 0;
}
.login-prompt a { 
  color: #007bff; 
  text-decoration: none; 
}

.related-notes {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.related-notes h3 {
  color: #495057;
  margin-bottom: 20px;
  font-size: 20px;
  border-bottom: 2px solid #28a745;
  padding-bottom: 8px;
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.note-card {
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.note-card:hover {
  border-color: #007bff;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.15);
}

.note-card h4 {
  margin: 0 0 8px 0;
  color: #495057;
  font-size: 16px;
}

.note-card p {
  color: #6c757d;
  font-size: 14px;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.note-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #868e96;
}
</style>