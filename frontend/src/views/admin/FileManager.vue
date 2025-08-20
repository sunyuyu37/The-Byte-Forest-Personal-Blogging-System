<template>
  <div class="file-manager">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>
            <el-icon class="title-icon"><Folder /></el-icon>
            文件管理
          </h1>
          <p>管理您上传的所有文件，包括图片、视频等</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="refreshFiles">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 文件统计 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ fileStats.totalFiles || 0 }}</div>
            <div class="stat-label">总文件数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Picture /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ fileStats.imageFiles || 0 }}</div>
            <div class="stat-label">图片文件</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><VideoCamera /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ fileStats.videoFiles || 0 }}</div>
            <div class="stat-label">视频文件</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Coin /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ formatFileSize(fileStats.totalSize || 0) }}</div>
            <div class="stat-label">总大小</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="filter-content">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文件名..."
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
            clearable
          />
        </div>
        <div class="filter-options">
          <el-select v-model="fileTypeFilter" placeholder="文件类型" clearable>
            <el-option label="全部" value="" />
            <el-option label="图片" value="image" />
            <el-option label="视频" value="video" />
            <el-option label="文档" value="document" />
          </el-select>
          <el-select v-model="sortBy" placeholder="排序方式">
            <el-option label="上传时间" value="createdAt" />
            <el-option label="文件名" value="originalName" />
            <el-option label="文件大小" value="fileSize" />
          </el-select>
          <el-select v-model="sortDir" placeholder="排序方向">
            <el-option label="降序" value="desc" />
            <el-option label="升序" value="asc" />
          </el-select>
        </div>
      </div>
    </div>

    <!-- 文件列表 -->
    <div class="files-section">
      <div class="view-toggle">
        <el-radio-group v-model="viewMode">
          <el-radio-button label="grid">
            <el-icon><Grid /></el-icon>
            网格视图
          </el-radio-button>
          <el-radio-button label="list">
            <el-icon><List /></el-icon>
            列表视图
          </el-radio-button>
        </el-radio-group>
      </div>

      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="files-grid" v-loading="loading">
        <div
          v-for="file in files"
          :key="file.id"
          class="file-card"
          @click="previewFile(file)"
        >
          <div class="file-preview">
            <!-- 图片预览 -->
            <img
              v-if="file.fileType === 'image'"
              :src="file.fileUrl"
              :alt="file.originalName"
              class="preview-image"
            />
            <!-- 视频预览 -->
            <div v-else-if="file.fileType === 'video'" class="video-preview">
              <video
                :src="file.fileUrl"
                class="preview-video"
                muted
                @mouseenter="playVideo"
                @mouseleave="pauseVideo"
              >
                您的浏览器不支持视频播放
              </video>
              <div class="video-overlay">
                <el-icon class="play-icon"><VideoPlay /></el-icon>
              </div>
            </div>
            <!-- 其他文件类型 -->
            <div v-else class="file-icon">
              <el-icon><Document /></el-icon>
            </div>
          </div>
          <div class="file-info">
            <div class="file-name" :title="file.originalName">
              {{ file.originalName }}
            </div>
            <div class="file-meta">
              <span class="file-size">{{ formatFileSize(file.fileSize) }}</span>
              <span class="file-date">{{ formatDate(file.createdAt) }}</span>
            </div>
          </div>
          <div class="file-actions">
            <el-button
              size="small"
              type="primary"
              @click.stop="copyFileUrl(file)"
            >
              <el-icon><Link /></el-icon>
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click.stop="deleteFileConfirm(file)"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-else class="files-table">
        <el-table :data="files" v-loading="loading">
          <el-table-column label="预览" width="80">
            <template #default="{ row }">
              <div class="table-preview">
                <img
                  v-if="row.fileType === 'image'"
                  :src="row.fileUrl"
                  :alt="row.originalName"
                  class="table-preview-image"
                />
                <el-icon v-else-if="row.fileType === 'video'" class="table-preview-icon">
                  <VideoCamera />
                </el-icon>
                <el-icon v-else class="table-preview-icon">
                  <Document />
                </el-icon>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="originalName" label="文件名" min-width="200" />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getFileTypeTagType(row.fileType)">
                {{ getFileTypeLabel(row.fileType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="大小" width="120">
            <template #default="{ row }">
              {{ formatFileSize(row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column label="视频信息" width="180" v-if="currentFilter === 'video'">
            <template #default="{ row }">
              <div v-if="row.fileType === 'video' && row.videoDuration" class="video-metadata">
                <div class="metadata-item">
                  <el-icon><Timer /></el-icon>
                  {{ formatDuration(row.videoDuration) }}
                </div>
                <div class="metadata-item" v-if="row.videoWidth && row.videoHeight">
                  <el-icon><Monitor /></el-icon>
                  {{ row.videoWidth }}×{{ row.videoHeight }}
                </div>
              </div>
              <span v-else-if="row.fileType === 'video'">解析中...</span>
            </template>
          </el-table-column>
          <el-table-column label="上传时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button
                size="small"
                type="primary"
                @click="previewFile(row)"
              >
                预览
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click="deleteFileConfirm(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 文件预览对话框 -->
    <el-dialog
      v-model="showPreviewDialog"
      :title="currentPreviewFile?.originalName"
      width="80%"
      center
    >
      <div class="preview-content">
        <!-- 图片预览 -->
        <img
          v-if="currentPreviewFile?.fileType === 'image'"
          :src="currentPreviewFile.fileUrl"
          :alt="currentPreviewFile.originalName"
          class="preview-full-image"
        />
        <!-- 视频预览 -->
        <video
          v-else-if="currentPreviewFile?.fileType === 'video'"
          :src="currentPreviewFile.fileUrl"
          controls
          class="preview-full-video"
        >
          您的浏览器不支持视频播放
        </video>
        <!-- 其他文件类型 -->
        <div v-else class="preview-other">
          <el-icon class="large-icon"><Document /></el-icon>
          <p>{{ currentPreviewFile?.originalName }}</p>
          <p>文件大小: {{ formatFileSize(currentPreviewFile?.fileSize) }}</p>
        </div>
      </div>
      <template #footer>
        <div class="preview-actions">
          <el-button @click="copyFileUrl(currentPreviewFile)">
            <el-icon><Link /></el-icon>
            复制链接
          </el-button>
          <el-button @click="downloadFile(currentPreviewFile)">
            <el-icon><Download /></el-icon>
            下载文件
          </el-button>
          <el-button @click="showPreviewDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Folder, Refresh, Document, Picture, VideoCamera, Coin,
  Search, Grid, List, VideoPlay, Link, Delete, Download,
  Timer, Monitor
} from '@element-plus/icons-vue'
import { fileApi } from '@/api/file'

// 响应式数据
const loading = ref(false)
const files = ref([])
const fileStats = ref({})
const searchKeyword = ref('')
const fileTypeFilter = ref('')
const sortBy = ref('createdAt')
const sortDir = ref('desc')
const viewMode = ref('grid')
const showPreviewDialog = ref(false)
const currentPreviewFile = ref(null)

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 加载文件列表
const loadFiles = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      sortBy: sortBy.value,
      sortDir: sortDir.value
    }

    let response
    if (searchKeyword.value) {
      response = await fileApi.searchFiles(searchKeyword.value, params)
    } else {
      response = await fileApi.getUserFiles(params)
    }

    files.value = response.data.content || []
    pagination.total = response.data.totalElements || 0
  } catch (error) {
    console.error('加载文件列表失败:', error)
    ElMessage.error('加载文件列表失败')
  } finally {
    loading.value = false
  }
}

// 加载文件统计
const loadFileStats = async () => {
  try {
    const response = await fileApi.getFileStats()
    fileStats.value = response.data || {}
  } catch (error) {
    console.error('加载文件统计失败:', error)
  }
}

// 搜索文件
const handleSearch = () => {
  pagination.page = 1
  loadFiles()
}

// 刷新文件列表
const refreshFiles = () => {
  loadFiles()
  loadFileStats()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadFiles()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadFiles()
}

// 预览文件
const previewFile = (file) => {
  currentPreviewFile.value = file
  showPreviewDialog.value = true
}

// 复制文件链接
const copyFileUrl = async (file) => {
  try {
    await navigator.clipboard.writeText(file.fileUrl)
    ElMessage.success('文件链接已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
    ElMessage.error('复制失败')
  }
}

// 下载文件
const downloadFile = (file) => {
  const link = document.createElement('a')
  link.href = file.fileUrl
  link.download = file.originalName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 删除文件确认
const deleteFileConfirm = async (file) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文件 "${file.originalName}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await fileApi.deleteFile(file.id)
    ElMessage.success('文件删除成功')
    refreshFiles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文件失败:', error)
      ElMessage.error('删除文件失败')
    }
  }
}

// 视频播放控制
const playVideo = (event) => {
  const video = event.target
  if (video.tagName === 'VIDEO') {
    video.play()
  }
}

const pauseVideo = (event) => {
  const video = event.target
  if (video.tagName === 'VIDEO') {
    video.pause()
    video.currentTime = 0
  }
}

// 工具函数
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getFileTypeLabel = (type) => {
  const labels = {
    image: '图片',
    video: '视频',
    document: '文档',
    audio: '音频'
  }
  return labels[type] || '其他'
}

const getFileTypeTagType = (type) => {
  const types = {
    image: 'success',
    video: 'primary',
    document: 'info',
    audio: 'warning'
  }
  return types[type] || 'info'
}

// 格式化视频时长
const formatDuration = (seconds) => {
  if (!seconds || seconds <= 0) return '00:00'
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = Math.floor(seconds % 60)
  
  if (hours > 0) {
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  } else {
    return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
}

// 监听筛选条件变化
watch([fileTypeFilter, sortBy, sortDir], () => {
  pagination.page = 1
  loadFiles()
})

// 初始化
onMounted(() => {
  loadFiles()
  loadFileStats()
})
</script>

<style lang="scss" scoped>
.file-manager {
  min-height: calc(100vh - 140px);
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 0;
  margin: -24px -24px 24px -24px;
  border-radius: 0 0 16px 16px;

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title-section {
      h1 {
        margin: 0 0 8px 0;
        font-size: 2rem;
        font-weight: 700;
        display: flex;
        align-items: center;
        gap: 12px;

        .title-icon {
          font-size: 2.2rem;
        }
      }

      p {
        margin: 0;
        font-size: 1.1rem;
        opacity: 0.9;
      }
    }
  }
}

.stats-section {
  margin-bottom: 24px;

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;

    .stat-card {
      background: white;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 24px;
      }

      .stat-content {
        .stat-number {
          font-size: 1.8rem;
          font-weight: 700;
          color: var(--el-text-color-primary);
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 0.875rem;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }
}

.filter-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .filter-content {
    display: flex;
    gap: 16px;
    align-items: center;

    .search-box {
      flex: 1;
      max-width: 300px;
    }

    .filter-options {
      display: flex;
      gap: 12px;
    }
  }
}

.files-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .view-toggle {
    margin-bottom: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .files-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
    margin-bottom: 24px;

    .file-card {
      border: 1px solid var(--el-border-color-light);
      border-radius: 8px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s ease;
      position: relative;

      &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        transform: translateY(-2px);
      }

      .file-preview {
        height: 150px;
        background: var(--el-bg-color-page);
        display: flex;
        align-items: center;
        justify-content: center;
        position: relative;

        .preview-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .video-preview {
          width: 100%;
          height: 100%;
          position: relative;

          .preview-video {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .video-overlay {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(0, 0, 0, 0.6);
            border-radius: 50%;
            width: 48px;
            height: 48px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 20px;
          }
        }

        .file-icon {
          font-size: 48px;
          color: var(--el-text-color-secondary);
        }
      }

      .file-info {
        padding: 12px;

        .file-name {
          font-weight: 500;
          margin-bottom: 8px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .file-meta {
          display: flex;
          justify-content: space-between;
          font-size: 0.75rem;
          color: var(--el-text-color-secondary);
        }
      }

      .file-actions {
        position: absolute;
        top: 8px;
        right: 8px;
        display: flex;
        gap: 4px;
        opacity: 0;
        transition: opacity 0.3s ease;
      }

      &:hover .file-actions {
        opacity: 1;
      }
    }
  }

  .files-table {
    margin-bottom: 24px;

    .table-preview {
      display: flex;
      align-items: center;
      justify-content: center;

      .table-preview-image {
        width: 40px;
        height: 40px;
        object-fit: cover;
        border-radius: 4px;
      }

      .table-preview-icon {
        font-size: 24px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .video-metadata {
    display: flex;
    flex-direction: column;
    gap: 4px;
    font-size: 0.75rem;
    
    .metadata-item {
      display: flex;
      align-items: center;
      gap: 4px;
      color: var(--el-text-color-regular);
      
      .el-icon {
        font-size: 12px;
        color: var(--el-color-primary);
      }
    }
  }

  .pagination-section {
    display: flex;
    justify-content: center;
  }
}

.preview-content {
  text-align: center;

  .preview-full-image {
    max-width: 100%;
    max-height: 60vh;
    object-fit: contain;
  }

  .preview-full-video {
    width: 100%;
    max-height: 60vh;
  }

  .preview-other {
    padding: 40px;

    .large-icon {
      font-size: 64px;
      color: var(--el-text-color-secondary);
      margin-bottom: 16px;
    }

    p {
      margin: 8px 0;
      color: var(--el-text-color-regular);
    }
  }
}

.preview-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

@media (max-width: 768px) {
  .file-manager {
    .page-header .header-content {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
    }

    .filter-section .filter-content {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;

      .search-box {
        max-width: none;
      }

      .filter-options {
        flex-wrap: wrap;
      }
    }

    .files-section {
      .files-grid {
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      }

      .view-toggle {
        justify-content: center;
      }
    }
  }
}
</style>