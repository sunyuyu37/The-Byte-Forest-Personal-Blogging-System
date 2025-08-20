<template>
  <div class="comment-wordcloud">
    <div class="chart-header">
      <h3>评论词云</h3>
      <div class="chart-controls">
        <el-select v-model="dataLimit" @change="loadWordCloudData" size="small">
          <el-option label="最近50条" :value="50" />
          <el-option label="最近100条" :value="100" />
          <el-option label="最近200条" :value="200" />
          <el-option label="最近500条" :value="500" />
        </el-select>
        <el-button @click="loadWordCloudData" size="small" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>
    
    <div class="chart-container" v-loading="loading">
      <div ref="wordCloudRef" class="wordcloud-chart"></div>
      <div v-if="!loading && wordCloudData.length === 0" class="no-data">
        <el-empty description="暂无评论数据" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { statsApi } from '@/api/stats'

// 词云图容器引用
const wordCloudRef = ref(null)
const loading = ref(false)
const dataLimit = ref(100)
const wordCloudData = ref([])

// 词云图实例
let wordCloudInstance = null

// 加载词云数据
const loadWordCloudData = async () => {
  loading.value = true
  try {
    const response = await statsApi.getCommentWordCloudData(dataLimit.value)
    if (response.code === 200) {
      wordCloudData.value = response.data || []
      await nextTick()
      renderWordCloud()
    } else {
      ElMessage.error(response.message || '获取词云数据失败')
    }
  } catch (error) {
    console.error('获取词云数据失败:', error)
    ElMessage.error('获取词云数据失败')
  } finally {
    loading.value = false
  }
}

