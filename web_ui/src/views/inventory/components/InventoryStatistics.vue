<template>
  <el-dialog
    title="库存统计报表"
    v-model="visible"
    width="1200px"
    @close="handleClose"
  >
    <div class="statistics-container">
      <!-- 统计时间选择 -->
      <div class="statistics-header">
        <el-form :model="statisticsForm" inline>
          <el-form-item label="统计时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="loadStatistics"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadStatistics" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
            <el-button type="success" @click="exportStatistics">
              <el-icon><Download /></el-icon>
              导出报表
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="statistics-cards">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon">
                <el-icon><Box /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.totalProducts || 0 }}</div>
                <div class="stat-label">商品总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">¥{{ formatNumber(statistics.totalStockValue || 0) }}</div>
                <div class="stat-label">库存总价值</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card warning">
            <div class="stat-item">
              <div class="stat-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.lowStockCount || 0 }}</div>
                <div class="stat-label">预警商品</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card danger">
            <div class="stat-item">
              <div class="stat-icon">
                <el-icon><CircleClose /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ statistics.outOfStockCount || 0 }}</div>
                <div class="stat-label">缺货商品</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="20" class="statistics-charts">
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>入库出库统计</span>
                <el-tag type="info">{{ getDateRangeText() }}</el-tag>
              </div>
            </template>
            <div class="chart-container">
              <div class="chart-item">
                <div class="chart-label">
                  <el-icon><Top /></el-icon>
                  入库数量
                </div>
                <div class="chart-value in">{{ statistics.totalInQuantity || 0 }}</div>
              </div>
              <div class="chart-item">
                <div class="chart-label">
                  <el-icon><Money /></el-icon>
                  入库金额
                </div>
                <div class="chart-value in">¥{{ formatNumber(statistics.totalInAmount || 0) }}</div>
              </div>
              <div class="chart-item">
                <div class="chart-label">
                  <el-icon><Bottom /></el-icon>
                  出库数量
                </div>
                <div class="chart-value out">{{ statistics.totalOutQuantity || 0 }}</div>
              </div>
              <div class="chart-item">
                <div class="chart-label">
                  <el-icon><Money /></el-icon>
                  出库金额
                </div>
                <div class="chart-value out">¥{{ formatNumber(statistics.totalOutAmount || 0) }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>库存周转分析</span>
            </template>
            <div class="turnover-analysis">
              <div class="turnover-item">
                <div class="turnover-label">平均库存周转率</div>
                <div class="turnover-value">{{ calculateTurnoverRate() }}</div>
              </div>
              <div class="turnover-item">
                <div class="turnover-label">库存周转天数</div>
                <div class="turnover-value">{{ calculateTurnoverDays() }}天</div>
              </div>
              <div class="turnover-item">
                <div class="turnover-label">库存健康度</div>
                <div class="turnover-value">
                  <el-tag :type="getHealthTagType()">{{ getHealthText() }}</el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分类统计 -->
      <el-row :gutter="20">
        <el-col :span="24">
          <el-card>
            <template #header>
              <span>分类库存统计</span>
            </template>
            <el-table :data="categoryStatsArray" border style="width: 100%">
              <el-table-column prop="category" label="商品分类" min-width="120" />
              <el-table-column prop="productCount" label="商品数量" width="100" align="center" />
              <el-table-column prop="stockCount" label="库存数量" width="100" align="center" />
              <el-table-column label="库存价值" width="120" align="center">
                <template #default="scope">
                  ¥{{ formatNumber(scope.row.stockValue) }}
                </template>
              </el-table-column>
              <el-table-column label="占比" width="100" align="center">
                <template #default="scope">
                  {{ calculatePercentage(scope.row.stockValue) }}%
                </template>
              </el-table-column>
              <el-table-column label="库存状态" width="120" align="center">
                <template #default="scope">
                  <el-progress 
                    :percentage="Math.min(100, (scope.row.stockCount / scope.row.productCount) * 10)" 
                    :color="getProgressColor(scope.row)"
                    :show-text="false"
                    style="width: 80px"
                  />
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Refresh, Download, Box, Money, Warning, CircleClose, 
  Top, Bottom 
} from '@element-plus/icons-vue'
import { getInventoryStatistics, exportInventoryReport } from '@/api/inventory'

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
}>()

const loading = ref(false)
const dateRange = ref<[Date, Date] | null>(null)
const statistics = ref<any>({})
const statisticsForm = reactive({})

// 分类统计数组
const categoryStatsArray = computed(() => {
  if (!statistics.value.categoryStats) return []
  
  return Object.entries(statistics.value.categoryStats).map(([category, stats]: [string, any]) => ({
    category,
    productCount: stats.productCount || 0,
    stockCount: stats.stockCount || 0,
    stockValue: stats.stockValue || 0
  }))
})

