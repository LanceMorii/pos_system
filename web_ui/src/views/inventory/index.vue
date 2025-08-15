<template>
  <div class="inventory-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>库存管理</span>
          <div>
            <el-button type="primary" @click="handleStockIn">
              <el-icon><Plus /></el-icon>
              入库
            </el-button>
            <el-button type="warning" @click="handleStockOut">
              <el-icon><Plus /></el-icon>
              出库
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :model="searchForm" inline>
          <el-form-item label="商品名称">
            <el-input v-model="searchForm.name" placeholder="请输入商品名称" clearable />
          </el-form-item>
          <el-form-item label="库存状态">
            <el-select v-model="searchForm.stockStatus" placeholder="请选择库存状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="正常" value="normal" />
              <el-option label="预警" value="warning" />
              <el-option label="缺货" value="shortage" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 库存表格 -->
      <el-table :data="inventoryList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="商品ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="barcode" label="条码" width="120" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="currentStock" label="当前库存" width="100">
          <template #default="scope">
            <span :class="getStockClass(scope.row)">
              {{ scope.row.currentStock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="最低库存" width="100" />
        <el-table-column prop="maxStock" label="最高库存" width="100" />
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column label="库存状态" width="100">
          <template #default="scope">
            <el-tag :type="getStockTagType(scope.row)">
              {{ getStockStatus(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastUpdateTime" label="最后更新" width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleStockInItem(scope.row)">入库</el-button>
            <el-button type="warning" size="small" @click="handleStockOutItem(scope.row)">出库</el-button>
            <el-button type="info" size="small" @click="handleStockRecord(scope.row)">记录</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 入库对话框 -->
    <el-dialog
      title="商品入库"
      v-model="stockInDialogVisible"
      width="500px"
      @close="resetStockInForm"
    >
      <el-form ref="stockInFormRef" :model="stockInForm" :rules="stockInRules" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="stockInForm.productName" disabled />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input v-model="stockInForm.currentStock" disabled />
        </el-form-item>
        <el-form-item label="入库数量" prop="quantity">
          <el-input-number v-model="stockInForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="入库类型" prop="type">
          <el-select v-model="stockInForm.type" placeholder="请选择入库类型" style="width: 100%">
            <el-option label="采购入库" value="purchase" />
            <el-option label="退货入库" value="return" />
            <el-option label="调拨入库" value="transfer" />
            <el-option label="盘点调整" value="adjustment" />
          </el-select>
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="stockInForm.supplier" placeholder="请输入供应商" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="stockInForm.unitPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockInForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="stockInDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStockIn">确认入库</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 库存记录对话框 -->
    <el-dialog
      :title="`${currentProductName} - 库存记录`"
      v-model="stockRecordDialogVisible"
      width="900px"
      @close="resetStockRecord"
    >
      <!-- 记录筛选 -->
      <div class="record-filters">
        <el-form :model="recordSearchForm" inline>
          <el-form-item label="记录类型">
            <el-select v-model="recordSearchForm.type" placeholder="全部类型" clearable style="width: 120px">
              <el-option label="全部" value="" />
              <el-option label="入库" value="IN" />
              <el-option label="出库" value="OUT" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="recordSearchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchStockRecords">查询</el-button>
            <el-button @click="resetRecordSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 记录表格 -->
      <el-table :data="stockRecordList" border style="width: 100%" v-loading="recordLoading" max-height="400">
        <el-table-column prop="recordNo" label="单据号" width="140" />
        <el-table-column prop="createdAt" label="操作时间" width="150">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'IN' ? 'success' : 'warning'">
              {{ scope.row.type === 'IN' ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subType" label="具体类型" width="100" />
        <el-table-column prop="quantity" label="数量" width="80">
          <template #default="scope">
            <span :class="scope.row.type === 'IN' ? 'quantity-in' : 'quantity-out'">
              {{ scope.row.type === 'IN' ? '+' : '-' }}{{ scope.row.quantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="80">
          <template #default="scope">
            ¥{{ scope.row.unitPrice ? scope.row.unitPrice.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="scope">
            ¥{{ scope.row.totalAmount ? scope.row.totalAmount.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="beforeStock" label="变动前库存" width="100">
          <template #default="scope">
            {{ scope.row.beforeStock !== null && scope.row.beforeStock !== undefined ? scope.row.beforeStock : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="afterStock" label="变动后库存" width="100">
          <template #default="scope">
            {{ scope.row.afterStock !== null && scope.row.afterStock !== undefined ? scope.row.afterStock : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作员" width="80" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      </el-table>
      
      <!-- 记录分页 -->
      <div class="record-pagination">
        <el-pagination
          v-model:current-page="recordPagination.currentPage"
          v-model:page-size="recordPagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="recordPagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleRecordSizeChange"
          @current-change="handleRecordCurrentChange"
        />
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="stockRecordDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 出库对话框 -->
    <el-dialog
      title="商品出库"
      v-model="stockOutDialogVisible"
      width="500px"
      @close="resetStockOutForm"
    >
      <el-form ref="stockOutFormRef" :model="stockOutForm" :rules="stockOutRules" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="stockOutForm.productName" disabled />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input v-model="stockOutForm.currentStock" disabled />
        </el-form-item>
        <el-form-item label="出库数量" prop="quantity">
          <el-input-number v-model="stockOutForm.quantity" :min="1" :max="stockOutForm.currentStock" style="width: 100%" />
        </el-form-item>
        <el-form-item label="出库类型" prop="type">
          <el-select v-model="stockOutForm.type" placeholder="请选择出库类型" style="width: 100%">
            <el-option label="销售出库" value="sale" />
            <el-option label="损耗出库" value="loss" />
            <el-option label="调拨出库" value="transfer" />
            <el-option label="盘点调整" value="adjustment" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="stockOutForm.unitPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="出库原因" prop="reason">
          <el-input v-model="stockOutForm.reason" type="textarea" :rows="3" placeholder="请输入出库原因" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="stockOutDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStockOut">确认出库</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  getInventoryList, 
  stockIn, 
  stockOut,
  getStockRecords
} from '@/api/inventory'

// 搜索表单
const searchForm = reactive({
  name: '',
  stockStatus: ''
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 库存列表
const inventoryList = ref([
  {
    id: '1',
    name: '有机鲜牛奶 1L',
    barcode: '6901234567890',
    category: '生鲜果蔬',
    currentStock: 50,
    minStock: 10,
    maxStock: 200,
    unit: '瓶',
    lastUpdateTime: '2024-01-15 10:30:00'
  },
  {
    id: '2',
    name: '面包新语吐司',
    barcode: '6901234567891',
    category: '休闲零食',
    currentStock: 5,
    minStock: 10,
    maxStock: 100,
    unit: '个',
    lastUpdateTime: '2024-01-15 09:15:00'
  },
  {
    id: '3',
    name: '特级橙子 500g',
    barcode: '6901234567892',
    category: '生鲜果蔬',
    currentStock: 0,
    minStock: 20,
    maxStock: 150,
    unit: '袋',
    lastUpdateTime: '2024-01-14 16:45:00'
  }
])

// 对话框状态
const stockInDialogVisible = ref(false)
const stockOutDialogVisible = ref(false)
const stockRecordDialogVisible = ref(false)

// 表单引用
const stockInFormRef = ref<FormInstance>()
const stockOutFormRef = ref<FormInstance>()

// 入库表单
const stockInForm = reactive({
  productId: '',
  productName: '',
  currentStock: 0,
  quantity: 1,
  type: 'purchase',
  supplier: '',
  unitPrice: 0,
  remark: ''
})

// 出库表单
const stockOutForm = reactive({
  productId: '',
  productName: '',
  currentStock: 0,
  quantity: 1,
  type: 'sale',
  unitPrice: 0,
  reason: ''
})

// 表单验证规则
const stockInRules = reactive<FormRules>({
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }],
  type: [{ required: true, message: '请选择入库类型', trigger: 'change' }]
})

const stockOutRules = reactive<FormRules>({
  quantity: [{ required: true, message: '请输入出库数量', trigger: 'blur' }],
  type: [{ required: true, message: '请选择出库类型', trigger: 'change' }],
  reason: [{ required: true, message: '请输入出库原因', trigger: 'blur' }]
})

// 加载状态
const loading = ref(false)

// 库存记录相关
const currentProductName = ref('')
const currentProductId = ref('')
const stockRecordList = ref([])
const recordLoading = ref(false)
const recordSearchForm = reactive({
  type: '',
  dateRange: []
})
const recordPagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 获取库存状态样式
const getStockClass = (row: any) => {
  if (row.currentStock === 0) return 'stock-danger'
  if (row.currentStock <= row.minStock) return 'stock-warning'
  return ''
}

// 获取库存状态标签类型
const getStockTagType = (row: any) => {
  if (row.currentStock === 0) return 'danger'
  if (row.currentStock <= row.minStock) return 'warning'
  return 'success'
}

// 获取库存状态文本
const getStockStatus = (row: any) => {
  if (row.currentStock === 0) return '缺货'
  if (row.currentStock <= row.minStock) return '预警'
  return '正常'
}

// 加载库存列表
const loadInventoryList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      limit: pagination.pageSize,
      name: searchForm.name || undefined,
      stockStatus: searchForm.stockStatus || undefined
    }
    
    const response = await getInventoryList(params)
    if (response.code === 200) {
      inventoryList.value = response.data.items
      pagination.total = response.data.total
    }
  } catch (error) {
    console.error('获取库存列表失败:', error)
    // 使用模拟数据
    pagination.total = inventoryList.value.length
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadInventoryList()
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  searchForm.stockStatus = ''
  handleSearch()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  loadInventoryList()
}

const handleCurrentChange = (val: number) => {
  pagination.currentPage = val
  loadInventoryList()
}

// 入库操作
const handleStockIn = () => {
  ElMessage.info('请选择要入库的商品')
}

const handleStockInItem = (row: any) => {
  resetStockInForm()
  stockInForm.productId = row.id
  stockInForm.productName = row.name
  stockInForm.currentStock = row.currentStock
  stockInDialogVisible.value = true
}

// 出库操作
const handleStockOut = () => {
  ElMessage.info('请选择要出库的商品')
}

const handleStockOutItem = (row: any) => {
  if (row.currentStock <= 0) {
    ElMessage.warning('该商品库存不足，无法出库')
    return
  }
  
  resetStockOutForm()
  stockOutForm.productId = row.id
  stockOutForm.productName = row.name
  stockOutForm.currentStock = row.currentStock
  stockOutDialogVisible.value = true
}

// 查看库存记录
const handleStockRecord = (row: any) => {
  currentProductName.value = row.name
  currentProductId.value = row.id
  stockRecordDialogVisible.value = true
  resetStockRecord()
  loadStockRecords()
}

// 重置入库表单
const resetStockInForm = () => {
  if (stockInFormRef.value) {
    stockInFormRef.value.resetFields()
  }
  
  Object.assign(stockInForm, {
    productId: '',
    productName: '',
    currentStock: 0,
    quantity: 1,
    type: 'purchase',
    supplier: '',
    unitPrice: 0,
    remark: ''
  })
}

// 重置出库表单
const resetStockOutForm = () => {
  if (stockOutFormRef.value) {
    stockOutFormRef.value.resetFields()
  }
  
  Object.assign(stockOutForm, {
    productId: '',
    productName: '',
    currentStock: 0,
    quantity: 1,
    type: 'sale',
    unitPrice: 0,
    reason: ''
  })
}

// 提交入库
const submitStockIn = async () => {
  if (!stockInFormRef.value) return
  
  await stockInFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const data = {
          productId: parseInt(stockInForm.productId),
          quantity: parseInt(stockInForm.quantity),
          type: stockInForm.type.toUpperCase(),
          unitPrice: parseFloat(stockInForm.unitPrice),
          remark: stockInForm.remark || ''
        }
        
        if (stockInForm.supplier) {
          data.supplierId = null
        }
        
        console.log('发送入库数据:', data)
        const response = await stockIn(data)
        console.log('入库响应:', response)
        ElMessage.success('入库成功')
        stockInDialogVisible.value = false
        resetStockInForm()
        loadInventoryList()
      } catch (error) {
        console.error('入库失败:', error)
        if (error.response && error.response.data && error.response.data.message) {
          ElMessage.error(`入库失败: ${error.response.data.message}`)
        } else if (error.message) {
          ElMessage.error(`入库失败: ${error.message}`)
        } else {
          ElMessage.error('入库失败，请重试')
        }
      }
    }
  })
}

// 提交出库
const submitStockOut = async () => {
  if (!stockOutFormRef.value) return
  
  await stockOutFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const data = {
          productId: parseInt(stockOutForm.productId),
          quantity: parseInt(stockOutForm.quantity),
          type: stockOutForm.type.toUpperCase(),
          unitPrice: parseFloat(stockOutForm.unitPrice) || 0.00,
          reason: stockOutForm.reason || '出库操作',
          remark: stockOutForm.reason || ''
        }
        
        console.log('发送出库数据:', data)
        const response = await stockOut(data)
        console.log('出库响应:', response)
        ElMessage.success('出库成功')
        stockOutDialogVisible.value = false
        resetStockOutForm()
        loadInventoryList()
      } catch (error) {
        console.error('出库失败:', error)
        if (error.response && error.response.data && error.response.data.message) {
          ElMessage.error(`出库失败: ${error.response.data.message}`)
        } else if (error.message) {
          ElMessage.error(`出库失败: ${error.message}`)
        } else {
          ElMessage.error('出库失败，请重试')
        }
      }
    }
  })
}

// 库存记录相关方法
const loadStockRecords = async () => {
  if (!currentProductId.value) return
  
  recordLoading.value = true
  try {
    // 构建API请求参数
    const params = {
      page: recordPagination.currentPage,
      limit: recordPagination.pageSize,
      type: recordSearchForm.type || undefined
    }
    
    // 处理时间范围参数
    if (recordSearchForm.dateRange && recordSearchForm.dateRange.length === 2) {
      params.startTime = recordSearchForm.dateRange[0] + ' 00:00:00'
      params.endTime = recordSearchForm.dateRange[1] + ' 23:59:59'
    }
    
    console.log('获取库存记录参数:', { productId: currentProductId.value, params })
    
    // 调用后端API
    const response = await getStockRecords(parseInt(currentProductId.value), params)
    
    if (response.code === 200) {
      stockRecordList.value = response.data.items || []
      recordPagination.total = response.data.total || 0
      
      console.log('库存记录响应:', response.data)
    } else {
      ElMessage.error(response.message || '获取库存记录失败')
    }
    
  } catch (error) {
    console.error('获取库存记录失败:', error)
    ElMessage.error('获取库存记录失败')
    
    // 如果API调用失败，显示模拟数据作为备用
    const mockRecords = [
      {
        id: 1,
        recordNo: 'IN20250127001',
        createdAt: '2025-01-27 10:30:00',
        type: 'IN',
        subType: '采购入库',
        quantity: 100,
        unitPrice: 12.50,
        totalAmount: 1250.00,
        beforeStock: 50,
        afterStock: 150,
        operatorName: '张三',
        remark: '新批次采购入库'
      },
      {
        id: 2,
        recordNo: 'OUT20250127001',
        createdAt: '2025-01-27 14:20:00',
        type: 'OUT',
        subType: '销售出库',
        quantity: 20,
        unitPrice: 0.00,
        totalAmount: 0.00,
        beforeStock: 150,
        afterStock: 130,
        operatorName: '李四',
        remark: '销售出库'
      }
    ]
    
    stockRecordList.value = mockRecords
    recordPagination.total = mockRecords.length
  } finally {
    recordLoading.value = false
  }
}

const resetStockRecord = () => {
  recordSearchForm.type = ''
  recordSearchForm.dateRange = []
  recordPagination.currentPage = 1
  recordPagination.pageSize = 10
  recordPagination.total = 0
  stockRecordList.value = []
}

const searchStockRecords = () => {
  recordPagination.currentPage = 1
  loadStockRecords()
}

const resetRecordSearch = () => {
  recordSearchForm.type = ''
  recordSearchForm.dateRange = []
  recordPagination.currentPage = 1
  loadStockRecords()
}

const handleRecordSizeChange = (val: number) => {
  recordPagination.pageSize = val
  recordPagination.currentPage = 1
  loadStockRecords()
}

const handleRecordCurrentChange = (val: number) => {
  recordPagination.currentPage = val
  loadStockRecords()
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 页面加载时获取数据
onMounted(() => {
  loadInventoryList()
})
</script>

<style scoped>
.inventory-container {
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.pagination-section {
  margin-top: 20px;
  text-align: right;
}

.stock-warning {
  color: #E6A23C;
  font-weight: bold;
}

.stock-danger {
  color: #F56C6C;
  font-weight: bold;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 库存记录样式 */
.record-filters {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.record-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.quantity-in {
  color: #67C23A;
  font-weight: bold;
}

.quantity-out {
  color: #F56C6C;
  font-weight: bold;
}
</style>