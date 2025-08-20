<template>
  <div class="article-detail">
    <div class="container">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="10" animated />
      </div>
      
      <div v-else-if="article" class="article-content">
        <!-- 文章头部 -->
        <header class="article-header">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
              <el-breadcrumb-item to="/articles">文章</el-breadcrumb-item>
              <el-breadcrumb-item>{{ article.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <h1 class="title">{{ article.title }}</h1>
          
          <div class="meta">
            <div class="author-info">
              <el-avatar :src="article.author?.avatar" :size="40">
                {{ article.author?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="author-details">
                <div class="name">{{ article.author?.nickname }}</div>
                <div class="time">{{ formatTime(article.publishedAt) }}</div>
              </div>
            </div>
            
            <div class="stats">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ article.viewCount }} 阅读
              </span>
              <span class="stat-item">
                <el-icon><Star /></el-icon>
                {{ article.likeCount }} 点赞
              </span>
              <span class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ article.commentCount || 0 }} 评论
              </span>
            </div>
          </div>
          
          <div class="tags" v-if="article.tags && article.tags.length">
            <el-tag
              v-for="tag in article.tags"
              :key="tag.id"
              type="primary"
              effect="plain"
              @click="$router.push(`/tag/${tag.slug}`)"
            >
              {{ tag.name }}
            </el-tag>
          </div>
        </header>
        
        <!-- 文章内容 -->
        <main class="article-body">
          <div class="content" v-html="article.content"></div>
        </main>
        
        <!-- 文章操作 -->
        <footer class="article-footer">
          <div class="actions">
            <el-button
              :type="isLiked ? 'primary' : 'default'"
              :icon="Star"
              @click="toggleLike"
            >
              {{ isLiked ? '已点赞' : '点赞' }} ({{ article.likeCount }})
            </el-button>
            
            <el-button :icon="Share" @click="shareArticle">
              分享
            </el-button>
          </div>
        </footer>
        
        <!-- 评论区 -->
        <section class="comments-section">
          <h3>评论 ({{ article.commentCount || 0 }})</h3>
          
          <!-- 评论表单 -->
          <div v-if="userStore.isLoggedIn" class="comment-form">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="4"
              placeholder="写下你的评论..."
              maxlength="500"
              show-word-limit
            />
            <div class="form-actions">
              <el-button
                type="primary"
                :loading="submittingComment"
                @click="submitComment"
              >
                发表评论
              </el-button>
            </div>
          </div>
          
          <div v-else class="login-prompt">
            <p>
              <router-link to="/auth/login">登录</router-link>
              后参与评论
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
                <el-avatar :src="comment.user?.avatar" :size="36">
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
          title="文章不存在"
          sub-title="抱歉，您访问的文章不存在或已被删除"
        >
          <template #extra>
            <el-button type="primary" @click="$router.push('/articles')">
              返回文章列表
            </el-button>
          </template>
        </el-result>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
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

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 加载文章详情
const loadArticle = async () => {
  loading.value = true
  try {
    const slug = route.params.slug
    
    // 调用后端 API 获取文章详情
    const res = await articleApi.getArticleBySlug(slug)
    article.value = res.data

    // 如果用户已登录，查询是否已点赞
    if (userStore.isLoggedIn && article.value?.id) {
      try {
        const likedRes = await articleApi.isArticleLiked(article.value.id)
        // request.js 返回的是后端 Result 对象，data 在 likedRes.data
        isLiked.value = !!likedRes.data
      } catch (e) {
        // 忽略点赞状态查询错误
        isLiked.value = false
      }
    } else {
      isLiked.value = false
    }
    
    // 加载文章评论
    await loadComments()
  } catch (error) {
    console.error('加载文章失败:', error)
    article.value = null
    if (error.response?.status === 404) {
      ElMessage.error('文章不存在')
    } else {
      ElMessage.error('加载文章失败，请稍后重试')
    }
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
    // 初始化点赞状态和计数
    comments.value = list.map(c => ({ ...c, isLiked: false, likeCount: c.likeCount || 0 }))

    // 已登录则批量查询每条评论是否点赞（逐条请求，简单实现；如需优化可后端提供批量接口）
    if (userStore.isLoggedIn) {
      await Promise.all(
        comments.value.map(async (c) => {
          try {
            const likedRes = await commentApi.isCommentLiked(c.id)
            c.isLiked = !!likedRes.data
          } catch {}
        })
      )
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

// 点赞/取消点赞评论
const toggleCommentLike = async (comment) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (!comment.isLiked) {
      await commentApi.likeComment(comment.id)
      comment.isLiked = true
      comment.likeCount = (comment.likeCount || 0) + 1
      ElMessage.success('已点赞评论')
    } else {
      await commentApi.unlikeComment(comment.id)
      comment.isLiked = false
      comment.likeCount = Math.max(0, (comment.likeCount || 0) - 1)
      ElMessage.success('已取消点赞评论')
    }
  } catch (error) {
    console.error('评论点赞失败:', error)
    ElMessage.error(error?.response?.data?.message || '操作失败，请重试')
  }
}

// 点赞/取消点赞
const toggleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (!article.value?.id) return
  try {
    if (!isLiked.value) {
      await articleApi.likeArticle(article.value.id)
      isLiked.value = true
      article.value.likeCount = (article.value.likeCount || 0) + 1
      ElMessage.success('点赞成功')
    } else {
      await articleApi.unlikeArticle(article.value.id)
      isLiked.value = false
      article.value.likeCount = Math.max(0, (article.value.likeCount || 0) - 1)
      ElMessage.success('取消点赞')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error(error?.response?.data?.message || '操作失败，请重试')
  }
}

// 分享文章
const shareArticle = () => {
  if (navigator.share) {
    navigator.share({
      title: article.value.title,
      text: article.value.summary,
      url: window.location.href
    })
  } else {
    navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制到剪贴板')
  }
}

// 提交评论
const submitComment = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  submittingComment.value = true
  try {
    const res = await commentApi.createComment({
      articleId: article.value.id,
      content: commentContent.value.trim()
    })
    await loadComments()
    article.value.commentCount = (article.value.commentCount || 0) + 1
    commentContent.value = ''
    ElMessage.success('评论成功')
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error(error?.response?.data?.message || '发表评论失败，请稍后重试')
  } finally {
    submittingComment.value = false
  }
}

