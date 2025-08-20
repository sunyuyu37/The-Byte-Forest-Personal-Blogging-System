<template>
  <div class="auth-layout">
    <!-- 森林背景 -->
    <div class="forest-background">
      <img src="@/styles/imgs/background.jpeg" alt="森林背景" class="background-image" />
      
    </div>
    
    <!-- 飘落的树叶粒子动画 -->
    <div class="falling-leaves">
      <div v-for="i in 35" :key="i" class="leaf" :style="getLeafStyle(i)"></div>
    </div>
    
    <!-- 主要内容容器 -->
    <div class="auth-container">
      <!-- 毛玻璃效果卡片 -->
      <div class="auth-card">
        <!-- Logo区域 -->
        <div class="logo-section">
          <div class="logo-with-icon">
            <ForestIcons name="forest" :size="32" color="#2E8B57" class="logo-icon" />
            <h1 class="logo-title">字节森林</h1>
          </div>
          <p class="logo-subtitle">Deep in the code forest</p>
        </div>
        
        <!-- 路由视图 -->
        <div class="auth-content">
          <router-view v-slot="{ Component, route }">
            <transition name="auth-slide" mode="out-in">
              <component :is="Component" :key="route.path" />
            </transition>
          </router-view>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ForestIcons from '@/components/ForestIcons.vue'

// 生成树叶的随机样式
const getLeafStyle = (index) => {
  const delay = Math.random() * 15  // 扩大延迟范围
  const duration = 6 + Math.random() * 8  // 6-14秒的持续时间范围
  const leftPosition = Math.random() * 110 - 5  // -5% 到 105% 的水平位置
  const leafType = Math.floor(Math.random() * 3) + 1
  const size = 0.7 + Math.random() * 0.8  // 0.7-1.5倍大小变化
  const opacity = 0.1 + Math.random() * 0.3  // 0.1-0.4透明度变化
  const swayIntensity = 30 + Math.random() * 60  // 30-90px摇摆幅度
  const rotationSpeed = 180 + Math.random() * 360  // 180-540度旋转
  
  return {
    left: `${leftPosition}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`,
    '--leaf-type': leafType,
    '--leaf-size': size,
    '--leaf-opacity': opacity,
    '--sway-intensity': `${swayIntensity}px`,
    '--rotation-speed': `${rotationSpeed}deg`
  }
}
</script>

<style scoped lang="scss">
.auth-layout {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

// 森林背景
.forest-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  
  .background-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .background-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(
      135deg,
      rgba(46, 139, 87, 0.3) 0%,
      rgba(74, 124, 89, 0.4) 50%,
      rgba(34, 139, 34, 0.3) 100%
    );
  }
}

