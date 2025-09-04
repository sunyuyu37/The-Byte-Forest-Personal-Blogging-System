<template>
  <div class="articles-page">
    <div class="container">
      <div class="page-header">
        <h1>文章列表</h1>
        <p>探索精彩内容，发现有趣观点</p>
      </div>
      
      <!-- 搜索和筛选 -->
      <div class="filters">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文章..."
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
            clearable
          />
        </div>
        
        <div class="filter-options">
          <el-select v-model="selectedCategory" placeholder="选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.slug"
            />
          </el-select>
          
          <el-select v-model="sortBy" placeholder="排序方式">
            <el-option label="最新发布" value="latest" />
            <el-option label="最多阅读" value="views" />
            <el-option label="最多点赞" value="likes" />
          </el-select>
        </div>
      </div>
      
      <!-- 文章列表 -->
      <div class="articles-content" v-loading="loading">
        <div class="articles-grid">
          <article-card
            v-for="article in articles"
            :key="article.id"
            :article="article"
          />
        </div>
        
        <!-- 分页 -->
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
        
        <!-- 空状态 -->
        <div v-if="!loading && articles.length === 0" class="empty-state">
          <el-empty description="暂无文章" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { generateArticleCoverPlaceholder } from '@/utils/placeholder'
import { articleApi } from '@/api/article'
import { categoryApi } from '@/api/category'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const articles = ref([])
const categories = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const searchKeyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('latest')

// 加载文章列表
const loadArticles = async () => {
  loading.value = true
  try {
    // 构建请求参数
    const sortKey =
      sortBy.value === 'latest'
        ? 'publishedAt'
        : sortBy.value === 'views'
          ? 'viewCount'
          : sortBy.value === 'likes'
            ? 'likeCount'
            : 'publishedAt'

    const params = {
      page: currentPage.value - 1, // 后端从0开始分页
      size: pageSize.value,
      sortBy: sortKey,
      sortDir: 'desc'
    }

    let response

    if (searchKeyword.value.trim()) {
      // 搜索模式
      params.keyword = searchKeyword.value.trim()
      params.sortBy = getSortByForSearch(sortBy.value)
      if (selectedCategory.value) {
        params.category = selectedCategory.value
      }
      response = await articleApi.searchArticles(params)
    } else if (selectedCategory.value) {
      // 分类筛选模式
      delete params.sortBy // 按分类查询使用默认排序
      delete params.sortDir
      response = await articleApi.getArticlesByCategory(selectedCategory.value, params)
    } else {
      // 默认获取已发布文章列表
      response = await articleApi.getPublishedArticles(params)
    }

    if (response && response.data) {
      const pageData = response.data
      articles.value = pageData.content || []
      total.value = pageData.totalElements || 0
    } else {
      articles.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败，请稍后重试')
    articles.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await categoryApi.getAllCategories()
    if (response && response.data) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  }
}

// 获取搜索排序参数
const getSortByForSearch = (sortValue) => {
  switch (sortValue) {
    case 'latest':
      return 'latest'
    case 'views':
      return 'views'
    case 'likes':
      return 'likes'
    default:
      return 'relevance'
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadArticles()
}

// 页面大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadArticles()
}

// 当前页改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  loadArticles()
}

// 监听筛选条件变化
watch([selectedCategory, sortBy], () => {
  currentPage.value = 1
  loadArticles()
})

// 初始化
onMounted(() => {
  // 从URL参数获取搜索关键词
  if (route.query.search) {
    searchKeyword.value = route.query.search
  }
  
  loadCategories()
  loadArticles()
})
</script>

