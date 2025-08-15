<template>
  <div class="connection-test">
    <div class="container">
      <h2>后端连接测试</h2>
      
      <div class="test-section">
        <h3>连接状态</h3>
        <div class="status-card" :class="connectionStatus.type">
          <div class="status-icon">
            <el-icon v-if="connectionStatus.type === 'success'" size="24"><SuccessFilled /></el-icon>
            <el-icon v-else-if="connectionStatus.type === 'error'" size="24"><CircleCloseFilled /></el-icon>
            <el-icon v-else size="24"><Loading /></el-icon>
          </div>
          <div class="status-text">{{ connectionStatus.message }}</div>
        </div>
      </div>

      <div class="config-section">
        <h3>配置信息</h3>
        <div class="config-grid">
          <div class="config-item">
            <label>前端地址:</label>
            <span>{{ frontendUrl }}</span>
          </div>
          <div class="config-item">
            <label>后端地址:</label>
            <span>{{ backendUrl }}</span>
          </div>
          <div class="config-item">
            <label>API基础路径:</label>
            <span>/api</span>
          </div>
        </div>
      </div>

      <div class="test-actions">
        <h3>测试操作</h3>
        <div class="action-buttons">
          <el-button @click="testDirectConnection" :loading="testing.direct">
            直接连接测试
          </el-button>
          <el-button @click="testHealthAPI" :loading="testing.health">
            健康检查API
          </el-button>
          <el-button @click="testCashierAPI" :loading="testing.cashier">
            收银台API
          </el-button>
          <el-button @click="testDataInitAPI" :loading="testing.dataInit">
            数据初始化API
          </el-button>
        </div>
      </div>

      <div class="manual-test">
        <h3>手动测试</h3>
        <div class="manual-inputs">
          <el-input
            v-model="manualUrl"
            placeholder="输入API地址，如: /api/cashier/health"
            style="width: 300px; margin-right: 10px;"
          />
          <el-select v-model="manualMethod" style="width: 100px; margin-right: 10px;">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
          </el-select>
          <el-button @click="testManualAPI" :loading="testing.manual">
            测试
          </el-button>
        </div>
      </div>

      <div class="solutions">
        <h3>常见解决方案</h3>
        <div class="solution-list">
          <div class="solution-item">
            <h4>1. 检查后端服务</h4>
            <p>确保Spring Boot应用正在运行在端口8080</p>
            <code>mvn spring-boot:run</code>
          </div>
          <div class="solution-item">
            <h4>2. 检查端口</h4>
            <p>确认后端运行在8080端口，前端运行在3000端口</p>
          </div>
          <div class="solution-item">
            <h4>3. 检查防火墙</h4>
            <p>确保8080端口没有被防火墙阻止</p>
          </div>
          <div class="solution-item">
            <h4>4. 直接访问后端</h4>
            <p>在浏览器中访问: <a href="http://localhost:8080/api/cashier/health" target="_blank">http://localhost:8080/api/cashier/health</a></p>
          </div>
        </div>
      </div>

      <div class="log-section">
        <h3>测试日志</h3>
        <div class="log-content">
          <div v-for="(log, index) in logs" :key="index" class="log-item">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-level" :class="log.level">{{ log.level.toUpperCase() }}</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { SuccessFilled, CircleCloseFilled, Loading } from '@element-plus/icons-vue'
import axios from 'axios'

// 响应式数据
const connectionStatus = ref({
  type: 'loading',
  message: '检测中...'
})

const frontendUrl = ref('')
const backendUrl = ref('http://localhost:8080')
const manualUrl = ref('/api/cashier/health')
const manualMethod = ref('GET')

const testing = ref({
  direct: false,
  health: false,
  cashier: false,
  dataInit: false,
  manual: false
})

const logs = ref([])

// 添加日志
const addLog = (message: string, level: string = 'info') => {
  logs.value.unshift({
    time: new Date().toLocaleTimeString(),
    level,
    message
  })
  if (logs.value.length > 50) {
    logs.value = logs.value.slice(0, 50)
  }
}

// 直接连接测试
const testDirectConnection = async () => {
  testing.value.direct = true
  addLog('开始直接连接测试...', 'info')
  
  try {
    const response = await fetch('http://localhost:8080/api/cashier/health', {
      method: 'GET',
      mode: 'cors'
    })
    
    if (response.ok) {
      const data = await response.json()
      addLog('直接连接成功: ' + JSON.stringify(data), 'success')
      connectionStatus.value = {
        type: 'success',
        message: '后端连接正常'
      }
    } else {
      addLog(`直接连接失败: HTTP ${response.status}`, 'error')
      connectionStatus.value = {
        type: 'error',
        message: `连接失败: HTTP ${response.status}`
      }
    }
  } catch (error) {
    addLog('直接连接异常: ' + error.message, 'error')
    connectionStatus.value = {
      type: 'error',
      message: '无法连接到后端服务'
    }
  } finally {
    testing.value.direct = false
  }
}

