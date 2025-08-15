<template>
  <div class="supplier-stats">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-icon total">
            <el-icon><DataLine /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-title">供应商总数</div>
            <div class="stats-value">{{ stats.total }}</div>
            <div class="stats-desc">较上月 <span :class="stats.growthRate >= 0 ? 'up' : 'down'">
              {{ stats.growthRate >= 0 ? '+' : '' }}{{ stats.growthRate }}%
            </span></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-icon active">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-title">活跃供应商</div>
            <div class="stats-value">{{ stats.active }}</div>
            <div class="stats-desc">占比 {{ Math.round((stats.active / stats.total) * 100) }}%</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-icon inactive">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-title">停用供应商</div>
            <div class="stats-value">{{ stats.inactive }}</div>
            <div class="stats-desc">占比 {{ Math.round((stats.inactive / stats.total) * 100) }}%</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-icon high-credit">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-title">高信用等级</div>
            <div class="stats-value">{{ stats.highCredit }}</div>
            <div class="stats-desc">AAA/AA级供应商</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { DataLine, CircleCheck, CircleClose, Star } from '@element-plus/icons-vue'

interface SupplierStats {
  total: number
  active: number
  inactive: number
  highCredit: number
  growthRate: number
}

const props = defineProps<{
  supplierList: any[]
}>()

const stats = ref<SupplierStats>({
  total: 0,
  active: 0,
  inactive: 0,
  highCredit: 0,
  growthRate: 0
})

// 计算统计数据
const calculateStats = () => {
  const total = props.supplierList.length
  const active = props.supplierList.filter(item => item.status === 'normal').length
  const inactive = total - active
  const highCredit = props.supplierList.filter(item => 
    item.creditRating === 'AAA' || item.creditRating === 'AA'
  ).length
  
  // 模拟增长率数据，实际项目中应从后端获取
  const growthRate = 5.8
  
  stats.value = {
    total,
    active,
    inactive,
    highCredit,
    growthRate
  }
}

// 监听供应商列表变化
watch(() => props.supplierList, () => {
  calculateStats()
}, { deep: true })

onMounted(() => {
  calculateStats()
})
</script>

<style scoped>
.supplier-stats {
  margin-bottom: 20px;
}

.stats-card {
  display: flex;
  align-items: center;
  padding: 15px;
  height: 100px;
}

.stats-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-right: 15px;
  color: #fff;
  font-size: 24px;
}

.stats-icon.total {
  background-color: #409eff;
}

.stats-icon.active {
  background-color: #67c23a;
}

.stats-icon.inactive {
  background-color: #f56c6c;
}

.stats-icon.high-credit {
  background-color: #e6a23c;
}

.stats-info {
  flex: 1;
}

.stats-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stats-desc {
  font-size: 12px;
  color: #909399;
}

.up {
  color: #67c23a;
}

.down {
  color: #f56c6c;
}
</style>