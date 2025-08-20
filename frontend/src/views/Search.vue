<template>
  <div class="search-page">
    <div class="container">
      <!-- 搜索头部 -->
      <div class="search-header">
        <h1>全局搜索</h1>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文章、标签、分类..."
            :prefix-icon="Search"
            size="large"
            @keyup.enter="handleSearch"
            clearable
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>
        
        <!-- 搜索统计 -->
        <div v-if="searchResults.length > 0 || hasSearched" class="search-stats">
          <p v-if="searchResults.length > 0">
            找到 <strong>{{ total }}</strong> 个结果，关键词：<strong>"{{ currentKeyword }}"</strong>
          </p>
          <p v-else-if="hasSearched">
            没有找到与 <strong>"{{ currentKeyword }}"</strong> 相关的结果
          </p>
        </div>
      </div>
      
      <!-- 高级搜索选项 -->
      <div class="advanced-search" v-show="showAdvanced">
        <el-card>
          <template #header>
            <span>高级搜索</span>
          </template>
          <el-form :model="advancedForm" label-width="80px">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="分类">
                  <el-select v-model="advancedForm.category" placeholder="选择分类" clearable>
                    <el-option
                      v-for="category in categories"
                      :key="category.id"
                      :label="category.name"
                      :value="category.slug"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="排序">
                  <el-select v-model="advancedForm.sortBy" placeholder="排序方式">
                    <el-option label="相关度" value="relevance" />
                    <el-option label="最新发布" value="latest" />
                    <el-option label="最多阅读" value="views" />
                    <el-option label="最多点赞" value="likes" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="时间范围">
                  <el-select v-model="advancedForm.timeRange" placeholder="选择时间范围" clearable>
                    <el-option label="最近一周" value="week" />
                    <el-option label="最近一月" value="month" />
                    <el-option label="最近三月" value="quarter" />
                    <el-option label="最近一年" value="year" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-button type="primary" @click="handleAdvancedSearch">搜索</el-button>
              <el-button @click="resetAdvancedForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
      
      <!-- 搜索选项工具栏 -->
      <div class="search-toolbar">
        <div class="toolbar-left">
          <el-button
            :type="showAdvanced ? 'primary' : ''"
            :icon="Setting"
            @click="showAdvanced = !showAdvanced"
          >
            高级搜索
          </el-button>
          
          <el-button-group>
            <el-button
              :type="searchType === 'all' ? 'primary' : ''"
              @click="changeSearchType('all')"
            >
              全部
            </el-button>
            <el-button
              :type="searchType === 'title' ? 'primary' : ''"
              @click="changeSearchType('title')"
            >
              标题
            </el-button>
            <el-button
              :type="searchType === 'content' ? 'primary' : ''"
              @click="changeSearchType('content')"
            >
              内容
            </el-button>
          </el-button-group>
        </div>
        
        <div class="toolbar-right">
          <el-select v-model="pageSize" @change="handlePageSizeChange" style="width: 120px">
            <el-option label="10条/页" :value="10" />
            <el-option label="20条/页" :value="20" />
            <el-option label="50条/页" :value="50" />
          </el-select>
        </div>
      </div>
      
      <!-- 搜索结果 -->
      <div class="search-results" v-loading="loading">
        <div v-if="searchResults.length > 0" class="results-list">
          <div
            v-for="article in searchResults"
            :key="article.id"
            class="result-item"
            @click="goToArticle(article.slug)"
          >
            <div class="result-content">
              <h3 class="result-title" v-html="highlightKeyword(article.title)"></h3>
              <p class="result-summary" v-html="highlightKeyword(article.summary)"></p>
              <div class="result-meta">
                <span class="category">{{ article.category?.name }}</span>
                <span class="author">{{ article.author?.nickname }}</span>
                <span class="date">{{ formatDate(article.publishedAt) }}</span>
                <span class="stats">
                  <el-icon><View /></el-icon>{{ article.viewCount }}
                  <el-icon><Star /></el-icon>{{ article.likeCount }}
                </span>
              </div>
            </div>
            <div v-if="getArticleCover(article)" class="result-image">
              <img :src="getArticleCover(article)" :alt="article.title" @error="onImageError" />
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="hasSearched && !loading" class="empty-state">
          <el-empty description="没有找到相关结果">
            <template #image>
              <el-icon size="100"><Search /></el-icon>
            </template>
            <el-button type="primary" @click="clearSearch">清空搜索</el-button>
          </el-empty>
        </div>
        
        <!-- 搜索建议 -->
        <div v-if="!hasSearched" class="search-suggestions">
          <h3>热门搜索</h3>
          <div class="suggestion-tags">
            <el-tag
              v-for="tag in hotSearchTags"
              :key="tag"
              @click="searchByTag(tag)"
              style="cursor: pointer; margin: 5px;"
            >
              {{ tag }}
            </el-tag>
          </div>
          
          <h3>最近搜索</h3>
          <div class="recent-searches">
            <el-tag
              v-for="search in recentSearches"
              :key="search"
              closable
              @click="searchByTag(search)"
              @close="removeRecentSearch(search)"
              style="cursor: pointer; margin: 5px;"
            >
              {{ search }}
            </el-tag>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Setting, View, Star } from '@element-plus/icons-vue'
