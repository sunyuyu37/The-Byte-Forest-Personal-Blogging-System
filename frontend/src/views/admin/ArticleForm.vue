<template>
  <div class="article-form">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑文章' : '新建文章' }}</h1>
      <div class="header-actions">
        <el-button @click="handleBack">返回</el-button>
        <el-button type="info" @click="handleSaveDraft" :loading="saving">
          保存草稿
        </el-button>
        <el-button type="primary" @click="handlePublish" :loading="publishing">
          {{ (form.status && form.status.toLowerCase() === 'published') ? '更新发布' : '发布文章' }}
        </el-button>
      </div>
    </div>
    
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="article-form-content"
      >
        <el-row :gutter="20">
          <!-- 左侧主要内容 -->
          <el-col :span="16">
            <el-card class="content-card">
              <el-form-item label="文章标题" prop="title">
                <el-input
                  v-model="form.title"
                  placeholder="请输入文章标题"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
              
              <el-form-item label="文章摘要" prop="summary">
                <el-input
                  v-model="form.summary"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入文章摘要"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
              
              <el-form-item label="内容格式">
                <el-radio-group v-model="form.contentType" @change="(value, event) => handleContentTypeChange(value, event)">
                  <el-radio label="markdown">Markdown</el-radio>
                  <el-radio label="html">富文本(HTML)</el-radio>
                  <el-radio label="text">纯文本</el-radio>
                </el-radio-group>
              </el-form-item>
              
              <el-form-item label="文章内容" prop="content">
                <div class="editor-container">
                  <!-- Markdown 编辑器 -->
                  <div v-if="form.contentType === 'markdown'" class="markdown-container">
                    <el-tabs v-model="editorMode" class="editor-tabs">
                      <el-tab-pane label="编辑" name="markdown">
                        <div class="editor-toolbar">
                          <el-button-group size="small">
                            <el-button @click="insertMarkdown('**', '**')" title="加粗">
                              <el-icon><Bold /></el-icon>
                            </el-button>
                            <el-button @click="insertMarkdown('*', '*')" title="斜体">
                              <el-icon><Italic /></el-icon>
                            </el-button>
                            <el-button @click="insertMarkdown('`', '`')" title="代码">
                              <el-icon><Code /></el-icon>
                            </el-button>
                            <el-button @click="insertMarkdown('![图片描述](', ')')" title="插入图片">
                              <el-icon><Picture /></el-icon>
                            </el-button>
                            <el-button @click="insertMarkdown('[链接文字](', ')')" title="插入链接">
                              <el-icon><Link /></el-icon>
                            </el-button>
                            <el-button @click="insertMarkdown('\n### ', '')" title="标题">
                              <el-icon><Heading /></el-icon>
                            </el-button>
                          </el-button-group>
                          <el-button size="small" @click="showImageUploadDialog = true" type="primary">
                            <el-icon><Upload /></el-icon>
                            上传图片
                          </el-button>
                          <el-button size="small" @click="showVideoUploadDialog = true" type="success">
                            <el-icon><VideoCamera /></el-icon>
                            上传视频
                          </el-button>
                        </div>
                        <el-input
                          ref="markdownTextarea"
                          v-model="form.content"
                          type="textarea"
                          :rows="20"
                          placeholder="请输入文章内容（支持Markdown语法）"
                          class="markdown-editor"
                        />
                      </el-tab-pane>
                      <el-tab-pane label="预览" name="preview">
                        <div class="markdown-preview" v-html="previewContent"></div>
                      </el-tab-pane>
                    </el-tabs>
                  </div>
                  
                  <!-- 富文本编辑器 -->
                  <div v-else-if="form.contentType === 'html'" class="rich-editor-container">
                    <RichTextEditor 
                      v-model="form.content" 
                      :placeholder="'请输入文章内容（支持图片、视频、链接等富媒体内容）'"
                      :height="'500px'"
                    />
                  </div>
                  
                  <!-- 纯文本编辑器 -->
                  <div v-else class="text-editor-container">
                    <el-input
                      v-model="form.content"
                      type="textarea"
                      :rows="20"
                      placeholder="请输入文章内容（纯文本）"
                      class="text-editor"
                    />
                  </div>
                </div>
              </el-form-item>
            </el-card>
          </el-col>
          
          <!-- 右侧设置 -->
          <el-col :span="8">
            <el-card class="settings-card">
              <template #header>
                <span>文章设置</span>
              </template>
              
              <el-form-item label="文章分类" prop="categoryId">
                <el-select
                  v-model="form.categoryId"
                  placeholder="请选择分类"
                  style="width: 100%"
                  @change="handleCategoryChange"
                >
                  <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  />
                </el-select>
              </el-form-item>
              
              <!-- 分类特定字段 -->
              <div v-if="selectedCategorySlug" class="category-specific-fields">
                <!-- 技术分享特定字段 -->
                <template v-if="selectedCategorySlug === 'tech'">
                  <el-form-item label="难度等级">
                    <el-rate
                      v-model="form.difficulty"
                      :max="5"
                      show-score
                      score-template="{value} 星"
                    />
                  </el-form-item>
                  <el-form-item label="技术栈">
                    <el-select
                      v-model="form.techStack"
                      multiple
                      filterable
                      allow-create
                      placeholder="请选择或输入技术栈"
                      style="width: 100%"
                    >
                      <el-option label="Java" value="Java" />
                      <el-option label="Spring Boot" value="Spring Boot" />
                      <el-option label="Vue.js" value="Vue.js" />
                      <el-option label="React" value="React" />
                      <el-option label="Node.js" value="Node.js" />
                      <el-option label="MySQL" value="MySQL" />
                      <el-option label="Redis" value="Redis" />
                      <el-option label="Docker" value="Docker" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="代码仓库">
                    <el-input
                      v-model="form.codeRepository"
                      placeholder="请输入代码仓库地址（可选）"
                    />
                  </el-form-item>
                </template>
                
                <!-- 新闻资讯特定字段 -->
                <template v-else-if="selectedCategorySlug === 'news'">
                  <el-form-item label="新闻来源">
                    <el-input
                      v-model="form.newsSource"
                      placeholder="请输入新闻来源"
                    />
                  </el-form-item>
                  <el-form-item label="原文链接">
                    <el-input
                      v-model="form.originalUrl"
                      placeholder="请输入原文链接（可选）"
                    />
                  </el-form-item>
                  <el-form-item label="紧急程度">
                    <el-radio-group v-model="form.urgency">
                      <el-radio label="normal">普通</el-radio>
                      <el-radio label="important">重要</el-radio>
                      <el-radio label="urgent">紧急</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </template>
                
                <!-- 生活随笔特定字段 -->
                <template v-else-if="selectedCategorySlug === 'life'">
                  <el-form-item label="心情">
                    <el-select v-model="form.mood" placeholder="选择当前心情">
                      <el-option label="😊 开心" value="happy" />
                      <el-option label="😌 平静" value="calm" />
                      <el-option label="🤔 思考" value="thoughtful" />
                      <el-option label="😔 忧郁" value="melancholy" />
                      <el-option label="😴 疲惫" value="tired" />
                      <el-option label="🎉 兴奋" value="excited" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="地点">
                    <el-input
                      v-model="form.location"
                      placeholder="记录当时的地点（可选）"
                    />
                  </el-form-item>
                  <el-form-item label="天气">
                    <el-select v-model="form.weather" placeholder="选择天气">
                      <el-option label="☀️ 晴天" value="sunny" />
                      <el-option label="⛅ 多云" value="cloudy" />
                      <el-option label="🌧️ 雨天" value="rainy" />
                      <el-option label="❄️ 雪天" value="snowy" />
                      <el-option label="🌫️ 雾天" value="foggy" />
                    </el-select>
                  </el-form-item>
                </template>
                
                <!-- 学习笔记特定字段 -->
                <template v-else-if="selectedCategorySlug === 'study'">
                  <el-form-item label="学习阶段">
                    <el-select v-model="form.studyPhase" placeholder="选择学习阶段">
                      <el-option label="入门" value="beginner" />
                      <el-option label="进阶" value="intermediate" />
                      <el-option label="高级" value="advanced" />
                      <el-option label="专家" value="expert" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="学习时长">
                    <el-input
                      v-model="form.studyDuration"
                      placeholder="例如：2小时、1周等"
                    />
                  </el-form-item>
                  <el-form-item label="参考资料">
                    <el-input
                      v-model="form.references"
                      type="textarea"
                      :rows="3"
                      placeholder="记录参考的书籍、网站、视频等"
                    />
                  </el-form-item>
                </template>
                
                <!-- 项目实战特定字段 -->
                <template v-else-if="selectedCategorySlug === 'project'">
                  <el-form-item label="项目类型">
                    <el-select v-model="form.projectType" placeholder="选择项目类型">
                      <el-option label="Web应用" value="web" />
                      <el-option label="移动应用" value="mobile" />
                      <el-option label="桌面应用" value="desktop" />
                      <el-option label="API服务" value="api" />
                      <el-option label="工具/脚本" value="tool" />
                      <el-option label="其他" value="other" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="项目规模">
                    <el-radio-group v-model="form.projectScale">
                      <el-radio label="small">小型</el-radio>
                      <el-radio label="medium">中型</el-radio>
                      <el-radio label="large">大型</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="项目状态">
                    <el-select v-model="form.projectStatus" placeholder="选择项目状态">
                      <el-option label="规划中" value="planning" />
                      <el-option label="开发中" value="developing" />
                      <el-option label="已完成" value="completed" />
                      <el-option label="已上线" value="deployed" />
                      <el-option label="已暂停" value="paused" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="演示地址">
                    <el-input
                      v-model="form.demoUrl"
                      placeholder="项目演示地址（可选）"
                    />
                  </el-form-item>
                </template>
                
                <!-- 竞赛活动特定字段 -->
                <template v-else-if="selectedCategorySlug === 'contest'">
                  <el-form-item label="竞赛名称">
                    <el-input
                      v-model="form.competitionName"
                      placeholder="请输入竞赛名称"
                    />
                  </el-form-item>
                  <el-form-item label="竞赛时间">
                    <el-date-picker
                      v-model="form.competitionDate"
                      type="daterange"
                      range-separator="至"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      style="width: 100%"
                    />
                  </el-form-item>
                  <el-form-item label="参赛结果">
                    <el-select v-model="form.competitionResult" placeholder="选择参赛结果">
                      <el-option label="一等奖" value="first" />
                      <el-option label="二等奖" value="second" />
                      <el-option label="三等奖" value="third" />
                      <el-option label="优秀奖" value="excellent" />
                      <el-option label="参与奖" value="participation" />
                      <el-option label="未获奖" value="none" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="团队成员">
                    <el-input
                      v-model="form.teamMembers"
                      placeholder="团队成员姓名，用逗号分隔"
                    />
                  </el-form-item>
                </template>
              </div>
              
              <el-form-item label="文章标签" prop="tags">
                <el-select
                  v-model="form.tags"
                  multiple
                  filterable
                  allow-create
                  placeholder="请选择或创建标签"
                  style="width: 100%"
                >
                  <el-option
                    v-for="tag in tags"
                    :key="tag.id"
                    :label="tag.name"
                    :value="tag.name"
                  />
                </el-select>
              </el-form-item>
              
              <el-form-item label="封面图片">
                <div class="cover-upload">
                  <el-input
                    v-model="form.coverImage"
                    placeholder="请输入图片URL或上传图片"
                  />
                  <el-upload
                    ref="uploadRef"
                    :show-file-list="false"
                    :before-upload="beforeUpload"
                    :http-request="handleUploadCover"
                    accept="image/*"
                    class="upload-demo"
                  >
                    <el-button type="primary" class="upload-btn" :loading="uploading">
                      {{ uploading ? '上传中...' : '上传图片' }}
                    </el-button>
                  </el-upload>
                  <div v-if="form.coverImage" class="cover-preview">
                    <img :src="form.coverImage" alt="封面预览" />
                    <div class="preview-actions">
                      <el-button size="small" type="danger" @click="removeCoverImage">
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-form-item>
              
              <el-form-item label="文章状态">
                <el-radio-group v-model="form.status">
                  <el-radio label="draft">草稿</el-radio>
                  <el-radio label="published">已发布</el-radio>
                </el-radio-group>
              </el-form-item>
              
              <el-form-item label="是否置顶">
                <el-switch v-model="form.isTop" />
              </el-form-item>
              
              <el-form-item label="是否推荐">
                <el-switch v-model="form.isRecommend" />
              </el-form-item>
              
              <el-form-item label="允许评论">
                <el-switch v-model="form.allowComment" />
              </el-form-item>
              
              <el-form-item label="发布时间">
                <el-date-picker
                  v-model="form.publishTime"
                  type="datetime"
                  placeholder="选择发布时间"
                  style="width: 100%"
                />
              </el-form-item>
              
              <el-form-item label="SEO关键词">
                <el-input
                  v-model="form.keywords"
                  placeholder="请输入SEO关键词，用逗号分隔"
                />
              </el-form-item>
              
              <el-form-item label="SEO描述">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入SEO描述"
                />
              </el-form-item>
            </el-card>
          </el-col>
        </el-row>
      </el-form>
    </div>
    

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { VideoCamera } from '@element-plus/icons-vue'
import { showWarningConfirm } from '@/utils/positionedConfirm'
import { generateArticleCoverPlaceholder } from '@/utils/placeholder'
import * as articleApi from '@/api/article'
import * as tagApi from '@/api/tag'
import { categoryApi } from '@/api/category'
import { fileApi } from '@/api/file'
import RichTextEditor from '@/components/RichTextEditor.vue'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const uploadRef = ref()
const imageUploadRef = ref()
const videoUploadRef = ref()
const markdownTextarea = ref()