// 健康检查API测试
const testHealthAPI = async () => {
  testing.value.health = true
  addLog('测试健康检查API...', 'info')
  
  try {
    const response = await axios.get('/api/cashier/health')
    addLog('健康检查成功: ' + JSON.stringify(response), 'success')
    ElMessage.success('健康检查API正常')
  } catch (error) {
    addLog('健康检查失败: ' + error.message, 'error')
    ElMessage.error('健康检查API失败')
  } finally {
    testing.value.health = false
  }
}

// 收银台API测试
const testCashierAPI = async () => {
  testing.value.cashier = true
  addLog('测试收银台API...', 'info')
  
  try {
    const response = await axios.get('/api/cashier/products')
    addLog('收银台API成功: ' + JSON.stringify(response), 'success')
    ElMessage.success('收银台API正常')
  } catch (error) {
    addLog('收银台API失败: ' + error.message, 'error')
    ElMessage.error('收银台API失败')
  } finally {
    testing.value.cashier = false
  }
}

// 数据初始化API测试
const testDataInitAPI = async () => {
  testing.value.dataInit = true
  addLog('测试数据初始化API...', 'info')
  
  try {
    const response = await axios.get('/api/data-init/stats')
    addLog('数据初始化API成功: ' + JSON.stringify(response), 'success')
    ElMessage.success('数据初始化API正常')
  } catch (error) {
    addLog('数据初始化API失败: ' + error.message, 'error')
    ElMessage.error('数据初始化API失败')
  } finally {
    testing.value.dataInit = false
  }
}

// 手动API测试
const testManualAPI = async () => {
  testing.value.manual = true
  addLog(`手动测试: ${manualMethod.value} ${manualUrl.value}`, 'info')
  
  try {
    const response = await axios({
      method: manualMethod.value.toLowerCase(),
      url: manualUrl.value
    })
    addLog('手动测试成功: ' + JSON.stringify(response), 'success')
    ElMessage.success('手动测试成功')
  } catch (error) {
    addLog('手动测试失败: ' + error.message, 'error')
    ElMessage.error('手动测试失败')
  } finally {
    testing.value.manual = false
  }
}

// 页面初始化
onMounted(() => {
  frontendUrl.value = window.location.origin
  addLog('连接测试页面加载完成', 'info')
  
  // 自动执行直接连接测试
  setTimeout(() => {
    testDirectConnection()
  }, 1000)
})
</script>

<style scoped>
.connection-test {
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

.test-section {
  margin-bottom: 30px;
}

.status-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.status-card.success {
  background: #f0f9ff;
  border: 1px solid #67c23a;
  color: #67c23a;
}

.status-card.error {
  background: #fef0f0;
  border: 1px solid #f56c6c;
  color: #f56c6c;
}

.status-card.loading {
  background: #f4f4f5;
  border: 1px solid #909399;
  color: #909399;
}

.status-icon {
  margin-right: 10px;
}

.status-text {
  font-size: 1.1rem;
  font-weight: 500;
}

.config-section {
  margin-bottom: 30px;
}

.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.config-item {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #f9f9f9;
  border-radius: 4px;
}

.config-item label {
  font-weight: 500;
  margin-right: 10px;
  min-width: 100px;
}

.test-actions {
  margin-bottom: 30px;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.manual-test {
  margin-bottom: 30px;
}

.manual-inputs {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.solutions {
  margin-bottom: 30px;
}

.solution-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.solution-item {
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.solution-item h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.solution-item p {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 0.9rem;
}

.solution-item code {
  background: #f4f4f5;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9rem;
}

.solution-item a {
  color: #409eff;
  text-decoration: none;
}

.solution-item a:hover {
  text-decoration: underline;
}

.log-section {
  margin-bottom: 20px;
}

.log-content {
  background: #f9f9f9;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 15px;
  max-height: 400px;
  overflow-y: auto;
  font-family: monospace;
  font-size: 0.85rem;
}

.log-item {
  display: flex;
  margin-bottom: 5px;
  line-height: 1.4;
}

.log-time {
  color: #909399;
  margin-right: 10px;
  min-width: 80px;
}

.log-level {
  margin-right: 10px;
  min-width: 60px;
  font-weight: bold;
}

.log-level.success {
  color: #67c23a;
}

.log-level.error {
  color: #f56c6c;
}

.log-level.info {
  color: #409eff;
}

.log-message {
  flex: 1;
  word-break: break-all;
}
</style>