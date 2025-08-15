<template>
  <div class="sales-container">
    <div class="header">
      <h2>销售管理</h2>
      <div class="header-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="loadOrders"
        />
        <el-button @click="loadOrders" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ totalSales.toFixed(2) }}</div>
          <div class="stat-label">总销售额</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ totalOrders }}</div>
          <div class="stat-label">订单数量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ totalItems }}</div>
          <div class="stat-label">商品数量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ avgOrderAmount.toFixed(2) }}</div>
          <div class="stat-label">平均订单金额</div>
        </div>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-section">
      <div class="section-header">
        <h3>订单列表</h3>
        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索订单号或客户姓名"
            @keyup.enter="loadOrders"
            clearable
            style="width: 250px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select
            v-model="statusFilter"
            placeholder="订单状态"
            @change="loadOrders"
            clearable
            style="width: 120px; margin-left: 10px"
          >
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="退款中" value="REFUNDING" />
          </el-select>
        </div>
      </div>

      <el-table
        :data="orders"
        v-loading="loading"
        stripe
        style="width: 100%"
        @row-click="viewOrderDetail"
      >
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="cashierName" label="收银员" width="120" />
        <el-table-column prop="totalAmount" label="原价" width="100">
          <template #default="scope">
            ¥{{ scope.row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="discountAmount" label="优惠" width="100">
          <template #default="scope">
            ¥{{ scope.row.discountAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="finalAmount" label="实付金额" width="100">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold">
              ¥{{ scope.row.finalAmount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100">
          <template #default="scope">
            <el-tag :type="getPaymentMethodType(scope.row.paymentMethod)">
              {{ getPaymentMethodText(scope.row.paymentMethod) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click.stop="viewOrderDetail(scope.row)"
            >
              查看详情
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click.stop="cancelOrder(scope.row)"
              :disabled="scope.row.status !== 'COMPLETED'"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>


    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="selectedOrder" class="order-detail">
        <!-- 订单基本信息 -->
        <div class="detail-section">
          <h4>订单信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">订单号:</span>
              <span class="value">{{ selectedOrder.orderNo }}</span>
            </div>
            <div class="detail-item">
              <span class="label">客户姓名:</span>
              <span class="value">{{ selectedOrder.customerName || '未填写' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">收银员:</span>
              <span class="value">{{ selectedOrder.cashierName || '未知' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">订单状态:</span>
              <el-tag :type="getStatusType(selectedOrder.status)">
                {{ getStatusText(selectedOrder.status) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="label">支付方式:</span>
              <el-tag :type="getPaymentMethodType(selectedOrder.paymentMethod)">
                {{ getPaymentMethodText(selectedOrder.paymentMethod) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="label">创建时间:</span>
              <span class="value">{{ formatDateTime(selectedOrder.createdAt) }}</span>
            </div>
          </div>
        </div>

        <!-- 商品明细 -->
        <div class="detail-section">
          <h4>商品明细</h4>
          <el-table :data="selectedOrder.items" stripe>
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="unitPrice" label="单价" width="100">
              <template #default="scope">
                ¥{{ scope.row.unitPrice.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="subtotal" label="小计" width="100">
              <template #default="scope">
                ¥{{ scope.row.subtotal.toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 金额汇总 -->
        <div class="detail-section">
          <h4>金额汇总</h4>
          <div class="amount-summary">
            <div class="summary-row">
              <span>商品总价:</span>
              <span>¥{{ selectedOrder.totalAmount.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>优惠金额:</span>
              <span>¥{{ selectedOrder.discountAmount.toFixed(2) }}</span>
            </div>
            <div class="summary-row final">
              <span>实付金额:</span>
              <span>¥{{ selectedOrder.finalAmount.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Money, Document, Goods, TrendCharts } from '@element-plus/icons-vue'
import { getSalesOrders, cancelSalesOrder } from '@/api/sales'

// 基础数据
const loading = ref(false)
const orders = ref([])
const searchKeyword = ref('')
const statusFilter = ref('')
const dateRange = ref([])

// 订单详情
const detailDialogVisible = ref(false)
const selectedOrder = ref(null)

// 统计数据
const totalSales = computed(() => {
  return orders.value.reduce((sum, order) => sum + order.finalAmount, 0)
})

const totalOrders = computed(() => orders.value.length)

const totalItems = computed(() => {
  return orders.value.reduce((sum, order) => sum + (order.itemCount || 0), 0)
})

const avgOrderAmount = computed(() => {
  return totalOrders.value > 0 ? totalSales.value / totalOrders.value : 0
})

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchKeyword.value.trim() || undefined,
      status: statusFilter.value || undefined,
      startDate: dateRange.value?.[0] || undefined,
      endDate: dateRange.value?.[1] || undefined
    }
    
    const res = await getSalesOrders(params)
    if (res.code === 200 && res.data) {
      orders.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('连接服务器失败')
  } finally {
    loading.value = false
  }
}

// 查看订单详情
const viewOrderDetail = (order) => {
  selectedOrder.value = order
  detailDialogVisible.value = true
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单 ${order.orderNo} 吗？`,
      '取消订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await cancelSalesOrder(order.id)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    } else {
      ElMessage.error(res.message || '取消订单失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 获取支付方式类型
const getPaymentMethodType = (method) => {
  const types = {
    'CASH': '',
    'WECHAT': 'success',
    'ALIPAY': 'warning',
    'CARD': 'info'
  }
  return types[method] || ''
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const texts = {
    'CASH': '现金',
    'WECHAT': '微信',
    'ALIPAY': '支付宝',
    'CARD': '银行卡'
  }
  return texts[method] || method
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    'COMPLETED': 'success',
    'CANCELLED': 'danger',
    'REFUNDING': 'warning'
  }
  return types[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDING': '退款中'
  }
  return texts[status] || status
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 页面初始化
onMounted(() => {
  // 设置默认日期范围为最近7天
  const endDate = new Date()
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - 7)
  
  dateRange.value = [
    startDate.toISOString().split('T')[0],
    endDate.toISOString().split('T')[0]
  ]
  
  loadOrders()
})
</script>

<style scoped>
.sales-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.header h2 {
  margin: 0;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  background: linear-gradient(135deg, #409eff, #67c23a);
  color: white;
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.orders-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.section-header h3 {
  margin: 0;
  color: #303133;
}

.search-area {
  display: flex;
  align-items: center;
}



.order-detail {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 25px;
}

.detail-section h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 8px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
}

.detail-item .label {
  min-width: 80px;
  color: #606266;
  font-weight: 500;
}

.detail-item .value {
  color: #303133;
}

.amount-summary {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.summary-row.final {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
  border-top: 1px solid #e4e7ed;
  padding-top: 8px;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 15px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .search-area {
    flex-direction: column;
    gap: 10px;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>