const saving = ref(false)
const publishing = ref(false)
const uploading = ref(false)
const editorMode = ref('markdown')

const categories = ref([])
const tags = ref([])

const form = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  contentType: 'markdown',
  categoryId: null,
  tags: [],
  coverImage: '',
  status: 'draft',
  isTop: false,
  isRecommend: false,
  allowComment: true,
  publishTime: null,
  keywords: '',
  description: '',
  // 技术分享特定字段
  difficulty: 0,
  techStack: [],
  codeRepository: '',
  // 新闻资讯特定字段
  newsSource: '',
  originalUrl: '',
  urgency: 'normal',
  // 生活随笔特定字段
  mood: '',
  location: '',
  weather: '',
  // 学习笔记特定字段
  studyPhase: '',
  studyDuration: '',
  references: '',
  // 项目实战特定字段
  projectType: '',
  projectScale: '',
  projectStatus: '',
  demoUrl: '',
  // 竞赛活动特定字段
  competitionName: '',
  competitionDate: null,
  competitionResult: '',
  teamMembers: ''
})

const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  summary: [
    { required: true, message: '请输入文章摘要', trigger: 'blur' },
    { min: 1, max: 200, message: '摘要长度在 1 到 200 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' },
    { min: 10, message: '内容至少需要 10 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择文章分类', trigger: 'change' }
  ]
}

