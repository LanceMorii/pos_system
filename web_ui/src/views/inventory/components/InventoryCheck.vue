<template>
  <el-dialog
    title="库存盘点"
    v-model="visible"
    width="1000px"
    @close="handleClose"
  >
    <div class="inventory-check-container">
      <!-- 盘点信息 -->
      <div class="check-header">
        <el-form :model="checkForm" inline>
          <el-form-item label="盘点日期">
            <el-date-picker
              v-model="checkForm.checkDate"
              type="date"
              placeholder="选择盘点日期"
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="盘点人员">
            <el-input v-model="checkForm.operator" placeholder="请输入盘点人员" style="width: 150px" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="checkForm.remark" placeholder="请输入备注" style="width: 200px" />
          </el-form-item>
        </el-form>
      </div>

      <!-- 商品搜索 -->
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品名称或编码"
          style="width: 300px"
          @input="filterProducts"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="selectAllVisible">全选当前页</el-button>
        <el-button @click="clearSelection">清空选择</el-button>
      </div>

      <!-- 盘点商品表格 -->
      <el-table 
        ref="checkTableRef"
        :data="filteredProducts" 
        border 
        style="width: 100%" 
        max-height="400"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="code" label="商品编码" width="120" />
        <el-table-column prop="currentStock" label="系统库存" width="100" />
        <el-table-column label="实际库存" width="120">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.actualStock"
              :min="0"
              size="small"
              style="width: 100%"
              @change="calculateDifference(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="差异" width="100">
          <template #default="scope">
            <span :class="getDifferenceClass(scope.row)">
              {{ getDifferenceValue(scope.row) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="差异类型" width="100">
          <template #default="scope">
            <el-tag :type="getDifferenceTagType(scope.row)">
              {{ getDifferenceText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button 
              type="text" 
              size="small" 
              @click="resetActualStock(scope.row)"
            >
              重置
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 盘点汇总 -->
      <div class="check-summary">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">盘点商品数</div>
              <div class="summary-value">{{ selectedProducts.length }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">盘盈商品</div>
              <div class="summary-value positive">{{ profitCount }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">盘亏商品</div>
              <div class="summary-value negative">{{ lossCount }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="summary-item">
              <div class="summary-label">正常商品</div>
              <div class="summary-value">{{ normalCount }}</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitCheck" :disabled="!hasChanges">
          确认盘点 ({{ changedCount }}项差异)
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { inventoryCheck } from '@/api/inventory'

interface Product {
  id: number
  name: string
  code: string
  currentStock: number
  actualStock: number
  unit: string
}

const props = defineProps<{
  visible: boolean
  products: Product[]
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  'check-completed': []
}>()

const checkTableRef = ref()
const searchKeyword = ref('')
const selectedProducts = ref<Product[]>([])

const checkForm = reactive({
  checkDate: new Date(),
  operator: '当前用户',
  remark: ''
})

// 初始化实际库存
const initializeProducts = () => {
  return props.products.map(product => ({
    ...product,
    actualStock: product.currentStock
  }))
}

const filteredProducts = ref<Product[]>([])

// 搜索过滤
const filterProducts = () => {
  if (!searchKeyword.value.trim()) {
    filteredProducts.value = initializeProducts()
  } else {
    const keyword = searchKeyword.value.toLowerCase()
    filteredProducts.value = initializeProducts().filter(product =>
      product.name.toLowerCase().includes(keyword) ||
      product.code.toLowerCase().includes(keyword)
    )
  }
}

// 监听产品变化
watch(() => props.products, () => {
  filterProducts()
}, { immediate: true })

// 计算差异
const calculateDifference = (product: Product) => {
  // 触发响应式更新
}

// 获取差异值
const getDifferenceValue = (product: Product) => {
  const difference = product.actualStock - product.currentStock
  return difference > 0 ? `+${difference}` : difference.toString()
}

// 获取差异样式类
const getDifferenceClass = (product: Product) => {
  const difference = product.actualStock - product.currentStock
  if (difference > 0) return 'difference-positive'
  if (difference < 0) return 'difference-negative'
  return ''
}

// 获取差异标签类型
const getDifferenceTagType = (product: Product) => {
  const difference = product.actualStock - product.currentStock
  if (difference > 0) return 'success'
  if (difference < 0) return 'danger'
  return 'info'
}

// 获取差异文本
const getDifferenceText = (product: Product) => {
  const difference = product.actualStock - product.currentStock
  if (difference > 0) return '盘盈'
  if (difference < 0) return '盘亏'
  return '正常'
}

// 重置实际库存
const resetActualStock = (product: Product) => {
  product.actualStock = product.currentStock
}

// 选择变化处理
const handleSelectionChange = (selection: Product[]) => {
  selectedProducts.value = selection
}

// 全选当前页
const selectAllVisible = () => {
  checkTableRef.value?.toggleAllSelection()
}

// 清空选择
const clearSelection = () => {
  checkTableRef.value?.clearSelection()
}

// 计算统计数据
const profitCount = computed(() => {
  return selectedProducts.value.filter(p => p.actualStock > p.currentStock).length
})

const lossCount = computed(() => {
  return selectedProducts.value.filter(p => p.actualStock < p.currentStock).length
})

const normalCount = computed(() => {
  return selectedProducts.value.filter(p => p.actualStock === p.currentStock).length
})

const changedCount = computed(() => {
  return selectedProducts.value.filter(p => p.actualStock !== p.currentStock).length
})

const hasChanges = computed(() => {
  return changedCount.value > 0
})

// 提交盘点
const submitCheck = async () => {
  if (!hasChanges.value) {
    ElMessage.info('没有发现库存差异，无需调整')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要提交盘点结果吗？将调整 ${changedCount.value} 个商品的库存。`,
      '确认盘点',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const checkData = {
      items: selectedProducts.value
        .filter(item => item.actualStock !== item.currentStock)
        .map(item => ({
          productId: item.id,
          actualStock: item.actualStock
        })),
      remark: checkForm.remark
    }

    await inventoryCheck(checkData)
    ElMessage.success('库存盘点完成')
    emit('check-completed')
    handleClose()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('库存盘点失败:', error)
      ElMessage.error('库存盘点失败，请重试')
    }
  }
}

// 关闭对话框
const handleClose = () => {
  emit('update:visible', false)
  // 重置数据
  searchKeyword.value = ''
  selectedProducts.value = []
  checkForm.remark = ''
}
</script>

<style scoped>
.inventory-check-container {
  .check-header {
    margin-bottom: 20px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 6px;
  }

  .search-section {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .check-summary {
    margin-top: 20px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 6px;

    .summary-item {
      text-align: center;
      
      .summary-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }
      
      .summary-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        
        &.positive {
          color: #67c23a;
        }
        
        &.negative {
          color: #f56c6c;
        }
      }
    }
  }
}

.difference-positive {
  color: #67c23a;
  font-weight: 600;
}

.difference-negative {
  color: #f56c6c;
  font-weight: 600;
}

.dialog-footer {
  text-align: right;
  
  .el-button {
    margin-left: 10px;
  }
}
</style>