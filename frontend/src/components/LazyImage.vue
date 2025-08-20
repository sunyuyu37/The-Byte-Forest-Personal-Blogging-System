<template>
  <div ref="imageRef" class="lazy-image-container" :style="{ width, height }">
    <img
      v-if="loaded"
      :src="src"
      :alt="alt"
      :class="['lazy-image', { 'fade-in': loaded }]"
      @load="onLoad"
      @error="onError"
    />
    <div v-else-if="loading" class="lazy-image-placeholder">
      <div class="loading-spinner"></div>
    </div>
    <div v-else-if="error" class="lazy-image-error">
      <i class="el-icon-picture-outline"></i>
      <span>图片加载失败</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: ''
  },
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String,
    default: 'auto'
  },
  placeholder: {
    type: String,
    default: ''
  },
  threshold: {
    type: Number,
    default: 0.1
  }
})

const loading = ref(false)
const loaded = ref(false)
const error = ref(false)
const imageRef = ref(null)
let observer = null

const loadImage = () => {
  if (loaded.value || loading.value) return
  
  loading.value = true
  const img = new Image()
  
  img.onload = () => {
    loaded.value = true
    loading.value = false
  }
  
  img.onerror = () => {
    error.value = true
    loading.value = false
  }
  
  img.src = props.src
}

const onLoad = () => {
  // 图片加载完成后的回调
}

const onError = () => {
  error.value = true
}

const createObserver = () => {
  if (!window.IntersectionObserver) {
    // 如果不支持IntersectionObserver，直接加载图片
    loadImage()
    return
  }
  
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          loadImage()
          observer.unobserve(entry.target)
        }
      })
    },
    {
      threshold: props.threshold,
      rootMargin: '50px'
    }
  )
}

onMounted(() => {
  createObserver()
  if (observer && imageRef.value) {
    observer.observe(imageRef.value)
  }
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style scoped>
.lazy-image-container {
  position: relative;
  overflow: hidden;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.lazy-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
}

.lazy-image.fade-in {
  opacity: 1;
}

.lazy-image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 200px;
  background-color: #f5f7fa;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e4e7ed;
  border-top: 3px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.lazy-image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 200px;
  color: #909399;
  background-color: #f5f7fa;
}

.lazy-image-error i {
  font-size: 48px;
  margin-bottom: 8px;
}

.lazy-image-error span {
  font-size: 14px;
}
</style>