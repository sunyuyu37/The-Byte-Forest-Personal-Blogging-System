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
  background: var(--el-bg-color-page);
  padding: 40px 0;
  
  .page-header {
    text-align: center;
    margin-bottom: 40px;
    
    h1 {
      font-size: 2.5rem;
      color: var(--el-text-color-primary);
      margin-bottom: 10px;
    }
    
    p {
      font-size: 1.1rem;
      color: var(--el-text-color-secondary);
    }
  }
  
  .filters {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    gap: 20px;
    
    .search-box {
      flex: 1;
      max-width: 400px;
    }
    
    .filter-options {
      display: flex;
      gap: 15px;
      
      .el-select {
        width: 150px;
      }
    }
  }
  
  .articles-content {
    .articles-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 30px;
      margin-bottom: 40px;
    }
    
    .pagination {
      display: flex;
      justify-content: center;
    }
    
    .empty-state {
      text-align: center;
      padding: 60px 0;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .articles-page {
    padding: 20px 0;
    
    .page-header {
      h1 {
        font-size: 2rem;
      }
    }
    
    .filters {
      flex-direction: column;
      align-items: stretch;
      
      .search-box {
        max-width: none;
      }
      
      .filter-options {
        justify-content: space-between;
        
        .el-select {
          flex: 1;
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