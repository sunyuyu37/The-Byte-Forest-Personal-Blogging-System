<template>
  <div class="life-essays-detail">
    <div class="container">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="10" animated />
      </div>

      <div v-else-if="article" class="article-content">
        <!-- 头部 -->
        <header class="life-header">
          <div class="breadcrumb">
            <el-breadcrumb separator=">">
              <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
              <el-breadcrumb-item to="/life-essays">生活随笔</el-breadcrumb-item>
              <el-breadcrumb-item>{{ article.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <h1 class="title">{{ article.title }}</h1>
          
          <div class="life-meta">
            <div class="author-info">
              <el-avatar :src="article.author?.avatar" :size="44">
                {{ article.author?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="author-details">
                <div class="name">{{ article.author?.nickname }}</div>
                <div class="time">{{ formatTime(article.publishedAt) }}</div>
              </div>
            </div>
            <div class="life-stats">
              <span class="stat-item"><el-icon><View /></el-icon>{{ article.viewCount }} 阅读</span>
              <span class="stat-item"><el-icon><Star /></el-icon>{{ article.likeCount }} 点赞</span>
              <span class="stat-item"><el-icon><ChatDotRound /></el-icon>{{ article.commentCount || 0 }} 评论</span>
            </div>
          </div>
          
          <div class="life-tags" v-if="article.tags && article.tags.length">
            <el-tag
              v-for="tag in article.tags"
              :key="tag.id"
              type="warning"
              effect="plain"
              @click="$router.push(`/tag/${tag.slug}`)"
            >
              {{ tag.name }}
            </el-tag>
          </div>
        </header>
        
        <!-- 内容主体 -->
        <main class="life-body">
          <div class="content" v-html="article.content"></div>
        </main>
        
        <!-- 操作 -->
        <footer class="life-footer">
          <div class="actions">
            <el-button :type="isLiked ? 'primary' : 'default'" :icon="Star" @click="toggleLike">
              {{ isLiked ? '已点赞' : '点赞' }} ({{ article.likeCount }})
            </el-button>
            <el-button :icon="Share" @click="shareArticle">分享</el-button>
          </div>
        </footer>
        
        <!-- 评论区 -->
        <section class="comments-section">
          <h3>留言 ({ { article.commentCount || 0 } })</h3>
          
          <div v-if="userStore.isLoggedIn" class="comment-form">
            <el-input v-model="commentContent" type="textarea" :rows="4" placeholder="写下此刻的想法..." maxlength="500" show-word-limit />
            <div class="form-actions">
              <el-button type="primary" :loading="submittingComment" @click="submitComment">发布留言</el-button>
            </div>
          </div>
          
          <div v-else class="login-prompt">
            <p>
              <router-link to="/auth/login">登录</router-link>
              后参与留言互动
            </p>
          </div>
          
          <div class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-avatar">
                <el-avatar :src="comment.user?.avatar" :size="36">{{ comment.user?.nickname?.charAt(0) }}</el-avatar>
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="username">{{ comment.user?.nickname }}</span>
                  <span class="time">{{ formatTime(comment.createdAt) }}</span>
                </div>
                <div class="comment-text">{{ comment.content }}</div>
                <div class="comment-actions">
                  <el-button size="small" text :type="comment.isLiked ? 'primary' : 'default'" :icon="Star" @click="toggleCommentLike(comment)">
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
        <el-result icon="warning" title="文章不存在" sub-title="抱歉，您访问的随笔不存在或已被删除">
          <template #extra>
            <el-button type="primary" @click="$router.push('/life-essays')">返回生活随笔</el-button>
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
import { View, Star, ChatDotRound, Share } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(true)
const article = ref(null)
const comments = ref([])
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
  } catch (error) {
    console.error('加载随笔失败:', error)
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
    navigator.share({ title: article.value.title, text: article.value.excerpt, url: window.location.href })
  } else {
    navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制到剪贴板')
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  submittingComment.value = true
  try {
    await commentApi.createComment({ articleId: article.value.id, content: commentContent.value })
    commentContent.value = ''
    ElMessage.success('留言发表成功')
    await loadComments()
  } catch (error) {
    ElMessage.error('留言失败')
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
.life-essays-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff7e6 0%, #ffe7ba 100%);
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.loading { padding: 30px; }

.life-header {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 6px 20px rgba(255, 153, 0, 0.1);
  border: 1px solid #ffe7ba;
}

.title {
  margin: 16px 0;
  font-size: 28px;
  color: #7c4a03;
}

.life-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.author-info { display: flex; align-items: center; gap: 10px; }
.author-details .name { font-weight: 600; color: #8a5a20; }
.author-details .time { color: #b07a2b; }

.life-stats { display: flex; gap: 14px; color: #a66a13; }
.stat-item { display: inline-flex; align-items: center; gap: 6px; }

.life-tags { margin-top: 12px; display: flex; gap: 8px; flex-wrap: wrap; }

.life-body {
  margin: 20px 0;
  background: #fff;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(255, 153, 0, 0.08);
}

.content { line-height: 1.9; color: #5c3b09; }

.life-footer {
  margin: 20px 0;
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(255, 153, 0, 0.08);
}

.actions { display: flex; gap: 12px; justify-content: center; }

.comments-section {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(255, 153, 0, 0.08);
}

.comments-section h3 { color: #a66a13; margin-bottom: 14px; }

.comment-item { display: flex; gap: 12px; padding: 16px 0; border-bottom: 1px solid #fff1b8; }
.username { font-weight: 600; color: #8a5a20; }
.time { color: #b07a2b; font-size: 13px; }
.comment-text { color: #5c3b09; }

.login-prompt { text-align: center; color: #8a5a20; }
.login-prompt a { color: #d48806; text-decoration: none; }
</style>