// 渲染词云图 - 智能防重叠布局
const renderWordCloud = () => {
  if (!wordCloudRef.value || wordCloudData.value.length === 0) return

  // 清除之前的内容
  wordCloudRef.value.innerHTML = ''

  const container = wordCloudRef.value
  const containerRect = container.getBoundingClientRect()
  const centerX = containerRect.width / 2
  const centerY = containerRect.height / 2
  
  // 椭圆参数 - 进一步扩大桌面端显示区域
  const ellipseA = Math.min(centerX * 0.92, 480) // 长轴（桌面端更大）
  const ellipseB = Math.min(centerY * 0.82, 360) // 短轴（桌面端更大）
  
  const maxCount = Math.max(...wordCloudData.value.map(item => item[1]))
  const minCount = Math.min(...wordCloudData.value.map(item => item[1]))
  
  // 按词频排序，高频词放在中心
  const sortedData = [...wordCloudData.value].sort((a, b) => b[1] - a[1])
  
  // 存储已放置的词汇位置信息
  const placedWords = []
  
  // 创建词汇元素
  sortedData.forEach((item, index) => {
    const [word, count] = item
    const wordElement = document.createElement('span')
    wordElement.textContent = word
    wordElement.className = 'word-item'
    
    // 标准化频率（0-1）
    const normalizedCount = (count - minCount) / Math.max(1, (maxCount - minCount))
    
    // 重新设计字体大小计算 - 更大、更合理的字号范围
    let fontSize
    if (normalizedCount > 0.85) {
      fontSize = Math.max(36, Math.min(58, normalizedCount * 50 + 18)) // 最高频词：36-58px
    } else if (normalizedCount > 0.65) {
      fontSize = Math.max(28, Math.min(40, normalizedCount * 32 + 12)) // 高频词：28-40px
    } else if (normalizedCount > 0.45) {
      fontSize = Math.max(22, Math.min(30, normalizedCount * 26 + 10)) // 中频词：22-30px
    } else if (normalizedCount > 0.25) {
      fontSize = Math.max(18, Math.min(26, normalizedCount * 20 + 8)) // 低频词：18-26px
    } else {
      fontSize = Math.max(15, Math.min(22, normalizedCount * 16 + 6)) // 最低频词：15-22px
    }
    
    wordElement.style.fontSize = `${fontSize}px`
    
    // 更丰富的颜色方案
    const color = getVibrantColor(index, normalizedCount)
    if (color.includes('linear-gradient')) {
      wordElement.style.background = color
      wordElement.style.webkitBackgroundClip = 'text'
      wordElement.style.backgroundClip = 'text'
      wordElement.style.webkitTextFillColor = 'transparent'
    } else {
      wordElement.style.color = color
    }
    
    wordElement.style.position = 'absolute'
    wordElement.style.cursor = 'pointer'
    wordElement.style.transition = 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
    wordElement.style.fontWeight = normalizedCount > 0.7 ? 'bold' : normalizedCount > 0.4 ? '600' : '500'
    wordElement.style.whiteSpace = 'nowrap'
    wordElement.style.userSelect = 'none'
    wordElement.style.animationDelay = `${index * 0.05}s`
    wordElement.style.lineHeight = '1.2'
    wordElement.style.borderRadius = '6px'
    wordElement.style.padding = '3px 6px'
    
    // 为高频词添加特殊样式
    if (normalizedCount > 0.8) {
      wordElement.style.textShadow = '2px 2px 6px rgba(0, 0, 0, 0.4)'
      wordElement.style.filter = 'drop-shadow(0 0 10px rgba(255, 255, 255, 0.4))'
    } else if (normalizedCount > 0.6) {
      wordElement.style.textShadow = '1px 1px 4px rgba(0, 0, 0, 0.3)'
    } else {
      wordElement.style.textShadow = '1px 1px 3px rgba(0, 0, 0, 0.2)'
    }
    
    // 获取词汇尺寸
    const tempElement = document.createElement('span')
    tempElement.style.fontSize = `${fontSize}px`
    tempElement.style.fontWeight = wordElement.style.fontWeight
    tempElement.style.visibility = 'hidden'
    tempElement.style.position = 'absolute'
    tempElement.style.lineHeight = '1.2'
    tempElement.style.padding = '3px 6px'
    tempElement.textContent = word
    container.appendChild(tempElement)
    const wordWidth = tempElement.offsetWidth
    const wordHeight = tempElement.offsetHeight
    container.removeChild(tempElement)
    
    // 改进的智能布局算法
    let positioned = false
    let attempts = 0
    const maxAttempts = 1000 // 增加尝试次数
    const GOLDEN_ANGLE = 2.399963229728653 // radians
    const maxRadius = Math.max(ellipseA, ellipseB) - 15
    
    while (!positioned && attempts < maxAttempts) {
      let x, y
      if (index === 0) {
        // 第一个词汇放在中心
        x = centerX - wordWidth / 2
        y = centerY - wordHeight / 2
      } else {
        const importance = 1 - (index / sortedData.length)
        const theta = attempts * GOLDEN_ANGLE
        
        // 调整螺旋参数，让高频词更靠近中心，低频词分布更广
        const baseRadius = 15 + 8 * importance // 基础半径
        const stepSize = 4.5 + (2 * (1 - importance)) // 步长动态调整
        const spiralRadius = Math.min(baseRadius + stepSize * attempts, maxRadius)
        
        // 椭圆映射，保持形状美观
        const scaleX = ellipseA / Math.max(ellipseA, ellipseB)
        const scaleY = ellipseB / Math.max(ellipseA, ellipseB)
        
        x = centerX + Math.cos(theta) * spiralRadius * scaleX - wordWidth / 2
        y = centerY + Math.sin(theta) * spiralRadius * scaleY - wordHeight / 2
        
        // 增加随机偏移，避免过于规律的排列
        const randomOffset = 3 + (fontSize / 10) // 大字体偏移更大
        x += (Math.random() - 0.5) * randomOffset
        y += (Math.random() - 0.5) * randomOffset
      }
      
      // 边界约束（桌面端留更宽边距）
      const margin = 12 + (fontSize / 4) // 根据字体大小动态调整边距
      if (x >= margin && x + wordWidth <= containerRect.width - margin &&
          y >= margin && y + wordHeight <= containerRect.height - margin) {
        
        // 重叠检测 - 更严格的距离要求
        let overlapping = false
        const currentRect = { 
          left: x, 
          top: y, 
          right: x + wordWidth, 
          bottom: y + wordHeight, 
          width: wordWidth, 
          height: wordHeight 
        }
        
        for (let placedWord of placedWords) {
          const distance = getMinDistance(currentRect, placedWord)
          // 根据字体大小动态调整最小距离
          const minDistance = 6 + (fontSize / 8) // 字体越大，要求的间距越大
          if (distance < minDistance) { 
            overlapping = true
            break 
          }
        }
        
        if (!overlapping) {
          positioned = true
          wordElement.style.left = `${x}px`
          wordElement.style.top = `${y}px`
          placedWords.push({ ...currentRect, fontSize })
        }
      }
      attempts++
    }
    
    // 如果无法定位，隐藏该词以保证不遮挡（极端情况下）
    if (!positioned) {
      wordElement.style.display = 'none'
      console.warn(`无法为词汇 "${word}" 找到合适位置，已隐藏`)
    }
    
    // 悬停效果保持
    wordElement.addEventListener('mouseenter', () => {
      wordElement.style.transform = 'scale(1.2) translateY(-3px)'
      wordElement.style.zIndex = '100'
      wordElement.style.filter = 'brightness(1.3) saturate(1.4) drop-shadow(0 6px 16px rgba(0, 0, 0, 0.5))'
      wordElement.style.background = color.includes('linear-gradient') ? color : `rgba(255, 255, 255, 0.15)`
      wordElement.style.backdropFilter = 'blur(10px)'
      
      // 为渐变色词汇特殊处理
      if (color.includes('linear-gradient')) {
        wordElement.style.webkitTextStroke = '1px rgba(255, 255, 255, 0.4)'
      } else {
        wordElement.style.textShadow = '0 6px 16px rgba(0, 0, 0, 0.5)'
      }
    })
    
    wordElement.addEventListener('mouseleave', () => {
      wordElement.style.transform = 'scale(1) translateY(0)'
      wordElement.style.zIndex = 'auto'
      wordElement.style.backdropFilter = 'blur(2px)'
      wordElement.style.webkitTextStroke = 'none'
      
      // 恢复原始样式
      if (color.includes('linear-gradient')) {
        wordElement.style.background = color
        wordElement.style.filter = normalizedCount > 0.8 ? 'drop-shadow(0 0 10px rgba(255, 255, 255, 0.4))' : 'none'
      } else {
        wordElement.style.background = 'none'
        wordElement.style.filter = 'none'
        if (normalizedCount > 0.8) {
          wordElement.style.textShadow = '2px 2px 6px rgba(0, 0, 0, 0.4)'
        } else if (normalizedCount > 0.6) {
          wordElement.style.textShadow = '1px 1px 4px rgba(0, 0, 0, 0.3)'
        } else {
          wordElement.style.textShadow = '1px 1px 3px rgba(0, 0, 0, 0.2)'
        }
      }
    })
    
    // 添加点击事件
    wordElement.addEventListener('click', () => {
      ElMessage.info(`"${word}" 出现了 ${count} 次`)
    })
    
    container.appendChild(wordElement)
  })
}