<style lang="scss" scoped>
.articles-page {
  min-height: 100vh;
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.1) 0%, rgba(80, 200, 120, 0.1) 100%), 
              var(--el-bg-color-page) url('/articles.png') no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
  padding: 60px 0;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, 
      rgba(255, 255, 255, 0.05) 0%, 
      rgba(255, 255, 255, 0.02) 50%, 
      rgba(255, 255, 255, 0.05) 100%);
    pointer-events: none;
  }
  
  .page-header {
    text-align: center;
    margin-bottom: 50px;
    position: relative;
    z-index: 1;
    
    h1 {
      font-size: 3rem;
      background: linear-gradient(135deg, #4a90e2 0%, #50c878 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 15px;
      font-weight: 700;
      letter-spacing: -0.02em;
      text-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      animation: fadeInUp 0.8s ease-out;
    }
    
    p {
      font-size: 1.2rem;
      color: var(--el-text-color-regular);
      opacity: 0.9;
      animation: fadeInUp 0.8s ease-out 0.2s both;
    }
  }
  
  .filters {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;
    gap: 20px;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    padding: 20px 30px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    position: relative;
    z-index: 1;
    animation: slideInDown 0.6s ease-out 0.4s both;
    
    .search-box {
      flex: 1;
      max-width: 400px;
      
      :deep(.el-input) {
        .el-input__wrapper {
          background: rgba(255, 255, 255, 0.8);
          border-radius: 12px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
          }
        }
      }
    }
    
    .filter-options {
      display: flex;
      gap: 15px;
      
      .el-select {
        width: 150px;
        
        :deep(.el-input) {
          .el-input__wrapper {
            background: rgba(255, 255, 255, 0.8);
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            
            &:hover {
              background: rgba(255, 255, 255, 0.9);
              box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
            }
          }
        }
      }
    }
  }
  
  .articles-content {
    position: relative;
    z-index: 1;
    
    .articles-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
      gap: 35px;
      margin-bottom: 50px;
      animation: fadeIn 0.8s ease-out 0.6s both;
      
      .article-card {
        animation: slideInUp 0.6s ease-out both;
        
        &:nth-child(1) { animation-delay: 0.1s; }
        &:nth-child(2) { animation-delay: 0.2s; }
        &:nth-child(3) { animation-delay: 0.3s; }
        &:nth-child(4) { animation-delay: 0.4s; }
        &:nth-child(5) { animation-delay: 0.5s; }
        &:nth-child(6) { animation-delay: 0.6s; }
      }
    }
    
    .pagination {
      display: flex;
      justify-content: center;
      
      :deep(.el-pagination) {
        background: rgba(255, 255, 255, 0.2);
        backdrop-filter: blur(20px);
        -webkit-backdrop-filter: blur(20px);
        border-radius: 16px;
        padding: 12px 20px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        
        .el-pager li {
          background: rgba(255, 255, 255, 0.6);
          border-radius: 8px;
          margin: 0 2px;
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(255, 255, 255, 0.8);
            transform: translateY(-2px);
          }
          
          &.is-active {
            background: linear-gradient(135deg, #4a90e2 0%, #50c878 100%);
            color: white;
            transform: translateY(-2px);
          }
        }
        
        .btn-prev, .btn-next {
          background: rgba(255, 255, 255, 0.6);
          border-radius: 8px;
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(255, 255, 255, 0.8);
            transform: translateY(-2px);
          }
        }
      }
    }
    
    .empty-state {
      text-align: center;
      padding: 80px 0;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      border-radius: 20px;
      margin: 20px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      
      .el-empty {
        :deep(.el-empty__description) {
          color: var(--el-text-color-regular);
          font-size: 16px;
        }
      }
    }
  }
}

// 动画关键帧
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .articles-page {
    padding: 30px 0;
    
    .page-header {
      margin-bottom: 30px;
      
      h1 {
        font-size: 2.2rem;
      }
      
      p {
        font-size: 1rem;
      }
    }
    
    .filters {
      flex-direction: column;
      align-items: stretch;
      padding: 15px 20px;
      margin-bottom: 30px;
      
      .search-box {
        max-width: none;
        margin-bottom: 15px;
      }
      
      .filter-options {
        justify-content: space-between;
        
        .el-select {
          flex: 1;
          margin: 0 5px;
        }
      }
    }
    
    .articles-content {
      .articles-grid {
        grid-template-columns: 1fr;
        gap: 25px;
        margin-bottom: 30px;
      }
      
      .empty-state {
        margin: 10px;
        padding: 60px 20px;
      }
    }
  }
}

@media (max-width: 480px) {
  .articles-page {
    padding: 20px 0;
    
    .page-header {
      h1 {
        font-size: 1.8rem;
      }
    }
    
    .filters {
      padding: 12px 15px;
      
      .filter-options {
        flex-direction: column;
        gap: 10px;
        
        .el-select {
          margin: 0;
        }
      }
    }
    
    .articles-content {
      .articles-grid {
        grid-template-columns: 1fr;
        gap: 20px;
      }
    }
  }
}
</style>