// 是否为编辑模式
const isEdit = computed(() => {
  return !!route.params.id && route.params.id !== 'new'
})

// 当前选择的分类slug
const selectedCategorySlug = computed(() => {
  if (!form.categoryId || !categories.value.length) return ''
  const category = categories.value.find(cat => cat.id === form.categoryId)
  return category ? category.slug : ''
})

// Markdown预览内容
const previewContent = computed(() => {
  // 这里应该使用实际的Markdown解析器，比如marked.js
  // 现在只是简单的换行处理
  return form.content.replace(/\n/g, '<br>')
})

// 返回列表
const handleBack = () => {
  router.push('/admin/articles')
}

// 保存草稿
const handleSaveDraft = async () => {
  try {
    console.log('开始保存草稿')
    await formRef.value.validate()
    saving.value = true
    
    form.status = 'draft'
    const result = await saveArticle()
    
    if (result) {
      console.log('草稿保存成功')
      ElMessage.success('草稿保存成功！')
    }
  } catch (error) {
    console.error('保存草稿失败:', error)
    console.error('错误类型:', typeof error)
    console.error('错误对象:', error)
    const errorMessage = error.message || '保存失败，请检查网络连接或稍后重试'
    console.error('显示错误消息:', errorMessage)
    ElMessage.error(errorMessage)
  } finally {
    saving.value = false
  }
}