onMounted(() => {
  loadArticle()
})

watch(() => route.params.slug, () => {
  loadArticle()
})
</script>

<style scoped>
.article-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--background-color-base) 0%, var(--background-color-light) 100%);
  padding: var(--spacing-xl) 0;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="dots" width="20" height="20" patternUnits="userSpaceOnUse"><circle cx="10" cy="10" r="1" fill="currentColor" opacity="0.05"/></pattern></defs><rect width="100" height="100" fill="url(%23dots)"/></svg>');
    color: var(--primary-color);
  }
  
  .loading {
    padding: var(--spacing-4xl) 0;
    position: relative;
    z-index: 2;
  }
  
  .article-content {
    background: var(--background-color-white);
    border-radius: var(--border-radius-xl);
    overflow: hidden;
    box-shadow: var(--shadow-lg);
    border: 1px solid var(--border-color-light);
    position: relative;
    z-index: 2;
    backdrop-filter: blur(10px);
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: var(--primary-gradient);
    }
  }
  
  .article-header {
    padding: var(--spacing-2xl);
    border-bottom: 1px solid var(--border-color-light);
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(248, 249, 250, 0.8) 100%);
    position: relative;
    
    .breadcrumb {
      margin-bottom: var(--spacing-lg);
      
      :deep(.el-breadcrumb__item) {
        .el-breadcrumb__inner {
          color: var(--text-color-secondary);
          font-weight: 500;
          transition: color 0.3s ease;
          
          &:hover {
            color: var(--primary-color);
          }
        }
        
        &:last-child .el-breadcrumb__inner {
          color: var(--text-color-primary);
        }
      }
    }
    
    .title {
      font-size: clamp(1.8rem, 4vw, 2.5rem);
      font-weight: 800;
      color: var(--text-color-primary);
      margin-bottom: var(--spacing-lg);
      line-height: 1.3;
      letter-spacing: -0.02em;
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -8px;
        left: 0;
        width: 80px;
        height: 3px;
        background: var(--primary-gradient);
        border-radius: var(--border-radius-full);
      }
    }
    
    .meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--spacing-lg);
      padding: var(--spacing-md) 0;
      
      .author-info {
        display: flex;
        align-items: center;
        gap: var(--spacing-md);
        
        .el-avatar {
          border: 2px solid var(--border-color-light);
          transition: all 0.3s ease;
          
          &:hover {
            border-color: var(--primary-color);
            transform: scale(1.05);
          }
        }
        
        .author-details {
          .name {
            font-weight: 600;
            color: var(--text-color-primary);
            font-size: 1.1rem;
            margin-bottom: 2px;
          }
          
          .time {
            font-size: 0.9rem;
            color: var(--text-color-secondary);
            font-weight: 500;
          }
        }
      }
      
      .stats {
        display: flex;
        gap: var(--spacing-lg);
        
        .stat-item {
          display: flex;
          align-items: center;
          gap: var(--spacing-xs);
          font-size: 0.9rem;
          color: var(--text-color-secondary);
          font-weight: 500;
          padding: var(--spacing-xs) var(--spacing-sm);
          border-radius: var(--border-radius-md);
          background: rgba(102, 126, 234, 0.1);
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(102, 126, 234, 0.2);
            color: var(--primary-color);
            transform: translateY(-2px);
          }
          
          .el-icon {
            font-size: 1rem;
          }
        }
      }
    }
    
    .tags {
      display: flex;
      flex-wrap: wrap;
      gap: var(--spacing-sm);
      
      .el-tag {
        cursor: pointer;
        border-radius: var(--border-radius-lg);
        font-weight: 500;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        border: 1px solid var(--border-color-light);
        
        &:hover {
          transform: translateY(-2px) scale(1.05);
          box-shadow: var(--shadow-md);
          border-color: var(--primary-color);
          background: var(--primary-color);
          color: var(--text-color-white);
        }
      }
    }
  }
  
  .article-body {
    padding: var(--spacing-2xl);
    
    .content {
      line-height: 1.8;
      color: var(--text-color-primary);
      font-size: 1.1rem;
      
      :deep(h2) {
        font-size: 1.6rem;
        font-weight: 700;
        margin: var(--spacing-2xl) 0 var(--spacing-lg);
        color: var(--text-color-primary);
        position: relative;
        padding-left: var(--spacing-md);
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 60%;
          background: var(--primary-gradient);
          border-radius: var(--border-radius-full);
        }
      }
      
      :deep(h3) {
        font-size: 1.3rem;
        font-weight: 600;
        margin: var(--spacing-xl) 0 var(--spacing-md);
        color: var(--text-color-primary);
      }
      
      :deep(p) {
        margin-bottom: var(--spacing-lg);
        text-align: justify;
      }
      
      :deep(ul) {
        margin: var(--spacing-lg) 0;
        padding-left: var(--spacing-xl);
        
        li {
          margin-bottom: var(--spacing-sm);
          position: relative;
          
          &::marker {
            color: var(--primary-color);
          }
        }
      }
      
      :deep(pre) {
        background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
        padding: var(--spacing-lg);
        border-radius: var(--border-radius-lg);
        overflow-x: auto;
        margin: var(--spacing-lg) 0;
        border: 1px solid var(--border-color-light);
        box-shadow: var(--shadow-sm);
        
        code {
          font-family: 'JetBrains Mono', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
          font-size: 0.9rem;
          line-height: 1.6;
        }
      }
      
      :deep(blockquote) {
        margin: var(--spacing-lg) 0;
        padding: var(--spacing-lg);
        border-left: 4px solid var(--primary-color);
        background: rgba(102, 126, 234, 0.05);
        border-radius: 0 var(--border-radius-lg) var(--border-radius-lg) 0;
        font-style: italic;
        color: var(--text-color-secondary);
      }
    }
  }
  
  .article-footer {
    padding: var(--spacing-xl) var(--spacing-2xl);
    border-top: 1px solid var(--border-color-light);
    background: linear-gradient(135deg, rgba(248, 249, 250, 0.5) 0%, rgba(255, 255, 255, 0.5) 100%);
    
    .actions {
      display: flex;
      gap: var(--spacing-md);
      justify-content: center;
      
      .el-button {
        border-radius: var(--border-radius-xl);
        font-weight: 600;
        padding: var(--spacing-md) var(--spacing-lg);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: var(--shadow-md);
        }
        
        &.is-liked {
          background: var(--primary-gradient);
          border-color: transparent;
          color: var(--text-color-white);
        }
      }
    }
  }
  
  .comments-section {
    margin-top: var(--spacing-2xl);
    background: var(--bg-color-white);
    border-radius: var(--border-radius-xl);
    padding: var(--spacing-2xl);
    box-shadow: var(--shadow-sm);
    border: 1px solid var(--border-color-light);
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: var(--primary-gradient);
      border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
    }
    
    h3 {
      margin-bottom: 20px;
      color: var(--el-text-color-primary);
    }
    
    .comment-form {
      margin-bottom: var(--spacing-2xl);
      
      .el-textarea {
        margin-bottom: var(--spacing-lg);
        
        :deep(.el-textarea__inner) {
          border-radius: var(--border-radius-lg);
          border: 2px solid var(--border-color-light);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          font-size: 1rem;
          line-height: 1.6;
          
          &:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
          }
        }
      }
      
      .form-actions {
        margin-top: 10px;
        text-align: right;
        
        .el-button {
          border-radius: var(--border-radius-xl);
          font-weight: 600;
          padding: var(--spacing-md) var(--spacing-xl);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: var(--shadow-md);
          }
        }
      }
    }
    
    .login-prompt {
      text-align: center;
      padding: var(--spacing-2xl);
      color: var(--text-color-secondary);
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(139, 92, 246, 0.05) 100%);
      border-radius: var(--border-radius-lg);
      border: 2px dashed var(--border-color-light);
      margin-bottom: 30px;
      
      a {
        color: var(--el-color-primary);
        text-decoration: none;
        
        &:hover {
          text-decoration: underline;
        }
      }
      
      .el-button {
        margin-top: var(--spacing-md);
        border-radius: var(--border-radius-xl);
        font-weight: 600;
      }
    }
    
    .comments-list {
      .comment-item {
        display: flex;
        gap: 12px;
        margin-bottom: var(--spacing-md);
        padding: var(--spacing-xl) 0;
        border-bottom: 1px solid var(--border-color-light);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        border-radius: var(--border-radius-lg);
        
        &:last-child {
          border-bottom: none;
          margin-bottom: 0;
        }
        
        &:hover {
          background: rgba(102, 126, 234, 0.02);
          transform: translateX(var(--spacing-xs));
        }
        
        .comment-content {
          flex: 1;
          
          .comment-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: var(--spacing-md);
            
            .username {
              font-weight: 700;
              color: var(--text-color-primary);
              font-size: 1.1rem;
            }
            
            .time {
              font-size: 0.9rem;
              color: var(--text-color-secondary);
              background: var(--bg-color-soft);
              padding: var(--spacing-xs) var(--spacing-sm);
              border-radius: var(--border-radius-md);
              display: inline-block;
            }
          }
          
          .comment-text {
            color: var(--text-color-regular);
            line-height: 1.7;
            font-size: 1rem;
            padding: var(--spacing-md);
            background: var(--bg-color-soft);
            border-radius: var(--border-radius-lg);
            border-left: 4px solid var(--primary-color);
            position: relative;
            
            &::before {
              content: '';
              position: absolute;
              top: var(--spacing-md);
              left: -12px;
              width: 0;
              height: 0;
              border-style: solid;
              border-width: 8px 12px 8px 0;
              border-color: transparent var(--bg-color-soft) transparent transparent;
            }
          }
        }
      }
    }
  }
  
  .not-found {
    padding: 60px 0;
  }
}