import { articleApi } from '@/api/article'
import { categoryApi } from '@/api/category'
import { ElMessage } from 'element-plus'
import { generateArticleCoverPlaceholder } from '@/utils/placeholder'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const currentKeyword = ref('')
const searchResults = ref([])
const categories = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const hasSearched = ref(false)
const showAdvanced = ref(false)
const searchType = ref('all')

// 高级搜索表单
const advancedForm = ref({
  category: '',
  sortBy: 'relevance',
  timeRange: ''
})

// 搜索建议数据
const hotSearchTags = ref([])
const recentSearches = ref([])
const searchSuggestions = ref([])
const showSuggestions = ref(false)

// 获取安全的文章封面链接（过滤外链占位图并提供本地SVG占位回退）
const getArticleCover = (article) => {
  const url = article?.coverImage
  const isExternalPlaceholder = typeof url === 'string' && /(^https?:)?\/\/via\.placeholder\.com/i.test(url)
  if (!url || isExternalPlaceholder) {
    // 使用与结果图尺寸一致的占位图
    return generateArticleCoverPlaceholder(120, 80)
  }
  return url
}

// 图片加载失败时回退到本地SVG占位
const onImageError = (e) => {
  const img = e?.target
  if (img) {
    img.onerror = null
    img.src = generateArticleCoverPlaceholder(120, 80)
  }
}

// 执行搜索
const performSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  loading.value = true
  hasSearched.value = true
  currentKeyword.value = searchKeyword.value.trim()
  
  try {
    const params = {
      keyword: currentKeyword.value,
      page: currentPage.value - 1,
      size: pageSize.value,
      searchType: searchType.value,
      ...advancedForm.value
    }
    
    const response = await articleApi.searchArticles(params)
    searchResults.value = response.data.content
    total.value = response.data.totalElements
    
    // 保存到最近搜索
    addToRecentSearches(currentKeyword.value)
    
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  performSearch()
}

// 高级搜索
const handleAdvancedSearch = () => {
  currentPage.value = 1
  performSearch()
}

// 重置高级搜索表单
const resetAdvancedForm = () => {
  advancedForm.value = {
    category: '',
    sortBy: 'relevance',
    timeRange: ''
  }
}

// 改变搜索类型
const changeSearchType = (type) => {
  searchType.value = type
  if (hasSearched.value) {
    currentPage.value = 1
    performSearch()
  }
}

// 分页处理
const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  if (hasSearched.value) {
    performSearch()
  }
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  performSearch()
}

// 跳转到文章详情
const goToArticle = (slug) => {
  router.push(`/article/${slug}`)
}

// 高亮关键词
const highlightKeyword = (text) => {
  if (!currentKeyword.value || !text) return text
  const regex = new RegExp(`(${currentKeyword.value})`, 'gi')
  return text.replace(regex, '<mark>$1</mark>')
}

