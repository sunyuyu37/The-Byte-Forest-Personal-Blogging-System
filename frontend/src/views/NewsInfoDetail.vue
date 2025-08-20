<template>
  <div class="news-info-detail">
    <div class="container">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="10" animated />
      </div>

      <div v-else-if="article" class="article-content">
        <!-- 新闻头部 -->
        <header class="news-header">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
              <el-breadcrumb-item to="/news-info">新闻资讯</el-breadcrumb-item>
              <el-breadcrumb-item>{{ article.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="news-category">
            <el-tag type="danger" size="large" effect="dark">
              <el-icon><BellFilled /></el-icon>
              新闻资讯
            </el-tag>
          </div>

          <h1 class="title">{{ article.title }}</h1>

          <div class="news-meta">
            <div class="author-info">
              <el-avatar :src="article.author?.avatar" :size="44">
                {{ article.author?.nickname?.charAt(0) }}
              </el-avatar>
              <div class="author-details">
                <div class="name">{{ article.author?.nickname }}</div>
                <div class="time">{{ formatTime(article.publishedAt) }}</div>
              </div>
            </div>
            <div class="news-stats">
              <span class="stat-item"><el-icon><View /></el-icon>{{ article.viewCount }} 阅读</span>
              <span class="stat-item"><el-icon><Star /></el-icon>{{ article.likeCount }} 点赞</span>
              <span class="stat-item"><el-icon><ChatDotRound /></el-icon>{{ article.commentCount || 0 }} 评论</span>
            </div>
          </div>

          <div class="news-tags" v-if="article.tags && article.tags.length">
            <el-tag
              v-for="tag in article.tags"
              :key="tag.id"
              type="danger"
              effect="plain"
              size="large"
              @click="$router.push(`/tag/${tag.slug}`)"
            >
              <el-icon><CollectionTag /></el-icon>
              {{ tag.name }}
            </el-tag>
          </div>
        </header>

        <!-- 新闻正文 -->
        <main class="news-body">
          <div class="content" v-html="article.content"></div>
        </main>

        <!-- 操作栏 -->
        <footer class="news-footer">
          <div class="actions">
            <el-button :type="isLiked ? 'danger' : 'default'" :icon="Star" @click="toggleLike">
              {{ isLiked ? '已点赞' : '点赞' }} ({{ article.likeCount }})
            </el-button>
            <el-button :icon="Share" @click="shareArticle">分享</el-button>
          </div>
        </footer>

        <!-- 评论区 -->
        <section class="news-comments-section">
          <h3>评论 ({{ article.commentCount || 0 }})</h3>

          <div v-if="userStore.isLoggedIn" class="comment-form">
            <el-input v-model="commentContent" type="textarea" :rows="4" placeholder="发表你的观点..." maxlength="500" show-word-limit />
            <div class="form-actions">
              <el-button type="danger" :loading="submittingComment" @click="submitComment">发表评论</el-button>
            </div>
          </div>

          <div v-else class="login-prompt">
            <p>
              <router-link to="/auth/login">登录</router-link>
              后参与评论
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
                  <el-button size="small" text :type="comment.isLiked ? 'danger' : 'default'" :icon="Star" @click="toggleCommentLike(comment)">
                    {{ comment.isLiked ? '已赞' : '点赞' }}
                    <span v-if="comment.likeCount > 0">({{ comment.likeCount }})</span>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 相关文章推荐 -->
        <aside class="related">
          <h3>相关文章推荐</h3>
          <div class="related-list">
            <div class="related-item" v-for="related in relatedArticles" :key="related.id" @click="$router.push(`/news-info/${related.slug}`)">
              <img :src="related.coverImage || '/default-news.jpg'" :alt="related.title" @error="onRelatedCoverError" />
              <div class="related-title">{{ related.title }}</div>
            </div>
          </div>
        </aside>
      </div>

      <div v-else class="not-found">
        <el-result icon="warning" title="新闻不存在" sub-title="抱歉，您访问的新闻不存在或已被删除">
          <template #extra>
            <el-button type="danger" @click="$router.push('/news-info')">返回新闻资讯</el-button>
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
import { View, Star, ChatDotRound, Share, BellFilled, CollectionTag } from '@element-plus/icons-vue'
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

    await Promise.all([
      loadComments(),
      loadRelatedArticles()
    ])
  } catch (error) {
    console.error('加载新闻失败:', error)
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

    // 若无标签或标签查询为空/失败，回退到分类 news
    if (!list.length) {
      const resByCat = await articleApi.getArticlesByCategory('news', { page: 0, size: 5 })
      list = resByCat.data?.content || resByCat.data || []
    }

    relatedArticles.value = list
      .filter(item => item.id !== article.value?.id)
      .slice(0, 3)
  } catch (error) {
    console.error('加载相关文章失败:', error)
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
    ElMessage.warning('请输入评论内容')
    return
  }
  submittingComment.value = true
  try {
    await commentApi.createComment({ articleId: article.value.id, content: commentContent.value })
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

// 修复：将相关文章图片错误兜底函数放入 <script setup> 中
const onRelatedCoverError = (e) => {
  e.target.src = '/vite.svg'
}

onMounted(loadArticle)
</script>

<style scoped>
.news-info-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff0f0 0%, #ffe6e6 100%);
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.loading { padding: 30px; }

.news-header {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(220, 53, 69, 0.1);
  border: 1px solid rgba(220, 53, 69, 0.15);
}

.title {
  font-size: 32px;
  font-weight: 800;
  color: #dc3545;
  margin: 16px 0;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 16px 0;
  padding: 16px;
  background: #fff5f5;
  border-radius: 8px;
}

.stat-item {
  margin-right: 16px;
  color: #dc3545;
}

.news-tags {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.news-body .content {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(220, 53, 69, 0.08);
  border: 1px solid rgba(220, 53, 69, 0.1);
}

.news-footer {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin: 24px 0;
  box-shadow: 0 4px 20px rgba(220, 53, 69, 0.08);
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.news-comments-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(220, 53, 69, 0.08);
  border: 1px solid rgba(220, 53, 69, 0.1);
}

.news-comments-section h3 { 
  color: #dc3545; 
  font-weight: 700;
  margin-bottom: 16px;
}

.comment-form { margin-bottom: 24px; }
.form-actions { display: flex; justify-content: flex-end; margin-top: 10px; }

.login-prompt { text-align: center; padding: 16px; background: #fff5f5; border-radius: 8px; margin-bottom: 16px; }
.login-prompt a { color: #dc3545; text-decoration: none; font-weight: 600; }

.comment-item { display: flex; gap: 10px; padding: 12px; background: #fff5f5; border-radius: 8px; border: 1px solid #f8d7da; }
.username { font-weight: 600; color: #dc3545; }
.time { color: #6c757d; font-size: 14px; }

/* 相关文章 */
.related { margin-top: 24px; background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 4px 20px rgba(220, 53, 69, 0.08); }
.related-list { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.related-item { cursor: pointer; border: 1px solid rgba(220, 53, 69, 0.1); border-radius: 8px; overflow: hidden; }
.related-item img { width: 100%; height: 88px; object-fit: cover; }
.related-title { padding: 8px; font-size: 14px; color: #333; }
</style>