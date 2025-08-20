<template>
  <teleport to="body">
    <div v-if="visible" class="positioned-confirm-overlay" @click="handleOverlayClick">
      <div 
        ref="dialogRef"
        class="positioned-confirm-dialog"
        :style="dialogStyle"
        @click.stop
      >
        <div class="dialog-header">
          <div class="dialog-icon">
            <el-icon v-if="type === 'warning'" class="warning-icon"><Warning /></el-icon>
            <el-icon v-else-if="type === 'error'" class="error-icon"><CircleClose /></el-icon>
            <el-icon v-else-if="type === 'info'" class="info-icon"><InfoFilled /></el-icon>
            <el-icon v-else class="question-icon"><QuestionFilled /></el-icon>
          </div>
          <h3 class="dialog-title">{{ title }}</h3>
        </div>
        
        <div class="dialog-content">
          <p class="dialog-message">{{ message }}</p>
        </div>
        
        <div class="dialog-footer">
          <el-button size="small" @click="handleCancel">
            {{ cancelButtonText }}
          </el-button>
          <el-button 
            :type="type === 'error' ? 'danger' : 'primary'" 
            size="small" 
            @click="handleConfirm"
          >
            {{ confirmButtonText }}
          </el-button>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { Warning, CircleClose, InfoFilled, QuestionFilled } from '@element-plus/icons-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '提示'
  },
  message: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'warning',
    validator: (value) => ['warning', 'error', 'info', 'question'].includes(value)
  },
  confirmButtonText: {
    type: String,
    default: '确定'
  },
  cancelButtonText: {
    type: String,
    default: '取消'
  },
  triggerElement: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['confirm', 'cancel', 'update:visible'])

const dialogRef = ref(null)
const dialogStyle = ref({})

// 计算弹窗位置
const calculatePosition = () => {
  if (!props.triggerElement || !dialogRef.value) return
  
  const triggerRect = props.triggerElement.getBoundingClientRect()
  const dialogRect = dialogRef.value.getBoundingClientRect()
  const viewportWidth = window.innerWidth
  const viewportHeight = window.innerHeight
  
  let left = triggerRect.left + triggerRect.width / 2 - dialogRect.width / 2
  let top = triggerRect.top - dialogRect.height - 10
  
  // 确保弹窗不超出视口
  if (left < 10) left = 10
  if (left + dialogRect.width > viewportWidth - 10) {
    left = viewportWidth - dialogRect.width - 10
  }
  
  // 如果上方空间不够，显示在下方
  if (top < 10) {
    top = triggerRect.bottom + 10
  }
  
  // 确保不超出底部
  if (top + dialogRect.height > viewportHeight - 10) {
    top = viewportHeight - dialogRect.height - 10
  }
  
  dialogStyle.value = {
    position: 'fixed',
    left: `${left}px`,
    top: `${top}px`,
    zIndex: 9999
  }
}

// 处理确认
const handleConfirm = () => {
  emit('confirm')
  emit('update:visible', false)
}

// 处理取消
const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}

// 处理遮罩点击
const handleOverlayClick = () => {
  handleCancel()
}

// 监听窗口大小变化
const handleResize = () => {
  if (props.visible) {
    nextTick(() => {
      calculatePosition()
    })
  }
}

// 监听可见性变化
const updatePosition = () => {
  if (props.visible) {
    nextTick(() => {
      calculatePosition()
    })
  }
}

// 生命周期
onMounted(() => {
  window.addEventListener('resize', handleResize)
  updatePosition()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

// 监听visible变化
const { visible } = toRefs(props)
watch(visible, () => {
  updatePosition()
})
</script>

<script>
import { toRefs, watch } from 'vue'
export default {
  name: 'PositionedConfirm'
}
</script>

<style lang="scss" scoped>
.positioned-confirm-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 9998;
  animation: overlayFadeIn 0.2s ease;
}

.positioned-confirm-dialog {
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  min-width: 280px;
  max-width: 400px;
  animation: dialogSlideIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  
  .dialog-header {
    padding: 20px 20px 0;
    text-align: center;
    
    .dialog-icon {
      margin-bottom: 12px;
      
      .el-icon {
        font-size: 24px;
        
        &.warning-icon {
          color: #e6a23c;
        }
        
        &.error-icon {
          color: #f56c6c;
        }
        
        &.info-icon {
          color: #409eff;
        }
        
        &.question-icon {
          color: #909399;
        }
      }
    }
    
    .dialog-title {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }
  }
  
  .dialog-content {
    padding: 12px 20px 20px;
    
    .dialog-message {
      margin: 0;
      font-size: 14px;
      color: #606266;
      text-align: center;
      line-height: 1.5;
    }
  }
  
  .dialog-footer {
    padding: 0 20px 20px;
    display: flex;
    justify-content: center;
    gap: 12px;
  }
}

@keyframes overlayFadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

@keyframes dialogSlideIn {
  0% {
    opacity: 0;
    transform: scale(0.8) translateY(-20px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>