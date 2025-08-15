<template>
  <div class="data-init-test">
    <div class="container">
      <h2>数据初始化测试</h2>
      
      <div class="stats-section">
        <h3>当前数据统计</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-number">{{ stats.categoryCount || 0 }}</div>
            <div class="stat-label">商品分类</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ stats.productCount || 0 }}</div>
            <div class="stat-label">商品数量</div>
          </div>
        </div>
        <el-button @click="loadStats" :loading="loadingStats">刷新统计</el-button>
      </div>

      <div class="actions-section">
        <h3>数据操作</h3>
        <div class="action-buttons">
          <el-button 
            type="primary" 
            @click="initCategories" 
            :loading="initingCategories"
          >
            初始化分类数据
          </el-button>
          
          <el-button 
            type="success" 
            @click="initProducts" 
            :loading="initingProducts"
          >
            初始化商品数据
          </el-button>
          
          <el-button 
            type="warning" 
            @click="initAllData" 
            :loading="initingAll"
          >
            一键初始化所有数据
          </el-button>
          
          <el-button 
            type="danger" 
            @click="clearAllData" 
            :loading="clearing"
          >
            清空所有数据
          </el-button>
        </div>
      </div>

      <div class="test-section">
        <h3>收银台测试</h3>
        <div class="test-buttons">
          <el-button @click="testCashierAPI" :loading="testing">
            测试收银台API
          </el-button>
          <el-button @click="goToCashier" type="primary">
            前往收银台
          </el-button>
        </div>
      </div>

      <div class="log-section">
        <h3>操作日志</h3>
        <div class="log-content">
          <div v-for="(log, index) in logs" :key="index" class="log-item">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-message" :class="log.type">{{ log.message }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { healthCheck } from '@/api/cashier'

const router = useRouter()

// 响应式数据
const stats = ref({})
const logs = ref([])
const loadingStats = ref(false)
const initingCategories = ref(false)
const initingProducts = ref(false)
const initingAll = ref(false)
const clearing = ref(false)
const testing = ref(false)

// 添加日志
const addLog = (message: string, type: string = 'info') => {
  logs.value.unshift({
    time: new Date().toLocaleTimeString(),
    message,
    type
  })
  // 只保留最近20条日志
  if (logs.value.length > 20) {
    logs.value = logs.value.slice(0, 20)
  }
}

// 加载统计数据
const loadStats = async () => {
  loadingStats.value = true
  try {
    const res = await request({
      url: '/data-init/stats',
      method: 'get'
    })
    if (res.code === 200) {
      // 解析统计信息
      const message = res.data || res.message || ''
      const categoryMatch = message.match(/分类数量:\s*(\d+)/)
      const productMatch = message.match(/商品数量:\s*(\d+)/)
      
      stats.value = {
        categoryCount: categoryMatch ? parseInt(categoryMatch[1]) : 0,
        productCount: productMatch ? parseInt(productMatch[1]) : 0
      }
      
      addLog('统计数据加载成功', 'success')
    } else {
      addLog('加载统计失败: ' + res.message, 'error')
    }
  } catch (error) {
    addLog('加载统计失败: ' + error.message, 'error')
  } finally {
    loadingStats.value = false
  }
}

// 初始化分类数据
const initCategories = async () => {
  initingCategories.value = true
  try {
    const res = await request({
      url: '/data-init/categories',
      method: 'post'
    })
    if (res.code === 200) {
      ElMessage.success('分类数据初始化成功')
      addLog('分类数据初始化成功', 'success')
      await loadStats()
    } else {
      ElMessage.error('初始化失败: ' + res.message)
      addLog('分类初始化失败: ' + res.message, 'error')
    }
  } catch (error) {
    ElMessage.error('初始化失败: ' + error.message)
    addLog('分类初始化失败: ' + error.message, 'error')
  } finally {
    initingCategories.value = false
  }
}

// 初始化商品数据
const initProducts = async () => {
  initingProducts.value = true
  try {
    const res = await request({
      url: '/data-init/products',
      method: 'post'
    })
    if (res.code === 200) {
      ElMessage.success('商品数据初始化成功')
      addLog('商品数据初始化成功', 'success')
      await loadStats()
    } else {
      ElMessage.error('初始化失败: ' + res.message)
      addLog('商品初始化失败: ' + res.message, 'error')
    }
  } catch (error) {
    ElMessage.error('初始化失败: ' + error.message)
    addLog('商品初始化失败: ' + error.message, 'error')
  } finally {
    initingProducts.value = false
  }
}

// 一键初始化所有数据
const initAllData = async () => {
  initingAll.value = true
  try {
    const res = await request({
      url: '/data-init/all',
      method: 'post'
    })
    if (res.code === 200) {
      ElMessage.success('所有数据初始化成功')
      addLog('所有数据初始化成功', 'success')
      await loadStats()
    } else {
      ElMessage.error('初始化失败: ' + res.message)
      addLog('数据初始化失败: ' + res.message, 'error')
    }
  } catch (error) {
    ElMessage.error('初始化失败: ' + error.message)
    addLog('数据初始化失败: ' + error.message, 'error')
  } finally {
    initingAll.value = false
  }
}

// 清空所有数据
const clearAllData = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有数据吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    clearing.value = true
    const res = await request({
      url: '/data-init/clear',
      method: 'delete'
    })
    
    if (res.code === 200) {
      ElMessage.success('数据清空成功')
      addLog('数据清空成功', 'success')
      await loadStats()
    } else {
      ElMessage.error('清空失败: ' + res.message)
      addLog('数据清空失败: ' + res.message, 'error')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败: ' + error.message)
      addLog('数据清空失败: ' + error.message, 'error')
    }
  } finally {
    clearing.value = false
  }
}

// 测试收银台API
const testCashierAPI = async () => {
  testing.value = true
  try {
    const res = await healthCheck()
    if (res.code === 200) {
      const data = res.data
      ElMessage.success('收银台API测试成功')
      addLog(`收银台API正常 - 商品:${data.productCount}, 分类:${data.categoryCount}`, 'success')
    } else {
      ElMessage.error('收银台API异常: ' + res.message)
      addLog('收银台API异常: ' + res.message, 'error')
    }
  } catch (error) {
    ElMessage.error('收银台API测试失败: ' + error.message)
    addLog('收银台API测试失败: ' + error.message, 'error')
  } finally {
    testing.value = false
  }
}

// 前往收银台
const goToCashier = () => {
  router.push('/dashboard/cashier')
}

// 页面初始化
onMounted(() => {
  addLog('数据初始化测试页面加载完成', 'info')
  loadStats()
})
</script>

<style scoped>
.data-init-test {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

h2 {
  text-align: center;
  color: #303133;
  margin-bottom: 30px;
}

h3 {
  color: #606266;
  margin-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 8px;
}

.stats-section {
  margin-bottom: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
}

.actions-section {
  margin-bottom: 30px;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.test-section {
  margin-bottom: 30px;
}

.test-buttons {
  display: flex;
  gap: 15px;
}

.log-section {
  margin-bottom: 20px;
}

.log-content {
  background: #f9f9f9;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 15px;
  max-height: 300px;
  overflow-y: auto;
}

.log-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.log-time {
  color: #909399;
  margin-right: 10px;
  min-width: 80px;
}

.log-message {
  flex: 1;
}

.log-message.success {
  color: #67c23a;
}

.log-message.error {
  color: #f56c6c;
}

.log-message.info {
  color: #606266;
}
</style>