// 加载统计数据
const loadStatistics = async () => {
  loading.value = true
  try {
    const params: any = {}
    if (dateRange.value) {
      const [startDate, endDate] = dateRange.value
      params.startDate = startDate.toISOString().split('T')[0]
      params.endDate = endDate.toISOString().split('T')[0]
    }
    
    const response = await getInventoryStatistics(params)
    if (response.code === 200) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

// 导出统计报表
const exportStatistics = async () => {
  try {
    const params: any = {}
    if (dateRange.value) {
      const [startDate, endDate] = dateRange.value
      params.startDate = startDate.toISOString().split('T')[0]
      params.endDate = endDate.toISOString().split('T')[0]
    }
    
    const response = await exportInventoryReport(params)
    if (response.code === 200) {
      ElMessage.success('报表导出成功')
      // 实际项目中这里应该触发文件下载
    }
  } catch (error) {
    console.error('导出报表失败:', error)
    ElMessage.error('导出报表失败')
  }
}

// 格式化数字
const formatNumber = (num: number) => {
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取日期范围文本
const getDateRangeText = () => {
  if (!dateRange.value) return '最近30天'
  const [start, end] = dateRange.value
  return `${start.toLocaleDateString()} - ${end.toLocaleDateString()}`
}

// 计算库存周转率
const calculateTurnoverRate = () => {
  const totalOut = statistics.value.totalOutAmount || 0
  const totalStock = statistics.value.totalStockValue || 1
  return (totalOut / totalStock * 100).toFixed(1) + '%'
}

// 计算库存周转天数
const calculateTurnoverDays = () => {
  const totalOut = statistics.value.totalOutQuantity || 0
  const totalStock = statistics.value.totalProducts || 1
  const days = dateRange.value ? 
    Math.ceil((dateRange.value[1].getTime() - dateRange.value[0].getTime()) / (1000 * 60 * 60 * 24)) : 30
  
  if (totalOut === 0) return '∞'
  return Math.ceil(totalStock / (totalOut / days))
}

// 获取健康度标签类型
const getHealthTagType = () => {
  const turnoverDays = calculateTurnoverDays()
  if (turnoverDays === '∞' || Number(turnoverDays) > 60) return 'danger'
  if (Number(turnoverDays) > 30) return 'warning'
  return 'success'
}

// 获取健康度文本
const getHealthText = () => {
  const turnoverDays = calculateTurnoverDays()
  if (turnoverDays === '∞' || Number(turnoverDays) > 60) return '较差'
  if (Number(turnoverDays) > 30) return '一般'
  return '良好'
}

// 计算百分比
const calculatePercentage = (value: number) => {
  const total = categoryStatsArray.value.reduce((sum, item) => sum + item.stockValue, 0)
  if (total === 0) return '0.0'
  return ((value / total) * 100).toFixed(1)
}

// 获取进度条颜色
const getProgressColor = (row: any) => {
  const ratio = row.stockCount / row.productCount
  if (ratio < 5) return '#f56c6c'
  if (ratio < 10) return '#e6a23c'
  return '#67c23a'
}

// 监听对话框显示状态
watch(() => props.visible, (visible) => {
  if (visible) {
    loadStatistics()
  }
})

// 关闭对话框
const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style scoped>
.statistics-container {
  .statistics-header {
    margin-bottom: 20px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 6px;
  }

  .statistics-cards {
    margin-bottom: 20px;

    .stat-card {
      &.warning {
        border-color: #e6a23c;
        
        .stat-value {
          color: #e6a23c;
        }
      }

      &.danger {
        border-color: #f56c6c;
        
        .stat-value {
          color: #f56c6c;
        }
      }

      .stat-item {
        display: flex;
        align-items: center;
        padding: 20px 0;

        .stat-icon {
          font-size: 32px;
          color: #409eff;
          margin-right: 15px;
        }

        .stat-content {
          flex: 1;

          .stat-value {
            font-size: 28px;
            font-weight: 600;
            color: #409eff;
            margin-bottom: 5px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }

  .statistics-charts {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .chart-container {
      padding: 20px 0;

      .chart-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px 0;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        .chart-label {
          display: flex;
          align-items: center;
          font-size: 14px;
          color: #606266;

          .el-icon {
            margin-right: 5px;
          }
        }

        .chart-value {
          font-size: 18px;
          font-weight: 600;

          &.in {
            color: #67c23a;
          }

          &.out {
            color: #f56c6c;
          }
        }
      }
    }

    .turnover-analysis {
      padding: 20px 0;

      .turnover-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px 0;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        .turnover-label {
          font-size: 14px;
          color: #606266;
        }

        .turnover-value {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }
    }
  }
}

.dialog-footer {
  text-align: right;
}

// 响应式设计
@media (max-width: 768px) {
  .statistics-cards {
    .el-col {
      margin-bottom: 15px;
    }
  }

  .statistics-charts {
    .el-col {
      margin-bottom: 20px;
    }
  }
}
</style>