/* 响应式设计*/
@media (max-width: 768px) {
  .article-detail {
    margin: var(--spacing-sm);
    border-radius: var(--border-radius-lg);
    
    .article-header {
      padding: var(--spacing-lg);
      
      .breadcrumb {
        margin-bottom: var(--spacing-md);
        
        :deep(.el-breadcrumb__item) {
          font-size: 0.9rem;
        }
      }
      
      .title {
        font-size: 1.8rem;
        margin-bottom: var(--spacing-lg);
        line-height: 1.3;
      }
      
      .meta {
        flex-direction: column;
        gap: var(--spacing-md);
        
        .author-info {
          justify-content: center;
        }
        
        .stats {
          justify-content: center;
          flex-wrap: wrap;
        }
        
        .tags {
          justify-content: center;
        }
      }
    }
    
    .article-body {
      padding: var(--spacing-lg);
      
      .content {
        font-size: 1rem;
        
        :deep(h2) {
          font-size: 1.4rem;
          margin: var(--spacing-xl) 0 var(--spacing-md);
        }
        
        :deep(h3) {
          font-size: 1.2rem;
        }
        
        :deep(pre) {
          padding: var(--spacing-md);
          font-size: 0.85rem;
          overflow-x: auto;
        }
      }
    }
    
    .article-footer {
      padding: var(--spacing-lg);
      
      .actions {
        justify-content: center;
        flex-wrap: wrap;
        gap: var(--spacing-sm);
        
        .el-button {
          padding: var(--spacing-sm) var(--spacing-md);
          font-size: 0.9rem;
        }
      }
    }
    
    .comments-section {
      padding: var(--spacing-lg);
      margin-top: var(--spacing-lg);
      
      .comment-form {
        .el-textarea {
          :deep(.el-textarea__inner) {
            font-size: 0.9rem;
          }
        }
      }
      
      .comment-item {
        flex-direction: column;
        gap: var(--spacing-sm);
        
        .comment-content {
          .comment-header {
            margin-bottom: var(--spacing-sm);
            
            .username {
              font-size: 1rem;
            }
            
            .time {
              font-size: 0.8rem;
            }
          }
          
          .comment-text {
            font-size: 0.9rem;
            padding: var(--spacing-sm);
            margin-left: 0;
            
            &::before {
              display: none;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .article-detail {
    margin: var(--spacing-xs);
    
    .article-header {
      padding: var(--spacing-md);
      
      .title {
        font-size: 1.6rem;
      }
      
      .meta {
        .stats {
          .stat-item {
            font-size: 0.8rem;
            padding: var(--spacing-xs) var(--spacing-sm);
          }
        }
        
        .tags {
          .tag {
            font-size: 0.8rem;
            padding: var(--spacing-xs) var(--spacing-sm);
          }
        }
      }
    }
    
    .article-body {
      padding: var(--spacing-md);
      
      .content {
        :deep(h2) {
          font-size: 1.3rem;
        }
        
        :deep(h3) {
          font-size: 1.1rem;
        }
      }
    }
    
    .article-footer {
      padding: var(--spacing-md);
    }
    
    .comments-section {
      padding: var(--spacing-md);
    }
  }
}
</style>