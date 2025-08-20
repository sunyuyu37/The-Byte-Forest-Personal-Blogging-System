<template>
  <div class="tag-page">
    <div class="container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
            <el-breadcrumb-item>标签</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTag">
              {{ currentTag.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div v-if="currentTag" class="tag-info">
          <h1 class="title">
            <el-tag :type="getTagType(currentTag.articleCount)" size="large">
              {{ currentTag.name }}
            </el-tag>
          </h1>
          <p v-if="currentTag.description" class="description">
            {{ currentTag.description }}
          </p>
          <div class="stats">
            <span class="stat-item">
              <el-icon><Document /></el-icon>
              {{ currentTag.articleCount }} 篇文章
            </span>
          </div>
        </div>
        
        <div v-else class="all-tags">
          <h1 class="title">文章标签</h1>
          <p class="description">浏览所有文章标签</p>
        </div>
      </div>
      
      <!-- 标签云 (当没有选择特定标签时显示) -->
      <div v-if="!currentTag" class="tags-section">
        <div class="tags-cloud">
          <el-tag
            v-for="tag in tags"
            :key="tag.id"
            :type="getTagType(tag.articleCount)"
            :size="getTagSize(tag.articleCount)"
            class="tag-item"
            @click="$router.push(`/tag/${tag.slug}`)"
          >
            {{ tag.name }} ({{ tag.articleCount }})
          </el-tag>
        </div>
        
        <!-- 热门标签 -->
        <div class="popular-tags">
          <h3>热门标签</h3>
          <div class="popular-tags-list">
            <div
              v-for="tag in popularTags"
              :key="tag.id"
              class="popular-tag-card"
              @click="$router.push(`/tag/${tag.slug}`)"
            >
              <div class="tag-name">
                <el-tag :type="getTagType(tag.articleCount)">
                  {{ tag.name }}
                </el-tag>
              </div>
              <div class="tag-stats">
                {{ tag.articleCount }} 篇文章
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 文章列表 (当选择了特定标签时显示) -->
      <div v-else class="articles-section">
        <!-- 筛选和排序 -->
        <div class="filters">
          <div class="filter-group">
            <label>排序方式：</label>
            <el-select v-model="sortBy" @change="loadArticles">
              <el-option label="最新发布" value="latest" />
              <el-option label="最多阅读" value="popular" />
              <el-option label="最多点赞" value="liked" />
            </el-select>
          </div>
        </div>
        
        <!-- 文章列表 -->
        <div v-if="loading" class="loading">
          <el-skeleton v-for="i in 6" :key="i" :rows="3" animated />
        </div>
        
        <div v-else-if="articles.length" class="articles-list">
          <ArticleCard
            v-for="article in articles"
            :key="article.id"
            :article="article"
          />
        </div>
        
        <div v-else class="empty-state">
          <el-empty description="该标签下暂无文章" />
        </div>
        
        <!-- 分页 -->
        <div v-if="total > pageSize" class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next, jumper"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Document } from '@element-plus/icons-vue'
import ArticleCard from '@/components/ArticleCard.vue'
import tagApi, { getPublicAllTags } from '@/api/tag'
import articleApi from '@/api/article'

const route = useRoute()

const loading = ref(false)
const tags = ref([])
const currentTag = ref(null)
const articles = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const sortBy = ref('latest')

// 热门标签 (文章数量最多的前8个)
const popularTags = computed(() => {
  return [...tags.value]
    .sort((a, b) => b.articleCount - a.articleCount)
    .slice(0, 8)
})

// 根据文章数量获取标签类型
const getTagType = (count) => {
  if (count >= 20) return 'danger'
  if (count >= 10) return 'warning'
  if (count >= 5) return 'success'
  return 'info'
}

// 根据文章数量获取标签大小
const getTagSize = (count) => {
  if (count >= 20) return 'large'
  if (count >= 10) return 'default'
  return 'small'
}

// 加载所有标签
const loadTags = async () => {
  try {
    const res = await getPublicAllTags()
    if (res.code === 200 && Array.isArray(res.data)) {
      tags.value = res.data
    } else {
      tags.value = []
    }
  } catch (error) {
    console.error('加载标签失败:', error)
    tags.value = []
  }
}

// 加载特定标签信息
const loadTag = async (slug) => {
  try {
    if (!tags.value.length) await loadTags()
    const tag = tags.value.find(t => t.slug === slug)
    currentTag.value = tag || null
  } catch (error) {
    console.error('加载标签信息失败:', error)
    currentTag.value = null
  }
}

// 加载文章列表
const loadArticles = async () => {
  if (!currentTag.value) return
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
    }

    // 使用专门的标签文章接口，而不是搜索接口
    const res = await articleApi.getArticlesByTag(currentTag.value.slug, params)
     if (res.code === 200 && res.data) {
       articles.value = res.data.content || []
       total.value = res.data.totalElements || 0
     } else {
       articles.value = []
       total.value = 0
     }
  } catch (error) {
    console.error('加载文章失败:', error)
    // 仅在 404（接口不存在）时使用搜索接口作为兜底
    const status = error?.response?.status
    if (status === 404) {
      try {
        const params = {
          keyword: '',
          tag: currentTag.value.slug,
          page: currentPage.value - 1,
          size: pageSize.value,
          sortBy: sortBy.value === 'popular' ? 'views' : sortBy.value === 'liked' ? 'likes' : 'latest'
        }
        const searchRes = await articleApi.searchArticles(params)
        if (searchRes.code === 200 && searchRes.data) {
          articles.value = searchRes.data.content || []
          total.value = searchRes.data.totalElements || 0
        } else {
          articles.value = []
          total.value = 0
        }
      } catch (fallbackError) {
        console.error('搜索兜底失败:', fallbackError)
        articles.value = []
        total.value = 0
      }
    } else {
      // 非404错误，不启用兜底，维持空列表
      articles.value = []
      total.value = 0
    }
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadArticles()
}

watch(
  () => route.params.slug,
  async (newSlug) => {
    currentPage.value = 1
    await loadTag(newSlug)
    if (currentTag.value) {
      await loadArticles()
    } else {
      // 无特定标签，加载全量标签云
      await loadTags()
    }
  },
  { immediate: true }
)

onMounted(() => {
  // 初始加载根据路由执行，上面的 watch 已经 immediate
})
</script>

<style lang="scss" scoped>
.tag-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(120, 119, 198, 0.15) 0%, transparent 50%);
  padding: var(--spacing-xl) 0;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Ccircle cx='30' cy='30' r='2'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E") repeat;
    pointer-events: none;
  }
}

