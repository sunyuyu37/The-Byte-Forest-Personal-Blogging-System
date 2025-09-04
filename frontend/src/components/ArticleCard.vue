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
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, 
      rgba(74, 144, 226, 0.05) 0%, 
      rgba(80, 200, 120, 0.05) 100%);
    opacity: 0;
    transition: opacity 0.4s ease;
    pointer-events: none;
    z-index: 1;
  }
  
  &:hover {
    transform: translateY(-8px) scale(1.02);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
    background: rgba(255, 255, 255, 0.85);
    border-color: rgba(255, 255, 255, 0.6);
    
    &::before {
      opacity: 1;
    }
  }
  
  .cover {
    width: 100%;
    height: 220px;
    overflow: hidden;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 60px;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.1));
      z-index: 2;
    }
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
    }
    
    &:hover img {
      transform: scale(1.08);
    }
  }
  
  .content {
    padding: 24px;
    position: relative;
    z-index: 2;
    
    .category {
      margin-bottom: 12px;
    }
    
    .title {
      font-size: 19px;
      font-weight: 700;
      color: var(--el-text-color-primary);
      margin-bottom: 14px;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      transition: all 0.3s ease;
      
      &:hover {
        background: linear-gradient(135deg, #4a90e2 0%, #50c878 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        transform: translateX(2px);
      }
    }
    
    .summary {
      font-size: 14px;
      color: var(--el-text-color-regular);
      line-height: 1.7;
      margin-bottom: 18px;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
      opacity: 0.9;
    }
    
    .tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-bottom: 20px;
      
      :deep(.el-tag) {
        background: rgba(74, 144, 226, 0.1);
        border: 1px solid rgba(74, 144, 226, 0.2);
        color: #4a90e2;
        border-radius: 12px;
        font-size: 12px;
        padding: 4px 10px;
        transition: all 0.3s ease;
        
        &:hover {
          background: rgba(74, 144, 226, 0.15);
          transform: translateY(-1px);
        }
      }
      
      .more-tags {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        padding: 4px 8px;
        background: rgba(0, 0, 0, 0.05);
        border-radius: 10px;
        font-weight: 500;
      }
    }
    
    .meta {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 12px;
      color: var(--el-text-color-secondary);
      padding-top: 12px;
      border-top: 1px solid rgba(0, 0, 0, 0.06);
      
      .author {
        display: flex;
        align-items: center;
        gap: 8px;
        
        :deep(.el-avatar) {
          border: 2px solid rgba(255, 255, 255, 0.8);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        
        .name {
          font-weight: 600;
          color: var(--el-text-color-primary);
          font-size: 13px;
        }
      }
      
      .stats {
        display: flex;
        gap: 16px;
        
        .stat-item {
          display: flex;
          align-items: center;
          gap: 4px;
          padding: 4px 8px;
          background: rgba(0, 0, 0, 0.04);
          border-radius: 8px;
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(74, 144, 226, 0.1);
            color: #4a90e2;
            transform: translateY(-1px);
          }
          
          .el-icon {
            font-size: 14px;
          }
        }
      }
      
      .time {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
        font-weight: 500;
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