<template>
  <div class="stats-overview-chart">
    <BaseChart :option="chartOption" height="300px" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      categories: [],
      articles: [],
      views: []
    })
  }
})

const chartOption = computed(() => {
  const { categories, articles, views } = props.data
  
  const colors = [
    ['#667eea', '#764ba2'],
    ['#f093fb', '#f5576c'],
    ['#4facfe', '#00f2fe'],
    ['#43e97b', '#38f9d7'],
    ['#fa709a', '#fee140'],
    ['#a8edea', '#fed6e3'],
    ['#ff9a9e', '#fecfef'],
    ['#ffecd2', '#fcb69f']
  ]
  
  const categoryData = categories.map((category, index) => ({
    name: category,
    value: articles[index] || 0,
    itemStyle: {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 1,
        y2: 1,
        colorStops: [
          { offset: 0, color: colors[index % colors.length][0] },
          { offset: 1, color: colors[index % colors.length][1] }
        ]
      },
      borderRadius: 12,
      borderColor: 'rgba(255, 255, 255, 0.8)',
      borderWidth: 3,
      shadowColor: 'rgba(0, 0, 0, 0.2)',
      shadowBlur: 15,
      shadowOffsetY: 5
    }
  }))

  return {
    title: {
      text: '分类文章分布',
      left: 'center',
      top: 15,
      textStyle: {
        color: '#2c3e50',
        fontSize: 18,
        fontWeight: '700',
        fontFamily: 'PingFang SC, Helvetica Neue, Arial, sans-serif'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} 篇 ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#667eea',
      borderWidth: 1,
      textStyle: {
        color: '#333',
        fontSize: 14,
        fontWeight: '500'
      },
      extraCssText: 'backdrop-filter: blur(10px); border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.15);',
      padding: [12, 16]
    },
    legend: {
      orient: 'horizontal',
      bottom: 5,
      textStyle: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: '500'
      },
      itemGap: 20,
      icon: 'circle',
      itemWidth: 12,
      itemHeight: 12
    },
    series: [
      {
        name: '文章数量',
        type: 'pie',
        radius: ['45%', '75%'],
        center: ['50%', '52%'],
        avoidLabelOverlap: false,
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold',
            color: '#333',
            formatter: '{b}\n{c} 篇'
          },
          itemStyle: {
            shadowBlur: 25,
            shadowOffsetX: 0,
            shadowOffsetY: 8,
            shadowColor: 'rgba(0, 0, 0, 0.3)'
          },
          scaleSize: 8
        },
        labelLine: {
          show: false
        },
        data: categoryData,
        animationType: 'scale',
        animationEasing: 'elasticOut',
        animationDelay: function (idx) {
          return idx * 150
        },
        animationDuration: 1000
      }
    ]
  }
})
</script>

<style scoped>
.stats-overview-chart {
  width: 100%;
  height: 100%;
}
</style>