// 发布文章
const handlePublish = async () => {
  try {
    console.log('开始发布文章')
    await formRef.value.validate()
    publishing.value = true
    
    // 设置为发布状态
    const originalStatus = form.status
    form.status = 'published'
    form.publishTime = form.publishTime || new Date()
    
    const result = await saveArticle()
    
    if (result) {
      console.log('文章发布成功')
      ElMessage.success(isEdit.value ? '文章更新并发布成功！' : '文章发布成功！')
      // 延迟跳转，让用户看到成功提示
      setTimeout(() => {
        router.push('/admin/articles')
      }, 1500)
    }
  } catch (error) {
    console.error('发布文章失败:', error)
    console.error('错误类型:', typeof error)
    console.error('错误对象:', error)
    if (error !== 'validation failed') {
      const errorMessage = error.message || '发布失败，请检查网络连接或稍后重试'
      console.error('显示错误消息:', errorMessage)
      ElMessage.error(errorMessage)
    }
  } finally {
    publishing.value = false
  }
}

// 保存文章
const saveArticle = async () => {
  try {
    // 将tags字符串数组转换为TagDTO对象数组
    const tagsData = form.tags.map(tagName => {
      // 如果是字符串，转换为TagDTO对象
      if (typeof tagName === 'string') {
        return { name: tagName }
      }
      // 如果已经是对象，直接返回
      return tagName
    })
    
    // 收集分类特定数据
    const categorySpecificData = {}
    
    // 根据当前选择的分类收集对应的特定字段
    if (selectedCategorySlug.value === 'tech') {
      categorySpecificData.difficulty = form.difficulty
      categorySpecificData.techStack = form.techStack
      categorySpecificData.codeRepository = form.codeRepository
    } else if (selectedCategorySlug.value === 'news') {
      categorySpecificData.newsSource = form.newsSource
      categorySpecificData.originalUrl = form.originalUrl
      categorySpecificData.urgency = form.urgency
    } else if (selectedCategorySlug.value === 'life') {
      categorySpecificData.mood = form.mood
      categorySpecificData.location = form.location
      categorySpecificData.weather = form.weather
    } else if (selectedCategorySlug.value === 'study') {
      categorySpecificData.studyPhase = form.studyPhase
      categorySpecificData.studyDuration = form.studyDuration
      categorySpecificData.references = form.references
    } else if (selectedCategorySlug.value === 'project') {
      categorySpecificData.projectType = form.projectType
      categorySpecificData.projectScale = form.projectScale
      categorySpecificData.projectStatus = form.projectStatus
      categorySpecificData.demoUrl = form.demoUrl
    } else if (selectedCategorySlug.value === 'contest') {
      categorySpecificData.competitionName = form.competitionName
      categorySpecificData.competitionDate = form.competitionDate
      categorySpecificData.competitionResult = form.competitionResult
      categorySpecificData.teamMembers = form.teamMembers
    }
    
    const articleData = {
      title: form.title,
      summary: form.summary,
      content: form.content,
      categoryId: form.categoryId,
      tags: tagsData,
      coverImage: form.coverImage,
      status: form.status,
      isTop: form.isTop,
      isRecommend: form.isRecommend,
      allowComment: form.allowComment,
      publishTime: form.publishTime,
      keywords: form.keywords,
      description: form.description,
      categorySpecificData: categorySpecificData
    }
    
    console.log('准备保存文章数据:', articleData)
    console.log('是否为编辑模式:', isEdit.value)
    console.log('文章ID:', route.params.id)
    
    let response
    if (isEdit.value) {
      console.log('调用更新文章API')
      response = await articleApi.updateAdminArticle(route.params.id, articleData)
    } else {
      console.log('调用创建文章API')
      response = await articleApi.createAdminArticle(articleData)
    }
    
    console.log('API响应:', response)
    
    if (response.code === 200) {
      // 如果是新建文章，更新路由参数为编辑模式
      if (!isEdit.value && response.data?.id) {
        router.replace(`/admin/articles/${response.data.id}/edit`)
      }
      return true
    } else {
      throw new Error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存文章失败:', error)
    console.error('错误详情:', {
      message: error.message,
      response: error.response,
      stack: error.stack
    })
    throw error
  }
}

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('上传图片大小不能超过 10MB!')
    return false
  }
  return true
}

