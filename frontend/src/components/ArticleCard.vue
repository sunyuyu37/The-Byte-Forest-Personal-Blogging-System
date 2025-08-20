<template>
  <div class="article-card" @click="goToArticle">
    <!-- 封面图片 -->
    <div class="cover" v-if="safeCover">
      <img :src="safeCover" :alt="article.title" @error="onImageError" />
    </div>
    
    <!-- 文章内容 -->
    <div class="content">
      <!-- 分类标签 -->
      <div class="category" v-if="article.category">
        <el-tag size="small" type="primary">{{ article.category.name }}</el-tag>
      </div>
      
      <!-- 标题 -->
      <h3 class="title">{{ article.title }}</h3>
      
      <!-- 摘要 -->
      <p class="summary" v-if="article.summary">{{ article.summary }}</p>
      
      <!-- 标签 -->
      <div class="tags" v-if="article.tags && article.tags.length">
        <el-tag
          v-for="tag in article.tags.slice(0, 3)"
          :key="tag.id"
          size="small"
          type="info"
          effect="plain"
        >
          {{ tag.name }}
        </el-tag>
        <span v-if="article.tags.length > 3" class="more-tags">+{{ article.tags.length - 3 }}</span>
      </div>
      
      <!-- 文章信息 -->
      <div class="meta">
        <!-- 作者信息 -->
        <div class="author" v-if="article.author">
          <el-avatar :src="article.author.avatar" :size="24">
            {{ article.author.nickname?.charAt(0) }}
          </el-avatar>
          <span class="name">{{ article.author.nickname }}</span>
        </div>
        
        <!-- 统计信息 -->
        <div class="stats">
          <span class="stat-item">
            <el-icon><View /></el-icon>
            {{ formatNumber(article.viewCount) }}
          </span>
          <span class="stat-item">
            <el-icon><Star /></el-icon>
            {{ formatNumber(article.likeCount) }}
          </span>
          <span class="stat-item">
            <el-icon><ChatDotRound /></el-icon>
            {{ formatNumber(article.commentCount || 0) }}
          </span>
        </div>
        
        <!-- 发布时间 -->
        <div class="time">
          {{ formatTime(article.publishedAt || article.createdAt) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { View, Star, ChatDotRound } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
import { generateArticleCoverPlaceholder } from '@/utils/placeholder'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const props = defineProps({
  article: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const safeCover = computed(() => {
  const url = props.article?.coverImage
  const isExternalPlaceholder = typeof url === 'string' && /(^https?:)?\/\/via\.placeholder\.com/i.test(url)
  if (!url || isExternalPlaceholder) {
    return generateArticleCoverPlaceholder(350, 200)
  }
  return url
})

const onImageError = (e) => {
  const img = e?.target
  if (img) {
    img.onerror = null
    img.src = generateArticleCoverPlaceholder(350, 200)
  }
}

// 跳转到文章详情
const goToArticle = () => {
  router.push(`/article/${props.article.slug}`)
}

// 格式化数字
const formatNumber = (num) => {
  if (!num) return 0
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).fromNow()
}
</script>

<style lang="scss" scoped>
.article-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.35);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 16px 48px rgba(0, 0, 0, 0.18);
    background: rgba(255, 255, 255, 0.75);
    border-color: rgba(255, 255, 255, 0.5);
  }
  
  .cover {
    width: 100%;
    height: 200px;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }
    
    &:hover img {
      transform: scale(1.05);
    }
  }
  
  .content {
    padding: 20px;
    
    .category {
      margin-bottom: 12px;
    }
    
    .title {
      font-size: 18px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin-bottom: 12px;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      
      &:hover {
        color: var(--el-color-primary);
      }
    }
    
    .summary {
      font-size: 14px;
      color: var(--el-text-color-regular);
      line-height: 1.6;
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 16px;
      
      .more-tags {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        padding: 2px 6px;
      }
    }
    
    .meta {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 12px;
      color: var(--el-text-color-secondary);
      
      .author {
        display: flex;
        align-items: center;
        gap: 6px;
        
        .name {
          font-weight: 500;
        }
      }
      
      .stats {
        display: flex;
        gap: 12px;
        
        .stat-item {
          display: flex;
          align-items: center;
          gap: 2px;
          
          .el-icon {
            font-size: 14px;
          }
        }
      }
      
      .time {
        font-size: 12px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .article-card {
    .content {
      padding: 16px;
      
      .title {
        font-size: 16px;
      }
      
      .meta {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
        
        .stats {
          gap: 8px;
        }
      }
    }
  }
}
</style>