// 计算两个矩形之间的最小距离
const getMinDistance = (rect1, rect2) => {
  // 如果矩形重叠，返回负值
  if (rect1.right > rect2.left && rect1.left < rect2.right &&
      rect1.bottom > rect2.top && rect1.top < rect2.bottom) {
    return -1
  }
  
  // 计算水平距离
  let horizontalDistance = 0
  if (rect1.right < rect2.left) {
    horizontalDistance = rect2.left - rect1.right
  } else if (rect2.right < rect1.left) {
    horizontalDistance = rect1.left - rect2.right
  }
  
  // 计算垂直距离
  let verticalDistance = 0
  if (rect1.bottom < rect2.top) {
    verticalDistance = rect2.top - rect1.bottom
  } else if (rect2.bottom < rect1.top) {
    verticalDistance = rect1.top - rect2.bottom
  }
  
  // 返回欧几里得距离
  return Math.sqrt(horizontalDistance * horizontalDistance + verticalDistance * verticalDistance)
}

// 生成更丰富的颜色方案
const getVibrantColor = (index, normalizedCount) => {
  // 精心设计的颜色调色板
  const colorPalettes = [
    // 暖色调
    { h: 0, s: 85, l: 65 },   // 珊瑚红
    { h: 25, s: 90, l: 60 },  // 橙红
    { h: 45, s: 95, l: 55 },  // 金橙
    { h: 60, s: 88, l: 58 },  // 金黄
    
    // 冷色调
    { h: 200, s: 85, l: 60 }, // 天蓝
    { h: 220, s: 80, l: 65 }, // 蔚蓝
    { h: 240, s: 75, l: 70 }, // 浅蓝
    { h: 260, s: 80, l: 65 }, // 紫蓝
    
    // 中性色调
    { h: 120, s: 70, l: 55 }, // 翠绿
    { h: 140, s: 75, l: 60 }, // 青绿
    { h: 160, s: 80, l: 58 }, // 薄荷绿
    { h: 180, s: 85, l: 62 }, // 青色
    
    // 紫色系
    { h: 280, s: 85, l: 65 }, // 紫色
    { h: 300, s: 80, l: 60 }, // 品红
    { h: 320, s: 85, l: 65 }, // 玫红
    { h: 340, s: 90, l: 60 }, // 粉红
    
    // 特殊色调
    { h: 15, s: 88, l: 62 },  // 朱红
    { h: 35, s: 92, l: 58 },  // 橘色
    { h: 75, s: 85, l: 55 },  // 柠檬黄
    { h: 95, s: 78, l: 58 },  // 黄绿
  ]
  
  // 根据索引选择基础颜色
  const baseColor = colorPalettes[index % colorPalettes.length]
  
  // 根据词频调整颜色属性
  let saturation = baseColor.s
  let lightness = baseColor.l
  let hue = baseColor.h
  
  // 高频词使用更鲜艳的颜色
  if (normalizedCount > 0.8) {
    saturation = Math.min(95, saturation + 10)
    lightness = Math.max(50, lightness - 5)
  } else if (normalizedCount > 0.6) {
    saturation = Math.min(90, saturation + 5)
    lightness = Math.max(55, lightness - 3)
  } else if (normalizedCount < 0.3) {
    saturation = Math.max(60, saturation - 10)
    lightness = Math.min(75, lightness + 8)
  }
  
  // 添加细微的随机变化
  const hueVariation = (Math.random() - 0.5) * 15
  const satVariation = (Math.random() - 0.5) * 8
  const lightVariation = (Math.random() - 0.5) * 6
  
  hue = (hue + hueVariation + 360) % 360
  saturation = Math.max(50, Math.min(100, saturation + satVariation))
  lightness = Math.max(40, Math.min(80, lightness + lightVariation))
  
  // 为最重要的词汇添加特殊效果
  if (index < 3 && normalizedCount > 0.7) {
    return `linear-gradient(45deg, hsl(${hue}, ${saturation}%, ${lightness}%), hsl(${(hue + 30) % 360}, ${Math.min(100, saturation + 10)}%, ${Math.max(35, lightness - 10)}%))`
  }
  
  return `hsl(${hue}, ${saturation}%, ${lightness}%)`
}

