<template>
  <el-dialog
    title="设置库存预警阈值"
    v-model="visible"
    width="600px"
    @close="handleClose"
  >
    <div class="threshold-setting-container">
      <!-- 商品信息 -->
      <div class="product-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商品名称">{{ product.name }}</el-descriptions-item>
          <el-descriptions-item label="商品编码">{{ product.code }}</el-descriptions-item>
          <el-descriptions-item label="当前库存">
            <span :class="getStockClass(product)">{{ product.currentStock }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="单位">{{ product.unit }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 阈值设置表单 -->
      <el-form 
        ref="formRef"
        :model="thresholdForm" 
        :rules="thresholdRules" 
        label-width="120px"
        class="threshold-form"
      >
        <el-form-item label="最低库存" prop="minStock">
          <el-input-number
            v-model="thresholdForm.minStock"
            :min="0"
            :max="thresholdForm.maxStock - 1"
            style="width: 100%"
            placeholder="请输入最低库存"
          />
          <div class="form-tip">
            当库存低于此值时将触发预警提醒
          </div>
        </el-form-item>

        <el-form-item label="最高库存" prop="maxStock">
          <el-input-number
            v-model="thresholdForm.maxStock"
            :min="thresholdForm.minStock + 1"
            style="width: 100%"
            placeholder="请输入最高库存"
          />
          <div class="form-tip">
            建议的最大库存量，用于采购参考
          </div>
        </el-form-item>

        <el-form-item label="安全库存">
          <el-input-number
            v-model="safeStock"
            :min="thresholdForm.minStock"
            :max="thresholdForm.maxStock"
            style="width: 100%"
            disabled
          />
          <div class="form-tip">
            安全库存 = (最低库存 + 最高库存) / 2
          </div>
        </el-form-item>

        <el-form-item label="预警级别">
          <el-radio-group v-model="alertLevel">
            <el-radio label="low">低级预警</el-radio>
            <el-radio label="medium">中级预警</el-radio>
            <el-radio label="high">高级预警</el-radio>
          </el-radio-group>
          <div class="form-tip">
            {{ getAlertLevelDescription() }}
          </div>
        </el-form-item>
      </el-form>

      <!-- 预警预览 -->
      <div class="alert-preview">
        <h4>预警预览</h4>
        <div class="preview-items">
          <div class="preview-item danger" v-if="product.currentStock === 0">
            <el-icon><CircleClose /></el-icon>
            <span>缺货预警：当前库存为0，需要立即补货</span>
          </div>
          <div class="preview-item warning" v-else-if="product.currentStock <= thresholdForm.minStock">
            <el-icon><Warning /></el-icon>
            <span>低库存预警：当前库存 {{ product.currentStock }}，低于最低库存 {{ thresholdForm.minStock }}</span>
          </div>
          <div class="preview-item success" v-else>
            <el-icon><CircleCheck /></el-icon>
            <span>库存正常：当前库存 {{ product.currentStock }}，在安全范围内</span>
          </div>
        </div>
      </div>

      <!-- 历史设置记录 -->
      <div class="history-records" v-if="historyRecords.length > 0">
        <h4>历史设置记录</h4>
        <el-table :data="historyRecords" size="small" max-height="200">
          <el-table-column prop="date" label="设置时间" width="150" />
          <el-table-column prop="minStock" label="最低库存" width="100" />
          <el-table-column prop="maxStock" label="最高库存" width="100" />
          <el-table-column prop="operator" label="操作人" width="100" />
          <el-table-column prop="remark" label="备注" min-width="120" />
        </el-table>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitThreshold" :loading="submitting">
          保存设置
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning, CircleClose, CircleCheck } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { setAlertThreshold } from '@/api/inventory'

interface Product {
  id: number
  name: string
  code: string
  currentStock: number
  minStock: number
  maxStock: number
  unit: string
}

const props = defineProps<{
  visible: boolean
  product: Product
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  'threshold-updated': [product: Product]
}>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const alertLevel = ref('medium')

const thresholdForm = reactive({
  minStock: 0,
  maxStock: 0
})

// 表单验证规则
const thresholdRules = reactive<FormRules>({
  minStock: [
    { required: true, message: '请输入最低库存', trigger: 'blur' },
    { type: 'number', min: 0, message: '最低库存不能小于0', trigger: 'blur' }
  ],
  maxStock: [
    { required: true, message: '请输入最高库存', trigger: 'blur' },
    { type: 'number', min: 1, message: '最高库存不能小于1', trigger: 'blur' }
  ]
})

// 安全库存计算
const safeStock = computed(() => {
  return Math.round((thresholdForm.minStock + thresholdForm.maxStock) / 2)
})

// 历史记录（模拟数据）
const historyRecords = ref([
  {
    date: '2025-01-20 10:30:00',
    minStock: 10,
    maxStock: 100,
    operator: '张三',
    remark: '初始设置'
  },
  {
    date: '2025-01-15 14:20:00',
    minStock: 5,
    maxStock: 50,
    operator: '李四',
    remark: '调整预警阈值'
  }
])

// 监听产品变化，初始化表单
watch(() => props.product, (product) => {
  if (product) {
    thresholdForm.minStock = product.minStock || 0
    thresholdForm.maxStock = product.maxStock || 100
  }
}, { immediate: true })

// 获取库存状态样式
const getStockClass = (product: Product) => {
  if (product.currentStock === 0) return 'stock-danger'
  if (product.currentStock <= product.minStock) return 'stock-warning'
  return 'stock-normal'
}

// 获取预警级别描述
const getAlertLevelDescription = () => {
  const descriptions = {
    low: '仅在系统中显示预警标识',
    medium: '显示预警标识并发送邮件通知',
    high: '显示预警标识、发送邮件和短信通知'
  }
  return descriptions[alertLevel.value as keyof typeof descriptions]
}

// 提交阈值设置
const submitThreshold = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (thresholdForm.minStock >= thresholdForm.maxStock) {
        ElMessage.error('最低库存不能大于等于最高库存')
        return
      }

      submitting.value = true
      try {
        await setAlertThreshold({
          productId: props.product.id,
          minStock: thresholdForm.minStock,
          maxStock: thresholdForm.maxStock
        })

        // 更新产品信息
        const updatedProduct = {
          ...props.product,
          minStock: thresholdForm.minStock,
          maxStock: thresholdForm.maxStock
        }

        ElMessage.success('库存预警阈值设置成功')
        emit('threshold-updated', updatedProduct)
        handleClose()
      } catch (error) {
        console.error('设置预警阈值失败:', error)
        ElMessage.error('设置预警阈值失败，请重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 关闭对话框
const handleClose = () => {
  emit('update:visible', false)
  // 重置表单
  if (formRef.value) {
    formRef.value.resetFields()
  }
  alertLevel.value = 'medium'
}
</script>

<style scoped>
.threshold-setting-container {
  .product-info {
    margin-bottom: 20px;
  }

  .threshold-form {
    margin-bottom: 20px;

    .form-tip {
      font-size: 12px;
      color: #909399;
      margin-top: 5px;
      line-height: 1.4;
    }
  }

  .alert-preview {
    margin-bottom: 20px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 6px;

    h4 {
      margin: 0 0 15px 0;
      color: #303133;
      font-size: 16px;
    }

    .preview-items {
      .preview-item {
        display: flex;
        align-items: center;
        padding: 10px;
        border-radius: 4px;
        margin-bottom: 10px;

        &:last-child {
          margin-bottom: 0;
        }

        .el-icon {
          margin-right: 8px;
          font-size: 16px;
        }

        &.success {
          background: #f0f9ff;
          color: #67c23a;
          border: 1px solid #b3e19d;
        }

        &.warning {
          background: #fdf6ec;
          color: #e6a23c;
          border: 1px solid #f5dab1;
        }

        &.danger {
          background: #fef0f0;
          color: #f56c6c;
          border: 1px solid #fbc4c4;
        }
      }
    }
  }

  .history-records {
    h4 {
      margin: 0 0 15px 0;
      color: #303133;
      font-size: 16px;
    }
  }
}

.stock-normal {
  color: #67c23a;
  font-weight: 600;
}

.stock-warning {
  color: #e6a23c;
  font-weight: 600;
}

.stock-danger {
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