// 上传封面图片
const handleUploadCover = async (options) => {
  const { file } = options
  
  try {
    uploading.value = true
    const response = await fileApi.uploadFile(file)
    
    if (response.code === 200) {
      form.coverImage = response.data.fileUrl
      ElMessage.success('图片上传成功!')
    } else {
      throw new Error(response.message || '上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error(error.message || '图片上传失败，请重试')
  } finally {
    uploading.value = false
  }
}

// 删除封面图片
const removeCoverImage = () => {
  form.coverImage = ''
  ElMessage.success('封面图片已删除')
}

// 内容格式切换
const handleContentTypeChange = async (newType, event) => {
  if (form.content && newType !== form.contentType) {
    try {
      await showWarningConfirm(
        '切换内容格式将可能导致格式丢失，是否继续？',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        },
        event
      )
      // 用户确认切换
    } catch (error) {
      // 用户取消，恢复原来的格式
      form.contentType = form.contentType
    }
  }
}

// Markdown 工具栏功能
const insertMarkdown = (before, after) => {
  const textarea = markdownTextarea.value?.textarea
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = form.content.substring(start, end)
  const replacement = before + selectedText + after
  
  form.content = form.content.substring(0, start) + replacement + form.content.substring(end)
  
  // 重新设置光标位置
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + before.length, start + before.length + selectedText.length)
  }, 0)
}

