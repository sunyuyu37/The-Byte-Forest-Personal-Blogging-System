<template>
  <div class="visit-chart">
    <BaseChart :option="chartOption" height="280px" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      hours: [],
      visits: [],
      uniqueVisits: []
    })
  }
})

const chartOption = computed(() => {
  const { hours, visits, uniqueVisits } = props.data
  
  // 确保即使数据为空也显示24小时的X轴
  const defaultHours = Array.from({ length: 24 }, (_, i) => i.toString().padStart(2, '0'))
  const displayHours = hours && hours.length > 0 ? hours : defaultHours
  const displayVisits = visits && visits.length > 0 ? visits : new Array(24).fill(0)
  const displayUniqueVisits = uniqueVisits && uniqueVisits.length > 0 ? uniqueVisits : new Array(24).fill(0)
  
  return {
    title: {
      text: '今日访问量分布',
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
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(102, 126, 234, 0.1)'
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
      padding: [12, 16],
      formatter: function(params) {
        let result = `${params[0].name}:00<br/>`
        params.forEach(param => {
          result += `${param.seriesName}: ${param.value} 次<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['总访问量', '独立访客'],
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
      data: displayHours,
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
        margin: 12,
        formatter: function(value) {
          return value + ':00'
        }
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: 'rgba(100, 116, 139, 0.1)',
          type: 'dashed'
        }
      }
    },
    yAxis: {
      type: 'value',
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
    series: [
      {
        name: '总访问量',
        type: 'bar',
        data: displayVisits,
        barWidth: '35%',
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ]
          },
          borderRadius: [8, 8, 0, 0],
          shadowColor: 'rgba(102, 126, 234, 0.3)',
          shadowBlur: 10,
          shadowOffsetY: 3
        },
        emphasis: {
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#7c3aed' },
                { offset: 1, color: '#a855f7' }
              ]
            },
            shadowBlur: 15,
            shadowOffsetY: 5
          }
        },
        animationDelay: function (idx) {
          return idx * 50
        }
      },
      {
        name: '独立访客',
        type: 'bar',
        data: displayUniqueVisits,
        barWidth: '35%',
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#43e97b' },
              { offset: 1, color: '#38f9d7' }
            ]
          },
          borderRadius: [8, 8, 0, 0],
          shadowColor: 'rgba(67, 233, 123, 0.3)',
          shadowBlur: 10,
          shadowOffsetY: 3
        },
        emphasis: {
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#10b981' },
                { offset: 1, color: '#059669' }
              ]
            },
            shadowBlur: 15,
            shadowOffsetY: 5
          }
        },
        animationDelay: function (idx) {
          return idx * 50 + 100
        }
      }
    ],
    animationEasing: 'cubicOut',
    animationDuration: 1000
  }
})
</script>

<style scoped>
.visit-chart {
  width: 100%;
  height: 100%;
}
</style>