// 飘落的树叶动画
.falling-leaves {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
  pointer-events: none;
  overflow: hidden;
  
  .leaf {
    position: absolute;
    top: -50px;
    width: calc(20px * var(--leaf-size, 1));
    height: calc(20px * var(--leaf-size, 1));
    opacity: var(--leaf-opacity, 0.2);
    animation: fallAndSway linear infinite;
    
    &::before {
      content: '';
      position: absolute;
      width: 100%;
      height: 100%;
      background: var(--leaf-color, #4A7C59);
      clip-path: polygon(50% 0%, 100% 38%, 82% 100%, 18% 100%, 0% 38%);
      transform: rotate(var(--leaf-rotation, 45deg));
      filter: blur(0.5px);
    }
    
    // 不同类型的叶子
    &:nth-child(5n+1) {
      --leaf-color: #2E8B57;
      --leaf-rotation: 30deg;
    }
    
    &:nth-child(5n+2) {
      --leaf-color: #4A7C59;
      --leaf-rotation: 60deg;
    }
    
    &:nth-child(5n+3) {
      --leaf-color: #228B22;
      --leaf-rotation: 90deg;
    }
    
    &:nth-child(5n+4) {
      --leaf-color: #32CD32;
      --leaf-rotation: 120deg;
    }
    
    &:nth-child(5n) {
      --leaf-color: #3CB371;
      --leaf-rotation: 150deg;
    }
  }
}

@keyframes fallAndSway {
  0% {
    transform: translateY(-50px) translateX(0) rotate(0deg);
    opacity: 0;
  }
  5% {
    opacity: var(--leaf-opacity, 0.2);
  }
  25% {
    transform: translateY(25vh) translateX(var(--sway-intensity, 50px)) rotate(calc(var(--rotation-speed, 360deg) * 0.25));
  }
  50% {
    transform: translateY(50vh) translateX(calc(var(--sway-intensity, 50px) * -0.8)) rotate(calc(var(--rotation-speed, 360deg) * 0.5));
  }
  75% {
    transform: translateY(75vh) translateX(calc(var(--sway-intensity, 50px) * 0.6)) rotate(calc(var(--rotation-speed, 360deg) * 0.75));
  }
  95% {
    opacity: var(--leaf-opacity, 0.2);
  }
  100% {
    transform: translateY(calc(100vh + 50px)) translateX(calc(var(--sway-intensity, 50px) * -0.3)) rotate(var(--rotation-speed, 360deg));
    opacity: 0;
  }
}

// 认证容器
.auth-container {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

// 毛玻璃效果卡片
.auth-card {
  width: 100%;
  padding: 40px;
  border-radius: 24px;
  backdrop-filter: blur(10px);
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.15) 0%,
    rgba(255, 255, 255, 0.1) 100%
  );
  border: 1px solid;
  border-image: linear-gradient(135deg, 
    rgba(168, 255, 120, 0.3) 0%, 
    rgba(120, 255, 214, 0.3) 100%
  ) 1;
  box-shadow: 
    0 8px 32px rgba(46, 139, 87, 0.1),
    0 4px 16px rgba(74, 124, 89, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, 
      rgba(168, 255, 120, 0.03) 0%, 
      rgba(120, 255, 214, 0.03) 100%
    );
    z-index: -1;
  }
}

// Logo区域
.logo-section {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
  
  .logo-title {
    font-family: 'Zhi Mang Xing', 'Ma Shan Zheng', 'Liu Jian Mao Cao', 'KaiTi', 'STKaiti', cursive;
    font-size: 2.5rem;
    font-weight: 400;
    color: #2E8B57;
    margin: 0 0 8px 0;
    text-shadow: 
      0 0 10px rgba(46, 139, 87, 0.3),
      2px 2px 4px rgba(0, 0, 0, 0.1);
    position: relative;
    transition: all 0.3s ease;
    
    &:hover {
      text-shadow: 
        0 0 20px rgba(46, 139, 87, 0.6),
        0 0 30px rgba(120, 255, 214, 0.4),
        2px 2px 4px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }
    
    // 叶脉纹理效果
    &::after {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      width: 100%;
      height: 1px;
      background: linear-gradient(90deg, 
        transparent 0%, 
        rgba(46, 139, 87, 0.3) 20%, 
        rgba(120, 255, 214, 0.4) 50%, 
        rgba(46, 139, 87, 0.3) 80%, 
        transparent 100%
      );
      transform: translate(-50%, -50%);
      opacity: 0;
      transition: opacity 0.3s ease;
    }
    
    &:hover::after {
      opacity: 1;
    }
  }
  
  .logo-subtitle {
    font-family: Georgia, 'Times New Roman', serif;
    font-size: 14px;
    color: #C8E6C9;
    margin: 0;
    font-style: italic;
    letter-spacing: 1px;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  }
  .logo-with-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    margin-bottom: 6px;

    .logo-icon {
      filter: drop-shadow(0 2px 6px rgba(46, 139, 87, 0.35));
    }
  }
}

// 认证内容区域
.auth-content {
  position: relative;
}

// 页面切换动画
.auth-slide-enter-active,
.auth-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.auth-slide-enter-from {
  opacity: 0;
  transform: translateX(30px) scale(0.95);
}

.auth-slide-leave-to {
  opacity: 0;
  transform: translateX(-30px) scale(0.95);
}

// 响应式设计
@media (max-width: 480px) {
  .auth-container {
    padding: 10px;
  }
  
  .auth-card {
    padding: 30px 20px;
    border-radius: 16px;
  }
  
  .logo-section .logo-title {
    font-size: 2rem;
  }
}
</style>