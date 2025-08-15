<template>
  <div class="supplier-analytics">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>供应商类别分布</span>
              <el-tooltip content="展示不同类别供应商的数量分布" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>供应商信用等级分布</span>
              <el-tooltip content="展示不同信用等级供应商的数量分布" placement="top">
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div ref="creditChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { QuestionFilled } from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册必要的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  PieChart,
  BarChart,
  CanvasRenderer
])

const props = defineProps<{
  supplierList: any[]
}>()

const categoryChartRef = ref<HTMLElement>()
const creditChartRef = ref<HTMLElement>()
let categoryChart: echarts.ECharts | null = null
let creditChart: echarts.ECharts | null = null

// 初始化图表
const initCharts = () => {
  if (categoryChartRef.value) {
    categoryChart = echarts.init(categoryChartRef.value)
  }
  
  if (creditChartRef.value) {
    creditChart = echarts.init(creditChartRef.value)
  }
  
  updateCharts()
}

// 更新图表数据
const updateCharts = () => {
  if (!props.supplierList.length) return
  
  // 类别分布数据
  const categoryData = getCategoryDistribution()
  if (categoryChart) {
    categoryChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        data: categoryData.map(item => item.name)
      },
      series: [
        {
          name: '供应商类别',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: categoryData
        }
      ]
    })
  }
  
  // 信用等级分布数据
  const creditData = getCreditDistribution()
  if (creditChart) {
    creditChart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: creditData.map(item => item.name),
        axisTick: {
          alignWithLabel: true
        }
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '供应商数量',
          type: 'bar',
          barWidth: '60%',
          data: creditData.map(item => ({
            value: item.value,
            itemStyle: {
              color: getCreditColor(item.name)
            }
          }))
        }
      ]
    })
  }
}

// 获取类别分布数据
const getCategoryDistribution = () => {
  const categoryMap = new Map()
  
  props.supplierList.forEach(supplier => {
    const category = supplier.category || '未分类'
    categoryMap.set(category, (categoryMap.get(category) || 0) + 1)
  })
  
  return Array.from(categoryMap.entries()).map(([name, value]) => ({ name, value }))
}

// 获取信用等级分布数据
const getCreditDistribution = () => {
  const creditMap = new Map()
  const creditOrder = ['AAA', 'AA', 'A', 'B', 'C', '未评级']
  
  props.supplierList.forEach(supplier => {
    const credit = supplier.creditRating || '未评级'
    creditMap.set(credit, (creditMap.get(credit) || 0) + 1)
  })
  
  // 确保所有等级都有数据
  creditOrder.forEach(credit => {
    if (!creditMap.has(credit)) {
      creditMap.set(credit, 0)
    }
  })
  
  // 按信用等级排序
  return creditOrder
    .filter(credit => creditMap.has(credit))
    .map(credit => ({ name: credit, value: creditMap.get(credit) }))
}

// 获取信用等级对应的颜色
const getCreditColor = (credit: string) => {
  switch (credit) {
    case 'AAA':
      return '#67C23A'
    case 'AA':
      return '#95D475'
    case 'A':
      return '#409EFF'
    case 'B':
      return '#E6A23C'
    case 'C':
      return '#F56C6C'
    default:
      return '#909399'
  }
}

// 监听窗口大小变化
const handleResize = () => {
  categoryChart?.resize()
  creditChart?.resize()
}

// 监听供应商列表变化
watch(() => props.supplierList, () => {
  updateCharts()
}, { deep: true })

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  categoryChart?.dispose()
  creditChart?.dispose()
})
</script>

<style scoped>
.supplier-analytics {
  margin-bottom: 20px;
}

.chart-card {
  height: 350px;
}

.chart-header {
  display: flex;
  align-items: center;
}

.chart-header .el-icon {
  margin-left: 8px;
  font-size: 16px;
  color: #909399;
  cursor: help;
}

.chart-container {
  height: 280px;
}
</style>