// 数组随机排列函数
const shuffleArray = (array) => {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[array[i], array[j]] = [array[j], array[i]]
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadWordCloudData()
})

// 监听窗口大小变化
const handleResize = () => {
  if (wordCloudInstance) {
    wordCloudInstance.resize()
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.comment-wordcloud {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.05);
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.comment-wordcloud::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #f5576c);
  background-size: 300% 100%;
  animation: gradientShift 3s ease-in-out infinite;
  border-radius: 20px 20px 0 0;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.comment-wordcloud:hover {
  transform: translateY(-4px);
  box-shadow: 
    0 16px 40px rgba(0, 0, 0, 0.15),
    0 4px 16px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  margin: -24px -24px 20px -24px;
  padding: 20px 24px 16px 24px;
  border-radius: 20px 20px 0 0;
}

.chart-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.chart-controls {
  display: flex;
  gap: 16px;
  align-items: center;
}

.chart-container {
  flex: 1;
  min-height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.wordcloud-chart {
  width: 100%;
  height: 100%;
  min-height: 400px;
  position: relative;
  overflow: hidden;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.wordcloud-chart::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(102, 126, 234, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(240, 147, 251, 0.05) 0%, transparent 50%);
  pointer-events: none;
  border-radius: 16px;
}

.word-item {
  user-select: none;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: 'Microsoft YaHei', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  text-decoration: none;
  position: relative;
  animation: fadeInScale 0.8s ease-out forwards;
  opacity: 0;
  transform: scale(0.8) translateY(10px);
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  padding: 2px 4px;
  backdrop-filter: blur(2px);
  will-change: transform;
}

@keyframes fadeInScale {
  0% {
    opacity: 0;
    transform: scale(0.6) translateY(20px) rotate(-2deg);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05) translateY(-2px) rotate(1deg);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0) rotate(0deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
}

.word-item:hover {
  transform: scale(1.15) translateY(-2px) !important;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.4) !important;
  z-index: 100 !important;
  filter: brightness(1.2) saturate(1.3);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
}

.word-item:nth-child(odd) {
  animation-delay: 0.1s;
}

.word-item:nth-child(even) {
  animation-delay: 0.2s;
}

/* 为高频词添加特殊效果 */
.word-item[style*="font-size: 3"],
.word-item[style*="font-size: 2"] {
  animation: fadeInScale 1s ease-out forwards, pulse 3s ease-in-out infinite;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4);
}

/* 词汇动画延迟 */
.word-item:nth-child(1) { animation-delay: 0.1s; }
.word-item:nth-child(2) { animation-delay: 0.15s; }
.word-item:nth-child(3) { animation-delay: 0.2s; }
.word-item:nth-child(4) { animation-delay: 0.25s; }
.word-item:nth-child(5) { animation-delay: 0.3s; }
.word-item:nth-child(6) { animation-delay: 0.35s; }
.word-item:nth-child(7) { animation-delay: 0.4s; }
.word-item:nth-child(8) { animation-delay: 0.45s; }
.word-item:nth-child(9) { animation-delay: 0.5s; }
.word-item:nth-child(10) { animation-delay: 0.55s; }
.word-item:nth-child(n+11) { animation-delay: 0.6s; }

.no-data {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.no-data .el-icon {
  font-size: 56px;
  margin-bottom: 20px;
  color: rgba(102, 126, 234, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chart-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    padding: 16px 20px;
  }
  
  .chart-controls {
    justify-content: center;
  }
  
  .wordcloud-chart {
    padding: 16px;
  }
  
  .word-item {
    font-size: 12px !important;
    padding: 6px 12px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-select) {
  border-radius: 12px;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(102, 126, 234, 0.2);
  transition: all 0.3s ease;
}

:deep(.el-select .el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

:deep(.el-button) {
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #7c3aed, #a855f7);
}
</style>