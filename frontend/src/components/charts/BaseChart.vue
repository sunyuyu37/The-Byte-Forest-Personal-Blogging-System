<template>
  <div class="chart-container">
    <v-chart
      :option="chartOption"
      :style="{ height: height, width: width }"
      :autoresize="true"
      @click="handleChartClick"
    />
  </div>
</template>

<script setup>
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

// 注册必要的组件
use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent
])

const props = defineProps({
  option: {
    type: Object,
    required: true
  },
  height: {
    type: String,
    default: '400px'
  },
  width: {
    type: String,
    default: '100%'
  }
})

const emit = defineEmits(['chart-click'])

const chartOption = computed(() => {
  return {
    backgroundColor: 'transparent',
    ...props.option
  }
})

const handleChartClick = (params) => {
  emit('chart-click', params)
}
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
}
</style>