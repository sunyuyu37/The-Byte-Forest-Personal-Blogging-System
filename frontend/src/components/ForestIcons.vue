<template>
  <div class="forest-icons">
    <!-- 树叶图标 -->
    <div class="leaf-icon" v-if="name === 'leaf'">
      <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path 
          d="M12 3C16.9706 3 21 7.02944 21 12C21 16.9706 16.9706 21 12 21C9.5 21 7 19.5 6 17C5 14.5 6 10 12 3Z" 
          :fill="color" 
          fill-opacity="0.8"
        />
        <path 
          d="M12 3C12 8 14 12 18 15L12 21C9 18 7.5 14 12 3Z" 
          :fill="shadeColor" 
          fill-opacity="0.3"
        />
        <path 
          d="M12 3L16 8L20 11L18 15L15 18L12 21" 
          stroke="rgba(255,255,255,0.3)" 
          stroke-width="1" 
          fill="none"
        />
      </svg>
    </div>

    <!-- 树木锁定图标 -->
    <div class="tree-lock-icon" v-else-if="name === 'tree-lock'">
      <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <!-- 树干 -->
        <rect x="11" y="16" width="2" height="6" :fill="color" />
        
        <!-- 树冠层 -->
        <circle cx="12" cy="8" r="6" :fill="color" fill-opacity="0.7" />
        <circle cx="12" cy="10" r="4.5" :fill="shadeColor" fill-opacity="0.5" />
        <circle cx="12" cy="12" r="3" :fill="color" fill-opacity="0.8" />
        
        <!-- 锁定形状 -->
        <rect x="10" y="13" width="4" height="3" rx="0.5" fill="rgba(255,255,255,0.9)" />
        <path 
          d="M10.5 13V11.5C10.5 10.6716 11.1716 10 12 10C12.8284 10 13.5 10.6716 13.5 11.5V13" 
          stroke="rgba(255,255,255,0.9)" 
          stroke-width="1" 
          fill="none"
        />
        <circle cx="12" cy="14.5" r="0.5" fill="rgba(0,0,0,0.6)" />
      </svg>
    </div>

    <!-- 小树苗图标 -->
    <div class="sapling-icon" v-else-if="name === 'sapling'">
      <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <!-- 土壤 -->
        <ellipse cx="12" cy="20" rx="8" ry="2" fill="rgba(139, 69, 19, 0.6)" />
        
        <!-- 主茎 -->
        <path d="M12 20L12 12" stroke="#228B22" stroke-width="2" stroke-linecap="round" />
        
        <!-- 左叶子 -->
        <path 
          d="M12 14C8 12 6 10 8 8C10 10 12 12 12 14Z" 
          :fill="color" 
          fill-opacity="0.8"
        />
        
        <!-- 右叶子 -->
        <path 
          d="M12 14C16 12 18 10 16 8C14 10 12 12 12 14Z" 
          :fill="shadeColor" 
          fill-opacity="0.6"
        />
        
        <!-- 叶脉 -->
        <path d="M10 10L12 14" stroke="rgba(255,255,255,0.3)" stroke-width="1" />
        <path d="M14 10L12 14" stroke="rgba(255,255,255,0.3)" stroke-width="1" />
      </svg>
    </div>

    <!-- 森林图标 -->
    <div class="forest-icon" v-else-if="name === 'forest'">
      <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <!-- 背景树 -->
        <circle cx="6" cy="10" r="3" :fill="shadeColor" fill-opacity="0.4" />
        <rect x="5.5" y="13" width="1" height="4" :fill="shadeColor" fill-opacity="0.6" />
        
        <circle cx="18" cy="9" r="2.5" :fill="shadeColor" fill-opacity="0.4" />
        <rect x="17.5" y="11.5" width="1" height="3.5" :fill="shadeColor" fill-opacity="0.6" />
        
        <!-- 主要树木 -->
        <circle cx="12" cy="8" r="4" :fill="color" fill-opacity="0.8" />
        <circle cx="12" cy="10" r="3" :fill="shadeColor" fill-opacity="0.6" />
        <rect x="11.5" y="12" width="1" height="5" :fill="color" />
        
        <!-- 地面 -->
        <ellipse cx="12" cy="19" rx="10" ry="1.5" fill="rgba(139, 69, 19, 0.3)" />
      </svg>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: {
    type: String,
    required: true,
    validator: (value) => ['leaf', 'tree-lock', 'sapling', 'forest'].includes(value)
  },
  size: {
    type: [String, Number],
    default: 24
  },
  color: {
    type: String,
    default: '#2E8B57'
  }
})

const shadeColor = computed(() => {
  // 根据主颜色生成较深的阴影色
  const hex = props.color.replace('#', '')
  const r = Math.max(0, parseInt(hex.substr(0, 2), 16) - 40)
  const g = Math.max(0, parseInt(hex.substr(2, 2), 16) - 40)
  const b = Math.max(0, parseInt(hex.substr(4, 2), 16) - 40)
  return `rgb(${r}, ${g}, ${b})`
})
</script>

<style scoped lang="scss">
.forest-icons {
  display: inline-block;
  
  .leaf-icon,
  .tree-lock-icon,
  .sapling-icon,
  .forest-icon {
    transition: all 0.3s ease;
    
    &:hover {
      transform: scale(1.1);
      filter: drop-shadow(0 0 8px rgba(46, 139, 87, 0.4));
    }
  }
  
  svg {
    transition: all 0.3s ease;
  }
}
</style>