// 格式化日期
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 标签搜索
const searchByTag = (tag) => {
  searchKeyword.value = tag
  handleSearch()
}

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = ''
  currentKeyword.value = ''
  searchResults.value = []
  hasSearched.value = false
  total.value = 0
  currentPage.value = 1
}

// 最近搜索管理
const addToRecentSearches = (keyword) => {
  const searches = recentSearches.value.filter(s => s !== keyword)
  searches.unshift(keyword)
  recentSearches.value = searches.slice(0, 10)
  localStorage.setItem('recentSearches', JSON.stringify(recentSearches.value))
}

const removeRecentSearch = (keyword) => {
  recentSearches.value = recentSearches.value.filter(s => s !== keyword)
  localStorage.setItem('recentSearches', JSON.stringify(recentSearches.value))
}

// 加载分类
const loadCategories = async () => {
  try {
    const response = await categoryApi.getAllCategories()
    categories.value = response.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 加载热门搜索关键词
const loadHotKeywords = async () => {
  try {
    const response = await articleApi.getHotKeywords()
    hotSearchTags.value = response.data
  } catch (error) {
    console.error('加载热门搜索关键词失败:', error)
  }
}

// 监听路由变化
watch(() => route.query.q, (newKeyword) => {
  if (newKeyword) {
    searchKeyword.value = newKeyword
    handleSearch()
  }
}, { immediate: true })

// 初始化
onMounted(() => {
  // 从localStorage加载最近搜索
  const saved = localStorage.getItem('recentSearches')
  if (saved) {
    recentSearches.value = JSON.parse(saved)
  }
  
  loadCategories()
  loadHotKeywords()
  
  // 如果URL中有搜索参数，自动搜索
  if (route.query.q) {
    searchKeyword.value = route.query.q
    handleSearch()
  }
})
</script>

<style lang="scss" scoped>
.search-page {
  min-height: 100vh;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(255, 107, 107, 0.1) 50%, rgba(78, 205, 196, 0.1) 100%),
              linear-gradient(rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.4)), 
              url('/search.png');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  padding: 60px 0 80px;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at 20% 80%, rgba(64, 158, 255, 0.15) 0%, transparent 50%),
                radial-gradient(circle at 80% 20%, rgba(255, 107, 107, 0.15) 0%, transparent 50%),
                radial-gradient(circle at 40% 40%, rgba(78, 205, 196, 0.1) 0%, transparent 50%);
    pointer-events: none;
  }
  
  .container {
    position: relative;
    z-index: 1;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
  
  .search-header {
    text-align: center;
    margin-bottom: 40px;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 24px;
    padding: 50px 40px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), 
                0 8px 32px rgba(0, 0, 0, 0.1),
                inset 0 1px 0 rgba(255, 255, 255, 0.8);
    border: 1px solid rgba(255, 255, 255, 0.3);
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
      animation: shimmer 3s infinite;
    }
    
    @keyframes shimmer {
      0% { left: -100%; }
      100% { left: 100%; }
    }
    
    h1 {
      font-size: 3rem;
      color: #000;
      margin-bottom: 30px;
      font-weight: 700;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      letter-spacing: -0.02em;
      
      @media (max-width: 768px) {
        font-size: 2.2rem;
      }
    }
    
    .search-box {
      max-width: 700px;
      margin: 0 auto 30px;
      position: relative;
      z-index: 2;
      
      :deep(.el-input) {
        .el-input__wrapper {
          background: rgba(255, 255, 255, 0.95) !important;
          backdrop-filter: blur(15px);
          border: 2px solid rgba(255, 255, 255, 0.4);
          border-radius: 25px;
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(255, 255, 255, 0.98) !important;
            border-color: rgba(64, 158, 255, 0.5);
            box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
          }
          
          &.is-focus {
            background: rgba(255, 255, 255, 1.0) !important;
            border-color: var(--el-color-primary);
            box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1), 0 12px 40px rgba(0, 0, 0, 0.15);
          }
        }
        
        .el-input__inner {
          background: transparent !important;
          border: none;
          color: var(--el-text-color-primary);
          font-size: 16px;
          font-weight: 500;
          
          &::placeholder {
            color: rgba(0, 0, 0, 0.6);
            font-weight: 400;
          }
        }
        
        .el-input__prefix {
          color: var(--el-color-primary);
        }
        
        .el-input__suffix {
          color: var(--el-text-color-secondary);
        }
      }
      
      :deep(.el-input-group__append) {
        background: rgba(64, 158, 255, 0.9);
        backdrop-filter: blur(15px);
        border: 2px solid rgba(64, 158, 255, 0.3);
        border-left: none;
        border-radius: 0 25px 25px 0;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
        
        .el-button {
          background: transparent;
          border: none;
          color: white;
          font-weight: 600;
          
          &:hover {
            background: rgba(255, 255, 255, 0.2);
          }
        }
      }
    }
    
    .search-stats {
      color: var(--el-text-color-secondary);
      font-size: 15px;
      font-weight: 500;
      position: relative;
      z-index: 2;
      
      p {
        margin: 0;
        padding: 12px 24px;
        background: rgba(255, 255, 255, 0.6);
        backdrop-filter: blur(10px);
        border-radius: 20px;
        border: 1px solid rgba(255, 255, 255, 0.3);
        display: inline-block;
        
        strong {
          color: var(--el-color-primary);
          font-weight: 700;
        }
      }
    }
  }
  
  .advanced-search {
    margin-bottom: 30px;
    
    :deep(.el-card) {
      background: rgba(255, 255, 255, 0.98);
      backdrop-filter: blur(20px);
      border: 1px solid rgba(255, 255, 255, 0.3);
      border-radius: 20px;
      box-shadow: 0 15px 45px rgba(0, 0, 0, 0.12), 
                  0 8px 32px rgba(0, 0, 0, 0.08),
                  inset 0 1px 0 rgba(255, 255, 255, 0.6);
      overflow: hidden;
      
      .el-card__header {
        background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(255, 107, 107, 0.1) 100%);
        border-bottom: 1px solid rgba(255, 255, 255, 0.2);
        padding: 20px 24px;
        
        span {
          font-size: 16px;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }
      }
      
      .el-card__body {
         padding: 30px;
         background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(64, 158, 255, 0.05) 100%);
       }
       
       .el-form {
         .el-form-item {
           margin-bottom: 24px;
           
           .el-form-item__label {
             font-weight: 600;
             color: var(--el-text-color-primary);
             font-size: 14px;
           }
         }
         
         .el-select, .el-input {
           .el-input__wrapper {
             background: rgba(255, 255, 255, 0.9) !important;
             backdrop-filter: blur(15px);
             border: 2px solid rgba(255, 255, 255, 0.4);
             border-radius: 12px;
             box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
             transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
             
             &:hover {
               background: rgba(255, 255, 255, 0.95) !important;
               border-color: rgba(64, 158, 255, 0.4);
               box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
               transform: translateY(-1px);
             }
             
             &.is-focus {
               background: rgba(255, 255, 255, 1) !important;
               border-color: var(--el-color-primary);
               box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.15), 0 8px 24px rgba(0, 0, 0, 0.15);
               transform: translateY(-2px);
             }
           }
           
           .el-input__inner {
             background: transparent !important;
             border: none;
             color: var(--el-text-color-primary);
             font-weight: 500;
             
             &::placeholder {
               color: rgba(0, 0, 0, 0.5);
               font-weight: 400;
             }
           }
         }
         
         .el-button {
           padding: 12px 24px;
           border-radius: 12px;
           font-weight: 600;
           transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
           box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
           
           &.el-button--primary {
             background: linear-gradient(135deg, var(--el-color-primary) 0%, #74b9ff 100%);
             border: none;
             
             &:hover {
               background: linear-gradient(135deg, #74b9ff 0%, var(--el-color-primary) 100%);
               transform: translateY(-2px);
               box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
             }
             
             &:active {
               transform: translateY(0);
             }
           }
           
           &:not(.el-button--primary) {
             background: rgba(255, 255, 255, 0.9);
             backdrop-filter: blur(10px);
             border: 2px solid rgba(255, 255, 255, 0.4);
             color: var(--el-text-color-primary);
             
             &:hover {
               background: rgba(255, 255, 255, 1);
               border-color: rgba(64, 158, 255, 0.3);
               transform: translateY(-2px);
               box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
             }
           }
         }
       }
     }
   }
  
  .search-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding: 20px 25px;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 18px;
    box-shadow: 0 12px 36px rgba(0, 0, 0, 0.12), 
                0 6px 20px rgba(0, 0, 0, 0.08),
                inset 0 1px 0 rgba(255, 255, 255, 0.6);
    border: 1px solid rgba(255, 255, 255, 0.3);
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 2px;
      background: linear-gradient(90deg, #409eff, #ff6b6b, #4ecdc4);
      border-radius: 18px 18px 0 0;
    }
    
    .toolbar-left {
      display: flex;
      gap: 15px;
      align-items: center;
      
      .el-button {
        background: rgba(255, 255, 255, 0.8);
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.3);
        border-radius: 20px;
        color: var(--el-text-color-primary);
        font-weight: 500;
        padding: 8px 16px;
        transition: all 0.3s ease;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        
        &:hover {
          background: rgba(255, 255, 255, 0.9);
          border-color: rgba(64, 158, 255, 0.4);
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
        }
        
        &.is-type-primary {
          background: rgba(64, 158, 255, 0.9);
          backdrop-filter: blur(10px);
          border-color: rgba(64, 158, 255, 0.3);
          color: white;
          
          &:hover {
            background: rgba(64, 158, 255, 0.95);
            border-color: rgba(64, 158, 255, 0.5);
          }
        }
      }
      
      :deep(.el-button-group) {
        .el-button {
          background: rgba(255, 255, 255, 0.8);
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.3);
          color: var(--el-text-color-primary);
          font-weight: 500;
          transition: all 0.3s ease;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          
          &:first-child {
            border-radius: 15px 0 0 15px;
          }
          
          &:last-child {
            border-radius: 0 15px 15px 0;
          }
          
          &:not(:first-child):not(:last-child) {
            border-radius: 0;
          }
          
          &:hover {
            background: rgba(255, 255, 255, 0.9);
            border-color: rgba(64, 158, 255, 0.4);
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            z-index: 1;
          }
          
          &.is-type-primary {
            background: rgba(64, 158, 255, 0.9);
            backdrop-filter: blur(10px);
            border-color: rgba(64, 158, 255, 0.3);
            color: white;
            
            &:hover {
              background: rgba(64, 158, 255, 0.95);
              border-color: rgba(64, 158, 255, 0.5);
            }
          }
        }
      }
    }
    
    .toolbar-right {
      display: flex;
      gap: 15px;
      align-items: center;
      
      :deep(.el-select) {
        .el-input {
          .el-input__wrapper {
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
            border-radius: 15px;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            
            &:hover {
              background: rgba(255, 255, 255, 0.9);
              border-color: rgba(64, 158, 255, 0.4);
              box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
            }
            
            &.is-focus {
              background: rgba(255, 255, 255, 0.95);
              border-color: var(--el-color-primary);
              box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1), 0 6px 20px rgba(0, 0, 0, 0.15);
            }
          }
          
          .el-input__inner {
            background: transparent;
            border: none;
            color: var(--el-text-color-primary);
            font-weight: 500;
          }
          
          .el-input__suffix {
            color: var(--el-text-color-secondary);
          }
        }
      }
      
      :deep(.el-date-editor) {
        .el-input__wrapper {
          background: rgba(255, 255, 255, 0.8);
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.3);
          border-radius: 15px;
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(255, 255, 255, 0.9);
            border-color: rgba(64, 158, 255, 0.4);
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
          }
          
          &.is-focus {
            background: rgba(255, 255, 255, 0.95);
            border-color: var(--el-color-primary);
            box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1), 0 6px 20px rgba(0, 0, 0, 0.15);
          }
        }
        
        .el-input__inner {
          background: transparent;
          border: none;
          color: var(--el-text-color-primary);
          font-weight: 500;
        }
      }
    }
  }
  
  .results-list {
    .result-item {
      display: flex;
      padding: 25px;
      margin-bottom: 20px;
      background: rgba(255, 255, 255, 0.98);
      backdrop-filter: blur(20px);
      border-radius: 18px;
      cursor: pointer;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08), 
                  0 4px 12px rgba(0, 0, 0, 0.05),
                  inset 0 1px 0 rgba(255, 255, 255, 0.6);
      border: 1px solid rgba(255, 255, 255, 0.3);
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
        transition: left 0.6s ease;
      }
      
      &:hover {
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), 
                    0 12px 40px rgba(0, 0, 0, 0.1),
                    0 0 0 1px rgba(64, 158, 255, 0.2),
                    inset 0 1px 0 rgba(255, 255, 255, 0.8);
        transform: translateY(-8px) scale(1.02);
        background: rgba(255, 255, 255, 1);
        border-color: rgba(64, 158, 255, 0.3);
        
        &::before {
          left: 100%;
        }
        
        .result-content .result-title {
          color: var(--el-color-primary);
        }
        
        .result-image img {
          transform: scale(1.1);
          filter: brightness(1.1) saturate(1.2);
        }
      }
      
      &:active {
        transform: translateY(-4px) scale(1.01);
        transition: all 0.1s ease;
      }
      
      .result-content {
        flex: 1;
        
        .result-title {
          font-size: 1.3rem;
          color: var(--el-text-color-primary);
          margin-bottom: 12px;
          font-weight: 600;
          line-height: 1.4;
          transition: all 0.3s ease;
          
          :deep(mark) {
            background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
            color: #856404;
            padding: 3px 6px;
            border-radius: 6px;
            font-weight: 700;
            box-shadow: 0 2px 4px rgba(133, 100, 4, 0.2);
          }
        }
        
        .result-summary {
          color: var(--el-text-color-secondary);
          line-height: 1.7;
          margin-bottom: 18px;
          font-size: 14px;
          
          :deep(mark) {
            background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
            color: #856404;
            padding: 3px 6px;
            border-radius: 6px;
            font-weight: 600;
            box-shadow: 0 2px 4px rgba(133, 100, 4, 0.2);
          }
        }
        
        .result-meta {
          display: flex;
          gap: 18px;
          align-items: center;
          font-size: 13px;
          color: var(--el-text-color-placeholder);
          
          .category {
            background: linear-gradient(135deg, var(--el-color-primary) 0%, #74b9ff 100%);
            color: white;
            padding: 4px 12px;
            border-radius: 16px;
            font-weight: 600;
            font-size: 12px;
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
            transition: all 0.3s ease;
            
            &:hover {
              transform: translateY(-1px);
              box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
            }
          }
          
          .author, .date {
            font-weight: 500;
            transition: color 0.3s ease;
            
            &:hover {
              color: var(--el-text-color-secondary);
            }
          }
          
          .stats {
            display: flex;
            align-items: center;
            gap: 8px;
            font-weight: 500;
            
            .el-icon {
              margin-right: 2px;
              color: var(--el-color-primary);
            }
          }
        }
      }
      
      .result-image {
        width: 140px;
        height: 100px;
        margin-left: 25px;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          filter: brightness(0.95) saturate(0.9);
        }
        
        &:hover {
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
          transform: scale(1.05);
        }
      }
    }
  }
  
  .empty-state {
    text-align: center;
    padding: 80px 40px;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 24px;
    box-shadow: 0 12px 48px rgba(0, 0, 0, 0.12), 
                0 6px 24px rgba(0, 0, 0, 0.08),
                inset 0 1px 0 rgba(255, 255, 255, 0.6);
    border: 1px solid rgba(255, 255, 255, 0.3);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, 
        rgba(64, 158, 255, 0.03) 0%, 
        transparent 50%);
      animation: float 6s ease-in-out infinite;
    }
    
    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(45deg, 
        transparent 30%, 
        rgba(64, 158, 255, 0.02) 50%, 
        transparent 70%);
      background-size: 200% 200%;
      animation: shimmer 4s ease-in-out infinite;
    }
    
    :deep(.el-empty) {
      position: relative;
      z-index: 1;
      
      .el-empty__image {
        margin-bottom: 30px;
        
        .el-icon {
          font-size: 120px !important;
          color: var(--el-color-primary);
          opacity: 0.6;
          animation: pulse 2s ease-in-out infinite;
          filter: drop-shadow(0 4px 12px rgba(64, 158, 255, 0.2));
        }
      }
      
      .el-empty__description {
        font-size: 18px;
        color: var(--el-text-color-secondary);
        margin-bottom: 30px;
        font-weight: 500;
        line-height: 1.6;
      }
      
      .el-button {
        padding: 12px 32px;
        border-radius: 20px;
        font-weight: 600;
        font-size: 15px;
        background: linear-gradient(135deg, 
          var(--el-color-primary) 0%, 
          #74b9ff 100%);
        border: none;
        box-shadow: 0 6px 20px rgba(64, 158, 255, 0.3);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        overflow: hidden;
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, 
            transparent, 
            rgba(255, 255, 255, 0.3), 
            transparent);
          transition: left 0.6s ease;
        }
        
        &:hover {
          transform: translateY(-2px) scale(1.05);
          box-shadow: 0 8px 25px rgba(64, 158, 255, 0.4);
          
          &::before {
            left: 100%;
          }
        }
        
        &:active {
          transform: translateY(-1px) scale(1.02);
          transition: all 0.1s ease;
        }
      }
    }
  }
  
  @keyframes float {
    0%, 100% {
      transform: translate(0, 0) rotate(0deg);
    }
    33% {
      transform: translate(20px, -20px) rotate(120deg);
    }
    66% {
      transform: translate(-20px, 20px) rotate(240deg);
    }
  }
  
  @keyframes pulse {
    0%, 100% {
      transform: scale(1);
      opacity: 0.6;
    }
    50% {
      transform: scale(1.1);
      opacity: 0.8;
    }
  }
  
  .search-suggestions {
    padding: 35px;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12), 
                0 4px 16px rgba(0, 0, 0, 0.08),
                inset 0 1px 0 rgba(255, 255, 255, 0.6);
    border: 1px solid rgba(255, 255, 255, 0.3);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(90deg, 
        var(--el-color-primary) 0%, 
        #74b9ff 50%, 
        var(--el-color-primary) 100%);
      background-size: 200% 100%;
      animation: shimmer 3s ease-in-out infinite;
    }
    
    h3 {
      color: var(--el-text-color-primary);
      margin-bottom: 20px;
      margin-top: 30px;
      font-size: 1.1rem;
      font-weight: 600;
      position: relative;
      padding-left: 15px;
      
      &:first-child {
        margin-top: 0;
      }
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 20px;
        background: linear-gradient(135deg, var(--el-color-primary) 0%, #74b9ff 100%);
        border-radius: 2px;
      }
    }
    
    .suggestion-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      
      :deep(.el-tag) {
        cursor: pointer;
        padding: 8px 16px;
        border-radius: 20px;
        font-weight: 500;
        font-size: 13px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        overflow: hidden;
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, 
            transparent, 
            rgba(255, 255, 255, 0.2), 
            transparent);
          transition: left 0.5s ease;
        }
        
        // 蓝色系标签
        &:nth-child(6n+1) {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border: 1px solid rgba(102, 126, 234, 0.3);
          color: white;
          
          &:hover {
            background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
          }
        }
        
        // 紫色系标签
        &:nth-child(6n+2) {
          background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
          border: 1px solid rgba(168, 85, 247, 0.3);
          color: white;
          
          &:hover {
            background: linear-gradient(135deg, #9333ea 0%, #db2777 100%);
            box-shadow: 0 6px 20px rgba(168, 85, 247, 0.4);
          }
        }
        
        // 绿色系标签
        &:nth-child(6n+3) {
          background: linear-gradient(135deg, #10b981 0%, #059669 100%);
          border: 1px solid rgba(16, 185, 129, 0.3);
          color: white;
          
          &:hover {
            background: linear-gradient(135deg, #059669 0%, #047857 100%);
            box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
          }
        }
        
        // 橙色系标签
        &:nth-child(6n+4) {
          background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
          border: 1px solid rgba(249, 115, 22, 0.3);
          color: white;
          
          &:hover {
            background: linear-gradient(135deg, #ea580c 0%, #dc2626 100%);
            box-shadow: 0 6px 20px rgba(249, 115, 22, 0.4);
          }
        }
        
        // 青色系标签
        &:nth-child(6n+5) {
          background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
          border: 1px solid rgba(6, 182, 212, 0.3);
          color: white;
          
          &:hover {
            background: linear-gradient(135deg, #0891b2 0%, #0e7490 100%);
            box-shadow: 0 6px 20px rgba(6, 182, 212, 0.4);
          }
        }
        
        // 粉色系标签
        &:nth-child(6n+6) {
          background: linear-gradient(135deg, #ec4899 0%, #be185d 100%);
          border: 1px solid rgba(236, 72, 153, 0.3);
          color: white;
          
          &:hover {
            background: linear-gradient(135deg, #be185d 0%, #9d174d 100%);
            box-shadow: 0 6px 20px rgba(236, 72, 153, 0.4);
          }
        }
        
        &:hover {
          transform: translateY(-2px) scale(1.05);
          
          &::before {
            left: 100%;
          }
        }
        
        &:active {
          transform: translateY(-1px) scale(1.02);
          transition: all 0.1s ease;
        }
      }
    }
    
    .recent-searches {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      
      :deep(.el-tag) {
        cursor: pointer;
        padding: 8px 16px;
        border-radius: 20px;
        font-weight: 500;
        font-size: 13px;
        background: linear-gradient(135deg, 
          rgba(255, 248, 225, 0.9) 0%, 
          rgba(254, 240, 138, 0.9) 100%);
        border: 1px solid rgba(245, 158, 11, 0.3);
        color: #92400e;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        overflow: hidden;
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, 
            transparent, 
            rgba(245, 158, 11, 0.1), 
            transparent);
          transition: left 0.5s ease;
        }
        
        &:hover {
          transform: translateY(-2px) scale(1.05);
          background: linear-gradient(135deg, 
            #f59e0b 0%, 
            #fbbf24 100%);
          color: white;
          border-color: #f59e0b;
          box-shadow: 0 6px 20px rgba(245, 158, 11, 0.3),
                      0 2px 8px rgba(0, 0, 0, 0.1);
          
          &::before {
            left: 100%;
          }
        }
        
        &:active {
          transform: translateY(-1px) scale(1.02);
          transition: all 0.1s ease;
        }
        
        .el-tag__close {
          color: var(--el-text-color-placeholder);
          transition: all 0.3s ease;
          margin-left: 8px;
          
          &:hover {
            color: #ff4757;
            background: rgba(255, 71, 87, 0.1);
            border-radius: 50%;
            transform: scale(1.2);
          }
        }
      }
    }
    
    .recent-searches {
      :deep(.el-tag) {
        background: linear-gradient(135deg, 
          rgba(255, 248, 225, 0.9) 0%, 
          rgba(254, 240, 138, 0.9) 100%);
        border-color: rgba(245, 158, 11, 0.3);
        color: #92400e;
        
        &:hover {
          background: linear-gradient(135deg, 
            #f59e0b 0%, 
            #fbbf24 100%);
          color: white;
          border-color: #f59e0b;
          box-shadow: 0 6px 20px rgba(245, 158, 11, 0.3),
                      0 2px 8px rgba(0, 0, 0, 0.1);
        }
      }
    }
  }
  
  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 40px;
  }
}
</style>