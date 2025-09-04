<template>
  <div class="admin-messages">
    <div class="page-header">
      <h1>留言管理</h1>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon total">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ messageStats.totalMessages || 0 }}</div>
                <div class="stats-label">留言总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon pending">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ messageStats.pendingMessages || 0 }}</div>
                <div class="stats-label">待审核</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon approved">
                <el-icon><Select /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ messageStats.approvedMessages || 0 }}</div>
                <div class="stats-label">已通过</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon rejected">
                <el-icon><Close /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ messageStats.rejectedMessages || 0 }}</div>
                <div class="stats-label">已拒绝</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 统计图表 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>留言趋势</span>
                <el-select v-model="trendDays" @change="handleTrendDaysChange" size="small" style="width: 100px">
                  <el-option label="7天" :value="7" />
                  <el-option label="15天" :value="15" />
                  <el-option label="30天" :value="30" />
                </el-select>
              </div>
            </template>
            <div v-loading="chartLoading" class="chart-container">
              <VChart :option="trendChartOption" style="height: 300px;" />
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>状态分布</span>
            </template>
            <div v-loading="chartLoading" class="chart-container">
              <VChart :option="statusChartOption" style="height: 300px;" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-form :model="searchForm" inline>
        <el-form-item label="留言内容">
          <el-input
            v-model="searchForm.content"
            placeholder="请输入留言内容"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input
            v-model="searchForm.userNickname"
            placeholder="请输入用户昵称"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="info" @click="showAdvancedSearch = !showAdvancedSearch">
            {{ showAdvancedSearch ? '收起高级搜索' : '展开高级搜索' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 高级搜索 -->
      <el-collapse-transition>
        <div v-show="showAdvancedSearch" class="advanced-search">
          <el-form :model="advancedSearchForm" inline>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="advancedSearchForm.dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 350px"
              />
            </el-form-item>
            <el-form-item label="IP地址">
              <el-input
                v-model="advancedSearchForm.ip"
                placeholder="请输入IP地址"
                clearable
                style="width: 150px"
              />
            </el-form-item>
            <el-form-item label="用户邮箱">
              <el-input
                v-model="advancedSearchForm.userEmail"
                placeholder="请输入用户邮箱"
                clearable
                style="width: 180px"
              />
            </el-form-item>
            <el-form-item label="内容长度">
              <el-select
                v-model="advancedSearchForm.contentLength"
                placeholder="请选择内容长度"
                clearable
                style="width: 140px"
              >
                <el-option label="短消息(<50字)" value="short" />
                <el-option label="中等(50-200字)" value="medium" />
                <el-option label="长消息(>200字)" value="long" />
              </el-select>
            </el-form-item>
            <el-form-item label="排序方式">
              <el-select
                v-model="advancedSearchForm.sortBy"
                placeholder="请选择排序方式"
                style="width: 140px"
              >
                <el-option label="最新优先" value="createdAt_desc" />
                <el-option label="最早优先" value="createdAt_asc" />
                <el-option label="内容长度" value="content_length" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-transition>
    </div>
    
    <!-- 留言列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="messages"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="user" label="用户" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="30" :src="row.user?.avatar" />
              <span>{{ row.user?.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="留言内容" min-width="400">
          <template #default="{ row }">
            <div class="message-content">
              <div class="content-wrapper">
                <p 
                  :class="{ 'content-collapsed': !row.expanded && row.content.length > 100 }"
                  class="content-text"
                >
                  {{ row.expanded || row.content.length <= 100 ? row.content : row.content.substring(0, 100) + '...' }}
                </p>
                <el-button 
                  v-if="row.content.length > 100"
                  type="text" 
                  size="small" 
                  class="expand-btn"
                  @click="toggleExpand(row)"
                >
                  {{ row.expanded ? '收起' : '展开' }}
                </el-button>
              </div>
              <div v-if="row.parentComment" class="reply-to">
                <el-icon><ChatLineRound /></el-icon>
                <span>回复：{{ row.parentComment.content.length > 50 ? row.parentComment.content.substring(0, 50) + '...' : row.parentComment.content }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="createdAt" label="留言时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === 'pending'"
                type="success"
                size="small"
                @click="handleApprove(row)"
              >
                <el-icon><Select /></el-icon>
                通过
              </el-button>
              <el-button
                v-if="row.status === 'pending'"
                type="warning"
                size="small"
                @click="(event) => handleReject(row, event)"
              >
                <el-icon><Close /></el-icon>
                拒绝
              </el-button>
              <el-button
                type="info"
                size="small"
                @click="handleViewDetail(row)"
              >
                <el-icon><ChatDotRound /></el-icon>
                详情
              </el-button>
              <el-dropdown trigger="click" @command="(command) => handleMoreAction(command, row)">
                <el-button type="primary" size="small">
                  更多操作
                  <el-icon class="el-icon--right"><More /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="reply">
                      <el-icon><ChatLineRound /></el-icon>
                      回复留言
                    </el-dropdown-item>
                    <el-dropdown-item command="copy">
                      <el-icon><Download /></el-icon>
                      复制内容
                    </el-dropdown-item>
                    <el-dropdown-item v-if="row.status !== 'pending'" command="reset" divided>
                      <el-icon><Clock /></el-icon>
                      重置为待审核
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
                      删除留言
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 批量操作 -->
      <div v-if="selectedMessages.length > 0" class="batch-actions">
        <span>已选择 {{ selectedMessages.length }} 项</span>
        <el-button type="success" size="small" @click="handleBatchApprove">
          批量通过
        </el-button>
        <el-button type="warning" size="small" @click="(event) => handleBatchReject(event)">
          批量拒绝
        </el-button>
        <el-button type="danger" size="small" @click="(event) => handleBatchDelete(event)">
          批量删除
        </el-button>
        <el-button type="primary" size="small" @click="handleExportMessages">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
    
    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="回复留言"
      width="600px"
      @close="handleReplyDialogClose"
    >
      <div v-if="currentMessage" class="original-message">
        <h4>原留言：</h4>
        <div class="message-info">
          <div class="user-info">
            <el-avatar :size="30" :src="currentMessage.user?.avatar" />
            <span>{{ currentMessage.user?.nickname }}</span>
            <span class="time">{{ formatTime(currentMessage.createdAt) }}</span>
          </div>
          <p class="content">{{ currentMessage.content }}</p>
        </div>
      </div>
      
      <el-form
        ref="replyFormRef"
        :model="replyForm"
        :rules="replyRules"
        label-width="80px"
      >
        <el-form-item label="回复内容" prop="content">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitReply" :loading="replying">
            发送回复
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 留言详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="留言详情"
      width="800px"
      @close="handleDetailDialogClose"
    >
      <div v-if="messageDetail" class="message-detail">
        <!-- 留言基本信息 -->
        <div class="detail-section">
          <h4>留言信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="留言ID">{{ messageDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(messageDetail.status)" size="small">
                {{ getStatusText(messageDetail.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="留言时间">{{ formatTime(messageDetail.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="IP地址">{{ messageDetail.ip || '未记录' }}</el-descriptions-item>
            <el-descriptions-item label="设备信息" :span="2">{{ messageDetail.userAgent || '未记录' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 用户信息 -->
        <div class="detail-section">
          <h4>用户信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户昵称">{{ messageDetail.user?.nickname || '匿名用户' }}</el-descriptions-item>
            <el-descriptions-item label="用户邮箱">{{ messageDetail.user?.email || '未提供' }}</el-descriptions-item>
            <el-descriptions-item label="用户头像" :span="2">
              <el-avatar :size="50" :src="messageDetail.user?.avatar" />
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 留言内容 -->
        <div class="detail-section">
          <h4>留言内容</h4>
          <div class="message-content-detail">
            <p>{{ messageDetail.content }}</p>
          </div>
        </div>

        <!-- 回复列表 -->
        <div v-if="messageDetail.replies && messageDetail.replies.length > 0" class="detail-section">
          <div class="replies-header">
            <h4>回复列表 ({{ messageDetail.replies.length }}条)</h4>
            <div class="replies-actions">
              <el-button type="primary" size="small" @click="handleManageReplies()">
                <el-icon><Setting /></el-icon>
                管理回复
              </el-button>
            </div>
          </div>
          <div class="replies-list">
            <div v-for="reply in messageDetail.replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <div class="reply-user">
                  <el-avatar :size="30" :src="reply.user?.avatar" />
                  <span class="nickname">{{ reply.user?.nickname || '管理员' }}</span>
                  <el-tag v-if="reply.user?.role === 'ADMIN'" type="warning" size="small">管理员</el-tag>
                  <el-tag :type="getStatusType(reply.status)" size="small">{{ getStatusText(reply.status) }}</el-tag>
                </div>
                <div class="reply-actions">
                  <span class="reply-time">{{ formatTime(reply.createdAt) }}</span>
                  <el-dropdown trigger="click" @command="(command) => handleReplyAction(command, reply)">
                    <el-button type="text" size="small">
                      <el-icon><More /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item v-if="reply.status === 'pending'" command="approve">
                          <el-icon><Select /></el-icon>
                          通过
                        </el-dropdown-item>
                        <el-dropdown-item v-if="reply.status === 'pending'" command="reject">
                          <el-icon><Close /></el-icon>
                          拒绝
                        </el-dropdown-item>
                        <el-dropdown-item command="edit">
                          <el-icon><Edit /></el-icon>
                          编辑
                        </el-dropdown-item>
                        <el-dropdown-item command="delete" divided>
                          <el-icon><Delete /></el-icon>
                          删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
              <div class="reply-content">
                <p>{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="detail-section">
          <h4>回复列表</h4>
          <div class="no-replies">
            <el-empty description="暂无回复" :image-size="80" />
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleReplyFromDetail">回复留言</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 回复管理对话框 -->
    <el-dialog
      v-model="replyManageDialogVisible"
      title="回复管理"
      width="900px"
      @close="handleReplyManageDialogClose"
    >
      <div v-if="currentMessage" class="reply-manage-content">
        <div class="original-message-info">
          <h4>原留言：{{ currentMessage.content }}</h4>
        </div>
        
        <el-table :data="currentMessage.replies || []" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="回复用户" width="150">
            <template #default="{ row }">
              <div class="user-info">
                <el-avatar :size="30" :src="row.user?.avatar" />
                <span>{{ row.user?.nickname || '管理员' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="回复内容" min-width="200">
            <template #default="{ row }">
              <div class="reply-content-preview">
                {{ row.content.length > 50 ? row.content.substring(0, 50) + '...' : row.content }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="回复时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 'pending'"
                type="success"
                size="small"
                @click="handleApproveReply(row)"
              >
                通过
              </el-button>
              <el-button
                v-if="row.status === 'pending'"
                type="warning"
                size="small"
                @click="handleRejectReply(row)"
              >
                拒绝
              </el-button>
              <el-button
                type="primary"
                size="small"
                @click="handleEditReply(row)"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="(event) => handleDeleteReply(row, event)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="replyManageDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑回复对话框 -->
    <el-dialog
      v-model="editReplyDialogVisible"
      title="编辑回复"
      width="600px"
      @close="handleEditReplyDialogClose"
    >
      <el-form
        ref="editReplyFormRef"
        :model="editReplyForm"
        :rules="replyRules"
        label-width="80px"
      >
        <el-form-item label="回复内容" prop="content">
          <el-input
            v-model="editReplyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editReplyForm.status" placeholder="请选择状态">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editReplyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitEditReply" :loading="editingReply">
            保存修改
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatDotRound, Clock, Select, Close, Download, Setting, More, Edit, Delete, ChatLineRound } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { generateAvatarPlaceholder } from '@/utils/placeholder'
import { messageApi } from '@/api/message'
import { showDeleteConfirm, showWarningConfirm } from '@/utils/positionedConfirm'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'

// 注册ECharts组件
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 注册VChart组件
const components = {
  VChart
}

const loading = ref(false)
const messages = ref([])
const selectedMessages = ref([])
const replyDialogVisible = ref(false)
const replying = ref(false)
const currentMessage = ref(null)
const replyFormRef = ref()
const messageStats = ref({})
const detailDialogVisible = ref(false)
const messageDetail = ref(null)
const showAdvancedSearch = ref(false)
const replyManageDialogVisible = ref(false)
const editReplyDialogVisible = ref(false)
const editingReply = ref(false)
const currentReply = ref(null)
const editReplyFormRef = ref()

const searchForm = reactive({
  content: '',
  userNickname: '',
  status: ''
})

const advancedSearchForm = reactive({
  dateRange: null,
  ip: '',
  userEmail: '',
  contentLength: '',
  sortBy: 'createdAt_desc'
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const replyForm = reactive({
  content: ''
})

const editReplyForm = reactive({
  id: null,
  content: '',
  status: 'pending'
})

// 图表相关数据
const chartLoading = ref(false)
const trendDays = ref(7)
const trendData = ref({
  dates: [],
  counts: [],
  statusCounts: {
    approved: [],
    pending: [],
    rejected: []
  }
})
const statusDistribution = ref({
  approved: 0,
  pending: 0,
  rejected: 0
})

const replyRules = {
  content: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 1, max: 500, message: '回复内容长度在 1 到 500 个字符', trigger: 'blur' }
  ]
}

// 获取留言列表
const getMessages = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      content: searchForm.content || undefined,
      userNickname: searchForm.userNickname || undefined,
      status: searchForm.status || undefined,
      ip: advancedSearchForm.ip || undefined,
      userEmail: advancedSearchForm.userEmail || undefined,
      contentLength: advancedSearchForm.contentLength || undefined,
      sortBy: advancedSearchForm.sortBy || 'createdAt_desc',
      startTime: advancedSearchForm.dateRange?.[0] || undefined,
      endTime: advancedSearchForm.dateRange?.[1] || undefined,
      messageOnly: true // 只获取留言（article_id为null的评论）
    }
    
    const response = await messageApi.getMessagesForAdmin(params)
    if (response.code === 200) {
      messages.value = response.data.records || []
      pagination.total = response.data.total || 0
      
      // 为没有头像的用户生成占位符头像
      messages.value.forEach(message => {
        if (message.user && !message.user.avatar) {
          message.user.avatar = generateAvatarPlaceholder(message.user.nickname)
        }
      })
    } else {
      ElMessage.error(response.message || '获取留言列表失败')
    }
  } catch (error) {
    console.error('获取留言列表失败:', error)
    ElMessage.error('获取留言列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getMessages()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    content: '',
    userNickname: '',
    status: ''
  })
  Object.assign(advancedSearchForm, {
    dateRange: null,
    ip: '',
    userEmail: '',
    contentLength: '',
    sortBy: 'createdAt_desc'
  })
  pagination.page = 1
  getMessages()
}

// 分页
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  getMessages()
}

const handlePageChange = (page) => {
  pagination.page = page
  getMessages()
}

// 选择
const handleSelectionChange = (selection) => {
  selectedMessages.value = selection
}

// 审核通过
const handleApprove = async (message) => {
  try {
    const response = await messageApi.approveMessage(message.id)
    if (response.code === 200) {
      ElMessage.success('留言审核通过')
      getMessageStats()
      getMessages()
    } else {
      ElMessage.error(response.message || '审核失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

// 拒绝
const handleReject = async (message, event) => {
  const confirmed = await showWarningConfirm(
    '确定要拒绝这条留言吗？',
    '拒绝留言',
    event
  )
  
  if (confirmed) {
    try {
      const response = await messageApi.rejectMessage(message.id)
      if (response.code === 200) {
        ElMessage.success('留言已拒绝')
        getMessageStats()
        getMessages()
      } else {
        ElMessage.error(response.message || '拒绝失败')
      }
    } catch (error) {
      console.error('拒绝失败:', error)
      ElMessage.error('拒绝失败')
    }
  }
}

// 删除
const handleDelete = async (message, event) => {
  const confirmed = await showDeleteConfirm(
    '确定要删除这条留言吗？删除后无法恢复。',
    '删除留言',
    event
  )
  
  if (confirmed) {
    try {
      const response = await messageApi.deleteMessage(message.id)
      if (response.code === 200) {
        ElMessage.success('留言已删除')
        getMessageStats()
        getMessages()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 回复
const handleReply = (message) => {
  currentMessage.value = message
  replyForm.content = ''
  replyDialogVisible.value = true
}

// 提交回复
const handleSubmitReply = async () => {
  if (!replyFormRef.value) return
  
  const valid = await replyFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  replying.value = true
  try {
    const response = await messageApi.replyToMessage({
      parentId: currentMessage.value.id,
      content: replyForm.content
    })
    
    if (response.code === 200) {
      ElMessage.success('回复成功')
      replyDialogVisible.value = false
      getMessageStats()
      getMessages()
    } else {
      ElMessage.error(response.message || '回复失败')
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  } finally {
    replying.value = false
  }
}

// 关闭回复对话框
const handleReplyDialogClose = () => {
  replyFormRef.value?.resetFields()
  replyForm.content = ''
  currentMessage.value = null
}

// 查看留言详情
const handleViewDetail = async (message) => {
  try {
    const response = await messageApi.getMessageDetail(message.id)
    if (response.code === 200) {
      messageDetail.value = response.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取留言详情失败')
    }
  } catch (error) {
    console.error('获取留言详情失败:', error)
    ElMessage.error('获取留言详情失败')
  }
}

// 关闭详情对话框
const handleDetailDialogClose = () => {
  messageDetail.value = null
}

// 从详情页面回复留言
const handleReplyFromDetail = () => {
  if (messageDetail.value) {
    currentMessage.value = messageDetail.value
    detailDialogVisible.value = false
    replyDialogVisible.value = true
  }
}

// 回复管理相关方法
const handleManageReplies = () => {
  if (messageDetail.value) {
    currentMessage.value = messageDetail.value
    replyManageDialogVisible.value = true
  }
}

const handleReplyManageDialogClose = () => {
  replyManageDialogVisible.value = false
  currentMessage.value = null
}

const handleReplyAction = async (command, reply) => {
  switch (command) {
    case 'approve':
      await handleApproveReply(reply)
      break
    case 'reject':
      await handleRejectReply(reply)
      break
    case 'edit':
      handleEditReply(reply)
      break
    case 'delete':
      await handleDeleteReply(reply)
      break
  }
}

const handleApproveReply = async (reply) => {
  try {
    const response = await messageApi.approveMessage(reply.id)
    if (response.code === 200) {
      ElMessage.success('回复已通过')
      reply.status = 'approved'
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('审核回复失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleRejectReply = async (reply) => {
  try {
    const response = await messageApi.rejectMessage(reply.id)
    if (response.code === 200) {
      ElMessage.success('回复已拒绝')
      reply.status = 'rejected'
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('拒绝回复失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleEditReply = (reply) => {
  currentReply.value = reply
  editReplyForm.id = reply.id
  editReplyForm.content = reply.content
  editReplyForm.status = reply.status
  editReplyDialogVisible.value = true
}

const handleEditReplyDialogClose = () => {
  editReplyDialogVisible.value = false
  currentReply.value = null
  editReplyForm.id = null
  editReplyForm.content = ''
  editReplyForm.status = 'pending'
  editReplyFormRef.value?.resetFields()
}

const handleSubmitEditReply = async () => {
  if (!editReplyFormRef.value) return
  
  try {
    const valid = await editReplyFormRef.value.validate().catch(() => false)
    if (!valid) return
    
    editingReply.value = true
    
    const response = await messageApi.updateMessage(editReplyForm.id, {
      content: editReplyForm.content,
      status: editReplyForm.status
    })
    
    if (response.code === 200) {
      ElMessage.success('回复修改成功')
      
      // 更新当前回复数据
      if (currentReply.value) {
        currentReply.value.content = editReplyForm.content
        currentReply.value.status = editReplyForm.status
      }
      
      editReplyDialogVisible.value = false
    } else {
      ElMessage.error(response.message || '修改失败')
    }
  } catch (error) {
    console.error('修改回复失败:', error)
    ElMessage.error('修改失败')
  } finally {
    editingReply.value = false
  }
}

const handleDeleteReply = async (reply, event) => {
  if (event) {
    event.stopPropagation()
  }
  
  const confirmed = await showDeleteConfirm(
    '确定要删除这条回复吗？删除后无法恢复。',
    '删除回复',
    event
  )
  
  if (confirmed) {
    try {
      const response = await messageApi.deleteMessage(reply.id)
      if (response.code === 200) {
        ElMessage.success('回复删除成功')
        
        // 从当前消息的回复列表中移除
        if (currentMessage.value && currentMessage.value.replies) {
          const index = currentMessage.value.replies.findIndex(r => r.id === reply.id)
          if (index > -1) {
            currentMessage.value.replies.splice(index, 1)
          }
        }
        
        // 从详情中移除
        if (messageDetail.value && messageDetail.value.replies) {
          const index = messageDetail.value.replies.findIndex(r => r.id === reply.id)
          if (index > -1) {
            messageDetail.value.replies.splice(index, 1)
          }
        }
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除回复失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量审核通过
const handleBatchApprove = async () => {
  if (selectedMessages.value.length === 0) {
    ElMessage.warning('请选择要审核的留言')
    return
  }
  
  try {
    const ids = selectedMessages.value.map(m => m.id)
    const response = await messageApi.batchApproveMessages(ids)
    if (response.code === 200) {
      ElMessage.success('批量审核成功')
      getMessageStats()
      getMessages()
    } else {
      ElMessage.error(response.message || '批量审核失败')
    }
  } catch (error) {
    console.error('批量审核失败:', error)
    ElMessage.error('批量审核失败')
  }
}

// 批量拒绝
const handleBatchReject = async (event) => {
  if (selectedMessages.value.length === 0) {
    ElMessage.warning('请选择要拒绝的留言')
    return
  }
  
  const confirmed = await showWarningConfirm(
    `确定要拒绝选中的 ${selectedMessages.value.length} 条留言吗？`,
    '批量拒绝',
    event
  )
  
  if (confirmed) {
    try {
      const ids = selectedMessages.value.map(m => m.id)
      const response = await messageApi.batchRejectMessages(ids)
      if (response.code === 200) {
        ElMessage.success('批量拒绝成功')
        getMessageStats()
        getMessages()
      } else {
        ElMessage.error(response.message || '批量拒绝失败')
      }
    } catch (error) {
      console.error('批量拒绝失败:', error)
      ElMessage.error('批量拒绝失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async (event) => {
  if (selectedMessages.value.length === 0) {
    ElMessage.warning('请选择要删除的留言')
    return
  }
  
  const confirmed = await showDeleteConfirm(
    `确定要删除选中的 ${selectedMessages.value.length} 条留言吗？删除后无法恢复。`,
    '批量删除',
    event
  )
  
  if (confirmed) {
    try {
      const ids = selectedMessages.value.map(m => m.id)
      const response = await messageApi.batchDeleteMessages(ids)
      if (response.code === 200) {
        ElMessage.success('批量删除成功')
        getMessageStats()
        getMessages()
      } else {
        ElMessage.error(response.message || '批量删除失败')
      }
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case 'approved':
      return 'success'
    case 'rejected':
      return 'danger'
    case 'pending':
    default:
      return 'warning'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'approved':
      return '已通过'
    case 'rejected':
      return '已拒绝'
    case 'pending':
    default:
      return '待审核'
  }
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 获取留言统计
const getMessageStats = async () => {
  try {
    const response = await messageApi.getMessageStats()
    if (response.code === 200) {
      messageStats.value = response.data
    } else {
      console.error('获取留言统计失败:', response.message)
    }
  } catch (error) {
    console.error('获取留言统计失败:', error)
  }
}

// 导出留言数据
const handleExportMessages = async () => {
  try {
    // 构建导出参数
    const exportParams = {}
    if (searchForm.status) {
      exportParams.status = searchForm.status
    }
    if (searchForm.userNickname) {
      exportParams.userNickname = searchForm.userNickname
    }
    if (searchForm.content) {
      exportParams.content = searchForm.content
    }
    
    const response = await messageApi.exportMessages(exportParams)
    
    // 创建下载链接
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    
    // 生成文件名
    const now = new Date()
    const timestamp = now.getFullYear() + 
      String(now.getMonth() + 1).padStart(2, '0') + 
      String(now.getDate()).padStart(2, '0') + '_' +
      String(now.getHours()).padStart(2, '0') + 
      String(now.getMinutes()).padStart(2, '0') + 
      String(now.getSeconds()).padStart(2, '0')
    
    link.download = `留言数据_${timestamp}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

// 获取留言趋势数据
const getMessageTrend = async () => {
  chartLoading.value = true
  try {
    const response = await messageApi.getMessageTrend(trendDays.value)
    if (response.code === 200) {
      const data = response.data
      trendData.value = {
        dates: data.dates || [],
        counts: data.counts || [],
        statusCounts: {
          approved: data.approvedCounts || [],
          pending: data.pendingCounts || [],
          rejected: data.rejectedCounts || []
        }
      }
      
      // 计算状态分布
      const total = data.totalApproved + data.totalPending + data.totalRejected
      statusDistribution.value = {
        approved: total > 0 ? Math.round((data.totalApproved / total) * 100) : 0,
        pending: total > 0 ? Math.round((data.totalPending / total) * 100) : 0,
        rejected: total > 0 ? Math.round((data.totalRejected / total) * 100) : 0
      }
    } else {
      ElMessage.error(response.message || '获取趋势数据失败')
    }
  } catch (error) {
    console.error('获取趋势数据失败:', error)
    ElMessage.error('获取趋势数据失败')
  } finally {
    chartLoading.value = false
  }
}

// 切换趋势天数
const handleTrendDaysChange = (days) => {
  trendDays.value = days
  getMessageTrend()
}

// 展开/收起留言内容
const toggleExpand = (message) => {
  message.expanded = !message.expanded
}

// 处理更多操作
const handleMoreAction = async (command, message) => {
  switch (command) {
    case 'reply':
      handleReply(message)
      break
    case 'copy':
      try {
        await navigator.clipboard.writeText(message.content)
        ElMessage.success('内容已复制到剪贴板')
      } catch (error) {
        console.error('复制失败:', error)
        ElMessage.error('复制失败')
      }
      break
    case 'reset':
      try {
        const response = await messageApi.updateMessage(message.id, {
          status: 'pending'
        })
        if (response.code === 200) {
          ElMessage.success('已重置为待审核状态')
          getMessageStats()
          getMessages()
          getMessageTrend()
        } else {
          ElMessage.error(response.message || '重置失败')
        }
      } catch (error) {
        console.error('重置失败:', error)
        ElMessage.error('重置失败')
      }
      break
    case 'delete':
      const event = { stopPropagation: () => {} }
      handleDelete(message, event)
      break
  }
}

// 趋势图表配置
const trendChartOption = computed(() => ({
  title: {
    text: '留言趋势',
    left: 'center',
    textStyle: {
      fontSize: 16,
      fontWeight: 'bold'
    }
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  legend: {
    data: ['总数', '已通过', '待审核', '已拒绝'],
    top: 30
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    top: '15%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: trendData.value.dates,
    axisLabel: {
      rotate: 45
    }
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '总数',
      type: 'line',
      data: trendData.value.counts,
      smooth: true,
      lineStyle: {
        color: '#409EFF'
      },
      itemStyle: {
        color: '#409EFF'
      }
    },
    {
      name: '已通过',
      type: 'line',
      data: trendData.value.statusCounts.approved,
      smooth: true,
      lineStyle: {
        color: '#67C23A'
      },
      itemStyle: {
        color: '#67C23A'
      }
    },
    {
      name: '待审核',
      type: 'line',
      data: trendData.value.statusCounts.pending,
      smooth: true,
      lineStyle: {
        color: '#E6A23C'
      },
      itemStyle: {
        color: '#E6A23C'
      }
    },
    {
      name: '已拒绝',
      type: 'line',
      data: trendData.value.statusCounts.rejected,
      smooth: true,
      lineStyle: {
        color: '#F56C6C'
      },
      itemStyle: {
        color: '#F56C6C'
      }
    }
  ]
}))

// 状态分布饼图配置
const statusChartOption = computed(() => ({
  title: {
    text: '状态分布',
    left: 'center',
    textStyle: {
      fontSize: 16,
      fontWeight: 'bold'
    }
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c}% ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    top: 'middle'
  },
  series: [
    {
      name: '留言状态',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        {
          value: statusDistribution.value.approved,
          name: '已通过',
          itemStyle: { color: '#67C23A' }
        },
        {
          value: statusDistribution.value.pending,
          name: '待审核',
          itemStyle: { color: '#E6A23C' }
        },
        {
          value: statusDistribution.value.rejected,
          name: '已拒绝',
          itemStyle: { color: '#F56C6C' }
        }
      ]
    }
  ]
}))

// 初始化
onMounted(() => {
  getMessageStats()
  getMessages()
  getMessageTrend()
})
</script>

<style scoped>
.admin-messages {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.stats-section {
  margin-bottom: 20px;
}

.stats-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-2px);
}

.charts-section {
  margin-bottom: 20px;
}

.chart-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
  width: 100%;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.action-buttons .el-button {
  margin: 0;
}

/* 展开/收起按钮样式 */
.expand-btn {
  color: #409eff;
  cursor: pointer;
  font-size: 12px;
  margin-left: 8px;
}

.expand-btn:hover {
  text-decoration: underline;
}

.stats-content {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: #fff;
}

.stats-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-icon.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stats-icon.approved {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stats-icon.rejected {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.search-section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.table-section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info span {
  font-size: 14px;
  color: #606266;
}

.message-content {
  line-height: 1.6;
}

.message-content p {
  margin: 0;
  word-break: break-word;
}

.reply-to {
  margin-top: 8px;
  padding: 8px;
  background: #f5f7fa;
  border-left: 3px solid #409eff;
  font-size: 12px;
  color: #909399;
}

.batch-actions {
  margin-top: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.batch-actions span {
  color: #606266;
  font-size: 14px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.original-message {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.original-message h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

.message-info .user-info {
  margin-bottom: 8px;
}

.message-info .user-info .time {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
}

.message-info .content {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

/* 详情对话框样式 */
.message-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 2px solid #e4e7ed;
  padding-bottom: 8px;
}

.message-content-detail {
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.message-content-detail p {
  margin: 0;
  color: #303133;
  line-height: 1.8;
  word-break: break-word;
}

.replies-list {
  max-height: 300px;
  overflow-y: auto;
}

.reply-item {
  padding: 12px;
  margin-bottom: 12px;
  background-color: #fafafa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.reply-user .nickname {
  font-weight: 500;
  color: #303133;
}

.reply-time {
  color: #909399;
  font-size: 12px;
}

.reply-content {
  margin-left: 38px;
}

.reply-content p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
  word-break: break-word;
}

.replies-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.replies-actions {
  display: flex;
  gap: 10px;
}

.reply-manage-content {
  padding: 10px 0;
}

.original-message-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.original-message-info h4 {
  margin: 0;
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

.reply-content-preview {
  line-height: 1.4;
  color: #606266;
}

/* 高级搜索样式 */
.advanced-search {
  margin-top: 16px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.advanced-search .el-form {
  margin-bottom: 0;
}

.advanced-search .el-form-item {
  margin-bottom: 16px;
}

.advanced-search .el-form-item:last-child {
  margin-bottom: 0;
}

.dialog-footer {
  text-align: right;
}

</style>