.page-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-2xl);
  margin-bottom: var(--spacing-xl);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #f5576c);
    background-size: 300% 100%;
    animation: gradientShift 3s ease-in-out infinite;
  }
  
  .breadcrumb {
    margin-bottom: var(--spacing-lg);
    
    :deep(.el-breadcrumb__item) {
      .el-breadcrumb__inner {
        color: var(--el-color-primary);
        font-weight: 500;
        transition: all 0.3s ease;
        
        &:hover {
          color: var(--el-color-primary-light-3);
        }
      }
      
      &:last-child .el-breadcrumb__inner {
        color: var(--el-text-color-primary);
      }
    }
  }
  
  .title {
    font-size: 2.5rem;
    font-weight: 700;
    color: var(--el-text-color-primary);
    margin-bottom: var(--spacing-md);
    line-height: 1.2;
    letter-spacing: -0.02em;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 0;
      width: 60px;
      height: 3px;
      background: linear-gradient(90deg, var(--el-color-primary), var(--el-color-success));
      border-radius: 2px;
      animation: slideIn 0.8s ease-out;
    }
    
    .el-tag {
      font-size: 1.5rem;
      padding: var(--spacing-md) var(--spacing-lg);
      border-radius: var(--border-radius-md);
      font-weight: 600;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
      }
    }
  }
  
  .description {
    color: var(--el-text-color-secondary);
    margin-bottom: var(--spacing-lg);
    line-height: 1.6;
    font-size: 1.1rem;
  }
  
  .stats {
    .stat-item {
      display: inline-flex;
      align-items: center;
      gap: var(--spacing-xs);
      color: var(--el-text-color-secondary);
      font-size: 1rem;
      font-weight: 500;
      background: rgba(var(--el-color-primary-rgb), 0.1);
      padding: var(--spacing-sm) var(--spacing-md);
      border-radius: var(--border-radius-md);
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(var(--el-color-primary-rgb), 0.15);
        transform: translateY(-1px);
      }
    }
  }
}

.tags-section {
  .tags-cloud {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: var(--border-radius-lg);
    padding: var(--spacing-2xl);
    margin-bottom: var(--spacing-xl);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 2px 8px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.2);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #f5576c);
      background-size: 300% 100%;
      animation: gradientShift 3s ease-in-out infinite;
    }
    
    .tag-item {
      margin: var(--spacing-sm);
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      border-radius: var(--border-radius-md);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      
      &:hover {
        transform: translateY(-3px) scale(1.05);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
      }
    }
  }
  
  .popular-tags {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: var(--border-radius-lg);
    padding: var(--spacing-2xl);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 2px 8px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.2);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #f5576c);
      background-size: 300% 100%;
      animation: gradientShift 3s ease-in-out infinite;
    }
    
    h3 {
      margin-bottom: var(--spacing-xl);
      color: var(--el-text-color-primary);
      font-size: 1.5rem;
      font-weight: 600;
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -8px;
        left: 0;
        width: 40px;
        height: 3px;
        background: linear-gradient(90deg, var(--el-color-primary), var(--el-color-success));
        border-radius: 2px;
      }
    }
    
    .popular-tags-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      gap: var(--spacing-lg);
      
      .popular-tag-card {
        padding: var(--spacing-xl);
        border: 1px solid rgba(var(--el-color-primary-rgb), 0.2);
        border-radius: var(--border-radius-lg);
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        text-align: center;
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.4) 100%);
        backdrop-filter: blur(10px);
        position: relative;
        overflow: hidden;
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
          transition: left 0.5s ease;
        }
        
        &:hover {
          border-color: var(--el-color-primary);
          transform: translateY(-5px);
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
          
          &::before {
            left: 100%;
          }
        }
        
        .tag-name {
          margin-bottom: var(--spacing-md);
          
          .el-tag {
            font-weight: 600;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            
            &:hover {
              transform: scale(1.05);
            }
          }
        }
        
        .tag-stats {
          color: var(--el-text-color-secondary);
          font-size: 0.95rem;
          font-weight: 500;
        }
      }
    }
  }
}