// 插入链接到Markdown编辑器
const insertLink = () => {
  const url = prompt('请输入链接地址:')
  if (url && form.contentType === 'markdown') {
    insertMarkdown('[链接文字](', `${url})`)
  }
}





// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await categoryApi.getAllCategories()
    if (response.code === 200) {
      categories.value = response.data || []
    } else {
      console.error('加载分类失败:', response.message)
      // 使用备用数据
      categories.value = [
        { id: 1, name: '技术分享', slug: 'tech-sharing' },
        { id: 2, name: '生活随笔', slug: 'life-essay' },
        { id: 3, name: '学习笔记', slug: 'study-notes' },
        { id: 4, name: '项目实战', slug: 'project-practice' },
        { id: 5, name: '新闻资讯', slug: 'news-info' },
        { id: 6, name: '竞赛活动', slug: 'competition-events' }
      ]
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    // 使用备用数据
    categories.value = [
      { id: 1, name: '技术分享', slug: 'tech-sharing' },
      { id: 2, name: '生活随笔', slug: 'life-essay' },
      { id: 3, name: '学习笔记', slug: 'study-notes' },
      { id: 4, name: '项目实战', slug: 'project-practice' },
      { id: 5, name: '新闻资讯', slug: 'news-info' },
      { id: 6, name: '竞赛活动', slug: 'competition-events' }
    ]
  }
}

// 加载标签列表
const loadTags = async () => {
  try {
    const response = await tagApi.getAllTags()
    if (response.code === 200) {
      tags.value = response.data || []
    } else {
      console.error('加载标签失败:', response.message)
      // 使用备用数据
      tags.value = [
        { id: 1, name: 'Vue.js' },
        { id: 2, name: 'React' },
        { id: 3, name: 'JavaScript' },
        { id: 4, name: 'TypeScript' },
        { id: 5, name: 'Node.js' },
        { id: 6, name: 'Spring Boot' },
        { id: 7, name: 'MySQL' },
        { id: 8, name: 'Redis' }
      ]
    }
  } catch (error) {
    console.error('加载标签失败:', error)
    // 使用备用数据
    tags.value = [
      { id: 1, name: 'Vue.js' },
      { id: 2, name: 'React' },
      { id: 3, name: 'JavaScript' },
      { id: 4, name: 'TypeScript' },
      { id: 5, name: 'Node.js' },
      { id: 6, name: 'Spring Boot' },
      { id: 7, name: 'MySQL' },
      { id: 8, name: 'Redis' }
    ]
  }
}

// 处理分类变化
const handleCategoryChange = (categoryId) => {
  // 设置分类ID
  form.categoryId = categoryId
  
  // 清空所有分类特定字段
  // 技术分享字段
  form.difficulty = 0
  form.techStack = []
  form.codeRepository = ''
  // 新闻资讯字段
  form.newsSource = ''
  form.originalUrl = ''
  form.urgency = 'normal'
  // 生活随笔字段
  form.mood = ''
  form.location = ''
  form.weather = ''
  // 学习笔记字段
  form.studyPhase = ''
  form.studyDuration = ''
  form.references = ''
  // 项目实战字段
  form.projectType = ''
  form.projectScale = ''
  form.projectStatus = ''
  form.demoUrl = ''
  // 竞赛活动字段
  form.competitionName = ''
  form.competitionDate = null
  form.competitionResult = ''
  form.teamMembers = ''
}

