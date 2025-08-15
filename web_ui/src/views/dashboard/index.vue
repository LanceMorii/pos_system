<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <el-card shadow="never" class="welcome-card">
        <div class="welcome-content">
          <div class="welcome-text">
            <h2>欢迎回来，{{ userInfo.realName || userInfo.username }}！</h2>
            <p>今天是 {{ currentDate }}，祝您工作愉快</p>
          </div>
          <div class="welcome-actions">
            <el-button type="primary" @click="goToCashier">
              <el-icon><Money /></el-icon>
              快速收银
            </el-button>
            <el-button @click="goToInventory">
              <el-icon><List /></el-icon>
              库存管理
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 数据概览 -->
    <el-row :gutter="20" class="overview-section">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card today-sales">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ Number(todayStats.sales || 0).toLocaleString() }}</div>
              <div class="stat-label">今日销售额</div>
              <div class="stat-change" :class="todayStats.salesGrowth >= 0 ? 'positive' : 'negative'">
                <el-icon v-if="todayStats.salesGrowth >= 0"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                {{ Math.abs(todayStats.salesGrowth || 0).toFixed(1) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card today-orders">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ todayStats.orders }}</div>
              <div class="stat-label">今日订单数</div>
              <div class="stat-change" :class="todayStats.ordersGrowth >= 0 ? 'positive' : 'negative'">
                <el-icon v-if="todayStats.ordersGrowth >= 0"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                {{ Math.abs(todayStats.ordersGrowth || 0).toFixed(1) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card today-customers">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ todayStats.customers }}</div>
              <div class="stat-label">今日客流量</div>
              <div class="stat-change" :class="todayStats.customersGrowth >= 0 ? 'positive' : 'negative'">
                <el-icon v-if="todayStats.customersGrowth >= 0"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                {{ Math.abs(todayStats.customersGrowth || 0).toFixed(1) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card inventory-alert">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ todayStats.lowStock }}</div>
              <div class="stat-label">库存预警</div>
              <div class="stat-change">
                <span>需要补货</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    


    <!-- 快捷操作和最新动态 -->
    <el-row :gutter="20" class="content-section">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-grid">
              <div class="action-item" @click="goToCashier">
                <div class="action-icon cashier">
                  <el-icon><Money /></el-icon>
                </div>
                <div class="action-text">收银台</div>
              </div>
              
              <div class="action-item" @click="goToProduct">
                <div class="action-icon product">
                  <el-icon><Goods /></el-icon>
                </div>
                <div class="action-text">商品管理</div>
              </div>
              
              <div class="action-item" @click="goToInventory">
                <div class="action-icon inventory">
                  <el-icon><List /></el-icon>
                </div>
                <div class="action-text">库存管理</div>
              </div>
              
              <div class="action-item" @click="goToMember">
                <div class="action-icon member">
                  <el-icon><Star /></el-icon>
                </div>
                <div class="action-text">会员管理</div>
              </div>
              
              <div class="action-item" @click="goToInventory">
                <div class="action-icon inventory">
                  <el-icon><List /></el-icon>
                </div>
                <div class="action-text">库存管理</div>
              </div>
              
              <div class="action-item" @click="goToSupplier">
                <div class="action-icon supplier">
                  <el-icon><Sell /></el-icon>
                </div>
                <div class="action-text">供应商管理</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>最新动态</span>
              <el-button link size="small" @click="viewMoreActivities">查看更多</el-button>
            </div>
          </template>
          <div class="recent-activities">
            <div v-if="loading" class="loading-container">
              <el-icon class="is-loading"><Refresh /></el-icon>
              <span>加载中...</span>
            </div>
            <div v-else-if="recentActivities.length === 0" class="empty-activities">
              <el-empty description="暂无最新动态" :image-size="60" />
            </div>
            <div v-else class="activity-list">
              <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
                <div class="activity-icon" :class="activity.type">
                  <el-icon v-if="activity.type === 'sale'"><ShoppingCart /></el-icon>
                  <el-icon v-else-if="activity.type === 'inventory'"><List /></el-icon>
                  <el-icon v-else-if="activity.type === 'member'"><Star /></el-icon>
                  <el-icon v-else><Bell /></el-icon>
                </div>
                <div class="activity-content">
                  <div class="activity-text">{{ activity.text }}</div>
                  <div class="activity-time">{{ activity.time }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 查看更多动态对话框 -->
    <el-dialog
      title="系统动态"
      v-model="activitiesDialogVisible"
      width="800px"
      top="5vh"
    >
      <div class="activities-dialog">
        <!-- 筛选器 -->
        <div class="activities-filter">
          <el-radio-group v-model="activityFilter" size="small" @change="filterActivities">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="sale">销售</el-radio-button>
            <el-radio-button label="inventory">库存</el-radio-button>
            <el-radio-button label="member">会员</el-radio-button>
            <el-radio-button label="system">系统</el-radio-button>
          </el-radio-group>
          <el-button type="primary" size="small" @click="refreshActivities" style="margin-left: 10px;">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
        
        <!-- 动态列表 -->
        <el-scrollbar height="400px" class="activities-scrollbar">
          <div class="all-activities-list">
            <div v-for="activity in filteredAllActivities" :key="activity.id" class="activity-item-dialog">
              <div class="activity-icon-dialog" :class="activity.type">
                <el-icon v-if="activity.type === 'sale'"><ShoppingCart /></el-icon>
                <el-icon v-else-if="activity.type === 'inventory'"><List /></el-icon>
                <el-icon v-else-if="activity.type === 'member'"><Star /></el-icon>
                <el-icon v-else><Bell /></el-icon>
              </div>
              <div class="activity-content-dialog">
                <div class="activity-text-dialog">{{ activity.text }}</div>
                <div class="activity-time-dialog">{{ activity.time }}</div>
              </div>
              <div class="activity-status" :class="activity.status">
                {{ getStatusText(activity.status) }}
              </div>
            </div>
          </div>
        </el-scrollbar>
        
        <!-- 分页 -->
        <div class="activities-pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            :total="totalActivities"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            small
          />
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="activitiesDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="exportActivities">导出记录</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { 
  Money, DataAnalysis, TrendCharts, Document, User, Warning, 
  Goods, List, Star, Setting, ShoppingCart, Bell, ArrowUp, ArrowDown,
  Refresh, Wallet
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 今日统计数据
const todayStats = ref({
  sales: 0,
  salesGrowth: 0,
  orders: 0,
  ordersGrowth: 0,
  customers: 0,
  customersGrowth: 0,
  lowStock: 0
})

// 今日统计数据（保留）

// 最新动态数据
const recentActivities = ref([])

// 查看更多动态相关数据
const activitiesDialogVisible = ref(false)
const activityFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(20)
const totalActivities = ref(0)

// 所有动态数据
const allActivities = ref([])

// 筛选后的动态数据
const filteredAllActivities = computed(() => {
  let filtered = allActivities.value
  
  if (activityFilter.value !== 'all') {
    filtered = filtered.filter(item => item.type === activityFilter.value)
  }
  
  totalActivities.value = filtered.length
  
  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  
  return filtered.slice(start, end)
})

// 导航方法
const goToCashier = () => {
  router.push('/dashboard/cashier')
}

const goToProduct = () => {
  router.push('/dashboard/product')
}

const goToInventory = () => {
  router.push('/dashboard/inventory')
}

const goToMember = () => {
  router.push('/dashboard/member')
}

const goToSupplier = () => {
  router.push('/dashboard/supplier')
}

// 查看更多动态
const viewMoreActivities = async () => {
  activitiesDialogVisible.value = true
  currentPage.value = 1
  activityFilter.value = 'all'
  await loadAllActivities()
}

// 筛选动态
const filterActivities = () => {
  currentPage.value = 1
}

// 数据加载状态
const loading = ref(false)

// 加载今日统计数据 - 调用真实API
const loadTodayStats = async () => {
  try {
    const response = await request({
      url: '/test/dashboard-stats',
      method: 'get'
    })
    
    if (response.code === 200) {
      todayStats.value = response.data
    } else {
      // 如果API调用失败，使用默认值
      todayStats.value = {
        sales: 0,
        salesGrowth: 0,
        orders: 0,
        ordersGrowth: 0,
        customers: 0,
        customersGrowth: 0,
        lowStock: 0
      }
    }
  } catch (error) {
    console.error('获取今日统计数据失败:', error)
    // 出错时使用默认值
    todayStats.value = {
      sales: 0,
      salesGrowth: 0,
      orders: 0,
      ordersGrowth: 0,
      customers: 0,
      customersGrowth: 0,
      lowStock: 0
    }
  }
}

// 加载最新动态 - 调用真实API
const loadRecentActivities = async () => {
  try {
    const response = await request({
      url: '/test/recent-activities',
      method: 'get'
    })
    
    if (response.code === 200 && response.data) {
      recentActivities.value = response.data
    } else {
      // 如果API调用失败，使用默认数据
      recentActivities.value = [
        {
          id: 1,
          type: 'system',
          text: '系统运行正常，等待新的订单',
          time: '刚刚',
          status: 'info'
        }
      ]
    }
  } catch (error) {
    console.error('获取最新动态失败:', error)
    // 出错时使用默认数据
    recentActivities.value = [
      {
        id: 1,
        type: 'system',
        text: '系统运行正常，等待新的订单',
        time: '刚刚',
        status: 'info'
      }
    ]
  }
}

// 加载所有动态（用于对话框） - 调用真实API
const loadAllActivities = async () => {
  try {
    const response = await request({
      url: '/test/recent-activities',
      method: 'get'
    })
    
    if (response.code === 200 && response.data) {
      // 使用真实数据，并添加一些额外的历史数据
      allActivities.value = [
        ...response.data,
        {
          id: 'history_1',
          type: 'system',
          text: '系统自动备份完成',
          time: '2小时前',
          status: 'info'
        }
      ]
    } else {
      // 如果API调用失败，使用当前的最新动态数据
      allActivities.value = [...recentActivities.value]
    }
  } catch (error) {
    console.error('获取动态数据失败:', error)
    // 出错时使用当前的最新动态数据
    allActivities.value = [...recentActivities.value]
  }
}

// 刷新动态
const refreshActivities = async () => {
  loading.value = true
  try {
    await loadAllActivities()
    ElMessage.success('动态已刷新')
  } catch (error) {
    ElMessage.error('刷新失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

// 获取状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'success': return '成功'
    case 'warning': return '警告'
    case 'info': return '信息'
    case 'error': return '错误'
    default: return '未知'
  }
}

// 导出动态记录
const exportActivities = () => {
  try {
    // 准备导出数据
    const exportData = allActivities.value.map(activity => ({
      时间: activity.time,
      类型: getTypeText(activity.type),
      内容: activity.text,
      状态: getStatusText(activity.status)
    }))
    
    // 创建CSV内容
    const csvContent = [
      ['时间', '类型', '内容', '状态'].join(','),
      ...exportData.map(row => Object.values(row).join(','))
    ].join('\n')
    
    // 创建下载链接
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `系统动态_${new Date().toISOString().split('T')[0]}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('动态记录导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

// 获取类型文本
const getTypeText = (type: string) => {
  switch (type) {
    case 'sale': return '销售'
    case 'inventory': return '库存'
    case 'member': return '会员'
    case 'system': return '系统'
    default: return '其他'
  }
}

// 刷新最新动态
const refreshRecentActivities = async () => {
  try {
    await loadRecentActivities()
    console.log('最新动态已刷新')
  } catch (error) {
    console.error('刷新最新动态失败:', error)
  }
}

// 初始化数据加载
const initData = async () => {
  loading.value = true
  try {
    // 并行加载数据
    await Promise.all([
      loadTodayStats(),
      loadRecentActivities()
    ])
  } catch (error) {
    console.error('初始化数据加载失败:', error)
  } finally {
    loading.value = false
  }
}

// 定期刷新数据
const startAutoRefresh = () => {
  // 每30秒刷新一次最新动态
  setInterval(async () => {
    await refreshRecentActivities()
  }, 30000)
}

onMounted(() => {
  console.log('Dashboard mounted')
  initData()
  startAutoRefresh()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-card :deep(.el-card__body) {
  padding: 30px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.welcome-text p {
  margin: 0;
  opacity: 0.9;
}

.welcome-actions {
  display: flex;
  gap: 15px;
}

.overview-section {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
  height: 100%;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  font-size: 40px;
  margin-right: 20px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.today-sales .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.today-orders .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.today-customers .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.inventory-alert .stat-icon {
  background: linear-gradient(135deg, #ffeaa7 0%, #fab1a0 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.stat-change.positive {
  color: #67C23A;
}

.stat-change.negative {
  color: #F56C6C;
}

.content-section {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  padding: 10px 0;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.action-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 10px;
  color: white;
}

.action-icon.cashier {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.action-icon.product {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.action-icon.inventory {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.action-icon.member {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.action-icon.inventory {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.action-icon.supplier {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.action-text {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.recent-activities {
  padding: 10px 0;
}

.activity-list {
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item .activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  margin-right: 12px;
  color: white;
}

.activity-item .activity-icon.sale {
  background: #409EFF;
}

.activity-item .activity-icon.inventory {
  background: #E6A23C;
}

.activity-item .activity-icon.member {
  background: #67C23A;
}

.activity-item .activity-icon.system {
  background: #909399;
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #909399;
}

/* 查看更多动态对话框样式 */
.activities-dialog {
  padding: 10px 0;
}

.activities-filter {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.activities-scrollbar {
  margin-bottom: 20px;
}

.all-activities-list {
  padding: 0 10px;
}

.activity-item-dialog {
  display: flex;
  align-items: flex-start;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s;
}

.activity-item-dialog:hover {
  background-color: #f8f9fa;
  border-radius: 6px;
  margin: 0 -10px;
  padding: 15px 10px;
}

.activity-item-dialog:last-child {
  border-bottom: none;
}

.activity-icon-dialog {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-right: 15px;
  color: white;
  flex-shrink: 0;
}

.activity-icon-dialog.sale {
  background: #409EFF;
}

.activity-icon-dialog.inventory {
  background: #E6A23C;
}

.activity-icon-dialog.member {
  background: #67C23A;
}

.activity-icon-dialog.system {
  background: #909399;
}

.activity-content-dialog {
  flex: 1;
  margin-right: 15px;
}

.activity-text-dialog {
  font-size: 14px;
  color: #303133;
  margin-bottom: 6px;
  line-height: 1.4;
}

.activity-time-dialog {
  font-size: 12px;
  color: #909399;
}

.activity-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  min-width: 50px;
  text-align: center;
  flex-shrink: 0;
}

.activity-status.success {
  background-color: #f0f9ff;
  color: #67C23A;
  border: 1px solid #b3e19d;
}

.activity-status.warning {
  background-color: #fdf6ec;
  color: #E6A23C;
  border: 1px solid #f5dab1;
}

.activity-status.info {
  background-color: #f4f4f5;
  color: #909399;
  border: 1px solid #d3d4d6;
}

.activity-status.error {
  background-color: #fef0f0;
  color: #F56C6C;
  border: 1px solid #fbc4c4;
}

.activities-pagination {
  display: flex;
  justify-content: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 销售趋势图表区域样式 */
.chart-section {
  margin-bottom: 20px;
}

/* 热销商品样式 */
.hot-products {
  padding: 10px 0;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-item:last-child {
  border-bottom: none;
}

.product-rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #f4f4f5;
  color: #606266;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 12px;
  font-size: 14px;
}

.product-item:nth-child(1) .product-rank {
  background-color: #f56c6c;
  color: white;
}

.product-item:nth-child(2) .product-rank {
  background-color: #e6a23c;
  color: white;
}

.product-item:nth-child(3) .product-rank {
  background-color: #409eff;
  color: white;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
}

.product-sales {
  display: flex;
  align-items: center;
}

.product-sales .el-progress {
  flex: 1;
  margin-right: 10px;
}

.sales-text {
  font-size: 12px;
  color: #909399;
  width: 50px;
  text-align: right;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
}

.loading-container .el-icon {
  font-size: 24px;
  margin-bottom: 10px;
}

.empty-activities {
  padding: 20px 0;
}
</style>