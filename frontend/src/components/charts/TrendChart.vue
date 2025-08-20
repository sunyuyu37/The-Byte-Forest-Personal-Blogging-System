<template>
  <div class="trend-chart">
    <BaseChart :option="chartOption" height="350px" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      dates: [],
      articles: [],
      views: [],
      comments: []
    })
  },
  period: {
    type: String,
    default: 'week'
  }
})

const chartOption = computed(() => {
  const { dates, articles, views, comments } = props.data
  
  return {
    title: {
      text: '数据趋势分析',
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
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: 'rgba(102, 126, 234, 0.8)',
          borderColor: '#667eea',
          borderWidth: 1,
          color: '#fff'
        },
        lineStyle: {
          color: 'rgba(102, 126, 234, 0.6)',
          type: 'dashed'
        },
        crossStyle: {
          color: 'rgba(102, 126, 234, 0.6)'
        }
      },
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
      data: ['文章发布', '阅读量', '评论数'],
      bottom: 5,
      textStyle: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: '500'
      },
      itemGap: 24,
      icon: 'circle',
      itemWidth: 12,
      itemHeight: 12
    },
    grid: {
      left: '5%',
      right: '8%',
      bottom: '15%',
      top: '20%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#64748b',
        fontSize: 12,
        fontWeight: '500',
        margin: 12
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: 'rgba(100, 116, 139, 0.1)',
          type: 'dashed'
        }
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '数量',
        position: 'left',
        nameTextStyle: {
          color: '#64748b',
          fontSize: 12,
          fontWeight: '500'
        },
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          color: '#64748b',
          fontSize: 12,
          fontWeight: '500'
        },
        splitLine: {
          lineStyle: {
            color: 'rgba(100, 116, 139, 0.1)',
            type: 'dashed'
          }
        }
      },
      {
        type: 'value',
        name: '阅读量',
        position: 'right',
        nameTextStyle: {
          color: '#64748b',
          fontSize: 12,
          fontWeight: '500'
        },
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          color: '#64748b',
          fontSize: 12,
          fontWeight: '500'
        },
        splitLine: {
          show: false
        }
      }
    ],
    series: [
      {
        name: '文章发布',
        type: 'line',
        data: articles,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          color: '#667eea',
          width: 4,
          shadowColor: 'rgba(102, 126, 234, 0.3)',
          shadowBlur: 10,
          shadowOffsetY: 3
        },
        itemStyle: {
          color: '#667eea',
          borderColor: '#fff',
          borderWidth: 3,
          shadowColor: 'rgba(102, 126, 234, 0.4)',
          shadowBlur: 8
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(102, 126, 234, 0.4)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
            ]
          }
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 15,
            shadowOffsetY: 5
          }
        },
        animationDelay: 0
      },
      {
        name: '评论数',
        type: 'line',
        data: comments,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          color: '#43e97b',
          width: 4,
          shadowColor: 'rgba(67, 233, 123, 0.3)',
          shadowBlur: 10,
          shadowOffsetY: 3
        },
        itemStyle: {
          color: '#43e97b',
          borderColor: '#fff',
          borderWidth: 3,
          shadowColor: 'rgba(67, 233, 123, 0.4)',
          shadowBlur: 8
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(67, 233, 123, 0.4)' },
              { offset: 1, color: 'rgba(67, 233, 123, 0.05)' }
            ]
          }
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 15,
            shadowOffsetY: 5
          }
        },
        animationDelay: 200
      },
      {
        name: '阅读量',
        type: 'line',
        yAxisIndex: 1,
        data: views,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          color: '#f093fb',
          width: 4,
          shadowColor: 'rgba(240, 147, 251, 0.3)',
          shadowBlur: 10,
          shadowOffsetY: 3
        },
        itemStyle: {
          color: '#f093fb',
          borderColor: '#fff',
          borderWidth: 3,
          shadowColor: 'rgba(240, 147, 251, 0.4)',
          shadowBlur: 8
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 15,
            shadowOffsetY: 5
          }
        },
        animationDelay: 400
      }
    ],
    animationEasing: 'cubicOut',
    animationDuration: 1000
  }
})
</script>

<style scoped>
.trend-chart {
  width: 100%;
  height: 100%;
}
</style>