// 加载文章详情
const loadArticle = async (id) => {
  try {
    const response = await articleApi.getAdminArticle(id)
    if (response.code === 200) {
      const article = response.data
      Object.assign(form, {
        title: article.title || '',
        summary: article.summary || '',
        content: article.content || '',
        categoryId: article.category?.id || null,
        tags: article.tags?.map(tag => tag.name) || [],
        coverImage: article.coverImage || '',
        status: article.status ? article.status.toLowerCase() : 'draft',
        isTop: article.isTop || false,
        isRecommend: article.isRecommend || false,
        allowComment: article.allowComment !== false,
        publishTime: article.publishedAt ? new Date(article.publishedAt) : null,
        keywords: article.keywords || '',
        description: article.description || ''
      })
      
      // 加载分类特定数据
      if (article.categorySpecificData) {
        const categoryData = article.categorySpecificData
        
        // 技术分享字段
        if (categoryData.difficulty !== undefined) form.difficulty = categoryData.difficulty
        if (categoryData.techStack !== undefined) form.techStack = categoryData.techStack || []
        if (categoryData.codeRepository !== undefined) form.codeRepository = categoryData.codeRepository || ''
        
        // 新闻资讯字段
        if (categoryData.newsSource !== undefined) form.newsSource = categoryData.newsSource || ''
        if (categoryData.originalUrl !== undefined) form.originalUrl = categoryData.originalUrl || ''
        if (categoryData.urgency !== undefined) form.urgency = categoryData.urgency || 'normal'
        
        // 生活随笔字段
        if (categoryData.mood !== undefined) form.mood = categoryData.mood || ''
        if (categoryData.location !== undefined) form.location = categoryData.location || ''
        if (categoryData.weather !== undefined) form.weather = categoryData.weather || ''
        
        // 学习笔记字段
        if (categoryData.studyPhase !== undefined) form.studyPhase = categoryData.studyPhase || ''
        if (categoryData.studyDuration !== undefined) form.studyDuration = categoryData.studyDuration || ''
        if (categoryData.references !== undefined) form.references = categoryData.references || ''
        
        // 项目实战字段
        if (categoryData.projectType !== undefined) form.projectType = categoryData.projectType || ''
        if (categoryData.projectScale !== undefined) form.projectScale = categoryData.projectScale || ''
        if (categoryData.projectStatus !== undefined) form.projectStatus = categoryData.projectStatus || ''
        if (categoryData.demoUrl !== undefined) form.demoUrl = categoryData.demoUrl || ''
        
        // 竞赛活动字段
        if (categoryData.competitionName !== undefined) form.competitionName = categoryData.competitionName || ''
        if (categoryData.competitionDate !== undefined) form.competitionDate = categoryData.competitionDate ? new Date(categoryData.competitionDate) : null
        if (categoryData.competitionResult !== undefined) form.competitionResult = categoryData.competitionResult || ''
        if (categoryData.teamMembers !== undefined) form.teamMembers = categoryData.teamMembers || ''
      }
    } else {
      ElMessage.error(response.message || '加载文章失败')
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
    
    // 如果API调用失败，使用模拟数据作为后备
    const article = {
      title: '示例文章标题',
      summary: '这是一篇示例文章的摘要',
      content: '# 示例文章\n\n这是文章的内容...',
      categoryId: 1,
      tags: ['Vue.js', 'JavaScript'],
      coverImage: generateArticleCoverPlaceholder(400, 200),
      status: 'published',
      isTop: false,
      isRecommend: true,
      allowComment: true,
      publishTime: new Date(),
      keywords: 'Vue.js,前端开发',
      description: '这是一篇关于Vue.js的文章'
    }
    
    Object.assign(form, article)
  }
}

onMounted(async () => {
  await Promise.all([
    loadCategories(),
    loadTags()
  ])
  
  if (isEdit.value) {
    await loadArticle(route.params.id)
  }
})
</script>

<style lang="scss" scoped>
.article-form {
  min-height: calc(100vh - 140px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h1 {
    font-size: 1.5rem;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin: 0;
  }
  
  .header-actions {
    display: flex;
    gap: 10px;
  }
}

.form-container {
  .content-card {
    margin-bottom: 20px;
  }
  
  .settings-card {
    position: sticky;
    top: 20px;
  }
}

.editor-container {
  .editor-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 15px;
    }
  }
  
  .editor-toolbar, .rich-editor-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    background: var(--el-fill-color-lighter);
    border: 1px solid var(--el-border-color);
    border-bottom: none;
    border-radius: 4px 4px 0 0;
    gap: 10px;
    flex-wrap: wrap;
  }
  
  .markdown-editor {
    font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
    
    :deep(textarea) {
      font-family: inherit;
      line-height: 1.6;
      border-radius: 0 0 4px 4px;
    }
  }
  
  .rich-editor {
    min-height: 400px;
    padding: 15px;
    border: 1px solid var(--el-border-color);
    border-radius: 0 0 4px 4px;
    background: var(--el-bg-color);
    line-height: 1.6;
    outline: none;
    
    &:focus {
      border-color: var(--el-color-primary);
    }
    
    :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
      margin-top: 1.5em;
      margin-bottom: 0.5em;
      font-weight: 600;
    }
    
    :deep(p) {
      margin-bottom: 1em;
    }
    
    :deep(img) {
      max-width: 100%;
      height: auto;
      border-radius: 4px;
    }
    
    :deep(ul), :deep(ol) {
      padding-left: 20px;
      margin-bottom: 1em;
    }
    
    :deep(blockquote) {
      border-left: 4px solid var(--el-color-primary);
      padding-left: 15px;
      margin: 1em 0;
      color: var(--el-text-color-regular);
    }
  }
  
  .text-editor {
    :deep(textarea) {
      line-height: 1.6;
    }
  }
  
  .markdown-preview {
    min-height: 400px;
    padding: 15px;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    background: var(--el-bg-color);
    line-height: 1.6;
    
    :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
      margin-top: 1.5em;
      margin-bottom: 0.5em;
      font-weight: 600;
    }
    
    :deep(p) {
      margin-bottom: 1em;
    }
    
    :deep(code) {
      background: var(--el-fill-color-light);
      padding: 2px 4px;
      border-radius: 3px;
      font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
    }
    
    :deep(pre) {
      background: var(--el-fill-color-light);
      padding: 15px;
      border-radius: 6px;
      overflow-x: auto;
      
      code {
        background: none;
        padding: 0;
      }
    }
    
    :deep(img) {
      max-width: 100%;
      height: auto;
      border-radius: 4px;
    }
  }
}