.articles-section {
  .filters {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: var(--border-radius-lg);
    padding: var(--spacing-xl);
    margin-bottom: var(--spacing-xl);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 2px 8px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.2);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #f5576c);
      background-size: 300% 100%;
      animation: gradientShift 3s ease-in-out infinite;
    }
    
    .filter-group {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);
      
      label {
        font-weight: 600;
        color: var(--el-text-color-primary);
        font-size: 1rem;
      }
      
      .el-select {
        min-width: 150px;
        
        :deep(.el-input__wrapper) {
          border-radius: var(--border-radius-md);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;
          
          &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          }
        }
      }
    }
  }
  
  .loading {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: var(--border-radius-lg);
    padding: var(--spacing-xl);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 2px 8px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    .el-skeleton {
      margin-bottom: var(--spacing-xl);
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
  
  .articles-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
    gap: var(--spacing-xl);
    margin-bottom: var(--spacing-2xl);
  }
  
  .empty-state {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: var(--border-radius-lg);
    padding: var(--spacing-3xl) var(--spacing-xl);
    text-align: center;
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 2px 8px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.2);
  }
  
  .pagination {
    display: flex;
    justify-content: center;
    padding: var(--spacing-xl) 0;
    
    :deep(.el-pagination) {
      .el-pager li {
        border-radius: var(--border-radius-md);
        margin: 0 var(--spacing-xs);
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }
        
        &.is-active {
          background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
          color: white;
          box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.4);
        }
      }
      
      .btn-prev,
      .btn-next {
        border-radius: var(--border-radius-md);
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .tag-page {
    padding: var(--spacing-md) 0;
  }
  
  .tags-section {
    .tags-cloud {
      padding: var(--spacing-lg);
      margin-bottom: var(--spacing-lg);
      
      .tag-item {
        margin: var(--spacing-xs);
      }
    }
    
    .popular-tags {
      padding: var(--spacing-lg);
      
      h3 {
        font-size: 1.3rem;
        margin-bottom: var(--spacing-lg);
      }
      
      .popular-tags-list {
        grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
        gap: var(--spacing-md);
        
        .popular-tag-card {
          padding: var(--spacing-lg);
        }
      }
    }
  }
  
  .articles-section {
    .articles-list {
      grid-template-columns: 1fr;
      gap: var(--spacing-lg);
    }
    
    .filters {
      padding: var(--spacing-lg);
      
      .filter-group {
        flex-direction: column;
        align-items: flex-start;
        gap: var(--spacing-md);
        
        .el-select {
          width: 100%;
        }
      }
    }
    
    .loading {
      padding: var(--spacing-lg);
    }
    
    .empty-state {
      padding: var(--spacing-2xl) var(--spacing-lg);
    }
  }
  
  .page-header {
    padding: var(--spacing-lg);
    margin-bottom: var(--spacing-lg);
    
    .title {
      font-size: 2rem;
      
      .el-tag {
        font-size: 1.3rem;
        padding: var(--spacing-sm) var(--spacing-md);
      }
    }
    
    .description {
      font-size: 1rem;
    }
  }
}

@media (max-width: 480px) {
  .tag-page {
    padding: var(--spacing-sm) 0;
  }
  
  .tags-section {
    .tags-cloud {
      padding: var(--spacing-md);
      
      .tag-item {
        margin: var(--spacing-xs);
        font-size: 0.9rem;
      }
    }
    
    .popular-tags {
      padding: var(--spacing-md);
      
      h3 {
        font-size: 1.2rem;
        margin-bottom: var(--spacing-md);
      }
      
      .popular-tags-list {
        grid-template-columns: 1fr;
        gap: var(--spacing-sm);
        
        .popular-tag-card {
          padding: var(--spacing-md);
          
          .tag-stats {
            font-size: 0.9rem;
          }
        }
      }
    }
  }
  
  .articles-section {
    .articles-list {
      gap: var(--spacing-md);
    }
    
    .filters {
      padding: var(--spacing-md);
    }
    
    .loading {
      padding: var(--spacing-md);
    }
    
    .empty-state {
      padding: var(--spacing-xl) var(--spacing-md);
    }
  }
  
  .page-header {
    padding: var(--spacing-md);
    
    .title {
      font-size: 1.8rem;
      
      .el-tag {
        font-size: 1.1rem;
        padding: var(--spacing-xs) var(--spacing-sm);
      }
    }
    
    .description {
      font-size: 0.95rem;
    }
    
    .stats {
      .stat-item {
        font-size: 0.9rem;
        padding: var(--spacing-xs) var(--spacing-sm);
      }
    }
  }
}
</style>