// 图片上传对话框样式
.image-upload-area {
  :deep(.el-upload) {
    width: 100%;
  }
  
  :deep(.el-upload-dragger) {
    width: 100%;
    height: 180px;
  }
}

.uploaded-image-preview {
  margin-top: 20px;
  text-align: center;
  
  h4 {
    color: var(--el-color-success);
    margin-bottom: 15px;
  }
  
  img {
    max-width: 100%;
    max-height: 200px;
    border-radius: 6px;
    border: 1px solid var(--el-border-color);
    margin-bottom: 15px;
  }
  
  .image-url-copy {
    margin-bottom: 15px;
  }
  
  .insert-actions {
    display: flex;
    justify-content: center;
    gap: 10px;
  }
}

.cover-upload {
  .upload-demo {
    margin-top: 10px;
    width: 100%;
    
    .upload-btn {
      width: 100%;
    }
  }
  
  .cover-preview {
    margin-top: 10px;
    position: relative;
    
    img {
      width: 100%;
      max-height: 200px;
      object-fit: cover;
      border-radius: 6px;
      border: 1px solid var(--el-border-color);
    }
    
    .preview-actions {
      position: absolute;
      top: 10px;
      right: 10px;
      background: rgba(0, 0, 0, 0.6);
      border-radius: 4px;
      padding: 5px;
    }
  }
}

// 分类特定字段样式
.category-specific-fields {
  margin-top: 20px;
  padding: 20px;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  border-left: 4px solid var(--el-color-primary);
  
  :deep(.el-form-item) {
    margin-bottom: 18px;
  }
  
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--el-text-color-primary);
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .form-container {
    :deep(.el-col) {
      width: 100% !important;
    }
    
    .settings-card {
      position: static;
      margin-top: 20px;
    }
  }
}

@media (max-width: 768px) {
  .article-form {
    min-height: calc(100vh - 120px);
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    
    .header-actions {
      width: 100%;
      justify-content: flex-end;
    }
  }
  
  .editor-container {
    .markdown-editor {
      :deep(textarea) {
        font-size: 14px;
      }
    }
  }
}
</style>